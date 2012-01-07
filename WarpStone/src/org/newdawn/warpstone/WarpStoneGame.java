package org.newdawn.warpstone;

import java.util.ArrayList;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Bootstrap;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.pathfinding.navmesh.NavMesh;
import org.newdawn.slick.util.pathfinding.navmesh.NavMeshBuilder;
import org.newdawn.slick.util.pathfinding.navmesh.NavPath;

public class WarpStoneGame extends BasicGame {
	private static Color OFF = new Color(1,1,1,0.5f);
	private static Color MAP = new Color(0,0,0,0.5f);
	private static Color HEALTH = new Color(0,1,0,0.5f);
	
	private static final int ANIM_INTERVAL = 100;
	private static final int UPDATE_INTERVAL = 25;	
	private static final int SECTION_HEIGHT = 21;
	private static final int SECTION_WIDTH = 27;
	
	private int tilNextFrame = ANIM_INTERVAL;
	private int tilNextUpdate = UPDATE_INTERVAL;

	private LevelMap map;
	private NavPath currentPath;
	private NavMesh mesh;
	
	private float offsetx;
	private float offsety;
	private int sectionx;
	private int sectiony;
	
	private Actor selected;
	private ArrayList<Actor> party = new ArrayList<Actor>();
	private GameContainer container;
	
	public WarpStoneGame() {
		super("WarpStone");
	}
	
	@Override
	public void init(GameContainer container) throws SlickException {
		this.container = container;
		
		container.setClearEachFrame(true);
		container.setShowFPS(false);
		
		Images.init();
		ActorTypeStore.init();
		
		generateMap();
	}

	private void generateMap() {
		Log.error("Building map....");
		map = new LevelMap(50,50);
		Log.error("Building navmesh...");
		NavMeshBuilder builder = new NavMeshBuilder();
		mesh = builder.build(map, false);	
		
		Log.error("Adding charactors...");
		selected = new Actor(ActorType.ARCHER, 10.0f, 10.0f, ActorType.SOUTH);
		selected.setLocation(map.getStartX()-1, map.getStartY()-1);
		map.addActor(selected);
		party.add(selected);
		selected = new Actor(ActorType.SWORDSMAN, 10.0f, 10.0f, ActorType.SOUTH);
		selected.setLocation(map.getStartX()+1, map.getStartY()-1);
		map.addActor(selected);
		party.add(selected);
		selected = new Actor(ActorType.PEASANT, 10.0f, 10.0f, ActorType.SOUTH);
		selected.setLocation(map.getStartX()-1, map.getStartY()+1);
		map.addActor(selected);
		party.add(selected);
		selected = new Actor(ActorType.MAGE, 10.0f, 10.0f, ActorType.SOUTH);
		selected.setLocation(map.getStartX()+1, map.getStartY()+1);
		map.addActor(selected);
		party.add(selected);
	}
	
	
	
	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		tilNextFrame -= delta;
		tilNextUpdate -= delta;
		
		map.update(tilNextFrame < 0, tilNextUpdate < 0, sectionx, sectiony, SECTION_WIDTH, SECTION_HEIGHT);
		
		if (tilNextFrame < 0) {
			tilNextFrame += ANIM_INTERVAL;
		}
		if (tilNextUpdate < 0) {
			tilNextUpdate += UPDATE_INTERVAL;
		}
		
		if (container.getInput().isKeyPressed(Input.KEY_SPACE)) {
			generateMap();
		}
	}

	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		if (offsetx == 0) {
			offsetx = 400-(selected.getX()*32);
			offsety = 300-(selected.getY()*32);
		} else {
			offsetx = (offsetx * 0.8f) + ((400-(selected.getX()*32)) * 0.2f);
			offsety = (offsety * 0.8f) + ((300-(selected.getY()*32)) * 0.2f);
		}
		
		g.translate((int) offsetx, (int) offsety);
		sectionx = (int) (selected.getX() - 13);
		sectiony = (int) (selected.getY() - 10);
		map.render(g, sectionx, sectiony, SECTION_WIDTH, SECTION_HEIGHT);
		g.translate(-(int) offsetx, -(int) offsety);
		
		g.translate(container.getWidth()-10-(map.getWidthInTiles())*3,
					container.getHeight()-10-(map.getHeightInTiles())*3);
		drawMap(g);
		g.translate(-(container.getWidth()-10-(map.getWidthInTiles()*3)),
				    -(container.getHeight()-10-(map.getHeightInTiles()*3)));
		
		for (int i=0;i<party.size();i++) {
			Actor actor = party.get(i);
			
			Color col = Color.white;
			if (actor != selected) {
				col = OFF;
			}
			Images.FACES.getSubImage(actor.getType().getIndex(), 0).draw(container.getWidth()-50,5+(i*60), col);
			g.setColor(Color.green.darker());
			g.fillRect(container.getWidth()-50,43+(i*60),44,4);
			g.setColor(HEALTH);
			g.fillRect(container.getWidth()-49,43+(i*60),44,4);
		}
		g.drawString("FPS: "+container.getFPS(),10,10);
	}

	public void drawMap(Graphics g) {
		Images.MAP.draw(-5,-5);
		g.setColor(MAP);
		for (int x=0;x<map.getWidthInTiles();x++) {
			for (int y=0;y<map.getHeightInTiles();y++) {
				if (map.discovered(x, y)) {
					if (map.blocked(x,y)) {
						g.fillRect(5+(x*3), 5+(y*3), 3,3);
					}
				}
			}
		}
	}
	
	private Actor getActorAt(float x, float y) {
		return map.getActorAt(x, y);
	}
	
	@Override
	public void mousePressed(int button, int x, int y) {
		super.mousePressed(button, x, y);
		
		if (x > container.getWidth() - 50) {
			int index = (y - 5) / 60;
			if (index < party.size()) {
				selected = party.get(index);
				return;
			}
		}
		
		float targetX = (x - offsetx) / 32.0f;
		float targetY = (y - offsety) / 32.0f;

		if ((x > container.getWidth()-10-(map.getWidthInTiles())*3) &&
		   (y > container.getHeight()-10-(map.getHeightInTiles())*3)) {
			targetX = (x - (container.getWidth()-10-(map.getWidthInTiles())*3) - 5) / 3.0f;
			targetY = (y - (container.getHeight()-10-(map.getHeightInTiles())*3) - 5) / 3.0f;
		} else {
			Actor actor = getActorAt(targetX,targetY);
			if (actor != null) {
				selected = actor;
				return;
			}
		}
		
		currentPath = mesh.findPath( selected.getX(),  selected.getY(), targetX, targetY, false);
		if (currentPath == null) {
			targetX += 0.5f;
			currentPath = mesh.findPath(selected.getX(),  selected.getY(), targetX, targetY, false);
		}
		if (currentPath == null) {
			targetX -= 1f;
			currentPath = mesh.findPath(selected.getX(),  selected.getY(), targetX, targetY, false);
		}
		
		selected.setCurrentPath(currentPath, targetX, targetY);
	}

	
	public static void main(String[] args) {
		Bootstrap.runAsApplication(new WarpStoneGame(), 800, 600, false);
	}

}
