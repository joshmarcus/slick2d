package org.newdawn.slick.thingle.internal;

/**
 * A dimension implementation providing AWT without it
 * 
 * @author kevin
 */
public class Dimension {
	/** The width stored */
	public int width;
	/** The height stored */
	public int height;
	
	/**
	 * Create a new 0,0 dimension
	 */
	public Dimension() {
	}
	
	/**
	 * Create a dimension based on the given width/height
	 * 
	 * @param width The width of the dimension
	 * @param height The height of the dimension
	 */
	public Dimension(int width, int height) {
		this.width = width;
		this.height = height;
	}
}
