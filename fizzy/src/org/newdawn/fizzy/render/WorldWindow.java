package org.newdawn.fizzy.render;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.newdawn.fizzy.World;

/**
 * A window to display a simulation running for debug purposes
 * 
 * @author kevin
 */
public class WorldWindow extends Frame {
	/** The world being displayed */
	private World world;
	/** The canvas doing the rendering */
	private WorldCanvas canvas;
	
	/**
	 * Create a new window to display a world using AWT
	 * 
	 * @param world The world to be displayed
	 */
	public WorldWindow(World world) {
		super("Fizzy Render Window");
		this.world = world;
	
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		canvas = new WorldCanvas(world);
		add(canvas);
		setSize(500,500);
	}
	
	/**
	 * Start the rendering
	 */
	public void start() {
		setVisible(true);
		canvas.start();
	}
	
	/**
	 * Stop the rendering
	 */
	public void stop() {
		canvas.stop();
	}
	
	/**
	 * Get the world that is currently being displayed in the render
	 * 
	 * @return The world being displayed 
	 */
	public World getWorld() {
		return world;
	}
}
