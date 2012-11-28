package rakatan;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class Rakatan extends StateBasedGame {
	private InGameState ingame;
	
	public Rakatan() {
		super("Toy Blocks");
	}

	/**
	 * @see org.newdawn.slick.state.StateBasedGame#initStatesList(org.newdawn.slick.GameContainer)
	 */
	public void initStatesList(GameContainer container) throws SlickException {
		container.setIcon("res/icon.png");
		Music music = new Music("res/toybox.mod");
		music.loop(1.0f,0.2f);

		ingame = new InGameState();
		
		addState(new TitleState());
		addState(new ModeSelectState());
		addState(new LevelSelectState());
		addState(ingame);
	}
	
	public InGameState getInGameState() {
		return ingame;
	}
	
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new Rakatan(), 1024, 768, false);
			container.start();
		} catch (Exception e) {
			Log.error(e);
		}
	}
}
