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
import javax.swing.Icon;
import javax.swing.JTree;
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

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (value != null) {
            String val = value.toString();
            if (val != null) {
                if (val.endsWith(".jar")) {
                    this.setIcon(jarIcon);
                } else if (val.endsWith(".class")) {
                    Icon ico = classIcon;
                    if ((leaf == true) && (value instanceof DoubleStringTreeNode)) {
                        DoubleStringTreeNode n = (DoubleStringTreeNode) value;
                        if (n.isDuplicate() == true) {
                            ico = class2Icon;
                            this.setForeground(gray2);
                        }
                    }
                    this.setIcon(ico);
                } else if (val.contains("public")) {		// FIXME: Would be faster if val.getIcon() and val.getValue()
                    this.setIcon(methodPublicIcon);
                } else if (val.contains("protected")) {
                    this.setIcon(methodProtectedIcon);
                } else if (val.contains("private")) {
                    this.setIcon(methodPrivateIcon);
                } else if (val.contains("(")) {		// HACK!
                    this.setIcon(methodDefaultIcon);
                } else if ((leaf == true) && (value instanceof DoubleStringTreeNode)) {
                    DoubleStringTreeNode n = (DoubleStringTreeNode) value;
                    if (n.isDuplicate() == true) {
                        this.setForeground(gray2);
                    }
                }
            }
        }
        return this;
    }
}
