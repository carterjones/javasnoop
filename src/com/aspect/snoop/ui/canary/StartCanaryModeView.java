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

import com.aspect.snoop.MethodWrapper;
import com.aspect.snoop.agent.AgentCommunicationException;
import com.aspect.snoop.agent.AgentLogger;
import com.aspect.snoop.agent.SnoopAgent;
import com.aspect.snoop.agent.manager.InstrumentationException;
import com.aspect.snoop.agent.manager.InstrumentationManager;
import com.aspect.snoop.agent.manager.MethodChanges;
import com.aspect.snoop.util.CanaryUtil;
import com.aspect.snoop.util.ClasspathUtil;
import com.aspect.snoop.util.StringUtil;
import com.aspect.snoop.util.UIUtil;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import org.jdesktop.application.Action;

public class StartCanaryModeView extends javax.swing.JDialog {

    CanaryResultTableModel model;
    SwingWorker canaryWorker = null;

    public StartCanaryModeView(java.awt.Frame parent, boolean modal) {

        super(parent, modal);

        initComponents();

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

        btnStartCanaryMode = new javax.swing.JButton();
        txtCanary = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        lstDataTypes = new javax.swing.JComboBox();
        btnStopCanaryMode = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCanaries = new javax.swing.JTable();
        txtPackage = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        prgCanary = new javax.swing.JProgressBar();
        chkIncludeJava = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.aspect.snoop.JavaSnoop.class).getContext().getResourceMap(StartCanaryModeView.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

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

        prgCanary.setName("prgCanary"); // NOI18N

        chkIncludeJava.setText(resourceMap.getString("chkIncludeJava.text")); // NOI18N
        chkIncludeJava.setName("chkIncludeJava"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPackage, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkIncludeJava))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
                    .addComponent(prgCanary, javax.swing.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCanary)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lstDataTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnStartCanaryMode, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnStopCanaryMode)))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(208, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtCanary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(lstDataTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnStartCanaryMode, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnStopCanaryMode, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPackage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(chkIncludeJava))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(prgCanary, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(294, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @Action
    public void beginCanaryMode() throws AgentCommunicationException {

        final String canary = txtCanary.getText();
        final String pkg = txtPackage.getText();

        if (canary.length() <= 0) {
            UIUtil.showErrorMessage(this, "Please enter a value to track through the application");
            return;
        }

        // setup background action to find canaries.
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        final String type = (String) lstDataTypes.getSelectedItem();

        try {

            final InstrumentationManager manager = SnoopAgent.getAgentManager();
            manager.resetAllClasses();

            CanaryUtil.currentCanary = canary;

            String cType = null;

            if ("String".equals(type)) {
                cType = "java.lang.String";
            } else if ("short".equals(type)) {
                cType = "java.lang.Short";
            } else if ("int".equals(type)) {
                cType = "java.lang.Integer";
            } else if ("long".equals(type)) {
                cType = "java.lang.Long";
            } else if ("double".equals(type)) {
                cType = "java.lang.Double";
            } else if ("float".equals(type)) {
                cType = "java.lang.Float";
            }

            final String canaryType = cType;

            if (canary == null) {
                throw new InstrumentationException("Can't apply canary to unknown type: " + type);
            }

            canaryWorker = new SwingWorker() {

                int clsCount = 0;
                int mtdCount = 0;
                String finalMsg = null;

                @Override
                protected void done() {
                    prgCanary.setString(finalMsg);
                    prgCanary.setEnabled(false);
                }

                @Override
                protected Object doInBackground() throws Exception {

                    prgCanary.setString("Starting canary mode...");
                    prgCanary.setStringPainted(true);
                    prgCanary.setEnabled(true);

                    List<Class> loadedClasses = manager.getLoadedClasses();

                    List<Class> targetClasses = new ArrayList<Class>();
                    for(Class c : loadedClasses) {
                        if (c == null || c.isInterface() || c.isArray())
                            continue;

                        String clsName = c.getName();

                        if (pkg != null && pkg.length() > 0 && !clsName.startsWith(pkg))
                            continue;

                        if (ClasspathUtil.isJavaSnoopClass(clsName))
                            continue;

                        // FIXME: run into errors here on applets. should work.
                        if ( ! chkIncludeJava.isSelected() && ClasspathUtil.isJavaOrSunClass(clsName) )
                            continue;

                        targetClasses.add(c);
                    }

                    prgCanary.setMinimum(0);
                    prgCanary.setMaximum(targetClasses.size());

                    AgentLogger.debug("Applying canaries to " + targetClasses.size() + " classes...");

                    for (int i=0;i<targetClasses.size();i++) {

                        if ( isCancelled() )
                            return null;

                        Class c = targetClasses.get(i);
                        String clsName = c.getName();
                        
                        prgCanary.setValue(i);
                        prgCanary.setString("Adding canaries to class " + c.getName() + " (" + (i+1) + "/" + (targetClasses.size()+1) + ")");

                        List<MethodChanges> classChanges = new ArrayList<MethodChanges>();

                        Method[] methods = null;

                        try {
                            methods = c.getDeclaredMethods();
                        } catch (Throwable t) {
                            t.printStackTrace();
                            continue;
                        }

                        Constructor[] constructors = null;

                        try {
                            constructors = c.getDeclaredConstructors();
                        } catch (Throwable t) {
                            AgentLogger.trace("Failed to canary " + c.getName() + ": " + t.getMessage());
                        }

                        List<Member> members = new ArrayList<Member>();

                        if (methods != null) {
                            members.addAll(Arrays.asList(methods));
                        }

                        if (constructors != null) {
                            members.addAll(Arrays.asList(constructors));
                        }

                        for (Member m : members) {

                            if (Modifier.isAbstract(m.getModifiers())) {
                                continue;
                            }

                            Class[] types = null;

                            if (m instanceof Constructor) {
                                types = ((Constructor) m).getParameterTypes();
                            } else {
                                types = ((Method) m).getParameterTypes();
                            }

                            boolean alreadyMatched = false;

                            for (Class paramType : types) {

                                if ( alreadyMatched )
                                    continue;

                                boolean match = paramType.getName().equals(canaryType);

                                if ( match) {

                                    alreadyMatched = true;
                                    
                                    MethodWrapper wrapper = MethodWrapper.getWrapper((AccessibleObject) m);

                                    MethodChanges change = new MethodChanges((AccessibleObject) m);
                                    change.setNewStartSrc(CanaryUtil.getChirp(canaryType, clsName, wrapper.getName(), wrapper.getReturnType().getName()));

                                    AgentLogger.debug("Applying canary to " + wrapper.getDescription());
                                    classChanges.add(change);
                                    mtdCount++;
                                }
                            }
                        }

                        try {
                            manager.instrument(c, classChanges.toArray(new MethodChanges[]{}));
                            clsCount++;
                        } catch (InstrumentationException ex) {
                            AgentLogger.debug("Failed to apply canary to " + clsName + ": " + ex.getMessage());
                        }
                    }

                    prgCanary.setValue(prgCanary.getMaximum());

                    finalMsg = "Successfully canaried " + clsCount + " classes and " + mtdCount + " methods.";
                    AgentLogger.info(finalMsg);

                    return null;
                }
            };


            canaryWorker.execute();

            txtCanary.setEnabled(false);
            lstDataTypes.setEnabled(false);
            btnStartCanaryMode.setEnabled(false);
            btnStopCanaryMode.setEnabled(true);

        } catch (InstrumentationException ex) {
            UIUtil.showErrorMessage(this, StringUtil.exception2string(ex));
            ex.printStackTrace();
        }
    }

    @Action
    public void endCanaryMode() throws AgentCommunicationException {

        if ( canaryWorker != null ) {
            canaryWorker.cancel(true);
            while(!canaryWorker.isDone()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) { }
            }
        }

        txtCanary.setEnabled(true);
        lstDataTypes.setEnabled(true);
        btnStartCanaryMode.setEnabled(true);
        btnStopCanaryMode.setEnabled(false);

        try {
            SnoopAgent.getAgentManager().resetAllClasses();
            CanaryUtil.currentCanary = null;
        } catch (InstrumentationException ex) {
            UIUtil.showErrorMessage(this, StringUtil.exception2string(ex));
            ex.printStackTrace();
        }

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnStartCanaryMode;
    private javax.swing.JButton btnStopCanaryMode;
    private javax.swing.JCheckBox chkIncludeJava;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox lstDataTypes;
    private javax.swing.JProgressBar prgCanary;
    private javax.swing.JTable tblCanaries;
    private javax.swing.JTextField txtCanary;
    private javax.swing.JTextField txtPackage;
    // End of variables declaration//GEN-END:variables

    public void addChirp(Class c, AccessibleObject method) {
        model.addChirp(new Chirp(c, method));
        model.fireTableDataChanged();
    }
}
