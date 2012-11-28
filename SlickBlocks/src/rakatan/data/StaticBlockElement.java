package rakatan.data;

import java.io.PrintStream;

import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.w3c.dom.Element;

import rakatan.ShapeRenderer;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class StaticBlockElement extends LevelElement {
	private int x;
	private int y;
	private int width;
	private int height;
	
	StaticBlockElement() {	
	}
	
	public StaticBlockElement(int x, int y,int width, int height, Image image) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.image = image;
		this.color = Color.white;
		
		body = new StaticBody(new Box(width * Level.SCALE,height * Level.SCALE));
		body.setPosition((x+(width/2))*Level.SCALE, (y+(height/2))*Level.SCALE);
		
		shapes.add(new Rectangle(-(width/2)*Level.SCALE,-(height/2)*Level.SCALE,width*Level.SCALE,height*Level.SCALE));	
	}
	
	public boolean contains(float x, float y) {
		return false;
	}
	
	public void addToWorld(World world) {
		world.add(body);
	}

	protected void fillShape(Graphics g) {
		for (int i=0;i<shapes.size();i++) {
			ShapeRenderer.fill((Shape) shapes.get(i), image, 0.005f / Level.SCALE);
		}
	}

	/**
	 * @see rakatan.data.LevelElement#update(int)
	 */
	public void update(int delta) {
	}

	/**
	 * @see rakatan.data.LevelElement#save(java.io.PrintStream)
	 */
	public void save(PrintStream pout) {
		pout.print("<StaticBlock ");
		pout.print("id=\""+id+"\" ");
		pout.print("width=\""+width+"\" ");
		pout.print("height=\""+height+"\" ");
		pout.print("x=\""+x+"\" ");
		pout.print("y=\""+y+"\" ");
		pout.println(">");
		pout.println("</StaticBlock>");
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
		
		StaticBlockElement block = new StaticBlockElement((int) x,(int) y,(int) width,(int) height,stat);
		block.setID(id);
		
		return block;
	}

	public boolean isSameKind(LevelElement other) {
		if (other instanceof StaticBlockElement) {
			StaticBlockElement o = (StaticBlockElement) other;
			
			return (o.width == this.width) && (o.height == this.height);
		}
		
		return false;
	}
}
