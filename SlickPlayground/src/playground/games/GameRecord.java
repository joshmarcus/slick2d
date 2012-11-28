package playground.games;

/**
 * A set of information retrieved from a store about single game
 *
 * @author kevin
 */
public interface GameRecord {
	/**
	 * Get the name of the game
	 * 
	 * @return The name of the game
	 */
	public String getName();
	
	/**
	 * Get the author of the game
	 * 
	 * @return The author of the game
	 */
	public String getAuthor();

	/**
	 * Get the description of the game
	 * 
	 * @return The description of the game
	 */
	public String getDescription();
	
	/**
	 * Get the rating stored for this game
	 * 
	 * @return The rating stored for this game
	 */
	public float getRating();
	
	/**
	 * Get the java version required to run this game
	 * 
	 * @return The java version required to run this game
	 */
	public float getRequiredJavaVersion();

	/**
	 * Get the opengl version required to run this game
	 * 
	 * @return The opengl version required to run this game
	 */
	public float getRequiredOpenGLVersion();
	
	/**
	 * Get the URL at which the thumbnail logo can be found
	 * 
	 * @return The URL at which the thumbnail logo can be found
	 */
	public String getThumbURL();

	/**
	 * Get the URL at which the logo can be found
	 * 
	 * @return The URL at which the logo can be found
	 */
	public String getLogoURL();
	
	/**
	 * Get the location of the JNLP launch descriptor
	 * 
	 * @return The location of the JNLP launch descriptor
	 */
	public String getJNLP();
	
	/**
	 * Get the comments that have been submitted about this game
	 * 
	 * @return The comments that have been submitted about this game
	 */
	public String getComments();
	
	/**
	 * Get the ID assocaited with this game, this should be unique
	 * across all stores.
	 * 
	 * @return The ID assocaited with this game
	 */
	public String getID();
	
	/**
	 * Submit a rating for this game
	 * 
	 * @param rate The rating to give for this game
	 */
	public void rate(float rate);
	
	/**
	 * Submit a comment on this game
	 * 
	 * @param comment The comment to submit
	 */
	public void comment(String comment);
	
	/**
	 * Get the name of the category in which this game resides
	 * 
	 * @return The name of the category in which this game resides
	 */
	public String getCategory();
}
