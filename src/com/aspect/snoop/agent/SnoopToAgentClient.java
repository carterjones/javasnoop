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

import com.aspect.snoop.JavaSnoop;
import com.aspect.snoop.SnoopSession;
import com.aspect.snoop.messages.agent.ClassBytes;
import com.aspect.snoop.messages.agent.ExecuteScriptRequest;
import com.aspect.snoop.messages.agent.ExecuteScriptResponse;
import com.aspect.snoop.messages.agent.ExitProgramRequest;
import com.aspect.snoop.messages.agent.ExitProgramResponse;
import com.aspect.snoop.messages.agent.GetClassDefinitionsRequest;
import com.aspect.snoop.messages.agent.GetClassDefinitionsResponse;
import com.aspect.snoop.messages.agent.GetClassesRequest;
import com.aspect.snoop.messages.agent.GetClassesResponse;
import com.aspect.snoop.messages.agent.GetProcessInfoRequest;
import com.aspect.snoop.messages.agent.GetProcessInfoResponse;
import com.aspect.snoop.messages.agent.LoadClassesRequest;
import com.aspect.snoop.messages.agent.LoadClassesResponse;
import com.aspect.snoop.messages.agent.QueryPidRequest;
import com.aspect.snoop.messages.agent.QueryPidResponse;
import com.aspect.snoop.messages.agent.StartCanaryRequest;
import com.aspect.snoop.messages.agent.StartCanaryResponse;
import com.aspect.snoop.messages.agent.StartSnoopingRequest;
import com.aspect.snoop.messages.agent.StartSnoopingResponse;
import com.aspect.snoop.messages.agent.StopCanaryRequest;
import com.aspect.snoop.messages.agent.StopCanaryResponse;
import com.aspect.snoop.messages.agent.StopSnoopingRequest;
import com.aspect.snoop.messages.agent.StopSnoopingResponse;
import com.aspect.snoop.messages.agent.ToggleDebugRequest;
import com.aspect.snoop.messages.agent.ToggleDebugResponse;
import com.aspect.snoop.messages.agent.TransportSessionRequest;
import com.aspect.snoop.messages.agent.TransportSessionRequest.SessionRetrievalType;
import com.aspect.snoop.messages.agent.TransportSessionResponse;
import com.sun.tools.attach.VirtualMachine;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class SnoopToAgentClient {

    String host;
    int agentPort;
    int ourPort;
    VirtualMachine vm;
    String pid;

    SnoopServerThread server;

    public SnoopToAgentClient(String host, int agentPort, int ourPort, VirtualMachine vm) {
        this.host = host;
        this.agentPort = agentPort;
        this.ourPort = ourPort;
        this.vm = vm;
        this.pid = null;

        // try to start the server on our port so we can listen for
        // tampering requests, pause requests, etc.

        server = new SnoopServerThread(ourPort);
        server.setDaemon(true);
        server.start();

    }

    public void setAgentPort(int agentPort) {
        this.agentPort = agentPort;
    }

    public int getAgentPort() {
        return agentPort;
    }

    public void setHost(String host) {
        this.host = host;
    }

    private Socket getConnection() throws UnknownHostException, IOException {
        Socket s = new Socket(host, agentPort);
        s.setKeepAlive(true);
        return s;
    }

    private void closeConnection(Socket s) {
        try {
            s.close();
        } catch (Exception ex) { }
    }

    public boolean isAgentAlive() throws AgentCommunicationException {
        
        return queryPid() != null;
        
    }

    /**
     * This method tells the agent to de-instrument any classes it has instrumented.
     * @throws AgentCommunicationException if there is a communication error
     */
    public void stopSnooping() throws AgentCommunicationException {

        Socket socket = null;
        
        try {

            socket = getConnection();

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            StopSnoopingRequest request = new StopSnoopingRequest();

            output.writeObject(request);

            StopSnoopingResponse response = (StopSnoopingResponse)input.readObject();

            if ( ! response.wasSuccessful() ) {
                throw new AgentCommunicationException("Couldn't tell agent to stop: " + response.getMessage());
            }

            server.endServer();

        } catch (ClassNotFoundException ex) {
            throw new AgentCommunicationException(ex);
        } catch (UnknownHostException uhe) {
            throw new AgentCommunicationException(uhe);
        } catch (IOException ioe) {
            throw new AgentCommunicationException(ioe);
        } finally {
            closeConnection(socket);
        }
    }

    /**
     * This method tells the target program to apply the SnoopSession hooks
     * @param session The SnoopSession with the hooks to instrument
     * @throws AgentCommunicationException if there is a communication error
     */
    public void startSnooping() throws AgentCommunicationException {

        Socket socket = null;

        try {

            socket = getConnection();

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            StartSnoopingRequest startRequest = new StartSnoopingRequest();
            output.writeObject(startRequest);

            StartSnoopingResponse startResponse = (StartSnoopingResponse)input.readObject();

            if ( ! startResponse.wasSuccessful() ) {
                throw new AgentCommunicationException("Couldn't send agent rules: " + startResponse.getMessage());
            }

        } catch (ClassNotFoundException cnfe) {
            throw new AgentCommunicationException(cnfe);
        } catch (UnknownHostException uhe) {
            throw new AgentCommunicationException(uhe);
        } catch (IOException ioe) {
            throw new AgentCommunicationException(ioe);
        } finally {
            closeConnection(socket);
        }

    }

     /**
     * This method lets the agent get updated class information.
     * @throws AgentCommunicationException if an error occurs communicating with the agent
     */
    public void importRemoteClasses() throws AgentCommunicationException {

        Socket socket = null;

        try {

            socket = getConnection();

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            // step #1: get all the remote classes
            GetClassesRequest classesRequest = new GetClassesRequest();
            output.writeObject(classesRequest);
            
            GetClassesResponse classesResponse = (GetClassesResponse)input.readObject();

            closeConnection(socket);

            List<String> allRemoteClasses = classesResponse.getClasses();

            // step #2: save a list of the classes that are not in our current classpath
            List<String> unknownClasses = new  ArrayList<String>();

            for ( String s : allRemoteClasses ) {
                try {
                    Class.forName(s, true, JavaSnoop.getClassLoader());

                /*
                 * Catching all of these in case we need to handle
                 * them differently someday.
                 */
                } catch (ClassNotFoundException cnfe) {
                    unknownClasses.add(s);
                } catch (NoClassDefFoundError ncdfe) {
                    unknownClasses.add(s);
                } catch (ExceptionInInitializerError e) {
                    unknownClasses.add(s);
                } catch (Exception e) {
                    unknownClasses.add(s);
                } catch (Throwable t) {
                    unknownClasses.add(s);
                }
            }

            // step #3: send the list of unknown classes to the agent

            socket = getConnection();

            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            
            GetClassDefinitionsRequest request = new GetClassDefinitionsRequest();
            request.setClasses(unknownClasses.toArray(new String[]{}));
            output.writeObject(request);

            GetClassDefinitionsResponse response = (GetClassDefinitionsResponse)input.readObject();

            List<ClassBytes> newClasses = response.getClasses();
            
            for ( ClassBytes classDef : newClasses ) {
                String clsName = classDef.getClassName();
                byte[] clsBytes = classDef.getClassBytes();
                JavaSnoop.getClassLoader().setClass(clsName, clsBytes);
            }

            closeConnection(socket);

            if ( ! response.wasSuccessful() ) {
                throw new AgentCommunicationException("Couldn't import remote classes from agent: " + response.getMessage());
            }

        } catch (ClassNotFoundException cnfe) {
            throw new AgentCommunicationException(cnfe);
        } catch (UnknownHostException uhe) {
            throw new AgentCommunicationException(uhe);
        } catch (IOException ioe) {
            throw new AgentCommunicationException(ioe);
        } catch (Exception e) {
            throw new AgentCommunicationException(e);
        } finally {
            closeConnection(socket);
        }

    }

    public void sendSnoopSession(SnoopSession session) throws AgentCommunicationException {

        Socket socket = null;

        try {

            socket = getConnection();

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            TransportSessionRequest request = new TransportSessionRequest(SessionRetrievalType.LoadFromObject);
            request.setSnoopSession(session);

            output.writeObject(request);

            TransportSessionResponse response = (TransportSessionResponse)input.readObject();

            if ( ! response.wasSuccessful() ) {
                throw new AgentCommunicationException("Couldn't send agent rules: " + response.getMessage());
            }

        } catch (ClassNotFoundException cnfe) {
            throw new AgentCommunicationException(cnfe);
        } catch (UnknownHostException uhe) {
            throw new AgentCommunicationException(uhe);
        } catch (IOException ioe) {
            throw new AgentCommunicationException(ioe);
        } finally {
            closeConnection(socket);
        }

    }

    public List<String> getRemoteClasses() throws AgentCommunicationException {

        Socket socket = null;

        try {

            socket = getConnection();

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            // Send our command

            GetClassesRequest request = new GetClassesRequest();
            output.writeObject(request);

            GetClassesResponse response = (GetClassesResponse)input.readObject();

            return response.getClasses();

        } catch (ClassNotFoundException ex) {
            throw new AgentCommunicationException(ex);
        } catch (UnknownHostException ex) {
            throw new AgentCommunicationException(ex);
        } catch (IOException ex) {
            throw new AgentCommunicationException(ex);
        } finally {
            closeConnection(socket);
        }

    }

    public ProcessInfo getProcessInfo() throws AgentCommunicationException {
        
        Socket socket = null;

        try {

            socket = getConnection();

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            // Send our command

            GetProcessInfoRequest request = new GetProcessInfoRequest();
            output.writeObject(request);

            GetProcessInfoResponse response = (GetProcessInfoResponse)input.readObject();

            return response.getProcessInfo();

        } catch (ClassNotFoundException ex) {
            throw new AgentCommunicationException(ex);
        } catch (UnknownHostException ex) {
            throw new AgentCommunicationException(ex);
        } catch (IOException ex) {
            throw new AgentCommunicationException(ex);
        } finally {
            closeConnection(socket);
        }
        
    }

    public String getPid() {
        return pid;
    }

    public String queryPid() throws AgentCommunicationException {

        Socket socket = null;

        try {

            socket = getConnection();

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            // Send our command

            QueryPidRequest request = new QueryPidRequest();
            output.writeObject(request);

            QueryPidResponse response = (QueryPidResponse)input.readObject();

            pid = response.getPid();

        } catch (ClassNotFoundException ex) {
            throw new AgentCommunicationException(ex);
        } catch (UnknownHostException ex) {
            throw new AgentCommunicationException(ex);
        } catch (IOException ex) {
            throw new AgentCommunicationException(ex);
        } finally {
            closeConnection(socket);
        }

        return pid;
    }

    public void killTargetProgram() throws AgentCommunicationException {

        Socket socket = null;

        try {

            socket = getConnection();

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            ExitProgramRequest request = new ExitProgramRequest();

            output.writeObject(request);

            ExitProgramResponse response = (ExitProgramResponse)input.readObject();

            if ( ! response.wasSuccessful() ) {
                throw new AgentCommunicationException("Couldn't send agent rules: " + response.getMessage());
            }

        } catch (ClassNotFoundException ex) {
            throw new AgentCommunicationException(ex);
        } catch (UnknownHostException ex) {
            throw new AgentCommunicationException(ex);
        } catch (IOException ex) {
            throw new AgentCommunicationException(ex);
        } finally {
            closeConnection(socket);
        }
    }

    public void setVirtualMachine(VirtualMachine vm) {
        this.vm = vm;
    }

    public void stopServer() {
        server.endServer();
    }

    public void beginCanaryMode(String text, String type, String pkg) throws AgentCommunicationException {

        Socket socket = null;

        try {

            socket = getConnection();

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            StartCanaryRequest request = new StartCanaryRequest();

            request.setCanary(text);
            request.setType(type);
            request.setPackage(pkg);
            
            output.writeObject(request);

            StartCanaryResponse response = (StartCanaryResponse)input.readObject();

            if ( ! response.wasSuccessful() ) {
                throw new AgentCommunicationException("Couldn't start Canary Mode: " + response.getMessage());
            }

        } catch (ClassNotFoundException ex) {
            throw new AgentCommunicationException(ex);
        } catch (UnknownHostException ex) {
            throw new AgentCommunicationException(ex);
        } catch (IOException ex) {
            throw new AgentCommunicationException(ex);
        } finally {
            closeConnection(socket);
        }
    }

    public void endCanaryMode() throws AgentCommunicationException {
        
        Socket socket = null;

        try {

            socket = getConnection();

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            StopCanaryRequest request = new StopCanaryRequest();

            output.writeObject(request);

            StopCanaryResponse response = (StopCanaryResponse)input.readObject();

            if ( ! response.wasSuccessful() ) {
                throw new AgentCommunicationException("Couldn't stop Canary Mode: " + response.getMessage());
            }

        } catch (ClassNotFoundException ex) {
            throw new AgentCommunicationException(ex);
        } catch (UnknownHostException ex) {
            throw new AgentCommunicationException(ex);
        } catch (IOException ex) {
            throw new AgentCommunicationException(ex);
        } finally {
            closeConnection(socket);
        }
    }

    public List<String> forceLoadClasses(List<String> classesToLoad) throws AgentCommunicationException {
        
        Socket socket = null;

        try {

            socket = getConnection();

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            // Send our command

            LoadClassesRequest request = new LoadClassesRequest();
            request.setClassesToLoad(classesToLoad);
            output.writeObject(request);

            LoadClassesResponse response = (LoadClassesResponse)input.readObject();

            return response.getFailedClasses();

        } catch (ClassNotFoundException ex) {
            throw new AgentCommunicationException(ex);
        } catch (UnknownHostException ex) {
            throw new AgentCommunicationException(ex);
        } catch (IOException ex) {
            throw new AgentCommunicationException(ex);
        } finally {
            closeConnection(socket);
        }
    }

    public void toggleDebug(int level) throws AgentCommunicationException {

        Socket socket = null;

        try {

            socket = getConnection();

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            // Send our command
            ToggleDebugRequest request = new ToggleDebugRequest();
            request.setLevel(level);
            output.writeObject(request);

            ToggleDebugResponse response = (ToggleDebugResponse)input.readObject();

        } catch (ClassNotFoundException ex) {
            throw new AgentCommunicationException(ex);
        } catch (UnknownHostException ex) {
            throw new AgentCommunicationException(ex);
        } catch (IOException ex) {
            throw new AgentCommunicationException(ex);
        } finally {
            closeConnection(socket);
        }
    }

    public ExecuteScriptResponse executeScript(String lang, String code) throws AgentCommunicationException {

        Socket socket = null;

        try {

            socket = getConnection();

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            // Send our command

            ExecuteScriptRequest request = new ExecuteScriptRequest();
            request.setLanguage(lang);
            request.setScript(code);

            output.writeObject(request);

            ExecuteScriptResponse response = (ExecuteScriptResponse)input.readObject();

            return response;
            
        } catch (ClassNotFoundException ex) {
            throw new AgentCommunicationException(ex);
        } catch (UnknownHostException ex) {
            throw new AgentCommunicationException(ex);
        } catch (IOException ex) {
            throw new AgentCommunicationException(ex);
        } finally {
            closeConnection(socket);
        }
    }

}
