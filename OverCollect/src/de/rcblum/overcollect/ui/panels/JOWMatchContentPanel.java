package de.rcblum.overcollect.ui.panels;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.rcblum.overcollect.data.OWCharacterStats;
import de.rcblum.overcollect.data.OWMatch;
import de.rcblum.overcollect.ui.hero.JHeroStatsPanel;
import de.rcblum.overcollect.ui.utils.UiStatics;

public class JOWMatchContentPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -905919036292700510L;

	private OWMatch match = null;

	JButton btnHeroes;
	private JScrollPane spContent;

	/**
	 * Create the panel.
	 */
	public JOWMatchContentPanel(OWMatch match) {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				JOWMatchContentPanel.this.clicked();
			}
		});
		this.match = match;
		setLayout(new FormLayout(
				new ColumnSpec[] { ColumnSpec.decode("20px"), ColumnSpec.decode("5px"), ColumnSpec.decode("left:20px"),
						ColumnSpec.decode("5px"), ColumnSpec.decode("20px"), ColumnSpec.decode("5px"),
						ColumnSpec.decode("20px"), ColumnSpec.decode("5px"), ColumnSpec.decode("20px:grow"),
						ColumnSpec.decode("5px"), ColumnSpec.decode("20px"), ColumnSpec.decode("5px"),
						ColumnSpec.decode("20px"), ColumnSpec.decode("5px"), ColumnSpec.decode("20px"),
						ColumnSpec.decode("5px"), ColumnSpec.decode("20px"), ColumnSpec.decode("5px"),
						ColumnSpec.decode("20px"), ColumnSpec.decode("5px"), ColumnSpec.decode("20px"),
						ColumnSpec.decode("5px"), ColumnSpec.decode("20px"), ColumnSpec.decode("5px"),
						ColumnSpec.decode("20px"), ColumnSpec.decode("5px"), ColumnSpec.decode("20px"),
						ColumnSpec.decode("5px"), ColumnSpec.decode("20px"), ColumnSpec.decode("5px"),
						ColumnSpec.decode("20px"), ColumnSpec.decode("5px"), ColumnSpec.decode("20px"),
						ColumnSpec.decode("5px"), ColumnSpec.decode("20px"), ColumnSpec.decode("5px"),
						ColumnSpec.decode("20px"), ColumnSpec.decode("5px"), ColumnSpec.decode("20px"),
						ColumnSpec.decode("5px"), ColumnSpec.decode("20px"), ColumnSpec.decode("5px"),
						ColumnSpec.decode("20px"), ColumnSpec.decode("5px"), ColumnSpec.decode("20px:grow"),
						ColumnSpec.decode("5px"), ColumnSpec.decode("20px"), ColumnSpec.decode("5px"),
						ColumnSpec.decode("20px"),
						ColumnSpec.decode("5px"), ColumnSpec.decode("20px"), ColumnSpec.decode("5px"),
						ColumnSpec.decode("20px"), },
				new RowSpec[] { RowSpec.decode("20px"), RowSpec.decode("5px"), RowSpec.decode("20px:grow"),
						RowSpec.decode("5px"), RowSpec.decode("20px"), RowSpec.decode("5px"), RowSpec.decode("20px"),
						RowSpec.decode("5px"), RowSpec.decode("20px"), RowSpec.decode("5px"), RowSpec.decode("20px"),
						RowSpec.decode("5px"), RowSpec.decode("20px"), RowSpec.decode("5px"), RowSpec.decode("20px"),
						RowSpec.decode("5px"), RowSpec.decode("20px"), RowSpec.decode("5px"), RowSpec.decode("20px"),
						RowSpec.decode("5px"), RowSpec.decode("20px"), RowSpec.decode("5px"), RowSpec.decode("20px"),
						RowSpec.decode("5px"), RowSpec.decode("20px"), RowSpec.decode("5px"), RowSpec.decode("20px"),
						RowSpec.decode("5px"), RowSpec.decode("20px"), RowSpec.decode("5px"),
						RowSpec.decode("20px"), }));

		btnHeroes = UiStatics.createButton("Heroes", 2, 0, 0, 2);
		btnHeroes.setFont(btnHeroes.getFont().deriveFont(Font.PLAIN, 18));
		btnHeroes.setFocusPainted(false);
		// btnHeroes.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
		add(btnHeroes, "18, 1, 4, 1");
		setOpaque(false);
		JPanel pContent = new JPanel();
		pContent.setOpaque(false);
		spContent = new PDControlScrollPane();
		spContent.setViewportView(pContent);
		spContent.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		spContent.getVerticalScrollBar().setUnitIncrement(10);
		spContent.setOpaque(false);
		spContent.getViewport().setOpaque(false);
		spContent.setBorder(null);
		pContent.setLayout(new BoxLayout(pContent, BoxLayout.Y_AXIS));
		for (int i = 0; i < match.getCharacterStats().size(); i++) {
			OWCharacterStats stat = match.getCharacterStats().get(i);
			pContent.add(new JHeroStatsPanel(stat, this));

			if (i + 1 < match.getCharacterStats().size())
				pContent.add(Box.createRigidArea(new Dimension(5, 5)));
		}
		add(spContent, "1, 3, 53, 29, fill, fill");

	}

	public void clicked() {
		if (JOWMatchContentPanel.this.getParent() instanceof JOWMatchPanel) {
			((JOWMatchPanel) JOWMatchContentPanel.this.getParent()).clicked();
		}
	}

}
