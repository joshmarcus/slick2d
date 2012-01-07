package org.newdawn.swingle;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

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

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

/**
 * A generic LWJGL based test for the Swing rendering to OpenGL stuff
 * 
 * @author bgjlib
 * @author kevin
 */
public class TestFrame {
	/** The width of the screen */
	private static final int SCREEN_WIDTH = 800;
	/** The height of the screen */
	private static final int SCREEN_HEIGHT = 600;
	
	/** The frame used to render swing */
	private HeadlessFrame frame;
	/** The texture being rendered with the swing image */
	private int textureID;
	/** The number of open frames */
	private int openFrameCount = 0;
	/** The xoffset of the next frame to be displayed */
	private int xOffset = 30;
	/** The yoffset of the next frame to be displayed */
	private int yOffset = 30;
	/** The ratio between image width and texture width */
	private float widthRatio;
	/** The ratio between image height and texture height */
	private float heightRatio;
	/** The mapping from keycode to character */
	private HashMap chars = new HashMap();
	
	/**
	 * Create a new test frame
	 */
	public TestFrame() {
	}
	
	/**
	 * Start the test 
	 */
	public void start() {
		initGL(SCREEN_WIDTH,SCREEN_HEIGHT);
		init();
		
		while (true) {
			update();
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			render();
			
			Display.update();
			Display.sync(100);

			if (Display.isCloseRequested()) {
				System.exit(0);
			}
		}
	}
	
	/**
	 * Initialise the GL display
	 * 
	 * @param width The width of the display
	 * @param height The height of the display
	 */
	private void initGL(int width, int height) {
		try {
			Display.setDisplayMode(new DisplayMode(width,height));
			Display.create();
			Display.setVSyncEnabled(true);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);        
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);                    
        
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);                
        GL11.glClearDepth(1);                                       
        
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        GL11.glViewport(0,0,width,height);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, height, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
	
	/**
	 * Create a GL texture ID
	 * 
	 * @return The GL texture ID generated
	 */
    public int createTextureID() 
    { 
       IntBuffer tmp = BufferUtils.createIntBuffer(1); 
       GL11.glGenTextures(tmp); 
       return tmp.get(0);
    } 
    
    /**
     * Create a texture to hold the swing rendering
     * 
     * @param width The width of the required image
     * @param height The height of the required image
     * @return The ID of the GL texture crated
     */
	public int createTexture(int width, int height) {
    	int target = GL11.GL_TEXTURE_2D;
    	
        // create the texture ID for this texture 
        int textureID = createTextureID(); 
        
        int minFilter = GL11.GL_LINEAR;
        int magFilter = GL11.GL_LINEAR;
        boolean flipped = false;
        
        // bind this texture 
        GL11.glBindTexture(target, textureID); 
 
        ByteBuffer textureBuffer;
        int texWidth = 1;
        int texHeight = 1;
        
        while (texHeight < height) {
        	texHeight *= 2;
        }
        while (texWidth < width) {
        	texWidth *= 2;
        }
        
        textureBuffer = BufferUtils.createByteBuffer(texWidth*texHeight*4);
    	int srcPixelFormat = GL11.GL_RGBA;
        int componentCount = 4;
        
        widthRatio = width / (float) texWidth;
        heightRatio = height / (float) texHeight;
        
        GL11.glTexParameteri(target, GL11.GL_TEXTURE_MIN_FILTER, minFilter); 
        GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, magFilter); 
        
        GL11.glTexImage2D(target, 
                      0, 
                      GL11.GL_RGBA, 
                      texWidth, 
                      texHeight, 
                      0, 
                      srcPixelFormat, 
                      GL11.GL_UNSIGNED_BYTE, 
                      textureBuffer); 
	
        return textureID;
	}
	
	/**
	 * Initialise the invisible frame and Swing components
	 */
	public void init() {
		frame = new HeadlessFrame(SCREEN_WIDTH,SCREEN_HEIGHT);
		textureID = createTexture(SCREEN_WIDTH,SCREEN_HEIGHT);

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
        desktop.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        
        createFrame(desktop);
        createFrame(desktop);
        createFrame(desktop);
        
		frame.setContentPane(desktop);
		frame.pack();
		frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
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
     * Update based on the events fired by LWJGL. Pipe them into the headless frame.
     */
    public void update() {
    	while (Mouse.next()) {
    		if (Mouse.getEventButton() < 0) {
    			frame.mouseMoved(Mouse.getX(), SCREEN_HEIGHT-Mouse.getY());
    		} else {
	    		if (Mouse.getEventButtonState()) {
	    			frame.mousePressed(Mouse.getEventX(), SCREEN_HEIGHT-Mouse.getEventY(), Mouse.getEventButton());
	    		} else {
	    			frame.mouseReleased(Mouse.getEventX(), SCREEN_HEIGHT-Mouse.getEventY(), Mouse.getEventButton());
	    		}
    		}
    	}
    	
    	while (Keyboard.next()) {
    		if (Keyboard.getEventKeyState()) {
    			chars.put(new Integer(Keyboard.getEventKey()), new Character(Keyboard.getEventCharacter()));
    			frame.keyPressed(Keyboard.getEventKey(), Keyboard.getEventCharacter());
    		} else {
    			Character ch = (Character) chars.get(new Integer(Keyboard.getEventKey()));
    			frame.keyReleased(Keyboard.getEventKey(), ch.charValue());
    		}
    	}
	}
    
    /**
     * Render the frame then the texture thats been generated from the swing
     */
	public void render() {
		frame.updateTexture(textureID);
		
		// alpha test makes things slightly faster in this case
		GL11.glAlphaFunc(GL11.GL_NOTEQUAL, 0);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0,0);
			GL11.glVertex2f(0,0);
			GL11.glTexCoord2f(widthRatio,0);
			GL11.glVertex2f(SCREEN_WIDTH,0);
			GL11.glTexCoord2f(widthRatio,heightRatio);
			GL11.glVertex2f(SCREEN_WIDTH,SCREEN_HEIGHT);
			GL11.glTexCoord2f(0,heightRatio);
			GL11.glVertex2f(0,SCREEN_HEIGHT);
		GL11.glEnd();
		
		GL11.glDisable(GL11.GL_ALPHA_TEST);
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
	 * Entry point to the test
	 * 
	 * @param argv The arguments passed to the app
	 */
	public static void main(String[] argv) {
		TestFrame frame = new TestFrame();
		frame.start();
	}

}
