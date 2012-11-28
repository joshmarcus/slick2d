package org.newdawn.fizzy;

import java.util.ArrayList;

/**
 * A shape built up of other shapes
 * 
 * @author kevin
 */
public class CompoundShape implements Shape {
	/** The children of this compound, i.e. the shapes it's built out of */
	private ArrayList<Shape> children = new ArrayList<Shape>();
	/** The body this shape is being used in if any */
	private Body body;
	
	/**
	 * Create a new shape built up of a set of others
	 */
	public CompoundShape() {
	}
	
	@Override
	public Body getBody() {
		return body;
	}
	
	/**
	 * Add a shape to build up this compound
	 * 
	 * @param child The shape to be added
	 */
	public void add(Shape child) {
		children.add(child);
	}
	
	/**
	 * Get the number of shapes in the compound
	 * 
	 * @return The number of shapes in the compound
	 */
	public int getShapeCount() {
		return children.size();
	}
	
	/**
	 * Get a sub-shape of this compound at the given index
	 * 
	 * @param i The index of the shape to retrieve
	 * @return The shape at the requested index
	 */
	public Shape getShape(int i) {
		return children.get(i);
	}
	
	@Override
	public void createInBody(Body body) {
		for (int i=0;i<children.size();i++) {
			children.get(i).createInBody(body);
		}
	}

	@Override
	public ArrayList<org.jbox2d.collision.Shape> getJBoxShapes() {
		ArrayList<org.jbox2d.collision.Shape> shapes = new ArrayList<org.jbox2d.collision.Shape>();

		for (int i=0;i<children.size();i++) {
			shapes.addAll(children.get(i).getJBoxShapes());
		}
		
		return shapes;
	}

	@Override
	public void setDensity(float density) {
		for (int i=0;i<children.size();i++) {
			children.get(i).setDensity(density);
		}
	}

	@Override
	public void setFriction(float friction) {
		for (int i=0;i<children.size();i++) {
			children.get(i).setFriction(friction);
		}
	}

	@Override
	public void setRestitution(float rest) {
		for (int i=0;i<children.size();i++) {
			children.get(i).setRestitution(rest);
		}
	}

}
