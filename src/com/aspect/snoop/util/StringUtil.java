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

import java.io.PrintWriter;
import java.io.StringWriter;

public class StringUtil {

    public static String join(String[] parts, String seperator) {
        String s = "";
        for( String part : parts ) {
            s += part + ",";
        }
        if (s.length()>0) {
            s = s.substring(0,s.length()-1);
        }
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
}
