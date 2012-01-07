package org.newdawn.warpstone.dungeon;

/**
 * A Door links two rooms in the dungeon
 * 
 * @author kevin
 */
public class Door {
	/** The first room the door links to */
	private Room roomA;
	/** The x coordinate of the position in room A of the door */
	private int ax;
	/** The y coordinate of the position in room A of the door */
	private int ay;
	/** The second  room the door links to */
	private Room roomB;
	/** The x coordinate of the position in room B of the door */
	private int bx;
	/** The y coordinate of the position in room B of the door */
	private int by;
	
	/**
	 * Create a new door
	 * 
	 * @param ax The x coordinate of the position in room A of the door
	 * @param ay The y coordinate of the position in room A of the door
	 * @param roomA The first room the door links to
	 * @param bx The x coordinate of the position in room B of the door
	 * @param by The y coordinate of the position in room B of the door
	 * @param roomB The second room the door links to
	 */
	public Door(int ax, int ay, Room roomA, int bx, int by, Room roomB) {
		this.ax = ax;
		this.ay = ay;
		this.roomA = roomA;
		this.bx = bx;
		this.by = by;
		this.roomB = roomB;
	}
	
	/**
	 * Check if this door is placed at the given location. This checks both
	 * linked rooms.
	 * 
	 * @param x The x position to check 
	 * @param y The y position to check
	 * @return True if this door contains the given position
	 */
	public boolean contains(int x, int y) {
		return ((ax == x) && (ay == y)) || ((bx == x) && (by == y));
	}
	
	/**
	 * Get the x coordinate of the position of door in room A
	 * 
	 * @return The x coordinate
 	 */
	public int getAX() {
		return ax;
	}

	/**
	 * Get the y coordinate of the position of door in room A
	 * 
	 * @return The y coordinate
 	 */
	public int getAY() {
		return ay;
	}

	/**
	 * Get the x coordinate of the position of door in room B
	 * 
	 * @return The x coordinate
 	 */
	public int getBX() {
		return bx;
	}

	/**
	 * Get the y coordinate of the position of door in room B
	 * 
	 * @return The y coordinate
 	 */
	public int getBY() {
		return by;
	}
	
	/**
	 * The first room that is linked by this door
	 * 
	 * @return The first room that is linked
	 */
	public Room getRoomA() {
		return roomA;
	}
	
	/**
	 * The second room that is linked by this door
	 * 
	 * @return The second room that is linked 
	 */
	public Room getRoomB() {
		return roomB;
	}

	/**
	 * Disconnect this door from it's rooms
	 */
	public void disconnect() {
		roomA.disconnect(this, roomB);
		roomB.disconnect(this, roomA);
	}
	
	/**
	 * Replace one of the rooms connected by this door with another. Used
	 * when merging rooms and transitioning doors
	 * 
	 * @param original The original room that has been merged out
	 * @param replacement The room that has taken it's place
	 */
	public void replace(Map map, Room original, Room replacement) {
		if (roomA == original) {
			if (map.getTile(ax,ay) != replacement.getID()) {
				throw new RuntimeException("Attempted to place room A with non-targeted room");
			}
			roomA = replacement;
		}
		else if (roomB == original) {
			if (map.getTile(bx,by) != replacement.getID()) {
				throw new RuntimeException("Attempted to place room B with non-targeted room");
			}
			roomB = replacement;
		}
		else {
			throw new RuntimeException("Tried to update a door that isn't using original");
		}
	}
	
	public void validate(Map map) {
		if (map.getTile(ax,ay) != roomA.getID()) {
			throw new RuntimeException("Door location A is in the wrong space: "+map.getTile(ax,ay)+" vs "+roomA.getID()+"\n"+
									   "\t this door links: "+roomA+" to "+roomB);
		}
		if (map.getTile(bx,by) != roomB.getID()) {
			throw new RuntimeException("Door location B is in the wrong space: "+map.getTile(bx,by)+" vs "+roomB.getID());
		}
		if (!roomA.containsDoor(this, ax, ay, roomB)) {
			throw new RuntimeException("Door is orphaned");
		}
		if (!roomB.containsDoor(this, bx, by, roomA)) {
			throw new RuntimeException("Door is orphaned");
		}
	}
}
