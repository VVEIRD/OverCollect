package de.rcblum.overcollect.extract.listener;

import java.awt.image.BufferedImage;

import de.rcblum.overcollect.data.OWMatch;
import de.rcblum.overcollect.extract.ocr.Glyph;

public interface OWMatchExtractionListener {
	public static enum StatType {
		PRIMARY, SECONDARY;
	}

	public void extractionError(BufferedImage image, Glyph probableGlyph, OWMatchExtractionListener.StatType type);

	public void matchExtracted(OWMatch match);
}
