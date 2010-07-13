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

import java.util.regex.Pattern;

/*
 * The point of this class is to provide a simple regular expression
 * string/pattern object that only has two metacharacters: '?' and '*'.
 */

public class SimplePattern {

    Pattern p;
    String original;

    public SimplePattern(String s) {

        original = s;

        s = s.replaceAll("-","\\-");
        s = s.replaceAll("\\[","\\[");
        s = s.replaceAll("\\]","\\]");
        s = s.replaceAll("\\(","\\(");
        s = s.replaceAll("\\)","\\)");
        s = s.replaceAll("\\.","\\\\.");
        s = s.replaceAll("\\*", "\\.\\*");

        p = Pattern.compile(s);
        
    }

    public boolean matches(String test) {
        return p.matcher(test).matches();
    }

    public String getValue() {
        return original;
    }

    public Pattern getPattern() {
        return p;
    }

}
