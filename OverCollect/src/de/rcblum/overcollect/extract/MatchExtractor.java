package de.rcblum.overcollect.extract;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import com.google.gson.Gson;

import de.rcblum.overcollect.collect.listener.owmatch.OWMatchEvent;
import de.rcblum.overcollect.collect.listener.owmatch.OWMatchListener;
import de.rcblum.overcollect.configuration.OCRConfiguration;
import de.rcblum.overcollect.configuration.OWLib;
import de.rcblum.overcollect.data.OWCharacterStats;
import de.rcblum.overcollect.data.OWMatch;
import de.rcblum.overcollect.data.OWMatch.Result;
import de.rcblum.overcollect.extract.listener.OWMatchExtractionListener;
import de.rcblum.overcollect.extract.ocr.Glyph;
import de.rcblum.overcollect.utils.Helper;

public class MatchExtractor implements Runnable, OWMatchListener {
	/**
	 * Subclass that processes the match screenshots into a OWMatch and saves it
	 * as a JSON String on disk.
	 * 
	 * @author Roland von Werden
	 *
	 */
	public class MatchExtractWorker implements Runnable {
		/**
		 * Path to matchfolder
		 */
		private final Path matchPath;

		/**
		 * Path to imagefolder where the images with the data are stored.
		 */
		private final Path imagePath;

		/**
		 * Path where the json file of the match will be stored
		 */
		private final Path dataPath;

		/**
		 * Gson for deserialization of the OCR Configuration.
		 */
		private Gson g = new Gson();

		/**
		 * Match that will be created from the images.
		 */
		private OWMatch match = null;

		public MatchExtractWorker(Path matchPath, Path imagePath, Path dataPath) {
			this.matchPath = Objects.requireNonNull(matchPath);
			this.dataPath = Objects.requireNonNull(dataPath);
			this.imagePath = Objects.requireNonNull(imagePath);
			this.match = new OWMatch(matchPath.getFileName().toString());
		}

		private ScreenExtract createScreenExtract(Path imagePath, Path ocrConfig) {
			if (OWLib.getInstance().getBoolean("debug.extraction"))
				Helper.debug(this.getClass(), "Extracting: " +imagePath.getFileName().toString());
			if (Files.exists(imagePath) && Files.exists(ocrConfig))
				try {
					BufferedImage image = ImageIO.read(imagePath.toFile());
					String text = new String(Files.readAllBytes(ocrConfig), StandardCharsets.UTF_8);
					OCRConfiguration config = g.fromJson(text, OCRConfiguration.class);
					if (OWLib.getInstance().getBoolean("debug.extraction"))
						Helper.debug(this.getClass(), "OCR-Configuration.pixelDetectionCount: " + config.pixelDetectionCount);
					ScreenExtract sc = new ScreenExtract(image, config);
					return sc;
				} catch (IOException e) {
					e.printStackTrace();
				}
			return null;
		}

		private void readStats(Path hero) {
			Helper.info(this.getClass(), "Hero: " + hero);
			String filename = hero.getFileName().toString().replace(".png", "");
			ScreenExtract sc = null;
			if (Files.exists(hero) && Files.exists(hero.getParent().resolve(filename + ".ocr")))
				sc = createScreenExtract(hero, hero.getParent().resolve(filename + ".ocr"));
			if (sc != null) {
				OWCharacterStats cStats = new OWCharacterStats();
				cStats.setDamageDone(Helper.toInteger(sc.getValue("damageDone"), -1));
				cStats.setDeaths(Helper.toInteger(sc.getValue("deaths"), 1));
				cStats.setEliminations(Helper.toInteger(sc.getValue("eliminations"), -1));
				cStats.setHealingDone(Helper.toInteger(sc.getValue("healingDone"), 1));
				cStats.setObjectiveKills(Helper.toInteger(sc.getValue("objectiveKills"), -1));
				cStats.setObjectiveTime(sc.getValue("objectiveTime"));
				cStats.setName(filename);
				for (String key : sc.getValueNames()) {
					this.writeImage(this.imagePath.resolve(filename + "_" + key + ".png"), sc.getImage(key));
				}
				for (String secStat : sc.getSecondaryValueNames()) {
					cStats.addSecondaryStat(secStat, Helper.toInteger(sc.getSecondaryValue(secStat), -1));
					this.writeImage(this.imagePath.resolve(filename + "_" + secStat + ".png"),
							sc.getSecondaryImage(secStat));
				}
				for (Map.Entry<BufferedImage, Glyph> entry : sc.nohitPrimary.entrySet()) {
					fireExtractionError(entry.getKey(), entry.getValue(), OWMatchExtractionListener.StatType.PRIMARY);
				}
				for (Map.Entry<BufferedImage, Glyph> entry : sc.nohitSecondary.entrySet()) {
					fireExtractionError(entry.getKey(), entry.getValue(), OWMatchExtractionListener.StatType.SECONDARY);
				}
				this.match.addCharacterStats(cStats);
			} else {
				Helper.info(this.getClass(), "..Stats could not be extracted, files missing");
			}
		}

