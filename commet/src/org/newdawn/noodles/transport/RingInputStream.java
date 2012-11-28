package org.newdawn.noodles.transport;

import java.io.IOException;
import java.io.InputStream;

/**
 * An ring buffer based input stream
 *
 * @author kevin
 */
public class RingInputStream extends InputStream {
	/** The maximum size of the ring buffer */
	public static final int MAX = 1024 * 8; // 8k stack
	
	/** The store when read data is held */
	private int[] store = new int[MAX];
	/** The write location */
	private int write;
	/** The read location */
	private int read;
	/** The total number of bytes available to be read */
	private int total;
	
	/**
	 * @see java.io.InputStream#available()
	 */
	public int available() throws IOException {
		return total;
	}

	/**
	 * Add some data into the buffer to be read from the
	 * stream.
	 * 
	 * @param data The data to be added
	 * @param index The index into the data array to add
	 * @param count The number of bytes to add
	 */
	public void add(byte[] data, int index, int count) {
		for (int i=0;i<count;i++) {
			store[write] = data[index+i];
			if (store[write] < 0) {
				store[write] = 256 + store[write];
			}
			write++;
			if (write >= store.length) {
				write = 0;
			}
			total++;
		}
	}

	/**
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		int value = store[read];
		read++;
		if (read >= store.length) {
			read = 0;
		}
		total--;
		
		return value;
	}
}
