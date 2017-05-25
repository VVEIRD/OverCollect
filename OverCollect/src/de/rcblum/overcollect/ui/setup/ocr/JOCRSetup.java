package de.rcblum.overcollect.ui.setup.ocr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import de.rcblum.overcollect.configuration.OCRConfiguration;
import de.rcblum.overcollect.configuration.OWItem;
import de.rcblum.overcollect.configuration.OWLib;
import de.rcblum.overcollect.extract.ScreenExtract;

public class JOCRSetup extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7211142348893952901L;
	private JPanel contentPane;
	private JComboBox<String> cbItem;
	private JComboBox<String> cbResolution;
	private JSpinner spPrimarySkew;

	private JScrollPane scrollPane;

	DefaultTableModel tblmPrimaryDatafieldColor = null;
	DefaultTableModel tblmPrimaryDatafields = null;
	DefaultTableModel tblmPrimaryDatafieldSize = null;

	DefaultTableModel tblmSecondaryDatafieldColor = null;
	DefaultTableModel tblmSecondaryDatafields = null;
	DefaultTableModel tblmSecondaryDatafieldSize = null;

	private OWItem item = null;
	private OCRConfiguration ocr = null;
	private BufferedImage image = null;

	private JCheckBox cbxRecolor;
	private JTable tblPrimaryColor;
	private JTable tblPrimaryDatafieldSize;
	private JTable tblPrimaryDatafields;

	private JTable tblSecondaryColor;
	private JTable tblSecondaryDatafieldSize;
	private JTable tblSecondaryPoints;
	private JSpinner spSecondarySkew;
	private JSpinner spSecondarySkewTrim;
	private JSpinner spSecondaryFontSize;
	private JSpinner spPrimarySkewTrim;
	private JSpinner spPrimaryFontSize;
	private JCheckBox chckbxEnableRecolorfilter;
	private JLabel lblSaved;

	/**
	 * Create the frame.
	 */
	public JOCRSetup() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1500, 1000);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);

		JLabel lblResolution = new JLabel("Resolution");
		panel.add(lblResolution);

		cbResolution = new JComboBox<>();
		panel.add(cbResolution);

		JLabel lblItem = new JLabel("Item");
		panel.add(lblItem);

		cbItem = new JComboBox<>();
		panel.add(cbItem);

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);

		JButton btnSave = new JButton("save");
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Primary fields
				int rows = tblmPrimaryDatafields.getRowCount();
				int columns = tblmPrimaryDatafields.getColumnCount();
				Map<String, int[]> primatyDatafields = new HashMap<>();
				for (int i = 0; i < rows; i++) {
					int[] dataPoint = new int[columns - 1];
					for (int j = 1; j < columns; j++) {
						dataPoint[j - 1] = (Integer) tblPrimaryDatafields.getValueAt(i, j);
					}
					primatyDatafields.put((String) tblPrimaryDatafields.getValueAt(i, 0), dataPoint);
				}
				// secondary fields
				rows = tblmSecondaryDatafields.getRowCount();
				columns = tblmSecondaryDatafields.getColumnCount();
				Map<String, int[]> secondaryDatafields = new HashMap<>();
				for (int i = 0; i < rows; i++) {
					int[] dataPoint = new int[columns - 1];
					for (int j = 1; j < columns; j++) {
						dataPoint[j - 1] = (Integer) tblmSecondaryDatafields.getValueAt(i, j);
					}
					secondaryDatafields.put((String) tblmSecondaryDatafields.getValueAt(i, 0), dataPoint);
				}
				double primarySkew = (double) spPrimarySkew.getValue();
				double secondarySkew = (double) spSecondarySkew.getValue();

				int[] primaryDataColor = new int[] { 0, 0, 0 };
				if (tblPrimaryColor.getColumnCount() >= 3 && tblPrimaryColor.getRowCount() >= 1) {
					primaryDataColor[0] = (Integer) tblPrimaryColor.getValueAt(0, 0);
					primaryDataColor[1] = (Integer) tblPrimaryColor.getValueAt(0, 1);
					primaryDataColor[2] = (Integer) tblPrimaryColor.getValueAt(0, 2);
				}

				int[] secondaryDataColor = new int[] { 0, 0, 0 };
				if (tblSecondaryColor.getColumnCount() >= 3 && tblSecondaryColor.getRowCount() >= 1) {
					secondaryDataColor[0] = (Integer) tblSecondaryColor.getValueAt(0, 0);
					secondaryDataColor[1] = (Integer) tblSecondaryColor.getValueAt(0, 1);
					secondaryDataColor[2] = (Integer) tblSecondaryColor.getValueAt(0, 2);
				}

				int[] primaryDataFiledSize = new int[] { 0, 0 };
				if (tblPrimaryDatafieldSize.getColumnCount() >= 2 && tblPrimaryDatafieldSize.getRowCount() >= 1) {
					primaryDataFiledSize[0] = (Integer) tblPrimaryDatafieldSize.getValueAt(0, 0);
					primaryDataFiledSize[1] = (Integer) tblPrimaryDatafieldSize.getValueAt(0, 1);
				}

				int[] secondaryDataFiledSize = new int[] { 0, 0 };
				if (tblSecondaryDatafieldSize.getColumnCount() >= 2 && tblSecondaryDatafieldSize.getRowCount() >= 1) {
					secondaryDataFiledSize[0] = (Integer) tblSecondaryDatafieldSize.getValueAt(0, 0);
					secondaryDataFiledSize[1] = (Integer) tblSecondaryDatafieldSize.getValueAt(0, 1);
				}

				boolean doRecolor = cbxRecolor.isSelected();

				int primarySkewTrim = (Integer) spPrimarySkewTrim.getValue();
				int secondarySkewTrim = (Integer) spSecondarySkewTrim.getValue();

				int primaryFontSize = (Integer) spPrimaryFontSize.getValue();
				int secondaryFontSize = (Integer) spSecondaryFontSize.getValue();

				OCRConfiguration nOcr = new OCRConfiguration(primatyDatafields, secondaryDatafields,
						primaryDataFiledSize, secondaryDataFiledSize, primaryFontSize, secondaryFontSize, primarySkew,
						secondarySkew, primarySkewTrim, secondarySkewTrim, doRecolor, primaryDataColor,
						secondaryDataColor, item.isMap() ? 3 : 1);
				try {
					item.saveOCRConfiguration(nOcr);
					lblSaved.setForeground(Color.green.darker());
					lblSaved.setText("saved");
				} catch (IOException e1) {
					lblSaved.setForeground(Color.RED.darker());
					lblSaved.setText("Error saving Configuration: " + e1.getMessage());
					e1.printStackTrace();
				}
				// if (filter == null) {
				// filter = new Filter(points, tolerance);
				// }
				// else {
				// filter.points = points;
				// filter.tolerance = tolerance;
				// }
				// String resolution = (String) cbResolution.getSelectedItem();
				// String item = (String) cbItem.getSelectedItem();
				// Filter.save(lib.getLibPath().toString(), resolution, item,
				// filter);
			}
		});
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		chckbxEnableRecolorfilter = new JCheckBox("Enable Recolorfilter Preview");
		chckbxEnableRecolorfilter.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				updateImage(image);
			}
		});
		panel_1.add(chckbxEnableRecolorfilter);

		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3);
		panel_1.add(btnSave);

		lblSaved = new JLabel("");
		panel_1.add(lblSaved);

		scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		JLabel lblImage = new JCroshairLable("");
		lblImage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				if (image != null) {
					Object[] tblEntry = new Object[] { "", x, y };
					if (SwingUtilities.isRightMouseButton(e)) {
						tblmSecondaryDatafields.addRow(tblEntry);
					} else {
						tblmPrimaryDatafields.addRow(tblEntry);
					}
					updateImage(image);
					repaint();
				}
			}
		});
		scrollPane.setViewportView(lblImage);

		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.WEST);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.Y_AXIS));

		JLabel lblPrimaryStatistics = new JLabel("Primary Statistics");
		lblPrimaryStatistics.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblPrimaryStatistics.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_2.add(lblPrimaryStatistics);

		cbxRecolor = new JCheckBox("Recoloring");
		cbxRecolor.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_2.add(cbxRecolor);

		JLabel lblSkew = new JLabel("Primary Skew");
		lblSkew.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_2.add(lblSkew);

		spPrimarySkew = new JSpinner();
		spPrimarySkew.setModel(new SpinnerNumberModel(0.26, 0.0, 1.0, 0.01));
		spPrimarySkew.setPreferredSize(new Dimension(150, 20));
		spPrimarySkew.setMaximumSize(new Dimension(300, 20));
		spPrimarySkew.setMinimumSize(new Dimension(100, 20));

		panel_2.add(spPrimarySkew);

		JLabel lblSkewTrim = new JLabel("Trimming after Skew");
		lblSkewTrim.setAlignmentX(0.5f);
		panel_2.add(lblSkewTrim);

		spPrimarySkewTrim = new JSpinner();
		spPrimarySkewTrim.setModel(new SpinnerNumberModel(21, 0, 100, 1));
		spPrimarySkewTrim.setPreferredSize(new Dimension(150, 20));
		spPrimarySkewTrim.setMinimumSize(new Dimension(100, 20));
		spPrimarySkewTrim.setMaximumSize(new Dimension(300, 20));
		panel_2.add(spPrimarySkewTrim);

		JLabel lblPrimaryFontSize = new JLabel("Primary Font Size");
		lblPrimaryFontSize.setAlignmentX(0.5f);
		panel_2.add(lblPrimaryFontSize);

		spPrimaryFontSize = new JSpinner();
		spPrimaryFontSize.setModel(new SpinnerNumberModel(55, 0, 2000, 1));
		spPrimaryFontSize.setPreferredSize(new Dimension(150, 20));
		spPrimaryFontSize.setMinimumSize(new Dimension(100, 20));
		spPrimaryFontSize.setMaximumSize(new Dimension(300, 20));
		panel_2.add(spPrimaryFontSize);

		JLabel lblColor = new JLabel("Font Color");
		lblColor.setAlignmentX(0.5f);
		panel_2.add(lblColor);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setPreferredSize(new Dimension(200, 45));
		scrollPane_2.setMinimumSize(new Dimension(100, 20));
		scrollPane_2.setMaximumSize(new Dimension(300, 45));
		scrollPane_2.setAutoscrolls(true);
		tblmPrimaryDatafieldColor = new DefaultTableModel(new Object[][] { { 0, 0, 0 }, },
				new String[] { "R", "G", "B" }) {
			Class[] columnTypes = new Class[] { Integer.class, Integer.class, Integer.class };

			@Override
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		};
		tblmSecondaryDatafieldColor = new DefaultTableModel(new Object[][] { { 0, 0, 0 }, },
				new String[] { "R", "G", "B" }) {
			Class[] columnTypes = new Class[] { Integer.class, Integer.class, Integer.class };

			@Override
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		};

		tblPrimaryColor = new JTable(tblmPrimaryDatafieldColor);

		tblPrimaryColor.setDefaultRenderer(Object.class, new OCRColoredCoordinatesCellRenderer());
		tblPrimaryColor.setDefaultRenderer(Integer.class, new OCRColoredCoordinatesCellRenderer());
		scrollPane_2.setViewportView(tblPrimaryColor);

		panel_2.add(scrollPane_2);

		JLabel lblDatafieldSize = new JLabel("Datafield Size");
		lblDatafieldSize.setAlignmentX(0.5f);
		panel_2.add(lblDatafieldSize);

		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setPreferredSize(new Dimension(200, 45));
		scrollPane_3.setMinimumSize(new Dimension(100, 20));
		scrollPane_3.setMaximumSize(new Dimension(300, 45));
		scrollPane_3.setAutoscrolls(true);
		panel_2.add(scrollPane_3);

		tblmPrimaryDatafieldSize = new DefaultTableModel(new Object[][] { { 172, 80 }, },
				new String[] { "Width", "Height" }) {
			Class[] columnTypes = new Class[] { Integer.class, Integer.class, Integer.class };

			@Override
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		};
		tblmSecondaryDatafieldSize = new DefaultTableModel(new Object[][] { { 130, 60 }, },
				new String[] { "Width", "Height" }) {
			Class[] columnTypes = new Class[] { Integer.class, Integer.class, Integer.class };

			@Override
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		};
		tblPrimaryDatafieldSize = new JTable(tblmPrimaryDatafieldSize);
		scrollPane_3.setViewportView(tblPrimaryDatafieldSize);

		JLabel lblFields = new JLabel("Primary Fields");
		lblFields.setAlignmentX(0.5f);
		panel_2.add(lblFields);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setAutoscrolls(true);
		scrollPane_1.setPreferredSize(new Dimension(200, 200));
		scrollPane_1.setMaximumSize(new Dimension(300, 2000));
		scrollPane_1.setMinimumSize(new Dimension(100, 20));
		panel_2.add(scrollPane_1);
		tblmPrimaryDatafields = new DefaultTableModel(new Object[][] {}, new String[] { "Name", "X", "Y" }) {
			Class[] columnTypes = new Class[] { String.class, Integer.class, Integer.class };

			@Override
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		};
		tblmSecondaryDatafields = new DefaultTableModel(new Object[][] {}, new String[] { "Name", "X", "Y" }) {
			Class[] columnTypes = new Class[] { String.class, Integer.class, Integer.class };

			@Override
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		};
		tblPrimaryDatafields = new JTable(tblmPrimaryDatafields);
		tblPrimaryDatafields.getColumnModel().getColumn(0).setPreferredWidth(120);
		tblPrimaryDatafields.getColumnModel().getColumn(0).setMinWidth(100);
		scrollPane_1.setViewportView(tblPrimaryDatafields);

		JButton btnRemovePrimaryEntry = new JButton("Remove Entry");
		btnRemovePrimaryEntry.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (tblPrimaryDatafields.getSelectedRow() >= 0) {
					tblmPrimaryDatafields.removeRow(tblPrimaryDatafields.getSelectedRow());
				}
			}
		});
		btnRemovePrimaryEntry.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_2.add(btnRemovePrimaryEntry);

		JPanel panel_4 = new JPanel();
		contentPane.add(panel_4, BorderLayout.EAST);
		panel_4.setLayout(new BoxLayout(panel_4, BoxLayout.Y_AXIS));

		JLabel lblNewLabel = new JLabel("Secondary Statistics");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_4.add(lblNewLabel);

		JLabel lblSecondarySkew = new JLabel("Secondary Skew");
		lblSecondarySkew.setAlignmentX(0.5f);
		panel_4.add(lblSecondarySkew);

		spSecondarySkew = new JSpinner();
		spSecondarySkew.setModel(new SpinnerNumberModel(new Float(0), new Float(0), new Float(1), new Float(0)));
		spSecondarySkew.setPreferredSize(new Dimension(150, 20));
		spSecondarySkew.setMinimumSize(new Dimension(100, 20));
		spSecondarySkew.setMaximumSize(new Dimension(300, 20));
		panel_4.add(spSecondarySkew);

		JLabel label = new JLabel("Trimming after Skew");
		label.setAlignmentX(0.5f);
		panel_4.add(label);

		spSecondarySkewTrim = new JSpinner();
		spSecondarySkewTrim.setModel(new SpinnerNumberModel(0, 0, 50, 1));
		spSecondarySkewTrim.setPreferredSize(new Dimension(150, 20));
		spSecondarySkewTrim.setMinimumSize(new Dimension(100, 20));
		spSecondarySkewTrim.setMaximumSize(new Dimension(300, 20));
		panel_4.add(spSecondarySkewTrim);

		JLabel lblSecondaryFontSize = new JLabel("Secondary Font Size");
		lblSecondaryFontSize.setAlignmentX(0.5f);
		panel_4.add(lblSecondaryFontSize);

		spSecondaryFontSize = new JSpinner();
		spSecondaryFontSize.setModel(new SpinnerNumberModel(40, 1, 500, 1));
		spSecondaryFontSize.setPreferredSize(new Dimension(150, 20));
		spSecondaryFontSize.setMinimumSize(new Dimension(100, 20));
		spSecondaryFontSize.setMaximumSize(new Dimension(300, 20));
		panel_4.add(spSecondaryFontSize);

		JLabel lblSecondaryFontColor = new JLabel("Secondary Font Color");
		lblSecondaryFontColor.setAlignmentX(0.5f);
		panel_4.add(lblSecondaryFontColor);

		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setPreferredSize(new Dimension(200, 45));
		scrollPane_4.setMinimumSize(new Dimension(100, 20));
		scrollPane_4.setMaximumSize(new Dimension(300, 45));
		scrollPane_4.setAutoscrolls(true);
		panel_4.add(scrollPane_4);

		tblSecondaryColor = new JTable(tblmSecondaryDatafieldColor);
		scrollPane_4.setViewportView(tblSecondaryColor);

		JLabel lblSecondaryDatafieldSize = new JLabel("Secondary Datafield Size");
		lblSecondaryDatafieldSize.setAlignmentX(0.5f);
		panel_4.add(lblSecondaryDatafieldSize);

		JScrollPane scrollPane_5 = new JScrollPane();
		scrollPane_5.setPreferredSize(new Dimension(200, 45));
		scrollPane_5.setMinimumSize(new Dimension(100, 20));
		scrollPane_5.setMaximumSize(new Dimension(300, 45));
		scrollPane_5.setAutoscrolls(true);
		panel_4.add(scrollPane_5);

		tblSecondaryDatafieldSize = new JTable(tblmSecondaryDatafieldSize);
		tblSecondaryDatafieldSize.getColumnModel().getColumn(0).setPreferredWidth(120);
		tblSecondaryDatafieldSize.getColumnModel().getColumn(0).setMinWidth(100);
		scrollPane_5.setViewportView(tblSecondaryDatafieldSize);

		JLabel lblSecondaryFields = new JLabel("Secondary Fields");
		lblSecondaryFields.setAlignmentX(0.5f);
		panel_4.add(lblSecondaryFields);

		JScrollPane scrollPane_6 = new JScrollPane();
		scrollPane_6.setPreferredSize(new Dimension(200, 200));
		scrollPane_6.setMinimumSize(new Dimension(100, 20));
		scrollPane_6.setMaximumSize(new Dimension(300, 2000));
		scrollPane_6.setAutoscrolls(true);
		panel_4.add(scrollPane_6);

		tblSecondaryPoints = new JTable(tblmSecondaryDatafields);
		scrollPane_6.setViewportView(tblSecondaryPoints);

		JButton btnRemoveSecondary = new JButton("Remove Entry");
		btnRemoveSecondary.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (tblSecondaryPoints.getSelectedRow() >= 0) {
					tblmSecondaryDatafields.removeRow(tblSecondaryPoints.getSelectedRow());
				}
			}
		});
		btnRemoveSecondary.setAlignmentX(0.5f);
		panel_4.add(btnRemoveSecondary);

		cbResolution.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (cbResolution.getSelectedItem() != null) {
					String resolution = (String) cbResolution.getSelectedItem();
					File folder = new File("lib" + File.separator + "owdata" + File.separator + resolution);
					cbItem.removeAllItems();
					if (folder.exists()) {
						File[] items = folder.listFiles();
						for (File item : items) {
							cbItem.addItem(item.getName());
						}
					}
				}
			}
		});

		cbItem.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (cbItem.getSelectedItem() != null && e.getStateChange() == ItemEvent.SELECTED) {
					cbItem.setEnabled(false);
					String resolution = (String) cbResolution.getSelectedItem();
					String itemName = (String) cbItem.getSelectedItem();
					item = OWLib.getInstance().getItem(resolution, itemName);
					TableModelListener[] listenersPDC = tblmPrimaryDatafieldColor.getTableModelListeners();
					for (TableModelListener tableModelListener : listenersPDC) {
						tblmPrimaryDatafieldColor.removeTableModelListener(tableModelListener);
					}
					TableModelListener[] listenersSDC = tblmSecondaryDatafieldColor.getTableModelListeners();
					for (TableModelListener tableModelListener : listenersSDC) {
						tblmSecondaryDatafieldColor.removeTableModelListener(tableModelListener);
					}
					TableModelListener[] listenersPD = tblmPrimaryDatafields.getTableModelListeners();
					for (TableModelListener tableModelListener : listenersPD) {
						tblmPrimaryDatafields.removeTableModelListener(tableModelListener);
					}
					TableModelListener[] listenersSD = tblmSecondaryDatafields.getTableModelListeners();
					for (TableModelListener tableModelListener : listenersSD) {
						tblmSecondaryDatafields.removeTableModelListener(tableModelListener);
					}
					for (int i = tblmPrimaryDatafields.getRowCount() - 1; i >= 0; i--) {
						tblmPrimaryDatafields.removeRow(i);
					}
					for (int i = tblmPrimaryDatafieldSize.getRowCount() - 1; i >= 0; i--) {
						tblmPrimaryDatafieldSize.removeRow(i);
					}
					for (int i = tblmPrimaryDatafieldColor.getRowCount() - 1; i >= 0; i--) {
						tblmPrimaryDatafieldColor.removeRow(i);
					}
					for (int i = tblmSecondaryDatafields.getRowCount() - 1; i >= 0; i--) {
						tblmSecondaryDatafields.removeRow(i);
					}
					for (int i = tblmSecondaryDatafieldSize.getRowCount() - 1; i >= 0; i--) {
						tblmSecondaryDatafieldSize.removeRow(i);
					}
					for (int i = tblmSecondaryDatafieldColor.getRowCount() - 1; i >= 0; i--) {
						tblmSecondaryDatafieldColor.removeRow(i);
					}

					if (item.hasTemplate()) {
						image = deepCopy(item.getTemplate());
						lblImage.setIcon(new ImageIcon(image));
						lblImage.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
						revalidate();
						// lblImage.setMaximumSize(new
						// Dimension(image.getWidth(), image.getHeight()));
						// lblImage.setMinimumSize(new
						// Dimension(image.getWidth(), image.getHeight()));
					} else {
						image = null;
						lblImage.setIcon(null);
					}
					if (item.hasOCRConfiguration()) {
						ocr = item.getOCRConfiguration();
						List<String> keys = new ArrayList<>(ocr.values.keySet());
						// Primary
						spPrimarySkew.setValue(ocr.skew);
						if (ocr.dataFieldSize != null)
							tblmPrimaryDatafieldSize
									.addRow(new Object[] { ocr.dataFieldSize[0], ocr.dataFieldSize[1] });
						else
							tblmPrimaryDatafieldSize.addRow(new Object[] { 172, 80 });

						if (ocr.dataColor != null)
							tblmPrimaryDatafieldColor
									.addRow(new Object[] { ocr.dataColor[0], ocr.dataColor[1], ocr.dataColor[2] });
						else
							tblmPrimaryDatafieldColor.addRow(new Object[] { 0, 0, 0 });
						spPrimarySkew.setValue(ocr.skew);
						spPrimarySkewTrim.setValue(ocr.skewTrim);
						spPrimaryFontSize.setValue(ocr.fontSize);
						cbxRecolor.setSelected(ocr.doRecolor);
						for (int i = 0; i < keys.size(); i++) {
							int[] coord = ocr.values.get(keys.get(i));
							tblmPrimaryDatafields.addRow(new Object[] { keys.get(i), coord[0], coord[1] });
						}
						// Secondary values
						keys = new ArrayList<>(ocr.secondaryValues.keySet());
						spSecondarySkew.setValue(ocr.skew);
						if (ocr.secondaryDataFieldSize != null)
							tblmSecondaryDatafieldSize.addRow(
									new Object[] { ocr.secondaryDataFieldSize[0], ocr.secondaryDataFieldSize[1] });
						else
							tblmSecondaryDatafieldSize.addRow(new Object[] { 120, 60 });

						if (ocr.secondaryDataColor != null)
							tblmSecondaryDatafieldColor.addRow(new Object[] { ocr.secondaryDataColor[0],
									ocr.secondaryDataColor[1], ocr.secondaryDataColor[2] });
						else
							tblmSecondaryDatafieldColor.addRow(new Object[] { 0, 0, 0 });
						spSecondarySkew.setValue(ocr.skewSecondary);
						spSecondarySkewTrim.setValue(ocr.skewSecondaryTrim);
						spSecondaryFontSize.setValue(ocr.secondaryFontSize);
						for (int i = 0; i < keys.size(); i++) {
							int[] coord = ocr.secondaryValues.get(keys.get(i));
							tblmSecondaryDatafields.addRow(new Object[] { keys.get(i), coord[0], coord[1] });
						}
						updateImage(image);
						revalidate();
						tblSecondaryPoints.revalidate();
					} else {
						tblmPrimaryDatafieldSize.addRow(new Object[] { 120, 60 });
						tblmPrimaryDatafieldColor.addRow(new Object[] { 0, 0, 0 });
						spPrimarySkew.setValue(0.26);
						spPrimarySkewTrim.setValue(21);
						spPrimaryFontSize.setValue(55);
						cbxRecolor.setSelected(false);
					}

					for (TableModelListener tableModelListener : listenersPDC) {
						tblmPrimaryDatafieldColor.addTableModelListener(tableModelListener);
					}
					for (TableModelListener tableModelListener : listenersSDC) {
						tblmSecondaryDatafieldColor.addTableModelListener(tableModelListener);
					}
					for (TableModelListener tableModelListener : listenersPD) {
						tblmPrimaryDatafields.addTableModelListener(tableModelListener);
					}
					for (TableModelListener tableModelListener : listenersSD) {
						tblmSecondaryDatafields.addTableModelListener(tableModelListener);
					}
					cbItem.setEnabled(true);
				}
			}
		});
		File folder = new File("lib" + File.separator + "owdata");
		cbResolution.removeAllItems();
		if (folder.exists()) {
			File[] items = folder.listFiles();
			for (File item : items) {
				if (item.isDirectory())
					cbResolution.addItem(item.getName());
			}
		}

		TableModelListener l = new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 0
						|| e.getType() == TableModelEvent.INSERT)
					updateImage(image);
				else if (e.getType() == TableModelEvent.UPDATE || e.getType() == TableModelEvent.DELETE) {
					if (item != null) {
						image = deepCopy(item.getTemplate());
						updateImage(image);
						lblImage.setIcon(new ImageIcon(image));
					}
				}
				repaint();
			}
		};

		tblmPrimaryDatafields.addTableModelListener(l);
		tblmSecondaryDatafields.addTableModelListener(l);

		TableModelListener l2 = new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getType() == TableModelEvent.UPDATE) {
					image = deepCopy(item.getTemplate());
					updateImage(image);
					lblImage.setIcon(new ImageIcon(image));
				}
				repaint();
			}
		};

		tblmPrimaryDatafieldSize.addTableModelListener(l2);
		tblmSecondaryDatafieldSize.addTableModelListener(l2);
	}

	private BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public boolean doRecolor() {
		return this.cbxRecolor.isSelected();
	}

	public Color getPrimaryColor() {
		if (this.tblmPrimaryDatafieldColor.getRowCount() >= 1 && this.tblmPrimaryDatafieldColor.getColumnCount() >= 3)
			return new Color((Integer) this.tblmPrimaryDatafieldColor.getValueAt(0, 0),
					(Integer) this.tblmPrimaryDatafieldColor.getValueAt(0, 1),
					(Integer) this.tblmPrimaryDatafieldColor.getValueAt(0, 2));
		return Color.BLACK;
	}

	public double getPrimarySkew() {
		return (Double) this.spPrimarySkew.getValue();
	}

	public int getPrimarySkewTrim() {
		return (Integer) this.spPrimarySkewTrim.getValue();
	}

	public Color getSecondaryColor() {
		if (this.tblmSecondaryDatafieldColor.getRowCount() >= 1
				&& this.tblmSecondaryDatafieldColor.getColumnCount() >= 3)
			return new Color((Integer) this.tblmSecondaryDatafieldColor.getValueAt(0, 0),
					(Integer) this.tblmSecondaryDatafieldColor.getValueAt(0, 1),
					(Integer) this.tblmSecondaryDatafieldColor.getValueAt(0, 2));
		return Color.BLACK;
	}

	public double getSecondarySkew() {
		return (Double) this.spSecondarySkew.getValue();
	}

	public int getSecondarySkewTrim() {
		return (Integer) this.spSecondarySkewTrim.getValue();
	}

	private void updateImage(BufferedImage image) {
		if (image != null) {
			Graphics2D g = image.createGraphics();
			g.setColor(Color.RED);
			int width = (Integer) tblmPrimaryDatafieldSize.getValueAt(0, 0);
			int height = (Integer) tblmPrimaryDatafieldSize.getValueAt(0, 1);
			Rectangle2D r = null;
			for (int i = 0; i < tblmPrimaryDatafields.getRowCount(); i++) {
				String text = (String) tblmPrimaryDatafields.getValueAt(i, 0);
				Rectangle2D r2 = g.getFont().getStringBounds(text, g.getFontRenderContext());
				if (r == null)
					r = r2;
				if (r.getWidth() < r2.getWidth())
					r.setRect(0, 0, r2.getWidth(), r.getHeight());
				if (r.getHeight() < r2.getHeight())
					r.setRect(0, 0, r.getWidth(), r2.getHeight());
			}
			for (int i = 0; i < tblmPrimaryDatafields.getRowCount(); i++) {
				String text = (String) tblmPrimaryDatafields.getValueAt(i, 0);
				int x = (Integer) tblmPrimaryDatafields.getValueAt(i, 1);
				int y = (Integer) tblmPrimaryDatafields.getValueAt(i, 2);
				if (chckbxEnableRecolorfilter.isSelected()) {
					BufferedImage subImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
					subImg.createGraphics().drawImage(this.item.getTemplate().getSubimage(x, y, width, height), 0, 0,
							null);
					BufferedImage bImg = ScreenExtract.adjustImage(subImg, this.getPrimaryColor(),
							this.getPrimarySkew(), this.getPrimarySkewTrim(), this.doRecolor());
					g.drawImage(bImg, x + this.getPrimarySkewTrim() / 2, y, null);
				} else {
					BufferedImage subImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
					subImg.createGraphics().drawImage(this.item.getTemplate().getSubimage(x, y, width, height), 0, 0,
							null);
					g.drawImage(subImg, x, y, null);
				}
				g.setColor(Color.DARK_GRAY);
				g.fillRect(x, (int) (height < 50 ? y - (r.getHeight() + 3) : y), (int) r.getWidth() + 8,
						(int) r.getHeight() + 5);
				g.setColor(Color.RED);
				g.drawString(text, x + 5, (int) (height < 50 ? (y + 15) - (r.getHeight() + 4) : y + 15));
				g.drawRect(x, y, width, height);
			}
			// Secondary fields
			g.setColor(Color.GREEN);
			width = (Integer) tblmSecondaryDatafieldSize.getValueAt(0, 0);
			height = (Integer) tblmSecondaryDatafieldSize.getValueAt(0, 1);
			r = null;
			for (int i = 0; i < tblmSecondaryDatafields.getRowCount(); i++) {
				String text = (String) tblmSecondaryDatafields.getValueAt(i, 0);
				Rectangle2D r2 = g.getFont().getStringBounds(text, g.getFontRenderContext());
				if (r == null)
					r = r2;
				if (r.getWidth() < r2.getWidth())
					r.setRect(0, 0, r2.getWidth(), r.getHeight());
				if (r.getHeight() < r2.getHeight())
					r.setRect(0, 0, r.getWidth(), r2.getHeight());
			}
			for (int i = 0; i < tblmSecondaryDatafields.getRowCount(); i++) {
				String text = (String) tblmSecondaryDatafields.getValueAt(i, 0);
				int x = (Integer) tblmSecondaryDatafields.getValueAt(i, 1);
				int y = (Integer) tblmSecondaryDatafields.getValueAt(i, 2);
				;
				if (chckbxEnableRecolorfilter.isSelected()) {
					BufferedImage subImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
					subImg.createGraphics().drawImage(this.item.getTemplate().getSubimage(x, y, width, height), 0, 0,
							null);
					BufferedImage bImg = ScreenExtract.adjustImage(subImg, this.getSecondaryColor(),
							this.getSecondarySkew(), this.getSecondarySkewTrim(), this.doRecolor());
					g.drawImage(bImg, x + this.getSecondarySkewTrim() / 2, y, null);
				} else {
					BufferedImage subImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
					subImg.createGraphics().drawImage(this.item.getTemplate().getSubimage(x, y, width, height), 0, 0,
							null);
					g.drawImage(subImg, x, y, null);
				}
				g.setColor(Color.DARK_GRAY);
				g.fillRect(x, (int) (height < 50 ? y - (r.getHeight() + 3) : y), (int) r.getWidth() + 8,
						(int) r.getHeight() + 5);
				g.setColor(Color.GREEN);
				g.drawString(text, x + 5, (int) (height < 50 ? (y + 15) - (r.getHeight() + 4) : y + 15));
				g.drawRect(x, y, width, height);
			}
			g.dispose();
			repaint();
		}
	}
}
