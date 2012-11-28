package virium;

import java.io.IOException;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SavedState;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.BasicComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.MouseOverArea;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.Log;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class TitleState extends BasicGameState implements ComponentListener {
	public static final int ID = 2;
	
	private Image bg;
	private Image logo;
	private MouseOverArea p1;
	private MouseOverArea p2;
	private MouseOverArea quit;
	private MouseOverArea play;
	private GameContainer container;
	
	private String[] controls = new String[] {"Unplayed", "Keyboard", "Gamepad"};
	private String[] subtext1 = new String[] {"", "(Cursors, RCtrl)", "Hit Fire to Configure"};
	private String[] subtext2 = new String[] {"", "(W,A,S,D, V/LCtrl)", "Hit Fire to Configure"};
	
	private int controlType1 = 1;
	private int controller1 = 0;
	private int controlType2 = 0;
	private int controller2 = 0;
	
	private Font font;
	private StateBasedGame game;
	private boolean waitingForButton1;
	private boolean waitingForButton2;
	private Virium virium;
	
	private SavedState savedConfig;
	
	public TitleState(Virium virium) {
		this.virium = virium;
	}
	
	/**
	 * @see org.newdawn.slick.state.BasicGameState#getID()
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @see org.newdawn.slick.state.GameState#init(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame)
	 */
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		try {
			savedConfig = new SavedState("viriumConfig");
			controlType1 = (int) savedConfig.getNumber("controlType1", 1);
			controller1 = (int) savedConfig.getNumber("controller1", 1);
			controlType2 = (int) savedConfig.getNumber("controlType2", 0);
			controller2 = (int) savedConfig.getNumber("controller2", 0);
			
			updateController1();
			updateController2();
		} catch (Exception e) {
			Log.error(e);
		}
		
		this.container = container;
		this.game = game;
		
		font = new AngelCodeFont("res/font.fnt","res/font_00.tga");
		
		bg = new Image("res/background.png");
		logo = new Image("res/logo.png");
		p1 = new MouseOverArea(container,new Image("res/player1.png"),120,200,this);
		p1.setNormalColor(new Color(0.7f,0.7f,0.7f,1f));
		p1.setMouseOverColor(new Color(0.9f,0.9f,0.9f,1f));
		p2 = new MouseOverArea(container,new Image("res/player2.png"),470,200,this);
		p2.setNormalColor(new Color(0.7f,0.7f,0.7f,1f));
		p2.setMouseOverColor(new Color(0.9f,0.9f,0.9f,1f));
		quit = new MouseOverArea(container,new Image("res/quit.png"),350,520,this);
		quit.setNormalColor(new Color(0.7f,0.7f,0.7f,1f));
		quit.setMouseOverColor(new Color(0.9f,0.9f,0.9f,1f));
		play = new MouseOverArea(container,new Image("res/play.png"),340,430,this);
		play.setNormalColor(new Color(0.7f,0.7f,0.7f,1f));
		play.setMouseOverColor(new Color(0.9f,0.9f,0.9f,1f));
	}

	/**
	 * @see org.newdawn.slick.state.BasicGameState#render(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		bg.draw(0,0,800,600);
		logo.draw(250,30);
		
		p1.render(container, g);
		font.drawString(220-(font.getWidth(controls[controlType1])/2),300,controls[controlType1]);
		if ((controlType1 != 2) || (waitingForButton1)) {
			font.drawString(220-(font.getWidth(subtext1[controlType1])/2),345,subtext1[controlType1],Color.yellow);
		}
		
		p2.render(container, g);
		font.drawString(570-(font.getWidth(controls[controlType2])/2),300,controls[controlType2]);
		if ((controlType2 != 2) || (waitingForButton2)) {
			font.drawString(570-(font.getWidth(subtext2[controlType2])/2),345,subtext2[controlType2],Color.yellow);
		}
		
		quit.render(container, g);
		play.render(container, g);
	}

	/**
	 * @see org.newdawn.slick.state.GameState#update(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame, int)
	 */
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		
	}

	/**
	 * @see org.newdawn.slick.state.BasicGameState#keyPressed(int, char)
	 */
	public void keyPressed(int key, char c) {
		if (key == Input.KEY_ESCAPE) {
			container.exit();
		}
	}
	
	private void saveState() {
		if (savedConfig != null) {
			savedConfig.setNumber("controlType1", controlType1);
			savedConfig.setNumber("controlType2", controlType2);
			savedConfig.setNumber("controller1", controller1);
			savedConfig.setNumber("controller2", controller2);
			
			try {
				savedConfig.save();
			} catch (IOException e) {
				Log.error(e);
			}
		}
	}
	
	private void updateController2() {
		virium.setController(1,controller2);
		if (controller1 == controller2) {
			controlType1 = 0;
			controller1 = 1000;
			virium.setController(0,controller1);
		}
		
		saveState();
	}
	
	private void updateController1() {
		virium.setController(0,controller1);
		if (controller2 == controller1) {
			controlType2 = 0;
			controller2 = 1000;
			virium.setController(1,controller2);
		}
		
		saveState();
	}
	
	public void controllerButtonPressed(int controller, int button) {
		if (waitingForButton1) {
			waitingForButton1 = false;
			controller1 = controller;
			updateController1();
			
		}
		if (waitingForButton2) {
			waitingForButton2 = false;
			controller2 = controller;
			updateController2();
		}
	}

	/**
	 * @see org.newdawn.slick.gui.ComponentListener#componentActivated(org.newdawn.slick.gui.BasicComponent)
	 */
	public void componentActivated(BasicComponent source) {
		if (source == quit) {
			container.exit();
		}
		if (source == play) {
			game.enterState(InGameState.ID, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
		}
		if (source == p1) {
			controlType1 = (controlType1+1)%3;
			waitingForButton1 = false;
			if (controlType1 == 2) {
				waitingForButton1 = true;
				waitingForButton2 = false;
			} else {
				saveState();
			}
		}
		if (source == p2) {
			controlType2 = (controlType2+1)%3;
			waitingForButton2 = false;
			if (controlType2 == 2) {
				waitingForButton2 = true;
				waitingForButton1 = false;
			} else {
				saveState();
			}
		}
	}

}
