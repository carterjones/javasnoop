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
import com.aspect.snoop.ui.tamper.array.EditArrayView;
import com.aspect.snoop.ui.tamper.bytearray.EditByteArrayView;
import com.aspect.snoop.ui.tamper.list.EditListView;
import com.aspect.snoop.ui.tamper.map.EditMapView;
import com.aspect.snoop.util.ReflectionUtil;
import com.aspect.snoop.util.UIUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class ParameterTableModel extends AbstractTableModel {

    private List<Parameter> parameters;
    private JTable table;

    private static Class[] columnTypes = {
        Integer.class,
        String.class,
        String.class,
        JButton.class
    };

    private static String[] columnNames = {
        "Index",
        "Type",
        "Value",
        ""
    };

    public ParameterTableModel(JTable table, List<Parameter> parameters) {
        this.table = table;
        this.parameters = parameters;
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
        return parameters.size();
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public void setValueAt(Object newObject, int rowIndex, int columnIndex) {

        Parameter p = parameters.get(rowIndex);

        try {

            String s = (String) newObject;
            // have to handle each one differently

            if ( p.getObject() instanceof Boolean) {
                p.setObject(Boolean.valueOf(s));
            } else if (p.getObject() instanceof Character) {
                p.setObject(s.charAt(0));
            } else if (p.getObject() instanceof String) {
                p.setObject(s);
            } else if (p.getObject() instanceof Byte) {
                p.setObject(Byte.parseByte(s));
            } else if (p.getObject() instanceof Short) {
                p.setObject(Short.parseShort(s));
            } else if (p.getObject() instanceof Integer) {
                p.setObject(Integer.parseInt(s));
            } else if (p.getObject() instanceof Long) {
                p.setObject(Long.parseLong(s));
            } else if (p.getObject() instanceof Double) {
                p.setObject(Double.parseDouble(s));
            } else if (p.getObject() instanceof Float) {
                p.setObject(Float.parseFloat(s));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getValueAt(int rowIndex, int columnIndex) {

        Parameter p = parameters.get(rowIndex);

        try {
            switch (columnIndex) {
                case 0:
                    return p.getIndex();

                case 1:
                    
                    if ( p.getObject() != null ) {
                        return ReflectionUtil.getSimpleClassName(p.getObject().getClass().getName());
                    } else {

                    }

                case 2:
                    return p.getObject();

                case 3:

                    if ( p.getObject() == null ) {
                        return null;
                    }

                    if ( ! ReflectionUtil.isPrimitiveButNotArray(p.getObject()) ) {

                        final Parameter param = p;
                        final Object o = p.getObject();
                        
                        JButton btn = new JButton("Edit");
                        btn.setEnabled(true);
                        btn.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {

                                if ( o instanceof Map ) {

                                    EditMapView view = new EditMapView(JavaSnoop.getApplication().getMainFrame(), true, (Map)o);
                                    view.setVisible(true);
                                    UIUtil.waitForInput(view);
                                    fireTableStructureChanged();

                                } else if ( o instanceof List ) {

                                    EditListView view = new EditListView(JavaSnoop.getApplication().getMainFrame(), true, (List)o);
                                    view.setVisible(true);
                                    UIUtil.waitForInput(view);
                                    fireTableStructureChanged();

                                } else if ( o instanceof byte[] ) {

                                    EditByteArrayView view = new EditByteArrayView(JavaSnoop.getApplication().getMainFrame(), true, (byte[])o);
                                    view.setVisible(true);
                                    UIUtil.waitForInput(view);
                                    param.setObject(view.getBytes());
                                    fireTableStructureChanged();

                                } else if (o.getClass().isArray()) {

                                    EditArrayView view = new EditArrayView(JavaSnoop.getApplication().getMainFrame(), true, (Object[])o);
                                    view.setVisible(true);
                                    UIUtil.waitForInput(view);
                                    fireTableStructureChanged();
                                    
                                } else {

                                    EditObjectView view = new EditObjectView(JavaSnoop.getApplication().getMainFrame(), true, o);
                                    view.setVisible(true);
                                    UIUtil.waitForInput(view);

                                    if ( view.shouldReplaceObject() ) {
                                        Object replacement = view.getObjectReplacement();
                                        param.setObject(replacement);
                                    }
                                    
                                    fireTableStructureChanged();
                                }

                            }
                        });

                        return btn;
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {

        if (columnIndex == 2) {
            Parameter p = parameters.get(rowIndex);

            if (ReflectionUtil.isPrimitiveButNotArray(p.getObject())) {
                return true;
            }
            
        } else if (columnIndex == 3) {
            return true;
        }

        return false;
    }
}
