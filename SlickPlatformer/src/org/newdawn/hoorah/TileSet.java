package org.newdawn.hoorah;

import java.util.HashMap;

import org.newdawn.penguin.Tile;
import org.newdawn.penguin.TileShapes;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.util.xml.XMLElement;
import org.newdawn.slick.util.xml.XMLElementList;
import org.newdawn.slick.util.xml.XMLParser;

/**
 * A utility class to read a set of tile definitions from XML. A tile
 * definition describes the image to display and the geometry to use 
 * for it.
 * 
 * @author kevin
 */
public class TileSet {
	/** The tile definitions read from the XML */
	private HashMap<Character, Tile> entries = new HashMap<Character, Tile>();
	
	/**
	 * Create a new tileset by loading an XML file
	 * 
	 * @param ref The reference to the XML file to load
	 * @throws SlickException Indicates a failure to locate or parse the XML
	 */
	public TileSet(String ref) throws SlickException {
		XMLParser parser = new XMLParser();
		XMLElement root = parser.parse(ref);
		
		XMLElementList list = root.getChildrenByName("tile");
		for (int i=0;i<list.size();i++) {
			XMLElement element = list.get(i);
			char id = element.getAttribute("id").charAt(0);
			String image = element.getAttribute("image");
			Shape shape = TileShapes.getShapeByName(element.getAttribute("shape"));
			
			entries.put(id, new Tile(new Image(image), shape));
		}
	}
	
	/**
	 * Get a simple tile definition based on a given character
	 * 
	 * @param c The character identifing the tile
	 * @return The tile definition
	 */
	public Tile getTile(char c) {
		return entries.get(c);
	}
}
