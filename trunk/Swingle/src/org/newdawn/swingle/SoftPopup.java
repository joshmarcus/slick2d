package org.newdawn.swingle;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.Popup;

/**
 * A popup implemenation that uses a software mode (Swing) panel to 
 * display the popup contents. On OSX popups are always heavyweight 
 * and this doesn't work when rendering to a image. 
 * 
 * @author kevin
 */
public class SoftPopup extends Popup {
	/** The frame that is repsonsible for this popup */
	private final HeadlessFrame frame;
	/** The panel on which the contents is shown */
	private JComponent panel = new JPanel();
	
	/**
	 * Create a new popup
	 * 
	 * @param frame The frame owning the popup
	 * @param component The component to be display 
	 * @param x The x coordinate at which to display the popup
	 * @param y The y coordinate at which to display the popup
	 */
	public SoftPopup(HeadlessFrame frame, Component component, int x, int y) {
		this.frame = frame;
		Dimension size = component.getPreferredSize();
		panel.setBounds(x,y,size.width,size.height);
		panel.setLayout(null);
		component.setBounds(0,0,size.width,size.height);
		panel.add(component);
		
		frame.getContentPane().add(panel);
		if (frame.getContentPane() instanceof JDesktopPane) {
			((JDesktopPane) frame.getContentPane()).moveToFront(panel);
		}
		component.invalidate();
		panel.validate();
	}

	/**
	 * @see javax.swing.Popup#hide()
	 */
	public void hide() {
		panel.setVisible(false);
	}

	/**
	 * @see javax.swing.Popup#show()
	 */
	public void show() {
		panel.setVisible(true);
	}
}