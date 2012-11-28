package org.newdawn.slick.thingle.internal;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.newdawn.slick.thingle.Thingle;
import org.newdawn.slick.thingle.spi.ThingleColor;
import org.newdawn.slick.thingle.spi.ThingleException;
import org.newdawn.slick.thingle.spi.ThingleFont;
import org.newdawn.slick.thingle.spi.ThingleGraphics;
import org.newdawn.slick.thingle.spi.ThingleImage;

/**
 * Adds skin support for Thinlets. This class does all the skin paint work and
 * is referenced by Skinlet.java when a component needs to be drawn.
 * 
 * @author Eugene Klein (http://www.realchat.com/thinlet/)
 * @version 0.3 ( preview )
 * 
 */

public class ThinletSkin {

	// skin pieces
	public Piece button, checkBox, radioButton, menuBar, menuItem, popUp,
			panel, label, separator, link, desktop, textField, table,
			tableItem, tableHeader, treeNode, toolTip, scrollBarShaft,
			scrollBarKnob, scrollBarArrow, spinBox, tabItem, tabPane, sideBar,
			progressBar, splitHoriz, splitVert, sliderBody, sliderKnobHoriz,
			sliderKnobVert, arrows, combobutton, comboborder, comboarrows,
			icon, dialog, titlebar, closeButton,
			// etc...

			// extensions, custom pieces used manually, for future
			// implementation
			extPieces[]

			;

	public ThingleColor[] colorPool, palette;

	public ThingleFont[] fontPool;

	boolean useImageCache = true, loaded = false;

	public Configurator conf;

	JarResource jar;

	boolean fromJar = true;

	// todo: cache for already used pieaces so we won't need to scale them
	// again...
	Hashtable imageCache;

	static boolean is11 = System.getProperty("java.version").startsWith("1.1");

	/**
	 * Default constructor
	 * 
	 * @param c -
	 *            a skinlet object
	 */
	public ThinletSkin() {
		
	}

	public boolean loaded() {
		return menuBar != null;
	}
	
	/**
	 * Load skin from local files or JAR archive
	 * 
	 * @param path -
	 *            Path to skin files ( e.g. /skins/ for local files or
	 *            http://www.example.com/skins/ for web resources )
	 * @param name -
	 *            of skin description file or skin JAR archive; the method first
	 *            looks for a JAR packaged skin ( name.jar ) and if no jar is
	 *            present looks for path/name/name.conf - i.e. resources are
	 *            stored as separate files ( used during development );
	 */
	public void load(String path, String name) throws ThingleException {
		try {
			imageCache = new Hashtable();

			InputStream in = Thingle.getContext().getResourceAsStream(name + ".conf");
			if (in == null) {
				throw new ThingleException("Unable to locate skin: "+name);
			}
			conf = new Configurator(new BufferedInputStream(in));

			colorPool = conf.getColorArrConf("Colors");
			palette = conf.getColorArrConf("Palette");
			fontPool = conf.getFontArrConf("Fonts");

			button = loadPiece("Button", 5, path);
			menuBar = loadPiece("MenuBar", 4, path);
			menuItem = loadPiece("MenuItem", 4, path);
			popUp = loadPiece("PopUp", 1, path);
			panel = loadPiece("Panel", 4, path);
			separator = loadPiece("Separator", 2, path);
			label = loadPiece("Label", 1, path);
			link = loadPiece("Link", 1, path);
			desktop = loadPiece("Desktop", 1, path);
			textField = loadPiece("TextField", 3, path);
			table = loadPiece("Table", 3, path);
			tableItem = loadPiece("TableItem", 3, path);
			tableHeader = loadPiece("TableHeader", 5, path);
			treeNode = loadPiece("TreeNode", 4, path);
			toolTip = loadPiece("ToolTip", 1, path);
			scrollBarShaft = loadPiece("ScrollBarShaft", 6, path);
			scrollBarKnob = loadPiece("ScrollBarKnob", 16, path);
			scrollBarArrow = loadPiece("ScrollBarArrow", 16, path);
			spinBox = loadPiece("SpinBox", 8, path);
			checkBox = loadPiece("CheckBox", 8, path);
			radioButton = loadPiece("RadioButton", 8, path);
			tabItem = loadPiece("TabItem", 5, path);
			tabPane = loadPiece("TabPane", 2, path);
			sideBar = loadPiece("SideBar", 5, path);
			splitHoriz = loadPiece("SplitHoriz", 2, path);
			splitVert = loadPiece("SplitVert", 2, path);
			sliderBody = loadPiece("SliderBody", 4, path);
			sliderKnobHoriz = loadPiece("SliderKnobHoriz", 5, path);
			sliderKnobVert = loadPiece("SliderKnobVert", 5, path);
			progressBar = loadPiece("ProgressBar", 8, path);
			arrows = loadPiece("Arrows", 12, path);
			combobutton = loadPiece("ComboButton", 4, path);
			comboborder = loadPiece("ComboBorder", 4, path);
			comboarrows = loadPiece("ComboArrows", 4, path);
			icon = loadPiece("Icon", 1, path);
			dialog = loadPiece("Dialog", 2, path);
			titlebar = loadPiece("Titlebar", 2, path);
			closeButton = loadPiece("CloseButton", 6, path);
			loaded = true;
		} catch (Exception ex) {
			throw new ThingleException("Failed to load skin: "+name, ex);
		}
	}

