package playground.games;

import java.util.ArrayList;

/**
 * A list of games records provided from a store
 *
 * @author kevin
 */
public interface GameList {
	/**
	 * Get a single game from this list
	 * 
	 * @param index The index of the game to retrieve
	 * @return The game retrieved
	 */
	public GameRecord getGame(int index);
	
	/**
	 * Get a count of the number of games in this list
	 * 
	 * @return The count of the number of games in this list
	 */
	public int size();
}
