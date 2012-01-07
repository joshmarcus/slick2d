package org.newdawn.slick.thingle.spi;

/**
 * Pluggability for thinlet GUI event call backs. 
 * 
 * @author kevin
 */
public interface MethodInvoker {
	/**
	 * Locate the method that will be invoked when a the specified name
	 * and argument types are specified in an event call back. The returned
	 * object can be anything applicable to the particular type of invoker and
	 * will be supplied (along with values) when the invocation takes place.
	 * 
	 * @param target The target object that will be invoked
	 * @param name The name of the target specified in the UI XML
	 * @param argumentTypes The argument types that will be passed to the invocation
	 * @return The identifer for the target located
	 * @throws ThingleException Indicates the target could not be located
	 */
	public Object getMethodHandle(Object target, String name, Class[] argumentTypes) throws ThingleException;
	
	/**
	 * Invoke the given target specific to this method invoker with the given
	 * argument values. 
	 * 
	 * @param target The target object that will be invoked
	 * @param methodHandle The target to invoke
	 * @param values The argument values to pass into the invocation
	 * @throws ThingleException Indicates the invocation caused a failure or
	 * the target is not appropriate to this method invoker
	 */
	public void invoke(Object methodHandle, Object target, Object[] values) throws ThingleException;
}
