package org.newdawn.hoorah;

import net.phys2d.raw.Body;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;

import org.newdawn.penguin.AbstractEntity;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * A simple crate showing a default physics body being used. This entity
 * doesn't have the "special" rules applied to it that an Actor does.
 * 
 * @author kevin
 */
public class Crate extends AbstractEntity {
	/** The image to display for the crate */
	private Image image;
	/** The width of the crate */
	private float width;
	/** The height of the crate */
	private float height;
	/** The world to which the crate has been added */
	private World world;
	
	/**
	 * Create a new crate
	 * 
	 * @param x The x position of the centre of the crate
	 * @param y The y position fo the centre of the crate
	 * @param width The width of the crate 
	 * @param height The height of the crate
	 * @param mass The mass of the crate
	 * @throws SlickException Indicates a failure to load the resources
	 */
	public Crate(float x, float y, float width, float height, float mass) throws SlickException {
		this.width = width;
		this.height = height;
		
		image = new Image("res/crate.png");
		body = new Body(new Box(width,height), mass);
		body.setPosition(x,y);
		body.setFriction(0.1f);
	}

	/**
	 * @see org.newdawn.penguin.Entity#getBody()
	 */
	public Body getBody() {
		return body;
	}

	/**
	 * @see org.newdawn.penguin.Entity#preUpdate(int)
	 */
	public void preUpdate(int delta) {
	}

	/**
	 * @see org.newdawn.penguin.Entity#render(org.newdawn.slick.Graphics)
	 */
	public void render(Graphics g) {
		g.translate(getX(), getY());
		g.rotate(0,0,(float) Math.toDegrees(body.getRotation()));
		image.draw(-width/2,-height/2,width,height);
		g.rotate(0,0,(float) -Math.toDegrees(body.getRotation()));
		g.translate(-getX(), -getY());
	}

	/**
	 * @see org.newdawn.penguin.Entity#setWorld(net.phys2d.raw.World)
	 */
	public void setWorld(World world) {
		this.world = world;
	}

	/**
	 * @see org.newdawn.penguin.Entity#update(int)
	 */
	public void update(int delta) {
	}
}
