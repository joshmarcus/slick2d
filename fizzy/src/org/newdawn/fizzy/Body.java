package org.newdawn.fizzy;

import java.util.ArrayList;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;

/**
 * A single body in the world. A body holds a shape which collide with
 * the rest of the world. It also holds properties about the shapes once
 * they have been created
 * 
 * @author kglass
 */
public class Body {
	/** The body held by JBox2D */
	private org.jbox2d.dynamics.Body jboxBody;
	/** The body definition held by JBox2D */
	private BodyDef jboxBodyDef;
	/** True if this should be a static body */
	private boolean staticBody;
	/** The list of bodies this body is touching */
	private ArrayList<Body> touching = new ArrayList<Body>();
	/** The shape used to represent this body */
	private Shape shape;
	/** The userdata assigned to this body if any */
	private Object userData;
	
	/**
	 * Create a new body
	 * 
	 * @param shape The shape the body should have
	 * @param x The x axis location of the body
	 * @param y The y axis location of the body
	 */
	public Body(Shape shape, float x, float y) {
		this(shape,x,y,false);
	}

	/**
	 * Create a new body
	 * 
	 * @param shape The shape the body should have
	 * @param x The x axis location of the body
	 * @param y The y axis location of the body
	 * @param staticBody True if this body should be static
	 */
	public Body(Shape shape, float x, float y, boolean staticBody) {
		jboxBodyDef = new BodyDef();
		jboxBodyDef.position = new Vec2(x,y);
		this.staticBody = staticBody;
		this.shape = shape;
	}
	
	/**
	 * Check if this body was declared as static
	 * 
	 * @return True if this body was declared as static
	 */
	public boolean isStatic() {
		return staticBody;
	}
	
	/**
	 * Get the user data assigned to this body if any
	 * 
	 * @return Get the user data assigned to this body (or null if none is defined);
	 */
	public Object getUserData() {
		return userData;
	}
	
	/**
	 * Set the user data assigned to this body
	 * 
	 * @param object The user data to be assigned to this body
	 */
	public void setUserData(Object object) {
		this.userData = object;
	}
	
	/**
	 * Check if this body is touching another
	 * 
	 * @param other The other body to check against 
	 * @return True if the bodies are touching
	 */
	public boolean isTouching(Body other) {
		return touching.contains(other);
	}
	
	/**
	 * Check how many contact points there are between two bodies
	 * 
	 * @param other The other body to check against
	 * @return The number of contact points
	 */
	public int touchCount(Body other) {
		int count = 0;
		
		for (int i=0;i<touching.size();i++) {
			if (touching.get(i) == other) {
				count++;
			}
		}
		
		return count;
	}
	
	/**
	 * Indicate that this body touches another at a *new* contact 
	 * point.
	 * 
	 * @param other The other body that is touched
	 */
	void touch(Body other) {
		touching.add(other);
	}
	
	/**
	 * Indicate that one contact point between the bodies has been removed
	 * 
	 * @param other The other body that is no longer touched by a particular 
	 * contact point.
	 */
	void untouch(Body other) {
		touching.remove(other);
	}
	
	/**
	 * Apply force to the body
	 * 
	 * @param x The amount of force on the X axis
	 * @param y The amount of force on the Y axis
	 */
	public void applyForce(float x, float y) {
		checkBody();
		jboxBody.applyForce(new Vec2(x,y), new Vec2(0,0));
	}
	
	/**
	 * Get the X position of the body
	 * 
	 * @return The x position of the body
	 */
	public float getX() {
		checkBody();
		return jboxBody.getPosition().x;		
	}

	
	/**
	 * Get the Y position of the body
	 * 
	 * @return The y position of the body
	 */
	public float getY() {
		checkBody();
		return jboxBody.getPosition().y;		
	}
	
	/**
	 * Get the rotation of the body
	 * 
	 * @return The rotation of the body
	 */
	public float getRotation() {
		checkBody();
		return jboxBody.getAngle();
	}

	/**
	 * Get the X velocity of the body
	 * 
	 * @return The x velocity of the body
	 */
	public float getXVelocity() {
		checkBody();
		return jboxBody.getLinearVelocity().x;		
	}

	
	/**
	 * Get the Y velocity of the body
	 * 
	 * @return The y velocity of the body
	 */
	public float getYVelocity() {
		checkBody();
		return jboxBody.getLinearVelocity().y;		
	}
	
	/**
	 * Get the angular velocity of the body
	 * 
	 * @return The angular velocity of the body
	 */
	public float getAngularVelocity() {
		checkBody();
		return jboxBody.getAngularVelocity();
	}
	
