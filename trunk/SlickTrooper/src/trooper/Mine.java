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
public class Mine {
	private float x;
	private float y = 430;
	private Image image;
	private boolean dead;
	
	public Mine(float x) throws SlickException {
		this.x = x;
		image = new Image("res/mine.png");
	}
	
	public void render(Graphics g) {
		image.draw((int) x, (int) y);
	}

	public boolean active() {
		return y >= 470;
	}
	
	public float getX() {
		return x;
	}
	
	public void update(int delta) {
		if (y < 470) {
			y += delta * 0.1f;
		}
	}
	public void explode(ParticleSystem system, ParticleEmitter emitter) {
		for (int i=0;i<10;i++) {
			Particle particle = system.getNewParticle(emitter, (int) (300+(Math.random()*200)));
			particle.setPosition(x+((int) (Math.random()*24)),y+((int) (Math.random()*15)));
			particle.setSize(5+((int) (Math.random()*30)));
			particle.setColor(1,1,1,1);
		}
		
		dead = true;
	}
}
