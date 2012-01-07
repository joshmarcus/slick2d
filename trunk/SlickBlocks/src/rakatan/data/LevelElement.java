package rakatan.data;

import java.io.PrintStream;
import java.util.ArrayList;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;
import net.phys2d.raw.Collide;
import net.phys2d.raw.Contact;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.ConvexPolygon;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Shape;
import org.w3c.dom.Element;

import rakatan.InGameState;
import rakatan.ShapeRenderer;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public abstract class LevelElement {
	protected Body body;
	protected Color color;
	protected ArrayList shapes = new ArrayList();
	protected Image image;
	protected boolean selected;
	protected int id;
	protected boolean matches;
	
	public void init() {
		body.setFriction(0.8f);
	}
	
	public void setMatches(boolean matches) {
		this.matches = matches;
	}
	
	public boolean matchesTarget() {
		return matches;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public int getID() {
		return id;
	}
	
	public Body getBody() {
		return body;
	}
	
	public void translate(float x, float y) {
		body.setPosition(getX()+x, getY()+y);
	}
	
	public void update(int delta) {
	}

	public void setPosition(float x, float y) {
		body.setPosition(x,y);
	}
	
	public boolean containsBody(Body body) {
		return this.body == body;
	}
	
	public float getX() {
		return body.getPosition().getX();
	}

	public float getY() {
		return body.getPosition().getY();
	}

	protected void makeStill(Body body) {
		body.setForce(0, 0);
		body.setTorque(0);
		body.adjustAngularVelocity(-body.getAngularVelocity());
		body.adjustBiasedAngularVelocity(-body.getBiasedAngularVelocity());
		
		Vector2f vel = new Vector2f(body.getVelocity());
		vel.scale(-1);
		body.adjustVelocity(vel);
		vel = new Vector2f(body.getBiasedVelocity());
		vel.scale(-1);
		body.adjustBiasedVelocity(vel);
	}
	
	public void makeStill() {
		makeStill(body);
	}
	
	public void setRotation(float rotation) {
		body.setRotation(rotation);
	}
	
	public float getRotation() {
		return body.getRotation();
	}
	
	protected void fillShape(Graphics g) {
		for (int i=0;i<shapes.size();i++) {
			ShapeRenderer.fill((Shape) shapes.get(i), image, 0.015f / Level.SCALE);
		}
	}
	
	protected void drawShape(Graphics g) {
		for (int i=0;i<shapes.size();i++) {
			g.draw((Shape) shapes.get(i));
		}
	}

	/**
	 * @see rakatan.data.LevelElement#render(org.newdawn.slick.Graphics)
	 */
	public void render(Graphics g) {
		render(g, null);
	}
	
	/**
	 * @see rakatan.data.LevelElement#render(org.newdawn.slick.Graphics)
	 */
	public final void render(Graphics g, Color c) {
		renderImpl(g,c);
	}
	
	public void renderImpl(Graphics g, Color c) {
		ROVector2f pos = body.getPosition();
		float rot = body.getRotation();
		
		g.translate(pos.getX(), pos.getY());
		g.rotate(0, 0, (float) Math.toDegrees(rot));

		g.setColor(color);
		fillShape(g);

		if (c != null) {
			g.setColor(c);
		} else {
			g.setColor(Color.black);
			if (InGameState.SHOW_MATCHES) {
				if (matches) {
					g.setColor(Color.orange);
				}
			} 
			if (InGameState.RESTING_BODDIES) {
				if (body.isResting()) {
					g.setColor(Color.yellow);
				}
			}
		}
		g.setAntiAlias(true);
		drawShape(g);
		g.setAntiAlias(false);
		
		g.setColor(Color.white);
		g.rotate(0, 0, (float) -Math.toDegrees(rot));
		g.translate(-pos.getX(), -pos.getY());
	}
	
	protected boolean boxContains(Body body, float x, float y) {
		Box hit = new Box(5,5);
		Body hitBody = new Body(hit, 1);
		hitBody.setPosition(x,y);
		
		Contact[] contacts = new Contact[] {new Contact(), new Contact()};
		return Collide.collide(contacts, hitBody, body, 0) != 0;
	}
	
	public boolean contains(float x, float y) {
		x *= Level.SCALE;
		y *= Level.SCALE;
		
		if (body.getShape() instanceof ConvexPolygon) {
			x -= body.getPosition().getX();
			y -= body.getPosition().getY();
			return ((ConvexPolygon) body.getShape()).contains(new Vector2f(x,y));
		}
		
		if (body.getShape() instanceof Box) {
			return boxContains(body, x, y);
		}
		
		for (int i=0;i<shapes.size();i++) {
			if (((Shape) shapes.get(i)).contains(x,y)) {
				return true;
			}
		}
		
		return false;
	}

	public void addToWorld(World world) {
		world.add(body);
	}
	
	public void removeFromWorld(World world) {
		world.remove(body);
	}
	
	public abstract void save(PrintStream pout);
	
	public abstract LevelElement load(Element xml, Image stat, Image dynamic);
	
	public abstract boolean isSameKind(LevelElement other);
	
	public boolean compareAngle(LevelElement local) {
		float ang1 = (float) (Math.toDegrees(getBody().getRotation()) % 360.0);
		float ang2 = (float) (Math.toDegrees(local.getBody().getRotation()) % 360.0);
		
		float degDif = ang1 - ang2;
		if (degDif > 180) {
			degDif -= 360;
		} else if (degDif < -180) {
			degDif += 360;
		}
		
		float rotationTolerance = 10;
		if (degDif > rotationTolerance) {
			return false;
		}
		
		return true;
	}

	public boolean isSimilar(LevelElement local) {
		float positionDif = ((Vector2f) local.getBody().getPosition()).distanceSquared(getBody().getPosition());

		float positionTolerance = 30;
		if (positionDif > positionTolerance * positionTolerance) {
			return false;
		}

		return compareAngle(local);
	}
	
	public static LevelElement loadFromXML(Element xml, Image stat, Image dynamic) {
		if (xml.getTagName().equals("Block")) {
			DynamicBlockElement element = new DynamicBlockElement();
			return element.load(xml, stat, dynamic);
		}
		if (xml.getTagName().equals("Arc")) {
			DynamicArcElement element = new DynamicArcElement();
			return element.load(xml, stat, dynamic);
		}
		if (xml.getTagName().equals("Wedge")) {
			DynamicWedgeElement element = new DynamicWedgeElement();
			return element.load(xml, stat, dynamic);
		}
		if (xml.getTagName().equals("StaticBlock")) {
			StaticBlockElement element = new StaticBlockElement();
			return element.load(xml, stat, dynamic);
		}
		
		return null;
	}
}
