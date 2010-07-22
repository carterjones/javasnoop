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
import com.aspect.snoop.agent.manager.UniqueMethod;
import com.aspect.snoop.messages.AgentMessage;
import com.aspect.snoop.messages.UnrecognizedMessage;
import com.aspect.snoop.messages.client.CanaryChirpRequest;
import com.aspect.snoop.messages.client.CanaryChirpResponse;
import com.aspect.snoop.messages.client.PauseRequest;
import com.aspect.snoop.messages.client.PauseResponse;
import com.aspect.snoop.messages.client.PrintParametersRequest;
import com.aspect.snoop.messages.client.PrintParametersResponse;
import com.aspect.snoop.messages.client.PrintStackTraceRequest;
import com.aspect.snoop.messages.client.PrintStackTraceResponse;
import com.aspect.snoop.messages.client.RunScriptRequest;
import com.aspect.snoop.messages.client.RunScriptResponse;
import com.aspect.snoop.messages.client.ShowErrorRequest;
import com.aspect.snoop.messages.client.ShowErrorResponse;
import com.aspect.snoop.messages.client.TamperParametersRequest;
import com.aspect.snoop.messages.client.TamperParametersResponse;
import com.aspect.snoop.messages.client.TamperReturnRequest;
import com.aspect.snoop.messages.client.TamperReturnResponse;
import com.aspect.snoop.util.SerializationUtil;
import com.thoughtworks.xstream.XStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SnoopServerThread extends AbstractServerThread {

    public final static String SUCCESS = "SUCCESS";
    public final static String FAIL = "FAIL";

    public int port = 0xADDA; // default

    private XStream serializer;

    protected SnoopServerThread(int port) {
        
        super();

        this.port = port;
        this.serializer = new XStream();

    }

    /**
     *
     * @param message
     * @param input
     * @param output
     * @throws IOException
     */
    protected void processCommand(AgentMessage message, ObjectInputStream input, ObjectOutputStream output) throws IOException {

        String nl = System.getProperty("line.separator");

        if ( message instanceof PauseRequest ) {

            PauseRequest request = (PauseRequest)message;
            PauseResponse response = new PauseResponse();

            try {

                // handle an incoming pause request
                JavaSnoop.getMainForm().pause(
                        request.getClassName(),
                        request.getHookId(),
                        request.getParameters());

            } catch (Exception e) {
                populateResponse(response,e);
            }

            output.writeObject(response);
            
        } else if ( message instanceof PrintParametersRequest ) {

            PrintParametersRequest request = (PrintParametersRequest)message;
            PrintParametersResponse response = new PrintParametersResponse();

            try {

                // print the parameters
                JavaSnoop.getMainForm().printParameters(
                        request.getClassName(),
                        request.getHookId(),
                        request.getParameterTypes(),
                        SerializationUtil.prepareObjectsForUsing(request.getParameters(),serializer));
                

            } catch(Exception e) {
                populateResponse(response,e);
            }

            output.writeObject(response);

        } else if ( message instanceof PrintStackTraceRequest ) {

            PrintStackTraceRequest request = (PrintStackTraceRequest)message;
            PrintStackTraceResponse response = new PrintStackTraceResponse();

            try {

                // print the parameters
                JavaSnoop.getMainForm().printStackTrace(
                        request.getClassName(),
                        request.getHookId(),
                        request.getStackTrace(),
                        request.getParameterTypes(),
                        SerializationUtil.prepareObjectsForUsing(request.getParameters(),serializer));

            } catch(Exception e) {
                populateResponse(response,e);
            }

            output.writeObject(response);

        } else if ( message instanceof RunScriptRequest ) {

            RunScriptRequest request = (RunScriptRequest)message;
            RunScriptResponse response = new RunScriptResponse();

            try {

                // run the script

            } catch(Exception e) {
                populateResponse(response,e);
            }

            output.writeObject(response);

        } else if ( message instanceof TamperParametersRequest ) {

            TamperParametersRequest request = (TamperParametersRequest)message;
            TamperParametersResponse response = new TamperParametersResponse();

            try {

                String[] types = request.getParameterTypes();
                Object[] objs = SerializationUtil.prepareObjectsForUsing(request.getParameters(),serializer);
                
                // tamper!
                Object[] modifications = JavaSnoop.getMainForm().tamperWithParameters(
                        request.getClassName(),
                        request.getHookId(),
                        objs,
                        types,
                        false);

                response.setModifiedParameters( SerializationUtil.prepareObjectsForSending(modifications,types,serializer) );

            } catch (Exception e) {
                populateResponse(message,e);
            }

            output.writeObject(response);

        } else if ( message instanceof TamperReturnRequest ) {

            TamperReturnRequest request = (TamperReturnRequest)message;
            TamperReturnResponse response = new TamperReturnResponse();

            try {
                
                Object[] objs = SerializationUtil.prepareObjectsForUsing( new Object[]{request.getValue()}, serializer);
                
                // tamper!
                Object[] modifications = JavaSnoop.getMainForm().tamperWithParameters(
                        request.getClassName(),
                        request.getHookId(),
                        objs,
                        new String[]{request.getType()}, true);

                String returnType = JavaSnoop.getMainForm().getHookById(request.getHookId()).getReturnType();

                response.setModifiedValue(
                        SerializationUtil.prepareObjectsForSending(
                            modifications,
                            new String[]{returnType},
                            serializer)[0] );

            } catch (Exception e) {
                populateResponse(message,e);
            }

            output.writeObject(response);
            
        } else if ( message instanceof ShowErrorRequest ) {
            
            ShowErrorRequest request = (ShowErrorRequest)message;
            
            JavaSnoop.getMainForm().showConsoleErrorMessage(request.getMessage());

            ShowErrorResponse response = new ShowErrorResponse();

            output.writeObject(response);

        } else if ( message instanceof CanaryChirpRequest ) {

            CanaryChirpRequest request = (CanaryChirpRequest)message;
            CanaryChirpResponse response = new CanaryChirpResponse();

            UniqueMethod chirp = request.getMethod();
            JavaSnoop.getMainForm().getCanaryView().addChirp(chirp);
            output.writeObject(response);

        } else {

            UnrecognizedMessage response = new UnrecognizedMessage();

            output.writeObject(response);

        }

    }

    protected int getServerPort() {
        return port;
    }

    public Object[] prepareObjectsForUsing(Object[] objects) {
        for(int i=0;i<objects.length;i++) {
            Object o = objects[i];
            if ( o instanceof TamperParameter ) {
                TamperParameter param = (TamperParameter)o;
                objects[i] = serializer.fromXML(param.getXML()); //  replaced!
            }
        }
        return objects;
    }
}