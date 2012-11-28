package org.newdawn.noodles.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Description of a simple blob of data to pass across a Message channel
 * 
 * @author kevin
 */
public interface Message {
	/** 
	 * Get a protocol unique ID for this type of message
	 * 
	 * @return The ID of this message type
	 */
	public short getID();
	
	/**
	 * Encode the contents of this message to the given stream/
	 * Note that this method must be symetric with the decode().
	 * 
	 * @param dout The stream the message should be written to
	 * @throws IOException Indicate a failure to the framework while encoding
	 */
	public void encode(DataOutputStream dout) throws IOException;
	
	/**
	 * Decode a message from the given stream. Read off each part of the message.
	 * Note that this method must be symetric with the encode().
	 * 
	 * @param din The stream from the message contents can be read 
	 * @throws IOException Indicate a failure to the framework while decoding
	 */
	public void decode(DataInputStream din) throws IOException;
}
