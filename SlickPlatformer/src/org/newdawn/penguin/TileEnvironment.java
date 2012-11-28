package org.newdawn.penguin;

import java.util.ArrayList;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.CollisionListener;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Polygon;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.GeomUtil;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;

/**
 * An environment based on a tilemap. The tile shapes are combined into more 
 * performant polygons for the purpose of the physical world as part of the 
 * initialisatin.
 * 
 * @author kevin
 */
public class TileEnvironment extends AbstractEnvironment {
	/** The images to draw for the tiles */
	private Image[][] images;
	/** The shape to use in the physical world */
	private Shape[][] shapes;
	/** The width in tiles of the environment */
	private int width;
	/** The height in tiles of the environment */
	private int height;
	/** The utility used to combine tile shapes */
	private GeomUtil util = new GeomUtil();
	
	/** The width in pixels of each tile */
	private int tileWidth = 32;
	/** The height in pixels of each tile */
	private int tileHeight = 32;
	/** The bounds of the entire environment */
	private Rectangle bounds;
	
	/**
	 * Create a new tile environment
	 * @param width
	 * @param height
	 */
	public TileEnvironment(int width, int height) {
		images = new Image[width][height];
		shapes = new Shape[width][height];
		this.width = width;
		this.height = height;
		
		world.addListener(new CollisionListener() {
			public void collisionOccured(CollisionEvent event) {
			}
		});
		
		calcBounds();
	}
	
	/**
	 * @see org.newdawn.penguin.Environment#getBounds()
	 */
	public Rectangle getBounds() {
		return bounds;
	}
	
	/**
	 * Set the image size per tile in pixels
	 * 
	 * @param w The width in pixels of each tile
	 * @param h The height in pixels of each tile
	 */
	public void setImageSize(int w, int h) {
		tileWidth = w;
		tileHeight = h;
		calcBounds();
	}
	
	/**
	 * Calculate the environment bounds
	 */
	private void calcBounds() {
		bounds = new Rectangle(0,0,width*tileWidth,height*tileHeight);
	}
	
	/**
	 * Set the tile at a given location
	 * 
	 * @param x The x coordinate of the tile to set
	 * @param y The y coordinate of the tile to set
	 * @param tile The tile to apply at the given location
	 */
	public void setTile(int x, int y, Tile tile) {
		images[x][y] = tile.getImage();
		shapes[x][y] = tile.getShape().transform(Transform.createTranslateTransform(x,y));
	}
	
	/**
	 * Initialise the tile map
	 */
	public void init() {
		// section the tile up into quads to improve performance. At the moment
		// we're using one big quad 
		int sectionX = width;
		int sectionY = height;
		
		for (int x=0;x<width;x+=sectionX) {
			for (int y=0;y<height;y+=sectionY) {
				buildSection(x,y,sectionX,sectionY);
			}
		}
	}
	
