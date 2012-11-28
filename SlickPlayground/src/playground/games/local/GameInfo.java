package playground.games.local;

import org.newdawn.slick.SlickException;

import playground.games.GameRecord;

/**
 * A set of information about a single game in the system
 *
 * @author kevin
 */
public class GameInfo implements GameRecord {
	/** The ID of this game */
	private String id;
	/** The name of this game */
	private String name;
	/** The author or vendor if this game */
	private String author;
	/** The description of this game */
	private String description;
	/** The java version required to run this game */
	private float javaVersion;
	/** The comments submitted about this game */
	private String comments;
	/** The rating that has been given for this game */
	private float rating;
	/** The opengl version required to run this game */
	private float glVersion;
	/** The location of the JNLP launch descriptor */
	private String jnlp;
	
	/** The location of the thumbnail logo image */
	private String thumb;
	/** The location of the log image */
	private String logo;
	/** The name of the category this game is in */
	private String category;
	
	/**
	 * Create a new game info record
	 * 
	 * @param id The ID for the new game
	 * @param name The name given for the new game 
	 * @param author The author or vendor given for the new game
	 * @param description The description of the new game
	 * @param javaVersion The java version required to run the game
	 * @param jnlp The location of the JNLP launch descriptor
	 * @param thumbUrl The location of the thumbnail logo
	 * @param logoUrl The location of the logo
	 * @param category The name of the category in which this game should be placed
	 * @param comments The comments provided for this game
	 * @param rating The rating provided for this game
	 * @param glVersion The OpenGL version required to run this game
	 */
	public GameInfo(String id, String name, String author, String description, String javaVersion,
					String jnlp, String thumbUrl, String logoUrl, String category, String comments,
					String rating, String glVersion) {
		this.category = category;
		this.id = id;
		this.name = name;
		this.author = author;
		this.comments = comments;
		this.glVersion = Float.parseFloat(glVersion);
		this.rating = Float.parseFloat(rating);
		this.description = description;
		this.javaVersion = Float.parseFloat(javaVersion);
		this.jnlp = jnlp;
		
		this.thumb = thumbUrl;
		this.logo = logoUrl;
	}
	
	/**
	 * @see playground.games.GameRecord#getThumbURL()
	 */
	public String getThumbURL() {
		return thumb;
	}
	
	/**
	 * @see playground.games.GameRecord#getLogoURL()
	 */
	public String getLogoURL() {
		return logo;
	}
	
	/**
	 * Prefix the game ID in this record with a given string
	 * 
	 * @param prefix The string to prefix the ID with
	 */
	public void prefixID(String prefix) {
		id = prefix+id;
	}
	
	/**
	 * @see playground.games.GameRecord#getID()
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * @see playground.games.GameRecord#getName()
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @see playground.games.GameRecord#getAuthor()
	 */
	public String getAuthor() {
		return author;
	}
	
	/**
	 * @see playground.games.GameRecord#getDescription()
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @see playground.games.GameRecord#getJNLP()
	 */
	public String getJNLP() {
		return jnlp;
	}

	/**
	 * @see playground.games.GameRecord#comment(java.lang.String)
	 */
	public void comment(String comment) {
	}

	/**
	 * @see playground.games.GameRecord#getCategory()
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @see playground.games.GameRecord#getComments()
	 */
	public String getComments() {
		return comments;
	}
	/**
	 * @see playground.games.GameRecord#getRating()
	 */
	public float getRating() {
		return rating;
	}

	/**
	 * @see playground.games.GameRecord#getRequiredJavaVersion()
	 */
	public float getRequiredJavaVersion() {
		return javaVersion;
	}

	/**
	 * @see playground.games.GameRecord#getRequiredOpenGLVersion()
	 */
	public float getRequiredOpenGLVersion() {
		return glVersion;
	}

	/**
	 * @see playground.games.GameRecord#rate(float)
	 */
	public void rate(float rate) {
	}
}
