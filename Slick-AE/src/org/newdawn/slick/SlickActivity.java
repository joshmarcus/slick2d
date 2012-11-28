package org.newdawn.slick;

import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import android.os.Bundle;
import android.util.Log;
import android.view.Display;

import com.badlogic.gdx.backends.android.AndroidApplication;

/**
 * An activity extended to host a Slick Game in an android application
 * 
 * @author kevin
 */
public class SlickActivity extends AndroidApplication {
	private AndroidGDXContainer container;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	org.newdawn.slick.util.Log.setLogSystem(new AndroidLogSystem());
    	org.newdawn.slick.util.Log.error("Slick-AE 0");
    	
		InternalTextureLoader.get().clear();
		InternalTextureLoader.get().setHoldTextureData(true);
		
		ResourceLoader.removeAllResourceLocations();
		ResourceLoader.addResourceLocation(new AndroidResourceLocation(getAssets()));	
    }
	
	/**
	 * Start the game. Should be called from onCreate.
	 * 
	 * @param game The game to be hosted
	 */
	public void start(Game game) {
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		int height = display.getHeight();
		
		start(game,width,height);
	}
	
	/**
	 * Start the game. Should be called from onCreate.
	 * 
	 * @param game The game to be hosted
	 * @param width The width to use for the game
	 * @param height The height to use for the game
	 */
	public void start(Game game, int width, int height) {
	    try {
			Display display = getWindowManager().getDefaultDisplay(); 
			int realWidth = display.getWidth();
			int realHeight = display.getHeight();
			
	    	container = new AndroidGDXContainer(game, width, height, realWidth, realHeight); 
	    	container.setAndroidApplication(this);
	    	initialize(container, false);
	    } catch (SlickException e) {
	    	Log.e("SLICK", "Failed to create container", e);
	    }
	}
}