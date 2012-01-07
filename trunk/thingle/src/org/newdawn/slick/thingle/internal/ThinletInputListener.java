package org.newdawn.slick.thingle.internal;

import org.newdawn.slick.thingle.spi.ThingleInput;

/**
 * The input interface supported by the Thinlet instance. This interface
 * must be enacted by the input provider of the SPI implementation.
 * 
 * @author kevin
 */
public interface ThinletInputListener {
	/**
	 * Notification that the mouse has been dragged
	 * 
	 * @param x The x location the mouse was dragged to
	 * @param y The y location the mouse was dragged to
	 * @param input The input instance firing this event
	 * @return True if the mouse was inside a GUI component
	 */
	public boolean mouseDragged(int x, int y, ThingleInput input);
	
	/**
	 * Notification that the mouse has been moved (not dragged)
	 * 
	 * @param x The x location the mouse was moved to
	 * @param y The y location the mouse was moved to
	 * @param input The input instance firing this event
	 * @return True if the mouse was inside a GUI component
	 */
	public boolean mouseMoved(int x, int y, ThingleInput input);

	/**
	 * Notification that the mouse button has been released
	 * 
	 * @param x The x location the mouse was released at
	 * @param y The y location the mouse was released at
	 * @param input The input instance firing this event
	 * @return True if the mouse was inside a GUI component
	 */
	public boolean mouseReleased(int x, int y, ThingleInput input);

	/**
	 * Notification that the mouse wheel has been moved
	 * 
	 * @param rotation The amount the wheel was moved by
	 * @param input The input instance firing this event
	 * @return True if the mouse was inside a GUI component
	 */
	public boolean mouseWheelMoved(int rotation, ThingleInput input);

	/**
	 * Notification that the mouse button has been pressed
	 * 
	 * @param x The x location the mouse was pressed at
	 * @param y The y location the mouse was pressed at
	 * @param clickCount The number of clicks at the given location
	 * @param input The input instance firing this event
	 * @return True if the mouse was inside a GUI component
	 */
	public boolean mousePressed(int x, int y, int clickCount, ThingleInput input);
		
	/**
	 * Notification that a key was pressed
	 * 
	 * @param keychar The character of the key pressed (if any)
	 * @param keycode The key code of the key pressed (as described by ThinletInput.getKeyMapping())
	 * @param input The input instance firing this event
	 * @param typed True if this key was typed (i.e. pressed then released)
	 * @return True if the key was processed
	 */
	public boolean keyPressed(char keychar, int keycode, ThingleInput input, boolean typed);
}
