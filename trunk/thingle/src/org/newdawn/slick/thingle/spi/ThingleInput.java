package org.newdawn.slick.thingle.spi;

/**
 * The input is expected to fire appropriate methods into the input listener
 * given in it's construction. 
 * 
 * @author kevin
 */
public interface ThingleInput {
	/** The mapping value to get the ENTER key code in the SPI's implementation */
	public static final int ENTER_KEY = 1;
	/** The mapping value to get the ESCAPE key code in the SPI's implementation */
	public static final int ESCAPE_KEY = 2;
	/** The mapping value to get the F6 key code in the SPI's implementation */
	public static final int F6_KEY = 3;
	/** The mapping value to get the F8 key code in the SPI's implementation */
	public static final int F8_KEY = 4;
	/** The mapping value to get the left cursor key code in the SPI's implementation */
	public static final int LEFT_KEY = 5;
	/** The mapping value to get the right cursor key code in the SPI's implementation */
	public static final int RIGHT_KEY = 6;
	/** The mapping value to get the up cursor key code in the SPI's implementation */
	public static final int UP_KEY = 7;
	/** The mapping value to get the down cursor key code in the SPI's implementation */
	public static final int DOWN_KEY = 8;
	/** The mapping value to get the F10 key code in the SPI's implementation */
	public static final int F10_KEY = 9;
	/** The mapping value to get the TAB key code in the SPI's implementation */
	public static final int TAB_KEY = 10;
	/** The mapping value to get the page up key code in the SPI's implementation */
	public static final int PRIOR_KEY = 11;
	/** The mapping value to get the page down key code in the SPI's implementation */
	public static final int NEXT_KEY = 12;
	/** The mapping value to get the home key code in the SPI's implementation */
	public static final int HOME_KEY = 13;
	/** The mapping value to get the end key code in the SPI's implementation */
	public static final int END_KEY = 14;
	/** The mapping value to get the return key code in the SPI's implementation */
	public static final int RETURN_KEY = 15;
	/** The mapping value to get the backspace key code in the SPI's implementation */
	public static final int BACK_KEY = 16;
	/** The mapping value to get the A key code in the SPI's implementation */
	public static final int A_KEY = 17;
	/** The mapping value to get the delete key code in the SPI's implementation */
	public static final int DELETE_KEY = 18;
	/** The mapping value to get the X key code in the SPI's implementation */
	public static final int X_KEY = 19;
	/** The mapping value to get the V key code in the SPI's implementation */
	public static final int V_KEY = 20;
	/** The mapping value to get the C key code in the SPI's implementation */
	public static final int C_KEY = 21;
	
	/**
	 * Update the input handler, allow it to process input and recorded
	 * events.
	 * 
	 * @param delta The amount of time in milliseconds thats passed since last update
	 */
	public void update(int delta);

	/**
	 * Check if the shift key is pressed
	 * 
	 * @return True if the shift key is pressed
	 */
	public boolean isShiftDown();
	
	/**
	 * Check if the system specific pop-up trigger is pressed (can be as 
	 * simple as right mouse button)
	 * 
	 * @return True if the pop-up trigger is pressed
	 */
	public boolean isPopupTrigger();

	/**
	 * Check if the ctrl key is pressed
	 * 
	 * @return True if the ctrl key is pressed
	 */
	public boolean isControlDown();

	/**
	 * Check if the alt key is pressed
	 * 
	 * @return True if the alt key is pressed
	 */
	public boolean isAltDown();
	
	/**
	 * Get the key code that maps to the given mapping code (as defined in this class). Since
	 * key codes vary from implementation to implementation this provides an abstract
	 * way of checking code without continuously looking them up
	 * 
	 * @param keyMapping The key mapping value as defined in this class
	 * @return The key code representing the key mapping
	 */
	public int getKeyCode(int keyMapping);
	
	/**
	 * Enable input handling. Until enable is called no input handling
	 * should take place
	 */
	public void enable();

	/**
	 * Disable input handling. Once disable is called no input handling
	 * should take place
	 */
	public void disable();
}
