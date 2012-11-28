package playground;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.util.FastTrig;
import org.newdawn.slick.util.Log;

import playground.games.GameList;
import playground.games.GameRecord;
import playground.games.GameStore;

/**
 * The state to display the main menu
 *
 * @author kevin
 */
public class MainMenuState extends State implements PodListener {
	/** The unique identifier for this state */
	public static final int ID = 1;
		
	/** The application holding this tate */
	private Playground playground;
	/** The group holding the pods */
	private PodGroup group;
	/** True if this state is on the screen */
	private boolean on;
	/** The next state we should transition to */
	private int nextState;
	/** The store of games to be accessed */
	private GameStore store;
	/** True if we're currently updating the games data */
	private boolean updating;
	/** The angle of the bounce */
	private float ang;
	/** The format to use when display the date */
	private SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
	/** True if we need to update image cache */
	private boolean updateImages;
	
	/**
	 * Create a new state
	 * 
	 * @param app The application holding this stae
	 * @param store The store of games to be presented
	 * @param reinit True if this is a reinitialisation
	 */
	public MainMenuState(Playground app, GameStore store, boolean reinit) {
		playground = app;
	
		group = new PodGroup();
		this.store = store;
		reinit();
		on = !reinit;
	}

	/**
	 * @see playground.State#getBackLabel()
	 */
	public String getBackLabel() {
		return null;
	}

	/**
	 * @see playground.State#getNextLabel()
	 */
	public String getNextLabel() {
		return null;
	}

	/**
	 * @see playground.State#getPrevLabel()
	 */
	public String getPrevLabel() {
		return null;
	}

	/**
	 * Get a list of fave games
	 * 
	 * @return The list of games marked as fave
	 */
	private GameList getFaves() {
		GameList list = store.getGames();
		GList faves = new GList();
		ArrayList ids = LocalSettings.getFaves();
		
		for (int i=0;i<list.size();i++) {
			GameRecord record = list.getGame(i);
			if (ids.contains(record.getID())) {
				faves.addGame(record);
			}
		}
		
		return faves;
	}
	
	/**
	 * @see playground.PodListener#podMoveCompleted(playground.Pod)
	 */
	public void podMoveCompleted(Pod pod) {
		playground.enterState(nextState);
	}

	/**
	 * @see playground.PodListener#podSelected(playground.Pod, java.lang.String)
	 */
	public void podSelected(Pod pod, String name) {
		if (name.equals("All Games")) {
			nextState = GamesListState.ID;
			playground.setGamesList(store.getGames());
			group.move(-800, 0);
		}
		if (name.equals("Categories")) {
			nextState = CategoriesState.ID;
			group.move(-800, 0);
		}
		if (name.equals("Favourites")) {
			nextState = GamesListState.ID;
			playground.setGamesList(getFaves());
			group.move(-800, 0);
		}
		if (name.equals("Setup")) {
			nextState = SetupState.ID;
			group.move(-800, 0);
		}
		if (name.equals("Update")) {
			updating = true;
			Thread t = new Thread() {
				public void run() {
					try {
						try {
							store.update();
						} catch (IOException e) {
							Log.error(e);
						}
					} catch (Throwable e) {
						Log.error(e);
					}
					playground.getGamesData().update(store);
					updateImages = true;
				}
			};
			t.start();
		}
		if (name.equals("Quit")) {
			playground.exit();
		}
	}

	/**
	 * @see playground.State#backSelected()
	 */
	public void backSelected() {
		playground.exit();
	}

	/**
	 * @see playground.State#enter(int, playground.Playground)
	 */
	public void enter(int lastState, Playground app) {
		nextState = -1;
		if (!on) {
			group.setPosition(-800, 0);
			group.move(800, 0);
			on = true;
		}
	}

	/**
	 * @see playground.State#leave(playground.Playground)
	 */
	public void leave(Playground app) {
		 on = false;
	}

	/**
	 * @see playground.State#nextSelected()
	 */
	public void nextSelected() {
	}

	/**
	 * @see playground.State#prevSelected()
	 */
	public void prevSelected() {
	}

	/**
	 * @see playground.State#reinit()
	 */
	public void reinit() {
		podGroups.clear();
		group.clear();
		
		try {
			Pod pod = new Pod(this, Resources.podImage, Resources.font, 90,150, "All Games");
			pod.addImage(new Image("res/gamepad.png"));
			group.add(pod);
			pod = new Pod(this, Resources.podImage, Resources.font, 300,150, "Categories");
			pod.addImage(new Image("res/cat.png"));
			group.add(pod);
			pod = new Pod(this, Resources.podImage, Resources.font, 510,150, "Favourites");
			pod.addImage(new Image("res/faves.png"));
			group.add(pod);
			pod = new Pod(this, Resources.podImage, Resources.font, 90,300, "Update");
			pod.addImage(new Image("res/update.png"));
			group.add(pod);
			pod = new Pod(this, Resources.podImage, Resources.font, 300,300, "Setup");
			pod.addImage(new Image("res/help.png"));
			group.add(pod);
			pod = new Pod(this, Resources.podImage, Resources.font, 510,300, "Quit");
			pod.addImage(new Image("res/exit.png"));
			group.add(pod);
			
			podGroups.add(group);
		} catch (SlickException e) {
			Log.error(e);
		}
	}

	/**
	 * @see playground.State#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g) {
		super.render(container, g);

		Date date = new Date(store.lastUpdated());

		Resources.font3.drawString(6, 550, store.getName(),new Color(0,0,0,0.3f));
		Resources.font3.drawString(6, 575, "Last Update: "+format.format(date),new Color(0,0,0,0.3f));
	
		if (updating) {
			TextureImpl.bindNone();
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glColor4f(0,0,0,0.9f);
				GL11.glVertex3f(0, 0, 0);
				GL11.glVertex3f(800, 0, 0);
				GL11.glVertex3f(800, 600, 0);
				GL11.glVertex3f(0, 600, 0);
			GL11.glEnd();
			
			Resources.font.drawString(180,(int) (300+(FastTrig.cos(ang)*10)),"Updating Files.. (this needs work)");
			try { Thread.sleep(50); } catch (Exception e) {};
		}
	}
	
	/**
	 * @see playground.State#update(org.newdawn.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta) {
		if (updateImages) {
			playground.getGamesData().loadImages();
			updating = false;
			updateImages = false;
		}
		ang += delta * 0.01f;
		if (updating) {
			return;
		}
		super.update(container, delta);
	}

	/**
	 * A game list implementation to allow fave filtering
	 *
	 * @author kevin
	 */
	private class GList implements GameList {
		/** The list of games */
		private ArrayList list = new ArrayList();
		
		/**
		 * @see playground.games.GameList#getGame(int)
		 */
		public GameRecord getGame(int index) {
			return (GameRecord) list.get(index);
		}

		/**
		 * Add a game to the list
		 * 
		 * @param record The record describing the game added
		 */
		public void addGame(GameRecord record) {
			list.add(record);
		}
		
		/**
		 * @see playground.games.GameList#size()
		 */
		public int size() {
			return list.size();
		}
		
	}

	/**
	 * @see playground.State#giveFocus(playground.Playground)
	 */
	public void giveFocus(Playground app) {
		app.setCurrentFocus(group);
	}

}
