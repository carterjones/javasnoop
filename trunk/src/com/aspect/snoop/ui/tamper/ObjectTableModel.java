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

package com.aspect.snoop.ui.tamper;

import com.aspect.snoop.JavaSnoop;
import com.aspect.snoop.agent.AgentLogger;
import com.aspect.snoop.ui.tamper.array.EditArrayView;
import com.aspect.snoop.ui.tamper.bytearray.EditByteArrayView;
import com.aspect.snoop.ui.tamper.list.EditListView;
import com.aspect.snoop.ui.tamper.map.EditMapView;
import com.aspect.snoop.util.ReflectionUtil;
import com.aspect.snoop.util.UIUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

class ObjectTableModel extends AbstractTableModel {

    private List<Field> fields;
    private Object toEdit;

    private static Class[] columnTypes = {
        String.class,
        String.class,
        String.class,
        JButton.class
    };

    private static String[] columnNames = {
        "Name",
        "Type",
        "toString()",
        ""
    };

    public ObjectTableModel(Object toEdit, List<Field> fields) {
        this.fields = fields;
        this.toEdit = toEdit;
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return columnTypes[columnIndex];
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    public int getRowCount() {
        return fields.size();
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 3;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Field f = fields.get(rowIndex);

        switch(columnIndex) {
            case 0:
                return f.getName();
            case 1:
            case 2:
            case 3:

                final Field accessibleField = ReflectionUtil.getAccessibleField(toEdit, f);
                
                try {

                    Object innerObject = accessibleField.get(toEdit);

                    if ( columnIndex == 1 ) {
                        return accessibleField.getType().getName();
                    } else if ( columnIndex == 2 ) {
                        return String.valueOf(innerObject);
                    }

                    if ( innerObject == null ) {
                        return null; // can't edit null objects
                    }

                    final Object obj = innerObject;
                    JButton btn = new JButton("Edit");
                    btn.setEnabled(true);
                    btn.addActionListener( new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        if ( obj.getClass().isArray()  ) {
                            EditArrayView view = new EditArrayView(JavaSnoop.getApplication().getMainFrame(), true, (Object[])obj);
                            view.setVisible(true);
                            UIUtil.waitForInput(view);
                            fireTableStructureChanged();

                        } else if ( obj instanceof Map ) {
                            EditMapView view = new EditMapView(JavaSnoop.getApplication().getMainFrame(), true, (Map)obj);
                            view.setVisible(true);
                            UIUtil.waitForInput(view);
                            fireTableStructureChanged();

                        } else if ( obj instanceof List ) {
                            EditListView view = new EditListView(JavaSnoop.getApplication().getMainFrame(), true, (List)obj);
                            view.setVisible(true);
                            UIUtil.waitForInput(view);
                            fireTableStructureChanged();

                        } else if ( obj instanceof byte[] ) {
                            EditByteArrayView view = new EditByteArrayView(JavaSnoop.getApplication().getMainFrame(), true, (byte[])obj);
                            view.setVisible(true);
                            UIUtil.waitForInput(view);
                            fireTableStructureChanged();

                            try {
                                accessibleField.set(toEdit, view.getBytes());
                            } catch (IllegalArgumentException ex) {
                                AgentLogger.error(ex);
                            } catch (IllegalAccessException ex) {
                                AgentLogger.error(ex);
                            }
                            fireTableStructureChanged();
                            
                        } else {
                            EditObjectView view = new EditObjectView(JavaSnoop.getApplication().getMainFrame(), true, obj);
                            view.setVisible(true);
                            UIUtil.waitForInput(view);
                            fireTableStructureChanged();
                            
                            if ( view.shouldReplaceObject() ) {
                                try {
                                    accessibleField.set(toEdit, view.getObjectReplacement());
                                } catch (IllegalArgumentException ex) {
                                    AgentLogger.error("Couldn't save edited object: " + ex.getMessage(), ex);
                                } catch (IllegalAccessException ex) {
                                    AgentLogger.error("Couldn't save edited object: " + ex.getMessage(), ex);
                                }
                            }
                        }
                    }});

                  return btn;

                } catch(Exception e) { e.printStackTrace(); }
        }

        return null;
    }

}
