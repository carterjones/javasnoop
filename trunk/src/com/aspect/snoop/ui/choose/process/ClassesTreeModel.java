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

import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class ClassesTreeModel extends DefaultTreeModel {
    
    private static Comparator alphabeticComparator = new Comparator() {
        public int compare(Object o1, Object o2) {
            String s1 = (String)o1;
            String s2 = (String)o2;
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    };
    
    List<String> classes;

    public ClassesTreeModel(TreeNode root) {
        super(root);
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    @Override
    public void reload() {

        for (int i = 0; i < classes.size(); i++) {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode)root;
            StringTokenizer st = new StringTokenizer(classes.get(i), ".");
            while (st.hasMoreTokens()) {
                String value = st.nextToken();
                boolean lastToken = !st.hasMoreTokens();	// is new element the last

                // [1] examine if children already exist
                boolean exist = false;
                boolean duplicate = false;
                Enumeration e = parent.children();
                while (e.hasMoreElements()) {
                    DefaultMutableTreeNode dtm = (DefaultMutableTreeNode) e.nextElement();
                    String dtmName = dtm.toString();
                    if ((dtmName != null) && (dtmName.equals(value))) {
                        if (lastToken == false) {// allow duplicated leafs
                            exist = true;// so only skip duplicated folders
                            parent = dtm;
                            break;
                        } else {
                            duplicate = true;// duplicate found
                            break;
                        }
                    }
                }

                // [2] add children
                if (exist == false) {
                    int index = 0;
                    boolean found = false;
                    e = parent.children();
                    while (e.hasMoreElements()) {
                        DefaultMutableTreeNode dtm = (DefaultMutableTreeNode) e.nextElement();
                        String dtmName = dtm.toString();
                        if (dtmName != null) {
                            if (dtm.isLeaf() && !lastToken) {	// current element is leaf and new element is not
                                index = parent.getIndex(dtm);	// therefore, insert folder above all leafs
                                found = true;
                                break;
                            }
                            if (!dtm.isLeaf() && lastToken) {	// current element is folder and new elemebt is leaf
                                continue;						// therefore, skip all folders
                            }
                            if (alphabeticComparator.compare(dtmName, value) >= 0) {
                                index = parent.getIndex(dtm);
                                found = true;
                                break;
                            }
                        }
                        index++;
                    }

                    DefaultMutableTreeNode child;
                    //if (lastToken == false) {
                       child = new DefaultMutableTreeNode(value);						// default class for folders
                    //} else {
                      // child = new DoubleStringTreeNode(value, jarName, duplicate);	// special class for leafs
                    //}
                    if (found == false) {		// place where to insert was not found
                        parent.add(child);
                    } else {					// insertion place found
                        if (duplicate) {		// move duplicates below first
                            index++;
                        }
                        parent.insert(child, index);
                    }
                    parent = child;
                }
            }
        }
    }
}
