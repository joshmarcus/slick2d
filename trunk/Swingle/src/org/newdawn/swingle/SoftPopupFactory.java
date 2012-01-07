package org.newdawn.swingle;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.Popup;
import javax.swing.PopupFactory;

/**
 * Popups on OSX are always heavyweight which doesn't work when rendering to a 
 * buffered image. This factory replaces the default one and always produces Swing
 * panel based popups that can be rendered to the image
 * 
 * @author kevin
 */
public class SoftPopupFactory extends PopupFactory {
	/** The frame owning this factory */
	private final HeadlessFrame frame;

	/**
	 * Create a new factory
	 * 
	 * @param frame The frame owning this factory
	 */
	SoftPopupFactory(HeadlessFrame frame) {
		this.frame = frame;
	}

	/**
	 * @see javax.swing.PopupFactory#getPopup(java.awt.Component, java.awt.Component, int, int)
	 */
	public Popup getPopup(Component owner, Component contents, int x, int y) throws IllegalArgumentException {
		JPanel panel = new JPanel();
		
		// popups are given in screen location, so we need to account for the
		// offset of the content pane (as with repaints) and the offset of the window
		// which should mostly be 0 exception on OSX where there is a permenent menu
		// bar
		y -= this.frame.getContentPane().getLocation().y;
		y -= this.frame.getLocation().y;
		
		return new SoftPopup(frame, contents, x, y);
	}
	
}