package org.newdawn.fizzy.examples;

import org.newdawn.fizzy.Body;
import org.newdawn.fizzy.CompoundShape;
import org.newdawn.fizzy.Polygon;
import org.newdawn.fizzy.Rectangle;
import org.newdawn.fizzy.World;

/**
 * Simple test for the debug rendering system
 * 
 * @author kevin
 */
public class CompoundTest extends AbstractTest {
	/**
	 * Create a simulation and run it through the renderer
	 * 
	 * @param argv Arguments passed to the test
	 */
	public static void main(String[] argv) {
		CompoundTest test = new CompoundTest();
		test.startInWindow();
	}

	@Override
	public World createWorld() {
		World world = new World(800,800);
		
		CompoundShape shape = new CompoundShape();
		shape.add(new Rectangle(20,10).setOffset(-5, 0, 0));
		shape.add(new Rectangle(10,10).setOffset(0, 10, 0));
		
		Body compound = new Body(shape, 0, 0.0f, false);
		compound.setRestitution(0.1f);
		world.add(compound);
		compound.setRotation(0.5f);
		
		shape = new CompoundShape();
		shape.add(new Rectangle(10,10));
		shape.add(new Rectangle(10,10).setOffset(-10, 0, 0));
		shape.add(new Rectangle(10,10).setOffset(0, 10, 0));
		
		// get your winding right or get inverted shapes
		Polygon spike = new Polygon();
		spike.addPoint(10, 15);
		spike.addPoint(0, 30);
		spike.addPoint(-10, 15);
		shape.add(spike);
		
		compound = new Body(shape, -10.0f, 80.0f, false);
		compound.setRestitution(0.1f);
		world.add(compound);
		compound.setRotation(0.5f);
		
		Body floor = new Body(new Rectangle(200.0f, 10.0f), 0, -50.0f, true);
		floor.setRestitution(0.1f);
		world.add(floor);
		floor = new Body(new Rectangle(10.0f, 100.0f), -105.0f, 0, true);
		floor.setRestitution(0.1f);
		world.add(floor);
		floor = new Body(new Rectangle(10.0f, 100.0f), 105.0f, 0, true);
		floor.setRestitution(0.1f);
		world.add(floor);
		
		return world;
	}
}
