package org.newdawn.noodles.transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import org.newdawn.noodles.util.Log;

/**
 * A server implementation that accepts channels connecting across TCP
 * 
 * @author kevin
 */
public class TCPServer implements TransportServer {
	/** The socket channel on which we're accepting connections */
	private ServerSocketChannel serverChannel;
	/** A selector to wait on this server and any channels it recieves */
	private Selector selector;
	/** The next id to give out */
	private short nextID = 1;
	
	/**
	 * Create a new server binding on all interfaces with the given port
	 * 
	 * @param port The port on which to bind
	 * @throws IOException Indicates a failure to bind to the port or
	 * configure the resulting socket
	 */
	public TCPServer(int port) throws IOException {
		Log.info("Creating TCP Server on "+port);
		
		selector = Selector.open();
		
		serverChannel = ServerSocketChannel.open();
		serverChannel.socket().bind(new InetSocketAddress("0.0.0.0",port));
		serverChannel.configureBlocking(false);
		
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
	}

	/**
	 * @see org.newdawn.netsession.transport.TransportServer#accept()
	 */
	public TransportChannel accept() throws IOException {
		SocketChannel channel = serverChannel.accept();
		if (channel != null) {
			Log.info("TCP Connection from: "+channel.socket().getRemoteSocketAddress());
			
			channel.configureBlocking(false);
			channel.register(selector, SelectionKey.OP_READ);

			TCPChannel tcp = new TCPChannel(channel, nextID);
			nextID++;
			return tcp;
		}
		
		return null;
	}

	/**
	 * @see org.newdawn.netsession.transport.TransportServer#close()
	 */
	public void close() throws IOException {
		serverChannel.close();
	}

	/**
	 * @see org.newdawn.noodles.transport.TransportServer#waitForActivity()
	 */
	public void waitForActivity() throws IOException {
		selector.selectedKeys().clear();
		
		int keys = selector.select();
	}

	/**
	 * @see org.newdawn.noodles.transport.TransportServer#waitForActivity(int)
	 */
	public boolean waitForActivity(int timeout) throws IOException {
		selector.selectedKeys().clear();
		
		return selector.select(timeout) != 0;
	}
}
