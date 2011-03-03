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

import com.aspect.snoop.SnoopSession;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;

public class ParameterTamperingView extends javax.swing.JDialog {

    private List<Parameter> parameters;
    private String clazz;
    private String method;
    private boolean shouldDisable;

    public List<Parameter> getParameters() {
        return parameters;
    }

    public String getClassName() {
        return clazz;
    }

    public String getMethod() {
        return method;
    }

    public ParameterTamperingView(java.awt.Frame parent, boolean modal,
            String clazz, String method, List<Parameter> parameters, boolean isReturnValue) {

        super(parent, modal);
        initComponents();

        this.shouldDisable = false;
        this.clazz = clazz;
        this.method = method;

        lblClass.setText(clazz);
        lblMethod.setText(method);

        this.parameters = parameters;
        tblParameters.setModel( new ParameterTableModel(tblParameters,parameters) );

        ParameterTableCellRenderer renderer =
                new ParameterTableCellRenderer();
        tblParameters.setDefaultEditor(JButton.class, renderer);
        tblParameters.setDefaultRenderer(JButton.class, renderer);
        tblParameters.setRowHeight(25);

        setTableDimensions();

        if ( isReturnValue ) {
            setTitle("Edit return value");
            lblNature.setText("Return value");
            tblParameters.getColumnModel().getColumn(0).setResizable(true);
            tblParameters.getColumnModel().getColumn(0).setWidth(1);
            tblParameters.getColumnModel().getColumn(0).setMaxWidth(1);
            tblParameters.getColumnModel().getColumn(0).setMinWidth(1);
            tblParameters.getColumnModel().getColumn(0).setHeaderValue("");
            tblParameters.getColumnModel().getColumn(0).setResizable(false);
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        lblClass = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblParameters = new javax.swing.JTable();
        lblNature = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblMethod = new javax.swing.JLabel();
        btnAcceptChanges = new javax.swing.JButton();
        btnAcceptAndDisable = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.aspect.snoop.JavaSnoop.class).getContext().getResourceMap(ParameterTamperingView.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        lblClass.setFont(resourceMap.getFont("lblClass.font")); // NOI18N
        lblClass.setText(resourceMap.getString("lblClass.text")); // NOI18N
        lblClass.setName("lblClass"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblParameters.setModel(new javax.swing.table.DefaultTableModel(
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
        tblParameters.setName("tblParameters"); // NOI18N
        jScrollPane1.setViewportView(tblParameters);

        lblNature.setText(resourceMap.getString("lblNature.text")); // NOI18N
        lblNature.setName("lblNature"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        lblMethod.setFont(resourceMap.getFont("lblMethod.font")); // NOI18N
        lblMethod.setText(resourceMap.getString("lblMethod.text")); // NOI18N
        lblMethod.setName("lblMethod"); // NOI18N

        btnAcceptChanges.setText(resourceMap.getString("btnAcceptChanges.text")); // NOI18N
        btnAcceptChanges.setName("btnAcceptChanges"); // NOI18N
        btnAcceptChanges.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAcceptChangesActionPerformed(evt);
            }
        });

        btnAcceptAndDisable.setText(resourceMap.getString("btnAcceptAndDisable.text")); // NOI18N
        btnAcceptAndDisable.setName("btnAcceptAndDisable"); // NOI18N
        btnAcceptAndDisable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAcceptAndDisableActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(lblClass))
                    .addComponent(lblNature)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblMethod))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnAcceptAndDisable)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAcceptChanges)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblClass))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblMethod))
                .addGap(18, 18, 18)
                .addComponent(lblNature)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAcceptChanges)
                    .addComponent(btnAcceptAndDisable))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAcceptChangesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAcceptChangesActionPerformed
        dispose();
    }//GEN-LAST:event_btnAcceptChangesActionPerformed

    private void btnAcceptAndDisableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAcceptAndDisableActionPerformed
        shouldDisable = true;
        dispose();
}//GEN-LAST:event_btnAcceptAndDisableActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                ArrayList<Parameter> params = new ArrayList<Parameter>();

                params.add ( new Parameter(1,"string test") );
                params.add ( new Parameter(2, "foobar".getBytes()) );
                params.add ( new Parameter(3, 3L) );
                params.add ( new Parameter(4, true) );
                params.add ( new Parameter(5,new SnoopSession()));

                ParameterTamperingView dialog = new ParameterTamperingView(
                        new javax.swing.JFrame(), true,
                        "com.foo.bar.service.eating.spoop.Fooclass",
                        "EatSteak",
                        params,true);

                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    private void setTableDimensions() {
        tblParameters.getColumnModel().getColumn(0).setWidth(40);
        tblParameters.getColumnModel().getColumn(0).setMinWidth(40);
        tblParameters.getColumnModel().getColumn(0).setMaxWidth(40);
        tblParameters.getColumnModel().getColumn(0).setResizable(false);

        tblParameters.getColumnModel().getColumn(3).setWidth(55);
        tblParameters.getColumnModel().getColumn(3).setMinWidth(55);
        tblParameters.getColumnModel().getColumn(3).setMaxWidth(4550);
        tblParameters.getColumnModel().getColumn(3).setResizable(false);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAcceptAndDisable;
    private javax.swing.JButton btnAcceptChanges;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblClass;
    private javax.swing.JLabel lblMethod;
    private javax.swing.JLabel lblNature;
    private javax.swing.JTable tblParameters;
    // End of variables declaration//GEN-END:variables

    public boolean shouldDisable() {
        return shouldDisable;
    }

}