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

package com.aspect.snoop.ui.hook;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class MethodTableModel extends DefaultTableModel {

    List<AccessibleObject> methods;

    public MethodTableModel(List<AccessibleObject> methods) {
        this.methods = methods;
    }

    private static Class colClasses[] = {
        String.class,
        String.class
    };

    private static String colNames[] = {
        "Return",
        "Method"
    };

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
    
    @Override
    public int getColumnCount() {
        return colNames.length;
    }

    @Override
    public Class getColumnClass(int col) {
        return colClasses[col];
    }

    @Override
    public String getColumnName(int col) {
        return colNames[col];
    }

    @Override
    public int getRowCount() {
        return methods != null ? methods.size() : 0;
    }

    @Override
    public Object getValueAt(int row, int col) {

        Method m = (Method)methods.get(row);

        switch(col) {
            case 0:
                return m.getReturnType().getName();
            case 1:
                return m;
        }

        return null;
    }
    
}
