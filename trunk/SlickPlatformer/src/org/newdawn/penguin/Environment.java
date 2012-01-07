package org.newdawn.penguin;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

/**
 * An environment in which entities can exist
 * 
 * @author kevin
 */
public interface Environment {
	/**
	 * Render the environment and any contained entities
	 * 
	 * @param g The graphics context on which the environment should be rendered
	 */
	public void render(Graphics g);
	
	/**
	 * Render any geometry bounds 
	 * 
	 * @param g The graphics context on which the environment should be rendered
	 */
	public void renderBounds(Graphics g);
	
	/**
	 * Update the environment, the phyiscal world and any contained entities
	 * 
	 * @param delta The amount of time thats passed since last update
	 */
	public void update(int delta);
	
	/**
	 * Get the limits of the environment
	 * 
	 * @return The limits of the environment
	 */
	public Rectangle getBounds();
}
