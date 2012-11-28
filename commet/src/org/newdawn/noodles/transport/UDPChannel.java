package org.newdawn.noodles.transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.newdawn.noodles.util.Log;

/**
 * A UDP channel that can support both reliable (via ACKs) and unreliable transport
 * of data.
 * 
 * @author kevin
 */
public class UDPChannel implements TransportChannel {
	/** The amount of time to wait between checking the read() when support blocking comms */
	private static final int PAUSE = 100;
	/** The timeout in which the channel must be active before considering closure */
	private static final int TIMEOUT = 10000;
	/** The timeout between sending NACKs */
	private static final int RESEND_INTERVAL = 500;
	/** The timeout for connection */
	private static final int CONNECT_TIMEOUT = 5000;
	
	/** The server this channel is using to send data */
	private UDPServer server;
	/** The address of the remote client/server */
	private SocketAddress remote;
	/** True if we're using blocking read */
	private boolean blocking;
	/** True if this is a client side (i.e. single channel single server) connection */
	private boolean clientSide;
	/** The next sequence number to send in a packet - used only for reliable transport */
	private short sentSeq = 1;
	/** The next sequence number to read data from in the cache */
	private short nextRead = 1;
	/** The next slot to read packets into - used only for unreliable transport */
	private short nextRecv = 1;
	
	/** The buffer of the data sent */
	private ByteBuffer[] sent = new ByteBuffer[UDPServer.BUFFER_SIZE+1];
	/** The buffer of the data recieved */
	private ByteBuffer[] recv = new ByteBuffer[UDPServer.BUFFER_SIZE+1];
	/** True if the channel has been closed */
	private boolean closed;
	/** The time of the last sent packet */
	private long lastSend = System.currentTimeMillis();
	/** The time of the last recieved packet */
	private long lastMessage = System.currentTimeMillis();
	/** The time of the last NACK */
	private long lastAck = System.currentTimeMillis();
	
	/** The list of unreliable packets recieved */
	private ArrayList<ByteBuffer> received = new ArrayList<ByteBuffer>();
	/** The ID of this channel */
	private short id = -1;
	
	/**
	 * Create a new UDP channel
	 * 
	 * @param server The server the channel belongs to
	 * @param remote The remote address of the connection 
	 * @throws IOException Indicates a failure to setup the initial socket
	 */
	public UDPChannel(UDPServer server, SocketAddress remote) throws IOException {
		this.server = server;
		this.remote = remote;
		
		sendStart();
	}
	
	/**
	 * Create a new client connectin
	 * 
	 * @param host The host to connect to 
	 * @param port The port to connect to 
	 * @param reliable True if this transport should be reliable
	 * @throws IOException Indicates a failure to setup the initial socket
	 */
	public UDPChannel(String host, int port, boolean reliable) throws IOException {
		server = new UDPServer(this, host, port);
		remote = new InetSocketAddress(host, port);
		clientSide = true;
		
		sendStart();
		
		int timeout = 0;
		while (id == -1) {
			server.read();
			try { Thread.sleep(50); } catch (Exception e) {}
			timeout++;
			if (timeout > CONNECT_TIMEOUT / 50) {
				throw new IOException("Timeout on connect");
			}
		}
	}

	/**
	 * Close this connection with an explanation
	 * 
	 * @param reason The reason the socket is closing
	 * @throws IOException Indicate a failure during shutdown
	 */
	public void close(String reason) throws IOException {
		Log.info("Client closing: "+reason);
		close();
	}
	
	/**
	 * @see org.newdawn.netsession.transport.TransportChannel#close()
	 */
	public void close() throws IOException {
		if (!closed) {
			server.send(UDPServer.CLOSE, ByteBuffer.allocate(1), remote);
			Log.info("Sent close to "+remote);
			closed = true;
		} 
		
		if (clientSide) {
			server.close();
		} else {
			server.remove(remote);
		}
	}

	/**
	 * @see org.newdawn.netsession.transport.TransportChannel#configureBlocking(boolean)
	 */
	public void configureBlocking(boolean blocking) throws IOException {
		this.blocking = blocking;
	}

	/**
	 * Add recieved data from the server
	 * 
	 * @param seq The sequence number recieved with the packet
	 * @param buffer The buffer containing the recieved data
	 * @throws IOException Indicates a failure to read data
	 */
	synchronized void addReceivedData(short seq, ByteBuffer buffer) throws IOException {
		if (closed) {
			return;
		}
		
		if (seq == UDPServer.CLOSE) {
			closed = true;
			close("Remote client closed connection");
			return;
		}
		
		lastMessage = System.currentTimeMillis();

		if (seq == UDPServer.KEEPALIVE) {
			return;
		}
		if (seq == UDPServer.UNRELIABLE) {
			received.add(buffer);
			return;
		}
	
		// if we're recieved a NACK, resend whatever was missing
		if (seq == UDPServer.ACK) {
			short ackIndex = buffer.getShort();

			// otherwise NACKing for something that hasn't been sent yet
			if (sent[ackIndex] != null) {
				sent[ackIndex] = null;
			}
			return;
		}
		
		sendAck(seq);
		if (recv[seq] == null) {
			recv[seq] = buffer;
			
			// if we haven't had the ID yet then this should be it
			if (id == -1) {
				id = buffer.getShort();
				Log.info("UDP Channel assigned server ID: "+id);
				nextRead++;
			} else {
				received.add(buffer);
			}
		} else {
			// pending some more data that needs to be got before we can continue
		}
	}
	
