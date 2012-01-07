package org.newdawn.warpstone.dungeon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

/**
 * A single room defined in a generate map. It specifies both it's size, location and
 * the other rooms which it links to
 * 
 * @author kevin
 */
public class Room {
	/** The island ID used for pruning doors */
	private static final int PRUNE_ID = 10000;
	
	/** The x coordinate of the position of this room */
	private int x;
	/** The y coordinate of the position of this room */
	private int y;
	/** The width of the room in tiles */
	private int width;
	/** The height of the room in tiles */
	private int height;
	/** The ID given for this room */
	private int id;
	
	/** The other rooms that this room links to */
	private UniqueList<Room> connections = new UniqueList<Room>();
	/** A mapping from a connected room to the door that links us to that room, used to check for duplicate doors */
	private HashMap<Room, Door> doors = new HashMap<Room, Door>();
	/** The island ID that has been assigned or -1 */
	private int islandID = -1;
	/** True if this room has been merged */
	private boolean hasMerged;
	/** true if the room is a corridor and hence can only merge with other corridors */
	private boolean corridor;
	
	/**
	 * Create a new room
	 * 
	 * @param id The ID to give the room
	 * @param x The x coordinate of the position of this room
	 * @param y The y coordinate of the position of this room
	 * @param width The width in tiles of the room
	 * @param height The height in tiles of the room
	 * @param corridor True if the room is a corridor
	 */
	public Room(int id, int x, int y, int width, int height, boolean corridor) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.corridor = corridor;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	/**
	 * Strip the corridor back to it's absolute limit
	 * 
	 * @param map The map to adjust
	 */
	public void limitCorridor(Map map) {
		if (height == 2) {
			int removed = 0;
			for (int xp=0;xp<width;xp++) {
				if (isDoor(x+xp,y)) {
					break;
				}
				if (isDoor(x+xp,y+1)) {
					break;
				}
				if (width - removed < 4) {
					break;
				}
				map.setTile(x+xp,y,0);
				map.setTile(x+xp,y+1,0);
				removed++;
			}
			for (int xp=width-1;xp>=0;xp--) {
				if (isDoor(x+xp,y)) {
					break;
				}
				if (isDoor(x+xp,y+1)) {
					break;
				}
				if (width - removed < 4) {
					break;
				}
				map.setTile(x+xp,y,0);
				map.setTile(x+xp,y+1,0);
				removed++;
			}
			
			width -= removed;
		} else {
			int removed = 0;
			for (int yp=0;yp<height;yp++) {
				if (isDoor(x,y+yp)) {
					break;
				}
				if (isDoor(x+1,y+yp)) {
					break;
				}
				if (height - removed < 4) {
					break;
				}
				map.setTile(x,y+yp,0);
				map.setTile(x+1,y+yp,0);
				removed++;
			}
			for (int yp=height-1;yp>=0;yp--) {
				if (isDoor(x,y+yp)) {
					break;
				}
				if (isDoor(x+1,y+yp)) {
					break;
				}
				if (height - removed < 4) {
					break;
				}
				map.setTile(x,y+yp,0);
				map.setTile(x+1,y+yp,0);
				removed++;
			}
			
			height -= removed;
		}
	}
	
	/**
	 * True if this room is a corridor
	 * 
	 * @return True if this room is a corridor
	 */
	public boolean isCorridor() {
		return corridor;
	}
	
	/**
	 * Get the number of doors in the room
	 * 
	 * @return The number of doors in the room 
	 */
	public int getDoorCount() {
		return connections.size();
	}
	
	/**
	 * Get a door defined in this room
	 * 
	 * @param index The index of the door to retrieve
	 * @return The door requested
	 */
	public Door getDoor(int index) {
		Room room = connections.get(index);
		return doors.get(room);
	}

	/**
	 * Place this room onto the tile map. Each tile included in this room
	 * will be set to the ID of this room
	 * 
	 * @param map The map to place the room into
	 */
	public void place(Map map) {
		place(map, id);
	}
	
