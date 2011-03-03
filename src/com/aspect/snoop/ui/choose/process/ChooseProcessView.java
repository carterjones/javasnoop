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
import com.aspect.snoop.util.ClasspathUtil;
import com.aspect.snoop.util.UIUtil;
import com.sun.tools.attach.VirtualMachineDescriptor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.Vector;

import javax.swing.event.ListSelectionEvent;
import org.jdesktop.application.Action;
import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;

public class ChooseProcessView extends javax.swing.JDialog {

    private static final Logger logger = Logger.getLogger(ChooseProcessView.class);

    private String pid;
    SnoopSession session;

    public ChooseProcessView(JFrame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        lstJavaProcesses.setListData(new String[0]);

        lstJavaProcesses.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean selected, boolean hasFocus)
            {
                super.getListCellRendererComponent(
                  list, value, index, selected, hasFocus);
                setText(getValueString(value));
                return this;
            }
            private String getValueString(Object value)
            {
                String returnString = "null";
                if (value != null) {
                  if (value instanceof VirtualMachineDescriptor) {
                      VirtualMachineDescriptor vmd = (VirtualMachineDescriptor)value;
                      returnString = "[PID=" + vmd.id() +"] " + vmd.displayName();
                  } else if (value instanceof JVMDescriptor) {
                      JVMDescriptor vmd = (JVMDescriptor)value;
                      returnString = "[PID=" + vmd.getId() +"] " + vmd.getTitle();
                      if (!vmd.isAttachable())
                              this.setForeground(Color.gray);
                  } else {
                    returnString = "???: " + value.toString();
                  }
                }
                return returnString;
            }
        });

        pid = null;

        loadProcesses();

        final JFrame p = parent;

        lstJavaProcesses.addListSelectionListener( new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if ( ! lstJavaProcesses.isSelectionEmpty() ) {
                    JVMDescriptor desc = (JVMDescriptor)lstJavaProcesses.getSelectedValue();
                    lblMainArgs.setText(desc.getMainArguments());
                    lblVmArgs.setText(desc.getJVMArguments());
                    lblPid.setText(desc.getId());
                    lblVmVersion.setText(desc.getVMVersion());
                    lblMainClass.setText(desc.getMainClass());
                    lblJar.setText(desc.getJar());

                    btnCopyToClipboard.setEnabled(true);
                    btnUseAndAttach.setEnabled(true);

                } else {
                    String nsy = "(none selected yet)";
                    lblMainArgs.setText(nsy);
                    lblVmArgs.setText(nsy);
                    lblPid.setText(nsy);
                    lblVmVersion.setText(nsy);
                    lblMainClass.setText(nsy);
                    lblJar.setText(nsy);

                    btnCopyToClipboard.setEnabled(false);
                    btnUseAndAttach.setEnabled(false);

                }
            }

        });

        lstJavaProcesses.addMouseListener(
                new MouseListener() {

            public void mouseClicked(MouseEvent e) {

                if ( e.getClickCount() == 2 ) {
                    // user double clicked an item selection
                    JVMDescriptor selection = (JVMDescriptor)lstJavaProcesses.getSelectedValue();

                    if  ( ! selection.isAttachable() ) {
                        UIUtil.showErrorMessage(p, "Sorry, can't attach to that VM. It's probably running on an old version of Java.");
                        return;
                    }

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

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        lstJavaProcesses = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        lblPid = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblVmArgs = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblMainArgs = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblVmVersion = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblMainClass = new javax.swing.JLabel();
        btnUseAndAttach = new javax.swing.JButton();
        btnCopyToClipboard = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        lblJar = new javax.swing.JLabel();
        btnUseAndAttachAndClobber = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.aspect.snoop.JavaSnoop.class).getContext().getResourceMap(ChooseProcessView.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        setName("Form"); // NOI18N
        setResizable(false);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        lstJavaProcesses.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstJavaProcesses.setName("lstJavaProcesses"); // NOI18N
        jScrollPane1.setViewportView(lstJavaProcesses);

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        lblPid.setText(resourceMap.getString("lblPid.text")); // NOI18N
        lblPid.setName("lblPid"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        lblVmArgs.setText(resourceMap.getString("lblVmArgs.text")); // NOI18N
        lblVmArgs.setName("lblVmArgs"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        lblMainArgs.setText(resourceMap.getString("lblMainArgs.text")); // NOI18N
        lblMainArgs.setName("lblMainArgs"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        lblVmVersion.setText(resourceMap.getString("lblVmVersion.text")); // NOI18N
        lblVmVersion.setName("lblVmVersion"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        lblMainClass.setText(resourceMap.getString("lblMainClass.text")); // NOI18N
        lblMainClass.setName("lblMainClass"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(com.aspect.snoop.JavaSnoop.class).getContext().getActionMap(ChooseProcessView.class, this);
        btnUseAndAttach.setAction(actionMap.get("useAndAttach")); // NOI18N
        btnUseAndAttach.setText(resourceMap.getString("btnUseAndAttach.text")); // NOI18N
        btnUseAndAttach.setToolTipText(resourceMap.getString("btnUseAndAttach.toolTipText")); // NOI18N
        btnUseAndAttach.setName("btnUseAndAttach"); // NOI18N

        btnCopyToClipboard.setAction(actionMap.get("copyToClipboard")); // NOI18N
        btnCopyToClipboard.setText(resourceMap.getString("btnCopyToClipboard.text")); // NOI18N
        btnCopyToClipboard.setToolTipText(resourceMap.getString("btnCopyToClipboard.toolTipText")); // NOI18N
        btnCopyToClipboard.setName("btnCopyToClipboard"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        lblJar.setText(resourceMap.getString("lblJar.text")); // NOI18N
        lblJar.setName("lblJar"); // NOI18N

        btnUseAndAttachAndClobber.setAction(actionMap.get("useAndAttachWithClobbering")); // NOI18N
        btnUseAndAttachAndClobber.setText(resourceMap.getString("btnUseAndAttachAndClobber.text")); // NOI18N
        btnUseAndAttachAndClobber.setToolTipText(resourceMap.getString("btnUseAndAttachAndClobber.toolTipText")); // NOI18N
        btnUseAndAttachAndClobber.setName("btnUseAndAttachAndClobber"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPid, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblVmArgs, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblMainArgs, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblVmVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblJar, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblMainClass, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnUseAndAttach)
                                    .addComponent(btnCopyToClipboard)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(421, Short.MAX_VALUE)
                        .addComponent(btnUseAndAttachAndClobber)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(6, 6, 6)
                        .addComponent(jLabel3)
                        .addGap(7, 7, 7)
                        .addComponent(jLabel5)
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(6, 6, 6)
                                .addComponent(jLabel2)
                                .addGap(7, 7, 7)
                                .addComponent(jLabel6))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(btnCopyToClipboard)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnUseAndAttach))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblPid)
                        .addGap(6, 6, 6)
                        .addComponent(lblVmArgs)
                        .addGap(6, 6, 6)
                        .addComponent(lblMainArgs, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(lblMainClass)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblVmVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(lblJar)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUseAndAttachAndClobber)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCopyToClipboard;
    private javax.swing.JButton btnUseAndAttach;
    private javax.swing.JButton btnUseAndAttachAndClobber;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblJar;
    private javax.swing.JLabel lblMainArgs;
    private javax.swing.JLabel lblMainClass;
    private javax.swing.JLabel lblPid;
    private javax.swing.JLabel lblVmArgs;
    private javax.swing.JLabel lblVmVersion;
    private javax.swing.JList lstJavaProcesses;
    // End of variables declaration//GEN-END:variables

    private final static boolean JPS_SHOW_JVM_ARGS = false;
    private final static boolean JPS_SHOW_LONG_PATHS = true;
    private final static boolean JPS_DEBUG = true;

    private void loadProcesses() {

    	// Get descriptors for all JVM processes (JDK 1.6+)
        // List<VirtualMachineDescriptor> list = VirtualMachine.list();
        // lstJavaProcesses.setListData(new Vector<VirtualMachineDescriptor>(list));

    	// Get descriptors for all JVM processes (JDK 1.5+)
    	Vector<JVMDescriptor> vmds = new Vector<JVMDescriptor>();
        try {
            HostIdentifier hostId = null;
            try {
                    hostId = new HostIdentifier("localhost");
            } catch (URISyntaxException e1) {
                    e1.printStackTrace();
            }
            MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(hostId);

            // Get the set of active JVMs on the specified host
            Set<Integer> jvmids = monitoredHost.activeVms();

            for (Integer jvmid: jvmids) {
                StringBuilder output = new StringBuilder();
                Throwable lastError = null;

                String jvmIdString = jvmid.toString();
                output.append(jvmIdString);

                MonitoredVm vm = null;
                String vmidString = "//" + jvmIdString + "?mode=r";

                try {
                    VmIdentifier id = new VmIdentifier(vmidString);
                    vm = monitoredHost.getMonitoredVm(id, 0);
                } catch (Exception e) {
                    lastError = e;
                } finally {
                    if (vm == null) {
                        // A JVM may have died before we got a chance to inspect it
                        output.append(" -- process information unavailable");
                        if (JPS_DEBUG) {
                            if ((lastError != null)
                                    && (lastError.getMessage() != null)) {
                                output.append("\n\t");
                                output.append(lastError.getMessage());
                            }
                        }

                        continue;
                    }
                }

                output.append(" ");
                output.append(MonitoredVmUtil.mainClass(vm, JPS_SHOW_LONG_PATHS));

                if (JPS_SHOW_JVM_ARGS) {
                    String jvmArgs = MonitoredVmUtil.jvmArgs(vm);
                    if (jvmArgs != null && jvmArgs.length() > 0) {
                      output.append(" ").append(jvmArgs);
                    }
                }

                String mainClassName = MonitoredVmUtil.mainClass(vm, true);

                boolean isAttachable = isAttachable(jvmIdString);

                JVMDescriptor desc = new JVMDescriptor(jvmIdString, mainClassName, isAttachable);
                desc.setJVMArguments(MonitoredVmUtil.jvmArgs(vm));
                desc.setMainArguments(MonitoredVmUtil.mainArgs(vm));
                desc.setCommandLine(MonitoredVmUtil.commandLine(vm));
                desc.setVMVersion(MonitoredVmUtil.vmVersion(vm));

                if ( mainClassName.length() == 0 ) {
                    mainClassName = desc.getCommandLine();

                } else if ( mainClassName.endsWith(".jar") ) {
                    desc.setJar(mainClassName);
                    try {
                        desc.setMainClass(ClasspathUtil.getMainClassFromJarFile(mainClassName));
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                } else {
                    desc.setMainClass(mainClassName);
                    desc.setJar("");
                }

                desc.setMainClass(MonitoredVmUtil.mainClass(vm, true));

                String currentPid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

                if ( ! currentPid.equals(desc.getId()) ) {
                    vmds.add(desc);
                }

                monitoredHost.detach(vm);
            }
        } catch (MonitorException e) {
            logger.error(e);
        }
      lstJavaProcesses.setListData(vmds);
    }

    private boolean isAttachable(String id) {

    	boolean attachable = false;

	    MonitoredVm mvm = null;
	    try {
		    VmIdentifier vmid = new VmIdentifier(id);
		    MonitoredHost host = MonitoredHost.getMonitoredHost(vmid);
		    mvm = host.getMonitoredVm(vmid);
		    if (MonitoredVmUtil.isAttachable(mvm)) {
		    	attachable = true;
		    } else {
		    	if (MonitoredVmUtil.isKernelVM(mvm)) {
        	            logger.trace("Kernel VM does not support the attach mechanism");
		    	}
		    	else {
                            logger.trace("The VM does not support the attach mechanism for pid " + id);
            	    	}
		    }
	    } catch (Throwable t) {
		    // we do not know what this id is
		    if (t instanceof ThreadDeath) {
		    	ThreadDeath td = (ThreadDeath) t;
		    	throw td;
		    }
	    } finally {
	    	if (mvm != null) {
	    		mvm.detach();
	    	}
	    }
	    return attachable;
    }

    private void finalizeSelection() {
        JVMDescriptor desc = (JVMDescriptor)lstJavaProcesses.getSelectedValue();
        pid = desc.getId();
    }

    public String getPid() {
        return pid;
    }


    @Action
    public void useAndAttach() {

        if ( lstJavaProcesses.isSelectionEmpty() ) {
            return;
        }

        JVMDescriptor jvm = (JVMDescriptor)lstJavaProcesses.getSelectedValue();

        this.session = toSession(jvm);

        finalizeSelection();
        dispose();
    }

    @Action
    public void useAndDispose() {

        if ( lstJavaProcesses.isSelectionEmpty() ) {
            return;
        }

        JVMDescriptor jvm = (JVMDescriptor)lstJavaProcesses.getSelectedValue();

        session = toSession(jvm);
        dispose();
    }

    public SnoopSession getSession() {
        return session;
    }

    private SnoopSession toSession(JVMDescriptor jvm) {

        SnoopSession sess = new SnoopSession();

        sess.setArguments(jvm.getMainArguments());
        sess.setJavaArguments(jvm.getJVMArguments());
        sess.setProcessId(Integer.parseInt(jvm.getId()));
        sess.setClasspathString(jvm.getJar());
        sess.setMainClass(jvm.getMainClass());

        return sess;
    }

    @Action
    public void copyToClipboard() {

        if ( lstJavaProcesses.isSelectionEmpty() ) {
            return;
        }

        String nl = System.getProperty("line.separator");

        JVMDescriptor jvm = (JVMDescriptor)lstJavaProcesses.getSelectedValue();

        StringBuilder sb = new StringBuilder(200);

        sb.append("PID: ").append(jvm.getId()).append(nl);
        sb.append("Main class: ").append(jvm.getMainClass()).append(nl);
        sb.append("Main arguments: ").append(jvm.getMainArguments()).append(nl);

        if ( jvm.getJar().length() > 0 ) {
            sb.append("Jar file: ").append(jvm.getJar()).append(nl);
        }

        sb.append("JVM arguments: ").append(jvm.getJVMArguments()).append(nl);
        sb.append("VM version: ").append(jvm.getVMVersion());

        StringSelection buffer = new StringSelection(sb.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(buffer, buffer);

    }

    @Action
    public void useAndAttachWithClobbering() {
    if ( lstJavaProcesses.isSelectionEmpty() ) {
            return;
        }

        JVMDescriptor jvm = (JVMDescriptor)lstJavaProcesses.getSelectedValue();

        this.session = toSession(jvm);
        session.setCloberLookAndFeel(true);
        finalizeSelection();
        dispose();
    }

}
