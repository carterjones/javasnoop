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

import com.aspect.snoop.Condition;
import com.aspect.snoop.FunctionHook;
import com.aspect.snoop.SnoopSession;
import com.aspect.snoop.agent.SnoopAgent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

public class SessionPersistenceUtil {

    public static void saveSession(SnoopSession session) throws FileNotFoundException, IOException {
        if ( session.alreadyBeenSaved() ) {
            saveSession(session, session.getSnoopSessionFilename());
        }
    }

    public static void saveSession(SnoopSession session, String filename) throws FileNotFoundException, IOException {

        Element sessionRoot = new Element("session");
        Document doc = new Document(sessionRoot);

        sessionRoot.addAttribute( new Attribute ("mainClass", session.getMainClass()) );
        sessionRoot.addAttribute( new Attribute ("javaArgs", session.getJavaArguments()) );
        sessionRoot.addAttribute( new Attribute ("progArgs", session.getArguments()) );
        sessionRoot.addAttribute( new Attribute ("classpath", session.getClasspathString()) );
        sessionRoot.addAttribute( new Attribute ("workingDir", session.getWorkingDir()) );
        
        // Add a <hooks> node

        Element hooksRoot = new Element("hooks");

        // Add all the children <hook> elements

        for(FunctionHook hook : session.getFunctionHooks() ) {

            Element hookRoot = new Element("hook");

            hookRoot.addAttribute ( new Attribute("enabled", Boolean.toString(hook.isEnabled()) ));
            hookRoot.addAttribute ( new Attribute("class", hook.getClazz().getName()) );
            hookRoot.addAttribute ( new Attribute("method", hook.getMethodName() ));

            hookRoot.addAttribute ( new Attribute("shouldInherit", Boolean.toString(hook.isAppliedToSubtypes())));

            String allParamTypes = StringUtil.join(hook.getParameterTypes(), ",");

            hookRoot.addAttribute ( new Attribute("params", allParamTypes ) );
            hookRoot.addAttribute ( new Attribute("returnType", hook.getReturnType().getName()));
            
            hookRoot.addAttribute ( new Attribute("shouldTamperParameters", Boolean.toString(hook.shouldTamperParameters())));
            hookRoot.addAttribute ( new Attribute("shouldTamperReturnValue", Boolean.toString(hook.shouldTamperReturnValue())));

            hookRoot.addAttribute ( new Attribute("shouldRunScript", Boolean.toString(hook.shouldRunScript())));
            hookRoot.addAttribute ( new Attribute("startScript", hook.getStartScript()) );
            hookRoot.addAttribute ( new Attribute("endScript", hook.getEndScript()) );

            hookRoot.addAttribute ( new Attribute("shouldPause", Boolean.toString(hook.shouldPause())) );

            hookRoot.addAttribute( new Attribute("shouldPrintParameters", Boolean.toString(hook.shouldPrintParameters())) );
            hookRoot.addAttribute( new Attribute("shouldPrintStackTrace", Boolean.toString(hook.shouldPrintStackTrace())) );
            
            hookRoot.addAttribute( new Attribute("outputToConsole", Boolean.toString(hook.isOutputToConsole())) );
            hookRoot.addAttribute( new Attribute("outputToFile", Boolean.toString(hook.isOutputToFile())) );
            hookRoot.addAttribute( new Attribute("outputFile", hook.getOutputFile()));

            hookRoot.addAttribute( new Attribute("interceptCondition", hook.getMode().name() ) );
            
            // Add a <conditions> node
            Element conditionsRoot = new Element("conditions");

            for(Condition c : hook.getConditions() ) {
                Element condition = new Element("condition");
                condition.addAttribute( new Attribute ("enabled", Boolean.toString(c.isEnabled()) ) );
                condition.addAttribute( new Attribute ("parameter", String.valueOf(c.getParameter()) ) );
                condition.addAttribute( new Attribute ("operator", c.getOperator().name()));
                condition.addAttribute( new Attribute ("operand", c.getOperand()));
                
                conditionsRoot.appendChild(condition);
            }

            hookRoot.appendChild(conditionsRoot);
            hooksRoot.appendChild(hookRoot);
        }

        sessionRoot.appendChild(hooksRoot);

        Element output = new Element("output");
        output.appendChild(session.getOutput());

        sessionRoot.appendChild(output);
        
        /*
         * Now that we're done, we write out to the filename in the
         * method parameter.
         */
        FileOutputStream fos = new FileOutputStream(new File(filename));

        fos.write(doc.toXML().getBytes());

        session.setSnoopSessionFilename(filename);
        session.markAsSaved();
    }

    public static SnoopSession loadSession(String filename) throws FileNotFoundException, IOException {
    	return loadSession(new File(filename));
    }

