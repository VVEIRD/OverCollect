package de.rcblum.overcollect.ui.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import de.rcblum.overcollect.configuration.OWLib;
import de.rcblum.overcollect.ui.panels.JMapPanel;

public class ImageCache 
{
	
	private static Map<String, BufferedImage> images = new HashMap<>();

	public static BufferedImage getImage(URL url) 
	{
		if (images.containsKey(url.toString()))
			return images.get(url.toString());
		BufferedImage image = null;
		String filename = url.toString().substring(url.toString().lastIndexOf('/')+1);
		Path cacheRoot = OWLib.getInstance().getTempPath().resolve("cache");
		Path cachFile = OWLib.getInstance().getTempPath().resolve("cache").resolve(filename);
		
		if (!Files.exists(cacheRoot))
			try {
				Files.createDirectories(cacheRoot);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if (Files.exists(cachFile)) {
			try {
				image = ImageIO.read(cachFile.toFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (image == null && url != null) {
			try {
			    image = ImageIO.read(url);
			    if (image != null)
			    	ImageIO.write(image, filename.substring(filename.lastIndexOf(".")+1).toUpperCase(), cachFile.toFile());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		images.put(url.toString(), image);
		return image;
	}
	
	public static BufferedImage getImageFromResource(String resourcePath) 
	{
		if (images.containsKey(resourcePath))
			return images.get(resourcePath);
		BufferedImage img = null;
		try {
			InputStream io = JMapPanel.class.getResourceAsStream(resourcePath);
			if (io != null)
				img = ImageIO.read(io);
		} catch (IOException | NullPointerException e) {
		}
		if (img != null)
			images.put(resourcePath, img);
		return img;
	}
}
