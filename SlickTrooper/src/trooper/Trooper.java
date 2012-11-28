package trooper;

import java.util.ArrayList;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.particles.ParticleEmitter;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.util.FastTrig;
import org.newdawn.slick.util.Log;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class Trooper extends BasicGame {
	private Image bg;
	private Image base;
	private Image turret;
	private Image baseRight;
	private Image turretRight;
	private Image pickup;
	private Image crate;
	private float ang;
	private int dir = 1;
	private float xp;
	
	private ArrayList shoots = new ArrayList();
	private ArrayList shells = new ArrayList();
	private ArrayList messages = new ArrayList();
	private ArrayList mines = new ArrayList();
	
	private SpriteSheet logo;
	private int startup = 1000;
	private boolean waitForStart;
	protected GameContainer container;
	private ParticleSystem system;
	private ParticleSystem glowSystem;
	private ParticleEmitter fadeEmitter;
	private ParticleEmitter glowFadeEmitter;
	private int spawnInterval = 1000;
	private int nextSpawn = 0;
	
	private int lives = 5;
	private int ammo;
	private int mineAmmo;
	private int score;
	private int box = -100;
	private int tilNextBox = 0;
	private int minePUP = -100;
	
	private Sound explode;
	private Sound bang;
	private Sound splat;
	private Sound reload;
	private boolean fps = false;
	private float fallSpeed = 0;
	private boolean cheat;
	private int cheatCount;
	
	public Trooper() {
		super("SuperTrooper");
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		this.container = container;
		container.setIcon("res/icon.png");
		container.setShowFPS(false);
		
		Music music = new Music("res/military.xm");
		music.loop();
		
		explode = new Sound("res/explode.wav");
		splat = new Sound("res/splat.wav");
		bang = new Sound("res/bang.wav");
		reload = new Sound("res/reload.wav");
		
		logo = new SpriteSheet("res/logo.png",256,64);
		bg = new Image("res/background.png");
		base = new Image("res/base.png");
		turret = new Image("res/turret.png");
		pickup = new Image("res/shells.png");
		crate = new Image("res/crate.png");
		
		container.setMaximumLogicUpdateInterval(10);
		
		turretRight = turret.getFlippedCopy(true, false);
		baseRight = base.getFlippedCopy(true, false);
		
		xp = 250;
		system = new ParticleSystem(new Image("res/particle.tga"),1000);
		system.setBlendingMode(ParticleSystem.BLEND_COMBINE);
		glowSystem = new ParticleSystem(new Image("res/particle.tga"));
		glowSystem.setBlendingMode(ParticleSystem.BLEND_ADDITIVE);
		
		fadeEmitter = new ParticleEmitter() {

			public boolean isEnabled() {
				return true;
			}

			public void setEnabled(boolean enabled) {
			}

			public void update(ParticleSystem system, int delta) {
			}

			public void updateParticle(Particle particle, int delta) {
				float d = delta;
				
				if (particle.getType() == 1) { // smoke
					float v = (particle.getLife() / (float) particle.getOriginalLife());
					particle.setColor(v,v,v,v);
					particle.adjustSize(d * 0.05f);
				}
				if (particle.getType() == 2) { // blood
					particle.adjustColor(0,0,0,-delta * 0.001f);
					particle.adjustSize(delta * -0.02f);
				}
			}

			public boolean completed() {
				// TODO Auto-generated method stub
				return false;
			}
		};
		system.addEmitter(fadeEmitter);
		
		glowFadeEmitter = new ParticleEmitter() {
			public boolean isEnabled() {
				return true;
			}

			public void setEnabled(boolean enabled) {
			}

			public void update(ParticleSystem system, int delta) {
			}

			public void updateParticle(Particle particle, int delta) {
				particle.adjustColor(0,-delta * 0.003f,-delta * 0.006f,-delta * 0.001f);
				particle.adjustSize(delta * 0.02f);
			}

			public boolean completed() {
				return false;
			}
		};
		glowSystem.addEmitter(glowFadeEmitter);
	}

	private void spawn() throws SlickException {
		Shoot s = new Shoot();
		s.init((float) (Math.random()*450)+20, (float) (-50-(Math.random()*10)),(float) (Math.random()*25)+20+fallSpeed);
		shoots.add(s);
	}
	
	private void resetGame() throws SlickException {
		shoots.clear();
		spawnInterval = 2000;
		xp = 250;
		
		mines.clear();
		box = -100;
		minePUP = -100;
		
		fallSpeed = 0;
		ammo = 20;
		score = 0;
		lives = 5;
		mineAmmo = 3;
		tilNextBox = (int) ((Math.random() * 3000) + 10000);
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g) throws SlickException {
		bg.draw(0,0);
		
		if (lives != 0) {
			if (dir == 1) {
				g.translate(xp, 0);
				base.draw(0,420);
				g.rotate(30,445,ang);
				turret.draw(0,420);
			} else {
				g.translate(xp, 0);
				baseRight.draw(0,420);
				g.rotate(25,445,-ang);
				turretRight.draw(0,420);
			}
		}

		g.resetTransform();
		pickup.draw(box,450);
		crate.draw(minePUP,460);
		
		for (int i=0;i<shells.size();i++) {
			Shell s = (Shell) shells.get(i);
			s.render(g);
		}
		g.resetTransform();
		for (int i=0;i<mines.size();i++) {
			Mine m = (Mine) mines.get(i);
			m.render(g);
		}
		system.render();
		g.resetTransform();
		for (int i=0;i<shoots.size();i++) {
			Shoot s = (Shoot) shoots.get(i);
			s.render();
		}
		glowSystem.render();
		for (int i=0;i<messages.size();i++) {
			FloatingMessage message = (FloatingMessage) messages.get(i);
			message.render(g);
		}
		
		if (waitForStart) {
			logo.getSprite(0, 0).draw(128-startup,100);
			logo.getSprite(0, 1).draw(128+startup,150);
			
			if (startup == 0) {
				g.translate(0,-100);
				g.setColor(new Color(0,0,0,0.5f));
				g.fillRect(176,350,160,80);
				g.setColor(Color.black);
				g.drawRect(176,350,160,80);
				drawString(g, "Cursors + Ctrl + Space", 355);
				drawString(g, "or", 370);
				drawString(g, "Gamepad to play", 385);
				drawString(g, "Press Fire To Start", 410);
				g.translate(0,100);
			}
		}

		g.setColor(Color.black);
		g.translate(1,1);
		g.drawString("SCORE: "+pad(score,8),5,495);
		g.drawString("AMMO: "+pad(ammo,3),440,495);
		g.drawString("MINES: "+pad(mineAmmo, 3),439,480);
		for (int i=0;i<lives;i++) {
			g.fillRect(220+(i*17), 496, 15, 13);
		}
		g.setColor(Color.white);
		g.translate(-1,-1);
		g.drawString("SCORE: "+pad(score,8),5,495);
		g.drawString("AMMO: "+pad(ammo,3),440,495);
		g.drawString("MINES: "+pad(mineAmmo,3),439,480);
		for (int i=0;i<lives;i++) {
			g.fillRect(220+(i*17), 496, 15, 13);
		}
		
		if (cheat) {
			drawString(g, "CHEAT MODE ON", 480);
		}
	}

	private void drawString(Graphics g, String msg, int y) {
		g.setColor(Color.black);
		g.drawString(msg, 256-(g.getFont().getWidth(msg)/2), y+1);
		g.setColor(Color.white);
		g.drawString(msg, 255-(g.getFont().getWidth(msg)/2), y);
	}
	
	private String pad(int v, int len) {
		String value = ""+v;
		while (value.length() < len) {
			value = "0"+value;
		}
		
		return value;
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta)
			throws SlickException {
		Input input = container.getInput();
		system.update(delta);
		glowSystem.update(delta);
		
		if (startup > 0) {
			waitForStart = true;
			startup -= delta;
			if (startup > 0) {
				return;
			}
			startup = 0;
		}
		if (waitForStart) {
			if (input.isKeyDown(Input.KEY_LCONTROL) || input.isButton1Pressed(Input.ANY_CONTROLLER)) {
				waitForStart = false;
				resetGame();
			}
			return;
		}
		
		if (input.isKeyDown(Input.KEY_UP) || input.isControllerUp(Input.ANY_CONTROLLER)) {
			ang += delta * 0.1f;
		}
		if (input.isKeyDown(Input.KEY_DOWN) || input.isControllerDown(Input.ANY_CONTROLLER)) {
			ang -= delta * 0.1f;
		}
		if (input.isKeyDown(Input.KEY_LEFT) || input.isControllerLeft(Input.ANY_CONTROLLER)) {
			dir = 1;
			xp -= delta * 0.05f;
		}
		if (input.isKeyDown(Input.KEY_RIGHT) || input.isControllerRight(Input.ANY_CONTROLLER)) {
			dir = -1;
			xp += delta * 0.05f;
		}
		
		xp = Math.max(0,xp);
		xp = Math.min(450,xp);
		
		ang = Math.max(-20,ang);
		ang = Math.min(35,ang);
		
		for (int i=0;i<shoots.size();i++) {
			Shoot s = (Shoot) shoots.get(i);
			s.update((int) xp, delta);
		}
		for (int i=0;i<shells.size();i++) {
			Shell s = (Shell) shells.get(i);
			s.update(delta, fadeEmitter, system);
		}
		for (int i=0;i<mines.size();i++) {
			Mine m = (Mine) mines.get(i);
			m.update(delta);
		}
		
		ArrayList removeMe = new ArrayList();
		for (int i=0;i<messages.size();i++) {
			FloatingMessage message = (FloatingMessage) messages.get(i);
			if (message.update(delta)) {
				removeMe.add(message);
			}
		}
		messages.removeAll(removeMe);
		
		ArrayList killme = new ArrayList();
		
		for (int i=0;i<shoots.size();i++) {
			Shoot shoot = (Shoot) shoots.get(i);

			if (shoot.running()) {
				boolean pop = false;
				
				for (int j=0;j<mines.size();j++) {
					Mine mine = (Mine) mines.get(j);
					if (mine.active()) {
						if (Math.abs(mine.getX()+3-shoot.getX()) < 5) {
							for (int k=0;k<shoots.size();k++) {
								Shoot target = (Shoot) shoots.get(k);
								if (target.running()) {
									if (Math.abs(mine.getX()+3-target.getX()) < 13) {
										target.die(system, fadeEmitter);
										killme.add(target);
									}
								}
							}
							
							mine.explode(glowSystem, glowFadeEmitter);
							pop = true;
							killme.add(mine);
							bang.play();
							break;
						}
					}
				}
				
				if (pop) {
					continue;
				}
			}
			if (shoot.running() && shoot.isWinged()) {
				shoot.die(system, fadeEmitter);
				killme.add(shoot);
				
				if (Math.random() > 0.75f) {
					if (minePUP == -100) {
						minePUP = (int) shoot.getX();
					}
				}
				continue;
			}
			if ((shoot.getY() > 430) && (Math.abs(xp+20-shoot.getX()) < 20)) {
				shoot.explode(glowSystem, glowFadeEmitter);
				if (box == -100) {
					tilNextBox = 0;
				}
				explode.play();
				if (!cheat) {
					lives--;
					
					if (lives <= 0) {
						lives = 0;
						gameOver();
					}
				}
				killme.add(shoot);
				continue;
			}
			for (int j=0;j<shells.size();j++) {
				Shell shell = (Shell) shells.get(j);
				float dx = Math.abs(shell.getX() - (shoot.getX() + 16));
				float dy = Math.abs(shell.getY() - (shoot.getY() + 24));
				
				if ((dx < 20) && (dy < 30)) {
					boolean wing = false;
					if ((!shoot.isWinged()) && (shell.getY() < (shoot.getY()+24))) {
						wing = true;
					}

					if (!wing) {
						shoot.die(system, fadeEmitter);
						splat.play();
						killme.add(shoot);
						
						if (Math.random() > 0.85f) {
							if (minePUP == -100) {
								minePUP = (int) shoot.getX();
							}
						}
					} else {
						shoot.wing();
					}
					killme.add(shell);
					
					j = shells.size();
					int bonus = (int) (Math.max(shoot.getY(), 10) * 5);
					bonus -= bonus % 10;
					if (wing) {
						bonus *= 2;
					}
					
					score += bonus;
					messages.add(new FloatingMessage(""+bonus,shoot.getX()+16,shoot.getY()));
				}
				if (shell.getY() < -10) {
					killme.add(shell);
				}
			}
		}
		
		for (int i=0;i<killme.size();i++) {
			shoots.remove(killme.get(i));
			shells.remove(killme.get(i));
			mines.remove(killme.get(i));
			
			if (killme.get(i) instanceof Shoot) {
				spawnInterval-=20;
				fallSpeed += 0.15f;
				if (spawnInterval < 500) {
					spawnInterval = 500;
				}
			}
		}
		
		nextSpawn -= delta;
		if (nextSpawn < 0) {
			nextSpawn = spawnInterval;
			spawn();
		}
		tilNextBox -= delta;
		if (tilNextBox < 0) {
			if (box == -100) {
				tilNextBox = (int) ((Math.random() * 10000) + 20000);
				box = (int) (Math.random()*450)+30;
			}
		}
		if (Math.abs(xp+20-box) < 20) {
			ammo+=15;
			reload.play();
			box = -100;
		}
		if (Math.abs(xp+20-minePUP) < 20) {
			mineAmmo++;
			reload.play();
			minePUP = -100;
		}
	}

	private void gameOver() {
		startup = 1000;
		waitForStart = true;
	}
	
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new Trooper(), 512, 512, false);
			container.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void mine() {
		if (mineAmmo == 0) {
			return;
		}
		
		if (!waitForStart) {
			try {
				if (dir == -1) {
					mines.add(new Mine(xp));
				} else if (dir == 1) {
					mines.add(new Mine(xp+32));
				}
				if (!cheat) {
					mineAmmo--;
				}
			} catch (SlickException e) {
				Log.error(e);
			}
		}
	}
	
	private void fire() {
		if (ammo == 0) {
			return;
		}
		
		if (!waitForStart) {
			try {
				float ofs = 20;
				if (dir == -1) {
					shells.add(new Shell(25+xp,436,(float) FastTrig.cos(Math.toRadians(-ang-ofs)), 
											   (float) FastTrig.sin(Math.toRadians(-ang-ofs))));
				} else if (dir == 1) {
					shells.add(new Shell(25+xp,436,-(float) FastTrig.cos(Math.toRadians(-ang-ofs)), 
							   (float) FastTrig.sin(Math.toRadians(-ang-ofs))));
				}
				if (!cheat) {
					ammo--;
				}
				bang.play();
			} catch (SlickException e) {
				Log.error(e);
			}
		}
	}
		
	public void controllerButtonPressed(int controller,int button) {
		if (button == 1) {
			fire();
		}
		if (button == 2) {
			mine();
		}
	}
	
	public void keyPressed(int key, char c) {
		if (key == Input.KEY_LCONTROL) {
			fire();
		}
		if (key == Input.KEY_SPACE) {
			mine();
		}
		if (key == Input.KEY_F8) {
			fps = !fps;
			container.setShowFPS(fps);
		}
		if (key == Input.KEY_F12) {
			cheatCount++;
			if (cheatCount > 3) {
				cheat = true;
				ammo += 100;
				mineAmmo += 100;
				lives = 5;
			}
		}
		if (key == Input.KEY_ESCAPE) {
			if (waitForStart) {
				container.exit();
			} else {
				startup = 1000;
				waitForStart = true;
			}
		}
	}
	
	private class FloatingMessage {
		public String message;
		public float x;
		public float y;
		public float a;
		
		public FloatingMessage(String message, float x, float y) {
			a = 1;
			this.x = x - (container.getDefaultFont().getWidth(message) / 2);
			this.y = y;
			this.message = message;
		}
		
		public boolean update(int delta) {
			y -= delta * 0.02f;
			a -= delta * 0.001f;
		
			if (a < 0) {
				return true;
			}
			
			return false;
		}
		
		public void render(Graphics g) {
			g.setColor(new Color(0,0,0,a));
			g.drawString(message, (int) x, (int) y);
		}
	}
}
