package playground.games.woogley;

import net.javaunlimited.playground.GameInfo;
import playground.games.GameRecord;

/**
 * A record represnting data read from the javaunlimited data store
 *
 * @author kevin
 */
public class WoogleyGameRecord implements GameRecord {
	/** The data read from the woogley store */
	private GameInfo info;
	
	/**
	 * Create a new game record based on data read from the woogle store
	 * 
	 * @param info The record from the woogley store
	 */
	public WoogleyGameRecord(GameInfo info) {
		this.info = info;
	}

	/**
	 * @see playground.games.GameRecord#comment(java.lang.String)
	 */
	public void comment(String comment) {
	}

	/**
	 * @see playground.games.GameRecord#getAuthor()
	 */
	public String getAuthor() {
		return info.getAuthor();
	}

	/**
	 * @see playground.games.GameRecord#getCategory()
	 */
	public String getCategory() {
		return info.getCategory().getName();
	}

	/**
	 * @see playground.games.GameRecord#getComments()
	 */
	public String getComments() {
		return "";
	}

	/**
	 * @see playground.games.GameRecord#getDescription()
	 */
	public String getDescription() {
		return info.getInfo();
	}

	/**
	 * @see playground.games.GameRecord#getID()
	 */
	public String getID() {
		return "woogley-net-"+info.getID();
	}

	/**
	 * @see playground.games.GameRecord#getJNLP()
	 */
	public String getJNLP() {
		return info.getLaunchURL().toString();
	}

	/**
	 * @see playground.games.GameRecord#getLogoURL()
	 */
	public String getLogoURL() {
		return info.getLogo().toString();
	}

	/**
	 * @see playground.games.GameRecord#getName()
	 */
	public String getName() {
		return info.getTitle();
	}

	/**
	 * @see playground.games.GameRecord#getRating()
	 */
	public float getRating() {
		return info.getRating();
	}

	/**
	 * @see playground.games.GameRecord#getRequiredJavaVersion()
	 */
	public float getRequiredJavaVersion() {
		return info.getJavaVersion();
	}

	/**
	 * @see playground.games.GameRecord#getRequiredOpenGLVersion()
	 */
	public float getRequiredOpenGLVersion() {
		return info.getGLVersion();
	}

	/**
	 * @see playground.games.GameRecord#getThumbURL()
	 */
	public String getThumbURL() {
		return info.getThumbnail().toString();
	}

	/**
	 * @see playground.games.GameRecord#rate(float)
	 */
	public void rate(float rate) {
	}
}
