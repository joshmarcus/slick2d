package org.newdawn.noodles.space;

import org.newdawn.noodles.object.NetworkField;

/**
 * A sample set of data that will be synchronized when placed within the network
 * space.
 * 
 * @author kevin
 */
public class Blob {
	/** The x position of this game blob */
	@NetworkField
	private int x;
	/** The y position of this game blob */
	@NetworkField
	private int y;
	/** A local owner timer that will be updated but changes only visible client side */
	private int timer;
	/** The ID of the owner of this blob - will be set as part of space listener */
	private int owner;
	
	/**
	 * Create a new empty blob. Note that the network space requires an empty
	 * constructor to be available
	 */
	private Blob() {
	}

	/**
	 * Create a new configured blob
	 * 
	 * @param x The x position of the blob
	 * @param y The y position of the blob
	 */
	public Blob(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Set the ID of the owner of this blob. This ID will be configured by the game
	 * code when the object is constructed
	 * 
	 * @param owner The owner of the object 
	 */
	public void setOwner(int owner) {
		this.owner = owner;
	}
	
	/**
	 * Get the x position of the blob
	 * 
	 * @return The x position of the blob
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Get the y position of the blob
	 * 
	 * @return The y position of the blob
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Update the blobs state based on time passing
	 * 
	 * @param delta The amount of time passed since last update
	 */
	public void update(int delta) {
		timer += delta;
		x += delta / 5;
		y += delta / 3;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[Blob "+owner+" ("+x+","+y+") "+timer+"]";
	}
}
