package org.newdawn.warpstone.dungeon;

import java.util.Random;

/**
 * A seed generated dungeon map consisting of a set of rooms and doors
 * 
 * @author kevin
 */
public class Map {
	/** The minimum size of any room that will be generated */
	public static final int ROOM_MIN_SIZE = 6;
	/** The maximum size of any room that will be genereated */
	public static final int ROOM_MAX_SIZE = 10;
	
	/** The number of times we can fail at placing a room before we give up */
	public static final int ALLOWED_PLACEMENT_FAILS = 5000;
	
	/** The tile data for the map, each room will be placed into the tile map */
	private int[][] data;
	/** The rooms that have been generated for the map - keyed on ID (not index!) */
	private Room[] rooms;
	/** The highest index of any room placed */
	private int highestRoomID;
	
	/** The width of the map in tiles */
	private int width = 50;
	/** The height of the map in tiles */
	private int height = 50;
	/** The maximum number of rooms that can be generated */
	private int roomCount = 300;
	
	private int startx;
	private int starty;
	
	private Room startRoom;
	private Room endRoom;
	
	/**
	 * Create a new map
	 * 
	 * @param width The width in tiles of the map
	 * @param height The height in tiles of the map
	 */
	public Map(int width, int height) {
		this.width = width;
		this.height = height;
		data = new int[width][height];
		rooms = new Room[roomCount];
	}
	
	/**
	 * Get the width of the map in tiles
	 * 
	 * @return The width of the map in tiles
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Get the height of the map in tiles
	 * 
	 * @return The height of the map in tiles
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Get the highest room ID that has been generated 
	 * 
	 * @return The highest room ID that has been generated
	 */
	public int getHighestRoomID() {
		return highestRoomID;
	}
	
	/**
	 * Set the tile at a given location
	 * 
	 * @param x The x coordinate at which to set the tile
	 * @param y The y coordinate at which to set the tile
	 * @param tile The tile to set or 0 to clear
	 */
	public void setTile(int x, int y, int tile) {
		data[x][y] = tile;
	}
	
	/**
	 * Get the tile at a given location
	 * 
	 * @param x The x coordinate at which to get the tile
	 * @param y The y coordinate at which to get the tile
	 * @return The tile at the given location
	 */
	public int getTile(int x, int y) {
		if ((x < 0) || (y < 0) || (x >= width) || (y >= height)) {
			return 0;
		}
		
		return data[x][y];
	}
	
