package de.rcblum.overcollect.configuration;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Each Screenshot must pass one of the filters to may be used.
 * 
 * @author rcBlum
 *
 */
public class Filter {
	/**
	 * each point consists of 5 values: x, y, r, g, b
	 */
	public int[][] points;

	/**
	 * tolerance 0-100 in how much % the compared pixel can deviate from the set
	 * color.
	 */
	public int tolerance;

	public Filter(int[][] points, int tolerance) {
		super();
		this.points = points;
		this.tolerance = tolerance;
	}

	public boolean match(BufferedImage i) {
		boolean matches = true;
		double toleranceVal = (100.0 / 5) * this.tolerance;
		for (int cpoint = 0; cpoint < points.length; cpoint++) {
			if (points[cpoint][0] >= i.getWidth() || points[cpoint][1] >= i.getHeight()) {
				matches = false;
				break;
			}
			Color c = new Color(i.getRGB(points[cpoint][0], points[cpoint][1]), true);
			// Test R G B
			// System.out.println("R diff: " +
			// Math.round(100*(Math.abs(points[cpoint][2] -
			// c.getRed())/255.0)));
			// System.out.println("B diff: " +
			// Math.round(100*(Math.abs(points[cpoint][3] -
			// c.getGreen())/255.0)));
			// System.out.println("G diff: " +
			// Math.round(100*(Math.abs(points[cpoint][4] -
			// c.getBlue())/255.0)));
			matches = matches && Math.abs(points[cpoint][2] - c.getRed()) <= toleranceVal
					&& Math.abs(points[cpoint][3] - c.getGreen()) <= toleranceVal
					&& Math.abs(points[cpoint][4] - c.getBlue()) <= toleranceVal;
			if (!matches)
				break;
		}

		return matches;
	}

	public static void save(String libPath, String resolution, String alias, Filter filter) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Path filterFile = Paths.get(libPath, resolution, alias, "filter.json");
		System.out.println(filterFile.toString());
		String text = gson.toJson(filter);
		Files.write(filterFile, text.getBytes("UTF-8"));
	}

	public String toJson() {
		Gson g = new Gson();
		return g.toJson(this);
	}
}
