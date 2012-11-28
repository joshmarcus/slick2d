package org.newdawn.noodles.space;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.noodles.message.Message;
import org.newdawn.noodles.message.MessageChannel;
import org.newdawn.noodles.message.MessageFactory;
import org.newdawn.noodles.object.ClassEncodingException;
import org.newdawn.noodles.transport.TransportChannel;
import org.newdawn.noodles.transport.TransportFactory;
import org.newdawn.noodles.util.Log;

/**
 * A space in which object can be placed and syncrhonized across a network 
 * transport. The object placed in the space must be annotated with @NetworkField
 * to describe which parts of the object should be synced.
 * 
 * @author kevin
 */
public class NetworkSpace {
	/** The TCP transport layer indicator */
	public static final int TCP = TransportFactory.TCP;
	/** The UDP transport layer indicator */
	public static final int UDP = TransportFactory.UDP;
	
	/** Indicates the object is under local control */
	public static final int LOCAL_AUTHORITY = 1;
	/** Indicates the object is under remote control */
	public static final int REMOTE_AUTHORITY = 2;

	/**
	 * Configure the transport to be produced. 
	 * 
	 * @param m The transport layer indicator. Should be one of {@link #TCP} or {@link #UDP}
	 */
	public static void configureMode(int m) {
		TransportFactory.configureMode(m);
	}
	
	/** The list of shared objects being synced */
	private ArrayList<SharedObject> shared = new ArrayList<SharedObject>();
	/** The list of channels connected to the space - on the client side this should be only one */
	private ArrayList<MessageChannel> channels = new ArrayList<MessageChannel>();
	/** The mapping between underlying transport channels and their message based wrappers */
	private HashMap<TransportChannel, MessageChannel> channelMap = new HashMap<TransportChannel, MessageChannel>();
	
	/** The mapping between objects as their shared object data wrappers */
	private HashMap<Object, SharedObject> extMap = new HashMap<Object, SharedObject>();
	/** The mapping between combined ID of objects and their shared object wrappers */
	private HashMap<Integer, SharedObject> idMap = new HashMap<Integer, SharedObject>();
	
	/** The interval in ms at which to send updates */
	private int updateInterval;
	/** The timer controlling update sends */
	private int updateTimer;
	/** The factory used to produce recieved messags */
	private SpaceMessageFactory factory = new SpaceMessageFactory();
	
	/** The next ID to give an added object */
	private short nextID = 1;
	/** The ID of the channel - client side only */
	private short channelID = 0;
	
	/** The listeners to be notified of space events */
	private ArrayList<NetworkSpaceListener> listeners = new ArrayList<NetworkSpaceListener>();
	/** True if we're broadcast updates we're given */
	private boolean broadcast;
	/** The global ID given for this network space */
	private int spaceID;
	
	/**
	 * Create a new network space as a client
	 * 
	 * @param host The host to connect to
	 * @param port The port to connect to
	 * @param updateInterval The interval in ms to send updates to locally controlled objects
	 * @throws IOException Indicates a failure make the initial connection
	 */
	public NetworkSpace(String host, int port, int updateInterval) throws IOException {
		this(TransportFactory.createChannel(host, port), updateInterval);
		broadcast = false;
	}

	/**
	 * Create a new network space as a client
	 * 
	 * @param channel The channel to used to connect to the server
	 * @param updateInterval The interval in ms to send updates to locally controlled objects
	 */
	public NetworkSpace(TransportChannel channel, int updateInterval) {
		this(updateInterval);
		broadcast = false;
		
		try {
			addChannel(channel);
		} catch (IOException e) {
			// should never happen since we won't be broadcasting
			// state 
			Log.error(e);
		}
		
		channelID = (short) channel.getChannelID();
	}
	
	/**
	 * Create an empty network space to which channels can be added as they connect.
	 * This constructor is intended to be used server side.
	 * 
	 * @param updateInterval The interval in ms between updating clients
	 */
	public NetworkSpace(int updateInterval) {
		this.updateInterval = updateInterval;
		updateTimer = updateInterval;
		
		// server side
		broadcast = true;
		
		spaceID = NetworkSpaceHolder.registerSpace(this);
	}
	
	/**
	 * Set the message factory used to produce recieved messages
	 * 
	 * @param factory The factory to use when decoding incoming messages
	 */
	public void setMessageFactory(SpaceMessageFactory factory) {
		this.factory = factory;
	}
	
