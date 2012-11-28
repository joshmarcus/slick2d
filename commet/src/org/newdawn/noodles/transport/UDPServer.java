package org.newdawn.noodles.transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.newdawn.noodles.util.Log;

/**
 * A server implementation using UDP. The UDP payload is prefixed with a limited
 * sequence number or an identifier that is used for custom packets ({@link #UNRELIABLE} 
 * {@link #KEEPALIVE} {@link #NACK} {@link #CLOSE})
 * 
 * @author kevin
 */
public class UDPServer implements TransportServer {
	/** The number of messages to store */
	public static final short BUFFER_SIZE = 1000;
	
	/** Indicates that a packet is sent on unreliable transport and hence doesn't have a sequence number */
	public static final short UNRELIABLE = BUFFER_SIZE+20;
	/** Indicates a packet is a NACK request. i.e. indication that a particular sequence number hasn't been recieved yet */
	public static final short ACK = BUFFER_SIZE+21;
	/** Indicates the channel is being closed */
	public static final short CLOSE = BUFFER_SIZE+22;
	/** Sent to keep the connection from timing out if no other data is sent */
	public static final short KEEPALIVE = BUFFER_SIZE+23;
	
	/** The channel used to send/recieve datagrams */
	private DatagramChannel server;
	/** The list of channels that have connected but haven't yet been reported as accepted() */
	private ArrayList<UDPChannel> pendingAccept = new ArrayList<UDPChannel>();
	/** The list of channels that have been detected but haven't yet acknowledged the initial ID message */
	private ArrayList<UDPChannel> pendingAck = new ArrayList<UDPChannel>();
	/** The hash between remote address and channels */
	private HashMap<SocketAddress, UDPChannel> channels = new HashMap<SocketAddress, UDPChannel>();
	
	/** True if the server is closed */
	private boolean closed = false;
	/** True if the server is in the process of closing */
	private boolean closing = false;
	
	/** A selector to wait on this server and any channels it recieves */
	private Selector selector;
	/** The next ID to give out */
	private short nextID = 1;
	
