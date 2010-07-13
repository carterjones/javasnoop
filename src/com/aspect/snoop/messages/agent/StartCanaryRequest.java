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

public class StartCanaryRequest extends AgentMessage implements Serializable {
    private String canary;
    private String type;
    private String pkg;
    
    /**
     * @return the canary
     */
    public String getCanary() {
        return canary;
    }

    /**
     * @param canary the canary to set
     */
    public void setCanary(String canary) {
        this.canary = canary;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }


    public void setPackage(String pkg) {
        this.pkg = pkg;
    }

    public String getPackage() {
        return pkg;
    }

}
