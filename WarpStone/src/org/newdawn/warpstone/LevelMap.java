package org.newdawn.warpstone;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;
import org.newdawn.warpstone.dungeon.Map;


public class LevelMap implements TileBasedMap {
	public static final int V_DOOR = 60;
	public static final int H_DOOR1 = 42;
	public static final int H_DOOR2 = 43;
	
	private int[][][] tiles;
	private int width;
	private int height;
	private Map map;
	private SpriteSheet sheet;

	private ArrayList<Actor> actors = new ArrayList<Actor>();
	private LightMap lightMap;
	
	public LevelMap(Map source) {
		init(source);
	}
		
	public LevelMap(int width, int height) {
		long seed = System.currentTimeMillis();
		Map map = new Map(width,height);
		map.generate(seed, 30, 500, 0);
		
		init(map);
	}
	
	public LightMap getLightMap() {
		return lightMap;
	}
	
	private void init(Map source) {
		sheet = Images.TILES_SHEET;
		
		this.map = source;
		this.width = source.getWidth();
		this.height = source.getHeight();
		
		tiles = new int[source.getWidth()][source.getHeight()][3];
		generate(source);
		
		lightMap = new LightMap(this);
	}
	
	public int getStartX() {
		return map.getStartX();
	}
	
	public int getStartY() {
		return map.getStartY();
	}
	
	public void stepped(int x, int y) {
		if (getTile(x,y,2) == 60) {
			setTile(x,y,2,63);	
			setTile(x,y-1,2,47);		
		}
		if (getTile(x,y-1,2) == 42) {
			setTile(x,y-1,2,45);
			setTile(x,y-2,2,29);
			setTile(x+1,y-1,2,46);
			setTile(x+1,y-2,2,30);
		}
		if (getTile(x,y-1,2) == 43) {
			setTile(x-1,y-1,2,45);
			setTile(x-1,y-2,2,29);
			setTile(x,y-1,2,46);
			setTile(x,y-2,2,30);
		}
	}
	
	private void generate(Map map) {
		for (int x=0;x<map.getWidth();x++) {
			for (int y=0;y<map.getHeight();y++) {
				int tile = map.getTile(x, y);
				if (tile != 0) {
					if (map.isDoor(x, y)) {
						setTile(x,y,0,1 + ((Math.random() > 0.9f) ? (int) (Math.random() * 7) : 0));
						// vertical door
						if (map.getTile(x, y-1) != tile) {
							setTile(x,y,2,V_DOOR);	
							setTile(x,y-1,2,44);			
							setTile(x,y-1,1,0);				
						} 
						
						if (map.getTile(x+1,y) != tile) {
							setTile(x,y,2,58);	
							setTile(x,y-1,2,42);
							setTile(x,y-2,2,26);
							
							// door off
							//setTile(x,y-1,2,45);
							//setTile(x,y-2,2,29);
						} else if (map.getTile(x-1,y) != tile) {
							setTile(x,y,2,59);	
							setTile(x,y-1,2,43);
							setTile(x,y-2,2,27);
							
							// door off
							//setTile(x,y-1,2,46);
							//setTile(x,y-2,2,30);
						}
						continue;
					}
					if (map.getTile(x,y-1) != tile) {
						// consider the top thick wall
						if (map.getTile(x-1,y) != tile) {
							setTile(x,y,1,49);
							setTile(x,y-1,1,33);
							setTile(x,y-1,0,0);		
						} else if (map.getTile(x+1,y) != tile) {
							setTile(x,y,1,51);
							setTile(x,y-1,1,35);
						} else {
							setTile(x,y,1,50);
							setTile(x,y-1,1,34);
						}
					} else {
						// consider edge walls
						if ((map.getTile(x-1,y) != tile) || (map.getTile(x-1,y+1) != tile)) {
							setTile(x,y,1,52);
						} else if (map.getTile(x-1,y-1) != tile) {
							setTile(x,y,1,56);
							setTile(x,y-1,1,40);				
						}
						if ((map.getTile(x+1,y) != tile) || (map.getTile(x+1,y+1) != tile)) {
							setTile(x,y,1,53);			
						} else if (map.getTile(x+1,y-1) != tile) {
							setTile(x,y,1,57);
							setTile(x,y-1,1,41);	
						} 
						
						if (map.getTile(x,y+1) != 0) {
							setTile(x,y,0,1 + ((Math.random() > 0.9f) ? (int) (Math.random() * 7) : 0));
						} else {
							setTile(x,y,1,38);
						}
					}					
				}
			}
		}

		// Start location can have an entry door
//		for (int x=0;x<map.getStartRoom().getWidth();x++) {
//			int xp = map.getStartRoom().getX() + x;
//			if (getTile(xp,map.getStartRoom().getY(),1) == 50) {
//				if ((getTile(xp-1,map.getStartRoom().getY(),1) != 0) &&
//				   (getTile(xp+1,map.getStartRoom().getY(),1) != 0)) {
//					setTile(xp, 
//							map.getStartRoom().getY() - 1, 
//							1, 48);
//					setTile(xp, 
//							map.getStartRoom().getY(), 
//							1, 64);
//					break;
//				}
//			}
//		}
		
		setTile(map.getEndRoom().getX() + (map.getEndRoom().getWidth() / 2), 
				map.getEndRoom().getY() + (map.getEndRoom().getHeight() / 2), 
				0, 17);
	}
	
