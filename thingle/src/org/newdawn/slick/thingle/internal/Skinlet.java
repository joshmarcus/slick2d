package org.newdawn.slick.thingle.internal;

import java.util.Hashtable;
import java.util.StringTokenizer;

import org.newdawn.slick.thingle.Thingle;
import org.newdawn.slick.thingle.internal.ThinletSkin.Insets;
import org.newdawn.slick.thingle.internal.ThinletSkin.Piece;
import org.newdawn.slick.thingle.spi.ThingleColor;
import org.newdawn.slick.thingle.spi.ThingleException;
import org.newdawn.slick.thingle.spi.ThingleFont;
import org.newdawn.slick.thingle.spi.ThingleGraphics;
import org.newdawn.slick.thingle.spi.ThingleImage;

/**
 * Adds skin support for Thinlets. Overrides some of original Thinlet layout and
 * paint methods to enable skinning. Take look at ThinletSkin.java - it does all
 * the skin paint work and is referenced when a component needs to be drawn.
 * 
 * @author Eugene Klein (http://www.realchat.com/thinlet/)
 * @version 0.4 ( preview )
 * 
 */
public class Skinlet extends Thinlet {
	public ThinletSkin skin = new ThinletSkin();

	Hashtable skinCache = new Hashtable();

	public String NAME_STRING = "Skinlet",
			VERSION_STRING = "0.4.2 beta, bugfix release";

	public void loadSkin(String path, String name) throws ThingleException {
		// cache preloaded skins...
		ThinletSkin s = (ThinletSkin) skinCache.get(name);
		if (s == null) {
			s = new ThinletSkin();
			s.load(path, name);
			skinCache.put(name, s);
		}
		setSkin(s);
	}

	public void setSkin(ThinletSkin s) {
		skin = s;
		setFont(skin.fontPool[0]);
	}

	/**
	 * Below are the original Thinlet methods, overriden to enable skin support.
	 * All were commented out in Thinlet.java to reduce size.
	 */

	int c76 = 76; // the original 76 used as default width for various
					// components

	/**
	 * Paints the components inside the graphics clip area:
	 * 
	 * @param g
	 *            Graphics
	 */
	public void paint(ThingleGraphics g, int width, int height) {
		if (!skin.loaded) {
			super.paint(g, width, height);
			return;
		}
		
		g.setFont(font);
		cacheGradients();
		Rectangle clip = g.getClip();
		paint(g, clip.x, clip.y, clip.width, clip.height, content, isEnabled());
		g.setClip(0,0,width,height);
	}

	/**
	 * @param component
	 *            a widget including the text and icon parameters
	 * @param dx
	 *            increase width by this value
	 * @param dy
	 *            increase height by this value
	 * @return size of the text and the image (plus a gap) including the given
	 *         offsets
	 */
	protected Dimension getSize(Object component, int dx, int dy) {
		if (!skin.loaded()) {
			return super.getSize(component, dx, dy);
		}
		
		String text = getString(component, "text", null);
		int tw = 0;
		int th = 0;
		if (text != null) {
			ThingleFont currentfont = (text != null) ? (ThingleFont) get(
					component, "font") : null;
			if (currentfont == null)
				currentfont = skin.fontPool[0];
			boolean styled = getBoolean(component, "styled", false);
			FontMetrics fm = getFontMetrics(currentfont);
			tw = styledStringWidth(styled, currentfont, text);
			th = fm.getAscent() + fm.getDescent();
		}
		ThingleImage icon = getIcon(component, "icon", null);
		int iw = 0;
		int ih = 0;
		if (icon != null) {
			iw = icon.getWidth();
			ih = icon.getHeight();
			if (text != null) {
				iw += skin.icon.delta;
			}
		}
		return new Dimension(tw + iw + dx, Math.max(th, ih) + dy);
	}

	/**
	 * @param component -
	 * @return -
	 */
	public Dimension getPreferredSize(Object component) {
		if (!skin.loaded()) {
			return super.getPreferredSize(component);
		}
		
		int width = getInteger(component, "width", 0);
		int height = getInteger(component, "height", 0);

		if ((width > 0) && (height > 0)) {
			return new Dimension(width, height);
		}
		String classname = getClass(component);
		if ("label" == classname) {
			return getSize(component, 0, 0);
		}
		if (("button" == classname) || ("togglebutton" == classname)) {
			return getSize(component, skin.button.insets.lr,
					skin.button.insets.tb);
		}
		if ("checkbox" == classname) {
			return getSize(component, skin.checkBox.pieceWidth
					+ skin.checkBox.delta, 0);
		}
		if ("combobox" == classname) {
			if (getBoolean(component, "editable", true)) {
				Dimension size = getFieldSize(component, skin.comboborder);
				ThingleImage icon = getIcon(component, "icon", null);
				if (icon != null) {
					size.width += icon.getWidth();
					size.height = Math.max(size.height, icon.getHeight()
							+ skin.icon.delta);
				}
				size.width += skin.combobutton.pieceWidth;
				return size;
			} else {
				// maximum size of current values and choices including combo
				// border insets
				int dw = skin.comboborder.insets.lr;
				int dh = skin.comboborder.insets.tb;
				Dimension size = getSize(component, dw, dh);
				for (Object item = get(component, ":comp"); item != null; item = get(
						item, ":next")) {
					Dimension d = getSize(item, dw, dh);
					size.width = Math.max(d.width, size.width);
					size.height = Math.max(d.height, size.height);
				}
				size.width += skin.combobutton.pieceWidth;
				if (size.height == dh) { // no content nor items, set text
											// height
					ThingleFont customfont = (ThingleFont) get(component,
							"font");
					FontMetrics fm = getFontMetrics((customfont != null) ? customfont
							: font);
					size.height = fm.getAscent() + fm.getDescent() + dh;
				}
				return size;
			}
		}
		if (("textfield" == classname) || ("passwordfield" == classname)) {
			Dimension size = getFieldSize(component, skin.textField);
			size.width += skin.textField.insets.lr;
			size.height += skin.textField.insets.tb;
			return size;
		}
		if ("textarea" == classname) {
			int columns = getInteger(component, "columns", 0);
			int rows = getInteger(component, "rows", 0); // 'e' -> 'm' ?
			ThingleFont currentfont = (ThingleFont) get(component, "font");
			FontMetrics fm = getFontMetrics((currentfont != null) ? currentfont
					: font);
			Dimension size = new Dimension(((columns > 0) ? (columns * fm
					.charWidth('e')) : c76), // why 76???
					((rows > 0) ? (rows * fm.getHeight() - fm.getLeading())
							: c76));
			size.width += skin.textField.insets.lr;
			size.height += skin.textField.insets.tb;
			return size;
		}
		if ("tabbedpane" == classname) {
			String placement = getString(component, "placement", "top");
			boolean horizontal = ((placement == "top") || (placement == "bottom"));
			boolean stacked = (placement == "stacked");
			int cnt = getItemCountImpl(component, ":comp");
			int tabsize = 0; // max tab height/width
			int contentwidth = 0;
			int contentheight = 0; // max content size
			for (Object tab = get(component, ":comp"); tab != null; tab = get(
					tab, ":next")) {
				Dimension d = getSize(tab, skin.tabItem.insets.lr,
						skin.tabItem.insets.tb);
				tabsize = Math.max(tabsize, horizontal ? d.height : d.width);

				Object comp = get(tab, ":comp");
				if ((comp != null) && getBoolean(comp, "visible", true)) {
					Dimension dc = getPreferredSize(comp);
					contentwidth = Math.max(contentwidth, dc.width);
					contentheight = Math.max(contentheight, dc.height);
				}
			}
			return new Dimension(contentwidth
					+ ((horizontal || stacked) ? 0 : (tabsize)), contentheight
					+ (horizontal ? (tabsize) : stacked ? (tabsize) * cnt : 0));
		}
		if (("panel" == classname) || (classname == "dialog")) {
			// title text and icon height
			Dimension size = getSize(component, 0, 0);
			// add border size
			if (classname == "dialog") {
				int titleheight = Math.max(getInteger(component,
						":titleheight", 0), skin.titlebar.pieceHeight);
				size.width = skin.dialog.insets.lr;
				size.height = skin.dialog.insets.tb + titleheight; // title
																	// width
																	// neglected
			} else if (getBoolean(component, "border", false)) { // bordered
																	// panel
				size.width += skin.panel.borderInsets.lr;
				size.height += skin.panel.borderInsets.tb; // title includes
															// line
			} else {
				size.width = 0;
			} // title width is clipped
			// add paddings
			size.width += getInteger(component, "left", 0)
					+ getInteger(component, "right", 0);
			size.height += getInteger(component, "top", 0)
					+ getInteger(component, "bottom", 0);
			// add content preferred size
			int gap = getInteger(component, "gap", 0);
			int[][] grid = getGrid(component); // TODO: Why is this argument
												// not here , gap );
			if (grid != null) { // has components
				size.width += getSum(grid[0], 0, grid[0].length, gap, false);
				size.height += getSum(grid[1], 0, grid[1].length, gap, false);
			}
			return size;
		} else if ("desktop" == classname) {
			Dimension size = new Dimension();
			for (Object comp = get(component, ":comp"); comp != null; comp = get(
					comp, ":next")) {
				String iclass = getClass(comp);
				if ((iclass != "dialog") && (iclass != ":popup")
						&& (iclass != ":combolist")) {
					Dimension d = getPreferredSize(comp);
					size.width = Math.max(d.width, size.width);
					size.height = Math.max(d.height, size.height);
				}
			}
			return size;
		}
		if ("spinbox" == classname) {
			Dimension size = getFieldSize(component, skin.textField);
			size.width += skin.spinBox.pieceWidth;
			size.height = skin.spinBox.pieceHeight * 2;
			return size;
		}
		if ("progressbar" == classname) {
			boolean horizontal = ("vertical" != get(component, "orientation"));
			return new Dimension(horizontal ? c76
					: skin.progressBar.pieceHeight,
					horizontal ? skin.progressBar.pieceHeight : c76);
		}
		if ("slider" == classname) {
			boolean horizontal = ("vertical" != get(component, "orientation"));
			return new Dimension(horizontal ? c76
					: skin.sliderKnobVert.pieceWidth,
					horizontal ? skin.sliderKnobHoriz.pieceHeight : c76);
		}
		if ("splitpane" == classname) {
			boolean horizontal = ("vertical" != get(component, "orientation"));
			Object comp1 = get(component, ":comp");
			Dimension size = ((comp1 == null) || !getBoolean(comp1, "visible",
					true)) ? new Dimension() : getPreferredSize(comp1);
			Object comp2 = get(comp1, ":next");
			if ((comp2 != null) && getBoolean(comp2, "visible", true)) {
				Dimension d = getPreferredSize(comp2);
				size.width = horizontal ? (size.width + d.width) : Math.max(
						size.width, d.width);
				size.height = horizontal ? Math.max(size.height, d.height)
						: (size.height + d.height);
			}
			if (horizontal) {
				size.width += skin.splitVert.pieceWidth;
			} else {
				size.height += skin.splitHoriz.pieceHeight;
			}
			return size;
		}
		if (("list" == classname) || ("table" == classname)
				|| ("tree" == classname)) {
			Dimension size = new Dimension(c76, c76);
			size.width += skin.table.insets.lr;
			size.height += skin.table.insets.tb;
			return size;
		}
		if ("separator" == classname) {
			return new Dimension(skin.separator.pieceWidth,
					skin.separator.pieceHeight);
		}
		if ("menubar" == classname) {
			Dimension size = new Dimension(0, 0);
			for (Object menu = get(component, ":comp"); menu != null; menu = get(
					menu, ":next")) {
				Dimension d = getSize(menu, skin.menuBar.insets.lr,
						skin.menuBar.insets.tb);
				size.width += d.width;
				size.height = Math.max(size.height, d.height);
			}
			return size;
		}
		// if ( "bean" == classname ) {
		// return ( (Component) get( component, "bean" ) ).getPreferredSize();
		// }

		throw new IllegalArgumentException(classname);
	}