	/**
	 * Get the owner ID of the local client
	 * 
	 * @return The owner ID of the local client
	 */
	public short getLocalOwnerID() {
		if (channels.size() == 1) {
			return (short) channels.get(0).getChannelID();
		}
		
		return 0;
	}
	
	/**
	 * Get a shared object based on it's combined ID (owner and object)
	 * 
	 * @param id The combined ID of the shared object to retrieve
	 * @return The shared object to retrieved or null if none exists with the given ID
	 */
	SharedObject getSharedObjectByID(int id) {
		return idMap.get(id);
	}
	
	/**
	 * Add a listener to be notified of space events
	 * 
	 * @param listener The listener to be notified of space events
	 */
	public void addListener(NetworkSpaceListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Remove a listener from the space, it will no longer be notified of events
	 * 
	 * @param listener The listener to be removed from the space
	 */
	public void removeListener(NetworkSpaceListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Add a channel that that should send and recieve updates to this space
	 * 
	 * @param channel The channel to be added to the broadcast
	 * @throws IOException Indicates a failure to send initial state
	 */
	public void addChannel(TransportChannel channel) throws IOException {
		MessageChannel msg = new MessageChannel(factory, channel);
		channelMap.put(channel, msg);
		channels.add(msg);
		
		if (broadcast) {
			sendState(msg);
		}
	}
	
	/**
	 * Remove a channel from the space. This channel will no longer be checked
	 * for updates or recieve updates from this space
	 * 
	 * @param channel The channel to be removed 
	 */
	public void removeChannel(TransportChannel channel) {
		MessageChannel msg = channelMap.remove(channel);
		channels.remove(msg);
	}
	
	/**
	 * Add an object to the space with local authority
	 * 
	 * @param obj The object to be added
	 */
	public void add(Object obj) {
		add(obj, LOCAL_AUTHORITY);
	}

	/**
	 * Add an object to the space
	 * 
	 * @param obj The object to be added
	 * @param authority The authority over the object, local or remote control
	 */
	public void add(Object obj, int authority) {
		short objId = nextID++;
		short ownerID = channelID;
		
		try {
			addSharedObject(new SharedObject(obj, objId, ownerID, authority));
		} catch (ClassEncodingException e) {
			throw new RuntimeException("Cannot encode "+obj, e);
		}
		sendMessage(new CreateMessage(obj.getClass().getName(), objId, ownerID), -1);
	}
	
	/**
	 * Add a new shared object to be controlled by this space
	 * 
	 * @param obj The object to be controlled by this space
	 */
	private void addSharedObject(SharedObject obj) {
		shared.add(obj);
		extMap.put(obj.getTarget(), obj);
		idMap.put(obj.getCombinedID(), obj);
		
		for (int i=0;i<listeners.size();i++) {
			listeners.get(i).objectAdded(this, obj.getTarget(), obj.getObjectID(), obj.getOwnerID());
		}
	}
	
	/**
	 * Remove a shared object from this space
	 * 
	 * @param obj The shared object to be removed
	 */
	private void removeSharedObject(SharedObject obj) {
		extMap.remove(obj.getTarget());
		shared.remove(obj);
		idMap.remove(obj.getCombinedID());
		
		for (int i=0;i<listeners.size();i++) {
			listeners.get(i).objectRemoved(this, obj.getTarget(), obj.getObjectID(), obj.getOwnerID());
		}
	}
	
	/**
	 * Remove an object that is being synchronized by this space. This will cause the 
	 * object to be removed from other remote spaces.
	 * 
	 * @param obj The object to be removed
	 */
	public void remove(Object obj) {
		SharedObject s = extMap.get(obj);
		
		if (s.getAuthority() == LOCAL_AUTHORITY) {
			removeSharedObject(s);
			sendMessage(new DestroyMessage(s.getObjectID(), s.getOwnerID()), -1);
		} else {
			throw new RuntimeException("Attempt to remove object without local authority");
		}
	}

	/**
	 * Send a message to all channels connected to this space
	 * 
	 * @param message The message to send
	 */
	public void sendMessage(Message message) {
		sendMessage(message, -1);
	}
	
	/**
	 * Send a message to all connected channels on this space
	 * 
	 * @param message The message to be sent
	 * @param ignoreID The channel ID to ignore to prevent loop backs
	 */
	private void sendMessage(Message message, int ignoreID) {
		for (int i=0;i<channels.size();i++) {
				if (channels.get(i).getChannelID() != ignoreID) {
				try {
					channels.get(i).write(message, true);
				} catch (IOException e) {
					Log.error(e);
				}
			}
		}
	}
	
	/**
	 * Update the space. This will cause updates to be applied 
	 * and if appropriate to be sent out
	 * 
	 * @param delta The amount of time thats passed since last update (ms)
	 */
	public void update(int delta) {
		updateTimer -= delta;
		if (updateTimer < 0) {
			updateTimer = updateInterval;
			sendUpdates();
		}
		
		for (int i=0;i<channels.size();i++) {
			try {
				Message message = channels.get(i).read();
				if (message != null) {
					switch (message.getID()) {
					case CreateMessage.ID:
						create((CreateMessage) message);
						break;
					case DestroyMessage.ID:
						destroy((DestroyMessage) message);
						break;
					case UpdateMessage.ID:
						// do nothing, the logic is contained
						// within the update message
						break;
					default:
						for (int j=0;j<listeners.size();j++) {
							listeners.get(j).customMessageRecieved(channels.get(i), message);
						}
						break;
					}
				}
			} catch (IOException e) {
				Log.error(e);
				
				try {
					channels.get(i).close();
				} catch (IOException e1) {
					Log.error(e1);
				}
				
				MessageChannel channel = channels.remove(i);
				i--;
				
				for (int j=0;j<listeners.size();j++) {
					listeners.get(j).channelDisconnected(channel);
				}
			}
		}
	}
	
	/**
	 * Create a new object based on a recieved message
	 * 
	 * @param message The message describing the object to be created
	 * @throws IOException Indicates a failure to create the object
	 */
	private void create(CreateMessage message) throws IOException {
		try {
			Class clazz = Class.forName(message.getClassName());
			Constructor con = clazz.getDeclaredConstructor(new Class[0]);
			con.setAccessible(true);
			Object obj = con.newInstance(new Object[0]);
			
			try { 
				addSharedObject(new SharedObject(obj, message.getObjectID(), 
													  message.getOwnerID(), REMOTE_AUTHORITY));
			} catch (ClassEncodingException e) {
				throw new RuntimeException("Cannot encode "+obj, e);
			}
			
			if (broadcast) {
				short objId = message.getObjectID();
				short ownerID = message.getOwnerID();
				
				sendMessage(new CreateMessage(obj.getClass().getName(), objId, ownerID), ownerID);
			}
		} catch (InstantiationException e) {
			throw new IOException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new IOException(e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new IOException(e.getMessage());
		} catch (SecurityException e) {
			throw new IOException(e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new IOException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new IOException(e.getMessage());
		} catch (InvocationTargetException e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Destroy an object based on a recieved message
	 * 
	 * @param message The message describing the object to be destroyed
	 * @throws IOException Indicates a failure to destroy the object
	 */
	private void destroy(DestroyMessage message) {
		int combined = getCombinedID(message.getObjectID(), message.getOwnerID());
		SharedObject shared = idMap.get(combined);
		removeSharedObject(shared);
		
		if (broadcast) {
			short objId = message.getObjectID();
			short ownerID = message.getOwnerID();

			sendMessage(new DestroyMessage(shared.getObjectID(), shared.getOwnerID()), shared.getOwnerID());
		}
	}
	
	/**
	 * Send the state of this space to the specifeid channel. 
	 * 
	 * @param channel The channel to which the data should be sent
	 * @throws IOException Indicates a failure to send data
	 */
	private void sendState(MessageChannel channel) throws IOException {
		for (int i=0;i<shared.size();i++) {
			SharedObject obj = shared.get(i);
			short objId = obj.getObjectID();
			short ownerID = obj.getOwnerID();

			channel.write(new CreateMessage(obj.getTarget().getClass().getName(), objId, ownerID), true);
		}
	}
	
	/**
	 * Get a combined ID
	 * 
	 * @param objectID The ID of the object
	 * @param ownerID The ID of the owner
	 * @return The IDs combined
	 */
	private int getCombinedID(short objectID, short ownerID) {
		return (ownerID << 16) + objectID;
	}
	
	/**
	 * Send an update describin the state of locally controlled objects to 
	 * all connected clients. On the server all objects are considered to 
	 * be locally controlled.
	 */
	private void sendUpdates() {
		UpdateMessage update = new UpdateMessage();
		
		for (int i=0;i<shared.size();i++) {
			if ((broadcast) || (shared.get(i).getAuthority() == LOCAL_AUTHORITY)) {
				update.add(shared.get(i));
			}
		}
		
		for (int i=0;i<channels.size();i++) {
			try {
				channels.get(i).write(update, true);
			} catch (IOException e) {
				Log.error(e);
			}
		}
	}
}
