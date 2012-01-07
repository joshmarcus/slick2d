package org.newdawn.noodles.message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import org.newdawn.noodles.BroadcastServer;
import org.newdawn.noodles.transport.TransportChannel;
import org.newdawn.noodles.util.Log;

/**
 * A server that will optionally broadcast <code>Message</code> object recieved 
 * from clients to all others.
 * 
 * @author kevin
 */
public class MessageBroadcastServer extends BroadcastServer {
	/** A mapping from transport channels that have be provided from the broadcast to the wrapping Message Channels */
	private HashMap<TransportChannel, MessageChannel> channelMap = new HashMap<TransportChannel, MessageChannel>();
	/** The factory used to produce recieved messages */
	private MessageFactory factory;
	/** The buffer used to store the data before decoding */
	private ByteBuffer buffer = ByteBuffer.allocate(4096);
	
	/**
	 * Create a new server to broadcast messages between clients
	 * 
	 * @param factory A factory used to create recieved messages
	 * @param port The port to run the server on
	 * @throws IOException Indicates a failure to start the server
	 */
	public MessageBroadcastServer(MessageFactory factory, int port) throws IOException {
		super(port);
	
		this.factory = factory;
	}

	/**
	 * @see org.newdawn.noodles.BroadcastServer#broadcastData(org.newdawn.noodles.transport.TransportChannel, java.nio.ByteBuffer)
	 */
	@Override
	protected boolean broadcastData(TransportChannel channel, ByteBuffer data) {
		try {
			return broadcastMessage(channelMap.get(channel), MessageChannel.decode(factory, data));
		} catch (IOException e) {
			Log.error(e);
			return true;
		}
	}

	/**
	 * Send the given message to all clients connected, i.e. the broadcast
	 * 
	 * @param message The message to send 
	 * @param ignoreChannelID The ID of the channel to ignore, the channel will not recieve the 
	 * message. This is used to prevent loop back of messages.
	 * @throws IOException Indicates a failure to send a message
	 */
	protected void sendToAll(Message message, int ignoreChannelID) throws IOException {
		buffer.clear();
		MessageChannel.encode(message, buffer);
		
		super.sendToAll(buffer, ignoreChannelID);
	}
	
	/**
	 * @see org.newdawn.noodles.BroadcastServer#channelConnected(org.newdawn.noodles.transport.TransportChannel)
	 */
	@Override
	protected void channelConnected(TransportChannel channel) {
		super.channelConnected(channel);
		
		channelMap.put(channel, new MessageChannel(factory, channel));
		channelConnected(channelMap.get(channel));
	}

	/**
	 * @see org.newdawn.noodles.BroadcastServer#channelDisconnected(org.newdawn.noodles.transport.TransportChannel)
	 */
	@Override
	protected void channelDisconnected(TransportChannel channel) {
		super.channelDisconnected(channel);
		
		channelDisconnected(channelMap.remove(channel));
	}

	/**
	 * Check if this the given message should be broadcast to other clients. This is
	 * where the custom server logic can plug in.
	 * 
	 * @param channel The channel on which the message was recieved
	 * @param message The message that was received
	 * @return True if the message should be broadcast to other clients
	 */
	protected boolean broadcastMessage(MessageChannel channel, Message message) {
		return true;
	}

	/**
	 * Notification that a channel has been connected to the server. A simple override 
	 * for those using message channels.
	 * 
	 * @param channel The channel that was connected
	 */
	protected void channelConnected(MessageChannel channel) {
	}

	/**
	 * Notification that a channel has been disconnected from the server. A simple override 
	 * for those using message channels.
	 * 
	 * @param channel The channel that was disconnected
	 */
	protected void channelDisconnected(MessageChannel channel) {
	}
}
