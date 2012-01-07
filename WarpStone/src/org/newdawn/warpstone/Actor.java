package org.newdawn.warpstone;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.navmesh.NavPath;

public class Actor implements Mover, DungeonElement {
	private static final float SPEED = 0.08f;
	
	public static final int IDLE = 0;
	public static final int WALK = 1;
	public static final int ATTACK = 2;
	public static final int DIE = 3;
	
	private ActorType type;
	private float x;
	private float y;
	private int dir;
	private int frame;
	
	private int action = IDLE;
	private int imageOX = 0;
	private int imageOY = 0;
	
	private NavPath currentPath;
	private int pathStep;
	private int moveIterations = 0;
	private float dx;
	private float dy;
	private float finalx;
	private float finaly;
	private float tx;
	private float ty;
	
	private Light light;
	
	public Actor(ActorType type, float x, float y, int dir) {
		this.type = type;
		this.x = x;
		this.y = y;
		this.dir = dir;
		light = new Light(x,y, 4, 2);
	}
	
	public ActorType getType() {
		return type;
	}
	
	public void setCurrentPath(NavPath currentPath, float finalx, float finaly) {
		this.currentPath = currentPath;
		pathStep = 0;
		this.finalx = finalx;
		this.finaly = finaly;
		nextStep();
	} 

	private boolean considerNextStep(LevelMap map) {
		if (pathStep < currentPath.length()-1) {
			tx = currentPath.getX(pathStep+1);
			ty = currentPath.getY(pathStep+1);
			
			if (map.hasLos(x,y,tx,ty,0.25f)) {
				nextStep();
				return true;
			}
		}
		
		return false;
	}
	
	private void nextStep() {
		if (currentPath == null) {
			return;
		}
		
		pathStep++;
		if (pathStep >= currentPath.length()) {
			currentPath = null;
			dx = 0;
			dy = 0;
			return;
		}
		
		tx = currentPath.getX(pathStep);
		ty = currentPath.getY(pathStep);
		if (pathStep == currentPath.length() - 1) {
			tx = finalx;
			ty = finaly;
		}
		
		dx = (tx - x);
		dy = (ty - y);
		
		moveIterations = (int) (Math.sqrt((dx*dx)+(dy*dy)) / SPEED);
		dx = dx / moveIterations;
		dy = dy / moveIterations;
		setDirection(dx,dy);
	}
	
	private void setDirection(float dx, float dy) {
		float ang = (float) Math.toDegrees(Math.atan2(dy, dx));
		if (ang < 0) {
			ang = 360 + ang;
		}
		ang = (ang + 90 + 22.5f) % 360;
		ang /= 45f;
		dir = (int) ang;
	}
	
	public void update(LevelMap map) {
		if (currentPath != null) {
			if (!considerNextStep(map)) {
				moveIterations--;
				x += dx;
				y += dy;
				
				if (moveIterations <= 0) {
					nextStep();
				}
			}
			
			action = WALK;
			light.position(x,y);			
		} else {
			action = IDLE;
		}
	}
	
	public void updateAnimation() {
		frame++;
	}
	
	public void render(Graphics g) {
		int[] cycle = null;
		if (action == ATTACK) {
			cycle = type.getAttackCycle();
		}
		if (action == DIE) {
			cycle = type.getDieCycle();
		}
		if (action == WALK) {
			cycle = type.getWalkCycle();
		}
		if (action == IDLE) {
			cycle = type.getWalkCycle();
			frame = 0;
		}
		int f = cycle[frame % cycle.length];
		
		if (imageOX == 0) {
			imageOX = 16-(type.getImage(0,0).getWidth()/2);
			imageOY = 16-(type.getImage(0,0).getHeight()/2);
		}
		type.getImage(dir, f).draw(((x-0.5f)*32)+imageOX,((y-0.5f)*32)+imageOY);
	}

	public float getY() {
		return y;
	}
	
	public float getX() {
		return x;
	}

	public void setLocation(float x, float y) {
		currentPath = null;
		this.x = x;
		this.y = y;
		light.position(x,y);	
	}

	@Override
	public void addedToMap(LevelMap map) {
		map.getLightMap().addLight(light);
	}

	@Override
	public void removedFromMap(LevelMap map) {
		map.getLightMap().removeLight(light);
	}

	public float distance2(float x2, float y2) {
		float dx = x2 - x;
		float dy = y2 - y;
		return (dx*dx)+(dy*dy);
	}
}
