package org.newdawn.noodles.space;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.newdawn.noodles.message.Message;

/**
 * Create a new message indicating the removal of an object from the
 * network space.
 * 
 * @author kevin
 */
public class DestroyMessage implements Message {
	/** The protocol unique ID of this message */
	public static final short ID = 1001;
	
	/** The ID of the object to be removed */
	private short id;
	/** The ID of the owner of the object to be removed */
	private short clientID;

	/**
	 * Create a new message, used on reception
	 */
	public DestroyMessage() {
	}
	
	/**
	 * Create a new message
	 * 
	 * @param id The ID of the object to be removed
	 * @param clientID The ID of the owner of the object
	 */
	public DestroyMessage(short id, short clientID) {
		this.id = id;
		this.clientID = clientID;
	}
	
	/**
	 * Get the ID of the object to be removed
	 * 
	 * @return The ID of of the object to be removed
 	 */
	public short getObjectID() {
		return id;
	}
	
	/**
	 * Get the ID of the owner of the object to be removd
	 * 
	 * @return The ID of the owner of the object to be removed
	 */
	public short getOwnerID() {
		return clientID;
	}
	
	/**
	 * @see org.newdawn.noodles.message.Message#decode(java.io.DataInputStream)
	 */
	public void decode(DataInputStream din) throws IOException {
		id = din.readShort();
		clientID = din.readShort();
	}

	/**
	 * @see org.newdawn.noodles.message.Message#encode(java.io.DataOutputStream)
	 */
	public void encode(DataOutputStream dout) throws IOException {
		dout.writeShort(id);
		dout.writeShort(clientID);
	}

	/**
	 * @see org.newdawn.noodles.message.Message#getID()
	 */
	public short getID() {
		return ID;
	}

}
