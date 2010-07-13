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

import com.aspect.snoop.messages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class AbstractServerThread extends Thread {

    private boolean listening;

    protected AbstractServerThread() {
        listening = true;
    }

    public void endServer() {
        try {
            listening = false;
        } catch(Exception e) { }
    }

    @Override
    public void run() {

        Socket toClientSocket = null;

        try {

            ServerSocket serverSocket = new ServerSocket(getServerPort());
            
            while(listening) {

                toClientSocket = serverSocket.accept();

                String host = toClientSocket.getInetAddress().getHostAddress();
                if ( ! "127.0.0.1".equals(host) ) {
                    toClientSocket.close();
                    continue;
                }

                // read cmd from client socket
                ObjectInputStream reader = new ObjectInputStream(toClientSocket.getInputStream());
                ObjectOutputStream writer = new ObjectOutputStream(toClientSocket.getOutputStream());

                // process cmd

                Object o = reader.readObject();

                if ( ! (o instanceof AgentMessage) ) {
                    UnrecognizedMessage response = new UnrecognizedMessage();
                    response.setMessage("Unexpected class encountered: "+o.getClass().getName());
                    response.setWasSuccessful(false);
                    continue;
                }

                AgentMessage message = (AgentMessage)o;

                processCommand(message, reader, writer);

                // hang up
                writer.close();
                reader.close();
                toClientSocket.close();

            }

            serverSocket.close();

        } catch (IOException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ex.printStackTrace();
        } finally {
            try {
                toClientSocket.close();
            } catch (Exception ex) { }
        }
    }

    protected abstract int getServerPort();

    protected abstract void processCommand(
                    AgentMessage message, ObjectInputStream input, ObjectOutputStream output)
                    throws IOException;

    protected void populateResponse(AgentMessage response, Throwable t) {
        response.setWasSuccessful(false);
        response.setMessage(t.getMessage());
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        response.setMessage(t.getMessage() + "\r\n" + sw.toString());
    }

}