package org.newdawn.noodles.object.encoders;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import org.newdawn.noodles.object.FieldEncoder;

/**
 * Encoder to set a field based on recieved data, and send data based on the state of a field
 * whose type is "float".
 * 
 * @author kevin
 */
public class FloatEncoder implements FieldEncoder {
	/** The field to be set/get */
	private Field field;
	
	/**
	 * Create a new encoder
	 * 
	 * @param field The field to be maintained
	 */
	public FloatEncoder(Field field) {
		this.field = field;
		field.setAccessible(true);
	}
	
	/**
	 * @see org.newdawn.noodles.object.FieldEncoder#decode(java.io.DataInputStream, java.lang.Object)
	 */
	public void decode(DataInputStream din, Object value) throws IOException {
		try {
			field.setFloat(value, din.readFloat());
		} catch (IllegalArgumentException e) {
			throw new IOException("Failed to set: "+field);
		} catch (IllegalAccessException e) {
			throw new IOException("Failed to set: "+field);
		}
	}

	/**
	 * @see org.newdawn.noodles.object.FieldEncoder#encode(java.io.DataOutputStream, java.lang.Object)
	 */
	public void encode(DataOutputStream dout, Object value) throws IOException {
		try {
			dout.writeFloat(field.getFloat(value));
		} catch (IllegalArgumentException e) {
			throw new IOException("Failed to get: "+field);
		} catch (IllegalAccessException e) {
			throw new IOException("Failed to get: "+field);
		}
	}

	
}
