package de.rcblum.overcollect.collect;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import de.rcblum.overcollect.collect.listener.OWItemImageListener;
import de.rcblum.overcollect.collect.listener.owmatch.OWMatchListener;
import de.rcblum.overcollect.collect.listener.owmatch.OWMatchEvent;
import de.rcblum.overcollect.configuration.Filter;
import de.rcblum.overcollect.configuration.OWItem;
import de.rcblum.overcollect.configuration.OWLib;
import de.rcblum.overcollect.utils.Helper;

/**
 * Composes captured screenshots into an orderly fashion and group them into a
 * match, if possible. Needs to be attached to a source that can deliver to the
 * Interface {@link OWItemImageListener}}
 * 
 * @author Roland von Werden
 * @version 0.2
 */
public class MatchComposer implements OWItemImageListener, Runnable {
	// final static Logger logger = Logger.getLogger(MatchComposer.class);

	/**
	 * 
	 */
	private String matchRoot;

	/**
	 * UUID of a match that is currently recorded.
	 */
	private UUID currentMatch = null;

	/**
	 * Starting date of the match
	 */
	private Date startDate = null;

	private int stacksize = 1;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * Last name of the {@link OWItem} of which the filter has passed on a
	 * screenshot. is used to filter duplicate screenshots.
	 */
	private String lastItem = null;

	/**
	 * List of items that starts a new match
	 */
	private List<String> matchIndicators = null;

	/**
	 * Map that is played on.
	 */
	private String map = null;

	/**
	 * <code>true</code> if the match reached the defeat/victory screen with the
	 * player cards.
	 */
	private boolean gameFinished = false;

	private Map<OWItem, BufferedImage> screenshots = new HashMap<>();

	/**
	 * A maximum of 4 Duplicates per entry allowed
	 */
	private Map<String, Integer> duplicateThreshold = new HashMap<>();

	/**
	 * Asynchronous process that executes the {@link ComposerTask}s
	 */
	private ExecutorService worker = Executors.newSingleThreadExecutor();

	/**
	 * Listeners for the {@link OWMatchEvent}s that are fired by this class
	 */
	private List<OWMatchListener> listeners = new LinkedList<>();

	/**
	 * Lock for concurrent access to the listeners
	 */
	private Object listenerLock = new Object();

	/**
	 * Deamon thread that dispatches all generated events.
	 */
	private Thread dispatcherDeamon = null;

	private ConcurrentLinkedQueue<OWMatchEvent> dispatchQueue = new ConcurrentLinkedQueue<>();

	/**
	 * Create a composer with a default match path. Every match is saved into
	 * its own sub-directory.
	 * 
	 * @param matchPath
	 *            Path where the images will be saved to.
	 */
	public MatchComposer() {
		this(System.getProperties().getProperty("owcollect.match.dir"));
	}

	/**
	 * Create a composer with a specified match path and Library path. Every
	 * match is saved into its own sub-directory.
	 * 
	 * @param matchRoot
	 *            Path where the images will be saved to.
	 * @param libDir
	 *            Path to OWLib configuration files
	 */
	public MatchComposer(String matchRoot) {
		this.matchRoot = Objects.requireNonNull(matchRoot);
		this.matchIndicators = OWLib.getInstance().getMatchIndicators();
		this.dispatcherDeamon = new Thread(this);
		this.dispatcherDeamon.setDaemon(true);
		this.dispatcherDeamon.start();
	}

	@Override
	public void addOWItem(BufferedImage i, OWItem item) {
		Objects.requireNonNull(item);
		Objects.requireNonNull(i);
		this.worker.submit(new ComposerTask(i, item));
	}

	/**
	 * Instances a new match. If there is a previous match taht has not been
	 * closed, it will be by this.
	 * 
	 * @param i
	 *            Screenshot that registered the match-screen which lead to an
	 *            new match
	 * @param item
	 *            OWItem that correspondents with the screenshot through its
	 *            {@link Filter}
	 */
	private void newMatch(BufferedImage i, OWItem item) {
		// End match if one is still active
		this.endMatch(i, item);

		this.currentMatch = UUID.randomUUID();
		this.duplicateThreshold.clear();
		this.screenshots.clear();
		this.stacksize = 1;
		System.out.println("New Match detected: " + this.currentMatch.toString());
		this.startDate = Calendar.getInstance().getTime();
		this.screenshots.put(item, i);
		OWMatchEvent e = new OWMatchEvent(this.currentMatch, this.startDate,
				Paths.get(this.matchRoot, this.currentMatch.toString()), item, OWMatchEvent.Type.NEW_MATCH, i);
		this.fireMatchEndEvent(e);
	}

