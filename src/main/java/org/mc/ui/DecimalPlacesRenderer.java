package org.mc.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;

public class DecimalPlacesRenderer extends DefaultTableCellRenderer {
    private DecimalFormat formatter;

    public DecimalPlacesRenderer(int decimalPlaces) {
        formatter = new DecimalFormat( "#." + "0".repeat(decimalPlaces));
        setHorizontalAlignment(JLabel.RIGHT);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        return super.getTableCellRendererComponent(
                table, formatter.format(value), isSelected, hasFocus, row, column );
    }
}