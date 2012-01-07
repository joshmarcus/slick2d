package playground;

import playground.games.GameStore;

/**
 * A state to show the list of categories available
 *
 * @author kevin
 */
public class CategoriesState extends State implements PodListener {
	/** The identifier given for this state */
	public static final int ID = 4;
		
	/** The app this state is part of */
	private Playground playground;
	/** The group of visual pods */
	private PodGroup group;
	/** The next state we should transition to */
	private int nextState;
	/** The game store containing the games (and categories to be displayed) */
	private GameStore store;
	
	/**
	 * Create a new state to show the categories
	 * 
	 * @param app The application this state is part of
	 * @param store The store to read the categories from
	 */
	public CategoriesState(Playground app, GameStore store) {
		playground = app;
	
		this.store = store;

		group = new PodGroup();
		reinit();
	}

	/**
	 * @see playground.State#getBackLabel()
	 */
	public String getBackLabel() {
		return "Back";
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
	 * @see playground.PodListener#podMoveCompleted(playground.Pod)
	 */
	public void podMoveCompleted(Pod pod) {
		playground.enterState(nextState);
	}

	/**
	 * @see playground.PodListener#podSelected(playground.Pod, java.lang.String)
	 */
	public void podSelected(Pod pod, String name) {
		nextState = GamesListState.ID;
		playground.setGamesList(store.getGames(name));
		group.move(-800, 0);
	}

	/**
	 * @see playground.State#backSelected()
	 */
	public void backSelected() {
		nextState = MainMenuState.ID;
		group.move(800,0);
	}

	/**
	 * @see playground.State#enter(int, playground.Playground)
	 */
	public void enter(int lastState, Playground app) {
		nextState = -1;
		if (lastState == MainMenuState.ID) {
			group.setPosition(800, 0);
			group.move(-800, 0);
		} else {
			group.setPosition(-800, 0);
			group.move(800, 0);
		}
	}

	/**
	 * @see playground.State#leave(playground.Playground)
	 */
	public void leave(Playground app) {
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
		String[] cats = store.getCategories();
		
		for (int i=0;i<cats.length;i++) {
			Pod pod = new Pod(this, Resources.podImage, Resources.font, 510,150, cats[i]);
			group.add(pod);
		}
		
		group.arrange(100,225-(((cats.length-1) / 3)*75),200,150);
		podGroups.add(group);
	}
	
	/**
	 * @see playground.State#giveFocus(playground.Playground)
	 */
	public void giveFocus(Playground app) {
		app.setCurrentFocus(group);
	}
}
