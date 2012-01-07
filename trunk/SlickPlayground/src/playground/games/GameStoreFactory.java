package playground.games;

import java.io.IOException;

import org.newdawn.slick.util.Log;

import playground.games.local.LocalGameStore;
import playground.games.woogley.WoogleyGameStore;

/**
 * The factory used to produce the store which the client will use
 * to list the games available
 *
 * @author kevin
 */
public class GameStoreFactory {
	/**
	 * Get a game store for the current client
     *
     * @param cacheLocation The location we should cache to
	 * @return The new game store
	 * @throws IOException Indicates a failure to access the game store (or backing system)
	 */
	public static GameStore getGameStore(String cacheLocation) throws IOException {
		String storeType = System.getProperty("playground.storeType", "playground.games.woogley.WoogleyGameStore");
		
		try {
			Log.info("Using: "+storeType);
			GameStore remoteStore = (GameStore) Class.forName(storeType).newInstance();
			
			return new LocalGameStore(remoteStore, cacheLocation);
		} catch (Exception e) {
			Log.error(e);
			throw new IOException(e.getMessage());
		}
	}
}
