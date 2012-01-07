package org.newdawn.noodles.chat;

import java.io.IOException;

import org.newdawn.noodles.message.Message;
import org.newdawn.noodles.message.MessageFactory;

/**
 * A factory to produce messages for the example chat client/server
 * 
 * @author kevin
 */
public class ChatMessageFactory implements MessageFactory {

	/**
	 * @see org.newdawn.noodles.message.MessageFactory#createMessageFor(short)
	 */
	public Message createMessageFor(short id) throws IOException {
		if (id == ChatMessage.ID) {
			return new ChatMessage();
		}
		
		throw new IOException("Unknown message recieved: "+id);
	}

}
