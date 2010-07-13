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

package com.aspect.snoop.messages;

import java.io.Serializable;

public abstract class AgentMessage implements Serializable {

    private boolean success = true;
    private String message = "";
    private int hookId = 0;

    public void setMessage(String message) {
        this.message = message;
    }

    public int getHookId() {
        return this.hookId;
    }
    
    public void setHookId(int hookId) {
        this.hookId = hookId;
    }

    public String getMessage() {
        return this.message;
    }
    
    public void setWasSuccessful(boolean success) {
        this.success = success;
    }
    
    public boolean wasSuccessful() {
        return success;
    }

}
