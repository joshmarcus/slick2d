package org.newdawn.noodles.tankexamples;

import org.newdawn.noodles.object.NetworkField;

/**
 * A game tank
 * 
 * @author kevin
 */
public class Tank {
	/** The x position of the tank */
	@NetworkField
	private float x = 100;
	/** The y position of the tank */
	@NetworkField
	private float y = 100;
	/** The angle of the tank */
	@NetworkField
	private float ang = 10;
	/** The angle of the tank's turret */
	@NetworkField
	private float turretAng = 20;
	
	/**
	 * Default constructor for network space use
	 */
	public Tank() {
	}
	
	/**
	 * Configure the tank 
	 * 
	 * @param x The x position of the tank
	 * @param y The y position of the tank
	 * @param ang The angle of the tank
	 * @param turretAng The angle of the tank's turret
	 */
	public void configure(float x, float y, float ang, float turretAng) {
		this.x = x;
		this.y = y;
		this.ang = ang;
		this.turretAng = turretAng;
	}
	
	/**
	 * Rotate the tank
	 * 
	 * @param ang The amount to rotate the tank by
	 */
	public void rotate(float ang) {
		this.ang += ang;
	}
	
	/**
	 * Move the tank forward
	 * 
	 * @param amount The amount to move the tank forward by
	 */
	public void move(float amount) {
		this.x += Math.sin(ang) * amount;
		this.y -= Math.cos(ang) * amount;
	}
	
	/**
	 * Get the X position of the tank
	 * 
	 * @return The X position of the tank
	 */
	public float getX() {
		return x;
	}
	
	/**
	 * Get the Y position of the tank
	 * 
	 * @return The Y position of the tank
	 */
	public float getY() {
		return y;
	}
	
	/**
	 * Get the angle of the tank
	 * 
	 * @return The angle of the tank
	 */
	public float getAngle() {
		return ang;
	}

	/**
	 * Get the angle of the tank's turret
	 * 
	 * @return The angle of the tank's turret
	 */
	public float getTurretAngle() {
		return turretAng;
	}
}
