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
import com.aspect.snoop.agent.SnoopAgent;
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
      String.class
    };

    List<Chirp> chirps;

    String[] columnNames = {
      "Function",
      ""
    };

    public CanaryResultTableModel() {
        chirps = new ArrayList<Chirp>();
    }

    public void addChirp(Chirp chirp) {
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
    
    public Chirp getChirpAt(int row) {
        return chirps.get(row);
    }

    @Override
    public Object getValueAt(int row, int column) {

        Chirp chirp = chirps.get(row);
        if ( column == 0 ) {
            return chirp.getMethod().toString();
        }

        return "Add Hook";
        /*
        JButton btnAddHook = new JButton("Add hook");
        btnAddHook.setSize(40, 25);
        final FunctionHook hook = new FunctionHook(chirp.getMethod());

        btnAddHook.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SnoopAgent.getMainView().addHook(hook);
                JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(this, "Function hook added to " + hook.getClazz().getName() + "." + hook.getMethodName() + "()");
            }
        });

        return btnAddHook;
         */
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class getColumnClass(int column) {
        return columnTypes[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 0;
    }
}
