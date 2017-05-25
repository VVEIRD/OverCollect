package de.rcblum.overcollect.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {
	
	
	public final static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static SimpleDateFormat SDF_FILE = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	
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

	public static void debug(Class<?> clazz, String string) {
		System.out.println(Helper.SDF.format(new Date(System.currentTimeMillis())) + "\tDEBUG\t" + clazz.getName() + "\t" + string);
	}

	public static void info(Class clazz, Object string) {
		System.out.println(Helper.SDF.format(new Date(System.currentTimeMillis())) + "\tINFO\t" + clazz.getName() + "\t" + string.toString());
	}

	public static void info(Class clazz, int intVal) {
		System.out.println(Helper.SDF.format(new Date(System.currentTimeMillis())) + "\tINFO\t" + clazz.getName() + "\t" + intVal);
	}

	public static boolean isInteger(String val) {
		return val != null && val.matches("^-?\\d+$");
	}

	public static int toInteger(String value, int defaultValue) {
		return isInteger(value) ? Integer.valueOf(value) : defaultValue;
	}
}
