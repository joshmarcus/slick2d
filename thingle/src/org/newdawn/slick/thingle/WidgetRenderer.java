package org.newdawn.slick.thingle;

import org.newdawn.slick.thingle.internal.Rectangle;
import org.newdawn.slick.thingle.spi.ThingleGraphics;

/**
 * A plugin to allow custom rendering to components, currently only panels
 * 
 * @author kevin
 */
public interface WidgetRenderer {

	/**
	 * Render the contents of the widget
	 * 
	 * @param g The graphics context to render to 
	 * @param widget The widget being rendered
	 * @param bounds The bounds of the component
	 */
	public void paint(ThingleGraphics g, Widget widget, Rectangle bounds);

	/**
	 * Returns the preferred width of the widget. If the "width" attribute is set on the widget, this method will
	 * not be used and can return 0.
	 * 
	 * @return The preferred width of the widget (0 if the width attribute is used on the widget)
	 */
	public int getPreferredWidth ();

	/**
	 * Returns the preferred height of the widget. If the "height" attribute is set on the widget, this method will
	 * not be used and can return 0.
	 * 
	 * @return The preferred height of the widget (0 if the height attribute is used on the widget)
	 */
	public int getPreferredHeight ();
}
