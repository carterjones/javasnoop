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

import com.aspect.snoop.FunctionHook;
import com.aspect.snoop.MethodWrapper;
import com.aspect.snoop.agent.AgentLogger;
import com.aspect.snoop.agent.manager.InstrumentationManager;
import com.aspect.snoop.agent.manager.LocalVariable;
import com.aspect.snoop.agent.manager.MethodChanges;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class Hook2JavaUtil {

    public static MethodChanges hook2Java(FunctionHook hook, InstrumentationManager manager) {

        StringBuilder javaCode = new StringBuilder();
        List<LocalVariable> vars = new ArrayList<LocalVariable>();
        int id = hook.getId();

        String nl = System.getProperty("line.separator");

        javaCode.append(nl);

        if (hook.shouldPrintParameters()) {
            javaCode.append("  com.aspect.snoop.agent.SnoopAgent.getMainView().printParameters(\"" + hook.getClazz().getName() + "\", " + id + ", $args, $sig);");
            javaCode.append(nl);
        }

        if (hook.shouldPrintStackTrace()) {
            javaCode.append("  com.aspect.snoop.agent.SnoopAgent.getMainView().printStackTrace(\"" + hook.getClazz().getName() + "\", " + id + ", $args, $sig);");
            javaCode.append(nl);
        }

        if (hook.shouldPause()) {
            javaCode.append("  com.aspect.snoop.agent.SnoopAgent.getMainView().pause(\"" + hook.getClazz().getName() + "\", " + id + ", $args, $sig);");
            javaCode.append(nl);
        }

        if (hook.shouldTamperParameters()) {

            try {

                // need to add a new local variable "mods" before this line will work
                Object[] o = new Object[]{};
                ClassPool cp = ClassPool.getDefault();

                cp.appendClassPath( new ClassClassPath(o.getClass()) );
                CtClass type = cp.get(o.getClass().getName());

                vars.add(new LocalVariable("mods", type));

                javaCode.append("  mods = com.aspect.snoop.agent.SnoopAgent.getMainView().tamperWithParameters(\"" + hook.getClazz().getName() + "\", " + id + ", $args, $sig);");
                javaCode.append(nl);

                int argLength = hook.getParameterTypes().length;

                for (int i = 0; i < argLength; i++) {

                    String argType = hook.getParameterTypes()[i].getName();
                    Unwrapper w = unwrappers.get(argType);

                    if ( w == null ) {
                        argType = getCastString(argType);
                    }

                    String line;

                    if ( w == null ) {
                        line = "  $" + (i + 1) + " = (" + argType + ")mods[" + i + "];";
                    } else {
                        line = "  $" + (i + 1) + " = " + w.prefix + "mods[" + i + "]"  + w.suffix + ";";
                    }

                    line += nl;

                    javaCode.append(line);

                }

            } catch (NotFoundException ex) {
                //logger.error(ex);
            }
        }

        MethodWrapper method = MethodWrapper.getWrapper(hook.getClazz(), hook.getMethodName(), hook.getParameterTypes());

        MethodChanges changes = new MethodChanges(method.getActualMethod());
        changes.initialize(vars.toArray(new LocalVariable[vars.size()]), javaCode.toString(), "");

        if (hook.shouldRunScript()) {
            changes.setNewStartSrc( changes.getNewStartSrc() + hook.getStartScript());
            changes.setNewEndSrc( changes.getNewEndSrc() + hook.getEndScript());
        }

        if ( hook.shouldTamperReturnValue() ) {

            String returnType = hook.getReturnType().getName();

            Unwrapper w = unwrappers.get(returnType);

            if ( w == null ) {
                returnType = getCastString(returnType);
            }

            String line = null;

            if ( w == null ) {
               line = "  $_ = (" + returnType + ")com.aspect.snoop.agent.SnoopAgent.getMainView().tamperWithReturnValue(\"" + hook.getClazz().getName() + "\", " + id + ", $args, $sig, $_, $type);";
            } else {
               line = "  $_ = " + w.prefix + "com.aspect.snoop.agent.SnoopAgent.getMainView().tamperWithReturnValue(\"" + hook.getClazz().getName() + "\", " + id + ", $args, $sig, com.aspect.snoop.util.ReflectionUtil.getObjectFrom($_), $type)" + w.suffix + ";";
            }

            changes.setNewEndSrc( changes.getNewEndSrc() + nl + line );
        }

        AgentLogger.debug("START: " + changes.getNewStartSrc());
        AgentLogger.debug("END: " + changes.getNewEndSrc());

        return changes;
    }

    public static String getCastString(String argType) {
        
        String toReturn = argType;

        if ( ReflectionUtil.primitiveArrayMap.containsKey(argType)) {
            toReturn = ReflectionUtil.primitiveArrayMap.get(argType);
        } else if ( argType.startsWith("[L") ) {
            int len = argType.length() - 1;
            toReturn = argType.substring(2,len) + "[]";
        }
        return toReturn;
    }
    
    public static HashMap<String,Unwrapper> unwrappers = new HashMap<String,Unwrapper>();
    
    static {
        unwrappers.put( "boolean", new Unwrapper("((Boolean)",").booleanValue()") );
        unwrappers.put( "byte", new Unwrapper("((Byte)",").byteValue()") );
        unwrappers.put( "char", new Unwrapper("((Character)",").charValue()") );
        unwrappers.put( "short", new Unwrapper("((Short)",").shortValue()") );
        unwrappers.put( "int", new Unwrapper("((Integer)",").intValue()") );
        unwrappers.put( "float", new Unwrapper("((Float)",").floatValue()") );
        unwrappers.put( "long", new Unwrapper("((Long)",").longValue()") );
        unwrappers.put( "double", new Unwrapper("((Double)",").doubleValue()") );
    }
}


class Unwrapper {
    String prefix;
    String suffix;
    public Unwrapper(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }
}
