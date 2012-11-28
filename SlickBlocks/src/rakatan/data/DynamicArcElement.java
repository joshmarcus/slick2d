package rakatan.data;

import java.io.PrintStream;
import java.util.ArrayList;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.ConvexPolygon;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Polygon;
import org.w3c.dom.Element;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class DynamicArcElement extends LevelElement {
	private int x;
	private int y;
	private int width;
	private int height;
	
	DynamicArcElement() {	
	}
	
	public DynamicArcElement(int x, int y,int width, int height, int segs, Image image, Color c) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.image = image;
		this.color = c;
		
		ArrayList pts = new ArrayList();
		for (int i=0;i<segs+1;i++) {
			float ang = (float) Math.toRadians((180.0f / segs) * i);
			float yo = (float) ((height/2) - (Math.sin(ang) * height));
			float xo = (float) -(Math.cos(ang) * (width/2));
			pts.add(new Vector2f(xo,yo));
		}
		
		Vector2f[] ptsArray = (Vector2f[]) pts.toArray(new Vector2f[0]);
		
		ConvexPolygon poly = new ConvexPolygon(ptsArray);
	
		body = new Body(poly, 1000);
		body.setPosition(x+(width/2), y+(height/2));
		
		Polygon p = new Polygon();
		for (int i=0;i<ptsArray.length;i++) {
			p.addPoint(ptsArray[i].getX(), ptsArray[i].getY());
		}
		shapes.add(p);
	}

	public boolean compareAngle(LevelElement local) {
		float expectedDistance = ((Vector2f) local.body.getPosition()).distanceSquared(body.getPosition());
		expectedDistance += 100;
		
		ConvexPolygon localBox = (ConvexPolygon) local.body.getShape();
		ConvexPolygon thisBox = (ConvexPolygon) body.getShape();
		
		Vector2f[] localPts = localBox.getVertices(local.body.getPosition(), local.body.getRotation());
		Vector2f[] thisPts = thisBox.getVertices(body.getPosition(), body.getRotation());
		
		for (int i=0;i<localPts.length;i++) {
			Vector2f findMe = localPts[i];
			boolean found = false;
			for (int j=0;j<thisPts.length;j++) {
				if (findMe.distanceSquared(thisPts[j]) < expectedDistance) {
					found = true;
				}
			}
			
			if (!found) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @see rakatan.data.LevelElement#save(java.io.PrintStream)
	 */
	public void save(PrintStream pout) {
		pout.print("<Arc ");
		pout.print("id=\""+id+"\" ");
		pout.print("width=\""+width+"\" ");
		pout.print("height=\""+height+"\" ");
		pout.print("rot=\""+body.getRotation()+"\" ");
		pout.print("x=\""+body.getPosition().getX()+"\" ");
		pout.print("y=\""+body.getPosition().getY()+"\" ");
		pout.println(">");
		pout.print("   <Color ");
		pout.print("red=\""+color.r+"\" ");
		pout.print("green=\""+color.g+"\" ");
		pout.print("blue=\""+color.b+"\" ");
		pout.println("/>");
		pout.println("</Arc>");
	}

	/**
	 * @see rakatan.data.LevelElement#load(org.w3c.dom.Element, org.newdawn.slick.Image, org.newdawn.slick.Image)
	 */
	public LevelElement load(Element xml, Image stat, Image dynamic) {
		int id = Integer.parseInt(xml.getAttribute("id"));
		float width = Float.parseFloat(xml.getAttribute("width"));
		float height = Float.parseFloat(xml.getAttribute("height"));
		float x = Float.parseFloat(xml.getAttribute("x"));
		float y = Float.parseFloat(xml.getAttribute("y"));
		float rot = Float.parseFloat(xml.getAttribute("rot"));

		Element c = (Element) xml.getElementsByTagName("Color").item(0);
		float r = Float.parseFloat(c.getAttribute("red"));
		float g = Float.parseFloat(c.getAttribute("green"));
		float b = Float.parseFloat(c.getAttribute("blue"));
		Color col = new Color(r,g,b);
		
		DynamicArcElement element = new DynamicArcElement(0,0,(int) width,(int) height,10, dynamic,col);
		element.setID(id);
		element.setPosition(x, y);
		element.setRotation(rot);
		
		return element;
	}

	public boolean isSameKind(LevelElement other) {
		if (other instanceof DynamicArcElement) {
			DynamicArcElement o = (DynamicArcElement) other;
			
			return (o.width == this.width) && (o.height == this.height);
		}
		
		return false;
	}
}
