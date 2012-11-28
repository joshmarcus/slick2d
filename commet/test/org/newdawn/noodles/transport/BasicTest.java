package org.newdawn.noodles.transport;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.newdawn.noodles.transport.TransportChannel;
import org.newdawn.noodles.transport.TransportFactory;
import org.newdawn.noodles.transport.TransportServer;

/**
 * Daft initial test to try out transport channels
 * 
 * @author kevin
 */
public class BasicTest {
	/**
	 * The channels that have been registered with the server 
	 */
	private static ArrayList<TransportChannel> channels = new ArrayList<TransportChannel>();
	
	/**
	 * Add a client to the list
	 * 
	 * @param channel The channel connected
	 * @throws IOException Indicates a failure to configure the channel
	 */
	private static void addClient(TransportChannel channel) throws IOException {
		System.out.println("Connected: "+channel.getRemoteSocketAddress());
		channel.configureBlocking(false);
		channels.add(channel);
	}
	
	/**
	 * Print out a byte array
	 * 
	 * @param data The byte array to display
	 */
	private static void print(byte[] data) {
		System.out.print("[");
		for (int i=0;i<data.length;i++) {
			System.out.print(data[i]+",");
		}
		System.out.println("]");
	}
	
	/**
	 * Check all the clients that have connected to the server for new data
	 */
	private static void pollClients() {
		for (int i=0;i<channels.size();i++) {
			TransportChannel channel = (TransportChannel) channels.get(i);
			ByteBuffer data = ByteBuffer.allocate(4096);
			try {
				int total = channel.read(data);
				if (total != 0) {
					for (int j=0;j<channels.size();j++) {
						TransportChannel send = (TransportChannel) channels.get(j);
						if (send != channel) {
							data.flip();
							send.write(data, true);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Entry point to the test
	 * 
	 * @param argv The arguments passed in to the test
	 * @throws IOException Indicates a failure to create clients or servers
	 */ 
	public static void main(String[] argv) throws IOException {
		TransportFactory.configureMode(TransportFactory.TCP);
		final TransportServer server = TransportFactory.createServer(10200);
		
		// sender
		Thread t = new Thread() {
			public void run() {
				ByteBuffer buffer = ByteBuffer.allocate(4096);
				String[] lines = {"hello", "super", "duper", "groovy", "networking", "library"};
				try { Thread.sleep(2000); } catch (Exception e) {};
				System.out.println("Sender connecting");
				try {
					TransportChannel client = TransportFactory.createChannel("localhost", 10200);
					client.configureBlocking(false);
					
					for (int i=0;i<lines.length;i++) {
						System.out.println("Writing: "+lines[i]);
						client.write(ByteBuffer.wrap(lines[i].getBytes()), true);
						
						buffer.clear();
						client.read(buffer);
					}				
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
		
		// reciever
		Thread t2 = new Thread() {
			public void run() {
				try { Thread.sleep(1000); } catch (Exception e) {};
				System.out.println("Reciever connecting");
				try {
					TransportChannel client = TransportFactory.createChannel("localhost", 10200);
					client.configureBlocking(false);
					try { Thread.sleep(2000); } catch (Exception e) {};
					
					while (true) {
						try { Thread.sleep(100); } catch (Exception e) {};
						ByteBuffer buffer = ByteBuffer.allocate(4096);
						int total = client.read(buffer);
						buffer.flip();
						
						if (total > 0) {
							byte[] temp = new byte[buffer.remaining()];
							buffer.get(temp, 0, buffer.remaining());
							//print(temp);
							System.out.println(new String(temp));
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		};
		t2.start();
		
		try {
			while (true) {
				TransportChannel client = server.accept();
				if (client != null) {
					addClient(client);
				}
				
				pollClients();
				
				try { Thread.sleep(10); } catch (Exception e) {};
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
