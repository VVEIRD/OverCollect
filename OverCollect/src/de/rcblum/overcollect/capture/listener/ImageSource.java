package de.rcblum.overcollect.capture.listener;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;

public interface ImageSource {

	void addImageListener(ImageListener i);

	int getCaptureInterval();

	Rectangle getResolution();

	GraphicsDevice getScreen();

	boolean isRunning();

	void removeImageListener(ImageListener i);

	void setCaptureInterval(int captureInterval);

	void setScreen(GraphicsDevice screen) throws AWTException;

	void start();

	void stop();

}