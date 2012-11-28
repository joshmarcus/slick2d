package org.newdawn.noodles.space;

import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JFrame;

/**
 * Simple test class to display the state of the blobs that are known about
 * at each stage, client 1 -> server -> client 2
 * 
 * @author kevin
 */
public class Display extends JFrame {
	/** The list of blobs we know about */
	private ArrayList<Blob> blobs = new ArrayList<Blob>();
	/** The last update of the screen */
	private long lastRedraw = System.currentTimeMillis();
	
	/**
	 * Create a new test display
	 * 
	 * @param title The title of the frame
	 */
	public Display(String title) {
		super(title);
		
		setSize(400,400);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Consider updating the screen if it hasn't been done recently
	 */
	public void refresh() {
		if (System.currentTimeMillis() - lastRedraw > 20) {
			repaint(0);
			lastRedraw = System.currentTimeMillis();
		}
	}
	
	/**
	 * Add a blob we know about to the display
	 * 
	 * @param blob The blob we know about
	 */
	public void add(Blob blob) {
		blobs.add(blob);
	}
	
	/**
	 * Remove a blob from the display
	 * 
	 * @param blob The blob to remove
	 */
	public void remove(Blob blob) {
		blobs.remove(blob);
	}
	
	/**
	 * @see java.awt.Container#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		super.paint(g);
		
		for (int i=0;i<blobs.size();i++) {
			g.drawString(blobs.get(i).toString(), 50,100+(i*20));
		}
	}
}
