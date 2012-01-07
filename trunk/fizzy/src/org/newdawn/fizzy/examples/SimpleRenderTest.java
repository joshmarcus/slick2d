package org.newdawn.fizzy.examples;

import org.newdawn.fizzy.Body;
import org.newdawn.fizzy.Circle;
import org.newdawn.fizzy.Rectangle;
import org.newdawn.fizzy.World;

/**
 * Simple test for the debug rendering system
 * 
 * @author kevin
 */
public class SimpleRenderTest extends AbstractTest {
	/**
	 * Create a simulation and run it through the renderer
	 * 
	 * @param argv Arguments passed to the test
	 */
	public static void main(String[] argv) {
		SimpleRenderTest test = new SimpleRenderTest();
		test.startInWindow();
	}

	@Override
	public World createWorld() {
		World world = new World(800,800);
		
		for (int i=0;i<5;i++) {
			Body body = new Body(new Circle(10.0f), i, 20*i);
			world.add(body);
		}
		Body floor = new Body(new Rectangle(200.0f, 10.0f), 0, -50.0f, true);
		world.add(floor);
		
		return world;
	}
}
