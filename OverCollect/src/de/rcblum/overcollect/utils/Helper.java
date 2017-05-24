package de.rcblum.overcollect.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Helper {
	public static boolean isInteger(String val) {
		return val != null && val.matches("^-?\\d+$");
	}

	public static int toInteger(String value, int defaultValue) {
		return isInteger(value) ? Integer.valueOf(value) : defaultValue;
	}

	public static BufferedImage copy(BufferedImage img) {
		try {
			int newW = img.getWidth();
			int newH = img.getHeight();
			Image tmp = img;
			BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = dimg.createGraphics();
			g2d.drawImage(tmp, 0, 0, null);
			g2d.dispose();
			return dimg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
