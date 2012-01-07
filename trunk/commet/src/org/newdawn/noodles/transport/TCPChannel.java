package org.newdawn.noodles.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;

import org.newdawn.noodles.util.Log;

/**
 * A channel implementation that runs flat over TCP. This is by far the simplest 
 * and most robust approach. However, even with Nagle's turned off TCP can suffer 
 * from lag spikes due to congestion control of infrastructure routers. UDP 
 * can "sometimes" offset this.
 * 
 * @author kevin
 */
public class TCPChannel implements TransportChannel {
	/** The channel which we're sending data across */
	private SocketChannel channel;

	/** The buffer used to read data from the stream (2K) */
	private ByteBuffer dataBuffer = ByteBuffer.allocate(2048);
	/** The buffer used to read the length and source ID (4 bytes - 2 shorts) */
	private ByteBuffer lenBuffer = ByteBuffer.allocate(4);
	/** The number of bytes we're allowed to read at the moment */
	private int allowedToRead;
	/** The stream used to store the data that's been read in */
	private RingInputStream input = new RingInputStream();
	/** The stream that translates data read in into useable types */
	private DataInputStream din = new DataInputStream(input);
	/** True if we're currently waiting for the length and source ID */
	private boolean waitingForLength = true;
	/** The length of the data we're waiting for */
	private int length;
	/** True if the channel has been closed at this end*/
	private boolean closed = false;
	/** The ID for this channel */
	private short id = -1;
	
	/**
	 * Create a new client channel
	 * 
	 * @param host The host to connect to
	 * @param port The port to connect to
	 * @throws IOException Indicates a failure to connect to the remote address
	 */
	public TCPChannel(String host, int port) throws IOException {
		channel = SocketChannel.open();
		channel.socket().setTcpNoDelay(true);
		channel.connect(new InetSocketAddress(host, port));
		
		ByteBuffer idBuffer = ByteBuffer.allocate(2);
		channel.read(idBuffer);
		idBuffer.flip();
		id = idBuffer.getShort();
		Log.info("TCP Connection assigned server ID: "+id);
		
		lenBuffer.order(ByteOrder.BIG_ENDIAN);
		allowedToRead = 4;
		
		channel.configureBlocking(false);
		channel.socket().setTcpNoDelay(true);
	}
	
	/**
	 * Create a channel a recieved as a connection on a server
	 * 
	 * @param channel The TCP channel recieved on the server 
	 * @param id The server provided ID for this channel
	 * @throws IOException Indicates a failure to configure the channel
	 */
	public TCPChannel(SocketChannel channel, short id) throws IOException {
		this.channel = channel;
		channel.configureBlocking(false);
		channel.socket().setTcpNoDelay(true);
		
		this.id = id;
		ByteBuffer idBuffer = ByteBuffer.allocate(2);
		idBuffer.putShort(id);
		idBuffer.flip();
		channel.write(idBuffer);
		
		lenBuffer.order(ByteOrder.BIG_ENDIAN);
		allowedToRead = 4;
	}
	
	/**
	 * @see org.newdawn.netsession.transport.TransportChannel#close()
	 */
	public void close() throws IOException {
		closed = true;
		channel.close();
	}

	/**
	 * @see org.newdawn.netsession.transport.TransportChannel#configureBlocking(boolean)
	 */
	public void configureBlocking(boolean blocking) throws IOException {
		channel.socket().setTcpNoDelay(!blocking);
		channel.configureBlocking(blocking);
	}

	/**
	 * @see org.newdawn.netsession.transport.TransportChannel#getRemoteSocketAddress()
	 */
	public SocketAddress getRemoteSocketAddress() {
		return channel.socket().getRemoteSocketAddress();
	}

	/**
	 * Internal read of length encoded data
	 * 
	 * @return The length encode data byte[] read or null if data isn't yet read
	 * @throws IOException Indicates a failure to read the length encoded data
	 */
	private byte[] readLE() throws IOException {
		if (allowedToRead > 0) {
			dataBuffer.clear();
			dataBuffer.limit(allowedToRead);
			int count = channel.read(dataBuffer);
			
			if (count < 0) {
				throw new IOException("Disconnection detected");
			}
			
			allowedToRead -= count;
			input.add(dataBuffer.array(),0,count);
		}

		if (waitingForLength) {
			if (input.available() >= 2) {
				length = din.readShort();
				waitingForLength = false;
				allowedToRead = length;
			}
		} else {
			// then read the right number of bytes
			if (input.available() >= length) {
				byte[] read = new byte[length];
				input.read(read,0,length);

				waitingForLength = true;
				allowedToRead = 2;
				return read;
			}
		}
		
		return null;
	}
	
	/**
	 * @see org.newdawn.netsession.transport.TransportChannel#read(java.nio.ByteBuffer)
	 */
	public int read(ByteBuffer data) throws IOException {
		if (closed) {
			return -1;
		}
		
		try {
			byte[] read = readLE();
			if (read == null) {
				read = readLE();
			}
			
			if (read == null) {
				return 0;
			}
			
			data.put(read);
			return read.length;
		} catch (IOException e) {
			close();
			throw e;
		}
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
		ByteBuffer[] withLen = new ByteBuffer[datas.length+1];
		short len = 0;
		for (int i=0;i<datas.length;i++) {
			withLen[i+1] = datas[i];
			len += datas[i].limit();
		}
		
		withLen[0] = ByteBuffer.allocate(2);
		withLen[0].putShort(len);
		withLen[0].flip();
		
		channel.write(withLen);
	}
	
	/**
	 * @see org.newdawn.netsession.transport.TransportChannel#write(java.nio.ByteBuffer, boolean)
	 */
	public void write(ByteBuffer data, boolean reliable) throws IOException {
		write(new ByteBuffer[] {data}, reliable);
	}

	/**
	 * @see org.newdawn.noodles.transport.TransportChannel#isClosed()
	 */
	public boolean isClosed() {
		return closed || !channel.isOpen();
	}

	/**
	 * @see org.newdawn.noodles.transport.TransportChannel#getChannelID()
	 */
	public int getChannelID() {
		return id;
	}

}
