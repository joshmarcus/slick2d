package org.newdawn.fizzy.examples;

import org.newdawn.fizzy.World;
import org.newdawn.fizzy.render.WorldWindow;

/**
 * Abstract test case. So we should be able to use tests in different forms.
 * 
 * @author kevin
 */
public abstract class AbstractTest {
	/** The world being simulated */
	private World world;
	
	/**
	 * Implemented by a test to create it's world
	 * 
	 * @return The world to be simulated
	 */
	public abstract World createWorld();
	
	/**
	 * Start the test in a standalone window
	 */
	public void startInWindow() {
		world = createWorld();
		
		WorldWindow window = new WorldWindow(world);
		window.start();
	}
}
