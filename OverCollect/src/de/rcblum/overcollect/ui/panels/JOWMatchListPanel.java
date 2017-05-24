package de.rcblum.overcollect.ui.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import de.rcblum.overcollect.configuration.OWLib;
import de.rcblum.overcollect.data.OWMatch;
import de.rcblum.overcollect.extract.listener.OWMatchExtractionListener;
import de.rcblum.overcollect.extract.ocr.Glyph;
import de.rcblum.overcollect.ui.utils.UiStatics;

public class JOWMatchListPanel extends JPanel implements OWMatchExtractionListener {
	List<OWMatch> matches = null;

	int lastSr = -1;

	/**
	 * Create the panel.
	 */
	public JOWMatchListPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(UiStatics.COLOR_BACKGROUND);
		matches = OWLib.getInstance().getMatches();
		matches = matches.stream().sorted((e1, e2) -> e2.getStartTime().compareTo(e1.getStartTime()))
				.collect(Collectors.toList());
		for (int i = 0; i < matches.size(); i++) {
			OWMatch match = matches.get(i);
			if (lastSr == -1)
				lastSr = match.getSr();
			OWMatch prevousMatch = i + 1 < matches.size() ? matches.get(i + 1) : null;
			int previousSr = prevousMatch != null ? prevousMatch.getSr() : match.getSr();
			JOWMatchPanel mp = new JOWMatchPanel(match, previousSr);
			this.add(mp);
		}
	}

	@Override
	public void matchExtracted(OWMatch match) {
		int lastSr = this.lastSr >= 0 ? this.lastSr : match.getSr();
		JOWMatchPanel mp = new JOWMatchPanel(match, lastSr);
		this.lastSr = match.getSr() != -1 ? match.getSr() : this.lastSr;
		Component[] components = this.getComponents();
		this.removeAll();
		this.add(mp);
		for (Component component : components) {
			this.add(component);
		}
		this.revalidate();
		List<OWMatch> newMatches = new ArrayList<>(this.matches.size() + 1);
		newMatches.add(match);
		newMatches.addAll(matches);
		matches = newMatches;
	}

	@Override
	public void extractionError(BufferedImage image, Glyph probableGlyph, OWMatchExtractionListener.StatType type) {
		// TODO Auto-generated method stub
	}

}
