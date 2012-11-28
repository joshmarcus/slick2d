package org.newdawn.noodles.tankexamples;

import java.io.IOException;

/**
 * Description of a client that can connect to a remote server
 * and synchronize an arena
 * 
 * @author kevin
 *
 */
public interface TankGameClient {
	/**
	 * Configure the client, including making the connection
	 * 
	 * @param host The host to connect to 
	 * @param port The server to connect to
	 * @param arena The arena to be synced
	 * @throws IOException Indicates a failure to connect to the server
	 */
	public void configure(String host, int port, Arena arena) throws IOException;
	
	/**
	 * Add a tank to the arena
	 * 
	 * @param tank The tank to be added to the arena
	 */
	public void addTank(Tank tank);
	
	/**
	 * Update the state of the client
	 * 
	 * @param delta The amount of time since last update 
	 * @throws IOException Indicates a failure to communicate with the server
	 */
	public void update(int delta) throws IOException;
}
