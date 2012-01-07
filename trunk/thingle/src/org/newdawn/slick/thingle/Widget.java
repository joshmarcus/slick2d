package org.newdawn.slick.thingle;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import org.newdawn.slick.thingle.internal.Dimension;
import org.newdawn.slick.thingle.internal.Thinlet;
import org.newdawn.slick.thingle.spi.ThingleColor;
import org.newdawn.slick.thingle.spi.ThingleFont;
import org.newdawn.slick.thingle.spi.ThingleImage;

/**
 * A wrapper around the thinlet representation of components.
 * 
 * @author kevin
 * @author Nathan Sweet <misc@n4te.com>
 */
public class Widget {
	/** The cache of references to widgets */
	private static HashMap cache = new HashMap();

	/**
	 * Get the widget for a particular thinlet component
	 * 
	 * @param thinlet
	 *            The thinlet instnce the widget will be associated with
	 * @param component
	 *            The component itself
	 * @return The widget relating to the given component
	 */
	public static Widget getWidget(Thinlet thinlet, Object component) {
		SoftReference ref = (SoftReference) cache.get(component);
		if (ref != null) {
			Widget cached = (Widget) ref.get();
			if (cached != null) {
				return cached;
			}
		}

		Widget widget = new Widget(thinlet, component);
		cache.put(component, new SoftReference(widget));

		return widget;
	}

	/** The thinlet instance being configured */
	private Thinlet thinlet;

	/** The component handle */
	Object component;

	/**
	 * Create a new widget.
	 * 
	 * @param thinlet
	 *            The thinlet instance to be configured
	 * @param component
	 *            The component handler
	 */
	private Widget(Thinlet thinlet, Object component) {
		this.thinlet = thinlet;
		this.component = component;
	}

	/**
	 * Get the name of the type of widget.
	 * 
	 * @return The name of the type of widget
	 */
	public String getWidgetClass() {
		return Thinlet.getClass(component);
	}

	/**
	 * Adds a child widget.
	 * 
	 * @param child
	 *            The child widget to add
	 */
	public void add(Widget child) {
		thinlet.add(component, child.component);
	}

	/**
	 * Adds a child widget at the specified index.
	 * 
	 * @param child
	 *            The child widget to add
	 * @param index
	 *            The index to insert the widget
	 */
	public void add(Widget child, int index) {
		thinlet.add(component, child.component, index);
	}

	/**
	 * Removes this widget from the page.
	 */
	public void remove() {
		thinlet.remove(component);
	}

	/**
	 * Returns the child widget with the specified name.
	 * 
	 * @param name
	 *            The name of the child widget
	 * @return The widget representing the child with the given name
	 */
	public Widget getChild(String name) {
		Object child = thinlet.find(component, name);
		if (child == null) {
			return null;
		}
		return getWidget(thinlet, child);
	}

	/**
	 * Returns the child widget at the specified index.
	 * 
	 * @param index
	 *            The child's index
	 * @return The widget representing the child at the given index
	 */
	public Widget getChild(int index) {
		Object child = thinlet.getItem(component, index);
		if (child == null) {
			return null;
		}

		return getWidget(thinlet, child);
	}

	/**
	 * Returns the child widgets.
	 * 
	 * @return A list of all all the children of the widget
	 */
	public Widget[] getChildren() {
		Object[] children = thinlet.getItems(component);
		Widget[] widgets = new Widget[children.length];
		for (int i = 0; i < children.length; i++) {
			widgets[i] = getWidget(thinlet, children[i]);
		}

		return widgets;
	}

	/**
	 * Returns the number of children this widget has.
	 * 
	 * @return The number of children the widget has
	 */
	public int getChildrenCount() {
		return thinlet.getCount(component);
	}

	/**
	 * Removes the child widget at the specified index from the page.
	 * 
	 * @param index
	 *            The index of the child to remove
	 */
	public void removeChild(int index) {
		Object child = thinlet.getItem(component, index);
		if (child != null) {
			thinlet.remove(child);
		}
	}

	/**
	 * Removes all child widgets from the page.
	 */
	public void removeChildren() {
		thinlet.removeAll(component);
	}

	/**
	 * Returns the parent widget.
	 * 
	 * @return The parent of this widget or null if there is none
	 */
	public Widget getParent() {
		Object parent = thinlet.getParent(component);
		if (parent == null) {
			return null;
		}
		return getWidget(thinlet, parent);
	}

	/**
	 * Returns the "popupmenu" widget for this widget.
	 * 
	 * @return The pop up menu widget for this component or null if there is
	 *         none
	 */
	public Widget getPopupMenu() {
		Object popupmenu = thinlet.getWidget(component, "popupmenu");
		if (popupmenu == null) {
			return null;
		}

		return getWidget(thinlet, popupmenu);
	}

	/**
	 * Returns the "header" widget for this widget.
	 * 
	 * @return The table header for this widget, or null if there is none
	 */
	public Widget getTableHeader() {
		Object header = thinlet.getWidget(component, "header");
		if (header == null) {
			return null;
		}
		return getWidget(thinlet, header);
	}

