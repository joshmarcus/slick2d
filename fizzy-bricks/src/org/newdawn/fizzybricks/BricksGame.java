package org.newdawn.fizzybricks;

import java.awt.Font;

import org.newdawn.fizzy.Body;
import org.newdawn.fizzy.CollisionEvent;
import org.newdawn.fizzy.CompoundShape;
import org.newdawn.fizzy.Rectangle;
import org.newdawn.fizzy.Shape;
import org.newdawn.fizzy.World;
import org.newdawn.fizzy.WorldListener;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.util.Bootstrap;
import org.newdawn.slick.util.FontUtils;
import org.newdawn.slick.util.ResourceLoader;

/**
 * A simple 99 bricks style game where you have to build the highest tower 
 * out of tetris style bricks before the tower falls over and before you run 
 * out of bricks (in this case 50)
 * 
 * This game is provided really as a demonstration of the Fizzy API over the top
 * of the wonder JBox2D library. It also provides so illustration of how to use
 * the library with Slick 2D.
 * 
 * @author kevin
 */
public class BricksGame extends BasicGame implements WorldListener {
	/** The size of a single box making up one of the pieces */
	public static final float BLOCK_SIZE = 28;
	/** The collision area of the block */
	public static final float INNER_SIZE = BLOCK_SIZE;
	/** The number of updates per second we'll update the world by */
	public static final int UPDATES_PER_SECOND = 20;
	/** The number of iterations the world integration will take - higher = more accurate */
	public static final int ITERATIONS = 30;
	/** The number of physics loops we'll run in our game update loop - used to speed the game up */
	public static final int UPDATES = 5;	
	
	/** The identifier for the L shaped piece */
	public static final int L_SHAPE = 0;
	/** The identifier for the T shaped piece */
	public static final int T_SHAPE = 1;
	/** The identifier for the O shaped piece */
	public static final int O_SHAPE = 2;
	/** The identifier for the I shaped piece */
	public static final int I_SHAPE = 3;
	
	/** The physics world we run the simulation in */
	private World world;
	/** The four coloured block images */
	private Image[] cols = new Image[4];
	/** The black outline image */
	private Image black;
	/** The image drawn for the platform */
	private Image platform;
	/** The gradient image for the background */
	private Image background;
	/** The exceeding average logo made by yours truely */
	private Image logo;
	/** The sound made when a block is placed */
	private Sound hit;
	/** The background music */
	private Music music;
	/** The physics body currently falling - or null if we need a new one */
	private Body current;
	
	/** The number of turns the current block should take - i.e. the number of frames the block should be turning for */
	private int turnCount;
	/** True if the current block has just spawned, used to prevent holding down the down cursor */
	private boolean justSpawned = false;
	/** The best tower height achieved this game */
	private int bestHeight = 0;
	/** The current tower height achieved this game */
	private int currentHeight = 0;
	/** The font used for small writing */
	private TrueTypeFont font;
	/** The font used for the number or bricks and the best tower height ever display */
	private TrueTypeFont bigfont;
	/** The height the view should be placed at - allows the viewport to scroll around */
	private float viewHeight;
	/** The number of blocks remaining in the game */
	private int blockCount;
	
	/** The best tower height we've achieved */
	private int bestEver;
	/** True if we're at the starting point - i.e. waiting to start the game */
	private boolean atStart = true;
	/** The game container the game is running in */
	private GameContainer container;
	
	/**
	 * Create a new game
	 */
	public BricksGame() {
		super("Fizzy Bricks Demo");
	}

