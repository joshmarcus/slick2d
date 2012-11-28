package org.newdawn.slick;

import org.newdawn.slick.Game;
import org.newdawn.slick.SlickException;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

/**
 * A GDX Container that works for desktop applications
 * 
 * @author kevin
 */
public class ApplicationGDXContainer extends GDXGameContainer {

	/**
	 * Create a new game container
	 * 
	 * @param game The game that will be hosted 
	 * @param width The width of the game container
	 * @param height The height of the game container
	 * @param realWidth The width of the screen
	 * @param realHeight The height of the screen
	 * @throws SlickException 
	 */
	public ApplicationGDXContainer(Game game, int width, int height,
			int realWidth, int realHeight) throws SlickException {
		super(game, width, height, realWidth, realHeight);
	}

	/**
	 * Start the game container as a desktop application
	 */
	public void start() {
		lastUpdate = System.currentTimeMillis();
        app = new LwjglApplication(this, game.getTitle(), width, height, false);
	}

}
