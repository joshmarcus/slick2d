package playground.games.woogley;

import java.io.IOException;
import java.util.ArrayList;

import net.javaunlimited.playground.Category;
import net.javaunlimited.playground.Database;
import net.javaunlimited.playground.GameInfo;
import net.javaunlimited.playground.Server;
import playground.games.GameList;
import playground.games.GameRecord;
import playground.games.GameStore;

/**
 * A game store accessing the game repository on javaunlimited.
 *
 * @author kevin
 */
public class WoogleyGameStore implements GameStore {
	/** The list of all games retrieved */
	private GList allgames = new GList();
	/** The names of the categories defined */
	private String[] catNames;
	
	/**
	 * Create an empty store to connect to javaunlimited
	 */
	public WoogleyGameStore() {
	}
	
	/**
	 * @see playground.games.GameStore#update()
	 */
	public void update() throws IOException {
		if (!Server.isOnline()) {
			throw new IOException("Failed to connect to woogley.net");
		}
		
		GameInfo[] infos = Database.getGames(Category.ALL,Database.SORT_DATE,Database.SORT_ASCENDING);
		
		allgames.clear();
		for (int i=0;i<infos.length;i++) {
			allgames.add(new WoogleyGameRecord(infos[i]));
		}
		
		Category[] cats = Database.getCategories();
		catNames = new String[cats.length];
		for (int i=0;i<cats.length;i++) {
			catNames[i] = cats[i].getName();
		}
	}
	
	/**
	 * @see playground.games.GameStore#getCategories()
	 */
	public String[] getCategories() {
		return catNames;
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
	 * @see playground.games.GameStore#getGames()
	 */
	public GameList getGames() {
		return allgames;
	}

	/**
	 * Simple implementation of the game list
	 *
	 * @author kevin
	 */
	private class GList extends ArrayList implements GameList {

		/**
		 * @see playground.games.GameList#getGame(int)
		 */
		public GameRecord getGame(int index) {
			return (GameRecord) get(index);
		}
		
	}


	/**
	 * @see playground.games.GameStore#lastUpdated()
	 */
	public long lastUpdated() {
		return 0;
	}

	/**
	 * @see playground.games.GameStore#getName()
	 */
	public String getName() {
		return "Java Unlimited Server";
	}
}
