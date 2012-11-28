package org.newdawn.noodles.object;

import java.util.HashMap;

/**
 * A registry holding encoders against the classes they can encode. The encoders are based on
 * reflection so they're relatively slow to build, this registry provides a cache of encoders
 * so each class is only handled oned.
 * 
 * @author kevin
 */
public class ClassEncoderRegistry {
	/** The map of cached encoders */
	private static HashMap<Class<?>, ObjectEncoder> encoders = new HashMap<Class<?>, ObjectEncoder>();

	/** The map of cached encoders */
	private static HashMap<Class<?>, ObjectEncoder> encodersNoAnnotations = new HashMap<Class<?>, ObjectEncoder>();
	
	/**
	 * Get an encoder for a given object type
	 * 
	 * @param clazz The class whose encoder is required
	 * @return The encoder that can be used to encode instances of the given class
	 * @throws ClassEncodingException Indicates that the particular class type can not be encoded
	 */
	public static ObjectEncoder getEncoder(Class<?> clazz) throws ClassEncodingException {
		return getEncoder(clazz, false);
	}
	
	/**
	 * Get an encoder for a given object type
	 * 
	 * @param clazz The class whose encoder is required
	 * @param allFields True if all fields should be encoded regardless of annotation
	 * @return The encoder that can be used to encode instances of the given class
	 * @throws ClassEncodingException Indicates that the particular class type can not be encoded
	 */
	public static ObjectEncoder getEncoder(Class<?> clazz, boolean allFields) throws ClassEncodingException {
		HashMap<Class<?>, ObjectEncoder> map = encoders;
		if (allFields) {
			map = encodersNoAnnotations;
		}
		
		ObjectEncoder encoder = map.get(clazz);
		
		if (encoder == null) {
			encoder = new ObjectEncoder(clazz, allFields);
			map.put(clazz, encoder);
		}
		
		return encoder;
	}
}
