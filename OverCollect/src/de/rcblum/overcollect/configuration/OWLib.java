package de.rcblum.overcollect.configuration;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import de.rcblum.overcollect.data.OWMatch;
import de.rcblum.overcollect.extract.ocr.Glyph;
import de.rcblum.overcollect.utils.Helper;

public class OWLib {

	static {
		if (System.getProperties().getProperty("owcollect.temp.dir") == null)
			System.getProperties().setProperty("owcollect.temp.dir", "tmp");
		if (System.getProperties().getProperty("owcollect.ui.dateformat") == null)
			System.getProperties().setProperty("owcollect.ui.dateformat", "yyyy-MM-dd HH:mm:ss");
		if (System.getProperties().getProperty("owcollect.match.dir") == null)
			System.getProperties().setProperty("owcollect.match.dir", "capture");
		if (System.getProperties().getProperty("owcollect.data.dir") == null)
			System.getProperties().setProperty("owcollect.data.dir", "data");
		if (System.getProperties().getProperty("owcollect.image.dir") == null)
			System.getProperties().setProperty("owcollect.image.dir", "images");
		if (System.getProperties().getProperty("owcollect.lib.dir") == null)
			System.getProperties().setProperty("owcollect.lib.dir", "lib" + File.separator + "owdata");

		// Path libPath =
		// Paths.get(System.getProperties().getProperty("owcollect.lib.dir"));
		Path dataPath = Paths.get(System.getProperties().getProperty("owcollect.data.dir"));
		Path imagePath = Paths.get(System.getProperties().getProperty("owcollect.image.dir"));
		Path matchPath = Paths.get(System.getProperties().getProperty("owcollect.match.dir"));
		Path tempDir = Paths.get(System.getProperties().getProperty("owcollect.temp.dir"));

		try {
			// Make Paths if necessary
			if (!Files.exists(matchPath))
				Files.createDirectories(matchPath);
			if (!Files.exists(dataPath))
				Files.createDirectories(dataPath);
			if (!Files.exists(imagePath))
				Files.createDirectories(imagePath);
			if (!Files.exists(tempDir))
				Files.createDirectories(tempDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//
	// Static attributes
	//

	public static final String VERSION_STRING = "0.1.8-alpha";

	private static String defaultFolder = Paths.get("lib", "owdata").toString();

	private static Map<String, OWLib> instances = new HashMap<>();

	public static OWLib getInstance() {
		defaultFolder = System.getProperties().getProperty("owcollect.lib.dir");
		if (instances.get(defaultFolder) == null)
			instances.put(defaultFolder, new OWLib(defaultFolder));
		return instances.get(defaultFolder);
	}

	//
	// Instance attributes
	//

	private Path libPath = null;

	private List<String> supportedScreenResolutions = null;

	private Map<String, Map<String, OWItem>> items = null;

	private Map<String, OWMatch> matches = null;

	private Properties config = null;
	
	private List<String> accounts = null;
	
	private String selectedAccount = null;

	private OWLib() {
		this(Paths.get("lib", "owdata"));
	}

	private OWLib(Path libPath) {
		this.libPath = Objects.requireNonNull(libPath);
		if (!Files.exists(this.libPath))
			throw new IllegalArgumentException(this.libPath.toAbsolutePath().toString() + " does no exists");
		this.supportedScreenResolutions = new ArrayList<>(10);
		this.items = new HashMap<>();
		this.config = new Properties();
		try (InputStream in = Files.newInputStream(this.libPath.resolve("configuration.properties"))) {
			this.config.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		init();
	}

	private OWLib(String libPath) {
		this(Paths.get(libPath));
	}

	public void addAccount(String accountName) {
		this.accounts.add(accountName);
		this.saveConfig();
	}

	public void addMatch(OWMatch match) {
		Objects.requireNonNull(match);
		this.matches.put(match.getMatchId(), match);
	}

	public List<String> getAccounts() {
		return this.accounts;
	}

	public String getActiveAccount() {
		return this.selectedAccount;
	}

	public boolean getBoolean(String key) {
		return Boolean.valueOf(this.config.getProperty(key, "false"));
	}

	public String getDebugDir() {
		String ddirName = getString("debug.dir",  "debug");
		Path ddir = Paths.get(ddirName);
		if (!Files.exists(ddir)) {
			try {
				Files.createDirectories(ddir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return ddirName;
	}

	public List<OWItem> getDropItems(int width, int height) {
		return this.items.get(width + "x" + height) != null ? this.items.get(width + "x" + height).values().stream()
				.filter(i -> i.drop()).collect(Collectors.toList()) : new LinkedList<>();
	}

	public List<OWItem> getHeroes() {
		return this.getItems("1920x1080").stream().filter(i -> i.isHero()).collect(Collectors.toList());
	}

	public int getInteger(String key, int defaultValue) {
		String val = this.config.getProperty(key, String.valueOf(defaultValue));
		return val.matches("^-?\\d+$") ? Integer.valueOf(val) : defaultValue;
	}

	public OWItem getItem(Dimension screenResolution, String itemName) {
		String res = ((int) screenResolution.getWidth()) + "x" + ((int) screenResolution.getHeight());
		return this.getItem(res, itemName);
	}

	public OWItem getItem(int width, int height, String itemName) {
		String res = width + "x" + height;
		return this.getItem(res, itemName);
	}

	public OWItem getItem(String screenResolution, String itemName) {
		return this.items.get(screenResolution).get(itemName);
	}

	public List<String> getItemNames(Dimension screenResolution) {
		String res = ((int) screenResolution.getWidth()) + "x" + ((int) screenResolution.getHeight());
		return this.getItemNames(res);
	}

	public List<String> getItemNames(String screenResolution) {
		return new ArrayList<>(this.items.get(screenResolution).keySet());
	}

	public List<OWItem> getItems(int width, int height) {
		return this.getItems(width + "x" + height);

	}

	public List<OWItem> getItems(String res) {
		return this.items.get(res) != null ? new ArrayList<>(this.items.get(res).values()) : null;
	}

	public Path getLibPath() {
		return libPath;
	}

	public List<String> getMaps() {
		return this.items.get("1920x1080").values().stream().filter(i -> i.isMap()).map(i -> i.getItemName())
				.collect(Collectors.toList());
	}

	public OWMatch getMatch(String matchId) {
		return this.matches.get(matchId);
	}

	public List<OWMatch> getMatches() {
		return new ArrayList<>(this.matches.values());
	}

	public List<String> getMatchIds() {
		return new ArrayList<>(this.matches.keySet());
	}

	public List<String> getMatchIndicators() {
		return this.items.get("1920x1080").values().stream().filter(i -> i.isMatchIndicator()).map(i -> i.getItemName())
				.collect(Collectors.toList());
	}

	public List<Glyph> getPrimaryFontGlyphs() {
		return this.items.get("ocr_primary_font") != null ? this.items.get("ocr_primary_font").values().stream()
				.filter(i -> i.hasGlyph()).map(i -> i.getGlyph()).collect(Collectors.toList()) : null;
	}

	public int getSecondaryFontBaseSize() {
		List<Glyph> g = getSecondaryFontGlyphs();
		return g.size() > 0 ? g.get(0).getBaseFontSize() : 57;
	}
	
	public List<Glyph> getSecondaryFontGlyphs() {
		return this.items.get("ocr_secondary_font") != null ? this.items.get("ocr_secondary_font").values().stream()
				.filter(i -> i.hasGlyph()).map(i -> i.getGlyph()).collect(Collectors.toList()) : null;
	}
	
	public String getString(String key, String defaultString) {
		return this.config.getProperty(key) != null ? this.config.getProperty(key) : defaultString;
	}
	
	public List<String> getSupportedScreenResolutions() {
		return this.supportedScreenResolutions;
	}

	public Path getTempPath() {
		return Paths.get(System.getProperties().getProperty("owcollect.temp.dir"));
	}

	private void init() {
		File[] resolutionFolders = this.libPath.toFile().listFiles();
		Arrays.sort(resolutionFolders);
		// load accounts
		this.accounts = new LinkedList(this.config.getProperty("accounts") != null ? Arrays.asList(this.config.getProperty("accounts").split(",")) : new LinkedList<>());
		this.selectedAccount = this.config.getProperty("activeAccount");
		
		// Find all resolutions
		for (File res : resolutionFolders) {
			if (res.isDirectory()) {
				String[] dimensionStrings = res.getName().split("x");
				if (dimensionStrings.length == 2 && dimensionStrings[0].matches("\\d+")
						&& dimensionStrings[1].matches("\\d+")) {

				}
				this.supportedScreenResolutions.add(res.getName());
			}
		}
		// Find all config items
		for (String res : this.supportedScreenResolutions) {
			this.items.put(res, new HashMap<>());
			File[] items = this.libPath.resolve(res).toFile().listFiles();
			Arrays.sort(items);
			for (File item : items) {
				if (item.exists() && item.isDirectory()) {
					this.items.get(res).put(item.getName(), new OWItem(res, item.getName(), this.libPath.toString()));
				}

			}
		}
		// Find all Matches
		this.matches = new HashMap<>();
		File[] matchFiles = Paths.get(System.getProperties().getProperty("owcollect.data.dir")).toFile().listFiles();
		for (File match : matchFiles) {
			Helper.info(this.getClass(), match);
			OWMatch m = OWMatch.fromJsonFile(match);
			if (m != null) {
				this.matches.put(m.getMatchId(), m);
				if (m.getAccount() == null && this.getActiveAccount() != null) {
					m.setAccount(this.getActiveAccount());
					OWMatch.toJsonFile(m, match);
				}
			}
		}
	}

	private void saveConfig() {
		this.config.setProperty("accounts", String.join(",", this.accounts));
		this.config.setProperty("activeAccount", this.selectedAccount);
		try (OutputStream os = Files.newOutputStream(this.libPath.resolve("configuration.properties"))) {
			this.config.store(os, "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void setActiveAccount(String account) {
		if (!this.accounts.contains(account))
			this.accounts.add(account);
		this.selectedAccount = account;
		this.config.setProperty("activeAccount", this.selectedAccount);
		this.saveConfig();
	}

	public boolean supportScreenResolution(Dimension screenResolution) {
		String res = ((int) screenResolution.getWidth()) + "x" + ((int) screenResolution.getHeight());
		return this.supportScreenResolution(res);
	}

	public boolean supportScreenResolution(int width, int height) {
		String res = width + "x" + height;
		return this.supportScreenResolution(res);
	}

	public boolean supportScreenResolution(String screenResolution) {
		return this.supportedScreenResolutions.contains(screenResolution);
	}
}
