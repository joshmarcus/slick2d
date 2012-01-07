package org.newdawn.noodles.space;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.newdawn.noodles.object.ClassEncoderRegistry;
import org.newdawn.noodles.object.ClassEncodingException;
import org.newdawn.noodles.object.ObjectEncoder;

/**
 * Wrapper storing object specific data for each object stored in the network space
 * 
 * @author kevin
 */
public class SharedObject {
	/** The encoder used to read/write the object state */
	private ObjectEncoder encoder;
	/** The object being wrapped */
	private Object target;
	/** The ID of the owner of the object */
	private short ownerID;
	/** The ID of the object within the network space */
	private short id;
	/** Who has authority to make changes to the wrapped object */
	private int authority;
	/** The server state for the object - only stored for local authority */
	private Object serverState;
	
	/**
	 * Create a new shared object wrapping an object placed in the network space
	 * 
	 * @param target The target object
	 * @param id The ID of the object within the space
	 * @param clientID The ID of the owner of the object 
	 * @param authority Who has the authority to make changes to the object
	 * @throws ClassEncodingException Indicates the object can't be encoded
	 */
	public SharedObject(Object target, short id, short clientID, int authority) throws ClassEncodingException {
		this.target = target;
		this.id = id;
		this.ownerID = clientID;
		this.authority = authority;
		
		encoder = ClassEncoderRegistry.getEncoder(target.getClass());
		
		if (authority == NetworkSpace.LOCAL_AUTHORITY) {
			try {
				Constructor con = target.getClass().getDeclaredConstructor(new Class[0]);
				con.setAccessible(true);
				serverState = con.newInstance(new Object[0]);
			} catch (InstantiationException e) {
				throw new ClassEncodingException("Shared objects must have the default constructor: "+target.getClass());
			} catch (IllegalAccessException e) {
				throw new ClassEncodingException("Shared objects must have the default constructor: "+target.getClass());
			} catch (SecurityException e) {
				throw new ClassEncodingException("Shared objects must have the default constructor: "+target.getClass());
			} catch (IllegalArgumentException e) {
				throw new ClassEncodingException("Shared objects must have the default constructor: "+target.getClass());
			} catch (InvocationTargetException e) {
				throw new ClassEncodingException("Shared objects must have the default constructor: "+target.getClass());
			} catch (NoSuchMethodException e) {
				throw new ClassEncodingException("Shared objects must have the default constructor: "+target.getClass());
			} 
		}
	}
	
	/**
	 * Get who has authority to change this object
	 * @see NetworkSpace#LOCAL_AUTHORITY
	 * @see NetworkSpace#REMOTE_AUTHORITY
	 * 
	 * @return Who has authority to change this object
	 */
	public int getAuthority() {
		return authority;
	}
	
	/**
	 * Get the target object stored in the space
	 * 
	 * @return The object added to the space
	 */
	public Object getTarget() {
		return target;
	}
	
	/** 
	 * Get the ID of the owner 
	 * 
	 * @return The ID of the owner of the target object
	 */
	public short getOwnerID() {
		return ownerID;
	}
	
	/**
	 * Get the ID of the object within the network space 
	 * 
	 * @return The ID of the object within the network space
	 */ 
	public short getObjectID() {
		return id;
	}
	
	/**
	 * Get the combined ID - owner and object IDs 
	 * 
	 * @return The combined ID
	 */
	public int getCombinedID() {
		return (ownerID << 16) + id;
	}
	
	/**
	 * Update the target object 
	 *  
	 * @param din The input stream from which to read the state
	 * @throws IOException Indicates a failure to read the state
	 */
	public void updateFrom(DataInputStream din) throws IOException {
		if (serverState != null) {
			encoder.decode(din, serverState);
		} else {
			encoder.decode(din, target);
		}
	}
	
	/**
	 * Write the target object's state out
	 * 
	 * @param dout The stream on which to write the state
	 * @throws IOException Indicates a failure to write the state
	 */
	public void updateTo(DataOutputStream dout) throws IOException {
		encoder.encode(dout, target);
	}
}
