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

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

public class HexTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    boolean shouldEditInHex = true;

    public void shouldEditInHex(boolean b) {
        this.shouldEditInHex = b;
    }
    
    // This is the component that will handle the editing of the cell value
    JComponent component = new JTextField();

    
    @Override
    public boolean isCellEditable(EventObject evt) {
        if ( evt instanceof MouseEvent ) {
            MouseEvent event = (MouseEvent)evt;
            
            return event.getClickCount() >= 2;
        }
        
        return false;
    }

    // This method is called when a cell value is edited by the user.
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {

       if ( shouldEditInHex ) {
            int i = Integer.parseInt( (String)value,16);
            ((JTextField)component).setText( Integer.toHexString(i) );
        } else {
            Byte b = (Byte)value;
            ((JTextField)component).setText( "" + b.byteValue() );
        }

        // Return the configured component
        return component;
    }

    // This method is called when editing is completed.
    // It must return the new value to be stored in the cell.
    public Object getCellEditorValue() {

        if ( shouldEditInHex ) {
            return Byte.parseByte(((JTextField)component).getText(),16);
        }
        return Byte.parseByte(((JTextField)component).getText());
    }
}
