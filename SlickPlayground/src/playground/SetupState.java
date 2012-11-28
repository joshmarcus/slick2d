package playground;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import playground.games.GameRecord;

/**
 * A state to allow configuration of the client
 *
 * @author kevin
 */
public class SetupState extends State implements PodListener {
	/** The unique identifier for this state */
	public static final int ID = 5;
	
	/** The application holding this state */
	private Playground playground;
	/** The group holding all the pods */
	private PodGroup group;
	/** True if this state is on screen */
	private boolean on;
	/** The next state we should transition to */
	private int nextState;
	
	/** The record currently being displaye */
	private GameRecord current;
	/** The last data cache location specified */
	private String lastDataCache;
	/** The last launch cache location specified */
	private String lastLaunchCache;
	
	/**
	 * Create a new state
	 * 
	 * @param app The application holding this state
	 */
	public SetupState(Playground app) {
		playground = app;
		
		group = new PodGroup();
		reinit();
		on = true;
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
		if (pod.getUserData() instanceof Color) {
			Color col = (Color) pod.getUserData();
			
			playground.setTheme(col);
		}
		if (name.equals("Toggle Fullscreen")) {
			playground.toggleFullscreen();
		}
	}

	/**
	 * @see playground.State#backSelected()
	 */
	public void backSelected() {
		nextState = MainMenuState.ID;
		group.move(0, -800);
	}

	/**
	 * @see playground.State#enter(int, playground.Playground)
	 */
	public void enter(int lastState, Playground app) {
		nextState = -1;
		group.setPosition(0, -800);
		group.move(0, 800);
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
		
		Color[] cols = new Color[] {Color.blue, Color.green, Color.red, Color.yellow, new Color(1,0,1,1f), new Color(0,1,1,1f)};
		
		TextPod label = new TextPod(this, Resources.font3, "Theme", 120);
		group.add(label);
		label = new TextPod(this, Resources.font3, "Java Version", 245);
		group.add(label);
		label = new TextPod(this, Resources.font3, System.getProperty("java.home"), 280);
		group.add(label);
		label = new TextPod(this, Resources.font3, "OpenGL Adapter", 345);
		group.add(label);
		label = new TextPod(this, Resources.font3, GL11.glGetString(GL11.GL_VENDOR) + " " + 
												   GL11.glGetString(GL11.GL_RENDERER), 380);
		group.add(label);
		label = new TextPod(this, Resources.font3, "OpenGL " + GL11.glGetString(GL11.GL_VERSION), 410);
		group.add(label);
		
		Pod pod = new Pod(this, Resources.smallPodImage, Resources.font3, 305, 470, 190, 35, "Toggle Fullscreen");
		group.add(pod);
		
		for (int i=0;i<cols.length;i++) {
			pod = new Pod(this, Resources.smallPodImage, Resources.font3, 220+(i*60), 170, 50, 25, "");
			pod.setColor(cols[i]);
			pod.setUserData(cols[i]);
			
			group.add(pod);
		}
		
		podGroups.add(group);
	}

	/**
	 * @see playground.State#giveFocus(playground.Playground)
	 */
	public void giveFocus(Playground app) {
		app.setCurrentFocus(group);
	}
}