	public void setTile(int x, int y, int layer, int tile) {
		if ((x < 0) || (y < 0) || (x >= width) || (y >= height)) {
			return;
		}
		tiles[x][y][layer] = tile;
	}
	
	public int getTile(int x, int y, int layer) {
		if ((x < 0) || (y < 0) || (x >= width) || (y >= height)) {
			return 0;
		}
		
		return tiles[x][y][layer];
	}

	@Override
	public boolean blocked(PathFindingContext context, int tx, int ty) {
		return blocked(tx,ty);
	}
	
	public boolean blocked(int tx, int ty) {
		if ((tx < 0) || (ty < 0) || (tx >= width) || (ty >= height)) {
			return false;
		}
		
		return tiles[tx][ty][1] != 0;
	}

	private boolean blocksSight(int tx, int ty) {
		if ((tx < 0) || (ty < 1) || (tx >= width) || (ty >= height)) {
			return false;
		}
		
		int tile = tiles[tx][ty][2];
		if (tile == V_DOOR) {
			return true;
		}
		tile = tiles[tx][ty-1][2];
		if ((tile == H_DOOR1) || (tile == H_DOOR2)) {
			return true;
		}
		
		return tiles[tx][ty][1] != 0;		
	}
	
	@Override
	public float getCost(PathFindingContext context, int tx, int ty) {
		int dx = tx - context.getSourceX();
		int dy = ty - context.getSourceY();
		
		// block the special diagonal move through corners
		if ((dy > 0) && (dx != 0)) {
			if (getTile(tx,ty-1,1) != 0) {
				return 100f;
			}
		}
		if ((dy < 0) && (dx != 0)) {
			if (getTile(tx-dx,ty,1) != 0) {
				return 100f;
			}
		}
		
		if ((dx != 0) && (dy != 0)) {
			return 1.1f;
		}
		return 1;
	}

	@Override
	public int getHeightInTiles() {
		return height;
	}

	@Override
	public int getWidthInTiles() {
		return width;
	}

	@Override
	public void pathFinderVisited(int x, int y) {
	}

	public boolean hasLom(float x1, float y1, float x2, float y2, float size) {
		float step = 0.1f;
		float dx = (x2-x1);
		float dy = (y2-y1);
		float len = Math.max(Math.abs(dx),Math.abs(dy)); //(float) Math.sqrt((dx*dx)+(dy*dy));
		dx /= len;
		dx *= step;
		dy /= len;
		dy *= step;
		int steps = (int) (len / step);
		for (int i=0;i<steps;i++) {
			if (blocked((int) x1, (int) y1)) {
				return false;
			}
			if (blocked((int) (x1-(dy*10*size)), (int) (y1+(dx*10*size)))) {
				return false;
			}
			if (blocked((int) (x1+(dy*10*size)), (int) (y1-(dx*10*size)))) {
				return false;
			}
			
			x1 += dx;
			y1 += dy;
		}
		
		return true;
	}
	
	public boolean hasLos(float x1, float y1, float x2, float y2, float size) {
		float step = 0.1f;
		float dx = (x2-x1);
		float dy = (y2-y1);
		float len = Math.max(Math.abs(dx),Math.abs(dy)); //(float) Math.sqrt((dx*dx)+(dy*dy));
		dx /= len;
		dx *= step;
		dy /= len;
		dy *= step;
		int steps = (int) (len / step);
		for (int i=0;i<steps;i++) {
			if (blocksSight((int) x1, (int) y1)) {
				return false;
			}
			if (blocksSight((int) (x1-(dy*10*size)), (int) (y1+(dx*10*size)))) {
				return false;
			}
			if (blocksSight((int) (x1+(dy*10*size)), (int) (y1-(dx*10*size)))) {
				return false;
			}
			
			x1 += dx;
			y1 += dy;
		}
		
		return true;
	}
	