	/**
	 * @param type -
	 *            button, panel etc.
	 * @param stateCount -
	 *            how many states in bitmap ( Normal, Pressed etc. )
	 * @param path -
	 *            to files
	 * @return a skin piece
	 */
	public Piece loadPiece(String type, int stateCount, String path) {
		ThingleImage image = loadImage(path, conf
				.getConf(type + ".image", null));
		Piece piece = new Piece(this, image, stateCount, conf
				.getConfInsets(type + ".border.insets"), conf
				.getConfInsets(type + ".insets"));
		piece.useTransparency = conf.getConf(type + ".image.transparency",
				false);
		piece.textColors = conf.getArrConf(type + ".colors.text", 10,
				new int[stateCount]);
		piece.faceColors = conf.getArrConf(type + ".colors.face", 10,
				new int[stateCount]);
		piece.borderColors = conf.getArrConf(type + ".colors.border", 10,
				new int[stateCount]);
		int[] pm = conf.getArrConf(type + ".paint", 10, new int[] { 3, 4, 3, 4,
				2 });
		piece.setPaintMethod(pm);
		piece.fonts = conf.getArrConf(type + ".fonts", 10, new int[stateCount]);
		piece.delta = conf.getConf(type + ".delta", 1);
		piece.type = type;
		return piece;
	}

	public ThingleImage loadImage(String path, String name) {
		if (name == null || name.endsWith("null"))
			return null;

		ThingleImage image = null;
		InputStream is = Thingle.getContext().getResourceAsStream(path+name);
		if (is == null) {
			Thingle.getContext().log("Unable to locate: "+path+name);
			return null;
		}
		try {
			image = Thingle.getContext().createImage(is, name, false);
		} catch (ThingleException e) {
			Thingle.getContext()
					.log("Failed to load: " + path + " " + name);
		}

		return image;
	}

	/**
	 * Converts loaded image to 'native' format ( on the fly during 1st painting ).
	 * Seems to speed up things a bit on 1.1 jre. Does not support transparency
	 * so bitmaps with image.transparency = true do not get converted.
	 * 
	 * @param src -
	 * @return -
	 */
	public ThingleImage toNative(ThingleImage src) {
		return src;
	}

	/**
	 * describes basic structure with all elements needed to skin a rectangular
	 * GUI element such as button, menu, checkbox...
	 */
	public class Piece {

		public static final int

		// push button states
				pbNormal = 0,
				pbPressed = 1,
				pbDisabled = 2,
				pbFocused = 3,
				pbHovered = 4,
				pbDefault = 5,

