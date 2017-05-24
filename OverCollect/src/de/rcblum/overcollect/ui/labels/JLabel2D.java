package de.rcblum.overcollect.ui.labels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

public class JLabel2D extends JLabel {
	public static final int EFFECT_PLAIN = 0;

	public static final int EFFECT_GRADIENT = 1;

	public static final int EFFECT_IMAGE = 2;

	public static final int EFFECT_IMAGE_ANIMATION = 3;

	public static final int EFFECT_COLOR_ANIMATION = 4;

	protected int effectIndex = EFFECT_PLAIN;

	protected double shearFactor = 0.0;

	protected Color outlineColor;

	protected Stroke stroke;

	protected GradientPaint gradient;

	protected Image foregroundImage;

	protected Thread animator;

	protected boolean isRunning = false;

	protected int m_delay;

	protected int m_xShift;

	public JLabel2D() {
		super();
	}

	public JLabel2D(String text) {
		super(text);
	}

	public JLabel2D(String text, int alignment) {
		super(text, alignment);
	}

	public void setEffectIndex(int e) {
		effectIndex = e;
		repaint();
	}

	public int getEffectIndex() {
		return effectIndex;
	}

	public void setShearFactor(double s) {
		shearFactor = s;
		repaint();
	}

	public double getShearFactor() {
		return shearFactor;
	}

	public void setOutlineColor(Color c) {
		outlineColor = c;
		repaint();
	}

	public Color getOutlineColor() {
		return outlineColor;
	}

	public void setStroke(Stroke s) {
		stroke = s;
		repaint();
	}

	public Stroke getStroke() {
		return stroke;
	}

	public void setGradient(GradientPaint g) {
		gradient = g;
		repaint();
	}

	public GradientPaint getGradient() {
		return gradient;
	}

	public void setForegroundImage(Image img) {
		foregroundImage = img;
		repaint();
	}

	public Image getForegroundImage() {
		return foregroundImage;
	}

	public void startAnimation(int delay) {
		if (animator != null)
			return;
		m_delay = delay;
		m_xShift = 0;
		isRunning = true;
		animator = new Thread() {
			double arg = 0;

			public void run() {
				while (isRunning) {
					if (effectIndex == EFFECT_IMAGE_ANIMATION)
						m_xShift += 10;
					else if (effectIndex == EFFECT_COLOR_ANIMATION && gradient != null) {
						arg += Math.PI / 10;
						double cos = Math.cos(arg);
						double f1 = (1 + cos) / 2;
						double f2 = (1 - cos) / 2;
						arg = arg % (Math.PI * 2);

						Color c1 = gradient.getColor1();
						Color c2 = gradient.getColor2();
						int r = (int) (c1.getRed() * f1 + c2.getRed() * f2);
						r = Math.min(Math.max(r, 0), 255);
						int g = (int) (c1.getGreen() * f1 + c2.getGreen() * f2);
						g = Math.min(Math.max(g, 0), 255);
						int b = (int) (c1.getBlue() * f1 + c2.getBlue() * f2);
						b = Math.min(Math.max(b, 0), 255);
						setForeground(new Color(r, g, b));
					}
					repaint();
					try {
						sleep(m_delay);
					} catch (InterruptedException ex) {
						break;
					}
				}
			}
		};
		animator.start();
	}

	public void stopAnimation() {
		isRunning = false;
		animator = null;
	}

