package org.newdawn.warpstone.dungeon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Bootstrap;

/**
 * A quick Slick based viewer for the dungeon generator to test
 * out the algorithm. 
 * 
 * @author kevin
 */
public class DungeonViewer extends BasicGame {
	/** The colour set to use for rendering the dungeon */
	private static final Color[] cols = new Color[] {Color.red, Color.green, Color.blue, Color.yellow, Color.cyan, Color.magenta, Color.orange};
	/** The map we're generating */
	private Map map;
	/** True if we're showing island IDs */
	private boolean showIslands = false;
	
	/**
	 * Create a basic game
	 */
	public DungeonViewer() {
		super("Dungeon Viewer");
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		container.setShowFPS(false);

		long seed = System.currentTimeMillis();
		System.out.println("SEED: "+seed);
		map = new Map(60,40);
		map.generate(seed, 20, 0, 0);
		
		try {
			saveMap(map);
		} catch (IOException e) {
			// TODO Auto-generated catch bloc
			e.printStackTrace();
		}
	}

	private void saveMap(Map map) throws IOException {
		OutputStream out = new FileOutputStream(new File("map.dat"));
		out.write((byte)map.getWidth());
		out.write((byte)map.getHeight());
		for (int x=0;x<map.getWidth();x++) {
			for (int y=0;y<map.getHeight();y++) {
				out.write((byte) map.getTile(x,y));
				out.write((byte) (map.isDoor(x,y) ? 1 : 0));
			}
		}
		out.close();
	}
	
	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		if (container.getInput().isKeyPressed(Input.KEY_ENTER)) {
			showIslands = !showIslands;
		}
		if (container.getInput().isKeyPressed(Input.KEY_SPACE)) {
			map.generate(System.currentTimeMillis(),20,0,0);
			try {
				saveMap(map);
			} catch (IOException e) {
				// TODO Auto-generated catch bloc
				e.printStackTrace();
			}
		}
	}

	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		for (int x=0;x<map.getWidth();x++) {
			for (int y=0;y<map.getHeight();y++) {
				int tile = map.getTile(x,y);
				if (tile != 0) {
					g.setColor(cols[tile % cols.length]);
					if (map.getRoomByID(tile).isCorridor()) {
						g.setColor(Color.gray);
					}
					if (showIslands) {
						g.setColor(cols[map.getRoomByID(tile).getIslandID() % cols.length]);
					}
					g.fillRect(1+(x*10),1+(y*10),9,9);
					
					g.setColor(Color.lightGray);
					g.setLineWidth(3);
					if (map.getTile(x-1,y) != tile) {
						g.drawLine((x*10),(y*10),(x*10),(y*10)+10);
					}
					if (map.getTile(x+1,y) != tile) {
						g.drawLine((x*10)+10,(y*10),(x*10)+10,(y*10)+10);
					}
					if (map.getTile(x,y-1) != tile) {
						g.drawLine((x*10),(y*10),(x*10)+10,(y*10));
					}
					if (map.getTile(x,y+1) != tile) {
						g.drawLine((x*10),(y*10)+10,(x*10)+10,(y*10)+10);
					}
				}
			}
		}

		for (int x=0;x<map.getWidth();x++) {
			for (int y=0;y<map.getHeight();y++) {
				if (map.isDoor(x, y)) {
					g.setColor(Color.white);
					g.drawRect(x*10,y*10,10,10);
					
					//Door door = map.getRoomByID(map.getTile(x, y)).getDoor(x,y);
					//g.drawRect(door.getAX()*10,door.getAY()*10,10,10);
					//g.drawRect(door.getBX()*10,door.getBY()*10,10,10);
				}
			}
		}
	}

	/**
	 * Entry point to out application
	 * 
	 * @param argv The arguments passed to the application
	 */
	public static void main(String[] argv) {
		Bootstrap.runAsApplication(new DungeonViewer(), 600, 600, false);
	}
}
