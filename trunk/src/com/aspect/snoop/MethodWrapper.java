/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
