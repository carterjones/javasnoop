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
package com.aspect.snoop.agent;

import com.aspect.snoop.FunctionHook;
import com.aspect.snoop.SnoopSession;
import com.aspect.snoop.agent.manager.ClassChanges;
import com.aspect.snoop.agent.manager.MethodChanges;
import com.aspect.snoop.agent.manager.UniqueMethod;
import com.aspect.snoop.util.ClasspathUtil;
import com.aspect.snoop.util.Hook2JavaUtil;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

/**
 *
 * This ClassFileTransformer is used for "Start and Snoop" actions to allow
 * the transformation of classes loaded too early in the application lifecycle
 * to be hooked like normal.
 *
 * For instance, if an application sends a "Hey, I just started up!" ping to a
 * server on startup and you wanted to catch that event, it'd be basically
 * impossible using "Attach and Snoop", since it happens immediately upon
 * process startup.
 *
 * Now, the ClassTransformer will catch all classes as they are first loaded
 * and instruments them according to the current session to the best of its
 * knowledge.  If the Transformer detects a class is not being loaded for the
 * first time, it will ignore it. Again, the only classes we care about are
 * non-JavaSnoop classes that are loaded for the first time.
 *
 * @author adabirsiaghi
 */
public class AgentClassTransfomer implements ClassFileTransformer {

    private SnoopSession session;

    /*
     * This class isn't in use yet. For early events, it will transform
     * early-loaded classes for the user.
     */
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classBytes) throws IllegalClassFormatException {

        className = className.replace('/', '.');

        /*
         * This ClassFileTransformer is only used for early-loaded classes
         * that the used won't be able to hook through the UI fast enough.
         *
         * If it's being redefined, that means it's not one of those classes,
         * or the user can handle changing it through the UI, and we don't
         * transform again.
         */
        if ( classBeingRedefined != null ) {
            return null;
        }

        /*
         * Don't try to redefine any JavaSnoop classes. Things could get messy.
         */
        if ( ClasspathUtil.isJavaSnoopClass(className) ) {
            return null;
        }

        /*
         * Based on the session, decide if this class needs to be instrumented.
         */

        ClassPool cp = ClassPool.getDefault();
        CtClass cls = null;

        Map<UniqueMethod,MethodChanges> methodChanges =
                new HashMap<UniqueMethod,MethodChanges>();

        for(FunctionHook hook : session.getFunctionHooks()) {

            if ( ! hook.isEnabled() ) {
                continue;
            }

            if ( hook.getClassName().equals(className) ) {
                /*
                 * Ok, this class is going to need to be instrumtend. First
                 * thing to do is get a CtClass object for it. We lazy load it
                 * here instead of above the loop because its expensive and
                 * there could be tens of thousands of classes in the JVM.
                 */
                if ( cls == null ) {
                    try {
                        cls = createCtClass(className, classBytes);
                    // this exception really shouldn't happen. (gulp)
                    } catch (NotFoundException ex) { }
                }

                /*
                 * Get all the methods that apply directly to this class (there
                 * is no inheritance or abstraction considered here).
                 */
                UniqueMethod m = new UniqueMethod(
                        hook.getClassName(),
                        hook.getMethodName(),
                        hook.getParameterTypes());

                MethodChanges changes = methodChanges.get(m);

                if ( changes == null ) {
                    // apparently we haven't had any previous changes
                    // to this method yet, so create a new changelist
                    // based on this hook's requirements
                    changes = Hook2JavaUtil.hook2Java(hook, null);
                    methodChanges.put(m, changes);
                } else {
                    // have to add the new changes to the old changes
                    MethodChanges newChanges = Hook2JavaUtil.hook2Java(hook, null);
                    changes.appendStartSrc(newChanges.getNewStartSrc());
                    changes.appendEndSrc(newChanges.getNewEndSrc());
                    changes.addLocalVariables(newChanges.getNewLocalVariables());
                }

            }

        }

        return null;
    }

    private CtClass createCtClass(String className, byte[] classBytes) throws NotFoundException {
        ClassPool cp = ClassPool.getDefault();
        CtClass cls = null;

        ByteArrayClassPath bacp = new ByteArrayClassPath(
            className,
            classBytes);
        
        cp.insertClassPath(bacp);

        return cp.get(className);
    }

}
