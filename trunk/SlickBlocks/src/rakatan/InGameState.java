package rakatan;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

import rakatan.data.DynamicArcElement;
import rakatan.data.DynamicBlockElement;
import rakatan.data.DynamicWedgeElement;
import rakatan.data.Level;
import rakatan.data.LevelData;
import rakatan.data.LevelElement;
import rakatan.data.LevelListener;
import rakatan.data.LoadedLevelState;
import rakatan.data.StaticBlockElement;

/**
 * TODO: Document this class
 * 
 * @author kevin
 */
public class InGameState extends BasicGameState implements GameState, LevelListener {
	public static boolean RESTING_BODDIES = false;
	public static boolean SHOW_MATCHES = false;
	public static final int ID = 1;
	
	
	private Level level;
	private Image back;
	private LevelElement selected;
	private float originalRotation;
	private float xoffset;
	private float yoffset;
	private GameContainer container;
	private float oldmx;
	private float oldmy;
	
	private AngelCodeFont big;
	private AngelCodeFont small;
	private AngelCodeFont tiny;
	private int nextLevel;
	
	private float xp = 800;
	private String instructions = ".. Drag with LMB to move .. Hold RMB to rotate .. Press R to reset .." +
	                              "Press Escape to choose a different level .. Press F to toggle Fullscreen .." +
	                              "Press Space to toggle the target .. Press M to toggle music/sound ..";
	private boolean rmb;
	
	private LevelElement over;
	private int mouseMoveIgnoreCount = 0;
	private boolean editMode = false;
	
	private String[] messages = new String[] {"","","","","",""};
	private Image blockTexture;
	private Image floorTexture;
	private LevelData levelData;
	private LoadedLevelState loadedState;
	private Sound sound;
	private int lastPlay;
	private boolean showTarget = true;
	private int lastCheck = 2000;
	private float currentMatch;
	private StateBasedGame game;
	private int matched = 0;
	private StaticBlockElement wall1;
	private StaticBlockElement wall2;
	private StaticBlockElement floor;
	private int removeTimer;
	private boolean balanced = false;
	
	public void setLevel(int level) 
	{
		nextLevel = level;
		try {
			reset();
		} catch (Exception e) {
			Log.error(e);
		}
	}
	
	/**
	 * @see org.newdawn.slick.state.BasicGameState#getID()
	 */
	public int getID() {
		return ID;
	}

	private void addMessage(String message) {
		for (int i=0;i<messages.length-1;i++) {
			messages[i] = messages[i+1];
		}
		messages[messages.length-1] = message;
	}
	
	/**
	 * @see org.newdawn.slick.state.GameState#init(org.newdawn.slick.GameContainer,
	 *      org.newdawn.slick.state.StateBasedGame)
	 */
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		this.game = game;
		this.container = container;
		container.setTargetFrameRate(100);
		container.setShowFPS(false);
		
		sound = new Sound("res/click.wav");
		big = new AngelCodeFont("res/big.fnt","res/big.png");
		small = new AngelCodeFont("res/small.fnt","res/small.png");
		tiny = new AngelCodeFont("res/tiny.fnt","res/tiny.png");
		
