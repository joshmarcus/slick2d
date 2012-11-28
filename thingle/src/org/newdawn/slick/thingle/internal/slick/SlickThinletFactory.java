package org.newdawn.slick.thingle.internal.slick;

import java.io.InputStream;
import java.net.URL;

import org.lwjgl.Sys;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.SlickCallable;
import org.newdawn.slick.thingle.internal.ThinletInputListener;
import org.newdawn.slick.thingle.spi.ThingleColor;
import org.newdawn.slick.thingle.spi.ThingleContext;
import org.newdawn.slick.thingle.spi.ThingleException;
import org.newdawn.slick.thingle.spi.ThingleFont;
import org.newdawn.slick.thingle.spi.ThingleGraphics;
import org.newdawn.slick.thingle.spi.ThingleImage;
import org.newdawn.slick.thingle.spi.ThingleImageBuffer;
import org.newdawn.slick.thingle.spi.ThingleInput;
import org.newdawn.slick.thingle.spi.ThingleUtil;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

/**
 * A SPI implementation for Slick
 * 
 * @author kevin
 */
public class SlickThinletFactory implements ThingleContext, ThingleUtil {
	/** The default font in Slick */
	private TrueTypeFont font = new TrueTypeFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12), false);
	/** The default font for Thinlet */
	private FontWrapper defaultFont = new FontWrapper(font);
	/** The game container running this implementation */
	private GameContainer container;
	
	/** The stored font */
	private Font storedFont;
	/** The stored colour */
	private Color storedColor;
	
	/**
	 * Create a new context
	 * 
	 * @param container The container the context will be rendered to
	 */
	public SlickThinletFactory(GameContainer container) {
		this.container = container;
		container.getInput().setDoubleClickInterval(250);
	}
	
	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#createFont(java.lang.String, int, int)
	 */
	public ThingleFont createFont(String face, int style, int size) {
		ThingleFont font = createThingleFont(new TrueTypeFont(new java.awt.Font(face, style, size), true));
		
		((FontWrapper) font).configure(face, size);
		return font;
	}
	
	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#createUtil()
	 */
	public ThingleUtil createUtil() {
		return this;
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleUtil#getClipboard()
	 */
	public String getClipboard() {
		return (String) Sys.getClipboard();
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#createColor(int)
	 */
	public ThingleColor createColor(int col) {
		return new ColorWrapper(col);
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#createColor(int, int, int)
	 */
	public ThingleColor createColor(int red, int green, int blue) {
		return new ColorWrapper(red, green, blue);
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#log(java.lang.String)
	 */
	public void log(String message) {
		Log.warn(message);
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#log(java.lang.String, java.lang.Throwable)
	 */
	public void log(String message, Throwable e) {
		Log.error(message, e);
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#log(java.lang.Throwable)
	 */
	public void log(Throwable e) {
		Log.error(e);
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#getDefaultFont()
	 */
	public ThingleFont getDefaultFont() {
		return defaultFont;
	}

	/**
	 * Create a thinlet font from a Slick Font
	 * 
	 * @param font The font to wrap
	 * @return The Thinlet font 
	 */
	public ThingleFont createThingleFont(Font font) {
		return new FontWrapper(font);
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#getResource(java.lang.String)
	 */
	public URL getResource(String ref) {
		return ResourceLoader.getResource(ref);
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#getResourceAsStream(java.lang.String)
	 */
	public InputStream getResourceAsStream(String ref) {
		try {
			return ResourceLoader.getResourceAsStream(ref);
		} catch (RuntimeException e) {
			Log.error("Failed to locate: "+ref);
			return null;
		}
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#createImage(java.io.InputStream, java.lang.String, boolean)
	 */
	public ThingleImage createImage(InputStream in, String name, boolean flipped)
			throws ThingleException {
		try {
			return new ImageWrapper(new Image(in, name, flipped));
		} catch (SlickException e) {
			throw new ThingleException(e);
		}
	}
	
	/**
	 * Create a new thingle image from a slick image
	 * 
	 * @param image The image to wrap
	 * @return The newly created image
	 */
	public ThingleImage createImage(Image image) {
		return new ImageWrapper(image);
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#createImageBuffer(int, int)
	 */
	public ThingleImageBuffer createImageBuffer(int width, int height) {
		return new ImageBufferWrapper(new ImageBuffer(width, height));
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#createInput(org.newdawn.slick.thingle.internal.ThinletInputListener)
	 */
	public ThingleInput createInput(ThinletInputListener listener) {
		InputHandler handler = new InputHandler(listener);
		handler.setInput(container.getInput());
		
		return handler;
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#getGraphics()
	 */
	public ThingleGraphics getGraphics() {
		return new SlickGraphics(container.getGraphics());
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#getHeight()
	 */
	public int getHeight() {
		return container.getHeight();
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#getWidth()
	 */
	public int getWidth() {
		return container.getWidth();
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#createColor(int, int, int, int)
	 */
	public ThingleColor createColor(int red, int green, int blue, int alpha) {
		return new ColorWrapper(red, green, blue, alpha);
	}
	
	/**
	 * Create a new thinlet color from a slick colour
	 * 
	 * @param color The color to wrap
	 * @return The thinlet color verson
	 */
	public ThingleColor createThingleColor(Color color) {
		return new ColorWrapper(color);
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#doPostRender()
	 */
	public void doPostRender() {
		SlickCallable.leaveSafeBlock();
		container.getGraphics().setFont(storedFont);
		container.getGraphics().setColor(storedColor);
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#doPreRender()
	 */
	public void doPreRender() {
		storedFont = container.getGraphics().getFont();
		storedColor = container.getGraphics().getColor();
		
		SlickCallable.enterSafeBlock();
	}

	public ThingleFont createBitmapFont(String ref, String image) {
		try {
			return createThingleFont(new AngelCodeFont(ref, image));
		} catch (SlickException e) {
			throw new RuntimeException("Unable to load font: "+ref, e);
		}
	}
}