	/**
	 * @param g -
	 * @param clipx
	 *            the cliping rectangle is relative to the component's parent
	 *            location similar to the component's bounds rectangle
	 * @param clipy -
	 * @param clipwidth -
	 * @param clipheight -
	 * @param component -
	 * @param enabled -
	 */
	protected void paint(ThingleGraphics g, int clipx, int clipy,
			int clipwidth, int clipheight, Object component, boolean enabled) {

		if (!skin.loaded) {
			super.paint(g, clipx, clipy, clipwidth, clipheight, component, enabled);
			return;
		}

		if (!getBoolean(component, "skin", true)) {
			super.paint(g, clipx, clipy, clipwidth, clipheight, component, enabled);
			return;
		}
		if (!getBoolean(component, "visible", true)) {
			return;
		}
		Rectangle bounds = getRectangle(component, "bounds");
		if (bounds == null) {
			return;
		}
		
		// negative component width indicates invalid component layout
		if (bounds.width < 0) {
			bounds.width = Math.abs(bounds.width);
			doLayout(component);
		}
		// return if the component was out of the cliping rectangle
		if ((clipx + clipwidth < bounds.x) || (clipx > bounds.x + bounds.width)
				|| (clipy + clipheight < bounds.y)
				|| (clipy > bounds.y + bounds.height)) {
			return;
		}
		// set the clip rectangle relative to the component location
		clipx -= bounds.x;
		clipy -= bounds.y;
		g.translate(bounds.x, bounds.y);
		// g.setClip(0, 0, bounds.width, bounds.height);
		String classname = getClass(component);
		boolean pressed = (mousepressed == component);
		boolean inside = (mouseinside == component)
				&& ((mousepressed == null) || pressed);
		boolean focus = focusinside && (focusowner == component);
		enabled = getBoolean(component, "enabled", true); // enabled &&
		boolean hovered = (inside && !pressed);
		ThingleColor bg = (ThingleColor) get(component, "background");
		ThingleColor fg = (ThingleColor) get(component, "foreground");

		if ("label" == classname) {
			if (bg != null) {
				g.setColor(bg);
				g.fillRect(0, 0, bounds.width, bounds.height);
			}
			paintContent(component, g, clipx, clipy, clipwidth, clipheight, 0,
					0, bounds.width, bounds.height, skin.label.getTextColor(
							(enabled) ? 0 : 1, fg), "left", true);
		} else if (("button" == classname) || ("togglebutton" == classname)) {
			boolean toggled = ("togglebutton" == classname)
					&& getBoolean(component, "selected", false);
			boolean link = ("button" == classname)
					&& (getChoice(component, "type") == "link");
			if (link) {
				paintContent(component, g, clipx, clipy, clipwidth, clipheight,
						skin.button.insets.left, skin.button.insets.top,
						bounds.width - skin.button.insets.lr, bounds.height
								- skin.button.insets.tb, skin.link
								.getTextColor((hovered) ? 1 : 0, fg), "left",
						true);
			} else {
				// skinlet:
				int state = (enabled) ? ThinletSkin.Piece.pbNormal
						: ThinletSkin.Piece.pbDisabled;
				if (focus)
					state = ThinletSkin.Piece.pbFocused;
				if (pressed)
					state = ThinletSkin.Piece.pbPressed;
				if (toggled)
					state = ThinletSkin.Piece.pbPressed;
				if (hovered)
					state = ThinletSkin.Piece.pbHovered;

				// rcs: flat property != null - just paint button face
				boolean flat = ((String) getProperty(component, "flat")) != null;
				if (!getBoolean(component, "transparent", false))
					skin.button.paintBody(g, state, 0, 0, bounds.width,
							bounds.height, !flat, !flat, !flat, !flat, bg);

				if (enabled && ("button" == classname)
						&& get(component, "type") == "default")
					state = ThinletSkin.Piece.pbDefault;

				paintContent(component, g, clipx, clipy, clipwidth, clipheight,
						skin.button.insets.left, skin.button.insets.top,
						bounds.width - skin.button.insets.lr, bounds.height
								- skin.button.insets.tb, skin.button
								.getTextColor(state, fg), "center", true);
			}

		} else if ("checkbox" == classname) {
			boolean selected = getBoolean(component, "selected", false);
			String group = getString(component, "group", null); // radio button?

			int dy = (bounds.height - skin.checkBox.pieceHeight) / 2;
			// if (group == null) // checkbox, else radio
			int state = 0;
			if (enabled) {
				if (inside && !pressed) {
					state = (selected) ? ThinletSkin.Piece.cbHoverChecked
							: ThinletSkin.Piece.cbHoverUnchecked;
				} else if (pressed) {
					state = (selected) ? ThinletSkin.Piece.cbPressedChecked
							: ThinletSkin.Piece.cbPressedUnchecked;
				} else {
					state = (selected) ? ThinletSkin.Piece.cbNormalChecked
							: ThinletSkin.Piece.cbNormalUnchecked;
				}
			} else {
				state = (selected) ? ThinletSkin.Piece.cbDisabledChecked
						: ThinletSkin.Piece.cbDisabledUnchecked;
			}

			if (group == null) {
				skin.checkBox.paintBody(g, state, 0, dy,
						skin.checkBox.pieceWidth, skin.checkBox.pieceHeight);
			} else
				skin.radioButton.paintBody(g, state, 0, dy,
						skin.radioButton.pieceWidth,
						skin.radioButton.pieceHeight);

			paintContent(component, g, clipx, clipy, clipwidth, clipheight,
					skin.checkBox.pieceWidth
							+ ((group == null) ? skin.checkBox.delta
									: skin.radioButton.delta), 0, bounds.width
							- skin.checkBox.pieceWidth - 3, bounds.height,
					(group == null) ? skin.checkBox.getTextColor(state, fg)
							: skin.radioButton.getTextColor(state, fg), "left",
					true);

		} else if ("combobox" == classname) {
			int state = enabled ? 0 : 3;
			if (inside && !pressed)
				state = 2;
			if (pressed)
				state = 1;

			ThingleImage icon = getIcon(component, "icon", null);
			int left = (icon != null) ? icon.getWidth() : 0;
			int dx = skin.combobutton.pieceWidth;
			int dy = skin.combobutton.pieceHeight;
			int tx = bounds.width - dx;

			// combo itself
			skin.comboborder.paintBody(g, state, 0, 0, tx, bounds.height);
			skin.combobutton.paintBody(g, state, tx, 0, dx, bounds.height);
			skin.comboarrows.paintBody(g, state, tx, (bounds.height - dy) / 2,
					dx, dy);

			// content
			if (getBoolean(component, "editable", true)) {
				paintField(g, clipx, clipy, clipwidth, clipheight, component,
						tx, bounds.height, inside, pressed, focus, enabled,
						false, left, skin.comboborder);
				if (icon != null) {
					g.drawImage(icon, skin.icon.delta, (bounds.height - icon
							.getHeight()) / 2);
				}
			} else {
				paintContent(component, g, clipx, clipy, clipwidth, clipheight,
						skin.comboborder.insets.left,
						skin.comboborder.insets.top, tx
								- skin.comboborder.insets.lr, bounds.height
								- skin.comboborder.insets.tb, skin.textField
								.getTextColor(enabled ? 0 : 3, fg), "left",
						false);
			}

		} else if (":combolist" == classname) {
			paintScroll(component, classname, bounds, pressed, inside, focus,
					enabled, g, clipx, clipy, clipwidth, clipheight);
		} else if (("textfield" == classname) || ("passwordfield" == classname)) {
			paintField(g, clipx, clipy, clipwidth, clipheight, component,
					bounds.width, bounds.height, inside, pressed, focus,
					enabled, ("passwordfield" == classname), 0, skin.textField);
		} else if ("textarea" == classname) {
			paintScroll(component, classname, bounds, pressed, inside, focus,
					enabled, g, clipx, clipy, clipwidth, clipheight);
		} else if ("tabbedpane" == classname) {
			int i = 0;
			Object selectedtab = null;
			int selected = getInteger(component, "selected", 0);
			String placement = getString(component, "placement", "top");
			boolean horizontal = ((placement == "top") || (placement == "bottom"));
			boolean stacked = (placement == "stacked");
			int bx = horizontal ? 0 : skin.tabItem.delta, by = horizontal ? skin.tabItem.delta
					: 0, bw = 2 * bx, bh = 2 * by;

			// paint tabs except the selected one
			g.setClip(0, 0, bounds.width, bounds.height); // +clip
			for (Object tab = get(component, ":comp"); tab != null; tab = get(
					tab, ":next")) {
				if (selected != i) {
					boolean hover = inside && (mousepressed == null)
							&& (insidepart == tab);
					boolean tabenabled = enabled
							&& getBoolean(tab, "enabled", true);
					Rectangle r = getRectangle(tab, "bounds");
					if (r == null) {
						r = new Rectangle();
						set(tab, "bounds", r);
					}
					// skinlet:
					int state = (tabenabled) ? ThinletSkin.Piece.pbNormal
							: ThinletSkin.Piece.pbDisabled;
					if (hover)
						state = ThinletSkin.Piece.pbHovered;
					if (stacked) {
						skin.sideBar.paintBody(g, state, r.x, r.y, r.width,
								r.height);
					} else {
						skin.tabItem.paintBody(g, state, r.x + bx, r.y + by,
								r.width - bw, r.height - bh,
								(placement != "bottom"),
								(placement != "right"), stacked ? false
										: (placement != "top"),
								(placement != "left"));
					}
					paintContent(tab, g, clipx, clipy, clipwidth, clipheight,
							r.x + skin.tabItem.insets.left, r.y
									+ skin.tabItem.insets.top, r.width
									- skin.tabItem.insets.lr, r.height
									- skin.tabItem.insets.tb, skin.tabItem
									.getTextColor(state, fg), "left", true);
				} else {
					selectedtab = tab;
					// idx = i;
				}
				i++;
			}

			// paint selected tab and its content
			if (selectedtab != null) {
				Rectangle r = getRectangle(selectedtab, "bounds");
				if (r == null) {
					r = new Rectangle();
					set(selectedtab, "bounds", r);
				}
				// paint tabbedpane border
				if (!stacked)
					skin.tabPane
							.paintBody(
									g,
									enabled ? 0 : 1,
									(placement == "left") ? r.width
											- skin.tabItem.delta : 0,
									(placement == "top") ? r.height
											- skin.tabItem.delta : 0,
									horizontal ? bounds.width : (bounds.width
											- r.width + skin.tabItem.delta),
									horizontal ? (bounds.height - r.height + skin.tabItem.delta)
											: bounds.height);

				// paint selected tab
				int state = (focus) ? ThinletSkin.Piece.pbFocused
						: ThinletSkin.Piece.pbPressed;

				if (stacked) {
					skin.sideBar.paintBody(g, state, r.x, r.y, r.width,
							r.height);
				} else {
					skin.tabItem.paintBody(g, state, r.x, r.y,
							stacked ? r.width : r.width, r.height,
							(placement != "bottom"), (placement != "right"),
							(placement != "top") && !stacked,
							(placement != "left"));
				}
				paintContent(selectedtab, g, clipx, clipy, clipwidth,
						clipheight, r.x + skin.tabItem.insets.left, r.y
								+ skin.tabItem.insets.top, r.width
								- skin.tabItem.insets.lr, r.height
								- skin.tabItem.insets.tb, skin.tabItem
								.getTextColor(state, fg), "left", true);

				Object comp = get(selectedtab, ":comp");
				if ((comp != null) && getBoolean(comp, "visible", true)) {
					if (stacked) {
						Rectangle cr = getRectangle(comp, "bounds");
						if (cr == null) {
							cr = new Rectangle();
							set(comp, "bounds", cr);
						}
						doLayout(comp);
					}
					clipx -= r.x;
					clipy -= r.y;
					g.translate(r.x, r.y); // relative to tab
					paint(g, clipx, clipy, clipwidth, clipheight, comp, enabled);
					clipx += r.x;
					clipy += r.y;
					g.translate(-r.x, -r.y);
				}
			}
			g.setClip(clipx, clipy, clipwidth, clipheight); // +clip
		} else if (("panel" == classname) || ("dialog" == classname)) {
			int titleheight = Math.max(
					getInteger(component, ":titleheight", 0),
					skin.titlebar.pieceHeight);
			if ("dialog" == classname) {
				// dialog titlebar
				skin.titlebar.paintBody(g, 0, 0, 0, bounds.width, titleheight);
				// lower part excluding titlebar
				skin.dialog.paintBody(g, 0, 0, titleheight, bounds.width,
						bounds.height - titleheight);
				// dialog title
				// TODO: Used to have getFont()
				ThingleFont tmp = g.getFont();
				g.setFont(skin.fontPool[skin.titlebar.fonts[0]]);
				paintContent(component, g, clipx, clipy, clipwidth, clipheight,
						skin.titlebar.insets.left, skin.titlebar.insets.top,
						bounds.width - skin.titlebar.insets.right, titleheight
								- skin.titlebar.insets.bottom, skin.titlebar
								.getTextColor(0, fg), "left", false);
				g.setFont(tmp);

				// close dialog button
				if (getBoolean(component, "closable", false)) {
					int x = bounds.width - skin.titlebar.borderInsets.right
							- skin.closeButton.pieceWidth, y = skin.titlebar.borderInsets.top;
					int cstate = 0;
					if ((insidepart == "closebutton"))
						cstate = 2;
					if ((pressedpart == "closebutton"))
						cstate = 1;
					skin.closeButton.paintBody(g, cstate, x, y);
				}

				// end of X
			} else { // panel
				if (!getBoolean(component, "transparent")) {
					titleheight = getInteger(component, ":titleheight", 0);
					int state = 0;
					boolean border = getBoolean(component, "border", false);
					state = (enabled) ? ThinletSkin.Piece.pnNormalNoBorder
							: ThinletSkin.Piece.pnDisabledNoBorder;
					if (border)
						state = (enabled) ? ThinletSkin.Piece.pnNormalBorder
								: ThinletSkin.Piece.pnDisabledBorder;
					skin.panel.paintBody(g, state, 0, titleheight,
							bounds.width, bounds.height - titleheight, border,
							border, border, border, bg);
					// todo: panel title ( groupbox ), remove constants
					// paintContent( component, g, clipx, clipy, clipwidth,
					// clipheight,
					// 3, 0, bounds.width - 6, titleheight, enabled ? c_text :
					// c_disable, "left", false );
				}
			}

			if (get(component, ":port") != null) {
				paintScroll(component, classname, bounds, pressed, inside,
						focus, enabled, g, clipx, clipy, clipwidth, clipheight);
			} else {
				for (Object comp = get(component, ":comp"); comp != null; comp = get(
						comp, ":next")) {
					paint(g, clipx, clipy, clipwidth, clipheight, comp, enabled);
				}
			}
		} else if ("desktop" == classname) {
			if (drawDesktop) {
				skin.desktop.paintBody(g, 0, 0, 0, bounds.width, bounds.height,
						false, false, false, false);
			}
			
			paintReverse(g, clipx, clipy, clipwidth, clipheight, get(component,
					":comp"), enabled);
			if ((tooltipowner != null) && (component == content)) {
				Rectangle r = getRectangle(tooltipowner, ":tooltipbounds");
				skin.toolTip.paintBody(g, 0, r.x, r.y, r.width, r.height);
				String text = getString(tooltipowner, "tooltip", null);
				g.setColor(skin.toolTip.getTextColor(0, fg));
				FontMetrics fm = getFontMetrics(g.getFont());
				StringTokenizer s = new StringTokenizer(text, "\n");
				int lines = 0;
				while (s.hasMoreTokens()) {
					g.drawString(s.nextToken(), r.x + skin.toolTip.insets.left,
							r.y + lines * (fm.getAscent() + fm.getDescent())
									+ skin.toolTip.insets.top); // +nullpointerexception
					lines++;
				}
			}
		} else if ("spinbox" == classname) {
			paintField(g, clipx, clipy, clipwidth, clipheight, component,
					bounds.width - skin.spinBox.pieceWidth, bounds.height,
					inside, pressed, focus, enabled, false, 0, skin.textField);
			int state = enabled ? 0 : 3;
			if (inside && (insidepart == "up"))
				state = 2;
			if (pressed && (pressedpart == "up"))
				state = 1;
			skin.spinBox.paintBody(g, state, bounds.width
					- skin.spinBox.pieceWidth, 0, skin.spinBox.pieceWidth,
					skin.spinBox.pieceHeight);
			state = enabled ? 0 : 3;
			if (inside && (insidepart == "down"))
				state = 2;
			if (pressed && (pressedpart == "down"))
				state = 1;
			skin.spinBox.paintBody(g, state + 4, bounds.width
					- skin.spinBox.pieceWidth, skin.spinBox.pieceHeight,
					skin.spinBox.pieceWidth, skin.spinBox.pieceHeight);

		} else if ("progressbar" == classname) {
			int minimum = getInteger(component, "minimum", 0);
			int maximum = getInteger(component, "maximum", 100);
			int value = getInteger(component, "value", 0);
			boolean horizontal = ("vertical" != get(component, "orientation"));
			int length = (value - minimum)
					* ((horizontal ? bounds.width : bounds.height))
					/ (maximum - minimum);
			int d = (horizontal) ? 0 : 4;
			skin.progressBar.paintBody(g, d + (enabled ? 0 : 2), 0, 0,
					bounds.width, bounds.height, true);
			skin.progressBar.paintBody(g, d + (enabled ? 1 : 3), 0,
					horizontal ? 0 : bounds.height - length,
					horizontal ? length : bounds.width,
					horizontal ? bounds.height : length, true);
		} else if ("slider" == classname) {
			int minimum = getInteger(component, "minimum", 0);
			int maximum = getInteger(component, "maximum", 100);
			int value = getInteger(component, "value", 0);
			boolean horizontal = ("vertical" != get(component, "orientation"));
			int length = (value - minimum)
					* ((horizontal ? bounds.width
							- skin.sliderKnobHoriz.pieceWidth : bounds.height
							- skin.sliderKnobVert.pieceHeight))
					/ (maximum - minimum);
			int bstate = enabled ? 0 : 1;
			int state = enabled ? 0 : 2; // todo: knob hover, pressed etc...
			if (horizontal) {
				skin.sliderBody.paintBody(g, bstate, 0, bounds.height / 2
						- skin.sliderBody.pieceHeight / 2, bounds.width,
						skin.sliderBody.pieceHeight);
				skin.sliderKnobHoriz.paintBody(g, state, length, 0,
						skin.sliderKnobHoriz.pieceWidth, bounds.height);
			} else {
				skin.sliderBody.paintBody(g, bstate + 2, bounds.width / 2
						- skin.sliderBody.pieceWidth / 2, 0,
						skin.sliderBody.pieceWidth, bounds.height);
				skin.sliderKnobVert.paintBody(g, state, 0, length,
						bounds.width, skin.sliderKnobVert.pieceHeight);
			}
		} else if ("splitpane" == classname) {
			if (!getBoolean(component, "transparent")) {
				boolean horizontal = ("vertical" != get(component,
						"orientation"));
				int divider = getInteger(component, "divider", -1);
				int xy = horizontal ? bounds.height : bounds.width;
				int delta = horizontal ? skin.splitVert.delta
						: skin.splitHoriz.delta;
				int xy1 = Math.max(0, xy / 2 - delta);
				int xy2 = Math.min(xy / 2 + delta, xy);
				if (horizontal) {
					skin.splitVert.paintBody(g, 0, divider, 0,
							skin.splitVert.pieceWidth, bounds.height);
					skin.splitVert.paintBody(g, 1, divider, xy1,
							skin.splitVert.pieceWidth, xy2 - xy1);
				} else {
					skin.splitHoriz.paintBody(g, 0, 0, divider, bounds.width,
							skin.splitHoriz.pieceHeight);
					skin.splitHoriz.paintBody(g, 1, xy1, divider, xy2 - xy1,
							skin.splitHoriz.pieceHeight);
				}
			}
			Object comp1 = get(component, ":comp");
			if (comp1 != null) {
				paint(g, clipx, clipy, clipwidth, clipheight, comp1, enabled);
				Object comp2 = get(comp1, ":next");
				if (comp2 != null) {
					paint(g, clipx, clipy, clipwidth, clipheight, comp2,
							enabled);
				}
			}
		} else if (("list" == classname) || ("table" == classname)
				|| ("tree" == classname)) {
			paintScroll(component, classname, bounds, pressed, inside, focus,
					enabled, g, clipx, clipy, clipwidth, clipheight);
		} else if ("separator" == classname) {
			skin.separator.paintBody(g, enabled ? 0 : 1, 0, 0, bounds.width,
					bounds.height);
		} else if ("menubar" == classname) {
			Object selected = get(component, "selected");
			int lastx = 0;
			for (Object menu = get(component, ":comp"); menu != null; menu = get(
					menu, ":next")) {
				Rectangle mb = getRectangle(menu, "bounds");
				if (clipx + clipwidth <= mb.x) {
					break;
				}
				if (clipx >= mb.x + mb.width) {
					continue;
				}
				boolean menuenabled = enabled
						&& getBoolean(menu, "enabled", true);
				boolean armed = (selected == menu);
				boolean hover = (selected == null) && (insidepart == menu);

				// skinlet:
				int state = (menuenabled) ? ThinletSkin.Piece.mbNormal
						: ThinletSkin.Piece.mbDisabled;
				if (armed)
					state = ThinletSkin.Piece.mbSelected;
				if (hover)
					state = ThinletSkin.Piece.mbHovered;

				skin.menuBar.paintBody(g, state, mb.x, 0, mb.width,
						bounds.height);
				paintContent(menu, g, clipx, clipy, clipwidth, clipheight, mb.x
						+ skin.menuBar.insets.left, skin.menuBar.insets.top,
						mb.width - skin.menuBar.insets.lr, bounds.height
								- skin.menuBar.insets.tb, skin.menuBar
								.getTextColor(state, fg), "center", true);
				lastx = mb.x + mb.width;
			}
			// the rest of menu bar:
			skin.menuBar.paintBody(g, ThinletSkin.Piece.mbNormal, lastx, 0,
					bounds.width - lastx, bounds.height);
		} else if (":popup" == classname) {
			skin.popUp.paintBody(g, 0, 0, 0, bounds.width, bounds.height);
			Object selected = get(component, "selected");
			for (Object menu = get(get(component, "menu"), ":comp"); menu != null; menu = get(
					menu, ":next")) {
				Rectangle r = getRectangle(menu, "bounds");
				if (clipy + clipheight <= r.y) {
					break;
				}
				if (clipy >= r.y + r.height) {
					continue;
				}
				String itemclass = getClass(menu);
				if (itemclass == "separator") {
					skin.menuItem.paintBody(g, ThinletSkin.Piece.miSeparator,
							r.x + skin.menuItem.borderInsets.left, r.y,
							bounds.width - skin.menuItem.borderInsets.lr,
							r.height, true, false, false, false, false, false,
							null);
				} else {
					boolean armed = (selected == menu);
					boolean menuenabled = getBoolean(menu, "enabled", true);
					// skinlet:
					int state = (menuenabled) ? ThinletSkin.Piece.mbNormal
							: ThinletSkin.Piece.mbDisabled;
					if (armed)
						state = ThinletSkin.Piece.mbSelected;
					skin.menuItem.paintBody(g, state, r.x, r.y, bounds.width,
							r.height);
					int tx = r.x;
					if (itemclass == "checkboxmenuitem") {
						tx += skin.checkBox.pieceWidth + skin.checkBox.delta;
						boolean checked = getBoolean(menu, "selected", false);
						String group = getString(menu, "group", null);
						int cstate = 0;
						if (menuenabled) {
							cstate = (checked) ? ThinletSkin.Piece.cbNormalChecked
									: ThinletSkin.Piece.cbNormalUnchecked;
						} else {
							cstate = (checked) ? ThinletSkin.Piece.cbDisabledChecked
									: ThinletSkin.Piece.cbDisabledUnchecked;
						}
						int dx = skin.menuItem.insets.left;
						int dy = (r.height - skin.checkBox.pieceHeight) / 2;
						if (group == null) {
							skin.checkBox.paintBody(g, cstate, r.x + dx, r.y
									+ dy);
						} else
							skin.radioButton.paintBody(g, cstate, r.x + dx, r.y
									+ dy);
					}

					paintContent(menu, g, clipx, clipy, clipwidth, clipheight,
							tx + skin.menuItem.insets.left, r.y
									+ skin.menuItem.insets.top, bounds.width
									- skin.menuItem.insets.lr, r.height
									- skin.menuItem.insets.tb, skin.menuItem
									.getTextColor(state, fg), "left", true);

					if (itemclass == "menu") {
						paintArrow(g, r.x + bounds.width
								- skin.arrows.pieceWidth
								- skin.menuItem.borderInsets.right, r.y
								+ (r.height - skin.arrows.pieceHeight) / 2,
								'E', state);
					} else {
						String accelerator = getAccelerator(menu);
						if (accelerator != null) {
							g.drawString(accelerator, bounds.width
									- 4
									- getFontMetrics(font).stringWidth(
											accelerator), r.y + 2 + 10);
						}
					}
				}
			}
			// } else if ( "bean" == classname ) {
			// g.clipRect( 0, 0, bounds.width, bounds.height );
			// ( (Component) get( component, "bean" ) ).paint( g );
			// g.setClip( clipx, clipy, clipwidth, clipheight );
		} else
			throw new IllegalArgumentException(classname);
		g.translate(-bounds.x, -bounds.y);
		clipx += bounds.x;
		clipy += bounds.y;
	}

