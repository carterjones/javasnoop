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

public class SnoopAgent {

    private static AgentServerThread server = null;

    public static void premain(String args, Instrumentation inst) {
        
        System.out.println("Starting up JavaSnoop server... (args="+args+")");
        final String fArgs = args;
        final Instrumentation fInst = inst;

        server = new AgentServerThread(fInst, fArgs);
        server.setDaemon(true);
        server.start();
    }

    public static void agentmain(String args, Instrumentation inst) {

        System.out.println("Starting up JavaSnoop server... (args="+args+")");
        if ( server == null ) {
            final String fArgs = args;
            final Instrumentation fInst = inst;

            server = new AgentServerThread(fInst, fArgs);
            server.setDaemon(true);
            server.start();
        }
    }

}
