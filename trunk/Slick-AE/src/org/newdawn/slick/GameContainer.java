package org.newdawn.slick;

import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.util.Log;

/**
 * A generic game container that handles the game loop, fps recording and
 * managing the input system
 *
 * @author kevin
 */
public abstract class GameContainer {
	/** The renderer to use for all GL operations */
	protected static SGL GL = Renderer.get();
	
	/** The time the last frame was rendered */
	protected long lastFrame;
	/** The last time the FPS recorded */
	protected long lastFPS;
	/** The last recorded FPS */
	protected int recordedFPS;
	/** The current count of FPS */
	protected int fps;
	/** True if we're currently running the game loop */
	protected boolean running = true;
	
	/** The width of the display */
	protected int width;
	/** The height of the display */
	protected int height;
	/** The game being managed */
	protected Game game;
	
	/** The default font to use in the graphics context */
	private Font defaultFont;
	/** The graphics context to be passed to the game */
	protected Graphics graphics;
	
	/** The input system to pass to the game */
	protected Input input;
	/** The FPS we want to lock to */
	protected int targetFPS = -1;
	/** True if we should show the fps */
	private boolean showFPS = true;
	/** The minimum logic update interval */
	protected long minimumLogicInterval = 1;
	/** The stored delta */
	protected long storedDelta;
	/** The maximum logic update interval */
	protected long maximumLogicInterval = 0;
	/** The last game started */
	protected Game lastGame;
	/** True if we should clear the screen each frame */
	protected boolean clearEachFrame = true;
	
	/** True if the game is paused */
	protected boolean paused;
	/** True if we should force exit */
	protected boolean forceExit = true;
	/** True if vsync has been requested */
	protected boolean vsync;
	/** Smoothed deltas requested */
	protected boolean smoothDeltas;
	/** The number of samples we'll attempt through hardware */
	protected int samples;
	
	/** True if this context supports multisample */
	protected boolean supportsMultiSample;
	
	/** True if we should render when not focused */
	protected boolean alwaysRender;
	
	/**
	 * Create a new game container wrapping a given game
	 * 
	 * @param game The game to be wrapped
	 */
	protected GameContainer(Game game) {
		this.game = game;
		lastFrame = getTime();

		getBuildVersion();
		Log.checkVerboseLogSetting();
	}

	/**
	 * Set the default font that will be intialised in the graphics held in this container
	 * 
	 * @param font The font to use as default
	 */
	public void setDefaultFont(Font font) {
		if (font != null) {
			this.defaultFont = font;
		} else {
			Log.warn("Please provide a non null font");
		}
	}
	
	/**
	 * Indicate whether we want to try to use fullscreen multisampling. This will
	 * give antialiasing across the whole scene using a hardware feature.
	 * 
	 * @param samples The number of samples to attempt (2 is safe)
	 */
	public void setMultiSample(int samples) {
		this.samples = samples;
	}
	
	/**
	 * Check if this hardware can support multi-sampling
	 * 
	 * @return True if the hardware supports multi-sampling
	 */
	public boolean supportsMultiSample() {
		return supportsMultiSample;
	}
	
	/**
	 * The number of samples we're attempting to performing using
	 * hardware multisampling
	 * 
	 * @return The number of samples requested
	 */
	public int getSamples() {
		return samples;
	}
	
	/**
	 * Indicate if we should force exitting the VM at the end
	 * of the game (default = true)
	 * 
	 * @param forceExit True if we should force the VM exit
	 */
	public void setForceExit(boolean forceExit) {
		this.forceExit = forceExit;
	}
	
	/**
	 * Indicate if we want to smooth deltas. This feature will report
	 * a delta based on the FPS not the time passed. This works well with 
	 * vsync.
	 * 
	 * @param smoothDeltas True if we should report smooth deltas
	 */
	public void setSmoothDeltas(boolean smoothDeltas) {
		this.smoothDeltas = smoothDeltas;
	}
	
	/**
	 * Check if the display is in fullscreen mode
	 * 
	 * @return True if the display is in fullscreen mode
	 */
	public boolean isFullscreen() {
		return false;
	}
	
	/**
	 * Get the aspect ratio of the screen
	 * 
	 * @return The aspect ratio of the display
	 */
	public float getAspectRatio() {
		return getWidth() / getHeight();
	}
	
