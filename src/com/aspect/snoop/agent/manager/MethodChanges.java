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

package com.aspect.snoop.agent.manager;

import java.lang.reflect.AccessibleObject;

public class MethodChanges {
    private LocalVariable[] newLocalVariables;
    private String newStartSrc;
    private String newEndSrc;
    private AccessibleObject method;

    public void initialize(LocalVariable[] newLocalVars, String newStartSrc, String newEndSrc) {
        this.newLocalVariables = newLocalVars;
        this.newStartSrc = newStartSrc;
        this.newEndSrc = newEndSrc;
    }

    public MethodChanges(AccessibleObject method) {
        this.method = method;
        this.newLocalVariables = new LocalVariable[0];
        this.newStartSrc = "";
        this.newEndSrc = "";
    }

    /**
     * @return the newLocalVariables
     */
    public LocalVariable[] getNewLocalVariables() {
        return newLocalVariables;
    }

    /**
     * @param newLocalVariables the newLocalVariables to set
     */
    public void setNewLocalVariables(LocalVariable[] newLocalVariables) {
        this.newLocalVariables = newLocalVariables;
    }

    /**
     * @return the newStartSrc
     */
    public String getNewStartSrc() {
        return newStartSrc;
    }

    /**
     * @param newStartSrc the newStartSrc to set
     */
    public void setNewStartSrc(String newStartSrc) {
        this.newStartSrc = newStartSrc;
    }

    /**
     * @return the newEndSrc
     */
    public String getNewEndSrc() {
        return newEndSrc;
    }

    /**
     * @param newEndSrc the newEndSrc to set
     */
    public void setNewEndSrc(String newEndSrc) {
        this.newEndSrc = newEndSrc;
    }

    public void appendStartSrc(String newStartSrc) {
        this.newStartSrc += newStartSrc;
    }

    public void appendEndSrc(String newEndSrc) {
        this.newEndSrc += newEndSrc;
    }

    public void addLocalVariables(LocalVariable[] newLocalVariables) {
        LocalVariable[] lvs = new LocalVariable[this.newLocalVariables.length + newLocalVariables.length];
        for(int i=0;i<this.newLocalVariables.length;i++) {
            lvs[i] = this.newLocalVariables[i];
        }
        for(int i=0;i<newLocalVariables.length;i++) {
            lvs[i + this.newLocalVariables.length] = newLocalVariables[i];
        }
        this.newLocalVariables = lvs;
    }

    public AccessibleObject getMethod() {
        return method;
    }

    
}
