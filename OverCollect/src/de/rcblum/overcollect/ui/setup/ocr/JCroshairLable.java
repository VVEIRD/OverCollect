package de.rcblum.overcollect.ui.setup.ocr;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.Icon;
import javax.swing.JLabel;

public class JCroshairLable extends JLabel {

	public JCroshairLable() {
		super();
	}

	public JCroshairLable(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
		addListener();
	}

	public JCroshairLable(Icon image) {
		super(image);
		addListener();
	}

	public JCroshairLable(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
		addListener();
	}

	public JCroshairLable(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
		addListener();
	}

	public JCroshairLable(String text) {
		super(text);
		addListener();
	}

	private void addListener() {
		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
				repaint();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
				repaint();
			}
		});
	}

	private int mouseX = -1;
	private int mouseY = -1;

	/**
	 * 
	 */
	private static final long serialVersionUID = -61182742534371331L;

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (mouseX >= 0 && mouseY >= 0) {
			g.setColor(Color.YELLOW);
			g.drawLine(mouseX, 0, mouseX, this.getHeight());
			g.drawLine(0, mouseY, this.getWidth(), mouseY);
		}
	}
}
