package org.newdawn.slick.thingle.internal.slick;

import org.newdawn.slick.Input;

/**
 * The state of the modifier keys held for thinlet events
 * 
 * @author kevin
 */
public class Modifiers {
	/** True if shift is pressed */
	public boolean isShiftDown;
	/** True if the event is a popup trigger */
	public boolean isPopupTrigger;
	/** True if control is pressed */
	public boolean isControlDown;
	/** True if alt is pressed */
	public boolean isAltDown;
	
	/** The input to read from */
	private Input input;
	
	/**
	 * Create a new modifiers state
	 * 
	 * @param input The input to read from
	 */
	public Modifiers(Input input) {	
		this.input = input;
	}
	
	/**
	 * Update the modifiers state
	 */
	public void update() {
		isPopupTrigger = input.isMouseButtonDown(2);
		isShiftDown = input.isKeyDown(Input.KEY_LSHIFT) || input.isKeyDown(Input.KEY_RSHIFT);
		isControlDown = input.isKeyDown(Input.KEY_LCONTROL) || input.isKeyDown(Input.KEY_RCONTROL);
		isAltDown = input.isKeyDown(Input.KEY_LALT) || input.isKeyDown(Input.KEY_RALT);
	}
}