	/**
	 * Set the restitution applied when this body collides
	 * 
	 * @param rest The restitution applied when this body collides
	 */
	public void setRestitution(float rest) {
		shape.setRestitution(rest);
	}

	/**
	 * Set the friction applied when this body collides
	 * 
	 * @param f The friction applied when this body collides
	 */
	public void setFriction(float f) {
		shape.setFriction(f);
	}
	
	/**
	 * Set the density of this body
	 * 
	 * @param den The density of this body
	 */
	public void setDensity(float den) {
		shape.setDensity(den);
	}
	
	/**
	 * True if this body has reached the edge of the world bounds and hence
	 * is frozen in space.
	 * 
	 * @return True if this body has reached the edge of the world bounds.
	 */
	public boolean isOutOfBounds() {
		checkBody();
		return jboxBody.isFrozen();
	}
	
	/**
	 * Notification that this body is being added to the world
	 * 
	 * @param world The world this body is being added to
	 */
	void addToWorld(World world) {
		org.jbox2d.dynamics.World jboxWorld = world.getJBoxWorld();
				
		jboxBody = jboxWorld.createBody(jboxBodyDef);
		shape.createInBody(this);
		
		if (!staticBody) {
			jboxBody.setMassFromShapes();
		} else {
			jboxBody.m_type = org.jbox2d.dynamics.Body.e_staticType;
		}
	}

	/**
	 * Notification that this body is being removed from the world
	 * 
	 * @param world The world this body is being removed from
	 */
	void removeFromWorld(World world) {
		org.jbox2d.dynamics.World jboxWorld = world.getJBoxWorld();
		jboxWorld.destroyBody(jboxBody);
	}
	
	/**
	 * Get the JBox2D body that is wrapped by this class
	 * 
	 * @return The body that is wrapped by this proxy class
	 */
	org.jbox2d.dynamics.Body getJBoxBody() {
		return jboxBody;
	}
	
	/**
	 * Get the Fizzy shape representing this body
	 * 
	 * @return The fizzy shape representing this body
	 */
	public Shape getShape() {
		return shape;
	}
	
	/**
	 * Set the position of the body. This can only be called after the body has been added
	 * to the world.
	 * 
	 * @param x The new x coordinate of the body
	 * @param y The new y coordinate of the body
	 */
	public void setPosition(float x, float y) {
		checkBody();
		jboxBody.setXForm(new Vec2(x,y), jboxBody.getAngle());
	}
	
	/**
	 * Set the rotation of the body. This can only be called after the body has been added
	 * to the world.
	 * 
	 * @param rotation The new rotation of the body
	 */
	public void setRotation(float rotation) {
		checkBody();
		jboxBody.setXForm(jboxBody.getPosition(), rotation);
	}
	
	/**
	 * Check the body has been added to the world 
	 */
	private void checkBody() {
		if (jboxBody == null) {
			throw new RuntimeException("This method requires that the body has been added to the world first");
		}
	}

	/**
	 * Check if this body is "sleeping", i.e. its not moving any more
	 * 
	 * @return True if this body is sleeping
	 */
	public boolean isSleeping() {
		checkBody();
		return jboxBody.isSleeping();
	}

	/**
	 * Translate this body by the given amount
	 * 
	 * @param x The amount to move the body on the x axis
	 * @param y The amount to move the body on the y axis
	 */
	public void translate(float x, float y) {
		setPosition(getX()+x, getY()+y);
	}

	/**
	 * Set the linear damping to apply to this body. Higher 
	 * value slows the body acceleration. Maximum is 1.0
	 * 
	 * @param damping The amount to dampen the movement by
	 */
	public void setDamping(float damping) {
		if (jboxBody == null) {
			jboxBodyDef.linearDamping = damping;
		}
	}

	/**
	 * Set the linear velocity of this body
	 * 
	 * @param xVelocity The x component of the velocity
	 * @param yVelocity The y component of the velocity
	 */
	public void setVelocity(float xVelocity, float yVelocity) {
		checkBody();
		Vec2 vel = jboxBody.getLinearVelocity();
		vel.x = xVelocity;
		vel.y = yVelocity;
		jboxBody.setLinearVelocity(vel);
	}

	/**
	 * Set the angular velocity (the speed at which it rotates)
	 * 
	 * @param vel The angular velocity to apply
	 */
	public void setAngularVelocity(float vel) {
		checkBody();
		jboxBody.setAngularVelocity(vel);
	}
}
