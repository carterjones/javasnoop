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

import com.aspect.snoop.util.ClasspathUtil;
import com.aspect.snoop.util.ReflectionUtil;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class UniqueMethod implements Serializable {

    Class c;
    String className; // in case c is serialized
    String method;
    String[] types;
    boolean isInterfaceOrAbstract;
    Class returnType;
    String returnTypeName;

    public UniqueMethod(String className, String methodName, String[] types) {
        this.className = className;
        this.method = methodName;
        this.types = types;
    }

    public void setReturnType(Class returnType) {
        this.returnType = returnType;
        this.returnTypeName = returnType.getName();
    }
    
    public void setReturnTypeName(String returnType) {
        this.returnTypeName = returnType;
    }

    public String getReturnTypeName() {
        return this.returnTypeName;
    }

    public boolean isInterfaceOrAbstract() {
        return isInterfaceOrAbstract;
    }

    public UniqueMethod(Constructor c) throws ClassNotFoundException {
        this.c = c.getDeclaringClass();
        this.className = c.getDeclaringClass().getName();
        this.method = "<init>";
        this.types = ClasspathUtil.asStrings(c.getParameterTypes());
        this.returnType = Void.class;
        this.returnTypeName = returnType.getName();
    }

    public UniqueMethod(Method m) throws ClassNotFoundException {
        this.c = m.getDeclaringClass();
        this.className = c.getName();
        this.method = m.getName();
        this.types = ClasspathUtil.asStrings(m.getParameterTypes());
        this.returnType = m.getReturnType();
        this.returnTypeName = returnType.getName();
    }

    
    public UniqueMethod(Class c, String method, String[] types, String returnType) {
        this.c = c;
        this.className = c.getName();
        this.method = method;
        this.types = types;
        this.returnTypeName = returnType;
        this.isInterfaceOrAbstract = ReflectionUtil.isInterfaceOrAbstract(c);
    }
    
    public String getName() {
        return method;
    }
    public Class getParentClass() {
        return c;
    }

    public String getParentClassName() {
        return className;
    }

    public String[] getParameterTypes() {
        return types;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(className + ".");
        sb.append(method + "(");

        for(int i=0;i<types.length;i++) {
            String type = types[i];
            sb.append( ReflectionUtil.getSimpleClassName(type) );
            if ( i != types.length-1 ) {
                sb.append(", ");
            }
        }

        sb.append(")");
        return sb.toString();

    }
}