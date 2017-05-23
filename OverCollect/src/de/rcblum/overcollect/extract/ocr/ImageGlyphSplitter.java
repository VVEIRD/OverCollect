package de.rcblum.overcollect.extract.ocr;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ImageGlyphSplitter
{
	/**
	 * Splits the Image by the primary color. Truncates all non-primary color
	 * sections. The image is split horizontally and truncated vertically after
	 * the last found primary color.
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
	public static BufferedImage[] splitImage(BufferedImage biSource, Color primary, float tolerance) 
	{
		Objects.requireNonNull(biSource);
		Objects.requireNonNull(primary);
		tolerance = tolerance > 1 ? 1 : tolerance < 0 ? 0 : tolerance;
		List<BufferedImage> intermediateResult = new LinkedList<>();
		int glyphXStart = -1;
		int glyphXEnd = -1;
		
		for(int x=0;x<biSource.getWidth();x++) 
		{
			int pr = primary.getRed();
			int pg = primary.getGreen();
			int pb = primary.getBlue();
			for(int y=0; y<biSource.getHeight();y++) {
				int argb = biSource.getRGB(x, y);
				int r = (argb>>16)&0xFF;
				int g = (argb>>8)&0xFF;
				int b = (argb>>0)&0xFF;
				if (Math.abs(r-pr)/255.0<tolerance && Math.abs(g-pg)/255.0<tolerance && Math.abs(b-pb)/255.0<tolerance && glyphXStart < 0 ) {
					glyphXStart = x;
					break;
				}
				else if (Math.abs(r-pr)/255.0<tolerance && Math.abs(g-pg)/255.0<tolerance && Math.abs(b-pb)/255.0<tolerance) {
					glyphXEnd = x;
				}
			}
			if (glyphXEnd >= 0 &&glyphXEnd < x && glyphXEnd <  biSource.getWidth()) {
				int glyphYStart = -1;
				int glyphYEnd = -1;
				for(int ysub=0;ysub<biSource.getHeight();ysub++) {
					for (int xsub=glyphXStart; xsub<=glyphXEnd;xsub++) {
						int argb = biSource.getRGB(xsub, ysub);
						int r = (argb>>16)&0xFF;
						int g = (argb>>8)&0xFF;
						int b = (argb>>0)&0xFF;
						if (Math.abs(r-pr)/255.0<tolerance && Math.abs(g-pg)/255.0<tolerance && Math.abs(b-pb)/255.0<tolerance && glyphYStart < 0 ) {
							glyphYStart = ysub;
							break;
						}
						else if (Math.abs(r-pr)/255.0<tolerance && Math.abs(g-pg)/255.0<tolerance && Math.abs(b-pb)/255.0<tolerance) {
							glyphYEnd = ysub;
						}
					}
//					if (glyphYEnd >= 0 && glyphYEnd < ysub) {
//						break;
//					}
				}
				if (glyphYStart >= 0 && glyphYEnd >= 0  && glyphXStart >= 0 && glyphXEnd >= 0 )
					intermediateResult.add(biSource.getSubimage(glyphXStart, glyphYStart, Math.min(glyphXEnd-glyphXStart, biSource.getWidth()-glyphXStart), Math.min(glyphYEnd-glyphYStart, biSource.getHeight()-glyphYStart)));
				glyphXStart = -1;
				glyphXEnd = -1;
			}
		}
		BufferedImage[] result = intermediateResult.toArray(new BufferedImage[0]);
		return result;
	}
}
