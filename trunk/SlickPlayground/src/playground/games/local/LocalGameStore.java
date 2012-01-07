package playground.games.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import org.newdawn.slick.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import playground.games.GameList;
import playground.games.GameRecord;
import playground.games.GameStore;

/**
 * A game store wrapped around a locally cached XML file. This allows us to continue
 * playing games while offline
 *
 * @author kevin
 */
public class LocalGameStore implements GameStore {
	/** The list of categories defined */
	private String[] categories = new String[] {"Unspecified"};
	/** The list of all the games defined */
	protected GList allgames = new GList();
	/** The location of the XML file */
	private File cacheFile;
	/** The store we're wrapping and caching */
	private GameStore remoteStore;
	/** The last update time */
	private long lastUpdated;
	/** The name given for this store */
	protected String name;
	/** The type of store we've wrapped */
	private String storeType;
	
	/**
	 * Default constructor for sub classes
	 *
	 */
	protected LocalGameStore() {
	}
	
	/**
	 * Create a new game store based on wrapping a remote implementation
	 * 
	 * @param remote The remote game store
	 * @param cacheLocation The location of the cache file
	 * @throws IOException Indicates a failure to access the local cache file and/or the remote store
	 */
	public LocalGameStore(GameStore remote, String cacheLocation) throws IOException {
		cacheFile = new File(cacheLocation, "store.xml");
		remoteStore = remote;
		cacheFile.getParentFile().mkdirs();
		
		if (!cacheFile.exists()) {
			Log.info("Getting initial games from remote game store");
			update();
		} else {
			Log.info("Loading cached games list");
			load();
			
			if (!storeType.equals(remote.getClass().getName())) {
				Log.info("Refreshing based on a change in store type: "+storeType+","+name);
				update();
			}
		}
	}
	
	/**
	 * @see playground.games.GameStore#getCategories()
	 */
	public String[] getCategories() {
		return categories;
	}

	/**
	 * @see playground.games.GameStore#getGames(java.lang.String)
	 */
	public GameList getGames(String category) {
		GList list = new GList();
		
		for (int i=0;i<allgames.size();i++) {
			if (allgames.getGame(i).getCategory().equals(category)) {
				list.add(allgames.getGame(i));
			}
		}
		
		return list;
	}

	/**
	 * Simple implementation of GameList
	 *
	 * @author kevin
	 */
	protected class GList extends ArrayList implements GameList {

		/**
		 * @see playground.games.GameList#getGame(int)
		 */
		public GameRecord getGame(int index) {
			return (GameRecord) get(index);
		}
		
	}

	/**
	 * @see playground.games.GameStore#getGames()
	 */
	public GameList getGames() {
		return allgames;
	}

	/**
	 * @see playground.games.GameStore#update()
	 */
	public void update() throws IOException {
		try {
			Log.info("Updating remote server");
			remoteStore.update();
		} catch (IOException e) {
			Log.error(e);
			Log.warn("Failed to update remote store");
		}

		try {
			Log.info("Caching");
			cache();
			load();
		} catch (IOException e) {
			Log.error(e);
			Log.warn("Failed to cache remote store");
		}
	}

	/**
	 * Load the locally stored games information
	 * 
	 * @throws IOException Indicates a failure to read the cache
	 */
	protected void load() throws IOException {
		load(new FileInputStream(cacheFile));
	}
	
