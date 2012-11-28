package org.newdawn.penguin;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Shape;

/**
 * A single tile within a tiled environment
 * 
 * @author kevin
 */
public class Tile {
	/** The geometry to use to represent the tile */
	private Shape shape;
	/** The image to draw for the tile */
	private Image image;
	
	/** 
	 * Create a new tile
	 * 
	 * @param image The image to be drawn for this tile
	 * @param shape The geometry to represent this tile in the physical world
	 */
	public Tile(Image image, Shape shape) {
		this.shape = shape;
		this.image = image;
	}
	
	/**
	 * Get the image to be drawn for this tile
	 * 
	 * @return The image to be drawn for this tile
	 */
	public Image getImage() {
		return image;
	}
	
	/**
	 * Get the geometry to be used in the physical world for this tile
	 * 
	 * @return The geometry to be used in the physical world for this tile
	 */
	public Shape getShape() {
		return shape;
	}
}
