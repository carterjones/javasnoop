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

import com.aspect.snoop.JavaSnoop;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class ClasspathUtil {

    //private static Logger logger = Logger.getLogger(ClasspathUtil.class);

    /*
     * This may all be unnecessary, but it's useful to have around.
     */
    public static byte[] getClassBytes (String clazz, String classpath, boolean useSystemClasspath) {
        
        String completeClasspath = "";
        
        if ( useSystemClasspath ) {
            completeClasspath += getSystemClasspath();
        }

        completeClasspath += classpath;

        String simpleClassName = ReflectionUtil.getSimpleClassName(clazz);

        StringTokenizer st = new StringTokenizer(completeClasspath, File.pathSeparator);

        while (st.hasMoreTokens()) {

            String token = st.nextToken();
            
            File classpathElement = new File(token);

            if (classpathElement.isDirectory() ) {

                String classFiles[] = classpathElement.list(new ClassFilter());

                for ( String file : classFiles ) {

                    if ( file.equals(simpleClassName + ".class") ) {

                        File targetFile = new File(classpathElement.getAbsolutePath() + File.separatorChar + file);
                        try {
                            return IOUtil.getBytesFromFile(targetFile);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                }

                String[] jarFiles = classpathElement.list(new JarFilter());

                for ( String file : jarFiles ) {
                    
                    byte[] classBytes = null;

                    try {
                         File jarFile = new File(classpathElement.getAbsolutePath() + File.separator + file);
                         classBytes = findClassInJar(clazz, jarFile);
                     } catch(IOException ex) {
                        ex.printStackTrace();
                     }

                    if ( classBytes != null ) {
                        return classBytes;
                    }
                }

            } else { // it's a jar
                
                byte[] classBytes = null;
                try {
                    classBytes = findClassInJar(clazz, classpathElement);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                if ( classBytes != null ) {
                    return classBytes;
                }
            }

        }

        return null;

    }
    
    
    public static String getSystemClasspath() {

        // Get the system classpath
        String classpath = System.getProperty("java.class.path", ".");
        
        if (classpath.equals("")) {
            System.err.println("error: classpath is not set");
        }

        if ( ! "".equals(classpath) ) {
            classpath += ";";
        }
        
        String javaHome = System.getProperty("java.home");

        if ( ! "".equals(javaHome) ) {
            javaHome += File.separator + "lib";
            classpath += javaHome;
            classpath += ";";
        }

        return classpath;

    }

    /**
     * This function retrieves all unique system classpath classes
     * and classes added to the classpath in the method parameter.
     * @param appClasspath A set of classpath entries, delimited by semicolons.
     * @return A List<String> contains the String name of all available classes.
     */

    public static List<String> getClasses(String appClasspath) {
        return getClasses(appClasspath, true);
    }
    
    public static List<String> getClasses(String appClasspath, boolean useSystemClasspath) {

        List<String> classes = new ArrayList<String>();

        try {

            String classpath = "";

            if ( useSystemClasspath ) {
                classpath = getSystemClasspath();
            }

            classpath += appClasspath;

            StringTokenizer st =
                    new StringTokenizer(classpath, File.pathSeparator);

            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                File classpathElement = new File(token);

                if ( classpathElement.isDirectory() ) {

                    // The classpath element is a directory, containing
                    // class files and possibly jar files. Search it recursively
                    // for both.

                    classes.addAll ( recursiveFindClasses(classpathElement.getAbsolutePath(),"") );

                } else if ( classpathElement.getName().endsWith(".jar")) {

                    // The classpath element is a jar file. Add all its classes.
                    classes.addAll( loadClassesFromJar(classpathElement) );

                } else if ( classpathElement.getName().endsWith(".class") ) {

                    // The classpath element is a class file itself. Assume the
                    // default package.
                    String clz = classpathElement.getName();
                    clz = clz.substring(0,clz.length()-6);
                    classes.add ( clz );

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        classes = removeDuplicates(classes);

        return classes;
    }

    /**
     * Gets the bytes of a class file from within a jar.
     * @param clazz The name of the class whose bytes are needed
     * @param jarFile The jar File object to find it in
     * @return The bytes of the class file requested
     * @throws IOException
     */
    private static byte[] findClassInJar(String clazz, File jarFile) throws IOException {
  
        if ( ! jarFile.getName().endsWith(".jar") ) {
            return null;
        }

        JarFile jar = new JarFile(jarFile);
        Enumeration<JarEntry> classes = jar.entries();
        
        while ( classes.hasMoreElements() ) {
            JarEntry entry = (JarEntry)classes.nextElement();
            String completeClass = entry.getName().replace("/", ".");
            
            if (completeClass.equals(clazz + ".class") ) {
                return IOUtil.getBytesFromStream(jar.getInputStream(entry));
            }
        }
        
        return null;
    }

    private static List<String> loadClassesFromJar(File jarFile) {
        List<String> files = new ArrayList<String>();
        try {
            //logger.debug(jarFile + " is being scanned");
            Enumeration<JarEntry> fileNames;
            fileNames = new JarFile(jarFile).entries();
            JarEntry entry = null;
            while (fileNames.hasMoreElements()) {
                entry = fileNames.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String converted = entry.getName().replace("/", ".");
                    converted = converted.substring(0,converted.length()-6);
                    files.add(converted);
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set noDupes = new LinkedHashSet(files);
        files.clear();
        files.addAll(noDupes);
        
        return files;
    }

    public static Class[] getAllSubtypes(Class c) {
        return new Class[] { c };
    }

    private static List recursiveFindClasses(String root, String dir) {

        List<String> classes = new ArrayList<String>();
        File currentDir;

        if ( dir.length() > 0 ) {
            currentDir = new File(dir);
        } else {
            currentDir = new File(root);
        }
        
        File[] classFiles = currentDir.listFiles(new ClassFilter());
        File[] jarFiles = currentDir.listFiles(new JarFilter());
        File[] subDirectories = currentDir.listFiles(new DirFilter());

        String pkg = "";
        
        if ( dir.length() > 0 ) {
            pkg = dir.substring(root.length()+1);
        }

        for ( File classFile : classFiles ) {
            String s = pkg + "." + classFile.getName();
            s = s.substring(0,s.length()-6); // remove .class
            s = s.replace(File.separator,".");
            classes.add(s);
        }

        for ( File jarFile : jarFiles ) {
            classes.addAll( loadClassesFromJar(jarFile) );
        }

        for ( File subDir : subDirectories ) {
            classes.addAll( recursiveFindClasses(root, subDir.getAbsolutePath() ) );
        }

        return classes;

    }

    /**
     * Return an array of Class objects based on the String class names passed in.
     * @param types the name of the classes to be returned
     * @return the Class representation of the class names passed in types
     * @throws ClassNotFoundException
     */
    public static Class[] asClasses(String[] types) throws ClassNotFoundException {

        Class[] classes = new Class[types.length];

        for(int i=0;i<types.length;i++) {

            if ( "boolean".equals(types[i]) ) {
                types[i] = "java.lang.Boolean";
            } else if ( "byte".equals(types[i]) ) {
                types[i] = "java.lang.Byte";
            } else if ( "short".equals(types[i]) ) {
                types[i] = "java.lang.Short";
            } else if ( "char".equals(types[i]) ) {
                types[i] = "java.lang.Character";
            } else if ( "long".equals(types[i]) ) {
                types[i] = "java.lang.Long";
            } else if ( "double".equals(types[i]) ) {
                types[i] = "java.lang.Double";
            } else if ( "float".equals(types[i]) ) {
                types[i] = "java.lang.Float";
            }

            classes[i] = Class.forName( types[i] );
        }

        return classes;
    }

    public static String[] asStrings(Class[] types) throws ClassNotFoundException {

        String[] classes = new String[types.length];

        for(int i=0;i<types.length;i++) {
            classes[i] = types[i].getName();
        }

        return classes;
    }

    public static String getManifestSystemClasspathString() {
        return getManifestClasspathString(System.getProperty("java.class.path"));
    }

    public static String getManifestClasspathString(String cp) {

        String classpath = new String(cp);

        classpath = classpath.replaceAll(" ", "%20");
        
        StringBuffer sb = new StringBuffer();

        int firstLineLength = 72-("Class-Path: ".length() + 1);

        String s = classpath.substring(0,firstLineLength);
        sb.append(s + " \r\n");

        int index = firstLineLength;

        while ( index < classpath.length() ) {

            int toBeCopied = 70;
            if ( classpath.length() - index < 70 ) {
                toBeCopied = classpath.length()-index;
            }

            sb.append(" ");
            sb.append(classpath.substring(index,index+toBeCopied));

            index += toBeCopied;

            if ( toBeCopied == 70 ) {
                sb.append(" ");
            }

            sb.append("\r\n");

        }

        return sb.toString();
    }

    private static ArrayList removeDuplicates(List<String> classes) {
        Set set = new LinkedHashSet();
        set.addAll(classes);
        return new ArrayList(set);
    }

    public static boolean isJavaOrSunClass(String cls) {
        String[] pkgs = new String[]{
            "java.",
            "javax.",
            "sun.",
            "sunw.",
            "netscape.",
            "apple.",
            "com.apple.",
            "com.sun."
        };
        
        for(String pkg : pkgs) {
            if ( cls.startsWith(pkg) ) {
                return true;
            }
        }
        
        return false;
    }

    public static boolean isJavaSnoopClass(String cls) {
        String[] pkgs = new String[] {
            "com.aspect.snoop",
            "org.fife",
            "org.apache.log4j",
            "org.codehaus",
            "org.xmlpull",
            "org.relaxng",
            "org.jaxen",
            "org.jcp",
            "org.joda",
            "com.aspect.org.jdesktop",
            "org.jdom",
            "org.ietf",
            "org.omg",
            "org.dom4j",
            "org.xml.sax",
            "org.w3c.dom",
            "org.codehaus.jettison",
            "net.sf.cglib",
            "com.wutka",
            "com.thoughtworks.xstream",
            "com.megginson",
            "com.ctc.wstx",
            "com.bea.xml.stream",
            "JDOMAbout",
            "javassist.",
            "net.sf.cgilib",
            "nu.xom",
            "org.python"
        };

    for(String pkg : pkgs) {
            if ( cls.startsWith(pkg) ) {
                return true;
            }
        }
        
        return false;
    }

    public static String getMainClassFromJarFile(String path) throws IOException {
        
        if ( ! new File(path).exists() ) {
            return "(unknown)";
        }
        
        JarFile jar = new JarFile(path);
        Manifest manifest = jar.getManifest();
        Attributes attrs = manifest.getMainAttributes();
        
        return attrs.getValue(Attributes.Name.MAIN_CLASS);
    }
    
}
class ClassFilter implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return (name.endsWith(".class"));
    }
}

class JarFilter implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return (name.endsWith(".jar"));
    }
}

class DirFilter implements FileFilter {
    public boolean accept(File pathname) {
        return pathname.isDirectory();
    }
}



