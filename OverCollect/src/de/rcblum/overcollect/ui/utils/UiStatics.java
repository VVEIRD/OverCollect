package de.rcblum.overcollect.ui.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class UiStatics {

	public static class OWButton extends JButton {

		/**
		* 
		*/
		private static final long serialVersionUID = 4494248002773780294L;
		private Color hoverBackgroundColor;
		private Color pressedBackgroundColor;

		public OWButton() {
			this(null);
		}

		public OWButton(String text) {
			super(text);
			super.setContentAreaFilled(false);
		}

		public Color getHoverBackgroundColor() {
			return hoverBackgroundColor;
		}

		public Color getPressedBackgroundColor() {
			return pressedBackgroundColor;
		}

		@Override
		protected void paintComponent(Graphics g) {
			if (getModel().isPressed()) {
				g.setColor(pressedBackgroundColor);
			} else if (getModel().isRollover()) {
				g.setColor(hoverBackgroundColor);
			} else {
				g.setColor(getBackground());
			}
			g.fillRect(0, 0, getWidth(), getHeight());
			super.paintComponent(g);
		}

		@Override
		public void setContentAreaFilled(boolean b) {
		}

		@Override
		public void setForeground(Color fg) {
			super.setForeground(fg);
			this.hoverBackgroundColor = fg;
		}

		public void setHoverBackgroundColor(Color hoverBackgroundColor) {
			this.hoverBackgroundColor = hoverBackgroundColor;
		}

		public void setPressedBackgroundColor(Color pressedBackgroundColor) {
			this.pressedBackgroundColor = pressedBackgroundColor;
		}
	}
	public static final Font OW_FONT_NORMAL = getFont("fonts" + File.separator + "koverwatch.ttf",
			"http://us.battle.net/forums/static/fonts/koverwatch/koverwatch.ttf");

	public static final Font OW_FONT_ITALIC = getFont("fonts" + File.separator + "bignoodletoo.ttf",
			"http://us.battle.net/forums/static/fonts/bignoodletoo/bignoodletoo.ttf").deriveFont(Font.PLAIN, 30);

	public static final Color TEXT_CONTENT = new Color(253, 203, 56);

	public static final Color TEXT_DESCRIPTION = new Color(190, 194, 202);

	public static final Color COLOR_BACKGROUND = new Color(21, 33, 63);

	public static final Color BUTTON_COLOR = new Color(148, 90, 25);

	public static final Color BUTTON_COLOR_PRESSED = new Color(112, 67, 19);

	public static final Color TEXT_COLOR_VICTORY = new Color(39, 170, 225);

	public static final Color TEXT_COLOR_DEFEAT = new Color(200, 0, 19);

	public static final Color TEXT_COLOR_DRAW = new Color(255, 220, 0);

	public static JButton createButton(String caption) {
		OWButton btnNewButton = new OWButton(caption);
		btnNewButton.setFont(UiStatics.OW_FONT_NORMAL.deriveFont(Font.PLAIN, 25));
		btnNewButton.setForeground(UiStatics.TEXT_DESCRIPTION);
		btnNewButton.setBackground(UiStatics.BUTTON_COLOR);
		btnNewButton.setHoverBackgroundColor(UiStatics.BUTTON_COLOR);
		btnNewButton.setPressedBackgroundColor(UiStatics.BUTTON_COLOR_PRESSED);
		Border line = new LineBorder(Color.BLACK);
		Border margin = new EmptyBorder(5, 15, 5, 15);
		Border compound = new CompoundBorder(line, margin);
		btnNewButton.setBorder(compound);
		btnNewButton.setMargin(null);
		btnNewButton.setFocusPainted(false);
		return btnNewButton;
	}

	public static JButton createButton(String caption, int top, int left, int bottom, int right) {
		OWButton btnNewButton = new OWButton(caption);
		btnNewButton.setFont(UiStatics.OW_FONT_NORMAL.deriveFont(Font.PLAIN, 25));
		btnNewButton.setForeground(UiStatics.TEXT_DESCRIPTION);
		btnNewButton.setBackground(UiStatics.BUTTON_COLOR);
		btnNewButton.setHoverBackgroundColor(UiStatics.BUTTON_COLOR);
		btnNewButton.setPressedBackgroundColor(UiStatics.BUTTON_COLOR_PRESSED);
		Border line = new LineBorder(UiStatics.BUTTON_COLOR.brighter());
		Border margin = new EmptyBorder(top, left, bottom, right);
		Border compound = new CompoundBorder(line, margin);
		btnNewButton.setBorder(compound);
		btnNewButton.setMargin(null);
		btnNewButton.setFocusPainted(false);
		return btnNewButton;
	}

	private static Font getFont(String localFontFile, String urlString) {
		File fontFolder = Paths.get(localFontFile).getParent().toFile();
		if (!fontFolder.exists() || !fontFolder.isDirectory())
			fontFolder.mkdir();
		File lfont = new File(localFontFile);
		if (!lfont.exists()) {
			System.out.println("Downloading Font " + urlString);
			try (FileOutputStream outputStream = new FileOutputStream(localFontFile)) {
				URL owFontNormal = new URL(urlString);
				ReadableByteChannel rbcFont = Channels.newChannel(owFontNormal.openStream());
				InputStream inputStream = owFontNormal.openConnection().getInputStream();
				// Save fonts
				int bytesRead = -1;
				byte[] buffer = new byte[1024 * 1024];
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				System.out.println("Done downloading Fonts");
			} catch (IOException e1) {
				System.out.println("Error downloading font:");
				e1.printStackTrace();
			}
		}
		if (lfont.exists()) {
			Font f = null;
			try {
				f = Font.createFont(Font.TRUETYPE_FONT, lfont);
			} catch (FontFormatException | IOException e) {
				e.printStackTrace();
			}
			if (f != null)
				return f;
		}
		return new JLabel().getFont();
	}

	public static SimpleDateFormat getUiDateFormat() {
		return new SimpleDateFormat(System.getProperties().getProperty("owcollect.ui.dateformat"));
	}
}
