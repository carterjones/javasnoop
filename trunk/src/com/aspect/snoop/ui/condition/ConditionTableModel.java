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

package com.aspect.snoop.ui.condition;

import com.aspect.snoop.JavaSnoop;
import com.aspect.snoop.Condition;
import com.aspect.snoop.util.ModelUIUtil;
import com.aspect.snoop.util.UIUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import javax.swing.JButton;
import javax.swing.JFrame;

public class ConditionTableModel extends AbstractTableModel {

    List<Condition> conditions;
    private static String[] columnNames = {
        "Enabled",
        "Parameter",
        "Operator",
        "Operand",
        ""
    };

    private static Class[] columnTypes = {
        Boolean.class,
        String.class,
        String.class,
        String.class,
        JButton.class
    };

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public ConditionTableModel() {
        this.conditions = new ArrayList<Condition>();
    }

    public ConditionTableModel(List<Condition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public int getRowCount() {
        if (conditions == null) {
            return 0;
        }
        return conditions.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnTypes[columnIndex];
    }

    @Override
    public void setValueAt(Object o, int rowIndex, int columnIndex) {
        if ( columnIndex == 0 ) {
            boolean b = ((Boolean)o).booleanValue();
            conditions.get(rowIndex).setEnabled(b);
        }
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Condition c = conditions.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return c.isEnabled();
            case 1:
                return " " + (c.getParameter()+1);
            case 2:
                return " " + ModelUIUtil.getDescriptionByOperator(c.getOperator());
            case 3:
                return " " + c.getOperand();
            case 4:

                final Condition c2 = c;
                JButton manageButton = new JButton("Manage");
                manageButton.addActionListener( new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        JFrame mainFrame = JavaSnoop.getApplication().getMainFrame();
                        AddEditConditionView view =
                                new AddEditConditionView(
                                    mainFrame,
                                    true,
                                    null,//JavaSnoop.getMainForm().getCurrentHook().getParameterTypes(),
                                    c2);
                        view.setVisible(true);
                        
                        UIUtil.waitForInput(view);

                        if ( view.getOperand() != null ) {
                            c2.setOperator(view.getOperator());
                            c2.setOperand(view.getOperand());
                            c2.setParameter(view.getParameter());
                            mainFrame.repaint();
                        }

                    }
                });

                return manageButton;

            default:
        }

        return null;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0 || columnIndex == 4;
    }

    public Condition getConditionAt(int selectedRow) {
        return conditions.get(selectedRow);
    }

}