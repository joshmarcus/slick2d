package org.newdawn.fizzy;

/**
 * Description of any class responding to collision events within the world 
 * simulation.
 * 
 * @author kevin
 */
public interface WorldListener {
	/**
	 * Notification that two bodies have collied within the simulation
	 * 
	 * @param event The event describing the collision
	 */
	public void collided(CollisionEvent event);
	
	/**
	 * Notification that two bodies have now separated (after having collided) within 
	 * the simulation
	 * 
	 * @param event The event describing which bodies have separated
	 */
	public void separated(CollisionEvent event);
}
