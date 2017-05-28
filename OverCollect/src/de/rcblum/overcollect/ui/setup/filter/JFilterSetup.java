package de.rcblum.overcollect.ui.setup.filter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.Box;
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
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import de.rcblum.overcollect.configuration.Filter;
import de.rcblum.overcollect.configuration.OWItem;
import de.rcblum.overcollect.configuration.OWLib;
import de.rcblum.overcollect.utils.Helper;

public class JFilterSetup extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7211142348893952901L;
	private JPanel contentPane;
	private JComboBox<String> cbItem;
	private JComboBox<String> cbResolution;
	private JSpinner spTolerance;

	private JScrollPane scrollPane;

	DefaultTableModel model = null;

	private JTable tPoints;

	private Filter filter = null;
	private BufferedImage image = null;
	private JCheckBox chckbxIsAHero;
	private JCheckBox chckbxIsGlyph;
	private JCheckBox chckbxIsMap;
	private JCheckBox chckbxIsMatchindicator;
	private JSpinner spGlyphsize;
	private JCheckBox chckbxDropScreenshot;
	private JLabel lblSaved;

	/**
	 * Create the frame.
	 */
	public JFilterSetup() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
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
				try {
					int rows = model.getRowCount();
					int[][] points = new int[rows][5];
					int tolerance = (int) spTolerance.getValue();
					for (int i = 0; i < rows; i++) {
						for (int j = 0; j < 5; j++) {
							points[i][j] = (Integer) model.getValueAt(i, j);
						}
					}
					if (filter == null) {
						filter = new Filter(points, tolerance);
					} else {
						filter.points = points;
						filter.tolerance = tolerance;
					}
					String resolution = (String) cbResolution.getSelectedItem();
					String itemName = (String) cbItem.getSelectedItem();
					OWItem item = OWLib.getInstance().getItem(resolution, itemName);
					item.saveFilter(filter);
					item.setMap(chckbxIsMap.isSelected());
					item.setHero(chckbxIsAHero.isSelected());
					item.setMatchIndicator(chckbxIsMatchindicator.isSelected());
					item.setDrop(chckbxDropScreenshot.isSelected());
					item.setGlyph(chckbxIsGlyph.isSelected(), (Integer) spGlyphsize.getValue());
					lblSaved.setForeground(Color.GREEN.darker());
					lblSaved.setText("saved");
				} catch (Exception e2) {
					e2.printStackTrace();
					lblSaved.setForeground(Color.RED.darker());
					lblSaved.setText("Failed to save: " + e2.getMessage());
				}
			}
		});
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));

		JButton btnTestFilters = new JButton("Test Filters");
		btnTestFilters.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new JFilterTest((String) cbResolution.getSelectedItem()).setVisible(true);
			}
		});
		panel_1.add(btnTestFilters);

		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3);

		lblSaved = new JLabel("");
		panel_1.add(lblSaved);
		panel_1.add(btnSave);

		scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		JLabel lblImage = new JLabel("");
		lblImage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				if (image != null && image.getWidth() > x && image.getHeight() > y) {
					Color c = new Color(image.getRGB(x, y), true);
					Object[] tblEntry = new Object[] { x, y, c.getRed(), c.getGreen(), c.getBlue() };
					model.addRow(tblEntry);
				}
			}
		});
		scrollPane.setViewportView(lblImage);

		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.WEST);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.Y_AXIS));

		JLabel lblTolerance = new JLabel("Tolerance");
		lblTolerance.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_2.add(lblTolerance);

		spTolerance = new JSpinner();
		spTolerance.setModel(new SpinnerNumberModel(5, 0, 100, 1));
		spTolerance.setPreferredSize(new Dimension(150, 20));
		spTolerance.setMaximumSize(new Dimension(300, 20));
		spTolerance.setMinimumSize(new Dimension(100, 20));

		panel_2.add(spTolerance);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setAutoscrolls(true);
		scrollPane_1.setPreferredSize(new Dimension(200, 200));
		scrollPane_1.setMaximumSize(new Dimension(300, 2000));
		scrollPane_1.setMinimumSize(new Dimension(100, 20));
		panel_2.add(scrollPane_1);
		model = new DefaultTableModel(new Object[][] {}, new String[] { "X", "Y", "R", "G", "B" }) {
			/**
			 * 
			 */
			private static final long serialVersionUID = -1041878165938322189L;
			Class<?>[] columnTypes = new Class[] { Integer.class, Integer.class, Integer.class, Integer.class,
					Integer.class };

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		};
		tPoints = new JTable(model);
		tPoints.setDefaultRenderer(Object.class, new ColoredCoordinatesCellRenderer());
		tPoints.setDefaultRenderer(Integer.class, new ColoredCoordinatesCellRenderer());
		scrollPane_1.setViewportView(tPoints);

		JButton btnDeleteEntry = new JButton("Delete Entry");
		btnDeleteEntry.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (tPoints.getSelectedRow() != -1) {
					int selNextRow = -1;
					int selNextColumn = tPoints.getSelectedColumn();
					if (tPoints.getSelectedRow() < tPoints.getRowCount() - 1)
						selNextRow = tPoints.getSelectedRow();
					model.removeRow(tPoints.getSelectedRow());
					if (selNextRow >= 0)
						tPoints.changeSelection(selNextRow, selNextColumn, false, false);
				}
			}
		});
		btnDeleteEntry.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_2.add(btnDeleteEntry);

		panel_2.add(Box.createRigidArea(new Dimension(5, 5)));
		chckbxIsAHero = new JCheckBox("Is a Hero");
		chckbxIsAHero.setToolTipText("This screenshot contains hero stats");
		chckbxIsAHero.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_2.add(chckbxIsAHero);

		Component rigidArea = Box.createRigidArea(new Dimension(5, 5));
		panel_2.add(rigidArea);

		chckbxIsMap = new JCheckBox("Is Map");
		chckbxIsMap.setToolTipText("This screenshot contains information which map is played");
		chckbxIsMap.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_2.add(chckbxIsMap);

		Component rigidArea_1 = Box.createRigidArea(new Dimension(5, 5));
		panel_2.add(rigidArea_1);

		chckbxIsMatchindicator = new JCheckBox("Is Matchindicator");
		chckbxIsMatchindicator
				.setToolTipText("The screenshot can be used to detect the beginning of a new competitive match");
		chckbxIsMatchindicator.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_2.add(chckbxIsMatchindicator);

		Component rigidArea_2 = Box.createRigidArea(new Dimension(5, 5));
		panel_2.add(rigidArea_2);

		chckbxIsGlyph = new JCheckBox("Is Glyph");
		chckbxIsGlyph.setToolTipText(
				"Indicates that the filter can be used to create a glyph, which extracts Data from screenshots");
		chckbxIsGlyph.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_2.add(chckbxIsGlyph);

		JLabel lblGlyphsizepxl = new JLabel("Glyphsize (pixel)");
		lblGlyphsizepxl.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_2.add(lblGlyphsizepxl);

		spGlyphsize = new JSpinner();
		spGlyphsize.setModel(new SpinnerNumberModel(55, 0, 600, 1));
		spGlyphsize.setMaximumSize(new Dimension(60, 20));
		panel_2.add(spGlyphsize);

		Component rigidArea_3 = Box.createRigidArea(new Dimension(5, 5));
		panel_2.add(rigidArea_3);

		chckbxDropScreenshot = new JCheckBox("Drop Screenshot");
		chckbxDropScreenshot
				.setToolTipText("Every Screenshot this matches will be dropped before processing it further");
		chckbxDropScreenshot.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_2.add(chckbxDropScreenshot);

		JLabel label = new JLabel(" ");
		panel_2.add(label);

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
					String resolution = (String) cbResolution.getSelectedItem();
					String itemName = (String) cbItem.getSelectedItem();
					OWItem item = OWLib.getInstance().getItem(resolution, itemName);
					for (int i = model.getRowCount() - 1; i >= 0; i--) {
						model.removeRow(i);
					}
					if (item.hasTemplate()) {
						image = Helper.copy(item.getTemplate());
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
					if (item.hasFilter()) {
						filter = item.getFilter();
						for (int i = 0; i < filter.points.length; i++) {
							model.addRow(new Object[] { filter.points[i][0], filter.points[i][1], filter.points[i][2],
									filter.points[i][3], filter.points[i][4] });
						}
					}
					spTolerance.setValue(item.getFilter().tolerance);
					chckbxIsAHero.setSelected(item.isHero());
					chckbxIsMap.setSelected(item.isMap());
					chckbxIsMatchindicator.setSelected(item.isMatchIndicator());
					chckbxIsGlyph.setSelected(item.hasGlyph());
					chckbxDropScreenshot.setSelected(item.drop());
					if (chckbxIsGlyph.isSelected())
						spGlyphsize.setValue(item.getGlyph().getBaseFontSize());
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
		model.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getFirstRow() < model.getRowCount() && e.getType() == TableModelEvent.INSERT) {
					int x = (int) JFilterSetup.this.model.getValueAt(e.getFirstRow(), 0);
					int y = (int) JFilterSetup.this.model.getValueAt(e.getFirstRow(), 1);
					if (image != null) {
						Graphics2D g = image.createGraphics();
						g.setColor(Color.YELLOW);
						g.drawRect(x, y, 1, 1);
						g.dispose();
					}
				}
			}
		});
	}
}
