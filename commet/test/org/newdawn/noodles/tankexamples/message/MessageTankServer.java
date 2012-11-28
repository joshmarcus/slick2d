package org.newdawn.noodles.tankexamples.message;

import java.io.IOException;

import org.newdawn.noodles.message.MessageBroadcastServer;
import org.newdawn.noodles.message.MessageChannel;
import org.newdawn.noodles.tankexamples.lowlevel.LowLevelTankServer;
import org.newdawn.noodles.util.Log;

/**
 * A tank game server implemented using message passing
 * 
 * @author kevin
 */
public class MessageTankServer extends MessageBroadcastServer {

	/**
	 * Create a new tank server
	 * 
	 * @param port The port on which the server should run
	 * @throws IOException Indicates a failure to start the server
	 */
	public MessageTankServer(int port) throws IOException {
		super(new TankMessageFactory(), port);
	}

	/**
	 * @see org.newdawn.noodles.message.MessageBroadcastServer#channelDisconnected(org.newdawn.noodles.message.MessageChannel)
	 */
	@Override
	protected void channelDisconnected(MessageChannel channel) {
		super.channelDisconnected(channel);
		
		try {
			sendToAll(new TankRemoveMessage(channel.getChannelID()), -1);
		} catch (IOException e) {
			Log.error(e);
		}
	}

	/**
	 * Entry point to the low level tank example
	 * 
	 * @param argv The arguments passed into the example tank server
	 */
	public static void main(String argv[]) {
		// create the server and start it
		try {
			MessageTankServer server = new MessageTankServer(12345);
			server.start();
		} catch (IOException e) {
			Log.error(e);
		}
	}
}
