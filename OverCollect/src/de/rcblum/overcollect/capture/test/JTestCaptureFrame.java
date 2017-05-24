package de.rcblum.overcollect.capture.test;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import de.rcblum.overcollect.capture.CaptureEngine;
import de.rcblum.overcollect.capture.listener.ImageListener;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import java.awt.Component;
import java.awt.Dimension;

public class JTestCaptureFrame extends JFrame implements ImageListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6380383693767869767L;

	private JPanel contentPane;
	private JLabel pImage;
	private JComboBox<GraphicsDevice> cboxDevices;

	private CaptureEngine engine = null;
	private JCheckBox chckbxPreview;
	private JPanel panel;
	private JPanel panel_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JTestCaptureFrame frame = new JTestCaptureFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @throws AWTException
	 */
	public JTestCaptureFrame() throws AWTException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		pImage = new JLabel();
		contentPane.add(pImage, BorderLayout.CENTER);
		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

		panel = new JPanel();
		contentPane.add(panel, BorderLayout.WEST);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		cboxDevices = new JComboBox<>(devices);
		cboxDevices.setPreferredSize(new Dimension(200, 20));
		cboxDevices.setMaximumSize(new Dimension(200, 20));
		cboxDevices.setMinimumSize(new Dimension(200, 20));
		cboxDevices.setAlignmentY(Component.TOP_ALIGNMENT);
		panel.add(cboxDevices);
		cboxDevices.setSelectedItem(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
		this.engine = new CaptureEngine((GraphicsDevice) cboxDevices.getSelectedItem());
		cboxDevices.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (cboxDevices.getSelectedItem() != null)
						engine.setScreen((GraphicsDevice) cboxDevices.getSelectedItem());
				} catch (AWTException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		chckbxPreview = new JCheckBox("Preview");
		chckbxPreview.setAlignmentX(Component.RIGHT_ALIGNMENT);
		chckbxPreview.setAlignmentY(Component.TOP_ALIGNMENT);
		chckbxPreview.setSelected(true);
		panel.add(chckbxPreview);

		panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.add(panel_1);
		this.engine.addImageListener(this);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				engine.start();
			}
		});

	}

	@Override
	public void addImage(BufferedImage i) {
		if (chckbxPreview.isSelected())
			this.pImage.setIcon(new ImageIcon(this.getScaledImage(i, this.pImage.getWidth(), this.pImage.getHeight())));
		else
			this.pImage.setIcon(null);
	}

	private Image getScaledImage(Image srcImg, int w, int h) {
		float srcW = srcImg.getWidth(null);
		int srcH = srcImg.getHeight(null);
		if (srcW / srcH > w / (float) h) {
			h = (int) (w / (srcW / srcH));
		} else {
			w = (int) (h * (srcW / srcH));
		}
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, w, h, null);
		g2.dispose();

		return resizedImg;
	}

}
