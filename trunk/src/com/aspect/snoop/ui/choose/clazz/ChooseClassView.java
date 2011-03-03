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

package com.aspect.snoop.ui.choose.clazz;

import com.aspect.snoop.util.ClasspathUtil;
import com.aspect.snoop.util.ReflectionUtil;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;

public class ChooseClassView extends javax.swing.JDialog {

    List<Class> allClasses;
    List<Class> filteredClasses;
    Class selectedClass;

    private String className;

    public String getClassName() {
        return className;
    }

    public ChooseClassView(JDialog parent, List<Class> classes) {
        super(parent, true);
        initComponents();
        customInit(classes);
    }

    public ChooseClassView(JDialog parent, List<Class> classes, boolean showMainsByDefault) {
        super(parent, true);
        initComponents();
        chkOnlyMains.setSelected(showMainsByDefault);
        customInit(classes);
    }


    public ChooseClassView(java.awt.Frame parent, List<Class> classes, boolean showMainsByDefault) {
        super(parent, true);
        initComponents();
        chkOnlyMains.setSelected(showMainsByDefault);
        customInit(classes);
    }

    public ChooseClassView(java.awt.Frame parent, List<Class> classes) {
        this(parent,classes,false);
    }

    private void customInit(List<Class> classes) {

        setTitle("Choose class - " + classes.size() + " classes loaded");

        Comparator c = new ClassComparator();
        Collections.sort(classes, c);

        this.allClasses = classes;
        this.filteredClasses = classes;

        lstClasses.setCellRenderer(new ClassListCellRenderer());

        lstClasses.addMouseListener(
                new MouseListener() {

            public void mouseClicked(MouseEvent e) {

                selectedClass = (Class)lstClasses.getSelectedValue();
                txtClass.setText( selectedClass.getName() );

                if ( e.getClickCount() == 2 ) {
                    // user double clicked an item selection

                    finalizeSelection();
                    dispose();
                }
            }

            public void mousePressed(MouseEvent e) { }

            public void mouseReleased(MouseEvent e) { }

            public void mouseEntered(MouseEvent e) { }

            public void mouseExited(MouseEvent e) { }

        }

        );

        listAllClasses();

        filterClasses();
    }

    private void listSomeClasses(String substring) {
        DefaultListModel list = new DefaultListModel();

        String lowered = substring.toLowerCase();

        for( Class clazz : filteredClasses ) {

            if ( clazz.getName().toLowerCase().contains(lowered)) {
                list.addElement(clazz);
            }

        }

        lstClasses.setModel(list);
    }

