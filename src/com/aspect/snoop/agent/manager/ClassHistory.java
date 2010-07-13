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

public class ClassHistory {

    private Class clazz;
    private byte[] originalClass;
    private byte[] lastClass;
    private byte[] currentClass;

    public ClassHistory(Class clazz, byte[] originalClass, byte[] currentClass) {
        this.clazz = clazz;
        this.originalClass = originalClass;
        this.currentClass = currentClass;
    }

    /**
     * @return the originalClass
     */
    public byte[] getOriginalClass() {
        return originalClass;
    }

    /**
     * @param originalClass the originalClass to set
     */
    public void setOriginalClass(byte[] originalClass) {
        this.originalClass = originalClass;
    }

    /**
     * @return the lastClass
     */
    public byte[] getLastClass() {
        return lastClass;
    }

    /**
     * @param lastClass the lastClass to set
     */
    public void setLastClass(byte[] lastClass) {
        this.lastClass = lastClass;
    }

    /**
     * @return the currentClass
     */
    public byte[] getCurrentClass() {
        return currentClass;
    }

    /**
     * @param currentClass the currentClass to set
     */
    public void setCurrentClass(byte[] currentClass) {
        this.currentClass = currentClass;
    }

    /**
     * @return the clazz
     */
    public Class getClazz() {
        return clazz;
    }

    /**
     * @param clazz the clazz to set
     */
    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

}