	/**
	 * Sets the map of the match. if no match is currently active, nothing
	 * happens. If there is already a map, nothing happens.
	 * 
	 * @param i
	 *            Screenshot that registered the match-screen which lead to an
	 *            new match
	 * @param item
	 *            OWItem that correspondents with the screenshot through its
	 *            {@link Filter}
	 */
	private void setMap(BufferedImage i, OWItem item) {
		if (currentMatch != null) {
			System.out.println("Map detected: " + item.getItemName().replace("_", " "));
			this.map = item.getItemName();
			this.screenshots.put(item, i);
		}
	}

	/**
	 * Match end has been detected. this sets up the composer to accept stats.
	 * if no match is currently active, nothing happens.
	 * 
	 * @param i
	 *            Screenshot that registered the match-end (vicory/defeat or
	 *            draw).
	 * @param item
	 *            OWItem that correspondents with the screenshot through its
	 *            {@link Filter}
	 */
	private void addRoundEnd(BufferedImage i, OWItem item) {
		if (currentMatch != null) {
			this.gameFinished = true;
			System.out.println("End of Match reached");
			this.screenshots.put(item, i);
			OWMatchEvent e = new OWMatchEvent(this.currentMatch, this.startDate,
					Paths.get(this.matchRoot, this.currentMatch.toString()), item, OWMatchEvent.Type.MATCH_DECIDED, i);
			this.fireMatchEndEvent(e);
		}
	}

	/**
	 * Stat Screen has been detected and will be saved. if no match is currently
	 * active, nothing happens.
	 * 
	 * @param i
	 *            Screenshot that registered a stat-screen.
	 * @param item
	 *            OWItem that correspondents with the screenshot through its
	 *            {@link Filter}
	 */
	private void addStats(BufferedImage i, OWItem item) {
		if (this.currentMatch != null) {
			System.out.println("..New Stats recorded: " + item.getItemName());
			this.screenshots.put(item, i);
			OWMatchEvent e = new OWMatchEvent(this.currentMatch, this.startDate,
					Paths.get(this.matchRoot, this.currentMatch.toString()), item, OWMatchEvent.Type.STAT_RECORDED, i);
			this.fireMatchEndEvent(e);
		}
	}

	/**
	 * Saves the registered SR-Screen to the match. if no match is currently
	 * active, nothing happens.
	 * 
	 * @param i
	 *            Screenshot that registered the sr-screen.
	 * @param item
	 *            OWItem that correspondents with the screenshot through its
	 *            {@link Filter}
	 */
	private void addSrScreen(BufferedImage i, OWItem item) {
		if (this.currentMatch != null) {
			System.out.println("..SR Info recorded");
			this.screenshots.put(item, i);
			OWMatchEvent e = new OWMatchEvent(this.currentMatch, this.startDate,
					Paths.get(this.matchRoot, this.currentMatch.toString()), item, OWMatchEvent.Type.SR_RECORDED, i);
			this.fireMatchEndEvent(e);
		}
	}

