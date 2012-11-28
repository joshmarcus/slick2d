package org.newdawn.noodles.object;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Description of any class that can maintain the state of field by encoding
 * and decoding network updates for it.
 * 
 * @author kevin
 */
public interface FieldEncoder {
	/**
	 * Encode the state of the field. This method should be implemented to be
	 * symetric to decode();
	 * 
	 * @param dout The stream on which the state should be written
	 * @param value The value containing the field
	 * @throws IOException Indicates a failure to enode the field state
	 */
	public void encode(DataOutputStream dout, Object value) throws IOException;
	
	/**
	 * Decode the state of the field. This method should be implemented to be
	 * symetric to encode();
	 * 
	 * @param din The stream from which the state can be read
	 * @param value The value containing the field
	 * @throws IOException Indicate a failure to decode the state into the field
	 */
	public void decode(DataInputStream din, Object value) throws IOException;
}
