package org.newdawn.noodles;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.newdawn.noodles.transport.TransportChannel;
import org.newdawn.noodles.transport.TransportFactory;
import org.newdawn.noodles.transport.TransportServer;
import org.newdawn.noodles.util.Log;

/**
 * Basic server that accepts connections and passes recieved data on
 * 
 * @author kevin
 */
public abstract class DefaultServer {
	/** The transport we're using to recieve connections */
	protected TransportServer transport;
	/** True if the server is running */
	protected boolean running = true;
	/** The list of connected player channels */
	protected ArrayList<TransportChannel> channels = new ArrayList<TransportChannel>();
	/** The buffer used to read and write */
	protected ByteBuffer buffer = ByteBuffer.allocate(4096);
	
	/**
	 * Create a simple server
	 * 
	 * @param port The port on which to run the server
	 * @throws IOException Indicates a failure to create the server
	 */
	public DefaultServer(int port) throws IOException {
		transport = TransportFactory.createServer(port);
	}
	
	/**
	 * Start the game server, broadcasting recieved data to all connected
	 */
	public void start() {
		while (running) {
			try {
				// wait for something to happen on the server, i.e. a connection to occur
				// or some data to arrive
				transport.waitForActivity();
			} catch (IOException e) {
				// a failure during the wait generally means the server has closed. might as well
				// give up now.
				Log.error(e);
				running = false;
				return;
			}
			
			update();
		}
	}
	
	/**
	 * Poll the server, check for new connections and new data
	 */
	public void update() {
		checkConnect();
		readChannels();
	}
	
	/**
	 * Check for new connections.
	 */
	public void checkConnect() {
		try {
			// check for new connections, we keep looping until there arn't any more
			// though in most cases the default connection waiting list is 5 connections under 
			// TCP, UDP connections can happen very quickly
			TransportChannel channel;
			do {
				channel = transport.accept();
				if (channel != null) {
					channels.add(channel);
					channelConnected(channel);
				}
			} while (channel != null);
		} catch (IOException e) {
			// a failure during accept doesn't really hurt. Other pending connections will 
			// get picked up next loop and the connection coming in will just be ignored
			Log.error(e);
		}
	}
	
	/**
	 * Read all the channels passing off the data to data recieved 
	 */
	public void readChannels() {
		// cycle through all the channels attempting to read from them. It's very important
		// that all channels are read each time since the only way we can detect closure
		// is on read. If you want to go one step lower you can use selectors to determine
		// which channels need reading but in general this just seems to confuse people. The 
		// overhead of reading a non-blocking connection with no data on it is tiny so this
		// seems a small sacrifice for an easier API
		for (int i=0;i<channels.size();i++) {
			TransportChannel channel = channels.get(i);
			buffer.clear();

			try {
				int read = channel.read(buffer);
				
				if (read > 0) {
					dataRecieved(channel, buffer);
				} 
				if (read < 0) {
					channels.remove(i);
					i--;
					channelDisconnected(channel);
				}
			} catch (IOException e) {
				// if we fail reading/writing data we can lose some information best to wrap
				// each layer in it's own handler. Just ignore failed reads
				Log.error(e);
				continue;
			}
		}
	}
	
	/**
	 * Notification that a channel connected to the server
	 * 
	 * @param channel The channel that connected
	 */
	protected void channelConnected(TransportChannel channel) {
	}

	/**
	 * Notification that a channel disconnected from the server
	 * 
	 * @param channel The channel that disconnected
	 */
	protected void channelDisconnected(TransportChannel channel) {
	}
	
	/**
	 * Notification that data has been recieved from a channel
	 * 
	 * @param channel The channel the data came from
	 * @param data The data recieved
	 */
	protected void dataRecieved(TransportChannel channel, ByteBuffer data) {
	}
}
