package de.rcblum.overcollect.collect.listener.owmatch;

public interface OWMatchListener {
	public void matchStarted(OWMatchEvent e);

	public void matchCompleted(OWMatchEvent e);

	public void matchStatRecorded(OWMatchEvent e);

	public void matchSrRecorded(OWMatchEvent e);

	public void matchEnded(OWMatchEvent e);
}