	/**
	 * Indicate whether we want to be in fullscreen mode. Note that the current
	 * display mode must be valid as a fullscreen mode for this to work
	 * 
	 * @param fullscreen True if we want to be in fullscreen mode
	 * @throws SlickException Indicates we failed to change the display mode
	 */
	public void setFullscreen(boolean fullscreen) throws SlickException {
	}
	
	/**
	 * Indicate if we should clear the screen at the beginning of each frame. If you're
	 * rendering to the whole screen each frame then setting this to false can give
	 * some performance improvements
	 * 
	 * @param clear True if the the screen should be cleared each frame
	 */
	public void setClearEachFrame(boolean clear) {
		this.clearEachFrame = clear;
	}
	
	/**
	 * Renitialise the game and the context in which it's being rendered
	 * 
	 * @throws SlickException Indicates a failure rerun initialisation routines
	 */
	public void reinit() throws SlickException {
	}
	
	/**
	 * Check if the container is currently paused.
	 * 
	 * @return True if the container is paused
	 */
	public boolean isPaused() {
		return paused;
	}
	
	/**
	 * Indicates if the game should be paused, i.e. if updates
	 * should be propogated through to the game.
	 * 
	 * @param paused True if the game should be paused
	 */
	public void setPaused(boolean paused)
	{
		this.paused = paused;
	}
	
	/** 
	 * True if this container should render when it has focus
	 * 
	 * @return True if this container should render when it has focus
	 */
	public boolean getAlwaysRender () {
		return alwaysRender;
	}

	/** 
	 * Indicate whether we want this container to render when it has focus
	 * 
	 * @param alwaysRender True if this container should render when it has focus
	 */
	public void setAlwaysRender (boolean alwaysRender) {
		this.alwaysRender = alwaysRender;
	}
	
	/**
	 * Get the build number of slick 
	 * 
	 * @return The build number of slick
	 */
	public static int getBuildVersion() {
		return 0;
	}
	
	/**
	 * Get the default system font
	 * 
	 * @return The default system font
	 */
	public Font getDefaultFont() {
		return defaultFont;
	}
	
	/**
	 * Check if sound effects are enabled
	 * 
	 * @return True if sound effects are enabled
	 */
	public boolean isSoundOn() {
		return SoundStore.get().soundsOn();
	}

	/**
	 * Check if music is enabled
	 * 
	 * @return True if music is enabled
	 */
	public boolean isMusicOn() {
		return SoundStore.get().musicOn();
	}
	
	/**
	 * Indicate whether music should be enabled
	 * 
	 * @param on True if music should be enabled
	 */ 
	public void setMusicOn(boolean on) {
		SoundStore.get().setMusicOn(on);
	}

	/**
	 * Indicate whether sound effects should be enabled
	 * 
	 * @param on True if sound effects should be enabled
	 */ 
	public void setSoundOn(boolean on) {
		SoundStore.get().setSoundsOn(on);
	}
	
	/**
	 * Retrieve the current default volume for music
	 * @return the current default volume for music
	 */
	public float getMusicVolume() {
		return SoundStore.get().getMusicVolume();
	}
	
	/**
	 * Retrieve the current default volume for sound fx
	 * @return the current default volume for sound fx
	 */
	public float getSoundVolume() {
		return SoundStore.get().getSoundVolume();
	}
	
	/**
	 * Set the default volume for sound fx
	 * @param volume the new default value for sound fx volume
	 */
	public void setSoundVolume(float volume) {
		SoundStore.get().setSoundVolume(volume);
	}

	/**
	 * Set the default volume for music
	 * @param volume the new default value for music volume
	 */
	public void setMusicVolume(float volume) {
		SoundStore.get().setMusicVolume(volume);
	}
	
	/**
	 * Get the width of the standard screen resolution
	 * 
	 * @return The screen width
	 */
	public abstract int getScreenWidth();
	
	/**
	 * Get the height of the standard screen resolution
	 * 
	 * @return The screen height
	 */
	public abstract int getScreenHeight();
	
	/**
	 * Get the width of the game canvas
	 * 
	 * @return The width of the game canvas
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Get the height of the game canvas
	 * 
	 * @return The height of the game canvas
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Set the icon to be displayed if possible in this type of
	 * container
	 * 
	 * @param ref The reference to the icon to be displayed
	 * @throws SlickException Indicates a failure to load the icon
	 */
	public abstract void setIcon(String ref) throws SlickException;
	
