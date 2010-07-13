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

import java.util.Arrays;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

public class TableSelectionHandler implements ListSelectionListener, TableColumnModelListener {

    private JTable me;
    private JTable otherTable;

    public TableSelectionHandler(JTable t1, JTable t2) {
        this.me = t1;
        this.otherTable = t2;
    }

    public void columnSelectionChanged(ListSelectionEvent e) {
        
        int[] rows = me.getSelectedRows();
        int[] cols = me.getSelectedColumns();

        int[] theirRows = otherTable.getSelectedRows();
        int[] theirCols = otherTable.getSelectedColumns();

        if ( Arrays.equals(rows, theirRows) && Arrays.equals(cols, theirCols)) {
            return;
        }
        
        if ( rows.length > 0 && cols.length > 0 ) {

            int minRow = min(rows);
            int maxRow = max(rows);

            int minCol = min(cols);
            int maxCol = max(cols);

            otherTable.clearSelection();
            otherTable.changeSelection(minRow, minCol, false, false);
            otherTable.changeSelection(maxRow, maxCol, true, true);
        }

    }

    public void valueChanged(ListSelectionEvent e) {
        
    }

    public void columnAdded(TableColumnModelEvent e) { }
    public void columnRemoved(TableColumnModelEvent e) { }
    public void columnMoved(TableColumnModelEvent e) { }
    public void columnMarginChanged(ChangeEvent e) { }

    private int max(int[] vals) {
        int max = vals[0];
        for(int i = 0;i<vals.length;i++) {
            if ( vals[i] > max ) {
                max = vals[i];
            }
        }
        return max;
    }

    private int min(int[] vals) {
        int min = vals[0];
        for(int i = 0;i<vals.length;i++) {
            if ( vals[i] < min ) {
                min = vals[i];
            }
        }
        return min;
    }

}
