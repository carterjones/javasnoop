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

import com.aspect.snoop.JavaSnoop;
import com.aspect.snoop.agent.manager.UniqueMethod;
import com.aspect.snoop.messages.AgentMessage;
import com.aspect.snoop.messages.UnrecognizedMessage;
import com.aspect.snoop.messages.client.CanaryChirpRequest;
import com.aspect.snoop.messages.client.CanaryChirpResponse;
import com.aspect.snoop.messages.client.PauseRequest;
import com.aspect.snoop.messages.client.PauseResponse;
import com.aspect.snoop.messages.client.PrintParametersRequest;
import com.aspect.snoop.messages.client.PrintParametersResponse;
import com.aspect.snoop.messages.client.PrintStackTraceRequest;
import com.aspect.snoop.messages.client.PrintStackTraceResponse;
import com.aspect.snoop.messages.client.RunScriptRequest;
import com.aspect.snoop.messages.client.RunScriptResponse;
import com.aspect.snoop.messages.client.ShowErrorRequest;
import com.aspect.snoop.messages.client.ShowErrorResponse;
import com.aspect.snoop.messages.client.TamperParametersRequest;
import com.aspect.snoop.messages.client.TamperParametersResponse;
import com.aspect.snoop.messages.client.TamperReturnRequest;
import com.aspect.snoop.messages.client.TamperReturnResponse;
import com.aspect.snoop.util.SerializationUtil;
import com.aspect.snoop.util.UIUtil;
import com.thoughtworks.xstream.InitializationException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterRegistry;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.DefaultConverterLookup;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.mapper.ArrayMapper;
import com.thoughtworks.xstream.mapper.AttributeAliasingMapper;
import com.thoughtworks.xstream.mapper.AttributeMapper;
import com.thoughtworks.xstream.mapper.CachingMapper;
import com.thoughtworks.xstream.mapper.ClassAliasingMapper;
import com.thoughtworks.xstream.mapper.DefaultImplementationsMapper;
import com.thoughtworks.xstream.mapper.DefaultMapper;
import com.thoughtworks.xstream.mapper.DynamicProxyMapper;
import com.thoughtworks.xstream.mapper.FieldAliasingMapper;
import com.thoughtworks.xstream.mapper.ImmutableTypesMapper;
import com.thoughtworks.xstream.mapper.ImplicitCollectionMapper;
import com.thoughtworks.xstream.mapper.LocalConversionMapper;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.OuterClassMapper;
import com.thoughtworks.xstream.mapper.PackageAliasingMapper;
import com.thoughtworks.xstream.mapper.SystemAttributeAliasingMapper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import org.apache.log4j.Logger;

public class SnoopServerThread extends AbstractServerThread {

    public static Logger logger = Logger.getLogger(SnoopServerThread.class);

    public final static String SUCCESS = "SUCCESS";
    public final static String FAIL = "FAIL";

    public int port = 0xADDA; // default

    private XStream serializer;

    protected SnoopServerThread(int port) {
        
        super();

        this.port = port;

        JVM jvm = new JVM();
        ReflectionProvider reflectionProvider = jvm.bestReflectionProvider();
        Mapper mapper = buildMapper(jvm,reflectionProvider,JavaSnoop.getClassLoader());

        this.serializer = new XStream(null,new XppDriver(),JavaSnoop.getClassLoader(),mapper);
        this.serializer.setClassLoader(JavaSnoop.getClassLoader());
    }

