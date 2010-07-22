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

import java.io.File;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import javassist.ClassPool;

public class InstrumentationUtil {

    private static ClassPool pool = ClassPool.getDefault();
    private static Instrumentation inst;
    
    public static void redefineClass(Class c, byte[] bytes) throws Exception {
        ClassDefinition cd = new ClassDefinition(c, bytes);
        inst.redefineClasses(cd);
    }

    public static void redefineClass(Class c, String filename) throws Exception {
        redefineClass(c, IOUtil.getBytesFromFile(new File(filename)));
    }

    private static boolean doesClassContainSnoopCode() {
        // statically look through Class code to find calls to Canary.start()
        // and Canary.end().
        return false;
    }


}
