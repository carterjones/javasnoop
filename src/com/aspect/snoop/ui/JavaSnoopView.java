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

import com.aspect.snoop.Condition;
import com.aspect.snoop.FunctionHook;
import com.aspect.snoop.FunctionHook.Mode;
import com.aspect.snoop.JavaSnoop;
import com.aspect.snoop.MethodWrapper;
import com.aspect.snoop.SnoopSession;
import com.aspect.snoop.agent.AgentLogger;
import com.aspect.snoop.agent.manager.InstrumentationManager;
import com.aspect.snoop.agent.manager.SessionManager;
import com.aspect.snoop.ui.canary.StartCanaryModeView;
import com.aspect.snoop.ui.choose.clazz.ChooseClassView;
import com.aspect.snoop.ui.condition.AddEditConditionView;
import com.aspect.snoop.ui.condition.ConditionTableCellRenderer;
import com.aspect.snoop.ui.condition.ConditionTableModel;
import com.aspect.snoop.ui.forceclass.ForceLoadClassesView;
import com.aspect.snoop.ui.hook.AddFunctionHookView;
import com.aspect.snoop.ui.hook.FunctionHookTableSelectionListener;
import com.aspect.snoop.ui.hook.FunctionsHookedTableModel;
import com.aspect.snoop.ui.pause.PauseView;
import com.aspect.snoop.ui.script.EditScriptView;
import com.aspect.snoop.ui.tamper.Parameter;
import com.aspect.snoop.ui.tamper.ParameterTamperingView;
import com.aspect.snoop.util.ConditionUtil;
import com.aspect.snoop.util.IOUtil;
import com.aspect.snoop.util.JadUtil;
import com.aspect.snoop.util.SessionPersistenceUtil;
import com.aspect.snoop.util.UIUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.jdesktop.application.Action;

/**
 *
 * @author adabirsiaghi
 */
public class JavaSnoopView extends javax.swing.JFrame {

    private static String faqUrl = "http://www.aspectsecurity.com/tools/javasnoop/javasnoop_faq.html";
    private static String homeUrl = "http://www.aspectsecurity.com/tools/javasnoop/";
    private static final String nl = System.getProperty("line.separator");

    private static File lastConfigurationDirectory;
    private static File lastConfigurationFile;

    private SnoopSession currentSession;
    private StyledDocument console;

    private boolean firstTimeLoading = true;

    private JPopupMenu popupMenu;

    private List<JCheckBoxMenuItem> mnuAgentLogLevels;
    private InstrumentationManager manager;

    private StartCanaryModeView canaryView;

