package trooper;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.particles.ParticleEmitter;
import org.newdawn.slick.particles.ParticleSystem;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class Shell {
	private float x;
	private float y;
	private float vx;
	private float vy;
	private Image image;
	private float ang;
	private int lastPart = 0;
	
	public Shell(float x, float y, float vx, float vy) throws SlickException {
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		image = new Image("res/shell.png");
		
		ang = (float) Math.toDegrees(Math.atan2(vy,vx)) - 180;
		this.x += vx * 35;
		this.y += vy * 35;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public void render(Graphics g) {
		g.translate((int) x,(int) y);
		g.rotate(6, 5, ang);
		image.draw();
		g.resetTransform();
	}
	
	public void update(int delta, ParticleEmitter emitter, ParticleSystem system) {
		lastPart -= delta;
		if (lastPart < 0) {
			lastPart = 10;
			Particle p = system.getNewParticle(emitter, (int) (150+(Math.random() * 250)));
			p.setPosition(x-(vx*8)+6+(float) (Math.random() * 3),y-(vy*8)+5+(float) (Math.random() * 3));
			p.setSize(5);
			p.setColor(1,1,1,1);
			p.setType(1);
		}
		
		x += vx * delta * 0.3f;
		y += vy * delta * 0.3f;
	}
}