	/**
	 * Build a section of the map by combining the tile shapes 
	 * 
	 * @param xp The x position of the section to build
	 * @param yp The y position of the section to build 
	 * @param width The width of the section to build
	 * @param height The height of the section to build
	 */
	private void buildSection(int xp, int yp, int width, int height) {
		// go through all the shapes in the section building a list
		ArrayList<Shape> shapes = new ArrayList<Shape>();
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				if (xp+x >= this.width) {
					continue;
				}
				if (yp+y >= this.height) {
					continue;
				}
				
				if (this.shapes[xp+x][yp+y] != null) {
					shapes.add(this.shapes[xp+x][yp+y]);
				}
			}
		}
		
		// combine the shapes together then build a static 
		// body for resulting shape
		ArrayList<Shape> combines = combine(shapes);
		for (int i=0;i<combines.size();i++) {
			Shape shape = combines.get(i);
			float[] pts = shape.getPoints();
			Vector2f[] vecs = new Vector2f[(pts.length / 2)];
			for (int j=0;j<vecs.length;j++) {
				vecs[j] = new Vector2f(pts[j*2]*tileWidth,pts[(j*2)+1]*tileHeight);
			}
			
			Polygon poly = new Polygon(vecs);
			StaticBody body = new StaticBody(poly);
			body.setFriction(1f);
			body.setRestitution(1f);
			world.add(body);
		}
	}

	/**
	 * Combine the shapes given into optimal shapes
	 *  
	 * @param shapes The shapes to combine
	 * @return The list of combined shapes
	 */
	private ArrayList<Shape> combineImpl(ArrayList<Shape> shapes) {
		ArrayList<Shape> result = new ArrayList<Shape>(shapes);

		// combine each shape with each other one
		for (int i = 0; i < shapes.size(); i++) {
			Shape first = (Shape) shapes.get(i);
			for (int j = i + 1; j < shapes.size(); j++) {
				Shape second = (Shape) shapes.get(j);

				// if only one shape is returned then remove the combined pair
				// and add the new one, otherwise leave them where they are
				Shape[] joined = util.union(first, second);
				if (joined.length == 1) {
					result.remove(first);
					result.remove(second);
					result.add(joined[0]);
					return result;
				}
			}
		}

		return result;
	}
	
	/**
	 * Combine the shapes by looping combining until there
	 * are no removed shapes.
	 * 
	 * @param shapes The shapes to combine
	 * @return The new list of shapes cleaned up
	 */
	private ArrayList<Shape> combine(ArrayList<Shape> shapes) {
		ArrayList<Shape> last = shapes;
		ArrayList<Shape> current = shapes;
		boolean first = true;

		while ((current.size() != last.size()) || (first)) {
			first = false;
			last = current;
			current = combineImpl(current);
		}

		ArrayList<Shape> pruned = new ArrayList<Shape>();
		for (int i = 0; i < current.size(); i++) {
			pruned.add(current.get(i).prune());
		}
		return pruned;
	}
	
	/**
	 * @see org.newdawn.penguin.Environment#render(org.newdawn.slick.Graphics)
	 */
	public void render(Graphics g) {
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				if (images[x][y] != null) {
					images[x][y].draw(x*tileWidth,y*tileHeight);
				}
			}
		}
		
		for (int i=0;i<entities.size();i++) {
			entities.get(i).render(g);
		}
	}
	
	/**
	 * @see org.newdawn.penguin.Environment#renderBounds(org.newdawn.slick.Graphics)
	 */
	public void renderBounds(Graphics g) {
		g.scale(tileWidth, tileHeight);

		g.setColor(Color.yellow);
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				if (shapes[x][y] != null) {
					g.draw(shapes[x][y]);
				}
			}
		}

		g.scale(1.0f/tileWidth, 1.0f/tileHeight);
		g.setColor(Color.white);
		g.setLineWidth(2);
		BodyList list = world.getBodies();
		for (int i=0;i<list.size();i++) {
			Body body = list.get(i);
			net.phys2d.raw.shapes.Shape shape = body.getShape();
			
			if (shape instanceof Polygon) {
				Polygon poly = (Polygon) shape;
				org.newdawn.slick.geom.Polygon p = new org.newdawn.slick.geom.Polygon();
				ROVector2f[] verts = poly.getVertices();
				for (int k=0;k<verts.length;k++) {
					p.addPoint(verts[k].getX(), verts[k].getY());
				}
				
				g.draw(p);
			}
			
			if (shape instanceof Box) {
				Box b = (Box) shape;
				Vector2f[] verts = b.getPoints(body.getPosition(), body.getRotation());
				
				org.newdawn.slick.geom.Polygon p = new org.newdawn.slick.geom.Polygon();
				for (int k=0;k<verts.length;k++) {
					p.addPoint(verts[k].getX(), verts[k].getY());
				}
				
				g.draw(p);
			}
		}

		g.setLineWidth(1);
	}
	
}