	/**
	 * Send a negative ack, i.e. indication that we didn't recieve a required packet
	 * yet
	 * 
	 * @throws IOException Indicates a failure to send the NACK
	 */
	private void resend() throws IOException {
		if (System.currentTimeMillis() - lastAck > RESEND_INTERVAL) {
			lastAck = System.currentTimeMillis();
			
			for (int i=0;i<sent.length;i++) {
				if (sent[i] != null) {
					lastSend = System.currentTimeMillis();
					server.resend(sent[i], remote);
				}
			}
		} 
		
		if (System.currentTimeMillis() - lastSend > 500) {
			lastSend = System.currentTimeMillis();
			server.send(UDPServer.KEEPALIVE, ByteBuffer.allocate(1), remote);
		}
	}
	
	/**
	 * Send the ACK to a recieved message
	 * 
	 * @param seq The sequence number to ack
	 * @throws IOException Indicates a failure to send the ACK
	 */
	private void sendAck(short seq) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(10);
		buffer.putShort(seq);
		buffer.flip();
		
		server.send(UDPServer.ACK, buffer, remote);
	}
	
	/**
	 * Send the starter mesage
	 * 
	 * @throws IOException Indicates a failure to send the starting message
	 */
	private void sendStart() throws IOException {
		server.send(UDPServer.UNRELIABLE, ByteBuffer.allocate(1), remote);
	}
	
	/**
	 * @see org.newdawn.netsession.transport.TransportChannel#getRemoteSocketAddress()
	 */
	public SocketAddress getRemoteSocketAddress() {
		return remote;
	}

	/**
	 * Get data from the current stored recieved packets in the cache. This takes
	 * care of joining sets of data together and reading partial packets
	 * 
	 * @param target The byte buffer we're trying to fill. This method will attempt
	 * to read target.remaining() bytes
	 * @return The number of bytes read into the buffer
	 */
	private int getData(ByteBuffer target) {
		int total = 0;
		
		if (recv[nextRead] != null) {
			ByteBuffer source = recv[nextRead];
			
			if (source.remaining() <= target.remaining()) {
				total += source.remaining();
				target.put(source);
			} else {
				byte[] copy = new byte[target.remaining()];
				source.get(copy);
				target.put(copy);
				total += copy.length;
			}
			
			recv[nextRead] = null;
			
			nextRead++;
			if (nextRead > UDPServer.BUFFER_SIZE) {
				nextRead = 1;
			}
		}
		
		return total;
	}
	
	/**
	 * @see org.newdawn.netsession.transport.TransportChannel#read(java.nio.ByteBuffer)
	 */
	public int read(ByteBuffer data) throws IOException {
		if (closed) {
			return -1;
		}
		
		try {
			server.read();
		} catch (IOException e) {
			close();
			throw e;
		}
		
		if (blocking) {
			while (recv[nextRead] == null) {
				try { Thread.sleep(PAUSE); } catch (Exception e) {};
				server.read();
			}
		}

		if (System.currentTimeMillis() - lastMessage > TIMEOUT) {
			Log.info("UDP timeout: "+remote);
			close("Client timed out, no messages for "+TIMEOUT+" ms");
			return -1;
		}
		
		int result = getData(data);
		return result;
	}

	/**
	 * Write a set of data out to the channel
	 * 
	 * @param datas The set of data to write out
	 * @param reliable True if the data must arrive at the other end. Unreliable data
	 * transfer can be more efficient.
	 * @throws IOException Indicates a failure to write the data out, normally due to the
	 * channel being closed.
	 */
	public void write(ByteBuffer[] datas, boolean reliable) throws IOException {
		if (closed) {
			return;
		}
		
		lastSend = System.currentTimeMillis();
		
		if (reliable) {
			sent[sentSeq] = server.send(sentSeq, datas, remote);
			
			sentSeq++;
			if (sentSeq > UDPServer.BUFFER_SIZE) {
				sentSeq = 1;
			}
		} else {
			server.send(UDPServer.UNRELIABLE, datas, remote);
		}
	}

	/**
	 * @see org.newdawn.netsession.transport.TransportChannel#write(java.nio.ByteBuffer, boolean)
	 */
	public void write(ByteBuffer data, boolean reliable) throws IOException {
		write(new ByteBuffer[] {data}, reliable);
	}

	/**
	 * Update the channel, send keep alive if required. Resend
	 * NACK if required
	 * 
	 * @throws IOException Indicates a failure to perform resending of ACKs
	 */
	void update() throws IOException {
		resend();
	}

	/**
	 * @see org.newdawn.noodles.transport.TransportChannel#isClosed()
	 */
	public boolean isClosed() {
		return closed;
	}

	/**
	 * @see org.newdawn.noodles.transport.TransportChannel#getChannelID()
	 */
	public int getChannelID() {
		return id;
	}
	
	/**
	 * Set the server ID for this channel
	 * 
	 * @param id The ID of this channel
	 * @throws IOException Indicates a failure sending the channel ID
	 */
	public void setID(short id) throws IOException {
		this.id = id;
		ByteBuffer buffer = ByteBuffer.allocate(2);
		buffer.putShort(id);
		buffer.flip();
		
		write(buffer, true);
	}
	
	/**
	 * Check if this channel has had the ID send acked already
	 * 
	 * @return True if this channel has had the ack for it's ID
	 */
	public boolean hasAckedID() {
		// if the first message has been acked
		return sent[1] == null;
	}
}
