package de.rcblum.overcollect.ui.setup.ocr;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

public class OCRColoredCoordinatesCellRenderer extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6764008451551747601L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		TableModel model = table.getModel();
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (model.getColumnCount() >= 3) {
			try {
				c.setBackground(new Color((Integer) model.getValueAt(row, 0), (Integer) model.getValueAt(row, 1),
						(Integer) model.getValueAt(row, 2)));
				int val = (Integer) model.getValueAt(row, 0) + (Integer) model.getValueAt(row, 1)
						+ (Integer) model.getValueAt(row, 2);
				if (val < 200)
					c.setForeground(Color.WHITE);
				else
					c.setForeground(Color.BLACK);

			} catch (Exception e) {
			}
		} else {
			c.setBackground(Color.WHITE);
			c.setForeground(Color.BLACK);
		}
		return c;
	}
}