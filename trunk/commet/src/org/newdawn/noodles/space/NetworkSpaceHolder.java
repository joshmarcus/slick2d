package org.newdawn.noodles.space;

import java.util.ArrayList;

/**
 * A hideous registry of network spaces. This is used to support decoding messages
 * related to network spaces before knowing which network space they belong to
 * 
 * @author kevin
 */
public class NetworkSpaceHolder {
	/** The spaces that have been registered */
	private static ArrayList<NetworkSpace> spaces = new ArrayList<NetworkSpace>();
	
	/**
	 * Get the first, and hence default, space registered
	 * 
	 * @return The default space
	 */
	public static NetworkSpace getDefaultSpace() {
		return spaces.get(0);
	}
	
	/**
	 * Register a space with the holder
	 * 
	 * @param space The space to be registered
	 * @return The ID given to this network space
	 */
	public static int registerSpace(NetworkSpace space) {
		spaces.add(space);
		return spaces.indexOf(space);
	}
}
