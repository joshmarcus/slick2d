package org.newdawn.hoorah;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * A simple platformer example that uses a combination of platformer style dynamics
 * and pure phys2d bodies by fudging the actor's physics to match those expected.
 * 
 * This source is provided as a simple example where to get started for platformer
 * in Slick using Phys2D.
 * 
 * @author kevin
 */
public class Hoorah extends StateBasedGame {
	/** The container in which the game is running */
	private GameContainer container;
	/** True if vertical sync is turned on */
	private boolean vsync = true;
	
	/**
	 * Create a new game
	 */
	public Hoorah() {
		super("My Last Hoorah");
	}
	
	/**
	 * @see org.newdawn.slick.state.StateBasedGame#initStatesList(org.newdawn.slick.GameContainer)
	 */
	public void initStatesList(GameContainer container) throws SlickException {
		this.container = container;
		
		container.setTargetFrameRate(100);
		container.setVSync(vsync);
		
		addState(new InGameState());
	}

	/**
	 * @see org.newdawn.slick.state.StateBasedGame#keyPressed(int, char)
	 */
	public void keyPressed(int key, char c) {
		super.keyPressed(key, c);
		
		if (key == Input.KEY_F7) {
			vsync = !vsync;
			container.setVSync(vsync);
		}
	}

	/**
	 * Entry point to the simple platformer example
	 * 
	 * @param argv The arguments given to the program
	 * @throws SlickException Indicates a failure to initialise resources
	 */
	public static void main(String[] argv) throws SlickException {
		AppGameContainer container = new AppGameContainer(new Hoorah(), 800, 600, false);
		container.start();
	}
}
