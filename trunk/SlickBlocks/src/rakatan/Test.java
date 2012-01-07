package rakatan;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import rakatan.data.DynamicBlockElement;
import rakatan.data.DynamicWedgeElement;
import rakatan.data.Level;
import rakatan.data.StaticBlockElement;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class Test {

	public static void main(String[] argv) {
		Level level = new Level();
		level.add(new StaticBlockElement(-500, 1024-40, 768+1000, 50, null));
		
		level.add(new DynamicBlockElement(140, 500, 50, 50, null, Color.yellow));
		level.add(new DynamicBlockElement(200, 490, 50, 50, null, Color.yellow));
		level.add(new DynamicBlockElement(140, 580, 50, 50, null, Color.yellow));
		level.add(new DynamicBlockElement(200, 590, 50, 50, null, Color.yellow));
		
		level.add(new DynamicBlockElement(300, 500, 250, 20, null, Color.cyan));
		level.add(new DynamicBlockElement(280, 515, 250, 20, null, Color.cyan));
		
		level.add(new DynamicBlockElement(440, 700, 100, 30, null, Color.blue));
		level.add(new DynamicBlockElement(440, 660, 100, 30, null, Color.blue));
		level.add(new DynamicBlockElement(440, 620, 100, 30, null, Color.blue));
		
		level.add(new DynamicBlockElement(650, 600, 70, 40, null, Color.green));
		level.add(new DynamicBlockElement(650, 520, 70, 40, null, Color.green));
		level.add(new DynamicBlockElement(650, 680, 70, 40, null, Color.green));
		
		level.add(new DynamicWedgeElement(850, 600, 70, 40, null, Color.red));
		level.add(new DynamicWedgeElement(850, 650, 70, 40, null, Color.red));
		
		long before = System.currentTimeMillis();
		for (int i=0;i<1000;i++) {
			System.out.println(i);
			level.update(10);
		}
		long after = System.currentTimeMillis();
		System.out.println("TT: "+(after-before));
	}
}
