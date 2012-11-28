package org.newdawn.slick.thingle.internal.slick;

import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.thingle.spi.ThingleImage;
import org.newdawn.slick.thingle.spi.ThingleImageBuffer;

/**
 * A wrapped round Slick image buffers to support the graident creation
 * for Thinlet
 * 
 * @author kevin
 */
public class ImageBufferWrapper implements ThingleImageBuffer {
	/** The buffer wrapped */
	private ImageBuffer buffer;
	
	/**
	 * Create a new wrapper round a slick image buffer
	 * 
	 * @param buffer The buffer to be wrapped
	 */
	public ImageBufferWrapper(ImageBuffer buffer) {
		this.buffer = buffer;
	}
	
	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleImageBuffer#getImage()
	 */
	public ThingleImage getImage() {
		return new ImageWrapper(buffer.getImage());
	}

	/**
	 * @see org.newdawn.slick.thingle.spi.ThingleImageBuffer#setRGBA(int, int, int, int, int, int)
	 */
	public void setRGBA(int x, int y, int r, int g, int b, int a) {
		buffer.setRGBA(x, y, r, g, b, a);
	}
}
