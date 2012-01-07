package org.newdawn.slick;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import org.newdawn.slick.util.ResourceLocation;

import android.content.res.AssetManager;
import android.util.Log;

public class AndroidResourceLocation implements ResourceLocation, URLStreamHandlerFactory {
	private static boolean handlerSet = false;
	
	private AssetManager manager;
	
	public AndroidResourceLocation(AssetManager manager) {
		this.manager = manager;
		if (!handlerSet) {
			handlerSet = true;
			URL.setURLStreamHandlerFactory(this);
		}
	}
	
	@Override
	public URL getResource(String ref) {
		try {
			return new URL("slick:"+ref);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public InputStream getResourceAsStream(String ref) {
		Log.e("SLICK", "Requesting: "+ref);
		
		try {
			return manager.open(ref);
		} catch (IOException e) {
			Log.e("SLICK", "Failed to open: "+ref,e);
			return null;
		}
	}
	
	private class Handler extends URLStreamHandler {

		@Override
		protected URLConnection openConnection(URL u) throws IOException {
			return new Connection(u, u.toString().substring(6));
		}
		
	}
	
	private class Connection extends URLConnection {
		private InputStream in;
		private String ref;
		
		public Connection(URL url, String ref) {
			super(url);
			
			this.ref = ref;
		}
		
		@Override
		public void connect() throws IOException {
			in = getResourceAsStream(ref);
		}
		
	}

	@Override
	public URLStreamHandler createURLStreamHandler(String protocol) {
		if (protocol.equals("slick")) {
			return new Handler();
		}
		
		return null;
	}
}
