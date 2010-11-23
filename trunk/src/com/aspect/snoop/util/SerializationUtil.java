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

import com.aspect.snoop.JavaSnoop;
import com.aspect.snoop.agent.TamperParameter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;

public class SerializationUtil {

    // For each object, check to see if its serializable. First by
    // checking if its a Java primitive (this should short-circuit
    // the large majority of the time) and then by doing the IBM
    // test stolen from the URL following this comment.

    // If it's not serializable using pure Java, then use the IBM
    // XStream library to serialize it. The Snoop program knows how
    // to deserialize these messages in the same lib. We just have
    // to tell them which indices are serialized this way so they
    // don't think they're just strings.

    // http://www.ibm.com/developerworks/library/j-serialtest.html

    public static Object[] prepareObjectsForSending(Object[] objects, String[] types, XStream serializer) {

        Object[] finalObjects = new Object[objects.length];

        for(int i=0;i<objects.length;i++) {

            if ( objects[i] != null && ReflectionUtil.isSerializable(objects[i]) ) {

                finalObjects[i] = objects[i];

            } else if ( objects[i] == null ) {
                
                finalObjects[i] = new TamperParameter(
                        types[i],
                        serializer.toXML(null));

            } else {
                finalObjects[i] = new TamperParameter(
                    objects[i].getClass().getName(),
                    serializer.toXML(objects[i]));
            }
        }

        return finalObjects;
    }

    public static Object[] prepareObjectsForUsing(Object[] objects, XStream serializer) {
        for(int i=0;i<objects.length;i++) {
            Object o = objects[i];
            if ( o instanceof TamperParameter ) {
                TamperParameter param = (TamperParameter)o;
                try {
                    objects[i] = serializer.fromXML(param.getXML());
                } catch (CannotResolveClassException e) {
                    e.printStackTrace();
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    serializer.setClassLoader(JavaSnoop.getClassLoader());
                    Thread.currentThread().setContextClassLoader(JavaSnoop.getClassLoader());
                    objects[i] = serializer.fromXML(param.getXML());
                    serializer.setClassLoader(cl);
                    Thread.currentThread().setContextClassLoader(cl);
                }

            }
        }
        return objects;
    }
}
