package org.newdawn.noodles.tankexamples;

import java.util.ArrayList;

/**
 * An arena in which the tank fight - maybe
 * 
 * @author kevin
 */
public class Arena {
	/** The tank in the arena */
	private ArrayList<Tank> tanks = new ArrayList<Tank>();
	
	/**
	 * Create a new empty arena
	 */
	public Arena() {
	}
	
	/**
	 * Get the number of tanks in the arena
	 * 
	 * @return The number of tanks in the arena
	 */
	public int getTankCount() {
		return tanks.size();
	}
	
	/**
	 * Get the tank at a given index
	 * 
	 * @param index The index of the tank
	 * @return The tank at the given index
	 */
	public Tank getTank(int index) {
		return tanks.get(index);
	}
	
	/**
	 * Add a tank of the arena
	 * 
	 * @param tank The tank to add
	 */
	public void addTank(Tank tank) {
		tanks.add(tank);
	}

	/**
	 * Remove a tank from the arena
	 * 
	 * @param tank The tank to remove
	 */
	public void removeTank(Tank tank) {
		tanks.remove(tank);
	}
}
