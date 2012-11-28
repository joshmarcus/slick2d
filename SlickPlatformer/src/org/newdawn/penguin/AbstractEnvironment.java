package org.newdawn.penguin;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Polygon;

/**
 * The common bits of all environments. Holds a physics world, allows addition
 * of entities and their phyiscal representation.
 * 
 * @author kevin
 */
public abstract class AbstractEnvironment implements Environment {
	/** The physical world the environment provides to it's entities */
	protected World world = new World(new Vector2f(0,10), 20); 

	/** The entities list */
	protected ArrayList<Entity> entities = new ArrayList<Entity>();
	/** The amount time in ms passed since last update */
	private int totalDelta;
	/** The amount of time to pass before updating the physics world */
	private int stepSize = 5;

	/**
	 * Add an entity to the environment. This will include it's physical
	 * body in the world.
	 * 
	 * @param entity The entity to be added
	 */
	public void addEntity(Entity entity) {
		if (entity.getBody() != null) {
			world.add(entity.getBody());
		}
		
		entities.add(entity);
		entity.setWorld(world);
	}
	
	/**
	 * @see org.newdawn.penguin.Environment#update(int)
	 */
	public void update(int delta) {
		boolean first = true;
		
		totalDelta += delta;
		while (totalDelta > stepSize) {
			world.step(stepSize * 0.01f);
			totalDelta -= stepSize;

			if (first) {
				first = false;
				for (int i=0;i<entities.size();i++) {
					entities.get(i).preUpdate(delta);
				}
			}
			
			for (int i=0;i<entities.size();i++) {
				entities.get(i).update(stepSize);
			}
		}
	}
}
