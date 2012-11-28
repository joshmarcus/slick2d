package org.newdawn.noodles.object.encoders;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import org.newdawn.noodles.object.ClassEncoderRegistry;
import org.newdawn.noodles.object.ClassEncodingException;
import org.newdawn.noodles.object.FieldEncoder;
import org.newdawn.noodles.object.ObjectEncoder;

/**
 * An encoder to maintain a field whose type is an object 
 * 
 * @author kevin
 */
public class ObjectFieldEncoder implements FieldEncoder {
	/** The encoder used to read/write the object type */
	private ObjectEncoder encoder;
	/** The field being maintained */
	private Field field;
	
	/**
	 * Create a new encoder
	 * 
	 * @param field The field to maintain
	 * @throws ClassEncodingException Indicates the particular type can't be encoded
	 */
	public ObjectFieldEncoder(Field field) throws ClassEncodingException {
		this.field = field;
		field.setAccessible(true);
		encoder = ClassEncoderRegistry.getEncoder(field.getType());
	}
	
	/**
	 * @see org.newdawn.noodles.object.FieldEncoder#decode(java.io.DataInputStream, java.lang.Object)
	 */
	public void decode(DataInputStream din, Object value) throws IOException {
		if (din.readBoolean()) {
			try {
				Object subValue = field.get(value);
				encoder.decode(din, subValue);
			} catch (IllegalArgumentException e) {
				throw new IOException("Failed to access field: "+field);
			} catch (IllegalAccessException e) {
				throw new IOException("Failed to access field: "+field);
			}
		}
	}

	/**
	 * @see org.newdawn.noodles.object.FieldEncoder#encode(java.io.DataOutputStream, java.lang.Object)
	 */
	public void encode(DataOutputStream dout, Object value) throws IOException {
		try {
			Object subValue = field.get(value);
			if (subValue != null) {
				dout.writeBoolean(true);
				encoder.encode(dout, subValue);
			} else {
				dout.writeBoolean(false);
			}
		} catch (IllegalArgumentException e) {
			throw new IOException("Failed to access field: "+field);
		} catch (IllegalAccessException e) {
			throw new IOException("Failed to access field: "+field);
		}
	}

}
