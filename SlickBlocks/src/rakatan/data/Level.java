package rakatan.data;

import java.io.PrintStream;
import java.util.ArrayList;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.CollisionListener;
import net.phys2d.raw.World;
import net.phys2d.raw.strategies.BruteCollisionStrategy;

import org.newdawn.slick.Graphics;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class Level implements CollisionListener {
	public static final float SCALE = 1;
	
	private int nextID = 1;
	
	private World world;
	private ArrayList elements = new ArrayList();
	
	private int remainder = 0;
	private int step = 5;
	private long lastLevelUpdate;
	private ArrayList listeners = new ArrayList();
	
	public Level() {
		world = new World(new Vector2f(0,1f * SCALE), 30, new BruteCollisionStrategy());
		world.enableRestingBodyDetection(0.01f, 0.000001f, 100f);
		world.addListener(this);
		world.setDamping(1);
	}
	
	public void addListener(LevelListener listener) {
		listeners.add(listener);
	}
	
	public void clearResting(LevelElement from) {
		from.getBody().setIsResting(false);
		world.clearArbiters(from.getBody());
	}
	
	public void clear() {
		world.clear();
		elements.clear();
	}
	
	public ArrayList getCollisions(final LevelElement source) {
		final ArrayList bodies = new ArrayList();
		
		CollisionListener listener = new CollisionListener() {
			public void collisionOccured(CollisionEvent event) {
				if ((source.containsBody(event.getBodyA())) || (source.containsBody(event.getBodyB()))) {
					bodies.add(event);
				}
			}
		};
		
		world.addListener(listener);
		world.collide(0);
		world.removeListener(listener);
	
		return bodies;
	}
	
	public void update(int delta) {
		delta += remainder;
		remainder = delta % step;
		for (int i = 0; i < delta / step; i++) {
			world.step(step / 30.0f);
		}
		
		for (int i=0;i<elements.size();i++) {
			((LevelElement) elements.get(i)).update(delta);
		}
	}
	
	public LevelElement getElementAt(int x, int y) {
		float distance = Float.MAX_VALUE;
		LevelElement found = null;
		
		for (int i=0;i<elements.size();i++) {
			LevelElement e = ((LevelElement) elements.get(i));
			if (e.contains(x,y)) {
				return e;
			}
		}
		
		return found;
	}
	
	public void render(Graphics g) {
		g.scale(1/SCALE, 1/SCALE);
		for (int i=0;i<elements.size();i++) {
			((LevelElement) elements.get(i)).render(g);
		}
		g.resetTransform();
	}
	
	public void add(LevelElement element) {
		if (element.getID() == 0) {
			element.setID(nextID);
			nextID++;
		} else {
			if (element.getID() >= nextID) {
				nextID = element.getID()+1;
			}
		}
		
		elements.add(element);
		element.addToWorld(world);
		
		element.init();
	}
	
	public void remove(LevelElement element) {
		elements.remove(element);
		element.removeFromWorld(world);
	}
	
	public void save(PrintStream out) {
		for (int i=0;i<elements.size();i++) {
			((LevelElement) elements.get(i)).save(out);
		}
	}
	
	public LevelElement getElementByID(int id) {
		for (int i=0;i<elements.size();i++) {
			LevelElement element = (LevelElement) elements.get(i);
			
			if (element.getID() == id) {
				return element;
			}
		}
		
		return null;
	}

	/**
	 * @see net.phys2d.raw.CollisionListener#collisionOccured(net.phys2d.raw.CollisionEvent)
	 */
	public void collisionOccured(CollisionEvent event) {
		float signif = 0.05f;
		if (event.getPenetrationDepth() < -signif) {
			for (int i=0;i<listeners.size();i++) {
				((LevelListener) listeners.get(i)).significantCollision(event.getBodyA(), event.getBodyB());
			}
		} 
	}
	
	public void translateAll(float x, float y) {
		for (int i=0;i<elements.size();i++) {
			LevelElement element = (LevelElement) elements.get(i);
			element.translate(x, y);
		}
	}
	
	public void clearMatchingFlags() {
		for (int j=0;j<elements.size();j++) {
			LevelElement localElement = (LevelElement) elements.get(j);
			localElement.setMatches(false);
		}
	}
	
	public float matches(Level target) {
		int matchCount = 0;
		int required = 0;
		
		for (int i=0;i<target.elements.size();i++) {
			LevelElement targetElement = (LevelElement) target.elements.get(i);
			
			// ignore static blocks
			if (targetElement instanceof StaticBlockElement) {
				continue;
			}
			
			required++;
			// try and find a match
			boolean foundMatch = false;
			for (int j=0;j<elements.size();j++) {
				LevelElement localElement = (LevelElement) elements.get(j);
				if (localElement.isSameKind(targetElement)) {
					if (localElement.isSimilar(targetElement)) {
						localElement.setMatches(true);
						if (!foundMatch) {
							foundMatch = true;
							matchCount++;
						}
						break;
					}
				}
			}
		}
		
		if (matchCount == required) {
			return 1;
		}
		
		return (float) matchCount / ((float) required);
	}
	
	public ArrayList findMatches(LevelElement target) {
		ArrayList list = new ArrayList();

		for (int i=0;i<elements.size();i++) {
			LevelElement element = (LevelElement) elements.get(i);
			if (element.isSameKind(target)) {
				list.add(element);
			}
		}
		
		return list;
	}
}
