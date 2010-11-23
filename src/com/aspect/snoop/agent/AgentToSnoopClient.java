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

import com.aspect.snoop.agent.manager.UniqueMethod;
import com.aspect.snoop.messages.client.CanaryChirpRequest;
import com.aspect.snoop.messages.client.CanaryChirpResponse;
import com.aspect.snoop.messages.client.PauseRequest;
import com.aspect.snoop.messages.client.PauseResponse;
import com.aspect.snoop.messages.client.PrintParametersRequest;
import com.aspect.snoop.messages.client.PrintParametersResponse;
import com.aspect.snoop.messages.client.PrintStackTraceRequest;
import com.aspect.snoop.messages.client.PrintStackTraceResponse;
import com.aspect.snoop.messages.client.ShowErrorRequest;
import com.aspect.snoop.messages.client.ShowErrorResponse;
import com.aspect.snoop.messages.client.TamperParametersRequest;
import com.aspect.snoop.messages.client.TamperParametersResponse;
import com.aspect.snoop.messages.client.TamperReturnRequest;
import com.aspect.snoop.messages.client.TamperReturnResponse;
import com.aspect.snoop.util.ClasspathUtil;
import com.aspect.snoop.util.SerializationUtil;
import com.thoughtworks.xstream.XStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Administrator
 */
public class AgentToSnoopClient {

    private String host;
    private int port;
    private XStream serializer;

    private String canary;
    private static AgentToSnoopClient current;

    public void setCanary(String canary) {
        this.canary = canary;
    }

    public void clearCanary() {
        this.canary = null;
    }

    public static void initialize(String host, int port) {
        current = new AgentToSnoopClient(host, port);
    }

    public static AgentToSnoopClient currentClient() {
        return current;
    }

    public static void finished() {
        current = null;
    }
    
    private AgentToSnoopClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.serializer = new XStream();
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setHost(String host) {
        this.host = host;
    }
    
    private Socket getConnection() throws UnknownHostException, IOException {
        Socket socket = new Socket(host, port);
        socket.setKeepAlive(true);
        return socket;
    }

    private void closeConnection(Socket s) {
        try {
            s.close();
        } catch (Exception ex) { }
    }

    /**
     * This method tells the Snoop program about the usage of this particular parameter type.
     */
    public void canaryChirp(Class canaryType, String className, String methodName, Class[] types, Object[] objects) {

        if ( ! canaryIsHeard(canaryType, types, objects)) {
            return;
        }

        Socket socket = null;

        try {

            socket = getConnection();

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            CanaryChirpRequest request = new CanaryChirpRequest();
            UniqueMethod m = new UniqueMethod(className, methodName, ClasspathUtil.asStrings(types));
            request.setMethod(m);
            
            output.writeObject(request);

            CanaryChirpResponse response = (CanaryChirpResponse)input.readObject();

            if ( ! response.wasSuccessful() ) {
                throw new AgentCommunicationException("Couldn't send chirp to home base: " + response.getMessage());
            }

        } catch (Exception e) {
            sendErrorMessage("Encountered exception during Canary communication: " + convert(e));
        } finally {
            closeConnection(socket);
        }
    }


    /**
     * This method tells the Snoop program to tamper with the parameters of a method.
     *
     * @throws AgentCommunicationException if there is a communication error
     */
    public Object[] tamperParameters(int hookId, Object[] objects, String argTypes) {

        String clazz = new Exception().getStackTrace()[1].getClassName();

        Socket socket = null;

        try {

            socket = getConnection();

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            String[] types = argTypes.split(",");

            TamperParametersRequest request = new TamperParametersRequest( SerializationUtil.prepareObjectsForSending(objects, types, serializer), types );
            request.setClassName(clazz);
            request.setHookId(hookId);

            output.writeObject(request);

            TamperParametersResponse response = (TamperParametersResponse)input.readObject();

            if ( ! response.wasSuccessful() ) {
                throw new AgentCommunicationException("Couldn't tell home base to tamper with parameters: " + response.getMessage());
            }

            Object[] modifications = SerializationUtil.prepareObjectsForUsing ( response.getModifiedParameters(), serializer );

            return modifications;

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorMessage("Encountered exception during Tamper communication: " + convert(e));
        } finally {
            closeConnection(socket);
        }

        return objects;
    }

        /**
     * This method tells the Snoop program to tamper with the parameters of a method.
     *
     * @throws AgentCommunicationException if there is a communication error
     */
    public Object tamperReturn(int hookId, Object object, String returnType) {

        String clazz = new Exception().getStackTrace()[1].getClassName();

        Socket socket = null;

        try {

            socket = getConnection();

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            TamperReturnRequest request = new TamperReturnRequest(
                    SerializationUtil.prepareObjectsForSending(
                        new Object[]{object},
                        new String[]{returnType},
                        serializer)[0],
                        returnType
            );

            request.setClassName(clazz);
            request.setHookId(hookId);

            output.writeObject(request);

            TamperReturnResponse response = (TamperReturnResponse)input.readObject();

            if ( ! response.wasSuccessful() ) {
                throw new AgentCommunicationException("Couldn't tell home base to tamper with parameters: " + response.getMessage());
            }

            Object modification = SerializationUtil.prepareObjectsForUsing ( new Object[]{response.getModifiedValue()}, serializer )[0];

            return modification;

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorMessage("Encountered exception during Tamper Return communication: " + convert(e));
        } finally {
            closeConnection(socket);
        }

        return object;
    }