				// checkbox & radio button states
				cbNormalUnchecked = 0,
				cbHoverUnchecked = 1,
				cbPressedUnchecked = 2,
				cbDisabledUnchecked = 3,
				cbNormalChecked = 4,
				cbHoverChecked = 5,
				cbPressedChecked = 6,
				cbDisabledChecked = 7,

				// panel states
				pnNormalNoBorder = 0,
				pnDisabledNoBorder = 1,
				pnNormalBorder = 2,
				pnDisabledBorder = 3,

				// menuBar & menuItem states
				mbNormal = 0, mbSelected = 1,
				mbDisabled = 2,
				mbHovered = 3,
				miSeparator = 3, // separator instead of hovered for menuitem
									// bitmap

				// textField & textArea states
				tfNormal = 0, tfSelected = 1, tfDisabled = 2,

				// different ways to paint parts
				None = 0, SolidFill = 1, Stretch = 2,
				TileHorizontally = 3,
				TileVertically = 4, TileBoth = 5;

		int
		// paint method
				paintTop,
				paintLeft, paintBottom, paintRight, paintFace;

		// bitmap image for this component
		ThingleImage bitmap;

		// image offsets for each part - [0] = Normal image, [1] = Pressed image
		// ..
		Rectangle[] topLeftCorner, topRightCorner, bottomRightCorner,
				bottomLeftCorner, topBorder, rightBorder, bottomBorder,
				leftBorder, face;

		// color set
		public int[] textColors, faceColors, borderColors;

		// font set
		int[] fonts;

		private ThinletSkin skin;

		// insets
		Insets borderInsets;

		Insets insets;

		// pref. size
		public int pieceWidth, pieceHeight, delta;

		boolean ready = false, bitmapConverted = false,
				useTransparency = false;

		String type;

		/**
		 * @param skin
		 *            The parent skin object
		 * @param bitmap
		 *            The bitmap containing all the piece states
		 * @param stateCount
		 *            How many pieces (states) are stored in source bitmap
		 * @param borderInsets
		 *            Border insets
		 * @param insets
		 *            Content insets
		 */
		public Piece(ThinletSkin skin, ThingleImage bitmap, int stateCount,
				Insets borderInsets, Insets insets) {
			this.skin = skin;

			this.borderInsets = borderInsets;
			this.insets = insets;
			this.bitmap = bitmap;

			pieceWidth = (bitmap == null) ? (borderInsets.left + borderInsets.right)
					: bitmap.getWidth() / stateCount;
			pieceHeight = (bitmap == null) ? (borderInsets.top + borderInsets.bottom)
					: bitmap.getHeight();

			// split source bitmap into pieces for each state
			topLeftCorner = new Rectangle[stateCount];
			topRightCorner = new Rectangle[stateCount];
			bottomRightCorner = new Rectangle[stateCount];
			bottomLeftCorner = new Rectangle[stateCount];
			topBorder = new Rectangle[stateCount];
			rightBorder = new Rectangle[stateCount];
			bottomBorder = new Rectangle[stateCount];
			leftBorder = new Rectangle[stateCount];
			face = new Rectangle[stateCount];

			for (int i = 0; i < stateCount; i++) {
				topLeftCorner[i] = new Rectangle(i * pieceWidth, 0,
						borderInsets.left, borderInsets.top);
				topRightCorner[i] = new Rectangle((i + 1) * pieceWidth
						- borderInsets.right, 0, borderInsets.right,
						borderInsets.top);
				bottomRightCorner[i] = new Rectangle((i + 1) * pieceWidth
						- borderInsets.right,
						pieceHeight - borderInsets.bottom, borderInsets.right,
						borderInsets.bottom);
				bottomLeftCorner[i] = new Rectangle(i * pieceWidth, pieceHeight
						- borderInsets.bottom, borderInsets.left,
						borderInsets.bottom);

				topBorder[i] = new Rectangle(
						i * pieceWidth + borderInsets.left, 0, pieceWidth
								- borderInsets.left - borderInsets.right,
						borderInsets.top);
				rightBorder[i] = new Rectangle((i + 1) * pieceWidth
						- borderInsets.right, borderInsets.top,
						borderInsets.right, pieceHeight - borderInsets.top
								- borderInsets.bottom);
				leftBorder[i] = new Rectangle(i * pieceWidth, borderInsets.top,
						borderInsets.left, pieceHeight - borderInsets.top
								- borderInsets.bottom);
				bottomBorder[i] = new Rectangle(i * pieceWidth
						+ borderInsets.left, pieceHeight - borderInsets.bottom,
						pieceWidth - borderInsets.left - borderInsets.right,
						borderInsets.bottom);

				face[i] = new Rectangle(i * pieceWidth + borderInsets.left,
						borderInsets.top, pieceWidth - borderInsets.left
								- borderInsets.right, pieceHeight
								- borderInsets.top - borderInsets.bottom);
			}
			ready = true;
		}