	/**
	 * Sets a name/value pair. Widgets can store a list of key/value pairs,
	 * defined in XML with the "property" attribute.
	 * 
	 * @param name
	 *            The property name
	 * @param value
	 *            The property value
	 */
	public void setProperty(Object name, Object value) {
		thinlet.putProperty(component, name, value);
	}

	/**
	 * Returns the value of the property with the specified name, or null if no
	 * property was found.
	 * 
	 * @param name
	 *            The property name
	 * @return The value for the given property
	 * @see #putProperty(Object, Object)
	 */
	public Object getProperty(Object name) {
		return thinlet.getProperty(component, name);
	}

	/**
	 * Causes this widget to be focused.
	 * 
	 * @return True if the component got focus
	 */
	public boolean focus() {
		return thinlet.requestFocus(component);
	}

	/**
	 * Gets a string attribute.
	 * 
	 * @param key
	 *            The name of the attribute to get
	 * @return The value of the attribute requested
	 */
	public String getString(String key) {
		return thinlet.getString(component, key);
	}

	/**
	 * Gets a boolean attribute.
	 * 
	 * @param key
	 *            The name of the attribute to get
	 * @return The value of the attribute requested
	 */
	public boolean getBoolean(String key) {
		return thinlet.getBoolean(component, key);
	}

	/**
	 * Gets a choice attribute.
	 * 
	 * @param key
	 *            The name of the attribute to get
	 * @return The value of the attribute requested
	 */
	public String getChoice(String key) {
		return thinlet.getChoice(component, key);
	}

	/**
	 * Gets an integer attribute.
	 * 
	 * @param key
	 *            The name of the attribute to get
	 * @return The value of the attribute requested
	 */
	public int getInteger(String key) {
		return thinlet.getInteger(component, key);
	}

	/**
	 * Gets the preffered size of the widget
	 * 
	 * @return the preffered size
	 */
	public Dimension getPreferredSize() {
		return thinlet.getPreferredSize(component);
	}

	/**
	 * Gets a color attribute.
	 * 
	 * @param attribute
	 *            The name of the attribute to get
	 * @return The value of the attribute requested
	 */
	public ThingleColor getColor(String attribute) {
		return thinlet.getThinletColor(component, attribute);
	}

	/**
	 * Gets a font attribute.
	 * 
	 * @param key
	 *            The name of the attribute to get
	 * @return The value of the attribute requested
	 */
	public ThingleFont getFont(String key) {
		return thinlet.getFont(component, key);
	}

	/**
	 * Gets an icon attribute.
	 * 
	 * @param key
	 *            The name of the attribute to get
	 * @return The value of the attribute requested
	 */
	public ThingleImage getIcon(String key) {
		return thinlet.getIcon(component, key);
	}

	/**
	 * Returns the value of the "text" attribute. This is only applicable to
	 * widgets that display text.
	 * 
	 * @return The text being displayed by this widget
	 */
	public String getText() {
		return thinlet.getString(component, "text");
	}

	/**
	 * Returns the first selected child widget.
	 * 
	 * @return The selected widget (as in selected items in a list)
	 */
	public Widget getSelectedWidget() {
		Object selected = thinlet.getSelectedItem(component);
		if (selected == null) {
			return null;
		}

		return getWidget(thinlet, selected);
	}

	/**
	 * Returns the index of the first selected child widget.
	 * 
	 * @return The index of the first selected item
	 */
	public int getSelectedIndex() {
		return thinlet.getSelectedIndex(component);
	}

	/**
	 * Returns the selected child widgets (as in selected items in a list)
	 * 
	 * @return The widgets selected within this widget
	 */
	public Object[] getSelectedWidgets() {
		Object[] selected = thinlet.getSelectedItems(component);
		Widget[] widgets = new Widget[selected.length];
		System.arraycopy(selected, 0, widgets, 0, selected.length);
		return widgets;
	}

	/**
	 * Sets a bit of thinlet script to be run when an event occurs on this
	 * widget.
	 * 
	 * @param event
	 *            The event name (eg, "action")
	 * @param script
	 *            The thinlet script to run (eg, "foo(this, this.text,
	 *            mybutton)")
	 * @param handler
	 *            The object which will have the method called when the event
	 *            occurs
	 */
	public void setMethod(String event, String script, Object handler) {
		setMethod(event, script, this, handler);
	}

	/**
	 * Sets a bit of thinlet script to be run when an event occurs on this
	 * widget.
	 * 
	 * @param event
	 *            The event name (eg, "action")
	 * @param script
	 *            The thinlet script to run (eg, "foo(this, this.text,
	 *            mybutton)")
	 * @param root
	 *            The widget to search for widgets named in the thinlet script
	 * @param handler
	 *            The object which will have the method called when the event
	 *            occurs
	 */
	public void setMethod(String event, String script, Widget root,
			Object handler) {
		thinlet.setMethod(component, event, script, root.component, handler);
	}

