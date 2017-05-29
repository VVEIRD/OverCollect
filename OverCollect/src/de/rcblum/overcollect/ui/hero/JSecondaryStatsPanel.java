package de.rcblum.overcollect.ui.hero;

import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import de.rcblum.overcollect.ui.utils.UiStatics;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;

public class JSecondaryStatsPanel extends JPanel {
	private JLabel lblSecondaryStat4;
	private JLabel lblSecondaryStat1;
	private JLabel lblSecondaryStat2;
	private JLabel lblSecondaryStat3;
	private JLabel lblSecondaryStat6;
	private JLabel lblSecondaryStat5;
	private JLabel lblSecSValue1;
	private JLabel lblSecSValue2;
	private JLabel lblSecSValue3;
	private JLabel lblSecSValue4;
	private JLabel lblSecSValue5;
	private JLabel lblSecSValue6;

	/**
	 * Create the panel.
	 */
	public JSecondaryStatsPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("100px"),
				ColumnSpec.decode("1px:grow"),
				ColumnSpec.decode("100px"),
				ColumnSpec.decode("1px:grow"),
				ColumnSpec.decode("100px"),
				ColumnSpec.decode("1px:grow"),
				ColumnSpec.decode("100px"),
				ColumnSpec.decode("1px:grow"),
				ColumnSpec.decode("100px"),
				ColumnSpec.decode("1px:grow"),
				ColumnSpec.decode("100px"),},
			new RowSpec[] {
				RowSpec.decode("bottom:15px"),
				RowSpec.decode("25px"),}));
		setBackground(UiStatics.COLOR_BACKGROUND);
		lblSecondaryStat1 = new JLabel("");
		add(lblSecondaryStat1, "1, 1, default, bottom");
		
		lblSecondaryStat2 = new JLabel("");
		add(lblSecondaryStat2, "3, 1");
		
		lblSecondaryStat3 = new JLabel("");
		add(lblSecondaryStat3, "5, 1");
		
		lblSecondaryStat4 = new JLabel("");
		add(lblSecondaryStat4, "7, 1");
		
		lblSecondaryStat5 = new JLabel("");
		add(lblSecondaryStat5, "9, 1");
		
		lblSecondaryStat6 = new JLabel(" ");
		add(lblSecondaryStat6, "11, 1");
		
		lblSecSValue1 = new JLabel("");
		add(lblSecSValue1, "1, 2");
		
		lblSecSValue2 = new JLabel("");
		add(lblSecSValue2, "3, 2");
		
		lblSecSValue3 = new JLabel("");
		add(lblSecSValue3, "5, 2");
		
		lblSecSValue4 = new JLabel("");
		add(lblSecSValue4, "7, 2");
		
		lblSecSValue5 = new JLabel("");
		add(lblSecSValue5, "9, 2");
		
		lblSecSValue6 = new JLabel("");
		add(lblSecSValue6, "11, 2");

		setupDescLable(lblSecondaryStat1);
		setupDescLable(lblSecondaryStat2);
		setupDescLable(lblSecondaryStat3);
		setupDescLable(lblSecondaryStat4);
		setupDescLable(lblSecondaryStat5);
		setupDescLable(lblSecondaryStat6);

		setupValueLable(lblSecSValue1);
		setupValueLable(lblSecSValue2);
		setupValueLable(lblSecSValue3);
		setupValueLable(lblSecSValue4);
		setupValueLable(lblSecSValue5);
		setupValueLable(lblSecSValue6);
	}
	
	private void setupDescLable(JLabel label) {
		label.setVerticalAlignment(SwingConstants.BOTTOM);
		label.setHorizontalAlignment(SwingConstants.LEFT);
		label.setForeground(UiStatics.TEXT_DESCRIPTION);
		label.setFont(UiStatics.OW_FONT_NORMAL.deriveFont(Font.PLAIN, 15));
	}
	
	private void setupValueLable(JLabel label) {
		label.setVerticalAlignment(SwingConstants.TOP);
		label.setHorizontalAlignment(SwingConstants.LEFT);
		label.setForeground(UiStatics.TEXT_DESCRIPTION);
		label.setFont(UiStatics.OW_FONT_ITALIC.deriveFont(Font.PLAIN, 25));
	}
	
	public void setSecondaryStatLabel1(String secStat) {
		this.lblSecondaryStat1.setText(secStat);
	}
	
	public void setSecondaryStatValue1(String secStat) {
		this.lblSecSValue1.setText(secStat);
	}
	
	public void setSecondaryStatLabel2(String secStat) {
		this.lblSecondaryStat2.setText(secStat);
	}
	
	public void setSecondaryStatValue2(String secStat) {
		this.lblSecSValue2.setText(secStat);
	}
	
	public void setSecondaryStatLabel3(String secStat) {
		this.lblSecondaryStat3.setText(secStat);
	}
	
	public void setSecondaryStatValue3(String secStat) {
		this.lblSecSValue3.setText(secStat);
	}
	
	public void setSecondaryStatLabel4(String secStat) {
		this.lblSecondaryStat4.setText(secStat);
	}
	
	public void setSecondaryStatValue4(String secStat) {
		this.lblSecSValue4.setText(secStat);
	}
	
	public void setSecondaryStatLabel5(String secStat) {
		this.lblSecondaryStat5.setText(secStat);
	}
	
	public void setSecondaryStatValue5(String secStat) {
		this.lblSecSValue5.setText(secStat);
	}
	
	public void setSecondaryStatLabel6(String secStat) {
		this.lblSecondaryStat6.setText(secStat);
	}
	
	public void setSecondaryStatValue6(String secStat) {
		this.lblSecSValue6.setText(secStat);
	}

}
