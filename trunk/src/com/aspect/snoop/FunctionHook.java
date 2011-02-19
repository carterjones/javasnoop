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

import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the base class for a function hook.
 *
 * @author adabirsiaghi
 */
public class FunctionHook {

    private boolean shouldPause;

    private boolean shouldRunScript;
    private String endScript;
    private String startScript;

    private boolean shouldTamperParameters;
    private boolean shouldTamperReturnValue;
    
    private boolean shouldPrintParameters;
    private boolean shouldPrintStackTrace;
    private boolean isOutputToConsole;
    private boolean isOutputToFile;
    private String outputFile;

    private int id;

    public FunctionHook(AccessibleObject m) {

        MethodWrapper wrapper = MethodWrapper.getWrapper(m);
        this.methodName = wrapper.getName();
        this.clazz = wrapper.getDeclaringClass();
        this.parameterTypes = wrapper.getParameterTypes();
        this.returnType = wrapper.getReturnType();
        this.applyToSubtypes = true;
        
        this.mode = Mode.AlwaysIntercept;
        this.outputFile = "";
        this.conditions = new ArrayList<Condition>();

        this.shouldPrintParameters = true;
        this.shouldPrintStackTrace = false;
        this.isOutputToConsole = false;
        this.isOutputToFile = false;
        this.shouldTamperParameters = false;
        this.shouldTamperReturnValue = false;
        this.startScript = "";
        this.endScript = "";
        this.id = hashCode();
    }

    public boolean shouldPause() {
        return shouldPause;
    }

    public boolean shouldRunScript() {
        return shouldRunScript;
    }

    public boolean shouldTamperParameters() {
        return shouldTamperParameters;
    }

    public boolean shouldTamperReturnValue() {
        return shouldTamperReturnValue;
    }

    public boolean shouldPrintParameters() {
        return shouldPrintParameters;
    }

    public boolean shouldPrintStackTrace() {
        return shouldPrintStackTrace;
    }

    public boolean isOutputToConsole() {
        return isOutputToConsole;
    }

    public boolean isOutputToFile() {
        return isOutputToFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    //needed for serialization
    public FunctionHook() {
        super();
        this.id = hashCode();
    }

    public FunctionHook(boolean shouldTamperParameters, boolean shouldTamperReturnValue, boolean shouldRunScript,
            String startScript, String endScript, boolean shouldPause, boolean enabled, Class clazz,
            String methodName, Class[] parameterTypes, Class returnType, boolean applyToSubTypes, Mode mode,
            boolean shouldPrintParameters,  boolean shouldPrintStackTrace, boolean isOutputToConsole,
            boolean isOutputToFile, String outputFile, List<Condition> conditions) {

        this.id = hashCode();

        this.enabled = enabled;

        this.mode = mode;
        this.startScript = startScript; // won't always be needed, but no harm
        this.endScript = endScript;
        
        this.isOutputToConsole = isOutputToConsole;
        this.isOutputToFile = isOutputToFile;
        this.outputFile = outputFile;

        this.shouldPrintParameters = shouldPrintParameters;
        this.shouldPrintStackTrace = shouldPrintStackTrace;
        
        this.shouldPause = shouldPause;
        this.shouldRunScript = shouldRunScript;
        this.shouldTamperParameters = shouldTamperParameters;
        this.shouldTamperReturnValue = shouldTamperReturnValue;

        this.clazz = clazz;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;

        this.applyToSubtypes = applyToSubTypes;

        this.conditions = conditions;
    }

    public void removeCondition(Condition c) {
        conditions.remove(c);
    }

    public void addCondition(Condition condition) {
        conditions.add(condition);
    }

    public void setOutputToConsole(boolean b) {
        this.isOutputToConsole = b;
    }
    
    public void setOutputToFile(boolean b) {
        this.isOutputToFile = b;
    }

    public void setOutputFile(String s) {
        this.outputFile = s;
    }

    public void setShouldTamperParameters(boolean b) {
        this.shouldTamperParameters = b;
    }

    public void setShouldTamperReturnValue(boolean b) {
        this.shouldTamperReturnValue = b;
    }

    public void setShouldPause(boolean b) {
        this.shouldPause = b;
    }

    public void setShouldRunScript(boolean b) {
        this.shouldRunScript = b;
    }

    public void setShouldPrintStackTrace(boolean b) {
        this.shouldPrintStackTrace = b;
    }

    public void setShouldPrintParameters(boolean b) {
        this.shouldPrintParameters = b;
    }

    public int getId() {
        return id;
    }

    public String getStartScript() {
        return startScript;
    }
    
    public String getEndScript() {
        return endScript;
    }

    public void setStartScript(String startScript) {
        this.startScript = startScript;
    }

    public void setEndScript(String endScript) {
        this.endScript = endScript;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    /**
     * @param parameterTypes the parameterTypes to set
     */
    public void setParameterTypes(Class[] parameterTypes) {
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
    private Class clazz;
    private String methodName;
    private Class[] parameterTypes;
    private Class returnType;
    private boolean applyToSubtypes;

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
    public Class getClazz() {
        return clazz;
    }

    /**
     * @param className the class name pattern to determine if should be intercepted
     */
    public void setClazz(Class clazz) {
        this.clazz = clazz;
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

    public Class getReturnType() {
        return returnType;
    }

    public void setReturnType(Class returnType) {
        this.returnType = returnType;
    }

}