	/**
	 * Generate a map
	 * 
	 * @param seed The seed for the random number generator
	 * @param minRooms The minimum number of rooms to accept in a generation
	 * @param mergeIterations How many times we'll try to merge rooms together
	 */
	public void generate(long seed, int minRooms, int mergeIterations, int corridorCount) {
		// create a random number generate
		// that we'll use for everything so we can
		// always generate the exact same map again
		Random random = new Random(seed);
		int totalRooms = 0;
		
		while (totalRooms < minRooms) {
			// initialise the generation data structures
			data = new int[width][height];
			rooms = new Room[roomCount];
			highestRoomID = 0;
			totalRooms = 0;
			int failCount = 0;
			
			// keep placing random sized rooms and random locations until we've
			// generated enough or we've failed too many times.
			while ((failCount < ALLOWED_PLACEMENT_FAILS) && (highestRoomID < roomCount-1)) {
				// random size and position
				int roomWidth = ROOM_MIN_SIZE + random.nextInt(ROOM_MAX_SIZE-ROOM_MIN_SIZE);
				int roomHeight = ROOM_MIN_SIZE + random.nextInt(ROOM_MAX_SIZE-ROOM_MIN_SIZE);
				
				boolean isCorridor = false;
				if (corridorCount > 0) {
					corridorCount--;
					if (random.nextFloat() > 0.5f) {
						roomWidth = 2;
						roomHeight *= 2; 
					} else {
						roomHeight = 2;
						roomWidth *= 2;
					}
					isCorridor = true;
				}
				
				int roomX = 1+random.nextInt((width - roomWidth)-2);
				int roomY = 2+random.nextInt((height - roomHeight)-3);
				
				// ensure that rooms at only position in even tiles, this is
				// to line rooms up better and give more chance for doors
				int alignment = 2;
				roomX = (roomX / alignment) * alignment;
				roomY = (roomY / alignment) * alignment;
				roomWidth = (roomWidth / alignment) * alignment;
				roomHeight = (roomHeight / alignment) * alignment;
				
				// Check there isn't already a room placed at the specified
				// size and location.
				boolean clear = true;
				for (int x=roomX;x<roomX+roomWidth;x++) {
					for (int y=roomY;y<roomY+roomHeight;y++) {
						if (getTile(x,y) != 0) {
							clear = false;
							break;
						}
					}
				}
				// if it's a corridor then check a bit further a field for 
				// corridors, don't want them too close together
				if (isCorridor) {
					for (int x=roomX-1;x<roomX+roomWidth+1;x++) {
						for (int y=roomY-1;y<roomY+roomHeight+1;y++) {
							Room room = getRoomByID(getTile(x,y));
							if (room != null) {
								if (room.isCorridor()) {
									clear = false;
								}
								break;
							}
						}
					}
				}
				
				if (clear) {
					// if there isn't a room at the given location, create a new one
					// and fill the map in for it's ID. Store the room away.
					highestRoomID++;
					totalRooms++;
					Room room = new Room(highestRoomID,roomX,roomY,roomWidth,roomHeight,isCorridor);
					room.place(this);
					rooms[highestRoomID] = room;
				} else {
					failCount++;
				}
			}

			// places doors to link adjacent rooms
			for (int i=0;i<highestRoomID;i++) {
				rooms[i+1].placeDoors(this, random);
			}
			
			// prune islands - this is important since we want to be 
			// able to explore the dungeon fully starting in any room - this 
			// means that all rooms must be linked together some how. So, we 
			// go through each room "filling" is with a new ID, this causes any rooms
			// linked to it, and rooms linked to those and so on, to be filled with the
			// same ID. If we find any rooms without an "island ID" then they are completely
			// separated.
			int islandID = 1;
			int bestCount = 0;
			int bestIsland = 0;
			
			for (int i=0;i<highestRoomID;i++) {
				Room room = rooms[i+1];
				if (room.getIslandID() == -1) {
					int count = room.flood(islandID);
					if (count > bestCount) {
						bestCount = count;
						bestIsland = islandID;
					}
					islandID++;
				}
			}
			
			// remove any rooms that don't form part of the biggest
			// island, leaving us with one big dungeon area to explore
			Room lastRoom = null;
			for (int i=0;i<highestRoomID;i++) {
				Room room = rooms[i+1];
				if (room.getIslandID() != bestIsland) {
					room.clear(this);
					rooms[i+1] = null;
					totalRooms--;
				} else {
					lastRoom = room;
				}
			}
			
			// prune unused doors by flood filling to find
			// the doors that don't need to be there
			for (int i=0;i<highestRoomID;i++) {
				Room room = rooms[i+1];
				if (room != null) {
					room.setIslandID(-1);
				}
			}
			lastRoom.pruneDoors();
			
			// make sure we're still able to fill the map
			for (int i=0;i<highestRoomID;i++) {
				Room room = rooms[i+1];
				if (room != null) {
					room.setIslandID(-1);
				}
			}
			int newCount = lastRoom.flood(3);
			if (newCount != bestCount) {
				throw new RuntimeException("Lost connectivity");
			}
			
			// limit the corridors size
			for (int i=0;i<highestRoomID;i++) {
				Room room = rooms[i+1];
				if (room != null) {
					if (room.isCorridor()) {
						if (room.getDoorCount() == 1) {
							room.delete(this);
							rooms[i+1] = null;
							totalRooms--;
						} else {
							room.limitCorridor(this);
						}
					}
				}
			}

			for (int i=0;i<highestRoomID;i++) {
				Room room = rooms[i+1];
				if (room != null) {
					room.validateDoors(this);
				}
			}
			
			// try to merge rooms together into interesting 
			// shapes
			for (int i=0;i<mergeIterations;i++) {
				Room target = rooms[random.nextInt(highestRoomID)];
				if (target != null) {
					Room mergedOut = target.tryToMerge(this, random);
					if (mergedOut != null) {
						if (rooms[mergedOut.getID()] == null) {
							throw new RuntimeException("Merged out an already disconnected room");
						} else {
							rooms[mergedOut.getID()] = null;
							totalRooms--;
						}
					}
				}
			}
		}
		
		generateFeatures();
	}
	
	private Room pickRandomRoom() {
		int count = (int) (Math.random() * roomCount);
		int roomIndex = 0;
		
		while (count > 0) {
			roomIndex++;
			if (roomIndex >= rooms.length) {
				roomIndex = 0;
			}
			if (rooms[roomIndex] != null) {
				count--;
			}
		}
		
		return rooms[roomIndex];
	}
	
	private void generateFeatures() {
		endRoom = startRoom = pickRandomRoom();
		while (endRoom == startRoom) {
			endRoom = pickRandomRoom();
		}
		
		startx = startRoom.getX() + (startRoom.getWidth()/2);
		starty = startRoom.getY() + (startRoom.getHeight()/2);
	}
	
	public Room getStartRoom() {
		return startRoom;
	}
	
	public Room getEndRoom() {
		return endRoom;
	}
	
	public int getStartX() {
		return startx;
	}
	
	public int getStartY() {
		return starty;
	}
	
	/**
	 * Get a room by it's ID
	 * 
	 * @param id The ID of the room to retrieve
	 * @return The room if it exists, or null
	 */
	public Room getRoomByID(int id) {
		return rooms[id];
	}
	
	public boolean isDoor(int x, int y) {
		int id = getTile(x, y);
		
		if (id != 0) {
			return getRoomByID(id).isDoor(x,y);
		}
		
		return false;
	}
}

