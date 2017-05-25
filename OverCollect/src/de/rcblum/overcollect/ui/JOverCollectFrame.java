package de.rcblum.overcollect.ui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import de.rcblum.overcollect.OverWatchCollectorApp;
import de.rcblum.overcollect.configuration.OWLib;
import de.rcblum.overcollect.ui.panels.JOWMatchListPanel;
import de.rcblum.overcollect.ui.panels.JOWSidebar;
import de.rcblum.overcollect.ui.panels.JOwCaptureStatus;
import de.rcblum.overcollect.ui.utils.UiStatics;

public class JOverCollectFrame extends JFrame {

	private OverWatchCollectorApp app = null;

	private JPanel contentPane;
	private JOwCaptureStatus pCaptureStatus;
	private JOWMatchListPanel pMatches;
	private JPanel pSidebar;
	JScrollPane scrollPane;

	/**
	 * Create the frame.
	 */
	public JOverCollectFrame(OverWatchCollectorApp app) {
		this.app = app;
		if (OWLib.getInstance().getAccounts().size() == 0) {
			String accountName = this.showAccountCreation();
			OWLib.getInstance().setActiveAccount(accountName);
		}
		setBackground(UiStatics.COLOR_BACKGROUND);
		getContentPane().setBackground(UiStatics.COLOR_BACKGROUND);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 570);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		pCaptureStatus = new JOwCaptureStatus();
		contentPane.add(pCaptureStatus, BorderLayout.SOUTH);

		scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(null);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		pMatches = new JOWMatchListPanel();
		scrollPane.setViewportView(pMatches);
		app.addOWMatchListener(pCaptureStatus);
		app.addOWMatchExtractionListener(pMatches);

		pSidebar = new JOWSidebar(app);
		contentPane.add(pSidebar, BorderLayout.NORTH);
	}
	
	public String showAccountCreation() {
		String name = JOptionPane.showInputDialog(this,
                "Add Account:", null);
		return name;
	}

	public void updateMatchList() {
		JOWMatchListPanel pMatches = new JOWMatchListPanel();
		app.addOWMatchExtractionListener(pMatches);
		app.removeOWMatchExtractionListener(this.pMatches);
		scrollPane.setViewportView(pMatches);
		this.pMatches = pMatches;
	}

}
