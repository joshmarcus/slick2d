package playground.games.remotexml;

import java.io.IOException;
import java.net.URL;

import playground.games.local.GameInfo;
import playground.games.local.LocalGameStore;

/**
 * A games stored based on a simple remote XML file
 *
 * @author kevin
 */
public class RemoteXMLGameStore extends LocalGameStore {
	/** The URL at which the XML can be found */
	private String url;
	/** The ID to use for the store */
	private String id;
	
	/**
	 * Create a new XML game store based on a remote URL
	 * 
	 * @throws IOException Indicates a failure to find configuration values
	 */
	public RemoteXMLGameStore() throws IOException {
		url = System.getProperty("playground.xmlstore.url");
		id = System.getProperty("playground.xmlstore.storeid");
	
		if (url == null) {
			throw new IOException("No URL specified, set playground.xmlstore.url system property");
		}
		if (id == null) {
			throw new IOException("No store ID specified, set playground.xmlstore.storeid system property");
		}
	}
	
	/**
	 * @see playground.games.local.LocalGameStore#update()
	 */
	public void update() throws IOException {
		try {
			URL remote = new URL(url);
			load(remote.openConnection().getInputStream());
			
			for (int i=0;i<allgames.size();i++) {
				GameInfo info = (GameInfo) allgames.get(i);
				info.prefixID(id);
			}
		} catch (IOException e) {
			name = "Server Unavailable";
		}
	}

	public String getName() {
		return name;
	}
}
