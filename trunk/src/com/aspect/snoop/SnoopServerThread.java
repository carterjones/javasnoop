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

import com.aspect.snoop.agent.AbstractServerThread;
import com.aspect.snoop.messages.*;
import com.aspect.snoop.messages.client.PauseRequest;
import com.aspect.snoop.messages.client.PrintParametersRequest;
import com.aspect.snoop.messages.client.RunScriptRequest;
import com.aspect.snoop.messages.client.TamperParametersRequest;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SnoopServerThread extends AbstractServerThread {

    public final static String ACTION_BREAKPOINT = "BREAKPOINT";
    
    public final static int SERVER_PORT = 0xDAAD;

	private SnoopSession snoopSession;

    SnoopServerThread() {
        super();
    }

	/**
	 * Too complicated.  Why not just send a serialized object for every message?  The XML doc can be a DOM
	 * document or even a String to be deserialized.
	 *
	 * @param command
	 * @param reader
	 * @param writer
	 * @throws IOException
	 */
	protected void processCommand(AgentMessage message, ObjectInputStream input, ObjectOutputStream output)
			throws IOException {

            if ( message instanceof PauseRequest ) {

            } else if ( message instanceof TamperParametersRequest ) {

            } else if ( message instanceof PrintParametersRequest ) {

            } else if ( message instanceof RunScriptRequest ) {

            }

            //Logger.getLogger(SnoopServerThread.class.getName()).log(Level.SEVERE, "Unrecognized command: " + command);

	}

	protected int getServerPort() {
		return SERVER_PORT;
	}

    /**
     * This function is the entry point into the instrumentation of the
     * running program. It turns on the rules we instrument.
     */
    private void applySessionRules() {

        for(FunctionHookInterceptor hook : snoopSession.getFunctionHooks() ) {

            // detect if hook is already in place instrumented

            // instrument hook
        }

    }

}