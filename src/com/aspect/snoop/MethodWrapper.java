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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MethodWrapper {

    AccessibleObject method;
    boolean constructor;
    String name;

    public static MethodWrapper getWrapper(Class clazz, String methodName, Class[] parameterTypes) {
        try {
            if ( "<init>".equals(methodName) ) {
                return getWrapper((AccessibleObject)clazz.getDeclaredConstructor(parameterTypes));
            } else {
                return getWrapper((AccessibleObject)clazz.getDeclaredMethod(methodName,parameterTypes));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static MethodWrapper getWrapper(Member obj) {
        if (obj instanceof Method)
            return new MethodWrapper((Method)obj);
        if (obj instanceof Constructor)
            return new MethodWrapper((Constructor)obj);
        throw new IllegalArgumentException("Expecting method or constructor");
    }

    public static MethodWrapper getWrapper(AccessibleObject obj) {
        if (obj instanceof Method)
            return new MethodWrapper((Method)obj);
        if (obj instanceof Constructor)
            return new MethodWrapper((Constructor)obj);
        throw new IllegalArgumentException("Expecting method or constructor");
    }

    public MethodWrapper(Method m) {
        this.method = m;
        constructor = false;
        name = m.getName();
    }

    public MethodWrapper(Constructor c) {
        this.method = c;
        constructor = true;
        name = "<init>";
    }

    public String getName() { 
        return name;
    }

    public Class getDeclaringClass() {
        if(constructor)
            return ((Constructor)method).getDeclaringClass();
        return ((Method)method).getDeclaringClass();
    }

    public Class[] getParameterTypes() {
        if(constructor)
            return ((Constructor)method).getParameterTypes();
        return ((Method)method).getParameterTypes();
    }

    public Class getReturnType() {
        if(constructor)
            return Void.class;
        return ((Method)method).getReturnType();
    }

    public boolean isAbstract() {
        if(constructor)
            return Modifier.isAbstract(((Constructor)method).getModifiers());
        return Modifier.isAbstract(((Method)method).getModifiers());
    }

    public boolean isConstructor() {
        return constructor;
    }

    public int getModifiers() {
        if(constructor)
            return ((Constructor)method).getModifiers();
        return ((Method)method).getModifiers();
    }

    public String getDescription() {
        return method.toString();
    }

    public AccessibleObject getActualMethod() {
        return method;
    }
}
