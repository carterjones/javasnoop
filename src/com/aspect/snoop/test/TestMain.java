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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestMain {

    public static void main(String[] args) throws IOException {

        while(true) {
            
            TestObject testObject = new TestObject();
            
            System.out.println("this is a test inside a main class: ");

            fireTest(testObject);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void fireTest(TestObject test) {
        test.printRandomNum();
    }

}
