package org.newdawn.slick.thingle.demos;

import java.io.File;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.thingle.Thingle;
import org.newdawn.slick.thingle.internal.slick.SlickThinletFactory;
import org.newdawn.slick.thingle.spi.ThingleException;
import org.newdawn.slick.thingle.util.FileChooser;
import org.newdawn.slick.thingle.util.FileChooserListener;

/**
 * The big test demo that has multiple tabs, sub-dialogs etc
 * 
 * @author kevin
 */
public class FileChooserTest extends BasicGame {
	/** The image to display in the background */
	private Image image;
	/** The file chooser under test */
	private FileChooser chooser;
	
	/**
	 * Create a new test
	 */
	public FileChooserTest() {
		super("Thingle File Chooser Demo");
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		Thingle.init(new SlickThinletFactory(container));
		
		container.setShowFPS(false);
		//container.setVSync(true);
		//container.setTargetFrameRate(100);
		container.getGraphics().setBackground(new Color(0.5f,0.7f,1.0f));
		
		Thingle.init(new SlickThinletFactory(container));
		
		image = new Image("res/logo.png");
		try {
			newChooser();
		} catch (ThingleException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Create a new chooser example
	 * 
	 * @throws SlickException Indicates a failure to load resources
	 */
	private void newChooser() throws ThingleException {
		chooser = new FileChooser("Open..", "Open", new FileChooserListener() {

			public void chooserCanceled() {
				System.out.println("CANCEL");
			}

			public void fileSelected(File file) {
				System.out.println("Selected file: "+file);
			}
			
		});
		chooser.show();
	}
	
	public void update(GameContainer container, int delta)
			throws SlickException {
		if (!chooser.isVisible()) {
			if (container.getInput().isKeyPressed(Input.KEY_SPACE)) {
				try {
					newChooser();
				} catch (ThingleException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void render(GameContainer container, Graphics g)
			throws SlickException {
		image.draw(100,10);
		chooser.render();
		
		g.setColor(Color.black);
		g.drawString("FPS: "+container.getFPS(), 530, 2);
		g.drawString("Press Space for a new File Chooser", 100, 550);
	}

	/**
	 * Entry point to the demo
	 * 
	 * @param argv The argments passed to the execution
	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new FileChooserTest(), 600, 600, false);
			container.start();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
