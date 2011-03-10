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
package com.aspect.snoop.ui;

import com.aspect.snoop.agent.AgentJarCreator;
import com.aspect.snoop.agent.AgentLogger;
import com.aspect.snoop.agent.SnoopAgent;
import com.aspect.snoop.agent.manager.InstrumentationManager;
import com.aspect.snoop.util.IOUtil;
import com.aspect.snoop.util.JadUtil;
import com.aspect.snoop.util.UIUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.SwingWorker;
import org.jdesktop.application.Action;

public class DumpSourceCodeView extends javax.swing.JDialog {

    final File dir;
    List<URL> urls;
    SwingWorker dump;

    public DumpSourceCodeView(java.awt.Frame parent, boolean modal, InstrumentationManager mgr, final File dir) {
        super(parent, modal);
        initComponents();

        this.dir = dir;
        this.urls = new ArrayList<URL>();

        for(URL u : mgr.getCodeSourceURLs()) {
            boolean matched = false;
            for(String snoopLib : AgentJarCreator.jarsToNotBootClasspath) {
                if ( u.getFile().endsWith(snoopLib) )
                    matched = true;
            }
            if(!matched)
                urls.add(u);
        }

        prgBarJarCount.setMinimum(0);
        prgBarJarCount.setMaximum(urls.size());
        prgBarJarCount.setValue(0);

        prgBarClassCountInJar.setMinimum(0);
        prgBarClassCountInJar.setMaximum(0);
        prgBarClassCountInJar.setValue(0);
    }

    public void startDump() {

        final java.awt.Component parent = this;

        dump = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {

                prgBarJarCount.setStringPainted(true);
                int urlCount = urls.size();
                
                for (int i = 0; i < urlCount; i++) {

                    if (isCancelled())
                        break;

                    prgBarJarCount.setString( (i+1) + " / " + urlCount);
                    prgBarJarCount.setValue(i + 1);
                    parent.repaint();

                    URL u = urls.get(i);

                    AgentLogger.debug("Dumping source for: " + u.toString());

                    if ( u.getFile().endsWith("JavaSnoop.jar") ) {
                        AgentLogger.trace("Skipping JavaSnoop JAR");
                        continue;
                    }

                    lblJar.setText(u.toExternalForm());
                    parent.repaint();

                    File f = null;
                    try {
                        f = new File(u.toURI());
                    } catch (URISyntaxException e) {
                        UIUtil.showErrorMessage(SnoopAgent.getMainView(), "Error looking up jar" + u.toExternalForm());
                        AgentLogger.error("Error looking up jar", e);
                        continue;
                    }

                    if (f.exists() && f.isFile() && u.getFile().endsWith(".jar")) {
                        JarFile jar = null;
                        try {
                            jar = new JarFile(f);
                        } catch (IOException e) {
                            UIUtil.showErrorMessage(SnoopAgent.getMainView(), "Error reading jar for dumping source: " + u.toExternalForm());
                            AgentLogger.error("Error reading jar for dumping souce", e);
                        }

                        Enumeration<JarEntry> entries = jar.entries();
                        prgBarClassCountInJar.setIndeterminate(true);
                        parent.repaint();

                        while (entries.hasMoreElements()) {

                            if (isCancelled()) {
                                break;
                            }

                            JarEntry entry = entries.nextElement();
                            File targetFile = new File(dir, entry.getName().replaceAll("\\.class", ".java"));

                            prgBarClassCountInJar.setStringPainted(true);
                            prgBarClassCountInJar.setString(entry.getName());
                            parent.repaint();

                            if (entry.isDirectory()) {
                                targetFile.mkdirs();
                                continue;
                            }

                            targetFile.getParentFile().mkdirs(); // create parent dirs as necessary

                            try {

                                InputStream is = jar.getInputStream(entry);
                                byte[] bytes = IOUtil.getBytesFromStream(is);

                                // replace .class with .java
                                FileOutputStream fos = new FileOutputStream(targetFile);

                                if (entry.getName().endsWith(".class")) {
                                    String clsName = entry.getName().replaceAll("/", "\\.");
                                    clsName = clsName.substring(0, clsName.length() - 6);
                                    String s = JadUtil.getDecompiledJava(clsName, bytes);
                                    fos.write(s.getBytes());
                                } else {
                                    fos.write(bytes);
                                }

                                fos.close();

                            } catch (IOException ioe) {
                                AgentLogger.debug("Error with dump source", ioe);
                            }
                        }
                        prgBarClassCountInJar.setIndeterminate(false);
                        parent.repaint();
                    }
                }
                return null;
            }

        };

        dump.execute();

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        prgBarJarCount = new javax.swing.JProgressBar();
        prgBarClassCountInJar = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnCancelDump = new javax.swing.JButton();
        lblJar = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.aspect.snoop.JavaSnoop.class).getContext().getResourceMap(DumpSourceCodeView.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        prgBarJarCount.setName("prgBarJarCount"); // NOI18N

        prgBarClassCountInJar.setName("prgBarClassCountInJar"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(com.aspect.snoop.JavaSnoop.class).getContext().getActionMap(DumpSourceCodeView.class, this);
        btnCancelDump.setAction(actionMap.get("cancelDump")); // NOI18N
        btnCancelDump.setText(resourceMap.getString("btnCancelDump.text")); // NOI18N
        btnCancelDump.setName("btnCancelDump"); // NOI18N

        lblJar.setText(resourceMap.getString("lblJar.text")); // NOI18N
        lblJar.setName("lblJar"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(prgBarClassCountInJar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
                    .addComponent(prgBarJarCount, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
                    .addComponent(btnCancelDump)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(lblJar, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblJar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(prgBarClassCountInJar, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(prgBarJarCount, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCancelDump)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {prgBarClassCountInJar, prgBarJarCount});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @Action
    public void cancelDump() {
        dump.cancel(true);
        dispose();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelDump;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lblJar;
    private javax.swing.JProgressBar prgBarClassCountInJar;
    private javax.swing.JProgressBar prgBarJarCount;
    // End of variables declaration//GEN-END:variables

}