		public void setPaintMethod(int[] arg) {
			paintTop = arg[0];
			paintLeft = arg[1];
			paintBottom = arg[2];
			paintRight = arg[3];
			paintFace = arg[4];
		}

		/**
		 * Draws single part ( border, corner etc. ) of this skin piece
		 * 
		 * @param g
		 *            the graphics to paint on
		 * @param r
		 *            coordinates of that part in source bitmap
		 * @param x
		 *            target coordinates
		 * @param y -
		 * @param w -
		 * @param h -
		 * @param paintMethod -
		 */
		public void drawPiece(ThingleGraphics g, Rectangle r, int x, int y,
				int w, int h, int paintMethod) {

			// skip borders and corners for fixed size components like
			// checkboxses
			// and don't paint empty pieces...
			if (w < 1 || h < 1)
				return;
			if ((paintMethod != SolidFill) && (r.width < 1 || r.height < 1))
				return;

			/*
			 * todo: image caching ... String key =
			 * this+":"+w+","+h+":"+r.x+","+r.y+","+r.width+","+r.height; Image
			 * tmp = (Image)imageCache.get( key ); if ( tmp == null ) {
			 * //System.out.println("not cached: "+key); tmp =
			 * observer.createImage( w, h ); tmp.getGraphics().drawImage(
			 * bitmap, 0, 0, w, h, r.x, r.y, r.x + r.width, r.y + r.height,
			 * observer ); imageCache.put( key, tmp ); }
			 */

			g.translate(x, y);
			if (paintMethod == SolidFill) {
				g.fillRect(0, 0, w, h);
			} else if (paintMethod == Stretch) {
				g.drawImage(bitmap, 0, 0, w, h, r.x, r.y, r.x + r.width, r.y
						+ r.height);
			} else if (paintMethod == TileHorizontally) {
				for (int i = 0; i < w; i += r.width) {
					g
							.drawImage(bitmap, i, 0, Math.min(i + r.width, w),
									h, r.x, r.y,
									r.x + Math.min(w - i, r.width), r.y
											+ r.height);
				}
			} else if (paintMethod == TileVertically) {
				for (int i = 0; i < h; i += r.height) {
					g.drawImage(bitmap, 0, i, w, Math.min(i + r.height, h),
							r.x, r.y, r.x + r.width, r.y
									+ Math.min(h - i, r.height));
				}
			} else if (paintMethod == TileBoth) {
				for (int i = 0; i < w; i += r.width) {
					for (int j = 0; j < h; j += r.height) {
						g.drawImage(bitmap, i, j, Math.min(i + r.width, w),
								Math.min(j + r.height, h), r.x, r.y, r.x
										+ Math.min(w - i, r.width), r.y
										+ Math.min(h - j, r.height));
					}
				}
			}
			g.translate(-x, -y);
		}

		public void drawPiece(ThingleGraphics g, Rectangle r, int x, int y,
				int paintMethod) {
			drawPiece(g, r, x, y, r.width, r.height, paintMethod);
		}

