package playground;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Font;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * A collection of common resources for the system
 *
 * @author kevin
 */
public class Resources {
	/** The font for title bar */
	public static Font font;
	/** The font for the big pods */
	public static Font font2;
	/** The font for the description text */
	public static Font font3;
	/** The background image for the big pods */
	public static Image podImage;
	/** The background image for the big pods */
	public static Image smallPodImage;
	
	/**
	 * Initialise the set of resources for Playground
	 * 
	 * @throws SlickException Indicates a failure to load the resources
	 */
	public static void init() throws SlickException {
		Resources.font = new AngelCodeFont("res/font2.fnt","res/font2_00.png");
		Resources.font3 = new AngelCodeFont("res/font3.fnt","res/font3_00.tga");
		Resources.font2 = new AngelCodeFont("res/font.fnt","res/font_00.png");
		Resources.podImage = new Image("res/pod.png");
		Resources.smallPodImage = new Image("res/smallpod.png");
	}
}
