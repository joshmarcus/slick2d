package org.newdawn.fizzy;

import org.jbox2d.common.Vec2;
import org.jbox2d.common.XForm;

public class Polygon extends PolygonBasedShape {
	/**
	 * Create a new polygon shape
	 */
	public Polygon() {
		this(DEFAULT_DENSITY, DEFAULT_RESTIUTION, DEFAULT_FRICTION);
	}

	/**
	 * Create a new polygon shape
	 * 
	 * @param density
	 *            The density of the polygon
	 */
	public Polygon(float density) {
		this(density, DEFAULT_RESTIUTION, DEFAULT_FRICTION);
	}

	/**
	 * Create a new polygon shape
	 * 
	 * @param density
	 *            The density of the polygon
	 * @param resitution
	 *            The resitution of the polygon
	 */
	public Polygon(float density, float resitution) {
		this(density, resitution, DEFAULT_FRICTION);
	}

	/**
	 * Create a new polygon shape
	 * 
	 * @param density
	 *            The density of the polygon
	 * @param restitution
	 *            The resitution of the polygon
	 * @param friction
	 *            The friction of the polygon
	 */
	public Polygon(float density, float restitution, float friction) {
		def.density = density;
		def.restitution = restitution;
		def.friction = friction;
	}

	/**
	 * Add a point to the polygon
	 * 
	 * @param x
	 *            The x coordinate of the point
	 * @param y
	 *            The y coordinate of the point
	 */
	public void addPoint(float x, float y) {
		def.addVertex(new Vec2(x, y));
	}

	/**
	 * Get the number of points in the shape
	 * 
	 * @return The number of points in the shape
	 */
	public int getPointCount() {
		return def.getVertexCount();
	}

	/**
	 * Get the x coordinate of the point at the given index
	 * 
	 * @param i
	 *            The index of the point to retrieve
	 * @return The x coordinate of the point
	 */
	public float getPointX(int i) {
		return def.vertices.get(i).x;
	}

	/**
	 * Get the y coordinate of the point at the given index
	 * 
	 * @param i
	 *            The index of the point to retrieve
	 * @return The y coordinate of the point
	 */
	public float getPointY(int i) {
		return def.vertices.get(i).y;
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
		applyOffset(-xoffset,-yoffset,-angleOffset);
		xoffset = x;
		yoffset = y;
		angleOffset = angle;
		applyOffset(x,y,angle);
		return this;
	}
	
	@Override
	protected void applyOffset(float x, float y, float angle) {
		final XForm xf = new XForm();
		xf.position.set(new Vec2(x, y));
		xf.R.set(angle);

		// Transform vertices and normals.
		for (int i = 0; i < def.getVertexCount(); ++i) {
			XForm.mulT(xf, def.vertices.get(i));
		}

	}
}