	/**
	 * @param component
	 *            menubar or :popup
	 * @return the created popupmenu
	 */
	protected Object popupMenu(Object component) {
		if (!skin.loaded()) {
			return super.popupMenu(component);
		}
		
		Object popup = get(component, ":popup"); // first :popup child
		Object selected = get(component, "selected"); // selected menu in of
														// the component
		if (popup != null) { // remove its current :popup
			if (get(popup, "menu") == selected) {
				return null;
			} // but the currect one
			set(popup, "selected", null);
			set(popup, "menu", null);
			repaint(popup);
			removeItemImpl(content, popup);
			set(popup, ":parent", null);
			set(component, ":popup", null);
			if (mouseinside == popup) {
				// TODO: Was a missing argument here
				checkLocation(component);
			}
			popupMenu(popup); // remove recursively
		}
		// pop up the selected menu only
		if ((selected == null) || (getClass(selected) != "menu")) {
			return null;
		}
		// create the :popup, :popup.menu -> menu,
		// menubar|:popup.:popup -> :popup, menubar|:popup.selected -> menu
		popup = createImpl(":popup");
		set(popup, "menu", selected);
		set(component, ":popup", popup);
		insertItem(content, ":comp", popup, 0);
		set(popup, ":parent", content);
		// layout menuitems and calculate :popup content size
		int pw = 0;
		int ph = 0;
		for (Object item = get(selected, ":comp"); item != null; item = get(
				item, ":next")) {
			String itemclass = getClass(item);
			Dimension d = (itemclass == "separator") ? new Dimension(
					skin.menuItem.insets.top, skin.menuItem.insets.top)
					: getSize(item, skin.menuItem.insets.lr,
							skin.menuItem.insets.tb);
			if (itemclass == "checkboxmenuitem")
				d.width += skin.checkBox.pieceWidth + skin.checkBox.delta;
			if (itemclass == "menu")
				d.width += skin.arrows.pieceWidth;
			// add accelerator width
			String accelerator = getAccelerator(item);
			if (accelerator != null) {
				d.width += 4 + getFontMetrics(font).stringWidth(accelerator); // +
																				// font,
																				// height
																				// and
																				// gap
			}

			setRectangle(item, "bounds", 0, ph, d.width, d.height);
			pw = Math.max(pw, d.width);
			ph += d.height;
		}
		// calculates the bounds of the previous menubar/:popup relative to the
		// root desktop
		int menux = 0, menuy = 0, menuwidth = 0, menuheight = 0;
		for (Object comp = component; comp != content; comp = getParent(comp)) {
			Rectangle r = getRectangle(comp, "bounds");
			menux += r.x;
			menuy += r.y;
			if (comp == component) {
				menuwidth = r.width;
				menuheight = r.height;
			}
		}
		// set :popup bounds
		String classname = getClass(component);
		Rectangle menubounds = getRectangle(selected, "bounds");
		if ("menubar" == classname) { // below or above the menubar
			boolean below = getString(component, "placement", "bottom") == "bottom";
			popupowner = component;
			if (below) {
				setRectangle(popup, "bounds", menux + menubounds.x, menuy
						+ menuheight - skin.menuBar.delta, pw
						+ skin.menuBar.delta, ph);
			} else {
				setRectangle(popup, "bounds", menux + menubounds.x, menuy - ph,
						pw + skin.menuBar.delta, ph);
			}
		} else { // right to the previous :popup
			setRectangle(popup, "bounds", menux + menuwidth
					- skin.menuBar.delta, menuy + menubounds.y, pw
					+ skin.menuBar.delta, ph);
		}
		repaint(popup);
		return popup;
	}

