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

import com.aspect.snoop.JavaSnoop;
import com.aspect.snoop.agent.AgentCommunicationException;
import com.aspect.snoop.ui.choose.clazz.ChooseClassView;
import com.aspect.snoop.ui.choose.process.ChooseProcessView;
import com.aspect.snoop.ui.condition.ConditionTableModel;
import com.aspect.snoop.ui.condition.ConditionTableCellRenderer;
import com.aspect.snoop.ui.condition.AddEditConditionView;
import com.aspect.snoop.ui.hook.FunctionsHookedTableModel;
import com.aspect.snoop.ui.hook.FunctionHookTableSelectionListener;
import com.aspect.snoop.ui.hook.AddFunctionHookView;
import com.aspect.snoop.Condition;
import com.aspect.snoop.FunctionHook;
import com.aspect.snoop.MethodInterceptor;
import com.aspect.snoop.MethodInterceptor.Mode;
import com.aspect.snoop.SnoopSession;
import com.aspect.snoop.agent.ProcessInfo;
import com.aspect.snoop.agent.SnoopToAgentClient;
import com.aspect.snoop.ui.canary.StartCanaryModeView;
import com.aspect.snoop.ui.pause.PauseView;
import com.aspect.snoop.ui.script.EditScriptView;
import com.aspect.snoop.ui.tamper.Parameter;
import com.aspect.snoop.ui.tamper.ParameterTamperingView;
import com.aspect.snoop.util.AttachUtil;
import com.aspect.snoop.util.ClasspathUtil;
import com.aspect.snoop.util.ConditionUtil;
import com.aspect.snoop.util.JadUtil;
import com.aspect.snoop.util.ReflectionUtil;
import com.aspect.snoop.util.SessionPersistenceUtil;
import com.aspect.snoop.util.SnoopClassLoader;
import com.aspect.snoop.util.UIUtil;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FileDialog;
import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import javax.swing.text.BadLocationException;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

/**
 * The application's main frame.
 */
public class JavaSnoopView extends FrameView {

    private static String faqUrl = "http://www.aspectsecurity.com/tools/javasnoop/javasnoop_faq.html";
    private static String homeUrl = "http://www.aspectsecurity.com/tools/javasnoop/";
    private static final String nl = System.getProperty("line.separator");

    private static File lastConfigurationDirectory;
    private static File lastConfigurationFile;
    
    private SnoopSession currentSession;
    private SnoopToAgentClient client;
    private StyledDocument console;

    private boolean firstTimeLoading = true;

    private JPopupMenu popupMenu;

    List<JCheckBoxMenuItem> mnuLogLevels;

    protected StartCanaryModeView canaryView;

    private static Logger logger = Logger.getLogger(JavaSnoopView.class);

