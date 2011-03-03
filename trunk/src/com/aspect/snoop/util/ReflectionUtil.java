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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class ReflectionUtil {

    private static final Class[] PRIMITIVE_CLASSES = {
        Boolean.class,
        Byte.class,
        Character.class,
        Short.class,
        Integer.class,
        Long.class,
        String.class, // not really a primitive but there is a default editor for it in the table
        Double.class,
        Float.class,
        boolean.class,
        byte.class,
        int.class,
        char.class,
        short.class,
        int.class,
        long.class,
        double.class,
        float.class
    };

    public static Map<String,String> primitiveArrayMap;

    public static boolean isPrimitiveClassName(String cls) {
        if ( primitiveArrayMap.get(cls) != null ) {
            return true;
        }

        String prims[] = { "boolean", "char", "byte", "short", "int", "long", "double", "float" };
        for( String s : prims ) {
            if ( s.equals(cls) ) {
                return true;
            }
        }
        return false;

    }

    static {
        primitiveArrayMap = new HashMap<String,String>();
        primitiveArrayMap.put("[Z", "boolean[]");
        primitiveArrayMap.put("[B", "byte[]");
        primitiveArrayMap.put("[C", "char[]");
        primitiveArrayMap.put("[S", "short[]");
        primitiveArrayMap.put("[I", "int[]");
        primitiveArrayMap.put("[J", "long[]");
        primitiveArrayMap.put("[D", "double[]");
        primitiveArrayMap.put("[F", "float[]");
    }

    public static final int LANGUAGE_MODIFIERS =
            Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE |
            Modifier.ABSTRACT | Modifier.STATIC | Modifier.FINAL |
            Modifier.SYNCHRONIZED | Modifier.NATIVE;

    public static String getMethodDescription(Member member) {

        StringBuilder desc = new StringBuilder("");
        String returnType = null;
        String methodName = null;
        Class[] params = null;

        if ( !(member instanceof Method) && !(member instanceof Constructor) ) {
            return member.toString();
        }

        //String modifiers = Modifier.toString(m.getModifiers() & LANGUAGE_MODIFIERS);
        Method m = null;
        Constructor c = null;

        if ( member instanceof Method ) {
            m = (Method)member;
            returnType = ReflectionUtil.getSimpleClassName(m.getReturnType().getName());
            methodName = m.getName();
            params = m.getParameterTypes();
        } else {
            c = (Constructor)member;
            returnType = "void";
            methodName = "<init>";
            params = c.getParameterTypes();
        }

        desc.append(returnType);
        desc.append(" ");
        
        desc.append(methodName);
        desc.append("(");

        for (int i = 0; i < params.length; i++) {
            String className = ReflectionUtil.getSimpleClassName(params[i].getName());
            desc.append(className);
            if (i != params.length - 1) {
                desc.append(",");
            }
        }

        desc.append(")");

        return desc.toString();

    }

    /*
    public static String getMethodDescription(UniqueMethod m) {
        StringBuilder desc = new StringBuilder("");

        desc.append(m.getName() + "(");

        String[] params = m.getParameterTypes();

        for (int i = 0; i < params.length; i++) {
            String className = ReflectionUtil.getSimpleClassName(params[i]);
            desc.append(className);
            if (i != params.length - 1) {
                desc.append(",");
            }
        }

        desc.append(")");

        return desc.toString();
    }*/

    public static Field getAccessibleField(Object o, String s) throws Exception {
        Field f = o.getClass().getDeclaredField(s);
        f.setAccessible(true);
        return f;
    }

    public static Field getAccessibleField(Object o, Field f) {
        f.setAccessible(true);
        return f;
    }

    public static String getStringValue(Object o, String fieldName) throws Exception {
        Field f = getAccessibleField(o, fieldName);

        return (String) f.get(o);
    }

    public static Object getAsStringValue(Object o, String fieldName) throws Exception {
        Field f = getAccessibleField(o, fieldName);
        return f.get(o).toString();
    }

    public static Class[] getClassArrayValue(Object wrappedObj, String s) throws Exception {
        Field f = getAccessibleField(wrappedObj, s);
        return (Class[]) f.get(wrappedObj);
    }

    public static Object[] getObjectArrayValue(Object wrappedObj, String s) throws Exception {
        Field f = getAccessibleField(wrappedObj, s);
        return (Object[]) f.get(wrappedObj);
    }

    public static String getSimpleClassName(String name) {

        String cls = name.substring(name.lastIndexOf(".") + 1);

        if ( cls.startsWith("["))  {
            String primArray = primitiveArrayMap.get(name);
            if ( primArray != null ) {
                return primArray;
            }
        } else if ( cls.endsWith(";") ) {
            cls = cls.substring(0,cls.length()-1) + "[]";
        }

        return cls;
    }

    public static String getPackageFromClass(String cls) {
        int lastDot = cls.lastIndexOf(".");

        if ( lastDot == -1 ) {
            return "";
        }

        return cls.substring(0,lastDot);
        
    }

    public static List<Field> getAllPrimitiveFields(Object o) {
        List<Field> primitiveFields = new ArrayList<Field>();

        Field[] fields = o.getClass().getDeclaredFields();

        List<Field> allFields = new ArrayList<Field>();
        allFields.addAll( Arrays.asList(fields) );

        Class cls = o.getClass();
        while( (cls = cls.getSuperclass()) != null && ! cls.getClass().equals(Object.class) ) {
            allFields.addAll( Arrays.asList(cls.getDeclaredFields()) );
        }

        for (Field f : allFields) {

            int mod = f.getModifiers();
            if ( Modifier.isStatic(mod) ) {
                continue;
            }

            f.setAccessible(true);

            for (Class c : PRIMITIVE_CLASSES) {
                try {

                    if (f.getType().equals(c)) {
                        primitiveFields.add(f);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        return primitiveFields;
    }

    public static List<Field> getAllNonPrimitiveFields(Object o) {

        List<Field> nonPrimitiveFields = new ArrayList<Field>();

        Field[] fields = o.getClass().getDeclaredFields();

        List<Field> allFields = new ArrayList<Field>();
        allFields.addAll( Arrays.asList(fields) );

        Class cls = o.getClass();
        while( (cls = cls.getSuperclass()) != null && ! cls.getClass().equals(Object.class) ) {
            allFields.addAll( Arrays.asList(cls.getDeclaredFields()) );
        }

        for (Field f : allFields) {

            f.setAccessible(true);

            int mod = f.getModifiers();

            if ( Modifier.isStatic(mod) || f.isSynthetic() ) {
                continue;
            }

            boolean isPrimitive = false;

            try {

                for (Class c : PRIMITIVE_CLASSES) {
                    if (f.getType().equals(c)) {
                        isPrimitive = true;
                    }
                }

                if (!isPrimitive) {
                    nonPrimitiveFields.add(f);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return nonPrimitiveFields;
    }


    public static boolean isBoolean(Field f) {
        return f.getType().equals(boolean.class) || f.getType().equals(Boolean.class);
    }

    public static boolean isString(Field f) {
        return f.getType().equals(String.class);
    }

    public static boolean getBooleanValue(Object o, String fieldName) throws Exception {
        Field f = getAccessibleField(o, fieldName);
        return ((Boolean) f.get(o)).booleanValue();
    }

    public static boolean isShort(Field f) {
        return f.getType().equals(short.class) || f.getType().equals(Short.class);
    }

    public static short getShortValue(Object o, String fieldName) throws Exception {
        Field f = getAccessibleField(o, fieldName);
        return ((Short) f.get(o)).shortValue();
    }

    public static boolean isInteger(Field f) {
        return f.getType().equals(int.class) || f.getType().equals(Integer.class);
    }

    public static int getIntegerValue(Object o, String fieldName) throws Exception {
        Field f = getAccessibleField(o, fieldName);
        return ((Integer) f.get(o)).intValue();
    }

    public static boolean isLong(Field f) {
        return f.getType().equals(long.class) || f.getType().equals(Long.class);
    }

    public static long getLongValue(Object o, String fieldName) throws Exception {
        Field f = getAccessibleField(o, fieldName);
        return ((Long) f.get(o)).longValue();
    }

    public static boolean isFloat(Field f) {
        return f.getType().equals(float.class) || f.getType().equals(Float.class);
    }

    public static boolean hasMainClass(Class c) {
        try {
            Method m = c.getDeclaredMethod("main", new String[]{}.getClass());
            return true;
        } catch (NoSuchMethodException e) {
        }
        return false;
    }

    public static boolean hasMainClass(CtClass c) {

        try {
            CtClass[] params = new CtClass[1];
            params[0] = ClassPool.getDefault().get(new String[]{}.getClass().getName());
            CtMethod m = c.getDeclaredMethod("main", params);
            return true;
        } catch (NotFoundException e) { }
        return false;
    }

    public static boolean hasMainClass(CtClass c, ClassPool cp) {

        try {
            CtClass[] params = new CtClass[1];
            params[0] = cp.get(new String[]{}.getClass().getName());
            CtMethod m = c.getDeclaredMethod("main", params);
            return true;
        } catch (NotFoundException e) { }
        return false;
    }

    public static boolean isInterfaceOrAbstract(Class c) {
        int modifier = c.getModifiers();

        return Modifier.isAbstract(modifier) || Modifier.isInterface(modifier);
    }

    public static Class[] getParameterTypes(AccessibleObject method) {
        if ( method instanceof Method )
            return ((Method)method).getParameterTypes();
        else if ( method instanceof Constructor )
            return ((Constructor)method).getParameterTypes();
        throw new IllegalArgumentException("Expected method or constructor");
    }

    public static Class getDeclaringClass(AccessibleObject method) {
        if ( method instanceof Method )
            return ((Method)method).getDeclaringClass();
        else if ( method instanceof Constructor )
            return ((Constructor)method).getDeclaringClass();
        throw new IllegalArgumentException("Expected method or constructor");
    }

    public static String getMethodName(AccessibleObject method) {
        if ( method instanceof Method )
            return ((Method)method).getName();
        else if ( method instanceof Constructor )
            return ((Constructor)method).getName();
        throw new IllegalArgumentException("Expected method or constructor");
    }

    public static boolean isInterfaceOrAbstract(AccessibleObject method) {
        if ( method instanceof Method )
            return Modifier.isAbstract(((Method)method).getModifiers());
        else if ( method instanceof Constructor )
            return Modifier.isAbstract(((Constructor)method).getModifiers());
        throw new IllegalArgumentException("Expected method or constructor");
    }

    public static Class getReturnType(AccessibleObject method) {
        if ( method instanceof Method )
            return ((Method)method).getReturnType();
        else if ( method instanceof Constructor )
            return Void.class;
        throw new IllegalArgumentException("Expected method or constructor");
    }

    public float getFloatValue(Object o, String fieldName) throws Exception {
        Field f = getAccessibleField(o, fieldName);
        return ((Float) f.get(o)).floatValue();
    }

    public static boolean isDouble(Field f) {
        return f.getType().equals(double.class) || f.getType().equals(Double.class);
    }

    public static double getDoubleValue(Object o, String fieldName) throws Exception {
        Field f = getAccessibleField(o, fieldName);
        return ((Double) f.get(o)).doubleValue();
    }

    public static boolean isCharacter(Field f) {
        return f.getType().equals(char.class) || f.getType().equals(Character.class);
    }

    public static char getCharacterValue(Object o, String fieldName) throws Exception {
        Field f = getAccessibleField(o, fieldName);
        return ((Character) f.get(o)).charValue();
    }

    public static boolean isByte(Field f) {
        return f.getType().equals(byte.class) || f.getType().equals(Byte.class);
    }

    public static byte getByteValue(Object o, String fieldName) throws Exception {
        Field f = getAccessibleField(o, fieldName);
        return ((Byte) f.get(o)).byteValue();
    }

    public static boolean isPrimitiveButNotArray(Object o) {

        for (Class c : PRIMITIVE_CLASSES) {
            if (c.equals(o.getClass())) {
                return true;
            }
        }
        
        return false;
    }

    public static boolean isPrimitive(Object o) {

        for (Class c : PRIMITIVE_CLASSES) {
            if (c.equals(o.getClass())) {
                return true;
            }
        }

        String clsName = o.getClass().getName();
        if ( primitiveArrayMap.get(clsName) != null ) {
            return true;
        }
        
        return false;
    }

    public static boolean isSerializable(Object object) {

        ObjectOutputStream oos = null;

        try {

            if (ReflectionUtil.isPrimitive(object)) {
                return true;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.close();

            return baos.toByteArray().length > 0;

        } catch (IOException ex) {

            return false;

        }

    }

    public static Object getObjectFrom(Object o) {
        return o;
    }

    public static Object getObjectFrom(byte b) {
        return new Byte(b);
    }

    public static Object getObjectFrom(boolean b) {
        return new Boolean(b);
    }

    public static Object getObjectFrom(char c) {
        return new Character(c);
    }

    public static Object getObjectFrom(short s) {
        return new Short(s);
    }

    public static Object getObjectFrom(int i) {
        return new Integer(i);
    }

    public static Object getObjectFrom(long l) {
        return new Long(l);
    }

    public static Object getObjectFrom(double d) {
        return new Double(d);
    }

    public static Object getObjectFrom(float f) {
        return new Float(f);
    }

}
