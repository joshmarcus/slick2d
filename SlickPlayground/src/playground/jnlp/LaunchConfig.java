package playground.jnlp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.newdawn.slick.util.Log;

/**
 * A launch configuration built from a JNLP descriptor that can be used
 * to launch a new VM and the application specified
 *
 * @author kevin
 */
public class LaunchConfig {
	/** The classpath built from downloaded JARs */
	private String classpath = "";
	/** The main-class for the application */
	private String mainclass;
	/** The libpath built from downloaded nativelib extractions */
	private String libPath = "-Djava.library.path=";
	/** The maximum heap */
	private int heap = 64;
	/** The location in which we should run the application */
	private String path;
	
	/** The policy content */
	private String policy = "";
	/** The list of JNLP files building up this configuration */
	private ArrayList files = new ArrayList();
	/** The ID of the application being launched */
	private String id;
	
	/**
	 * Create a new launch configuration
	 * 
	 * @param id The ID of the download used to identify the cache location
	 */ 
	public LaunchConfig(String id) {
		this.id = id;
	}
	
	/**
	 * Add a JNLP file that contributed to this launch configuration
	 * 
	 * @param file The file to add
	 */
	public void addJNLP(JNLPFile file) {
		files.add(file);
	}
	
	/**
	 * Get a count of the number of JNLPs that contributed to this launch configuration
	 * 
	 * @return The number of JNLPs that contributed to this launch configuration
	 */
	public int getJNLPFileCount() {
		return files.size();
	}
	
	/**
	 * Get the JNLP at the specifid index that contributed to this configuration
	 * 
	 * @param i The index of the JNLP file to retrieve
	 * @return The JNLP file requested
	 */
	public JNLPFile getJNLPFile(int i) {
		return (JNLPFile) files.get(i);
	}
	
	/**
	 * Set the base path where the application should be run from and the path from
	 * which directories should be held relative to
	 * 
	 * @param path The base path
	 */
	public void setPathsRelativeTo(String path) {
		this.path = path;
	}
	
	/**
	 * Add a cache location where a set of signed and approved JARs are held
	 * 
	 * @param dir The location where the signed jars are
	 */
	public void addSignedCodebase(File dir) {
		try {
			policy += "grant codeBase \""+dir.toURI().toASCIIString()+"*\" {\n";
			policy += "  permission java.security.AllPermission;\n";
			policy += "};\n";
			policy += "\n";
		} catch (Exception e) {
			Log.error(e);
		}
	}

	/**
	 * Add a cache location where a set of untrusted JARs are
	 * 
	 * @param host The host from which the JARs were downloaded and hence can connect
	 * @param dir The location containing the untrusted JARs
	 */
	public void addUnsignedCodebase(String host, File dir) {
		try {
			policy += "grant codeBase \""+dir.toURI().toASCIIString()+"*\" {\n";
			policy += "  permission java.net.SocketPermission \""+host+"\", \"connect\";\n";  
			policy += "  permission java.util.PropertyPermission \"*\", \"read\";\n";
			policy += "};\n";
			policy += "\n";
		} catch (Exception e) {
			Log.error(e);
		}
	}
	
	/**
	 * Add a JAR that is required to be included as past of the launch
	 * 
	 * @param jar The JAR to be included
	 */
	public void addJar(String jar) {
		if (jar.startsWith(path)) {
			jar = jar.substring(path.length()+1);
		}
		classpath += jar+File.pathSeparator;
	}
	
	/**
	 * Set the maximum heap specified. It will only be applied if it's greater
	 * than the currently configured heap
	 * 
	 * @param heap The new heap size in megabytes
	 */
	public void setMaxHeap(int heap) {
		this.heap = Math.max(heap, this.heap);
	}
	
	/**
	 * Add a path containing native libraries for this launch
	 * 
	 * @param lPath The path containing the native libraries
	 */
	public void addLibraryPath(String lPath) {
		if (lPath.startsWith(path)) {
			lPath = lPath.substring(path.length()+1);
		}
		libPath += lPath+File.pathSeparator;
	}
	
	/**
	 * Set the main class to execute for this application
	 * 
	 * @param mainclass The main clas to execute
	 */
	public void setMainClass(String mainclass) {
		if (this.mainclass == null) {
			this.mainclass = mainclass;
		}
	}
	
	/**
	 * Run a seperate VM to start this application description
	 * 
	 * @return The return code from the application
	 * @throws IOException Indicates a failure to find the required files
	 * @throws InterruptedException Indicates there was a process interrupted in the blocking wait
	 */
	public int run() throws IOException, InterruptedException {
		File runDir = new File(path);
		
		// copy the JNLP implementation into the right place
		File libDir = new File(runDir, "lib");
		libDir.mkdirs();
		
		URL libURL = Thread.currentThread().getContextClassLoader().getResource("res/pgjnlp.jar");
		if (libURL == null) {
			throw new IOException("Packaging error, unable to locate pgjnlp.jar");
		}
		Launcher.getURL(libURL.openConnection(), new File(libDir, "pgjnlp.jar"));
		addJar("lib/pgjnlp.jar");
		addSignedCodebase(libDir);
		
		File policyFile = new File(runDir, id+"/java.policy");
		
		try {
			PrintStream pout = new PrintStream(new FileOutputStream(policyFile));
			pout.println(policy);
			pout.flush();
			pout.close();
		} catch (IOException e) {
			Log.error(e);
			throw new IOException("Unable to create java policy file");
		}
		
		Map map = System.getenv();
		String[] env = new String[map.keySet().size()];
		Iterator keys = map.keySet().iterator();
		int index = 0;
		while (keys.hasNext()) {
			String key = (String) keys.next();
			env[index] = key+"="+(String) map.get(key);
			index++;
		}
		
		String java = System.getProperty("java.home")+"/bin/java";
		String command = java+" -Xmx"+heap+"m -Dpg.id="+id+" -Djava.security.manager -Djava.security.policy="+id+"/java.policy "+libPath+" -cp "+classpath+" "+mainclass;
		
		Log.info("Running: "+command);
		Log.info("From: "+runDir);
		
		Process process = Runtime.getRuntime().exec(command, env, runDir);
		new Pipe(process.getInputStream(), System.out);
		new Pipe(process.getErrorStream(), System.err);
		
		int code = process.waitFor();
		return code;
	}
	
	/**
	 * A utility to pipe one input stream to an output stream
	 *
	 * @author kevin
	 */
	private class Pipe extends Thread {
		/** The stream to read from */
		private InputStream in;
		/** The stream to write out to */
		private OutputStream out;
		
		/**
		 * Pipe the contents of the input stream to the output stream
		 * 
		 * @param in The input stream to read from
		 * @param out The output stream to write to
		 */
		public Pipe(InputStream in, OutputStream out) {
			this.in = in;
			this.out = out;
		
			setDaemon(false);
			start();
		}
		
		/**
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			while (true) {
				try {
					int i = in.read();
					if (i < 0) {
						return;
					}
					out.write(i);
					out.flush();
				} catch (IOException e) {
					return;
				}
			}
		}
	}
}
