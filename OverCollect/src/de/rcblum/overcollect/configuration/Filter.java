package de.rcblum.overcollect.configuration;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.rcblum.overcollect.utils.Helper;

/**
 * Each Screenshot must pass one of the filters to may be used.
 * 
 * @author rcBlum
 *
 */
public class Filter {
	public static void save(String libPath, String resolution, String alias, Filter filter) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Path filterFile = Paths.get(libPath, resolution, alias, "filter.json");
		Helper.info(Filter.class, filterFile.toString());
		String text = gson.toJson(filter);
		Files.write(filterFile, text.getBytes("UTF-8"));
	}

	/**
	 * each point consists of 5 values: x, y, r, g, b
	 */
	public int[][] points;

	/**
	 * Tolerance 0-100 in how much % the compared pixel can deviate from the set
	 * color.
	 */
	public int tolerance;

	public Filter(int[][] points, int tolerance) {
		super();
		for (int[] point : points) {
			if (point.length < 5)
				throw new IllegalArgumentException("Each point array must have 5 entries 0=x, 1=y, 2=r, 3=g, 4=b");
		}
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
			// Helper.info(this.getClass(), "R diff: " +
			// Math.round(100*(Math.abs(points[cpoint][2] -
			// c.getRed())/255.0)));
			// Helper.info(this.getClass(), "B diff: " +
			// Math.round(100*(Math.abs(points[cpoint][3] -
			// c.getGreen())/255.0)));
			// Helper.info(this.getClass(), "G diff: " +
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

	public Filter rescale(float rescale) {
		int[][] rescaledPoinst = new int[this.points.length][5];
		for (int i = 0; i < points.length; i++) {
			int[] point = points[i];
			rescaledPoinst[i][0] = Math.round(point[0] * rescale);
			rescaledPoinst[i][1] = Math.round(point[1] * rescale);
			rescaledPoinst[i][2] = point[2];
			rescaledPoinst[i][3] = point[3];
			rescaledPoinst[i][4] = point[4];
		}
		return new Filter(rescaledPoinst, this.tolerance);
	}

	public String toJson() {
		Gson g = new Gson();
		return g.toJson(this);
	}
}
