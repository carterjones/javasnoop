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

import com.aspect.snoop.messages.AgentMessage;
import java.io.Serializable;

/**
 *
 * @author adabirsiaghi
 */
public class ExecuteScriptRequest extends AgentMessage implements Serializable {

    String script;
    boolean rememberState;
    String language;

    public void setLanguage(String l) {
        this.language = l;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getScript() {
        return script;
    }

    public void setRememberState(boolean b) {
        this.rememberState = b;
    }

    public boolean shouldRememberState() {
        return this.rememberState;
    }
}
