package org.newdawn.noodles.object;

/**
 * Indicates an inability to encode a particular object type 
 * 
 * @author kevin
 */
public class ClassEncodingException extends Exception {
	/**
	 * Create a new exception
	 * 
	 * @param msg The message describing the exception
	 * @param e The cause of this exception
	 */
	public ClassEncodingException(String msg, Throwable e) {
		super(msg, e);
	}

	/**
	 * Create a new exception
	 * 
	 * @param msg The message describing the exception
	 */
	public ClassEncodingException(String msg) {
		super(msg);
	}
}