	/**
	 * Create a new server based on UDP binding to all interfaces and the given port
	 * 
	 * @param port The port to bind on
	 * @throws IOException Indicates a failure to bind to the socket
	 */
	public UDPServer(int port) throws IOException {
		Log.info("Creating UDP Server on "+port);
		
		selector = Selector.open();
		
		server = DatagramChannel.open();
		server.socket().bind(new InetSocketAddress("0.0.0.0", port));
		server.configureBlocking(false);
		server.register(selector, SelectionKey.OP_READ);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					close();
				} catch (IOException e) {
					// never do anything in a shutdown hook
				}
			}
		});
		
		
		startKeepAlive();
	}

	/**
	 * Create a new UDP Server owned by a client. This server is used by one channel
	 * only and is a convience for client sending
	 * 
	 * @param owner The owner of the server port
	 * @param host The host to which the client is connecting
	 * @param port The port to which the client is connecting
	 * @throws IOException Indicates a failure to bind to the given port or
	 * to connect the remote server
	 */
	public UDPServer(UDPChannel owner, String host, int port) throws IOException {
		SocketAddress remote = new InetSocketAddress(host, port);
		channels.put(remote, owner);
		
		server = DatagramChannel.open();
		server.socket().bind(null);
		server.connect(remote);
		server.configureBlocking(false);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					close();
				} catch (IOException e) {
					// never do anything in a shutdown hook
				}
			}
		});
		
		startKeepAlive();
	}
	
	/**
	 * Start the thread that keeps the channels alive and pushing NACKS
	 */
	private void startKeepAlive() {
		Thread t = new Thread() {
			public void run() {
				while (!closed) {
					try { Thread.sleep(10); } catch (Exception e) {};
					pollChannels();
				
					try {
						read();
					} catch (IOException e) {
						// ignore
					}
				}
			}
		};
		t.setDaemon(false);
		t.start();
	}
	
	/**
	 * Poll all the channels check for resends 
	 */
	private synchronized void pollChannels() {
		Iterator chans = channels.values().iterator();
		while (chans.hasNext()) {
			try {
				UDPChannel remote = (UDPChannel) chans.next();
				remote.update();
			} catch (IOException e) {
				// ignore, did our best to close cleanly
			}
		}
	}
	
	/**
	 * @see org.newdawn.netsession.transport.TransportServer#accept()
	 */
	public synchronized TransportChannel accept() throws IOException {
		read();
		
		if (pendingAccept.size() > 0) {
			return (UDPChannel) pendingAccept.remove(0);
		}
		
		return null;
	}

	/**
	 * Try to read a packet from the socket. If one is available read it and then
	 * pass it on to the channel assocaited with the remote address
	 * 
	 * @throws IOException Indicates a failure to read the packet
	 */
	synchronized void read() throws IOException {
		// evil data creation - this should be worked out but not sure how yet
		ByteBuffer data = ByteBuffer.allocate(4096);
		
		// try to read data. 
		SocketAddress address = server.receive(data);
		if (address != null) {
//			// 50% packet loss
//			if (Math.random() > 0.5f) {
//				return;
//			}
			
			// if we have data, try to find the channel it's associated with
			UDPChannel channel = (UDPChannel) channels.get(address);
			if (channel == null) {
				// if it's a new connection record it, create the channel
				// and continue
				Log.info("UDP Connection from: "+address+" ID="+nextID);
				channel = new UDPChannel(this, address);
				channels.put(address, channel);
				pendingAck.add(channel);
				channel.setID(nextID);

				nextID++;
			}
			
			// let the channel know about the data
			data.flip();
			short seq = data.getShort();
			channel.addReceivedData(seq, data);
		}
		
		for (int i=0;i<pendingAck.size();i++) {
			UDPChannel channel = (UDPChannel) pendingAck.get(i);
			if (channel.hasAckedID()) {
				pendingAck.remove(channel);
				i--;
				pendingAccept.add(channel);
			}
		}
	}
	
	/**
	 * @see org.newdawn.netsession.transport.TransportServer#close()
	 */
	public void close() throws IOException {
		if (closed) {
			return;
		}
		if (closing) {
			return;
		}
		
		closing = true;
		Iterator chans = channels.values().iterator();
		while (chans.hasNext()) {
			try {
				UDPChannel remote = (UDPChannel) chans.next();
				remote.close("Server closed");
			} catch (IOException e) {
				// ignore, did our best to close cleanly
			}
		}

		Log.info("UDP Server closed");
		closed = true;
		server.close();
	}

	/**
	 * Remove a channel from this server
	 * 
	 * @param address The address of the channel to remove
 	 */
	void remove(SocketAddress address) {
		UDPChannel channel = (UDPChannel) channels.remove(address);
		pendingAccept.remove(channel);
	}
	
	/**
	 * Send a packet out on the datagram socket
	 * 
	 * @param seq The sequence number (or identifier) to send
	 * @param datas The data buffers to concatonate and send as the packet
	 * @param address The address to send the packet to
	 * @throws IOException Indicates a failure to send the data
	 * @return The buffer encoded with the given data and sequence number
	 */
	ByteBuffer send(short seq, ByteBuffer[] datas, SocketAddress address) throws IOException {
		if (closed) {
			return null;
		}
		
		ByteBuffer temp = ByteBuffer.allocate(4096);
		temp.putShort(seq);
		for (int i=0;i<datas.length;i++) {
			temp.put(datas[i]);
		}
		
		temp.flip();
		server.send(temp, address);
		
		return temp;
	}

	/**
	 * Resend a previously encoded buffer
	 * 
	 * @param data The buffer to send 
	 * @param address The address to send it to
	 * @throws IOException Indicates a failure to send the data
	 */
	void resend(ByteBuffer data, SocketAddress address) throws IOException {
		data.flip();
		server.send(data, address);
	}
	
	/**
	 * Send a packet out on the datagram socket
	 * 
	 * @param seq The sequence number (or identifier) to send
	 * @param data The data buffer to send as the packet
	 * @param address The address to send the packet to
	 * @throws IOException Indicates a failure to send the data
	 */
	void send(short seq, ByteBuffer data, SocketAddress address) throws IOException {
		send(seq, new ByteBuffer[] {data}, address);
	}

	/**
	 * @see org.newdawn.noodles.transport.TransportServer#waitForActivity()
	 */
	public void waitForActivity() throws IOException {
		selector.selectedKeys().clear();
		
		selector.select();
	}

	/**
	 * @see org.newdawn.noodles.transport.TransportServer#waitForActivity(int)
	 */
	public boolean waitForActivity(int timeout) throws IOException {
		selector.selectedKeys().clear();
		
		return selector.select(timeout) != 0;
	}
}
