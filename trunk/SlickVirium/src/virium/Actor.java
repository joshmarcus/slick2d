package virium;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.PackedSpriteSheet;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Circle;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class Actor extends AbstractEntity implements Entity {
	private Animation animation;
	private float x;
	private float y;
	private boolean moving;
	private int dx;
	private int dy;
	private int fx;
	private int fy;
	private float ang;
	private AreaMap map;
	
	private float scalar;
	private int applyDX;
	private int applyDY;
	private ActorController controller;
	
	private float speed = 0.1f;
	private boolean type;
	private boolean fire;
	private int fireInterval = 200;
	private int tilNextFire;
	
	private int[] bulletOffsets = new int[] {0, -2, 0, 4, 6, 4};
	private Generator gen;
	private boolean dead;
	
	private int keys;
	private int score;
	
	public Actor(float x, float y, PackedSpriteSheet sheet, String ref, boolean type) {
		this(x,y,sheet,ref,type,null);
		
		keys = 5;
	}
	
	public Actor(float x, float y, PackedSpriteSheet sheet, String ref, boolean type, ActorController controller) {
		this.x = x;
		this.y = y;
		this.type = type;
		
		scalar = (float) Math.sqrt(0.5f);
		bounds = new Circle(x,y,14);
		
		this.controller = controller;
		if (controller != null) {
			controller.init(this);
		}
		
		animation = new Animation();
		SpriteSheet temp = sheet.getSpriteSheet(ref);
		if (type) {
			animation.addFrame(temp.getSprite(1,0), 75);
			animation.addFrame(temp.getSprite(0,0), 75);
			animation.addFrame(temp.getSprite(1,0), 75);
			animation.addFrame(temp.getSprite(2,0), 75);
			animation.addFrame(temp.getSprite(3,0), 75);
			animation.addFrame(temp.getSprite(2,0), 75);
		} else {
			animation.addFrame(temp.getSprite(0,1), 75);
			animation.addFrame(temp.getSprite(0,0), 75);
			animation.addFrame(temp.getSprite(0,1), 75);
			animation.addFrame(temp.getSprite(0,2), 75);
			animation.addFrame(temp.getSprite(0,3), 75);
			animation.addFrame(temp.getSprite(0,2), 75);
		}
		animation.stop();
	}

	public int getKeys() {
		return keys;
	}
	
	public int getScore() {
		return score;
	}
	public void setGenerator(Generator generator) {
		gen = generator;
	}
	
	public void setFire(boolean fire) {
		this.fire = fire;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public Circle getBounds() {
		return bounds;
	}
	
	public void setMap(AreaMap map) {
		this.map = map;
		map.entityPositionUpdated(this);
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public void draw(Graphics g) {
		int xp = (int) x;
		int yp = (int) y;
		
		g.rotate(xp, yp, ang);
		animation.draw(xp-16,yp-16);
		g.resetTransform();
	}
	
	public void applyDirection(int x,int y) {
		applyDX = x;
		applyDY = y;
	}
	
	public void update(GameContext context, int delta) {
		if (controller != null) {
			controller.update(context, this, delta);
		}
		
		boolean oldMoving = moving;
		moving = (applyDX != 0) || (applyDY != 0);
		
		int oldfx = (int) this.fx;
		int oldfy = (int) this.fy;
		
		if (moving) {
			this.dx = applyDX;
			this.dy = applyDY;
			
			float oldx = x;
			float oldy = y;
			
			float s = 1;
			if ((applyDX != 0) && (applyDY != 0)) {
				s = scalar;
			}
			
			x += applyDX * delta * speed * s;
			y += applyDY * delta * speed * s;
			
			int size = 13;
			
			boolean valid = true;
			
			// if players then we need to make
			if ((type) && (context.getPlayer1() != null) && (context.getPlayer2() != null)) {
				int pdx = (int) Math.abs(context.getPlayer1().getX() - context.getPlayer2().getX());
				int pdy = (int) Math.abs(context.getPlayer1().getY() - context.getPlayer2().getY());
				
				int count = 0;
				if (pdx > 750) {
					x = oldx;
					count++;
				}
				if (pdy > 550) {
					y = oldy;
					count++;
				}
				
				if (count == 2) {
					valid = false;
				}
			}
			if (valid) {
				if (!validPosition(map,x,y,size)) {
					if (!validPosition(map,x,oldy,size)) {
						if (!validPosition(map,oldx,y,size)) {
							x = oldx;
							y = oldy;
							
							if (!type) {
								x -= applyDY * delta * speed * s;
								y += applyDX * delta * speed * s;
								
								if (!validPosition(map,x,y,size)) {
									x = oldx;
									y = oldy;
									x += applyDY * delta * speed * s;
									y -= applyDX * delta * speed * s;
	
									if (!validPosition(map,x,y,size)) {
										x = oldx;
										y = oldy;
									}
								}
							}
						} else {
							x = oldx;
						}
					} else {
						y = oldy;
					}
				} 
			}
			
			if ((oldx != x) || (oldy != y)) {
				map.entityPositionUpdated(this);
			}
		}
		
		if (oldMoving != moving) {
			if (!moving) {
				animation.stop();
			} else {
				animation.restart();
			}
		}
		
		bounds.setX(x);
		bounds.setY(y);
		
		if ((!fire) || ((fx == 0) && (fy == 0))) {
			fx = dx;
			fy = dy;
		}

		if ((oldfx != this.fx) || (oldfy != this.fy)) {
			ang = (float) Math.toDegrees(Math.atan2(this.fy, this.fx)) + 90;
		}
		
		if (tilNextFire > 0) {
			tilNextFire -= delta;
		}
		if ((fire) && (tilNextFire <= 0) && ((fx != 0) || (fy != 0))) {
			tilNextFire = fireInterval;
			
			int index = animation.getFrame();
			int bulletOffset = bulletOffsets[index];
			
			map.addEntity(new BulletEntity(this, x - (bulletOffset*fy),y + (bulletOffset*fx),fx,fy));
		}
	}

	/**
	 * @see virium.Entity#getOwner()
	 */
	public Entity getOwner() {
		return this;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public void die() {
		if (!dead) {
			dead = true;
			if (gen != null) {
				gen.entityDied();
			}
			
			map.addSplat(x,y);
			map.removeEntity(this);
		}
	}
	
	public void hitByBullet(Actor source) {
		if (!type) {
			die();
		}
	}

	protected void hitTile(int x, int y, int tile) {
		if (!type) { // monster check
			return;
		}
		
		if (tile != 0) {
			// horizontal doors
			if ((tile == 455) && (keys > 0)) {
				keys--;
				int xp = x;
				while (map.getTileId(xp, y, 1) == 455) {
					map.setBlocked(xp,y,false);
					map.setTile(xp,y,1,0);
					xp--;
				}
				xp = x+1;
				while (map.getTileId(xp, y, 1) == 455) {
					map.setBlocked(xp,y,false);
					map.setTile(xp,y,1,0);
					xp++;
				}
			}
			// vertical doors
			if ((tile == 454) && (keys > 0)) {
				keys--;
				int yp = y;
				while (map.getTileId(x, yp, 1) == 454) {
					map.setBlocked(x,yp,false);
					map.setTile(x,yp,1,0);
					yp--;
				}
				yp = y+1;
				while (map.getTileId(x, yp, 1) == 454) {
					map.setBlocked(x,yp,false);
					map.setTile(x,yp,1,0);
					yp++;
				}
			}
		}
	}
}
