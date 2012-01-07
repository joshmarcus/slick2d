package org.newdawn.warpstone;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class ActorType {
	public static ActorType PEASANT;
	public static ActorType SKELETON;
	public static ActorType SWORDSMAN;
	public static ActorType MAGE;
	public static ActorType ARCHER;
	
	public static final int NORTH = 0;
	public static final int NORTH_EAST = 1;
	public static final int EAST = 2;
	public static final int SOUTH_EAST = 3;
	public static final int SOUTH = 4;
	public static final int SOUTH_WEST = 5;
	public static final int WEST = 6;
	public static final int NORTH_WEST = 7;
	
	private SpriteSheet sheet;
	private Image[][] frames = new Image[8][20];
	private int[] walk;
	private int[] attack;
	private int[] die;
	private int index;
	
	public ActorType(int index, String res, int size, int[] walk, int[] attack, int[] die) throws SlickException {
		this(index, res, size, size, walk, attack, die);
	}
	
	public ActorType(int index, String res, int width, int height, int[] walk, int[] attack, int[] die) throws SlickException {
		this.walk = walk;
		this.attack = attack;
		this.die = die;
		this.index = index;
		
		sheet = new SpriteSheet(res, width, height);
		int tilesWidth = sheet.getWidth() / width;
		
		int frameCount = (tilesWidth * (sheet.getHeight() / height)) / 5;
		
		for (int frame=0;frame<frameCount;frame++) {
			for (int dir=0;dir<8;dir++) {
				int tile = dir;
				boolean flip = false;
				
				if (dir > SOUTH) {
					tile = 8 - dir;
					flip = true;
				}
				
				int tileIndex = (frame*5) + tile;
				int x = tileIndex % tilesWidth;
				int y = tileIndex / tilesWidth;
				
				frames[dir][frame] = sheet.getSubImage(x,y);
				if (flip) {
					frames[dir][frame] = frames[dir][frame].getFlippedCopy(true, false);
				}
			}
		}
	}
	
	public int getIndex() {
		return index;
	}
	
	public int[] getAttackCycle() {
		return attack;
	}
	
	public int[] getWalkCycle() {
		return walk;
	}

	public int[] getDieCycle() {
		return die;
	}
	
	public Image getImage(int dir, int frame) {
		return frames[dir][frame];
	}
}