	public void paintComponent(Graphics g) {
		Dimension d = getSize();
		Insets ins = getInsets();
		int x = ins.left;
		int y = ins.top;
		int w = d.width - ins.left - ins.right;
		int h = d.height - ins.top - ins.bottom;

		if (isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(0, 0, d.width, d.height);
		}
		paintBorder(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		FontRenderContext frc = g2.getFontRenderContext();
		TextLayout tl = new TextLayout(getText(), getFont(), frc);

		AffineTransform shear = AffineTransform.getShearInstance(shearFactor, 0.0);
		Shape src = tl.getOutline(shear);
		Rectangle rText = src.getBounds();

		float xText = x - rText.x;
		switch (getHorizontalAlignment()) {
		case CENTER:
			xText = x + (w - rText.width) / 2;
			break;
		case RIGHT:
			xText = x + (w - rText.width);
			break;
		}
		float yText = y + h / 2 + tl.getAscent() / 4;

		AffineTransform shift = AffineTransform.getTranslateInstance(xText, yText);
		Shape shp = shift.createTransformedShape(src);

		if (outlineColor != null) {
			g2.setColor(outlineColor);
			if (stroke != null)
				g2.setStroke(stroke);
			g2.draw(shp);
		}

		switch (effectIndex) {
		case EFFECT_GRADIENT:
			if (gradient == null)
				break;
			g2.setPaint(gradient);
			g2.fill(shp);
			break;

		case EFFECT_IMAGE:
			fillByImage(g2, shp, 0);
			break;

		case EFFECT_COLOR_ANIMATION:
			g2.setColor(getForeground());
			g2.fill(shp);
			break;

		case EFFECT_IMAGE_ANIMATION:
			if (foregroundImage == null)
				break;
			int wImg = foregroundImage.getWidth(this);
			if (m_xShift > wImg)
				m_xShift = 0;
			fillByImage(g2, shp, m_xShift - wImg);
			break;

		default:
			g2.setColor(getForeground());
			g2.fill(shp);
			break;
		}
	}

	protected void fillByImage(Graphics2D g2, Shape shape, int xOffset) {
		if (foregroundImage == null)
			return;
		int wImg = foregroundImage.getWidth(this);
		int hImg = foregroundImage.getHeight(this);
		if (wImg <= 0 || hImg <= 0)
			return;
		g2.setClip(shape);
		Rectangle bounds = shape.getBounds();
		for (int xx = bounds.x + xOffset; xx < bounds.x + bounds.width; xx += wImg)
			for (int yy = bounds.y; yy < bounds.y + bounds.height; yy += hImg)
				g2.drawImage(foregroundImage, xx, yy, this);
	}

	public static void main(String argv[]) {
		JFrame f = new JFrame("2D Labels");
		f.setSize(600, 300);
		f.getContentPane().setLayout(new GridLayout(6, 1, 5, 5));
		f.getContentPane().setBackground(Color.white);
		Font bigFont = new Font("Helvetica", Font.BOLD, 24);

		JLabel2D lbl = new JLabel2D("Java Source and Support With Outline", JLabel.CENTER);
		lbl.setFont(bigFont);
		lbl.setForeground(Color.blue);
		lbl.setBorder(new LineBorder(Color.black));
		lbl.setBackground(Color.cyan);
		lbl.setOutlineColor(Color.yellow);
		lbl.setStroke(new BasicStroke(5f));
		lbl.setOpaque(true);
		lbl.setShearFactor(0.3);
		f.getContentPane().add(lbl);

		lbl = new JLabel2D("Java Source and Support With Color Gradient", JLabel.CENTER);
		lbl.setFont(bigFont);
		lbl.setOutlineColor(Color.black);
		lbl.setEffectIndex(JLabel2D.EFFECT_GRADIENT);
		GradientPaint gp = new GradientPaint(0, 0, Color.red, 100, 50, Color.blue, true);
		lbl.setGradient(gp);
		f.getContentPane().add(lbl);

		lbl = new JLabel2D("Java Source and Support Filled With Image", JLabel.CENTER);
		lbl.setFont(bigFont);
		lbl.setEffectIndex(JLabel2D.EFFECT_IMAGE);
		ImageIcon icon = new ImageIcon("mars.gif");
		lbl.setForegroundImage(icon.getImage());
		lbl.setOutlineColor(Color.red);
		f.getContentPane().add(lbl);

		lbl = new JLabel2D("Java Source and Support With Image Animation", JLabel.CENTER);
		lbl.setFont(bigFont);
		lbl.setEffectIndex(JLabel2D.EFFECT_IMAGE_ANIMATION);
		icon = new ImageIcon("ocean.gif");
		lbl.setForegroundImage(icon.getImage());
		lbl.setOutlineColor(Color.black);
		lbl.startAnimation(400);
		f.getContentPane().add(lbl);

		lbl = new JLabel2D("Java Source and Support With Color Animation", JLabel.CENTER);
		lbl.setFont(bigFont);
		lbl.setEffectIndex(JLabel2D.EFFECT_COLOR_ANIMATION);
		lbl.setGradient(gp);
		lbl.setOutlineColor(Color.black);
		lbl.startAnimation(400);
		f.getContentPane().add(lbl);

		JLabel lbl1 = new JLabel("Plain Java Source and Support For Comparison", JLabel.CENTER);
		lbl1.setFont(bigFont);
		lbl1.setForeground(Color.black);
		f.getContentPane().add(lbl1);

		WindowListener wndCloser = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};
		f.addWindowListener(wndCloser);

		f.setVisible(true);

	}

}