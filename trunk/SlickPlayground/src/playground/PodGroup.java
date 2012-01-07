package playground;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * A collection of PODs that can be slid around the screen.
 *
 * @author kevin
 */
public class PodGroup {
	/** The list of PODs to be displayed */
	private ArrayList pods = new ArrayList();
	/** The x component of the velocity of the group */
	private double vx;
	/** The y component of the velocity of the group */
	private double vy;
	/** The remaining movement on the X axis */
	private double dx;
	/** The remaining movement on the Y axis */
	private double dy;
	/** The current x offset from base */
	private double x;
	/** The current y offset from base */
	private double y;
	/** True if the group is using cursor focus */
	private boolean cursorFocus;
	/** The current POD cursor selected */
	private Pod selected;
	/** The selected index */
	private int index;
	/** True if we're using vertical scan lines */
	private boolean vertScan;
	/** The up down step */
	private int upDownStep = 3;
	
	/**
	 * Create a new empty group
	 */
	public PodGroup() {
		
	}
	
	/**
	 * Set the amount ot move when cursor up/down
	 * 
	 * @param upDownStep The amount to move when cursor up/down
	 */
	public void setUpDownStep(int upDownStep) {
		this.upDownStep = upDownStep;
	}
	
	/**
	 * Indicate whether this group is current controlled by cursor
	 * 
	 * @param cursorFocus True if the group should be configured by a cursor
	 */
	public void setCursorFocus(boolean cursorFocus) {
		if (this.cursorFocus == cursorFocus) {
			return;
		}
		
		this.cursorFocus = cursorFocus;
		if (cursorFocus) {
			selected = (Pod) pods.get(0);
			selected.setSelected(true);
			index = -1;
		} else {
			if (selected != null) {
				selected.setSelected(false);
				selected = null;
			}
		}
		
		for (int i=0;i<pods.size();i++) {
			((Pod) pods.get(i)).setCursorFocus(cursorFocus);
		}
	}
	
	/**
	 * Arrange the pods in this group on the screen
	 */
	public void arrange() {
		arrange(50,50,255,180);
	}
	
	/**
	 * Arrange the pods in this group on the screen
	 * 
	 * @param xoffset The base x coordinate offset
	 * @param yoffset The base y cooridnate offset
	 * @param width The width that should be given for each POD
	 * @param height The height that should be given for each POD
	 */
	public void arrange(int xoffset, int yoffset, int width, int height) {
		vertScan = false;
		if (pods.size() <= 9) {
			for (int y=0;y<3;y++) {
				for (int x=0;x<3;x++) {
					int index = (y*3)+x;
					if (index < pods.size()) {
						Pod pod = (Pod) pods.get(index);
						pod.setX(xoffset+(x*width));
						pod.setY(yoffset+(y*height));
					} 
				}
			}
		} else {
			vertScan = true;
			for (int x=0;x<((pods.size()-1)/3)+1;x++) {
				for (int y=0;y<3;y++) {
					int index = (x*3)+y;
					if (index < pods.size()) {
						Pod pod = (Pod) pods.get(index);
						pod.setX(xoffset+(x*width));
						pod.setY(yoffset+(y*height));
					}
				}
			}
		}
	}

	/**
	 * Set the position on the screen of this POD group and hence all the PODs
	 * it holds
	 * 
	 * @param x The new x position of the POD group
	 * @param y The new y position of the POD group
	 */
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Add a pod to the group
	 * 
	 * @param pod The pod to add to the group
	 */
	public void add(Pod pod) {
		pod.setGroup(this);
		pods.add(pod);
	}
	
	/**
	 * Check if this group contains the specified POD
	 * 
	 * @param pod The POD to check for
	 * @return True if the POD is in the group
	 */
	public boolean contains(Pod pod) {
		return pods.contains(pod);
	}
	
	/**
	 * Update the group, moving and updating the PODs inside
	 * 
	 * @param container The container holding the display
	 * @param d The amount of time thats passed since last update (milliseconds)
	 */
	public void update(GameContainer container, int d) {
		double mx = 0;
		double my = 0;
		
		if ((dx != 0) || (dy != 0)) {
			double delta = (d * 0.2);
			if (dx < 0) {
				vx -= delta;
			}
			if (dx > 0) {
				vx += delta;
			}
			if (dx == 0) {
				vx = 0;
			}
			if (dy < 0) {
				vy -= delta;
			}
			if (dy > 0) {
				vy += delta;
			}
			if (dy == 0) {
				vy = 0;
			}
			
			mx = vx * delta * 0.1;
			my = vy * delta * 0.1;
			
			if (Math.abs(mx) > Math.abs(dx)) {
				mx = dx;
				vx = 0;
			}
			if (Math.abs(my) > Math.abs(dy)) {
				my = dy;
				vy = 0;
			}

			dx -= mx;
			dy -= my;
			if ((dx == 0) && (dy == 0)) {
				for (int i=0;i<pods.size();i++) {
					((Pod) pods.get(i)).fireMoveComplete();
				}
			}
		} 
		
		x += mx;
		y += my;
		for (int i=0;i<pods.size();i++) {
			((Pod) pods.get(i)).update(container, d, (float) x, (float) y);
		}
	}

