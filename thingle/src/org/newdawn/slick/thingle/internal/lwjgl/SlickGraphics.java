package org.newdawn.slick.thingle.internal.lwjgl;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.thingle.internal.Dimension;
import org.newdawn.slick.thingle.internal.Rectangle;
import org.newdawn.slick.thingle.spi.ThingleColor;
import org.newdawn.slick.thingle.spi.ThingleFont;
import org.newdawn.slick.thingle.spi.ThingleGraphics;
import org.newdawn.slick.thingle.spi.ThingleImage;

/**
 * A binding that uses slick to render what thinlet requires
 * 
 * @author kevin
 */
public class SlickGraphics implements ThingleGraphics {
	/** The graphics context to render to */
	private Graphics g;
	/** The font currently set */
	private ThingleFont currentFont;
	private int translationX, translationY;
	
	/**
	 * Create a new slick binding
	 * 
	 * @param g The graphics context to render to
	 */
	public SlickGraphics(Graphics g) {
		this.g = g;
	}
	
	/**
	 * Return the graphics context to which we're drawing
	 * 
	 * @return The graphics context to which we're rendering
	 */
	public Graphics getGraphics() {
		return g;
	}
	
	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleGraphics#getFont()
	 */
	public ThingleFont getFont() {
		return currentFont;
	}
	
	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleGraphics#drawImage(org.newdawn.slick.Image, int, int)
	 */
	public void drawImage(ThingleImage image, int x, int y) {
		g.drawImage(((ImageWrapper) image).getSlickImage(), x, y);
	}
	
	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleGraphics#drawLine(int, int, int, int)
	 */
	public void drawLine(int x, int y, int ax, int ay) {
		if ((x == ax) || (y == ay)) {
			int x1 = Math.min(x,ax);
			int x2 = Math.max(x,ax)+1;
			int y1 = Math.min(y,ay);
			int y2 = Math.max(y,ay)+1;
			
			g.fillRect(x1,y1,(x2-x1),(y2-y1));
			return;
		}
		
		g.drawLine(x,y,ax,ay);
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleGraphics#drawImage(org.newdawn.slick.Image, int, int, int, int, int, int, int, int)
	 */
	public void drawImage(ThingleImage image, int x, int y, int x2, int y2, int sx1, int sy1, int sx2, int sy2){
		g.drawImage(((ImageWrapper) image).getSlickImage(), x, y, x2, y2, sx1, sy1, sx2, sy2);
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleGraphics#drawOval(int, int, int, int)
	 */
	public void drawOval(int x, int y, int width, int height) {
		g.drawOval(x, y, width, height);
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleGraphics#drawRect(int, int, int, int)
	 */
	public void drawRect(int x, int y, int width, int height) {
		g.drawRect(x,y,width,height);
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleGraphics#drawString(java.lang.String, int, int)
	 */
	public void drawString(String str, int x, int y) {
		g.drawString(str, x, y);
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleGraphics#fillOval(int, int, int, int)
	 */
	public void fillOval(int x, int y, int width, int height) {
		g.fillOval(x, y, width, height);
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleGraphics#fillRect(int, int, int, int)
	 */
	public void fillRect(int x, int y, int width, int height) {
		g.fillRect(x,y,width,height);
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleGraphics#getClip()
	 */
	public Rectangle getClip() {
		org.newdawn.slick.geom.Rectangle rect = g.getWorldClip();
		if (rect == null) {
			return new Rectangle(0,0,2000,2000);
		}
		return new Rectangle((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleGraphics#setClip(int, int, int, int)
	 */
	public void setClip(int x, int y, int width, int height) {
		g.setWorldClip(x, y, width, height);
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleGraphics#setColor(org.newdawn.slick.thingle.spi.ThingleColor)
	 */
	public void setColor(ThingleColor color) {
		g.setColor(((ColorWrapper) color).getSlickColor());
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleGraphics#setFont(org.newdawn.slick.Font)
	 */
	public void setFont(ThingleFont font) {
		currentFont = font;
		g.setFont(((FontWrapper) font).getSlickFont());
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleGraphics#translate(int, int)
	 */
	public void translate(int x, int y) {
		translationX += x;
		translationY += y;
		g.translate(x, y);
	}

	public int getTranslationY () {
		return translationY;
	}

	public int getTranslationX () {
		return translationX;
	}
}