		back = new Image("res/back.png");
		reset();
	}
	
	private void addFloor() {
		floor = new StaticBlockElement(-500, container.getHeight()-40, container.getWidth()+1000, 50, floorTexture);
		level.add(floor);
	}
	
	private void reset() throws SlickException {
		blockTexture = new Image("res/block.png");
		floorTexture = new Image("res/floor.png");
		selected = null;
		if (nextLevel != 100) {
			try {
				loadedState = LoadedLevelState.load(ResourceLoader.getResourceAsStream("res/levels/level"+nextLevel+".xml"), 
													       	   floorTexture, blockTexture);
				level = loadedState.getInitialState();
				level.addListener(this);
				lastPlay = 100;
				check();
			} catch (IOException e) {
				Log.error(e);
				throw new SlickException("Failed to load level", e);
			}
	
			level.add(new StaticBlockElement(-51, -500, 50, 1500, floorTexture));	
			level.add(new StaticBlockElement(1025, -500, 50, 1500, floorTexture));
		} else {
			level = new Level();
			addFloor();
			balanced = false;
			for (int i=0;i<13;i++) {
				int x = (int) ((200) + (Math.random() * 500));
				int y = 600 - (i*60);
				int r = (int) (Math.random() * 6);
				switch (r) {
				case 0:
					level.add(new DynamicBlockElement(x, y, 50, 50, blockTexture, Color.yellow));
					break;
				case 1:
					level.add(new DynamicBlockElement(x, y, 250, 20, blockTexture, Color.cyan));
					break;
				case 2:
					level.add(new DynamicBlockElement(x, y, 100, 30, blockTexture, Color.blue));
					break;
				case 3:
					level.add(new DynamicBlockElement(x, y, 70, 40, blockTexture, Color.green));
					break;
				case 4:
					level.add(new DynamicWedgeElement(x, y, 70, 40, blockTexture, Color.red));
					break;
				case 5:
					level.add(new DynamicArcElement(x, y, 100, 50, 10, blockTexture, Color.magenta));
					break;
				}
			}
			
			wall1 = new StaticBlockElement(-51, -500, 50, 1500, floorTexture);
			wall2 = new StaticBlockElement(1025, -500, 50, 1500, floorTexture);
			level.add(wall1);	
			level.add(wall2);
			removeTimer = 5000;
			level.addListener(this);
			lastPlay = 100;
		}
	}

	/**
	 * @see org.newdawn.slick.state.GameState#render(org.newdawn.slick.GameContainer,
	 *      org.newdawn.slick.state.StateBasedGame, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.scale(2, 2);
		g.fillRect(0, 0, container.getWidth(), container.getHeight(), back, 0, 0);
		g.resetTransform();

		level.render(g);
		if (over != null) {
			g.setLineWidth(2);
			g.scale(1/Level.SCALE, 1/Level.SCALE);
			over.render(g, Color.white);
			g.resetTransform();
			g.setLineWidth(1);
		}

		if ((showTarget) && (nextLevel != 0) && (nextLevel != 100)) {
			g.setColor(Color.black);
			g.fillRect(38,48,114,104);
			g.setColor(Color.white);
			g.fillRect(39,49,112,102);
			g.setClip(40,50,110,100);
			g.setColor(new Color(0.3f,0.6f,0.8f));
			g.fillRect(40,50,300,300);
			g.setColor(Color.white);
			g.translate(0,0);
			g.scale(0.2f, 0.2f);
			loadedState.getTargetState().render(g);
			g.resetTransform();
			g.clearClip();
			big.drawString(2, 20, "Target");
			
			String cm = ""+(((int) (100 * (currentMatch))));
			cm+="%";
			big.drawString(810+(big.getWidth("Match")/2)-(big.getWidth(cm)/2), 60, cm);
			big.drawString(810, 20, "Match");
		}

		g.setFont(tiny);
		if ((matched > 1000) && (nextLevel != 0) && (nextLevel != 100)) {
			String title = "Matched! Good Job!";
			int x = (container.getWidth() - big.getWidth(title)) / 2;
			float yoffset = (float) Math.sin(matched / 100.0f) * 10.0f;
			big.drawString(x, 200-yoffset, title,Color.yellow);
			String line = "(Press Escape to Choose Another)";
			x = (container.getWidth() - g.getFont().getWidth(line)) / 2;
			g.drawString(line, x, 250);
		}
		if ((balanced) && (nextLevel == 100)) {
			String title = "Balance! Nice Work!";
			int x = (container.getWidth() - big.getWidth(title)) / 2;
			float yoffset = (float) Math.sin(matched / 100.0f) * 10.0f;
			big.drawString(x, 200-yoffset, title,Color.yellow);
			String line = "(Press R for a new setup)";
			x = (container.getWidth() - g.getFont().getWidth(line)) / 2;
			g.drawString(line, x, 250);
		}
		
		String title = "Toy Blocks";
		int x = (container.getWidth() - big.getWidth(title)) / 2;
		big.drawString(x, 7, title);

		String line = "blocks@cokeandcode.com";
		x = (container.getWidth() - g.getFont().getWidth(line)) / 2;
		g.drawString(line, x, 50);
		line = "http://www.cokeandcode.com";
		x = (container.getWidth() - g.getFont().getWidth(line)) / 2;
		g.drawString(line, x, 65);
		if (editMode) {
			line = "[EDIT MODE]";
			x = (container.getWidth() - g.getFont().getWidth(line)) / 2;
			g.drawString(line, x, 80);
		
			for (int i=0;i<messages.length;i++) {
				g.drawString(messages[i], 5, 700+(i*15)-(messages.length * 15));
			}
		}
		
		small.drawString(xp, container.getHeight()-30, instructions);
	}

	/**
	 * @see org.newdawn.slick.state.GameState#update(org.newdawn.slick.GameContainer,
	 *      org.newdawn.slick.state.StateBasedGame, int)
	 */
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		if (wall1 != null) {
			removeTimer -= delta;
			if (removeTimer < 0) {
				level.remove(wall1);
				level.remove(wall2);
				wall1 = null;
				wall2 = null;
			}
		}
		if (nextLevel == 100) {
			matched += delta;
			if (selected == null) {
				if (floor.getBody().getTouching().size() == 1) {
					if (floor.getBody().getConnected(false).size() == 13) {
						balanced = true;
					}
				}
			}
		} else {
			if (currentMatch == 1) {
				matched += delta;
			} else {
				matched = 0;
			}
		}
		if (lastPlay >= 0) {
			lastPlay -= delta;
		}
		lastCheck -= delta;
		if (lastCheck < 0) {
			check();
		}
		
		xp -= (delta * 0.1f);
		if (xp < -small.getWidth(instructions)) 
		{
			xp = 900;
		}
		
		float x = 0;
		float y = 0;
		
		if (selected != null) {
			x = selected.getX();
			y = selected.getY();
		}
		level.update(delta);
		if (selected != null) {
			selected.setPosition(x, y);
			selected.setRotation(originalRotation);
			if (selected.getBody().getVelocity().getY() > 0) {
				selected.makeStill();
			}
		} 
	}

	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		if (mouseMoveIgnoreCount > 0) {
			mouseMoveIgnoreCount--;
			return;
		}
		
		if (selected != null) {
			if (rmb) {
				float oldRot = selected.getRotation();
				ArrayList before = level.getCollisions(selected);
				selected.setRotation(oldRot + (newx - oldx) * 0.01f);
				ArrayList after = level.getCollisions(selected);

				boolean fine = true;
				for (int i=0;i<after.size();i++) {
					if (!before.contains(after.get(i))) {
						fine = false;
					}
				}
				if (!fine) {
					selected.setRotation(oldRot);
				} else {
					level.clearResting(selected);
					originalRotation = selected.getRotation();
				}
			} else {
				float offsetx = ((newx - oldx) * Level.SCALE);
				float offsety = ((newy - oldy) * Level.SCALE);
				
				float x = selected.getX();
				float y = selected.getY();
				float nx = x + offsetx;
				float ny = y + offsety;
				
				if ((nx < 0) || (nx > 1024 * Level.SCALE)) {
					return;
				}
				if ((ny < 0) || (ny > 720 * Level.SCALE)) {
					return;
				}

				ArrayList before = level.getCollisions(selected);
				selected.getBody().move(nx, ny);
				ArrayList after = level.getCollisions(selected);
				
				boolean fine = true;
				for (int i=0;i<after.size();i++) {
					if (!before.contains(after.get(i))) {
						fine = false;
						break;
					}
				}
				if (!fine) {
					selected.setPosition(x, y);
					selected.makeStill();
				} else {
					level.clearResting(selected);
					selected.makeStill();
					selected.getBody().adjustVelocity(new Vector2f(offsetx, Math.max(-20, offsety)));
				}
			}
		} else {
			over = level.getElementAt(newx, newy);
		}
	}

	public void mousePressed(int button, int x, int y) {
		if (button == 0) {
			selected = level.getElementAt(x, y);
			if (selected != null) {
				xoffset = x - selected.getX();
				yoffset = y - selected.getY();
				originalRotation = selected.getRotation();
				mouseMoveIgnoreCount = 5;
				container.setMouseGrabbed(true);
			}
		}
		if (button != 0) {
			rmb = true;
		}
	}

	public void mouseReleased(int button, int x, int y) {
		if (!container.getInput().isMouseButtonDown(0) && 
			!container.getInput().isMouseButtonDown(1) && 
			!container.getInput().isMouseButtonDown(2)) {
			container.setMouseGrabbed(false);
			if (selected != null) {
				Mouse.setCursorPosition((int) selected.getX(), (int) (container.getHeight()-selected.getY()));
				mouseMoveIgnoreCount+=5;
				over = null;
				selected = null;
				lastCheck = 1000;
			}
			rmb = false;
		}
		if (button != 0) {
			rmb = false;
		}
	}

	public void keyPressed(int key, char c) {
		if (key == Input.KEY_F10) {
			editMode = !editMode;
			if (editMode) {
				levelData = new LevelData();
				addMessage("Entered Edit Mode");
			}
		}
		if (key == Input.KEY_F11) {
			RESTING_BODDIES = !RESTING_BODDIES;
		}
		if (key == Input.KEY_F8) {
			SHOW_MATCHES = !SHOW_MATCHES;
		}
		if (key == Input.KEY_F7) {
			container.setShowFPS(!container.isShowingFPS());
		}
		
		if (editMode) {
			if (key == Input.KEY_I) {
				levelData.setInitialState(level);
				addMessage("Inital loadedState saved");
			}
			if (key == Input.KEY_K) {
				if (over != null) {
					levelData.setKeyElement(over);
					addMessage("Key block selected");
				} else {
					addMessage("Hightlight a block to use as key to pattern");
				}
			}
			if (key == Input.KEY_T) {
				levelData.setTargetState(level);
				addMessage("Target loadedState saved");
			}
			if (key == Input.KEY_S) {
				try {
					levelData.save(new FileOutputStream("res/levels/save"+System.currentTimeMillis()+".xml"));
					addMessage("Level data saved");
				} catch (Throwable e) {
					addMessage("Failed to save: "+e.getMessage());
				}
			}
			if (key == Input.KEY_C) {
				level.clear();
				addFloor();
				addMessage("World cleared");
			}
			if (key == Input.KEY_D) {
				if (over != null) {
					level.remove(over);
					level.clearResting(over);
					over = null;
					selected = null;
				} else {
					addMessage("Hightlight a block to delete");
				}
			}
			if (key == Input.KEY_1) {
				if (selected == null) {
					over = selected = new DynamicBlockElement(container.getInput().getMouseX(), 
													   container.getInput().getMouseY(), 
													   50, 50, blockTexture, Color.yellow);
					level.add(selected);
					xoffset = 0;
					yoffset = 0;
					originalRotation = 0;
					container.setMouseGrabbed(true);
				} else {
					addMessage("Drop the current piece first");
				}
			}
			if (key == Input.KEY_2) {
				if (selected == null) {
					over = selected = new DynamicBlockElement(container.getInput().getMouseX(), 
							  						   container.getInput().getMouseY(), 
							  						   250, 20, blockTexture, Color.cyan);
					level.add(selected);
					xoffset = 0;
					yoffset = 0;
					originalRotation = 0;
					container.setMouseGrabbed(true);
				} else {
					addMessage("Drop the current piece first");
				}
			}
			if (key == Input.KEY_3) {
				if (selected == null) {
					over = selected = new DynamicBlockElement(container.getInput().getMouseX(), 
							  						   container.getInput().getMouseY(), 
							  						   100, 30, blockTexture, Color.blue);
					level.add(selected);
					xoffset = 0;
					yoffset = 0;
					originalRotation = 0;
					container.setMouseGrabbed(true);
				} else {
					addMessage("Drop the current piece first");
				}
			}
			if (key == Input.KEY_4) {
				if (selected == null) {
					over = selected = new DynamicBlockElement(container.getInput().getMouseX(), 
							  						   container.getInput().getMouseY(), 
							  						   70, 40, blockTexture, Color.green);
					level.add(selected);
					xoffset = 0;
					yoffset = 0;
					originalRotation = 0;
					container.setMouseGrabbed(true);
				} else {
					addMessage("Drop the current piece first");
				}
			}
			if (key == Input.KEY_5) {
				if (selected == null) {
					over = selected = new DynamicWedgeElement(container.getInput().getMouseX(), 
							  						   container.getInput().getMouseY(), 
							  						   70, 40, blockTexture, Color.red);
					level.add(selected);
					xoffset = 0;
					yoffset = 0;
					originalRotation = 0;
					container.setMouseGrabbed(true);
				} else {
					addMessage("Drop the current piece first");
				}
			}
			if (key == Input.KEY_6) {
				if (selected == null) {
					over = selected = new DynamicArcElement(container.getInput().getMouseX(), 
							  						 container.getInput().getMouseY(), 
							  						 100, 50, 10, blockTexture, Color.magenta);
					level.add(selected);
					xoffset = 0;
					yoffset = 0;
					originalRotation = 0;
					container.setMouseGrabbed(true);
				} else {
					addMessage("Drop the current piece first");
				}
			}
		}

		if (key == Input.KEY_M) {
			container.setSoundOn(!container.isSoundOn());
			container.setMusicOn(!container.isMusicOn());
		}
		if (key == Input.KEY_R) {
			try {
				reset();
			} catch (SlickException e) {
				Log.error(e);
			}
		}
		if (key == Input.KEY_F) {
			try {
				boolean isFullscreen = ((AppGameContainer) container).isFullscreen();
				((AppGameContainer) container).setFullscreen(!isFullscreen);
			} catch (SlickException e) {
				Log.error(e);
			}
		}
		if (key == Input.KEY_ESCAPE) {
			if ((this.nextLevel == 0) || (this.nextLevel == 100)) {
				game.enterState(ModeSelectState.ID, new FadeOutTransition(Color.white), new FadeInTransition(Color.white));	
			} else {
				game.enterState(LevelSelectState.ID, new FadeOutTransition(Color.white), new FadeInTransition(Color.white));	
			}
		}
		if (key == Input.KEY_SPACE) {
			showTarget = !showTarget;
		}
		if (key == Input.KEY_0) {
			check();
		}
	}

	public void check() {
		if (selected != null) {
			return;
		}
		
		float bestMatch = 0;
		level.clearMatchingFlags();
		
		LevelElement element = loadedState.getKeyElement();
		ArrayList matches = level.findMatches(element);
		for (int i=0;i<matches.size();i++) {
			LevelElement local = (LevelElement) matches.get(i);
			
			float dx = local.getX() - element.getX();
			float dy = local.getY() - element.getY();
			Level target = loadedState.getTargetState();
			target.translateAll(dx, dy);
			
			float matchLevel = level.matches(target);
			if (matchLevel > bestMatch) {
				bestMatch = matchLevel;
			}
			target.translateAll(-dx, -dy);
		}
		
		currentMatch = bestMatch;
		lastCheck = 500;
	}
	
	/**
	 * @see rakatan.data.LevelListener#significantCollision()
	 */
	public void significantCollision(Body a, Body b) {
		lastCheck = 0;
		
		if (selected != null) {
			if (selected.getBody() == a) {
				return;
			}
			if (selected.getBody() == b) {
				return;
			}
		}
		if (lastPlay < 0) {
			sound.play((float) (0.7f+(Math.random() * 0.3f)),
					   (float) (0.5f+(Math.random() * 0.5f)));
			lastPlay = 10;
		}
	}

	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		super.enter(container, game);
	}
}