	/**
	 * Set the icons to be used for this application. Note that the size of the icon
	 * defines how it will be used. Important ones to note
	 * 
	 * Windows window icon must be 16x16
	 * Windows alt-tab icon must be 24x24 or 32x32 depending on Windows version (XP=32)
	 * 
	 * @param refs The reference to the icon to be displayed
	 * @throws SlickException Indicates a failure to load the icon
	 */
	public abstract void setIcons(String[] refs) throws SlickException;
	
	/**
	 * Get the accurate system time
	 * 
	 * @return The system time in milliseconds
	 */
	public long getTime() {
		return System.currentTimeMillis();
	}

	/**
	 * Sleep for a given period
	 * 
	 * @param milliseconds The period to sleep for in milliseconds
	 */
	public void sleep(int milliseconds) {
		long target = getTime()+milliseconds;
		while (getTime() < target) {
			try { Thread.sleep(1); } catch (Exception e) {}
		}
	}
	
	/**
	 * Set the default mouse cursor - i.e. the original cursor before any native 
	 * cursor was set
	 */
	public abstract void setDefaultMouseCursor();
	
	/**
	 * Get the input system
	 * 
	 * @return The input system available to this game container
	 */
	public Input getInput() {
		return input;
	}

	/**
	 * Get the current recorded FPS (frames per second)
	 * 
	 * @return The current FPS
	 */
	public int getFPS() {
		return recordedFPS;
	}
	
	/**
	 * Indicate whether mouse cursor should be grabbed or not
	 * 
	 * @param grabbed True if mouse cursor should be grabbed
	 */
	public abstract void setMouseGrabbed(boolean grabbed);
	
	/**
	 * Check if the mouse cursor is current grabbed. This will cause it not
	 * to be seen.
	 * 
	 * @return True if the mouse is currently grabbed
	 */
	public abstract boolean isMouseGrabbed();
	
	/**
	 * Retrieve the time taken to render the last frame, i.e. the change in time - delta.
	 * 
	 * @return The time taken to render the last frame
	 */
	protected int getDelta() {
		long time = getTime();
		int delta = (int) (time - lastFrame);
		lastFrame = time;
		
		return delta;
	}
	
