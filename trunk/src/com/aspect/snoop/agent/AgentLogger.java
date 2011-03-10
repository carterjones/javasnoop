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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AgentLogger {

    public static final int TRACE = 0;
    public static final int DEBUG = 1;
    public static final int INFO = 2;
    public static final int WARN = 3;
    public static final int ERROR = 4;
    public static final int FATAL = 5;
    public static final int OFF = 6;

    private static final Map<Integer,String> levelMap = new HashMap<Integer,String>();
    private static final Map<String,Integer> nameMap = new HashMap<String,Integer>();

    private static String logFile = System.getProperty("user.home") + "/snoop.log";

    private static final String prefix = "[JSNOOP ";
    private static final String suffix = "] ";
    private static final String nl = System.getProperty("line.separator");

    static {
        levelMap.put(TRACE, "TRACE");
        levelMap.put(DEBUG, "DEBUG");
        levelMap.put(INFO, "INFO");
        levelMap.put(WARN, "WARN");
        levelMap.put(ERROR, "ERROR");
        levelMap.put(FATAL, "FATAL");
        levelMap.put(OFF, "OFF");

        nameMap.put("TRACE",TRACE);
        nameMap.put("DEBUG",DEBUG);
        nameMap.put("INFO", INFO);
        nameMap.put("WARN", WARN);
        nameMap.put("ERROR", ERROR);
        nameMap.put("FATAL", FATAL);
        nameMap.put("OFF", OFF);
    }

    public static int level = DEBUG;

    public static String levelName(int l) {
        return levelMap.get(l);
    }

    public static Integer levelValue(String s) {
        return nameMap.get(s);
    }

    public static void trace(String s) {
        if ( level <= TRACE ) _log(getPrefix() + s);
    }


    public static void debug(String s, Throwable t) {
        if ( level <= DEBUG ) {
            _log(getPrefix() + s);
            _log(t);
        }
    }

    public static void debug(String s) {
        if ( level <= DEBUG )
            _log(getPrefix() + s);
    }

    public static void info(String s) {
        if ( level <= INFO ) _log(getPrefix() + s);
    }

    public static void warn(String s) {
        if ( level <= WARN ) _log(getPrefix() + s);
    }

    public static void warn(String s, Throwable t) {
        if ( level <= WARN ) {
            _log(getPrefix() + s);
            _log(t);
        }
    }

    public static void error(Throwable t) {
        if ( level <= ERROR ) {
            _log(getPrefix() + t.getMessage());
            _log(t);
        }
    }

    public static void error(String s) {
        if ( level <= ERROR ) _log(getPrefix() + s);
    }

    public static void error(String s, Throwable t) {
        if ( level <= ERROR ) {
            _log(getPrefix() + s);
            _log(t);
        }
    }

    public static void fatal(String s) {
        if ( level <= FATAL ) _log(getPrefix() + s);
    }

    public static void fatal(Throwable t) {
        if ( level <= FATAL ) {
            _log(getPrefix() + t.getMessage());
            _log(t);
        }
    }

    public static void fatal(String s, Throwable t) {
        if ( level <= FATAL ) {
            _log(getPrefix() + s);
            _log(t);
        }
    }

    private static String getPrefix() {
        return prefix + getTime() + " " + levelName(level) + suffix;
    }

    private static String getTime() {
        return new SimpleDateFormat().format(new Date());
    }

    private static void _log(String s) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(logFile,true);
            fos.write(s.getBytes());
            fos.write(nl.getBytes());
            fos.close();
        } catch (IOException e) {
            _log(s);
        }
    }

    private static void _log(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.flush();
        _log(sw.toString());
    }
}
