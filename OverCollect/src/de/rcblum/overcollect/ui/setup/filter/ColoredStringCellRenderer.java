package de.rcblum.overcollect.ui.setup.filter;

import java.awt.Color;
import java.awt.Component;
import java.util.Objects;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import sun.swing.table.DefaultTableCellHeaderRenderer;


public class ColoredStringCellRenderer extends DefaultTableCellRenderer 
{
	
	private String matcher = null;
	DefaultTableCellHeaderRenderer dtchr = new DefaultTableCellHeaderRenderer();
	
	public ColoredStringCellRenderer(String matcher)
	{
		this.matcher = Objects.requireNonNull(matcher);
	}

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        TableModel model = table.getModel();
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (column == 0) {
        	c = dtchr.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        	return c;
        }
        if (value instanceof String) {
        	String val = (String) value;
        	if (this.matcher.equals(val)) {
        		if (model.getColumnName(column).equals(model.getValueAt(row, 0)))
        			c.setBackground(Color.green);
        		else
        			c.setBackground(Color.red);
        	}
        	else {
				c.setBackground(Color.white);
			}
        }
    	else {
			c.setBackground(Color.white);
		}
        return c;
    }
}