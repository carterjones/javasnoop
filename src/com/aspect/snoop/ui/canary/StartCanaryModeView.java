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

import com.aspect.snoop.agent.AgentCommunicationException;
import com.aspect.snoop.agent.SnoopToAgentClient;
import com.aspect.snoop.agent.manager.UniqueMethod;
import com.aspect.snoop.util.UIUtil;
import javax.swing.JButton;
import javax.swing.WindowConstants;
import org.jdesktop.application.Action;

public class StartCanaryModeView extends javax.swing.JDialog {

    CanaryResultTableModel model;
    SnoopToAgentClient client;
    
    public StartCanaryModeView(SnoopToAgentClient client, java.awt.Frame parent, boolean modal) {

        super(parent, modal);

        initComponents();

        this.client = client;
        
        this.model = new CanaryResultTableModel();

        ButtonTableCellRenderer renderer = new ButtonTableCellRenderer();
        tblCanaries.setDefaultEditor(JButton.class, renderer);
        tblCanaries.setDefaultRenderer(JButton.class, renderer);
        tblCanaries.setRowHeight(25);
        tblCanaries.setModel(model);

        tblCanaries.getColumnModel().getColumn(0).setPreferredWidth(450);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblDescription = new javax.swing.JLabel();
        btnStartCanaryMode = new javax.swing.JButton();
        txtCanary = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        lstDataTypes = new javax.swing.JComboBox();
        btnStopCanaryMode = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCanaries = new javax.swing.JTable();
        txtPackage = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.aspect.snoop.JavaSnoop.class).getContext().getResourceMap(StartCanaryModeView.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        lblDescription.setText(resourceMap.getString("lblDescription.text")); // NOI18N
        lblDescription.setName("lblDescription"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(com.aspect.snoop.JavaSnoop.class).getContext().getActionMap(StartCanaryModeView.class, this);
        btnStartCanaryMode.setAction(actionMap.get("beginCanaryMode")); // NOI18N
        btnStartCanaryMode.setFont(resourceMap.getFont("btnStartCanaryMode.font")); // NOI18N
        btnStartCanaryMode.setText(resourceMap.getString("btnStartCanaryMode.text")); // NOI18N
        btnStartCanaryMode.setName("btnStartCanaryMode"); // NOI18N

        txtCanary.setText(resourceMap.getString("txtCanary.text")); // NOI18N
        txtCanary.setName("txtCanary"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        lstDataTypes.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "String", "short", "int", "long", "double", "float" }));
        lstDataTypes.setName("lstDataTypes"); // NOI18N

        btnStopCanaryMode.setAction(actionMap.get("endCanaryMode")); // NOI18N
        btnStopCanaryMode.setFont(resourceMap.getFont("btnStopCanaryMode.font")); // NOI18N
        btnStopCanaryMode.setText(resourceMap.getString("btnStopCanaryMode.text")); // NOI18N
        btnStopCanaryMode.setEnabled(false);
        btnStopCanaryMode.setName("btnStopCanaryMode"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblCanaries.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblCanaries.setName("tblCanaries"); // NOI18N
        jScrollPane1.setViewportView(tblCanaries);

        txtPackage.setText(resourceMap.getString("txtPackage.text")); // NOI18N
        txtPackage.setName("txtPackage"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
                    .addComponent(lblDescription, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCanary)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lstDataTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnStartCanaryMode)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnStopCanaryMode))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPackage, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnStartCanaryMode, btnStopCanaryMode});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtCanary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(lstDataTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnStopCanaryMode, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnStartCanaryMode, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtPackage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @Action
    public void beginCanaryMode() throws AgentCommunicationException {

        String canary = txtCanary.getText();
        String pkg = txtPackage.getText();
        
        if ( canary.length() <= 0 ) {
            UIUtil.showErrorMessage(this, "Please enter a value to track through the application");
            return;
        }

        txtCanary.setEnabled(false);
        lstDataTypes.setEnabled(false);
        btnStartCanaryMode.setEnabled(false);
        btnStopCanaryMode.setEnabled(true);

        // setup background action to find canaries.
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        Object o = lstDataTypes.getSelectedItem();

        client.beginCanaryMode(canary,(String)o, pkg);
        
    }

    @Action
    public void endCanaryMode() throws AgentCommunicationException {

        txtCanary.setEnabled(true);
        lstDataTypes.setEnabled(true);
        btnStartCanaryMode.setEnabled(true);
        btnStopCanaryMode.setEnabled(false);

        try {
            client.endCanaryMode();
        } catch (AgentCommunicationException e) {
            throw e;
        } finally {
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnStartCanaryMode;
    private javax.swing.JButton btnStopCanaryMode;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JComboBox lstDataTypes;
    private javax.swing.JTable tblCanaries;
    private javax.swing.JTextField txtCanary;
    private javax.swing.JTextField txtPackage;
    // End of variables declaration//GEN-END:variables

    public void addChirp(UniqueMethod chirp) {
        model.addChirp(chirp);
        tblCanaries.updateUI();
        tblCanaries.repaint();
    }

}
