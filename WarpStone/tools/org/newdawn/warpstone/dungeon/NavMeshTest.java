package org.newdawn.warpstone.dungeon;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Bootstrap;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.navmesh.Link;
import org.newdawn.slick.util.pathfinding.navmesh.NavMesh;
import org.newdawn.slick.util.pathfinding.navmesh.NavMeshBuilder;
import org.newdawn.slick.util.pathfinding.navmesh.NavPath;
import org.newdawn.slick.util.pathfinding.navmesh.Space;
import org.newdawn.warpstone.LevelMap;

public class NavMeshTest extends BasicGame implements PathFindingContext {
	private LevelMap renderMap;
	private Map map;
	private NavMesh navMesh;
	private NavMeshBuilder builder;
	private boolean showSpaces = true;
	private boolean showLinks = true;
	private NavPath path;
	
	private float sx;
	private float sy;
	private float ex;
	private float ey;
	
	public NavMeshTest() {
		super("Nav-mesh Test");
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		container.setShowFPS(false);

		map = new Map(50,50);
		generateMap();
	}

	private void generateMap() {
		long seed = System.currentTimeMillis();
		map.generate(seed, 30, 500, 0);
		renderMap = new LevelMap(map);
		builder = new NavMeshBuilder();
		navMesh = builder.build(renderMap, true);
		
		System.out.println("Navmesh shapes: "+navMesh.getSpaceCount());
	}
	
	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		if (container.getInput().isKeyPressed(Input.KEY_SPACE)) {
			generateMap();
		}
		if (container.getInput().isKeyPressed(Input.KEY_1)) {
			showLinks = !showLinks;
		}
		if (container.getInput().isKeyPressed(Input.KEY_2)) {
			showSpaces = !showSpaces;
		}
	}

	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		g.translate(50,50);
		for (int x=0;x<50;x++) {
			for (int y=0;y<50;y++) {
				if (renderMap.blocked(this, x, y)) {
					g.setColor(Color.gray);
					g.fillRect((x*10)+1,(y*10)+1,8,8);
				}
			}
		}
		
		if (showSpaces) {
			for (int i=0;i<navMesh.getSpaceCount();i++) {
				Space space = navMesh.getSpace(i);
				if (builder.clear(renderMap, space)) {
					g.setColor(new Color(1,1,0,0.5f));
					g.fillRect(space.getX()*10, space.getY()*10, space.getWidth()*10, space.getHeight()*10);
				}
				g.setColor(Color.yellow);
				g.drawRect(space.getX()*10, space.getY()*10, space.getWidth()*10, space.getHeight()*10);

				if (showLinks) {
					int links = space.getLinkCount();
					for (int j=0;j<links;j++) {
						Link link = space.getLink(j);
						g.setColor(Color.red);
						g.fillRect((link.getX()*10)-2, (link.getY()*10)-2,5,5);
					}
				}
			}
		}
		
		if (path != null) {
			g.setColor(Color.white);
			for (int i=0;i<path.length()-1;i++) {
				g.drawLine(path.getX(i)*10, path.getY(i)*10, path.getX(i+1)*10, path.getY(i+1)*10);
			}
		}
	}
	
	@Override
	public Mover getMover() {
		return null;
	}

	@Override
	public int getSearchDistance() {
		return 0;
	}

	@Override
	public int getSourceX() {
		return 0;
	}

	@Override
	public int getSourceY() {
		return 0;
	}

	@Override
	public void mousePressed(int button, int x, int y) {
		float mx = (x - 50) / 10.0f;
		float my = (y - 50) / 10.0f;
		
		if (button == 0) {
			sx = mx;
			sy = my;
		} else {
			ex = mx;
			ey = my;
		}

		path = navMesh.findPath(sx,sy,ex,ey,true);
	}

	/**
	 * Entry point to out application
	 * 
	 * @param argv The arguments passed to the application
	 */
	public static void main(String[] argv) {
		Bootstrap.runAsApplication(new NavMeshTest(), 600, 600, false);
	}
}
