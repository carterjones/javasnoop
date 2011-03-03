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
import com.aspect.snoop.util.ClasspathUtil;
import com.aspect.snoop.util.UIUtil;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.jdesktop.application.Action;

public class FunctionSearchView extends javax.swing.JDialog {

    List<Class> classes;
    Method methodChosen;

    public Method getChosenMethod() {
        return methodChosen;
    }

    public FunctionSearchView(javax.swing.JDialog parent, boolean modal, List<Class> classes) {

        super(parent, modal);

        initComponents();

        this.classes = classes;
        this.methodChosen = null;

        List<AccessibleObject> empty = new ArrayList<AccessibleObject>();

        tblResults.setModel( new MethodTableModel(empty) );
        tblResults.setRowHeight(20);

        tblResults.getColumnModel().getColumn(0).setPreferredWidth(75);
        tblResults.getColumnModel().getColumn(1).setPreferredWidth(575);


        tblResults.addMouseListener(
                new MouseListener() {

                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            // user double clicked an item selection
                            int sel = tblResults.getSelectedRow();
                            if ( sel != -1 ) {
                                methodChosen = (Method) tblResults.getModel().getValueAt(sel,1);
                                dispose();
                            }
                        }
                    }

                    public void mousePressed(MouseEvent e) {
                    }

                    public void mouseReleased(MouseEvent e) {
                    }

                    public void mouseEntered(MouseEvent e) {
                    }

                    public void mouseExited(MouseEvent e) {
                    }
                });
    }

    public Method getMethodChosen() {
        return methodChosen;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtMethod = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        chkHideJavaClasses = new javax.swing.JCheckBox();
        chkHideJavaSnoopClasses = new javax.swing.JCheckBox();
        chkReturnType = new javax.swing.JCheckBox();
        lstReturnType = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblResults = new javax.swing.JTable();
        chkIgnoreCase = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.aspect.snoop.JavaSnoop.class).getContext().getResourceMap(FunctionSearchView.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        txtMethod.setText(resourceMap.getString("txtMethod.text")); // NOI18N
        txtMethod.setName("txtMethod"); // NOI18N
        txtMethod.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtMethodKeyTyped(evt);
            }
        });

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(com.aspect.snoop.JavaSnoop.class).getContext().getActionMap(FunctionSearchView.class, this);
        btnSearch.setAction(actionMap.get("populateResults")); // NOI18N
        btnSearch.setText(resourceMap.getString("btnSearch.text")); // NOI18N
        btnSearch.setFocusable(false);
        btnSearch.setName("btnSearch"); // NOI18N

        chkHideJavaClasses.setSelected(true);
        chkHideJavaClasses.setText(resourceMap.getString("chkHideJavaClasses.text")); // NOI18N
        chkHideJavaClasses.setFocusable(false);
        chkHideJavaClasses.setName("chkHideJavaClasses"); // NOI18N

        chkHideJavaSnoopClasses.setSelected(true);
        chkHideJavaSnoopClasses.setText(resourceMap.getString("chkHideJavaSnoopClasses.text")); // NOI18N
        chkHideJavaSnoopClasses.setFocusable(false);
        chkHideJavaSnoopClasses.setName("chkHideJavaSnoopClasses"); // NOI18N

        chkReturnType.setAction(actionMap.get("flipReturnType")); // NOI18N
        chkReturnType.setText(resourceMap.getString("chkReturnType.text")); // NOI18N
        chkReturnType.setName("chkReturnType"); // NOI18N

        lstReturnType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "void", "String", "boolean", "byte", "char", "short", "int", "long", "double", "float" }));
        lstReturnType.setEnabled(false);
        lstReturnType.setName("lstReturnType"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblResults.setModel(new javax.swing.table.DefaultTableModel(
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
        tblResults.setName("tblResults"); // NOI18N
        jScrollPane2.setViewportView(tblResults);

        chkIgnoreCase.setSelected(true);
        chkIgnoreCase.setText(resourceMap.getString("chkIgnoreCase.text")); // NOI18N
        chkIgnoreCase.setName("chkIgnoreCase"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtMethod, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSearch)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chkHideJavaClasses)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkHideJavaSnoopClasses))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chkReturnType)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lstReturnType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkIgnoreCase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(12, 12, 12))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(12, 12, 12)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(chkHideJavaClasses)
                    .addComponent(chkHideJavaSnoopClasses))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch)
                    .addComponent(chkReturnType)
                    .addComponent(lstReturnType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkIgnoreCase))
                .addContainerGap(265, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(66, 66, 66)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtMethodKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMethodKeyTyped

        if ( evt.getKeyChar() == java.awt.event.KeyEvent.VK_ENTER ) {
            btnSearch.setEnabled(false);
            populateResults();
            btnSearch.setEnabled(true);
        }

    }//GEN-LAST:event_txtMethodKeyTyped

    @Action
    public void populateResults() {

        if ( txtMethod.getText().length() < 3 ) {
            UIUtil.showErrorMessage(this, "You must type at least 3 characters before searching");
            return;
        }

        String substring = txtMethod.getText();
        String loweredSubstring = substring.toLowerCase();

        List<AccessibleObject> hits = new ArrayList<AccessibleObject>();

        for(int i = 0;i<classes.size(); i++) {

            Class cls = classes.get(i);

            if ( chkHideJavaClasses.isSelected() && ClasspathUtil.isJavaOrSunClass(cls.getName()) ) {
                continue;
            }

            if ( chkHideJavaSnoopClasses.isSelected() && ClasspathUtil.isJavaSnoopClass(cls.getName()) ) {
                continue;
            }

            try {
                Method[] methods = cls.getDeclaredMethods();
                Constructor[] constructors = cls.getDeclaredConstructors();

                for(int j=0;j<constructors.length;j++) {
                    Constructor constructor = constructors[j];
                    String loweredMethodName = constructor.getName().toLowerCase();

                    if ( (! chkIgnoreCase.isSelected() && constructor.getName().contains(substring)) ||
                           (chkIgnoreCase.isSelected() && loweredMethodName.contains(loweredSubstring)) ) {

                        if ( chkReturnType.isSelected() ) {
                            if ( isReturnTypeMatch(Void.class,(String)lstReturnType.getSelectedItem()) ) {
                                hits.add(constructor);
                            }
                        } else {
                            hits.add(constructor);
                        }
                    }
                }

                for(int j=0; j<methods.length; j++) {
                    Method m = methods[j];
                    String loweredMethodName = m.getName().toLowerCase();

                    if ( (! chkIgnoreCase.isSelected() && m.getName().contains(substring)) ||
                           (chkIgnoreCase.isSelected() && loweredMethodName.contains(loweredSubstring)) ) {

                        if ( chkReturnType.isSelected() ) {
                            if ( isReturnTypeMatch(m.getReturnType(),(String)lstReturnType.getSelectedItem()) ) {
                                hits.add(m);
                            }
                        } else {
                            hits.add(m);
                        }
                    }
                }

            } catch (NoClassDefFoundError ncde) {
                ncde.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error e) {
                e.printStackTrace();
            }

        }

        MethodTableModel model = new MethodTableModel( hits );
        tblResults.setModel(model);

        tblResults.getColumnModel().getColumn(0).setPreferredWidth(75);
        tblResults.getColumnModel().getColumn(1).setPreferredWidth(575);

        tblResults.repaint();
        tblResults.updateUI();

        if ( hits.isEmpty() ) {
            UIUtil.showErrorMessage(this, "No methods found");
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearch;
    private javax.swing.JCheckBox chkHideJavaClasses;
    private javax.swing.JCheckBox chkHideJavaSnoopClasses;
    private javax.swing.JCheckBox chkIgnoreCase;
    private javax.swing.JCheckBox chkReturnType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JComboBox lstReturnType;
    private javax.swing.JTable tblResults;
    private javax.swing.JTextField txtMethod;
    // End of variables declaration//GEN-END:variables

    private boolean isReturnTypeMatch(Class c, String ret) {

        boolean result = false;

        if ("String".equals(ret) && c.equals(String.class)) {
            result = true;
        } else if ( "boolean".equals(ret) && (c.equals(boolean.class) || c.equals(Boolean.class))) {
            result = true;
        } else if ( "char".equals(ret) && (c.equals(char.class) || c.equals(Character.class)) ) {
            result = true;
        } else if ( "byte".equals(ret) && (c.equals(byte.class) || c.equals(Byte.class)) ) {
            result = true;
        } else if ( "short".equals(ret) && (c.equals(short.class) || c.equals(Short.class)) ) {
            result = true;
        } else if ( "int".equals(ret) && (c.equals(int.class) || c.equals(Integer.class)) ) {
            result = true;
        } else if ( "long".equals(ret) && (c.equals(long.class) || c.equals(Long.class)) ) {
            result = true;
        } else if ( "double".equals(ret) && (c.equals(Double.class) || c.equals(double.class)) ) {
            result = true;
        } else if ( "float".equals(ret) && (c.equals(float.class) || c.equals(Float.class)) ) {
            result = true;
        } else if ( "void".equals(ret) && (c.equals(void.class) || c.equals(Void.class)) ) {
            result = true;
        }

        return result;
    }

    @Action
    public void flipReturnType() {
        lstReturnType.setEnabled(chkReturnType.isSelected());
    }
}
