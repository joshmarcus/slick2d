package org.newdawn.noodles.chat;

import java.io.IOException;

import org.newdawn.noodles.BroadcastServer;

/**
 * Simple chat server. It's just a broadcast server - nothing else.
 * 
 * @author kevin
 */
public class ChatServer extends BroadcastServer {
	/**
	 * Create a new server
	 * 
	 * @param port The port on which the server should be run
	 * @throws IOException Indicates a failure to start the server
	 */
	public ChatServer(int port) throws IOException {
		super(port);
	}

	/**
	 * Entry point into the chat server
	 * 
	 * @param argv The arguments to the server
	 * @throws IOException Indicates a failure to start the server
	 */
	public static void main(String[] argv) throws IOException {
		ChatServer server = new ChatServer(12345);
		server.start();
	}
}
