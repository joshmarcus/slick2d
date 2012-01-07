package playground.jnlp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.newdawn.slick.util.Log;

/**
 * A JNLP launcher. This class provides a way to read, cache and launch webstart applications
 *
 * @author kevin
 */
public class Launcher {
	/** The location at which we should cache files */
	private File cacheLocation;
	
	/**
	 * Create a new launcher
	 * 
	 * @param cacheLocation The location where we'll cache files
	 */
	public Launcher(String cacheLocation) {
		this.cacheLocation = new File(cacheLocation);
		this.cacheLocation.mkdirs();
	}
	
	/**
	 * Launch an application 
	 * 
	 * @param id A symbolic ID to use locally (could use the URL)
	 * @param location The URL location of the JNLP
	 * @param update True if we should attempt to update the install if possible
	 * @return The configuration used to launch the application after return
	 * @throws IOException Indicates a failure to read the required files
	 */
	public LaunchConfig getLaunch(String id, String location, boolean update) throws IOException {
		LaunchConfig config = new LaunchConfig(id);
		config.setPathsRelativeTo(cacheLocation.getAbsolutePath());
		getLaunch(id, location, update, config);
		
		return config;
	}

	/**
	 * Launch an application 
	 * 
	 * @param id A symbolic ID to use locally (could use the URL)
	 * @param location The URL location of the JNLP
	 * @param update True if we should attempt to update the install if possible
	 * @param config The launch configuration to add to
	 * @throws IOException Indicates a failure to read the required files
	 */
	private void getLaunch(String id, String location, boolean update, LaunchConfig config) throws IOException {
		File local = new File(cacheLocation, id);
		String fname = location.substring(location.lastIndexOf("/")+1);
		File descriptor = new File(local, refToID(fname));
		
		if (update) {
			URLConnection conn;
			
			try {
				URL url = new URL(location);
				conn = url.openConnection();
				conn.setUseCaches(false);
				
				local.mkdirs();
				getURL(conn, descriptor);
			} catch (IOException e) {
				if (!descriptor.exists()) {
					Log.error(e);
					throw new IOException("Unable to retrieve: "+location);
				}
			}
		}
		
		if (!descriptor.exists()) {
			throw new IOException("Unable to retrieve: "+location);
		}

		JNLPFile file = new JNLPFile(descriptor);
		config.addJNLP(file);
		
		ArrayList jarFiles = new ArrayList();
		downloadResources(local, file, update, jarFiles, config);
		
		if (file.isSecurityRequired()) {
			config.addSignedCodebase(local);
			confirmSigning(jarFiles);
		} else {
			config.addUnsignedCodebase(new URL(location).getHost(), local);
		}
		
		if (file.getNatives().size() > 0) {
			extractNatives(file, local);
			config.addLibraryPath(local.getPath());
		}
		
		config.setMainClass(file.getMainClass());
		config.setMaxHeap(file.getMaxHeap());
	}

	/**
	 * Extract the natives specified in the JNLP file to the location specified
	 * 
	 * @param file The file specifing the natives
	 * @param local The location to extract to
	 * @throws IOException Indicates a failure to write the natives out
	 */
	private void extractNatives(JNLPFile file, File local) throws IOException {
		ArrayList natives = file.getNatives();
		for (int i=0;i<natives.size();i++) {
			String nativ = (String) natives.get(i);
			File nFile = new File(local, nativ);
			
			JarFile nativeJar = new JarFile(nFile);
			Enumeration entries = nativeJar.entries();
			
			while (entries.hasMoreElements()) {
				JarEntry entry = (JarEntry) entries.nextElement();
				if (entry.isDirectory()) {
					continue;
				}
				if (entry.getName().indexOf("/") >= 0) {
					continue;
				}
				
				File outFile = new File(local, entry.getName());
				if (outFile.exists()) {
					if (outFile.lastModified() <= nFile.lastModified()) {
						if (outFile.length() == nFile.length()) {
							continue;
						}
					}
				}
				copyFile(nativeJar.getInputStream(entry), new FileOutputStream(outFile));
			}
			
		}
	}
	
	/**
	 * Check that all the signatures on the files are the same and valid
	 * 
	 * @param jarFiles The list of JARs to track
	 * @throws IOException Indicates a failure to read and check the JARs
	 */
	private void confirmSigning(ArrayList jarFiles) throws IOException {
		Certificate[] baseCerts = null;
		
		for (int i=0;i<jarFiles.size();i++) {
			File sourceFile = (File) jarFiles.get(i);
			JarFile lib = new JarFile(sourceFile);
			
			Enumeration entries = lib.entries();
			boolean certsFound = false;
			
			while (entries.hasMoreElements()) {
				JarEntry entry = (JarEntry) entries.nextElement();
				if (entry.isDirectory()) {
					continue;
				}
				
				readFile(lib.getInputStream(entry));
				
				Certificate[] certs = entry.getCertificates();
				if ((certs == null) || (certs.length == 0)) {
					continue;
				}
				
				certsFound = true;
				if (i == 0) {
					baseCerts = certs;
				} else {
					if ((certs == null) || (certs.length == 0)) {
						throw new IOException("Invalid, JARs must be singed to use privledges! :"+sourceFile);
					}
					if (baseCerts.length != certs.length) {
						throw new IOException("All jars must be signed with the same certificate :"+sourceFile);
					}
					
					for (int j=0;j<baseCerts.length;j++) {
						if (!baseCerts[j].equals(certs[j])) {
							throw new IOException("All jars must be signed with the same certificate :"+sourceFile);		
						}
					}
				}
				break;
			}
			
			if (!certsFound) {
				throw new IOException("Invalid, JARs must be singed to use privledges! :"+sourceFile);
			}
		}
		
		// TODO: Verify signatures against signing?
	}
	