	/**
	 * Place this room onto the tile map. Each tile included in this room
	 * will be set to the ID of this room
	 * 
	 * @param map The map to place the room into
	 * @param id The ID to place into the map
	 */
	private void place(Map map, int id) {
		for (int xp=0;xp<width;xp++) {
			for (int yp=0;yp<height;yp++) {
				map.setTile(x+xp, y+yp, id);
			}
		}
	}
	
	/**
	 * Check if there is a door at the given location
	 * 
	 * @param x The x coordinate of the position to check
	 * @param y The y coordinate of the position to check
	 * @return True if there is a door at the given position
	 */
	public boolean isDoor(int x, int y) {
		for (int i=0;i<connections.size();i++) {
			Room room = connections.get(i);
			if (doors.get(room).contains(x,y)) {
				return true;
			}
		}
		
		return false;
	}

	public Door getDoor(int x, int y) {
		for (int i=0;i<connections.size();i++) {
			Room room = connections.get(i);
			if (doors.get(room).contains(x,y)) {
				return doors.get(room);
			}
		}
		
		return null;
	}
	
	/**
	 * Consider where it's possible to place a door a given location. If it is
	 * add it to the list
	 * 
	 * @param possibles The map from target room ID to the list of possible doors to the room
	 * @param map The map the doors will exist on
	 * @param x The x location in the target room to consider
	 * @param y The y location in the target room to consider
	 * @param ox The x location in the current room the door would start from
	 * @param oy The y location in the current room the door would start from
	 */
	private void considerDoor(HashMap<Integer, ArrayList<int[]>> possibles, Map map, int x, int y, int ox, int oy) {
		// if theres a room at the target location
		int targetRoomID = map.getTile(x,y);
		if (targetRoomID != 0) {
			Room targetRoom = map.getRoomByID(targetRoomID);
			// if the room doesn't already have a door at the given location
			if (targetRoom.isDoor(x, y)) {
				return;
			}
			// and this room doesn't already have a door at the location
			if (isDoor(ox, oy)) {
				return;
			}
			if (((y == targetRoom.y) || (y == targetRoom.y + targetRoom.height - 1)) && 
			    ((x == targetRoom.x) || (x == targetRoom.x + targetRoom.width - 1))) {
				return;
			}
			
			// record the location as a possible door
			ArrayList<int[]> doors = possibles.get(targetRoomID);
			if (doors == null) {
				doors = new ArrayList<int[]>();
				possibles.put(targetRoomID, doors);
			}
			doors.add(new int[] {x,y,ox,oy});
		}
	}
	
	public void placeDoors(Map map, Random random) {
		HashMap<Integer, ArrayList<int[]>> possibles = new HashMap<Integer, ArrayList<int[]>>();

		// first record all the possible locations in the room
		// that could be doors and which room they link to
		// check top and bottom
		for (int xp=1;xp<width-1;xp++) {	
			considerDoor(possibles, map, x+xp,y-1,x+xp,y);	
			considerDoor(possibles, map, x+xp,y+height,x+xp,y+height-1);	
		}
		// check sides
		for (int yp=1;yp<height-1;yp++) {
			considerDoor(possibles, map, x-1,y+yp,x,y+yp);	
			considerDoor(possibles, map, x+width,y+yp,x+width-1,y+yp);	
		}
		
		// for each room that we could potentially link to
		Iterator<Integer> targets = possibles.keySet().iterator();
		while (targets.hasNext()) {
			// get the target room
			Integer target = targets.next();
			Room targetRoom = map.getRoomByID(target);
			
			// if we don't already have a connection to the target room
			if (!connections.contains(targetRoom)) {
				// pick one of the possible locations for a door randomly
				ArrayList<int[]> options = possibles.get(target);
				int option = random.nextInt(options.size());
				int[] pos = options.get(option);
				
				int tx = pos[0];
				int ty = pos[1];
				int ox = pos[2];
				int oy = pos[3];
				
				if (map.getTile(tx, ty) != targetRoom.getID()) {
					throw new RuntimeException("Failed to place room correctly, target room not targeted");
				}
				if (map.getTile(ox, oy) != getID()) {
					throw new RuntimeException("Failed to place room correctly, original room not targeted");
				}
				
				// and link up the two rooms up
				Door door = new Door(tx,ty,targetRoom,ox,oy,this);
				connections.add(targetRoom);
				targetRoom.connections.add(this);
				doors.put(targetRoom, door);
				targetRoom.doors.put(this, door);
			}	
		}
	}

