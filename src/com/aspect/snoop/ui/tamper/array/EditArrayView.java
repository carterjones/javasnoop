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

package com.aspect.snoop.ui.tamper.array;

import com.aspect.snoop.ui.tamper.common.AddItemView;
import java.util.Arrays;
import javax.swing.JOptionPane;

public class EditArrayView extends javax.swing.JDialog {

    private Object[] array;

    public EditArrayView(java.awt.Frame parent, boolean modal, Object[] array) {
        super(parent, modal);
        initComponents();
        this.array = array;

        tblListItems.setModel( new ArrayTableModel(array) );
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        lblClassName = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblListItems = new javax.swing.JTable();
        btnMoveUp = new javax.swing.JButton();
        btnMoveDown = new javax.swing.JButton();
        btnDeleteItem = new javax.swing.JButton();
        btnAddItem = new javax.swing.JButton();
        btnAccept = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.aspect.snoop.JavaSnoop.class).getContext().getResourceMap(EditArrayView.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        lblClassName.setText(resourceMap.getString("lblClassName.text")); // NOI18N
        lblClassName.setName("lblClassName"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblListItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Title 2"
            }
        ));
        tblListItems.setName("tblListItems"); // NOI18N
        jScrollPane2.setViewportView(tblListItems);

        btnMoveUp.setText(resourceMap.getString("btnMoveUp.text")); // NOI18N
        btnMoveUp.setName("btnMoveUp"); // NOI18N
        btnMoveUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveUpActionPerformed(evt);
            }
        });

        btnMoveDown.setText(resourceMap.getString("btnMoveDown.text")); // NOI18N
        btnMoveDown.setName("btnMoveDown"); // NOI18N
        btnMoveDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveDownActionPerformed(evt);
            }
        });

        btnDeleteItem.setText(resourceMap.getString("btnDeleteItem.text")); // NOI18N
        btnDeleteItem.setName("btnDeleteItem"); // NOI18N
        btnDeleteItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteItemActionPerformed(evt);
            }
        });

        btnAddItem.setText(resourceMap.getString("btnAddItem.text")); // NOI18N
        btnAddItem.setName("btnAddItem"); // NOI18N
        btnAddItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddItemActionPerformed(evt);
            }
        });

        btnAccept.setText(resourceMap.getString("btnAccept.text")); // NOI18N
        btnAccept.setName("btnAccept"); // NOI18N
        btnAccept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAcceptActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(lblClassName))
                    .addComponent(jLabel3)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btnMoveUp, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAddItem, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnMoveDown)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDeleteItem)))
                        .addGap(117, 117, 117)
                        .addComponent(btnAccept)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblClassName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnAddItem)
                            .addComponent(btnMoveUp))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnMoveDown)
                            .addComponent(btnDeleteItem)))
                    .addComponent(btnAccept))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMoveUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveUpActionPerformed
        int idx = tblListItems.getSelectedRow();

        if ( idx == -1 ) {
            return;
        }

        if ( idx == 0 ) {
            return;
        }

        Object above = array[idx-1];
        Object current = array[idx];

        array[idx-1] = current;
        array[idx] = above;

        tblListItems.changeSelection(idx-1, 0, false, false);

        tblListItems.updateUI();
}//GEN-LAST:event_btnMoveUpActionPerformed

    private void btnMoveDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveDownActionPerformed

        int idx = tblListItems.getSelectedRow();

        if ( idx == -1 ) {
            return;
        }

        if ( idx == array.length-1 ) {
            return;
        }

        Object below = array[idx+1];
        Object current = array[idx];

        array[idx+1] = current;
        array[idx] = below;

        tblListItems.changeSelection(idx+1, 0, false, false);

        tblListItems.updateUI();
    }//GEN-LAST:event_btnMoveDownActionPerformed

    private void btnDeleteItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteItemActionPerformed

        try {
            int idx = tblListItems.getSelectedRow();

            if ( idx == -1 ) {
                return;
            }

            Object[] newArray = new Object[array.length-1];
            int j = 0;

            for(int i=0;i<idx;i++) {
                newArray[j++] = array[i];
            }

            for(int i=idx+1;i<array.length;i++) {
                newArray[j++] = array[i];
            }

            array = newArray;

        } catch(Exception e) { }

        tblListItems.updateUI();
    }//GEN-LAST:event_btnDeleteItemActionPerformed

    private void btnAddItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddItemActionPerformed

        Class c = null;

        AddItemView view = null;

        if ( array.getClass().equals(new String[]{}.getClass()) ) {
            c = String.class;
        } else if ( array.getClass().equals(new Byte[]{}.getClass()) ) {
            c = Byte.class;
        } else if ( array.getClass().equals(new Boolean[]{}.getClass()) ) {
            c = Boolean.class;
        } else if ( array.getClass().equals(new Character[]{}.getClass()) ) {
            c = Character.class;
        } else if ( array.getClass().equals(new Short[]{}.getClass()) ) {
            c = Short.class;
        } else if ( array.getClass().equals(new Integer[]{}.getClass()) ) {
            c = Integer.class;
        } else if ( array.getClass().equals(new Long[]{}.getClass()) ) {
            c = Long.class;
        } else if ( array.getClass().equals(new Float[]{}.getClass()) ) {
            c = Float.class;
        } else if ( array.getClass().equals(new Double[]{}.getClass()) ) {
            c = Double.class;
        } else if ( array.getClass().equals(new Object[]{}.getClass() )) {
            c = Object.class;
        }

        if ( c != null ) {
            view = new AddItemView(this, true, c);
            view.setVisible(true);

            while(view.isShowing()) {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException ex) { }
            }

            if ( view.getNewItem() != null ) {
                Object[] newArray = Arrays.copyOf(array, array.length+1);
                newArray[array.length] = view.getNewItem();
                this.array = newArray;
                tblListItems.updateUI();
            }

        } else {
            JOptionPane.showMessageDialog(this, "Can't add custom item class: "+array.getClass(),"Error adding new item",JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btnAddItemActionPerformed

    private void btnAcceptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAcceptActionPerformed
        dispose();
    }//GEN-LAST:event_btnAcceptActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                EditArrayView dialog = new EditArrayView(new javax.swing.JFrame(), true, new String[] {"foo", "bar" });
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAccept;
    private javax.swing.JButton btnAddItem;
    private javax.swing.JButton btnDeleteItem;
    private javax.swing.JButton btnMoveDown;
    private javax.swing.JButton btnMoveUp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblClassName;
    private javax.swing.JTable tblListItems;
    // End of variables declaration//GEN-END:variables

}
