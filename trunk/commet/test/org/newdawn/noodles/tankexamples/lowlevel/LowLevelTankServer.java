package org.newdawn.noodles.tankexamples.lowlevel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.newdawn.noodles.BroadcastServer;
import org.newdawn.noodles.transport.TransportChannel;
import org.newdawn.noodles.transport.TransportFactory;
import org.newdawn.noodles.transport.TransportServer;
import org.newdawn.noodles.util.Log;

/**
 * The server of the low level example of the tank game. The server is essentially
 * just broadcasting what it's been told. Logic could be worked in during the recieve
 * to validate and control the game. Trying to keep it simple here though
 * 
 * @author kevin
 */
public class LowLevelTankServer extends BroadcastServer {
	/**
	 * Create a simple tank tank server
	 * 
	 * @param port The port on which to run the server
	 * @throws IOException Indicates a failure to create the server
	 */
	private LowLevelTankServer(int port) throws IOException {
		super(port);
		
		Log.info("********************************************");
		Log.info("* Low Level Tank Example Server            *");
		Log.info("********************************************");
	}
	
	/**
	 * @see org.newdawn.noodles.BroadcastServer#channelDisconnected(org.newdawn.noodles.transport.TransportChannel)
	 */
	@Override
	protected void channelDisconnected(TransportChannel channel) {
		super.channelDisconnected(channel);
		
		System.out.println("Channel" + channel.getChannelID() + " closed. Sending tank remove");
		// send a remove tank to all connected parties
		buffer.clear();
		buffer.putShort((short) channel.getChannelID());
		buffer.putFloat(-1);
		buffer.putFloat(-1);
		buffer.putFloat(-1);
		buffer.putFloat(-1);
		// -1 to incidate we'll include everyone
		sendToAll(buffer, -1);
	}

	/**
	 * Entry point to the low level tank example
	 * 
	 * @param argv The arguments passed into the example tank server
	 */
	public static void main(String argv[]) {
		// create the server and start it
		try {
			LowLevelTankServer server = new LowLevelTankServer(12345);
			server.start();
		} catch (IOException e) {
			Log.error(e);
		}
	}
}
