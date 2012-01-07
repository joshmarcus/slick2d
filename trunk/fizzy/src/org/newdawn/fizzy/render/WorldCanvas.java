package org.newdawn.fizzy.render;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import org.newdawn.fizzy.Body;
import org.newdawn.fizzy.Circle;
import org.newdawn.fizzy.CompoundShape;
import org.newdawn.fizzy.Polygon;
import org.newdawn.fizzy.Rectangle;
import org.newdawn.fizzy.Shape;
import org.newdawn.fizzy.World;

/**
 * A canvas that uses Java2D to display the world for debug
 * 
 * @author kevin
 */
public class WorldCanvas extends Canvas {
	/** The world we're displaying */
	private World world;
	/** True if the simulation is running */
	private boolean running;
	
	/**
	 * Create a new canvas
	 * 
	 * @param world The to be displayed
	 */
	public WorldCanvas(World world) {
		this.world = world;
	}
	
	/** 
	 * Start the render loop
	 */
	public void start() {
		createBufferStrategy(2);
		running = true;
		runLoop();
	}
	
	/**
	 * The logic and render loop
	 */
	private void runLoop() {
		BufferStrategy buffer = getBufferStrategy();
		
		while (running) {
			world.update(0.003f);
			
			Graphics2D g = (Graphics2D) buffer.getDrawGraphics();
			
			g.clearRect(0,0,getWidth(),getHeight());
			g.translate(getWidth()/2,getHeight()/2);
			g.scale(1,-1);
			for (int i=0;i<world.getBodyCount();i++) {
				drawBody(g, world.getBody(i));
			}
			
			g.dispose();
			buffer.show();
		}
	}
	
	/**
	 * Stop the rendering
	 */
	public void stop() {
		running = false;
	}
	
	/**
	 * Get the world being rendered
	 * 
	 * @return The world being renderer
	 */
	public World getWorld() {
		return world;
	}
	
	/**
	 * Draw a body to the canvas
	 * 
	 * @param g The graphics context on which to draw
	 * @param body The body to be rendered
	 */
	private void drawBody(Graphics2D g, Body body) {
		Shape shape = body.getShape();
		drawShape(g,body,shape);
	}

	/**
	 * Draw a shape 
	 * 
	 * @param g The graphics context to render to
	 * @param body The body to be rendered
	 * @param shape The shape representing the body
	 */
	private void drawShape(Graphics2D g, Body body, Shape shape) {
		if (shape instanceof Circle) {
			drawCircle(g, body, (Circle) shape);
		}
		if (shape instanceof Rectangle) {
			drawRectangle(g, body, (Rectangle) shape);
		}
		if (shape instanceof Polygon) {
			drawPolygon(g, body, (Polygon) shape);
		}
		if (shape instanceof CompoundShape) {
			drawCompound(g, body, (CompoundShape) shape);
		}
	}

	/**
	 * Draw a compound shape 
	 * 
	 * @param g The graphics context to render to
	 * @param body The body to be rendered
	 * @param shape The shape representing the body
	 */
	private void drawCompound(Graphics2D g, Body body, CompoundShape shape) {
		int count = shape.getShapeCount();
		for (int i=0;i<count;i++) {
			drawShape(g, body, shape.getShape(i));
		}
	}
	
	/**
	 * Draw a body represented by a circle
	 * 
	 * @param g The graphics context to render to
	 * @param body The body to be rendered
	 * @param shape The shape representing the body
	 */
	private void drawCircle(Graphics2D g, Body body, Circle shape) {
		g = (Graphics2D) g.create();
		g.translate(body.getX(), body.getY());
		g.rotate(body.getRotation());
		
		float radius = shape.getRadius();
		
		g.setColor(Color.black);
		g.drawOval((int) -radius,(int) -radius,(int) (radius*2),(int) (radius*2));
		g.drawLine(0,0,0,(int) -radius);
	}
	
	/**
	 * Draw a body represented by a rectangle
	 * 
	 * @param g The graphics context on which to render
	 * @param body The body to be rendered
	 * @param shape The shape representing the body
	 */
	private void drawRectangle(Graphics2D g, Body body, Rectangle shape) {
		g = (Graphics2D) g.create();
		g.translate(body.getX(), body.getY());
		g.rotate(body.getRotation());
		g.translate(shape.getXOffset(), shape.getYOffset());
		g.rotate(shape.getAngleOffset());
		
		float width = shape.getWidth();
		float height = shape.getHeight();
		
		g.setColor(Color.black);
		g.drawRect((int) -(width/2),(int) -(height/2),(int) width,(int) height);
	}
	
	/**
	 * Draw a body represented by a polygon
	 * 
	 * @param g The graphics context on which to render
	 * @param body The body to be rendered
	 * @param shape The shape representing the body
	 */
	private void drawPolygon(Graphics2D g, Body body, Polygon shape) {
		g = (Graphics2D) g.create();
		g.translate(body.getX(), body.getY());
		g.rotate(body.getRotation());
		g.translate(shape.getXOffset(), shape.getYOffset());
		g.rotate(shape.getAngleOffset());

		g.setColor(Color.black);
		for (int i=0;i<shape.getPointCount();i++) {
			int n = i+1;
			if (n >= shape.getPointCount()) {
				n = 0;
			}
			g.drawLine((int) shape.getPointX(i), (int) shape.getPointY(i),
					   (int) shape.getPointX(n), (int) shape.getPointY(n));
		}
		
	}
}