    public JavaSnoopView(SingleFrameApplication app) {

        super(app);
        
        initializeSession();

        chkShowMethodCode.setSelected( JavaSnoop.getBooleanProperty(JavaSnoop.USE_JAD,false) );

        getFrame().setResizable(false);

        String icon = "/META-INF/about.png";
        
        try {
           getFrame().setIconImage(ImageIO.read(this.getClass().getResourceAsStream(icon)));
        } catch(IOException e) {
            // couldn't load snoopy icon. oh well. rip schulz
        }

        // Get the mnulog items altogether
        mnuLogLevels = new ArrayList<JCheckBoxMenuItem>();
        mnuLogLevels.add(mnuLogOff);
        mnuLogLevels.add(mnuLogFatal);
        mnuLogLevels.add(mnuLogError);
        mnuLogLevels.add(mnuLogInfo);
        mnuLogLevels.add(mnuLogWarn);
        mnuLogLevels.add(mnuLogDebug);
        mnuLogLevels.add(mnuLogTrace);
        
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
        
        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = JavaSnoop.getApplication().getMainFrame();
            aboutBox = new JavaSnoopAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        JavaSnoop.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        functionHookPanel = new javax.swing.JPanel();
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
        programSetupPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtMainClass = new javax.swing.JTextField();
        txtClasspath = new javax.swing.JTextField();
        txtArguments = new javax.swing.JTextField();
        btnBrowseForMainClass = new javax.swing.JButton();
        btnAttachSpy = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        txtWorkingDir = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtJavaArgs = new javax.swing.JTextField();
        btnStartSpy = new javax.swing.JButton();
        btnStopSpy = new javax.swing.JButton();
        btnKillProgram = new javax.swing.JButton();
        reportingPanel = new javax.swing.JPanel();
        lblSnoopingOrNot = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        lblFilename = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        mnuLoadConfiguration = new javax.swing.JMenuItem();
        mnuSaveConfiguration = new javax.swing.JMenuItem();
        mnuSaveConfigurationAs = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        javax.swing.JMenuItem mnuExit = new javax.swing.JMenuItem();
        actionsMenu = new javax.swing.JMenu();
        mnuGetProcessInfo = new javax.swing.JMenuItem();
        mnuBrowseRemoteClasses = new javax.swing.JMenuItem();
        mnuStartCanaryMode = new javax.swing.JMenuItem();
        mnuDecompileClass = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        mnuManageJavaAppletSecuritySettings = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        mnuManageJad = new javax.swing.JMenu();
        chkShowMethodCode = new javax.swing.JCheckBoxMenuItem();
        mnuSetJadPath = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        mnuLogSetting = new javax.swing.JMenu();
        mnuLogTrace = new javax.swing.JCheckBoxMenuItem();
        mnuLogDebug = new javax.swing.JCheckBoxMenuItem();
        mnuLogInfo = new javax.swing.JCheckBoxMenuItem();
        mnuLogWarn = new javax.swing.JCheckBoxMenuItem();
        mnuLogError = new javax.swing.JCheckBoxMenuItem();
        mnuLogFatal = new javax.swing.JCheckBoxMenuItem();
        mnuLogOff = new javax.swing.JCheckBoxMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem mnuAbout = new javax.swing.JMenuItem();
        mnuGotoHomePage = new javax.swing.JMenuItem();
        mnuViewFAQ = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        btnGrpHookConditions = new javax.swing.ButtonGroup();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setRequestFocusEnabled(false);
        mainPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                onMainPanelResize(evt);
            }
        });

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.aspect.snoop.JavaSnoop.class).getContext().getResourceMap(JavaSnoopView.class);
        functionHookPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("functionHookPanel.border.title"))); // NOI18N
        functionHookPanel.setName("functionHookPanel"); // NOI18N

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
        tblFunctionsHooked.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(tblFunctionsHooked);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("pnlOnExecution.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("pnlOnExecution.border.titleFont"))); // NOI18N
        jPanel1.setName("pnlOnExecution"); // NOI18N

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
                .addComponent(chkPause)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(chkOutputToFile)
                                .addComponent(chkOutputToConsole))
                            .addComponent(chkPrintParameters))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkPrintStackTrace))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(2, 2, 2)))
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

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("pnlConditions.border.title"))); // NOI18N
        jPanel2.setName("pnlConditions"); // NOI18N

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rdoDontHookIf)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                        .addComponent(btnAddNewCondition)
                        .addContainerGap())
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdoAlwaysHook)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(rdoHookIf)
                        .addComponent(rdoDontHookIf)
                        .addComponent(btnAddNewCondition)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnAddHook.setText(resourceMap.getString("btnAddHook.text")); // NOI18N
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

        javax.swing.GroupLayout functionHookPanelLayout = new javax.swing.GroupLayout(functionHookPanel);
        functionHookPanel.setLayout(functionHookPanelLayout);
        functionHookPanelLayout.setHorizontalGroup(
            functionHookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(functionHookPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(functionHookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, functionHookPanelLayout.createSequentialGroup()
                        .addGroup(functionHookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(functionHookPanelLayout.createSequentialGroup()
                                .addComponent(btnAddHook)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDeleteHook))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(functionHookPanelLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tabConsoleCode, javax.swing.GroupLayout.PREFERRED_SIZE, 551, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        functionHookPanelLayout.setVerticalGroup(
            functionHookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(functionHookPanelLayout.createSequentialGroup()
                .addGroup(functionHookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, functionHookPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addGroup(functionHookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnAddHook)
                            .addComponent(btnDeleteHook)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(functionHookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tabConsoleCode, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        programSetupPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("programSetupPanel.border.title"))); // NOI18N
        programSetupPanel.setName("programSetupPanel"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        txtMainClass.setText(resourceMap.getString("txtMainClass.text")); // NOI18N
        txtMainClass.setName("txtMainClass"); // NOI18N

        txtClasspath.setText(resourceMap.getString("txtClasspath.text")); // NOI18N
        txtClasspath.setName("txtClasspath"); // NOI18N

        txtArguments.setText(resourceMap.getString("txtArguments.text")); // NOI18N
        txtArguments.setName("txtArguments"); // NOI18N

        btnBrowseForMainClass.setAction(actionMap.get("showChooseClassForm")); // NOI18N
        btnBrowseForMainClass.setText(resourceMap.getString("btnBrowseForMainClass.text")); // NOI18N
        btnBrowseForMainClass.setFocusable(false);
        btnBrowseForMainClass.setName("btnBrowseForMainClass"); // NOI18N

        btnAttachSpy.setAction(actionMap.get("beginProgramSpy")); // NOI18N
        btnAttachSpy.setText(resourceMap.getString("btnAttachSpy.text")); // NOI18N
        btnAttachSpy.setFocusable(false);
        btnAttachSpy.setName("btnAttachSpy"); // NOI18N

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        txtWorkingDir.setText(resourceMap.getString("txtWorkingDir.text")); // NOI18N
        txtWorkingDir.setName("txtWorkingDir"); // NOI18N

        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        txtJavaArgs.setText(resourceMap.getString("txtJavaArguments.text")); // NOI18N
        txtJavaArgs.setName("txtJavaArguments"); // NOI18N

        btnStartSpy.setAction(actionMap.get("beginProgramSpy")); // NOI18N
        btnStartSpy.setText(resourceMap.getString("btnStartSpy.text")); // NOI18N
        btnStartSpy.setFocusable(false);
        btnStartSpy.setName("btnStartSpy"); // NOI18N

        btnStopSpy.setText(resourceMap.getString("btnStopSpy.text")); // NOI18N
        btnStopSpy.setEnabled(false);
        btnStopSpy.setFocusable(false);
        btnStopSpy.setName("btnStopSpy"); // NOI18N
        btnStopSpy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopSpyActionPerformed(evt);
            }
        });

        btnKillProgram.setText(resourceMap.getString("btnKillProgram.text")); // NOI18N
        btnKillProgram.setEnabled(false);
        btnKillProgram.setFocusable(false);
        btnKillProgram.setName("btnKillProgram"); // NOI18N
        btnKillProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKillProgramActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout programSetupPanelLayout = new javax.swing.GroupLayout(programSetupPanel);
        programSetupPanel.setLayout(programSetupPanelLayout);
        programSetupPanelLayout.setHorizontalGroup(
            programSetupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(programSetupPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(programSetupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(programSetupPanelLayout.createSequentialGroup()
                        .addComponent(btnStartSpy, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAttachSpy, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnStopSpy)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnKillProgram))
                    .addGroup(programSetupPanelLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtWorkingDir, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtJavaArgs, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                    .addGroup(programSetupPanelLayout.createSequentialGroup()
                        .addGroup(programSetupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addGap(8, 8, 8)
                        .addGroup(programSetupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(programSetupPanelLayout.createSequentialGroup()
                                .addComponent(txtMainClass, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBrowseForMainClass, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                            .addComponent(txtArguments, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                            .addComponent(txtClasspath, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE))))
                .addContainerGap())
        );
        programSetupPanelLayout.setVerticalGroup(
            programSetupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(programSetupPanelLayout.createSequentialGroup()
                .addGroup(programSetupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMainClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(btnBrowseForMainClass))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(programSetupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtClasspath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(programSetupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtArguments, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(programSetupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtWorkingDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel15)
                    .addComponent(txtJavaArgs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(programSetupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStartSpy)
                    .addComponent(btnAttachSpy)
                    .addComponent(btnStopSpy)
                    .addComponent(btnKillProgram)))
        );

        reportingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("reportingPanel.border.title"))); // NOI18N
        reportingPanel.setName("reportingPanel"); // NOI18N

        lblSnoopingOrNot.setFont(resourceMap.getFont("lblStatus.font")); // NOI18N
        lblSnoopingOrNot.setForeground(resourceMap.getColor("lblStatus.foreground")); // NOI18N
        lblSnoopingOrNot.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSnoopingOrNot.setText(resourceMap.getString("lblStatus.text")); // NOI18N
        lblSnoopingOrNot.setName("lblStatus"); // NOI18N

        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N

        lblFilename.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblFilename.setText(resourceMap.getString("lblFilename.text")); // NOI18N
        lblFilename.setName("lblFilename"); // NOI18N

        javax.swing.GroupLayout reportingPanelLayout = new javax.swing.GroupLayout(reportingPanel);
        reportingPanel.setLayout(reportingPanelLayout);
        reportingPanelLayout.setHorizontalGroup(
            reportingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reportingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(reportingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(reportingPanelLayout.createSequentialGroup()
                        .addComponent(lblFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                        .addContainerGap())
                    .addComponent(lblSnoopingOrNot)
                    .addGroup(reportingPanelLayout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addContainerGap(172, Short.MAX_VALUE))))
        );
        reportingPanelLayout.setVerticalGroup(
            reportingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reportingPanelLayout.createSequentialGroup()
                .addComponent(lblSnoopingOrNot)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFilename, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(58, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(programSetupPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reportingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(functionHookPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(reportingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(programSetupPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(functionHookPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        jMenuItem2.setAction(actionMap.get("newSession")); // NOI18N
        jMenuItem2.setText(resourceMap.getString("jMenuItem2.text")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        fileMenu.add(jMenuItem2);

        jSeparator2.setName("jSeparator2"); // NOI18N
        fileMenu.add(jSeparator2);

        mnuLoadConfiguration.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        mnuLoadConfiguration.setText(resourceMap.getString("mnuLoadConfiguration.text")); // NOI18N
        mnuLoadConfiguration.setName("mnuLoadConfiguration"); // NOI18N
        mnuLoadConfiguration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLoadConfigurationActionPerformed(evt);
            }
        });
        fileMenu.add(mnuLoadConfiguration);

        mnuSaveConfiguration.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        mnuSaveConfiguration.setText(resourceMap.getString("mnuSaveConfiguration.text")); // NOI18N
        mnuSaveConfiguration.setEnabled(false);
        mnuSaveConfiguration.setName("mnuSaveConfiguration"); // NOI18N
        mnuSaveConfiguration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaveConfigurationActionPerformed(evt);
            }
        });
        fileMenu.add(mnuSaveConfiguration);

        mnuSaveConfigurationAs.setText(resourceMap.getString("mnuSaveConfigurationAs.text")); // NOI18N
        mnuSaveConfigurationAs.setName("mnuSaveConfigurationAs"); // NOI18N
        mnuSaveConfigurationAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaveConfigurationAsActionPerformed(evt);
            }
        });
        fileMenu.add(mnuSaveConfigurationAs);

        jSeparator3.setName("jSeparator3"); // NOI18N
        fileMenu.add(jSeparator3);

        mnuExit.setAction(actionMap.get("quit")); // NOI18N
        mnuExit.setName("mnuExit"); // NOI18N
        fileMenu.add(mnuExit);

        menuBar.add(fileMenu);

        actionsMenu.setText(resourceMap.getString("actionsMenu.text")); // NOI18N
        actionsMenu.setName("actionsMenu"); // NOI18N

        mnuGetProcessInfo.setAction(actionMap.get("getProcessInfo")); // NOI18N
        mnuGetProcessInfo.setText(resourceMap.getString("mnuGetProcessInfo.text")); // NOI18N
        mnuGetProcessInfo.setEnabled(false);
        mnuGetProcessInfo.setName("mnuGetProcessInfo"); // NOI18N
        actionsMenu.add(mnuGetProcessInfo);

        mnuBrowseRemoteClasses.setAction(actionMap.get("browseRemoteClasses")); // NOI18N
        mnuBrowseRemoteClasses.setText(resourceMap.getString("mnuBrowseRemoteClasses.text")); // NOI18N
        mnuBrowseRemoteClasses.setEnabled(false);
        mnuBrowseRemoteClasses.setName("mnuBrowseRemoteClasses"); // NOI18N
        actionsMenu.add(mnuBrowseRemoteClasses);

        mnuStartCanaryMode.setAction(actionMap.get("enterCanaryMode")); // NOI18N
        mnuStartCanaryMode.setText(resourceMap.getString("mnuStartCanaryMode.text")); // NOI18N
        mnuStartCanaryMode.setEnabled(false);
        mnuStartCanaryMode.setName("mnuStartCanaryMode"); // NOI18N
        actionsMenu.add(mnuStartCanaryMode);

        mnuDecompileClass.setAction(actionMap.get("decompileClass")); // NOI18N
        mnuDecompileClass.setText(resourceMap.getString("mnuDecompileClass.text")); // NOI18N
        mnuDecompileClass.setName("mnuDecompileClass"); // NOI18N
        actionsMenu.add(mnuDecompileClass);

        jSeparator1.setEnabled(false);
        jSeparator1.setName("jSeparator1"); // NOI18N
        actionsMenu.add(jSeparator1);

        mnuManageJavaAppletSecuritySettings.setAction(actionMap.get("manageJavaSecuritySettings")); // NOI18N
        mnuManageJavaAppletSecuritySettings.setText(resourceMap.getString("mnuManageJavaAppletSecuritySettings.text")); // NOI18N
        mnuManageJavaAppletSecuritySettings.setEnabled(false);
        mnuManageJavaAppletSecuritySettings.setName("mnuManageJavaAppletSecuritySettings"); // NOI18N
        actionsMenu.add(mnuManageJavaAppletSecuritySettings);

        jSeparator4.setName("jSeparator4"); // NOI18N
        actionsMenu.add(jSeparator4);

        mnuManageJad.setText(resourceMap.getString("mnuManageJad.text")); // NOI18N
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
        mnuManageJad.add(mnuSetJadPath);

        actionsMenu.add(mnuManageJad);

        jSeparator5.setName("jSeparator5"); // NOI18N
        actionsMenu.add(jSeparator5);

        mnuLogSetting.setText(resourceMap.getString("mnuLogSetting.text")); // NOI18N
        mnuLogSetting.setName("mnuLogSetting"); // NOI18N

        mnuLogTrace.setAction(actionMap.get("changeLogLevel")); // NOI18N
        mnuLogTrace.setText(resourceMap.getString("mnuLogTrace.text")); // NOI18N
        mnuLogTrace.setName("mnuLogTrace"); // NOI18N
        mnuLogSetting.add(mnuLogTrace);

        mnuLogDebug.setAction(actionMap.get("changeLogLevel")); // NOI18N
        mnuLogDebug.setSelected(true);
        mnuLogDebug.setText(resourceMap.getString("mnuLogDebug.text")); // NOI18N
        mnuLogDebug.setName("mnuLogDebug"); // NOI18N
        mnuLogSetting.add(mnuLogDebug);

        mnuLogInfo.setAction(actionMap.get("changeLogLevel")); // NOI18N
        mnuLogInfo.setText(resourceMap.getString("mnuLogInfo.text")); // NOI18N
        mnuLogInfo.setName("mnuLogInfo"); // NOI18N
        mnuLogSetting.add(mnuLogInfo);

        mnuLogWarn.setAction(actionMap.get("changeLogLevel")); // NOI18N
        mnuLogWarn.setText(resourceMap.getString("mnuLogWarn.text")); // NOI18N
        mnuLogWarn.setName("mnuLogWarn"); // NOI18N
        mnuLogSetting.add(mnuLogWarn);

        mnuLogError.setAction(actionMap.get("changeLogLevel")); // NOI18N
        mnuLogError.setText(resourceMap.getString("mnuLogError.text")); // NOI18N
        mnuLogError.setName("mnuLogError"); // NOI18N
        mnuLogSetting.add(mnuLogError);

        mnuLogFatal.setAction(actionMap.get("changeLogLevel")); // NOI18N
        mnuLogFatal.setText(resourceMap.getString("mnuLogFatal.text")); // NOI18N
        mnuLogFatal.setName("mnuLogFatal"); // NOI18N
        mnuLogSetting.add(mnuLogFatal);

        mnuLogOff.setAction(actionMap.get("changeLogLevel")); // NOI18N
        mnuLogOff.setText(resourceMap.getString("mnuLogOff.text")); // NOI18N
        mnuLogOff.setName("mnuLogOff"); // NOI18N
        mnuLogSetting.add(mnuLogOff);

        actionsMenu.add(mnuLogSetting);

        menuBar.add(actionsMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        mnuAbout.setAction(actionMap.get("showAboutBox")); // NOI18N
        mnuAbout.setName("mnuAbout"); // NOI18N
        helpMenu.add(mnuAbout);

        mnuGotoHomePage.setAction(actionMap.get("browseToHomePage")); // NOI18N
        mnuGotoHomePage.setText(resourceMap.getString("mnuGotoHomePage.text")); // NOI18N
        mnuGotoHomePage.setName("mnuGotoHomePage"); // NOI18N
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
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 878, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 708, Short.MAX_VALUE)
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

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddHookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddHookActionPerformed

        AddFunctionHookView view = new AddFunctionHookView(getFrame(), true, txtClasspath.getText());

        view.setVisible(true);

        UIUtil.waitForInput(view);

        if (view.getSelectedClass() != null) {

            String selectedClass = view.getSelectedClass();
            String selectedMethod = view.getSelectedMethod();
            String[] parameterTypes = view.getParameterTypes();
            String returnType = view.getReturnType();

            /*
             * Before adding it, we make sure a hook for this class/method hasn't
             * already been added.
             */
            for(int i=0;i<currentSession.getFunctionHooks().size();i++) {
                FunctionHook oldHook = currentSession.getFunctionHooks().get(i);
                if ( oldHook.getClassName().equals(selectedClass) ) {
                    if ( oldHook.getMethodName().equals(selectedMethod) ) {
                        if ( Arrays.equals(oldHook.getParameterTypes(),parameterTypes)) {
                           JOptionPane.showMessageDialog(getFrame(), "That method is already hooked!","Error adding hook",JOptionPane.ERROR_MESSAGE);
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
                    selectedMethod,
                    parameterTypes,
                    returnType,
                    shouldInherit,
                    MethodInterceptor.Mode.AlwaysIntercept,
                    true,
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

    private void onMainPanelResize(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_onMainPanelResize
    }//GEN-LAST:event_onMainPanelResize

    private void btnAddNewConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNewConditionActionPerformed

        FunctionHook hook = getCurrentHook();

        // if no hook is selected, fail. we need to know which hook to add
        // the condition to
        if (hook == null) {
            return;
        }

        AddEditConditionView view = new AddEditConditionView(getFrame(), true, hook.getParameterTypes());
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

    private void chkTamperParametersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTamperParametersActionPerformed

        FunctionsHookedTableModel model = (FunctionsHookedTableModel) tblFunctionsHooked.getModel();
        FunctionHook hook = model.getHookFromRow(tblFunctionsHooked.getSelectedRow());
        hook.setShouldTamperParameters(chkTamperParameters.isSelected());

        if ( hook.isEnabled() ) {
            sendAgentNewRules();
        }

    }//GEN-LAST:event_chkTamperParametersActionPerformed

    private void chkRunScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRunScriptActionPerformed
        FunctionHook hook = getCurrentHook();
        hook.setShouldRunScript(chkRunScript.isSelected());

        if ( hook.isEnabled() ) {
            sendAgentNewRules();
        }
    }//GEN-LAST:event_chkRunScriptActionPerformed

    private void chkPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPauseActionPerformed
        FunctionHook hook = getCurrentHook();
        hook.setShouldPause(chkPause.isSelected());

        if ( hook.isEnabled() ) {
            sendAgentNewRules();
        }
    }//GEN-LAST:event_chkPauseActionPerformed

    private void chkPrintParametersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPrintParametersActionPerformed
        FunctionHook hook = getCurrentHook();
        hook.setShouldPrintParameters(chkPrintParameters.isSelected());

        if ( hook.isEnabled() ) {
            sendAgentNewRules();
        }
    }//GEN-LAST:event_chkPrintParametersActionPerformed

    private void chkOutputToFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOutputToFileActionPerformed
        FunctionHook hook = getCurrentHook();
        hook.setOutputToFile(chkOutputToFile.isSelected());
    }//GEN-LAST:event_chkOutputToFileActionPerformed

    private void chkOutputToConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOutputToConsoleActionPerformed
        FunctionHook hook = getCurrentHook();
        hook.setOutputToConsole(chkOutputToConsole.isSelected());
    }//GEN-LAST:event_chkOutputToConsoleActionPerformed

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

    private void btnStopSpyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopSpyActionPerformed

        try {

            int rc = JOptionPane.showConfirmDialog(getFrame(), "Are you sure you want to stop snooping? You will not be able to re-attach to this process later.");

            if (rc == JOptionPane.YES_OPTION) {
                statusMessageLabel.setText("Telling agent to stop snooping...");
                client.stopSnooping();
                disableSnoopUI();
                statusMessageLabel.setText("Done");
            }

        } catch (AgentCommunicationException ex) {
            UIUtil.showErrorMessage(getFrame(), "Could not communicate with agent: " + ex.getMessage());
            statusMessageLabel.setText("Failed to communicate with agent while stopping");
            disableSnoopUI(); // the failure to communicate at least shows the agent is no longer working!
        }

    }//GEN-LAST:event_btnStopSpyActionPerformed

    private void btnKillProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKillProgramActionPerformed

        try {

            statusMessageLabel.setText("Sending kill message to target program...");
            client.killTargetProgram();
            statusMessageLabel.setText("Done");
            disableSnoopUI();

        } catch (AgentCommunicationException ex) {
            UIUtil.showErrorMessage(getFrame(), "Could not kill target program: " + ex.getMessage());
        }
    }//GEN-LAST:event_btnKillProgramActionPerformed

    private void btnEditScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditScriptActionPerformed

        FunctionHook hook = getCurrentHook();

        EditScriptView view = new EditScriptView(getFrame(), true, hook.getStartScript(), hook.getEndScript());
        view.setVisible(true);

        UIUtil.waitForInput(view);

        if (view.getStartScript() != null) {
            hook.setStartScript(view.getStartScript());
            hook.setEndScript(view.getEndScript());
        }

        sendAgentNewRules();

    }//GEN-LAST:event_btnEditScriptActionPerformed

    private void mnuViewFAQActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuViewFAQActionPerformed
        try {
            Desktop.getDesktop().browse(URI.create(faqUrl));
        } catch (IOException ex) {
            showConsoleErrorMessage("Couldn't browse to FAQ page: " + ex.getMessage());
        }
    }//GEN-LAST:event_mnuViewFAQActionPerformed

    private void btnDeleteHookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteHookActionPerformed
        
        FunctionHook hook = getCurrentHook();
        
        if (hook != null) {
            int rc = JOptionPane.showConfirmDialog(getFrame(), "Are you sure you want to delete this hook?");
            if (rc == JOptionPane.YES_OPTION) {
                FunctionsHookedTableModel model = (FunctionsHookedTableModel) tblFunctionsHooked.getModel();

                model.removeHook(hook);
                updateSessionUI(false);
            }

            sendAgentNewRules();
        }
        
    }//GEN-LAST:event_btnDeleteHookActionPerformed

    private void mnuSaveConfigurationAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaveConfigurationAsActionPerformed
        try {
            File file = null;

            // show file input dialog, get target filename
            if (System.getProperty("os.name").toLowerCase().indexOf("mac") != -1) {

                FileDialog fileDialog = new FileDialog(getFrame(), "Save Configuration", FileDialog.SAVE);
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

                int rc = fc.showOpenDialog(getFrame());

                if (rc == JFileChooser.APPROVE_OPTION) {
                    file = fc.getSelectedFile();
                }
            }

            if (file != null) {
                lastConfigurationFile = file;

                // save file
                logger.error("Saving configuration file: " + file.getAbsolutePath());
                updateCurrentSession();

                SessionPersistenceUtil.saveSession(currentSession, file.getAbsolutePath());
                statusMessageLabel.setText("Saved session to " + file.getAbsolutePath() + " at " + getHumanTime());

                // enable the "Save Configuration" menu item, since we have a
                // default filename to save to now

                mnuSaveConfiguration.setEnabled(true);

                // putting the html tags will allow it to wrap
                lblFilename.setText("<html>" + currentSession.getSnoopSessionFilename() + "</html>");
            }

        } catch (FileNotFoundException ex) {
            logger.error(ex);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }//GEN-LAST:event_mnuSaveConfigurationAsActionPerformed

    private void mnuSaveConfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaveConfigurationActionPerformed

        try {
            // save file where it is
            SessionPersistenceUtil.saveSession(currentSession);
            statusMessageLabel.setText("Saved session to " + new File(currentSession.getSnoopSessionFilename()).getAbsolutePath() + " at " + getHumanTime());

            lblFilename.setText("<html>" + currentSession.getSnoopSessionFilename() + "</html>");

        } catch (FileNotFoundException ex) {
            logger.error(ex);
        } catch (IOException ex) {
            logger.error(ex);
        }
}//GEN-LAST:event_mnuSaveConfigurationActionPerformed

    private void mnuLoadConfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLoadConfigurationActionPerformed

        try {

            File file = null;

            // show open file dialog
            if (System.getProperty("os.name").toLowerCase().indexOf("mac") != -1) {
                FileDialog fileDialog = new FileDialog(getFrame(), "Load Configuration", FileDialog.LOAD);
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
                int rc = fc.showOpenDialog(getFrame());
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
            logger.error(ex);
        } catch (IOException ex) {
            logger.error(ex);
        }
}//GEN-LAST:event_mnuLoadConfigurationActionPerformed

    private void chkTamperReturnValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTamperReturnValueActionPerformed
        FunctionHook hook = getCurrentHook();
        hook.setShouldTamperReturnValue(chkTamperReturnValue.isSelected());

        if ( hook.isEnabled() ) {
            sendAgentNewRules();
        }
    }//GEN-LAST:event_chkTamperReturnValueActionPerformed

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

    private void chkShowMethodCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowMethodCodeActionPerformed
        boolean useJad = chkShowMethodCode.isSelected();
        JavaSnoop.setProperty(JavaSnoop.USE_JAD, Boolean.toString(useJad));
        JavaSnoop.saveProperties();
    }//GEN-LAST:event_chkShowMethodCodeActionPerformed

    @Action
    public void enableDisableAllHooks(ActionEvent evt) {

        FunctionsHookedTableModel model = (FunctionsHookedTableModel) tblFunctionsHooked.getModel();

        if (evt.getActionCommand().startsWith("Enable")) {
            model.enableAll();
        } else {
            model.disableAll();
        }

        updateSessionUI(false);
    }

    @Action
    public void showChooseClassForm() {
        ChooseClassView ccv = new ChooseClassView(getFrame(), ClasspathUtil.getClasses(txtClasspath.getText()), true);
        ccv.setVisible(true);

        UIUtil.waitForInput(ccv);

        if (ccv.getClassName() != null) {
            txtMainClass.setText(ccv.getClassName());
        }
    }

    @Action
    public void beginProgramSpy(ActionEvent evt) {

        btnStartSpy.setEnabled(false);
        btnAttachSpy.setEnabled(false);

        final boolean isAttaching = evt.getActionCommand().startsWith("Attach");

        if (isAttaching) {
            attachingUI("ATTACHING...");
        } else {
            attachingUI("STARTING...");
        }

        startProgressBar();
        
        String mainClass = txtMainClass.getText().trim();
        String cp = txtClasspath.getText().trim();
        boolean isJar = false; // not used for now
        
        try {

            SnoopClassLoader loader = JavaSnoop.getClassLoader();
            loader.setClasspath(txtClasspath.getText());

            if (!isAttaching) {

                if ( mainClass.length() == 0 ) {
                    if ( cp.length() != 0 && cp.endsWith(".jar") ) {
                        isJar = true;
                    } else {
                        UIUtil.showErrorMessage(getFrame(), "No main class specified and no jar specified in classpath");
                        disableSnoopUI();
                        return;
                    }
                } else {
                    Class c = Class.forName(mainClass, true, loader);
                    if (!ReflectionUtil.hasMainClass(c)) {
                        UIUtil.showErrorMessage(getFrame(), "Specified class does not have main() method");
                        disableSnoopUI();
                        return;
                    }
                }
            }

        } catch (ClassNotFoundException ex) {
            UIUtil.showErrorMessage(getFrame(), "Could not find main class: " + mainClass);
            disableSnoopUI();
            return;
        }

        client = null;

        updateCurrentSession();

        final JavaSnoopView mainView = this;

        SwingWorker attachWorker = new SwingWorker<SnoopToAgentClient, Void>() {

            public SnoopToAgentClient doInBackground() {

                try {

                    if (isAttaching) {
                        // the user hit the "Attach and Spy" button
                        ChooseProcessView view = new ChooseProcessView(mainView, true);
                        view.setVisible(true);

                        UIUtil.waitForInput(view);

                        String pid = view.getPid();

                        if (pid != null) {
                            return AttachUtil.attachToVM(pid);
                        }

                    } else {
                        // the user hit the "Start and Spy" button
                        if (JavaSnoop.getBooleanProperty(JavaSnoop.SEPARATE_VM,true)) {
                            statusMessageLabel.setText("Spawning new process to attach...");
                            return AttachUtil.launchInNewVM(currentSession);
                        } else {
                            statusMessageLabel.setText("Running the Main-Class in the current JVM...");
                            return AttachUtil.launchInThisVM(txtMainClass.getText());
                        }
                    }

                } catch (AgentCommunicationException ex) {
                    logger.error(ex);
                    UIUtil.showErrorMessage(getFrame(), "Could not communicate with agent. It's possible that this process has already been attached to once.");
                } catch (ClassNotFoundException ex) {
                    logger.error(ex);
                    UIUtil.showErrorMessage(getFrame(), "Could not find class: " + ex.getMessage());
                } catch (NoSuchMethodException ex) {
                    logger.error(ex);
                    UIUtil.showErrorMessage(getFrame(), "Could not find main method in class: " + ex.getMessage());
                } catch (AttachNotSupportedException ex) {
                    logger.error(ex);
                    UIUtil.showErrorMessage(getFrame(), "Targeted virtual machine does not support attaching: " + ex.getMessage());
                } catch (AgentLoadException ex) {
                    logger.error(ex);
                    UIUtil.showErrorMessage(getFrame(), "Could not load agent: " + ex.getMessage());
                } catch (AgentInitializationException ex) {
                    logger.error(ex);
                    UIUtil.showErrorMessage(getFrame(), "Could not initialize agent: " + ex.getMessage());
                } catch (IOException ex) {
                    logger.error(ex);
                    UIUtil.showErrorMessage(getFrame(), "Could not attach to new virtual machine due to I/O error: " + ex.getMessage());
                } finally {
                    stopProgressBar();
                }

                setStatus("NOT SNOOPING", Color.red);
                return null;
            }

            @Override
            public void done() {
                try {

                    client = get();

                    if (client != null) {

                        statusMessageLabel.setText("Sending agent the hook rules...");
                        client.sendSnoopSession(currentSession);

                        statusMessageLabel.setText("Telling the agent to begin snooping...");
                        client.startSnooping();

                        statusMessageLabel.setText("Importing remote classes...");
                        client.importRemoteClasses();

                        enableSpyUI();

                        statusMessageLabel.setText("Done");

                    } else {

                        disableSnoopUI();

                        btnStartSpy.setEnabled(true);
                        btnAttachSpy.setEnabled(true);

                    }

                } catch (InterruptedException ex) {
                    statusMessageLabel.setText("Error attaching agent");
                    disableSnoopUI();
                    UIUtil.showErrorMessage(getFrame(), "Encountered interruption exception when starting agent: " + ex.getMessage());
                    logger.error(ex);
                } catch (ExecutionException ex) {
                    statusMessageLabel.setText("Error attaching agent");
                    disableSnoopUI();
                    UIUtil.showErrorMessage(getFrame(), "Encountered execution exception when starting agent: " + ex.getMessage());
                    logger.error(ex);
                    ex.printStackTrace();
                } catch (AgentCommunicationException ex) {
                    statusMessageLabel.setText("Error attaching agent");
                    disableSnoopUI();
                    UIUtil.showErrorMessage(getFrame(), "Encountered communication error while starting agent snooping: " + ex.getMessage());
                    logger.error(ex);
                }

            }
        };

        attachWorker.execute();

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu actionsMenu;
    private javax.swing.JButton btnAddHook;
    private javax.swing.JButton btnAddNewCondition;
    private javax.swing.JButton btnAttachSpy;
    private javax.swing.JButton btnBrowseForMainClass;
    private javax.swing.JButton btnBrowseForOutputFile;
    private javax.swing.JButton btnDeleteHook;
    private javax.swing.JButton btnEditScript;
    private javax.swing.ButtonGroup btnGrpHookConditions;
    private javax.swing.JButton btnKillProgram;
    private javax.swing.JButton btnStartSpy;
    private javax.swing.JButton btnStopSpy;
    private javax.swing.JCheckBox chkOutputToConsole;
    private javax.swing.JCheckBox chkOutputToFile;
    private javax.swing.JCheckBox chkPause;
    private javax.swing.JCheckBox chkPrintParameters;
    private javax.swing.JCheckBox chkPrintStackTrace;
    private javax.swing.JCheckBox chkRunScript;
    private javax.swing.JCheckBoxMenuItem chkShowMethodCode;
    private javax.swing.JCheckBox chkTamperParameters;
    private javax.swing.JCheckBox chkTamperReturnValue;
    private javax.swing.JPanel functionHookPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JLabel lblFilename;
    private javax.swing.JLabel lblSnoopingOrNot;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem mnuBrowseRemoteClasses;
    private javax.swing.JMenuItem mnuDecompileClass;
    private javax.swing.JMenuItem mnuGetProcessInfo;
    private javax.swing.JMenuItem mnuGotoHomePage;
    private javax.swing.JMenuItem mnuLoadConfiguration;
    private javax.swing.JCheckBoxMenuItem mnuLogDebug;
    private javax.swing.JCheckBoxMenuItem mnuLogError;
    private javax.swing.JCheckBoxMenuItem mnuLogFatal;
    private javax.swing.JCheckBoxMenuItem mnuLogInfo;
    private javax.swing.JCheckBoxMenuItem mnuLogOff;
    private javax.swing.JMenu mnuLogSetting;
    private javax.swing.JCheckBoxMenuItem mnuLogTrace;
    private javax.swing.JCheckBoxMenuItem mnuLogWarn;
    private javax.swing.JMenu mnuManageJad;
    private javax.swing.JMenuItem mnuManageJavaAppletSecuritySettings;
    private javax.swing.JMenuItem mnuSaveConfiguration;
    private javax.swing.JMenuItem mnuSaveConfigurationAs;
    private javax.swing.JMenuItem mnuSetJadPath;
    private javax.swing.JMenuItem mnuStartCanaryMode;
    private javax.swing.JMenuItem mnuViewFAQ;
    private javax.swing.JScrollPane pnlCode;
    private javax.swing.JScrollPane pnlConsole;
    private javax.swing.JPanel programSetupPanel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton rdoAlwaysHook;
    private javax.swing.JRadioButton rdoDontHookIf;
    private javax.swing.JRadioButton rdoHookIf;
    private javax.swing.JPanel reportingPanel;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTabbedPane tabConsoleCode;
    private javax.swing.JTable tblConditions;
    private javax.swing.JTable tblFunctionsHooked;
    private javax.swing.JTextField txtArguments;
    private javax.swing.JTextField txtClasspath;
    private javax.swing.JTextArea txtCode;
    private javax.swing.JTextPane txtConsole;
    private javax.swing.JTextField txtJavaArgs;
    private javax.swing.JTextField txtMainClass;
    private javax.swing.JTextField txtOutputFile;
    private javax.swing.JTextField txtWorkingDir;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;

    /*
     * Updates the form fields for the screen to match what's in the
     * currentSession object. This is used to keep them in sync when
     * they diverge.
     */
    public void updateSessionUI(boolean shouldOverwriteConsole) {

        txtMainClass.setText(currentSession.getMainClass());
        txtJavaArgs.setText(currentSession.getJavaArguments());
        txtArguments.setText(currentSession.getArguments());
        txtClasspath.setText(currentSession.getClasspathString());
        txtWorkingDir.setText(currentSession.getWorkingDir());

        if (currentSession.alreadyBeenSaved()) {
            mnuSaveConfiguration.setEnabled(true);
            lblFilename.setText("<html>" + currentSession.getSnoopSessionFilename() + "</html>");
        } else {
            mnuSaveConfiguration.setEnabled(false);
            lblFilename.setText("(not saved yet)");
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
            
            if ( hook.getReturnType().equalsIgnoreCase("void")) {
                chkTamperReturnValue.setEnabled(false);
            } else {
                chkTamperReturnValue.setEnabled(true);
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

    private void updateCurrentSession() {

        currentSession.setMainClass(txtMainClass.getText());
        currentSession.setClasspathString(txtClasspath.getText());
        currentSession.setArguments(txtArguments.getText());
        currentSession.setJavaArguments(txtJavaArgs.getText());
        currentSession.setWorkingDir(txtWorkingDir.getText());

        // function hooks are kept updated by the GUI already, no
        // need to update their state in the "currentSession"

    }

    private void setTableDimensions() {

        tblFunctionsHooked.getColumnModel().getColumn(0).setWidth(50);
        tblFunctionsHooked.getColumnModel().getColumn(0).setMaxWidth(50);
        tblFunctionsHooked.getColumnModel().getColumn(0).setMinWidth(50);
        tblFunctionsHooked.getColumnModel().getColumn(0).setResizable(false);

        tblFunctionsHooked.getColumnModel().getColumn(2).setWidth(65);
        tblFunctionsHooked.getColumnModel().getColumn(2).setMaxWidth(65);
        tblFunctionsHooked.getColumnModel().getColumn(2).setMinWidth(65);
        tblFunctionsHooked.getColumnModel().getColumn(2).setResizable(false);

        //tblFunctionsHooked.setIntercellSpacing(new Dimension(15,1));

        tblConditions.getColumnModel().getColumn(0).setWidth(50);
        tblConditions.getColumnModel().getColumn(0).setMaxWidth(50);
        tblConditions.getColumnModel().getColumn(0).setMinWidth(50);
        tblConditions.getColumnModel().getColumn(0).setResizable(false);

        tblConditions.getColumnModel().getColumn(1).setWidth(65);
        tblConditions.getColumnModel().getColumn(1).setMaxWidth(65);
        tblConditions.getColumnModel().getColumn(1).setMinWidth(65);
        tblConditions.getColumnModel().getColumn(1).setResizable(false);

        tblConditions.getColumnModel().getColumn(2).setWidth(70);
        tblConditions.getColumnModel().getColumn(2).setMaxWidth(70);
        tblConditions.getColumnModel().getColumn(2).setMinWidth(70);

        tblConditions.getColumnModel().getColumn(4).setMaxWidth(80);
        tblConditions.getColumnModel().getColumn(4).setMinWidth(80);
        tblConditions.getColumnModel().getColumn(4).setWidth(80);
        tblConditions.getColumnModel().getColumn(4).setResizable(false);
    }

    public void setStatus(String msg, Color color) {
        lblSnoopingOrNot.setText(msg);
        lblSnoopingOrNot.setForeground(color);
    }

    boolean isSnooping() {
        return client != null;
    }

    public void pause(String className, int hookId, Object[] parameters) {

        FunctionHook hook = getHookById(hookId);

        if (!hook.isEnabled() || !areConditionsMet(hook.getMode(), hook.getConditions(), parameters)) {
            return;
        }

        /*
         * Decide whether or not to show the code.
         */
        showCodeIfNeeded(className);
        
        PauseView view = new PauseView(getFrame(), false, className, hook.getMethodName());
        view.setVisible(true);

        UIUtil.waitForInput(view);

    }

    private String join(String[] types) {
        StringBuffer sb = new StringBuffer(100);
        for(int i=0;i<types.length;i++) {
            sb.append(ReflectionUtil.getSimpleClassName(types[i]));
            if ( i != types.length-1 ) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public String getTimeStamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        StringBuffer sb = new StringBuffer(100);
        sb.append("[");
        sb.append(dateFormat.format(date));
        sb.append("] ");
        return sb.toString();
    }

    public void printParameters(String className, int hookId, String[] types, Object[] parameters) {

        FunctionHook hook = getHookById(hookId);

        if ( hook == null ) {
            showConsoleErrorMessage("Didn't recognize hook " + hookId + " for class " + className);
        }

        if (!hook.isEnabled() || ! hook.shouldPrintParameters() || !areConditionsMet(hook.getMode(), hook.getConditions(), parameters)) {
            return;
        }

        StringBuffer sb = new StringBuffer();

        sb.append(getTimeStamp());
        sb.append("Print parameter request from: " + className + "." + hook.getMethodName() + "(" + join(types) + "): " + nl);

        for (int i = 0; i < parameters.length; i++) {
            Object o = parameters[i];
            sb.append("Parameter " + (i + 1) + " (type: " + ReflectionUtil.getSimpleClassName(types[i]) + "): " + String.valueOf(o) + nl);
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

    public void printStackTrace(String className, int hookId, String st, String[] types, Object[] parameters) {

        FunctionHook hook = getHookById(hookId);

        if (!hook.isEnabled() || ! hook.shouldPrintParameters() || !areConditionsMet(hook.getMode(), hook.getConditions(), parameters)) {
            return;
        }

        StringBuffer sb = new StringBuffer();
        sb.append(getTimeStamp());
        sb.append("Stack trace print request from: " + className + "." + hook.getMethodName() + "(" + join(types) + "):" + nl);
        sb.append( skipLines(st,2) + nl);

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

    public Object[] tamperWithParameters(String className, int hookId, Object[] parameters, String[] types, boolean isReturn) {

        List params = new ArrayList<Parameter>();

        FunctionHook hook = getHookById(hookId);

        if (!hook.isEnabled() || !areConditionsMet(hook.getMode(), hook.getConditions(), parameters)) {
            return parameters;
        }

        /*
         * Decide whether or not to show the code.
         */
        showCodeIfNeeded(className);

        String action = "Parameter";
        if ( isReturn ) {
            action = "Return value";
        }

        StringBuffer sb = new StringBuffer();
        sb.append(getTimeStamp());
        sb.append(action);
        sb.append(" tampering request from: " + className + "." + hook.getMethodName() + "(" + join(types) + ")" + nl);
        
        showSnoopMessage(sb.toString());

        for (int i = 0; i < parameters.length; i++) {
            params.add(new Parameter(i, parameters[i], types[i]));
        }

        ParameterTamperingView view = new ParameterTamperingView(
                getFrame(),
                false,
                className,
                hook.getMethodName(),
                params,
                isReturn);

        view.setVisible(true);

        while (view.isShowing()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                logger.error(ex);
            }
        }

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

        //System.out.println("Looking for hook: " + hookId);

        for (int i = 0; i < model.getRowCount(); i++) {
            FunctionHook hook = model.getHookFromRow(i);
            //System.out.println("Comparing to stored hook: " + hook.hashCode());
            if (hook.hashCode() == hookId) {
                return hook;
            }
        }

        //System.out.println("Compared " + model.getRowCount() + " hooks");

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
            logger.error(ex);
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
            logger.error(ex);
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

    private void disableSnoopUI() {

        setStatus("NOT SNOOPING", Color.red);

        btnStartSpy.setEnabled(true);
        btnAttachSpy.setEnabled(true);
        btnStopSpy.setEnabled(false);
        btnKillProgram.setEnabled(false);
        btnBrowseForMainClass.setEnabled(true);

        mnuBrowseRemoteClasses.setEnabled(false);
        mnuGetProcessInfo.setEnabled(false);
        mnuStartCanaryMode.setEnabled(false);
        mnuDecompileClass.setEnabled(false);

        txtMainClass.setEnabled(true);
        txtMainClass.setBackground(Color.white);
        txtJavaArgs.setEnabled(true);
        txtJavaArgs.setBackground(Color.white);
        txtArguments.setEnabled(true);
        txtArguments.setBackground(Color.white);
        txtWorkingDir.setEnabled(true);
        txtWorkingDir.setBackground(Color.white);
        txtClasspath.setEnabled(true);
        txtClasspath.setBackground(Color.white);

        client = null;
    }

    private void attachingUI(String msg) {

        setStatus(msg, Color.orange);

        btnStartSpy.setEnabled(false);
        btnAttachSpy.setEnabled(false);
        btnStopSpy.setEnabled(false);
        btnKillProgram.setEnabled(false);
        btnBrowseForMainClass.setEnabled(false);

        mnuBrowseRemoteClasses.setEnabled(false);
        mnuGetProcessInfo.setEnabled(false);
        mnuStartCanaryMode.setEnabled(false);
        mnuDecompileClass.setEnabled(false);

        txtMainClass.setEnabled(false);
        txtMainClass.setBackground(Color.orange);
        txtJavaArgs.setEnabled(false);
        txtJavaArgs.setBackground(Color.orange);
        txtArguments.setEnabled(false);
        txtArguments.setBackground(Color.orange);
        txtWorkingDir.setEnabled(false);
        txtWorkingDir.setBackground(Color.orange);
        txtClasspath.setEnabled(false);
        txtClasspath.setBackground(Color.orange);

    }

    private void enableSpyUI() {

        setStatus("SNOOPING", Color.green);

        btnStartSpy.setEnabled(false);
        btnAttachSpy.setEnabled(false);
        btnStopSpy.setEnabled(true);
        btnKillProgram.setEnabled(true);
        btnBrowseForMainClass.setEnabled(false);

        mnuBrowseRemoteClasses.setEnabled(true);
        mnuGetProcessInfo.setEnabled(true);
        mnuStartCanaryMode.setEnabled(true);
        mnuDecompileClass.setEnabled(true);

        txtMainClass.setBackground(Color.green);
        txtMainClass.setEnabled(false);
        txtJavaArgs.setBackground(Color.green);
        txtJavaArgs.setEnabled(false);
        txtArguments.setBackground(Color.green);
        txtArguments.setEnabled(false);
        txtWorkingDir.setBackground(Color.green);
        txtWorkingDir.setEnabled(false);
        txtClasspath.setBackground(Color.green);
        txtClasspath.setEnabled(false);
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

    @Action
    public void deleteHook() {

        FunctionHook hook = getCurrentHook();
        if (hook != null) {
            // user does have a hook selected, so we delete it!
            FunctionsHookedTableModel model = (FunctionsHookedTableModel) tblFunctionsHooked.getModel();
            model.removeHook(hook);
        }

    }

    @Action
    public void initializeSession() {

        currentSession = new SnoopSession();

        client = null;

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

    @Action
    public void getProcessInfo() {
        try {
            ProcessInfo info = client.getProcessInfo();

            showSnoopMessage("Process command: " + info.getCmd() + nl);
            showSnoopMessage("Classpath: " + info.getClasspath() + nl);

        } catch (AgentCommunicationException ex) {
            UIUtil.showErrorMessage(getFrame(), "Couldn't get process info from client: " + ex.getMessage());
        }

    }

    @Action
    public void browseRemoteClasses() {

        try {

            List<String> remoteClasses = client.getRemoteClasses();

            ChooseClassView view = new ChooseClassView(getFrame(), remoteClasses);
            view.setVisible(true);

            UIUtil.waitForInput(view);

        } catch (AgentCommunicationException ex) {
            UIUtil.showErrorMessage(getFrame(), "Couldn't get process info from client: " + ex.getMessage());
        }

    }

    public static File getConfigurationFile()
    {
    	return lastConfigurationFile;
    }

    @Action
    public void newSession() {

        initializeSession();
        
    }
    
    public void sendAgentNewRules() {

        try {
            
            startProgressBar();

            if ( isSnooping() ) {

                statusMessageLabel.setText("Sending agent new rules...");

                client.sendSnoopSession(currentSession);
                client.startSnooping();

                statusMessageLabel.setText("Sent agent updated rules at " + getHumanTime() );
            }
            
        } catch (AgentCommunicationException ex) {
            UIUtil.showErrorMessage(getFrame(), "Failure sending agent new rules: " + ex.getMessage());
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

    @Action
    public void manageJavaSecuritySettings() {

        String javaHome = System.getProperty("java.home");

        String policyPath = javaHome + File.separator +
                            "lib" + File.separator +
                            "security" + File.separator +
                            "java.policy";

        File policyFile = new File(policyPath);

        EditJavaPolicyView view = null;

        try {
            view = new EditJavaPolicyView(getFrame(), true, policyFile);
        } catch (IOException ioe) {
            UIUtil.showErrorMessage(getFrame(), "Couldn't find policy file, which should be at " + policyPath + ". The error encountered: " + ioe.getMessage());
            return;
        }

        view.setVisible(true);

        UIUtil.waitForInput(view);

        if ( view.userAcceptedChanges() ) {
            String text = view.getPolicy();
            try {
                FileOutputStream fos = new FileOutputStream(policyFile);
                fos.write(text.getBytes());
            } catch (IOException ioe) {
                UIUtil.showErrorMessage(getFrame(), "Could not overwrite Java security settings: " + ioe.getMessage() + nl + "You may have to copy the policy text into the policy file yourself since JavaSnoop doesn't appear to have the privileges.");
                manageJavaSecuritySettings();
            }

        }
        
    }

    @Action
    public void enterCanaryMode() {
        
        canaryView = new StartCanaryModeView(client, getFrame(), true);
        
        canaryView.setVisible(true);
        
        UIUtil.waitForInput(getCanaryView());

        try {
            
            canaryView = null;
            
            client.sendSnoopSession(currentSession);
            statusMessageLabel.setText("Sent agent updated rules at " + getHumanTime() );
        } catch (AgentCommunicationException ex) {
            UIUtil.showErrorMessage(getFrame(), "Failure sending agent new rules: " + ex.getMessage());
        }

    }

    /**
     * @return the canaryView
     */
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

    private void showCodeIfNeeded(String className) {
        boolean useJad = chkShowMethodCode.isSelected();
        if (useJad) {
            SnoopClassLoader cl = JavaSnoop.getClassLoader();
            byte[] bytes = cl.loadClassData(className);
            try {

                String javaCode = JadUtil.getDecompiledJava(className,bytes);
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
        StringBuffer sb = new StringBuffer();
        
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

    @Action
    public void changeLogLevel(ActionEvent e) {

        String action = e.getActionCommand();

        for(JCheckBoxMenuItem item : mnuLogLevels) {
            if ( ! item.getText().equals(action) ) {
                item.setSelected(false);
                item.setState(false);
            }
        }

        Logger.getRootLogger().setLevel(Level.toLevel(action));
    }

    @Action
    public void changeJadPath() {
        
        String oldPath = JavaSnoop.getProperty(JavaSnoop.JAD_PATH);

        if ( oldPath == null ) {
            oldPath = "";
        }

        String newPath = JOptionPane.showInputDialog(getFrame(),"Enter the path to the jad executable (leave blank if it's on the PATH)",oldPath);

        if ( newPath != null ) {
            JavaSnoop.setProperty(JavaSnoop.JAD_PATH,newPath);
            JavaSnoop.saveProperties();
        }
    }

    @Action
    public void decompileClass() {

        if ( JadUtil.getJadLocation() == null ) {
            UIUtil.showErrorMessage(getFrame(), "Could not locate Jad (not on the path and not setup)");
            return;
        } else if ( client == null ) {
            UIUtil.showErrorMessage(getFrame(), "Please snoop a process before decompiling a class");
            return;
        }

        try {
            
            List<String> remoteClasses = client.getRemoteClasses();

            ChooseClassView view = new ChooseClassView(getFrame(), remoteClasses);
            view.setVisible(true);

            UIUtil.waitForInput(view);

            String cn = view.getClassName();

            if (cn != null) {
                try {
                    String java = JadUtil.getDecompiledJava(cn, JavaSnoop.getClassLoader().loadClassData(cn));
                    fillInCode(java);
                } catch(IOException ioe) { }
            }

        } catch (AgentCommunicationException ex) {
            UIUtil.showErrorMessage(getFrame(), "Couldn't get process info from client: " + ex.getMessage());
        }

    }

    @Action
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
        int rc = fc.showOpenDialog(getFrame());

        if (rc == JFileChooser.APPROVE_OPTION) {
            File of = fc.getSelectedFile();
            txtOutputFile.setText(of.getAbsolutePath());
        }
    }

    @Action
    public void browseToHomePage() {
        try {
            Desktop.getDesktop().browse(URI.create(homeUrl));
        } catch (IOException ex) {
            showConsoleErrorMessage("Couldn't browse to FAQ page: " + ex.getMessage());
        }
    }

}
