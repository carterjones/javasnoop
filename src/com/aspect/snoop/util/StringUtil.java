package com.aspect.snoop.util;

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



import java.io.PrintWriter;
import java.io.StringWriter;

public class StringUtil {

    public static String join(Object[] parts, String seperator) {
        String s = "";
        for( Object part : parts )
            s += String.valueOf(part) + seperator;
        
        if (s.length()>0) 
            s = s.substring(0,s.length()-1);
        
        return s;
    }

    public static String join(Class[] parts, String seperator) {
        String s = "";
        for( Class part : parts )
            s += part.getName() + seperator;
        
        if (s.length()>0) 
            s = s.substring(0,s.length()-1);
        
        return s;
    }

    public static boolean isEmpty(String startSrc) {
        return startSrc != null && startSrc.length() > 0;
    }

    public static String exception2string(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    public static boolean isIn(String needle, String[] haystack) {
        for(String hay : haystack) {
            if (hay.equals(needle))
                return true;
        }
        return false;
    }
}
