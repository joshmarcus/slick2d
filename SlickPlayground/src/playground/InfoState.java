package playground;

import java.io.File;
import java.io.IOException;

import org.newdawn.slick.util.Log;

import playground.games.GameRecord;

/**
 * A state in which the information about a game will be displayed and
 * can be manipulated
 *
 * @author kevin
 */
public class InfoState extends State implements PodListener {
	/** The unique identifier for this state */
	public static final int ID = 3;

	/** The pod displaying the game info */
	private InfoPod infoPod;
	/** The pod to "get" the game */
	private Pod getPod;
	/** The pod to check for updates */
	private Pod updatePod;
	/** The pod to play the game */
	private Pod playPod;
	/** The pod to make the game favourite */
	private Pod favePod;
	
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
	public InfoState(Playground app) {
		playground = app;
		
		group = new PodGroup();
		reinit();
		on = true;
	}
	
	/**
	 * Set the information to be displayed in this state
	 * 
	 * @param dataCacheLocation The location to cache the image data to
	 * @param launchCacheLocation The location to cache the launch information to
	 * @param info The information to be displayed
	 */
	public void setInfo(String dataCacheLocation, String launchCacheLocation, GameRecord info) {
		lastDataCache = dataCacheLocation;
		lastLaunchCache = launchCacheLocation;
		
		infoPod.setInfo(info, playground.getGamesData().getLogoImage(info));
		this.current = info;
		
		if (new File(launchCacheLocation+"/"+info.getID()).exists()) {
			playPod.setActive(true);
			getPod.setActive(false);
			updatePod.setActive(true);
		} else {
			playPod.setActive(false);
			getPod.setActive(true);
			updatePod.setActive(false);
		}
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
		if (pod == getPod) {
			playground.doDownload(current, true, false);
		}
		if (pod == playPod) {
			playground.doDownload(current, false, true);
		}
		if (pod == updatePod) {
			playground.doDownload(current, true, false);
		}
		if (pod == favePod) {
			if (LocalSettings.getFaves().contains(current.getID())) {
				LocalSettings.removeFave(current.getID());
			} else {
				LocalSettings.addFave(current.getID());
			}
			
			try {
				LocalSettings.save();
			} catch (IOException e) {
				Log.error(e);
			}
			playground.setInfo(current);
		}
	}

	/**
	 * @see playground.State#backSelected()
	 */
	public void backSelected() {
		nextState = GamesListState.ID;
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
		
		infoPod = new InfoPod(this, Resources.font3);
		getPod = new Pod(this, Resources.smallPodImage, Resources.font3, 640,365,140,30,"Download");
		playPod = new Pod(this, Resources.smallPodImage, Resources.font3, 640,400,140,30,"Play");
		updatePod = new Pod(this, Resources.smallPodImage, Resources.font3, 640,435,140,30,"Update");
		favePod = new Pod(this, Resources.smallPodImage, Resources.font3, 640,470,140,30,"Favourite");
		group.add(infoPod);
		group.add(getPod);
		group.add(playPod);
		group.add(updatePod);
		group.add(favePod);
		
		group.setUpDownStep(1);
		podGroups.add(group);
		
		if (current != null) {
			setInfo(lastDataCache, lastLaunchCache, current);
		}
	}

	/**
	 * @see playground.State#giveFocus(playground.Playground)
	 */
	public void giveFocus(Playground app) {
		app.setCurrentFocus(group);
	}
}
