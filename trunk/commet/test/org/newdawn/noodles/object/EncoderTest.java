package org.newdawn.noodles.object;

import org.newdawn.noodles.object.ClassEncodingException;
import org.newdawn.noodles.object.ObjectEncoder;
import org.newdawn.noodles.space.Blob;

/**
 * Simple test to stimualte the encoder analysis
 * 
 * @author kevin
 */
public class EncoderTest {

	/**
	 * Entry point in the test
	 * 
	 * @param argv No arugments
	 * @throws ClassEncodingException Indicates a failure to build an encoder for the class
	 */
	public static void main(String[] argv) throws ClassEncodingException {
		ObjectEncoder encoder = new ObjectEncoder(Blob.class, false);
	}
}
