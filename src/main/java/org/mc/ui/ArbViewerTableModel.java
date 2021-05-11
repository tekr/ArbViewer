package org.mc.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ArbViewerTableModel extends DefaultTableModel {
    private final String[] columnNames = { "Buy instrument", "Buy exchange", "Buy price",
            "Sell instrument", "Sell exchange", "Sell price",
            "Days to expiry", "Flat premium (%)", "Ann. premium (%)" };

    private final int[] columnWidths = { 100, 70, 70, 200, 70, 70, 80, 90, 90 };

    public int getColumnCount() {
        return columnNames.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Class getColumnClass(int c) {
        return getRowCount() > 0
                ? getValueAt(0, c).getClass()
                : String.class;
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }

    public void initColumns(JTable table) {
        var columnModel = table.getColumnModel();

        for (int i=0; i < table.getColumnCount(); i++) {
            var column = columnModel.getColumn(i);
            column.setPreferredWidth(columnWidths[i]);
        }

        columnModel.getColumn(7).setCellRenderer(new DecimalPlacesRenderer(2));
        columnModel.getColumn(8).setCellRenderer(new DecimalPlacesRenderer(1));
    }
}