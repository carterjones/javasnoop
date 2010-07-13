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

import java.io.Serializable;

public class ClassBytes implements Serializable {

    private String className;
    private byte[] classBytes;

    public ClassBytes(String clazz, byte[] bytes) {
        this.className = clazz;
        this.classBytes = bytes;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return the classBytes
     */
    public byte[] getClassBytes() {
        return classBytes;
    }

    /**
     * @param classBytes the classBytes to set
     */
    public void setClassBytes(byte[] classBytes) {
        this.classBytes = classBytes;
    }
    
}