    public static SnoopSession loadSession(File file) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(file);
        SnoopSession session = loadSession(fis);
        session.markAsSaved();
        session.setSnoopSessionFilename(file.getAbsolutePath());
        return session;
    }

    public static SnoopSession loadSession(Reader reader) throws IOException {
        
        try {

            Builder parser = new Builder();
            Document doc = parser.build(reader);
            return loadSession(doc);

        } catch (ParsingException ex) {
            Logger.getLogger(SessionPersistenceUtil.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex);
        }
    }

    public static SnoopSession loadSession(InputStream is) throws IOException {

        try {

            Builder parser = new Builder();
            Document doc = parser.build(is);
            return loadSession(doc);

        } catch (ParsingException ex) {
            Logger.getLogger(SessionPersistenceUtil.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex);
        }

    }

    private static SnoopSession loadSession(Document doc) throws IOException {

	Element root = doc.getRootElement();

        SnoopSession session = new SnoopSession();

        session.setMainClass(root.getAttributeValue("mainClass"));
        session.setJavaArguments(root.getAttributeValue("javaArgs"));
        session.setArguments(root.getAttributeValue("progArgs"));
        session.setClasspathString(root.getAttributeValue("classpath"));
        session.setWorkingDir(root.getAttributeValue("workingDir"));

        List<FunctionHook> hooks = new ArrayList<FunctionHook>();

        Element hooksRoot = root.getFirstChildElement("hooks");

        for ( int i=0; i< hooksRoot.getChildElements("hook").size(); i++ ) {

            Element hookRoot = hooksRoot.getChildElements("hook").get(i);

            boolean enabled = "true".equals(hookRoot.getAttributeValue("enabled"));
            String clazz  = hookRoot.getAttributeValue("class");
            boolean applyToSubTypes = "true".equals(hookRoot.getAttributeValue("shouldInherit"));
            String method = hookRoot.getAttributeValue("method");
            String params = hookRoot.getAttributeValue("params");
            String returnType = hookRoot.getAttributeValue("returnType");

            String interceptCondition = hookRoot.getAttributeValue("interceptCondition");

            FunctionHook.Mode mode = FunctionHook.Mode.valueOf(interceptCondition);

            boolean shouldTamperParameters = "true".equals(hookRoot.getAttributeValue("shouldTamperParameters"));
            boolean shouldTamperReturnValue = "true".equals(hookRoot.getAttributeValue("shouldTamperReturnValue"));

            boolean shouldRunScript = "true".equals(hookRoot.getAttributeValue("shouldRunScript"));
            boolean shouldPause = "true".equals(hookRoot.getAttributeValue("shouldPause"));

            String startScript = hookRoot.getAttributeValue("startScript");
            String endScript = hookRoot.getAttributeValue("endScript");

            boolean printParameters = "true".equals(hookRoot.getAttributeValue("shouldPrintParameters"));
            boolean printStackTrace = "true".equals(hookRoot.getAttributeValue("shouldPrintStackTrace"));

            boolean isOutputToFile = "true".equals(hookRoot.getAttributeValue("outputToFile"));
            boolean isOutputToConsole = "true".equals(hookRoot.getAttributeValue("outputToConsole"));
            String outputFile = hookRoot.getAttributeValue("outputFile");

            List<Condition> conditions = new ArrayList<Condition>();

            Element conditionRoot = hookRoot.getFirstChildElement("conditions");
            Elements conditionElements = conditionRoot.getChildElements("condition");

            for (int j=0; j < conditionElements.size(); j++ ) {
                Element e = conditionElements.get(j);
                boolean conditionEnabled = "true".equals(e.getAttributeValue("enabled"));
                String operand = e.getAttributeValue("operand");
                int parameter = Integer.parseInt(e.getAttributeValue("parameter"));
                String operatorString = e.getAttributeValue("operator");
                Condition.Operator test = Condition.Operator.valueOf(operatorString);
                
                Condition c = new Condition(
                    conditionEnabled, test, parameter, operand
                );

                conditions.add(c);
            }

            try {
                Class realClass = SnoopAgent.getAgentManager().getFromAllClasses(clazz);
                Class realReturnClass = getClazzFor(returnType);
                Class[] realParams = null;
                if ( params.trim().length() == 0 ) {
                    realParams = new Class[]{};
                } else {
                    String[] sParamType = params.split(",");
                    realParams = new Class[sParamType.length];
                    for(int j=0;j<realParams.length;j++) {
                        realParams[j] = getClazzFor(sParamType[j]);
                    }

                }

                FunctionHook hook = new FunctionHook(
                        shouldTamperParameters,
                        shouldTamperReturnValue,
                        shouldRunScript,
                        startScript,
                        endScript,
                        shouldPause,
                        enabled,
                        realClass,
                        method,
                        realParams,
                        realReturnClass,
                        applyToSubTypes,
                        mode,
                        printParameters,
                        printStackTrace,
                        isOutputToConsole,
                        isOutputToFile,
                        outputFile,
                        conditions);

                 hooks.add(hook);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        session.setFunctionHooks(hooks);

        session.setOutput( root.getChildElements("output").get(0).getValue() );

        return session;
        
    }

    private static Class getClazzFor(String type) throws ClassNotFoundException {
        if ( "boolean".equals(type) ) {
            return boolean.class;
        } else if ( "byte".equals(type) ) {
            return byte.class;
        } else if ( "char".equals(type) ) {
            return char.class;
        } else if ( "short".equals(type) ) {
            return short.class;
        } else if ( "int".equals(type) ) {
            return int.class;
        } else if ( "long".equals(type) ) {
            return long.class;
        } else if ( "double".equals(type) ) {
            return double.class;
        } else if ( "float".equals(type) ) {
            return float.class;
        } else if ( "void".equals(type) ) {
            return void.class;
        }
        
        return SnoopAgent.getAgentManager().getFromAllClasses(type);
    }

}