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

package com.aspect.snoop.ui.tamper.map;

import com.aspect.snoop.JavaSnoop;
import com.aspect.snoop.ui.tamper.EditObjectView;
import com.aspect.snoop.ui.tamper.array.EditArrayView;
import com.aspect.snoop.ui.tamper.bytearray.EditByteArrayView;
import com.aspect.snoop.ui.tamper.list.EditListView;
import com.aspect.snoop.util.ReflectionUtil;
import com.aspect.snoop.util.UIUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

class MapTableModel extends AbstractTableModel {

    private Map items;
    private Set keySet;
    private Object[] keys;

    private static Class[] columnTypes = {
        String.class,
        String.class,
        String.class,
        JButton.class
    };

    private static String[] columnNames = {
        "Key",
        "Type",
        "toString()",
        ""
    };

    public MapTableModel(Map items) {
        this.items = items;
        this.keySet = items.keySet();
        this.keys = keySet.toArray();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public int getRowCount() {
        return items.size();
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return columnTypes[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {

        return (ReflectionUtil.isPrimitiveButNotArray(items.get(keys[rowIndex])) && (columnIndex == 2))
                ||
               (!ReflectionUtil.isPrimitiveButNotArray(items.get(keys[rowIndex])) && (columnIndex == 3));
    }

    public Object getValueAt(int rowIndex, int columnIndex) {

        Object o = items.get( keys[rowIndex] );
        final int row = rowIndex;
        
        switch(columnIndex) {
            case 0:
                return keys[rowIndex];
            case 1:
                return o.getClass().getName();
            case 2:
                return o.toString();
            case 3:
                if ( ! ReflectionUtil.isPrimitiveButNotArray(o) ) {

                    final Object copy = o;
                    JButton btn = new JButton("Edit");
                    btn.addActionListener( new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if ( copy instanceof Map ) {
                                EditMapView view = new EditMapView(JavaSnoop.getApplication().getMainFrame(), true, (Map)copy);
                                view.setVisible(true);
                                UIUtil.waitForInput(view);
                            } else if ( copy instanceof List ) {
                                EditListView view = new EditListView(JavaSnoop.getApplication().getMainFrame(), true, (List)copy);
                                view.setVisible(true);
                                UIUtil.waitForInput(view);
                            } else if ( copy instanceof byte[] ) {
                                EditByteArrayView view = new EditByteArrayView(JavaSnoop.getApplication().getMainFrame(), true, (byte[])copy);
                                view.setVisible(true);
                                UIUtil.waitForInput(view);
                                items.put( keys[row], view.getBytes() );
                                fireTableStructureChanged();
                            } else if ( copy.getClass().isArray()  ) {
                                EditArrayView view = new EditArrayView(JavaSnoop.getApplication().getMainFrame(), true, (Object[])copy);
                                view.setVisible(true);
                                UIUtil.waitForInput(view);
                            } else {
                                EditObjectView view = new EditObjectView(JavaSnoop.getApplication().getMainFrame(), true, copy);
                                view.setVisible(true);
                                UIUtil.waitForInput(view);
                                if ( view.shouldReplaceObject() ) {
                                    items.put( keys[row], view.getObjectReplacement());
                                }
                            }
                        }
                    });
                    return btn;
                }
        }

        return null;
    }

    @Override
    public void setValueAt(Object newObject, int rowIndex, int columnIndex) {

        Object o = items.get(keys[rowIndex]);

        try {

            String s = (String) newObject;

            // have to handle each field differently, depending on the type
            Object key = keys[rowIndex];
            
            if ( o instanceof Boolean ) {
                items.put(key, Boolean.parseBoolean(s));
            }

            else if ( o instanceof Byte ) {
                items.put(key, Byte.parseByte(s));
            }

            else if ( o instanceof Character ) {
                items.put(key, s.charAt(0));
            }

            else if ( o instanceof String ) {
                items.put(key, s);
            }

            else if ( o instanceof Short ) {
                items.put(key, Short.parseShort(s));
            }

            else if ( o instanceof Integer ) {
                items.put(key, Integer.parseInt(s));
            }

            else if ( o instanceof Long ) {
                items.put(key, Long.parseLong(s));
            }

            else if ( o instanceof Double ) {
                items.put(key, Double.parseDouble(s));
            }

            else if ( o instanceof Float ) {
                items.put(key, Float.parseFloat(s));
            }

        } catch (Exception e) {
            //ignore
            e.printStackTrace();
        }
    }


    public void refresh() {
        this.keySet = items.keySet();
        this.keys = keySet.toArray();
    }


}
