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

package com.aspect.snoop.util;

import com.aspect.snoop.JavaSnoop;
import com.aspect.snoop.agent.AgentCommunicationException;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.aspect.snoop.SnoopSession;
import com.aspect.snoop.agent.AgentJarCreator;
import com.aspect.snoop.agent.SnoopToAgentClient;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Utility methods for launching JVMs and using the Attach API.
 * 
 * @author David Anderson
 */
public class AttachUtil {

    private static Logger logger = Logger.getLogger(AttachUtil.class);

    public static SnoopToAgentClient attachToVM() throws AttachNotSupportedException, IOException, AgentLoadException, AgentInitializationException, AgentCommunicationException {
        // Use the process id of this VM
        return attachToVM(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    }

    public static SnoopToAgentClient attachToVM(String pid) throws AttachNotSupportedException, IOException, AgentLoadException, AgentInitializationException, AgentCommunicationException {

        VirtualMachine vm = VirtualMachine.attach(pid);
        String agentJarPath = AgentJarCreator.createAgentJar(false);

        logger.debug("Loading agent from " + agentJarPath);
        
        int agentServerPort = getAnyAvailablePort();
        int ourServerPort = getAnyAvailablePort();

        vm.loadAgent(agentJarPath,  agentServerPort + "," + ourServerPort + ","+agentJarPath);
        vm.detach();
        
        return getClient("localhost", agentServerPort, ourServerPort, vm); //default port
    }

    public static SnoopToAgentClient launchInThisVM(String mainClass) throws ClassNotFoundException, NoSuchMethodException, AttachNotSupportedException, IOException, AgentLoadException, AgentInitializationException, AgentCommunicationException {

        Class targetClass = Class.forName(mainClass);
        Class[] argTypes = {new String[0].getClass()};
        Method m = targetClass.getMethod("main", argTypes);
        Object[] targetArgs = {new String[0]};    // We will want to pass this as a parameter to this method

        logger.debug("Invoking target method: " + m);

        try {
            m.invoke(null, targetArgs);
        } catch (IllegalAccessException ex) {
            logger.error(ex);
        } catch (IllegalArgumentException ex) {
            logger.error(ex);
        } catch (InvocationTargetException ex) {
            logger.error(ex);
        }

        return attachToVM();
    }

    public static SnoopToAgentClient launchInNewVM(SnoopSession session) throws AttachNotSupportedException, ClassNotFoundException, NoSuchMethodException, IOException, AttachNotSupportedException,AgentCommunicationException {

        String agentJarPath = AgentJarCreator.createAgentJar(true);
        boolean isJar = session.getMainClass().trim().length() == 0 &&
                        session.getClasspathString().trim().length() > 0 &&
                        session.getClasspathString().trim().endsWith(".jar");

        String javaHome = System.getProperty("java.home") + File.separator + "bin";
        List<String> arguments = new ArrayList<String>();

        String command = javaHome + File.separator + "java";

        arguments.add(command);

        String agent = "-javaagent:" + agentJarPath;
        
        int agentServerPort = getAnyAvailablePort();
        int ourServerPort = getAnyAvailablePort();

        agent += "=" + agentServerPort + "," + ourServerPort + "," + agentJarPath;

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
            sb.append(s + " ");
        }
        
        String workingDir = new File(".").getPath();

        if ( session.getWorkingDir().trim().length() > 0 ) {
            workingDir = session.getWorkingDir().trim();
        }

        Runtime.getRuntime().exec(commandArgs, null, new File(workingDir));
        
        SnoopToAgentClient client = getClient("localhost", agentServerPort, ourServerPort, null);

        String pid = client.queryPid();
        VirtualMachine vm = VirtualMachine.attach(pid);

        client.setVirtualMachine(vm);

        vm.detach();
        
        return client;
    }

    /*
    public static void testSnoopToAgentComms() {

        // DEBUG: This method is just for testing snoop-to-agent comms
        final SnoopToAgentClient client = new SnoopToAgentClient("localhost");
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {

            protected String doInBackground() {
                String pid = client.queryPid();
                return pid;
            }
        };
        String agentPid = null;
        try {
            agentPid = worker.get();
        } catch (InterruptedException ex) {
            Logger.getLogger(JavaSnoopView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(JavaSnoopView.class.getName()).log(Level.SEVERE, null, ex);
        }

        Logger.getLogger(SnoopServerThread.class.getName()).log(Level.SEVERE, "PID of agent: " + agentPid);
    }
    */

    private static SnoopToAgentClient getClient(String host, int agentServerPort, int ourServerPort, VirtualMachine vm) throws AgentCommunicationException {
        
        int delay = JavaSnoop.getIntProperty(JavaSnoop.LOAD_WAIT);
        
        if ( delay == 0 ) {
            // initialize to default value
            delay = 3000; 
        }

        SnoopToAgentClient client = new SnoopToAgentClient(host, agentServerPort, ourServerPort, vm);
        
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ex) { }

        boolean b = client.isAgentAlive();
        if ( b ) {
            return client;
        }

        // server did not start up in 10 seconds - fail
        return null;
    }

    private static int getAnyAvailablePort() throws IOException {
        int portToUse;

        ServerSocket testSocket = new ServerSocket(0);
        portToUse = testSocket.getLocalPort();
        testSocket.close();

        // allow the OS to reclaim the socket
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) { }

        return portToUse;
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
