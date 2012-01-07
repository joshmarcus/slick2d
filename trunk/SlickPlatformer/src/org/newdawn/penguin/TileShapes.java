package org.newdawn.penguin;

import java.util.HashMap;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;

/**
 * The set of shapes supported for different tiles in the tiled environment
 * 
 * @author kevin
 */
public class TileShapes {
	/** The map of names to shapes */
	public static final HashMap<String, Shape> shapes = new HashMap<String, Shape>();
	/** The shape for the standard block tile */
	public static final Polygon BLOCK;
	/** The shape for the left slope tile */
	public static final Polygon LEFTSLOPE;
	/** The shape for the right slope tile */
	public static final Polygon RIGHTSLOPE;
	
	static {
		BLOCK = new Polygon();
		BLOCK.addPoint(0,0);
		BLOCK.addPoint(1,0);
		BLOCK.addPoint(1,1);
		BLOCK.addPoint(0,1);
		shapes.put("BLOCK", BLOCK);
		LEFTSLOPE = new Polygon();
		LEFTSLOPE.addPoint(1,0);
		LEFTSLOPE.addPoint(1,1);
		LEFTSLOPE.addPoint(0,1);
		shapes.put("LEFTSLOPE", LEFTSLOPE);
		RIGHTSLOPE = new Polygon();
		RIGHTSLOPE.addPoint(0,0);
		RIGHTSLOPE.addPoint(1,1);
		RIGHTSLOPE.addPoint(0,1);
		shapes.put("RIGHTSLOPE", RIGHTSLOPE);
	}
	
	/**
	 * Get a piece of geometry by name specified in the tile XML
	 * 
	 * @param name The name of the geometry to retrieve
	 * @return The shape to represent the given tile type
	 * @throws SlickException Indicates a failure to locate the geometry for the given name
	 */
	public static Shape getShapeByName(String name) throws SlickException {
		Shape shape = shapes.get(name);
		if (shape == null) {
			throw new SlickException("Unrecognised shape: "+name);
		}
		
		return shape;
	}
}
