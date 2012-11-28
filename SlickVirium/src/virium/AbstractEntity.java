package virium;

import java.util.ArrayList;

import org.newdawn.slick.geom.Circle;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public abstract class AbstractEntity implements Entity {
	protected Circle bounds;
	protected Bag list;
	protected int team;
	
	protected void hitTile(int x, int y, int tile) {
		
	}
	
	public void setTeam(int team) {
		this.team = team;
	}
	
	public int getTeam() {
		return team;
	}
	
	public void hitEntity(Entity entity) {
		
	}
	
	public void hitByBullet(Actor source) {
		
	}

	
	public Bag getQuadList() {
		return list;
	}
	
	public void setQuadList(Bag list) {
		this.list = list;
	}
	
	protected boolean validPosition(AreaMap map, float x, float y,int size) {
		boolean tileHit = false;
		
		for (int xs=-size;xs<=size;xs+=size/2) {
			for (int ys=-size;ys<=size;ys+=size/2) {
				if (map.isBlocked((int) x+xs,(int)y+ys)) {
					int tx = (int) ((x+xs)/16);
					int ty = (int) ((y+ys)/16);
					
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
}
