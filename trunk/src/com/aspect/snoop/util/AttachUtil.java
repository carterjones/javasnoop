package com.aspect.snoop.util;

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

import com.aspect.snoop.SnoopSession;
import com.aspect.snoop.agent.AgentCommunicationException;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import com.aspect.snoop.agent.AgentJarCreator;
import com.aspect.snoop.agent.AgentLogger;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility methods for launching JVMs and using the Attach API.
 * 
 * @author David Anderson
 */
public class AttachUtil {

    public static void attachToVM() throws AttachNotSupportedException, IOException, AgentLoadException, AgentInitializationException, AgentCommunicationException {
        // Use the process id of this VM
        String agentJarPath = AgentJarCreator.createAgentJar(false);
        loadAgentInOtherVM(agentJarPath, ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    }

    public static void loadAgentInOtherVM(String agentJarPath, String pid) throws AttachNotSupportedException, IOException, AgentLoadException, AgentInitializationException, AgentCommunicationException {

        VirtualMachine vm = VirtualMachine.attach(pid);
        
        AgentLogger.debug("Loading agent from " + agentJarPath);
        
        vm.loadAgent(agentJarPath, new File(".").getAbsolutePath());
        vm.detach();
    }

    public static void launchInNewVM(String agentJarPath, SnoopSession session) throws AttachNotSupportedException, ClassNotFoundException, NoSuchMethodException, IOException, AttachNotSupportedException,AgentCommunicationException {

        boolean isJar = session.getMainClass().trim().length() == 0 &&
                        session.getClasspathString().trim().length() > 0 &&
                        session.getClasspathString().trim().endsWith(".jar");

        String javaHome = System.getProperty("java.home") + File.separator + "bin";
        List<String> arguments = new ArrayList<String>();

        String command = javaHome + File.separator + "java";

        arguments.add(command);

        String agent = "-javaagent:" + agentJarPath;
        
        arguments.add(agent);

        String javaArgs = session.getJavaArguments().trim();
        
        if ( javaArgs.length() > 0) {
            String[] args = parseArguments(javaArgs);
            arguments.addAll(Arrays.asList(args));
        }

        String cp = session.getClasspathString();

        if ( cp.trim().length() >0) {
            if ( isJar ) {
                arguments.add("-jar");
            } else {
                arguments.add("-cp");
            }
            arguments.add(cp);
        }

        if ( ! isJar ) {
            arguments.add(session.getMainClass());
        }
        
        if (session.getArguments().length()>0) {
            String[] args = parseArguments(session.getArguments());
            arguments.addAll(Arrays.asList(args));
        }

        String[] commandArgs = arguments.toArray( new String[]{} );

        StringBuilder sb = new StringBuilder();
        for(String s : commandArgs) {
            sb.append(s).append(" ");
        }
        
        String workingDir = new File(".").getPath();

        if ( session.getWorkingDir().trim().length() > 0 ) {
            workingDir = session.getWorkingDir().trim();
        }

        Runtime.getRuntime().exec(commandArgs, null, new File(workingDir));
    }
    
    private static String[] parseArguments(String args) {

        List<String> arguments = new ArrayList<String>();

        boolean quoted = false;
        String currentArg = "";

        for (int i = 0; i < args.length(); i++) {
            char c = args.charAt(i);
            if (!quoted && c == ' ') {
                arguments.add(currentArg);
                currentArg = "";
            } else if (quoted && c == '"') {
                arguments.add(currentArg);
                currentArg = "";
                quoted = false;
                i++; // skip over the space
            } else if (c == '"') {
                quoted = true;
            } else {
                currentArg += c;
            }
            if ( i == args.length()-1 ) {
                arguments.add(currentArg);
            }
        }

        String[] toReturn = new String[arguments.size()];

        for (int i = 0; i < arguments.size(); i++) {
            toReturn[i] = arguments.get(i);
        }

        return toReturn;
    }
}
