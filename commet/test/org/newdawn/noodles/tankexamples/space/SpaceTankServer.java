package org.newdawn.noodles.tankexamples.space;

import java.io.IOException;

import org.newdawn.noodles.message.Message;
import org.newdawn.noodles.message.MessageChannel;
import org.newdawn.noodles.space.Blob;
import org.newdawn.noodles.space.NetworkSpace;
import org.newdawn.noodles.space.NetworkSpaceServer;

/**
 * A test server that will maintain a single network space. Right now theres no
 * way to host more than one network space on a server, but thats intended to 
 * be changed 
 * 
 * @author kevin
 */
public class SpaceTankServer extends NetworkSpaceServer {
	
	/**
	 * Create a new test server 
	 * 
	 * @param port The port to run the server on
	 * @throws IOException Indicates a failure to start the server
	 */
	public SpaceTankServer(int port) throws IOException {
		super(port);
	}

	/**
	 * Update the server causing the space to be updated
	 * 
	 * @param delta The amount of time passed since last update
	 */
	public void update(int delta) {
		// check for new connections - this is provided by the default server
		// along with the ability to readChannels(). However, the space is going
		// to take control of reading from channels
		checkConnect();
		space.update(delta);
	}
	
	/**
	 * Entry point to our network space server
	 * 
	 * @param arg The arguments passed to the test
	 * @throws IOException Indicates a failure to start the server
	 */
	public static void main(String[] arg) throws IOException {
		SpaceTankServer server = new SpaceTankServer(12345);
		while (true) {
			server.update(10);
			try { Thread.sleep(10); } catch (Exception e) {};
		}
	}


}
