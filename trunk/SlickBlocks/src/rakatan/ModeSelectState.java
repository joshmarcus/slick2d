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
public class ModeSelectState extends BasicGameState {
	public static final int ID = 4;
	private Image back;
	private AngelCodeFont big;
	private AngelCodeFont small;
	private AngelCodeFont tiny;
	private Image blockTexture;
	private Image floorTexture;
	private Rakatan game;
	
	private int over = -1;
	private String[] options = new String[] {"Match Up","Balance","Free Play"};
	private String[] lines = new String[] {"Stack the blocks to match the target pattern",
										   "Balanace the blocks given with only one of the floor",
										   "Just mess around with a bunch of blocks",
	};
	private String line = "Select a gameplay mode!";
	
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
		
	}

	/**
	 * @see org.newdawn.slick.state.GameState#render(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.scale(2, 2);
		g.fillRect(0, 0, container.getWidth(), container.getHeight(), back, 0, 0);
		g.resetTransform();
		
		String title = "Game Play Mode";
		int x = (container.getWidth() - big.getWidth(title)) / 2;
		big.drawString(x, 20, title);
		
		for (int i=0;i<options.length;i++) {
			Color col = Color.white;
			if (over != i) {
				col = new Color(1,1,1,0.5f);
			}
			big.drawString((1024 - big.getWidth(options[i]))/2,300+(i*100),options[i],col);
		}
		
		g.setColor(new Color(0,0,0,0.3f));
		g.fillRect(0,container.getHeight()-55,container.getWidth(),25);
		g.setColor(Color.white);
		g.setFont(tiny);
		x = (container.getWidth() - g.getFont().getWidth(line)) / 2;
		g.drawString(line, x, container.getHeight()-50);
	}

	public void keyPressed(int key, char c) {
		if (key == Input.KEY_ESCAPE) {
			System.exit(0);
		};
	}

	/**
	 * @see org.newdawn.slick.state.GameState#update(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame, int)
	 */
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		over = -1;
		line = "Select a gameplay mode!";
		int mx = container.getInput().getMouseX();
		int my = container.getInput().getMouseY();
		
		for (int i=0;i<options.length;i++) {
			if (my > (300+(i*100)) && (my < (390+(i*100)))) {
				int width = big.getWidth(options[i]) / 2;
				
				if ((mx > 512-width) && (mx < 512+width)) {
					over = i;
				}
			}
		}
		if (over != -1) {
			line = lines[over];
		}
	}

	
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		super.leave(container, game);
		
		over = -1;
	}

	public void mousePressed(int button, int x, int y) {
		if (over != -1) {
			if (over == 0) {
				game.enterState(LevelSelectState.ID, new FadeOutTransition(Color.white), new FadeInTransition(Color.white));		
				return;
			}
			if (over == 1) {
				game.getInGameState().setLevel(100);	
				game.enterState(InGameState.ID, new FadeOutTransition(Color.white), new FadeInTransition(Color.white));		
				return;
			}
			if (over == 2) {
				game.getInGameState().setLevel(0);	
				game.enterState(InGameState.ID, new FadeOutTransition(Color.white), new FadeInTransition(Color.white));		
				return;
			}
		}
	}
}
