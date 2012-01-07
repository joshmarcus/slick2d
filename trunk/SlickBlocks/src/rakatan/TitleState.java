package rakatan;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class TitleState extends BasicGameState {
	public static final int ID = 2;
	
	private Image back;
	private AngelCodeFont big;
	private AngelCodeFont small;
	private AngelCodeFont tiny;
	private int timer = 3000;
	
	/**
	 * @see org.newdawn.slick.state.BasicGameState#getID()
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @see org.newdawn.slick.state.GameState#init(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame)
	 */
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		back = new Image("res/back.png");
		big = new AngelCodeFont("res/big.fnt","res/big.png");
		small = new AngelCodeFont("res/small.fnt","res/small.png");
		tiny = new AngelCodeFont("res/tiny.fnt","res/tiny.png");
	}

	/**
	 * @see org.newdawn.slick.state.GameState#render(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.scale(2, 2);
		g.fillRect(0, 0, container.getWidth(), container.getHeight(), back, 0, 0);
		g.resetTransform();
		
		String title = "Toy Blocks";
		int x = (container.getWidth() - big.getWidth(title)) / 2;
		big.drawString(x, container.getHeight()/2-100, title);
		
		g.setFont(small);
		String line = "By Kevin Glass";
		x = (container.getWidth() - g.getFont().getWidth(line)) / 2;
		g.drawString(line, x, container.getHeight()/2);

		g.setColor(new Color(0,0,0,0.3f));
		g.fillRect(0,container.getHeight()-55,container.getWidth(),25);
		g.setColor(Color.white);
		g.setFont(tiny);
		line = "http://www.cokeandcode.com";
		x = (container.getWidth() - g.getFont().getWidth(line)) / 2;
		g.drawString(line, x, container.getHeight()-50);
	}

	/**
	 * @see org.newdawn.slick.state.GameState#update(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame, int)
	 */
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		timer -= delta;
		if (timer < 0) {
			game.enterState(ModeSelectState.ID, new FadeOutTransition(Color.white), new FadeInTransition(Color.white));
		}
	}

}
