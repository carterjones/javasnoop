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

package com.aspect.snoop.ui.hook;

import java.lang.reflect.Method;
import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

public class MethodListModel implements ListModel {

    List<Method> methods;

    public MethodListModel(List<Method> methods) {
        this.methods = methods;
    }

    public int getSize() {
        return methods.size();
    }

    public Object getElementAt(int index) {
        Method m = methods.get(index);
        return m;
    }

    public void addListDataListener(ListDataListener l) { }

    public void removeListDataListener(ListDataListener l) { }

}
