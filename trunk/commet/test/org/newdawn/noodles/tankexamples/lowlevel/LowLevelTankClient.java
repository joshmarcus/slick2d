package org.newdawn.noodles.tankexamples.lowlevel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.noodles.tankexamples.Arena;
import org.newdawn.noodles.tankexamples.Tank;
import org.newdawn.noodles.tankexamples.TankGameClient;
import org.newdawn.noodles.transport.TransportChannel;
import org.newdawn.noodles.transport.TransportFactory;

/**
 * A tank client implementation using the lowest level transport
 * 
 * @author kevin
 */
public class LowLevelTankClient implements TankGameClient {
	/** The arena being synced */
	private Arena arena;
	/** The channel being connected to the server */
	private TransportChannel channel;
	/** The list of tanks held lcoally */
	private ArrayList<Tank> localTanks = new ArrayList<Tank>();
	
	/** The buffered used to encode/decode data */
	private ByteBuffer data = ByteBuffer.allocate(4096);
	/** The interval at which updates are sent */
	private int updateInterval = 400;
	/** The timer controlling the interval */
	private int updateTimer = 0;
	
	/** The tanks held based on their IDs */
	private HashMap<Integer, Tank> tanks = new HashMap<Integer, Tank>();
	
	/**
	 * @see org.newdawn.noodles.tankexamples.TankGameClient#addTank(org.newdawn.noodles.tankexamples.Tank)
	 */
	public void addTank(Tank tank) {
		arena.addTank(tank);
		localTanks.add(tank);
		
		tanks.put(channel.getChannelID(), tank);
		// on first update the tank will be created for everyone else
	}

	/**
	 * @see org.newdawn.noodles.tankexamples.TankGameClient#configure(java.lang.String, int, org.newdawn.noodles.tankexamples.Arena)
	 */
	public void configure(String host, int port, Arena arena) throws IOException {
		this.arena = arena;
		
		channel = TransportFactory.createChannel(host, port);
	}

	/**
	 * @see org.newdawn.noodles.tankexamples.TankGameClient#update(int)
	 */
	public void update(int delta) throws IOException {
		updateTimer -= delta;
		if (updateTimer < 0) {
			updateTimer += updateInterval;
			
			// send an update message for each local tank
			for (int i=0;i<localTanks.size();i++) {
				Tank tank = localTanks.get(i);
				
				data.clear();
				data.putShort((short) channel.getChannelID()); // the local channel ID
				data.putFloat(tank.getX());
				data.putFloat(tank.getY());
				data.putFloat(tank.getAngle());
				data.putFloat(tank.getTurretAngle());
				data.flip();
				
				channel.write(data, true);
			}
		}
		
		// read any data pending
		int read = 1;
		
		data.clear();
		read = channel.read(data);
		
		if (read == -1) {
			throw new IOException("Server Disconnected");
		}
		
		while (read != 0) {
			data.flip();
			// we no that only entire reads will be pumped so we can safely
			// read the whole block
			short id = data.getShort();
			float x = data.getFloat();
			float y = data.getFloat();
			float angle = data.getFloat();
			float turret = data.getFloat();
			
			// -1 used an indicator that the tank is no longer valid. An example
			// of the sort of hack you do when prototyping that really isn't great. Wouldn't
			// it be nice if prototyping was quick and sane?
			if (angle == -1) {
				Tank tank = tanks.remove((int) id);
				if (tank != null) {
					arena.removeTank(tank);
					localTanks.remove(tank);
				}
			} else {
				Tank tank = tanks.get((int) id);
				if (tank == null) {
					tank = new Tank();
					tanks.put((int) id, tank);
					arena.addTank(tank);
				}
				
				tank.configure(x,y,angle,turret);
			}
			
			// get some more data if there is any
			data.clear();
			read = channel.read(data);
		}
	}

}
