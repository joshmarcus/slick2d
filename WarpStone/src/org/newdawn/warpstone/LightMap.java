package org.newdawn.warpstone;

import java.util.ArrayList;

public class LightMap {
	private static final float AMBIENT = 0.1f;
	
	private float[][][] lightMap;
	private float[][][] smoothedLightMap;
	private LevelMap map;
	private ArrayList<Light> lights = new ArrayList<Light>();
	private int width;
	private int height;
	
	public LightMap(LevelMap map) {
		this.map = map;
		width = map.getWidthInTiles()+1;
		height = map.getHeightInTiles()+1;
		
		lightMap = new float[width][height][3];
		smoothedLightMap = new float[width][height][3];		
	}
	
	public void addLight(Light light) {
		lights.add(light);
		light.setCacheSize(width,height);
	}
	
	public void removeLight(Light light) {
		lights.remove(light);
	}
	
	public boolean discovered(int x, int y) {
		for (int i=0;i<3;i++) {
			if (lightMap[x][y][i] != 0) {
				return true;
			}
		}
		
		return false;
	}
	public float getRed(int x, int y) {
		return smoothedLightMap[x][y][0];
	}

	public float getGreen(int x, int y) {
		return smoothedLightMap[x][y][1];
	}
	
	public float getBlue(int x, int y) {
		return smoothedLightMap[x][y][2];
	}
	
	public void update(int sectionx, int sectiony, int width, int height) {
		int sx = sectionx;
		int sy = sectiony;
		int ex = sectionx+width;
		int ey = sectiony+height;

		// wipe the map applying fog of war
		for (int x=sx;x<ex;x++) {
			if ((x < 0) || (x >= lightMap.length)) {
				continue;
			}
			for (int y=sy;y<ey;y++) {
				if ((y < 0) || (y >= lightMap[0].length)) {
					continue;
				}
				float xp = x;
				float yp = y;
				
				for (int i=0;i<3;i++) {
					if (lightMap[x][y][i] != 0) {
						lightMap[x][y][i] = AMBIENT;
					}
				}
			}
		}

		for (int n=0;n<lights.size();n++) {
			Light light = lights.get(n);
			if (!light.effects(sectionx, sectiony, width, height)) {
				continue;
			}
			
			for (int x=sx;x<ex;x++) {
				if ((x < 0) || (x >= lightMap.length)) {
					continue;
				}
				for (int y=sy;y<ey;y++) {
					if ((y < 0) || (y >= lightMap[0].length)) {
						continue;
					}
					
					float[] effect = light.getCached(x,y, map);
					for (int i=0;i<3;i++) {
						lightMap[x][y][i] += effect[i];
					}
				}
			}
			light.setDirty(false);
		}
		
		float smooth = 1 / 9f;
		for (int x=sx+1;x<ex-1;x++) {
			if ((x < 1) || (x >= lightMap.length-1)) {
				continue;
			}
			for (int y=sy+1;y<ey-1;y++) {
				if ((y < 1) || (y >= lightMap[0].length-1)) {
					continue;
				}
				for (int i=0;i<3;i++) {
					smoothedLightMap[x][y][i] = 0;
					for (int a=-1;a<2;a++) {
						for (int b=-1;b<2;b++) {
							smoothedLightMap[x][y][i] += (lightMap[x+a][y+b][i]*smooth);
						}
					}
				}
			}
		}
	}
}
