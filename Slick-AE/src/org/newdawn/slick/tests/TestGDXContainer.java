package org.newdawn.slick.tests;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.ApplicationGDXContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.renderer.GDXRenderer;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.particles.effects.FireEmitter;

/**
 * A simple test game for the GDX/Android based container
 * 
 * @author kevin
 */
public class TestGDXContainer extends BasicGame {
	/** The test image being displayed */
	private Image test;
	/** The font used to confirm rendering of text */
	private AngelCodeFont font;
	/** The particle system for the fire emitters */
	private ParticleSystem system;
	/** The current position of the logo */
	private int logox = 100;
	/** The current position of the logo */
	private int logoy = 100;
	
	/**
	 * Create a new test
	 */
	public TestGDXContainer() {
		super("GDX Test");
	}

	/*
	 * (non-Javadoc)
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		Image image = new Image("testdata/particle.tga", true);
		system = new ParticleSystem(image);
		
		system.addEmitter(new FireEmitter(100,300,45));
		system.addEmitter(new FireEmitter(200,300,60));
		system.addEmitter(new FireEmitter(300,300,30));
		
		test = new Image("testdata/logo.png");
		font = new AngelCodeFont("testdata/demo2.fnt","testdata/demo2_00.tga",false);
	}

	/*
	 * (non-Javadoc)
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta)
			throws SlickException {
		system.update(delta);
	}

	/*
	 * (non-Javadoc)
	 * @see org.newdawn.slick.Game#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		test.draw(logox,logoy);
		font.drawString(100,200,"HELLO");
		system.render();
	}

	/*
	 * (non-Javadoc)
	 * @see org.newdawn.slick.BasicGame#mousePressed(int, int, int)
	 */
	public void mousePressed(int button, int x, int y) {
		logox = x;
		logoy = y;
	}

	/*
	 * (non-Javadoc)
	 * @see org.newdawn.slick.BasicGame#mouseDragged(int, int, int, int)
	 */
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		mousePressed(0, newx, newy);
	}

	/**
	 * Entry point tp the test.
	 * 
	 * @param argv The arguments to the test
	 */
	public static void main(String[] argv) {
		try {
			Renderer.setRenderer(new GDXRenderer());
			ApplicationGDXContainer container = new ApplicationGDXContainer(new TestGDXContainer(), 800, 480, 800, 480);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
}
