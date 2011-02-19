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

import com.aspect.snoop.util.IOUtil;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import javassist.ClassPath;

public class SmartURLClassPath implements ClassPath {

    protected URL url;

    private Map<String,Integer> classesResponsibleFor;

    public SmartURLClassPath(URL url) {
        this.url = url;
        this.classesResponsibleFor = new HashMap<String,Integer>();
    }

    public void addClass(String className) {
        classesResponsibleFor.put(className, 1);
    }
    
    @Override
    public String toString() {
        return url.toString();
    }

    /**
     * Opens a class file with http.
     *
     * @return null if the class file could not be found.
     */
    public InputStream openClassfile(String classname) {

        if ( classesResponsibleFor.get(classname) == null ) {
            return null;
        }

        try {
            return openClassfile0(classname);
        }
        catch (IOException e) {e.printStackTrace();}
        return null;        // not found
    }



    private InputStream openClassfile0(String classname) throws IOException {

        return fetchClass( getURL(classname) );

    }

    private URL getURL(String classname) throws MalformedURLException {
        
        URL finalUrl;

        if ( url.getPath().endsWith(".jar") || url.getPath().endsWith(".zip") ) {

            String finalPath = url.getPath() + "!" + "/" + classname.replace('.', '/') + ".class";
            URL tmp = new URL(url.getProtocol(), url.getHost(), url.getPort(), finalPath);

            String s = "jar:" + tmp.toString();
            finalUrl = new URL(s);

        } else {
            
            String finalPath = url.getPath() + classname.replace('.', '/') + ".class";
            finalUrl = new URL(url.getProtocol(), url.getHost(), url.getPort(), finalPath);

        }
        
        return finalUrl;
    }

    public InputStream fetchClass(URL url) throws IOException {
        return url.openStream();
    }

    /**
     * Returns the URL.
     *
     * @return null if the class file could not be obtained.
     */
    public URL find(String classname) {

        if ( classesResponsibleFor.get(classname) == null ) {
            return null;
        }

        try {

            InputStream is = openClassfile0(classname);
            if (is != null) {
                is.close();
                return getURL(classname);
            }
        } catch (IOException e) {e.printStackTrace();}
        
        return null;
    }

    /**
     * Closes this class path.
     */
    public void close() {}



    public static byte[] getBytesFromURL(URL url)
        throws IOException
    {
        byte[] b;
        URLConnection con = url.openConnection();
        int size = con.getContentLength();
        InputStream s = con.getInputStream();
        try {
            if (size <= 0)
                b = IOUtil.getBytesFromStream(s);
            else {
                b = new byte[size];
                int len = 0;
                do {
                    int n = s.read(b, len, size - len);
                    if (n < 0)
                        throw new IOException("the stream was closed: " + url.toString());

                    len += n;
                } while (len < size);
            }
        }
        finally {
            s.close();
        }

        return b;
    }

}