	/**
	 * Updated the FPS counter
	 */
	protected void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			lastFPS = getTime();
			recordedFPS = fps;
			fps = 0;
		}
		fps++;
	}
	
	/**
	 * Set the minimum amount of time in milliseonds that has to 
	 * pass before update() is called on the container game. This gives
	 * a way to limit logic updates compared to renders.
	 * 
	 * @param interval The minimum interval between logic updates
	 */
	public void setMinimumLogicUpdateInterval(int interval) {
		minimumLogicInterval = interval;
	}

	/**
	 * Set the maximum amount of time in milliseconds that can passed
	 * into the update method. Useful for collision detection without
	 * sweeping.
	 * 
	 * @param interval The maximum interval between logic updates
	 */
	public void setMaximumLogicUpdateInterval(int interval) {
		maximumLogicInterval = interval;
	}
	
	/**
	 * Update and render the game
	 * 
	 * @param delta The change in time since last update and render
	 * @throws SlickException Indicates an internal fault to the game.
	 */
	protected void updateAndRender(int delta) throws SlickException {
		if (smoothDeltas) {
			if (getFPS() != 0) {
				delta = 1000 / getFPS();
			}
		}

		input.poll(width, height);
		
//		Music.poll(delta);
		if (!paused) {
			storedDelta += delta;
			if (storedDelta >= minimumLogicInterval) {
				try {
					if (maximumLogicInterval != 0) {
						long cycles = storedDelta / maximumLogicInterval;
						for (int i=0;i<cycles;i++) {
							game.update(this, (int) maximumLogicInterval);
						}
						
						int remainder = (int) (delta % maximumLogicInterval);
						if (remainder > minimumLogicInterval) {
							game.update(this, (int) (delta % maximumLogicInterval));
							storedDelta = 0;
						} else {
							storedDelta = remainder;
						}
					} else {
						game.update(this, (int) storedDelta);
						storedDelta = 0;
					}
					
				} catch (Throwable e) {
					Log.error(e);
					throw new SlickException("Game.update() failure - check the game code.");
				}
			}
		} else {
			game.update(this, 0);
		}
		
		if (hasFocus() || getAlwaysRender()) {
			if (clearEachFrame) {
				GL.glClear(SGL.GL_COLOR_BUFFER_BIT | SGL.GL_DEPTH_BUFFER_BIT);
			} 
			
			GL.glLoadIdentity();
			
			graphics.resetFont();
			graphics.resetLineWidth();
			graphics.setAntiAlias(false);
			try {
				game.render(this, graphics);
			} catch (Throwable e) {
				Log.error(e);
				throw new SlickException("Game.render() failure - check the game code.");
			}
			graphics.resetTransform();
			
			if (showFPS) {
				defaultFont.drawString(10, 10, "FPS: "+recordedFPS);
			}
			
			GL.flush();
		}
	}
	
	/**
	 * Indicate if the display should update only when the game is visible
	 * (the default is true)
	 * 
	 * @param updateOnlyWhenVisible True if we should updated only when the display is visible
	 */
	public void setUpdateOnlyWhenVisible(boolean updateOnlyWhenVisible) {
	}

	/**
	 * Check if this game is only updating when visible to the user (default = true)
	 * 
	 * @return True if the game is only updated when the display is visible
	 */
	public boolean isUpdatingOnlyWhenVisible() {
		return true;
	}
	
	/** 
	 * Not implemented for android
	 */
	public void setVSync(boolean sync) {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.newdawn.slick.gui.GUIContext#setMouseCursor(java.lang.String, int, int)
	 */
	public void setMouseCursor(String ref, int x, int y) {
		
	}
	
	/**
	 * Initialise the GL context
	 */
	protected abstract void initGL();
	
	/**
	 * Initialise the system components, OpenGL and OpenAL.
	 * 
	 * @throws SlickException Indicates a failure to create a native handler
	 */
	protected void initSystem() throws SlickException {
		initGL();
//		setMusicVolume(1.0f);
//		setSoundVolume(1.0f);

		try {
			Graphics.DEFAULT_FONT = new AngelCodeFont("defaultfont.fnt", "defaultfont.png");
		} catch (SlickException e) {
			Log.error(e);
		}
		graphics = new Graphics(width, height);
		defaultFont = graphics.getFont();
	}
	
	/**
	 * Enter the orthographic mode 
	 */
	protected void enterOrtho() {
		enterOrtho(width, height);
	}
	
	/**
	 * Indicate whether the container should show the FPS
	 * 
	 * @param show True if the container should show the FPS
	 */
	public void setShowFPS(boolean show) {
		showFPS = show;
	}
	
	/**
	 * Check if the FPS is currently showing
	 * 
	 * @return True if the FPS is showing
	 */
	public boolean isShowingFPS() {
		return showFPS;
	}
	
	/**
	 * Set the target fps we're hoping to get
	 * 
	 * @param fps The target fps we're hoping to get
	 */
	public void setTargetFrameRate(int fps) {
		targetFPS = fps;
	}
	
	/**
	 * True if the game is running
	 * 
	 * @return True if the game is running
	 */
	protected boolean running() {
		return running;
	}
	
	/**
	 * Inidcate we want verbose logging
	 * 
	 * @param verbose True if we want verbose logging (INFO and DEBUG)
	 */
	public void setVerbose(boolean verbose) {
		Log.setVerbose(verbose);
	}
	
	/**
	 * Cause the game to exit and shutdown cleanly
	 */
	public void exit() {
		running = false;
	}
	
	/**
	 * Check if the game currently has focus
	 * 
	 * @return True if the game currently has focus
	 */
	public abstract boolean hasFocus();
	
	/**
	 * Get the graphics context used by this container. Note that this 
	 * value may vary over the life time of the game.
	 * 
	 * @return The graphics context used by this container
	 */
	public Graphics getGraphics() {
		return graphics;
	}
	
	/**
	 * Enter the orthographic mode 
	 * 
	 * @param xsize The size of the panel being used
	 * @param ysize The size of the panel being used
	 */
	protected void enterOrtho(int xsize, int ysize) {
		GL.enterOrtho(xsize, ysize);
	}
}