		public void paintBody(ThingleGraphics g, int state, int x, int y,
				int w, int h, boolean top, boolean left, boolean bottom,
				boolean right, boolean pface, boolean preserveMinSize,
				ThingleColor customFaceColor) {
			if (!ready)
				return;
			if (is11 && !useTransparency && !bitmapConverted) {
				bitmap = toNative(bitmap);
				bitmapConverted = true;
			}

			if (preserveMinSize
					&& (w < borderInsets.left + borderInsets.right || h < borderInsets.top
							+ borderInsets.bottom))
				return;

			g.setColor(colorPool[borderColors[state]]);
			// borders
			
			if (top)
				drawPiece(g, topBorder[state], x
						+ ((left) ? borderInsets.left : 0), y, w
						- ((left) ? borderInsets.left : 0)
						- ((right) ? borderInsets.right : 0), borderInsets.top,
						paintTop);
			if (left)
				drawPiece(g, leftBorder[state], x, y
						+ ((top) ? borderInsets.top : 0), borderInsets.left, h
						- ((top) ? borderInsets.top : 0)
						- ((bottom) ? borderInsets.bottom : 0), paintLeft);
			if (bottom)
				drawPiece(g, bottomBorder[state], x
						+ ((left) ? borderInsets.left : 0), y + h
						- borderInsets.bottom, w
						- ((left) ? borderInsets.left : 0)
						- ((right) ? borderInsets.right : 0),
						borderInsets.bottom, paintBottom);
			if (right)
				drawPiece(g, rightBorder[state], x + w - borderInsets.right, y
						+ ((top) ? borderInsets.top : 0), borderInsets.right, h
						- ((top) ? borderInsets.top : 0)
						- ((bottom) ? borderInsets.bottom : 0), paintRight);

			// corners
			if (top && left)
				drawPiece(g, topLeftCorner[state], x, y, paintTop);
			if (top && right)
				drawPiece(g, topRightCorner[state], x + w - borderInsets.right,
						y, paintTop);
			if (bottom && right)
				drawPiece(g, bottomRightCorner[state], x + w
						- borderInsets.right, y + h - borderInsets.bottom,
						paintBottom);
			if (bottom && left)
				drawPiece(g, bottomLeftCorner[state], x, y + h
						- borderInsets.bottom, paintBottom);

			g.setColor(colorPool[faceColors[state]]);
			// flexible space ( face )
			if (pface) {
				if (paintFace == SolidFill || customFaceColor != null)
					g.setColor(customFaceColor != null ? customFaceColor
							: colorPool[faceColors[state]]);
				drawPiece(g, face[state], x + ((left) ? borderInsets.left : 0),
						y + ((top) ? borderInsets.top : 0), w
								- ((left) ? borderInsets.left : 0)
								- ((right) ? borderInsets.right : 0), h
								- ((top) ? borderInsets.top : 0)
								- ((bottom) ? borderInsets.bottom : 0),
						customFaceColor != null ? 1 : paintFace);
			}
		} // paint

		// vasious wrappers ( helpers )...
		public void paintBody(ThingleGraphics g, int state, int x, int y) {
			paintBody(g, state, x, y, pieceWidth, pieceHeight, paintTop != 0,
					paintLeft != 0, paintBottom != 0, paintRight != 0,
					paintFace != 0, true, null);
		}

		public void paintBody(ThingleGraphics g, int state, int x, int y,
				int w, int h) {
			paintBody(g, state, x, y, w, h, paintTop != 0, paintLeft != 0,
					paintBottom != 0, paintRight != 0, paintFace != 0, true,
					null);
		}

		public void paintBody(ThingleGraphics g, int state, int x, int y,
				int w, int h, ThingleColor c) {
			paintBody(g, state, x, y, w, h, paintTop != 0, paintLeft != 0,
					paintBottom != 0, paintRight != 0, paintFace != 0, true, c);
		}

