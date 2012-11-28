package org.newdawn.noodles.tankexamples.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.newdawn.noodles.message.Message;

/**
 * Message indicating a tank has been removed
 * 
 * @author kevin
 */
public class TankRemoveMessage implements Message {
	/** The unique ID of the message type */
	public static final short ID = 2;
	
	/** The ID of the owner of the tank being removed */
	private int channelID;
	
	/**
	 * Create a new message - used on reception
	 *
	 */
	public TankRemoveMessage() {
	}
	
	/**
	 * Create a new message
	 * 
	 * @param channelID The ID of the tank being removed
	 */
	public TankRemoveMessage(int channelID) {
		this.channelID = channelID;
	}
	
	/**
	 * Get the ID of the tank being removed
	 * 
	 * @return The ID of the tank being removed
	 */
	public int getChannelID() {
		return channelID;
	}
	
	/**
	 * @see org.newdawn.noodles.message.Message#decode(java.io.DataInputStream)
	 */
	public void decode(DataInputStream din) throws IOException {
		channelID = din.readInt();
	}

	/**
	 * @see org.newdawn.noodles.message.Message#encode(java.io.DataOutputStream)
	 */
	public void encode(DataOutputStream dout) throws IOException {
		dout.writeInt(channelID);
	}

	/**
	 * @see org.newdawn.noodles.message.Message#getID()
	 */
	public short getID() {
		return ID;
	}

}
