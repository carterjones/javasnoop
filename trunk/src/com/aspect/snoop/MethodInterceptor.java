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

package com.aspect.snoop;

import java.io.Serializable;
import java.util.List;

public abstract class MethodInterceptor implements Serializable {

    /**
     * @return the paramTypes
     */
    public String[] getParameterTypes() {
        return parameterTypes;
    }

    /**
     * @param parameterTypes the parameterTypes to set
     */
    public void setParameterTypes(String[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
 
    public enum Mode {
        AlwaysIntercept,
        InterceptIf,
        DontInterceptIf
    };

    private Mode mode;
    private boolean enabled;
    protected List<Condition> conditions;
    private String className;
    private String methodName;
    private String[] parameterTypes;
    private String returnType;
    private boolean applyToSubtypes;

    // needed for serialization
    public MethodInterceptor() { }

    public MethodInterceptor(Mode mode, boolean enabled, String className, String methodName, String[] parameterTypes, String returnType, boolean applyToSubTypes, List<Condition> conditions) {
        this.mode = mode;
        this.enabled = enabled;
        this.conditions = conditions;
        this.className = className;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
        this.applyToSubtypes = applyToSubTypes;
    }

    

    /**
     * @return the mode
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    /**
     * @return whether or not this interception is currently enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled whether this interception should be enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the conditions for interception
     */
    public List<Condition> getConditions() {
        return conditions;
    }

    /**
     * @param conditions the conditions to set for interception
     */
    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    /**
     * @return the class name pattern to determine if should be intercepted
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className the class name pattern to determine if should be intercepted
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return the methodName
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * @param methodName the methodName to set
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * @return the applyToSubtypes
     */
    public boolean isAppliedToSubtypes() {
        return applyToSubtypes;
    }

    /**
     * @param applyToSubtypes the applyToSubtypes to set
     */
    public void setApplyToSubtypes(boolean applyToSubtypes) {
        this.applyToSubtypes = applyToSubtypes;
    }

    public boolean isConstructor() {
        if (methodName == null) {
            return true;
        }
        return methodName.equals("<init>") || methodName.length() == 0;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

}
