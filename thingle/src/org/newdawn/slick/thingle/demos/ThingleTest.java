package org.newdawn.slick.thingle.demos;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.thingle.LegacyMethodInvoker;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Thingle;
import org.newdawn.slick.thingle.internal.slick.SlickThinletFactory;
import org.newdawn.slick.thingle.spi.ThingleException;

/**
 * The big test demo that has multiple tabs, sub-dialogs etc
 * 
 * @author kevin
 */
public class ThingleTest extends BasicGame {
	/** The UI page being displayed */
	private Page page;
	/** The image to display in the background */
	private Image image;
	
	/**
	 * Create a new test
	 */
	public ThingleTest() {
		super("Thingle Demo");
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		Thingle.init(new SlickThinletFactory(container));
		Thingle.setMethodInvoker(new LegacyMethodInvoker());
		
		container.setShowFPS(false);
		//container.setVSync(true);
		//container.setTargetFrameRate(100);
		
		image = new Image("res/logo.png");
		container.getGraphics().setBackground(Color.white);
		
		try {
			page = new Page("res/demo.xml", new Demo());
		} catch (ThingleException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
//		Theme theme = new Theme();
//		theme.setBackground(Thingle.createColor(0.6f,0.6f,1f,1f));
//		theme.setBorder(Thingle.createColor(0,0,0.5f));
//		theme.setFocus(Thingle.createColor(0,0,0));
//		page.setTheme(theme);
		page.setDrawDesktop(true);
		
		page.enable();
		try {
			page.loadSkin("crystallized");
		} catch (ThingleException e) {
			// ignore, no skin
		}
	}
	
	public void update(GameContainer container, int delta)
			throws SlickException {
	}

	public void render(GameContainer container, Graphics g)
			throws SlickException {
		image.draw(100,200);
		page.render();
		
		g.setColor(Color.black);
		g.drawString("FPS: "+container.getFPS(), 530, 2);
	}

	/**
	 * Entry point to the demo
	 * 
	 * @param argv The argments passed to the execution
	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new ThingleTest(), 600, 600, false);
			container.start();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
