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

package com.aspect.snoop.ui.choose.process;

import com.aspect.snoop.SnoopSession;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jdesktop.application.Action;

public class NewProcessInfoView extends javax.swing.JDialog {

    ClasspathTreeModel classpath;

    public NewProcessInfoView(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        classpath = new ClasspathTreeModel(new DefaultMutableTreeNode(null));
        treeClasspath.setCellRenderer(new ClasspathTreeCellRenderer());
        treeClasses.setCellRenderer(new ClasspathTreeCellRenderer());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMainClass = new javax.swing.JPanel();
        txtMainClass = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        pnlClasspathView = new javax.swing.JPanel();
        tabClasspath = new javax.swing.JTabbedPane();
        pnlJarView = new javax.swing.JPanel();
        btnRemoveEntry = new javax.swing.JButton();
        btnAddEntry = new javax.swing.JButton();
        pnlClasspath = new javax.swing.JScrollPane();
        treeClasspath = new javax.swing.JTree();
        pnlClassesView = new javax.swing.JPanel();
        pnlClasspath1 = new javax.swing.JScrollPane();
        treeClasses = new javax.swing.JTree();
        pnlExecutionInfo = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtWorkingDir = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtArguments = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtJvmArguments = new javax.swing.JTextField();
        btnStartProcess = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.aspect.snoop.JavaSnoop.class).getContext().getResourceMap(NewProcessInfoView.class);
        pnlMainClass.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("pnlMainClass.border.title"))); // NOI18N
        pnlMainClass.setName("pnlMainClass"); // NOI18N

        txtMainClass.setText(resourceMap.getString("txtMainClass.text")); // NOI18N
        txtMainClass.setName("txtMainClass"); // NOI18N

        btnSearch.setText(resourceMap.getString("btnSearch.text")); // NOI18N
        btnSearch.setName("btnSearch"); // NOI18N

        javax.swing.GroupLayout pnlMainClassLayout = new javax.swing.GroupLayout(pnlMainClass);
        pnlMainClass.setLayout(pnlMainClassLayout);
        pnlMainClassLayout.setHorizontalGroup(
            pnlMainClassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMainClassLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtMainClass, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch)
                .addContainerGap())
        );
        pnlMainClassLayout.setVerticalGroup(
            pnlMainClassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainClassLayout.createSequentialGroup()
                .addGroup(pnlMainClassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSearch)
                    .addComponent(txtMainClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlClasspathView.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("pnlClasspathView.border.title"))); // NOI18N
        pnlClasspathView.setName("pnlClasspathView"); // NOI18N

        tabClasspath.setName("tabClasspath"); // NOI18N

        pnlJarView.setName("pnlJars"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(com.aspect.snoop.JavaSnoop.class).getContext().getActionMap(NewProcessInfoView.class, this);
        btnRemoveEntry.setAction(actionMap.get("removeClasspathEntry")); // NOI18N
        btnRemoveEntry.setText(resourceMap.getString("btnRemoveEntry.text")); // NOI18N
        btnRemoveEntry.setName("btnRemoveEntry"); // NOI18N

        btnAddEntry.setAction(actionMap.get("addClasspathEntry")); // NOI18N
        btnAddEntry.setText(resourceMap.getString("btnAddEntry.text")); // NOI18N
        btnAddEntry.setName("btnAddEntry"); // NOI18N

        pnlClasspath.setName("pnlClasspath"); // NOI18N

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        treeClasspath.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeClasspath.setName("treeClasspath"); // NOI18N
        treeClasspath.setRootVisible(false);
        pnlClasspath.setViewportView(treeClasspath);

        javax.swing.GroupLayout pnlJarViewLayout = new javax.swing.GroupLayout(pnlJarView);
        pnlJarView.setLayout(pnlJarViewLayout);
        pnlJarViewLayout.setHorizontalGroup(
            pnlJarViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlJarViewLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlJarViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlClasspath, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                    .addGroup(pnlJarViewLayout.createSequentialGroup()
                        .addComponent(btnAddEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoveEntry)))
                .addContainerGap())
        );
        pnlJarViewLayout.setVerticalGroup(
            pnlJarViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlJarViewLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlClasspath, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlJarViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddEntry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRemoveEntry))
                .addGap(47, 47, 47))
        );

        tabClasspath.addTab(resourceMap.getString("pnlJars.TabConstraints.tabTitle"), pnlJarView); // NOI18N

        pnlClassesView.setName("pnlClassesView"); // NOI18N

        pnlClasspath1.setName("pnlClasspath1"); // NOI18N

        treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        treeClasses.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeClasses.setName("treeClasses"); // NOI18N
        treeClasses.setRootVisible(false);
        pnlClasspath1.setViewportView(treeClasses);

        javax.swing.GroupLayout pnlClassesViewLayout = new javax.swing.GroupLayout(pnlClassesView);
        pnlClassesView.setLayout(pnlClassesViewLayout);
        pnlClassesViewLayout.setHorizontalGroup(
            pnlClassesViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 384, Short.MAX_VALUE)
            .addGroup(pnlClassesViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlClassesViewLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(pnlClasspath1, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        pnlClassesViewLayout.setVerticalGroup(
            pnlClassesViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 152, Short.MAX_VALUE)
            .addGroup(pnlClassesViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlClassesViewLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(pnlClasspath1, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        tabClasspath.addTab(resourceMap.getString("pnlClassesView.TabConstraints.tabTitle"), pnlClassesView); // NOI18N

        javax.swing.GroupLayout pnlClasspathViewLayout = new javax.swing.GroupLayout(pnlClasspathView);
        pnlClasspathView.setLayout(pnlClasspathViewLayout);
        pnlClasspathViewLayout.setHorizontalGroup(
            pnlClasspathViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClasspathViewLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabClasspath, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlClasspathViewLayout.setVerticalGroup(
            pnlClasspathViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabClasspath, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pnlExecutionInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("pnlExecutionInfo.border.title"))); // NOI18N
        pnlExecutionInfo.setName("pnlExecutionInfo"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        txtWorkingDir.setText(resourceMap.getString("txtWorkingDir.text")); // NOI18N
        txtWorkingDir.setName("txtWorkingDir"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        txtArguments.setText(resourceMap.getString("txtArguments.text")); // NOI18N
        txtArguments.setName("txtArguments"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        txtJvmArguments.setText(resourceMap.getString("txtJvmArguments.text")); // NOI18N
        txtJvmArguments.setName("txtJvmArguments"); // NOI18N

        javax.swing.GroupLayout pnlExecutionInfoLayout = new javax.swing.GroupLayout(pnlExecutionInfo);
        pnlExecutionInfo.setLayout(pnlExecutionInfoLayout);
        pnlExecutionInfoLayout.setHorizontalGroup(
            pnlExecutionInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlExecutionInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlExecutionInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtWorkingDir, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1)
                    .addComponent(txtArguments, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
                    .addComponent(jLabel2)
                    .addComponent(txtJvmArguments, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlExecutionInfoLayout.setVerticalGroup(
            pnlExecutionInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlExecutionInfoLayout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtWorkingDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtArguments, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtJvmArguments, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnStartProcess.setAction(actionMap.get("startProcess")); // NOI18N
        btnStartProcess.setText(resourceMap.getString("btnStartProcess.text")); // NOI18N
        btnStartProcess.setName("btnStartProcess"); // NOI18N

        btnCancel.setAction(actionMap.get("close")); // NOI18N
        btnCancel.setText(resourceMap.getString("btnCancel.text")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlClasspathView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlMainClass, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlExecutionInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnStartProcess)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlMainClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(pnlClasspathView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlExecutionInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStartProcess)
                    .addComponent(btnCancel))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NewProcessInfoView dialog = new NewProcessInfoView(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    File lastSelectedDir;
    SnoopSession model;

    public SnoopSession getSnoopSession() {
        return model;
    }

    @Action
    public void addClasspathEntry() {

        JFileChooser fc;

        if ( lastSelectedDir == null ) {
            fc = new JFileChooser(new File(System.getProperty("user.dir")));
        } else {
            fc = new JFileChooser(lastSelectedDir);
        }

        fc.setApproveButtonText("Select");
        fc.setDialogTitle("Select root folders or JAR files");
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setFileFilter(new FileFilter(){
            @Override
            public boolean accept(File f) {
                return
                        f.isDirectory() ||
                        f.getName().endsWith(".jar") ||
                        f.getName().endsWith(".class");
            }

            @Override
            public String getDescription() {
                return "Directories and JAR files";
            }
        });

        fc.setMultiSelectionEnabled(true);
        int rc = fc.showOpenDialog(this);
        if (rc == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fc.getSelectedFiles();
            lastSelectedDir = selectedFiles[0];
            
            for(File f : selectedFiles) {
                classpath.addEntry(f.getAbsolutePath());
            }
            classpath.reload();
        }
    }

    @Action
    public void removeClasspathEntry() {

        for(TreePath path : treeClasspath.getSelectionPaths()) {
            System.out.println("Gonna delete: " + path);
            
        }

    }

    @Action
    public void close() {
        model = null;
        dispose();
    }

    @Action
    public void startProcess() {
        model = new SnoopSession();
        model.setMainClass(txtMainClass.getText());
        model.setArguments(txtArguments.getText());
        model.setJavaArguments(txtJvmArguments.getText());
        model.setWorkingDir(txtWorkingDir.getText());
        model.setClasspathString( Util.convertListToString(classpath.getEntries(), ";") );
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddEntry;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnRemoveEntry;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnStartProcess;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel pnlClassesView;
    private javax.swing.JScrollPane pnlClasspath;
    private javax.swing.JScrollPane pnlClasspath1;
    private javax.swing.JPanel pnlClasspathView;
    private javax.swing.JPanel pnlExecutionInfo;
    private javax.swing.JPanel pnlJarView;
    private javax.swing.JPanel pnlMainClass;
    private javax.swing.JTabbedPane tabClasspath;
    private javax.swing.JTree treeClasses;
    private javax.swing.JTree treeClasspath;
    private javax.swing.JTextField txtArguments;
    private javax.swing.JTextField txtJvmArguments;
    private javax.swing.JTextField txtMainClass;
    private javax.swing.JTextField txtWorkingDir;
    // End of variables declaration//GEN-END:variables

}
