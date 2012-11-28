package org.newdawn.noodles.tankexamples.space;

import java.io.IOException;
import java.util.HashMap;

import org.newdawn.noodles.message.Message;
import org.newdawn.noodles.message.MessageChannel;
import org.newdawn.noodles.space.NetworkSpace;
import org.newdawn.noodles.space.NetworkSpaceListener;
import org.newdawn.noodles.tankexamples.Arena;
import org.newdawn.noodles.tankexamples.Tank;
import org.newdawn.noodles.tankexamples.TankGameClient;

/**
 * A tank client implementation that syncs everything through a network space
 * 
 * This implementation will get much more simple once we can sync lists of objects
 * but this is more complicated that it sounds. At this point you could just add
 * the Arena to the network space and do no listening at all.
 * 
 * @author kevin
 */
public class SpaceTankClient implements TankGameClient, NetworkSpaceListener {
	/** The arena */
	private Arena arena;
	/** The network space syncing the tanks */
	private NetworkSpace space;
	
	/**
	 * @see org.newdawn.noodles.tankexamples.TankGameClient#addTank(org.newdawn.noodles.tankexamples.Tank)
	 */
	public void addTank(Tank tank) {
		// when we add a tank we just add it to the space. We'll be notified of the 
		// addition and can use this to consistantly add tanks to the arena
		space.add(tank);
	}

	/**
	 * @see org.newdawn.noodles.tankexamples.TankGameClient#configure(java.lang.String, int, org.newdawn.noodles.tankexamples.Arena)
	 */
	public void configure(String host, int port, Arena arena)
			throws IOException {
		this.arena = arena;

		// connect to the space and add a listener so we can be notified of 
		// changes
		space = new NetworkSpace("localhost", 12345, 400);
		space.addListener(this);
	}

	/**
	 * @see org.newdawn.noodles.tankexamples.TankGameClient#update(int)
	 */
	public void update(int delta) throws IOException {
		space.update(delta);
	}

	/**
	 * @see org.newdawn.noodles.space.NetworkSpaceListener#channelDisconnected(org.newdawn.noodles.message.MessageChannel)
	 */
	public void channelDisconnected(MessageChannel channel) {
		// if the server goes down, quit
		System.exit(0);
	}

	/**
	 * @see org.newdawn.noodles.space.NetworkSpaceListener#customMessageRecieved(org.newdawn.noodles.message.MessageChannel, org.newdawn.noodles.message.Message)
	 */
	public void customMessageRecieved(MessageChannel channel, Message message) {
	}

	/**
	 * @see org.newdawn.noodles.space.NetworkSpaceListener#objectAdded(org.newdawn.noodles.space.NetworkSpace, java.lang.Object, short, short)
	 */
	public void objectAdded(NetworkSpace source, Object obj, short id, short ownerID) {
		// a tank has been added to the network space, add it to the game world
		arena.addTank((Tank) obj);
	}

	/**
	 * @see org.newdawn.noodles.space.NetworkSpaceListener#objectRemoved(org.newdawn.noodles.space.NetworkSpace, java.lang.Object, short, short)
	 */
	public void objectRemoved(NetworkSpace source, Object obj, short id, short ownerID) {
		// a tank has been removed from the network space, remove it from the game world
		arena.removeTank((Tank) obj);
	}

}
