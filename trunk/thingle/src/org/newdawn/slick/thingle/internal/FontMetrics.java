package org.newdawn.slick.thingle.internal;

import org.newdawn.slick.thingle.spi.ThingleFont;

/**
 * A font metrics to provide AWT like features
 * 
 * @author kevin
 */
public class FontMetrics {
	/** The slick font wrapped */
	private ThingleFont font;
	
	/** 
	 * Create a new metrics based on the given font
	 * 
	 * @param font The font to wrap
	 */
	public FontMetrics(ThingleFont font) {
		this.font = font;
	}
	
	/**
	 * Get the pixel width of a given character
	 * 
	 * @param c The character to get the width of
	 * @return The width of the character
	 */
	public int charWidth(char c) {
		return font.getWidth(""+c);
	}
	
	/**
	 * Get the height of the font 
	 * 
	 * @return The height of the font
	 */
	public int getHeight() {
		return font.getLineHeight();
	}
	
	/**
	 * Get the width of a given character sequence
	 * 
	 * @param data The character sequence
	 * @param start The start index into the sequence
	 * @param length The length of the string
	 * @return The pixel width
	 */
	public int charsWidth(char[] data, int start, int length) {
		return font.getWidth(new String(data, start, length));
	}
	
	/**
	 * The pixel width of the string
	 * 
	 * @param str The string to get the width
	 * @return The pixel width
	 */
	public int stringWidth(String str) {
		return font.getWidth(str);
	}
	
	/**
	 * Get the ascent of the font
	 * 
	 * @return The ascent of the font
	 */
	public int getAscent() {
		return font.getLineHeight();
	}
	
	/**
	 * Get the leading entry of the font
	 * 
	 * @return The leading entry of the font
	 */
	public int getLeading() {
		return 0;
	}
	
	/**
	 * Get the descent of the font
	 * 
	 * @return The descent of the font
	 */
	public int getDescent() {
		return 1;
	}
}
