package org.newdawn.fizzy;

import java.util.ArrayList;

import org.jbox2d.collision.ShapeDef;

/**
 * A primitive shape handling adding to the world and configuration of physics 
 * properties.
 * 
 * @author kevin
 *
 * @param <T> A shape definition for the primitive
 */
public abstract class PrimitiveShape<T extends ShapeDef> implements Shape {
	/** The array list that contains this shape */
	protected ArrayList<org.jbox2d.collision.Shape> jbox2DShapes = new ArrayList<org.jbox2d.collision.Shape>();
	/** The primitive shape that represent this fizzy shape */
	protected org.jbox2d.collision.Shape jbox2DShape;
	/** The definition of the core shape */
	protected T def;
	/** The body this shape is being used in if any */
	protected Body body;
	
	/**
	 * The body this shape is being used in if any
	 * 
	 * @return The body this shape is being used in if any
	 */
	public Body getBody() {
		return body;
	}
	
	/**
	 * Create a new primitive shape 
	 * 
	 * @param localDef The shape definition backing this fizzy shape
	 */
	protected PrimitiveShape(T localDef) {
		this.def = localDef;
	}
	
	@Override
	public void createInBody(Body body) {
		this.body = body;
		jbox2DShape = body.getJBoxBody().createShape(def);
		jbox2DShapes.add(jbox2DShape);
	}

	@Override
	public ArrayList<org.jbox2d.collision.Shape> getJBoxShapes() {
		return jbox2DShapes;
	}

	@Override
	public void setDensity(float density) {
		if (jbox2DShape == null) {
			def.density = density;
		} else {
			jbox2DShape.m_density = density;
		}
	}

	@Override
	public void setFriction(float friction) {
		if (jbox2DShape == null) {
			def.friction = friction;
		} else {
			jbox2DShape.setFriction(friction);
		}
	}

	@Override
	public void setRestitution(float rest) {
		if (jbox2DShape == null) {
			def.restitution = rest;
		} else {
			jbox2DShape.setRestitution(rest);
		}
	}

}
