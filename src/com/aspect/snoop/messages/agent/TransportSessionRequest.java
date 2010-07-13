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

package com.aspect.snoop.messages.agent;

import com.aspect.snoop.messages.*;
import com.aspect.snoop.SnoopSession;
import java.io.Serializable;

public class TransportSessionRequest extends AgentMessage implements Serializable {

    public enum SessionRetrievalType {
        LoadFromFile,
        LoadFromObject,
        LoadFromXMLString
    }

    private SessionRetrievalType type;
    private String filename;
    private String xmlString;
    private SnoopSession session;


    public TransportSessionRequest(SessionRetrievalType type) {
        this.type = type;
    }

    public SnoopSession getSnoopSession() {
        return session;
    }

    public void setSnoopSession(SnoopSession session) {
        this.session = session;
    }
    public String getXML() {
        return this.xmlString;
    }
    public void setXML(String xmlString) {
        this.xmlString = xmlString;
    }

    public String getFilename() {

        return this.filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setType(SessionRetrievalType type) {
        this.type = type;
    }
    public SessionRetrievalType getType() {
        return this.type;
    }


}
