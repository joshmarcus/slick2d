package playground;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import playground.games.GameRecord;

/**
 * A POD to display a simple text message in the middle of the
 * screen.
 *
 * @author kevin
 */
public class TextPod extends Pod {
	/** The font to render the text with */
	private Font font;
	/** The colour to use for shadowing the text */
	private Color shadow = new Color(0,0,0,0.5f);
	/** The text to be displayed */
	private String text;
	/** The x offset */
	private int xofs;
	/** The y offset */
	private int yofs;
	
	/**
	 * Create a new POD to display a single piece of text
	 * 
	 * @param listener The listener to be notified of updates to this POD
	 * @param font The font to use to render the text
	 * @param text The text to be displayed
	 */
	public TextPod(PodListener listener, Font font, String text) {
		this.listener = listener;
		this.font = font;
		this.text = text;
		rect = new Rectangle(0,0,0,0);
		yofs = 250;
	}

	/**
	 * Create a new POD to display a single piece of text
	 * 
	 * @param listener The listener to be notified of updates to this POD
	 * @param font The font to use to render the text
	 * @param text The text to be displayed
	 * @param x The x position of the text
	 * @param y The y position of the text
	 */
	public TextPod(PodListener listener, Font font, String text, int x, int y) {
		this.listener = listener;
		this.font = font;
		this.xofs = x;
		this.yofs = y;
		this.text = text;
		rect = new Rectangle(0,0,0,0);
	}

	/**
	 * Create a new POD to display a single piece of text
	 * 
	 * @param listener The listener to be notified of updates to this POD
	 * @param font The font to use to render the text
	 * @param text The text to be displayed
	 * @param y The y position of the text
	 */
	public TextPod(PodListener listener, Font font, String text, int y) {
		this.listener = listener;
		this.font = font;
		this.yofs = y;
		this.text = text;
		rect = new Rectangle(0,0,0,0);
	}
	
	/**
	 * @see playground.Pod#draw(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void draw(GameContainer container, Graphics g) {
		g.setFont(font);
		if (x == 0) {
			center(container, font, g, text, (int) y+yofs);
		} else {
			g.setColor(shadow);
			g.drawString(text, (int) (x+xofs+1), (int) (y+yofs+1));
			g.setColor(Color.white);
			g.drawString(text, (int) (x+xofs), (int) (y+yofs));
		}
	}

	/**
	 * Center a piece of text on the display
	 * 
	 * @param container The container holding the display
	 * @param font The font to render the text with
	 * @param g The graphics context to render the text to
	 * @param text The text to be render
	 * @param y The y coordinate to render the text at
	 */
	private void center(GameContainer container, Font font, Graphics g, String text, int y) {
		int xo = (container.getWidth()-font.getWidth(text))/2;
		g.setColor(shadow);
		g.drawString(text, xo+1, y+1);
		g.setColor(Color.white);
		g.drawString(text, xo, y);
	}
}
