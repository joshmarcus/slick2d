package virium;

import java.util.ArrayList;

import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.particles.ParticleEmitter;
import org.newdawn.slick.particles.ParticleSystem;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class BloodEmitter implements ParticleEmitter {
	private ArrayList newSplats = new ArrayList();
	
	public void addSplat(float x, float y) {
		newSplats.add(new Splat(x,y));
	}
	
	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#update(org.newdawn.slick.particles.ParticleSystem, int)
	 */
	public void update(ParticleSystem system, int delta) {
		for (int i=0;i<newSplats.size();i++) {
			Splat splat = (Splat) newSplats.get(i);
			splat.spawn(system);
		}
		newSplats.clear();
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#updateParticle(org.newdawn.slick.particles.Particle, int)
	 */
	public void updateParticle(Particle particle, int delta) {
		if (particle.getLife() < 500) {
			if (particle.getType() == 0) {
				particle.adjustSize(-delta * 0.02f);
				particle.adjustColor(0, 0, 0, -delta * 0.003f);
			} else {
				particle.adjustSize(-delta * 0.005f);
				particle.adjustColor(0, 0, 0, -delta * 0.001f);
			}
		}
	}

	private class Splat {
		public float x;
		public float y;
		
		public Splat(float x, float y) {
			this.x = x;
			this.y = y;
		}
		
		public void spawn(ParticleSystem system) {
			for (int i=0;i<3;i++) {
				Particle p = system.getNewParticle(BloodEmitter.this, 5000);
				p.setType(1);
				float dx = (float) (Math.random() * 2) - 1;
				float dy = (float) (Math.random() * 2) - 1;
				float dis = (float) (Math.random() * 15);
				float size = (float) ((Math.random() * 15) + 15);
				
				p.setPosition(x + (dis * dx), y + (dis * dy));
				p.setSize(size);
				p.setColor(((float) Math.random()*0.3f)+0.3f,0,0,0.3f);
			}
			for (int i=0;i<10;i++) {
				Particle p = system.getNewParticle(BloodEmitter.this, 500);
				float dx = (float) (Math.random() * 2) - 1;
				float dy = (float) (Math.random() * 2) - 1;
				float dis = (float) (Math.random() * 5);
				float speed = (float) (Math.random() * 0.06f);
				float size = (float) ((Math.random() * 10) + 15);
				
				p.setPosition(x + (dis * dx), y + (dis * dy));
				p.setSize(size);
				p.setColor(((float) Math.random()*0.6f) + 0.4f,0,0,1);
				p.setVelocity(dx,dy,speed);
			}
		}
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#completed()
	 */
	public boolean completed() {
		// TODO Auto-generated method stub
		return false;
	}
}
