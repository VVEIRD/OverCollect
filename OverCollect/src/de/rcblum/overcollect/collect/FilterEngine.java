package de.rcblum.overcollect.collect;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.rcblum.overcollect.capture.listener.ImageListener;
import de.rcblum.overcollect.collect.listener.OWItemImageListener;
import de.rcblum.overcollect.configuration.Filter;
import de.rcblum.overcollect.configuration.OWItem;
import de.rcblum.overcollect.configuration.OWLib;

/**
 * FilterEnginge will test all captured screenshots against the configured
 * {@link Filter}s of the {@link OWItem}s of t he appropriate screen resolution.
 * If a hit is detected, the image and {@link OWItem} will be forwarded to its
 * listeners. The processing of the screenshots is done asynchronous by a
 * SingleThreadExecutor. The FilterEngine can be configured to drop duplicate
 * Screenshots by the name of the used {@link OWItem}. This can be done with
 * "dropDuplicateFilter" in lib\owdata\configuration.properties
 * 
 * @author rcBlum
 *
 */
public class FilterEngine implements ImageListener {

	private List<OWItemImageListener> listeners = new LinkedList<>();

	private ExecutorService worker = Executors.newSingleThreadExecutor();

	private boolean dropDuplicateFilter = true;

	private String lastFilter = "";

	public FilterEngine() {
		this.dropDuplicateFilter = OWLib.getInstance().getBoolean("dropDuplicateFilter");
	}

	@Override
	public void addImage(BufferedImage i) {
		if (OWLib.getInstance().supportScreenResolution(i.getWidth(), i.getHeight())) {
			worker.submit(new FilterTask(i));
		} else {
			System.out.println("Image resolution " + i.getWidth() + " x " + i.getHeight() + " not supported");
		}
	}

	/**
	 * Adds a {@link OWItemImageListener}, that will be triggered if a filter is
	 * matched
	 * 
	 * @param listener
	 */
	public void addOWItemImageListener(OWItemImageListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Removes a {@link OWItemImageListener}
	 * 
	 * @param listener
	 */
	public void removeOWItemImageListener(OWItemImageListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * Informs all listeners that a Filter matched a screenshot
	 * 
	 * @param i
	 *            Image that matched the filter
	 * @param item
	 *            {@link OWItem} that contains the filter which matched the
	 *            image.
	 */
	private void fireImage(BufferedImage i, OWItem item) {
		for (OWItemImageListener owItemImageListener : listeners) {
			owItemImageListener.addOWItem(i, item);
		}
	}

	/**
	 * Task tah will check the screenshot against the filters of the appropriate
	 * screen resolution.
	 * 
	 * @author rcBlum
	 *
	 */
	private class FilterTask implements Runnable {

		private final BufferedImage i;

		public FilterTask(BufferedImage i) {
			this.i = i;
		}

		@Override
		public void run() {
			List<OWItem> items = OWLib.getInstance().getItems(i.getWidth(), i.getHeight());
			List<OWItem> dropItems = OWLib.getInstance().getDropItems(i.getWidth(), i.getHeight());
			for (OWItem item : dropItems) {
				if (item.hasFilter() && item.getFilter().match(i)) {
					System.out.println("Dropping screenshot: " + item.getItemName());
					return;
				}
			}
			for (OWItem item : items) {
				if (dropDuplicateFilter && !item.getItemName().equals(lastFilter) || !dropDuplicateFilter) {
					if (item.hasFilter() && item.getFilter().match(i)) {
						fireImage(i, item);
						lastFilter = item.getItemName();
					}
				}
			}
		}

	}

}
