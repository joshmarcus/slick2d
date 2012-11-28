package org.newdawn.noodles.tankexamples;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.io.IOException;

import javax.swing.JFrame;

import org.newdawn.noodles.tankexamples.space.SpaceTankClient;
import org.newdawn.noodles.util.Log;

/**
 * A java 2D based "tank game". In fact the tanks are just drawn as rectangles
 * at the right location and orientation.
 * 
 * @author kevin
 */
public class SimpleTankGame extends JFrame implements KeyListener, MouseListener, MouseMotionListener {
	/** The arena the tanks fight it out in */
	private Arena arena = new Arena();
	/** The client connected to the game server */
	private TankGameClient client;
	/** The tank we're controlling locally */
	private Tank localTank;
	/** The last time an update occured */
	private long lastUpdate = System.currentTimeMillis();
	
	/** True if we're pushing the left key */
	private boolean left;
	/** True if we're pushing the right key */
	private boolean right;
	/** True if we're pushing the up key */
	private boolean up;
	/** True if we're pushing the down key */
	private boolean down;
	
	/**
	 * Create a new game
	 * 
	 * @param client The client to connect to the server
	 * @throws IOException Indicates the initial configuration of the client failed
	 */
	public SimpleTankGame(TankGameClient client) throws IOException {
		super("Tank Game");
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600,600);
		setResizable(false);
		
		System.out.println("Connecting...");
		this.client = client;
		client.configure("localhost", 12345, arena);

		System.out.println("Connected");
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		setVisible(true);
	}
	
	/**
	 * Start the game loop
	 */
	public void start() {
		createBufferStrategy(2);
		BufferStrategy strategy = getBufferStrategy();
		
		localTank = new Tank();
		client.addTank(localTank);
		
		while (isVisible()) {
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.black);
			g.fillRect(0,0,600,600);
			
			for (int i=0;i<arena.getTankCount();i++) {
				Tank tank = arena.getTank(i);
				
				g.setColor(Color.red);
				if (localTank == tank) {
					g.setColor(Color.green);
				}
				
				g.translate((int) tank.getX(), (int) tank.getY());
				g.rotate(tank.getAngle());
				g.fillRect(-10, -20, 20, 40);
				g.rotate(-tank.getAngle());
				g.translate((int) -tank.getX(), (int) -tank.getY());
			}
			
			g.dispose();
			strategy.show();
			
			update();
			try { Thread.sleep(10); } catch (Exception e) {}
		}
	}
	
	/**
	 * Update the game, check controls, poll the network etc
	 */
	private void update() {
		int delta = (int) (System.currentTimeMillis() - lastUpdate);
		lastUpdate = System.currentTimeMillis();
		
		if (left) {
			localTank.rotate(-delta * 0.01f);
		}
		if (right) {
			localTank.rotate(delta * 0.01f);
		}
		if (up) {
			localTank.move(-delta * 0.05f);
		}
		if (right) {
			localTank.move(delta * 0.05f);
		}
		
		try {
			client.update(delta);
		} catch (IOException e) {
			Log.error(e);
			System.exit(0);
		}
	}

	/**
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_A) {
			left = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_D) {
			right = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			down = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_W) {
			up = true;
		}
	}

	/**
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_A) {
			left = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_D) {
			right = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			down = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_W) {
			up = false;
		}
	}

	/**
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
	}

	/**
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
	}

	/**
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent e) {
	}

	/**
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {
	}

	/**
	 * Entry point to the tank "game"
	 * 
	 * @param argv The arguments passed in
	 * @throws IOException Indicates a failure to connect to the game server
	 */
	public static void main(String argv[]) throws IOException {
		//SimpleTankGame game = new SimpleTankGame(new LowLevelTankClient());
		//SimpleTankGame game = new SimpleTankGame(new MessageTankClient());
		SimpleTankGame game = new SimpleTankGame(new SpaceTankClient());
		
		game.start();
	}
}