    public JavaSnoopView(InstrumentationManager  manager) {

        this.manager = manager;
        AgentLogger.debug("Loading JavaSnoopView GUI components");

        try {

            initializeSession();

            AgentLogger.debug("Done loading components. Finalizing UI...");

            beMacFriendly();

            chkShowMethodCode.setSelected( JavaSnoop.getBooleanProperty(JavaSnoop.USE_JAD,false) );

            String icon = "/META-INF/about.png";

            try {
               setIconImage(ImageIO.read(this.getClass().getResourceAsStream(icon)));
            } catch(Exception e) { // couldn't load icon. not a big deal.
            }

            mnuAgentLogLevels = new ArrayList<JCheckBoxMenuItem>();
            mnuAgentLogLevels.add(mnuAgentLogOff);
            mnuAgentLogLevels.add(mnuAgentLogFatal);
            mnuAgentLogLevels.add(mnuAgentLogError);
            mnuAgentLogLevels.add(mnuAgentLogInfo);
            mnuAgentLogLevels.add(mnuAgentLogWarn);
            mnuAgentLogLevels.add(mnuAgentLogDebug);
            mnuAgentLogLevels.add(mnuAgentLogTrace);

            // create the "Delete condition" popup menu for the Conditions table
            JMenuItem deleteCondition = new JMenuItem("Delete condition");
            deleteCondition.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    FunctionHook hook = getCurrentHook();
                    if ( hook == null ) {
                        return;
                    }
                    if (tblConditions.getSelectedRow() != -1) {
                        ConditionTableModel model = (ConditionTableModel)tblConditions.getModel();
                        Condition c = model.getConditionAt(tblConditions.getSelectedRow());
                        hook.removeCondition(c);
                        tblConditions.repaint();
                        tblConditions.updateUI();
                    }
                }
            });

            popupMenu = new JPopupMenu();
            popupMenu.add(deleteCondition);

            PopupListener popupListener = new PopupListener();
            tblConditions.addMouseListener( popupListener );

        } catch (Throwable t) {
            AgentLogger.debug("Error initializing JavaSnoopView UI", t);
            throw new RuntimeException(t);
        }
    }

    private void beMacFriendly() {
        if (isMac()) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "JavaSnoop");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                AgentLogger.error(e);
            }
        }
    }

    private boolean isMac()
    {
        return System.getProperty("os.name").toLowerCase().indexOf("mac") != -1;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGrpHookConditions = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblFunctionsHooked = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        chkOutputToFile = new javax.swing.JCheckBox();
        chkOutputToConsole = new javax.swing.JCheckBox();
        chkPrintParameters = new javax.swing.JCheckBox();
        chkPrintStackTrace = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        txtOutputFile = new javax.swing.JTextField();
        btnBrowseForOutputFile = new javax.swing.JButton();
        chkRunScript = new javax.swing.JCheckBox();
        btnEditScript = new javax.swing.JButton();
        chkTamperParameters = new javax.swing.JCheckBox();
        chkTamperReturnValue = new javax.swing.JCheckBox();
        chkPause = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        rdoAlwaysHook = new javax.swing.JRadioButton();
        rdoHookIf = new javax.swing.JRadioButton();
        rdoDontHookIf = new javax.swing.JRadioButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblConditions = new javax.swing.JTable();
        btnAddNewCondition = new javax.swing.JButton();
        btnAddHook = new javax.swing.JButton();
        btnDeleteHook = new javax.swing.JButton();
        tabConsoleCode = new javax.swing.JTabbedPane();
        pnlConsole = new javax.swing.JScrollPane();
        txtConsole = new JTextPane(console);
        pnlCode = new javax.swing.JScrollPane();
        txtCode = new RSyntaxTextArea();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        mnuNewSession = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        mnuLoadSession = new javax.swing.JMenuItem();
        mnuSaveSession = new javax.swing.JMenuItem();
        mnuSaveSessionAs = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        javax.swing.JMenuItem mnuExit = new javax.swing.JMenuItem();
        mnuExitAndKill = new javax.swing.JMenuItem();
        jvmMenu = new javax.swing.JMenu();
        mnuGetProcessInfo = new javax.swing.JMenuItem();
        mnuDumpThreads = new javax.swing.JMenuItem();
        mnuOpenScriptingConsole = new javax.swing.JMenuItem();
        mnuStartCanaryMode = new javax.swing.JMenuItem();
        mnuDumpAllSourceCode = new javax.swing.JMenuItem();
        classesMenu = new javax.swing.JMenu();
        mnuBrowseRemoteClasses = new javax.swing.JMenuItem();
        mnuForceLoadClasses = new javax.swing.JMenuItem();
        mnuDecompileClass = new javax.swing.JMenuItem();
        settingsMenu = new javax.swing.JMenu();
        mnuAgentLogSetting = new javax.swing.JMenu();
        mnuAgentLogTrace = new javax.swing.JCheckBoxMenuItem();
        mnuAgentLogDebug = new javax.swing.JCheckBoxMenuItem();
        mnuAgentLogInfo = new javax.swing.JCheckBoxMenuItem();
        mnuAgentLogWarn = new javax.swing.JCheckBoxMenuItem();
        mnuAgentLogError = new javax.swing.JCheckBoxMenuItem();
        mnuAgentLogFatal = new javax.swing.JCheckBoxMenuItem();
        mnuAgentLogOff = new javax.swing.JCheckBoxMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        mnuManageJad = new javax.swing.JMenu();
        chkShowMethodCode = new javax.swing.JCheckBoxMenuItem();
        mnuSetJadPath = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem mnuAbout = new javax.swing.JMenuItem();
        mnuGotoHomePage = new javax.swing.JMenuItem();
        mnuViewFAQ = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.aspect.snoop.JavaSnoop.class).getContext().getResourceMap(JavaSnoopView.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                handleFocusGained(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblFunctionsHooked.setModel(new javax.swing.table.DefaultTableModel(
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
        tblFunctionsHooked.setFocusable(false);
        tblFunctionsHooked.setName("tblFunctionsHooked"); // NOI18N
        jScrollPane1.setViewportView(tblFunctionsHooked);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("On execution"));
        jPanel1.setName("jPanel1"); // NOI18N

        chkOutputToFile.setText(resourceMap.getString("chkOutputToFile.text")); // NOI18N
        chkOutputToFile.setFocusable(false);
        chkOutputToFile.setName("chkOutputToFile"); // NOI18N
        chkOutputToFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOutputToFileActionPerformed(evt);
            }
        });

        chkOutputToConsole.setText(resourceMap.getString("chkOutputToConsole.text")); // NOI18N
        chkOutputToConsole.setFocusable(false);
        chkOutputToConsole.setName("chkOutputToConsole"); // NOI18N
        chkOutputToConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOutputToConsoleActionPerformed(evt);
            }
        });

        chkPrintParameters.setText(resourceMap.getString("chkPrintParameters.text")); // NOI18N
        chkPrintParameters.setName("chkPrintParameters"); // NOI18N
        chkPrintParameters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPrintParametersActionPerformed(evt);
            }
        });

        chkPrintStackTrace.setText(resourceMap.getString("chkPrintStackTrace.text")); // NOI18N
        chkPrintStackTrace.setName("chkPrintStackTrace"); // NOI18N
        chkPrintStackTrace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPrintStackTraceActionPerformed(evt);
            }
        });

        jPanel3.setAlignmentX(0.0F);
        jPanel3.setAlignmentY(0.0F);
        jPanel3.setFocusable(false);
        jPanel3.setName("jPanel3"); // NOI18N

        txtOutputFile.setName("txtOutputFile"); // NOI18N
        txtOutputFile.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtOutputFileKeyReleased(evt);
            }
        });

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(com.aspect.snoop.JavaSnoop.class).getContext().getActionMap(JavaSnoopView.class, this);
        btnBrowseForOutputFile.setAction(actionMap.get("browseToOutputFile")); // NOI18N
        btnBrowseForOutputFile.setText(resourceMap.getString("btnBrowseForOutputFile.text")); // NOI18N
        btnBrowseForOutputFile.setToolTipText(resourceMap.getString("btnBrowseForOutputFile.toolTipText")); // NOI18N
        btnBrowseForOutputFile.setFocusable(false);
        btnBrowseForOutputFile.setName("btnBrowseForOutputFile"); // NOI18N

        chkRunScript.setText(resourceMap.getString("chkRunScript.text")); // NOI18N
        chkRunScript.setName("chkRunScript"); // NOI18N
        chkRunScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRunScriptActionPerformed(evt);
            }
        });

        btnEditScript.setText(resourceMap.getString("btnEditScript.text")); // NOI18N
        btnEditScript.setToolTipText(resourceMap.getString("btnEditScript.toolTipText")); // NOI18N
        btnEditScript.setFocusable(false);
        btnEditScript.setName("btnEditScript"); // NOI18N
        btnEditScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditScriptActionPerformed(evt);
            }
        });

        chkTamperParameters.setText(resourceMap.getString("chkTamperParameters.text")); // NOI18N
        chkTamperParameters.setName("chkTamperParameters"); // NOI18N
        chkTamperParameters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkTamperParametersActionPerformed(evt);
            }
        });

        chkTamperReturnValue.setText(resourceMap.getString("chkTamperReturnValue.text")); // NOI18N
        chkTamperReturnValue.setName("chkTamperReturnValue"); // NOI18N
        chkTamperReturnValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkTamperReturnValueActionPerformed(evt);
            }
        });

        chkPause.setText(resourceMap.getString("chkPause.text")); // NOI18N
        chkPause.setName("chkPause"); // NOI18N
        chkPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPauseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(txtOutputFile, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnBrowseForOutputFile))
                    .addComponent(chkPause)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(btnEditScript))
                    .addComponent(chkTamperReturnValue)
                    .addComponent(chkTamperParameters)
                    .addComponent(chkRunScript, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBrowseForOutputFile)
                    .addComponent(txtOutputFile, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkRunScript)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEditScript, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkTamperParameters)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkTamperReturnValue)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkPause))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(chkOutputToFile)
                                .addComponent(chkOutputToConsole))
                            .addComponent(chkPrintParameters))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkPrintStackTrace))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkPrintParameters)
                    .addComponent(chkPrintStackTrace))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkOutputToConsole)
                .addGap(3, 3, 3)
                .addComponent(chkOutputToFile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(145, 145, 145))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Conditions"));
        jPanel2.setName("jPanel2"); // NOI18N

        btnGrpHookConditions.add(rdoAlwaysHook);
        rdoAlwaysHook.setText(resourceMap.getString("rdoAlwaysHook.text")); // NOI18N
        rdoAlwaysHook.setName("rdoAlwaysHook"); // NOI18N
        rdoAlwaysHook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoAlwaysHookActionPerformed(evt);
            }
        });

        btnGrpHookConditions.add(rdoHookIf);
        rdoHookIf.setText(resourceMap.getString("rdoHookIf.text")); // NOI18N
        rdoHookIf.setName("rdoHookIf"); // NOI18N
        rdoHookIf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoHookIfActionPerformed(evt);
            }
        });

        btnGrpHookConditions.add(rdoDontHookIf);
        rdoDontHookIf.setText(resourceMap.getString("rdoDontHookIf.text")); // NOI18N
        rdoDontHookIf.setName("rdoDontHookIf"); // NOI18N
        rdoDontHookIf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoDontHookIfActionPerformed(evt);
            }
        });

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblConditions.setModel(new javax.swing.table.DefaultTableModel(
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
        tblConditions.setFocusable(false);
        tblConditions.setName("tblConditions"); // NOI18N
        jScrollPane2.setViewportView(tblConditions);

        btnAddNewCondition.setText(resourceMap.getString("btnAddNewCondition.text")); // NOI18N
        btnAddNewCondition.setEnabled(false);
        btnAddNewCondition.setName("btnAddNewCondition"); // NOI18N
        btnAddNewCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddNewConditionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(rdoAlwaysHook)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rdoHookIf)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rdoDontHookIf)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                        .addComponent(btnAddNewCondition))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdoAlwaysHook)
                    .addComponent(rdoHookIf)
                    .addComponent(rdoDontHookIf)
                    .addComponent(btnAddNewCondition))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        btnAddHook.setText(resourceMap.getString("btnAddHook.text")); // NOI18N
        btnAddHook.setToolTipText(resourceMap.getString("btnAddHook.toolTipText")); // NOI18N
        btnAddHook.setActionCommand(resourceMap.getString("btnAddHook.actionCommand")); // NOI18N
        btnAddHook.setFocusable(false);
        btnAddHook.setName("btnAddHook"); // NOI18N
        btnAddHook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddHookActionPerformed(evt);
            }
        });

        btnDeleteHook.setAction(actionMap.get("deleteHook")); // NOI18N
        btnDeleteHook.setText(resourceMap.getString("btnDeleteHook.text")); // NOI18N
        btnDeleteHook.setToolTipText(resourceMap.getString("btnDeleteHook.toolTipText")); // NOI18N
        btnDeleteHook.setActionCommand(resourceMap.getString("btnDeleteHook.actionCommand")); // NOI18N
        btnDeleteHook.setFocusable(false);
        btnDeleteHook.setName("btnDeleteHook"); // NOI18N
        btnDeleteHook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteHookActionPerformed(evt);
            }
        });

        tabConsoleCode.setBackground(resourceMap.getColor("tabConsoleCode.background")); // NOI18N
        tabConsoleCode.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabConsoleCode.setName("tabConsoleCode"); // NOI18N

        pnlConsole.setName("pnlConsole"); // NOI18N

        txtConsole.setName("txtConsole"); // NOI18N
        pnlConsole.setViewportView(txtConsole);

        tabConsoleCode.addTab(resourceMap.getString("pnlConsole.TabConstraints.tabTitle"), pnlConsole); // NOI18N

        pnlCode.setName("pnlCode"); // NOI18N

        txtCode.setColumns(20);
        txtCode.setEditable(false);
        txtCode.setRows(5);
        txtCode.setName("txtCode"); // NOI18N
        pnlCode.setViewportView(txtCode);

        tabConsoleCode.addTab(resourceMap.getString("pnlCode.TabConstraints.tabTitle"), pnlCode); // NOI18N

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 866, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 696, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        mnuNewSession.setText(resourceMap.getString("mnuNewSession.text")); // NOI18N
        mnuNewSession.setToolTipText(resourceMap.getString("mnuNewSession.toolTipText")); // NOI18N
        mnuNewSession.setName("mnuNewSession"); // NOI18N
        mnuNewSession.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNewSessionActionPerformed(evt);
            }
        });
        fileMenu.add(mnuNewSession);

        jSeparator2.setName("jSeparator2"); // NOI18N
        fileMenu.add(jSeparator2);

        mnuLoadSession.setText(resourceMap.getString("mnuLoadSession.text")); // NOI18N
        mnuLoadSession.setToolTipText(resourceMap.getString("mnuLoadSession.toolTipText")); // NOI18N
        mnuLoadSession.setName("mnuLoadSession"); // NOI18N
        mnuLoadSession.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLoadSessionActionPerformed(evt);
            }
        });
        fileMenu.add(mnuLoadSession);

        mnuSaveSession.setText(resourceMap.getString("mnuSaveSession.text")); // NOI18N
        mnuSaveSession.setEnabled(false);
        mnuSaveSession.setName("mnuSaveSession"); // NOI18N
        mnuSaveSession.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaveSessionActionPerformed(evt);
            }
        });
        fileMenu.add(mnuSaveSession);

        mnuSaveSessionAs.setText(resourceMap.getString("mnuSaveSessionAs.text")); // NOI18N
        mnuSaveSessionAs.setToolTipText(resourceMap.getString("mnuSaveSessionAs.toolTipText")); // NOI18N
        mnuSaveSessionAs.setName("mnuSaveSessionAs"); // NOI18N
        mnuSaveSessionAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaveSessionAsActionPerformed(evt);
            }
        });
        fileMenu.add(mnuSaveSessionAs);

        jSeparator3.setName("jSeparator3"); // NOI18N
        fileMenu.add(jSeparator3);

        mnuExit.setText(resourceMap.getString("mnuExit.text")); // NOI18N
        mnuExit.setToolTipText(resourceMap.getString("mnuExit.toolTipText")); // NOI18N
        mnuExit.setName("mnuExit"); // NOI18N
        mnuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExitActionPerformed(evt);
            }
        });
        fileMenu.add(mnuExit);

        mnuExitAndKill.setText(resourceMap.getString("mnuExitAndKill.text")); // NOI18N
        mnuExitAndKill.setToolTipText(resourceMap.getString("mnuExitAndKill.toolTipText")); // NOI18N
        mnuExitAndKill.setName("mnuExitAndKill"); // NOI18N
        mnuExitAndKill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExitAndKillActionPerformed(evt);
            }
        });
        fileMenu.add(mnuExitAndKill);

        menuBar.add(fileMenu);

        jvmMenu.setText(resourceMap.getString("jvmMenu.text")); // NOI18N
        jvmMenu.setName("jvmMenu"); // NOI18N

        mnuGetProcessInfo.setAction(actionMap.get("getProcessInfo")); // NOI18N
        mnuGetProcessInfo.setText(resourceMap.getString("mnuGetProcessInfo.text")); // NOI18N
        mnuGetProcessInfo.setToolTipText(resourceMap.getString("mnuGetProcessInfo.toolTipText")); // NOI18N
        mnuGetProcessInfo.setName("mnuGetProcessInfo"); // NOI18N
        mnuGetProcessInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGetProcessInfoActionPerformed(evt);
            }
        });
        jvmMenu.add(mnuGetProcessInfo);

        mnuDumpThreads.setAction(actionMap.get("dumpThreads")); // NOI18N
        mnuDumpThreads.setText(resourceMap.getString("mnuDumpThreads.text")); // NOI18N
        mnuDumpThreads.setToolTipText(resourceMap.getString("mnuDumpThreads.toolTipText")); // NOI18N
        mnuDumpThreads.setName("mnuDumpThreads"); // NOI18N
        jvmMenu.add(mnuDumpThreads);

        mnuOpenScriptingConsole.setAction(actionMap.get("openScriptingConsole")); // NOI18N
        mnuOpenScriptingConsole.setText(resourceMap.getString("mnuOpenScriptingConsole.text")); // NOI18N
        mnuOpenScriptingConsole.setToolTipText(resourceMap.getString("mnuOpenScriptingConsole.toolTipText")); // NOI18N
        mnuOpenScriptingConsole.setName("mnuOpenScriptingConsole"); // NOI18N
        mnuOpenScriptingConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuOpenScriptingConsoleActionPerformed(evt);
            }
        });
        jvmMenu.add(mnuOpenScriptingConsole);

        mnuStartCanaryMode.setAction(actionMap.get("enterCanaryMode")); // NOI18N
        mnuStartCanaryMode.setText(resourceMap.getString("mnuStartCanaryMode.text")); // NOI18N
        mnuStartCanaryMode.setToolTipText(resourceMap.getString("mnuStartCanaryMode.toolTipText")); // NOI18N
        mnuStartCanaryMode.setName("mnuStartCanaryMode"); // NOI18N
        mnuStartCanaryMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuStartCanaryModeActionPerformed(evt);
            }
        });
        jvmMenu.add(mnuStartCanaryMode);

        mnuDumpAllSourceCode.setAction(actionMap.get("dumpSourceCode")); // NOI18N
        mnuDumpAllSourceCode.setText(resourceMap.getString("mnuDumpAllSourceCode.text")); // NOI18N
        mnuDumpAllSourceCode.setToolTipText(resourceMap.getString("mnuDumpAllSourceCode.toolTipText")); // NOI18N
        mnuDumpAllSourceCode.setName("mnuDumpAllSourceCode"); // NOI18N
        jvmMenu.add(mnuDumpAllSourceCode);

        menuBar.add(jvmMenu);

        classesMenu.setText(resourceMap.getString("classesMenu.text")); // NOI18N
        classesMenu.setName("classesMenu"); // NOI18N

        mnuBrowseRemoteClasses.setAction(actionMap.get("browseRemoteClasses")); // NOI18N
        mnuBrowseRemoteClasses.setText(resourceMap.getString("mnuBrowseRemoteClasses.text")); // NOI18N
        mnuBrowseRemoteClasses.setToolTipText(resourceMap.getString("mnuBrowseRemoteClasses.toolTipText")); // NOI18N
        mnuBrowseRemoteClasses.setName("mnuBrowseRemoteClasses"); // NOI18N
        mnuBrowseRemoteClasses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBrowseRemoteClassesActionPerformed(evt);
            }
        });
        classesMenu.add(mnuBrowseRemoteClasses);

        mnuForceLoadClasses.setAction(actionMap.get("forceLoadClasses")); // NOI18N
        mnuForceLoadClasses.setText(resourceMap.getString("mnuForceLoadClasses.text")); // NOI18N
        mnuForceLoadClasses.setToolTipText(resourceMap.getString("mnuForceLoadClasses.toolTipText")); // NOI18N
        mnuForceLoadClasses.setName("mnuForceLoadClasses"); // NOI18N
        mnuForceLoadClasses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuForceLoadClassesActionPerformed(evt);
            }
        });
        classesMenu.add(mnuForceLoadClasses);

        mnuDecompileClass.setAction(actionMap.get("decompileClass")); // NOI18N
        mnuDecompileClass.setText(resourceMap.getString("mnuDecompileClass.text")); // NOI18N
        mnuDecompileClass.setToolTipText(resourceMap.getString("mnuDecompileClass.toolTipText")); // NOI18N
        mnuDecompileClass.setName("mnuDecompileClass"); // NOI18N
        mnuDecompileClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDecompileClassActionPerformed(evt);
            }
        });
        classesMenu.add(mnuDecompileClass);

        menuBar.add(classesMenu);

        settingsMenu.setText(resourceMap.getString("settingsMenu.text")); // NOI18N
        settingsMenu.setName("settingsMenu"); // NOI18N

        mnuAgentLogSetting.setText(resourceMap.getString("mnuAgentLogSetting.text")); // NOI18N
        mnuAgentLogSetting.setToolTipText(resourceMap.getString("mnuAgentLogSetting.toolTipText")); // NOI18N
        mnuAgentLogSetting.setName("mnuAgentLogSetting"); // NOI18N

        mnuAgentLogTrace.setText(resourceMap.getString("mnuAgentLogTrace.text")); // NOI18N
        mnuAgentLogTrace.setName("mnuAgentLogTrace"); // NOI18N
        mnuAgentLogTrace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAgentLogTraceActionPerformed(evt);
            }
        });
        mnuAgentLogSetting.add(mnuAgentLogTrace);

        mnuAgentLogDebug.setText(resourceMap.getString("mnuAgentLogDebug.text")); // NOI18N
        mnuAgentLogDebug.setName("mnuAgentLogDebug"); // NOI18N
        mnuAgentLogDebug.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handleChangeLogLevel(evt);
            }
        });
        mnuAgentLogSetting.add(mnuAgentLogDebug);

        mnuAgentLogInfo.setSelected(true);
        mnuAgentLogInfo.setText(resourceMap.getString("mnuAgentLogInfo.text")); // NOI18N
        mnuAgentLogInfo.setName("mnuAgentLogInfo"); // NOI18N
        mnuAgentLogInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAgentLogInfoActionPerformed(evt);
            }
        });
        mnuAgentLogSetting.add(mnuAgentLogInfo);

        mnuAgentLogWarn.setText(resourceMap.getString("mnuAgentLogWarn.text")); // NOI18N
        mnuAgentLogWarn.setName("mnuAgentLogWarn"); // NOI18N
        mnuAgentLogWarn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAgentLogWarnActionPerformed(evt);
            }
        });
        mnuAgentLogSetting.add(mnuAgentLogWarn);

        mnuAgentLogError.setText(resourceMap.getString("mnuAgentLogError.text")); // NOI18N
        mnuAgentLogError.setName("mnuAgentLogError"); // NOI18N
        mnuAgentLogError.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAgentLogErrorActionPerformed(evt);
            }
        });
        mnuAgentLogSetting.add(mnuAgentLogError);

        mnuAgentLogFatal.setText(resourceMap.getString("mnuAgentLogFatal.text")); // NOI18N
        mnuAgentLogFatal.setName("mnuAgentLogFatal"); // NOI18N
        mnuAgentLogFatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAgentLogFatalActionPerformed(evt);
            }
        });
        mnuAgentLogSetting.add(mnuAgentLogFatal);

        mnuAgentLogOff.setText(resourceMap.getString("mnuAgentLogOff.text")); // NOI18N
        mnuAgentLogOff.setName("mnuAgentLogOff"); // NOI18N
        mnuAgentLogOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAgentLogOffActionPerformed(evt);
            }
        });
        mnuAgentLogSetting.add(mnuAgentLogOff);

        settingsMenu.add(mnuAgentLogSetting);

        jSeparator5.setName("jSeparator5"); // NOI18N
        settingsMenu.add(jSeparator5);

        mnuManageJad.setText(resourceMap.getString("mnuManageJad.text")); // NOI18N
        mnuManageJad.setToolTipText(resourceMap.getString("mnuManageJad.toolTipText")); // NOI18N
        mnuManageJad.setName("mnuManageJad"); // NOI18N

        chkShowMethodCode.setSelected(true);
        chkShowMethodCode.setText(resourceMap.getString("chkShowMethodCode.text")); // NOI18N
        chkShowMethodCode.setName("chkShowMethodCode"); // NOI18N
        chkShowMethodCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowMethodCodeActionPerformed(evt);
            }
        });
        mnuManageJad.add(chkShowMethodCode);

        mnuSetJadPath.setAction(actionMap.get("changeJadPath")); // NOI18N
        mnuSetJadPath.setText(resourceMap.getString("mnuSetJadPath.text")); // NOI18N
        mnuSetJadPath.setName("mnuSetJadPath"); // NOI18N
        mnuSetJadPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSetJadPathActionPerformed(evt);
            }
        });
        mnuManageJad.add(mnuSetJadPath);

        settingsMenu.add(mnuManageJad);

        menuBar.add(settingsMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        mnuAbout.setText(resourceMap.getString("mnuAbout.text")); // NOI18N
        mnuAbout.setName("mnuAbout"); // NOI18N
        mnuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAboutActionPerformed(evt);
            }
        });
        helpMenu.add(mnuAbout);

        mnuGotoHomePage.setAction(actionMap.get("browseToHomePage")); // NOI18N
        mnuGotoHomePage.setText(resourceMap.getString("mnuGotoHomePage.text")); // NOI18N
        mnuGotoHomePage.setName("mnuGotoHomePage"); // NOI18N
        mnuGotoHomePage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGotoHomePageActionPerformed(evt);
            }
        });
        helpMenu.add(mnuGotoHomePage);

        mnuViewFAQ.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11, 0));
        mnuViewFAQ.setText(resourceMap.getString("mnuViewFAQ.text")); // NOI18N
        mnuViewFAQ.setName("mnuViewFAQ"); // NOI18N
        mnuViewFAQ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuViewFAQActionPerformed(evt);
            }
        });
        helpMenu.add(mnuViewFAQ);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnAddHook)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDeleteHook))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tabConsoleCode, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(statusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnAddHook)
                            .addComponent(btnDeleteHook)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(tabConsoleCode, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkOutputToFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOutputToFileActionPerformed
        FunctionHook hook = getCurrentHook();
        hook.setOutputToFile(chkOutputToFile.isSelected());
}//GEN-LAST:event_chkOutputToFileActionPerformed

    private void chkOutputToConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOutputToConsoleActionPerformed
        FunctionHook hook = getCurrentHook();
        hook.setOutputToConsole(chkOutputToConsole.isSelected());
}//GEN-LAST:event_chkOutputToConsoleActionPerformed

    private void chkPrintParametersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPrintParametersActionPerformed
        FunctionHook hook = getCurrentHook();
        hook.setShouldPrintParameters(chkPrintParameters.isSelected());

        if ( hook.isEnabled() ) {
            sendAgentNewRules();
        }
}//GEN-LAST:event_chkPrintParametersActionPerformed

    private void chkPrintStackTraceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPrintStackTraceActionPerformed
        FunctionHook hook = getCurrentHook();
        hook.setShouldPrintStackTrace(chkPrintStackTrace.isSelected());

        if ( hook.isEnabled() ) {
            sendAgentNewRules();
        }
}//GEN-LAST:event_chkPrintStackTraceActionPerformed

    private void txtOutputFileKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtOutputFileKeyReleased
        FunctionHook hook = getCurrentHook();
        hook.setOutputFile(txtOutputFile.getText());
}//GEN-LAST:event_txtOutputFileKeyReleased

    private void chkRunScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRunScriptActionPerformed
        FunctionHook hook = getCurrentHook();
        hook.setShouldRunScript(chkRunScript.isSelected());

        if ( hook.isEnabled() ) {
            sendAgentNewRules();
        }
}//GEN-LAST:event_chkRunScriptActionPerformed

    private void btnEditScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditScriptActionPerformed

        FunctionHook hook = getCurrentHook();

        EditScriptView view = new EditScriptView(this, true, hook.getStartScript(), hook.getEndScript());
        view.setVisible(true);

        UIUtil.waitForInput(view);

        if (view.getStartScript() != null) {
            hook.setStartScript(view.getStartScript());
            hook.setEndScript(view.getEndScript());
        }

        sendAgentNewRules();
    }//GEN-LAST:event_btnEditScriptActionPerformed

    private void chkTamperParametersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTamperParametersActionPerformed

        FunctionsHookedTableModel model = (FunctionsHookedTableModel) tblFunctionsHooked.getModel();
        FunctionHook hook = model.getHookFromRow(tblFunctionsHooked.getSelectedRow());
        hook.setShouldTamperParameters(chkTamperParameters.isSelected());

        if ( hook.isEnabled() ) {
            sendAgentNewRules();
        }
    }//GEN-LAST:event_chkTamperParametersActionPerformed

    private void chkTamperReturnValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTamperReturnValueActionPerformed
        FunctionHook hook = getCurrentHook();
        hook.setShouldTamperReturnValue(chkTamperReturnValue.isSelected());

        if ( hook.isEnabled() ) {
            sendAgentNewRules();
        }
}//GEN-LAST:event_chkTamperReturnValueActionPerformed

    private void chkPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPauseActionPerformed
        FunctionHook hook = getCurrentHook();
        hook.setShouldPause(chkPause.isSelected());

        if ( hook.isEnabled() ) {
            sendAgentNewRules();
        }
}//GEN-LAST:event_chkPauseActionPerformed

    private void rdoAlwaysHookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoAlwaysHookActionPerformed
        FunctionHook hook = getCurrentHook();
        hook.setMode(Mode.AlwaysIntercept);
}//GEN-LAST:event_rdoAlwaysHookActionPerformed

    private void rdoHookIfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoHookIfActionPerformed
        FunctionHook hook = getCurrentHook();
        hook.setMode(Mode.InterceptIf);
}//GEN-LAST:event_rdoHookIfActionPerformed

    private void rdoDontHookIfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoDontHookIfActionPerformed
        FunctionHook hook = getCurrentHook();
        hook.setMode(Mode.DontInterceptIf);
}//GEN-LAST:event_rdoDontHookIfActionPerformed

    private void btnAddNewConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNewConditionActionPerformed

        FunctionHook hook = getCurrentHook();

        // if no hook is selected, fail. we need to know which hook to add
        // the condition to
        if (hook == null) {
            return;
        }

        AddEditConditionView view = new AddEditConditionView(this, true, hook.getParameterTypes());
        view.setVisible(true);

        UIUtil.waitForInput(view);

        if (view.getOperator() != null) {

            Condition condition = new Condition(
                    true,
                    view.getOperator(),
                    view.getParameter(),
                    view.getOperand());

            hook.addCondition(condition);

            setTableDimensions();

            tblConditions.repaint();
            tblConditions.updateUI();
        }
}//GEN-LAST:event_btnAddNewConditionActionPerformed

    private void btnAddHookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddHookActionPerformed

        AddFunctionHookView view = new AddFunctionHookView(this, true, currentSession.getClasspathString());

        view.setVisible(true);

        UIUtil.waitForInput(view);

        if (view.getSelectedClass() != null) {

            Class selectedClass = view.getSelectedClass();
            MethodWrapper method = MethodWrapper.getWrapper(view.getSelectedMethod());
            Class[] parameterTypes = view.getParameterTypes();
            Class returnType = view.getReturnType();

            /*
             * Before adding it, we make sure a hook for this class/method hasn't
             * already been added.
             */
            for(int i=0;i<currentSession.getFunctionHooks().size();i++) {
                FunctionHook oldHook = currentSession.getFunctionHooks().get(i);
                if ( oldHook.getClazz().equals(selectedClass) ) {
                    if ( oldHook.getMethodName().equals(method.getName()) ) {
                        if ( Arrays.equals(oldHook.getParameterTypes(),parameterTypes)) {
                            JOptionPane.showMessageDialog(this, "That method is already hooked!","Error adding hook",JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
            }

            boolean shouldInherit = view.getShouldInherit();

            FunctionHook hook = new FunctionHook(
                    false,
                    false,
                    false,
                    "", // starting script
                    "", // ending script
                    false,
                    true,
                    selectedClass,
                    method.getName(),
                    parameterTypes,
                    returnType,
                    shouldInherit,
                    FunctionHook.Mode.AlwaysIntercept,
                    false,
                    false,
                    false,
                    false,
                    "",
                    new ArrayList<Condition>());

            hook.setEnabled(true);

            currentSession.getFunctionHooks().add(hook);

            ((FunctionsHookedTableModel) tblFunctionsHooked.getModel()).setHooks(
                    currentSession.getFunctionHooks());

            // save the text values before
            // they would get overridden below
            updateCurrentSession();

            // update the UI
            updateSessionUI(false);

            sendAgentNewRules();

        }
    }//GEN-LAST:event_btnAddHookActionPerformed

    private void btnDeleteHookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteHookActionPerformed

        FunctionHook hook = getCurrentHook();

        if (hook != null) {
            int rc = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this hook?");
            if (rc == JOptionPane.YES_OPTION) {
               
                try {

                    startProgressBar();

                    statusMessageLabel.setText("Setting hooks...");

                    SessionManager.uninstallHooks(currentSession);
                    currentSession.getFunctionHooks().remove(hook);
                    SessionManager.installHooks(currentSession);

                    FunctionsHookedTableModel model = (FunctionsHookedTableModel) tblFunctionsHooked.getModel();
                    model.removeHook(hook);
                    updateSessionUI(false);

                    statusMessageLabel.setText("Finished setting hooks at " + getHumanTime() );

                } catch (Exception ex) {
                    UIUtil.showErrorMessage(this, "Failure establishing hooks: " + ex.getMessage());
                    AgentLogger.error("Failure establishing hooks", ex);
                } finally {
                    stopProgressBar();
                }
            }
        }
    }//GEN-LAST:event_btnDeleteHookActionPerformed

    private void mnuLoadSessionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLoadSessionActionPerformed

        try {

            File file = null;

            // show open file dialog
            if (System.getProperty("os.name").toLowerCase().indexOf("mac") != -1) {
                FileDialog fileDialog = new FileDialog(this, "Load Configuration", FileDialog.LOAD);
                if (lastConfigurationDirectory != null) {
                    fileDialog.setDirectory(lastConfigurationDirectory.getAbsolutePath());
                }
                fileDialog.setVisible(true);
                if (fileDialog.getFile() != null) {
                    file = new File(fileDialog.getFile());
                }
            } else {
                JFileChooser fc = new JFileChooser(lastConfigurationDirectory);
                fc.setDialogTitle("Load Configuration");
                int rc = fc.showOpenDialog(this);
                if (rc == JFileChooser.APPROVE_OPTION) {
                    file = fc.getSelectedFile();
                }
            }

            if (file != null) {
                lastConfigurationDirectory = file.getParentFile();

                currentSession = SessionPersistenceUtil.loadSession(file.getAbsolutePath());
                currentSession.markAsSaved();
                updateSessionUI(true);
            }

        } catch (FileNotFoundException ex) {
            AgentLogger.error(ex);
        } catch (IOException ex) {
            AgentLogger.error(ex);
        }
}//GEN-LAST:event_mnuLoadSessionActionPerformed

    private void mnuSaveSessionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaveSessionActionPerformed

        try {
            // save file where it is
            SessionPersistenceUtil.saveSession(currentSession);
            statusMessageLabel.setText("Saved session to " + new File(currentSession.getSnoopSessionFilename()).getAbsolutePath() + " at " + getHumanTime());

            //lblFilename.setText("<html>" + currentSession.getSnoopSessionFilename() + "</html>");

        } catch (FileNotFoundException ex) {
            AgentLogger.error(ex);
        } catch (IOException ex) {
            AgentLogger.error(ex);
        }
}//GEN-LAST:event_mnuSaveSessionActionPerformed

    private void mnuSaveSessionAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaveSessionAsActionPerformed
        try {
            File file = null;

            // show file input dialog, get target filename
            if (System.getProperty("os.name").toLowerCase().indexOf("mac") != -1) {

                FileDialog fileDialog = new FileDialog(this, "Save Configuration", FileDialog.SAVE);
                if (lastConfigurationFile != null && lastConfigurationFile.getParentFile() != null) {
                    fileDialog.setDirectory(lastConfigurationFile.getParentFile().getAbsolutePath());
                }

                fileDialog.setVisible(true);

                if (fileDialog.getFile() != null) {
                    file = new File(fileDialog.getFile());
                }

            } else {

                JFileChooser fc = null;

                if ( lastConfigurationFile != null ) {
                    fc = new JFileChooser(lastConfigurationFile.getParentFile());
                } else {
                    fc = new JFileChooser();
                }

                int rc = fc.showOpenDialog(this);

                if (rc == JFileChooser.APPROVE_OPTION) {
                    file = fc.getSelectedFile();
                }
            }

            if (file != null) {
                lastConfigurationFile = file;

                // save file
                AgentLogger.error("Saving configuration file: " + file.getAbsolutePath());
                updateCurrentSession();

                SessionPersistenceUtil.saveSession(currentSession, file.getAbsolutePath());
                statusMessageLabel.setText("Saved session to " + file.getAbsolutePath() + " at " + getHumanTime());

                // enable the "Save Configuration" menu item, since we have a
                // default filename to save to now

                mnuSaveSession.setEnabled(true);

                // putting the html tags will allow it to wrap
                //lblFilename.setText("<html>" + currentSession.getSnoopSessionFilename() + "</html>");
            }

        } catch (FileNotFoundException ex) {
            AgentLogger.error(ex);
        } catch (IOException ex) {
            AgentLogger.error(ex);
        }
}//GEN-LAST:event_mnuSaveSessionAsActionPerformed

    private void chkShowMethodCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowMethodCodeActionPerformed
        boolean useJad = chkShowMethodCode.isSelected();
        JavaSnoop.setProperty(JavaSnoop.USE_JAD, Boolean.toString(useJad));
        JavaSnoop.saveProperties();
}//GEN-LAST:event_chkShowMethodCodeActionPerformed

    private void mnuViewFAQActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuViewFAQActionPerformed
        try {
            Desktop.getDesktop().browse(URI.create(faqUrl));
        } catch (IOException ex) {
            showConsoleErrorMessage("Couldn't browse to FAQ page: " + ex.getMessage());
        }
}//GEN-LAST:event_mnuViewFAQActionPerformed

    private void mnuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAboutActionPerformed
        JavaSnoopAboutBox about = new JavaSnoopAboutBox(this);
        about.setVisible(true);
    }//GEN-LAST:event_mnuAboutActionPerformed

    private void mnuForceLoadClassesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuForceLoadClassesActionPerformed
        forceLoadClasses();
    }//GEN-LAST:event_mnuForceLoadClassesActionPerformed

    private void mnuStartCanaryModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuStartCanaryModeActionPerformed
        enterCanaryMode();
    }//GEN-LAST:event_mnuStartCanaryModeActionPerformed

    private void mnuDecompileClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDecompileClassActionPerformed
        decompileClass();
    }//GEN-LAST:event_mnuDecompileClassActionPerformed

    private void mnuOpenScriptingConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuOpenScriptingConsoleActionPerformed
        openScriptingConsole();
    }//GEN-LAST:event_mnuOpenScriptingConsoleActionPerformed

    private void mnuBrowseRemoteClassesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBrowseRemoteClassesActionPerformed
        ChooseClassView view = new ChooseClassView(this,manager.getLoadedClasses());
        view.setVisible(true);
        UIUtil.waitForInput(view);
    }//GEN-LAST:event_mnuBrowseRemoteClassesActionPerformed

    private void mnuSetJadPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSetJadPathActionPerformed
        changeJadPath();
    }//GEN-LAST:event_mnuSetJadPathActionPerformed

    private void handleChangeLogLevel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_handleChangeLogLevel
        changeLogLevel(evt.getActionCommand());
    }//GEN-LAST:event_handleChangeLogLevel

    private void mnuGotoHomePageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGotoHomePageActionPerformed
        browseToHomePage();
    }//GEN-LAST:event_mnuGotoHomePageActionPerformed

    private void mnuNewSessionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNewSessionActionPerformed
        newSession();
    }//GEN-LAST:event_mnuNewSessionActionPerformed

    private void mnuExitAndKillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExitAndKillActionPerformed
        System.exit(0);
    }//GEN-LAST:event_mnuExitAndKillActionPerformed

    private void mnuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExitActionPerformed
        initializeSession();
        sendAgentNewRules();
        dispose();
    }//GEN-LAST:event_mnuExitActionPerformed

    private void mnuGetProcessInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGetProcessInfoActionPerformed
        StringBuilder sb = new StringBuilder();
        sb.append("Process ID: ");
        sb.append(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        sb.append(nl);
        sb.append("Working directory: ");
        sb.append(new File(".").getAbsolutePath());
        sb.append(nl);
        sb.append("Classes loaded: ");
        sb.append(manager.getLoadedClasses().size());
        sb.append(nl);
        sb.append("System properties: ");
        sb.append(nl);
        Properties p = System.getProperties();
        Set keySet = p.keySet();
        for(Object s : keySet) {
            String key = (String)s;
            if ( "line.separator".equals(key) )
                continue;
            sb.append("   ");
            sb.append(key);
            sb.append("=");
            sb.append(p.getProperty(key));
            sb.append(nl);
        }
        showSnoopMessage(sb.toString());
    }//GEN-LAST:event_mnuGetProcessInfoActionPerformed

    private void mnuAgentLogTraceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAgentLogTraceActionPerformed
        changeLogLevel(evt.getActionCommand());
    }//GEN-LAST:event_mnuAgentLogTraceActionPerformed

    private void mnuAgentLogInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAgentLogInfoActionPerformed
        changeLogLevel(evt.getActionCommand());
    }//GEN-LAST:event_mnuAgentLogInfoActionPerformed

    private void mnuAgentLogWarnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAgentLogWarnActionPerformed
        changeLogLevel(evt.getActionCommand());
    }//GEN-LAST:event_mnuAgentLogWarnActionPerformed

    private void mnuAgentLogErrorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAgentLogErrorActionPerformed
        changeLogLevel(evt.getActionCommand());
    }//GEN-LAST:event_mnuAgentLogErrorActionPerformed

    private void mnuAgentLogFatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAgentLogFatalActionPerformed
        changeLogLevel(evt.getActionCommand());
    }//GEN-LAST:event_mnuAgentLogFatalActionPerformed

    private void mnuAgentLogOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAgentLogOffActionPerformed
        changeLogLevel(evt.getActionCommand());
    }//GEN-LAST:event_mnuAgentLogOffActionPerformed

    private void handleFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_handleFocusGained
        updateTitle();
    }//GEN-LAST:event_handleFocusGained

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JavaSnoopView(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddHook;
    private javax.swing.JButton btnAddNewCondition;
    private javax.swing.JButton btnBrowseForOutputFile;
    private javax.swing.JButton btnDeleteHook;
    private javax.swing.JButton btnEditScript;
    private javax.swing.ButtonGroup btnGrpHookConditions;
    private javax.swing.JCheckBox chkOutputToConsole;
    private javax.swing.JCheckBox chkOutputToFile;
    private javax.swing.JCheckBox chkPause;
    private javax.swing.JCheckBox chkPrintParameters;
    private javax.swing.JCheckBox chkPrintStackTrace;
    private javax.swing.JCheckBox chkRunScript;
    private javax.swing.JCheckBoxMenuItem chkShowMethodCode;
    private javax.swing.JCheckBox chkTamperParameters;
    private javax.swing.JCheckBox chkTamperReturnValue;
    private javax.swing.JMenu classesMenu;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JMenu jvmMenu;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JCheckBoxMenuItem mnuAgentLogDebug;
    private javax.swing.JCheckBoxMenuItem mnuAgentLogError;
    private javax.swing.JCheckBoxMenuItem mnuAgentLogFatal;
    private javax.swing.JCheckBoxMenuItem mnuAgentLogInfo;
    private javax.swing.JCheckBoxMenuItem mnuAgentLogOff;
    private javax.swing.JMenu mnuAgentLogSetting;
    private javax.swing.JCheckBoxMenuItem mnuAgentLogTrace;
    private javax.swing.JCheckBoxMenuItem mnuAgentLogWarn;
    private javax.swing.JMenuItem mnuBrowseRemoteClasses;
    private javax.swing.JMenuItem mnuDecompileClass;
    private javax.swing.JMenuItem mnuDumpAllSourceCode;
    private javax.swing.JMenuItem mnuDumpThreads;
    private javax.swing.JMenuItem mnuExitAndKill;
    private javax.swing.JMenuItem mnuForceLoadClasses;
    private javax.swing.JMenuItem mnuGetProcessInfo;
    private javax.swing.JMenuItem mnuGotoHomePage;
    private javax.swing.JMenuItem mnuLoadSession;
    private javax.swing.JMenu mnuManageJad;
    private javax.swing.JMenuItem mnuNewSession;
    private javax.swing.JMenuItem mnuOpenScriptingConsole;
    private javax.swing.JMenuItem mnuSaveSession;
    private javax.swing.JMenuItem mnuSaveSessionAs;
    private javax.swing.JMenuItem mnuSetJadPath;
    private javax.swing.JMenuItem mnuStartCanaryMode;
    private javax.swing.JMenuItem mnuViewFAQ;
    private javax.swing.JScrollPane pnlCode;
    private javax.swing.JScrollPane pnlConsole;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton rdoAlwaysHook;
    private javax.swing.JRadioButton rdoDontHookIf;
    private javax.swing.JRadioButton rdoHookIf;
    private javax.swing.JMenu settingsMenu;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTabbedPane tabConsoleCode;
    private javax.swing.JTable tblConditions;
    private javax.swing.JTable tblFunctionsHooked;
    private javax.swing.JTextArea txtCode;
    private javax.swing.JTextPane txtConsole;
    private javax.swing.JTextField txtOutputFile;
    // End of variables declaration//GEN-END:variables

   public void pause(String className, int hookId, Object[] parameters, Class[] types) {

        FunctionHook hook = getHookById(hookId);

        if (!hook.isEnabled() || !areConditionsMet(hook.getMode(), hook.getConditions(), parameters)) {
            return;
        }

        /*
         * Decide whether or not to show the code.
         */
        showCodeIfNeeded(hook.getClazz());

        PauseView view = new PauseView(this, true, className, hook.getMethodName());
        view.setVisible(true);

        UIUtil.waitForInput(view);
    }

    private String join(Class[] types) {
        StringBuilder sb = new StringBuilder(100);
        for(int i=0;i<types.length;i++) {
            sb.append(types[i].getSimpleName());
            if ( i != types.length-1 ) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public String getTimeStamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        StringBuilder sb = new StringBuilder(100);
        sb.append("[");
        sb.append(dateFormat.format(date));
        sb.append("] ");
        return sb.toString();
    }

    public void printParameters(String className, int hookId, Object[] parameters, Class[] types) {

        FunctionHook hook = getHookById(hookId);

        if ( hook == null ) {
            showConsoleErrorMessage("Didn't recognize hook " + hookId + " for class " + className);
        }

        if (!hook.isEnabled() || ! hook.shouldPrintParameters() || !areConditionsMet(hook.getMode(), hook.getConditions(), parameters)) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        sb.append(getTimeStamp());
        sb.append("Print parameter request from: " + className + "." + hook.getMethodName() + "(" + join(types) + "): " + nl);

        for (int i = 0; i < parameters.length; i++) {
            Object o = parameters[i];
            sb.append("Parameter " + (i + 1) + " (type: " + types[i].getSimpleName() + "): " + String.valueOf(o) + nl);
        }

        sb.append(nl);

        if ( hook.isOutputToConsole()) {
            showSnoopMessage(sb.toString());
        }

        if (hook.isOutputToFile()) {
            File f = new File(hook.getOutputFile());
            try {
                FileOutputStream fos = new FileOutputStream(f, true);
                fos.write(sb.toString().getBytes());
                fos.close();
            } catch (FileNotFoundException fnfe) {
                showConsoleErrorMessage("Failed to append data to file. Could not find or write to file: " + f.getAbsolutePath());
            } catch (IOException ioe) {
                showConsoleErrorMessage("Failed to append data to file. Problem writing to file " + f.getAbsolutePath() + ": " + ioe.getMessage());
            }
        }

    }

    public void printStackTrace(String className, int hookId, Object[] parameters, Class[] types) {

        FunctionHook hook = getHookById(hookId);

        if (!hook.isEnabled() || ! hook.shouldPrintStackTrace() || !areConditionsMet(hook.getMode(), hook.getConditions(), parameters)) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(getTimeStamp());
        sb.append("Stack trace print request from: " + className + "." + hook.getMethodName() + "(" + join(types) + "):" + nl);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        new Exception().printStackTrace(pw);
        sb.append( skipLines(sw.toString(),3) + nl);

        if ( hook.isOutputToConsole()) {
            showSnoopMessage(sb.toString());
        }

        if (hook.isOutputToFile()) {
            File f = new File(hook.getOutputFile());
            if (f.exists() && f.canWrite()) {
                try {
                    FileOutputStream fos = new FileOutputStream(f, true);
                    fos.write(sb.toString().getBytes());
                    fos.close();
                } catch (FileNotFoundException fnfe) {
                    showConsoleErrorMessage("Failed to append data to file. Could not find or write to file: " + f.getAbsolutePath());
                } catch (IOException ioe) {
                    showConsoleErrorMessage("Failed to append data to file. Problem writing to file " + f.getAbsolutePath() + ": " + ioe.getMessage());
                }
            }
        }

    }

    public Object tamperWithReturnValue(String className, int hookId, Object[] parameters, Class[] types, Object retVal, Class retValType) {
        FunctionHook hook = getHookById(hookId);

        if (!hook.isEnabled() || !areConditionsMet(hook.getMode(), hook.getConditions(), parameters)) {
            return retVal;
        }

        /*
         * Decide whether or not to show the code.
         */
        showCodeIfNeeded(hook.getClazz());

        String action = "Return value";

        StringBuilder sb = new StringBuilder();
        sb.append(getTimeStamp());
        sb.append(action);
        sb.append(" tampering request from: " + className + "." + hook.getMethodName() + "(" + join(types) + ")" + nl);

        showSnoopMessage(sb.toString());

        List<Parameter> params = new ArrayList<Parameter>();

        //for (int i = 0; i < parameters.length; i++) {
          //  params.add(new Parameter(i, parameters[i], types[i]));
        //}
        params.add(new Parameter(0, retVal, retValType));
        ParameterTamperingView view = new ParameterTamperingView(
                this,
                true,
                className,
                hook.getMethodName(),
                params,
                true);

        view.setVisible(true);

        UIUtil.waitForInput(view);

        if ( view.shouldDisable() ) {
            hook.setEnabled(false);
            updateSessionUI(false);
        }

        List<Parameter> results = view.getParameters();
        return results.get(0).getObject();
    }


    public Object[] tamperWithParameters(String className, int hookId, Object[] parameters, Class[] types) {

        FunctionHook hook = getHookById(hookId);

        if (!hook.isEnabled() || !areConditionsMet(hook.getMode(), hook.getConditions(), parameters)) {
            return parameters;
        }

        /*
         * Decide whether or not to show the code.
         */
        showCodeIfNeeded(hook.getClazz());

        String action = "Parameter";

        StringBuilder sb = new StringBuilder();
        sb.append(getTimeStamp());
        sb.append(action);
        sb.append(" tampering request from: " + className + "." + hook.getMethodName() + "(" + join(types) + ")" + nl);

        showSnoopMessage(sb.toString());

        List<Parameter> params = new ArrayList<Parameter>();

        for (int i = 0; i < parameters.length; i++) {
            params.add(new Parameter(i, parameters[i], types[i]));
        }

        ParameterTamperingView view = new ParameterTamperingView(
                this,
                true,
                className,
                hook.getMethodName(),
                params,
                false);

        view.setVisible(true);

        UIUtil.waitForInput(view);

        if ( view.shouldDisable() ) {
            hook.setEnabled(false);
            updateSessionUI(false);
        }

        List<Parameter> results = view.getParameters();
        Object[] newParameters = new Object[parameters.length];

        for (int i = 0; i < newParameters.length; i++) {
            newParameters[i] = results.get(i).getObject();
        }

        return newParameters;

    }

    public FunctionHook getHookById(int hookId) {

        FunctionsHookedTableModel model = (FunctionsHookedTableModel) tblFunctionsHooked.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            FunctionHook hook = model.getHookFromRow(i);
            if (hook.hashCode() == hookId) {
                return hook;
            }
        }

        return null;
    }

    public void showSnoopMessage(String s) {

        SimpleAttributeSet attributes = new SimpleAttributeSet();
        attributes.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.FALSE);
        attributes.addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.FALSE);
        attributes.addAttribute(StyleConstants.CharacterConstants.Foreground, Color.blue);

        try {
            console.insertString(console.getLength(), s, attributes);
            txtConsole.setCaretPosition( console.getLength() );
        } catch (BadLocationException ex) {
            AgentLogger.error(ex);
        }
    }

    public void showConsoleErrorMessage(String message) {

        SimpleAttributeSet attributes = new SimpleAttributeSet();
         attributes.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.FALSE);
        attributes.addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.FALSE);
       attributes.addAttribute(StyleConstants.CharacterConstants.Foreground, Color.red);

        try {

            console.insertString(console.getLength(), "[SNOOP ERROR] " + message + nl, attributes);

        } catch (BadLocationException ex) {
            AgentLogger.error(ex);
        }
    }

    /**
     * Function to decide whether or not the conditions are satisfied.
     * @param mode
     * @param conditions
     * @return
     */
    private boolean areConditionsMet(Mode mode, List<Condition> conditions, Object[] parameters) {

        if (mode.equals(Mode.AlwaysIntercept)) {
            return true;
        } else if (mode.equals(Mode.InterceptIf)) {
            // all conditions have to be true - if we reach an
            // enabled condition that is false, we return false
            for (Condition c : conditions) {
                if (c.isEnabled() && !ConditionUtil.evaluate(c, parameters[c.getParameter()])) {
                    return false;
                }
            }
        } else if (mode.equals(Mode.DontInterceptIf)) {
            // all conditions have to be false - if we reach an
            // enabled condition that is true, we return false
            for (Condition c : conditions) {
                if (c.isEnabled() && ConditionUtil.evaluate(c, parameters[c.getParameter()])) {
                    return false;
                }
            }
        }

        return true;
    }

    public FunctionHook getCurrentHook() {

        if (tblFunctionsHooked.getSelectedRow() != -1) {
            FunctionsHookedTableModel model = (FunctionsHookedTableModel) tblFunctionsHooked.getModel();
            return model.getHookFromRow(tblFunctionsHooked.getSelectedRow());
        }

        return null;
    }

    private String getHumanTime() {
        int hour = Calendar.getInstance().get(Calendar.HOUR);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        int second = Calendar.getInstance().get(Calendar.SECOND);
        int am = Calendar.getInstance().get(Calendar.AM_PM);

        boolean AM = am == Calendar.AM;

        return hour + ":" + pad(minute) + ":" + pad(second) + " " + (AM ? "AM" : "PM");
    }

    private String pad(int i) {
        return i >= 10 ? String.valueOf(i) : "0" + i;
    }

    public void deleteHook() {

        FunctionHook hook = getCurrentHook();
        if (hook != null) {
            // user does have a hook selected, so we delete it!
            FunctionsHookedTableModel model = (FunctionsHookedTableModel) tblFunctionsHooked.getModel();
            model.removeHook(hook);
            currentSession.getFunctionHooks().remove(hook);
        }

    }

    public final void initializeSession() {

        currentSession = new SnoopSession();

        console = new DefaultStyledDocument();

        if ( firstTimeLoading ) {
            initComponents();
            firstTimeLoading = false;
        }

        ((RSyntaxTextArea)txtCode).setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        ((RSyntaxTextArea)txtCode).setFont(new Font("Courier",Font.PLAIN,12));

        tblFunctionsHooked.setModel(new FunctionsHookedTableModel(null));

        FunctionHookTableSelectionListener listener =
                new FunctionHookTableSelectionListener(
                tblFunctionsHooked,
                tblConditions,
                btnAddNewCondition,
                rdoAlwaysHook,
                rdoDontHookIf,
                rdoHookIf,
                chkTamperParameters,
                chkTamperReturnValue,
                chkRunScript,
                btnEditScript,
                chkPause,
                chkPrintParameters,
                chkPrintStackTrace,
                chkOutputToFile,
                chkOutputToConsole,
                txtOutputFile,
                btnBrowseForOutputFile,
                btnGrpHookConditions);

        tblFunctionsHooked.getSelectionModel().addListSelectionListener(listener);

        tblConditions.setModel(new ConditionTableModel());

        ConditionTableCellRenderer renderer = new ConditionTableCellRenderer();
        tblConditions.setDefaultEditor(JButton.class, renderer);
        tblConditions.setDefaultRenderer(JButton.class, renderer);
        tblConditions.setRowHeight(20);

        setTableDimensions();

        updateSessionUI(false);
    }

    public static File getConfigurationFile()
    {
    	return lastConfigurationFile;
    }

    public void newSession() {
        initializeSession();
    }

    public void sendAgentNewRules() {

        try {

            startProgressBar();

            statusMessageLabel.setText("Setting hooks...");

            SessionManager.recycleHooks(currentSession);

            statusMessageLabel.setText("Finished setting hooks at " + getHumanTime() );

        } catch (Exception ex) {
            UIUtil.showErrorMessage(this, "Failure establishing hooks: " + ex.getMessage());
            AgentLogger.error("Failure establishing hooks", ex);
        } finally {
            stopProgressBar();
        }
    }

    public void startProgressBar() {
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
    }

    public void stopProgressBar() {
        progressBar.setVisible(false);
        progressBar.setIndeterminate(false);
    }

    public void enterCanaryMode() {

        canaryView = new StartCanaryModeView(this, false);
        final SnoopSession session = currentSession;
        final javax.swing.JFrame frame = this;

        disableAllComponentsForCanaryMode();
        
        canaryView.addWindowListener(new WindowListener() {

            private void enableAllComponentsForCanaryMode() {
                //FIXME: Save the "enabled" state in a <Component,Boolean>
                //       HashMap and disable all fields. Right now, all we
                //       do is disable. When we enable, we enable things that
                //       were not previously enabled.
                recursiveSetEnabled(frame, true);
            }

            public void windowClosed(WindowEvent e) {
                try {
                    statusMessageLabel.setText("Resetting hooks...");
                    SessionManager.recycleHooks(session);
                    statusMessageLabel.setText("Finished resetting hooks at " + getHumanTime() );
                } catch (Exception ex) {
                    UIUtil.showErrorMessage(frame, "Failure resetting hooks: " + ex.getMessage());
                    AgentLogger.error("Failure resetting hooks", ex);
                } finally {
                    enableAllComponentsForCanaryMode();
                }
            }

            public void windowOpened(WindowEvent e) { }
            public void windowClosing(WindowEvent e) { }
            public void windowIconified(WindowEvent e) { }
            public void windowDeiconified(WindowEvent e) { }
            public void windowActivated(WindowEvent e) { }
            public void windowDeactivated(WindowEvent e) { }
        });

        canaryView.setVisible(true);
        this.setEnabled(true);
    }

    private void recursiveSetEnabled(Component c, boolean b) {
        if ( c instanceof java.awt.Container ) {
            for (Component child : ((java.awt.Container)c).getComponents()) {
                recursiveSetEnabled(child, b);
            }
        }
        c.setEnabled(b);
    }

    public StartCanaryModeView getCanaryView() {
        return canaryView;
    }

    public SnoopSession getSession() {
        return currentSession;
    }

    public void setSession(SnoopSession session) {
        currentSession = session;
    }

    public void addHook(FunctionHook hook) {
        currentSession.getFunctionHooks().add(hook);
        updateSessionUI(false);
    }

    private void showCodeIfNeeded(Class clazz) {
        boolean useJad = chkShowMethodCode.isSelected();
        if (useJad) {
            byte[] bytes = IOUtil.getClassBytes(clazz);
            try {

                String javaCode = JadUtil.getDecompiledJava(clazz.getName(),bytes);
                fillInCode(javaCode);

            } catch(Exception e) {
                txtCode.setText(e.getMessage());
            }
        }
    }

    private void fillInCode(String javaCode) {
        txtCode.setText(javaCode);
        txtCode.setCaretPosition(0);
    }

    private String skipLines(String st, int num) {
        StringReader sr = new StringReader(st);
        BufferedReader br = new BufferedReader(sr);
        StringBuilder sb = new StringBuilder();

        try {
            while(num>0) {
                br.readLine();
                num--;
            }
            String buff;
            while((buff=br.readLine())!=null) {
                sb.append(buff);
                sb.append(nl);
            }
        } catch(IOException ioe){}

        return sb.toString();
    }

    private void disableAllComponentsForCanaryMode() {
        recursiveSetEnabled(this, false);
    }

    class PopupListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            showConditionDeleteMenu(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            showConditionDeleteMenu(e);
        }

        private void showConditionDeleteMenu(MouseEvent e) {
            if ( e.isPopupTrigger() ) {
                popupMenu.show(e.getComponent(),e.getX(),e.getY());
            }
        }
    }

    public void changeLogLevel(String level) {

        for(JCheckBoxMenuItem item : mnuAgentLogLevels) {
            if ( ! item.getText().equals(level) ) {
                item.setSelected(false);
                item.setState(false);
            }
        }

        AgentLogger.level = AgentLogger.levelValue(level);
    }

    public void changeJadPath() {

        String oldPath = JavaSnoop.getProperty(JavaSnoop.JAD_PATH);

        if ( oldPath == null ) {
            oldPath = "";
        }

        String newPath = JOptionPane.showInputDialog(this,"Enter the path to the jad executable (leave blank if it's on the PATH)",oldPath);

        if ( newPath != null ) {
            JavaSnoop.setProperty(JavaSnoop.JAD_PATH,newPath);
            JavaSnoop.saveProperties();
        }
    }

    public void decompileClass() {

        if ( JadUtil.getJadLocation() == null ) {
            UIUtil.showErrorMessage(this, "Could not locate Jad (not on the path and not setup)");
            return;
        }

        try {

            List<Class> classes = manager.getLoadedClasses();

            ChooseClassView view = new ChooseClassView(this, classes);
            view.setVisible(true);

            UIUtil.waitForInput(view);

            String cn = view.getClassName();

            if (cn != null) {
                try {
                    String java = JadUtil.getDecompiledJava(cn, IOUtil.getClassBytes(view.getChosenClass()));
                    fillInCode(java);
                } catch(IOException ioe) { }
            }

        } catch (Exception ex) {
            UIUtil.showErrorMessage(this, "Couldn't get process info from client: " + ex.getMessage());
        }

    }

    public void browseToOutputFile() {

        String currentOutputFile = txtOutputFile.getText();
        JFileChooser fc = null;

        if ( currentOutputFile.length() > 0 ) {
            File f = new File(currentOutputFile);
            if ( f.exists() ) {
                fc = new JFileChooser(f.getParentFile());
            } else {
                fc = new JFileChooser();
            }
        }
        int rc = fc.showOpenDialog(this);

        if (rc == JFileChooser.APPROVE_OPTION) {
            File of = fc.getSelectedFile();
            txtOutputFile.setText(of.getAbsolutePath());
        }
    }

    public void browseToHomePage() {
        try {
            Desktop.getDesktop().browse(URI.create(homeUrl));
        } catch (IOException ex) {
            showConsoleErrorMessage("Couldn't browse to FAQ page: " + ex.getMessage());
        }
    }

    public void forceLoadClasses() {

        ForceLoadClassesView view = new ForceLoadClassesView(this,true);
        view.setVisible(true);

        UIUtil.waitForInput(view);

        if ( ! view.userCanceled() ) {

            List<String> classesToLoad = view.getClassesToLoad();

            statusMessageLabel.setText("Asking process to load classes...");
            List<String> failedClasses = forceLoadClasses(classesToLoad);
            statusMessageLabel.setText("Done");

            int succeeded = classesToLoad.size() - failedClasses.size();
            showSnoopMessage(getTimeStamp() + "Successfully loaded " + succeeded + " classes" + nl);

            if ( ! failedClasses.isEmpty() ) {
                StringBuilder sb = new StringBuilder();
                sb.append("Failed to load the following classes: ");
                sb.append(nl);
                for(int i=0;i<failedClasses.size() && i<25;i++) {
                    String fail = failedClasses.get(i);
                    sb.append(fail);
                    sb.append(nl);
                }
                if ( failedClasses.size() > 25 ) {
                    sb.append("... ");
                    sb.append(failedClasses.size());
                    sb.append(" total");
                }
                String errorMsg = sb.toString();
                UIUtil.showErrorMessage(this,errorMsg);
                AgentLogger.error(errorMsg);
            }
        }
    }

    private List<String> forceLoadClasses(List<String> classesToLoad) {

        List<String> failedClasses = new ArrayList<String>();
        List<ClassLoader> classloaders = manager.getClassLoaders();

        for(String cls : classesToLoad) {
            boolean loaded = false;
            try {

                /*
                 * First, try to load the class using current class loader.
                 */
                AgentLogger.debug("Trying to load " + cls + " with " + this.getClass().getClassLoader());
                Class.forName(cls);
                loaded = true;
            } catch (Throwable t) {
                for (int i=0;i<classloaders.size() && !loaded;i++) {
                    ClassLoader cl = classloaders.get(i);
                    try {
                        AgentLogger.debug("Trying to load " + cls + " with " + cl);
                        Class.forName(cls,true,cl);
                        loaded = true;
                        /*
                         * To speed up future loads, use the successful
                         * classloader first next time.
                         */
                        Collections.swap(classloaders, 0, i);
                    } catch (Throwable t2) { }
                }
            }

            if ( ! loaded ) {
                failedClasses.add(cls);
                AgentLogger.debug("Failed to force load " + cls);
            } else {
                AgentLogger.debug("Successfully loaded " + cls);
            }
        }

        return failedClasses;
    }

    public void openScriptingConsole() {
        ScriptingView scriptView = new ScriptingView(this,true);
        scriptView.setVisible(true);

        UIUtil.waitForInput(scriptView);
    }



    /*
     * Synchronizes the UI from session.
     */
    public void updateSessionUI(boolean shouldOverwriteConsole) {

        //txtMainClass.setText(currentSession.getMainClass());
        //txtJavaArgs.setText(currentSession.getJavaArguments());
        //txtArguments.setText(currentSession.getArguments());
        //txtClasspath.setText(currentSession.getClasspathString());
        //txtWorkingDir.setText(currentSession.getWorkingDir());

        if (currentSession.alreadyBeenSaved()) {
            //mnuSaveConfiguration.setEnabled(true);
            //lblFilename.setText("<html>" + currentSession.getSnoopSessionFilename() + "</html>");
        } else {
            //mnuSaveConfiguration.setEnabled(false);
            //lblFilename.setText("(not saved yet)");
        }

        if ( shouldOverwriteConsole ) {
            txtConsole.setText("");
            showSnoopMessage(currentSession.getOutput());
        }

        tblFunctionsHooked.setModel(new FunctionsHookedTableModel(currentSession.getFunctionHooks()));

        // if there are any functions hooked, select the first one
        List<FunctionHook> hooks = currentSession.getFunctionHooks();

        tblFunctionsHooked.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblFunctionsHooked.setColumnSelectionAllowed(false);
        tblFunctionsHooked.setRowSelectionAllowed(true);

        if (hooks.size() > 0) {

            //tblFunctionsHooked.changeSelection(0, 0, false, false);
            FunctionHook hook = hooks.get(0);

            chkPrintParameters.setSelected(hook.shouldPrintParameters());
            chkPrintStackTrace.setSelected(hook.shouldPrintStackTrace());
            chkOutputToConsole.setSelected(hook.isOutputToConsole());
            chkOutputToFile.setSelected(hook.isOutputToFile());

            chkTamperParameters.setSelected(hook.shouldTamperParameters());

            if ( hook.getParameterTypes().length == 0 ) {
                chkTamperParameters.setEnabled(false);
            }

            chkTamperReturnValue.setSelected(hook.shouldTamperReturnValue());

            if ( hook.getReturnType().getName().equals("void") || hook.getMethodName().startsWith("<") ) {
                chkTamperReturnValue.setEnabled(false);
            }

            chkRunScript.setSelected(hook.shouldRunScript());

            chkPause.setSelected(hook.shouldPause());

            // update conditions table

            tblConditions.setModel(new ConditionTableModel(hook.getConditions()));

        }

        setTableDimensions();

        tblFunctionsHooked.repaint();
        tblFunctionsHooked.updateUI();

        tblConditions.updateUI();

    }

    private void setTableDimensions() {

        tblFunctionsHooked.getColumnModel().getColumn(0).setWidth(70);
        tblFunctionsHooked.getColumnModel().getColumn(0).setMaxWidth(70);
        tblFunctionsHooked.getColumnModel().getColumn(0).setMinWidth(70);
        tblFunctionsHooked.getColumnModel().getColumn(0).setResizable(false);

        tblFunctionsHooked.getColumnModel().getColumn(2).setWidth(75);
        tblFunctionsHooked.getColumnModel().getColumn(2).setMaxWidth(75);
        tblFunctionsHooked.getColumnModel().getColumn(2).setMinWidth(75);
        tblFunctionsHooked.getColumnModel().getColumn(2).setResizable(false);

        //tblFunctionsHooked.setIntercellSpacing(new Dimension(15,1));

        tblConditions.getColumnModel().getColumn(0).setWidth(60);
        tblConditions.getColumnModel().getColumn(0).setMaxWidth(60);
        tblConditions.getColumnModel().getColumn(0).setMinWidth(60);
        tblConditions.getColumnModel().getColumn(0).setResizable(false);

        tblConditions.getColumnModel().getColumn(1).setWidth(75);
        tblConditions.getColumnModel().getColumn(1).setMaxWidth(75);
        tblConditions.getColumnModel().getColumn(1).setMinWidth(75);
        tblConditions.getColumnModel().getColumn(1).setResizable(false);

        tblConditions.getColumnModel().getColumn(2).setWidth(70);
        tblConditions.getColumnModel().getColumn(2).setMaxWidth(70);
        tblConditions.getColumnModel().getColumn(2).setMinWidth(70);

        tblConditions.getColumnModel().getColumn(4).setMaxWidth(80);
        tblConditions.getColumnModel().getColumn(4).setMinWidth(80);
        tblConditions.getColumnModel().getColumn(4).setWidth(80);
        tblConditions.getColumnModel().getColumn(4).setResizable(false);
    }

    /*
     * Sync from session to the UI.
     */
     private void updateCurrentSession() {

        //currentSession.setMainClass(txtMainClass.getText());
        //currentSession.setClasspathString(txtClasspath.getText());
        //currentSession.setArguments(txtArguments.getText());
        //currentSession.setJavaArguments(txtJavaArgs.getText());
        //currentSession.setWorkingDir(txtWorkingDir.getText());

        // function hooks are kept updated by the GUI already, no
        // need to update their state in the "currentSession"

    }

     private void updateTitle() {
         String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
         StringBuilder sb = new StringBuilder();
         sb.append("JavaSnoop - attached to process ");
         sb.append(pid);
         if(currentSession.alreadyBeenSaved()) {
             sb.append(" [");
             sb.append(currentSession.getSnoopSessionFilename());
             sb.append("]");
         }
         setTitle(sb.toString());
     }

    @Action
    public void dumpThreads() {
        Map<Thread,StackTraceElement[]> threadStacks = Thread.getAllStackTraces();
        StringBuilder sb = new StringBuilder();
        for(Thread t : threadStacks.keySet()) {
            StackTraceElement[] stack = threadStacks.get(t);
            sb.append("Thread: ");
            sb.append(t.getName());
            sb.append(" (id=");
            sb.append(t.getId());
            sb.append(")");
            sb.append(nl);
            for(StackTraceElement frame : stack) {
                sb.append("  ");
                sb.append(frame);
                sb.append(nl);
            }
        }
        showSnoopMessage(sb.toString());
    }

    @Action
    public void dumpSourceCode() {

        String startingDir = lastDumpDirectory != null ? lastDumpDirectory : System.getProperty("user.dir");
        File dir = UIUtil.getFileSelection(this,true,startingDir);

        if ( dir == null ) // user cancelled
            return;

        if ( JadUtil.getJadLocation() == null ) {
            UIUtil.showErrorMessage(this, "Can't dump source without Jad location set. Go to Settings->Manage Jad->Set jad path.");
            return;
        }

        manager.updateClassPool();

        AgentLogger.debug("Looking up source code for the following URLs:");
        for(java.net.URL u : manager.getCodeSourceURLs()) {
            AgentLogger.debug(u.toString());
        }

        DumpSourceCodeView view = new DumpSourceCodeView(this,true, manager, dir);
        view.startDump();
        view.setVisible(true);

        UIUtil.waitForInput(view);

    }

    private String lastDumpDirectory = JavaSnoop.getProperty(JavaSnoop.LAST_DUMPED_DIR);

}
