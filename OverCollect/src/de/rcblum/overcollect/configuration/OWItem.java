package de.rcblum.overcollect.configuration;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import de.rcblum.overcollect.extract.ocr.Glyph;
import de.rcblum.overcollect.utils.CacheOptional;
import de.rcblum.overcollect.utils.Helper;

/**
 * Configuration item for detecting, extracting and configuring data for
 * overwatch from screenshots taken while playing a comp match. ALl OWItems will
 * be loaded by {@link OWLib} from the library path, for detailed information
 * see {@link OWLib}.
 * 
 * @author rcBlum
 *
 */
public class OWItem {

	private String category = null;

	private String itemName = null;

	private String libPath = null;

	/**
	 * concrete instance of the template image
	 */
	private transient CacheOptional<BufferedImage> template = null;

	/**
	 * Filter to match against items relevant for this item
	 */
	private transient Filter filter = null;

	/**
	 * configuration for extracting text from a captured screenshot.
	 */
	private transient OCRConfiguration ocr = null;

	/**
	 * Like filter a matcher to test if an image is in fact a glyph, that can be
	 * extracted.
	 */
	private transient Glyph glyph = null;

	public OWItem(String category, String itemName, String libPath) {
		super();
		this.category = category;
		this.itemName = itemName;
		this.libPath = libPath;
		this.template = CacheOptional.empty();
	}

	/**
	 * Returns if the Item should not be processed further
	 * 
	 * @return <code>true</code> if filtered screenshots represents a map,
	 *         <code>false</code> if not
	 */
	public boolean drop() {
		return Files.exists(Paths.get(libPath, category, itemName, "drop"));
	}

	/**
	 * Returns the category of the item, for filters & co its the screen
	 * resolution in String form, e.G. "1920x1080". For glyphs of the primary
	 * stats its ocr_primary_font, for the secondary stats its
	 * ocr_secondary_font
	 * 
	 * @return
	 */
	public String getCategory() {
		return category;
	}

	public Filter getFilter() {
		if (this.filter != null)
			return this.filter;
		Gson gson = new Gson();
		Path filterFile = Paths.get(this.libPath, this.category, this.itemName, "filter.json");
		if (Files.exists(filterFile) && !Files.isDirectory(filterFile)) {
			try {
				String text = new String(Files.readAllBytes(filterFile), StandardCharsets.UTF_8);
				filter = gson.fromJson(text, Filter.class);
			} catch (JsonIOException | JsonSyntaxException | IOException e) {
				e.printStackTrace();
			}
		}
		return filter;
	}

	public Glyph getGlyph() {
		if (this.glyph != null)
			return this.glyph;
		Gson gson = new Gson();
		Path filterFile = Paths.get(this.libPath, this.category, this.itemName, "glyph.json");
		if (Files.exists(filterFile) && !Files.isDirectory(filterFile)) {
			try {
				String text = new String(Files.readAllBytes(filterFile), StandardCharsets.UTF_8);
				this.glyph = gson.fromJson(text, Glyph.class);
			} catch (JsonIOException | JsonSyntaxException | IOException e) {
				e.printStackTrace();
			}
		}
		return glyph;
	}

	/**
	 * Returns the item name, for Heroes it the heroname, for map its the map
	 * name.
	 * 
	 * @return
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * Returns the library path for this item
	 * 
	 * @return Path to the library
	 */
	public String getLibPath() {
		return libPath;
	}

	public OCRConfiguration getOCRConfiguration() {
		if (this.ocr != null)
			return this.ocr;
		Gson gson = new Gson();
		Path ocrFile = Paths.get(this.libPath, this.category, this.itemName, "ocr_fields.json");
		if (Files.exists(ocrFile) && !Files.isDirectory(ocrFile)) {
			try {
				String text = new String(Files.readAllBytes(ocrFile), StandardCharsets.UTF_8);
				ocr = gson.fromJson(text, OCRConfiguration.class);
			} catch (JsonIOException | JsonSyntaxException | IOException e) {
				e.printStackTrace();
			}
		}
		return ocr;
	}

