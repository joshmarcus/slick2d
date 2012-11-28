package playground;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * A single state the playground application can be in. Each state
 * represents a set of options for the user.
 *
 * @author kevin
 */
public abstract class State {
	/** The list of POD groups building up the state that will be rendered and updated */
	protected ArrayList podGroups = new ArrayList();
	
	/**
	 * Notification that a reinitialisation has occured - normally after a 
	 * game has been launched
	 */
	public abstract void reinit();
	
	/**
	 * Update the state based on a given amount of time passing
	 * 
	 * @param container The container holding the application
	 * @param delta The amount of time that passed since last update
	 */
	public void update(GameContainer container, int delta) {
		for (int i=0;i<podGroups.size();i++) {
			((PodGroup) podGroups.get(i)).update(container, delta);
		}
	}
	
	/**
	 * Render the state and all the PODs contained
	 * 
	 * @param container The container holding the application
	 * @param g The graphics context to render to
	 */
	public void render(GameContainer container, Graphics g) {
		for (int i=0;i<podGroups.size();i++) {
			((PodGroup) podGroups.get(i)).draw(container, g);
		}
	}
	
	/**
	 * Notification that we've entered this state
	 * 
	 * @param lastState The ID of the state we came from
	 * @param app The application entering this state
	 */
	public abstract void enter(int lastState, Playground app);
	
	/**
	 * Notification that we've left this state
	 * 
	 * @param app The application leaving this state
	 */
	public abstract void leave(Playground app);
	
	/**
	 * Get the label of the left/previous alt-menu POD
	 * 
	 * @return The label required in this state
	 */
	public abstract String getPrevLabel();

	/**
	 * Get the label of the right/next alt-menu POD
	 * 
	 * @return The label required in this state
	 */
	public abstract String getNextLabel();

	/**
	 * Get the label of the middle/back alt-menu POD
	 * 
	 * @return The label required in this state
	 */
	public abstract String getBackLabel();
	
	/**
	 * Notification that the left/previous POD was selected
	 * while in this state
	 */
	public abstract void prevSelected();

	/**
	 * Notification that the next/right POD was selected
	 * while in this state
	 */
	public abstract void nextSelected();

	/**
	 * Notification that the middle/back POD was selected
	 * while in this state
	 */
	public abstract void backSelected();
	
	/**
	 * Give the focus to the control in this state
	 * 
	 * @param app The application given focus
	 */
	public abstract void giveFocus(Playground app);
}
