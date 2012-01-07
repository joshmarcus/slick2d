package org.newdawn.swingle.slicktest;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

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
public class TestFrame extends BasicGame {
	/** The frame used to render swing */
	private HeadlessFrame frame;
	/** The Slick image rendered into - used to get a texture */
	private Image image;
	/** The number of open frames */
	private int openFrameCount = 0;
	/** The xoffset of the next frame to be displayed */
	private int xOffset = 30;
	/** The yoffset of the next frame to be displayed */
	private int yOffset = 30;
	/** The angle of the background rotation */
	private float ang;
	/** The dir of fade */
	private float dir = 1;
	/** The current fade in and out */
	private float alpha = 0;
	
	/**
	 * Create a new test frame
	 */
	public TestFrame() {
		super("Swingle Test Frame");
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		container.setShowFPS(false);
		container.getGraphics().setBackground(new Color(0.5f,0.7f,0.9f,1f));

		frame = new HeadlessFrame(800,600);
		image = new Image(800,600);

		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File");
		file.add(new JMenuItem("Open"));
		file.add(new JMenuItem("Close"));
		JMenuItem quit = new JMenuItem("Quit");
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		file.add(quit);
		bar.add(file);
		
		frame.setJMenuBar(bar);

		JDesktopPane desktop = new JDesktopPane();
		desktop.setOpaque(false);
		desktop.setBackground(new java.awt.Color(0,0,0,0f));
        desktop.setPreferredSize(new Dimension(800, 600));
        
        createFrame(desktop);
        createFrame(desktop);
        createFrame(desktop);
        
		frame.setContentPane(desktop);
		frame.pack();
		frame.setSize(800, 600);
	}

	/**
	 * Create an internal frame
	 * 
	 * @param desktop The desktop the frame will belong to
	 */
    private void createFrame(JDesktopPane desktop)
    {
    	ExampleFrame iframe = new ExampleFrame();
        iframe.setVisible(true);
        desktop.add(iframe);
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
		
		frame.updateTexture(image.getTexture().getTextureID());
		
		if (alpha != 0) {
			// alpha test makes things slightly faster in this case
			GL11.glAlphaFunc(GL11.GL_NOTEQUAL, 0);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			image.draw(0,0,new Color(1,1,1f,alpha));
			GL11.glDisable(GL11.GL_ALPHA_TEST);
		}
		
		Texture.bindNone();
		
		g.setColor(Color.black);
		g.drawString("FPS: "+container.getFPS(), 10, 200);
		g.drawString("F1 - Toggle GUI", 10, 220);
	}

	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new TestFrame(), 800, 600, false);
			container.start();
		} catch (Exception e) {
			Log.error(e);
		}
	}

	/**
	 * A test frame to be displayed within the desktop
	 * 
	 * @author bjglib
	 */
	private class ExampleFrame extends JInternalFrame {
		/**
		 * Create a new test frame 
		 */
		public ExampleFrame() {
	        super("Document #" + (++openFrameCount), true, true, true, true);
	        setSize(300,300);

	        setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
	        
	        JPanel p = new JPanel( new BorderLayout() );
	        p.setBackground(java.awt.Color.red);
	        JPanel tmp = new JPanel();
	        JButton b = new JButton("Start Game");
	        b.setFocusable(false);
	        b.setFont(b.getFont().deriveFont(32.0f));
	        b.addActionListener( new ActionListener()
	        {
	            public void actionPerformed(ActionEvent e)
	            {
	                System.out.println( "test button on internal frame " + getTitle() );
	            }
	        });
	        tmp.add( b );
	        p.add( tmp, BorderLayout.SOUTH );
	        
	        tmp = new JPanel();
	        JComboBox cb = new JComboBox( new Object[]{ "item 1", "item 2", "item 3" } );
	        tmp.add( cb );
	        p.add( tmp, BorderLayout.NORTH );
	        
	        JTabbedPane tabs = new JTabbedPane();
	        tabs.addTab( "Test", p );
	        
	        JTextArea textArea = new JTextArea();
	        textArea.setEditable(true);
	        textArea.setFocusable(true);
	        textArea.append("Some sample text");
	        tabs.addTab( "Test 2", new JScrollPane(textArea) );
	        this.add( tabs );
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
