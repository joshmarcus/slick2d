package playground.jnlp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.newdawn.slick.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A JNLP descriptor file describing a deployment of an application including jars, natives and
 * extensions.
 *
 * @author kevin
 */
public class JNLPFile {
	/** The name or title given to the JNLP file */
	private String name = "";
	/** The author or vendor of the JNLP file */
	private String author = "";
	/** The description given in the JNLP file */
	private String description = "";
	/** The home page reference given */
	private String homepage = "";
	
	/** True if this JNLP file requires permissions */
	private boolean requiresPermissions;
	/** The list of jars requires by this JNLP file for the current platform */
	private ArrayList requiredJars = new ArrayList();
	/** The list of native jars required by this JNLP file for the current platform */
	private ArrayList requiredNatives = new ArrayList();
	/** The list of extension hrefs required by this JNLP */
	private ArrayList requiredExtensions = new ArrayList();
	/** The code base all jar references should be taken from */
	private String codebase;
	/** The main class used to start the application */
	private String mainclass;
	/** The maximum heap specified */
	private int maxHeap = 64;
	
	/**
	 * Create a new JNLP file based on a local file
	 * 
	 * @param file The file containing the JNLP XML
	 * @throws IOException Indicates a failure to read or parse the local file
	 */
	public JNLPFile(File file) throws IOException {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.parse(file);
	
			codebase = document.getDocumentElement().getAttribute("codebase");
			requiresPermissions = document.getElementsByTagName("security").getLength() != 0;

			Element descElement = getFirstInstanceOf(document.getDocumentElement() ,"application-desc");
			if (descElement != null) {
				mainclass = descElement.getAttribute("main-class");
			}

			Element infElement = getFirstInstanceOf(document.getDocumentElement() ,"information");
			if (infElement != null) {
				Element titleElement = getFirstInstanceOf(document.getDocumentElement() ,"title");
				if (titleElement != null) {
					name = getValue(titleElement);
				}
				Element vendorElement = getFirstInstanceOf(document.getDocumentElement() ,"vendor");
				if (vendorElement != null) {
					author = getValue(vendorElement);
				}
				Element desElement = getFirstInstanceOf(document.getDocumentElement() ,"description");
				if (desElement != null) {
					description = getValue(desElement);
				}
				Element homepageElement = getFirstInstanceOf(document.getDocumentElement() ,"homepage");
				if (homepageElement != null) {
					homepage = homepageElement.getAttribute("href");
					if (homepage == null) {
						homepage = "";
					}
				}
			}
			
			NodeList resources = document.getElementsByTagName("resources");
			for (int i=0;i<resources.getLength();i++) {
				Element resource = (Element) resources.item(i);
				
				NodeList j2s = resource.getElementsByTagName("j2se");
				for (int j=0;j<j2s.getLength();j++) {
					Element j2se = (Element) j2s.item(j);
					
					String value = j2se.getAttribute("max-heap-size");
					if (value.endsWith("m")) {
						int max = Integer.parseInt(value.substring(0,value.length()-1));
						maxHeap = Math.max(maxHeap,max);
					}
				}
				
				String os = resource.getAttribute("os");
				if ((os == null) || (os.equals("")) || (System.getProperty("os.name").startsWith(os))) {
					processResource(resource);
				}
			}
		} catch (Exception e) {
			Log.error(e);
			throw new IOException("Failed to parse JNLP");
		}
	}
	
	/**
	 * Get the value from within the tags specified
	 * 
	 * @param element The element whose text context should be retrieved
	 * @return The text value or "" if none is specified
	 */
	private String getValue(Element element) {
		if (element.getFirstChild() == null) {
			return "";
		}
		return element.getFirstChild().getNodeValue();
	}
	
	/**
	 * Get the first instance of a particular named tag within an element
	 * 
	 * @param parent The element search
	 * @param name The name of the element to retrieve
	 * @return The first instance of that element or null if there are no elements with the name
	 */
	private Element getFirstInstanceOf(Element parent, String name) {
		NodeList list = parent.getElementsByTagName(name);
		if (list.getLength() == 0) {
			return null;
		}
		
		return (Element) list.item(0);
	}
	
	/**
	 * Get the name or title from the JNLP
	 * 
	 * @return The name of title from the JNLP
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the description given in the JNLP
	 * 
	 * @return The description given in the JNLP
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Get the author or vendor specified in the JNLP
	 * 
	 * @return The author or vendor specified in the JNLP
	 */
	public String getAuthor() {
		return author;
	}
	
	/**
	 * Get the homepage specified in the JNLP
	 * 
	 * @return The homepage specified in the JNLP
	 */
	public String getHomepage() {
		return homepage;
	}
	
	/**
	 * Get the maximum heap specified for the running VM
	 * 
	 * @return The maximum heap specified for the running VM
	 */
	public int getMaxHeap() {
		return maxHeap;
	}
	
	/**
	 * Get the main-class specified to execute the application
	 * 
	 * @return The main-class specified to execute the application
	 */
	public String getMainClass() {
		return mainclass;
	}
	
	/**
	 * Check if this JNLP describes an application or extension that 
	 * requires privaledged actions
	 * 
	 * @return True if the JNLP specifies security constraints
	 */ 
	public boolean isSecurityRequired() {
		return requiresPermissions;
	}
	
	/**
	 * Get the code base that all references to JARs are relative to
	 * 
	 * @return The code base that all JAR references are relative to
	 */
	public String getCodeBase() {
		return codebase;
	}
	
	/**
	 * Get the list of required JARs
	 * 
	 * @return The list of required JARs
	 */
	public ArrayList getJars() {
		return requiredJars;
	}

	/**
	 * Get the list of required JARs containing native libraries
	 * 
	 * @return The list of required JARs
	 */
	public ArrayList getNatives() {
		return requiredNatives;
	}

	/**
	 * Get the list of extension references required for this JNLP
	 * 
	 * @return The list of extension references required
	 */
	public ArrayList getExtensions() {
		return requiredExtensions;
	}
	
	/**
	 * Process the resources specified in the XML element supplied
	 * 
	 * @param resource The resource element to process
	 */
	private void processResource(Element resource) {
		NodeList jars = resource.getElementsByTagName("jar");
		for (int i=0;i<jars.getLength();i++) {
			Element jar = (Element) jars.item(i);
			String ref = jar.getAttribute("href");
			requiredJars.add(ref);
		}
		
		NodeList natives = resource.getElementsByTagName("nativelib");
		for (int i=0;i<natives.getLength();i++) {
			Element nativ = (Element) natives.item(i);
			String ref = nativ.getAttribute("href");
			requiredNatives.add(ref);
		}
		
		NodeList extensions = resource.getElementsByTagName("extension");
		for (int i=0;i<extensions.getLength();i++) {
			Element ext = (Element) extensions.item(i);
			String ref = ext.getAttribute("href");
			String version = ext.getAttribute("version");

			if ((version != null) && (!version.equals(""))) {
				ref += "?version-id="+version;
			}
			requiredExtensions.add(ref);
		}
	}
}
