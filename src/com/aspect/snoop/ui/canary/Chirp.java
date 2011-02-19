/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aspect.snoop.ui.canary;

import java.lang.reflect.AccessibleObject;

/**
 *
 * @author adabirsiaghi
 */
class Chirp {

    AccessibleObject method;
    Class c;
    public Chirp(Class c, AccessibleObject method) {
        this.method = method;
        this.c = c;
    }

    public Class getClazz() {
        return c;
    }

    public AccessibleObject getMethod() {
        return method;
    }

}
