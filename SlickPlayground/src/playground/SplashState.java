package playground;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.util.FastTrig;

/**
 * The state showing the startup screen
 *
 * @author kevin
 */
public class SplashState extends State {
	/** The unique ID for this state */
	public static final int ID = 6;

	/** The background logo */
	private Image backgroundLogo;
	/** The offset of the progress */
	private int ofs;
	
	/**
	 * Create a new splash state
	 * 
	 * @throws SlickException Indicates a failure to load required resources
	 */
	public SplashState() throws SlickException {
		backgroundLogo = new Image("res/logo.png");
	}
	
	/**
	 * @see playground.State#backSelected()
	 */
	public void backSelected() {
	}

	/**
	 * @see playground.State#enter(int, playground.Playground)
	 */
	public void enter(int lastState, Playground app) {
	}

	/**
	 * @see playground.State#getBackLabel()
	 */
	public String getBackLabel() {
		return null;
	}

	/**
	 * @see playground.State#getNextLabel()
	 */
	public String getNextLabel() {
		return null;
	}

	/**
	 * @see playground.State#getPrevLabel()
	 */
	public String getPrevLabel() {
		return null;
	}

	/**
	 * @see playground.State#leave(playground.Playground)
	 */
	public void leave(Playground app) {
	}

	/**
	 * @see playground.State#nextSelected()
	 */
	public void nextSelected() {
	}

	/**
	 * @see playground.State#prevSelected()
	 */
	public void prevSelected() {
	}

	/**
	 * @see playground.State#reinit()
	 */
	public void reinit() {
	}

	/**
	 * @see playground.State#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g) {
		TextureImpl.bindNone();
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor3f(1f, 1f, 1f);
			GL11.glVertex3f(0, 0, 0);
			GL11.glColor3f(0.9f, 0.9f, 0.9f);
			GL11.glVertex3f(800, 0, 0);
			GL11.glColor3f(0.8f, 0.8f, 0.8f);
			GL11.glVertex3f(800, 600, 0);
			GL11.glColor3f(0.7f, 0.7f, 0.7f);
			GL11.glVertex3f(0, 600, 0);
		GL11.glEnd();
		
		backgroundLogo.draw(300,100,200,200,new Color(0,0,0,0.1f));
		Resources.font.drawString(300, 300, "PLAYGROUND",new Color(0,0,0,0.1f));
		Resources.font3.drawString(330, 430, "Getting Games",new Color(0,0,0,0.1f));
		int x = (int) (FastTrig.cos(ofs * 0.005f) * 100);
		g.setColor(new Color(0,0,0,0.2f));
		g.fillRect(390+x,400,20,20);
		g.setColor(new Color(0,0,0,0.5f));
		g.drawRect(290,400,220,20);
	}

	/**
	 * @see playground.State#update(org.newdawn.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta) {
		ofs += delta;
	}

	/**
	 * @see playground.State#giveFocus(playground.Playground)
	 */
	public void giveFocus(Playground app) {
	}
}
