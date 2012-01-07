package playground;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.newdawn.slick.Color;
import org.newdawn.slick.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A collection of settings held locally for the client.
 *
 * @author kevin
 */
public class LocalSettings {
	/** The IDs of the games marked as favourites */
	private static ArrayList faves = new ArrayList();
	/** The theme selected */
	private static Color theme = Color.blue;
	/** True if we should start in fullscreen mode */
	private static boolean fullscreen = false;
	/** The file holding our local settings */
	private static File file;
	
	/**
	 * Initialise the settings from a local cache file
	 * 
	 * @param cacheLocation The location of the local cache file
	 */
	public static void init(String cacheLocation) {
		File loc = new File(cacheLocation);
		file = new File(loc, "config.xml");
	}
	
	/**
	 * Load the settings from the file
	 * 
	 * @throws IOException Indicates a failure to read the local settings
	 */
	public static void load() throws IOException {
		if (file.exists()) {
			try {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = builder.parse(file);
				Element root = doc.getDocumentElement();
				fullscreen = "true".equals(root.getAttribute("fullscreen"));
				
				NodeList list = root.getElementsByTagName("theme");
				
				if (list.getLength() > 0) {
					Element themeElement = (Element) list.item(0);
					float r = Float.parseFloat(themeElement.getAttribute("r"));
					float g = Float.parseFloat(themeElement.getAttribute("g"));
					float b = Float.parseFloat(themeElement.getAttribute("b"));
					theme = new Color(r,g,b,1f);
				}
				
				NodeList favesList = root.getElementsByTagName("fave");
				for (int i=0;i<favesList.getLength();i++) {
					Element f = (Element) favesList.item(i);
					faves.add(f.getAttribute("id"));
				}
			} catch (Exception e) {
				Log.error(e);
				throw new IOException(e.getMessage());
			}
		}
	}
	
	/**
	 * Save the settings to a local file
	 * 
	 * @throws IOException Indicates a failure to write the file
	 */
	public static void save() throws IOException {
		file.getParentFile().mkdirs();

		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.newDocument();
			
			Element root = doc.createElement("config");
			root.setAttribute("fullscreen",""+fullscreen);
			Element t = doc.createElement("theme");
			t.setAttribute("r", ""+theme.r);
			t.setAttribute("g", ""+theme.g);
			t.setAttribute("b", ""+theme.b);
			root.appendChild(t);
			
			for (int i=0;i<faves.size();i++) {
				Element f = doc.createElement("fave");
				f.setAttribute("id", ""+faves.get(i));
				root.appendChild(f);
			}
			doc.appendChild(root);

            Result result = new StreamResult(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));   
			DOMSource source = new DOMSource(doc);
			
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setAttribute("indent-number", new Integer(2));
			Transformer xformer = factory.newTransformer();
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
            xformer.transform(source, result);
		} catch (Exception e) {
			Log.error(e);
			throw new IOException(e.getMessage());
		}
	}
	
	/**
	 * Indicate if fullscreen mode should be used
	 * 
	 * @param fs True if fullscreen mode should be used 
	 */
	public static void setFullscreen(boolean fs) {
		fullscreen = fs;
	}
	
	/**
	 * Check if fullscreen mode should be used
	 * 
	 * @return True if fullscreen mode should be used
	 */
	public static boolean getFullscreen() {
		return fullscreen;
	}
	
	/**
	 * Get a list of the IDs of the games marked as favourite
	 * 
	 * @return The list of IDs
	 */
	public static ArrayList getFaves() {
		return faves;
	}
	
	/**
	 * Add a game to the favourites list
	 * 
	 * @param id The ID of the game to add
	 */
	public static void addFave(String id) {
		faves.add(id);
	}
	
	/**
	 * Remove a game from the favourites list
	 * 
	 * @param id The ID of the game to remove
	 */
	public static void removeFave(String id) {
		faves.remove(id);
	}
	
	/**
	 * Get the theme that should be used
	 * 
	 * @return The colour theme that should be used
	 */
	public static Color getTheme() {
		return theme;
	}
	
	/**
	 * Set the theme that should be used
	 * 
	 * @param col The colour of theme that should be used
	 */
	public static void setTheme(Color col) {
		theme = col;
	}
}