    private void listAllClasses() {
        DefaultListModel list = new DefaultListModel();

        for( Class clazz : allClasses ) {
            list.addElement(clazz);
        }

        lstClasses.setModel(list);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtClass = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstClasses = new javax.swing.JList();
        chkOnlyMains = new javax.swing.JCheckBox();
        chkHideJavaClasses = new javax.swing.JCheckBox();
        chkHideJavaSnoopClasses = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.aspect.snoop.JavaSnoop.class).getContext().getResourceMap(ChooseClassView.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        setName("Form"); // NOI18N

        txtClass.setText(resourceMap.getString("txtClass.text")); // NOI18N
        txtClass.setName("txtClass"); // NOI18N
        txtClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClassActionPerformed(evt);
            }
        });
        txtClass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtClassKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtClassKeyTyped(evt);
            }
        });

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        lstClasses.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { " " };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstClasses.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstClasses.setName("lstClasses"); // NOI18N
        jScrollPane1.setViewportView(lstClasses);

        chkOnlyMains.setText(resourceMap.getString("chkOnlyMains.text")); // NOI18N
        chkOnlyMains.setFocusable(false);
        chkOnlyMains.setName("chkOnlyMains"); // NOI18N
        chkOnlyMains.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOnlyMainsActionPerformed(evt);
            }
        });

        chkHideJavaClasses.setSelected(true);
        chkHideJavaClasses.setText(resourceMap.getString("chkHideJavaClasses.text")); // NOI18N
        chkHideJavaClasses.setName("chkHideJavaClasses"); // NOI18N
        chkHideJavaClasses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkHideJavaClassesActionPerformed(evt);
            }
        });

        chkHideJavaSnoopClasses.setSelected(true);
        chkHideJavaSnoopClasses.setText(resourceMap.getString("chkHideJavaSnoopClasses.text")); // NOI18N
        chkHideJavaSnoopClasses.setName("chkHideJavaSnoopClasses"); // NOI18N
        chkHideJavaSnoopClasses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkHideJavaSnoopClassesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtClass)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkOnlyMains)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(chkHideJavaClasses)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkHideJavaSnoopClasses)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(chkOnlyMains, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkHideJavaClasses)
                            .addComponent(chkHideJavaSnoopClasses))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {chkHideJavaClasses, chkHideJavaSnoopClasses, chkOnlyMains});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtClassKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtClassKeyTyped

        /*
        String substring  = txtClass.getText();

        if (    evt.getKeyChar() != java.awt.event.KeyEvent.VK_ENTER &&
                evt.getKeyChar() != java.awt.event.KeyEvent.VK_BACK_SPACE ) { // backspace

            int pos = txtClass.getCaretPosition();

            if ( pos != 0 ) {
                String s1 = substring.substring(0, pos);
                String s2 = substring.substring(pos,txtClass.getText().length());
                substring = s1 + evt.getKeyChar() + s2;
            } else {
                substring += evt.getKeyChar();
            }
        }

        if ( substring.length() == 0 ) {
            listAllClasses();
        } else {
            listSomeClasses(substring);
        }
         */

    }//GEN-LAST:event_txtClassKeyTyped

    private void txtClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClassActionPerformed
        lstClasses.setSelectedIndex(0);
        lstClasses.requestFocus();
    }//GEN-LAST:event_txtClassActionPerformed

    private void chkOnlyMainsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOnlyMainsActionPerformed

        filterClasses();

    }//GEN-LAST:event_chkOnlyMainsActionPerformed

    private void chkHideJavaClassesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkHideJavaClassesActionPerformed

        filterClasses();

    }//GEN-LAST:event_chkHideJavaClassesActionPerformed

    private void chkHideJavaSnoopClassesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkHideJavaSnoopClassesActionPerformed

        filterClasses();

    }//GEN-LAST:event_chkHideJavaSnoopClassesActionPerformed

    private void txtClassKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtClassKeyReleased
        String substring  = txtClass.getText();
        if ( substring.length() == 0 ) {
            filterClasses();
        } else {
            filterClasses();
            listSomeClasses(substring);
        }
    }//GEN-LAST:event_txtClassKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkHideJavaClasses;
    private javax.swing.JCheckBox chkHideJavaSnoopClasses;
    private javax.swing.JCheckBox chkOnlyMains;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList lstClasses;
    private javax.swing.JTextField txtClass;
    // End of variables declaration//GEN-END:variables


    private void finalizeSelection() {
        className = txtClass.getText();
    }

    private void filterClasses() {

        filteredClasses = new ArrayList<Class>();

        DefaultListModel list = new DefaultListModel();

        for ( Class clazz : allClasses ) {

            String cls = clazz.getName();

            boolean shouldShow = true;
            boolean isJavaOrSunClass = ClasspathUtil.isJavaOrSunClass(cls);

            if ( chkHideJavaClasses.isSelected() ) {
                shouldShow = ! isJavaOrSunClass;
            }

            if ( shouldShow && chkHideJavaSnoopClasses.isSelected() ) {
               shouldShow = ! ClasspathUtil.isJavaSnoopClass(cls);
            }

            if ( shouldShow && ! isJavaOrSunClass && chkOnlyMains.isSelected() ) {
                if ( ! ReflectionUtil.hasMainClass(clazz) ) {
                    shouldShow = false;
                }
            }

            if ( shouldShow ) {
                filteredClasses.add(clazz);
                list.addElement(clazz);
            }

        }

        lstClasses.setModel(list);

    }

    public Class getChosenClass() {
        return selectedClass;
    }

    class ClassListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
            JList list,
            Object value,   // value to display
            int index,      // cell index
            boolean iss,    // is the cell selected
            boolean chf)    // the list and the cell have the focus
        {
                /* The DefaultListCellRenderer class will take care of
             * the JLabels text property, it's foreground and background
             * colors, and so on.
             */
            super.getListCellRendererComponent(list, value, index, iss, chf);

            /*
             * We additionally set the JLabels icon property here.
             */
             Class c = (Class)value;
             String name = c.getName();

             if (c.isInterface()) {
                 name += " (interface)";
             } else if (Modifier.isAbstract(c.getModifiers())) {
                 name += " (abstract)";
             }

            return super.getListCellRendererComponent(list,name,index,iss,chf);
        }
    }
}
