package rakatan.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.newdawn.slick.util.Log;
import org.w3c.dom.Document;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class LevelData {
	private String initialState = "";
	private String targetState = "";
	private LevelElement key = null;
	
	public void setInitialState(Level level) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		level.save(new PrintStream(bout));
		
		initialState = new String(bout.toByteArray());
	}
	
	public void setKeyElement(LevelElement key) {
		this.key = key;
	}
	
	public void setTargetState(Level level) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		level.save(new PrintStream(bout));
		
		targetState = new String(bout.toByteArray());
	}
	
	public void save(OutputStream out) {
		PrintStream pout = new PrintStream(out);
		pout.println("<Level>");
		pout.println("<Initial>");
		pout.println(initialState);
		pout.println("</Initial>");
		pout.println("<Target key=\""+key.getID()+"\">");
		pout.println(targetState);
		pout.println("</Target>");
		pout.println("</Level>");
	}
	
}