		public void paintBody(ThingleGraphics g, int state, int x, int y,
				int w, int h, boolean pms) {
			paintBody(g, state, x, y, w, h, paintTop != 0, paintLeft != 0,
					paintBottom != 0, paintRight != 0, paintFace != 0, pms,
					null);
		}

		public void paintBody(ThingleGraphics g, int state, int x, int y,
				int w, int h, boolean top, boolean left, boolean bottom,
				boolean right) {
			paintBody(g, state, x, y, w, h, top, left, bottom, right, true,
					true, null);
		}

		public void paintBody(ThingleGraphics g, int state, int x, int y,
				int w, int h, boolean top, boolean left, boolean bottom,
				boolean right, ThingleColor c) {
			paintBody(g, state, x, y, w, h, top, left, bottom, right, true,
					true, c);
		}

		/**
		 * Get a color from the color pool
		 * 
		 * @param state -
		 *            the state needed
		 * @param def -
		 *            default value ( for custom colors on components )
		 */
		public ThingleColor getTextColor(int state, ThingleColor def) {
			if (state >= textColors.length) {
				state = 0;
			}
			
			return def != null ? def : skin.colorPool[textColors[state]];
		}

		public ThingleColor getFaceColor(int state, ThingleColor def) {
			if (state >= faceColors.length) {
				state = 0;
			}
			
			return def != null ? def : skin.colorPool[faceColors[state]];
		}

		public ThingleColor getBorderColor(int state, ThingleColor def) {
			if (state >= borderColors.length) {
				state = 0;
			}
			
			return def != null ? def : skin.colorPool[borderColors[state]];
		}

	} // Piece

	public class Insets {
		public int top, left, bottom, right, tb, lr; // tb, lr - shorthand
														// for top+bottom,
														// left+right

		public Insets() {
			this(0, 0, 0, 0);
		}

		public Insets(int t, int l, int b, int r) {
			top = t;
			left = l;
			bottom = b;
			right = r;
			tb = top + bottom;
			lr = left + right;
		}
	}

	/**
	 * Helper class to read various sirectives from skin config file.
	 */
	public class Configurator {

		Properties p;

		public Configurator(String name) {
			p = new Properties();
			try {
				InputStream is = getClass().getResourceAsStream(name);
				p.load(is);
				is.close();
			} catch (Exception e) {
			}
		}

		public Configurator(InputStream is) {
			p = new Properties();
			try {
				p.load(is);
				is.close();
			} catch (Exception e) {
			}
		}

		// Get a parameter value ( string, int, boolean, or inset ) /
		public String getConf(String key, String def) {
			String res = null;
			try {
				res = p.getProperty(key);
			} catch (Exception ex) {
				res = def;
			}
			return (res != null) ? res.trim() : def;
		}

		public int getConf(String key, int def) {
			String res = null;
			try {
				res = p.getProperty(key);
				return Integer.parseInt(res, 10);
			} catch (Exception ex) {
				return def;
			}
		}

		public boolean getConf(String key, boolean def) {
			String res = null;
			try {
				res = p.getProperty(key);
			} catch (Exception ex) {
				return def;
			}
			return (res != null) ? res.trim().equalsIgnoreCase("yes") : def;
		}

		public Insets getConfInsets(String key) {
			String res = null;
			try {
				res = p.getProperty(key);
				StringTokenizer st = new StringTokenizer(res, ",");
				return new Insets(Integer.parseInt(st.nextToken().trim()),
						Integer.parseInt(st.nextToken().trim()), Integer
								.parseInt(st.nextToken().trim()), Integer
								.parseInt(st.nextToken().trim()));
			} catch (Exception ex) {
				return new Insets(0, 0, 0, 0); // some default value
			}
		}

		public int[] getArrConf(String key, int radix, int[] def) {
			String res = null;
			try {
				res = p.getProperty(key);
				StringTokenizer st = new StringTokenizer(res, ",");
				int[] arr = new int[st.countTokens()];
				for (int i = 0; i < arr.length; i++) {
					arr[i] = Integer.parseInt(st.nextToken().trim(), radix);
				}
				return arr;
			} catch (Exception ex) {
				return def;
			}
		}

