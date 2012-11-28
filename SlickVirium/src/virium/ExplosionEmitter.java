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
public class ExplosionEmitter implements ParticleEmitter {
	private ArrayList booms = new ArrayList();
	
	public void addBoom(float x, float y, float size) {
		booms.add(new Boom(x,y,size));
	}
	
	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#update(org.newdawn.slick.particles.ParticleSystem, int)
	 */
	public void update(ParticleSystem system, int delta) {
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#updateParticle(org.newdawn.slick.particles.Particle, int)
	 */
	public void updateParticle(Particle particle, int delta) {
	}

	private class Boom {
		public float x;
		public float y;
		public float size;
		
		public Boom(float x, float y, float size) {
			this.x = x;
			this.y = y;
			this.size = size;
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
