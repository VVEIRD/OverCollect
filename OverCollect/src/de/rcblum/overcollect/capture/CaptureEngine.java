package de.rcblum.overcollect.capture;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import de.rcblum.overcollect.capture.listener.ImageListener;
import de.rcblum.overcollect.capture.listener.ImageSource;
import de.rcblum.overcollect.configuration.OWItem;
import de.rcblum.overcollect.configuration.OWLib;
import de.rcblum.overcollect.utils.Helper;

public class CaptureEngine implements ActionListener, ImageSource {

	private static GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

	private List<ImageListener> listeners = new ArrayList<>(5);

	private Robot r = null;

	private Timer t = null;

	private GraphicsDevice screen = null;

	private int captureInterval = 1000;

	long screenAutodetectCount = 0;

	public CaptureEngine() throws AWTException {
		this.captureInterval = OWLib.getInstance().getInteger("captureInterval", 1000);
		// this.screen = Objects.requireNonNull(screen);
		// this.r = new Robot(screen);
		System.out.println(this.captureInterval);
		t = new Timer(this.captureInterval, this);
	}

	public CaptureEngine(GraphicsDevice screen) throws AWTException {
		this.screen = Objects.requireNonNull(screen);
		this.captureInterval = OWLib.getInstance().getInteger("captureInterval", 1000);
		this.r = new Robot(screen);
		t = new Timer(this.captureInterval, this);
	}

	public CaptureEngine(int captureInterval) throws AWTException {
		this.captureInterval = captureInterval;
		// this.screen = Objects.requireNonNull(screen);
		// this.r = new Robot(screen);
		t = new Timer(this.captureInterval, this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.screen == null)
			autodetectScreen();
		else {
			BufferedImage br = r.createScreenCapture(this.screen.getDefaultConfiguration().getBounds());
			this.fireImage(br);
			if (OWLib.getInstance().getBoolean("debug.capture")) {
				Path debugFile = Paths.get(OWLib.getInstance().getDebugDir(), "capture", Helper.SDF.format(new Date(System.currentTimeMillis())) + ".png");
				try {
					ImageIO.write(br, "PNG", debugFile.toFile());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public void addImageListener(ImageListener i) {
		listeners.add(i);
	}

	private void autodetectScreen() {
		// Wait for Overwatch to launch
		OWLib lib = OWLib.getInstance();

		// if (screenAutodetectCount%(this.captureInterval/100)==0) {
		// System.out.println("Autodetecting
		// Overwatch["+Math.round(screenAutodetectCount/(this.captureInterval/1000.0))+"
		// sec]...");
		// }
		screenAutodetectCount++;
		Robot[] robots = new Robot[screens.length];
		for (int i = 0; i < robots.length; i++) {
			try {
				robots[i] = new Robot(screens[i]);
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
		for (int i = 0; i < robots.length; i++) {
			Robot robot = robots[i];
			GraphicsDevice screen = screens[i];
			if (robot != null) {
				BufferedImage br = robot.createScreenCapture(screen.getDefaultConfiguration().getBounds());
				if (lib.supportScreenResolution(br.getWidth(), br.getHeight())) {
					OWItem itemStart = lib.getItem(br.getWidth(), br.getHeight(), "_start_screen");
					OWItem itemMain = lib.getItem(br.getWidth(), br.getHeight(), "_main_menu");
					if (itemStart.hasFilter() && itemStart.getFilter().match(br)
							|| itemMain.hasFilter() && itemMain.getFilter().match(br)) {
						this.screen = screen;
						this.r = robot;
						System.out.println("Screen found");
						break;
					}
				}
			}
		}
		// if (isRunning)
		// this.start();
	}

	public void fireImage(BufferedImage i) {
		for (ImageListener imageListener : listeners) {
			imageListener.addImage(i);
		}
	}

	public int getCaptureInterval() {
		return captureInterval;
	}

	public Rectangle getResolution() {
		return this.screen.getDefaultConfiguration().getBounds();
	}

	public GraphicsDevice getScreen() {
		return screen;
	}

	public boolean isRunning() {
		return this.t.isRunning();
	}

	@Override
	public void removeImageListener(ImageListener i) {
		listeners.remove(i);
	}

	public void setCaptureInterval(int captureInterval) {
		this.captureInterval = captureInterval;
	}

	public void setScreen(GraphicsDevice screen) throws AWTException {
		boolean isRunning = this.t.isRunning();
		if (isRunning)
			this.stop();
		this.screen = Objects.requireNonNull(screen);
		this.r = new Robot(this.screen);
		if (isRunning)
			this.start();
	}

	public void start() {
		System.out.println("Start capture");
		this.t.start();
	}

	public void stop() {
		System.out.println("Stop capture");
		this.t.stop();
	}

}
