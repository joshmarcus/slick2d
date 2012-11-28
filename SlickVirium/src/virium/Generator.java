package virium;

import java.util.ArrayList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.PackedSpriteSheet;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Circle;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class Generator extends AbstractEntity implements Entity {
	private SpriteSheet sheet1;
	private SpriteSheet sheet2;
	private SpriteSheet shet;
	private Animation anim1;
	private Animation anim2;
	private Animation current;
	private int x;
	private int y;
	private int spawnInterval = 1000;
	private int nextSpawn;
	private PackedSpriteSheet pack;
	private AreaMap map;
	private int hitTimeOut = 200;
	private int timeOutCounter = 0;
	private int life = 20;
	private int count;
	private int maxInPlay = 30;
	
	public Generator(PackedSpriteSheet pack,int x, int y) throws SlickException {
		this.pack = pack;
		sheet1 = pack.getSpriteSheet("generator");
		sheet2 = pack.getSpriteSheet("generator2");
		anim1 = new Animation();
		anim1.addFrame(sheet1.getSprite(0,0), 50);
		anim1.addFrame(sheet1.getSprite(1,0), 50);
		anim1.addFrame(sheet1.getSprite(2,0), 50);
		anim2 = new Animation();
		anim2.addFrame(sheet2.getSprite(0,0), 50);
		anim2.addFrame(sheet2.getSprite(1,0), 50);
		anim2.addFrame(sheet2.getSprite(2,0), 50);
		
		current = anim1;
		this.x = x;
		this.y = y;
		
		nextSpawn = spawnInterval;
		bounds = new Circle(x,y,25);
	}
	
	public Circle getBounds() {
		return bounds;
	}
	
	/**
	 * @see virium.Entity#update(virium.GameContext, int)
	 */
	public void update(GameContext context, int delta) {
		if (timeOutCounter > 0) {
			timeOutCounter -= delta;
			if (timeOutCounter <= 0) {
				current = anim1;
			}
		}
		
		nextSpawn -= delta;
		if (nextSpawn <= 0) {
			spawn();
			nextSpawn = spawnInterval;
		}
		
		if (current != anim1) {
			anim1.update(delta);
		}
		if (current != anim2) {
			anim2.update(delta);
		}
	}

	protected Actor createActor(int x, int y) {
		return new Actor(x,y,
				pack, "alien1", false,
				new ZombieActorController());
	}
	
	private void spawn() {
		if (count >= maxInPlay) {
			return;
		}
		
		Actor p1 = createActor(x,y-40);
		Actor p2 = createActor(x,y+40);
		Actor p3 = createActor(x-40,y);
		Actor p4 = createActor(x+40,y);
		
		ArrayList possibles = new ArrayList();
		if (!map.intersects(p1)) {
			possibles.add(p1);
		}
		if (!map.intersects(p2)) {
			possibles.add(p2);
		}
		if (!map.intersects(p3)) {
			possibles.add(p3);
		}
		if (!map.intersects(p4)) {
			possibles.add(p4);
		}
		
		if (possibles.size() > 0) {
			int r = (int) (Math.random() * possibles.size());
			Actor spawned = (Actor) possibles.get(r);
			map.addEntity(spawned);
			spawned.setGenerator(this);
			count++;
		}
	}
	
	/**
	 * @see virium.Entity#render(org.newdawn.slick.Graphics)
	 */
	public void draw(Graphics g) {
		int ofs = sheet1.getHeight() / 2;
		current.draw(x-ofs,y-ofs);
	}

	/**
	 * @see virium.Entity#setMap(virium.AreaMap)
	 */
	public void setMap(AreaMap map) {
		this.map = map;
		map.entityPositionUpdated(this);
	}

	/**
	 * @see virium.Entity#getOwner()
	 */
	public Entity getOwner() {
		return this;
	}

	public void hitByBullet(Actor source) {
		timeOutCounter = hitTimeOut;
		current = anim2;
		life--;
		if (life <= 0) {
			// blow up here
			map.removeEntity(this);
		}
	}

	/**
	 * @see virium.Entity#getX()
	 */
	public float getX() {
		return x;
	}

	/**
	 * @see virium.Entity#getY()
	 */
	public float getY() {
		return y;
	}
	
	public void entityDied() {
		count--;
	}
}
