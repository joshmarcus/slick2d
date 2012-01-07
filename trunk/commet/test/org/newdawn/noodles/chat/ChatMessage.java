package org.newdawn.noodles.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.newdawn.noodles.message.Message;

/**
 * A simple chat message containing the name of the sender and the message. Normally
 * you wouldn't include the name of the sender since it's open to spoofing and would
 * require server side validation. For test purposes it'll do.
 * 
 * @author kevin
 */
public class ChatMessage implements Message {
	/** The protocol unique ID */
	public static final int ID = 1;
	
	/** The name of the chat sender */
	private String sender;
	/** The chat text sent */
	private String message;
	
	/**
	 * Create a new message, used on reception
	 */
	public ChatMessage() {
		
	}
	
	/**
	 * Create a new message
	 * 
	 * @param sender The name of the sender
	 * @param message The message sent
	 */
	public ChatMessage(String sender, String message) {
		this.sender = sender;
		this.message = message;
	}

	/**
	 * Get the name of the sender
	 * 
	 * @return The name of the sender
	 */
	public String getSender() {
		return sender;
	}
	
	/**
	 * Get the text sent as chat
	 * 
	 * @return The text sent as chat
	 */
	public String getText() {
		return message;
	}
	
	/**
	 * @see org.newdawn.noodles.message.Message#decode(java.io.DataInputStream)
	 */
	public void decode(DataInputStream din) throws IOException {
		sender = din.readUTF();
		message = din.readUTF();
	}

	/**
	 * @see org.newdawn.noodles.message.Message#encode(java.io.DataOutputStream)
	 */
	public void encode(DataOutputStream dout) throws IOException {
		dout.writeUTF(sender);
		dout.writeUTF(message);
	}

	/**
	 * @see org.newdawn.noodles.message.Message#getID()
	 */
	public short getID() {
		return ID;
	}
}
