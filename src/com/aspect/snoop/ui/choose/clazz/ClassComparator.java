/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aspect.snoop.ui.choose.clazz;

import java.util.Comparator;

/**
 *
 * @author adabirsiaghi
 */
class ClassComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        Class c1 = (Class)o1;
        Class c2 = (Class)o2;
        return c1.getName().compareTo(c2.getName());
    }

}
