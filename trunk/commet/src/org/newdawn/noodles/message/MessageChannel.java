package org.newdawn.noodles.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import org.newdawn.noodles.transport.TransportChannel;
import org.newdawn.noodles.transport.TransportFactory;

/**
 * A channel that supports sending and recieving <code>Message<code> objects
 * 
 * @author kevin
 */
public class MessageChannel {
	/** The underlying transport for the messages */
	private TransportChannel channel;
	/** A buffer to assist in sending and recieving messages */
	private ByteBuffer buffer = ByteBuffer.allocate(4096);
	/** The factory used to create recieved channels */
	private MessageFactory factory;
	
	/**
	 * Create a new channel to send and recieve messages
	 * 
	 * @param factory The factory used to create recieved messages 
	 * @param host The host to connect to
	 * @param port The port to connect on
	 * @throws IOException Indicates a failure to connect
	 */
	public MessageChannel(MessageFactory factory, String host, int port) throws IOException {
		this(factory, TransportFactory.createChannel(host, port));
	}
	
	/**
	 * Create a message based channel around an existing transport channel
	 * 
	 * @param factory The factory used to create recieved message objects
	 * @param channel The underlying transport for the messages
	 */
	public MessageChannel(MessageFactory factory, TransportChannel channel) {
		this.channel = channel;
		this.factory = factory;
	}
	
	/**
	 * Get the the underlying transport for the messages
	 * 
	 * @return The channel used to send and recieve the messages
	 */
	public TransportChannel getTransport() {
		return channel;
	}
	
	/**
	 * Read a single message from the channel. Non-blocking.
	 * 
	 * @return The message read or null if no message was available
	 * @throws IOException Indicates a failure to read from the channel, normally
	 * channel closed.
	 */
	public Message read() throws IOException {
		buffer.clear();
		int read = channel.read(buffer);
		
		if (read > 0) {
			return decode(factory, buffer);
		}
		
		return null;
	}
	
	/**
	 * Write a message to the channel.
	 * 
	 * @param message The message to write 
	 * @param reliable True if the message must reach the other end. Unreliable transport
	 * may have better performance but does not guarnatee delivery
	 * @throws IOException Indicates a failure to write to the underlying transport
	 */
	public void write(Message message, boolean reliable) throws IOException {
		encode(message, buffer);
		
		buffer.flip();
		channel.write(buffer, reliable);
	}
	
	/**
	 * Decode a message from byte buffer
	 * 
	 * @param factory The factory used to create the recieved message object
	 * @param buffer The buffer to decode
	 * @return A decoding message object
	 * @throws IOException Indicates the message could not be decoded, either unrecognised or
	 * malformed.
	 */
	public static Message decode(MessageFactory factory, ByteBuffer buffer) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(buffer.array(), 0, buffer.limit());
		DataInputStream din = new DataInputStream(in);
		
		Message message = factory.createMessageFor(din.readShort());
		message.decode(din);
		
		return message;
	}
	
	/**
	 * Encode a message to a buffer ready for sending
	 * 
	 * @param message The message to encode
	 * @param target The buffer to fill with the encoded message
	 * @throws IOException Indicates a failure to encode the message
	 */
	public static void encode(Message message, ByteBuffer target) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(out);
		
		dout.writeShort(message.getID());
		message.encode(dout);

		target.clear();
		target.put(out.toByteArray());
	}
	
	/**
	 * Close the channel, releasing any underlying resources
	 * 
	 * @throws IOException Indicates an IO failure occured while closing
	 */
	public void close() throws IOException {
		channel.close();
	}
	
	/**
	 * Get the address of the socket at the other end of the 
	 * channel
	 * 
	 * @return The address of the remote end of the channel
	 */
	public SocketAddress getRemoteSocketAddress() {
		return channel.getRemoteSocketAddress();
	}
	
	/**
	 * Check if the channel has been closed
	 * 
	 * @return True if the channel has been closed
	 */
	public boolean isClosed() {
		return channel.isClosed();
	}
	
	/**
	 * Get the ID given by the server for this connection
	 * 
	 * @return The ID given by the server for this connection
	 */
	public int getChannelID() {
		return channel.getChannelID();
	}
}
