package org.newdawn.slick.thingle.internal.slick;

import org.newdawn.slick.Font;
import org.newdawn.slick.thingle.spi.ThingleFont;

/**
 * A wrapper to make a Slick font look like a Thinlet font
 * 
 * @author kevin
 */
public class FontWrapper implements ThingleFont {
	/** The font wrapped */
	private Font font;
	/** The family */
	private String family;
	/** The size */
	private int size;
	
	/**
	 * Create a new font wrapped
	 * 
	 * @param font The font to be wrapped
	 */
	public FontWrapper(Font font) {
		this.font = font;
	}
	
	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleFont#getLineHeight()
	 */
	public int getLineHeight() {
		return font.getLineHeight();
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleFont#getWidth(java.lang.String)
	 */
	public int getWidth(String str) {
		return font.getWidth(str);
	}

	/**
	 * Get the wrapped font
	 * 
	 * @return The slick font that is wrapped up
	 */
	public Font getSlickFont() {
		return font;
	}

	/**
	 * Configure the font
	 * 
	 * @param family The family
	 * @param size The size
	 */
	public void configure(String family, int size) {
		this.family = family;
		this.size = size;
	}
	
	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleFont#getFamily()
	 */
	public String getFamily() {
		return family;
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleFont#getSize()
	 */
	public int getSize() {
		return size;
	}
}