	/**
	 * Spawn a single tetris piece ready to be controlled by the player
	 */
	private void spawnShape() {
		// reduce the number blocks remaining, if we don't have any 
		// left then end the game
		blockCount--;
		if (blockCount < 0) {
			blockCount = 0;
			atStart = true;
			return;
		}
		
		// We're going to create the new physics body here representing the shape.
		// Using a compound body we can represent a single body with multiple
		// sub-shapes, in this case that means a series of primitive rectangles
		// building up the shape. Obviously this could have been done with custom
		// polygon shapes but this served as a better 
		CompoundShape shape = new CompoundShape();
		int type = (int) (Math.random() * 4);
		
		// add the sub-blocks that make up the tetris shape
		switch (type) {
		case L_SHAPE:
			shape.add(new Rectangle(INNER_SIZE,INNER_SIZE));
			shape.add(new Rectangle(INNER_SIZE,INNER_SIZE).setOffset(BLOCK_SIZE, 0, 0));
			shape.add(new Rectangle(INNER_SIZE,INNER_SIZE).setOffset(0, -BLOCK_SIZE, 0));
			shape.add(new Rectangle(INNER_SIZE,INNER_SIZE).setOffset(0, -BLOCK_SIZE*2, 0));
			break;
		case I_SHAPE:
			shape.add(new Rectangle(INNER_SIZE,INNER_SIZE).setOffset(0, -BLOCK_SIZE*0.5f, 0));
			shape.add(new Rectangle(INNER_SIZE,INNER_SIZE).setOffset(0, -BLOCK_SIZE*1.5f, 0));
			shape.add(new Rectangle(INNER_SIZE,INNER_SIZE).setOffset(0, BLOCK_SIZE*0.5f, 0));
			shape.add(new Rectangle(INNER_SIZE,INNER_SIZE).setOffset(0, BLOCK_SIZE*1.5f, 0));
			break;
		case O_SHAPE:
			shape.add(new Rectangle(INNER_SIZE,INNER_SIZE).setOffset(-BLOCK_SIZE/2, 0, 0));
			shape.add(new Rectangle(INNER_SIZE,INNER_SIZE).setOffset(BLOCK_SIZE/2, 0, 0));
			shape.add(new Rectangle(INNER_SIZE,INNER_SIZE).setOffset(-BLOCK_SIZE/2, -BLOCK_SIZE, 0));
			shape.add(new Rectangle(INNER_SIZE,INNER_SIZE).setOffset(BLOCK_SIZE/2, -BLOCK_SIZE, 0));
			break;
		case T_SHAPE:
			shape.add(new Rectangle(INNER_SIZE,INNER_SIZE));
			shape.add(new Rectangle(INNER_SIZE,INNER_SIZE).setOffset(BLOCK_SIZE, -BLOCK_SIZE, 0));
			shape.add(new Rectangle(INNER_SIZE,INNER_SIZE).setOffset(0, -BLOCK_SIZE, 0));
			shape.add(new Rectangle(INNER_SIZE,INNER_SIZE).setOffset(0, -BLOCK_SIZE*2, 0));
			break;
		}
		
		
		// create a body using the shape we created above and position
		// it above the platform ready to come down
		current = new Body(shape, 0, currentHeight + 280.0f, false);
		current.setRestitution(0f);
		current.setFriction(0.5f);
		current.setDamping(0.8f);
		// assign some user data that describes how the body should be rendered
		current.setUserData(new BlockData(cols[type]));
		world.add(current);
		
		// set up a random rotation (at one of four 90 degree angles) 
		// and reset the counters for the block
		float rotation = (float) (((int) (Math.random() * 4)) * (Math.PI / 2));
		current.setRotation(rotation);
		turnCount = 0;
		justSpawned = true;
	}
	
	/**
	 * Start a new game with a new physics simulation
	 */
	private void startGame() {
		// create the world, the bounds are set with a really high
		// ceiling so the blocks don't stop moving if they get too high
		world = new World(-800,-500,800,50000,-10f,ITERATIONS);
		
		// Create a body to represent the platform, note the "true" on the end
		// defining this body as static
		Body floor = new Body(new Rectangle(256.0f, 512.0f), 0, -256.0f, true);
		floor.setRestitution(0f);
		floor.setFriction(1f);
		world.add(floor);
		
		// add a listener to the world so we'll know when the body being controlled
		// by the use has hit the stack and hence it can be stopped
		world.addListener(this);
		
		// reset all the game counters
		current = null;
		bestHeight = 0;
		viewHeight = currentHeight = 0;
		blockCount = 50;
	}
	
	@Override
	public void init(GameContainer container) throws SlickException {
		// load up our resources and configure the container
		this.container = container;
		container.setShowFPS(false);
		container.setIcons(new String[] {"res/icon16.png","res/icon24.png","res/icon32.png"});
		
		Font javaFont;
		try {
			javaFont = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream("res/font.ttf"));
			font = new TrueTypeFont(javaFont.deriveFont(30.0f), true);
			bigfont = new TrueTypeFont(javaFont.deriveFont(90.0f), true);
		} catch (Exception e) {
			throw new SlickException("Failed to load font: ", e);
		}
		
