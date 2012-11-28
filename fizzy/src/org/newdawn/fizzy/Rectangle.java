package org.newdawn.fizzy;

import org.jbox2d.common.Vec2;

/**
 * A shape implementation defining a rectangle for collision 
 * 
 * @author kevin
 */
public class Rectangle extends PolygonBasedShape {
	/** The width of the rectangle */
	private float width;
	/** The height of the rectangle */
	private float height;
	
	/**
	 * Create a new rectangle shape
	 * 
	 * @param width The width of the rectangle
	 * @param height The height of the rectangle
	 */
	public Rectangle(float width, float height) {
		this(width, height, DEFAULT_DENSITY, DEFAULT_RESTIUTION, DEFAULT_FRICTION);
	}

	/**
	 * Create a new rectangle shape
	 * 
	 * @param width The width of the rectangle
	 * @param height The height of the rectangle
	 * @param density The density of the rectangle
	 */
	public Rectangle(float width, float height, float density) {
		this(width, height, density, DEFAULT_RESTIUTION, DEFAULT_FRICTION);
	}

	/**
	 * Create a new rectangle shape
	 * 
	 * @param width The width of the rectangle
	 * @param height The height of the rectangle
	 * @param density The density of the rectangle
	 * @param resitution The resitution of the rectangle
	 */
	public Rectangle(float width, float height, float density, float resitution) {
		this(width, height, density, resitution, DEFAULT_FRICTION);
	}

	/**
	 * Create a new rectangle shape
	 * 
	 * @param width The width of the rectangle
	 * @param height The height of the rectangle
	 * @param density The density of the rectangle
	 * @param restitution The resitution of the rectangle
	 * @param friction The friction of the rectangle
	 */
	public Rectangle(float width, float height, float density, float restitution, float friction) {
		def.setAsBox(width / 2, height / 2);
		def.density = density;
		def.restitution = restitution;
		def.friction = friction;
		
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Get the width of the rectangle
	 * 
	 * @return The width of the rectangle
	 */
	public float getWidth() {
		return width;
	}
	
	/**
	 * Get the height of the rectangle
	 * 
	 * @return The height of the rectangle
	 */
	public float getHeight() {
		return height;
	}

	@Override
	protected void applyOffset(float x, float y, float angle) {
		def.setAsBox(width/2, height/2, new Vec2(x,y), angle);
	}
}
