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

package com.aspect.snoop.test;

import java.io.File;
import java.io.FileOutputStream;

public class TestObject {
    
    private int i = 5;
    private String test = "foo";

    public double printRandomNum() {
        System.out.println(i + " - " + test);
        return Math.random();
    }

    protected void log(String string) {

        try {

            File f = new File("JavaSnoop.txt");

            if ( ! f.exists() ) {
                f.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(f, true);
            fos.write(string.getBytes());
            fos.write("\r\n".getBytes());
            fos.close();

        } catch(Exception e) {e.printStackTrace();}

    }
}
