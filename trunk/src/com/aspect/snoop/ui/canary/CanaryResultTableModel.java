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
package com.aspect.snoop.ui.canary;

import com.aspect.snoop.FunctionHook;
import com.aspect.snoop.JavaSnoop;
import com.aspect.snoop.agent.manager.UniqueMethod;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class CanaryResultTableModel extends DefaultTableModel {

    Class[] columnTypes = {
      String.class,
      JButton.class
    };

    List<UniqueMethod> chirps;

    String[] columnNames = {
      "Function",
      ""
    };

    public CanaryResultTableModel() {
        chirps = new ArrayList<UniqueMethod>();
    }

    public void addChirp(UniqueMethod chirp) {
        chirps.add(chirp);
    }

    @Override
    public int getRowCount() {
        return chirps == null ? 0 : chirps.size();
    }

    @Override
    public int getColumnCount() {
        return columnTypes.length;
    }
    
    @Override
    public Object getValueAt(int row, int column) {

        UniqueMethod chirp = chirps.get(row);
        if ( column == 0 ) {
            return chirp.toString();
        }

        JButton btnAddHook = new JButton("Add hook");
        btnAddHook.setSize(40, 25);
        final FunctionHook hook = new FunctionHook(chirp);

        btnAddHook.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JavaSnoop.getMainForm().addHook(hook);
                JOptionPane.showMessageDialog(JavaSnoop.getMainForm().getFrame(), "Function hook added to " + hook.getClassName() + "." + hook.getMethodName() + "()");
            }
        });

        return btnAddHook;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class getColumnClass(int column) {
        return columnTypes[column];
    }

}
