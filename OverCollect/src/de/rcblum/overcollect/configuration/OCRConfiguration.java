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
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import de.rcblum.overcollect.utils.Helper;

public class OCRConfiguration {

	public static void save(String libPath, String resolution, String alias, OCRConfiguration ocr)
			throws UnsupportedEncodingException, IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Path ocrFile = Paths.get(libPath, resolution, alias, "ocr_fields.json");
		Helper.info(OCRConfiguration.class, ocrFile.toString());
		String text = gson.toJson(ocr);
		Files.write(ocrFile, text.getBytes("UTF-8"));
	}

	public final boolean doRecolor;

	/**
	 * Fontsize in pixel of the primary values
	 */
	public final int fontSize;
	
	/**
	 * Amount of pixels that is needed to recognize a character on screen.
	 * Fix for Master level player. Only used on maps
	 */
	public final int pixelDetectionCount;

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
			int[] secondaryDataFieldSize, int fontSize, int secondaryFontSize, double skew, double skewSecondary,
			int skewTrim, int skewSecondaryTrim, boolean doRecolor, int[] dataColor, int[] secondaryDataColor, int pixelDetectionCount) {
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
		this.pixelDetectionCount = pixelDetectionCount;
	}

	/**
	 * Returns the color of the primary font, if set. if not set, it returns
	 * black if doRecolor is set, else white.
	 * 
	 * @return
	 */
	public Color getDataColor() {
		if (dataColor != null && dataColor.length == 3)
			return new Color(dataColor[0], dataColor[1], dataColor[2]);
		else
			return doRecolor ? Color.BLACK : Color.WHITE;
	}

	public Color getSecondaryDataColor() {
		if (secondaryDataColor != null && secondaryDataColor.length == 3)
			return new Color(secondaryDataColor[0], secondaryDataColor[1], secondaryDataColor[2]);
		else
			return doRecolor ? Color.BLACK : Color.WHITE;
	}

	public OCRConfiguration rescale(float rescale, boolean isMap) {
		int skewTrim = Math.round(this.skewTrim * rescale);
		int skewSecondaryTrim = Math.round(this.skewSecondaryTrim * rescale);
		int fontSize = Math.round(this.fontSize * rescale);
		int secondaryFontSize = Math.round(this.secondaryFontSize * rescale);

		Map<String, int[]> values = new HashMap<>();
		Map<String, int[]> secondaryValues = new HashMap<>();
		int[] secondaryDataFieldSize = new int[this.secondaryDataFieldSize.length];
		int[] dataFieldSize = new int[this.dataFieldSize.length];
		for (int i = 0; i < this.dataFieldSize.length; i++) {
			dataFieldSize[i] = Math.round(this.dataFieldSize[i] * rescale);
		}
		for (int i = 0; i < this.secondaryDataFieldSize.length; i++) {
			secondaryDataFieldSize[i] = Math.round(this.secondaryDataFieldSize[i] * rescale);
		}
		Set<String> valuesKeys = this.values.keySet();
		for (String key : valuesKeys) {
			int[] xy = this.values.get(key);
			int[] nxy = new int[xy.length];
			for (int i = 0; i < xy.length; i++) {
				nxy[i] = Math.round(xy[i] * rescale);
			}
			values.put(key, nxy);
		}
		valuesKeys = this.secondaryValues.keySet();
		for (String key : valuesKeys) {
			int[] xy = this.secondaryValues.get(key);
			int[] nxy = new int[xy.length];
			for (int i = 0; i < xy.length; i++) {
				nxy[i] = Math.round(xy[i] * rescale);
			}
			secondaryValues.put(key, nxy);
		}
		return new OCRConfiguration(values, secondaryValues, dataFieldSize, secondaryDataFieldSize, fontSize,
				secondaryFontSize, skew, skewSecondary, skewTrim, skewSecondaryTrim, doRecolor,
				dataColor, secondaryDataColor, this.pixelDetectionCount);
	}

	public String toJson() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(this);
	}
}
