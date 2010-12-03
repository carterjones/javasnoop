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
import com.aspect.snoop.agent.manager.InstrumentationException;
import com.aspect.snoop.agent.manager.InstrumentationManager;
import com.aspect.snoop.agent.manager.MethodChanges;
import com.aspect.snoop.agent.manager.UniqueMethod;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CanaryUtil {

    //private static Logger logger = Logger.getLogger(CanaryUtil.class);
    public static String getChirp(String canaryType, String className, String methodName, String returnType) {

        StringBuilder sb = new StringBuilder();

        String nl = System.getProperty("line.separator");

        //sb.append("  javassist.runtime.Desc.useContextClassLoader = true;" + nl);

        sb.append("  com.aspect.snoop.agent.AgentToSnoopClient.currentClient().canaryChirp(");
        sb.append(canaryType);
        sb.append(".class, \"");
        sb.append(className);
        sb.append("\", \"");
        sb.append(methodName);
        sb.append("\", $sig, $args, \"");
        sb.append(returnType);
        sb.append("\");");
        sb.append(nl);
        //sb.append("  javassist.runtime.Desc.useContextClassLoader = false;" + nl);

        return sb.toString();
    }

    public static void applyCanaries(InstrumentationManager manager, String type, String canary, String pkg) throws InstrumentationException {

        String canaryType = null;

        if ("String".equals(type)) {
            canaryType = "java.lang.String";
        } else if ("short".equals(type)) {
            canaryType = "java.lang.Short";
        } else if ("int".equals(type)) {
            canaryType = "java.lang.Integer";
        } else if ("long".equals(type)) {
            canaryType = "java.lang.Long";
        } else if ("double".equals(type)) {
            canaryType = "java.lang.Double";
        } else if ("float".equals(type)) {
            canaryType = "java.lang.Float";
        }

        if (canary == null) {
            throw new InstrumentationException("Can't apply canary to unknown type: " + type);
        }

        manager.resetAllClasses();

        List<Class> loadedClasses = manager.getLoadedClasses();

        AgentLogger.debug("Applying canaries to " + loadedClasses.size() + " classes...");
        int clsCount = 0;
        int mtdCount = 0;

        for (Class c : loadedClasses) {

            if (c == null || c.isInterface() || c.isArray()) {
                continue;
            }

            String clsName = c.getName();

            if (pkg != null && pkg.length() > 0 && ! clsName.startsWith(pkg)) {
                continue;
            }

            // FIXME: run into errors here on applets. should work.
            if (ClasspathUtil.isJavaOrSunClass(clsName) || ClasspathUtil.isJavaSnoopClass(clsName)) {
                continue;
            }

            List<MethodChanges> classChanges = new ArrayList<MethodChanges>();

            Method[] methods = null;

            try {
                methods = c.getDeclaredMethods();
            } catch (Throwable t) {
                // sometimes we'll get NoClassDefFoundErrors here if the
                // methods refere to a class that hasn't been loaded by the
                // target process (and therefore definitely not visible here
                // in our world).
                continue;
            }

            Constructor[] constructors = null;

            try {
                constructors = c.getDeclaredConstructors();
            } catch (Throwable t) {
                AgentLogger.trace("Failed to canary " + c.getName() + ": " + t.getMessage());
            }

            List<Member> members = new ArrayList<Member>();

            if (methods != null) {
                members.addAll(Arrays.asList(methods));
            }

            if (constructors != null) {
                members.addAll(Arrays.asList(constructors));
            }

            for (Member m : members) {

                if (Modifier.isAbstract(m.getModifiers())) {
                    continue;
                }

                Class[] types = null;

                if (m instanceof Constructor) {
                    types = ((Constructor) m).getParameterTypes();
                } else {
                    types = ((Method) m).getParameterTypes();
                }

                for (Class paramType : types) {

                    boolean match = paramType.getName().equals(canaryType);

                    if (match) {

                        try {

                            UniqueMethod method = null;

                            if (m instanceof Constructor) {
                                method = new UniqueMethod((Constructor) m);
                            } else {
                                method = new UniqueMethod((Method) m);
                            }

                            MethodChanges change = new MethodChanges(method);

                            change.setNewStartSrc(getChirp(canaryType, clsName, m.getName(),method.getReturnTypeName()));

                            //System.out.println("Applying canary to " + method.toString());
                            classChanges.add(change);
                            mtdCount++;

                        } catch (ClassNotFoundException ex) {
                            AgentLogger.trace("Couldn't apply canary to " + c.getName() + "." + m.getName() + ": " + ex.getMessage());
                        } catch (NoClassDefFoundError ex) {
                            AgentLogger.trace("Couldn't apply canary to " + c.getName() + "." + m.getName() + ": " + ex.getMessage());
                        }
                    }
                }
            }

            try {
                manager.instrument(c, classChanges.toArray(new MethodChanges[]{}));
                clsCount++;
            } catch (InstrumentationException ex) {
                AgentLogger.trace("Failed to apply canary to " + clsName + ": " + ex.getMessage());
            }

        }

        AgentLogger.info("Successfully canaried " + clsCount + " classes and " + mtdCount + " methods.");
    }
}