	public BufferedImage getTemplate() {
		if (this.template.isPresent())
			return this.template.get();
		Path templateFile = Paths.get(this.libPath, this.category, this.itemName, "template.png");
		if (Files.exists(templateFile) && !Files.isDirectory(templateFile)) {
			try {
				template = CacheOptional.of(ImageIO.read(Files.newInputStream(templateFile)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return template.isPresent() ? template.get() : null;
	}

	/**
	 * Reutnrs if the item has a filter, to match screenshots against.
	 * 
	 * @return <code>True</code> if a {@link Filter} is present,
	 *         <code>false</code> if not
	 */
	public boolean hasFilter() {
		return Files.exists(Paths.get(libPath, category, itemName, "filter.json"));
	}

	/**
	 * Returns if this represents a glyph (0..9) and ahs a Glyph-Object to test
	 * an image against
	 * 
	 * @return <code>true</code> if item is a glyph, <code>false</code> if not
	 */
	public boolean hasGlyph() {
		return Files.exists(Paths.get(libPath, category, itemName, "glyph.json"));
	}

	/**
	 * Returns if the item has an OCR filter present, to extract text from the
	 * captured screenshot.
	 * 
	 * @return <code>True</code> if {@link OCRConfiguration} is present,
	 *         <code>false</code> if not
	 */
	public boolean hasOCRConfiguration() {
		return Files.exists(Paths.get(libPath, category, itemName, "ocr_fields.json"));
	}

	/**
	 * Returns if the item has a template Screenshot attached to it
	 * 
	 * @return <code>true</code> if there is an screenshot, <code>false</code>
	 *         if not
	 */
	public boolean hasTemplate() {
		return Files.exists(Paths.get(libPath, category, itemName, "template.png"));
	}

	/**
	 * Returns if the captrured Screen contains stats of an hero
	 * 
	 * @return <code>true</code> if screenshot contains hero stats,
	 *         <code>false</code> if not
	 */
	public boolean isHero() {
		return Files.exists(Paths.get(libPath, category, itemName, "hero"));
	}

	/**
	 * Returns if the Item representátes a map
	 * 
	 * @return <code>true</code> if filtered screenshots represents a map,
	 *         <code>false</code> if not
	 */
	public boolean isMap() {
		return Files.exists(Paths.get(libPath, category, itemName, "map"));
	}

	/**
	 * Returns if this item starts a new Match
	 * 
	 * @return <code>true</code> if item is a Match indicator,
	 *         <code>false</code> if it is not.
	 */
	public boolean isMatchIndicator() {
		return Files.exists(Paths.get(libPath, category, itemName, "matchindicator"));
	}

	public OWItem rescale(float rescale) {
		if (getCategory().contains("x")) {
			int width = Helper.toInteger(getCategory().split("x")[0], -1);
			int height = Helper.toInteger(getCategory().split("x")[1], -1);
			if (width != -1 && height != -1) {
				width = Math.round(width * rescale);
				height = Math.round(height * rescale);
				String category = width + "x" + height;
				Path itemPath = Paths.get(this.libPath, category, getItemName());
				try {
					if (!Files.exists(itemPath)) {
						Files.createDirectories(itemPath);
					}
					OWItem nItem = new OWItem(category, this.itemName, this.libPath);
					if (this.hasFilter())
						nItem.saveFilter(this.getFilter().rescale(rescale));
					if (this.hasOCRConfiguration())
						nItem.saveOCRConfiguration(this.getOCRConfiguration().rescale(rescale));
					nItem.setDrop(this.drop());
					nItem.setHero(this.isHero());
					nItem.setMap(this.isMap());
					nItem.setMatchIndicator(this.isMatchIndicator());
					return nItem;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public void saveFilter(Filter f) throws IOException {
		this.filter = f;
		Filter.save(this.libPath, this.category, this.itemName, f);
	}

	public void saveGlyph(Glyph f) throws IOException {
		this.glyph = f;
		Glyph.save(this.libPath, this.category, this.itemName, f);
	}

	public void saveOCRConfiguration(OCRConfiguration ocr) throws IOException {
		this.ocr = ocr;
		OCRConfiguration.save(this.libPath, this.category, this.itemName, ocr);
	}

	public void setDrop(boolean selected) throws IOException {
		if (selected && !Files.exists(Paths.get(libPath, category, itemName, "drop")))
			Files.createFile(Paths.get(libPath, category, itemName, "drop"));
		else if (!selected && Files.exists(Paths.get(libPath, category, itemName, "drop")))
			Files.delete(Paths.get(libPath, category, itemName, "drop"));
	}

	public void setGlyph(boolean selected, int fontSize) throws IOException {
		if (selected)
			saveGlyph(Glyph.fromFilter(this.getItemName().charAt(0), this.getFilter(), fontSize));
		else if (Files.exists(Paths.get(libPath, category, itemName, "glyph.json")))
			Files.delete(Paths.get(libPath, category, itemName, "glyph.json"));
	}

	public void setHero(boolean selected) throws IOException {
		if (selected && !Files.exists(Paths.get(libPath, category, itemName, "hero")))
			Files.createFile(Paths.get(libPath, category, itemName, "hero"));
		else if (!selected && Files.exists(Paths.get(libPath, category, itemName, "hero")))
			Files.delete(Paths.get(libPath, category, itemName, "hero"));
	}

	/**
	 * Sets the name of the item, this is only temporally, as the items are
	 * loaded from the library at program start
	 * 
	 * @param itemName
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * Sets the library path this item is containt within
	 * 
	 * @param libPath
	 */
	public void setLibPath(String libPath) {
		this.libPath = libPath;
	}

	public void setMap(boolean selected) throws IOException {
		if (selected && !Files.exists(Paths.get(libPath, category, itemName, "map")))
			Files.createFile(Paths.get(libPath, category, itemName, "map"));
		else if (!selected && Files.exists(Paths.get(libPath, category, itemName, "map")))
			Files.delete(Paths.get(libPath, category, itemName, "map"));
	}

	public void setMatchIndicator(boolean selected) throws IOException {
		if (selected && !Files.exists(Paths.get(libPath, category, itemName, "matchindicator")))
			Files.createFile(Paths.get(libPath, category, itemName, "matchindicator"));
		else if (!selected && Files.exists(Paths.get(libPath, category, itemName, "matchindicator")))
			Files.delete(Paths.get(libPath, category, itemName, "matchindicator"));
	}
	
	/**
	 * Sets the screen resolution this item is configured for. Do not use if you
	 * are not shure that the item works for the new Screen resolution
	 * 
	 * @param resolution
	 */
	public void setResolution(String resolution) {
		this.category = resolution;
	}
}