	/**
	 * The width in PODs of the group's display
	 * 
	 * @return The width in PODs of the groups display
	 */
	public int width() {
		return pods.size() / 3;
	}
	
	/**
	 * Check if the POD group is moving 
	 * 
	 * @return True if the POD group is moving 
	 */
	public boolean moving() {
		return ((dx != 0) || (dy != 0));
	}
	
	/**
	 * Clear the group, remove all PODs
	 */
	public void clear() {
		pods.clear();
	}
	
	/**
	 * Move the group a given amount in the x and y axis. Note that it won't
	 * move instantly, rather it will slide.
	 * 
	 * @param dx The ammount to move on the x-axis (pixels)
	 * @param dy The ammount to move on teh y-axis (pixels)
	 */
	public void move(double dx, double dy) {
		if (moving()) {
			return;
		}
		this.dx = dx;
		this.dy = dy;
		vx = 0;
		vy = 0;
	}
	
	/**
	 * Draw this group and hence all the enabled PODs inside
	 * 
	 * @param container The container holding the graphics context 
	 * @param g The graphics context on which to draw the POD
	 */
	public void draw(GameContainer container, Graphics g) {
		g.translate((float) x,(float) y);
		for (int i=0;i<pods.size();i++) {
			((Pod) pods.get(i)).draw(container, g);
		}
		g.translate((float) -x,(float) -y);
	}
	
	/**
	 * Move the cursor left
	 * 
	 * @param app The application instructing this POD
	 */
	public void left(Playground app) {
		if (vertScan) {
			index = index - 3;
		} else {
			index -= 1;
		}
		
		if (index < 0) {
			app.giveFocus();
			return;
		}
		
		index = Math.max(index, 0);
		index = Math.min(index, pods.size()-1);
		
		if (selected != null) {
			selected.setSelected(false);
		}
		selected = (Pod) pods.get(index);
		selected.setSelected(true);
		
		if (!selected.isActive()) {
			left(app);
		}
	}

	/**
	 * Move the cursor right
	 * 
	 * @param app The application instructing this POD
	 */
	public void right(Playground app) {
		if (vertScan) {
			index = index + 3;
		} else {
			index += 1;
		}

		if (index >= pods.size()) {
			app.giveFocus();
			return;
		}
		
		index = Math.max(index, 0);
		index = Math.min(index, pods.size()-1);
		
		if (selected != null) {
			selected.setSelected(false);
		}
		selected = (Pod) pods.get(index);
		selected.setSelected(true);
		
		if (!selected.isActive()) {
			right(app);
		}
	}

	/**
	 * Move the cursor up
	 * 
	 * @param app The application instructing this POD
	 */
	public void up(Playground app) {
		if (!vertScan) {
			index = index - upDownStep;
		} else {
			index -= 1;
		}

		if (index < 0) {
			app.giveFocus();
			return;
		}
		
		index = Math.max(index, 0);
		index = Math.min(index, pods.size()-1);
		
		if (selected != null) {
			selected.setSelected(false);
		}
		selected = (Pod) pods.get(index);
		selected.setSelected(true);
		
		if (!selected.isActive()) {
			up(app);
		}
	}

	/**
	 * Move the cursor down
	 * 
	 * @param app The application instructing this POD
	 */
	public void down(Playground app) {
		if (!vertScan) {
			index = index + upDownStep;
		} else {
			index += 1;
		}
		
		if (index >= pods.size()) {
			app.giveFocus();
			return;
		}
		
		index = Math.max(index, 0);
		index = Math.min(index, pods.size()-1);
		
		if (selected != null) {
			selected.setSelected(false);
		}
		selected = (Pod) pods.get(index);
		selected.setSelected(true);
		
		if (!selected.isActive()) {
			down(app);
		}
	}

	/**
	 * Select the POD currently on the cursor
	 * 
	 * @param app The application instructing this POD
	 */
	public void select(Playground app) {
		if (selected != null) {
			selected.firePodSelected();
		}
	}
}
