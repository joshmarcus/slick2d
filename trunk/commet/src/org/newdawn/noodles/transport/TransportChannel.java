package org.newdawn.noodles.transport;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

/**
 * A channel on which data can be sent
 * 
 * @author kevin
 */
public interface TransportChannel {
	/**
	 * Close the channel, releasing any underlying resources
	 * 
	 * @throws IOException Indicates an IO failure occured while closing
	 */
	public void close() throws IOException;
	
	/**
	 * Configure whether this channel should block on read/write
	 * 
	 * @param blocking True if this channel should block on read/write
	 * @throws IOException Indicates a failure configuring the underlying resources
	 */ 
	public void configureBlocking(boolean blocking) throws IOException;
	
	/**
	 * Get the address of the socket at the other end of the 
	 * channel
	 * 
	 * @return The address of the remote end of the channel
	 */
	public SocketAddress getRemoteSocketAddress();
	
	/**
	 * Attempt to read a set of data from the channel. The data will
	 * be placed in the provided buffer. If the channel is not blocking
	 * no data may be read if none is available. If the channel is blocking
	 * this operation will not return until some data has become available
	 * 
	 * @param data The buffer in which to place any read data in
	 * @return The number of bytes read (or -1 if the end of the channel has been reached)
	 * @throws IOException Indicates a failure to read the underling resource
	 */
	public int read(ByteBuffer data) throws IOException;

	/**
	 * Write some data to the channel
	 * 
	 * @param data The byte buffer that should be written
	 * in one batch down the channel
	 * @param reliable True if the data should be guarnateed to arrive. Unreliable transport
	 * maps directly to UDP style transport. i.e. the packet will be sent and will most likely
	 * arrive. The packets may arrive in a different order to they are sent. The user is expected
	 * to timestamp/sequence their packets to remove older packets where required.
	 * @throws IOException Indicates a failure write the data to the channel. Channel 
	 * is either closed or timed out
	 */
	public void write(ByteBuffer data, boolean reliable) throws IOException;
	
	/**
	 * Check if the channel has been closed
	 * 
	 * @return True if the channel has been closed
	 */
	public boolean isClosed();
	
	/**
	 * Get the ID given by the server for this connection
	 * 
	 * @return The ID given by the server for this connection
	 */
	public int getChannelID();
}