	protected void popupPopup(Object popupmenu, int x, int y) {
		// :popup.menu -> popupmenu, popupmenu.:popup -> :popup
		Object popup = createImpl(":popup");
		set(popup, "menu", popupmenu);
		set(popupmenu, ":popup", popup);
		// add :popup to the root desktop and set the combobox as popupowner
		popupowner = popupmenu;
		insertItem(content, ":comp", popup, 0);
		set(popup, ":parent", content);
		// lay out
		int pw = 0;
		int ph = 0;
		for (Object item = get(popupmenu, ":comp"); item != null; item = get(
				item, ":next")) {
			String itemclass = getClass(item);
			Dimension d = (itemclass == "separator") ? new Dimension(
					skin.menuItem.insets.top, skin.menuItem.insets.top)
					: getSize(item, skin.menuItem.insets.lr,
							skin.menuItem.insets.tb);
			if (itemclass == "checkboxmenuitem")
				d.width += skin.checkBox.pieceWidth + skin.checkBox.delta;

			setRectangle(item, "bounds", 0, ph, d.width, d.height);
			pw = Math.max(pw, d.width);
			ph += d.height;
		}
		// if popup would fall outside desktop bounds, shift it
		Rectangle bounds = getRectangle(content, "bounds");
		if (x + pw + skin.menuBar.delta > bounds.x + bounds.width)
			x -= (pw + skin.menuBar.delta);
		if (y + ph + skin.menuBar.delta > bounds.y + bounds.height)
			y -= (ph + skin.menuBar.delta);
		// set :popup bounds
		setRectangle(popup, "bounds", x, y, pw, ph);
		repaint(popup);
	}

	/**
	 * Pop up the list of choices for the given combobox
	 * 
	 * @param combobox -
	 * @return the created combolist
	 */
	protected Object popupCombo(Object combobox) {
		if (!skin.loaded()) {
			return super.popupCombo(combobox);
		}
		
		// combobox bounds relative to the root desktop
		int combox = 0, comboy = 0, combowidth = 0, comboheight = 0;
		for (Object comp = combobox; comp != content; comp = getParent(comp)) {
			Rectangle r = getRectangle(comp, "bounds");
			combox += r.x;
			comboy += r.y;
			if (comp == combobox) {
				combowidth = r.width;
				comboheight = r.height;
			}
		}
		// :combolist -> combobox and combobox -> :combolist
		Object combolist = createImpl(":combolist");
		set(combolist, "combobox", combobox);
		set(combobox, ":combolist", combolist);
		// add :combolist to the root desktop and set the combobox as popupowner
		popupowner = combobox;
		insertItem(content, ":comp", combolist, 0);
		set(combolist, ":parent", content);
		// lay out choices verticaly and calculate max width and height sum
		int pw = 0;
		int ph = 0;
		for (Object item = get(combobox, ":comp"); item != null; item = get(
				item, ":next")) {
			Dimension d = getSize(item, skin.menuItem.insets.lr,
					skin.menuItem.insets.tb);
			setRectangle(item, "bounds", 0, ph, d.width, d.height);
			pw = Math.max(pw, d.width);
			ph += d.height;
		}
		// set :combolist bounds
		int listy = 0, listheight = 0;
		int bellow = getRectangle(content, "bounds").height - comboy
				- comboheight;
		if ((ph > bellow) && (comboy > bellow)) { // popup above combobox
			listy = Math.max(0, comboy - ph);
			listheight = Math.min(comboy, ph);
		} else { // popup bellow combobox
			listy = comboy + comboheight;
			listheight = Math.min(bellow, ph);
		}
		setRectangle(combolist, "bounds", combox, listy, combowidth, listheight);
		layoutScroll(combolist, pw, ph, 0, 0, 0, 0, true, 0);
		repaint(combolist);
		// hover the selected item
		int selected = getInteger(combobox, "selected", -1);
		setInside(combolist, (selected != -1) ? getItem(combobox, selected)
				: null, true);
		return combolist;
	}

	protected void paintField(ThingleGraphics g, int clipx, int clipy,
			int clipwidth, int clipheight, Object component, int width,
			int height, boolean inside, boolean pressed, boolean focus,
			boolean enabled, boolean hidden, int left, Piece piece) {

		int state = ThinletSkin.Piece.tfNormal;
		if (focus)
			state = ThinletSkin.Piece.tfSelected;
		if (!enabled)
			state = ThinletSkin.Piece.tfDisabled;
		if (!getBoolean(component, "transparent")) {
			piece.paintBody(g, state, 0, 0, width, height, (ThingleColor) get(
					component, "background"));
		}
		g.setClip(piece.insets.left + left, piece.insets.top, width - left
				- piece.insets.lr, height - piece.insets.tb);

		String text = getString(component, "text", "");
		
		int offset = getInteger(component, ":offset", 0);
		ThingleFont currentfont = (ThingleFont) get(component, "font");
		if (currentfont != null) {
			g.setFont(currentfont);
		}
		FontMetrics fm = getFontMetrics(g.getFont());
		ThingleColor fg = (ThingleColor) get(component, "foreground");

		int caret = 0;
		if (focus) {
			int start = getInteger(component, "start", 0);
			int end = getInteger(component, "end", 0);
			caret = hidden ? (fm.charWidth('*') * end) : fm.stringWidth(text
					.substring(0, end));
			if (start != end) {
				int is = hidden ? (fm.charWidth('*') * start) : fm
						.stringWidth(text.substring(0, start));
				g.setColor(piece.getTextColor(1, null));
				g.fillRect(piece.insets.left + left - offset
						+ Math.min(is, caret), 1, Math.abs(caret - is),
						height - 2);
			}
		}

		if (focus) {
			g.setColor(piece.getTextColor(2, null));
			g.fillRect(piece.insets.left + left - offset + caret, 1, 1,
					height - 2);
		}
		g.setColor(piece.getTextColor(enabled ? 0 : 3, enabled ? fg : null));
		int fx = piece.insets.left + left - offset;
		int fy = (height - fm.getHeight()) / 2;
		if (hidden) {
			int fh = fm.charWidth('*');
			for (int i = text.length(); i > 0; i--) {
				g.drawString("*", fx, fy);
				fx += fh;
			}
		} else {
			g.drawString(text, fx, fy);
		}
		if (currentfont != null) {
			g.setFont(font);
		}
		g.setClip(clipx, clipy, clipwidth, clipheight);
	}