	/**
	 * Sets a string attribute.
	 * 
	 * @param attribute
	 *            The name of the attribute to set
	 * @param value
	 *            The value of the attribute
	 */
	public void setString(String attribute, String value) {
		thinlet.setString(component, attribute, value);
	}

	/**
	 * Sets a boolean attribute.
	 * 
	 * @param attribute
	 *            The name of the attribute to set
	 * @param value
	 *            The value of the attribute
	 */
	public void setBoolean(String attribute, boolean value) {
		thinlet.setBoolean(component, attribute, value);
	}

	/**
	 * Sets a choice attribute.
	 * 
	 * @param attribute
	 *            The name of the attribute to set
	 * @param value
	 *            The value of the attribute
	 */
	public void setChoice(String attribute, String value) {
		thinlet.setChoice(component, attribute, value);
	}

	/**
	 * Sets a color attribute.
	 * 
	 * @param attribute
	 *            The name of the attribute to set
	 * @param color
	 *            The value of the attribute
	 */
	public void setColor(String attribute, ThingleColor color) {
		thinlet.setColor(component, attribute, color);
	}

	/**
	 * Sets a font attribute.
	 * 
	 * @param attribute
	 *            The name of the attribute to set
	 * @param font
	 *            The value of the attribute
	 */
	public void setFont(String attribute, ThingleFont font) {
		thinlet.setFont(component, attribute, font);
	}

	/**
	 * Sets the "font" font attribute.
	 * 
	 * @param attribute
	 *            The name of the attribute to set
	 * @param font
	 *            The value of the attribute
	 */
	public void setFont(ThingleFont font) {
		thinlet.setFont(component, font);
	}

	/**
	 * Sets an icon attribute.
	 * 
	 * @param attribute
	 *            The name of the attribute to set
	 * @param iconRef
	 *            The value of the attribute
	 */
	public void setIcon(String attribute, String iconRef) {
		thinlet.setIcon(component, attribute, thinlet.getIcon(iconRef));
	}

	/**
	 * Set the "icon" icon attribute.
	 * 
	 * @param iconRef
	 *            The icon to be displayed on the widget
	 */
	public void setIcon(String iconRef) {
		thinlet.setIcon(component, "icon", thinlet.getIcon(iconRef));
	}

	/**
	 * Set the icon explicitly on the widget
	 * 
	 * @param image
	 *            The image to apply
	 */
	public void setIcon(ThingleImage image) {
		thinlet.setIcon(component, "icon", image);
	}

	/**
	 * Sets an integer attribute.
	 * 
	 * @param attribute
	 *            The name of the attribute to set
	 * @param value
	 *            The value of the attribute
	 */
	public void setInteger(String attribute, int value) {
		thinlet.setInteger(component, attribute, value);
	}

	/**
	 * Sets the "text" attribute. This is only applicable to widgets that
	 * display text.
	 * 
	 * @param text
	 *            The text to display
	 */
	public void setText(String text) {
		thinlet.setString(component, "text", text);
	}

	/**
	 * Sets the "accelerator" attribute to the specified keystroke. This is only
	 * applicable to "menuitem" and "checkboxmenuitem" widgets.
	 * 
	 * @param keys
	 *            The string denoting the accelerator
	 */
	public void setAccelerator(String keys) {
		thinlet.setKeystroke(component, "accelerator", keys);
	}

	/**
	 * Cause this widget to be added to the top level of the thinlet instance
	 * it's connected to
	 */
	void addAtTopLevel() {
		thinlet.add(component);
	}

	/**
	 * Cause the widget to be removed frm the top level of thinlet
	 */
	void removeFromTopLevel() {
		thinlet.remove(component);
	}

	/**
	 * Set the renderer that will be used to produce the content of this widget
	 * 
	 * @param renderer
	 *            The renderer to produce the content of this widget
	 */
	public void setRenderer(WidgetRenderer renderer) {
		Thinlet.set(component, "renderer", renderer);
	}

	/**
	 * Sets the amount scrolled in both directions.
	 * 
	 * @param hpercent
	 *            The amount scrolled horizontally.
	 * @param vpercent
	 *            The amount scrolled vertically.
	 */
	public void setScroll(float hpercent, float vpercent) {
		thinlet.setScroll(component, hpercent, vpercent);
	}

	/**
	 * Sets the position of the caret (or insertion cursor) to a specific
	 * location in a text field.
	 * 
	 * @param position
	 *            Position of the caret, in characters
	 */
	public void setCaret(int position) {
		thinlet.setSelection(component, position, position);
	}

	/**
	 * Sets the selected text.
	 * 
	 * @param start
	 *            Position of the first character to select
	 * @param end
	 *            Position of the last character to select
	 */
	public void setSelection(int start, int end) {
		thinlet.setSelection(component, start, end);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String name = thinlet.getString(component, "name");
		if (name == null) {
			name = getWidgetClass();
		}
		return name;
	}
}
