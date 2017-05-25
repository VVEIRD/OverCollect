package de.rcblum.overcollect.extract.ocr;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.rcblum.overcollect.configuration.Filter;
import de.rcblum.overcollect.configuration.OWItem;
import de.rcblum.overcollect.configuration.OWLib;

public class Glyph {
	/**
	 * Creates a glyph from a filter. Positive pixels are from Black dots,
	 * negatives from all other
	 * 
	 * @param character
	 * @param filter
	 * @return
	 */
	public static Glyph fromFilter(char character, Filter filter, int fontSize) {
		List<int[]> positivePixels = new LinkedList<>();
		List<int[]> negativePixels = new LinkedList<>();
		for (int i = 0; i < filter.points.length; i++) {
			int[] pixel = filter.points[i];
			if (pixel[2] / 255.0 < 0.06d && pixel[3] / 255.0 < 0.06d && pixel[3] / 255.0 < 0.06d)
				positivePixels.add(new int[] { pixel[0], pixel[1] });
			else
				negativePixels.add(new int[] { pixel[0], pixel[1] });
		}

		return new Glyph(character, fontSize,
				positivePixels.size() == 0 ? null : positivePixels.toArray(new int[][] { {} }),
				negativePixels.size() == 0 ? null : negativePixels.toArray(new int[][] { {} }));
	}

	public static void main(String[] args) throws IOException {
		List<OWItem> items = OWLib.getInstance().getItems("ocr_primary_font");
		for (OWItem owItem : items) {
			if (owItem.hasFilter()) {
				Glyph g = fromFilter(owItem.getItemName().charAt(0) == '_' ? ':' : owItem.getItemName().charAt(0),
						owItem.getFilter(), 55);
				owItem.saveGlyph(g);
			}
		}
		items = OWLib.getInstance().getItems("ocr_secondary_font");
		for (OWItem owItem : items) {
			if (owItem.hasFilter()) {
				Glyph g = fromFilter(owItem.getItemName().charAt(0), owItem.getFilter(), 57);
				owItem.saveGlyph(g);
			}
		}
		testGlyph("ocr_primary_font", "1");
		testGlyph("ocr_primary_font", "2");
		testGlyph("ocr_primary_font", "3");
		testGlyph("ocr_primary_font", "4");
		testGlyph("ocr_primary_font", "5");
		testGlyph("ocr_primary_font", "6");
		testGlyph("ocr_primary_font", "7");
		testGlyph("ocr_primary_font", "8");
		testGlyph("ocr_primary_font", "9");

		testGlyph("ocr_secondary_font", "1");
		testGlyph("ocr_secondary_font", "2");
		testGlyph("ocr_secondary_font", "3");
		testGlyph("ocr_secondary_font", "4");
		testGlyph("ocr_secondary_font", "5");
		testGlyph("ocr_secondary_font", "6");
		testGlyph("ocr_secondary_font", "7");
		testGlyph("ocr_secondary_font", "8");
		testGlyph("ocr_secondary_font", "9");

		System.exit(0);
	}

