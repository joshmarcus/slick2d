package playground;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.Image;
import org.newdawn.slick.util.Log;

import playground.games.GameList;
import playground.games.GameRecord;
import playground.games.GameStore;

/**
 * A set of visual data relating the games that have been loaded
 *
 * @author kevin
 */
public class GamesData {
	/** The logos stored for each game */
	private HashMap logos = new HashMap();
	/** The thumbnails logos stored for each game */
	private HashMap thumbs = new HashMap();
	/** The location where we should cache image data */
	private String dataCacheLocation;
	
	/** The list of thumbs images to be loaded */
	private ArrayList thumbsToLoad = new ArrayList();
	/** The list of thumbs images to be loaded */
	private ArrayList logosToLoad = new ArrayList();
	
	/**
	 * Create a new set of games data
	 * 
	 * @param cache The location we should cache image data
	 */
	public GamesData(String cache) {
		dataCacheLocation = cache;
	}
	
	/**
	 * Cache a single game record's data into this holder
	 * 
	 * @param info The description of game to cache
	 */
	public void cache(GameRecord info) {
		getThumbImage(info, false);
		getLogoImage(info, false);
	}
	
	/**
	 * Update the visual data based on the potentially new settings 
	 * from the games store.
	 * 
	 * @param store The store whose games should be updated
	 */
	public void update(GameStore store) {
		GameList list = store.getGames();
		
		for (int i=0;i<list.size();i++) {
			update(list.getGame(i));
		}
	}
	
	/**
	 * Update the information about a single game
	 * 
	 * @param info The description of the game whose data should be updated
	 */
	public void update(GameRecord info) {
		getThumbImage(info, true);
		getLogoImage(info, true);
	}

	/**
	 * Get the thumnail logo for a given game
	 * 
	 * @param info The description of the game whose thumbnail should be retrieve
	 * @return The thumbnail image
	 */
	public Image getThumbImage(GameRecord info) {
		return getThumbImage(info, false);
	}

	/**
	 * Get the thumnail logo for a given game
	 * 
	 * @param info The description of the game whose thumbnail should be retrieve
	 * @param update True if we should update the records is needed
	 * @return The thumbnail image
	 */
	public Image getThumbImage(GameRecord info, boolean update) {
		if ((thumbs.get(info) == null) || (update)) {
			String ref = info.getThumbURL();
			String ext = ref.substring(ref.length()-4);
			
			File thumb = new File(dataCacheLocation+"/"+info.getID()+"/thumb"+ext);
			
			thumb.getParentFile().mkdirs();
			
			if (!thumb.exists() || update) {
				try {
					URLConnection thumbURL = new URL(info.getThumbURL()).openConnection();
					thumbURL.setUseCaches(false);
					
					if ((!thumb.exists()) ||(thumbURL.getLastModified() > thumb.lastModified()) ||
						(thumbURL.getContentLength() != thumb.length())) {
						copy(thumbURL.getInputStream(), new FileOutputStream(thumb));
					}
				} catch (Exception e) {
					Log.warn("Unable to download: "+info.getThumbURL());
				}
			}
			
			try {
				if (thumb.exists()) {
					thumbsToLoad.add(new UpdateRecord(info, thumb));
					//thumbs.put(info, new Image(new FileInputStream(thumb), thumb.getAbsolutePath(), true));
				} else {
					thumbsToLoad.add(new UpdateRecord(info, "res/nothumb.png"));
					//thumbs.put(info, new Image("res/nothumb.png"));
				}
			} catch (Exception e) {
				Log.error("Unable to load: "+thumb);
				Log.error(e);
			}
		}
		
		return (Image) thumbs.get(info);
	}

	/**
	 * Get the logo logo for a given game
	 * 
	 * @param info The description of the game whose logo should be retrieve
	 * @return The logo image
	 */
	public Image getLogoImage(GameRecord info) {
		return getLogoImage(info, false);
	}

