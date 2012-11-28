package org.newdawn.slick.thingle.internal.lwjgl;

import org.newdawn.slick.Input;
import org.newdawn.slick.thingle.internal.ThinletInputListener;
import org.newdawn.slick.thingle.spi.ThingleInput;
import org.newdawn.slick.util.InputAdapter;
import org.newdawn.slick.util.Log;

/** 
 * The input handler responsible for translating from Slick input events
 * to the thinlet event model
 * 
 * @author kevin
 */
public class InputHandler extends InputAdapter implements ThingleInput {
	/** The thinlet instance events should be sent to */
	private ThinletInputListener thinlet;
	/** The current modifiers state (alt, shift etc) */
	private Modifiers mods;
	/** The input we're listening to */
	private Input input;
	
	/**
	 * Create a new handler that maps between slick input and thinlet
	 * 
	 * @param thinlet The thinlet instance we're sending events to
	 */
	public InputHandler(ThinletInputListener thinlet) {
		this.thinlet = thinlet;
	}

	/**
	 * Update the modifiers status 
	 */
	protected void updateMods() {
		mods.update();
	}
	
	/**
	 * Consume the current event
	 */
	protected void consumeEvent() {
		input.consumeEvent();
	}
	
	/**
	 * Check if the given mouse button is pressed
	 * 
	 * @param index The index of the mouse button
	 * @return True if the mouse button is pressed
	 */
	protected boolean isMouseButtonDown(int index) {
		return input.isMouseButtonDown(index);
	}
	
	/**
	 * @see org.newdawn.slick.util.InputAdapter#mouseClicked(int, int, int, int)
	 */
	public void mouseClicked(int button, int x, int y, int clickCount) {
		if (clickCount == 2) {
			if (button == 0) {
				updateMods();
				mouseMoved(x,y,x,y);
				if (thinlet.mousePressed(x, y, 2, this)) {
					consumeEvent();
				}
			}
		}
	}

	/**
	 * @see org.newdawn.slick.util.InputAdapter#mouseMoved(int, int, int, int)
	 */
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		updateMods();
		if (isMouseButtonDown(0)) {
			if (thinlet.mouseDragged(newx, newy, this)) {
				consumeEvent();
			}
		} else {
			if (thinlet.mouseMoved(newx, newy, this)) {
				consumeEvent();
			}
		}
	}

	/**
	 * @see org.newdawn.slick.util.InputAdapter#mousePressed(int, int, int)
	 */
	public void mousePressed(int button, int x, int y) {
		if (button == 0) {
			updateMods();
			mouseMoved(x,y,x,y);
			if (thinlet.mousePressed(x, y, 1, this)) {
				consumeEvent();
			}
		}
	}

	/**
	 * @see org.newdawn.slick.util.InputAdapter#mouseReleased(int, int, int)
	 */
	public void mouseReleased(int button, int x, int y) {
		if (button == 0) {
			updateMods();
			mouseMoved(x,y,x,y);
			if (thinlet.mouseReleased(x, y, this)) {
				consumeEvent();
			}
		}
	}

	/**
	 * @see org.newdawn.slick.util.InputAdapter#mouseWheelMoved(int)
	 */
	public void mouseWheelMoved(int change) {
		updateMods();
		if (thinlet.mouseWheelMoved(-change, this)) {
			consumeEvent();
		}
	}

	/**
	 * @see org.newdawn.slick.util.InputAdapter#setInput(org.newdawn.slick.Input)
	 */
	public void setInput(Input input) {
		this.input = input;
		mods = new Modifiers(input);
	}
	
	/**
	 * @see org.newdawn.slick.util.InputAdapter#keyPressed(int, char)
	 */
	public void keyPressed(int key, char c) {
		updateMods();
		if (thinlet.keyPressed(c, key, this, false)) {
			consumeEvent();
		}
	}

	/**
	 * @see org.newdawn.slick.util.InputAdapter#keyReleased(int, char)
	 */
	public void keyReleased(int key, char c) {
		if (thinlet.keyPressed(c, key, this, true)) {
			consumeEvent();
		}
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleInput#isAltDown()
	 */
	public boolean isAltDown() {
		return mods.isAltDown;
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleInput#isControlDown()
	 */
	public boolean isControlDown() {
		return mods.isControlDown;
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleInput#isPopupTrigger()
	 */
	public boolean isPopupTrigger() {
		return mods.isPopupTrigger;
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleInput#isShiftDown()
	 */
	public boolean isShiftDown() {
		return mods.isShiftDown;
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleInput#update(int)
	 */
	public void update(int delta) {
	}

	public int getKeyCode(int keyMapping) {
		switch (keyMapping) {
		case ENTER_KEY:
			return Input.KEY_ENTER;
		case ESCAPE_KEY:
			return Input.KEY_ESCAPE;
		case F6_KEY:
			return Input.KEY_F6;
		case F8_KEY:
			return Input.KEY_F8;
		case LEFT_KEY:
			return Input.KEY_LEFT;
		case RIGHT_KEY:
			return Input.KEY_RIGHT;
		case UP_KEY:
			return Input.KEY_UP;
		case DOWN_KEY:
			return Input.KEY_DOWN;
		case F10_KEY:
			return Input.KEY_F10;
		case TAB_KEY:
			return Input.KEY_TAB;
		case PRIOR_KEY:
			return Input.KEY_PRIOR;
		case NEXT_KEY:
			return Input.KEY_NEXT;
		case HOME_KEY:
			return Input.KEY_HOME;
		case END_KEY:
			return Input.KEY_END;
		case RETURN_KEY:
			return Input.KEY_RETURN;
		case BACK_KEY:
			return Input.KEY_BACK;
		case A_KEY:
			return Input.KEY_A;
		case V_KEY:
			return Input.KEY_V;
		case X_KEY:
			return Input.KEY_X;
		case C_KEY:
			return Input.KEY_C;
		case DELETE_KEY:
			return Input.KEY_DELETE;
		}

		Log.error("Didn't map key mapping: " + keyMapping);
		return -1;
	}

	/**
	 * Enable input to this GUI page
	 */
	public void enable() {
		input.addPrimaryListener(this);
	}

	/**
	 * Disabl input to this GUI page
	 */
	public void disable() {
		input.removeListener(this);
		input.clearKeyPressedRecord();
	}
}
