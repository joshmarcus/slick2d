package org.newdawn.slick.thingle.spi;

import java.io.InputStream;
import java.net.URL;

import org.newdawn.slick.thingle.internal.ThinletInputListener;

/**
 * The context holding all the elements of the SPI implementation. 
 * 
 * @author kevin
 */
public interface ThingleContext {
	/**
	 * Create a the utility implementation. A new instance should
	 * be created.
	 * 
	 * @return The utility implementation
	 */
	public ThingleUtil createUtil();
	
	/**
	 * Create the input implementation for this SP. A new instance should
	 * be created.
	 * 
	 * @param listener The listener to notify of input events
	 * @return The input implemetation instance
	 */
	public ThingleInput createInput(ThinletInputListener listener);

	/**
	 * Create a color
	 * 
	 * @param col The 0xAARRGGBB encoding of the color
	 * @return A colour object representing the value given
	 */
	public ThingleColor createColor(int col);

	/**
	 * Create a colour
	 * 
	 * @param red The red component of the colour
	 * @param green The green component of the colour
	 * @param blue The blue component of the colour
	 * @return The newly created colour
	 */
	public ThingleColor createColor(int red, int green, int blue);

	/**
	 * Create a colour
	 * 
	 * @param red The red component of the colour
	 * @param green The green component of the colour
	 * @param blue The blue component of the colour
	 * @param alpha The alpha component of the colour
	 * @return The newly created colour
	 */
	public ThingleColor createColor(int red, int green, int blue, int alpha);
	
	/**
	 * Get the default font to use for the GUI
	 * 
	 * @return Default to use for thinlet GUIs
	 */
	public ThingleFont getDefaultFont();
	
	/**
	 * Log a warning message
	 * 
	 * @param message The message to be logged
	 */
	public void log(String message);

	/**
	 * Log a error message
	 * 
	 * @param message The message to be logged
	 * @param e The exception to log
	 */
	public void log(String message, Throwable e);

	/**
	 * Log a error message
	 * 
	 * @param e The exception to log
	 */
	public void log(Throwable e);
	
	/**
	 * Get a resource by a name, normally via the class path
	 * 
	 * @param ref The reference to the resource
	 * @return An input stream to read the resource
	 */
	public InputStream getResourceAsStream(String ref);

	/**
	 * Get a resource by a name, normally via the class path
	 * 
	 * @param ref The reference to the resource
	 * @return A URL to the resource
	 */
	public URL getResource(String ref);
	
	/**
	 * Create an image based on this SPI
	 * 
	 * @param in The input stream from which to read the image
	 * @param name The name of the image resource
	 * @param flipped True if the image should be flipped vertically
	 * @return The image that's been loaded
	 * @throws ThingleException
	 */
	public ThingleImage createImage(InputStream in, String name, boolean flipped) throws ThingleException;
	
	/**
	 * Create an image buffer in this SPI
	 * 
	 * @param width The width of the buffer
	 * @param height The height of the buffer
	 * @return The image buffer implementation instance
	 */
	public ThingleImageBuffer createImageBuffer(int width, int height);
	
	/**
	 * Get a graphics context specific to this SPI
	 * 
	 * @return The graphics context for this SPI
	 */
	public ThingleGraphics getGraphics();
	
	/**
	 * Get the width of the graphics context for this SPI (in pixels)
	 * 
	 * @return The width of the graphcis context for this SPI
	 */
	public int getWidth();

	/**
	 * Get the height of the graphics context for this SPI (in pixels)
	 * 
	 * @return The height of the graphcis context for this SPI
	 */
	public int getHeight();
	
	/**
	 * Create a font with the given specifications
	 * 
	 * @param face The name of the type face of the font
	 * @param style The style of font as defined by java.awt.Font
	 * @param size The size of the font
	 * @return A thinlet font created from the given information
	 */
	public ThingleFont createFont(String face, int style, int size);

	/**
	 * Create a font with the given specifications
	 * 
	 * @param ref The reference to the bitmap font definition file
	 * @param image The reference to the image file
	 * @return A thinlet font created from the given information
	 */
	public ThingleFont createBitmapFont(String ref, String image);
	
	/**
	 * Notification of rendering about to happen. Store GL state 
	 * here
	 */
	public void doPreRender();

	/**
	 * Notification of rendering happened. Restore GL state 
	 * here
	 */
	public void doPostRender();
}