	/**
	 * Get the logo logo for a given game
	 * 
	 * @param info The description of the game whose logo should be retrieve
	 * @param update True if we should update the records is needed
	 * @return The logo image
	 */
	public Image getLogoImage(GameRecord info, boolean update) {
		if ((logos.get(info) == null) || (update)) {
			String ref = info.getLogoURL();
			String ext = ref.substring(ref.length()-4);
			
			File logo = new File(dataCacheLocation+"/"+info.getID()+"/logo"+ext);

			logo.getParentFile().mkdirs();
			
			if (!logo.exists() || update) {
				try {
					URLConnection logoURL = new URL(info.getLogoURL()).openConnection();
					logoURL.setUseCaches(false);
					
					if ((!logo.exists()) ||(logoURL.getLastModified() > logo.lastModified()) ||
					    (logoURL.getContentLength() != logo.length())) {
						copy(logoURL.getInputStream(), new FileOutputStream(logo));
					}
				} catch (Exception e) {
					Log.warn("Unable to download: "+info.getLogoURL());
				}
			}
			
			try {
				if (logo.exists()) {
					logosToLoad.add(new UpdateRecord(info, logo));
					//logos.put(info, new Image(new FileInputStream(logo), logo.getAbsolutePath(), true));
				} else {
					logosToLoad.add(new UpdateRecord(info, "res/nologo.png"));
					//logos.put(info, new Image("res/nologo.png"));
				}
			} catch (Exception e) {
				Log.error("Unable to load: "+logo);
				Log.error(e);
			}
		}
		
		return (Image) logos.get(info);
	}
	
	/**
	 * Copy the contents of one stream to another
	 * 
	 * @param is The stream to read from
	 * @param out The stream to write to
	 * @throws IOException Indicates a failure to copy the data from stream to the other
	 */
	private void copy(InputStream is, OutputStream out) throws IOException {
		byte[] buffer = new byte[4096];
		
		int len;
		while ((len = is.read(buffer)) > 0) {
			out.write(buffer, 0, len);
		}
		
		out.close();
	}
	
	/**
	 * Load the images that have been determined as out of date
	 */
	public void loadImages() {
		while (thumbsToLoad.size() > 0) {
			try { 
				UpdateRecord update = (UpdateRecord) thumbsToLoad.remove(0);
				thumbs.put(update.record, update.getImage());
			} catch (Exception e) {
				Log.error(e);
			}
		}
		
		while (logosToLoad.size() > 0) {
			try { 
				UpdateRecord update = (UpdateRecord) logosToLoad.remove(0);
				logos.put(update.record, update.getImage());
			} catch (Exception e) {
				Log.error(e);
			}
		}
	}
	
	/**
	 * A record describing an image that has been determined to be out of date
	 *
	 * @author kevin
	 */
	private class UpdateRecord {
		/** The record the image is assocaited with */
		public GameRecord record;
		/** The local reference in the classpath */
		public String ref;
		/** The file system reference */
		public File fin;

		/**
		 * Create a record for a downloaded file
		 * 
		 * @param record The game the image pertains to
		 * @param fin The file containing the image
		 */
		public UpdateRecord(GameRecord record, File fin) {
			this.record = record;
			this.fin = fin;
		}

		/**
		 * Create a record for a default image
		 * 
		 * @param record The game the image pertains to
		 * @param ref The reference to the image
		 */
		public UpdateRecord(GameRecord record, String ref) {
			this.record = record;
			this.ref = ref;
		}
		
		/**
		 * Load the image specified
		 * 
		 * @return The image specified
		 * @throws Exception Thrown to indicate the image can't be loaded of the reference is invalid
		 */
		public Image getImage() throws Exception {
			if (fin != null) {
				return new Image(new FileInputStream(fin), fin.getAbsolutePath(), true);
			} else 
				return new Image(ref);
		}
	}
}
