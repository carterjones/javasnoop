/*
 * Copyright, Aspect Security, Inc.
 *
 * This file is part of JavaSnoop.
 *
 * JavaSnoop is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JavaSnoop is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaSnoop.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aspect.snoop.ui.tamper.bytearray;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class ByteArrayTableModel extends AbstractTableModel {

    private byte[] bytes;

    private static Class[] columnTypes = {
        Byte.class,
        Byte.class,
        Byte.class,
        Byte.class,
        Byte.class,
        Byte.class,
        Byte.class,
        Byte.class,
        Byte.class,
        Byte.class,
        Byte.class,
        Byte.class,
        Byte.class,
        Byte.class,
        Byte.class,
        Byte.class
    };

    private boolean showInHex = true;

    public void showInHex(boolean b) {
        showInHex = b;
    }

    private static String[] columnNames = {
        "0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"
    };

    private JTable mirrorTable;
    public ByteArrayTableModel(JTable mirrorTable, byte[] bytes) {
        this.bytes = bytes;
        this.mirrorTable = mirrorTable;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class getColumnClass(int column) {
        return columnTypes[column];
    }

    public int getRowCount() {
        int rows = bytes.length/16;
        boolean isRemainder = (bytes.length % 16) != 0;

        if ( isRemainder ) {
            rows++;
        }

        return rows;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public void setValueAt(Object o, int rowIndex, int columnIndex) {

        int offset = (rowIndex * 16) + columnIndex;

        if ( o != null ) {
            bytes[offset] = ((Byte)o).byteValue();
            mirrorTable.updateUI();
            mirrorTable.repaint();
        }
        
    }

    public Object getValueAt(int rowIndex, int columnIndex) {

        int offset = (rowIndex * 16) + columnIndex;

        if ( offset >= bytes.length ) {
            return null;
        }

        int i = bytes[offset] & 0xff;

        if ( showInHex ) {
            return Integer.toHexString(i);
        }

        return i;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
