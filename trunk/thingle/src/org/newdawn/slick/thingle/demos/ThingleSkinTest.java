package org.newdawn.slick.thingle.demos;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Thingle;
import org.newdawn.slick.thingle.internal.slick.SlickThinletFactory;
import org.newdawn.slick.thingle.spi.ThingleException;

/**
 * Quick test for skinning
 * 
 * @author Nate
 */
public class ThingleSkinTest extends BasicGame {
	/** The page being rendered */
	private Page page;

	/** 
	 * Create a new test
	 */
	public ThingleSkinTest() {
		super("ThingleSkinTest");
	}

	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		Thingle.init(new SlickThinletFactory(container));
		try {
			page = new Page();
			page.setDrawDesktop(false);
			page.enable();
			page.loadSkin("metropolitan");
			page.add(page.parse( //
					"<dialog text='ThingleSkinTest'>" //
							+ "<list weightx='1' weighty='1'>" //
							+ "<item text='Mooooo' />" //
							+ "</list>" //
							+ "</dialog>"));
			page.layout();
		} catch (ThingleException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta)
			throws SlickException {
	}

	/**
	 * @see org.newdawn.slick.Game#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		page.render();
	}

	/**
	 * Entry point for the test
	 * 
	 * @param args Arguments passed to the application
	 * @throws Exception Something went wrong - dumped to stdout.
	 */
	public static void main(String[] args) throws Exception {
		AppGameContainer container = new AppGameContainer(new ThingleSkinTest());
		container.setDisplayMode(640, 480, false);
		container.start();
	}
}