	/**
	 * @param component -
	 */
	protected void doLayout(Object component) {
		if (!skin.loaded()) {
			super.doLayout(component);
			return;
		}
		
		String classname = getClass(component);
		if ("combobox" == classname) {
			if (getBoolean(component, "editable", true)) {
				ThingleImage icon = getIcon(component, "icon", null);
				layoutField(component, block, false, (icon != null) ? icon
						.getWidth() : 0);
			} // set editable -> validate (overwrite textfield repaint)
			else {
				int selected = getInteger(component, "selected", -1);
				if (selected != -1) { // ...
					Object choice = getItem(component, selected);
					set(component, "text", get(choice, "text"));
					set(component, "icon", get(choice, "icon"));
				}
			}
		} else if (("textfield" == classname) || ("passwordfield" == classname)) {
			layoutField(component, 0, ("passwordfield" == classname), 0);
		} else if ("textarea" == classname) {
			String text = getString(component, "text", "");
			int start = getInteger(component, "start", 0);
			if (start > text.length()) {
				setInteger(component, "start", start = text.length(), 0);
			}
			int end = getInteger(component, "end", 0);
			if (end > text.length()) {
				setInteger(component, "end", end = text.length(), 0);
			}

			boolean wrap = getBoolean(component, "wrap", false);
			char[] chars = null;
			if (wrap) {
				Rectangle bounds = getRectangle(component, "bounds");
				chars = getChars(component, text, true, bounds.width
						- skin.textField.insets.left
						- skin.textField.insets.right, bounds.height);
				if (chars == null) { // need scrollbars
					chars = getChars(component, text, true, bounds.width
							- skin.textField.insets.left
							- skin.textField.insets.right
							- skin.scrollBarKnob.pieceWidth, 0);
				}
			} else {
				chars = getChars(component, text, false, 0, 0);
			}

			ThingleFont currentfont = (ThingleFont) get(component, "font");
			if (currentfont == null)
				currentfont = skin.fontPool[skin.textField.fonts[0]];
			FontMetrics fm = getFontMetrics(currentfont);
			boolean styled = getBoolean(component, "styled", false);
			int width = 0, height = 0;
			int caretx = 0;
			int carety = 0;
			for (int i = 0, j = 0; j <= chars.length; j++) {
				if ((j == chars.length) || (chars[j] == '\n')) {
					width = Math.max(width, styledCharsWidth(styled,
							currentfont, chars, i, j - i));
					if ((end >= i) && (end <= j)) {
						caretx = styledCharsWidth(styled, currentfont, chars,
								i, end - i);
						carety = height;
					}
					height += fm.getHeight();
					i = j + 1;
				}
			}

			layoutScroll(component, width, height + skin.textField.insets.tb
					- fm.getLeading(), 0, 0, 0, 0, true, 0);
			// autoscroll property != null - scroll to end
			boolean autoscroll = ((String) getProperty(component, "autoscroll")) != null;
			if (!autoscroll)
				scrollToVisible(component, caretx, carety, 2, fm.getAscent()
						+ fm.getDescent() + 2); // ?

			// TODO: Why is this method missing?
			// else
			// scrollToEnd( component, skin.textField.insets.tb, height );
		} else if ("tabbedpane" == classname) {
			// tabbedpane (not selected) tab padding are 1, 3, 1, and 3 pt
			Rectangle bounds = getRectangle(component, "bounds");
			String placement = getString(component, "placement", "top");
			boolean horizontal = ((placement == "top") || (placement == "bottom"));
			boolean stacked = (placement == "stacked");
			int selected = getInteger(component, "selected", 0);
			int cnt = getItemCountImpl(component, ":comp");
			// draw up tabs in row/column
			int tabd = 0;
			Rectangle first = null; // x/y location of tab left/top
			int tabsize = 0; // max height/width of tabs
			for (Object tab = get(component, ":comp"); tab != null; tab = get(
					tab, ":next")) {
				Dimension d = getSize(tab, skin.tabItem.insets.lr,
						skin.tabItem.insets.tb);
				if ((tabd == 0)
						&& ((first = getRectangle(tab, "bounds")) != null)) {
					tabd = horizontal && !stacked ? first.x : first.y; // restore
																		// previous
																		// offset
				}
				setRectangle(tab, "bounds", horizontal ? tabd : 0,
						horizontal ? 0 : tabd, d.width, d.height);
				tabd += (horizontal ? d.width : d.height);
				tabsize = Math.max(tabsize, (horizontal || stacked) ? d.height
						: d.width);
			}
			// match tab height/width, set tab content size
			int cx = (placement == "left") ? tabsize : 0;
			int cy = (placement == "top") ? tabsize : 0;
			int cwidth = bounds.width - ((horizontal || stacked) ? 0 : tabsize);
			int cheight = bounds.height
					- (horizontal ? tabsize : stacked ? (tabsize) * cnt : 0);
			if (cheight < 0)
				cheight = 0;
			tabd = tabsize;
			int i = 0;
			for (Object tab = get(component, ":comp"); tab != null; tab = get(
					tab, ":next")) {
				Rectangle r = getRectangle(tab, "bounds");
				if (r == null) {
					r = new Rectangle();
					set(tab, "bounds", r);
				}
				if (horizontal || stacked) {
					if (placement == "bottom") {
						r.y = bounds.height - tabsize;
					} else if (stacked) {
						r.y = tabd;
						r.x = 0;
						r.width = cwidth; // full width
						tabd += tabsize;
						if (i == selected)
							tabd += cheight;
						cy += tabsize;
						i++;
					}
					r.height = tabsize;
				} else {
					if (placement == "right") {
						r.x = bounds.width - tabsize;
					}
					r.width = tabsize;
				}

				Object comp = get(tab, ":comp"); // relative to the tab
													// location
				if ((comp != null) && getBoolean(comp, "visible", true)) {
					setRectangle(comp, "bounds", cx - r.x
							+ skin.tabPane.insets.left, cy - r.y
							+ skin.tabPane.insets.top, cwidth
							- skin.tabPane.insets.lr, cheight
							- skin.tabPane.insets.tb);
					doLayout(comp);
				}
			}
			checkOffset(component);
		} else if (("panel" == classname) || (classname == "dialog")) {
			int gap = getInteger(component, "gap", 0);
			int[][] grid = getGrid(component);
			int top = 0; int left = 0;
			int contentwidth = 0; int contentheight = 0;
			if (grid != null) { // has subcomponents
				top = getInteger(component, "top", 0);
				left = getInteger(component, "left", 0);
				int bottom = getInteger(component, "bottom", 0);
				int right = getInteger(component, "right", 0);
				
				ThingleColor back = getThinletColor(component, "background");
				if ((back == null) || (back.getAlpha() != 0)) {
					left += skin.panel.insets.left;
					top += skin.panel.insets.top;
					bottom += skin.panel.insets.bottom;
					right += skin.panel.insets.right;
				}
				
				// sums the preferred size of cell widths and heights, gaps
				contentwidth = left + getSum(grid[0], 0, grid[0].length, gap, false) + right;
				contentheight = top + getSum(grid[1], 0, grid[1].length, gap, false) + bottom;
			}
			
			int titleheight = getSize(component, 0, 0).height; // title text and icon
			setInteger(component, ":titleheight", titleheight, 0);
			boolean scrollable = getBoolean(component, "scrollable", false);
			boolean border = ("panel" == classname) && getBoolean(component, "border", false);
			int iborder = (border ? 1 : 0);
			if (scrollable) { // set scrollpane areas
				if ("panel" == classname) {
					int head = titleheight / 2;
					int headgap = (titleheight > 0) ? (titleheight - head - iborder) : 0;
					scrollable = layoutScroll(component, contentwidth, contentheight,
						head, 0, 0, 0, border, headgap);
				}
				else { // dialog
					scrollable = layoutScroll(component, contentwidth, contentheight,
						3 + titleheight, 3, 3, 3, true, 0);
				}
			}
			if (!scrollable) { // clear scrollpane bounds //+
				set(component, ":view", null); set(component, ":port", null);
			}
			
			if (grid != null) {
				int areax = 0; int areay = 0; int areawidth = 0; int areaheight = 0;
				if (scrollable) {
					// components are relative to the viewport
					Rectangle view = getRectangle(component, ":view");
					areawidth = view.width; areaheight = view.height;
				}
				else { // scrollpane isn't required
					// components are relative to top/left corner
					Rectangle bounds = getRectangle(component, "bounds");
					areawidth = bounds.width; areaheight = bounds.height;
					if ("panel" == classname) {
						areax = iborder; areay = Math.max(iborder, titleheight);
						areawidth -= 2 * iborder; areaheight -= areay + iborder;
					}
					else { // dialog
						areax = 4; areay = 4 + titleheight;
						areawidth -= 8; areaheight -= areay + 4;
					}
				}
			
				for (int i = 0; i < 2; i++) { // i=0: horizontal, i=1: vertical
					// remaining space
					int d = ((i == 0) ? (areawidth - contentwidth) : (areaheight - contentheight));
					if (d != 0) { //+ > 0
						int w = getSum(grid[2 + i], 0, grid[2 + i].length, 0, false);
						if (w > 0) {
							for (int j = 0; j < grid[i].length; j++) {
								if (grid[2 + i][j] != 0) {
									grid[i][j] += d * grid[2 + i][j] / w;
								}
							}
						}
					}
				}
				
				Object comp = get(component, ":comp");
				for (int i = 0; comp != null; comp = get(comp, ":next")) {
					if (!getBoolean(comp, "visible", true)) { continue; }
					int ix = areax + left + getSum(grid[0], 0, grid[4][i], gap, true);
					int iy = areay + top + getSum(grid[1], 0, grid[5][i], gap, true);
					int iwidth = getSum(grid[0], grid[4][i], grid[6][i], gap, false);
					int iheight = getSum(grid[1], grid[5][i], grid[7][i], gap, false);
					String halign = getString(comp, "halign", "fill");
					String valign = getString(comp, "valign", "fill");
					if ((halign != "fill") || (valign != "fill")) {
						Dimension d = getPreferredSize(comp);
						if (halign != "fill") {
							int dw = Math.max(0, iwidth - d.width);
							if (halign == "center") { ix += dw / 2; }
								else if (halign == "right") { ix += dw; }
							iwidth -= dw;
						}
						if (valign != "fill") {
							int dh = Math.max(0, iheight - d.height);
							if (valign == "center") { iy += dh / 2; }
								else if (valign == "bottom") { iy += dh; }
							iheight -= dh;
						}
					}
					setRectangle(comp, "bounds", ix, iy, iwidth, iheight);
					doLayout(comp);
					i++;
				}
			}
		} else if ("desktop" == classname) {
			Rectangle bounds = getRectangle(component, "bounds");
			for (Object comp = get(component, ":comp"); comp != null; comp = get(
					comp, ":next")) {
				String iclass = getClass(comp);
				if (iclass == "dialog") {
					Dimension d = getPreferredSize(comp);
					if (get(comp, "bounds") == null)
						setRectangle(comp, "bounds", Math.max(0,
								(bounds.width - d.width) / 2), Math.max(0,
								(bounds.height - d.height) / 2), Math.min(
								d.width, bounds.width), Math.min(d.height,
								bounds.height));
				} else if ((iclass == ":combolist") || (iclass == ":popup")) {
					iclass = iclass; // compiler bug
				} else {
					setRectangle(comp, "bounds", 0, 0, bounds.width,
							bounds.height);
				}
				doLayout(comp);
			}
		} else if ("spinbox" == classname) {
			layoutField(component, skin.spinBox.pieceWidth, false, 0);
		} else if ("splitpane" == classname) {
			Rectangle bounds = getRectangle(component, "bounds");
			boolean horizontal = ("vertical" != get(component, "orientation"));
			int size = !horizontal ? skin.splitHoriz.pieceHeight
					: skin.splitVert.pieceWidth;
			int divider = getInteger(component, "divider", -1);
			// rcs: add 5 so we can hide the split bar by setting divider to
			// outside position
			int maxdiv = Math.max(0, (horizontal ? bounds.width + size
					: bounds.height + size)
					- size);

			Object comp1 = get(component, ":comp");
			boolean visible1 = (comp1 != null)
					&& getBoolean(comp1, "visible", true);
			if (divider == -1) {
				int d1 = 0;
				if (visible1) {
					Dimension d = getPreferredSize(comp1);
					d1 = horizontal ? d.width : d.height;
				}
				divider = Math.min(d1, maxdiv);
				setInteger(component, "divider", divider, -1);
			} else if (divider > maxdiv) {
				setInteger(component, "divider", divider = maxdiv, -1);
			}

			if (visible1) {
				setRectangle(comp1, "bounds", 0, 0, horizontal ? divider
						: bounds.width, horizontal ? bounds.height : divider);
				doLayout(comp1);
			}
			Object comp2 = (comp1 != null) ? get(comp1, ":next") : null;
			if ((comp2 != null) && getBoolean(comp2, "visible", true)) {
				setRectangle(comp2, "bounds",
						horizontal ? (divider + size) : 0, horizontal ? 0
								: (divider + size), horizontal ? (bounds.width
								- size - divider) : bounds.width,
						horizontal ? bounds.height
								: (bounds.height - size - divider));
				doLayout(comp2);
			}
		} else if (("list" == classname) || ("table" == classname)
				|| ("tree" == classname)) {
			int line = getBoolean(component, "line", true) ? skin.table.delta
					: 0;
			int width = 0;
			int columnheight = 0;
			if ("table" == classname) {
				Object header = get(component, "header");
				int[] columnwidths = null;
				if (header != null) {
					columnwidths = new int[getCount(header)];
					Object column = get(header, ":comp");
					for (int i = 0; i < columnwidths.length; i++) {
						if (i != 0) {
							column = get(column, ":next");
						}
						columnwidths[i] = getInteger(column, "width", c76);
						width += columnwidths[i];
						Dimension d = getSize(column,
								skin.tableHeader.insets.lr,
								skin.tableHeader.insets.tb);
						columnheight = Math.max(columnheight, d.height);
					}
				}
				set(component, ":widths", columnwidths);
			}
			int y = 0;
			int level = 0;
			for (Object item = get(component, ":comp"); item != null;) {
				int x = 0;
				int iwidth = 0;
				int iheight = 0;
				if ("table" == classname) {
					iwidth = width;
					for (Object cell = get(item, ":comp"); cell != null; cell = get(
							cell, ":next")) {
						Dimension d = getSize(cell, skin.tableItem.insets.lr,
								skin.tableItem.insets.tb);
						iheight = Math.max(iheight, d.height);
					}
				} else {
					if ("tree" == classname) {
						x = (level + 1) * skin.treeNode.pieceWidth;
					}
					Dimension d = getSize(item, skin.tableItem.insets.lr,
							skin.tableItem.insets.tb);
					iwidth = d.width;
					iheight = d.height;
					width = Math.max(width, x + d.width);
				}
				setRectangle(item, "bounds", x, y, iwidth, iheight);
				y += iheight + line;
				if ("tree" == classname) {
					Object next = get(item, ":comp");
					if ((next != null) && getBoolean(item, "expanded", true)) {
						level++;
					} else {
						while (((next = get(item, ":next")) == null)
								&& (level > 0)) {
							item = getParent(item);
							level--;
						}
					}
					item = next;
				} else {
					item = get(item, ":next");
				}
			}
			layoutScroll(component, width + skin.table.insets.lr, y
					+ skin.table.insets.tb - line, columnheight, 0, 0, 0, true,
					0);

		} else if ("menubar" == classname) {
			int x = 0;
			for (Object menu = get(component, ":comp"); menu != null; menu = get(
					menu, ":next")) {
				Dimension d = getSize(menu, skin.menuBar.insets.lr,
						skin.menuBar.insets.tb);
				setRectangle(menu, "bounds", x, 0, d.width, d.height);
				x += d.width;
			}
		}
		// else if ( "bean" == classname ) {
		// Rectangle r = getRectangle( component, "bounds" );
		// ( (Component) get( component, "bean" ) ).setBounds( r );
		// }
	}

