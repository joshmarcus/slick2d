package org.newdawn.noodles.space;

import java.io.IOException;

import org.newdawn.noodles.message.Message;
import org.newdawn.noodles.message.MessageChannel;
import org.newdawn.noodles.space.NetworkSpace;
import org.newdawn.noodles.space.NetworkSpaceListener;
import org.newdawn.noodles.transport.TransportFactory;

/**
 * A test network space game client syncing it's own Blob game entities.
 * 
 * @author kevin
 */
public class NetworkSpaceTest implements NetworkSpaceListener {
	/** The interval of updates from the client (ms) */
	private static final int UPDATE_INTERVAL = 250;
	/** The display showing the state of our in game blobs */
	private static Display blobs = new Display("Client");
	
	/**
	 * Entry point in the network space test client
	 * 
	 * @param argv The arguments supplied to the application
	 * @throws IOException Indicates a failure connecting to the test server
	 */
	public static void main(String[] argv) throws IOException {
		// configure the type of networking we want to use across any 
		// transports created
		NetworkSpace.configureMode(NetworkSpace.UDP);
		
		// create a network space connecting it to the remote server
		// thats hosting the space
		NetworkSpace space = new NetworkSpace("localhost", 12345, UPDATE_INTERVAL);
		space.addListener(new NetworkSpaceTest());
		
		// create a game object and add it into the network space. By default
		// the game object will be controlled from this client however it's possible
		// to create an object which you're expecting a server process to update
		Blob blob = new Blob(102,110);
		space.add(blob);
		
		// for this example we'll just loop round updating our network space, the display
		// and game object that we own. This is representing the normal game loop. The network
		// space is intended to sit within a game loop and be update()d.
		while (true) {
			// don't tight loop
			try { Thread.sleep(5); } catch (Exception e) {};
			
			// update the network space giving it a chance to send and 
			// recieve updates which will be applied as part of the call
			space.update(5);
			
			// update our game object. for this example it just moves 
			// however this could be anything
			blob.update(5);
			
			// update the display of the game. Blobs is a JFrame showing the status
			// of each blob
			blobs.refresh();
		}
	}

	/**
	 * @see org.newdawn.noodles.space.NetworkSpaceListener#objectAdded(org.newdawn.noodles.space.NetworkSpace, java.lang.Object, short, short)
	 */
	public void objectAdded(NetworkSpace source, Object obj, short id, short ownerID) {
		// notification that an object has been added to the network space. Note that
		// we'll get notifications about both remote objects and the ones that we add locally
		Blob blob = (Blob) obj;
		blob.setOwner(ownerID);
		
		blobs.add(blob);
	}

	/**
	 * @see org.newdawn.noodles.space.NetworkSpaceListener#objectRemoved(org.newdawn.noodles.space.NetworkSpace, java.lang.Object, short, short)
	 */
	public void objectRemoved(NetworkSpace source, Object obj, short id, short ownerID) {
		// notification that an object has been removed from the network space
		blobs.remove((Blob) obj);
	}

	/**
	 * @see org.newdawn.noodles.space.NetworkSpaceListener#channelDisconnected(org.newdawn.noodles.transport.TransportChannel)
	 */
	public void channelDisconnected(MessageChannel channel) {
		// Notification of a disconnection from the network space.
		System.out.println("Disconnection");
		System.exit(0);
	}

	/**
	 * @see org.newdawn.noodles.space.NetworkSpaceListener#customMessageRecieved(org.newdawn.noodles.message.MessageChannel, org.newdawn.noodles.message.Message)
	 */
	public void customMessageRecieved(MessageChannel channel, Message message) {
	}
}