	/**
	 * Load the locally stored games information
	 * 
	 * @param in The input stream from which to read the XML
	 * @throws IOException Indicates a failure to read the cache
	 */
	protected void load(InputStream in) throws IOException {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.parse(in);
			Element root = document.getDocumentElement();
			
			lastUpdated = Long.parseLong(root.getAttribute("lastCache"));
			storeType = root.getAttribute("storeType");
			name = root.getAttribute("name");
			if ((name == null) || (name.equals(""))) {
				name = "Unnamed store";
			}
			
			NodeList cats = root.getElementsByTagName("category");
			categories = new String[cats.getLength()];
			for (int i=0;i<categories.length;i++) {
				Element cat = (Element) cats.item(i);
				categories[i] = cat.getAttribute("name");
			}
			
			allgames.clear();
			NodeList games = root.getElementsByTagName("game");
			for (int i=0;i<games.getLength();i++) {
				Element element = (Element) games.item(i);
				allgames.add(elementToGame(element));
			}
		} catch (Exception e) {
			Log.error(e);
			throw new IOException("Failed to load cached store");
		}
	}
	
	/**
	 * Cache the remote store locally
	 * 
	 * @throws IOException Indicates a failure to write the cache
	 */
	private void cache() throws IOException {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.newDocument();
			Element root =  document.createElement("store");
			
			lastUpdated = System.currentTimeMillis();
			root.setAttribute("lastCache", ""+lastUpdated);
			root.setAttribute("storeType", ""+remoteStore.getClass().getName());
			root.setAttribute("name", ""+remoteStore.getName());
			
			String[] cats = remoteStore.getCategories();
			for (int i=0;i<cats.length;i++) {
				Element cat = document.createElement("category");
				cat.setAttribute("name", cats[i]);
				root.appendChild(cat);
			}
			
			GameList list = remoteStore.getGames();
			for (int i=0;i<list.size();i++) {
				root.appendChild(gameToElement(document, list.getGame(i)));
			}

			document.appendChild(root);
			
            Result result = new StreamResult(new OutputStreamWriter(new FileOutputStream(cacheFile), "utf-8"));   
			DOMSource source = new DOMSource(document);
			
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setAttribute("indent-number", new Integer(2));
			Transformer xformer = factory.newTransformer();
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
            xformer.transform(source, result);
		} catch (Exception e) {
			Log.error(e);
			throw new IOException("Failed to cache store");
		}
	}
	
	/**
	 * Convert an XML element to a game record
	 * 
	 * @param element The XML element representing the game record
	 * @return The game record generated from the XML
	 */
	private GameRecord elementToGame(Element element) {
		String id = element.getAttribute("id");
		String name = element.getAttribute("name");
		String category = element.getAttribute("category");
		String author = element.getAttribute("author");
		String comments = element.getAttribute("comments");
		String description = element.getAttribute("description");
		String jnlp = element.getAttribute("jnlp");
		String logo = element.getAttribute("logo");
		String thumb = element.getAttribute("thumb");
		float rating = Float.parseFloat(element.getAttribute("rating"));
		float java = Float.parseFloat(element.getAttribute("java"));
		float opengl = Float.parseFloat(element.getAttribute("opengl"));
		
		return new GameInfo(id, name, author, description, ""+java,
							jnlp, thumb, logo, category, comments, ""+rating, ""+opengl);
	}
	
	/**
	 * Create an XML element represeting a given game
	 * 
	 * @param doc The document the element is created within
	 * @param record The record describing the game
	 * @return The element reprsenting the game in XML
	 */
	private Element gameToElement(Document doc, GameRecord record) {
		Element element = doc.createElement("game");

		element.setAttribute("id", record.getID());
		element.setAttribute("name", record.getName());
		element.setAttribute("author", record.getAuthor());
		element.setAttribute("comments", record.getComments());
		element.setAttribute("category", record.getCategory());
		element.setAttribute("description", record.getDescription());
		element.setAttribute("jnlp", record.getJNLP());
		element.setAttribute("logo", record.getLogoURL());
		element.setAttribute("thumb", record.getThumbURL());
		element.setAttribute("rating", ""+record.getRating());
		element.setAttribute("java", ""+record.getRequiredJavaVersion());
		element.setAttribute("opengl", ""+record.getRequiredOpenGLVersion());
		
		return element;
	}

	/**
	 * @see playground.games.GameStore#lastUpdated()
	 */
	public long lastUpdated() {
		return lastUpdated;
	}

	public String getName() {
		String result = remoteStore.getName();
		if (result == null) {
			result = this.name;
		}
		
		return result;
	}
	
}