	/**
	 * Set viewport (:port) bounds excluding borders, view position and content
	 * size (:view), horizontal (:horizontal), and vertical (:vertical)
	 * scrollbar bounds
	 * 
	 * @param component
	 *            scrollable widget
	 * @param contentwidth
	 *            preferred component width
	 * @param contentheight
	 *            preferred component height
	 * @param top
	 *            top inset (e.g. table header, dialog title, half of panel
	 *            title)
	 * @param left
	 *            left inset (e.g. dialog border)
	 * @param bottom
	 *            bottom inset (e.g. dialog border)
	 * @param right
	 *            right inset (e.g. dialog border)
	 * @param topgap
	 *            (lower half of panel title)
	 * @param border -
	 * @return true if scrollpane is required, otherwise false
	 * 
	 * list: 0, 0, 0, 0, true, 0 | table: header, ... | dialog: header, 3, 3, 3,
	 * true, 0 title-border panel: header / 2, 0, 0, 0, true, head
	 */
	protected boolean layoutScroll(Object component, int contentwidth,
			int contentheight, int top, int left, int bottom, int right,
			boolean border, int topgap) {
		if (!skin.loaded()) {
			return super.layoutScroll(component, contentwidth, contentheight, top, left, bottom, right, border, topgap);
		}
		
		int sbBlock = skin.scrollBarArrow.pieceHeight; // override defaults,
														// bind to bitmap size
														// ...
		Rectangle bounds = getRectangle(component, "bounds");
		int portwidth = bounds.width - left - right; // available horizontal
														// space
		int portheight = bounds.height - top - topgap - bottom; // vertical
																// space
		boolean hneed = contentwidth > portwidth; // horizontal scrollbar
													// required
		boolean vneed = contentheight > portheight - (hneed ? sbBlock : 0); // vertical
																			// scrollbar
																			// needed
		if (vneed) {
			portwidth -= sbBlock;
		} // subtract by vertical scrollbar width
		hneed = hneed || (vneed && (contentwidth > portwidth));
		if (hneed) {
			portheight -= sbBlock;
		} // subtract by horizontal scrollbar height

		setRectangle(component, ":port", left, top + topgap, portwidth,
				portheight);
		if (hneed) {
			setRectangle(component, ":horizontal", left, bounds.height - bottom
					- sbBlock, bounds.width - left - right
					- (vneed ? sbBlock : 0), sbBlock);
		} else {
			set(component, ":horizontal", null);
		}
		if (vneed) {
			setRectangle(component, ":vertical",
					bounds.width - right - sbBlock, top, sbBlock, bounds.height
							- top - bottom - (hneed ? sbBlock : 0));
		} else {
			set(component, ":vertical", null);
		}

		contentwidth = Math.max(contentwidth, portwidth);
		contentheight = Math.max(contentheight, portheight);
		int viewx = 0, viewy = 0;
		Rectangle view = getRectangle(component, ":view");
		if (view != null) { // check the previous location
			viewx = Math.max(0, Math.min(view.x, contentwidth - portwidth));
			viewy = Math.max(0, Math.min(view.y, contentheight - portheight));
		}
		setRectangle(component, ":view", viewx, viewy, contentwidth,
				contentheight);
		return vneed || hneed;
	}

	// scrollbar parts helper
	public int getPartState(String part, boolean enabled, boolean inside,
			boolean pressed) {
		int s = enabled ? 0 : 3;
		if (inside && (insidepart == part))
			s = 2;
		if (pressed && (pressedpart == part))
			s = 1;
		if (part.equals("left"))
			s += 4;
		if (part.equals("right"))
			s += 12;
		if (part.equals("down"))
			s += 8;
		if (part.equals("vknob"))
			s += 4;
		return s;
	}

	protected void paintScroll(Object component, String classname,
			Rectangle bounds, boolean pressed, boolean inside, boolean focus,
			boolean enabled, ThingleGraphics g, int clipx, int clipy,
			int clipwidth, int clipheight) {

		int sbBlock = skin.scrollBarArrow.pieceHeight; // override defaults,
														// bind to bitmap size
														// ...

		Rectangle port = getRectangle(component, ":port");
		Rectangle horizontal = getRectangle(component, ":horizontal");
		Rectangle vertical = getRectangle(component, ":vertical");
		Rectangle view = getRectangle(component, ":view");

		if (horizontal != null) { // paint horizontal scrollbar
			int x = horizontal.x;
			int y = horizontal.y;
			int width = horizontal.width;
			skin.scrollBarArrow.paintBody(g, getPartState("left", enabled,
					inside, pressed), x, y, sbBlock, sbBlock);
			skin.scrollBarArrow.paintBody(g, getPartState("right", enabled,
					inside, pressed), x + width - sbBlock, y, sbBlock, sbBlock);

			int track = width - (2 * sbBlock);
			if (track < skin.scrollBarKnob.pieceWidth) {
				skin.scrollBarShaft.paintBody(g, enabled ? 0 : 2, x + sbBlock,
						y, track, sbBlock);
			} else {
				int knob = Math.max(track * port.width / view.width,
						skin.scrollBarKnob.pieceWidth);
				int decrease = view.x * (track - knob)
						/ (view.width - port.width);
				// left part
				int state = enabled ? 0 : 2; // todo: hover, focus...
				skin.scrollBarShaft.paintBody(g, state, x + sbBlock, y,
						decrease, sbBlock);

				// scbar knob
				skin.scrollBarKnob.paintBody(g, getPartState("hknob", enabled,
						inside, pressed), x + sbBlock + decrease, y, knob,
						sbBlock);
				int cx = (x + sbBlock + decrease) + (knob - sbBlock) / 2;
				skin.scrollBarKnob.paintBody(g, getPartState("hknob", enabled,
						inside, pressed) + 8, cx, y,
						skin.scrollBarKnob.pieceWidth,
						skin.scrollBarKnob.pieceWidth);

				int increase = track - decrease - knob;
				// right part
				skin.scrollBarShaft.paintBody(g, state, x + sbBlock + decrease
						+ knob, y, increase, sbBlock);
			}
		}

		if (vertical != null) { // paint vertical scrollbar
			int x = vertical.x;
			int y = vertical.y;
			int height = vertical.height;
			skin.scrollBarArrow.paintBody(g, getPartState("up", enabled,
					inside, pressed), x, y, sbBlock, sbBlock);
			skin.scrollBarArrow
					.paintBody(g,
							getPartState("down", enabled, inside, pressed), x,
							y + height - sbBlock, sbBlock, sbBlock);

			int track = height - (2 * sbBlock);
			if (track < skin.scrollBarKnob.pieceWidth) {
				skin.scrollBarShaft.paintBody(g, enabled ? 3 : 5, x, y
						+ sbBlock, sbBlock, track);
			} else {
				int knob = Math.max(track * port.height / view.height,
						skin.scrollBarKnob.pieceWidth);
				int decrease = view.y * (track - knob)
						/ (view.height - port.height);
				int state = enabled ? 3 : 5;
				skin.scrollBarShaft.paintBody(g, state, x, y + sbBlock,
						sbBlock, decrease);

				skin.scrollBarKnob.paintBody(g, getPartState("vknob", enabled,
						inside, pressed), x, y + sbBlock + decrease, sbBlock,
						knob);
				int cy = (y + sbBlock + decrease) + (knob - sbBlock) / 2;
				skin.scrollBarKnob.paintBody(g, getPartState("vknob", enabled,
						inside, pressed) + 8, x, cy,
						skin.scrollBarKnob.pieceWidth,
						skin.scrollBarKnob.pieceWidth);

				int increase = track - decrease - knob;

				skin.scrollBarShaft.paintBody(g, state, x, y + sbBlock
						+ decrease + knob, sbBlock, increase);
			}
		}

		boolean hneed = (horizontal != null);
		boolean vneed = (vertical != null);
		boolean border = ("textarea" == classname) ? getBoolean(component,
				"border", true) : true;

		if (("panel" != classname) && ("dialog" != classname)) { // textarea
																	// or table
			int state = ThinletSkin.Piece.tfNormal;
			if (focus)
				state = ThinletSkin.Piece.tfSelected;
			if (!enabled)
				state = ThinletSkin.Piece.tfDisabled;

			// paint body for opaque lists, tables, textareas etc...
			if ("textarea" == classname) {
				if (!getBoolean(component, "transparent"))
					skin.textField.paintBody(g, state, port.x, port.y,
							port.width, port.height, border, border, !hneed
									&& border, !vneed && border,
							(ThingleColor) get(component, "background"));
			} else if (":combolist" == classname) {
				skin.popUp.paintBody(g, state, port.x, port.y, port.width,
						port.height, border, border, !hneed && border, !vneed
								&& border, (ThingleColor) get(component,
								"background"));
			} else { // list, table, tree
				skin.table.paintBody(g, state, port.x, port.y, port.width,
						port.height, border, border, !hneed && border, !vneed
								&& border, (ThingleColor) get(component,
								"background"));
			}

			if ("table" == classname) {
				Object header = get(component, "header");
				if (header != null) {
					int[] columnwidths = (int[]) get(component, ":widths");
					Object column = get(header, ":comp");
					int x = 0;
					for (int i = 0; i < columnwidths.length; i++) {
						if (i != 0) {
							column = get(column, ":next");
						}
						boolean lastcolumn = (i == columnwidths.length - 1);

						int hstate = enabled ? 0 : 3; // todo: pressed, hover
														// etc...
						int width = lastcolumn ? Math.max(port.width - x,
								columnwidths[i]) : columnwidths[i];
						if (x - view.x
								+ Math.min(port.width - (x - view.x), width) > 0
								&& Math.max(0, x - view.x) <= port.width) {
							int truncwidth = (port.width - (x - view.x));
							if (truncwidth > width)
								truncwidth = -1;

							skin.tableHeader.paintBody(g, hstate, x - view.x,
									0, width, port.y, true, true, true,
									!lastcolumn);
							paintContent(
									column,
									g,
									clipx,
									clipy,
									clipwidth,
									clipheight,
									x - view.x + skin.tableHeader.insets.left,
									skin.tableHeader.insets.top,
									width - skin.tableHeader.insets.lr,
									port.y - skin.tableHeader.insets.tb,
									skin.tableHeader.getTextColor(hstate, null),
									"left", false);

							Object sort = get(column, "sort"); // "none",
																// "ascent",
																// "descent"
							if (sort != null) {
								// todo:
								// paintArrow(g, x - view.x + width - block, 0,
								// block, port.y,
								// (sort == "ascent") ? 'S' : 'N');
							}
						}
						x += width;
					}
				}
			}
		}

		/**
		 * 
		 * TODO: scroll This is a *temporary* patch to enable insets for tables,
		 * lists and textareas It works for smaller insets ( 2 pix ), but is not
		 * nice...
		 * 
		 * Should be implemented on layout phase, however this probably involves
		 * messing with mouse and keyboard handlers and/or the scrollToVisible
		 * method of thinlet.java
		 * 
		 * TEMPscrollToVisible has been used to repaint the whole thing and
		 * enable border and background change on focus...
		 * 
		 * for me it is not pretty clear what exactly happens, this needs
		 * further investigation :\
		 * 
		 * 18-aug-03
		 * 
		 */
		Insets delta = skin.table.insets;
		if ("textarea" == classname)
			delta = skin.textField.insets;
		int px = port.x + delta.left, py = port.y + delta.top, pw = port.width
				- delta.right - ((vneed) ? 0 : delta.left), ph = port.height
				- delta.top - ((hneed) ? 0 : delta.bottom), vw = view.width
				- delta.right - ((vneed) ? 0 : delta.left);

		int x1 = Math.max(clipx, px);
		int x2 = Math.min(clipx + clipwidth, px + pw);
		int y1 = Math.max(clipy, py);
		int y2 = Math.min(clipy + clipheight, py + ph);

		if ((x2 > x1) && (y2 > y1)) {
			g.setClip(x1, y1, x2 - x1, y2 - y1);
			g.translate(px - view.x, py - view.y);

			paint(component, classname, focus, enabled, g, view.x - px + x1,
					view.y - py + y1, x2 - x1, y2 - y1, pw, ph, vw);

			g.translate(view.x - px, view.y - py);
			g.setClip(clipx, clipy, clipwidth, clipheight);
		}
	}