	/**
	 * Ends a currently running Match, fires an OWMatchEvent. if no match is
	 * currently active, nothing happens.
	 * 
	 * @param i
	 *            Screenshot that registered the match-screen which lead to an
	 *            new match
	 * @param item
	 *            OWItem that correspondents with the screenshot through its
	 *            {@link Filter}
	 */
	private void endMatch(BufferedImage i, OWItem item) {
		if (this.currentMatch != null) {
			// create folder & date.properties
			Path folder = Paths.get(this.matchRoot, this.currentMatch.toString());
			Path dataFile = Paths.get(this.matchRoot, this.currentMatch.toString(), "data.properties");
			if (!Files.exists(folder) || !Files.isDirectory(folder)) {
				try {
					Files.createDirectories(folder);
					Files.createDirectories(folder.resolve("stats"));
					write(i, item, folder);
				} catch (IOException e) {
					System.out.println("Error creating match folder: " + folder.toAbsolutePath().toString());
					e.printStackTrace();
					this.currentMatch = null;
					this.gameFinished = false;
					this.map = null;
					this.duplicateThreshold.clear();
					return;
				}
			}
			String propertiesString = "startTime=" + this.sdf.format(this.startDate) + "\r\n" + "endTime="
					+ this.sdf.format(Calendar.getInstance().getTime()) + "\r\n" + "map=" + this.map + "\r\n"
					+ "stacksize=" + this.stacksize;
			writeFile(dataFile, propertiesString);
			// Write BufferedImages primary files
			Path matchPath = Paths.get(this.matchRoot, this.currentMatch.toString());
			Path statPath = Paths.get(this.matchRoot, this.currentMatch.toString(), "stats");
			try {
				if (!Files.exists(matchPath) || !Files.isDirectory(matchPath))
					Files.createDirectories(matchPath);
				if (!Files.exists(statPath) || !Files.isDirectory(statPath))
					Files.createDirectories(statPath);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Set<OWItem> items = this.screenshots.keySet();
			for (OWItem owItem : items) {
				Path writeTo = owItem.isHero() ? statPath : matchPath;
				this.write(this.screenshots.get(owItem), owItem, writeTo);
			}
			Path finaliserFile = Paths.get(this.matchRoot, this.currentMatch.toString(), "done");
			if (!Files.exists(finaliserFile))
				try {
					this.write(i, item, Paths.get(this.matchRoot, this.currentMatch.toString()));
					Files.createFile(finaliserFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			checkMatchAndFireEvent(item);
			this.currentMatch = null;
			this.gameFinished = false;
			this.map = null;
			this.duplicateThreshold.clear();
			System.out.println("Match recording ended");
		}
	}

	/**
	 * Checks if match is complete and fires an OWMatchEvent to its listeners.
	 */
	private void checkMatchAndFireEvent(OWItem item) {
		// Check for completion
		boolean finished = false;
		boolean complete = false;
		try {
			finished = Files.exists(Paths.get(this.matchRoot, this.currentMatch.toString(), this.map + ".png"))
					&& (Files.exists(Paths.get(this.matchRoot, this.currentMatch.toString(), "_victory.png"))
							|| Files.exists(Paths.get(this.matchRoot, this.currentMatch.toString(), "_defeat.png")))
					&& Files.exists(Paths.get(this.matchRoot, this.currentMatch.toString(), "data.properties"));

			complete = Files.exists(Paths.get(this.matchRoot, this.currentMatch.toString(), "_sr_screen.png"))
					&& !isDirEmpty(Paths.get(this.matchRoot, this.currentMatch.toString(), "stats"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!finished) {
			try {
				Files.createFile(Paths.get(this.matchRoot, this.currentMatch.toString(), "aborted"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		if (!complete) {
			try {
				Files.createFile(Paths.get(this.matchRoot, this.currentMatch.toString(), "incomplete"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		OWMatchEvent e = new OWMatchEvent(this.currentMatch, this.startDate,
				Paths.get(this.matchRoot, this.currentMatch.toString()), item,
				finished && complete ? OWMatchEvent.Type.END_NORMAL
						: finished ? OWMatchEvent.Type.END_PARTIAL : OWMatchEvent.Type.END_ABORTED,
				null);
		this.fireMatchEndEvent(e);
	}

	private static boolean isDirEmpty(final Path directory) throws IOException {
		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
			return !dirStream.iterator().hasNext();
		}
	}

	/**
	 * Writes the recognized Screenshot & metadata like Filter, OCRConfiguration
	 * into the match folder.
	 * 
	 * @param i
	 *            Screenshot
	 * @param item
	 *            OWItem that correspondents with the screenshot through its
	 *            {@link Filter}
	 * @param folder
	 *            match folder
	 */
	private void write(BufferedImage i, OWItem item, Path folder) {
		this.writeImage(folder.resolve(item.getItemName() + ".png"), i);
		if (item.hasFilter())
			this.writeFile(folder.resolve(item.getItemName() + ".filter"), item.getFilter().toJson());
		if (item.hasOCRConfiguration())
			this.writeFile(folder.resolve(item.getItemName() + ".ocr"), item.getOCRConfiguration().toJson());
	}

	/**
	 * Writes a json file to the match folder
	 * 
	 * @param filePath
	 *            path to json file within the match folder
	 * @param json
	 *            json string to be saved
	 */
	private void writeFile(Path filePath, String json) {
		try {
			Files.write(filePath, json.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error writing file: " + filePath.toAbsolutePath().toString());
			e.printStackTrace();
		}
	}

	/**
	 * Writes the screenshot into the match folder
	 * 
	 * @param imagePath
	 *            path to image file within the match folder
	 * @param i
	 *            Image to be saved to disk
	 */
	private void writeImage(Path imagePath, BufferedImage i) {
		try (OutputStream os = Files.newOutputStream(imagePath)) {
			ImageIO.write(i, "PNG", os);
			os.close();
		} catch (IOException e) {
			System.out.println("Error writing image: " + imagePath.toAbsolutePath().toString());
			e.printStackTrace();
		}
	}

	/**
	 * Queues a {@link OWMatchEvent} to be fired by the dispatcherDaemon
	 * 
	 * @param e
	 *            Event to be fired
	 */
	private void fireMatchEndEvent(OWMatchEvent e) {
		this.dispatchQueue.add(e);
	}

	/**
	 * @return Returns the start date of the match or null, if no match is
	 *         currently being recorded.
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Returns the match-state of the composer.
	 * 
	 * @return <code>true</code> if a match is being recorded,
	 *         <code>false</code> if not.
	 */
	public boolean isRecording() {
		return this.currentMatch != null;
	}

	/**
	 * @return <code>true</code> if a map was recognized, <code>false</code> if
	 *         not
	 */
	public boolean mapRecognised() {
		return this.map != null;
	}

	/**
	 * @return The name of the current map, if no match is active,
	 *         <code>null</code>;
	 */
	public String getMap() {
		return this.map;
	}

	/**
	 * The UUID as String of the current match. This is also used as the sub
	 * directory in which the match screenshots are written to disk.
	 * 
	 * @return The match UUID or <code>null</code> if no match is active.
	 */
	public String getMatchId() {
		return this.currentMatch != null ? this.currentMatch.toString() : null;
	}

	public void addOWMatchListener(OWMatchListener listener) {
		synchronized (this.listenerLock) {
			System.out.println("OWMatchListener added: " + listener);
			this.listeners.add(listener);
		}
	}

	public void removeOWMatchListener(OWMatchListener listener) {
		synchronized (this.listenerLock) {
			this.listeners.remove(listener);
		}
	}

	@Override
	public void run() {

		for (;;) {
			while (!this.dispatchQueue.isEmpty()) {
				OWMatchEvent e = this.dispatchQueue.poll();
				synchronized (this.listenerLock) {
					for (OWMatchListener listener : this.listeners) {
						switch (e.type) {
						case NEW_MATCH:
							listener.matchStarted(e);
							break;
						case MATCH_DECIDED:
							listener.matchCompleted(e);
							break;
						case SR_RECORDED:
							listener.matchSrRecorded(e);
							break;
						case STAT_RECORDED:
							listener.matchStatRecorded(e);
							break;
						case END_ABORTED:
						case END_NORMAL:
						case END_PARTIAL:
							listener.matchEnded(e);
							break;
						}
					}
				}
			}
			try {
				Thread.sleep(OWLib.getInstance().getInteger("captureInterval", 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	private void addStacksize(BufferedImage i, OWItem item) {
		String stackSize = item.getItemName().substring(1, 2);
		if (Helper.isInteger(stackSize)) {
			int stacksize = Helper.toInteger(stackSize, this.stacksize);
			if (stacksize > this.stacksize)
				this.stacksize = stacksize;
		}
	}

	/**
	 * This Class sorts the received image into the flow of a match, e. g. it
	 * recognises when a Match starts, finishes, when stats come up etc.
	 * 
	 * @author Roland von Werden
	 *
	 */
	public class ComposerTask implements Runnable {
		private final BufferedImage i;

		private final OWItem item;

		public ComposerTask(BufferedImage i, OWItem item) {
			this.i = Objects.requireNonNull(i);
			this.item = Objects.requireNonNull(item);
		}

		@Override
		public void run() {
			try {
				if (!duplicateThreshold.containsKey(item.getItemName()) || duplicateThreshold
						.get(item.getItemName()) < OWLib.getInstance().getInteger("duplicateThreshold", 4)) {
					System.out.println("Filter found: " + item.getItemName());
					if (matchIndicators.contains(item.getItemName()) && !matchIndicators.contains(lastItem))
						newMatch(i, item);
					if (currentMatch != null && item.isMap())
						setMap(i, item);
					if (currentMatch != null && "_sr_screen".equals(item.getItemName()))
						addSrScreen(i, item);
					if (currentMatch != null && ("_defeat".equals(item.getItemName())
							|| "_victory".equals(item.getItemName()) || "_draw".equals(item.getItemName())))
						addRoundEnd(i, item);
					if (currentMatch != null && "_main_menu".equals(item.getItemName()))
						endMatch(i, item);
					if (gameFinished && item.isHero())
						addStats(i, item);
					if (gameFinished && item.getItemName().contains("_stack"))
						addStacksize(i, item);
					duplicateThreshold.put(item.getItemName(), duplicateThreshold.containsKey(item.getItemName())
							? duplicateThreshold.get(item.getItemName()) + 1 : 1);
					lastItem = item.getItemName();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
