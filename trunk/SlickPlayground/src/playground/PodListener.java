package playground;

/**
 * A description of any class responding to event notifications from PODs
 *
 * @author kevin
 */
public interface PodListener {
	/**
	 * Notification that the user selected the given POD and action 
	 * should be taken.
	 * 
	 * @param pod The POD that was selected
	 * @param name The name/label on the POD that was selected
	 */
	public void podSelected(Pod pod, String name);
	
	/**
	 * Notification that a POD has stopped moving after having performed
	 * a slide.
	 * 
	 * @param pod The pod that has stopped moving.
	 */
	public void podMoveCompleted(Pod pod);
}
