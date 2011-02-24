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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class ClasspathTreeModel extends DefaultTreeModel {

    List<String> entries = new ArrayList<String>();

    private static Comparator alphabeticComparator = new Comparator() {

        public int compare(Object o1, Object o2) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    };

    public ClasspathTreeModel(TreeNode root) {
        super(root);
    }

    public void addEntry(String file) {

        File entry = new File(file);

        /*
         * If it's a dupe of an existing jar, throw it out.
         */
        if ( entry.isFile() && entries.contains(file) )
            return;

        /*
         * If it's a directory that is beneath an already-listed directory
         * then throw it out.
         */
        else if(entry.isDirectory()) {
            for(String existingEntry : entries) {
                File existing = new File(existingEntry);
                if ( entry.getAbsolutePath().startsWith(existing.getAbsolutePath()))
                    return;
            }
        }

        entries.add(file);
        Collections.sort(entries,alphabeticComparator);
    }

    public void removeEntry(String entry) {
        entries.remove(entry);
        Collections.sort(entries,alphabeticComparator);
    }

    public List<String> getEntries() {
        return entries;
    }

    public int getSize() {
        return entries.size();
    }

    @Override
    public void reload() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();

        for(String entry : entries) {
            JarFileEntry jar = new JarFileEntry(entry);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(jar);
            root.add(node);
        }

        super.reload();
    }
}
