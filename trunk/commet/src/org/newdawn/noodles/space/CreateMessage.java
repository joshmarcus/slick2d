package org.newdawn.noodles.space;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.newdawn.noodles.message.Message;

/**
 * A message describing the creation of an object in network space
 * 
 * @author kevin
 */
public class CreateMessage implements Message {
	/** The protocol unique ID of this message type */
	public static final short ID = 1000;
	
	/** The name of the class to be instanced */
	private String className;
	/** The ID of the object to be created */
	private short id;
	/** The ID of the owner of the object to be created */
	private short ownerID;
	
	/**
	 * Create a new empty message, used on reception
	 */
	public CreateMessage() {
	}
	
	/**
	 * Create a new message
	 * 
	 * @param className The name of the class to be instanced
	 * @param id The ID that should be assigned to the new object 
	 * @param ownerID The ID of the owner of the object
	 */
	public CreateMessage(String className, short id, short ownerID) {
		this.className = className;
		this.id = id;
		this.ownerID = ownerID;
	}
	
	/**
	 * Get the name of the class that should be instanced. Eventually this 
	 * should be encoded as an integer and a shared table maintained - maybe
	 * as a seperate network space?
	 * 
	 * @return The name of the class that should be instanced
	 */
	public String getClassName() {
		return className;
	}
	
	/**
	 * Get the ID of the object to be created
	 * 
	 * @return The ID of the object to be created
	 */
	public short getObjectID() {
		return id;
	}
	
	/**
	 * Get the ID of the owner of the new object
	 * 
	 * @return The ID of the owner of the new object
	 */
	public short getOwnerID() {
		return ownerID;
	}
	
	/**
	 * @see org.newdawn.noodles.message.Message#decode(java.io.DataInputStream)
	 */
	public void decode(DataInputStream din) throws IOException {
		className = din.readUTF();
		id = din.readShort();
		ownerID = din.readShort();
	}

	/**
	 * @see org.newdawn.noodles.message.Message#encode(java.io.DataOutputStream)
	 */
	public void encode(DataOutputStream dout) throws IOException {
		dout.writeUTF(className);
		dout.writeShort(id);
		dout.writeShort(ownerID);
	}

	/**
	 * @see org.newdawn.noodles.message.Message#getID()
	 */
	public short getID() {
		return ID;
	}

}
