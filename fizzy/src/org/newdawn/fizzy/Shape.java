package org.newdawn.fizzy;

import java.util.ArrayList;

/**
 * Decription of any class representing a shape for collision. The shape 
 * both defines the geometry and the collision properties. However, these
 * can be augmented at the Body level.
 * 
 * @author kevin
 */
public interface Shape {
	/** The default density applied to shapes if none is specified (25.0f) */
	public static final float DEFAULT_DENSITY = 25.0f;
	/** The default restitution applied to shapes if none is specified (0.9f) */
	public static final float DEFAULT_RESTIUTION = 0.9f;
	/** The default friction applied to shapes if none is specified (0.1f) */
	public static final float DEFAULT_FRICTION = 0.1f;
	
	/**
	 * The body this shape is being used in if any 
	 * 
	 * @return The body this shape is being used in if any
 	 */
	Body getBody();
	
	/**
	 * Create this shape against the body given
	 * 
	 * @param body The body to create the shape within
	 */
	void createInBody(Body body);
	
	/**
	 * Set the restitution applied to this shape
	 * 
	 * @param rest The restitution applied to this shape
	 */
	void setRestitution(float rest);

	/**
	 * Set the friction applied to this shape
	 * 
	 * @param rest The friction applied to this shape
	 */
	void setFriction(float friction);

	/**
	 * Set the density applied to this shape
	 * 
	 * @param rest The density applied to this shape
	 */
	void setDensity(float density);
	
	/**
	 * Get the JBox2D shapes that build up this fizzy shape
	 *
	 * @return The shapes that build up this fizzy shape 
	 */
    ArrayList<org.jbox2d.collision.Shape> getJBoxShapes();
}
