package de.rcblum.overcollect.ui.panels;

import java.awt.Container;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.rcblum.overcollect.OverWatchCollectorApp;
import de.rcblum.overcollect.configuration.OWLib;
import de.rcblum.overcollect.export.XSSFExporter;
import de.rcblum.overcollect.ui.JOverCollectFrame;
import de.rcblum.overcollect.ui.utils.ComboBoxRenderer;
import de.rcblum.overcollect.ui.utils.FileUtils;
import de.rcblum.overcollect.ui.utils.FileUtils.OWFileFilter;
import de.rcblum.overcollect.ui.utils.UiStatics;
import javax.swing.JComboBox;
import javax.swing.JComponent;

public class JOWSidebar extends JPanel {

	private static final Icon ICON_NOT_RECORDING = new ImageIcon(
			JOWSidebar.class.getResource("/resources/ui/NoRecording.png"));

	private static final Icon ICON_RECORDING = new ImageIcon(
			JOWSidebar.class.getResource("/resources/ui/Recording.png"));
	public static void infoBox(String infoMessage, String titleBar, int msgType) {
		JOptionPane.showMessageDialog(null, infoMessage, titleBar, msgType);
	}

	private JLabel lblRecording;

	private OverWatchCollectorApp app = null;

	JButton btnRecording = null;

	JButton btnExportData = null;
	private JComboBox<String> cbxAccount;
	private JButton button;

	/**
	 * Create the panel.
	 */
	@SuppressWarnings("serial")
	public JOWSidebar(OverWatchCollectorApp app) {
		this.app = app;

		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30px"),
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("30px"),
				FormSpecs.RELATED_GAP_ROWSPEC,}));

		setBackground(UiStatics.COLOR_BACKGROUND);

		btnRecording = UiStatics.createButton(app != null && app.isRecording() ? "Stop Recording" : "Start Recording");
		btnRecording.setBackground(UiStatics.BUTTON_COLOR);
		add(btnRecording, "4, 2, 9, 1");

		lblRecording = new JLabel("");
		lblRecording.setIcon(app != null && app.isRecording() ? ICON_RECORDING : ICON_NOT_RECORDING);
		add(lblRecording, "2, 2");

		btnExportData = UiStatics.createButton("Export Data");
		add(btnExportData, "36, 2, 9, 1");
		
		cbxAccount = new JComboBox();
		add(cbxAccount, "16, 2, 7, 1, fill, default");
		btnRecording.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lblRecording.getIcon() == ICON_NOT_RECORDING && app != null) {
					lblRecording.setIcon(ICON_RECORDING);
					app.startCapture();
					btnRecording.setText("Stop Recording");
				} else if (app != null) {
					lblRecording.setIcon(ICON_NOT_RECORDING);
					app.stopCapture();
					btnRecording.setText("Start Recording");
				}

			}
		});

		btnExportData.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				OWFileFilter ffXlsx = new FileUtils.OWFileFilter(".xlsx");
				OWFileFilter ffOds = new FileUtils.OWFileFilter(".ods");
				fileChooser.addChoosableFileFilter(ffXlsx);
				fileChooser.addChoosableFileFilter(ffOds);
				fileChooser.setAcceptAllFileFilterUsed(false);
				if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					if (!file.toString().contains("."))
						file = Paths
								.get(file.getAbsoluteFile()
										+ ((FileUtils.OWFileFilter) fileChooser.getFileFilter()).getExtension())
								.toFile();
					if (ffXlsx.accept(file)) {
						XSSFExporter export = new XSSFExporter();
						export.generateMatchSheet();
						export.generateHeroSheets();
						try {
							export.save(file.toPath());
							Desktop desktop = Desktop.getDesktop();
							desktop.open(file);
						} catch (IOException e1) {
							e1.printStackTrace();
							infoBox("File \"" + file.getName() + "\" could not be saved", "Error exporting ",
									JOptionPane.ERROR_MESSAGE);
						}
					}
					// else if (ffOds.accept(file)) {
					// try {
					// OdsExporter export = new OdsExporter();
					// export.generateMatchSheet();
					// export.save(file.toPath());
					// Desktop desktop = Desktop.getDesktop();
					// desktop.open(file);
					// } catch (Exception e1) {
					// e1.printStackTrace();
					// infoBox("File \"" + file.getName() + "\" could not be
					// saved", "Error exporting ",
					// JOptionPane.ERROR_MESSAGE);
					// }
					// }
				}

			}
		});
		List<String> accounts = OWLib.getInstance().getAccounts();
		for (String acc : accounts) {
			cbxAccount.addItem(acc);
			cbxAccount.setSelectedItem(OWLib.getInstance().getActiveAccount());
		}
		cbxAccount.setBorder(new EmptyBorder(0, 0, 0, 0));
		for (int i = 0; i < cbxAccount.getComponentCount(); i++) 
		{
		    if (cbxAccount.getComponent(i) instanceof JComponent) {
		        ((JComponent) cbxAccount.getComponent(i)).setBorder(new EmptyBorder(0, 0, 0, 0));
		    }
		    if (cbxAccount.getComponent(i) instanceof AbstractButton) {
		        ((AbstractButton) cbxAccount.getComponent(i)).setBorderPainted(false);
		    }
		}
		cbxAccount.setFocusable(false);
		cbxAccount.setBackground(UiStatics.COLOR_BACKGROUND);
		cbxAccount.setFont(UiStatics.OW_FONT_NORMAL.deriveFont(Font.PLAIN, 25));
		cbxAccount.setRenderer(new ComboBoxRenderer());
		cbxAccount.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					OWLib.getInstance().setActiveAccount((String) e.getItem());
					Container parent = getParent();
					while ((parent != null) && !(parent instanceof JOverCollectFrame)) {
						parent = parent.getParent();
					}
					if (parent != null) {
						((JOverCollectFrame)parent).updateMatchList();
					}
				}
			}
		});
		button = UiStatics.createButton(" + ");
		button.setBorder(null);
		button.setMargin(new Insets(50, 0, 0, 0));
		add(button, "24, 2");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Container parent = getParent();
				while ((parent != null) && !(parent instanceof JOverCollectFrame)) {
					parent = parent.getParent();
				}
				if (parent != null) {
					String accountName = ((JOverCollectFrame)parent).showAccountCreation();
					OWLib.getInstance().addAccount(accountName);
					cbxAccount.addItem(accountName);
				}
			}
		});
	}

}
