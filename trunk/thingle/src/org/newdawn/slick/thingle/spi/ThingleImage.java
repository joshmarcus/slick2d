package org.newdawn.slick.thingle.spi;

/**
 * The contract for thinlets use of images
 * 
 * @author kevin
 */
public interface ThingleImage {
	/**
	 * Get the width of the image
	 * 
	 * @return The width of the image in pixels
	 */
	public int getWidth();
	
	/**
	 * Get the height of the image 
	 * 
	 * @return The height of the image in pixels
	 */
	public int getHeight();
}
