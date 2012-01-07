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
public class BulletEmitter implements ParticleEmitter {
	private ArrayList addMe = new ArrayList();
	
	public void add(float x, float y) {
		addMe.add(new float[] {x,y});
	}
	
	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#update(org.newdawn.slick.particles.ParticleSystem, int)
	 */
	public void update(ParticleSystem system, int delta) {
		for (int i=0;i<addMe.size();i++) {
			float[] add = (float[]) addMe.get(i);
			
			Particle p = system.getNewParticle(this, 130);
			p.setColor(1, 1, 1, 0.5f);
			p.setPosition(add[0],add[1]);
			p.setColor(1,1,0.5f,1);
			p.setSize(8);
			p.setVelocity(0, 0, 0);
		}
		addMe.clear();
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#updateParticle(org.newdawn.slick.particles.Particle, int)
	 */
	public void updateParticle(Particle particle, int delta) {
		particle.adjustSize(delta * -0.04f);
		float adj = -delta * 0.005f;
		particle.adjustColor(0, adj*2, adj*2, adj*2);
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
