package org.newdawn.slick.thingle.internal.lwjgl;

import java.io.InputStream;
import java.net.URL;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.TextureImpl;
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
public class LWJGLContext implements ThingleContext, ThingleUtil {
	/** The default font in Slick */
	private TrueTypeFont font = new TrueTypeFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12), false);
	/** The default font for Thinlet */
	private FontWrapper defaultFont = new FontWrapper(font);
	
	/** The graphics context */
	private Graphics graphics;
	
	/** The width of the dipslay */
	protected int width;
	/** The height of the display */
	protected int height;
	
	/**
	 * Create a new context
	 * 
	 * @param width The width of the context
	 * @param height The height of the context
	 */
	public LWJGLContext(int width, int height) {
		this.width = width;
		this.height = height;
		
		graphics = new Graphics(width, height);
	}
	
	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#createFont(java.lang.String, int, int)
	 */
	public ThingleFont createFont(String face, int style, int size) {
		ThingleFont font = createThingleFont(new TrueTypeFont(new java.awt.Font(face, style, size), false));
		
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
		//handler.setInput(container.getInput());
		
		return handler;
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#getGraphics()
	 */
	public ThingleGraphics getGraphics() {
		return new SlickGraphics(graphics);
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#getHeight()
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#getWidth()
	 */
	public int getWidth() {
		return width;
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
		GL11.glPopClientAttrib();
		GL11.glPopAttrib();
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleContext#doPreRender()
	 */
	public void doPreRender() {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushClientAttrib(GL11.GL_ALL_CLIENT_ATTRIB_BITS);
		
		TextureImpl.bindNone();
		if (GLContext.getCapabilities().OpenGL13) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
		}
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);    
		
		GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_R);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
		
		GL11.glDisable(GL11.GL_STENCIL_TEST);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);    
		GL11.glDisable(GL11.GL_ALPHA_TEST);    
		GL11.glDisable(GL11.GL_CULL_FACE);   
	    
		GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, height, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
	}

	public ThingleFont createBitmapFont(String ref, String image) {
		try {
			return createThingleFont(new AngelCodeFont(ref, image));
		} catch (SlickException e) {
			throw new RuntimeException("Unable to load font: "+ref, e);
		}
	}
}