        /**
     * This method tells the Snoop program to print the stack trace of a method.
     *
     * @throws AgentCommunicationException if there is a communication error
     */
    public void printStackTrace(int hookId, Object[] objects, String paramTypes) {

        String clazz = new Exception().getStackTrace()[1].getClassName();
        Socket socket = null;

        try {

            String[] types = paramTypes.split(",");

            socket = getConnection();

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            PrintStackTraceRequest request = new PrintStackTraceRequest( SerializationUtil.prepareObjectsForSending(objects, types, serializer), types, convert(new Exception("Function was called: ")) );
            request.setClassName(clazz);
            request.setHookId(hookId);
            
            output.writeObject(request);

            PrintStackTraceResponse response = (PrintStackTraceResponse)input.readObject();

            if ( ! response.wasSuccessful() ) {
                throw new AgentCommunicationException("Couldn't tell home base to print parameters: " + response.getMessage());
            }

        } catch (Exception e) {
            sendErrorMessage("Encountered exception during PrintParameters communication: " + convert(e));
        } finally {
            closeConnection(socket);
        }
    }


    /**
     * This method tells the Snoop program to print the parameters of a method.
     *
     * @throws AgentCommunicationException if there is a communication error
     */
    public void printParameters(int hookId, Object[] objects, String paramTypes) {
        
        String clazz = new Exception().getStackTrace()[1].getClassName();

        Socket socket = null;

        try {

            String[] types = paramTypes.split(",");

            socket = getConnection();

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            PrintParametersRequest request = new PrintParametersRequest( SerializationUtil.prepareObjectsForSending(objects, types, serializer), types );
            request.setClassName(clazz);
            request.setHookId(hookId);

            output.writeObject(request);

            PrintParametersResponse response = (PrintParametersResponse)input.readObject();

            if ( ! response.wasSuccessful() ) {
                throw new AgentCommunicationException("Couldn't tell home base to print parameters: " + response.getMessage());
            }
       
        } catch (Exception e) {
            sendErrorMessage("Encountered exception during PrintParameters communication: " + convert(e));
        } finally {
            closeConnection(socket);
        }
    }

    /**
     * This method tells the Snoop program to prompt the user when to unpause and
     * continue functioning.
     *
     * @throws AgentCommunicationException if there is a communication error
     */
    public void pauseProgram(int hookId, Object[] objects, String paramTypes) {

        String clazz = new Exception().getStackTrace()[1].getClassName();

        Socket socket = null;

        try {

            String[] types = paramTypes.split(",");

            socket = getConnection();

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            PauseRequest request = new PauseRequest( SerializationUtil.prepareObjectsForSending(objects, types, serializer) );
            request.setClassName(clazz);
            request.setHookId(hookId);

            output.writeObject(request);

            PauseResponse response = (PauseResponse)input.readObject();

            if ( ! response.wasSuccessful() ) {
                throw new AgentCommunicationException("Couldn't tell home base to pause: " + response.getMessage());
            }

        } catch (Exception e) {
            sendErrorMessage("Encountered exception during Pause communication: " + convert(e));
        } finally {
            closeConnection(socket);
        }
        
    }

    private String convert(Exception e) {
        
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);

        e.printStackTrace(writer);

        return sw.toString();
        
    }

    private void sendErrorMessage(String s) {

        Socket socket = null;

        try {

            socket = getConnection();

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            ShowErrorRequest request = new ShowErrorRequest();
            request.setMessage(s);

            output.writeObject(request);

            ShowErrorResponse response = (ShowErrorResponse)input.readObject();

            if ( ! response.wasSuccessful() ) {
                throw new AgentCommunicationException("Couldn't tell agent to show error message: " + response.getMessage());
            }

        } catch (Exception e) {
            // encountered an error while phoning home about an error. just bitch
            // about it to stdout.
            e.printStackTrace();
        } finally {
            closeConnection(socket);
        }

    }

    private boolean canaryIsHeard(Class canaryType, Class[] types, Object[] objects) {

        if ( canary == null ) {
            System.err.println("Got canary message but canary is null");
            return false;
        }

        for(int i=0; i<types.length; i++) {
            
            if ( objects[i] == null )
                continue;

            Class type = types[i];

            if ( canaryType.equals(type) ) {
                
                if ( type.equals(String.class) ) {
                    String value = (String)objects[i];
                    if ( canary.contains(value) ) {
                        return true;
                    }
                } else if ( type.equals(Short.class) ) {
                    Short value = (Short)objects[i];
                    if ( Short.valueOf(canary).equals(value)) {
                        return true;
                    }
                } else if ( type.equals(Integer.class) ) {
                    Integer value = (Integer)objects[i];
                    if ( Integer.valueOf(canary).equals(value)) {
                        return true;
                    }
                } else if ( type.equals(Long.class) ) {
                    Long value = (Long)objects[i];
                    if ( Long.valueOf(canary).equals(value)) {
                        return true;
                    }
                } else if ( type.equals(Double.class) ) {
                    Double value = (Double)objects[i];
                    if ( Double.valueOf(canary).equals(value)) {
                        return true;
                    }
                } else if ( type.equals(Float.class) ) {
                    Float value = (Float)objects[i];
                    if ( Float.valueOf(canary).equals(value)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

}
