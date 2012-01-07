package playground;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

/**
 * A pod is a section of the the GUI that can slide around and display images and text
 *
 * @author kevin
 */
public class Pod {
	/** The area occupied by this rectangle */
	protected Rectangle rect;
	/** The image being displayed for this POD */
	protected Image image;
	/** The x coordinate of the position of the POD */
	protected float x;
	/** The y cooridnate of the position of the POD */
	protected float y;
	/** The font used to render the label on the POD */
	protected Font font;
	/** The label to display on the POD */
	protected String label;
	/** The listener to report updates to */
	protected PodListener listener;
	/** The list of images to layer on top of the POD */
	protected ArrayList images = new ArrayList();
	/** The arbitrary user data assigned to this POD - eeek, untyped */
	protected Object data;
	/** True if this POD is rendered and updated */
	protected boolean enabled = true;
	/** True if the mouse button has been released since last press on this POD */
	protected boolean hasBeenReleased = true;
	/** True if the mouse is over this POD */
	protected boolean over;
	/** The group that this POD belongs to */
	private PodGroup group = new PodGroup();
	/** The colour block to show on the pod */
	private Color color;
	/** True if the POD is active */
	private boolean active = true;
	/** True if the pod is currently cursor selected */
	private boolean selected = false;
	/** True if the cursor focus is being used */
	private boolean cursorFocus;
	
	/**
	 * Default constructor for subclasses
	 */
	protected Pod() {
	}

	/**
	 * Create a new POD 
	 * 
	 * @param listener The listener to report updates 
	 * @param image The image to display in the background of the POD
	 * @param font The font to us for the label
	 * @param x The x coordinate of the position of the POD
	 * @param y The y coordinate of the position of the POD
	 * @param label The label to display on the POD
	 */
	public Pod(PodListener listener, Image image, Font font, int x, int y, String label) {
		this(listener, image, font, x,y, image.getWidth(),image.getHeight(),label);
	}

	/**
	 * Create a new POD 
	 * 
	 * @param listener The listener to report updates 
	 * @param img The image to display in the background of the POD
	 * @param font The font to us for the label
	 * @param x The x coordinate of the position of the POD
	 * @param y The y coordinate of the position of the POD
	 * @param width The width of the POD
	 * @param height The height of the POD
	 * @param label The label to display on the POD
	 */
	public Pod(PodListener listener, Image img, Font font, int x, int y, int width, int height, String label) {
		this.font = font;
		this.x = x;
		this.y = y;
		this.label = label;
		this.listener = listener;
		
		this.image = img;

		rect = new Rectangle(x,y,width,height);
	}

	/**
	 * True if the pod is currently active
	 * 
	 * @return True if the POD is currently active
	 */
	public boolean isActive() {
		return active && enabled;
	}
	
	/**
	 * Indicate if cursor focus should be being used
	 * 
	 * @param cursorFocus True if the cursor focus should be used
	 */
	public void setCursorFocus(boolean cursorFocus) {
		this.cursorFocus = cursorFocus;
	}
	
	/**
	 * True if this POD is currently cursor selected
	 * 
	 * @param selected True if this POD is cursor selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	/**
	 * Set the color of the block to show
	 * 
	 * @param color The color of the block to show
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	/**
	 * Set the group this POD belongs to
	 * 
	 * @param group The group this POD belongs to 
	 */
	public void setGroup(PodGroup group) {
		this.group = group;
	}
	
	/**
	 * Indicate if this POD should be enabled. A disabled POD
	 * is neither rendered or updated.
	 * 
	 * @param enabled True if this POD should be enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * Check if this POD is enabled
	 * 
	 * @return True if this POD is enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Set the arbitrary user data given for this pod - eeek this is untyped data!
	 * 
	 * @param data The data to assign to this POD
	 */
	public void setUserData(Object data) {
		this.data = data;
	}
	
	/**
	 * Get the arbitrary data thats been given for this POD
	 * 
	 * @return The untyped data assigned to this POD
	 */
	public Object getUserData() {
		return data;
	}
	
	/**
	 * Set the x coordiante of the position of this POD
	 * 
	 * @param x The new x coordinate
	 */
	public void setX(int x) {
		rect.setX(x);
		this.x = x;
	}

	/**
	 * Set the y coordiante of the position of this POD
	 * 
	 * @param y The new y coordinate
	 */
	public void setY(int y) {
		rect.setY(y);
		this.y = y;
	}
	
