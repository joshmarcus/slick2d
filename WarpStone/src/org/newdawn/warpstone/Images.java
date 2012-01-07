package org.newdawn.warpstone;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class Images {
	public static SpriteSheet TILES_SHEET;
	public static SpriteSheet FACES;
	public static Image MAP;
	
	public static void init() throws SlickException {
		TILES_SHEET = new SpriteSheet("res/tiles.png", 32, 32);
		FACES = new SpriteSheet("res/faces.png", 46, 36);
		MAP = new Image("res/map.png");
	}
}
