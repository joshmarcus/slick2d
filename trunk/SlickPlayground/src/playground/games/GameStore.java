package playground.games;

import java.io.IOException;

/**
 * A game store which holds a set of categories records about a list of games
 *
 * @author kevin
 */
public interface GameStore {
	/**
	 * Get the games from this store matching the specified category
	 * 
	 * @param category The name of the category in which the games should reside
	 * @return The list of games in that category
	 */
	public GameList getGames(String category);
	
	/**
	 * Get all the games stored here
	 * 
	 * @return The list of all the games
	 */
	public GameList getGames();
	
	/**
	 * Get a list of the catgeories in which games are placed
	 * 
	 * @return The list of categories in which the games are placed
	 */
	public String[] getCategories();
	
	/**
	 * Update this store based on the backing store
	 * 
	 * @throws IOException Indicates a failure to access the backing store
	 */
	public void update() throws IOException;
	
	/**
	 * Get the last time when the store was last update
	 * 
	 * @return The time in milliseconds (timestamp) when the last update occurd
	 */
	public long lastUpdated();
	
	/**
	 * Get the name of the store 
	 * 
	 * @return The name of the store
	 */
	public String getName();
}
