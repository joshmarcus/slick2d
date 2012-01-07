package rakatan;

import java.io.IOException;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

import rakatan.data.LoadedLevelState;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class LevelSelectState extends BasicGameState {
	public static final int ID = 3;
	private Image back;
	private AngelCodeFont big;
	private AngelCodeFont small;
	private AngelCodeFont tiny;
	private int levelCount = 6;
	private Image blockTexture;
	private Image floorTexture;
	private Rakatan game;
	
	private LoadedLevelState[] states = new LoadedLevelState[levelCount];
	private int over = -1;
	
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
		this.game = (Rakatan) game;
		back = new Image("res/back.png");
		big = new AngelCodeFont("res/big.fnt","res/big.png");
		small = new AngelCodeFont("res/small.fnt","res/small.png");
		tiny = new AngelCodeFont("res/tiny.fnt","res/tiny.png");
		blockTexture = new Image("res/block.png");
		floorTexture = new Image("res/floor.png");
		
		for (int i=1;i<levelCount;i++) {

			try {
				states[i] = LoadedLevelState.load(ResourceLoader.getResourceAsStream("res/levels/level"+i+".xml"), 
													       	   floorTexture, blockTexture);
				
			} catch (IOException e) {
				Log.error(e);
				throw new SlickException("Failed to load level", e);
			}
		}
	}

	/**
	 * @see org.newdawn.slick.state.GameState#render(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.scale(2, 2);
		g.fillRect(0, 0, container.getWidth(), container.getHeight(), back, 0, 0);
		g.resetTransform();
		
		String title = "Select a Level";
		int x = (container.getWidth() - big.getWidth(title)) / 2;
		big.drawString(x, 20, title);
		
		for (int i=1;i<levelCount;i++) {
			Color col = Color.white;
			if (over != i) {
				col = new Color(1,1,1,0.5f);
			}
			big.drawString(80,200+(i*60),states[i].getName(),col);
		}

		g.setColor(Color.black);
		g.fillRect(548,258,404,304);
		g.setColor(Color.white);
		g.fillRect(549,259,402,302);
		g.setColor(new Color(0.3f,0.6f,0.8f));
		g.fillRect(551,261,398,298);
		if (over != -1) {
			g.setClip(551,261,398,298);
			g.translate(550, 260);
			g.scale(0.4f,0.4f);
			states[over].getTargetState().render(g);
			g.resetTransform();
			g.clearClip();
		}
		
		g.setColor(new Color(0,0,0,0.3f));
		g.fillRect(0,container.getHeight()-55,container.getWidth(),25);
		g.setColor(Color.white);
		g.setFont(tiny);
		String line = "Select a level to play - you can always press escape to choose another later!";
		x = (container.getWidth() - g.getFont().getWidth(line)) / 2;
		g.drawString(line, x, container.getHeight()-50);
	}

	public void keyPressed(int key, char c) {
		if (key == Input.KEY_ESCAPE) {
			game.enterState(ModeSelectState.ID, new FadeOutTransition(Color.white), new FadeInTransition(Color.white));		
		};
	}

	/**
	 * @see org.newdawn.slick.state.GameState#update(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame, int)
	 */
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		over = -1;
		int mx = container.getInput().getMouseX();
		int my = container.getInput().getMouseY();
		
		for (int i=1;i<levelCount;i++) {
			if (my > (200+(i*60)) && (my < (250+(i*60)))) {
				if ((mx > 80) && (mx < big.getWidth(states[i].getName())+80)) {
					over = i;
				}
			}
		}
	}

	
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		super.leave(container, game);
		
		over = -1;
	}

	public void mousePressed(int button, int x, int y) {
		if (over != -1) {
			game.getInGameState().setLevel(over);	
			game.enterState(InGameState.ID, new FadeOutTransition(Color.white), new FadeInTransition(Color.white));		
		}
	}
}