		hit = new Sound("res/hit.wav");
		music = new Music("res/funked.xm");
		music.loop();
		cols[0] = new Image("res/blue.png");
		cols[1] = new Image("res/green.png");
		cols[2] = new Image("res/red.png");
		cols[3] = new Image("res/yellow.png");
		black = new Image("res/black.png");
		background = new Image("res/background.png");
		platform = new Image("res/platform.png").getFlippedCopy(false, true);
		logo = new Image("res/logo.png");
		
		container.setTargetFrameRate(60);
		container.setVSync(true);
		
		// finally start the first game so we'll have something to display in the
		// background behind the title screen
		startGame();
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		// if we're waiting at the title screen don't
		// do anything
		if (atStart) {
			return;
		}
		
		// update the world UPDATES time, this gives us a way to 
		// speed up the game if the physics just don't "feel" nice and crisp
		for (int i=0;i<UPDATES;i++) {
			if (atStart) {
				return;
			}
			
			// update the world at a fixed amount
			world.update(1f/UPDATES_PER_SECOND);
			
			// if we have a piece to control then
			// apply keyboard inputs to the body
			if (current != null) {
				float xVelocity = 0;
				float yVelocity = 0;
				
				// if the user is pressing down then we want
				// the block to move extra fast downwards
				if (container.getInput().isKeyDown(Input.KEY_DOWN) && !justSpawned) {
					yVelocity = -90f / UPDATES;
				} else {
					yVelocity = -20f / UPDATES;
				}
				current.setVelocity(xVelocity, yVelocity);
				
				// if the block is rotating then apply the rotation
				if (turnCount > 0) {
					turnCount--;
					current.setAngularVelocity((float) (Math.PI / 2) / UPDATES);
				} else {
					// if we're not rotating we want to make sure the position
					// is exactly on a block margin - this is so block can slide past each
					// other in most cases
					current.setAngularVelocity(0);
					if (current.getX() < 0) {
						int resolveX = ((int) current.getX()-1) / (int) (BLOCK_SIZE/2);
						current.setPosition(resolveX * (BLOCK_SIZE/2), current.getY());					
					} else {
						int resolveX = ((int) current.getX()+1) / (int) (BLOCK_SIZE/2);
						current.setPosition(resolveX * (BLOCK_SIZE/2), current.getY());
					}
				}
			} else {
				// if we haven't got a block to control then
				// we should attempt to spawn another
				spawnShape();
			}
		}

		if (atStart) {
			return;
		}
		
		// quick visual flashing update. When a block hits the rest it flashes 
		// white and then fades, this loop is doing the fading.
		for (int i=0;i<world.getBodyCount();i++) {
			Body body = world.getBody(i);
			BlockData data = (BlockData) body.getUserData();
			if (data != null) {
				if (data.flash > 0) {
					data.flash -= 5;
					if (data.flash < 0) {
						data.flash = 0;
					}
				}
			}
		}
		
		// if the block has gone out of bounds, i.e. way outside of the left/right or off
		// the bottom then count it as lost
		if (current.isOutOfBounds()) {
			current = null;
		}
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		// this rendering loop is mostly Slick related, but I'll comment it up
		// just for completeness
		
		// render the gradient across the background
		g.drawImage(background, 0,0,480,640,0,0,8,640);
		
		// adjust the current view height based on the current height of the
		// tower. This just means that the view slowly slides to the appropriate
		// height
		viewHeight = (currentHeight * 0.2f)  + (viewHeight * 0.8f);
		g.translate(0,viewHeight+(container.getHeight()/2));
		currentHeight = 0;
		g.pushTransform();
		g.translate(container.getWidth()/2,0);
		g.scale(1,-1);
		
		// for each body render the blocks. Check out the rendering methods
		// for more details. We're doing a multiphase rendering here - this 
		// is so I can render a consistant black outline block first, then 
		// the actual colour image
		for (int phase=1;phase<3;phase++) {
			for (int i=0;i<world.getBodyCount();i++) {
				drawBody(g, world.getBody(i), phase);
			}
		}
		g.popTransform();

		// render the lines on the edge of the tower based on the
		// best and current heights
		g.setColor(Color.white);
		g.fillRect(80,-bestHeight,5,bestHeight);
		g.fillRect(80,-bestHeight,20,6);
		g.fillRect(80,0,20,6);
		g.fillRect(400,-currentHeight,5,currentHeight);
		g.fillRect(385,-currentHeight,20,6);
		g.fillRect(385,0,20,6);
		g.setFont(font);

