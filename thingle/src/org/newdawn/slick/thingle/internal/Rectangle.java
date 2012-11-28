package org.newdawn.slick.thingle.internal;

/**
 * A simple rectangle implementation for thingle
 * 
 * @author kevin
 */
public class Rectangle {
	/** The x coordiante of the top left hand corner */
	public int x;
	/** The y coordiante of the top left hand corner */
	public int y;
	/** The width of the rectangle */
	public int width;
	/** The height of the rectangle */
	public int height;
	
	/**
	 * Create an empty rectangle
	 */
	public Rectangle() { 
	}
	
	/**
	 * Create a new rectangle
	 * 
	 * @param x The x coordinate of the top left hand corner
	 * @param y The y coordinate of the top left hand corner
	 * @param width The width of the rectangle
	 * @param height The height of the rectangle
	 */
	public Rectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Check if this rectangle contains the given point
	 * 
	 * @param xp The x coordiante of the point to check
	 * @param yp The y coordiante of the point to check
	 * @return True if the rectangle includes the given point
	 */
	public boolean contains(int xp, int yp) {
		if (xp < x) {
			return false;
		}
		if (yp < y) {
			return false;
		}
		
		if (xp > x+width) {
			return false;
		}
		if (yp > y+height) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Check if this rectangle intersects another
	 * 
	 * @param other The other rectangle to check against
	 * @return True if this rectangle intersects this one
	 */
	public boolean intersects(Rectangle other) {
		if ((x > (other.x + other.width)) || ((x + width) < other.x)) {
			return false;
		}
		if ((y > (other.y + other.height)) || ((y + height) < other.y)) {
			return false;
		}
		
        return true;	
	}
	
	/**
	 * Get the intersection of this rectangle with another
	 * 
	 * @param other The other rectangle to intersect with
	 * @return The rectangle that is the intersection
	 */
	public Rectangle intersection(Rectangle other) {
		if (!intersects(other)) {
			return new Rectangle(0,0,0,0);
		}
		
		int x1 = Math.max(x, other.x);
		int y1 = Math.max(y, other.y);
		int x2 = Math.min(x+width, other.x+other.width);
		int y2 = Math.min(y+height, other.y+other.height);
		
		return new Rectangle(x1,y1,(x2-x1),(y2-y1));
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return x+","+y+" "+width+","+height;
	}
}
