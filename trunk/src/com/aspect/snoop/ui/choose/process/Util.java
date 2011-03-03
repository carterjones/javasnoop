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

package com.aspect.snoop.ui.choose.process;

import com.aspect.snoop.ui.JavaSnoopView;
import javax.swing.ImageIcon;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class Util {

    /**
     * Replaces the occurrences of a certain pattern in a string with a
     * replacement String. This is the fastest replace function.
     *
     * @param s      the string to be inspected
     * @param sub    the string pattern to be replaced
     * @param with   the string that should go where the pattern was
     *
     * @return the string with the replacements done
     */
    public static String stringReplace(String s, String sub, String with) {
        if ((s == null) || (sub == null) || (with == null)) {
            return s;
        }
        int c = 0;
        int i = s.indexOf(sub, c);
        if (i == -1) {
            return s;
        }
        StringBuilder buf = new StringBuilder(s.length() + with.length());
        do {
            buf.append(s.substring(c, i));
            buf.append(with);
            c = i + sub.length();
        } while ((i = s.indexOf(sub, c)) != -1);
        if (c < s.length()) {
            buf.append(s.substring(c, s.length()));
        }
        return buf.toString();
    }

    /**
     * Character replacement in a string. All occurrences of a character will be
     * replaces.
     *
     * @param s      input string
     * @param sub    character to replace
     * @param with   character to replace with
     *
     * @return string with replaced characters
     */
    public static String stringReplaceChar(String s, char sub, char with) {
        if (s == null) {
            return s;
        }
        char[] str = s.toCharArray();
        for (int i = 0; i < str.length; i++) {
            if (str[i] == sub) {
                str[i] = with;
            }
        }
        return new String(str);
    }
    
    public static String convertListToString(List list, String delimiter) {
        String result;

        if (list != null) {
            StringBuilder resultBuffer = new StringBuilder();
            Iterator i = list.iterator();
            while (i.hasNext()) {
                Object o = i.next();
                if ( o instanceof ClasspathEntry )
                    resultBuffer.append(((ClasspathEntry)o).getStringEntry());
                else if ( o instanceof String )
                    resultBuffer.append(String.valueOf(o));
                
                if (i.hasNext()) {
                    resultBuffer.append(delimiter);
                }
            }

            result = resultBuffer.toString();
        } else {
            result = "";
        }

        return result;
    }

    public static List<String> convertStringToList(String value, String delimiter) {
        List<String> list = null;

        if (value != null) {
            list = Arrays.asList(value.split(","));
        }

        return list;
    }

    /**
     * Returns icon image from the resource.
     */
    public static ImageIcon createImageIcon(String iconResourcePath) {
        return new ImageIcon(JavaSnoopView.class.getResource(iconResourcePath));
    }
}
