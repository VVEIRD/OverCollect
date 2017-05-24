package de.rcblum.overcollect.ui.setup.filter;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import de.rcblum.overcollect.configuration.Filter;
import de.rcblum.overcollect.configuration.OWItem;
import de.rcblum.overcollect.configuration.OWLib;

public class JFilterTest extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4242809696601088803L;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JFilterTest frame = new JFilterTest();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	private JPanel contentPane;
	private JTable tItems;

	private DefaultTableModel model;

	private String selectedScreenResolution = null;
	private JComboBox<String> cbScreenResolution;
	private JScrollPane scrollPane;
	private JButton btnTestFilter;

	private JButton btnClose;

	/**
	 * Create the frame.
	 */
	public JFilterTest() {
		this(null);
	}

	public JFilterTest(String selectedScreenResolution) {
		this.selectedScreenResolution = selectedScreenResolution;
		setTitle("Test Filters");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 850, 620);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		JLabel lblScreenResolution = new JLabel("Screen Resolution");
		lblScreenResolution.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPane.add(lblScreenResolution);

		cbScreenResolution = new JComboBox<>();
		cbScreenResolution.setPreferredSize(new Dimension(200, 20));
		cbScreenResolution.setMaximumSize(new Dimension(2000, 20));
		cbScreenResolution.setMinimumSize(new Dimension(50, 20));
		contentPane.add(cbScreenResolution);

		JLabel lblItems = new JLabel("Items");
		lblItems.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPane.add(lblItems);

		scrollPane = new JScrollPane();
		contentPane.add(scrollPane);

		tItems = new JTable();
		tItems.setDefaultRenderer(String.class, new ColoredStringCellRenderer("Matches!"));
		tItems.setDefaultRenderer(Object.class, new ColoredStringCellRenderer("Matches!"));
		model = new DefaultTableModel(new Object[][] {}, new String[] { "Item Name", "Template" });
		tItems.setModel(model);
		scrollPane.setViewportView(tItems);

		JLabel label = new JLabel(" ");
		contentPane.add(label);

		JPanel panel = new JPanel();
		contentPane.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		btnTestFilter = new JButton("Test Filter");
		panel.add(btnTestFilter);

		JPanel panel_1 = new JPanel();
		panel.add(panel_1);

		btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		panel.add(btnClose);
		init();
	}

	private void init() {
		OWLib lib = OWLib.getInstance();
		for (String res : lib.getSupportedScreenResolutions())
			this.cbScreenResolution.addItem(res);
		if (this.selectedScreenResolution != null) {
			for (int i = 0; i < this.cbScreenResolution.getModel().getSize(); i++) {
				if (cbScreenResolution.getItemAt(i).equals(selectedScreenResolution)) {
					cbScreenResolution.setSelectedIndex(i);
					break;
				}
			}
		}
		updateItems();
		cbScreenResolution.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					updateItems();
			}
		});
		btnTestFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnTestFilter.setEnabled(false);
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							String res = (String) cbScreenResolution.getSelectedItem();
							int rows = tItems.getRowCount();
							int columnCount = tItems.getColumnCount();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							System.out.println("Start: " + sdf.format(Calendar.getInstance().getTime()));
							for (int i = 0; i < rows; i++) {
								for (int j = 1; j < columnCount; j++) {
									if (!"--".equals(model.getValueAt(i, j))) {
										System.out.println("Source: " + model.getValueAt(i, 0));
										System.out.println("Target: " + model.getColumnName(j));
										System.out.println();
										OWItem sourceItem = lib.getItem(res, (String) model.getValueAt(i, 0));
										OWItem targetItem = lib.getItem(res, model.getColumnName(j));
										if (sourceItem.hasFilter()) {
											Filter f = sourceItem.getFilter();
											if (targetItem.hasTemplate()) {
												BufferedImage image = targetItem.getTemplate();
												boolean succ = f.match(image);
												model.setValueAt(succ ? "Matches!" : "No match", i, j);
											} else {
												model.setValueAt("No template", i, j);
											}
										}
									}
								}
							}
							System.out.println("End: " + sdf.format(Calendar.getInstance().getTime()));
						} catch (Exception e) {
							e.printStackTrace();
						}
						btnTestFilter.setEnabled(true);
					}
				});
				t.start();
				// String itemName = (String) cbItem.getSelectedItem();
				// Filter f = lib.getItem(res, itemName).getFilter();
				// int rows = model.getRowCount();
				// for(int i=0; i<rows;i++) {
				// if ("Ready".equals(model.getValueAt(i, 1))) {
				// OWItem testSubject = lib.getItem(res,
				// (String)model.getValueAt(i, 0));
				// if(testSubject.hasTemplate()) {
				// boolean success = f.match(testSubject.getTemplate());
				// model.setValueAt(success ? "Machtes!" : "No match", i, 1);
				// }
				// }
				// }
			}
		});
	}

	private void updateItems() {
		String res = (String) this.cbScreenResolution.getSelectedItem();
		List<String> itemNames = OWLib.getInstance().getItemNames(res);
		Collections.sort(itemNames);
		Object[] columnNames = new Object[itemNames.size() + 1];
		Object[][] rows = new Object[itemNames.size()][itemNames.size() + 1];
		columnNames[0] = "Items";
		for (int i = 0; i < itemNames.size(); i++) {
			String itemName = itemNames.get(i);
			columnNames[i + 1] = itemName;
			rows[i][0] = itemName;
			for (int c = i + 2; c < rows[i].length; c++)
				rows[i][c] = "NA";
			for (int c = 1; c <= i + 1; c++)
				rows[i][c] = "NA";
		}
		model = new DefaultTableModel(rows, columnNames) {
			private static final long serialVersionUID = -2324701057366647233L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tItems.setModel(model);
		tItems.getColumnModel().getColumn(0).setPreferredWidth(120);
		tItems.getColumnModel().getColumn(0).setMinWidth(75);
		// List<OWItem> items = lib.getItems(res);
		// for (OWItem owItem : items) {
		// Object[] row = new Object[]{owItem.getItemName(),
		// (owItem.hasTemplate() ? "Ready" : "No template image") };
		// model.addRow(row);
		// }
	}

}
