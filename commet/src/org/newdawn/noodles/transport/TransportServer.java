package org.newdawn.noodles.transport;

import java.io.IOException;

/**
 * A server that can recieve connection by transport channels and spawn the
 * appropriate server side channels
 * 
 * @author kevin
 */
public interface TransportServer {
	/**
	 * Check for new connections. If a new connection has been recieved an appropriate
	 * channel will be created and passed back
	 * 
	 * @return A newly connected channel or null if no new connections are pending
	 * @throws IOException Indicates a failure to accept the connection, normallying
	 * indicating the underling socket has been closed
	 */
	public TransportChannel accept() throws IOException;
	
	/**
	 * Wait for some activity within this server. Either a new connection
	 * arriving or some data arriving on a channel that has been accepted
	 * by this server. 
	 * 
	 * @throws IOException Indicates an IO failure while waiting.
	 */
	public void waitForActivity() throws IOException;

	/**
	 * Wait for some activity within this server. Either a new connection
	 * arriving or some data arriving on a channel that has been accepted
	 * by this server.
	 * 
	 * The wait will also return if the given timeout value (in ms) expires
	 * 
	 * @param timeout The time in ms to wait before returning anyway
	 * @return True if some activity occured, false if we timed out
	 * @throws IOException Indicates an IO failure while waiting.
	 */
	public boolean waitForActivity(int timeout) throws IOException;
	
	/**
	 * Close down the server. No more connections will be accepted
	 * 
	 * @throws IOException Indicates a failure during shutdown
	 */
	public void close() throws IOException;
}
