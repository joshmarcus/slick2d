package org.newdawn.slick.thingle.demos;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.newdawn.slick.thingle.Thingle;
import org.newdawn.slick.thingle.internal.Thinlet;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

/**
 * Simple demonstration of widgets and events
 */
public class Demo {

	/** The dialog created on search */
	private Object dialog;
	/** The current colour settings */
	private Object sl_red;
	/** The current colour settings */
	private Object sl_green;
	/** The current colour settings */
	private Object sl_blue;
	/** The current HSB settings */
	private Object tf_hue;
	/** The current HSB settings */
	private Object tf_saturation;
	/** The current HSB settings */
	private Object tf_brightness;
	/** The current HSB settings */
	private Object pb_hue;
	/** The current HSB settings */
	private Object pb_saturation;
	/** The current HSB settings */
	private Object pb_brightness;
	/** The colour of the label */
	private Object rgb_label;

	/** The thinlet instance to control */
	private Thinlet thinlet;
	
	/**
	 * Loads the xml file
	 */
	public Demo() {
	}
	
	/**
	 * Initialise the demo panel
	 * 
	 * @param thinlet The thinlet instance to control
	 */
	public void init(Thinlet thinlet) {
		this.thinlet = thinlet;
	}
	
	/**
	 * Called if the demo.xml was loaded,
	 * it fills the textarea from a resource file
	 * 
	 * @param textarea The text area into which the text should be loaded
	 * @throws Exception Indicates a failure loading the text
	 */
	public void loadText(Object textarea) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
			ResourceLoader.getResourceAsStream("res/demodialog.xml")));
		StringBuffer text = new StringBuffer();
		for (int c = reader.read(); c != -1; c = reader.read()) {
			if (((c > 0x1f) && (c < 0x7f)) ||
					((c > 0x9f) && (c < 0xffff)) || (c == '\n')) {
				text.append((char) c);
			}
			else if (c == '\t') {
				text.append("  ");
			}
		}
		reader.close();
		thinlet.setString(textarea, "text", text.toString());
	}

	/**
	 * Updates textarea's editable property depending on a checkbox state
	 * 
	 * @param textarea The text area to be modified
	 * @param editable True if the text area should be editable
	 */
	public void changeEditable(boolean editable, Object textarea) {
		thinlet.setBoolean(textarea, "editable", editable);
	}

	/**
	 * Updates textarea's enabled property
	 * 
	 * @param textarea The text area to be modified
	 * @param enabled True if the text area should be editable
	 */
	public void changeEnabled(boolean enabled, Object textarea) {
		thinlet.setBoolean(textarea, "enabled", enabled);
	}

	/**
	 * Shows the modal find dialog, creates only one dialog instance
	 * 
	 * @throws Exception Indicates a failure to create the dialog
	 */
	public void showDialog() throws Exception {
		if (dialog == null) {
			dialog = thinlet.parse("res/demodialog.xml", this);
		}
		thinlet.add(dialog);
	}

	/**
	 * Updates the textarea's selection range,
	 * and add the search string to the history 
	 * 
	 * @param combobox The box in which the criteria was entered
	 * @param what The search string
	 * @param match True if we should match case
	 * @param down True if we search down the text
	 */
	public void findText(Object combobox, String what,
			boolean match, boolean down) {
		closeDialog();
		if (what.length() == 0) { return; }

		boolean cacheditem = false;
		for (int i = thinlet.getCount(combobox) - 1; i >= 0; i--) {
			String choicetext = thinlet.getString(thinlet.getItem(combobox, i), "text");
			if (what.equals(choicetext)) { cacheditem = true; break; }
		}
		if (!cacheditem) {
			Object choice = Thinlet.create("choice");
			thinlet.setString(choice, "text", what);
			thinlet.add(combobox, choice);
		}

		Object textarea = thinlet.find("textarea");
		int end = thinlet.getInteger(textarea, "end");
		String text = thinlet.getString(textarea, "text");
		
		if (!match) {
			what = what.toLowerCase();
			text = text.toLowerCase();
		}

		int index = text.indexOf(what, down ? end : 0);
		if (!down && (index != -1) && (index >= end)) { index = -1; }
		if (index != -1) {
			thinlet.setInteger(textarea, "start", index);
			thinlet.setInteger(textarea, "end", index + what.length());
			thinlet.requestFocus(textarea);
		}
	}

	/**
	 * Closes the dialog
	 */
	public void closeDialog() {
		thinlet.remove(dialog);
	}
	
	/**
	 * Insert a new item into the list
	 * 
	 * @param list Insert a new item into the list
	 */
	public void insertList(Object list) {
		Object item = Thinlet.create("item");
		thinlet.setString(item, "text", "New item");
		thinlet.setIcon(item, "icon", thinlet.getIcon("res/icon/library.gif"));
		thinlet.add(list, item, 0);
	}

	/**
	 * Removes the selected items from the list
	 * 
	 * @param delete The item to delete
	 * @param list The list to delete from
	 */
	public void deleteList(Object delete, Object list) {
		for (int i = thinlet.getCount(list) - 1; i >= 0; i--) {
			Object item = thinlet.getItem(list, i);
			if (thinlet.getBoolean(item, "selected")) {
				thinlet.remove(item);
			}
		}
		thinlet.setBoolean(delete, "enabled", false);
	}
	
	/**
	 * Delete button's state depends on the list selection
	 * 
	 * @param list The list to change the selection in
	 * @param delete The item to delete
	 */
	public void changeSelection(Object list, Object delete) {
		thinlet.setBoolean(delete, "enabled", thinlet.getSelectedIndex(list) != -1);
	}

	/**
	 * Clears list selection and updates the selection model
	 * 
	 * @param list The list to set the selection on
	 * @param selection The currently selected object
	 * @param delete True if deleteion should be allowed
	 */
	public void setSelection(Object list, String selection, Object delete) {
		for (int i = thinlet.getCount(list) - 1; i >= 0; i--) {
			thinlet.setBoolean(thinlet.getItem(list, i), "selected", false);
		}
		thinlet.setChoice(list, "selection", selection);
		thinlet.setBoolean(delete, "enabled", false);
	}
	
	/**
	 * Notification that slider value changed
	 * 
	 * @param value The value that was changed
	 * @param spinbox The spinbox to update
	 */
	public void sliderChanged(int value, Object spinbox) {
		thinlet.setString(spinbox, "text", String.valueOf(value));
		hsbChanged();
	}
	
	/**
	 * Notification that spinbox value has been changd
	 * 
	 * @param text The new text in the spin box
	 * @param slider The slider to be updated
	 */
	public void spinboxChanged(String text, Object slider) {
		try {
			int value = Integer.parseInt(text);
			if ((value >= 0) && (value <= 255)) {
				thinlet.setInteger(slider, "value", value);
				hsbChanged();
			}
		} catch (NumberFormatException nfe) { Log.error(nfe); }
	}
	
	/**
	 * Store the values of the current HSB widget
	 * 
	 * @param sl_red The red component
	 * @param sl_green The green component
	 * @param sl_blue The blue component 
	 * @param tf_hue The hue
	 * @param tf_saturation The saturation
	 * @param tf_brightness The brightness
	 * @param pb_hue The hue
	 * @param pb_saturation The saturation
	 * @param pb_brightness The brightness
	 * @param rgb_label The RGB value of the label
	 */
	public void storeWidgets(Object sl_red, Object sl_green, Object sl_blue,
			Object tf_hue, Object tf_saturation, Object tf_brightness,
			Object pb_hue, Object pb_saturation, Object pb_brightness, Object rgb_label) {
		this.sl_red = sl_red;
		this.sl_green = sl_green;
		this.sl_blue = sl_blue;
		this.tf_hue = tf_hue;
		this.tf_saturation = tf_saturation;
		this.tf_brightness = tf_brightness;
		this.pb_hue = pb_hue;
		this.pb_saturation = pb_saturation;
		this.pb_brightness = pb_brightness;
		this.rgb_label = rgb_label;
	}
	
	/**
	 * Notification that HSB changed
	 */
	private void hsbChanged() {
		int red = thinlet.getInteger(sl_red, "value");
		int green = thinlet.getInteger(sl_green, "value");
		int blue = thinlet.getInteger(sl_blue, "value");
		
		float[] hsb = java.awt.Color.RGBtoHSB(red, green, blue, null);

		thinlet.setString(tf_hue, "text", String.valueOf(hsb[0]));
		thinlet.setString(tf_saturation, "text", String.valueOf(hsb[1]));
		thinlet.setString(tf_brightness, "text", String.valueOf(hsb[2]));

		thinlet.setInteger(pb_hue, "value", (int) (100f * hsb[0]));
		thinlet.setInteger(pb_saturation, "value", (int) (100f * hsb[1]));
		thinlet.setInteger(pb_brightness, "value", (int) (100f * hsb[2]));

		thinlet.setColor(rgb_label, "background", Thingle.createColor(red, green, blue));
		thinlet.setColor(rgb_label, "foreground", Thingle.createColor(255 - red, 255 - green, 255 - blue));
	}
}