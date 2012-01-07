package org.newdawn.slick;

import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.util.Log;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;

/**
 * A container that uses GDX to render everything
 * 
 * @author kevin
 */
public abstract class GDXGameContainer extends GameContainer implements ApplicationListener {
	/** The time of the last update */
	protected long lastUpdate;
	/** The application hosting the GDX listener */
	protected Application app;
	/** The screen width */
	protected int realWidth;
	/** The screen height */
	protected int realHeight;
	
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
	public GDXGameContainer(Game game,int width,int height,int realWidth, int realHeight) throws SlickException {
		super(game);

		this.game = game;
		this.width = width;
		this.height = height;
		this.realHeight = realHeight;
		this.realWidth = realWidth;
		lastUpdate = System.currentTimeMillis();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.newdawn.slick.GameContainer#getScreenHeight()
	 */
	public int getScreenHeight() {
		return height;
	}

	/*
	 * (non-Javadoc)
	 * @see org.newdawn.slick.GameContainer#getScreenWidth()
	 */
	public int getScreenWidth() {
		return width;
	}

	/**
	 *  Not implemented for android
	 */
	public boolean hasFocus() {
		return true;
	}

	/**
	 *  Not implemented for android
	 */
	public boolean isMouseGrabbed() {
		return false;
	}

	/**
	 *  Not implemented for android
	 */
	public void setDefaultMouseCursor() {
	}

	/**
	 *  Not implemented for android
	 */
	public void setIcon(String ref) throws SlickException {
	}

	/**
	 *  Not implemented for android
	 */
	public void setIcons(String[] refs) throws SlickException {
	}

	/**
	 *  Not implemented for android
	 */
	public void setMouseCursor(String ref, int hotSpotX, int hotSpotY) {
	}

	/**
	 *  Not implemented for android
	 */
	public void setMouseCursor(ImageData data, int hotSpotX, int hotSpotY)
			throws SlickException {
	}

	/**
	 *  Not implemented for android
	 */
	public void setMouseCursor(Image image, int hotSpotX, int hotSpotY)
			throws SlickException {
	}

	/*
	 * (non-Javadoc)
	 * @see org.newdawn.slick.GameContainer#setMouseGrabbed(boolean)
	 */
	public void setMouseGrabbed(boolean grabbed) {
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.ApplicationListener#create()
	 */
	public void create() {
		try {
			initSystem();
			Log.error("Creating application: "+app);
			if (app != null) {
				app.getInput().setInputProcessor(getInput());
			}
			enterOrtho();
			
			game.init(this);
		} catch (SlickException e) {
			Log.error("Failed to init", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.ApplicationListener#dispose()
	 */
	public void dispose() {
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.ApplicationListener#render()
	 */
	public void render() {
		int delta = (int) (System.currentTimeMillis() - lastUpdate);
		lastUpdate = System.currentTimeMillis();
		
		try {
			updateAndRender(delta);
			updateFPS();
		} catch (SlickException e) {
			Log.error("Failed to render/update", e);
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.ApplicationListener#resize(int, int)
	 */
	public void resize(int width, int height) {
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.ApplicationListener#resume()
	 */
	@Override
	public void resume() {
		try {
			initSystem();
			enterOrtho();
			
			InternalTextureLoader.get().reload();
		} catch (SlickException e) {
			Log.error("Failed to init", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.ApplicationListener#pause()
	 */
	@Override
	public void pause() {
	}

	
	/**
	 * Initialise the GL context
	 */
	protected void initGL() {
		Log.error("Starting display "+width+"x"+height+" "+realWidth+"x"+realHeight);
		GL.initDisplay(realWidth, realHeight);
		
		if (input == null) {
			input = new Input(realHeight);
			input.setScale(width / ((float) realWidth), height / ((float) realHeight));
		}
		input.init(realHeight);
		
		if (game instanceof InputListener) {
			input.removeListener((InputListener) game);
			input.addListener((InputListener) game);
		}

		if (graphics != null) {
			graphics.setDimensions(getWidth(), getHeight());
		}
		lastGame = game;
	}
}
