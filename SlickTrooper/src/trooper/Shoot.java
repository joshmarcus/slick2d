package trooper;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.particles.ParticleEmitter;
import org.newdawn.slick.particles.ParticleSystem;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class Shoot {
	private float x;
	private float y;
	private Animation fall;
	private Animation runningLeft;
	private Animation runningRight;
	private Animation wingedAnim;
	private float vx = 30;
	private float vy;
	private Animation anim;
	private Image splat;
	private boolean dead;
	private boolean winged;
	
	public Shoot() throws SlickException {
		splat = new Image("res/splat.tga");
		
		wingedAnim = new Animation();
		wingedAnim.addFrame(new Image("res/fallguy.png"), (int) 1);
		
		fall = new Animation();
		fall.addFrame(new Image("res/shoot2.png"), (int) (Math.random()*100) + 150);
		fall.addFrame(new Image("res/shoot3.png"), (int) (Math.random()*100) + 150);
		fall.addFrame(new Image("res/shoot1.png"), (int) (Math.random()*100) + 150);
		fall.addFrame(new Image("res/shoot3.png"), (int) (Math.random()*100) + 150);

		SpriteSheet sheet = new SpriteSheet("res/running.png",32,32);
		runningLeft = new Animation();
		runningLeft.addFrame(sheet.getSprite(0,0), 100);
		runningLeft.addFrame(sheet.getSprite(1,0), 100);
		runningLeft.addFrame(sheet.getSprite(2,0), 100);
		runningLeft.addFrame(sheet.getSprite(3,0), 100);
		runningLeft.addFrame(sheet.getSprite(4,0), 100);
		runningLeft.addFrame(sheet.getSprite(5,0), 100);
		runningRight = new Animation();
		runningRight.addFrame(sheet.getSprite(0,0).getFlippedCopy(true, false), 100);
		runningRight.addFrame(sheet.getSprite(1,0).getFlippedCopy(true, false), 100);
		runningRight.addFrame(sheet.getSprite(2,0).getFlippedCopy(true, false), 100);
		runningRight.addFrame(sheet.getSprite(3,0).getFlippedCopy(true, false), 100);
		runningRight.addFrame(sheet.getSprite(4,0).getFlippedCopy(true, false), 100);
		runningRight.addFrame(sheet.getSprite(5,0).getFlippedCopy(true, false), 100);
		
		anim = fall;
	}

	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public void init(float x, float y, float vy) {
		this.x = x;
		this.y = y;
		this.vy = vy;
	}
	
	public boolean isWinged() {
		return winged;
	}
	
	public void wing() {
		anim = wingedAnim;
		winged = true;
		y += 24;
		vy *= 5;
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
	
	public void die(ParticleSystem system, ParticleEmitter emitter) {
		if (dead) {
			return;
		}
		dead = true;
		
		for (int i=0;i<10;i++) {
			int ofs = 0;
			if (!winged) {
				ofs = 24;
			}
			Particle particle = system.getNewParticle(emitter, (int) (400+(Math.random()*100)));
			particle.setImage(splat);
			particle.setPosition(x+((int) (Math.random()*24)),y+ofs+((int) (Math.random()*15)));
			particle.setSize(25+((int) (Math.random()*10)));
			particle.setColor((float) (Math.random()+0.5f),0,0,(float) ((Math.random()*0.5f)+0.1f));
			particle.setType(2);
			if (!winged) {
				particle.setVelocity(0, vy / 300, 1);
			}
		}
	}
	
	public void render() {
		anim.draw((int) x, (int) y);
	}
	
	public boolean running() {
		return (anim == runningLeft) || (anim == runningRight);
	}
	
	public void update(int playerX, int delta) {
		if (anim == runningLeft) {
			x -= delta * vx * 0.001f;
		} else if (anim == runningRight) {
			x += delta * vx * 0.001f;
		} else {
			y += delta * vy * 0.001f;
		}
		
		if (y > 420) {
			y = 450;
			vy = 0;
			if (x > playerX+20) {
				anim = runningLeft;
			} else {
				anim = runningRight;
			}
		}
	}
}
