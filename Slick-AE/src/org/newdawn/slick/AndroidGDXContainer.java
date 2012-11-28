package org.newdawn.slick;

import com.badlogic.gdx.backends.android.AndroidApplication;

/**
 * A game container that works for android
 * 
 * @author kevin
 */
public class AndroidGDXContainer extends GDXGameContainer {
	
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
	public AndroidGDXContainer(Game game, int width, int height, int realWidth,
			int realHeight) throws SlickException {
		super(game, width, height, realWidth, realHeight);
	}
	
	/**
	 * Set the android application being used when running on the handset
	 * 
	 * @param android The android application being used when running on the handset
	 */
	void setAndroidApplication(AndroidApplication android) {
		app = android;
	}

}
