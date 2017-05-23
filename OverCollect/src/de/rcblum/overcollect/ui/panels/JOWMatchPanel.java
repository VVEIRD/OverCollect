package de.rcblum.overcollect.ui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import de.rcblum.overcollect.configuration.OWLib;
import de.rcblum.overcollect.data.OWMatch;
import de.rcblum.overcollect.ui.animation.Animation;
import de.rcblum.overcollect.ui.animation.IAnimatable;
import de.rcblum.overcollect.ui.utils.ImageCache;
import de.rcblum.overcollect.ui.utils.UiStatics;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.JLabel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingConstants;
import javax.swing.JButton;

public class JOWMatchPanel extends JPanel implements IAnimatable
{
	private OWMatch match = null;
	
	private int lastSr = -1;

	private int minHeight = 125;
	private int maxHeight = 525;
	
	private int currentHeight = minHeight;
	
	private boolean expanding = false;
	
	private Animation animation = null;
	
	private BufferedImage background = null;
	private JLabel lblMapName;
	private JLabel lblSrDesc;
	private JLabel lblSr;
	private JLabel lblTeamSrDesc;
	private JLabel lblEnemySrDesc;
	private JLabel lblTeamSr;
	private JLabel lblEnemySr;
	private JLabel lblMatchdurationDesc;
	private JLabel lblMatchduration;
	private JLabel lblDate;
	private JLabel lblResult;
	private JLabel lblSrDiff;
	private JButton btnHeroes;
	private JOWMatchContentPanel pContent;
	