    /**
     *
     * @param message
     * @param input
     * @param output
     * @throws IOException
     */
    protected void processCommand(AgentMessage message, ObjectInputStream input, ObjectOutputStream output) throws IOException {

        if ( message instanceof PauseRequest ) {

            PauseRequest request = (PauseRequest)message;
            PauseResponse response = new PauseResponse();

            try {

                // handle an incoming pause request
                JavaSnoop.getMainForm().pause(
                        request.getClassName(),
                        request.getHookId(),
                        request.getParameters());

            } catch (Exception e) {
                populateResponse(response,e);
            }

            output.writeObject(response);
            
        } else if ( message instanceof PrintParametersRequest ) {

            PrintParametersRequest request = (PrintParametersRequest)message;
            PrintParametersResponse response = new PrintParametersResponse();

            try {

                // print the parameters
                JavaSnoop.getMainForm().printParameters(
                        request.getClassName(),
                        request.getHookId(),
                        request.getParameterTypes(),
                        SerializationUtil.prepareObjectsForUsing(request.getParameters(),serializer));
                

            } catch(Exception e) {
                populateResponse(response,e);
            }

            output.writeObject(response);

        } else if ( message instanceof PrintStackTraceRequest ) {

            PrintStackTraceRequest request = (PrintStackTraceRequest)message;
            PrintStackTraceResponse response = new PrintStackTraceResponse();

            try {

                // print the parameters
                JavaSnoop.getMainForm().printStackTrace(
                        request.getClassName(),
                        request.getHookId(),
                        request.getStackTrace(),
                        request.getParameterTypes(),
                        SerializationUtil.prepareObjectsForUsing(request.getParameters(),serializer));

            } catch(Exception e) {
                populateResponse(response,e);
            }

            output.writeObject(response);

        } else if ( message instanceof RunScriptRequest ) {

            RunScriptRequest request = (RunScriptRequest)message;
            RunScriptResponse response = new RunScriptResponse();

            try {

              // run the script

            } catch(Exception e) {
                populateResponse(response,e);
            }

            output.writeObject(response);

        } else if ( message instanceof TamperParametersRequest ) {

            TamperParametersRequest request = (TamperParametersRequest)message;
            TamperParametersResponse response = new TamperParametersResponse();

            try {

                String[] types = request.getParameterTypes();
                Object[] objs = SerializationUtil.prepareObjectsForUsing(request.getParameters(),serializer);
                
                // tamper!
                Object[] modifications = JavaSnoop.getMainForm().tamperWithParameters(
                        request.getClassName(),
                        request.getHookId(),
                        objs,
                        types,
                        false);

                response.setModifiedParameters( SerializationUtil.prepareObjectsForSending(modifications,types,serializer) );

            } catch (Exception e) {
                populateResponse(message,e);
                UIUtil.showErrorMessage(JavaSnoop.getMainForm().getFrame(), "Problem tampering with parameters: " + e.getMessage());
                logger.error(e);
                e.printStackTrace();
            }

            output.writeObject(response);

        } else if ( message instanceof TamperReturnRequest ) {

            TamperReturnRequest request = (TamperReturnRequest)message;
            TamperReturnResponse response = new TamperReturnResponse();

            try {
                
                Object[] objs = SerializationUtil.prepareObjectsForUsing( new Object[]{request.getValue()}, serializer);
                
                // tamper!
                Object[] modifications = JavaSnoop.getMainForm().tamperWithParameters(
                        request.getClassName(),
                        request.getHookId(),
                        objs,
                        new String[]{request.getType()}, true);

                String returnType = JavaSnoop.getMainForm().getHookById(request.getHookId()).getReturnType();

                response.setModifiedValue(
                        SerializationUtil.prepareObjectsForSending(
                            modifications,
                            new String[]{returnType},
                            serializer)[0] );

            } catch (Exception e) {
                populateResponse(message,e);
                UIUtil.showErrorMessage(JavaSnoop.getMainForm().getFrame(), "Problem tampering with return value: " + e.getMessage());
                logger.error(e);
            }

            output.writeObject(response);
            
        } else if ( message instanceof ShowErrorRequest ) {
            
            ShowErrorRequest request = (ShowErrorRequest)message;
            
            JavaSnoop.getMainForm().showConsoleErrorMessage(request.getMessage());

            ShowErrorResponse response = new ShowErrorResponse();

            output.writeObject(response);

        } else if ( message instanceof CanaryChirpRequest ) {

            CanaryChirpRequest request = (CanaryChirpRequest)message;
            CanaryChirpResponse response = new CanaryChirpResponse();

            UniqueMethod chirp = request.getMethod();
            JavaSnoop.getMainForm().getCanaryView().addChirp(chirp);
            output.writeObject(response);

        } else {

            UnrecognizedMessage response = new UnrecognizedMessage();

            output.writeObject(response);

        }

    }

    protected int getServerPort() {
        return port;
    }

    public Object[] prepareObjectsForUsing(Object[] objects) {
        for(int i=0;i<objects.length;i++) {
            Object o = objects[i];
            if ( o instanceof TamperParameter ) {
                TamperParameter param = (TamperParameter)o;
                objects[i] = serializer.fromXML(param.getXML()); //  replaced!
            }
        }
        return objects;
    }

    private Mapper buildMapper(JVM jvm, ReflectionProvider reflectionProvider, ClassLoader cl) {

        ConverterLookup converterLookup = new DefaultConverterLookup();

        Mapper mapper = new DefaultMapper(cl);

        mapper = new DynamicProxyMapper(mapper);
        mapper = new PackageAliasingMapper(mapper);
        mapper = new ClassAliasingMapper(mapper);
        mapper = new FieldAliasingMapper(mapper);
        mapper = new AttributeAliasingMapper(mapper);
        mapper = new SystemAttributeAliasingMapper(mapper);
        mapper = new ImplicitCollectionMapper(mapper);
        mapper = new OuterClassMapper(mapper);
        mapper = new ArrayMapper(mapper);
        mapper = new DefaultImplementationsMapper(mapper);
        mapper = new AttributeMapper(mapper, converterLookup);

        if (JVM.is15()) {
            mapper = buildMapperDynamically(
                "com.thoughtworks.xstream.mapper.EnumMapper", new Class[]{Mapper.class},
                new Object[]{mapper});
        }
        mapper = new LocalConversionMapper(mapper);
        mapper = new ImmutableTypesMapper(mapper);
        if (JVM.is15()) {
            mapper = buildMapperDynamically(
                "com.thoughtworks.xstream.mapper.AnnotationMapper",
                new Class[]{Mapper.class, ConverterRegistry.class, ClassLoader.class, ReflectionProvider.class, JVM.class},
                new Object[]{mapper, converterLookup, cl, reflectionProvider, jvm});
        }

        mapper = new CachingMapper(mapper);
        return mapper;
    }

    private Mapper buildMapperDynamically(
            String className, Class[] constructorParamTypes,
            Object[] constructorParamValues) {
        try {
            Class type = Class.forName(className, false, JavaSnoop.getClassLoader());
            Constructor constructor = type.getConstructor(constructorParamTypes);
            return (Mapper)constructor.newInstance(constructorParamValues);
        } catch (Exception e) {
            throw new InitializationException("Could not instantiate mapper : " + className, e);
        }
    }
}