	/**
	 * Clear the area in the map that this room occupies, this is 
	 * to undo @see Room.place
	 * 
	 * @param map The map to clear this room from
	 */
	public void clear(Map map) {
		for (int xp=0;xp<width;xp++) {
			for (int yp=0;yp<height;yp++) {
				map.setTile(x+xp, y+yp, 0);
			}
		}
	}
	
	/**
	 * Get the island ID that has been assigned to this room
	 * 
	 * @return The island ID that has been assigned to this room
	 */
	public int getIslandID() {
		return islandID;
	}
	
	/**
	 * Floor fill starting at this room identifying any rooms
	 * that are connected directly, or indirectly to this one.
	 * 
	 * @param id The island ID to use for the fill
	 * @return The number of rooms that have been found linked together
	 */
	public int flood(int id) {
		islandID = id;
		int count = 1;
		
		for (int i=0;i<connections.size();i++) {
			Room target = connections.get(i);
			if (target.getIslandID() == -1) {
				count += target.flood(id);
			}
		}
		
		return count;
	}
	
	/**
	 * Prune unused doors from the map. Essentially search round finding
	 * any doors that haven't been used and remove them.s
	 */
	public void pruneDoors() {
		HashSet<Door> blocked = new HashSet<Door>();
		HashSet<Door> used = new HashSet<Door>();
		pruneDoorsSearch(blocked, used);
		
		blocked.removeAll(used);
		ArrayList<Door> removeMe = new ArrayList<Door>(blocked);
		for (int i=0;i<removeMe.size();i++) {
			removeMe.get(i).disconnect();
		}
	}
	
	/**
	 * Search for unused doors. If we attempt to go back our own path
	 * then the door isn't required.
	 * 
	 * @param blocked The set of doors that have been blocked and hence
	 * arn't being used
	 * @param used The set of doors that have been used for successful moves
	 */
	private void pruneDoorsSearch(HashSet<Door> blocked, HashSet<Door> used) {
		islandID = PRUNE_ID;
		
		for (int i=0;i<connections.size();i++) {
			Room target = connections.get(i);
			if (target.getIslandID() == -1) {
				used.add(doors.get(target));
				target.pruneDoorsSearch(blocked, used);
			} else {
				// if we've already been that route then 
				// the door isn't need, prune it
				blocked.add(doors.get(target));
			}
		}
	}
	
	/**
	 * Try to merge this room with any room it connects to, picked randomly
	 * 
	 * @param map The map we're working on
	 * @param random The seeded random number generator used for room selection
	 * @return The room thats been merged with or null if no room was found
	 */
	public Room tryToMerge(Map map, Random random) {
		if (hasMerged) {
			return null;
		}
		
		ArrayList<Room> possibleMerges = new ArrayList<Room>();
		for (int i=0;i<connections.size();i++) {
			if (canMerge(connections.get(i))) {
				possibleMerges.add(connections.get(i));
			}
		}
		
		if (possibleMerges.size() == 0) {
			return null;
		}
		hasMerged = true;
		Room mergeTarget = possibleMerges.get(random.nextInt(possibleMerges.size()));
		
		merge(map, mergeTarget);
		mergeTarget.hasMerged = true;
		
		return mergeTarget;		
	}
	