		String best = ""+(int) bestHeight;
		int wid = font.getWidth(best);
		g.drawString(best, 75-wid, -6-currentHeight);
		g.drawString(""+(int) currentHeight, 410, -6-currentHeight);
		
		g.resetTransform();

		// bit of a background for the best score
		g.setColor(new Color(0,0,0,128));
		g.fillRect(0,585,640,100);
		g.setColor(new Color(0,0,0,70));
		
		// if we're not at the start then draw the block counter
		if (!atStart) {
			g.fillRect(0,0,640,60);
			g.setColor(Color.white);
			String blocks = ""+blockCount;
			if (blockCount < 10) {
				blocks = "0"+blocks;
			}
			g.setFont(bigfont);
			g.drawString(blocks, (container.getWidth() - bigfont.getWidth(blocks))/2, -10);
			g.setFont(font);
			g.drawString(" blocks left", (container.getWidth()/2) + (bigfont.getWidth(blocks)/2), 5);
		}
		
		g.setColor(Color.white);
		best = ""+(int) bestEver;
		while (best.length() < 4) {
			best = "0"+best;
		}
		best = "BEST: " + best;
		wid = bigfont.getWidth(best);
		g.setFont(bigfont);
		g.drawString(best, (480-wid)/2, 570);
		
		// if we're at the start render the title screen and the instructions
		if (atStart) {
			logo.draw((480-logo.getWidth())/2, 50);
			g.setColor(new Color(255,255,255,200));
			g.fillRect(80,240,320,240);
			g.setColor(Color.black);
			g.drawRect(80,240,320,240);
			g.setFont(font);
			FontUtils.drawCenter(font,"Drop the pieces onto",0,250,480,Color.black);
			FontUtils.drawCenter(font,"the platform, to build",0,270,480,Color.black);			
			FontUtils.drawCenter(font,"the highest tower possible",0,290,480,Color.black);			
			FontUtils.drawCenter(font,"Left/Right - Move",0,330,480,Color.black);			
			FontUtils.drawCenter(font,"Down - Fast",0,350,480,Color.black);			
			FontUtils.drawCenter(font,"Up - Rotate",0,370,480,Color.black);	
			FontUtils.drawCenter(font,"F5 - Toggle Sound",0,400,480,Color.black);	
			FontUtils.drawCenter(font,"F6 - Toggle Music",0,420,480,Color.black);			
			FontUtils.drawCenter(font,"Press any key to start..",0,450,480,Color.black);	
		}
	}

	/**
	 * Draw a body to the canvas
	 * 
	 * @param g The graphics context on which to draw
	 * @param body The body to be rendered
	 */
	private void drawBody(Graphics g, Body body, int phase) {
		Shape shape = body.getShape();
		drawShape(g,body,shape, phase);
	}

	/**
	 * Draw a shape 
	 * 
	 * @param g The graphics context to render to
	 * @param body The body to be rendered
	 * @param shape The shape representing the body
	 */
	private void drawShape(Graphics g, Body body, Shape shape, int phase) {
		if (shape instanceof Rectangle) {
			drawRectangle(g, body, (Rectangle) shape, phase);
		}
		if (shape instanceof CompoundShape) {
			drawCompound(g, body, (CompoundShape) shape, phase);
		}
	}

	/**
	 * Draw a compound shape 
	 * 
	 * @param g The graphics context to render to
	 * @param body The body to be rendered
	 * @param shape The shape representing the body
	 */
	private void drawCompound(Graphics g, Body body, CompoundShape shape, int phase) {
		int count = shape.getShapeCount();
		// when we draw a compound shape we're just draw each of the sub-shapes
		for (int i=0;i<count;i++) {
			drawShape(g, body, shape.getShape(i), phase);
		}
	}
	
	/**
	 * Draw a body represented by a rectangle
	 * 
	 * @param g The graphics context on which to render
	 * @param body The body to be rendered
	 * @param shape The shape representing the body
	 */
	private void drawRectangle(Graphics g, Body body, Rectangle shape, int phase) {
		// if the body isn't currently being controlled then work
		// out where the top of it is. This gives us a potential height of the tower.
		// Take the highest to work out how high the tower is at the moment
		if (body != current) {
			Transform t = new Transform();
			t.concatenate(Transform.createTranslateTransform(body.getX(), body.getY()));
			t.concatenate(Transform.createRotateTransform(body.getRotation(), 0, 0));
			t.concatenate(Transform.createTranslateTransform(shape.getXOffset(), shape.getYOffset()));
			t.concatenate(Transform.createRotateTransform(shape.getAngleOffset(),0,0));				
			Vector2f bodyCenter = t.transform(new Vector2f(0,0));
			
			float towerHeight = (bodyCenter.getY() + BLOCK_SIZE);
			if (towerHeight > currentHeight) {
				currentHeight = (int) towerHeight;
				if (currentHeight > bestHeight) {
					bestHeight = currentHeight;
					if (bestHeight > bestEver) {
						bestEver = bestHeight;
					}
				}
			}
		}
		
		// finally render the actual rectangles. We apply the body transform first,
		// then that of the local shape.
		g.pushTransform();
		g.translate(body.getX(), body.getY());
		g.rotate(0,0,(float) Math.toDegrees(body.getRotation()));
		g.translate(shape.getXOffset(), shape.getYOffset());
		g.rotate(0,0,(float) Math.toDegrees(shape.getAngleOffset()));
		
		float width = shape.getWidth();
		float height = shape.getHeight();
		
		// and then draw the body at the location we've reached through transform
		if (body.isStatic()) {
			if (phase == 1) {
			} else {
				g.drawImage(platform, (int) -(width/2),(int) -(height/2));
			}
		} else {
			if (phase == 1) {
				g.drawImage(black, (int) -(width/2)-1,(int) -(height/2)-1);
			} else {
				BlockData data = (BlockData) body.getUserData();
				Image image = data.col;
				
				image.draw((int) -(width/2),(int) -(height/2));
				if (data.flash != 0) {
					image.drawFlash((int) -(width/2),(int) -(height/2), width, height, new Color(255,255,255,data.flash));
				}
			}
		}
		g.popTransform();
	}
	
	@Override
	public void collided(CollisionEvent event) {
		if (current == null) {
			return;
		}
		// notification that a body in the simulation has hit another. If the body
		// we're controlling is involved then we end control of the body (since it's hit the tower)
		// and spawn another
		if (event.contains(current)) {
			hit.play();
			current.setDamping(0);
			BlockData data = (BlockData) current.getUserData();
			data.flash = 255;
			current = null;
		}
	}

	@Override
	public void separated(CollisionEvent event) {
	}

	@Override
	public void keyPressed(int key, char c) {
		// toggles for sound fx / music
		if (key == Input.KEY_F5) {
			container.setSoundOn(!container.isSoundOn());
			return;
		}
		if (key == Input.KEY_F6) {
			container.setMusicOn(!container.isMusicOn());
			return;
		}
		
		// if we're at the start and a key is pressed, start the game
		if (atStart) {
			atStart = false;
			startGame();
			return;
		}
		// in game we can use escape to exit
		if (key == Input.KEY_ESCAPE) {
			atStart = true;
			return;
		}
		
		// if we're controlling a block then ...
		if (current != null) {
			// if the down key has just been pressed then
			// we can actually apply it. This is stop holding down
			// the down key for every piece
			if (key == Input.KEY_DOWN){
				justSpawned = false;
			}
			// if the piece isn't current rotating then
			// we can move the piece or start a new rotation
			if (turnCount == 0) {
				if (key == Input.KEY_LEFT) {
					current.translate(-BLOCK_SIZE/2,0);
				}
				if (key == Input.KEY_RIGHT) {
					current.translate(BLOCK_SIZE/2,0);
				}
				if (key == Input.KEY_UP) {
					turnCount += UPDATES_PER_SECOND * UPDATES;
				}
			}
		}
	}
	
	/**
	 * A simple piece of data assocaited with each block allowing
	 * us to define it's colour and where it's flashing (on placement)
	 * 
	 * @author kevin
	 */
	private class BlockData {
		/** The image to be used to draw the block */
		private Image col;
		/** The flash amount to be drawn over the top of the block */
		private int flash = 0;
		
		/**
		 * Create a new piece of rendering data for the body
		 * 
		 * @param col The image to draw the block with 
 		 */
		private BlockData(Image col) {
			this.col = col;
		}
	}
	
	/**
	 * Entry point to out game
	 * 
	 * @param argv The arguments passed in
	 */
	public static void main(String[] argv) {
		// using the new bootstrap because it's quicker :)
		Bootstrap.runAsApplication(new BricksGame(), 480, 640, false);
	}

}
