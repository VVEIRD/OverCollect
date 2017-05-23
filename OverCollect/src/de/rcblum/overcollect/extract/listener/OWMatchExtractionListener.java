package de.rcblum.overcollect.extract.listener;

import java.awt.image.BufferedImage;

import de.rcblum.overcollect.data.OWMatch;
import de.rcblum.overcollect.extract.ocr.Glyph;

public interface OWMatchExtractionListener 
{
	public void matchExtracted(OWMatch match);
	
	public void extractionError(BufferedImage image, Glyph probableGlyph, OWMatchExtractionListener.StatType type);
	
	
	public static enum StatType {
		PRIMARY, SECONDARY;
	}
}
