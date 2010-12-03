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

import java.lang.instrument.Instrumentation;
import java.util.Properties;

public class SnoopAgent {

    private static AgentServerThread server = null;

    public static void premain(String args, Instrumentation inst) {

        final String fArgs = args;
        final Instrumentation fInst = inst;

        turnOffSecurity();

        server = new AgentServerThread(fInst, fArgs);
        server.setDaemon(true);
        server.start();
    }

    public static void agentmain(String args, Instrumentation inst) {

        if ( server == null ) {
            final String fArgs = args;
            final Instrumentation fInst = inst;

            turnOffSecurity();

            server = new AgentServerThread(fInst, fArgs);
            server.setDaemon(true);
            server.start();
        }
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
}