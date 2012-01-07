package org.newdawn.warpstone;

import org.newdawn.slick.geom.Rectangle;


public class Light {
	private float r = 1;
	private float g = 1;
	private float b = 1;
	
	private float x;
	private float y;
	private float full;
	private float max;
	private float[] apply = new float[3];
	private float[] none = new float[] {0,0,0};
	private boolean dirty;
	private float[][][] cache;
	
	public Light(float x, float y, float full, float falloff) {
		this.x = x;
		this.y = y;
		this.full = full;
		max = full+falloff;
		
		dirty = true;
	}
	
	public void color(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
		dirty = true;
	}
	
	public void position(float x, float y) {
		this.x = x;
		this.y = y;
		dirty = true;
	}
	
	public boolean effects(float x, float y, float width, float height) {	
		Rectangle rect = new Rectangle(x-max,y-max,width+(max*2),height+(max*2));
		return rect.contains(this.x,this.y);
	}
	
	public float[] getCached(int xp, int yp, LevelMap map) {
		if (dirty) {
			float[] calc = apply(xp,yp,map);
			for (int i=0;i<3;i++) {
				cache[xp][yp][i] = calc[i];
			}
		}
		
		return cache[xp][yp];
	}
	
	public float[] apply(float xp, float yp, LevelMap map) {	
		if (!map.hasLos(x,y,xp,yp)) {
			return none;
		}
		
		float dx = x - xp;
		float dy = y - yp;
		float dis2 = ((dx*dx)+(dy*dy));

		float light;
		
		if (dis2 < full*full) {
			light = 1;
		} else {
			if (dis2 > (max*max)) {
				light = 0;
			} else {
				light = 1 - (dis2 / (max*max));
			}
		}
		
		apply[0] = r * light;
		apply[1] = g * light;
		apply[2] = b * light;
		
		return apply;
	}

	public void setCacheSize(int width, int height) {
		cache = new float[width][height][3];
	}

	public void setDirty(boolean c) {
		dirty = c;
	}
	
	public boolean isDirty() {
		return dirty;
	}
}