	/**
	 * Read the contents of a file - used to make certificates valid
	 * 
	 * @param is The stream from which to read the file
  	 * @throws IOException Indicates a failure to read
	 */
	private void readFile(InputStream is) throws IOException {
		byte[] buffer = new byte[4096];
		
		int len;
		while ((len = is.read(buffer)) > 0) {
		}
	}

	/**
	 * Copy the contents of one stream to another
	 * 
	 * @param is The input stream to read from
	 * @param out The output stream to write to
	 * @throws IOException Indicates a failure to complete the copy
	 */
	private void copyFile(InputStream is, OutputStream out) throws IOException {
		byte[] buffer = new byte[4096];
		
		int len;
		while ((len = is.read(buffer)) > 0) {
			out.write(buffer, 0, len);
		}
		
		out.close();
	}
	
	/**
	 * Download the resources required for a JNLP file
	 * 
	 * @param local The location to download to
	 * @param file The file describing the remote location
	 * @param update True if should update the JAR files if they're just out of date
	 * @param jarFiles The list of jars to update with the resources
	 * @param config The configuration we're creating 
	 * @throws IOException Inidcates a failure to access the remote resources, or write to the local ones. i.e. something went wrong that couldn't be recovered from, ok?
	 */
	private void downloadResources(File local, JNLPFile file, boolean update, ArrayList jarFiles, LaunchConfig config) throws IOException {
		try {
			// deal with getting the jars
			ArrayList jars = file.getJars();
			for (int i=0;i<jars.size();i++) {
				String name = (String) jars.get(i);
				URL jarUrl = new URL(file.getCodeBase()+"/"+name);
				File jarLocal = new File(local, name);
				config.addJar(jarLocal.getPath());
				
				if (!jarLocal.exists() || update) {
					URLConnection jarConn;
					
					jarFiles.add(jarLocal);
					try { 
						jarConn = jarUrl.openConnection();
						jarConn.setUseCaches(false);
					} catch (Exception e) {
						if (jarLocal.exists()) {
							continue;
						}
						
						throw new IOException("Failed to retrieve: "+jarUrl);
					}
					
					// if we haven't yet got the jar or the remote jar is newer than this
					// one then try to get it
					if (!jarLocal.exists() || jarConn.getLastModified() > jarLocal.lastModified() || jarConn.getContentLength() != jarLocal.length()) {
						getURL(jarConn, jarLocal);
					} 
				}
			}

			// deal with getting the jars
			ArrayList nativeJars = file.getNatives();
			for (int i=0;i<nativeJars.size();i++) {
				String name = (String) nativeJars.get(i);
				URL jarUrl = new URL(file.getCodeBase()+"/"+name);
				File jarLocal = new File(local, name);
				
				if (!jarLocal.exists() || update) {
					URLConnection jarConn;
					
					jarFiles.add(jarLocal);
					try { 
						jarConn = jarUrl.openConnection();
						jarConn.setUseCaches(false);
					} catch (Exception e) {
						if (jarLocal.exists()) {
							continue;
						}
						
						throw new IOException("Failed to retrieve: "+jarUrl);
					}
					
					// if we haven't yet got the jar or the remote jar is newer than this
					// one then try to get it
					if (!jarLocal.exists() || jarConn.getLastModified() > jarLocal.lastModified() || jarConn.getContentLength() != jarLocal.length()) {
						getURL(jarConn, jarLocal);
					}
				}
			}
			
			// deal with getting the extensions
			ArrayList extensions = file.getExtensions();
			for (int i=0;i<extensions.size();i++) {
				String ref = (String) extensions.get(i);
				String newID = refToID(ref);
				
				getLaunch(newID,ref,update,config);
			}
		} catch (Exception e) {
			Log.error(e);
			throw new IOException("Failed to cache from location");
		}
	}
	
	/**
	 * Convert a reference into the local file system name
	 * 
	 * @param ref The referece to convert
	 * @return The local file system name
	 */
	private String refToID(String ref) {
		ref = ref.replace(':','_');
		ref = ref.replace('?','_');
		ref = ref.replace('=','_');
		ref = ref.replace('/','_');
		ref = ref.replace('&','_');
		ref = ref.replace('.','_');
		
		return ref;
	}
	
	/**
	 * Retrieve the contents of a URL and store in the specified file
	 * 
	 * @param url The URL to read
	 * @param file The file to write to
	 * @throws IOException Indicates a failure to grab the URL from the remote location
	 */
	static void getURL(URLConnection url, File file) throws IOException {
		file.getParentFile().mkdirs();

		InputStream in = url.getInputStream();
		FileOutputStream out = new FileOutputStream(file);
		
		byte[] buffer = new byte[10000];
		int size = url.getContentLength();
		while (size > 0) {
			int read = in.read(buffer);
			out.write(buffer, 0, read);
			size -= read;
		}
		
		out.close();
		in.close();
	}
	
	/**
	 * Test case main
	 * 
	 * @param argv The arguments passed to the test (none supported)
	 */
	public static void main(String[] argv) {
		try {
			Launcher starter = new Launcher(System.getProperty("user.home")+"/.playground/cache");
			//LaunchConfig config = starter.getLaunch("roll4k","http://javaunlimited.net/games/launch.php?type=jnlp&id=50",true);
			
			LaunchConfig config = starter.getLaunch("trooper","http://slick.cokeandcode.com/demos/supertrooper.jnlp",true);
			for (int i=0;i<config.getJNLPFileCount();i++) {
				JNLPFile file = config.getJNLPFile(i);
				
				System.out.println(file.getName());
				System.out.println("\t"+file.getAuthor());
				System.out.println("\t"+file.getDescription());
				System.out.println("\t"+file.getHomepage());
			}
			
			config.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
