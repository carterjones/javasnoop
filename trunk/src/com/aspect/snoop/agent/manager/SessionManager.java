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

import com.aspect.snoop.FunctionHook;
import com.aspect.snoop.SnoopSession;
import com.aspect.snoop.agent.AgentLogger;
import com.aspect.snoop.agent.SnoopAgent;
import com.aspect.snoop.util.ReflectionUtil;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SessionManager {

    public static void recycleHooks(SnoopSession session) throws InstrumentationException {
        uninstallHooks(session);
        installHooks(session);
    }

    /**
     * This function is the entry point into the instrumentation of the
     * running program. It turns on the rules we instrument.
     */
    public static void uninstallHooks(SnoopSession snoopSession) throws InstrumentationException {

        if ( snoopSession == null ) {
            return;
        }

        InstrumentationManager manager = SnoopAgent.getAgentManager();

        for (FunctionHook hook : snoopSession.getFunctionHooks()) {

            Class clazz = hook.getClazz();

            /*if ( ! manager.hasClassBeenModified(clazz) ) {
                continue;
            }*/

            if ( ReflectionUtil.isInterfaceOrAbstract(clazz) || hook.isAppliedToSubtypes() ) {

                Class[] subtypes = getAllSubtypes(clazz);

                for (Class c : subtypes ) {
                    if ( manager.hasClassBeenModified(c) ) {
                        manager.deinstrument(c);
                    }
                }

            }

            try {
                if ( manager.hasClassBeenModified(clazz) ) {
                    manager.deinstrument(clazz);
                } else {
                    AgentLogger.debug("Not de-instrumenting " + clazz.getName() + " because it has no history");
                }
            } catch(InstrumentationException e) {
                AgentLogger.error("Problem de-instrumenting class", e);
            }
        }
    }

    public static void installHooks(SnoopSession snoopSession) throws InstrumentationException {

        HashMap<Class, ClassChanges> classChanges = new HashMap<Class,ClassChanges>();

        InstrumentationManager manager = SnoopAgent.getAgentManager();

        for(FunctionHook hook : snoopSession.getFunctionHooks() ) {

            if ( ! hook.isEnabled() ) {
                continue;
            }

            String methodName = hook.getMethodName();
            Class[] parameterTypes = hook.getParameterTypes();

            Class clazz = hook.getClazz();

            // if it applies to all subtypes, only do it to the subtypes
            if ( ReflectionUtil.isInterfaceOrAbstract(clazz) || hook.isAppliedToSubtypes() ) {
                Class[] subtypes = getAllSubtypes(clazz);

                for (Class c : subtypes ) {

                    if ( hasMethod(c, methodName, parameterTypes) ) {
                        ClassChanges change = classChanges.get(c);

                        if (change == null) {
                            change = new ClassChanges(c);
                            classChanges.put(c, change);
                        }

                        change.registerHook(hook, manager);
                    }

                }

            }

            if ( ! clazz.isInterface() ) {

                if ( ! Modifier.isAbstract(clazz.getModifiers()) || hasMethod(clazz,methodName,parameterTypes)) {
                    ClassChanges change = classChanges.get(clazz);

                    if (change == null) {
                        change = new ClassChanges(clazz);
                        classChanges.put(clazz, change);
                    }

                    change.registerHook(hook, manager);
                }

            }

        }

        for ( Class clazz : classChanges.keySet() ) {

            try {

                ClassChanges change = classChanges.get(clazz);

                manager.instrument(
                        clazz,
                        change.getAllMethodChanges());

            } catch (InstrumentationException ex) {
                throw ex;
            }

        }
    }

    private static Class[] getAllSubtypes(Class clazz) {
        List<Class> subtypes = new ArrayList<Class>();

        for(Class c : SnoopAgent.getAgentManager().getLoadedClasses() ) {
            if ( clazz.isAssignableFrom(c) && ! c.isInterface() && ! c.equals(clazz)) {
                subtypes.add(c);
            }
        }

        return subtypes.toArray( new Class[]{} );
    }

    private static boolean hasMethod(Class c, String name, Class[] params) {

        try {
            Method m = c.getDeclaredMethod(name, params);
            return !Modifier.isAbstract(m.getModifiers());
        } catch(Exception e) { }
        return false;
    }
}
