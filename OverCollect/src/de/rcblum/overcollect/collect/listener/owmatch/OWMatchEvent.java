package de.rcblum.overcollect.collect.listener.owmatch;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import de.rcblum.overcollect.configuration.OWItem;

/**
 * Event that is triggered when a match recording is completed. Contains
 * information on where the raw match data can be found, the UUID of the match
 * and if the recording finished completely, partially or was aborted.
 * 
 * @author Roland von Werden
 *
 */
public class OWMatchEvent {

	public static enum Type {
		NEW_MATCH, SR_RECORDED, STAT_RECORDED, MATCH_DECIDED, END_NORMAL, END_PARTIAL, END_ABORTED;
	}

	/**
	 * UUID of the match
	 */
	public final UUID id;

	public final Date startTime;

	/**
	 * Folder in which the raw data of the match was saved to
	 */
	public final Path matchPath;

	/**
	 * OWItem that triggered the event.
	 */
	public final OWItem item;

	/**
	 * completion status of the recording
	 */
	public final OWMatchEvent.Type type;

	public final BufferedImage screenshot;

	public OWMatchEvent(UUID id, Date startTime, Path matchPath, OWItem item, OWMatchEvent.Type type,
			BufferedImage screenshot) {
		super();
		this.id = Objects.requireNonNull(id);
		this.startTime = Objects.requireNonNull(startTime);
		this.matchPath = Objects.requireNonNull(matchPath);
		this.item = Objects.requireNonNull(item);
		this.type = Objects.requireNonNull(type);
		this.screenshot = screenshot;
	}
}
