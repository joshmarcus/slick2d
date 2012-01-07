package virium;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class BulletEntity extends AbstractEntity implements Entity {
	private AreaMap map;
	private Actor owner;
	private float x;
	private float y;
	private float dx;
	private float dy;
	private int particleInterval = 5;
	private int nextParticle;
	private float speed = 0.3f;
	private int dead = 0;
	
	public BulletEntity(Actor owner, float x, float y, float dx, float dy) {
		this.owner = owner;
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		bounds = new Circle(x,y,10);
	}

	/**
	 * @see virium.Entity#getOwner()
	 */
	public Entity getOwner() {
		return owner;
	}
	
	private boolean validPosition(float x, float y) {
		boolean tileHit = false;
		
		for (int xs=2;xs<=28;xs+=13) {
			for (int ys=2;ys<=28;ys+=13) {
				if (map.isBlocked((int) x+xs-16,(int)y+ys-16)) {
					int tx = (int) ((x+xs-16)/16);
					int ty = (int) ((y+ys-16)/16);
					
					hitTile(tx,ty, map.getTileId(tx,ty, 1));
					tileHit = true;
				}
			}
		}
		
		if (tileHit) {
			return false;
		}
		
		bounds.setX(x);
		bounds.setY(y);
		
		if (map.intersects(this)) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * @see virium.Entity#draw(org.newdawn.slick.Graphics)
	 */
	public void draw(Graphics g) {

	}

	/**
	 * @see virium.Entity#getBounds()
	 */
	public Circle getBounds() {
		return bounds;
	}

	/**
	 * @see virium.Entity#setMap(virium.AreaMap)
	 */
	public void setMap(AreaMap map) {
		this.map = map;
		map.entityPositionUpdated(this);
	}

	/**
	 * @see virium.Entity#update(virium.GameContext, int)
	 */
	public void update(GameContext context, int delta) {
		if (dead > 0) {
			dead -= delta;
			if (dead <= 0) {
				map.removeEntity(this);
				return;
			}
		} 
		
		x += dx * delta * speed;
		y += dy * delta * speed;
		bounds.setX(x);
		bounds.setY(y);
		
		if (nextParticle > 0) {
			nextParticle -= delta;
		}
		
		if (nextParticle <= 0) {
			nextParticle = particleInterval;
			map.addBulletParticle(x, y);
		}
		
		if (dead == 0) {
			if (!validPosition(map,x,y,8)) {
				dead = 30;
			}
		}
	}

	public void hitEntity(Entity entity) {
		entity.hitByBullet(owner);
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

}