	// Paint scrollable content
	protected void paint(Object component, String classname, boolean focus,
			boolean enabled, ThingleGraphics g, int clipx, int clipy,
			int clipwidth, int clipheight, int portwidth, int portheight,
			int viewwidth) {

		// rcs: custom text colors on all components, incl. list items
		ThingleColor fg = (ThingleColor) get(component, "foreground");
		if ("textarea" == classname) {
			char[] chars = (char[]) get(component, ":text");
			int start = focus ? getInteger(component, "start", 0) : 0;
			int end = focus ? getInteger(component, "end", 0) : 0;
			boolean editable = getBoolean(component, "editable", true);
			boolean styled = getBoolean(component, "styled", false);
			int is = Math.min(start, end);
			int ie = Math.max(start, end);
			ThingleFont currentfont = (ThingleFont) get(component, "font");
			if (currentfont == null)
				currentfont = skin.fontPool[skin.textField.fonts[0]];

			ThingleColor currentcolor = skin.textField.getTextColor(enabled ? 0
					: 3, enabled ? fg : null);
			g.setFont(currentfont);
			FontMetrics fm = getFontMetrics(g.getFont());
			int fontascent = fm.getAscent();
			int fontheight = fm.getHeight();
			int ascent = 0;

			int n = chars.length;

			for (int i = 0, j = 0; j <= chars.length; j++) {
				if ((j == chars.length) || (chars[j] == '\n')) {
					if (clipy + clipheight <= ascent) {
						break;
					} // the next lines are bellow paint rectangle
					if (clipy < ascent + fontheight) { // this line is not
														// above painting area
						if (focus && (is != ie) && (ie >= i) && (is <= j)
								&& !getBoolean(component, "transparent")
								&& !styled /* not fully implemented ... */) {
							int xs = (is < i) ? -1
									: ((is > j) ? (viewwidth - 1)
											: styledCharsWidth(styled,
													currentfont, chars, i, is
															- i));
							int xe = ((j != -1) && (ie > j)) ? (viewwidth - 1)
									: styledCharsWidth(styled, currentfont,
											chars, i, ie - i);
							g.setColor(skin.textField.getTextColor(1, null));
							g.fillRect(1 + xs, ascent, xe - xs, fontheight);
						}
						drawStyledChars(g, enabled, styled, currentfont,
								currentcolor, chars, i, j - i, 1, ascent
										+ fontascent);
						if (focus && (end >= i) && (end <= j) && editable
								&& !styled) {
							int caret = styledCharsWidth(styled, currentfont,
									chars, i, end - i);
							g.setColor(skin.textField.getTextColor(2, null));
							g.fillRect(caret, ascent, 1, fontheight);
						}
					}
					ascent += fontheight;
					i = j + 1;
				}
			}
			if (currentfont != null) {
				g.setFont(font);
			}
		} else if (":combolist" == classname) {
			Object lead = get(component, ":lead");
			for (Object choice = get(get(component, "combobox"), ":comp"); choice != null; choice = get(
					choice, ":next")) {
				Rectangle r = getRectangle(choice, "bounds");
				if (clipy + clipheight <= r.y) {
					break;
				}
				if (clipy >= r.y + r.height) {
					continue;
				}
				int mstate = (lead == choice) ? ThinletSkin.Piece.mbSelected
						: ThinletSkin.Piece.mbNormal;
				if (!getBoolean(choice, "enabled", true))
					mstate = ThinletSkin.Piece.mbDisabled;
				skin.menuItem.paintBody(g, mstate, r.x, r.y, portwidth,
						r.height);
				paintContent(choice, g, clipx, clipy, clipwidth, clipheight,
						r.x + skin.menuItem.insets.left, r.y
								+ skin.menuItem.insets.top, portwidth
								- skin.menuItem.insets.lr, r.height
								- skin.menuItem.insets.tb, skin.menuItem
								.getTextColor(mstate, fg), "left", false);
			}
		} else if (("panel" == classname) || ("dialog" == classname)) {
			for (Object comp = get(component, ":comp"); comp != null; comp = get(
					comp, ":next")) {
				paint(g, clipx, clipy, clipwidth, clipheight, comp, enabled);
			}
		} else { // if (("list" == classname) || ("table" == classname) ||
					// ("tree" == classname))
			Object lead = get(component, ":lead");
			int[] columnwidths = ("table" == classname) ? ((int[]) get(
					component, ":widths")) : null;
			boolean line = getBoolean(component, "line", true);
			int iline = line ? skin.table.delta : 0;
			for (Object item = get(component, ":comp"), next = null; item != null; item = next) {
				if (focus && (lead == null)) {
					set(component, ":lead", lead = item); // draw first item
															// focused when lead
															// is null
				}
				Rectangle r = getRectangle(item, "bounds");

				if (r == null) {
					r = new Rectangle();
					set(item, "bounds", r);
				}
				if (clipy + clipheight <= r.y) {
					break;
				} // clip rectangle is above
				boolean subnode = false;
				boolean expanded = false;
				if ("tree" != classname) {
					next = get(item, ":next");
				} else {
					subnode = (next = get(item, ":comp")) != null;
					expanded = subnode && getBoolean(item, "expanded", true);
					if (!expanded) {
						for (Object node = item; (node != component)
								&& ((next = get(node, ":next")) == null); node = getParent(node))
							;
					}
				}
				if (clipy >= r.y + r.height + iline) {
					continue;
				} // clip rectangle is bellow

				boolean selected = getBoolean(item, "selected", false);
				boolean focused = focus && (lead == item);
				/**
				 * invert selection for custom backgrounded list items temp
				 * patch - if you need custom foreground on an item set it as
				 * background otherwise we need to mess with paintContent...? -
				 * todo....
				 */
				ThingleColor cbg = (ThingleColor) get(item, "background");
				ThingleColor ifg = (cbg != null && selected) ? skin.table
						.getFaceColor(1, null) : cbg;
				int istate = (selected) ? ThinletSkin.Piece.mbSelected
						: ThinletSkin.Piece.mbNormal;
				if (!enabled)
					istate = ThinletSkin.Piece.mbDisabled;
				if (selected) {
					skin.tableItem.paintBody(g, ThinletSkin.Piece.mbSelected,
							("tree" != classname) ? 0 : r.x, r.y,
							("tree" != classname) ? viewwidth : r.width,
							r.height, cbg);
				}
				if (line) {
					g.setColor(skin.tableItem.getBorderColor(0, null));
					g.drawLine(0, r.y + r.height, viewwidth, r.y + r.height);
				}
				if ("table" != classname) {
					boolean itemenabled = enabled
							&& getBoolean(item, "enabled", true);
					if (!itemenabled)
						istate = ThinletSkin.Piece.mbDisabled;
					paintContent(item, g, clipx, clipy, clipwidth, clipheight,
							r.x + skin.tableItem.insets.left, r.y
									+ skin.tableItem.insets.top, viewwidth
									- skin.tableItem.insets.lr, r.height
									- skin.tableItem.insets.tb, skin.tableItem
									.getTextColor(istate, ifg), "left", false);
					if ("tree" == classname) {
						int x = r.x - skin.treeNode.pieceWidth;
						int y = r.y + (r.height - skin.treeNode.pieceHeight)
								/ 2;
						if (subnode) {
							int nstate = (expanded) ? 0 : 2;
							if (!itemenabled)
								nstate += 1;
							skin.treeNode.paintBody(g, nstate, x, y);
						}
					}
				} else { // table
					int i = 0;
					int x = 0;
					for (Object cell = get(item, ":comp"); cell != null; cell = get(
							cell, ":next")) {
						if (clipx + clipwidth <= x) {
							break;
						}
						int iwidth = columnwidths[i];
						boolean lastcolumn = (i == columnwidths.length - 1);
						if (lastcolumn) {
							iwidth = Math.max(iwidth, viewwidth - x);
						}
						if (clipx < x + iwidth) {
							boolean cellenabled = enabled
									&& getBoolean(cell, "enabled", true);
							if (!cellenabled)
								istate = ThinletSkin.Piece.mbDisabled;
							paintContent(cell, g, clipx, clipy, clipwidth,
									clipheight, r.x + x
											+ skin.tableItem.insets.left, r.y
											+ skin.tableItem.insets.top, iwidth
											- skin.tableItem.insets.lr,
									r.height - skin.tableItem.insets.tb,
									skin.tableItem.getTextColor(istate, ifg),
									"left", false);
						}
						i++;
						x += iwidth;
					}
				}
			}
		}
	}

	protected void showTip() {
		if (!skin.loaded()) {
			super.showTip();
			return;
		}
		
		String text = null;
		tooltipowner = null;
		String classname = getClass(mouseinside);
		if ((classname == "tabbedpane") || (classname == "menubar")
				|| (classname == ":popup")) {
			if (insidepart != null) {
				text = getString(insidepart, "tooltip", null);
			}
		} else if (classname == ":combolist") {
			if (insidepart instanceof Object[]) {
				text = getString(insidepart, "tooltip", null);
			}
		}
		// list table tree
		if (text == null) {
			text = getString(mouseinside, "tooltip", null);
		} else {
			tooltipowner = insidepart;
		}
		if (text != null) {
			StringTokenizer s = new StringTokenizer(text, "\n");
			int lines = s.countTokens();
			FontMetrics fm = getFontMetrics(font);
			int width = 0;
			while (s.hasMoreTokens()) {
				int tmpw = fm.stringWidth(s.nextToken())
						+ skin.toolTip.insets.lr;
				if (tmpw > width)
					width = tmpw;
			}
			int height = lines * (fm.getAscent() + fm.getDescent())
					+ skin.toolTip.insets.tb;
			;
			if (tooltipowner == null) {
				tooltipowner = mouseinside;
			}
			Rectangle bounds = getRectangle(content, "bounds");
			int tx = Math.max(0, Math.min(mousex + 10, bounds.width - width));
			int ty = Math.max(0, Math.min(mousey + 10, bounds.height - height));
			setRectangle(tooltipowner, ":tooltipbounds", tx, ty, width, height);
			repaint(tx, ty, width, height);
		}
	}

	/**
	 * Paint component icon and text (using default or custom font)
	 * 
	 * @param component -
	 * @param g -
	 * @param clipx -
	 * @param clipy -
	 * @param clipwidth -
	 * @param clipheight -
	 * @param x -
	 * @param y -
	 * @param width -
	 * @param height -
	 * @param foreground
	 *            default text color if custom foreground is null
	 * @param defaultalignment -
	 * @param checkmnemonic
	 *            find mnemonic index and underline text
	 */
	protected void paintContent(Object component, ThingleGraphics g, int clipx,
			int clipy, int clipwidth, int clipheight, int x, int y, int width,
			int height, ThingleColor foreground, String defaultalignment,
			boolean checkmnemonic) {
		String text = getString(component, "text", null);
		boolean styled = getBoolean(component, "styled");
		// rcs: hover icon
		ThingleImage icon = getIcon(component, "icon", null);
		if (mouseinside == component) {
			ThingleImage hicon = getIcon(component, "hicon", null);
			if (hicon != null)
				icon = hicon;
		}

		if ((text == null) && (icon == null)) {
			return;
		}
		boolean link = (getClass(component) == "button")
				&& (getChoice(component, "type") == "link");
		String alignment = getString(component, "alignment", defaultalignment);
		ThingleFont currentfont = (ThingleFont) get(component, "font");
		ThingleColor customfg = (ThingleColor) get(component, "foreground"); // +no
																				// if
																				// disabled
		
		FontMetrics fm = null;
		int tw = 0, th = 0;
		int ta = 0;
		if (text != null) {
			if (currentfont == null)
				currentfont = skin.fontPool[0];
			if (customfg == null)
				customfg = foreground;
			fm = getFontMetrics(currentfont);
			tw = styledStringWidth(styled, currentfont, text);
			ta = fm.getAscent();
			th = fm.getDescent() + ta;
		}
		int iw = 0, ih = 0;
		if (icon != null) {
			iw = icon.getWidth();
			ih = icon.getHeight();
			if (text != null) {
				iw += skin.icon.delta;
			}
		}

		boolean clipped = (tw + iw > width) || (th > height) || (ih > height);
		int cx = x;
		if ("center" == alignment) {
			cx += (width - tw - iw) / 2;
		} else if ("right" == alignment) {
			cx += width - tw - iw;
		}

		if (clipped) {
			// TODO: Consider this used to be clipRect
			g.setClip(x, y, width, height);
		}
		if (icon != null) {
			g.drawImage(icon, cx, y + (height - ih) / 2);
			cx += iw;
		}
		if (text != null) {
			int ty = y + (height - th) / 2 + ta;
			drawStyledString(g, true, styled, currentfont, customfg, text, cx,
					ty);
			if (link)
				g.drawLine(cx, ty + 1, cx + tw, ty + 1);
			if (checkmnemonic) {
				int mnemonic = getInteger(component, "mnemonic", -1);
				if ((mnemonic != -1) && (mnemonic < text.length())) {
					int mx = cx + fm.stringWidth(text.substring(0, mnemonic));
					g.drawLine(mx, ty - 1, mx
							+ fm.charWidth(text.charAt(mnemonic)), ty - 1);
				}
			}
		}
		if (clipped) {
			g.setClip(clipx, clipy, clipwidth, clipheight);
		}
		if (currentfont != null) {
			g.setFont(font);
		} // restore the default font
	}

