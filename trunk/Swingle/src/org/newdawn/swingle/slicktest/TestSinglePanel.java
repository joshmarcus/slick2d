package org.newdawn.swingle.slicktest;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.Log;
import org.newdawn.swingle.HeadlessFrame;

/**
 * A slick based test for the Swing rendering to OpenGL stuff
 * 
 * @author bgjlib
 * @author kevin
 */
public class TestSinglePanel extends BasicGame {
	/** The frame used to render swing */
	private HeadlessFrame frame;
	/** The Slick image rendered into - used to get a texture */
	private Image image;
	/** The main menu panel */
	private JPanel mainMenu;
	/** The angle of the background rotation */
	private float ang;
	/** The dir of fade */
	private float dir = 1;
	/** The current fade in and out */
	private float alpha = 0;
	
	/**
	 * Create a new test frame
	 */
	public TestSinglePanel() {
		super("Swingle Single Component");
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		container.setShowFPS(false);
		container.setVSync(true);
		container.setTargetFrameRate(100);
		container.getGraphics().setBackground(new Color(0.5f,0.7f,0.9f,1f));

		frame = new HeadlessFrame(800,600);
		image = new Image(300,400);

		mainMenu = new JPanel();
//		JPanel panel = new JPanel();
//		frame.setContentPane(panel);
		
		mainMenu.setBounds(250,100,300,400);
		mainMenu.setBackground(new java.awt.Color(1,1,1,0.5f));
		mainMenu.setLayout(new FlowLayout());
		mainMenu.setBorder(BorderFactory.createEmptyBorder(50,0,0,0));
		
		JButton practice = new JButton("Practice Game");
		practice.setFont(practice.getFont().deriveFont(32.0f));
		practice.setPreferredSize(new Dimension(280,50));
		mainMenu.add(practice);
		JButton join = new JButton("Join LAN Game");
		join.setFont(join.getFont().deriveFont(32.0f));
		join.setPreferredSize(new Dimension(280,50));
		mainMenu.add(join);
		JButton start = new JButton("Start LAN Game");
		start.setFont(start.getFont().deriveFont(32.0f));
		start.setPreferredSize(new Dimension(280,50));
		mainMenu.add(start);
		JButton exit = new JButton("Exit Game");
		exit.setFont(exit.getFont().deriveFont(32.0f));
		exit.setPreferredSize(new Dimension(280,50));
		mainMenu.add(exit);
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setBackground(new java.awt.Color(0,0,0,0f));
		frame.getContentPane().setPreferredSize(new Dimension(800, 600));
		frame.getContentPane().add(mainMenu);
		
		frame.pack();
		frame.setSize(800, 600);
	}

    /**
     * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer, int)
     */
	public void update(GameContainer container, int delta) throws SlickException {
		ang += delta * 0.1f;
		alpha += delta * (dir * 0.002f);
		alpha = Math.min(alpha,1);
		alpha = Math.max(alpha,0);
		
		if (container.getInput().isKeyPressed(Input.KEY_F1)) {
			dir = -dir;
		}
		
		frame.setInputEnabled(alpha == 1);
	}

	/**
	 * @see org.newdawn.slick.Game#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.translate(400, 300);
		
		g.rotate(0,0,ang);
		for (int a=0;a<360;a+=40) {
			g.rotate(0,0,40);
			g.setColor(Color.red);
			g.fillRect(-40,-250,80,20);
		}
		
		g.resetTransform();
		
		frame.updateTexture(image.getTexture().getTextureID(), mainMenu);
		
		if (alpha != 0) {
			GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
			image.draw(mainMenu.getX(),mainMenu.getY(),new Color(alpha,alpha,alpha,alpha));
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		
		Texture.bindNone();
		
		g.setColor(Color.black);
		g.drawString("FPS: "+container.getFPS(), 10, 200);
		g.drawString("F1 - Toggle GUI", 10, 220);
	}

	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new TestSinglePanel(), 800, 600, false);
			container.start();
		} catch (Exception e) {
			Log.error(e);
		}
	}

	/**
	 * @see org.newdawn.slick.BasicGame#mouseMoved(int, int, int, int)
	 */
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		super.mouseMoved(oldx, oldy, newx, newy);
		
		frame.mouseMoved(newx, newy);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#mousePressed(int, int, int)
	 */
	public void mousePressed(int button, int x, int y) {
		super.mousePressed(button, x, y);
		
		frame.mousePressed(x, y, button);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#mouseReleased(int, int, int)
	 */
	public void mouseReleased(int button, int x, int y) {
		super.mouseReleased(button, x, y);
		
		frame.mouseReleased(x, y, button);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#keyPressed(int, char)
	 */
	public void keyPressed(int key, char c) {
		super.keyPressed(key, c);
	
		frame.keyPressed(key, c);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#keyReleased(int, char)
	 */
	public void keyReleased(int key, char c) {
		super.keyReleased(key, c);
		
		frame.keyReleased(key, c);
	}
}