	/**
	 * Create the panel.
	 */
	public JOWMatchPanel(OWMatch match, int lastSr) 
	{
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				expanding = !expanding;
			}
		});
		this.match = match;
		this.lastSr = lastSr < 0 ? this.match.getSr() : lastSr;
		//setSize(new Dimension(750, 125));
		setBackground(UiStatics.COLOR_BACKGROUND);
		setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("right:20px:grow"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("right:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),
				ColumnSpec.decode("left:20px"),
				ColumnSpec.decode("left:5px"),},
			new RowSpec[] {
				RowSpec.decode("top:5px"),
				RowSpec.decode("top:20px"),
				RowSpec.decode("top:5px"),
				RowSpec.decode("top:20px"),
				RowSpec.decode("top:5px"),
				RowSpec.decode("top:20px"),
				RowSpec.decode("top:20px"),
				RowSpec.decode("top:5px"),
				RowSpec.decode("top:20px"),
				RowSpec.decode("top:5px"),
				RowSpec.decode("top:20px:grow"),
				RowSpec.decode("top:5px"),
				RowSpec.decode("top:20px"),
				RowSpec.decode("top:5px"),
				RowSpec.decode("top:20px"),
				RowSpec.decode("top:5px"),
				RowSpec.decode("top:20px"),
				RowSpec.decode("top:5px"),
				RowSpec.decode("top:20px"),
				RowSpec.decode("top:5px"),
				RowSpec.decode("top:20px"),
				RowSpec.decode("top:5px"),
				RowSpec.decode("top:20px"),
				RowSpec.decode("top:5px"),
				RowSpec.decode("top:20px"),
				RowSpec.decode("top:5px"),
				RowSpec.decode("top:20px"),
				RowSpec.decode("top:5px"),
				RowSpec.decode("top:20px"),
				RowSpec.decode("top:5px"),
				RowSpec.decode("top:20px"),
				RowSpec.decode("top:5px"),
				RowSpec.decode("top:20px"),
				RowSpec.decode("top:5px"),
				RowSpec.decode("top:20px"),
				RowSpec.decode("top:5px"),
				RowSpec.decode("top:20px"),
				RowSpec.decode("top:5px"),
				RowSpec.decode("top:20px"),
				RowSpec.decode("top:5px"),
				RowSpec.decode("top:20px"),
				RowSpec.decode("top:5px"),}));
		
		lblMapName = new JLabel(this.match.getMap() + " ");
		lblMapName.setForeground(UiStatics.TEXT_CONTENT);
		lblMapName.setFont(UiStatics.OW_FONT_ITALIC.deriveFont(Font.PLAIN, 50));
		add(lblMapName, "2, 2, 39, 3");
		
		lblSr = new JLabel("" );
		lblSr.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSr.setForeground(UiStatics.TEXT_CONTENT);
		lblSr.setFont(UiStatics.OW_FONT_ITALIC.deriveFont(Font.PLAIN, 50));
		add(lblSr, "42, 2, 11, 3, right, default");
		
		lblSrDesc = new JLabel("SR ");
		lblSrDesc.setVerticalAlignment(SwingConstants.TOP);
		lblSrDesc.setHorizontalAlignment(SwingConstants.LEFT);
		lblSrDesc.setForeground(UiStatics.TEXT_DESCRIPTION);
		lblSrDesc.setFont(UiStatics.OW_FONT_NORMAL.deriveFont(Font.PLAIN, 25));
		add(lblSrDesc, "54, 2, 3, 2, default, top");
		
		lblSrDiff = new JLabel(" ");
		lblSrDiff.setVerticalAlignment(SwingConstants.TOP);
		lblSrDiff.setHorizontalAlignment(SwingConstants.LEFT);
		lblSrDiff.setFont(UiStatics.OW_FONT_NORMAL.deriveFont(Font.PLAIN, 20));
		if (this.lastSr >= 0) {
			if (this.match.getSr() - this.lastSr > 0) {
				lblSrDiff.setText("+" + String.valueOf(this.match.getSr() - this.lastSr) + " ");
				lblSrDiff.setForeground(UiStatics.TEXT_COLOR_VICTORY);
			}
			else if (this.match.getSr() - this.lastSr < 0) {
				lblSrDiff.setText(String.valueOf(this.match.getSr() - this.lastSr) + " ");
				lblSrDiff.setForeground(UiStatics.TEXT_COLOR_DEFEAT);
			}
			else {
				lblSrDiff.setText("   -");
				lblSrDiff.setForeground(UiStatics.TEXT_COLOR_DRAW);
			}
		}
		
		add(lblSrDiff, "53, 4, 3, 1");
		
		lblTeamSrDesc = new JLabel("Team SR ");
		lblTeamSrDesc.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTeamSrDesc.setForeground(UiStatics.TEXT_DESCRIPTION);
		lblTeamSrDesc.setFont(UiStatics.OW_FONT_NORMAL.deriveFont(Font.PLAIN, 15));
		add(lblTeamSrDesc, "2, 6, 7, 1, default, bottom");
		
		lblEnemySrDesc = new JLabel("Enemy SR ");
		lblEnemySrDesc.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEnemySrDesc.setForeground(UiStatics.TEXT_DESCRIPTION);
		lblEnemySrDesc.setFont(UiStatics.OW_FONT_NORMAL.deriveFont(Font.PLAIN, 15));
		add(lblEnemySrDesc, "10, 6, 7, 1, default, bottom");
		
		lblTeamSr = new JLabel(" ");
		lblTeamSr.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTeamSr.setForeground(UiStatics.TEXT_CONTENT);
		lblTeamSr.setFont(UiStatics.OW_FONT_ITALIC.deriveFont(Font.PLAIN, 35));
		add(lblTeamSr, "2, 7, 7, 3, default, top");
		
		lblEnemySr = new JLabel(" ");
		lblEnemySr.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEnemySr.setForeground(UiStatics.TEXT_CONTENT);
		lblEnemySr.setFont(UiStatics.OW_FONT_ITALIC.deriveFont(Font.PLAIN, 35));
		add(lblEnemySr, "10, 7, 7, 3, default, top");
		
		lblMatchdurationDesc = new JLabel("Matchduration ");
		lblMatchdurationDesc.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMatchdurationDesc.setForeground(UiStatics.TEXT_DESCRIPTION);
		lblMatchdurationDesc.setFont(UiStatics.OW_FONT_NORMAL.deriveFont(Font.PLAIN, 15));
		add(lblMatchdurationDesc, "20, 6, 7, 1, default, bottom");
		
		lblMatchduration = new JLabel("");
		lblMatchduration.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMatchduration.setForeground(UiStatics.TEXT_CONTENT);
		lblMatchduration.setFont(UiStatics.OW_FONT_ITALIC.deriveFont(Font.PLAIN, 35));
		add(lblMatchduration, "20, 7, 7, 3, default, top");
		
		lblResult = new JLabel(" ");
		lblResult.setHorizontalAlignment(SwingConstants.RIGHT);
		lblResult.setFont(UiStatics.OW_FONT_ITALIC.deriveFont(Font.PLAIN, 35));
		add(lblResult, "30, 7, 7, 3");
		
		lblDate = new JLabel("");
		lblDate.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDate.setForeground(UiStatics.TEXT_DESCRIPTION);
		lblDate.setFont(UiStatics.OW_FONT_ITALIC.deriveFont(Font.PLAIN, 35));
		add(lblDate, "41, 7, 14, 3, right, top");
		this.setData();
		this.animation = new Animation(this);
		
		pContent = new JOWMatchContentPanel(this.match);
		pContent.setOpaque(false);
		add(pContent, "2, 11, 55, 31, fill, fill");
		
		this.animation.start();
	}
	
	private void setData()
	{
		this.background = ImageCache.getImageFromResource("/resources/ui/background/" +  this.match.getMap().replaceAll("_", "").trim() + ".png");
		// try loading the default Image
		if (this.background == null) {
			this.background = ImageCache.getImageFromResource("/resources/ui/background/default.png");
		}
		this.lblSr.setText(this.match.getSr() != -1 ? this.match.getSr() + " " : "N/A ");
		this.lblTeamSr.setText(this.match.getTeamSr() + " ");
		this.lblEnemySr.setText(this.match.getEnemySr() + " ");
		this.lblDate.setText(this.match.getStartTime() != null ?UiStatics.getUiDateFormat().format(this.match.getStartTime()) + " " : "--");
		// Match result
		if (this.match.isVictory()){
			lblResult.setForeground(UiStatics.TEXT_COLOR_VICTORY);
			lblResult.setText("VICTORY ");
		}
		else if (this.match.isDefeat()) {
			lblResult.setForeground(UiStatics.TEXT_COLOR_DEFEAT);
			lblResult.setText("DEFEAT ");
		}
		else if (this.match.isDraw()) {
			lblResult.setForeground(UiStatics.TEXT_COLOR_DRAW);
			lblResult.setText("DRAW ");
		}
		// Match duration
		long curr = System.currentTimeMillis();
		Date endDate = this.match.getEndTime();
		Date startDate = this.match.getStartTime();
		long end = endDate != null ? endDate.getTime() : curr;
		long start = startDate != null ? startDate.getTime() : curr;
		long minutes = ((end-start)/(1000*60))%60;
		long seconds = ((end-start)/(1000))%60;
		String time = String.format("%02d:%02d ", minutes, seconds);
		this.lblMatchduration.setText(time);
	}
	
	@Override
	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		drawBackground((Graphics2D)g, background);
	}
	private void drawBackground(Graphics2D g2d, BufferedImage img) { 
		if (background != null)
			g2d.drawImage(background, 0, 0, null);
	}
	
	public void clicked()
	{
		expanding = !expanding;
	}
	
	@Override
	public Dimension getPreferredSize() 
	{
		Dimension d = super.getPreferredSize();
		d.setSize(d.getWidth(), this.currentHeight);
		return d;
	}
	
	@Override
	public Dimension getMaximumSize() 
	{
		Dimension d = super.getPreferredSize();
		d.setSize(2500, this.currentHeight);
		return d;
	}

	@Override
	public Dimension getMinimumSize()
	{
		Dimension d = super.getPreferredSize();
		d.setSize(d.getWidth(), this.minHeight);
		return d;
	}
	
	/* (non-Javadoc)
	 * @see de.rcblum.overcollect.ui.panels.Animatable#getMinValue()
	 */
	@Override
	public int getMinValue() 
	{
		return minHeight;
	}
	
	/* (non-Javadoc)
	 * @see de.rcblum.overcollect.ui.panels.Animatable#getMaxValue()
	 */
	@Override
	public int getMaxValue() 
	{
		return maxHeight;
	}
	
	/* (non-Javadoc)
	 * @see de.rcblum.overcollect.ui.panels.Animatable#setValue(int)
	 */
	@Override
	public void setValue(int v) 
	{
		//setSize(new Dimension(750, v));
		this.currentHeight = v;
		this.revalidate();
		this.repaint();
	}
	
	/* (non-Javadoc)
	 * @see de.rcblum.overcollect.ui.panels.Animatable#getCurrentValue()
	 */
	@Override
	public int getCurrentValue() 
	{
		return currentHeight;
	}
	
	/* (non-Javadoc)
	 * @see de.rcblum.overcollect.ui.panels.Animatable#isExpanding()
	 */
	@Override
	public boolean isExpanding() 
	{
		return expanding;
	}

	
	public static void main(String[] args) {
		JFrame f = new JFrame();
		OWMatch match = OWLib.getInstance().getMatch("d56c1237-9f17-4635-9229-b0cacb8289f9");
		JOWMatchPanel matchPanel = new JOWMatchPanel(match, -1);
		f.getContentPane().setLayout(new BoxLayout(f.getContentPane(), BoxLayout.Y_AXIS));
		f.getContentPane().add(matchPanel);
		f.getContentPane().add(new JOWMatchPanel(match, -1));
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}

}
