package org.newdawn.fizzy;

/**
 * An event structure decribing the collision of two bodies
 * in the world simulation
 * 
 * @author kevin
 */
public class CollisionEvent {
	/** The first body in the collision */
	private Body bodyA;
	/** The second body in the collision */
	private Body bodyB;
	
	/**
	 * Create a new event
	 * 
	 * @param bodyA The first body in the collision
	 * @param bodyB The second body in the collision
	 */
	CollisionEvent(Body bodyA, Body bodyB) {
		this.bodyA = bodyA;
		this.bodyB = bodyB;
	}
	
	/**
	 * Get the first body taking part in the collision
	 * 
	 * @return The first body taking part in the collision
	 */
	public Body getBodyA() {
		return bodyA;
	}

	/**
	 * Get the second body taking part in the collision
	 * 
	 * @return The second body taking part in the collision
 	 */
	public Body getBodyB() {
		return bodyB;
	}

	/**
	 * Check if this event relates to the given body
	 * 
	 * @param current The body to check
	 * @return True if this event relates to the given body
	 */
	public boolean contains(Body current) {
		return ((bodyA == current) || (bodyB == current));
	}
}
