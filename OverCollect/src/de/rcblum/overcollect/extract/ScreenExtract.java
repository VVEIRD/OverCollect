package de.rcblum.overcollect.extract;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;

import de.rcblum.overcollect.configuration.OCRConfiguration;
import de.rcblum.overcollect.configuration.OWLib;
import de.rcblum.overcollect.extract.ocr.Glyph;
import de.rcblum.overcollect.extract.ocr.ImageGlyphSplitter;
import de.rcblum.overcollect.utils.Helper;

public class ScreenExtract {

	public static BufferedImage adjustImage(BufferedImage source, Color primaryColor, double skewX, int skewTrim,
			boolean doRecolor) {
		if (doRecolor) {
			// Reverse color
			for (int x = 0; x < source.getWidth(); x++) {
				for (int y = 0; y < source.getHeight(); y++) {
					int rgba = source.getRGB(x, y);
					Color col = new Color(rgba, true);
					int red = col.getRed();
					int green = col.getGreen();
					int blue = col.getBlue();
					col = new Color(255 - red, 255 - green, 255 - blue);
					if (Math.abs(red - 16) < 15 && Math.abs(green - 209) < 15 && Math.abs(blue - 24) < 15)
						col = Color.BLACK;
					source.setRGB(x, y, col.getRGB());
				}
			}
			// Test against primary text color
			for (int x = 0; x < source.getWidth(); x++) {
				for (int y = 0; y < source.getHeight(); y++) {
					int rgba = source.getRGB(x, y);
					Color col = new Color(rgba, true);
					int pixelR = col.getRed();
					int pixelG = col.getGreen();
					int pixelB = col.getBlue();

					int textR = primaryColor.getRed();
					int textG = primaryColor.getGreen();
					int textB = primaryColor.getBlue();
					Color c = null;
					int tolerance = 80;
					if (Math.abs(pixelR - textR) < tolerance && Math.abs(pixelG - textG) < tolerance
							&& Math.abs(pixelB - textB) < tolerance)
						c = primaryColor;
					else
						c = new Color(255 - textR, 255 - textG, 255 - textB);

					// int colorValue = (pixelR - 16 < 15 && pixelG - 205 < 12
					// && pixelB - 22 < 10) ? 203 + 205 + 208
					// : (col.getRed() + col.getGreen() + col.getBlue());
					// colorValue = colorValue <= 200 ? colorValue / 15 :
					// colorValue / 2;
					// colorValue = colorValue > 255 ? 255 : colorValue;
					// col = new Color(255 - colorValue, 255 - colorValue, 255 -
					// colorValue);
					source.setRGB(x, y, c.getRGB());
				}
			}
		}
		BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
		if (skewX > 0.05 || skewX < 0.05) {
			// Adjust the image width if we use a negative skew...
			// double skewX = config.skew; //0.225d;
			double xSkew = (skewX < 0) ? -skewX * source.getHeight() : 0;
			AffineTransform at = AffineTransform.getTranslateInstance(xSkew, 0);
			at.shear(skewX, 0);
			AffineTransformOp op = new AffineTransformOp(at,
					new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC));
			image = op.filter(source, null);
			image = image.getSubimage(skewTrim/* 17 */, 0, image.getWidth() - (skewTrim * 2), image.getHeight());
		}
		Graphics2D g = image.createGraphics();
		g.setColor(doRecolor ? new Color(249, 249, 249) : new Color(26, 31, 38));
		g.fillRect(0, 0, image.getWidth(), 7);
		g.dispose();
		return image;
	}

	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		try {
			Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
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

	private OCRConfiguration config = null;

	private BufferedImage screenImg = null;

	private Map<String, String> values = null;

	private Map<String, String> secondaryValues = null;

	public Map<String, BufferedImage> valueImages = null;

	public Map<BufferedImage, Glyph> nohitPrimary = null;

	public Map<BufferedImage, Glyph> nohitSecondary = null;

	public Map<String, BufferedImage> secondaryValueImages = null;

	public ScreenExtract(BufferedImage endScreen, OCRConfiguration config) {
		super();
		this.config = Objects.requireNonNull(config);
		this.screenImg = Objects.requireNonNull(endScreen);
		this.values = new HashMap<>();
		this.secondaryValues = new HashMap<>();
		this.valueImages = new HashMap<>();
		this.nohitPrimary = new HashMap<>();
		this.nohitSecondary = new HashMap<>();
		this.secondaryValueImages = new HashMap<>();
		if (this.config.values != null) {
			for (String field : this.config.values.keySet()) {
				int[] bounds = this.config.values.get(field);
				Helper.info(this.getClass(), "Primary Field: [name=" + field + ", x=" + bounds[0] + ", y=" + bounds[1] + ", w="
						+ this.config.secondaryDataFieldSize[0] + ", h=" + this.config.secondaryDataFieldSize[1] + "]");
				this.valueImages.put(field,
						adjustImage(
								this.screenImg.getSubimage(bounds[0], bounds[1], this.config.dataFieldSize[0],
										this.config.dataFieldSize[1]),
								this.config.getDataColor(), config.skew, config.skewTrim, config.doRecolor));
			}
		}
		if (this.config.secondaryValues != null) {
			for (String field : this.config.secondaryValues.keySet()) {
				int[] bounds = this.config.secondaryValues.get(field);
				Helper.info(this.getClass(), "Secondary Field: [name=" + field + ", x=" + bounds[0] + ", y=" + bounds[1] + ", w="
						+ this.config.secondaryDataFieldSize[0] + ", h=" + this.config.secondaryDataFieldSize[1] + "]");
				this.secondaryValueImages.put(field, this.screenImg.getSubimage(bounds[0], bounds[1],
						this.config.secondaryDataFieldSize[0], this.config.secondaryDataFieldSize[1]));
			}
		}
		readValues();
	}

	public BufferedImage getImage(String string) {
		return this.valueImages.get(string);
	}

	public BufferedImage getSecondaryImage(String string) {
		return this.secondaryValueImages.get(string);
	}

	public String getSecondaryValue(String key) {
		return this.secondaryValues.get(key);
	}

	public List<String> getSecondaryValueNames() {
		return new ArrayList<>(this.secondaryValues.keySet());
	}

	public String getValue(String key) {
		return this.values.get(key);
	}

	public List<String> getValueNames() {
		return new ArrayList<>(this.values.keySet());
	}

	private void readValues() {
		for (String field : this.valueImages.keySet()) {
			BufferedImage img = this.valueImages.get(field);
			// try {
			// ImageIO.write(img, "PNG", Paths.get("tmp", field +
			// ".png").toFile());
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			BufferedImage[] charImgs = ImageGlyphSplitter.splitImage(img, this.config.getDataColor(), 0.06f, this.config.pixelDetectionCount);
			char[] chars = new char[charImgs.length];
			Glyph[] mostProbableGlyph = new Glyph[charImgs.length];
			BufferedImage[] mostProbableImg = new BufferedImage[charImgs.length];
			float[] mostProbableGlyphPerc = new float[charImgs.length];
			for (int i = 0; i < chars.length; i++) {
				chars[i] = ' ';
			}
			List<Glyph> glyphs = OWLib.getInstance().getPrimaryFontGlyphs();
			for (int i = 0; i < charImgs.length; i++) {
				for (Glyph glyph : glyphs) {
					int newW = Math.round(charImgs[i].getWidth()
							* (((float) glyph.getBaseFontSize()) / ((float) charImgs[i].getHeight())));
					int newH = Math.round(charImgs[i].getHeight()
							* (((float) glyph.getBaseFontSize()) / ((float) charImgs[i].getHeight())));
					BufferedImage rescaled = glyph.getBaseFontSize() != charImgs[i].getHeight()
							? resize(charImgs[i], newW, newH) : charImgs[i];
					if (OWLib.getInstance().getBoolean("debug.extraction")) {
						try {
							ImageIO.write(rescaled, "PNG",
									Paths.get("tmp", field + "[" + i + "]_rescaled.png").toFile());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					float probability = glyph.matchPercentage(rescaled, this.config.getDataColor(), 0.06f);
					if (mostProbableGlyph[i] == null || mostProbableGlyphPerc[i] < probability) {
						mostProbableGlyph[i] = glyph;
						mostProbableGlyphPerc[i] = probability;
						mostProbableImg[i] = rescaled;
					}
					if (glyph.match(rescaled, this.config.getDataColor(), 0.06f)) {
						try {
							ImageIO.write(rescaled, "PNG",
									Paths.get("tmp", (glyph.getChar() == ':' ? '_' : glyph.getChar()) + "_" + field
											+ "[" + i + "]_rescaled.png").toFile());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						chars[i] = glyph.getChar();
					}
				}
				if (chars[i] == ' ') {
					try {
						this.nohitPrimary.put(charImgs[i], mostProbableGlyph[i]);
						ImageIO.write(charImgs[i], "PNG",
								Paths.get("tmp", "NOHIT_" + field + "[" + i + "]_rescaled.png").toFile());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			this.values.put(field, new String(chars).replace(" ", ""));
		}
		// Secondary Values
		for (String field : this.secondaryValueImages.keySet()) {
			BufferedImage img = this.secondaryValueImages.get(field);
			// try {
			// ImageIO.write(img, "PNG", Paths.get("tmp", field +
			// ".png").toFile());
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			int glyphFontSize = OWLib.getInstance().getSecondaryFontBaseSize();
			int newW = (int) (img.getWidth() * glyphFontSize / (float) config.secondaryFontSize);
			int newH = (int) (img.getHeight() * glyphFontSize / (float) config.secondaryFontSize);
			BufferedImage imgResc = glyphFontSize != config.secondaryFontSize ? resize(img, newW, newH) : img;
			imgResc = adjustImage(imgResc, this.config.getSecondaryDataColor(), config.skewSecondary,
					config.skewSecondaryTrim, config.doRecolor);
			BufferedImage[] charImgs = ImageGlyphSplitter.splitImage(imgResc, config.getSecondaryDataColor(), 0.06f, 3);
			char[] chars = new char[charImgs.length];
			Glyph[] mostProbableGlyph = new Glyph[charImgs.length];
			BufferedImage[] mostProbableImg = new BufferedImage[charImgs.length];
			float[] mostProbableGlyphPerc = new float[charImgs.length];
			for (int i = 0; i < chars.length; i++) {
				chars[i] = ' ';
			}
			List<Glyph> glyphs = OWLib.getInstance().getSecondaryFontGlyphs();
			Collections.sort(glyphs, new Comparator<Glyph>() {
				@Override
				public int compare(Glyph o1, Glyph o2) {
					return Character.compare(o1.getChar(), o2.getChar());
				}
			});
			for (int i = 0; i < charImgs.length; i++) {
				for (Glyph glyph : glyphs) {
					// int newW = (int)(charImgs[i].getWidth() *
					// glyph.getBaseFontSize()/(float)config.secondaryFontSize);
					// int newH = (int)(charImgs[i].getHeight() *
					// glyph.getBaseFontSize()/(float)config.secondaryFontSize);
					BufferedImage rescaled = charImgs[i]; // glyph.getBaseFontSize()
															// !=
															// config.secondaryFontSize?
															// resize(charImgs[i],
															// newW, newH) :
															// charImgs[i];
					// try {
					// ImageIO.write(rescaled, "PNG", Paths.get("tmp", field +
					// "[" + i + "].png").toFile());
					// } catch (IOException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					if (glyph.match(rescaled, this.config.getSecondaryDataColor(), 0.06f))
						chars[i] = chars[i] != ' ' && chars[i] == '3' && glyph.getChar() == '8' ? chars[i]
								: glyph.getChar();
				}
				if (chars[i] == ' ') {
					try {
						this.nohitSecondary.put(charImgs[i], mostProbableGlyph[i]);
						ImageIO.write(charImgs[i], "PNG",
								Paths.get("tmp", "NOHIT_" + field + "[" + i + "]_secondary_rescaled.png").toFile());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			this.secondaryValues.put(field, new String(chars).replace(" ", ""));
			// try {
			//
			// this.secondaryValues.put(field,
			// instance.doOCR(img).trim().replace(",", ""));
			// } catch (NumberFormatException | TesseractException e) {
			// e.printStackTrace();
			// }
		}
	}
}