	protected boolean findComponent(Object component, int x, int y) {
		if (!skin.loaded()) {
			return super.findComponent(component, x, y);
		}
		
		if (component == content) {
			mouseinside = insidepart = null;
			mousex = x;
			mousey = y;
		}
		if (!getBoolean(component, "visible", true)) {
			return false;
		}
		Rectangle bounds = getRectangle(component, "bounds");
		if ((bounds == null) || !(bounds.contains(x, y))) {
			return false;
		}
		mouseinside = component;
		x -= bounds.x;
		y -= bounds.y;
		String classname = getClass(component);

		if ("combobox" == classname) {
			if (getBoolean(component, "editable", true)
					&& (x <= bounds.width - skin.combobutton.pieceWidth)) {
				ThingleImage icon = getIcon(component, "icon", null);
				insidepart = ((icon != null) && (x <= skin.icon.delta
						+ icon.getWidth())) ? "icon" : null;
			} else {
				insidepart = "down";
			}
		} else if (":combolist" == classname) {
			if (!findScroll(component, x, y)) {
				y += getRectangle(component, ":view").y;
				for (Object choice = get(get(component, "combobox"), ":comp"); choice != null; choice = get(
						choice, ":next")) {
					Rectangle r = getRectangle(choice, "bounds");
					if ((y >= r.y) && (y < r.y + r.height)) {
						insidepart = choice;
						break;
					}
				}
			}
		} else if ("textarea" == classname) {
			findScroll(component, x, y);
		} else if ("tabbedpane" == classname) {
			int selected = getInteger(component, "selected", 0);
			int i = 0;
			for (Object tab = get(component, ":comp"); tab != null; tab = get(
					tab, ":next")) {
				Rectangle r = getRectangle(tab, "bounds");
				if (i == selected) {
					Object tabcontent = get(tab, ":comp");
					if ((tabcontent != null)
							&& findComponent(tabcontent, x - r.x, y - r.y)) {
						break;
					}
				}
				if (r.contains(x, y)) {
					insidepart = tab;
					break;
				}
				i++;
			}
		} else if (("panel" == classname) || ("desktop" == classname)
				|| ("dialog" == classname)) {
			if (("dialog" == classname) && (y < skin.titlebar.pieceHeight)) {
				insidepart = "header";
			}
			// rcs: close dialog button
			if (("dialog" == classname)
					&& (y < skin.titlebar.pieceHeight)
					&& (y > skin.titlebar.insets.top)
					&& (x > bounds.width - skin.titlebar.insets.right
							- skin.closeButton.pieceWidth)
					&& (x < bounds.width - skin.titlebar.insets.right)) {
				insidepart = "closebutton";
			} else if (!findScroll(component, x, y)) {
				Rectangle port = getRectangle(component, ":port");
				if (port != null) { // content scrolled
					Rectangle view = getRectangle(component, ":view");
					x += view.x - port.x;
					y += view.y - port.y;
				}
				for (Object comp = get(component, ":comp"); comp != null; comp = get(
						comp, ":next")) {
					if (findComponent(comp, x, y)) {
						break;
					}
					if (("desktop" == classname)
							&& getBoolean(comp, "modal", false)) {
						insidepart = "modal";
						break;
					} // && dialog
				}
			}
		} else if ("spinbox" == classname) {
			insidepart = (x <= bounds.width - skin.spinBox.pieceWidth) ? null
					: ((y <= bounds.height / 2) ? "up" : "down");
		} else if ("splitpane" == classname) {
			Object comp1 = get(component, ":comp");
			if (comp1 != null) {
				if (!findComponent(comp1, x, y)) {
					Object comp2 = get(comp1, ":next");
					if (comp2 != null) {
						findComponent(comp2, x, y);
					}
				}
			}
		} else if ("list" == classname) {
			findScroll(component, x, y);
		} else if ("table" == classname) {
			if (!findScroll(component, x, y)) {
			}
		} else if ("tree" == classname) {
			findScroll(component, x, y);
		} else if ("menubar" == classname) {
			for (Object menu = get(component, ":comp"); menu != null; menu = get(
					menu, ":next")) {
				Rectangle r = getRectangle(menu, "bounds");
				if ((x >= r.x) && (x < r.x + r.width)) {
					insidepart = menu;
					break;
				}
			}
		} else if (":popup" == classname) {
			for (Object menu = get(get(component, "menu"), ":comp"); menu != null; menu = get(
					menu, ":next")) {
				Rectangle r = getRectangle(menu, "bounds");
				if ((y >= r.y) && (y < r.y + r.height)) {
					insidepart = menu;
					break;
				}
			}
		}
		return true;
	}

	protected void paintArrow(ThingleGraphics g, int x, int y, char dir,
			int state) {
		if (dir == 'N') { // north
			skin.arrows.paintBody(g, state, x, y);
		} else if (dir == 'W') { // west
			skin.arrows.paintBody(g, state + 3, x, y);
		} else if (dir == 'S') { // south
			skin.arrows.paintBody(g, state + 6, x, y);
		} else { // east
			skin.arrows.paintBody(g, state + 9, x, y);
		}
	}

	protected Dimension getFieldSize(Object component, Piece container) {
		int columns = getInteger(component, "columns", 0);
		ThingleFont currentfont = (ThingleFont) get(component, "font");
		FontMetrics fm = getFontMetrics((currentfont != null) ? currentfont
				: font);
		return new Dimension(((columns > 0) ? (columns * fm.charWidth('e'))
				: c76)
				+ container.insets.lr, fm.getAscent() + fm.getDescent()
				+ container.insets.tb); // fm.stringWidth(text)
	}

	/**
	 * A replacement to Graphics.drawChars() for rich-text ( styled ) texts
	 * supported codes are `C where C is a single char i.e. `0 = normal, `1 =
	 * bold, `2 = italic, `3 = bold-italic `a, `b, ... `z the colors from
	 * current skin Palette
	 * 
	 * TODO: more advanced implementation - editable rich-text area right now
	 * only useful for buttons, labels etc. and static non-editable textareas -
	 * more calculations need to be done for text selection, cursor position
	 * etc. depending on current font style being used...
	 * 
	 * depends on "richtext" property
	 * 
	 * @param f -
	 *            default font
	 * @param c -
	 *            disabled color
	 */
	public void drawStyledChars(ThingleGraphics g, boolean enabled,
			boolean styled, ThingleFont f, ThingleColor c, char[] chars,
			int offset, int len, int sx, int sy) {

		if (!styled) {
			g.setColor(c);
			g.setFont(f);
			g.drawString(new String(chars).substring(offset, offset+len), sx, sy - f.getLineHeight());
			return;
		}
		if (!enabled)
			g.setColor(c);
		ThingleFont[] fnts = getFNTs(f);
		int s = 0, x = sx, y = sy, i;
		int lastTag = offset;
		for (i = offset; i < offset + len; i++) {
			if ((chars[i] == '`') && (i + 1 < (offset + len))) {
				g.drawString(new String(chars).substring(lastTag, i),
						x, y - g.getFont().getLineHeight());
				x += getFontMetrics(g.getFont()).charsWidth(chars, lastTag,
						i - lastTag);
				switch (chars[i + 1]) {
				case '0':
					g.setFont(fnts[0]);
					break;
				case '1':
					g.setFont(fnts[1]);
					break;
				case '2':
					g.setFont(fnts[2]);
					break;
				case '3':
					g.setFont(fnts[3]);
					break;
				default: // colors
					if ((enabled) && (chars[i + 1] >= 'a')
							&& (chars[i + 1] <= 'z'))
						g.setColor(skin.palette[chars[i + 1] - 97]);
					break;
				}
				lastTag = i + 2;
			}
		}
		g.drawString(new String(chars).substring(lastTag, i), x, y - g.getFont().getLineHeight());
	}

	public void drawStyledString(ThingleGraphics g, boolean enabled,
			boolean styled, ThingleFont f, ThingleColor c, String s, int sx,
			int sy) {
		drawStyledChars(g, enabled, styled, f, c, s.toCharArray(), 0, s
				.length(), sx, sy);
	}

	/**
	 * calculates char witdh of styled chars, ignoring styling tags, counting
	 * different font styles slows down painting for about 15% :(
	 * 
	 * @param f -
	 *            default font
	 */
	public int styledCharsWidth(boolean styled, ThingleFont f, char[] chars,
			int offset, int len) {

		if (!styled) {
			return getFontMetrics(f).charsWidth(chars, offset, len);
		}

		FontMetrics[] fms = getFMs(f);
		int s = 0, res = 0, i, lastTag = offset;
		for (i = offset; i < offset + len; i++) {
			if ((chars[i] == '`') && (i + 1 < (offset + len))) {
				res += fms[s].charsWidth(chars, lastTag, i - lastTag);
				switch (chars[i + 1]) {
				case '0':
					s = 0;
					break;
				case '1':
					s = 1;
					break;
				case '2':
					s = 2;
					break;
				case '3':
					s = 3;
					break;
				}
				lastTag = i + 2;
			}
		}
		res += fms[s].charsWidth(chars, lastTag, i - lastTag);
		return res;
	}

	public int styledStringWidth(boolean styled, ThingleFont f, String s) {
		return styledCharsWidth(styled, f, s.toCharArray(), 0, s.length());
	}

	protected char[] getChars(Object component, String text, boolean wrap,
			int width, int height) {
		if (!skin.loaded) {
			return super.getChars(component, text, wrap, width, height);
		}
		
		char[] chars = (char[]) get(component, ":text");
		if ((chars == null) || (chars.length != text.length())) {
			chars = text.toCharArray();
			set(component, ":text", chars);
		} else
			text.getChars(0, chars.length, chars, 0);

		boolean styled = getBoolean(component, "styled", false);
		if (wrap) {
			ThingleFont currentfont = (ThingleFont) get(component, "font");
			if (currentfont == null) {
				currentfont = skin.fontPool[skin.textField.fonts[0]];
			}
			
			FontMetrics fm = getFontMetrics(currentfont);
			int lines = (height - 4 + fm.getLeading()) / fm.getHeight();
			boolean prevletter = false;
			int n = chars.length;
			int linecount = 0;
			for (int i = 0, j = -1, k = 0; k <= n; k++) { // j is the last
															// space index
															// (before k)
				if (((k == n) || (chars[k] == '\n') || (chars[k] == ' '))
						&& (j > i)
						&& (styledCharsWidth(styled, currentfont, chars, i, k
								- i) > width)) {
					chars[j] = '\n';
					k--; // draw line to the begin of the current word (+
							// spaces) if it is out of width
				} else if ((k == n) || (chars[k] == '\n')) { // draw line to
																// the text/line
																// end
					j = k;
					prevletter = false;
				} else {
					if ((chars[k] == ' ') && (prevletter || (j > i))) {
						j = k;
					} // keep spaces starting the line
					prevletter = (chars[k] != ' ');
					continue;
				}
				linecount++;
				if ((lines != 0) && (linecount == lines)) {
					return null;
				}
				i = j + 1;
			}
		}
		return chars;
	}

	/**
	 * Cache objects so they have to be created only once. increas spead by
	 * about 15%....
	 */
	Hashtable fontCache, fontMetricsCache;

	FontMetrics getFM(ThingleFont f, int style) {
		if (fontMetricsCache == null)
			fontMetricsCache = new Hashtable();
		String key = f + "," + style + "," + f;
		FontMetrics fm = (FontMetrics) fontMetricsCache.get(key);
		if (fm == null) {
			fm = getFontMetrics(f);
			fontMetricsCache.put(key, fm);
		}
		return fm;
	}

	FontMetrics[] getFMs(ThingleFont f) {
		FontMetrics[] fms = new FontMetrics[] { getFM(f, 1), getFM(f, 2),
				getFM(f, 4), getFM(f, 6) };
		return fms;
	}

	ThingleFont getFNt(ThingleFont f, int style) {
		if (fontCache == null)
			fontCache = new Hashtable();
		String key = f + "," + style + "," + f;
		ThingleFont fn = (ThingleFont) fontCache.get(key);
		if (fn == null) {
			fn = Thingle.getContext().createFont(f.getFamily(), style,
					f.getSize());
			fontCache.put(key, fn);
		}
		return fn;
	}

	ThingleFont[] getFNTs(ThingleFont f) {
		ThingleFont[] fnts = new ThingleFont[] { getFNt(f, 1), getFNt(f, 2),
				getFNt(f, 4), getFNt(f, 6) };
		return fnts;
	}

}
