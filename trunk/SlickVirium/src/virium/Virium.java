package virium;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class Virium extends StateBasedGame {

	private InGameState ingame = new InGameState();
	private TitleState title;
	private AppGameContainer container;
	
	public Virium() {
		super("Virium");
	}
	
	/**
	 * @see org.newdawn.slick.state.StateBasedGame#initStatesList(org.newdawn.slick.GameContainer)
	 */
	public void initStatesList(GameContainer container) throws SlickException {
		if (container instanceof AppGameContainer) {
			this.container = (AppGameContainer) container;
		}
		
		title = new TitleState(this);
		
		container.setShowFPS(false);
		container.setMinimumLogicUpdateInterval(10);
		container.setMaximumLogicUpdateInterval(10);
		addState(title);
		addState(ingame);
	}

	public void setController(int player, int controller) {
		ingame.setController(player, controller);
	}
	
	/**
	 * @see org.newdawn.slick.state.StateBasedGame#keyPressed(int, char)
	 */
	public void keyPressed(int key, char c) {
		super.keyPressed(key, c);

		if (key == Input.KEY_F2) {
			if (container != null) {
				try {
					container.setFullscreen(!container.isFullscreen());
				} catch (SlickException e) {
					Log.error(e);
				}
			}
		}
	}
	
	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments passed to the test
	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new Virium());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