	public boolean hasLos(float x1, float y1, float x2, float y2) {
		float step = 0.1f;
		float dx = (x2-x1);
		float dy = (y2-y1);
		float len = Math.max(Math.abs(dx),Math.abs(dy)); //(float) Math.sqrt((dx*dx)+(dy*dy));
		dx /= len;
		dx *= step;
		dy /= len;
		dy *= step;
		int steps = (int) (len / step);
		for (int i=0;i<steps;i++) {
			if (blocksSight((int) x1, (int) y1)) {
				return false;
			}
			
			x1 += dx;
			y1 += dy;
		}
		
		return true;
	}
	
	public void render(Graphics g, int sectionx, int sectiony, int width, int height) {
		int layer = 0;
		
		sheet.startUse();
		for (int y=sectiony;y<sectiony+height;y++) {
			for (int x=sectionx;x<sectionx+width;x++) {
				int tile = getTile(x,y,layer);
				if (tile != 0) {
					tile--;
					int tx = tile % 16;
					int ty = tile / 16;
					
					drawQuads(lightMap, x,y,sheet.getSubImage(tx,ty), tile, true); 
				}
			}
		}
		sheet.endUse();
		
		for (int y=sectiony;y<sectiony+height;y++) {
			for (int i=0;i<actors.size();i++) {
				Actor current = actors.get(i);
				if (((int) current.getY()) == y) {
					current.render(g);
				}
			}
			sheet.startUse();
			for (int x=sectionx;x<sectionx+width;x++) {
				int tile = getTile(x,y,1);
				if (tile != 0) {
					tile--;
					int tx = tile % 16;
					int ty = tile / 16;
					drawQuads(lightMap, x,y,sheet.getSubImage(tx,ty), tile, true);
				}
			}
			for (int x=sectionx;x<sectionx+width;x++) {
				int tile = getTile(x,y,2);
				if (tile != 0) {
					tile--;
					int tx = tile % 16;
					int ty = tile / 16;
					drawQuads(lightMap, x,y,sheet.getSubImage(tx,ty), tile, true);
				}
			}
			sheet.endUse();
		}
	}
		
	private void drawQuads(LightMap lightMap, int x, int y, Image image, int tile, boolean walls) {
		configureLights(lightMap, image, Image.TOP_LEFT, x,y);
		configureLights(lightMap, image, Image.TOP_RIGHT, x+1,y);
		configureLights(lightMap, image, Image.BOTTOM_LEFT, x,y+1);
		configureLights(lightMap, image, Image.BOTTOM_RIGHT, x+1,y+1);
		image.drawEmbedded((x*32),(y*32),32,32);
	}
	
	private void configureLights(LightMap lightMap, Image image, int corner, int x, int y) {
		image.setColor(corner, lightMap.getRed(x,y),
				lightMap.getGreen(x,y),
				lightMap.getBlue(x,y),
				1);
	}
	
	public boolean discovered(int x, int y) {
		if (lightMap.discovered(x, y)) {
			return true;
		}
		if (lightMap.discovered(x+1, y)) {
			return true;
		}
		if (lightMap.discovered(x, y+1)) {
			return true;
		}
		if (lightMap.discovered(x+1, y+1)) {
			return true;
		}
		
		return false;
	}
	
	public void addActor(Actor actor) {
		actors.add(actor);
		actor.addedToMap(this);
	}
	
	public void removeActor(Actor actor) {
		actors.remove(actor);
		actor.removedFromMap(this);
	}

	public void update(boolean updateAnimation, boolean updateLogic, int sx, int sy, int swidth, int sheight) {
		if (updateLogic) {
			lightMap.update(sx,sy,swidth,sheight);
		}
		for (int i=0;i<actors.size();i++) {
			Actor actor = actors.get(i);
			
			if (updateAnimation) {
				actor.updateAnimation();
			}
			if (updateLogic) {
				actor.update(this);
				stepped((int) actor.getX(), (int) actor.getY());
			}
		}
	}

	public Actor getActorAt(float x, float y) {
		float tolerance = 1;
		for (int i=0;i<actors.size();i++) {
			if (actors.get(i).distance2(x,y) < tolerance) {
				return actors.get(i);
			}
		}
		
		return null;
	}
}
