package rakatan.data;

import java.io.PrintStream;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.Collide;
import net.phys2d.raw.Contact;
import net.phys2d.raw.World;
import net.phys2d.raw.collide.ColliderFactory;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.ConvexPolygon;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.w3c.dom.Element;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class DynamicBlockElement extends LevelElement {
	private int x;
	private int y;
	private int width;
	private int height;
	
	DynamicBlockElement() {	
	}
	
	public DynamicBlockElement(int x, int y,int width, int height, Image image, Color c) {
		this(x,y,width,height,image,c,0);
	}

	public DynamicBlockElement(int x, int y,int width, int height, Image image, Color c, float rot) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.image = image;
		this.color = c;
		
		shapes.add(new Rectangle(-(width/2)*Level.SCALE,-(height/2)*Level.SCALE,width*Level.SCALE,height*Level.SCALE));
		
//		ConvexPolygon poly = new ConvexPolygon(new Vector2f[] {
//							new Vector2f(-width/2,-height/2),
//							new Vector2f(width/2,-height/2),
//							new Vector2f(width/2,height/2),
//							new Vector2f(-width/2,height/2),
//		});
		
		body = new Body(new Box(width*Level.SCALE,height*Level.SCALE), 1000);
		//body = new Body(poly, 1);
		body.setPosition((x+(width/2))*Level.SCALE, (y+(height/2))*Level.SCALE);
		body.setRotation(rot);
	}

	public boolean compareAngle(LevelElement local) {
		float expectedDistance = ((Vector2f) local.body.getPosition()).distanceSquared(body.getPosition());
		expectedDistance += 100;
		
		Box localBox = (Box) local.body.getShape();
		Box thisBox = (Box) body.getShape();
		
		Vector2f[] localPts = localBox.getPoints(local.body.getPosition(), local.body.getRotation());
		Vector2f[] thisPts = thisBox.getPoints(body.getPosition(), body.getRotation());
		
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
		pout.print("<Block ");
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
		pout.println("</Block>");
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
		
		DynamicBlockElement element = new DynamicBlockElement(0,0,(int) width,(int) height,dynamic,col);
		element.setID(id);
		element.setPosition(x, y);
		element.setRotation(rot);
		
		return element;
	}
	
	public boolean isSameKind(LevelElement other) {
		if (other instanceof DynamicBlockElement) {
			DynamicBlockElement o = (DynamicBlockElement) other;
			
			return (o.width == this.width) && (o.height == this.height);
		}
		
		return false;
	}

}