	/**
	 * Merge this room with another. The other room will be removed
	 * from the map. It's space will be assigned this room's ID and
	 * it's doors will be linked to this room
	 * 
	 * @param map The map we're working on
	 * @param target The target room to merge into this one
	 */
	private void merge(Map map, Room target) {
		// finally replace the rooms tile entries with the common ID
		target.place(map, id);
		
		doors.get(target).disconnect();
		
		// inherit all the doors
		ArrayList<Room> targetConnections = new ArrayList<Room>(target.connections);
		
		for (int i=0;i<targetConnections.size();i++) {
			Room conn = targetConnections.get(i);	

			// disconnect the doors in the old room
			Door door = target.doors.get(conn);
			
			door.disconnect();			
			if (doors.get(conn) != null) {
				continue;
			}
			if (conn == this) {
				throw new RuntimeException("Connecting a room to itself");
			}
			
			// reconnect to the door to the room
			// we've merged with and reassign it			
			door.replace(map, target, this);

			connections.add(conn);
			doors.put(conn, door);
			conn.connections.add(this);
			conn.doors.put(this, door);					
		}
		
	}
	
	/**
	 * Check if it's possible to merge this room with another. Rooms
	 * can only be moved if one of their sides is completely contained
	 * within the other room's adjacent side.
	 * 
	 * @param other The other room to compare against
	 * @return True if the rooms can be merged
	 */
	private boolean canMerge(Room other) {
		if (other.hasMerged) {
			return false;
		}
		if (other.corridor != corridor) {
			return false;
		}
		if ((other.x > x) && (other.x+other.width == x+width)) {
			return true;
		}
		if ((other.x == x) && (other.x+other.width < x+width)) {
			return true;
		}
		if ((other.y > y) && (other.y+other.height == y+height)) {
			return true;
		}
		if ((other.y == y) && (other.y+other.height < y+height)) {
			return true;
		}
		
		return false;
	}

	/**
	 * Get the ID assigned to this room
	 * 
	 * @return The ID assigned to this room
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Get a string representation of this room
	 * 
	 * @return A string representation of this room
	 */
	public String toString() {
		return "[Room "+x+","+y+" "+width+"x"+height+"  (ID: "+id+" ConnectionCount: "+connections.size()+")]";
	}

	/**
	 * Set the island ID in this room. Used for detection of linked up
	 * rooms.
	 * 
	 * @param i The ID to assign to this room 
	 */
	public void setIslandID(int i) {
		this.islandID = i;
	}

	/**
	 * Disconnect the given door from this room assuming it's connected
	 * to the given other room
	 * 
	 * @param door The door to disconnect
	 * @param other The other room that the door is connected to
	 */
	public void disconnect(Door door, Room other) {
		if (!connections.remove(other)) {
			throw new RuntimeException("Disconnected a door that isn't connected");			
		}
		if (doors.remove(other) == null) {
			throw new RuntimeException("Disconnected a door that isn't connected");
		}
	}
	
	/**
	 * Delete this room from the map
	 * 
	 * @param map The map to clear the room from
	 */
	public void delete(Map map) {
		clear(map);
		for (int i=0;i<connections.size();i++) {
			doors.get(connections.get(i)).disconnect();
		}
	}

	/**
	 * Get the width of the room
	 * 
	 * @return The width of the room
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Get the height of the room
	 * 
	 * @return The height of the room
	 */
	public int getHeight() {
		return height;
	}

	public boolean containsDoor(Door door, int x, int y, Room room) {
		return doors.get(room) == getDoor(x,y);
	}

	public void validateDoors(Map map) {
		for (int i=0;i<getDoorCount();i++) {
			getDoor(i).validate(map);
		}
	}
	
	private class UniqueList<T> extends ArrayList<T> {
		public boolean add(T item) {
			if (!contains(item)) {
				return super.add(item);
			}
			
			return false;
		}
	}
}
