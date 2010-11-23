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

import com.aspect.snoop.util.ReflectionUtil;
import java.lang.reflect.Field;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class PrimitiveTableModel extends AbstractTableModel {

    private List<Field> fields;
    private Object toEdit;

    public PrimitiveTableModel(Object o, List<Field> fields) {
        this.toEdit = o;
        this.fields = fields;
    }

    private static String[] columnNames = {
        "Name",
        "Type",
        "Value",
    };


    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public int getRowCount() {
        return fields.size();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 2;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {

        switch(columnIndex) {
            case 0:
                return fields.get(rowIndex).getName();
            case 1:
            case 2:
                try {
                    
                    Field f = fields.get(rowIndex);
                    f.setAccessible(true);
                    Object innerObject = f.get(toEdit);

                    if ( columnIndex == 1 ) {
                        return f.getType().getName();
                    }

                    return String.valueOf(innerObject);

                } catch (Exception e) {e.printStackTrace(); };
        }

        return null;
        
    }

    @Override
    public void setValueAt(Object newObject, int rowIndex, int columnIndex) {

        Field f = fields.get(rowIndex);

        try {

            String s = (String) newObject;

            // have to handle each field differently, depending on the type
            if ( ReflectionUtil.isCharacter(f) ) {
                f.setChar(toEdit, s.charAt(0));
            }

            else if ( ReflectionUtil.isBoolean(f) ) {
                f.setBoolean(toEdit, Boolean.parseBoolean(s));
            }

            else if ( ReflectionUtil.isCharacter(f) ) {
                f.setChar(toEdit, s.charAt(0));
            }

            else if ( ReflectionUtil.isString(f) ) {
                f.set(toEdit, s);
            }

            else if ( ReflectionUtil.isByte(f) ) {
                f.setByte(toEdit, Byte.parseByte(s));
            }

            else if ( ReflectionUtil.isShort(f) ) {
                f.setShort(toEdit, Short.parseShort(s));
            }

            else if ( ReflectionUtil.isInteger(f) ) {
                f.setInt(toEdit, Integer.parseInt(s));
            }

            else if ( ReflectionUtil.isLong(f) ) {
                f.setLong(toEdit, Long.parseLong(s));
            }

            else if ( ReflectionUtil.isDouble(f) ) {
                f.setDouble(toEdit, Double.parseDouble(s));
            }

            else if ( ReflectionUtil.isFloat(f) ) {
                f.setFloat(toEdit, Float.parseFloat(s));
            }

        } catch (Exception e) {
            //ignore 
        }
    }

}
