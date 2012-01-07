package org.newdawn.warpstone.crush;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Crush {

	private static void crush(String in, String out, int inWidth, int inHeight, int outWidth, int outHeight) throws IOException {
		BufferedImage input = ImageIO.read(new File(in));
		int tilesWidth = input.getWidth() / inWidth;
		int tilesHeight = input.getHeight() / inHeight;
		
		BufferedImage output = new BufferedImage(tilesWidth * outWidth, tilesHeight * outHeight, BufferedImage.TYPE_INT_ARGB);
		int xoffset = (inWidth - outWidth) / 2;
		int yoffset = (inHeight - outHeight) / 2;
		for (int x=0;x<tilesWidth;x++) {
			for (int y=0;y<tilesHeight;y++) {
				int xp = (x*inWidth)+xoffset;
				int yp = (y*inHeight)+yoffset;
				
				BufferedImage tile = input.getSubimage(xp, yp, outWidth, outHeight);
				output.getGraphics().drawImage(tile, x*outWidth, y*outHeight, null);
			}
		}
		System.out.println("Writing: "+out);
		ImageIO.write(output, "PNG", new File(out));
	}
	
	public static void main(String[] argv) throws IOException {
		crush("originals/peasant.png", "res/peasant.png", 73, 73, 50, 50);
		crush("originals/sword.png", "res/sword.png", 73, 73, 70, 70);
		crush("originals/skel.png", "res/skel.png", 57, 57, 55, 55);
		crush("originals/mage.png", "res/mage.png", 73, 73, 50, 73);
		crush("originals/archer.png", "res/archer.png", 73, 73, 50, 50);
	}
}
