package org.newdawn.noodles.space;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.newdawn.noodles.message.Message;

/**
 * A message describing the update to the network space
 * 
 * @author kevin
 */
public class UpdateMessage implements Message {
	/** The ID of the message type */
	public static final short ID = 1002;
	
	/** The list of shared objects to be sent in this update */
	private ArrayList<SharedObject> objectsToSend = new ArrayList<SharedObject>();
	
	/**
	 * Create a new empty message - used on reception
	 */
	public UpdateMessage() {	
	}
	
	/**
	 * Add a shared object to be sent in this update
	 * 
	 * @param object The object to sent in this update
	 */
	public void add(SharedObject object) {
		objectsToSend.add(object);
	}
	
	/**
	 * @see org.newdawn.noodles.message.Message#decode(java.io.DataInputStream)
	 */
	public void decode(DataInputStream din) throws IOException {
		NetworkSpace space = NetworkSpaceHolder.getDefaultSpace();
		int count = din.readShort();
		
		for (int i=0;i<count;i++) {
			int id = din.readInt();
			SharedObject object = space.getSharedObjectByID(id);
			if (object == null) {
				throw new IOException("Recieved update for unknown object with id: "+id);
			}
			
			object.updateFrom(din);
		}
	}

	/**
	 * @see org.newdawn.noodles.message.Message#encode(java.io.DataOutputStream)
	 */
	public void encode(DataOutputStream dout) throws IOException {
		NetworkSpace space = NetworkSpaceHolder.getDefaultSpace();
		dout.writeShort(objectsToSend.size());
		
		for (int i=0;i<objectsToSend.size();i++) {
			SharedObject object = objectsToSend.get(i);
			
			dout.writeInt(object.getCombinedID());
			object.updateTo(dout);
		}
	}

	/**
	 * @see org.newdawn.noodles.message.Message#getID()
	 */
	public short getID() {
		return ID;
	}

}
