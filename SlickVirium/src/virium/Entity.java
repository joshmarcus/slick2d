package virium;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public interface Entity {

	public void hitEntity(Entity entity);
	
	public void setMap(AreaMap map);
	
	public void draw(Graphics g);
	
	public void update(GameContext context, int delta);
	
	public Circle getBounds();
	
	public Entity getOwner();
	
	public void hitByBullet(Actor source);
	
	public float getX();
	
	public float getY();
	
	public Bag getQuadList();
	
	public void setQuadList(Bag list);
	
	public int getTeam();
}
