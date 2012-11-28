package rakatan.data;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.newdawn.slick.Image;
import org.newdawn.slick.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class LoadedLevelState {
	private Level initalState;
	private Level targetState;
	private LevelElement key;
	private String name = "Unknown";
	
	public String getName() {
		return name;
	}
	
	public Level getInitialState() {
		return initalState;
	}
	
	public Level getTargetState() {
		return targetState;
	}
	
	public LevelElement getKeyElement() {
		return key;
	}
	
	public static LoadedLevelState load(InputStream in, Image stat, Image dynamic) throws IOException {
		LoadedLevelState state = new LoadedLevelState();
		
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(in);
			state.name = doc.getDocumentElement().getAttribute("name");
			if (state.name.length() == 0) {
				state.name = "Unknown";
			}
			
			Element initial = (Element) doc.getElementsByTagName("Initial").item(0);
			state.initalState = readElements(initial.getChildNodes(), stat, dynamic);
			
			Element target = (Element) doc.getElementsByTagName("Target").item(0);
			int keyID = Integer.parseInt(target.getAttribute("key"));
			state.targetState = readElements(target.getChildNodes(), stat, dynamic);
			state.key = state.targetState.getElementByID(keyID);
			
			return state;
		} catch (Exception e) {
			Log.error(e);
			throw new IOException(e.getMessage());
		}
	}
	
	public static Level readElements(NodeList nodes, Image stat, Image dynamic) {
		Level level = new Level();
		
		for (int i=0;i<nodes.getLength();i++) {
			if (nodes.item(i) instanceof Element) {
				level.add(LevelElement.loadFromXML((Element) nodes.item(i), stat, dynamic));
			}
		}
		return level;
	}
}
