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

import com.aspect.snoop.JavaSnoop;
import com.aspect.snoop.FunctionHookInterceptor;
import com.aspect.snoop.util.ReflectionUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

public class FunctionsHookedTableModel extends AbstractTableModel {

    private static Logger logger = Logger.getLogger(FunctionsHookedTableModel.class);

    private static String[] columnNames = {
        "Enabled",
        "Class/Method",
        "Inheritable"
    };

    private static Class[] columnTypes = {
        Boolean.class,
        String.class,
        Boolean.class
    };

    List<FunctionHookInterceptor> hooks = new ArrayList<FunctionHookInterceptor>();

    public FunctionsHookedTableModel(List<FunctionHookInterceptor> hooks) {
        
        if ( hooks != null ) {
            this.hooks = hooks;
        }
    }

    @Override
    public int getRowCount() {
        if ( hooks == null ) {
            return 0;
        }
        return hooks.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnTypes[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if ( columnIndex == 0 ) {
            return true;
        } else if ( columnIndex == 2 ) {
            FunctionHookInterceptor hook = getHookFromRow(rowIndex);
            Class c;
            try {
                c = Class.forName(hook.getClassName(), true, JavaSnoop.getClassLoader());
                if ( ReflectionUtil.isInterfaceOrAbstract(c) ) {
                    return false;
                } else {
                    return true;
                }
            } catch (ClassNotFoundException ex) {
                logger.error("Couldn't find class during table row editing: " + ex);
            }

        }
        return false;
    }

    public FunctionHookInterceptor getHookFromRow(int rowIndex) {

        if ( rowIndex > hooks.size() || rowIndex < 0 ) {
            return null;
        }

        return hooks.get(rowIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        FunctionHookInterceptor hook = hooks.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return hook.isEnabled();

            case 1:
                String methodSignature = " " + hook.getClassName();
                methodSignature += "." + hook.getMethodName() + "(";

                for(String param : hook.getParameterTypes()) {
                    
                    String tmp = ReflectionUtil.getSimpleClassName(param);
                    
                    methodSignature += tmp + ", ";
                }

                if ( hook.getParameterTypes().length > 0 ) {
                    methodSignature = methodSignature.substring(0,methodSignature.length()-2);
                }

                methodSignature += ")";
                return methodSignature;

            case 2:
                return hook.isAppliedToSubtypes();
                
            default:
        }

        return null;
    }
    
    public void setHooks(List<FunctionHookInterceptor> functionHooks) {
        hooks = functionHooks;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        FunctionHookInterceptor hook = hooks.get(rowIndex);
        
        if ( columnIndex == 0 ) {
            hook.setEnabled(((Boolean)aValue).booleanValue());
            JavaSnoop.getMainForm().sendAgentNewRules();

        } else if ( columnIndex == 2 ) {
            hook.setApplyToSubtypes(((Boolean)aValue).booleanValue());
        }
    }

    public void disableAll() {
        for(int i=0;i<hooks.size();i++) {
            FunctionHookInterceptor hook = hooks.get(i);
            hook.setEnabled(false);
        }
        JavaSnoop.getMainForm().sendAgentNewRules();
    }

    public void enableAll() {
        for(int i=0;i<hooks.size();i++) {
            FunctionHookInterceptor hook = hooks.get(i);
            hook.setEnabled(true);
        }
        JavaSnoop.getMainForm().sendAgentNewRules();
    }

    public void removeHook(FunctionHookInterceptor hook) {
        hooks.remove(hook);
    }
}