	public static void save(String libPath, String resolution, String alias, Glyph glyph) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Path glyphFile = Paths.get(libPath, resolution, alias, "glyph.json");
		System.out.println(glyphFile.toString());
		String text = gson.toJson(glyph);
		Files.write(glyphFile, text.getBytes("UTF-8"));
	}
	private static void testGlyph(String category, String itemId) {
		System.out.println("Category: " + category + ", glyph: " + itemId);
		Glyph g2 = OWLib.getInstance().getItem(category, itemId).getGlyph();
		BufferedImage b = OWLib.getInstance().getItem(category, itemId).getTemplate();
		System.out.println("    Matched: " + g2.match(b, Color.BLACK, 0.06f) + ", Percentage: "
				+ Math.round(g2.matchPercentage(b, Color.BLACK, 0.06f) * 100));
		System.out.println();
	}

	private int baseFontSize = 55;

	private char character = '~';

	private int[][] positivePixels = null;

	private int[][] negativePixels = null;

	public Glyph(char character, int fontSize, int[][] positivePixels) {
		this(character, fontSize, positivePixels, null);
	}

	public Glyph(char character, int fontSize, int[][] positivePixels, int[][] negativePixels) {
		this.character = character;
		this.positivePixels = Objects.requireNonNull(positivePixels);
		this.negativePixels = negativePixels;
		this.baseFontSize = fontSize;
	}

	public int getBaseFontSize() {
		return baseFontSize;
	}

	public char getChar() {
		// TODO Auto-generated method stub
		return this.character;
	}

	public boolean match(BufferedImage image, Color primary, float tolerance) {
		// image = image.
		int rPrim = primary.getRed();
		int gPrim = primary.getGreen();
		int bPrim = primary.getBlue();
		// check positives
		for (int i = 0; i < positivePixels.length; i++) {
			int x = Math.round(positivePixels[i][0]);
			int y = Math.round(positivePixels[i][1]);
			if (image.getWidth() <= x || image.getHeight() <= y)
				return false;
			int argb = image.getRGB(x, y);
			int r = (argb >> 16) & 0xFF;
			int g = (argb >> 8) & 0xFF;
			int b = (argb >> 0) & 0xFF;
			if (!(Math.abs((rPrim - r) / 255.0f) <= tolerance && Math.abs((gPrim - g) / 255.0f) <= tolerance
					&& Math.abs((bPrim - b) / 255.0f) <= tolerance))
				return false;
		}
		if (this.negativePixels != null) {
			// check for negatives
			for (int i = 0; i < negativePixels.length; i++) {
				int x = Math.round(negativePixels[i][0]);
				int y = Math.round(negativePixels[i][1]);
				if (image.getWidth() <= x || image.getHeight() <= y)
					return false;
				int argb = image.getRGB(x, y);
				int r = (argb >> 16) & 0xFF;
				int g = (argb >> 8) & 0xFF;
				int b = (argb >> 0) & 0xFF;
				if ((Math.abs((rPrim - r) / 255.0f) <= tolerance && Math.abs((gPrim - g) / 255.0f) <= tolerance
						&& Math.abs((bPrim - b) / 255.0f) <= tolerance))
					return false;
			}
		}
		return true;
	}

	public float matchPercentage(BufferedImage image, Color primary, float tolerance) {
		float totalPoints = this.positivePixels.length + (this.negativePixels != null ? this.negativePixels.length : 0);
		int ok = 0;
		// image = image.
		int rPrim = primary.getRed();
		int gPrim = primary.getGreen();
		int bPrim = primary.getBlue();
		// check positives
		for (int i = 0; i < positivePixels.length; i++) {
			int x = Math.round(positivePixels[i][0]);
			int y = Math.round(positivePixels[i][1]);
			if (image.getWidth() <= x || image.getHeight() <= y) {
				continue;
			}
			int argb = image.getRGB(x, y);
			int r = (argb >> 16) & 0xFF;
			int g = (argb >> 8) & 0xFF;
			int b = (argb >> 0) & 0xFF;
			if (!(Math.abs((rPrim - r) / 255.0f) <= tolerance && Math.abs((gPrim - g) / 255.0f) <= tolerance
					&& Math.abs((bPrim - b) / 255.0f) <= tolerance))
				continue;
			ok++;
		}
		if (this.negativePixels != null) {
			// check for negatives
			for (int i = 0; i < negativePixels.length; i++) {
				int x = Math.round(negativePixels[i][0]);
				int y = Math.round(negativePixels[i][1]);
				if (image.getWidth() <= x || image.getHeight() <= y) {
					//System.out.println("Outofbounds pixel");
					continue;
				}
				int argb = image.getRGB(x, y);
				int r = (argb >> 16) & 0xFF;
				int g = (argb >> 8) & 0xFF;
				int b = (argb >> 0) & 0xFF;
				if ((Math.abs((rPrim - r) / 255.0f) <= tolerance && Math.abs((gPrim - g) / 255.0f) <= tolerance
						&& Math.abs((bPrim - b) / 255.0f) <= tolerance)) {
					//System.out.println("Negative misses " + x + ", " + y);
					continue;
				}
				ok++;
			}
		}
		return ok / totalPoints;
	}
}
