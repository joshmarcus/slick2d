package org.newdawn.noodles.space;

import org.newdawn.noodles.message.Message;
import org.newdawn.noodles.message.MessageChannel;
import org.newdawn.noodles.transport.TransportChannel;

/**
 * A listener recieving events indicating changes in state in the network space
 * 
 * @author kevin
 */
public interface NetworkSpaceListener {

	/**
	 * Notification that an object was added to the space
	 * 
	 * @param source The space to which the object was added
	 * @param obj The object that was added
	 * @param id The object ID for the object within this space
	 * @param ownerID The ID of the owner of this object
	 */
	public void objectAdded(NetworkSpace source, Object obj, short id, short ownerID);
	
	/**
	 * Notification that an object was removed from the space
	 * 
	 * @param source The space to which the object was removed
	 * @param obj The object that was removed
	 * @param id The ID of the object removed from the space
	 * @param ownerID The ID of the owner of the object removed
	 */
	public void objectRemoved(NetworkSpace source, Object obj, short id, short ownerID);
	
	/**
	 * Notification that a channel disconnected from the space
	 * 
	 * @param channel The channel that disconnected
	 */
	public void channelDisconnected(MessageChannel channel);

	/**
	 * Notification that a custom message, not a network space message was recieved.
	 * 
	 * @param channel The channel from which the message was recieved
	 * @param message The message that was recieved
	 */
	public void customMessageRecieved(MessageChannel channel, Message message);
}
