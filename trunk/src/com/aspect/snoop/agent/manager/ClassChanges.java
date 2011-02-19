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
import com.aspect.snoop.MethodWrapper;
import com.aspect.snoop.util.Hook2JavaUtil;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClassChanges {

    String className;
    Class clazz;
    HashMap<MethodWrapper, MethodChanges> methodChanges;

    public ClassChanges(Class clazz) {
        this.clazz = clazz;
        this.methodChanges = new HashMap<MethodWrapper,MethodChanges>();
    }

    public MethodChanges[] getAllMethodChanges() {
        
        List<MethodChanges> changes = new ArrayList<MethodChanges>();
        
        for ( MethodWrapper method : methodChanges.keySet() ) {
            changes.add( methodChanges.get(method) );
        }
        
        return changes.toArray( new MethodChanges[]{} );
    }

    public MethodChanges getMethodChanges(Member m) {
        MethodWrapper method = MethodWrapper.getWrapper(m);
        return methodChanges.get(method);
    }

    public MethodChanges getMethodChanges(AccessibleObject m) {
        MethodWrapper method = MethodWrapper.getWrapper(m);
        return methodChanges.get(method);
    }

    public void registerHook(FunctionHook hook, InstrumentationManager manager) {
        
        MethodWrapper method = MethodWrapper.getWrapper(hook.getClazz(),hook.getMethodName(),hook.getParameterTypes());

        // #1: get the method changes we already have for this method
        MethodChanges changes = methodChanges.get(method);

        if ( changes == null ) {
            changes = new MethodChanges(method.getActualMethod());
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
