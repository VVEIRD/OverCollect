package de.rcblum.overcollect.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import de.rcblum.overcollect.configuration.Filter;
import de.rcblum.overcollect.configuration.OWItem;
import de.rcblum.overcollect.configuration.OWLib;

public class FontUtils {

	private static void and(BufferedImage destImage, BufferedImage sourceImage, Color newColor,
			boolean reddenOutOfBoundAreas) {
		if (reddenOutOfBoundAreas) {
			if (sourceImage.getWidth() != destImage.getWidth()) {
				int widthEnd = Math.max(destImage.getWidth(), sourceImage.getWidth());
				int widthStart = Math.min(destImage.getWidth(), sourceImage.getWidth());
				if (widthEnd <= destImage.getWidth()) {
					for (int x = widthStart; x < widthEnd; x++) {
						for (int y = 0; y < destImage.getHeight(); y++) {
							destImage.setRGB(x, y, Color.RED.getRGB());
						}
					}
				}
			}
			if (sourceImage.getHeight() != destImage.getHeight()) {
				int heightEnd = Math.max(destImage.getHeight(), sourceImage.getHeight());
				int heightStart = Math.min(destImage.getHeight(), sourceImage.getHeight());
				if (heightEnd <= destImage.getHeight()) {
					for (int y = heightStart; y < heightEnd; y++) {
						for (int x = 0; x < destImage.getWidth(); x++) {
							destImage.setRGB(x, y, Color.RED.getRGB());
						}
					}
				}
			}
		}
		int width = Math.min(destImage.getWidth(), sourceImage.getWidth());
		int height = Math.min(destImage.getHeight(), sourceImage.getHeight());
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (sourceImage.getRGB(x, y) == destImage.getRGB(x, y))
					destImage.setRGB(x, y, newColor.getRGB());
			}
		}
	}

	public static void createFilter(Path imageRoot, boolean isPrimary) {
		try {
			List<Path> imagesPath = Files.list(imageRoot).filter(p -> Files.isRegularFile(p))
					.collect(Collectors.toList());
			Map<String, LinkedList<BufferedImage>> inputImages = new HashMap<>();
			for (Path path : imagesPath) {
				BufferedImage b = ImageIO.read(path.toFile());
				String n = "" + path.getFileName().toString().charAt(0);
				if (inputImages.get(n) == null)
					inputImages.put(n, new LinkedList<>());
				inputImages.get(n).add(b);
				System.out.println(n + ": " + path.getFileName());
			}
			List<int[]> filterPoints = new LinkedList<>();
			Set<String> glyphs = inputImages.keySet();
			for (String glyph : glyphs) {
				OWItem item = OWLib.getInstance().getItem(isPrimary ? "ocr_primary_font_2" : "ocr_secondary_font",
						glyph);
				for (BufferedImage image : inputImages.get(glyph)) {
					int[] filterPointBlack = findPoint(image, Color.BLACK);
					int[] filterPointWhite = findPoint(image, Color.WHITE);
					if (filterPointBlack != null)
						filterPoints.add(filterPointBlack);
					if (filterPointWhite != null)
						filterPoints.add(filterPointWhite);
				}
				if (filterPoints.size() > 5) {
					Filter f = new Filter(filterPoints.toArray(new int[0][0]),
							item.hasFilter() ? item.getFilter().tolerance : 5);
					item.saveFilter(f);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static int[] findPoint(BufferedImage image, Color seekedColor) {
		int rectSize = 7;
		int center = 3;
		long imageWidth = image.getWidth();
		long imageHeight = image.getHeight();
		while (rectSize > 1) {
			for (int x = 0; x < imageWidth; x++) {
				for (int y = 0; y < imageHeight; y++) {
					if (x + rectSize < imageWidth && y + rectSize < imageHeight) {
						boolean abort = false;
						for (int yRect = y; yRect < y + rectSize; yRect++) {
							if (abort)
								break;
						}
						for (int xRect = x; xRect < x + rectSize; xRect++) {
							if (image.getRGB(xRect, y + center) != seekedColor.getRGB()) {
								abort = true;
								break;
							}
						}
						if (abort)
							continue;
						return new int[] { x + center, y + center, seekedColor.getRed(), seekedColor.getGreen(),
								seekedColor.getBlue() };
					}
				}
			}
			rectSize = rectSize - 2;
			center = center - 1;
		}
		return null;
	}

	public static void main(String[] args) {
		mergeSecondaryFontTestFiles();
		mergePrimaryFontTestFiles();
	}

	public static void mergePrimaryFontTestFiles() {
		File[] fs = Paths.get("lib", "samples", "primary_font_compiled").toFile().listFiles();
		for (File file : fs) {
			file.delete();
		}
		mergeTestFiles(Paths.get("lib", "samples", "primary_font_source"),
				Paths.get("lib", "samples", "primary_font_compiled"), true);
		createFilter(Paths.get("lib", "samples", "primary_font_compiled"), true);
		fs = Paths.get("lib", "samples", "primary_font_compiled").toFile().listFiles();
		for (File file : fs) {
			file.delete();
		}
		mergeTestFiles(Paths.get("lib", "samples", "primary_font_source"),
				Paths.get("lib", "samples", "primary_font_compiled"), false);
		try {
			Stream<Path> files = Files.list(Paths.get("lib", "samples", "primary_font_compiled"));
			files.filter(f -> f.toString().endsWith(".png")).forEach(c -> {
				try {
					BufferedImage source = ImageIO.read(c.toFile());
					BufferedImage target = new BufferedImage(source.getWidth(), 2000, BufferedImage.TYPE_INT_ARGB);
					Color color = new Color(0, 0, 0, 0);
					Graphics2D g = target.createGraphics();
					g.setColor(color);
					g.fillRect(0, 0, target.getWidth(), target.getHeight());
					g.drawImage(source, 0, 0, null);
					g.dispose();
					ImageIO.write(target,
							"PNG", Paths
									.get("lib", "owdata", "ocr_primary_font_2",
											String.valueOf(c.getFileName().toString().charAt(0)), "template.png")
									.toFile());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fs = Paths.get("lib", "samples", "primary_font_compiled").toFile().listFiles();
		for (File file : fs) {
			file.delete();
		}
		mergeTestFiles(Paths.get("lib", "samples", "primary_font_source"),
				Paths.get("lib", "samples", "primary_font_compiled"), true);
	}

	public static void mergeSecondaryFontTestFiles() {
		mergeTestFiles(Paths.get("lib", "samples", "secondary_font_source"),
				Paths.get("lib", "samples", "secondary_font_compiled"), true);
	}

	public static void mergeTestFiles(Path imageRoot, Path imageDest, boolean filterAgainstOtherGlyphs) {
		if (Files.exists(imageRoot) && Files.isDirectory(imageRoot)) {
			try {
				List<Path> imagesPath = Files.list(imageRoot).filter(p -> Files.isRegularFile(p))
						.collect(Collectors.toList());
				Map<String, LinkedList<BufferedImage>> inputImages = new HashMap<>();
				Map<String, BufferedImage> mergedImages = new HashMap<>();
				for (Path path : imagesPath) {
					BufferedImage b = ImageIO.read(path.toFile());
					String n = "" + path.getFileName().toString().charAt(0);
					if (inputImages.get(n) == null)
						inputImages.put(n, new LinkedList<>());
					inputImages.get(n).add(b);
				}
				Set<String> keys = inputImages.keySet();
				// for (String key : keys) {
				// Queue<BufferedImage> imageQueue = inputImages.get(key);
				// if(mergedImages.get(key) == null)
				// mergedImages.put(key, copy(imageQueue.peek()));
				// for(String key2 : keys) {
				// if (!key.equals(key2)) {
				// List<BufferedImage> b = inputImages.get(key2);
				// for (BufferedImage bufferedImage : b) {
				// xor(mergedImages.get(key), bufferedImage, Color.BLACK);
				// }
				// }
				// }
				// }
				for (String key : keys) {
					List<BufferedImage> imageQueue = inputImages.get(key);
					if (mergedImages.get(key) == null)
						mergedImages.put(key, Helper.copy(imageQueue.get(0)));
					for (BufferedImage img : imageQueue) {
						neq(mergedImages.get(key), img, Color.RED, true);
					}
				}
				if (filterAgainstOtherGlyphs) {
					for (String key : keys) {
						for (String key2 : keys) {
							if (!key.equals(key2)) {
								List<BufferedImage> b = inputImages.get(key2);
								BufferedImage dest = Helper.copy(mergedImages.get(key));
								for (BufferedImage bufferedImage : b) {
									and(dest, bufferedImage, Color.GREEN, false);
								}
								ImageIO.write(dest, "PNG", imageDest.resolve(key + "." + key2 + ".png").toFile());
							}
						}
					}
				}
				keys = mergedImages.keySet();
				if (!filterAgainstOtherGlyphs) {
					for (String key : keys) {
						ImageIO.write(mergedImages.get(key), "PNG", imageDest.resolve(key + ".png").toFile());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void neq(BufferedImage destImage, BufferedImage sourceImage, Color newColor,
			boolean reddenOutOfBoundAreas) {
		if (reddenOutOfBoundAreas) {
			if (sourceImage.getWidth() != destImage.getWidth()) {
				int widthEnd = Math.max(destImage.getWidth(), sourceImage.getWidth());
				int widthStart = Math.min(destImage.getWidth(), sourceImage.getWidth());
				if (widthEnd <= destImage.getWidth()) {
					for (int x = widthStart; x < widthEnd; x++) {
						for (int y = 0; y < destImage.getHeight(); y++) {
							destImage.setRGB(x, y, Color.RED.getRGB());
						}
					}
				}
			}
			if (sourceImage.getHeight() != destImage.getHeight()) {
				int heightEnd = Math.max(destImage.getHeight(), sourceImage.getHeight());
				int heightStart = Math.min(destImage.getHeight(), sourceImage.getHeight());
				if (heightEnd <= destImage.getHeight()) {
					for (int y = heightStart; y < heightEnd; y++) {
						for (int x = 0; x < destImage.getWidth(); x++) {
							destImage.setRGB(x, y, Color.RED.getRGB());
						}
					}
				}
			}
		}
		int width = Math.min(destImage.getWidth(), sourceImage.getWidth());
		int height = Math.min(destImage.getHeight(), sourceImage.getHeight());
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (sourceImage.getRGB(x, y) != destImage.getRGB(x, y))
					destImage.setRGB(x, y, newColor.getRGB());
			}
		}
	}

}
