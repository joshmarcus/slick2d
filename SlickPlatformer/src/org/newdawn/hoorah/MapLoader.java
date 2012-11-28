package org.newdawn.hoorah;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.newdawn.penguin.Tile;
import org.newdawn.penguin.TileEnvironment;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.ResourceLoader;

/**
 * A loader for a very simple tile based map text format that maps from
 * characters in a text file to tile definitions in an XML file.
 * 
 * @author kevin
 */
public class MapLoader {
	/** The definition of each tile mapped to a single character */
	private TileSet set;
	
	/**
	 * Create a new loader using a specific set of tile definitions
	 * 
	 * @param set The tileset to load from while loading maps
	 */
	public MapLoader(TileSet set) {
		this.set = set;
	}
	
	/**
	 * Load a map from the given resource reference. The map is parsed character for
	 * character and a set of tiles equal to the size of the text file is creatd.
	 * 
	 * @param ref The reference to the map to load
	 * @return The configured enviornment thats been populated with tiles
	 * @throws SlickException Indicates a failure to locate or read the tile map
	 */
	public TileEnvironment load(String ref) throws SlickException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(ResourceLoader.getResourceAsStream(ref)));
		int width = 0;
		ArrayList<String> lines = new ArrayList<String>();
		
		// read through the lines recording them into a list and
		// determining the maximum width.
		try {
			while (reader.ready()) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
			
				width = Math.max(line.length(), width);
				lines.add(line);
			}
		} catch (IOException e) {
			throw new SlickException("Failed to load map: "+ref, e);
		}
		
		int height = lines.size();
		// create an environment based on the number of lines in the file 
		// and maximum length of any line in the file
		TileEnvironment env = new TileEnvironment(width,height);
		
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				char c = lines.get(y).charAt(x);
				Tile tile = set.getTile(c);
				if (tile != null) {
					env.setTile(x, y, tile);
				}
			}
		}
		
		return env;
	}
}
