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

package com.aspect.snoop.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SnoopClassLoader extends ClassLoader {

    String classpath;

    Map<String,byte[]> classes;

    public SnoopClassLoader(String classpath) {
        super();
        this.classes = new HashMap<String,byte[]>();
        this.classpath = classpath;
    }

    public SnoopClassLoader() {
        super();
        this.classes = new HashMap<String,byte[]>();
        this.classpath = System.getProperty("java.class.path");
    }

    public Set<String> getClassNames() {
        Set<String> classNames = classes.keySet();
        return classNames;
    }

    public void setClass(String name, byte[] bytes) {
        classes.put(name, bytes);
    }

    @Override
    public Class findClass(String name) throws ClassNotFoundException {

       byte[] b = loadClassData(name);

        if ( b == null ) {
         throw new ClassNotFoundException();
        }

        return defineClass(name, b, 0, b.length);
     }

     public byte[] loadClassData(String name) {

         byte[] bytes = classes.get(name);

         if ( bytes != null ) {
             return bytes;
         }

         bytes = ClasspathUtil.getClassBytes(name, classpath, false);

         if ( bytes != null ) {
             classes.put(name, bytes);
         }

         return bytes;
     }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

}
