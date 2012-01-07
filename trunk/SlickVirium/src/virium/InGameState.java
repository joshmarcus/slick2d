package virium;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.PackedSpriteSheet;
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
public class InGameState extends BasicGameState implements GameContext {

	public static final int ID = 1;
	private AreaMap map;
	private PackedSpriteSheet packed;
	
	private Actor player1;
	private Actor player2;
	
	private int player1Controller = 1;
	private int player2Controller = 0;
	
	private StateBasedGame game;
	private Image left;
	private Image right;
	
	/**
	 * @see org.newdawn.slick.state.BasicGameState#getID()
	 */
	public int getID() {
		return ID;
	}

	public void setController(int player, int controller) {
		if (player == 0) {
			player1Controller = controller;
		}
		if (player == 1) {
			player2Controller = controller;
		}
	}
	
	/**
	 * @see org.newdawn.slick.state.GameState#init(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame)
	 */
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		packed = new PackedSpriteSheet("res/pack.def");
		this.game = game;
		
		map = new AreaMap(packed, "res/base1.tmx");
		left = new Image("res/p1board.png");
		right = new Image("res/p2board.png");
	}

	public void initSession() throws SlickException {
		player1 = null;
		player2 = null;
		map = new AreaMap(packed, "res/base1.tmx");
	}
	
	public void setPlayer1Controller(int index) {
		player1Controller = index;
	}

	public void setPlayer2Controller(int index) {
		player2Controller = index;
	}
	
	/**
	 * @see org.newdawn.slick.state.BasicGameState#render(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		if (map != null) {
			map.draw(this, g);
		}
		
		left.draw(-100,550,new Color(1,1,1,0.5f));
		g.drawString("P1",10,560);
		String msg = "";
		if (player1 == null) {
			msg = "PRESS FIRE TO JOIN";
		} else {
			msg = "KEYS: "+pad(player1.getKeys(),2)+"    SCORE: "+pad(player1.getScore(),8);
		}
		g.drawString(msg,10,580);
		
		right.draw(550,550,new Color(1,1,1,0.5f));
		g.drawString("P2",775,560);
		if (player2 == null) {
			msg = "PRESS FIRE TO JOIN";
		} else {
			msg = "KEYS: "+pad(player2.getKeys(),2)+"    SCORE: "+pad(player2.getScore(),8);
		}
		g.drawString(msg,790-g.getFont().getWidth(msg),580);
	}

	private String pad(int score, int len) {
		String reg = ""+score;
		while (reg.length() < len) {
			reg = "0"+reg;
		}
		
		return reg;
	}
	
	/**
	 * @see org.newdawn.slick.state.GameState#update(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame, int)
	 */
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		Input input = container.getInput();
		
		// player 1
		if (player1 == null) {
			if (input.isKeyDown(Keyboard.KEY_RCONTROL) || input.isButton1Pressed(player1Controller)) {
				player1 = new Actor(230,130,packed,"player1",true);
				player1.setTeam(1);
				map.addEntity(player1);
			}
		} else {
			int x = 0;
			int y = 0;
			
			if (input.isKeyDown(Keyboard.KEY_LEFT) || input.isControllerLeft(player1Controller)) {
				x -= 1;
			}
			if (input.isKeyDown(Keyboard.KEY_RIGHT) || input.isControllerRight(player1Controller)) {
				x += 1;
			}
			if (input.isKeyDown(Keyboard.KEY_UP) || input.isControllerUp(player1Controller)) {
				y -= 1;
			}
			if (input.isKeyDown(Keyboard.KEY_DOWN) || input.isControllerDown(player1Controller)) {
				y += 1;
			}
			player1.setFire(input.isKeyDown(Keyboard.KEY_RCONTROL) || input.isButton1Pressed(player1Controller));
			player1.applyDirection(x, y);
		}
		
		// player 2
		if (player2 == null) {
			if (input.isKeyDown(Keyboard.KEY_V) || input.isKeyDown(Keyboard.KEY_LCONTROL) || input.isButton1Pressed(player2Controller)) {
				player2 = new Actor(230,130,packed,"player2",true);
				player2.setTeam(1);
				map.addEntity(player2);
			}
		} else {
			int x = 0;
			int y = 0;

			if (input.isKeyDown(Keyboard.KEY_A) || input.isControllerLeft(player2Controller)) {
				x -= 1;
			}
			if (input.isKeyDown(Keyboard.KEY_D) || input.isControllerRight(player2Controller)) {
				x += 1;
			}
			if (input.isKeyDown(Keyboard.KEY_W) || input.isControllerUp(player2Controller)) {
				y -= 1;
			}
			if (input.isKeyDown(Keyboard.KEY_S) || input.isControllerDown(player2Controller)) {
				y += 1;
			}
			player2.setFire(input.isKeyDown(Keyboard.KEY_V) || input.isKeyDown(Keyboard.KEY_LCONTROL) ||input.isButton1Pressed(player2Controller));
			player2.applyDirection(x, y);
		}
		
		map.update(this, delta);
	}

	/**
	 * @see virium.GameContext#getPlayer1()
	 */
	public Actor getPlayer1() {
		return player1;
	}

	/**
	 * @see virium.GameContext#getPlayer2()
	 */
	public Actor getPlayer2() {
		return player2;
	}

	public void keyPressed(int key, char c) {
		if (key == Input.KEY_ESCAPE) {
			game.enterState(TitleState.ID, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
		}
		if ((key == Input.KEY_1) && (player1 != null)) {
			player1.die();
			player1 = null;
		}
		if ((key == Input.KEY_2) && (player2 != null)) {
			player2.die();
			player2 = null;
		}
	}
	
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		initSession();
	}
	
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		map = null;
	}
}
