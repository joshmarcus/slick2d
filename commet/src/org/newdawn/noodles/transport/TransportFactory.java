package org.newdawn.noodles.transport;

import java.io.IOException;

/**
 * A factory to create the underlying channels and servers. This factory is not present
 * to hide which type of protocol is being used under the hood, since it's important
 * from the game's point of view what you are using. However, this class makes it easier
 * to experiement with different approaches and keeps a nice simple common API.
 * 
 * @author kevin
 */
public class TransportFactory {
	/** The TCP transport layer indicator */
	public static final int TCP = 1;
	/** The UDP transport layer indicator */
	public static final int UDP = 2;

	/** The current type of transport that will be created */
	public static int mode = TCP;
	
	/**
	 * Configure the transport to be produced. 
	 * 
	 * @param m The transport layer indicator. Should be one of {@link #TCP} or {@link #UDP}
	 */
	public static void configureMode(int m) {
		mode = m;
	}
	
	/**
	 * Create a transport channel that can be used to send data across the network. The channel
	 * is created with reliable transport
	 * 
	 * @param host The host that should be connected to 
	 * @param port The port that should be connected to
	 * @return The channel that has been created
	 * @throws IOException Indicates a failure to connect to the remote host. Either the host and port
	 * are not running a server, or the a network failure has occured (i.e. host not found)
	 */
	public static TransportChannel createChannel(String host, int port) throws IOException {
		return createChannel(host, port, true);
	}

	/**
	 * Create a transport channel that can be used to send data across the network
	 * 
	 * @param host The host that should be connected to 
	 * @param port The port that should be connected to
	 * @param reliable True if the transport is required to be reliable. Note that some
	 * transport modes can not be unreliable.
	 * @return The channel that has been created
	 * @throws IOException Indicates a failure to connect to the remote host. Either the host and port
	 * are not running a server, or the a network failure has occured (i.e. host not found)
	 */
	public static TransportChannel createChannel(String host, int port, boolean reliable) throws IOException {
		switch (mode) {
		case TCP:
			return new TCPChannel(host, port);
		case UDP:
			return new UDPChannel(host, port, reliable);
		}
		
		throw new RuntimeException("Unknown mode: "+mode);
	}
	
	/**
	 * Create a server that will accept channel connections. Note that the server is currently
	 * bound to the global address (0.0.0.0) and hence all interfaces.
	 * 
	 * @param port The port on which to start the server.
	 * @return The transport server created with the currently configured mode
	 * @throws IOException Indicates a failure to start or create the server
	 */
	public static TransportServer createServer(int port) throws IOException {
		switch (mode) {
		case TCP:
			return new TCPServer(port);
		case UDP:
			return new UDPServer(port);
		}
		
		throw new RuntimeException("Unknown mode: "+mode);
	}
}
