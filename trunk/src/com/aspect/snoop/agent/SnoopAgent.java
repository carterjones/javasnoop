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

package com.aspect.snoop.agent;

import com.aspect.snoop.agent.manager.InstrumentationManager;
import com.aspect.snoop.ui.JavaSnoopView;
import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.Properties;
import java.util.jar.JarFile;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class SnoopAgent {

    private static Instrumentation inst;
    private static InstrumentationManager manager;

    private static JavaSnoopView mainView;
    
    public static JavaSnoopView getMainView() {
        return mainView;
    }

    public static void premain(String args, Instrumentation instrumentation) {
        install(args,instrumentation);
    }

    public static void agentmain(String args, Instrumentation instrumentation) {
        turnOffSecurity();
        install(args,instrumentation);
    }

    public static void install(String args, Instrumentation instrumentation) {
        inst = instrumentation;
        manager = new InstrumentationManager(inst);
        turnOffSecurity();

        /*
         * We need to add some of the libraries to be eventually
         * loaded by the system class loader instead of the
         * boostrap class loader, which is represented as null in
         * Java land. This causes problems.
         */
        String javaSnoopDir = args;
        String libDir = new File(javaSnoopDir).getAbsolutePath() + File.separator + "lib" + File.separator;

        try {
            inst.appendToSystemClassLoaderSearch(new JarFile(libDir + "rsyntaxtextarea.jar"));
            inst.appendToSystemClassLoaderSearch(new JarFile(libDir + "swing-worker-1.1.jar"));
            inst.appendToSystemClassLoaderSearch(new JarFile(libDir + "appframework-1.0.3.jar"));
            inst.appendToSystemClassLoaderSearch(new JarFile(libDir + "bsh-2.0b4.jar"));
            inst.appendToSystemClassLoaderSearch(new JarFile(libDir + "jython.jar"));
            inst.appendToSystemClassLoaderSearch(new JarFile(libDir + "xom-1.1.jar"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                        if ("Nimbus".equals(info.getName())) {
                            UIManager.setLookAndFeel(info.getClassName());
                            break;
                        }
                    }
                } catch (Throwable t){}
                JFrame.setDefaultLookAndFeelDecorated(true);
                JavaSnoopView view = new JavaSnoopView(manager);
                mainView = view;
                view.setVisible(true);
            }
        });
    }

    private static void turnOffSecurity() {
        /*
         * Test if we're inside an applet. We should be inside
         * an applet if the System property ("package.restrict.access.sun")
         * is not null and is set to true.
         */

        boolean restricted = System.getProperty("package.restrict.access.sun") != null;

        /*
         * If we're in an applet, we need to change the System properties so
         * as to avoid class restrictions. We go through the current properties
         * and remove anything related to package restriction.
         */
        if ( restricted ) {

            Properties newProps = new Properties();

            Properties sysProps = System.getProperties();

            for(String prop : sysProps.stringPropertyNames()) {
                if ( prop != null && ! prop.startsWith("package.restrict.") ) {
                    newProps.setProperty(prop,sysProps.getProperty(prop));
                }
            }

            System.setProperties(newProps);
        }

        /*
         * Should be the final nail in (your) coffin.
         */
        System.setSecurityManager(null);
    }

    public static Instrumentation getInstrumentation() {
        return inst;
    }

    public static InstrumentationManager getAgentManager() {
        return manager;
    }
}