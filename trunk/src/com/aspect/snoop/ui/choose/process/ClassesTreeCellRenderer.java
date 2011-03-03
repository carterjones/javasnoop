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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class ClassesTreeCellRenderer extends DefaultTreeCellRenderer {

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
    public Component getTreeCellRendererComponent(JTree tree, Object n, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, n, sel, expanded, leaf, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode)n;
        String name = (String)node.getUserObject();

        if(node.isLeaf())
            setIcon(classIcon);
        else
            setIcon(openIcon);

        setText(name);
        
        return this;
    }

}
