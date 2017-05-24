package de.rcblum.overcollect.collect.listener.owmatch;

public interface OWMatchListener {
	public void matchCompleted(OWMatchEvent e);

	public void matchEnded(OWMatchEvent e);

	public void matchSrRecorded(OWMatchEvent e);

	public void matchStarted(OWMatchEvent e);

	public void matchStatRecorded(OWMatchEvent e);
}