		@Override
		public void run() {

			if (OWLib.getInstance().getBoolean("debug.extraction"))
				Helper.debug(this.getClass(), "Extracting match: " + matchPath.getFileName().toString() + "[" + matchPath.toAbsolutePath().toString() +"]");
			// Extract time and map
			Properties properties = new Properties();
			try (InputStream inD = Files.newInputStream(this.matchPath.resolve("data.properties"))) {
				properties.load(inD);
				this.match.setStartTime(properties.getProperty("startTime"));
				this.match.setEndTime(properties.getProperty("endTime"));
				this.match.setAccount(properties.getProperty("account"));
				this.match.setStacksize(properties.getProperty("stacksize") != null
						&& Helper.isInteger(properties.getProperty("stacksize"))
								? Helper.toInteger(properties.getProperty("stacksize"), 1) : 1);
				this.match.setMap(
						properties.getProperty("map") != null ? properties.getProperty("map").replace("_", " ") : null);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Check for defeat
			Path defeat = this.matchPath.resolve("_defeat.png");
			Path victory = this.matchPath.resolve("_victory.png");
			Path draw = this.matchPath.resolve("_draw.png");
			if (Files.exists(victory))
				this.match.setResult(Result.VICTORY);
			else if (Files.exists(draw))
				this.match.setResult(Result.DRAW);
			else
				this.match.setResult(Result.DEFEAT);
			// Extract Team / Enemy Group SR
			ScreenExtract sc = null;
			if (properties.getProperty("map") != null
					&& Files.exists(this.matchPath.resolve(properties.getProperty("map") + ".png"))
					&& Files.exists(this.matchPath.resolve(properties.getProperty("map") + ".ocr")))
				sc = createScreenExtract(this.matchPath.resolve(properties.getProperty("map") + ".png"),
						this.matchPath.resolve(properties.getProperty("map") + ".ocr"));
			if (sc != null) {
				this.match.setTeamSr(sc.getValue("teamSR"));
				this.match.setEnemySr(sc.getValue("enemySR"));
				this.writeImage(this.imagePath.resolve("teamSR.png"), sc.getImage("teamSR"));
				this.writeImage(this.imagePath.resolve("enemySR.png"), sc.getImage("enemySR"));
				for (Map.Entry<BufferedImage, Glyph> entry : sc.nohitPrimary.entrySet()) {
					fireExtractionError(entry.getKey(), entry.getValue(), OWMatchExtractionListener.StatType.PRIMARY);
				}
				for (Map.Entry<BufferedImage, Glyph> entry : sc.nohitSecondary.entrySet()) {
					fireExtractionError(entry.getKey(), entry.getValue(), OWMatchExtractionListener.StatType.SECONDARY);
				}
			}
			sc = null;
			// Extract SR if possible
			if (Files.exists(this.matchPath.resolve("_sr_screen.png"))
					&& Files.exists(this.matchPath.resolve("_sr_screen.ocr")))
				sc = createScreenExtract(this.matchPath.resolve("_sr_screen.png"),
						this.matchPath.resolve("_sr_screen.ocr"));
			if (sc != null) {
				this.match.setSr(sc.getValue("sr"));
				Helper.info(this.getClass(), "SR: " + sc.getValue("sr"));
				this.writeImage(this.imagePath.resolve("sr.png"), sc.getImage("sr"));
				for (Map.Entry<BufferedImage, Glyph> entry : sc.nohitPrimary.entrySet()) {
					fireExtractionError(entry.getKey(), entry.getValue(), OWMatchExtractionListener.StatType.PRIMARY);
				}
				for (Map.Entry<BufferedImage, Glyph> entry : sc.nohitSecondary.entrySet()) {
					fireExtractionError(entry.getKey(), entry.getValue(), OWMatchExtractionListener.StatType.SECONDARY);
				}
			}
			sc = null;
			// Extract overall-/hero-stats
			Path stats = this.matchPath.resolve("stats");
			if (Files.exists(stats) && Files.isDirectory(stats)) {
				try {
					Files.list(stats).filter(f -> f.toString().endsWith(".png")).forEach(f -> readStats(f));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				Files.write(this.dataPath.resolve(this.matchPath.getFileName().toString() + ".json"),
						this.match.toJson().getBytes("UTF-8"));
				OWLib.getInstance().addMatch(this.match);
				fireMatchExtracted(match);
				Files.createFile(this.matchPath.resolve("extracted"));

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {

				if (OWLib.getInstance().getBoolean("debug.extraction"))
					Helper.debug(this.getClass(), "Error writing match to file: " +e.getMessage());
				e.printStackTrace();
			}

			if (OWLib.getInstance().getBoolean("debug.extraction"))
				Helper.debug(this.getClass(), "Done extracting match: " + matchPath.getFileName().toString());
		}

		private void writeImage(Path imagePath, BufferedImage i) {
			try (OutputStream os = Files.newOutputStream(imagePath)) {
				writePNG(i, os);
				// ImageIO.write(i, "JPG", );
			} catch (IOException e) {
				Helper.info(this.getClass(), "Error writing image: " + imagePath.toAbsolutePath().toString());
				e.printStackTrace();
			}
		}

		public void writePNG(BufferedImage bufferedImage, OutputStream outputStream) throws IOException {
			Iterator<ImageWriter> iterator = ImageIO.getImageWritersByFormatName("png");
			ImageWriter imageWriter = iterator.next();
			ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
			// imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			// imageWriteParam.setCompressionQuality(quality);
			ImageOutputStream imageOutputStream = new MemoryCacheImageOutputStream(outputStream);
			imageWriter.setOutput(imageOutputStream);
			IIOImage iioimage = new IIOImage(bufferedImage, null, null);
			imageWriter.write(null, iioimage, imageWriteParam);
			imageOutputStream.flush();
		}

	}

	/**
	 * Root folder where all matches a saved as raws
	 */
	private Path matchRoot = null;

	/**
	 * Image root where all extracted Images will be saved for qs if some values
	 * are wrong.
	 */
	private Path imageRoot = null;

	/**
	 * Root where all compiled match data will be stored
	 */
	private Path dataRoot = null;

	/**
	 * Daemon that checks for new matches
	 */
	private Thread daemon = null;

	/**
	 * Threadpool that processes new matches
	 */
	private ExecutorService worker = Executors.newSingleThreadExecutor();

	/**
	 * Listern for extraced matches
	 */
	private List<OWMatchExtractionListener> extractionListener = new LinkedList<>();

	/**
	 * queue with new matches
	 */
	private ConcurrentLinkedQueue<OWMatchEvent> events = new ConcurrentLinkedQueue<>();

	/**
	 * Creates a MatchExtractor to extract match information from gathered match
	 * screenshots. Scans for finished matches that have not been finished.
	 * 
	 * @param matchRoot
	 *            Rootfolder where all matches a saved as raws
	 * @param imageRoot
	 *            Image root where all extracted Images will be saved for qs if
	 *            some values are wrong.
	 * @param dataRoot
	 *            Root where all compiled match data will be stored
	 * @throws IOException
	 *             Thrown when {@link #matchRoot} is inaccessible
	 */
	public MatchExtractor(Path matchRoot, Path imageRoot, Path dataRoot) throws IOException {
		this.matchRoot = matchRoot;
		this.imageRoot = imageRoot;
		this.dataRoot = dataRoot;
		Files.list(this.matchRoot).filter(p -> Files.exists(p.resolve("done")) && !Files.exists(p.resolve("extracted"))
				&& !Files.exists(p.resolve("aborted"))).forEach(p -> this.addMatch(p));
		this.daemon = new Thread(this);
		this.daemon.setDaemon(true);
		this.daemon.start();
	}

	public void addExtractionListener(OWMatchExtractionListener extractionListener) {
		this.extractionListener.add(extractionListener);
	}

	/**
	 * Adds a finished match to the processing service;
	 * 
	 * @param matchPath
	 *            path to the match-folder
	 */
	private void addMatch(Path matchPath) {
		try {
			if (OWLib.getInstance().getBoolean("debug.extraction"))
				Helper.debug(this.getClass(), "Adding match to be extraced: " + matchPath.getFileName().toString() + "[" + matchPath.toAbsolutePath().toString() +"]");
			Path imagePath = this.imageRoot.resolve(matchPath.getFileName().toString());
			if (!Files.exists(imagePath))
				Files.createDirectories(imagePath);
			Path dataPath = this.dataRoot;
			if (!Files.exists(dataPath))
				Files.createDirectories(dataPath);
			MatchExtractWorker w = new MatchExtractWorker(matchPath, imagePath, dataPath);
			this.worker.submit(w);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void fireExtractionError(BufferedImage image, Glyph glyph, OWMatchExtractionListener.StatType type) {
		for (OWMatchExtractionListener owMatchExtractionListener : extractionListener) {
			owMatchExtractionListener.extractionError(image, glyph, type);
		}
	}

	private void fireMatchExtracted(OWMatch match) {
		for (OWMatchExtractionListener owMatchExtractionListener : extractionListener) {
			owMatchExtractionListener.matchExtracted(match);
		}
	}

	@Override
	public void matchCompleted(OWMatchEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void matchEnded(OWMatchEvent e) {
		Helper.info(this.getClass(), this.getClass().toString() + " match ended: " + e.id.toString() + ", " + e.item.getItemName()
				+ ", " + e.type);
		if (e.type != OWMatchEvent.Type.END_ABORTED)
			this.events.add(e);
	}

	@Override
	public void matchSrRecorded(OWMatchEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void matchStarted(OWMatchEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void matchStatRecorded(OWMatchEvent e) {
		// TODO Auto-generated method stub

	}

	public void removeExtractionListener(OWMatchExtractionListener extractionListener) {
		this.extractionListener.add(extractionListener);
	}

	@Override
	public void run() {
		for (;;) {
			while (!this.events.isEmpty())
				addMatch(this.events.poll().matchPath);
			try {
				Thread.sleep(OWLib.getInstance().getInteger("captureInterval", 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}

	}
}
