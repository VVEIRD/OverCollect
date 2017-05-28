package de.rcblum.overcollect.extract.ocr;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ImageGlyphSplitter {
	/**
	 * Splits the Image by the primary color. Truncates all non-primary color
	 * sections. The image is split horizontally and truncated vertically after
	 * the last found primary color. <br>
	 * <br>
	 * <b>Bugfix:</b> Splitting image did not work correctly if there were
	 * artifacts with one pixel width. It would jump to the end of the next
	 * number and include the whole whitespace in between. Fixed by resetting
	 * the start position on the x axis, when no pixel was found and x-start was
	 * set but x-end was not set, meaning there was only an artifact of 1 pixel width.
	 * 
	 * @param biSource
	 *            Image that should be split
	 * @param primary
	 *            Primary Color after which the image should be split
	 * @param tolerance
	 *            Tolerance for deviation to the primary color. Accepted values:
	 *            0.0..1.0. Values that differentiate form the accepted range
	 *            will be reduced to either 0 or 1.
	 * @return Array with the images split by the primary color.
	 */
	public static BufferedImage[] splitImage(BufferedImage biSource, Color primary, float tolerance, int pixelDetectionCount) {
		pixelDetectionCount = pixelDetectionCount == 0 ? 1 : pixelDetectionCount;
		Objects.requireNonNull(biSource);
		Objects.requireNonNull(primary);
		tolerance = tolerance > 1 ? 1 : tolerance < 0 ? 0 : tolerance;
		List<BufferedImage> intermediateResult = new LinkedList<>();
		int glyphLeftBound = -1;
		int glyphRightBound = -1;

		for (int x = 0; x < biSource.getWidth(); x++) {
			int pr = primary.getRed();
			int pg = primary.getGreen();
			int pb = primary.getBlue();
			int cCount=0;
			int hCount=0;
			// Find right and left bounds of the next number
			for (int y = 0; y < biSource.getHeight(); y++) {
				int argb = biSource.getRGB(x, y);
				int r = (argb >> 16) & 0xFF;
				int g = (argb >> 8) & 0xFF;
				int b = (argb >> 0) & 0xFF;
				boolean foundFontColor = Math.abs(r - pr) / 255.0 < tolerance
						&& Math.abs(g - pg) / 255.0 < tolerance && Math.abs(b - pb) / 255.0 < tolerance;
				if (foundFontColor)
					cCount++;
				if (cCount >= pixelDetectionCount && glyphLeftBound < 0) {
					glyphLeftBound = x;
//					glyphRightBound = x;
//					break;
				}
				if (cCount>= pixelDetectionCount)
						glyphRightBound = x;
				// Reset left bound position of the sub-image, if there was only one pixel of primary color 
//				if (!foundFontColor && glyphLeftBound >= 0 && glyphRightBound < 0)
//					glyphLeftBound = -1;
			}
			// Find top and bottom bounds
			if (glyphRightBound >= 0 && glyphRightBound < x && glyphRightBound < biSource.getWidth()) {
				int glyphYStart = -1;
				int glyphYEnd = -1;
				for (int ysub = 0; ysub < biSource.getHeight(); ysub++) {
					for (int xsub = glyphLeftBound; xsub <= glyphRightBound; xsub++) {
						int argb = biSource.getRGB(xsub, ysub);
						int r = (argb >> 16) & 0xFF;
						int g = (argb >> 8) & 0xFF;
						int b = (argb >> 0) & 0xFF;
						boolean foundFontColor = Math.abs(r - pr) / 255.0 < tolerance
								&& Math.abs(g - pg) / 255.0 < tolerance && Math.abs(b - pb) / 255.0 < tolerance;
						if (foundFontColor)
							hCount++;
						if (hCount >= pixelDetectionCount && glyphYStart < 0) {
							glyphYStart = ysub;
							break;
						} else if (foundFontColor && glyphYStart >= 0) {
							glyphYEnd = ysub;
						}
					}
					// if (glyphYEnd >= 0 && glyphYEnd < ysub) {
					// break;
					// }
				}
				if (glyphYStart >= 0 && glyphYEnd >= 0 && glyphLeftBound >= 0 && glyphRightBound >= 0)
					intermediateResult.add(biSource.getSubimage(glyphLeftBound, glyphYStart,
							Math.min(glyphRightBound - glyphLeftBound, biSource.getWidth() - glyphLeftBound),
							Math.min(glyphYEnd - glyphYStart, biSource.getHeight() - glyphYStart)));
				glyphLeftBound = -1;
				glyphRightBound = -1;
			}
		}
		BufferedImage[] result = intermediateResult.toArray(new BufferedImage[0]);
		return result;
	}
}