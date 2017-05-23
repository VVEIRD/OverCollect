package de.rcblum.overcollect.capture.listener;

import java.awt.image.BufferedImage;

/**
 * Interface for Classes to recieve captured images
 * @author rcBlum
 *
 */
public interface ImageListener {

	/**
	 * Recievs a captured image
	 * @param i Image captured
	 */
	public void addImage(BufferedImage i);
}
