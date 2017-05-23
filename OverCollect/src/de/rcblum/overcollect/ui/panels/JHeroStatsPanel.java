package de.rcblum.overcollect.ui.panels;

import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.google.gson.Gson;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import de.rcblum.overcollect.data.OWCharacterStats;
import de.rcblum.overcollect.ui.utils.ImageCache;
import de.rcblum.overcollect.ui.utils.UiStatics;

import com.jgoodies.forms.layout.FormSpecs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;

public class JHeroStatsPanel extends JPanel {

	OWCharacterStats hero = null;
	private JOWMatchContentPanel parent = null;
	private JTextField txtEliminations;
	private JTextField txtObjkills;
	private JTextField txtObjTime;
	private JTextField txtDmgdone;
	private JTextField txtHealing;
	private JTextField txtDeaths;
	/**
	 * Create the panel.
	 */
	public JHeroStatsPanel(OWCharacterStats hero, JOWMatchContentPanel parent) {
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (parent != null) {
					parent.clicked();
				}
			}
		});
		this.hero = hero;
		this.setBorder(null);
		setBackground(new Color(0,0,0,55));
		setOpaque(false);
		setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("15px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("15px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("15px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("15px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("15px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("20px:grow"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("20px:grow"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("20px:grow"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("20px:grow"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("20px:grow"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("20px:grow"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("10px:grow"),
				ColumnSpec.decode("20px:grow"),
				ColumnSpec.decode("10px:grow"),},
			new RowSpec[] {
				RowSpec.decode("1px"),
				RowSpec.decode("4px"),
				RowSpec.decode("20px"),
				RowSpec.decode("5px"),
				RowSpec.decode("30px"),
				RowSpec.decode("5px"),
				RowSpec.decode("15px"),
				RowSpec.decode("5px"),
				RowSpec.decode("20px"),
				RowSpec.decode("5px"),
				RowSpec.decode("10px"),}));

		Properties p = new Properties();
		BufferedImage b = null;
		try {
			p.load(JHeroStatsPanel.class.getResourceAsStream("/resources/potraits.properties"));
			URL url = new URL(p.getProperty(this.hero.getName().replaceAll(" ", "")));
			b = ImageCache.getImage(url);
		} catch (IOException e) {
		}
		ImageIcon image = b != null ? new ImageIcon(b) : null;
		JLabel lblNewLabel = new JLabel(this.hero.getName().equalsIgnoreCase("allheroes") ? "All Heroes" : this.hero.getName());
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setForeground(UiStatics.TEXT_CONTENT);
		if (image != null) {
			lblNewLabel.setText(null);
			lblNewLabel.setIcon(image);
			lblNewLabel.setFont(UiStatics.OW_FONT_ITALIC.deriveFont(Font.PLAIN, 35));
		}
		else
			lblNewLabel.setFont(UiStatics.OW_FONT_ITALIC.deriveFont(Font.PLAIN, 15));
		add(lblNewLabel, "1, 1, 10, 11");
		
		JLabel lblEliminations = new JLabel("Eliminations");
		lblEliminations.setForeground(UiStatics.TEXT_DESCRIPTION);
		lblEliminations.setFont(UiStatics.OW_FONT_NORMAL.deriveFont(Font.PLAIN, 15));
		add(lblEliminations, "12, 3, 5, 1");
		
		JLabel lblObjectiveKills = new JLabel("Objective Kills");
		lblObjectiveKills.setForeground(UiStatics.TEXT_DESCRIPTION);
		lblObjectiveKills.setFont(UiStatics.OW_FONT_NORMAL.deriveFont(Font.PLAIN, 15));
		add(lblObjectiveKills, "18, 3, 5, 1");
		
		JLabel lblObjectiveTime = new JLabel("Objective Time");
		lblObjectiveTime.setForeground(UiStatics.TEXT_DESCRIPTION);
		lblObjectiveTime.setFont(UiStatics.OW_FONT_NORMAL.deriveFont(Font.PLAIN, 15));
		add(lblObjectiveTime, "24, 3, 5, 1");
		
		JLabel lblDamageDone = new JLabel("Damage Done");
		lblDamageDone.setForeground(UiStatics.TEXT_DESCRIPTION);
		lblDamageDone.setFont(UiStatics.OW_FONT_NORMAL.deriveFont(Font.PLAIN, 15));
		add(lblDamageDone, "30, 3, 5, 1");
		
		JLabel lblHealingDone = new JLabel("Healing Done");
		lblHealingDone.setForeground(UiStatics.TEXT_DESCRIPTION);
		lblHealingDone.setFont(UiStatics.OW_FONT_NORMAL.deriveFont(Font.PLAIN, 15));
		add(lblHealingDone, "36, 3, 5, 1");
		
		JLabel lblDeaths = new JLabel("Deaths");
		lblDeaths.setForeground(UiStatics.TEXT_DESCRIPTION);
		lblDeaths.setFont(UiStatics.OW_FONT_NORMAL.deriveFont(Font.PLAIN, 15));
		add(lblDeaths, "42, 3, 5, 1");
		
		txtEliminations = new JTextField();
		txtEliminations.setEditable(false);
		txtEliminations.setFont(UiStatics.OW_FONT_ITALIC);
		txtEliminations.setForeground(UiStatics.TEXT_DESCRIPTION);
		txtEliminations.setDisabledTextColor(UiStatics.TEXT_DESCRIPTION);
		txtEliminations.setBorder(BorderFactory.createEmptyBorder());
		txtEliminations.setOpaque(false);
		add(txtEliminations, "12, 5, 5, 1, fill, top");
		txtEliminations.setColumns(10);
		
		txtObjkills = new JTextField();
		txtObjkills.setEditable(false);
		txtObjkills.setFont(UiStatics.OW_FONT_ITALIC);
		txtObjkills.setForeground(UiStatics.TEXT_DESCRIPTION);
		txtObjkills.setDisabledTextColor(UiStatics.TEXT_DESCRIPTION);
		txtObjkills.setBorder(BorderFactory.createEmptyBorder());
		txtObjkills.setOpaque(false);
		add(txtObjkills, "18, 5, 5, 1, fill, top");
		txtObjkills.setColumns(10);
		
		txtObjTime = new JTextField();
		txtObjTime.setEditable(false);
		txtObjTime.setFont(UiStatics.OW_FONT_ITALIC);
		txtObjTime.setForeground(UiStatics.TEXT_DESCRIPTION);
		txtObjTime.setDisabledTextColor(UiStatics.TEXT_DESCRIPTION);
		txtObjTime.setBorder(BorderFactory.createEmptyBorder());
		txtObjTime.setOpaque(false);
		add(txtObjTime, "24, 5, 5, 1, fill, top");
		txtObjTime.setColumns(10);
		
		txtDmgdone = new JTextField();
		txtDmgdone.setEditable(false);
		txtDmgdone.setFont(UiStatics.OW_FONT_ITALIC);
		txtDmgdone.setForeground(UiStatics.TEXT_DESCRIPTION);
		txtDmgdone.setDisabledTextColor(UiStatics.TEXT_DESCRIPTION);
		txtDmgdone.setBorder(BorderFactory.createEmptyBorder());
		txtDmgdone.setOpaque(false);
		add(txtDmgdone, "30, 5, 5, 1, fill, top");
		txtDmgdone.setColumns(10);
		
		txtHealing = new JTextField();
		txtHealing.setEditable(false);
		txtHealing.setFont(UiStatics.OW_FONT_ITALIC);
		txtHealing.setForeground(UiStatics.TEXT_DESCRIPTION);
		txtHealing.setDisabledTextColor(UiStatics.TEXT_DESCRIPTION);
		txtHealing.setBorder(BorderFactory.createEmptyBorder());
		txtHealing.setOpaque(false);
		add(txtHealing, "36, 5, 5, 1, fill, top");
		txtHealing.setColumns(10);
		
		txtDeaths = new JTextField();
		txtDeaths.setEditable(false);
		txtDeaths.setFont(UiStatics.OW_FONT_ITALIC);
		txtDeaths.setForeground(UiStatics.TEXT_DESCRIPTION);
		txtDeaths.setDisabledTextColor(UiStatics.TEXT_DESCRIPTION);
		txtDeaths.setBorder(BorderFactory.createEmptyBorder());
		txtDeaths.setOpaque(false);
		add(txtDeaths, "42, 5, 5, 1, fill, top");
		txtDeaths.setColumns(10);
		setData();
	}
	
	public void setData()
	{
		this.txtEliminations.setText(""+ this.hero.getEliminations());
		this.txtObjkills.setText(""+ this.hero.getObjectiveKills());
		this.txtObjTime.setText(""+ this.hero.getObjectiveTime());
		this.txtDmgdone.setText(""+ this.hero.getDamageDone());
		this.txtHealing.setText(""+ this.hero.getHealingDone());
		this.txtDeaths.setText(""+ this.hero.getDeaths());
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(new Color(0,0,0,55));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		super.paint(g);
	}
	
	public static void main(String[] args) {
		String json = "{" +
				"      \"name\": \"Reinhardt\"," +
				"      \"secondaryStats\": {" +
				"        \"killStreak\": \"8\"," +
				"        \"fireStrikeKills\": \"9\"," +
				"        \"chargeKills\": \"4\"," +
				"        \"earthshatterKills\": \"5\"," +
				"        \"damageBlocked\": \"26119\"" +
				"      }," +
				"      \"eliminations\": \"39\"," +
				"      \"objectiveKills\": \"17\"," +
				"      \"objectiveTime\": \"0253\"," +
				"      \"damageDone\": \"1513\"," +
				"      \"healingDone\": \"0\"," +
				"      \"deaths\": \"15\" " +
				"    }";
		Gson g = new Gson();
		OWCharacterStats owStats = g.fromJson(json, OWCharacterStats.class);
		JHeroStatsPanel hPanel = new JHeroStatsPanel(owStats, null);
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(hPanel);
		f.pack();
		f.setVisible(true);
	}

}
