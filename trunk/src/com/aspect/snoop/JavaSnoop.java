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
package com.aspect.snoop;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class JavaSnoop extends SingleFrameApplication {

    public static final String VERSION = "version";
    public static final String SEPARATE_VM = "exec_separate_vm";
    public static final String LAST_SELECTED_DIR = "last_classpath_dir";
    public static final String USE_JAD = "use_jad";
    public static final String JAD_PATH = "jad_path";
    public static final String LOAD_WAIT = "load_wait";
    public static final String LAST_DUMPED_DIR = "last_dumped_dir";

    private static Properties props;
    private static String propFile;


    private StartupView mainForm;

    static {
        props = new Properties();
        propFile = System.getProperty("user.home") + File.separator + "JavaSnoop.properties";

        try {
            if (!new File(propFile).exists()) {
                initializePropertiesFile(propFile);
            }
            props.load(new FileReader(propFile));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        mainForm = new StartupView(this);
        show(mainForm);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of JavaSnoop
     */
    public static JavaSnoop getApplication() {
        return Application.getInstance(JavaSnoop.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(JavaSnoop.class, args);
    }

    public static Properties getProperties() {
        return props;
    }

    public static String getProperty(String key) {
        return props.getProperty(key);
    }

    public static int getIntProperty(String key) {
        try {
            return Integer.parseInt(props.getProperty(key));
        } catch(NumberFormatException nfe) {
            return 0;
        }
    }

    public static boolean getBooleanProperty(String key, boolean def) {
        String val = props.getProperty(key);
        return val != null ? val.equalsIgnoreCase("true") : def;
    }

    public static void saveProperties() {
        try {
            props.store(new FileWriter(new File(propFile)), null);
        } catch (Exception e) {
            e.printStackTrace();
            // can't use log4j or the AgentLogger here because this class is
            // used in both the agent/GUI and the startup screen
        }
    }

    private static void initializePropertiesFile(String propFile) throws IOException {
        getDefaultProperties().store(new FileWriter(new File(propFile)), null);
    }

    private static Properties getDefaultProperties() {
        Properties p = new Properties();
        p.setProperty(VERSION, "1.0");
        p.setProperty(SEPARATE_VM, "true");
        p.setProperty(LOAD_WAIT, "3000");
        p.setProperty(USE_JAD,"false");
        p.setProperty(LAST_SELECTED_DIR,new File(System.getProperty("user.home")).getAbsolutePath());
        return p;
    }

    public static void setProperty(String key, String val) {
        props.put(key, val);
    }
}
