package de.rcblum.overcollect.configuration;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class OCRConfiguration 
{
	private static transient Map<String, Map<String, OCRConfiguration>> configurations = new HashMap<>();
	
	public final boolean doRecolor;

	/**
	 * Fontsize in pixel of the primary values
	 */
	public final int fontSize;
	
	/**
	 * color of the data
	 */
	public final int[] dataColor;
	
	/**
	 * Size of the data field on screen
	 */
	public final int[] dataFieldSize;
	
	/**
	 * Fontsize in pixel of the secondary values
	 */
	public final int secondaryFontSize;
	
	/**
	 * color of the secondary data
	 */
	public final int[] secondaryDataColor;
	
	/**
	 * Size of the secondary data field on screen
	 */
	public final int[] secondaryDataFieldSize;
	
	/**
	 * Skewing of the primary datafields
	 */
	public final double skew;
	
	public final int skewTrim;

	public final double skewSecondary;

	public final int skewSecondaryTrim;

	/**
	 * Coordinates for the data on screen. "identifier" = [x, y]
	 */
	public final Map<String, int[]> values;
	public final Map<String, int[]> secondaryValues;

	public OCRConfiguration(Map<String, int[]> values, Map<String, int[]> secondaryValues, int[] dataFieldSize,
			int[] secondaryDataFieldSize, int fontSize, int secondaryFontSize, double skew, double skewSecondary, int skewTrim, int skewSecondaryTrim, boolean doRecolor,
			int[] dataColor, int[] secondaryDataColor) {
		super();
		this.dataFieldSize = dataFieldSize;
		this.secondaryDataFieldSize = secondaryDataFieldSize;
		this.fontSize = fontSize;
		this.secondaryFontSize = secondaryFontSize;
		this.values = values;
		this.secondaryValues = secondaryValues;
		this.skewSecondary = skewSecondary;
		this.skew = skew;
		this.skewTrim = skewTrim;
		this.skewSecondaryTrim = skewSecondaryTrim;
		this.doRecolor = doRecolor;
		this.dataColor = dataColor;
		this.secondaryDataColor = secondaryDataColor;
	}

	public static OCRConfiguration getInstance(Dimension dimension, String alias) 
	{
		OCRConfiguration configuration = null;
		String resolution = ((int)dimension.getWidth()) + "x" + ((int)dimension.getHeight());
		Gson gson = new Gson();
		File ocrFile = new File("lib" + File.separator + "owdata" + File.separator + resolution + File.separator + alias + File.separator + "ocr_fields.json");
		System.out.println(ocrFile.getAbsolutePath());
		if (configurations.containsKey(resolution) && configurations.get(resolution).containsKey(alias)) {
			return configurations.get(resolution).get(alias);
		}
		else if (!configurations.containsKey(resolution)) {
			configurations.put(resolution, new HashMap<>());
		}
		if (ocrFile.exists()) {
			try {
				String text = new String(Files.readAllBytes(Paths.get(ocrFile.getAbsolutePath())), StandardCharsets.UTF_8);
				configuration = gson.fromJson(text, OCRConfiguration.class);
			} catch (JsonIOException | JsonSyntaxException | IOException e) {
				e.printStackTrace();
			}
		}
		configurations.get(resolution).put(alias, configuration);
		return configuration;
	}

	public static void save(String libPath, String resolution, String alias, OCRConfiguration ocr) throws UnsupportedEncodingException, IOException 
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Path ocrFile = Paths.get(libPath, resolution, alias, "ocr_fields.json"); 
		System.out.println(ocrFile.toString());
		String text = gson.toJson(ocr);
		Files.write(ocrFile, text.getBytes("UTF-8"));
	}
	
	/**
	 * Returns the color of the primary font, if set. if not set, it returns black if doRecolor is set, else white.
	 * @return
	 */
	public Color getDataColor() 
	{
		if (dataColor != null && dataColor.length == 3)
			return new Color(dataColor[0], dataColor[1], dataColor[2]);
		else 
			return doRecolor ?Color.BLACK : Color.WHITE;
	}
	
	public Color getSecondaryDataColor() 
	{
		if (secondaryDataColor != null && secondaryDataColor.length == 3)
			return new Color(secondaryDataColor[0], secondaryDataColor[1], secondaryDataColor[2]);
		else 
			return doRecolor ?Color.BLACK : Color.WHITE;
	}

	public String toJson() 
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(this);
	}
}
