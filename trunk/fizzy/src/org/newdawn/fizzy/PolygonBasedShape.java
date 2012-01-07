package org.newdawn.fizzy;

import org.jbox2d.collision.PolygonDef;

public abstract class PolygonBasedShape extends PrimitiveShape<PolygonDef> {
	/** The horizontal axis offset */
	protected float xoffset;
	/** The vertical axis offset */
	protected float yoffset;
	/** The angle offset */
	protected float angleOffset;

	/**
	 * Create a new shape
	 */
	public PolygonBasedShape() {
		super(new PolygonDef());
	}
	
	/**
	 * Move the rectangle away from the center of it's potential body. The rectangle will still 
	 * be positioned based on it's center but will be offset from the body's center by the given 
	 * value, and rotated by the angle given.
	 * 
	 * @param x The horizontal axis offset
	 * @param y The vertical axis offset
	 * @param angle
	 * @return This rectangle for chaining operations
	 */
	public PolygonBasedShape setOffset(float x, float y, float angle) {
		xoffset = x;
		yoffset = y;
		angleOffset = angle;
		applyOffset(x,y,angle);
		return this;
	}
	
	/**
	 * It's up to the subclass to apply the offset as it needs to
	 * 
	 * @param x The xoffset to apply
	 * @param y The yoffset to apply
	 * @param angle The angle to apply
	 */
	protected abstract void applyOffset(float x, float y, float angle);
	
	/**
	 * Get the horizontal axis offset from the body's center
	 * 
	 * @return The horizontal axis offset
	 */
	public float getXOffset() {
		return xoffset;
	}

	/**
	 * Get the vertical axis offset from the body's center
	 * 
	 * @return The horizontal axis offset
	 */
	public float getYOffset() {
		return yoffset;
	}

	/**
	 * Get the angle offset from the body's angle
	 * 
	 * @return The angle offset
	 */
	public float getAngleOffset() {
		return angleOffset;
	}
}