		public ThingleFont[] getFontArrConf(String key) {
			String res = null;
			int delta = 0; // ms vm has smaller font size...?
			// todo: check... if ( ( ( System.getProperty( "java.vendor" )
			// ).indexOf( "Microsoft" ) ) != -1 ) delta = 1;
			try {
				res = p.getProperty(key);
				StringTokenizer st = new StringTokenizer(res, ",");
				ThingleFont[] arr = new ThingleFont[st.countTokens()];
				for (int i = 0; i < arr.length; i++) {
					String tmp = st.nextToken().trim();
					int t1 = tmp.indexOf(":");
					int t2 = tmp.indexOf(":", t1 + 1);
					String face = tmp.substring(0, t1);
					String styleString = tmp.substring(t1 + 1, t2);
					String sizeString = tmp.substring(t2 + 1);
					if (face.equals("bitmap")) {
						 arr[i] = Thingle.createBitmapFont(styleString, sizeString);
					} else {
						int style = Integer.parseInt(styleString, 10);
						int size = Integer.parseInt(sizeString, 10)
								+ delta;
						arr[i] = Thingle.createFont(face, style, size);
					}
					// System.out.println(arr[i]);
				}
				return arr;
			} catch (Exception ex) {
				return new ThingleFont[] { Thingle.createFont("Dialog", 0,
						12) };
				// some default value
			}
		}

		public ThingleColor[] getColorArrConf(String key) {
			ThingleColor[] colors = new ThingleColor[50];
			for (int row = 0; row < 5; row++) {
				int[] clrs = getArrConf(key + row, 16, null);
				if (clrs == null)
					break;
				for (int i = 0; i < clrs.length; i++)
					colors[i + row * 10] = Thingle.createColor(clrs[i]);
			}
			return colors;
		}

	} // Configurator

	/**
	 * JarResource: JarResource maps all resources included in a Zip or Jar
	 * file. Additionaly, it provides a method to extract one as a blob.
	 * 
	 * taken from the tutorial at:
	 * http://www.javaworld.com/javaworld/javatips/jw-javatip49.html
	 * 
	 */
	private class JarResource {

		private Hashtable htJarContents = new Hashtable();

		private String jarFileName;

		/**
		 * creates a JarResource. It extracts all resources from a Jar into an
		 * internal hashtable, keyed by resource names.
		 * 
		 * @param jarFileName
		 *            a jar or zip file
		 */
		public JarResource(String jarFileName) {
			this.jarFileName = jarFileName;
			init();
		}

		/**
		 * Extracts a jar resource as a blob.
		 * 
		 * @param name
		 *            a resource name.
		 */
		public byte[] getResource(String name) {
			return (byte[]) htJarContents.get(name);
		}

		/**
		 * initializes internal hash tables with Jar file resources.
		 */
		private void init() {
			try {
				InputStream is;
				if (jarFileName.startsWith("http://")) {
					URL url = new URL(jarFileName);
					URLConnection uc = url.openConnection();
					is = uc.getInputStream();
				} else {
					// ???? is = getClass().getResourceAsStream( jarFileName );
					is = new FileInputStream(jarFileName);
				}

				BufferedInputStream bis = new BufferedInputStream(is);
				ZipInputStream zis = new ZipInputStream(bis);
				ZipEntry ze = null;
				while ((ze = zis.getNextEntry()) != null) {
					if (ze.isDirectory()) {
						continue;
					}
					int size = (int) ze.getSize();
					// -1 means unknown size.
					if (size == -1)
						size = 25000; // some value ......
					byte[] tmp = new byte[size];
					int rb = 0;
					int chunk = 0;
					while (chunk != -1) {
						chunk = zis.read(tmp, rb, size - rb);
						rb += chunk;
					}
					byte[] b = new byte[rb];
					System.arraycopy(tmp, 0, b, 0, rb);
					// add to internal resource hashtable
					htJarContents.put(ze.getName(), b);
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
	}

}