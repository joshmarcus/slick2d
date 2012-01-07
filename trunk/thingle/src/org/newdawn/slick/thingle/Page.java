package org.newdawn.slick.thingle;

import java.io.IOException;
import java.io.StringReader;

import org.newdawn.slick.thingle.internal.Skinlet;
import org.newdawn.slick.thingle.internal.Thinlet;
import org.newdawn.slick.thingle.spi.ThingleException;
import org.newdawn.slick.thingle.spi.ThingleFont;

/**
 * A single UI page. This page provides a Slick oriented wrapper around the 
 * thinlet API.
 * 
 * @author kevin
 * @author Nate
 */
public class Page {
	/** The thinlet instance in use */
	private Skinlet thinlet;
	/** The colour theme applied */
	private Theme theme;
	
	/**
	 * Create new a new UI page
	 * 
	 * @throws ThingleException Indicates a failure to create thinlet
	 */
	public Page() {
		thinlet = new Skinlet();
		thinlet.setKeyFocus(true);
		thinlet.setPage(this);
		theme = new Theme();
		setColors();
	}

	/**
	 * Create a new UI page
	 * 
	 * @param ref A reference to a thinlet xml file to describe the UI
	 * @throws ThingleException Indicates a failure to create thinlet
	 */
	public Page(String ref) throws ThingleException {
		this();
		
		addWidgets(ref);
	}

	/**
	 * Create a new UI page
	 * 
	 * @param ref A reference to a thinlet xml file to describe the UI
	 * @param handler The object to respond to events from the GUI specified in the ref XML
	 * @throws ThingleException Indicates a failure to create thinlet
	 */
	public Page(String ref, Object handler) throws ThingleException {
		this();
		
		addWidgets(ref, handler);
	}
	
	/**
	 * Load a skin from a configuration file assumed to be on the class path
	 * 
	 * @param name The name of the configuration file (without it's extension)
	 * @throws ThingleException Indicates a failure to load the given skin
	 */
	public void loadSkin(String name) throws ThingleException {
		String path = "";
		if (name.indexOf("/") >= 0) {
			path = name.substring(0, name.lastIndexOf("/")+1);
		}
		
		thinlet.loadSkin(path, name);
	}
	
	/**
	 * Indicate if the background desktop should be drawn 
	 * 
	 * @param drawDesktop True if the background should be drawn
	 */
	public void setDrawDesktop(boolean drawDesktop) {
		thinlet.setDrawDesktop(drawDesktop);
	}
	
	/**
	 * Get the thinlet in use
	 * 
	 * @return The thinlet instance in use
	 */
	Thinlet getThinlet() {
		return thinlet;
	}
	
	/**
	 * Set the colour theme to apply
	 * 
	 * @param theme The colour theme to apply
	 */
	public void setTheme(Theme theme) {
		theme.apply(thinlet);
	}
	
	/**
	 * Utility to apply the colour scheme
	 */
	private void setColors() {
		theme.apply(thinlet);
	}

	/**
	 * Sets the default font to use for the page. Note that skins can force 
	 * their fonts to be used.
	 * 
	 * @param font The font to use for the UI
	 */
	public void setFont(ThingleFont font) {
		thinlet.setFont(font);
	}

	/**
	 * Add the components specified in the referenced XML file. The action
	 * events are sent to this page.
	 * 
	 * @param ref The reference to the XML file
  	 * @throws ThingleException Indicates a failure to load the XML
	 */
	public void addWidgets(String ref) throws ThingleException {
		addWidgets(ref, this);
	}

	/**
	 * Add the components specified in the referenced XML file. The action
	 * events are sent to the action handler specified.
	 * 
	 * @param actionHandler The handler to send events to
	 * @param ref The reference to the XML file
  	 * @throws ThingleException Indicates a failure to load the XML
	 */
	public void addWidgets(String ref, Object actionHandler) throws ThingleException {
		thinlet.add(loadComponents(ref, actionHandler));
		layout();
	}
	
	/**
	 * Loads the components specified in the referenced XML file and returns the root. The action
	 * events are sent to this page.
	 * 
	 * @param ref The reference to the XML file
	 * @return The object representing the top level widget
  	 * @throws ThingleException Indicates a failure to load the XML
	 */
	public Widget loadWidgets(String ref) throws ThingleException {
		return loadWidgets(ref, this);
	}

