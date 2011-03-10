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

import com.aspect.snoop.FunctionHook;
import com.aspect.snoop.agent.SnoopAgent;
import com.aspect.snoop.util.ReflectionUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class FunctionsHookedTableModel extends AbstractTableModel {

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

    List<FunctionHook> hooks = new ArrayList<FunctionHook>();

    public FunctionsHookedTableModel(List<FunctionHook> hooks) {
        
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
            FunctionHook hook = getHookFromRow(rowIndex);
            if ( ReflectionUtil.isInterfaceOrAbstract(hook.getClazz()) ) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public FunctionHook getHookFromRow(int rowIndex) {

        if ( rowIndex > hooks.size() || rowIndex < 0 ) {
            return null;
        }

        return hooks.get(rowIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        FunctionHook hook = hooks.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return hook.isEnabled();

            case 1:
                String methodSignature = " " + hook.getClazz().getName();
                methodSignature += "." + hook.getMethodName() + "(";

                for(Class param : hook.getParameterTypes())
                    methodSignature += param.getSimpleName() + ", ";

                if ( hook.getParameterTypes().length > 0 )
                    methodSignature = methodSignature.substring(0,methodSignature.length()-2);

                methodSignature += ")";
                return methodSignature;

            case 2:
                return hook.isAppliedToSubtypes();
                
            default:
        }

        return null;
    }
    
    public void setHooks(List<FunctionHook> functionHooks) {
        hooks = functionHooks;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        FunctionHook hook = hooks.get(rowIndex);
        
        if ( columnIndex == 0 ) {
            hook.setEnabled(((Boolean)aValue).booleanValue());
        } else if ( columnIndex == 2 ) {
            hook.setApplyToSubtypes(((Boolean)aValue).booleanValue());
        }
        SnoopAgent.getMainView().sendAgentNewRules();
    }

    public void disableAll() {
        for(int i=0;i<hooks.size();i++) {
            FunctionHook hook = hooks.get(i);
            hook.setEnabled(false);
        }
        //JavaSnoop.getMainForm().sendAgentNewRules();
    }

    public void enableAll() {
        for(int i=0;i<hooks.size();i++) {
            FunctionHook hook = hooks.get(i);
            hook.setEnabled(true);
        }
        //JavaSnoop.getMainForm().sendAgentNewRules();
    }

    public void removeHook(FunctionHook hook) {
        hooks.remove(hook);
    }
}
