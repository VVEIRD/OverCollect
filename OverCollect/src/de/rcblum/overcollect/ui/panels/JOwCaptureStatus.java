package de.rcblum.overcollect.ui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.rcblum.overcollect.capture.listener.ImageListener;
import de.rcblum.overcollect.collect.listener.owmatch.OWMatchEvent;
import de.rcblum.overcollect.collect.listener.owmatch.OWMatchListener;
import de.rcblum.overcollect.configuration.OWItem;
import de.rcblum.overcollect.ui.utils.UiStatics;

public class JOwCaptureStatus extends JPanel implements OWMatchListener, ActionListener, ImageListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5710708290677918754L;

	private static BufferedImage getImageFrom(Path matchPath, OWItem item) throws IOException {
		matchPath = Objects.requireNonNull(matchPath);
		item = Objects.requireNonNull(item);
		BufferedImage img = ImageIO.read(matchPath.resolve(item.getItemName() + ".png").toFile());
		return img;
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		JFrame f = new JFrame("");
		JOwCaptureStatus mapPanel = new JOwCaptureStatus();
		mapPanel.matchStarted(null);
		f.getContentPane().add(mapPanel);
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		Thread.sleep(64_000);
		mapPanel.matchCompleted(null);
		Thread.sleep(5_000);
		mapPanel.matchEnded(null);
	}

	private boolean matchRunning = false;
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
	private JMapPanel pMap;
	private JLabel lblMatchStartet;
	private JLabel lblMatchEndedDesc;
	private JLabel lblMatchEnded;
	private JLabel lblMatchTimeDesc;
	private JLabel lblMatchTime;

	private JLabel lblSrRecorded;

	private JLabel lblHeroStatsRecorded;

	private long startTime = -1;
	private long endTime = -1;

	private Timer t = null;

	private JLabel lblResult;

	/**
	 * Create the panel.
	 */
	public JOwCaptureStatus() {
		this.setPreferredSize(new Dimension(972, 130));
		this.setMinimumSize(new Dimension(800, 130));
		this.setSize(new Dimension(800, 130));
		this.setMaximumSize(new Dimension(5000, 130));
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("220px"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(100px;default)"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(90px;default)"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(100px;default)"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(75px;default)"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(75px;default)"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(30px;default)"),
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("30dlu"),
				FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC,}));

		pMap = new JMapPanel();
		add(pMap, "2, 2, 1, 4, fill, fill");
		setBackground(UiStatics.COLOR_BACKGROUND);
		setForeground(new Color(190, 194, 202));

		JLabel lblMatchStartetDesc = new JLabel("Match started");
		lblMatchStartetDesc.setForeground(UiStatics.TEXT_DESCRIPTION);
		lblMatchStartetDesc.setFont(UiStatics.OW_FONT_NORMAL.deriveFont(Font.PLAIN, 25));
		add(lblMatchStartetDesc, "4, 2, 3, 1");

		lblMatchEndedDesc = new JLabel("Match ended");
		lblMatchEndedDesc.setForeground(UiStatics.TEXT_DESCRIPTION);
		lblMatchEndedDesc.setFont(UiStatics.OW_FONT_NORMAL.deriveFont(Font.PLAIN, 25));
		add(lblMatchEndedDesc, "8, 2, 3, 1");

		lblMatchTimeDesc = new JLabel("Match Time");
		lblMatchTimeDesc.setForeground(UiStatics.TEXT_DESCRIPTION);
		lblMatchTimeDesc.setFont(UiStatics.OW_FONT_NORMAL.deriveFont(Font.PLAIN, 25));
		add(lblMatchTimeDesc, "14, 2, 3, 1");

		lblMatchStartet = new JLabel(" ");
		lblMatchStartet.setFont(UiStatics.OW_FONT_ITALIC.deriveFont(Font.PLAIN, 30));
		lblMatchStartet.setForeground(UiStatics.TEXT_CONTENT); // new Color(190,
																// 194, 202)
		add(lblMatchStartet, "4, 3, 3, 1");

		lblMatchEnded = new JLabel("");
		lblMatchEnded.setFont(UiStatics.OW_FONT_ITALIC.deriveFont(Font.PLAIN, 30));
		lblMatchEnded.setForeground(UiStatics.TEXT_CONTENT); // new Color(190,
																// 194, 202)
		add(lblMatchEnded, "8, 3, 3, 1");

		lblMatchTime = new JLabel("05:54");
		lblMatchTime.setFont(UiStatics.OW_FONT_ITALIC.deriveFont(Font.PLAIN, 30));
		lblMatchTime.setForeground(UiStatics.TEXT_CONTENT); // new Color(190,
															// 194, 202)
		add(lblMatchTime, "14, 3, 3, 1");

		lblSrRecorded = new JLabel(" ");
		lblSrRecorded.setVerticalAlignment(SwingConstants.TOP);
		lblSrRecorded.setFont(UiStatics.OW_FONT_ITALIC.deriveFont(Font.PLAIN, 30));
		lblSrRecorded.setForeground(UiStatics.TEXT_CONTENT); // new Color(190,
																// 194, 202)
		add(lblSrRecorded, "4, 5, 3, 1");

		lblHeroStatsRecorded = new JLabel(" ");
		lblHeroStatsRecorded.setVerticalAlignment(SwingConstants.TOP);
		lblHeroStatsRecorded.setFont(UiStatics.OW_FONT_ITALIC.deriveFont(Font.PLAIN, 30));
		lblHeroStatsRecorded.setForeground(UiStatics.TEXT_CONTENT); // new
																	// Color(190,
																	// 194, 202)
		add(lblHeroStatsRecorded, "8, 5, 6, 1");

		t = new Timer(250, this);

		lblResult = new JLabel("Won");
		lblResult.setVerticalAlignment(SwingConstants.TOP);
		lblResult.setFont(UiStatics.OW_FONT_ITALIC.deriveFont(Font.PLAIN, 30));
		lblResult.setForeground(UiStatics.TEXT_COLOR_DRAW); // new Color(190,
															// 194, 202)
		lblResult.setText(" ");
		add(lblResult, "14, 5");
		t.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.startTime != -1) {
			long curr = this.endTime != -1 ? this.endTime : System.currentTimeMillis();
			long start = this.startTime;
			long minutes = ((curr - start) / (1000 * 60)) % 60;
			long seconds = ((curr - start) / (1000)) % 60;
			String time = String.format("%02d:%02d", minutes, seconds);
			this.lblMatchTime.setText(time);
		} else {
			this.lblMatchTime.setText("");
		}
	}

	@Override
	public void addImage(BufferedImage i) {
		this.pMap.setBackgroundImage(i);
	}

	@Override
	public void matchCompleted(OWMatchEvent e) {
		this.endTime = System.currentTimeMillis();
		lblMatchEnded.setText(sdf.format(new Date(endTime)));
		if (e.item.getItemName().equals("_victory")) {
			this.lblResult.setText("Victory");
			this.lblResult.setForeground(UiStatics.TEXT_COLOR_VICTORY);
		} else if (e.item.getItemName().equals("_defeat")) {
			this.lblResult.setText("Defeat");
			this.lblResult.setForeground(UiStatics.TEXT_COLOR_DEFEAT);
		} else {
			this.lblResult.setText("Draw");
			this.lblResult.setForeground(UiStatics.TEXT_COLOR_DRAW);
		}

	}

	@Override
	public void matchEnded(OWMatchEvent e) {
		matchRunning = false;
		if (this.endTime == -1) {
			this.endTime = System.currentTimeMillis();
			lblMatchEnded.setText(sdf.format(new Date(endTime)));
		}
		this.pMap.setBackgroundImage(null);
		this.pMap.setSecondText("Recording ended");

	}

	@Override
	public void matchSrRecorded(OWMatchEvent e) {
		if (matchRunning)
			lblSrRecorded.setText("SR Recorded");
	}

	@Override
	public void matchStarted(OWMatchEvent e) {
		this.endTime = -1;
		this.pMap.setMap(" ", null);
		this.pMap.setSecondText(" ");
		lblHeroStatsRecorded.setText(" ");
		lblSrRecorded.setText(" ");
		lblResult.setText(" ");
		startTime = System.currentTimeMillis();
		lblMatchStartet.setText(sdf.format(new Date(startTime)));
		lblMatchEnded.setText(" ");
		matchRunning = true;
		this.pMap.setMap(e.item.getItemName().replace("_", " ").trim(), e.screenshot);
	}

	@Override
	public void matchStatRecorded(OWMatchEvent e) {
		System.out.println("Stat ev fired");
		if (matchRunning)
			lblHeroStatsRecorded.setText("Hero Stat recorded");
	}

}