	/**
	 * Loads the components specified in the referenced XML file and returns the root. The action
	 * events are sent to the action handler specified.
	 * 
	 * @param actionHandler The handler to send events to
	 * @param ref The reference to the XML file
	 * @return The object represent the top level widget
  	 * @throws ThingleException Indicates a failure to load the XML
	 */
	public Widget loadWidgets(String ref, Object actionHandler) throws ThingleException {
		return Widget.getWidget(thinlet, loadComponents(ref, actionHandler));
	}
	
	/**
	 * Loads the components specified in the referenced XML file and returns the root. The action
	 * events are sent to the action handler specified.
	 * 
	 * @param actionHandler The handler to send events to
	 * @param ref The reference to the XML file
	 * @return The object represent the top level widget
  	 * @throws ThingleException Indicates a failure to load the XML
	 */
	protected Object loadComponents(String ref, Object actionHandler) throws ThingleException {
		try {
			return thinlet.parse(Thingle.getContext().getResourceAsStream(ref), actionHandler);
		} catch (IOException e) {
			Thingle.getContext().log(e);
			throw new ThingleException("Failed to load: "+ref, e);
		}
	}

	/**
	 * Loads the components in the specified XML returns the root. The action events are sent to
	 * this page.
	 * 
	 * @param xml The XML to parse
	 * @return The top level widget parsed from the string
	 * @throws ThingleException Indicates a failure to load the XML
	 */
	public Widget parse(String xml) throws ThingleException {
		return parse(xml, this);
	}
	
	/**
	 * Loads the components in the specified XML returns the root. The action events are sent to
	 * the action handler specified.
	 * 
	 * @param xml The XML to parse
	 * @param actionHandler The handler to send events to
	 * @return The top level widget parsed from the string
	 * @throws ThingleException Indicates a failure to load the XML
	 */
	public Widget parse(String xml, Object actionHandler) throws ThingleException {
		try {
			return Widget.getWidget(thinlet, thinlet.parse(new StringReader(xml), actionHandler));
		} catch (IOException e) {
			Thingle.getContext().log(e);
			throw new ThingleException("Failed to load: " + xml, e);
		}
	}
	
	/**
	 * Render the page to the given graphics context
	 * 
	 * @param g The graphics context to render to
	 */
	public void render() {
		Thingle.doPreRender();
		thinlet.paint(Thingle.getGraphics(), Thingle.getWidth(), Thingle.getHeight());	
		Thingle.doPostRender();
	}
	
	/**
	 * Layout the GUI
	 */
	public void layout() {
		thinlet.layout(Thingle.getWidth(), Thingle.getHeight());
	}
	
	/**
	 * Update the input handling for the page
	 * 
	 * @param delta The amount of time in milliseconds thats passed since last update
	 */
	public void update(int delta) {
		thinlet.getInput().update(delta);
	}
	
	/**
	 * Enable input to this GUI page
	 */
	public void enable() {
		thinlet.getInput().enable();
	}

	/**
	 * Disabl input to this GUI page
	 */
	public void disable() {
		thinlet.getInput().disable();
	}
	
	/**
	 * Get a widget in the page
	 * 
	 * @param name The name of the widget to retrieve
 	 * @return The widget or null if no widget by that name could be found
	 */
	public Widget getWidget(String name) {
		Object component = thinlet.find(name);
		if (component == null) {
			return null;
		}
		return Widget.getWidget(thinlet, component);
	}

	/**
	 * Returns a new widget with the specified name.
	 * 
	 * @param classname The name to give the widget (as defined by thinlet)
	 * @return The newly created widget
	 */
	public Widget createWidget(String classname) {
		return Widget.getWidget(thinlet, Thinlet.create(classname));
	}

	/**
	 * Returns the desktop widget.
	 * 
	 * @return The widget representing the desktop
	 */
	public Widget getDesktop() {
		return Widget.getWidget(thinlet, thinlet.getDesktop());
	}
	
	/**
	 * Add a widget to the top level component
	 * 
	 * @param widget The widget to add
	 */
	public void add(Widget widget) {
		widget.addAtTopLevel();
	}
	
	/**
	 * Remove a widget from the top level component
	 * 
	 * @param widget The widget to remove
	 */
	public void remove(Widget widget) {
		widget.removeFromTopLevel();
	}
	
	/**
	 * Clears the focus from any widgets.
	 */
	public void clearFocus () {
		thinlet.setFocus(null);
	}
}
