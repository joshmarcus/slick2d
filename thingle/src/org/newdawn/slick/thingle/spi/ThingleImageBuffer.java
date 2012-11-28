package org.newdawn.slick.thingle.spi;

/**
 * An image buffer that can be used to do pixel operations before 
 * creating a image.
 * 
 * @author kevin
 */
public interface ThingleImageBuffer {
	/**
	 * Set a given pixel to the given colour value
	 * 
	 * @param x The x coordinate of the pixel to set
	 * @param y The y coordiante of the pixel to set
	 * @param r The red component of the colour to set
	 * @param g The green component of the colour to set
	 * @param b The blue component of the colour to set
	 * @param a The alpha component of the colour to set
	 */
	public void setRGBA(int x, int y, int r, int g, int b, int a);
	
	/**
	 * Get/Create the image defined by this buffer
	 * 
	 * @return The image created from the buffer
	 */
	public ThingleImage getImage();
}
