package org.newdawn.slick.thingle.internal.slick;

import org.newdawn.slick.Color;
import org.newdawn.slick.thingle.spi.ThingleColor;

/**
 * A wrapped making a Slick colour looks like a ThinletColor
 * 
 * @author kevin
 */
public class ColorWrapper implements ThingleColor {
	/** The color wrapped */
	private Color color;
	
	/**
	 * Create a new colour
	 * 
	 * @param r The red component of the colour (0-255)
	 * @param g The green component of the colour (0-255)
	 * @param b The blue component of the colour (0-255)
	 */
	public ColorWrapper(int r, int g, int b) {
		color = new Color(r, g, b);
	}

	/**
	 * Create a new colour
	 * 
	 * @param r The red component of the colour (0-255)
	 * @param g The green component of the colour (0-255)
	 * @param b The blue component of the colour (0-255)
	 * @param a The alpha component of the colour (0-255)
	 */
	public ColorWrapper(int r, int g, int b, int a) {
		color = new Color(r, g, b, a);
	}

	/**
	 * Create a colour from an evil integer packed 0xAARRGGBB
	 * 
	 * @param value The value to interpret for the colour
	 */
	public ColorWrapper(int value) {
		color = new Color(value);
	}

	/**
	 * Create a wrapper round the given colour
	 * 
	 * @param col The colour to wrap
	 */
	public ColorWrapper(Color col) {
		this.color = col;
	}
	
	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleColor#brighter()
	 */
	public ThingleColor brighter() {
		return new ColorWrapper(color.brighter());
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleColor#darker()
	 */
	public ThingleColor darker() {
		return new ColorWrapper(color.darker());
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleColor#getBlue()
	 */
	public int getBlue() {
		return color.getBlue();
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleColor#getGreen()
	 */
	public int getGreen() {
		return color.getGreen();
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleColor#getRed()
	 */
	public int getRed() {
		return color.getRed();
	}
	
	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleColor#getAlpha()
	 */
	public int getAlpha() {
		return color.getAlpha();
	}

	/**
	 * Get the slick colour wrapped 
	 * 
	 * @return The slick colour wrapped
	 */
	public Color getSlickColor() {
		return color;
	}
}
