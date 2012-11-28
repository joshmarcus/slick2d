package org.newdawn.swingle;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.PopupFactory;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ARBPixelBufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;

/**
 * A frame whose contents will be rendered to an image, which can subsequently
 * be updated into a texture to be rendered in an OpenGL context.
 * 
 * @author kevin
 */
public class HeadlessFrame extends JFrame {
	/** The colour model including alpha for the GL image */
    private static final ColorModel glAlphaColorModel = 
    		new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
	            new int[] {8,8,8,8},
	            true,
	            false,
	            ComponentColorModel.TRANSLUCENT,
	            DataBuffer.TYPE_BYTE);
    
	/** The image being updated with the contents of the GUI */
	private BufferedImage image;
	/** True if the window has been created */
	private boolean created = false;
	/** True if sub-image texture updates are supported */
	private boolean subImageSupported = true;
	/** The width of the window */
	private int width;
	/** The height of the window */
	private int height;
	/** The bufer used to update the text */
	private ByteBuffer dataBuffer = null;
	/** The data array used to read pixels from the buffered image */
	private int[] data;
	/** The current AWT mouse modifier state */
	private int mouseModifiers;
	/** The current AWT key modifier state */
	private int keyModifiers;
	/** The rectangle marked as needing update */
	private Rectangle dirty = new Rectangle();
	/** The last time a texture update was performed */
	private long lastUpdateTime;
	/** The cached update area for sycnhronizing dirty rectangle updates */
	private Rectangle update = new Rectangle();

	/**
	 * The minimum interval between texture updates - for throttling
	 * (milliseconds)
	 */
	private int minimumUpdateInterval = 35;
	/**
	 * The interval between mouse clicks that can be considered a multi-click
	 * (millseconds)
	 */
	private int doubleClickInterval = 200;
	/** The last time the mouse was pressed */
	private long lastPress = 0;
	/** The number of multi-clicks that occured */
	private int clickCount;
	/** True if the window is accepting input */
	private boolean inputEnabled = true;
	/**
	 * The component that was originally focused - used to work round a macosx
	 * issue where LWJGL uses an AWT window
	 */
	private Component originalFocus = null;
	/** True if we should antialias drawing */
	private boolean antialias = true;
	
	/** The PBO id */
	private int PBO;
	/** True if the PBO is supported */
	private boolean pboSupported;
	
	/**
	 * Create a new frame
	 * 
	 * @param width The width of the new frame
	 * @param height The height of the new frame
	 */
	public HeadlessFrame(int width, int height) {
		this(width,height,false);
	}
	
	/**
	 * Create a new frame
	 * 
	 * @param width The width of the new frame
	 * @param height The height of the new frame
	 * @param pboRequsted True if we want to try and use pbos
	 */
	public HeadlessFrame(int width, int height,boolean pboRequested) {
		RepaintManager.setCurrentManager(new RecordingRepaintManager());
		RepaintManager.currentManager(null).setDoubleBufferingEnabled(false);

		data = new int[width * height];
		dataBuffer = BufferUtils.createByteBuffer(width * height * 4);
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);

		PopupFactory.setSharedInstance(new SoftPopupFactory(this));

		this.width = width;
		this.height = height;
		dirty = new Rectangle(0, 0, width, height);

		setFocusableWindowState(false);
		setIgnoreRepaint(true);
		setBackground(new Color(0, 0, 0, 0));
		setUndecorated(true);
		setSize(width, height);
		pack();
		created = true;

		pboSupported = GLContext.getCapabilities().GL_ARB_pixel_buffer_object && pboRequested;
		if (pboSupported) {
			IntBuffer buffer = BufferUtils.createIntBuffer(1);
			ARBPixelBufferObject.glGenBuffersARB(buffer);
			PBO = buffer.get();
	        ARBPixelBufferObject.glBindBufferARB(ARBPixelBufferObject.GL_PIXEL_UNPACK_BUFFER_ARB, PBO);
	        ARBPixelBufferObject.glBufferDataARB(ARBPixelBufferObject.GL_PIXEL_UNPACK_BUFFER_ARB, 
	        									 width * height * 4, 
	        									 ARBPixelBufferObject.GL_STREAM_DRAW_ARB);
	        ARBPixelBufferObject.glBindBufferARB(ARBPixelBufferObject.GL_PIXEL_UNPACK_BUFFER_ARB, 0);
		}
	}

	/**
	 * @see javax.swing.JFrame#setJMenuBar(javax.swing.JMenuBar)
	 */
	public void setJMenuBar(JMenuBar bar) {
		// osx fix for setting a jmenu bar since it's dependent on the window
		// being
		// able to maintain focus
		setFocusableWindowState(true);
		super.setJMenuBar(bar);
		setFocusableWindowState(false);
	}

	/**
	 * Indicate whether this window should take input instructions. Useful to
	 * just turn the GUI off when game input is taking over
	 * 
	 * @param inputEnabled True if this window should convert input instructions into AWT events
	 */
	public void setInputEnabled(boolean inputEnabled) {
		this.inputEnabled = inputEnabled;
	}

	/**
	 * Set the minimum update interval for the texture. Used to throttle updates
	 * as required.
	 * 
	 * @param min The minimum interval in milliseconds
	 */
	public void setMinimumUpdateInterval(int min) {
		minimumUpdateInterval = min;
	}

	/**
	 * @see java.awt.Window#isShowing()
	 */
	public boolean isShowing() {
		return true;
	}

	/**
	 * @see java.awt.Component#isVisible()
	 */
	public boolean isVisible() {
		return created;
	}

	/**
	 * @see java.awt.Component#getGraphics()
	 */
	public Graphics getGraphics() {
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		if (antialias) {
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}

		return graphics;
	}

	/**
	 * Get the image that's been built from rendering the GUI - useful for
	 * debugging
	 * 
	 * @return The image thats been built from rendering the GUI
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * Update the AWT key modifiers based on a key being pressed
	 * 
	 * @param keyCode The LWJGL key code that that was pressed or release
	 * @param pressed True if the key was pressed
	 */
	private void updateKeyModifiers(int keyCode, boolean pressed) {
		int mask = 0;

		switch (keyCode) {
		case Keyboard.KEY_LMENU:
		case Keyboard.KEY_RMENU:
			mask = KeyEvent.ALT_DOWN_MASK;
			break;
		case Keyboard.KEY_LCONTROL:
		case Keyboard.KEY_RCONTROL:
			mask = KeyEvent.CTRL_DOWN_MASK;
			break;
		case Keyboard.KEY_LSHIFT:
		case Keyboard.KEY_RSHIFT:
			mask = KeyEvent.SHIFT_DOWN_MASK;
			break;

		}
		if (pressed) {
			keyModifiers |= mask;
		} else {
			keyModifiers &= ~mask;
		}
	}

	/**
	 * Update the AWT mouse modifier record based on a mouse button being
	 * pressed or released.
	 * 
	 * @param lwjglButton The LWJGL index of the button pressed or released
	 * @param pressed True if the button was pressed
	 */
	private void updateMouseModifiers(int lwjglButton, boolean pressed) {
		int mask = 0;

		switch (lwjglButton) {
		case 0:
			mask = MouseEvent.BUTTON1_DOWN_MASK;
			break;
		case 1:
			mask = MouseEvent.BUTTON2_DOWN_MASK;
			break;
		case 2:
			mask = MouseEvent.BUTTON3_DOWN_MASK;
			break;
		default:
			return;
		}

		if (pressed) {
			mouseModifiers |= mask;
		} else {
			mouseModifiers &= ~mask;
		}
	}

	/**
	 * Utility to fire a mouse event
	 * 
	 * @param event  The ID of the mouse event
	 * @param time The time to assign the event
	 * @param modifiers The mouse modifiers
	 * @param x The x position of the mouse at the time of the event
	 * @param y The y position of the mouse at the time of the event
	 * @param clickCount The number of clicks that have taken place
	 * @param popUp True if the mouse event was a popup trigger
	 * @param button The index of the button that was pressed (AWT index)
	 */
	private void fireMouseEvent(int event, long time, int modifiers, int x,
			int y, int clickCount, boolean popUp, int button) {
		Component source = this;

		if (getFocusOwner() != null) {
			source = getFocusOwner();
			Point p = Util.getComponentLocation(this, source);
			x -= p.getX();
			y -= p.getY();
		}

		invokeEvent(source, new MouseEvent(source, event, time, modifiers, x,
				y, clickCount, popUp, button));
	}

	/**
	 * Indicate that the mouse has been pressed on the LWJGL window
	 * 
	 * @param x The x position that the mouse was pressed at (origin at top left corner - as with AWT)
	 * @param y The y position that the mouse was pressed at (origin at top left corner - as with AWT)
	 * @param button The LWJGL index of the button pressed
	 */
	public void mousePressed(int x, int y, int button) {
		if (!inputEnabled) {
			return;
		}

		int swingButton = Util.toAWTMouseButton(button);

		updateMouseModifiers(button, true);
		if (System.currentTimeMillis() - lastPress < doubleClickInterval) {
			clickCount++;
		} else {
			clickCount = 1;
		}
		lastPress = System.currentTimeMillis();

		// finally fire the events associated with mouse presses
		fireMouseEvent(MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0,
				x, y, clickCount, false, swingButton);
		fireMouseEvent(MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0,
				x, y, clickCount, false, swingButton);

		macosFocusWorkaround(x, y);
	}

	/**
	 * Indicate that the mouse has been release on the LWJGL window
	 * 
	 * @param x The x position that the mouse was release at (origin at top left corner - as with AWT)
	 * @param y The y position that the mouse was release at (origin at top left corner - as with AWT)
	 * @param button The LWJGL index of the button release
	 */
	public void mouseReleased(int x, int y, int button) {
		if (!inputEnabled) {
			return;
		}

		int swingButton = Util.toAWTMouseButton(button);

		fireMouseEvent(MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(),
				0, x, y, clickCount, false, swingButton);

		updateMouseModifiers(button, false);
	}

	/**
	 * Indicate that the mouse has been moved on the LWJGL window
	 * 
	 * @param x The x position that the mouse was moved to (origin at top left corner - as with AWT)
	 * @param y The y position that the mouse was moved to (origin at top left corner - as with AWT)
	 */
	public void mouseMoved(int x, int y) {
		if (!inputEnabled) {
			return;
		}

		if (mouseModifiers != 0) {
			fireMouseEvent(MouseEvent.MOUSE_DRAGGED,
					System.currentTimeMillis(), mouseModifiers, x, y, 0, false,
					MouseEvent.NOBUTTON);
		} else {
			fireMouseEvent(MouseEvent.MOUSE_MOVED, System.currentTimeMillis(),
					mouseModifiers, x, y, 0, false, MouseEvent.NOBUTTON);
		}
	}

	/**
	 * MacOSX focus handling in Swing is dependent on the focused element
	 * recieving all input. However LWJGL uses a normal AWT window on OSX so
	 * this doesn't work with focus. Instead this method manually manages focus
	 * back and forth between the component in focus and the LWJGL canvas
	 * 
	 * @param x The x coordinate thats been clicked
	 * @param y The y coordinate thats been clicked
	 */
	private void macosFocusWorkaround(int x, int y) {
		// macos focus handling work around. Macos displays focus based on the
		// the last thing to recieve focus gained - LWJGL on macos expects it's
		// canvas
		// to always be the focused entity
		Component comp = SwingUtilities.getDeepestComponentAt(getContentPane(),
				x - getContentPane().getX(), y - getContentPane().getY());
		if (originalFocus == null) {
			originalFocus = KeyboardFocusManager
					.getCurrentKeyboardFocusManager().getFocusOwner();
		}

		setFocusableWindowState(true);
		Component oldFocusOwner = getFocusOwner();

		// we only allow focus of JTextComponents
		if (!(comp instanceof JTextComponent)) {
			comp = null;
		}

		if (oldFocusOwner != comp) {
			if (oldFocusOwner != null) {
				invokeEvent(oldFocusOwner, new FocusEvent(oldFocusOwner,
						FocusEvent.FOCUS_LOST, false, comp));
			}
			KeyboardFocusManager.getCurrentKeyboardFocusManager()
					.clearGlobalFocusOwner();
			if (comp != null) {
				invokeEvent(comp, new FocusEvent(comp, FocusEvent.FOCUS_GAINED,
						false, oldFocusOwner));
			}
		}
		setFocusableWindowState(false);

		if ((originalFocus != null) && (comp == null)) {
			originalFocus.requestFocusInWindow();
		}
	}

	/**
	 * Indicate that the key was pressed in the LWJGL window
	 * 
	 * @param key The LWJGL key code of the key pressed
	 * @param c The character associated with the key press
	 */
	public void keyPressed(int key, char c) {
		if (!inputEnabled) {
			return;
		}

		updateKeyModifiers(key, true);

		if (getFocusOwner() == null) {
			return;
		}

		int awtKey = Util.toAWTKeyCode(key);

		invokeEvent(getFocusOwner(), new KeyEvent(getFocusOwner(),
				KeyEvent.KEY_PRESSED, System.currentTimeMillis(), keyModifiers,
				awtKey, c));
		invokeEvent(getFocusOwner(), new KeyEvent(getFocusOwner(),
				KeyEvent.KEY_TYPED, System.currentTimeMillis(), keyModifiers,
				KeyEvent.VK_UNDEFINED, c));
	}

	/**
	 * Indicate that the key was releasd in the LWJGL window
	 * 
	 * @param key The LWJGL key code of the key releasd
	 * @param c The character associated with the key release
	 */
	public void keyReleased(int key, char c) {
		if (!inputEnabled) {
			return;
		}

		updateKeyModifiers(key, false);

		if (getFocusOwner() == null) {
			return;
		}
		int awtKey = Util.toAWTKeyCode(key);
		invokeEvent(getFocusOwner(), new KeyEvent(getFocusOwner(),
				KeyEvent.KEY_RELEASED, System.currentTimeMillis(),
				keyModifiers, awtKey, c));

	}

	/**
	 * Utilitiy to invoke an AWT event in the AWT dispatch thread
	 * 
	 * @param target The target component
	 * @param event The event to be fired at the component
	 */
	private void invokeEvent(final Component target, final AWTEvent event) {
		invokeAndWait(new Runnable() {
			public void run() {
				target.dispatchEvent(event);		
			}
		});
	}

	/**
	 * Utility to hide away the exception that neer occurs
	 * 
	 * @param runnable The runnable that should be invoked on the AWT dispatch thread
	 */
	private void invokeAndWait(Runnable runnable) {
		try {
			SwingUtilities.invokeAndWait(runnable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update the buffered image from the GUI
	 */
	private void updateImage() {
		invokeAndWait(new Runnable() {
			public void run() {
				Graphics2D g = (Graphics2D) getGraphics();
				Composite c = g.getComposite();
				Color col = g.getColor();
				g.setComposite(AlphaComposite.Src);
				g.setColor(new Color(0, 0, 0, 0));
				g.fillRect(update.x, update.y, update.width, update.height);
				g.setComposite(c);
				g.setColor(col);

				// Would been nice to use paintImmediately here, but OSX Java
				// doesn't
				// handle repainting in the same way - so resort to manually
				// calling
				// paint against our image's graphics context
				g.setClip(update);
				paint(g);
				// comp.paintImmediately(update);

				JComponent comp = ((JComponent) getLayeredPane());
				RepaintManager.currentManager(null).markCompletelyClean(comp);
			}
		});
	}

	/**
	 * Update the given texture with the contents of the frame. Note only dirty
	 * sections will be updated
	 * 
	 * @param id
	 *            The OpenGL ID of the texture to be updated.
	 */
	public void updateTexture(int id) {
		updateTexture(id, null);
	}
	
	/**
	 * Update the given texture with the contents of the frame. Note only dirty
	 * sections will be updated
	 * 
	 * @param id
	 *            The OpenGL ID of the texture to be updated.
	 * @param component
	 * 			  The top level component to be rendered
	 */
	public void updateTexture(int id, Component component) {
		if (System.currentTimeMillis() - lastUpdateTime < minimumUpdateInterval) {
			return;
		}

		// the dirty rectangle gets updated by the repaint manager, which itself
		// is caled through the AWT event thread. Just need to make sure here we
		// don't get a section to be updated half way through. Note that no
		// actual
		// painting is ever going on outside of our control
		synchronized (dirty) {
			if (dirty.isEmpty()) {
				return;
			}

			update.setBounds(dirty);
			dirty.width = 0;
		}

		lastUpdateTime = System.currentTimeMillis();
		
		updateImage();
		
		Rectangle total = new Rectangle(0, 0, width, height);
		if (component != null) {
			total = component.getBounds();
		}
		update = update.intersection(total);
		updateTextureImpl(id, update,component);
	}
	
	/**
	 * Update the dirty section of the texture
	 * 
	 * @param id
	 *            The OpenGL texture ID to update
	 * @param dirty
	 *            The rectangle which is considered dirty and should be updated
	 */
	private void updateTextureImpl(int id, Rectangle dirty, Component component) {
		if (dirty.getWidth() < 0) {
			return;
		}
		if (dirty.getHeight() < 0) {
			return;
		}
		
		image.getRaster().getDataElements(dirty.x, dirty.y, dirty.width, dirty.height, data);	
		
		updateTextureFromDataBuffer(id, component, dirty);
	}
	
	/**
	 * Update the texture data from the buffered image
	 * 
	 * @param id The ID of the texture to be updatd
	 * @param component The component that is being copied over
	 * @param dirty The dirty rectangle to be updated
	 */
	private void updateTextureFromDataBuffer(int id, Component component, Rectangle dirty) {
		if (pboSupported) {
	        ARBPixelBufferObject.glBindBufferARB(ARBPixelBufferObject.GL_PIXEL_UNPACK_BUFFER_ARB, PBO);
	        dataBuffer = ARBPixelBufferObject.glMapBufferARB(ARBPixelBufferObject.GL_PIXEL_UNPACK_BUFFER_ARB, 
	        									ARBPixelBufferObject.GL_WRITE_ONLY_ARB, null);
	        
	    	dataBuffer.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().put(data, 0, dirty.width * dirty.height);
		} else {
        	dataBuffer.clear();
        	dataBuffer.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().put(data, 0, dirty.width * dirty.height);
        }

		int xoffset = 0;
		int yoffset = 0;
		if (component != null) {
			xoffset = component.getX();
			yoffset = component.getY();
		}
		
		if (pboSupported) {
	        ARBPixelBufferObject.glUnmapBufferARB(ARBPixelBufferObject.GL_PIXEL_UNPACK_BUFFER_ARB);
	        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
	        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0,
					             dirty.x-xoffset, dirty.y-yoffset, dirty.width,
					             dirty.height, GL12.GL_BGRA,
					             GL11.GL_UNSIGNED_BYTE, 0);
	        ARBPixelBufferObject.glBindBufferARB(ARBPixelBufferObject.GL_PIXEL_UNPACK_BUFFER_ARB, 0);
		} else {
	        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
	        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0,
					             dirty.x-xoffset, dirty.y-yoffset, dirty.width,
					             dirty.height, GL12.GL_BGRA,
					             GL11.GL_UNSIGNED_BYTE, dataBuffer);
		}
	}

	/**
	 * A repaint manager that records the sections that have been marked as
	 * dirty by Swing as the updates occured. This is then used to update the
	 * appropriate sections of the texture
	 * 
	 * @author kevin
	 */
	private class RecordingRepaintManager extends RepaintManager {
		/**
		 * @see javax.swing.RepaintManager#addDirtyRegion(javax.swing.JComponent,
		 *      int, int, int, int)
		 */
		public synchronized void addDirtyRegion(JComponent c, int x, int y,
				int w, int h) {
			synchronized (dirty) {
				Point p = Util.getComponentLocation(HeadlessFrame.this, c);
				p.x += x;
				p.y += y;

				// adjust for any offset the content pane has from the actual
				// window - we only want the content pane to be visible
				y -= getContentPane().getLocation().y;

				// if this is the first dirty update then just take the new
				// rectangle as the
				// the area. If we've already got some dirty area union that
				// with new bit. This
				// means we'll only perform one potentially larger update to the
				// texture which
				// OpenGL performance guidelines say should be quicker than a
				// series of smaller
				// updates
				Rectangle rect = new Rectangle((int) p.getX(), (int) p.getY(),
						w, h);
				if (dirty.isEmpty()) {
					dirty.setBounds(rect);
				} else {
					dirty.setBounds(dirty.union(rect));
				}

				super.addDirtyRegion(c, x, y, w, h);
			}
		}
	}
}
