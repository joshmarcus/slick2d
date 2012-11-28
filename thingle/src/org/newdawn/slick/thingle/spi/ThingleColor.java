package org.newdawn.slick.thingle.spi;

/**
 * A colour represented in Thinlet
 * 
 * @author kevin
 */
public interface ThingleColor {
	/** 
	 * Get the red component of the colour (0-255)
	 * 
	 * @return The red component of the colour
	 */
	public int getRed();

	/** 
	 * Get the blue component of the colour (0-255)
	 * 
	 * @return The blue component of the colour
	 */
	public int getBlue();

	/** 
	 * Get the green component of the colour (0-255)
	 * 
	 * @return The green component of the colour
	 */
	public int getGreen();

	/** 
	 * Get the alpha component of the colour (0-255)
	 * 
	 * @return The alpha component of the colour
	 */
	public int getAlpha();
	
	/**
	 * Get a darker version of the colour
	 * 
	 * @return The darker copy of the colour
	 */
	public ThingleColor darker();

	/**
	 * Get a brighter version of the colour
	 * 
	 * @return The brighter copy of the colour
	 */
	public ThingleColor brighter();
}
