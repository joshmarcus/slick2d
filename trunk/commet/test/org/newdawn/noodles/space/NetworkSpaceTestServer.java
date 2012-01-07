package org.newdawn.noodles.space;

import java.io.IOException;
import java.util.ArrayList;

import org.newdawn.noodles.message.Message;
import org.newdawn.noodles.message.MessageChannel;
import org.newdawn.noodles.space.NetworkSpace;
import org.newdawn.noodles.space.NetworkSpaceListener;
import org.newdawn.noodles.space.NetworkSpaceServer;

/**
 * A test server that will maintain a single network space. Right now theres no
 * way to host more than one network space on a server, but thats intended to 
 * be changed 
 * 
 * @author kevin
 */
public class NetworkSpaceTestServer extends NetworkSpaceServer implements NetworkSpaceListener {
	/** The display for the server - this will show the server's view of the game state */
	private static Display blobs = new Display("Server");
	
	/**
	 * Create a new test server 
	 * 
	 * @param port The port to run the server on
	 * @throws IOException Indicates a failure to start the server
	 */
	public NetworkSpaceTestServer(int port) throws IOException {
		super(port);
		
		// add a listener to so we can report when changes happen to the space
		getSpace().addListener(this);
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
	 * @see org.newdawn.noodles.space.NetworkSpaceListener#objectAdded(org.newdawn.noodles.space.NetworkSpace, java.lang.Object, short, short)
	 */
	public void objectAdded(NetworkSpace source, Object obj, short id, short ownerID) {
		Blob blob = (Blob) obj;
		blob.setOwner(ownerID);
		
		blobs.add(blob);
	}
	
	/**
	 * @see org.newdawn.noodles.space.NetworkSpaceListener#objectRemoved(org.newdawn.noodles.space.NetworkSpace, java.lang.Object, short, short)
	 */
	public void objectRemoved(NetworkSpace source, Object obj, short id, short ownerID) {
		blobs.remove((Blob) obj);
	}

	/**
	 * @see org.newdawn.noodles.space.NetworkSpaceListener#customMessageRecieved(org.newdawn.noodles.message.MessageChannel, org.newdawn.noodles.message.Message)
	 */
	public void customMessageRecieved(MessageChannel channel, Message message) {
	}
	
	/**
	 * @see org.newdawn.noodles.space.NetworkSpaceListener#channelDisconnected(org.newdawn.noodles.message.MessageChannel)
	 */
	public void channelDisconnected(MessageChannel channel) {
		// ugly piece of API here. Since the network space is looking after the
		// channels we need to let the server know that a connection has
		// be lost to a client when the space lets us know
		
		// normally the default server would monitor this for itself
		channelDisconnected(channel.getTransport());
	}
	
	/**
	 * Entry point to our network space server
	 * 
	 * @param arg The arguments passed to the test
	 * @throws IOException Indicates a failure to start the server
	 */
	public static void main(String[] arg) throws IOException {
		NetworkSpace.configureMode(NetworkSpace.UDP);
		
		NetworkSpaceTestServer server = new NetworkSpaceTestServer(12345);
		while (true) {
			server.update(10);
			blobs.refresh();
			
			try { Thread.sleep(10); } catch (Exception e) {};
		}
	}


}
