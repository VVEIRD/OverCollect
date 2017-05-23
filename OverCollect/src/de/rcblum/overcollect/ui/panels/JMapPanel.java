package de.rcblum.overcollect.ui.panels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import de.rcblum.overcollect.ui.labels.JLabel2D;
import de.rcblum.overcollect.ui.utils.UiStatics;

public class JMapPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6792531438908056066L;

	private BufferedImage background = null;
	
	private BufferedImage _draw = new BufferedImage(220, 123, BufferedImage.TYPE_INT_ARGB);
	
	private BufferedImage defaultBackground = null;

	private  JLabel2D lblMap = null;
	
	private  JLabel2D lblRecording = null;
	/**
	 * Create the panel.
	 */
	public JMapPanel() {
		setBorder(new LineBorder(Color.DARK_GRAY));
		this.setPreferredSize(new Dimension(220, 123));
		this.setMinimumSize(new Dimension(220, 123));
		this.setMaximumSize(new Dimension(220, 123));
		setLayout(null);
		try {
			defaultBackground = ImageIO.read(JMapPanel.class.getResourceAsStream("/resources/defaultBackground.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lblMap = new JLabel2D(" ");
		lblMap.setHorizontalAlignment(SwingConstants.CENTER);
		Map<TextAttribute, Object> attributes = new HashMap<>();
		attributes.put(TextAttribute.TRACKING, 0.05);
		Font textFont = UiStatics.OW_FONT_ITALIC.deriveFont(Font.PLAIN, 35).deriveFont(attributes);
		lblMap.setFont(textFont);
		lblMap.setForeground(Color.YELLOW);
		//lblMap.setBorder(new LineBorder(Color.black));
		lblMap.setStroke(new BasicStroke(2.5f));

		lblMap.setOutlineColor(Color.BLACK); //new Color(20, 20, 20)
		lblMap.setBounds(0, 11, 220, 43);
		add(lblMap);
		//

		lblRecording = new JLabel2D(" ");
		lblRecording.setHorizontalAlignment(SwingConstants.CENTER);
		lblRecording.setFont(textFont);
		lblRecording.setForeground(Color.YELLOW);
		//lblMap.setBorder(new LineBorder(Color.black));
		lblRecording.setStroke(new BasicStroke(2.5f));

		lblRecording.setOutlineColor(Color.BLACK); //new Color(20, 20, 20)
		lblRecording.setBounds(0, 80, 220, 43);
		add(lblRecording);
	}
	
	@Override
	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		if (background != null || defaultBackground != null) {
			drawBackground((Graphics2D)g, background != null ? background : defaultBackground);
			g.setColor(new Color(200, 200, 200, 70));
			if (background != null )
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
	}
	private void drawBackground(Graphics2D g2d, BufferedImage img) { 
    	Image tmp = img.getScaledInstance(this.getWidth()
    			, (int) (img.getHeight()*((float)this.getWidth())/img.getWidth())
    			, Image.SCALE_SMOOTH);
	    g2d.drawImage(tmp, 0, 0, null);
	}
	
	public void setMap(String name, BufferedImage background)
	{
		this.background = background;// != null ? resize(background, this.getWidth(), (int) (background.getHeight()*((float)this.getWidth())/background.getWidth())) : null;
		Map<TextAttribute, Object> attributes = new HashMap<>();
		attributes.put(TextAttribute.TRACKING, 0.05);
		int fontSize = 35;
		Font textFont = UiStatics.OW_FONT_ITALIC.deriveFont(Font.PLAIN, fontSize).deriveFont(attributes);
		FontMetrics metrics = lblMap.getGraphics().getFontMetrics(textFont);
		while (metrics.stringWidth(name) + 6 > this.getWidth()) {
			textFont = UiStatics.OW_FONT_ITALIC.deriveFont(Font.PLAIN, --fontSize).deriveFont(attributes);
			metrics = lblMap.getGraphics().getFontMetrics(textFont);
		}
		this.lblMap.setFont(textFont);
		this.lblMap.setText(name);
		repaint();
	}

	public void setBackgroundImage(BufferedImage background) 
	{
		this.background = background;// != null ? resize(background, this.getWidth(), (int) (background.getHeight()*((float)this.getWidth())/background.getWidth())) : null;
		repaint();
	}
	
	public void setSecondText(String stext)
	{
		this.lblRecording.setText(stext != null && stext.length() > 0 ? stext : " ");
	}
	
	

	
	public static void main(String[] args) throws IOException, InterruptedException {
		BufferedImage bImg = ImageIO.read(new File("C:\\Java-Projekte\\OverCollect\\lib\\owdata\\1920x1080\\_Volskaya_Industries\\template.png"));
		JFrame f = new JFrame("");
		JMapPanel mapPanel = new JMapPanel();
		f.getContentPane().add(mapPanel);
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		Thread.sleep(5_000);
		mapPanel.setMap("Volskaya Industries", bImg);
		Thread.sleep(5_000);
		bImg = ImageIO.read(new File("C:\\Java-Projekte\\OverCollect\\lib\\owdata\\1920x1080\\_Temple_of_Anubis\\template.png"));
		mapPanel.setMap("Temple of Anubis", bImg);
	}
}
