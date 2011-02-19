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

import com.aspect.snoop.agent.AgentLogger;
import com.aspect.snoop.agent.SnoopAgent;

public class CanaryUtil {

    public static String currentCanary = null;

    public static String getChirp(String canaryType, String className, String methodName, String returnType) {

        StringBuilder sb = new StringBuilder();

        String nl = System.getProperty("line.separator");

        sb.append("  com.aspect.snoop.util.CanaryUtil.canaryChirp(");
        sb.append(canaryType);
        sb.append(".class, ");
        sb.append(className);
        sb.append(".class, \"");
        sb.append(methodName);
        sb.append("\", $sig, $args);");
        sb.append(nl);

        return sb.toString();
    }

    public static void canaryChirp(Class canaryType, Class clazz, String methodName, Class[] types, Object[] objects) {

        if ( ! canaryIsHeard(canaryType, types, objects)) {
            return;
        }
        try {
            SnoopAgent.getMainView().getCanaryView().addChirp(clazz, clazz.getDeclaredMethod(methodName, types));
        } catch (Exception ex) {
            AgentLogger.error("Problem receiving canary",ex);
        }
    }

    private static boolean canaryIsHeard(Class canaryType, Class[] types, Object[] objects) {

        if ( currentCanary == null ) {
            System.err.println("Got canary message but canary is null");
            return false;
        }

        for(int i=0; i<types.length; i++) {

            if ( objects[i] == null )
                continue;

            Class type = types[i];

            if ( canaryType.equals(type) ) {

                if ( type.equals(String.class) ) {
                    String value = (String)objects[i];
                    if ( value != null && value.contains(currentCanary) ) {
                        AgentLogger.info("Reporting canary: found '" + value + "' which matches canary '" + currentCanary + "'");
                        return true;
                    }
                } else if ( type.equals(Short.class) ) {
                    Short value = (Short)objects[i];
                    if ( Short.valueOf(currentCanary).equals(value)) {
                        return true;
                    }
                } else if ( type.equals(Integer.class) ) {
                    Integer value = (Integer)objects[i];
                    if ( Integer.valueOf(currentCanary).equals(value)) {
                        return true;
                    }
                } else if ( type.equals(Long.class) ) {
                    Long value = (Long)objects[i];
                    if ( Long.valueOf(currentCanary).equals(value)) {
                        return true;
                    }
                } else if ( type.equals(Double.class) ) {
                    Double value = (Double)objects[i];
                    if ( Double.valueOf(currentCanary).equals(value)) {
                        return true;
                    }
                } else if ( type.equals(Float.class) ) {
                    Float value = (Float)objects[i];
                    if ( Float.valueOf(currentCanary).equals(value)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
