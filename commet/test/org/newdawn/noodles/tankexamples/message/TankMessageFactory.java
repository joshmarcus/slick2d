package org.newdawn.noodles.tankexamples.message;

import java.io.IOException;

import org.newdawn.noodles.message.Message;
import org.newdawn.noodles.message.MessageFactory;

/**
 * Factory to produce messages on reception in the tank example
 * 
 * @author kevin
 */
public class TankMessageFactory implements MessageFactory {
	/**
	 * @see org.newdawn.noodles.message.MessageFactory#createMessageFor(short)
	 */
	public Message createMessageFor(short id) throws IOException {
		switch (id) {
		case TankUpdateMessage.ID:
			return new TankUpdateMessage();
		case TankRemoveMessage.ID:
			return new TankRemoveMessage();
		}
		
		return null;
	}
}
