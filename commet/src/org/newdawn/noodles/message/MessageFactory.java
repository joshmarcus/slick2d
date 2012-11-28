package org.newdawn.noodles.message;

import java.io.IOException;

/**
 * Description of a class that can create message objects for recieved data based on
 * the protocol unique ID provided in the data stream.
 * 
 * @author kevin 
 */
public interface MessageFactory {

	/**
	 * Create a new message based on the given ID
	 * 
	 * @param id The ID of the message to be created
	 * @return A newly created and empty message object ready to be filled
	 * with the returned data
	 * @throws IOException Indicates a failure to create the right type of message, most
	 * commonly an unrecognised ID.
	 */
	public Message createMessageFor(short id) throws IOException;
}