	/**
	 * Indicate if this POD should be active
	 * 
	 * @param active True if this POD should be active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
	/**
	 * Draw the POD to the graphics context provided
	 * 
	 * @param container The container housing the graphics
	 * @param g The graphics context to which the POD should be drawn
	 */
	public void draw(GameContainer container, Graphics g) {
		if (!enabled) {
			return;
		}
		
		int offset = 0;
		Color col = new Color(0,0,0,0.4f);
		if (image == null) {
			col = new Color(0,0,0,0.3f);
		}
		if (!active) {
			col = new Color(0,0,0,0.1f);
		}
		if (!group.moving()) {
			if (over) {
				offset = -2;
				col = new Color(0,0,0,0.6f);
				if (image == null) {
					col = Color.white;
					offset += 2;
				}
			}
		}

		if (image != null) {
			image.draw((int) x,(int) y+offset, (int) rect.getWidth(), (int) rect.getHeight());
		}
		if (color != null) {
			g.setColor(color);
			g.fillRect((int) x+5,(int) y+offset+5, (int) rect.getWidth()-10, (int) rect.getHeight()-10);
		}
		
		for (int i=0;i<images.size();i++) {
			Image image = (Image) images.get(i);
			int xp = (int) ((rect.getWidth() - image.getWidth()) / 2);
			int yp = (int) ((rect.getHeight() - image.getHeight()) / 2);
			
			image.draw((int) (x+xp),(int) (y+yp+offset));
		}
		
		int xo = (int) ((rect.getWidth() - font.getWidth(label)) / 2);
		int yo = (int) ((rect.getHeight() - font.getHeight(label)) / 2);
		
		font.drawString((int) (x+xo), (int) (y+yo-2)+offset, label, col);
	}
	
	/**
	 * Add an image to the pod
	 * 
	 * @param image The image to add
	 */
	public void addImage(Image image) {
		addImage(image, false);
	}
	
	/**
	 * Add an image to the pod
	 * 
	 * @param image The image to add
	 * @param scale True if we should scale the image to fit
	 */
	public void addImage(Image image, boolean scale) {
		if (scale) {
			if (image != null) {
				int maxWidth = 170;
				if (image.getWidth() > maxWidth) {
					float rat = maxWidth / (float) image.getWidth();
					image = image.getScaledCopy((int) (image.getWidth()*rat),(int) (image.getHeight()*rat));
				}
				int maxHeight = 130;
				if (image.getHeight() > maxHeight) {
					float rat = maxHeight / (float) image.getHeight();
					image = image.getScaledCopy((int) (image.getWidth()*rat),(int) (image.getHeight()*rat));
				}
			}
		}
		
		images.add(image);
	}
	
	/**
	 * Update the POD's state
	 * 
	 * @param container The container holding the game using this POD
	 * @param delta The amount of time passed since last update
	 * @param xoffset The amount the POD has been offset by its group 
	 * @param yoffset The amount the POD has been offset by its group
	 */
	public void update(GameContainer container, int delta, float xoffset, float yoffset) {
		if (!enabled) {
			hasBeenReleased = true;
			return;
		}
		
		over = false;
		if ((!group.moving()) && (hasBeenReleased) && (active)) {
			if ((selected) || ((rect.contains(container.getInput().getMouseX()-xoffset, container.getInput().getMouseY()-yoffset)) && !cursorFocus)) {
				if (container.getInput().isMouseButtonDown(0)) {
					listener.podSelected(this, label);
					hasBeenReleased = false;
				}
				over = true;
			} 
		}
		if (!container.getInput().isMouseButtonDown(0)) {
			hasBeenReleased = true;
		}
	}
	
	/**
	 * Fire an event indicating that this POD has stopped moving
	 */
	public void fireMoveComplete() {
		listener.podMoveCompleted(this);
	}

	/**
	 * Fire an event indicating that this POD has been selected
	 */
	public void firePodSelected() {
		if (active) {
			listener.podSelected(this, label);
		}
	}
	
	/**
	 * Get the x position of this POD
	 * 
	 * @return The x coordinate of the position of this POD
	 */
	public int getX() {
		return (int) x;
	}

	/**
	 * Get the y position of this POD
	 * 
	 * @return The y coordinate of the position of this POD
	 */
	public int getY() {
		return (int) y;
	}
	
	/**
	 * Set the label to be displayed in text on this POD
	 * 
	 * @param label The label to be displayed on the POD
	 */
	public void setLabel(String label) {
		this.label = label;
	}
}
