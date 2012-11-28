package org.newdawn.slick.thingle.spi;

import org.newdawn.slick.Image;
import org.newdawn.slick.thingle.internal.Rectangle;

/**
 * The contract thinlet requires from it's graphics rendering
 * 
 * @author kevin
 */
public interface ThingleGraphics {
	/**
	 * Get the font currently configured 
	 * 
	 * @return The font currently configured
	 */
	public ThingleFont getFont();
	
	/**
	 * Set the font to make use of when rendering text
	 * 
	 * @param font The font to use
	 */
	public void setFont(ThingleFont font);
	
	/**
	 * Draw an image to the graphics context
	 * 
	 * @param image The image to draw
	 * @param x The x coordinate to draw at
	 * @param y The y coordiante to draw at
	 * @param x2 The x coordinate of the lower right corner of the image
	 * @param y2 The y coordinate of the lower right corner of the image
	 * @param sx1 The x coordinate of the source top left corner
	 * @param sy1 The y coordinate of the source top left corner
	 * @param sx2 The x coordinate of the source bottom right corner
	 * @param sy2 The y coordinate of the source bottom right corner
	 */
	public void drawImage(ThingleImage image, int x, int y, int x2, int y2, int sx1, int sy1, int sx2, int sy2);
		
	/**
	 * Draw an image to the context
	 * 
	 * @param image The image to draw
	 * @param x The x coordinate to draw at
	 * @param y The y coordiante to draw at
	 */
	public void drawImage(ThingleImage image, int x, int y);
	
	/**
	 * Draw a line to the context
	 * 
	 * @param x The x coordiante of the first point
	 * @param y The y coordiante of the first point
	 * @param x2 The x coordiante of the second point
	 * @param y2 The y coordiante of the second point
	 */
	public void drawLine(int x, int y, int x2, int y2);
	
	/**
	 * Draw an oval to the graphics context
	 * 
	 * @param x The x coordinate to draw at
	 * @param y The y coordiante to draw at
	 * @param width The width of the oval to draw
	 * @param height The height of the oval to draw
	 */
	public void drawOval(int x, int y, int width, int height);

	/**
	 * Draw an rectangle to the graphics context
	 * 
	 * @param x The x coordinate to draw at
	 * @param y The y coordiante to draw at
	 * @param width The width of the rectangle to draw
	 * @param height The height of the rectangle to draw
	 */
	public void drawRect(int x, int y, int width, int height);

	/**
	 * Fill an oval to the graphics context
	 * 
	 * @param x The x coordinate to draw at
	 * @param y The y coordiante to draw at
	 * @param width The width of the oval to fill
	 * @param height The height of the oval to fill
	 */
	public void fillOval(int x, int y, int width, int height);

	/**
	 * Fill an rectangle to the graphics context
	 * 
	 * @param x The x coordinate to draw at
	 * @param y The y coordiante to draw at
	 * @param width The width of the rectangle to fill
	 * @param height The height of the rectangle to fill
	 */
	public void fillRect(int x, int y, int width, int height);
	
	/**
	 * Get the current clip of the graphics context
	 * 
	 * @return The clip of the graphics context
	 */
	public Rectangle getClip();
	
	/**
	 * Set the clip of the graphics context
	 * 
	 * @param x The x coodinate of the top left of the clip area
	 * @param y The y coorinate of the top left of the clip area
	 * @param width The width of the clip area
	 * @param height The height of the clip area
	 */
	public void setClip(int x, int y, int width, int height);
	
	/**
	 * Set the colour to use when drawing
	 * 
	 * @param color The colour to use when drawing
	 */
	public void setColor(ThingleColor color);
	
	/**
	 * Draw a string to the screen
	 * 
	 * @param str The string to write to the screen
	 * @param x The x coordinate to draw the text at
	 * @param y The y coordinate to draw the text at (top of text)
	 */
	public void drawString(String str, int x, int y);
	
	/**
	 * Translate the graphics context 
	 * 
	 * @param x The x factor to translate by
	 * @param y The y factor to translate by
	 */
	public void translate(int x, int y);

	public int getTranslationY ();

	public int getTranslationX ();
}
