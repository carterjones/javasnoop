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

import com.aspect.snoop.agent.manager.UniqueMethod;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FunctionHookInterceptor extends MethodInterceptor implements Serializable, Cloneable {

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

    public FunctionHookInterceptor(UniqueMethod m) {
        super(Mode.AlwaysIntercept,false,m.getParentClassName(),m.getName(), m.getParameterTypes(), m.getReturnTypeName(), true, new ArrayList<Condition>());
        setOutputToConsole(false);
        setOutputToFile(false);
        setShouldTamperParameters(false);
        setShouldTamperReturnValue(false);
        setStartScript("");
        setEndScript("");
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
        return shouldPrintStackTrace;
    }

    public boolean shouldPrintStackTrace() {
        return shouldPrintParameters;
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
    public FunctionHookInterceptor() { 
        super();
        this.id = hashCode();
    }

    public FunctionHookInterceptor(boolean shouldTamperParameters, boolean shouldTamperReturnValue, boolean shouldRunScript,
            String startScript, String endScript, boolean shouldPause, boolean enabled, String className,
            String methodName, String[] parameterTypes, String returnType, boolean applyToSubTypes, Mode mode,
            boolean shouldPrintParameters,  boolean shouldPrintStackTrace, boolean isOutputToConsole,
            boolean isOutputToFile, String outputFile, List<Condition> conditions) {

        super(mode, enabled, className, methodName, parameterTypes, returnType, applyToSubTypes, conditions);

        this.id = hashCode();

        this.startScript = startScript; // won't always be needed, but no harm
        this.endScript = endScript;
        
        this.isOutputToConsole = isOutputToConsole;
        this.isOutputToFile = isOutputToFile;
        this.outputFile = outputFile;

        this.shouldPrintParameters = shouldPrintParameters;
        this.shouldPrintParameters = shouldPrintStackTrace;
        
        this.shouldPause = shouldPause;
        this.shouldRunScript = shouldRunScript;
        this.shouldTamperParameters = shouldTamperParameters;
        this.shouldTamperReturnValue = shouldTamperReturnValue;
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

}
