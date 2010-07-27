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
import com.aspect.snoop.util.Hook2JavaUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClassChanges {

    String className;
    Class clazz;
    HashMap<UniqueMethod, MethodChanges> methodChanges;

    public ClassChanges(Class clazz) {
        this.clazz = clazz;
        this.methodChanges = new HashMap<UniqueMethod,MethodChanges>();
    }

    public MethodChanges[] getAllMethodChanges() {
        
        List<MethodChanges> changes = new ArrayList<MethodChanges>();
        
        for ( UniqueMethod method : methodChanges.keySet() ) {
            changes.add( methodChanges.get(method) );
        }
        
        return changes.toArray( new MethodChanges[]{} );
    }

    public MethodChanges getMethodChanges(String methodName, String[] parameterTypes, String returnType) {

        UniqueMethod method = new UniqueMethod(clazz, methodName, parameterTypes, returnType);
        
        return methodChanges.get(method);
    }

    public void registerHook(FunctionHook hook, InstrumentationManager manager) {
        
        UniqueMethod method =
                new UniqueMethod(
                    clazz,
                    hook.getMethodName(),
                    hook.getParameterTypes(),
                    hook.getReturnType());

        // #1: get the method changes we already have for this method
        MethodChanges changes = methodChanges.get(method);

        if ( changes == null ) {
            changes = new MethodChanges(method);
            methodChanges.put(method, changes);
        }

        // #2: get the new changes we're going to need for this hook
        MethodChanges newChanges = Hook2JavaUtil.hook2Java(hook, manager);

        // #3: add the new changes to the old changes
        changes.appendStartSrc(newChanges.getNewStartSrc());
        changes.appendEndSrc(newChanges.getNewEndSrc());
        changes.addLocalVariables(newChanges.getNewLocalVariables());

    }
 
}
