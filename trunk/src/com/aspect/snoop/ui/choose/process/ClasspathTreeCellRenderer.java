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

import java.awt.Color;
import java.awt.Component;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class ClasspathTreeCellRenderer extends DefaultTreeCellRenderer {

    private Icon openIcon = Util.createImageIcon("/gfx/open.gif");
    private Icon closedIcon = Util.createImageIcon("/gfx/close.gif");
    private Icon leafIcon = Util.createImageIcon("/gfx/leaf.gif");
    private Icon jarIcon = Util.createImageIcon("/gfx/jar.gif");
    private Icon classIcon = Util.createImageIcon("/gfx/class.gif");
    private Icon class2Icon = Util.createImageIcon("/gfx/class2.gif");
    private Icon methodPublicIcon = Util.createImageIcon("/gfx/methpub_obj.gif");
    private Icon methodDefaultIcon = Util.createImageIcon("/gfx/methdef_obj.gif");
    private Icon methodProtectedIcon = Util.createImageIcon("/gfx/methpro_obj.gif");
    private Icon methodPrivateIcon = Util.createImageIcon("/gfx/methpri_obj.gif");
    private Color gray2 = new Color(0x666666);

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object val, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, val, sel, expanded, leaf, row, hasFocus);

        if (val == null)
            return this;

        DefaultMutableTreeNode node = (DefaultMutableTreeNode)val;

        Object value = node.getUserObject();

        /*
         * Handle creating method icons/layouts.
         */
        if (value instanceof CtMethod ) {
            CtMethod method = (CtMethod)value;
            if (Modifier.isPublic(method.getModifiers()))  {
                this.setIcon(methodPublicIcon);
            } else if (Modifier.isProtected(method.getModifiers())) {
                this.setIcon(methodProtectedIcon);
            } else if (Modifier.isPrivate(method.getModifiers())) {
                this.setIcon(methodPrivateIcon);
            } else {
                this.setIcon(methodDefaultIcon);
            }
           
            setText(method.getLongName());
            

        /*
         * Handle creating class icons/layouts.
         */
        } else if ( value instanceof CtClass ) {
            CtClass clazz = (CtClass)value;
            this.setIcon(classIcon);
            this.setText(clazz.getName());

        /*
         * Handle creating jar icons/layouts.
         */
        } else if ( value instanceof ClasspathEntry ) {
            ClasspathEntry entry = (ClasspathEntry)value;
            this.setIcon(jarIcon);
            this.setText(entry.getStringEntry());
        
        /*
         * Handle creating jar resource icons/layouts.
         */
        } else {
            this.setIcon(leafIcon);
        }
        
        return this;
    }
}
