package org.newdawn.slick.thingle.demos;

import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Widget;

/**
 * Login test logic
 * 
 * @author kevin
 */
public class Login {
	/** The dialog */
	public Object dialog;
	/** The thinlet instance being controlled */
	private Page page;
	
	/**
	 * Initialise 
	 * 
	 * @param page The page displaying the GUI
	 */ 
	public void init(Page page) {
		this.page = page;
	}
	
	/**
	 * Login notification
	 * 
	 * @param dialog The dialog being shown
	 * @param username The username given
	 * @param password The password given
	 */
	public void login(Widget dialog, String username, String password) {
		this.dialog = dialog;
		
		page.remove(dialog);
		System.out.println("Attempting login with the following criteria: "+username+","+password);
	}
}
