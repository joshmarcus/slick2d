package org.newdawn.hoorah;

import org.newdawn.penguin.Actor;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

/**
 * The example Alien actor running around. This is responsible for rendering
 * specifics for this game character. Everything physics related is handled
 * up in Actor.
 * 
 * @author kevin
 */
public class Alien extends Actor {
	/** The sprite sheet containing the running sprites */
	private SpriteSheet run;
	/** The sprite sheet containing the jumping sprites */
	private SpriteSheet jump;
	
	/** The current frame of animation */
	private int frame;
	/** The timer controlling frame change */
	private int frameTimer = 0;
	/** The interval between animation frame change in milliseconds */
	private int frameInterval = 100;
	
	/**
	 * Create a new Alien actor read to add to the world
	 * 
	 * @param x The x position of the alien
	 * @param y The y position of the alien
	 * @param mass The mass of the alien
	 * @param size The size of the alien (collision size)
	 * @throws SlickException Indicates a failure to load resources for this alien
	 */
	public Alien(float x, float y, float mass, float size) throws SlickException {
		super(x, y, mass, size);
	
		run = new SpriteSheet("res/run.png", 50, 50);
		jump = new SpriteSheet("res/jump.png", 50, 50);
	}

	/**
	 * @see org.newdawn.penguin.Actor#render(org.newdawn.slick.Graphics)
	 */
	public void render(Graphics g) {
		// work out which animation we're showing based 
		// on the state of the actor
		SpriteSheet sheet = jump;
		int sx = 0;
		int sy = 0;
		
		if (jumping()) {
			sheet = jump;
			sx = 0;
			sy = 0;
		} else if (falling()) {
			sheet = jump;
			sx = 2;
			sy = 0;
		} else if (moving() && onGround()) {
			int f = (frame % 6) + 1;
			sheet = run;
			sx = f % 3;
			sy = f / 3;
 		} else if (onGround()) {
			sheet = run;
			sx = 0;
			sy = 0;
		}
		
		// get the approprite sprite 
		Image image = sheet.getSprite(sx,sy);
	
		// if we're facing the other direction, flip the sprite over
		if (facingRight()) {
			image = image.getFlippedCopy(true, false);
		}
		
		image.drawCentered(getX(), getY()-12);
		
		//g.drawString(""+onGround()+" "+getX()+"x"+getY()+" "+velx, getX(), getY()-50);
	}
	
	/**
	 * @see org.newdawn.penguin.Actor#preUpdate(int)
	 */
	public void preUpdate(int delta) {
		super.preUpdate(delta);
		
		frameTimer -= delta;
		while (frameTimer < 0) {
			frame++;
			frameTimer += frameInterval;
		}
	}

}
