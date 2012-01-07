package org.newdawn.slick.thingle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.newdawn.slick.thingle.spi.MethodInvoker;
import org.newdawn.slick.thingle.spi.ThingleException;

/**
 * A method invoker to handle old action handlers which expected thinlet
 * components to be passed as objects.
 * 
 * @author kevin
 */
public class LegacyMethodInvoker implements MethodInvoker {

	/**
	 * @see org.newdawn.slick.thingle.spi.MethodInvoker#getMethodHandle(java.lang.Object, java.lang.String, java.lang.Class[])
	 */
	public Object getMethodHandle(Object target, String name, Class[] argumentTypes)
			throws ThingleException {
		if (argumentTypes != null) {
			// convert the thingle widgets to objects for legacy callbacks
			for (int i=0;i<argumentTypes.length;i++) {
				if (argumentTypes[i] == Widget.class) {
					argumentTypes[i] = Object.class;
				}
			}
		}
		
		try {
			return target.getClass().getMethod(name, argumentTypes);
		} catch (Exception e) {
			throw new ThingleException(e);
		}
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.MethodInvoker#invoke(java.lang.Object, java.lang.Object, java.lang.Object[])
	 */
	public void invoke(Object methodHandle, Object target, Object[] values)
			throws ThingleException {
		if (values != null) {
			// convert the thingle widgets to objects for legacy callbacks
			for (int i=0;i<values.length;i++) {
				if (values[i] instanceof Widget) {
					values[i] = ((Widget) values[i]).component;
				}
			}
		}
		
		try {
			((Method) methodHandle).invoke(target, values);
		} catch (InvocationTargetException ite) {
			throw new ThingleException(ite.getTargetException());
		} catch (Throwable throwable) {
			throw new ThingleException(throwable);
		}
	}

}
