package org.newdawn.penguin;

import net.phys2d.raw.Body;
import net.phys2d.raw.World;

import org.newdawn.slick.Graphics;

/**
 * An entity existing in the game world. An entity can have an optional
 * physical body.
 * 
 * @author kevin
 */
public interface Entity {
	/**
	 * Set the world this entity is part of
	 * 
	 * @param world The world this entity is part of
	 */
	public void setWorld(World world);
	
	/**
	 * Get the body representing this entity in the world. 
	 * 
	 * @return The body or null if the entity doesn't have a physical reprsentation
	 */
	public Body getBody();

	/**
	 * Update this entity. This method is called once before physical world is updated
	 * each cycle. It is only ever called once per update.
	 * 
	 * @param delta The amount of time passed since last update
	 */
	public void preUpdate(int delta);
	
	/**
	 * Update the entity during the physics loop. This method is called each time the physical
	 * world is updated 
	 * 
	 * @param delta The amount of time passed into the physical world update
	 */
	public void update(int delta);
	
	/**
	 * Render this entity to the screen
	 * 
	 * @param g The graphics context on which to render
	 */
	public void render(Graphics g);
}
