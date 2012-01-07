package org.newdawn.slick.thingle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.newdawn.slick.thingle.spi.MethodInvoker;
import org.newdawn.slick.thingle.spi.ThingleException;

/**
 * A method invoker to handle the default case of invoking local 
 * java methods.
 * 
 * @author kevin
 */
public class JavaLocalMethodInvoker implements MethodInvoker {

	/**
	 * @see org.newdawn.slick.thingle.spi.MethodInvoker#getMethodHandle(java.lang.Object, java.lang.String, java.lang.Class[])
	 */
	public Object getMethodHandle(Object target, String name, Class[] argumentTypes)
			throws ThingleException {
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

		try {
			((Method) methodHandle).invoke(target, values);
		} catch (InvocationTargetException ite) {
			throw new ThingleException(ite.getTargetException());
		} catch (Throwable throwable) {
			throw new ThingleException(throwable);
		}
	}

}
