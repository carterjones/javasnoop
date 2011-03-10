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

import com.aspect.snoop.util.IOUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class ClasspathTreeModel extends DefaultTreeModel {

    List<ClasspathEntry> entries = new ArrayList<ClasspathEntry>();

    List<String> classesSeen;

    private static Comparator alphabeticComparator = new Comparator() {

        public int compare(Object o1, Object o2) {
            String s1 = ((ClasspathEntry)o1).getStringEntry();
            String s2 = ((ClasspathEntry)o2).getStringEntry();
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    };

    public ClasspathTreeModel(TreeNode root) {
        super(root);
    }

    @Override
    public boolean isLeaf(Object node) {
        return node instanceof CtMethod;
    }

    private boolean hasEntry(String file) {
        for(ClasspathEntry entry : entries) {
            if ( entry.getStringEntry().equals(file) ) {
                return true;
            }
        }
        return false;
    }

    public void addEntry(ClasspathEntry entry) {

        String file = entry.getStringEntry();
        File f = new File(file);

        /*
         * If it's a dupe of an existing jar, throw it out.
         */
        if ( f.isFile() && hasEntry(file) )
            return;

        /*
         * If it's a directory that is beneath an already-listed directory
         * then throw it out.
         */
        else if(f.isDirectory()) {
            for(ClasspathEntry existingEntry : entries) {
                File existing = new File(existingEntry.getStringEntry());
                if ( f.getAbsolutePath().startsWith(existing.getAbsolutePath()))
                    return;
            }
        }

        entries.add(entry);
        Collections.sort(entries,alphabeticComparator);
    }

    public void removeEntry(ClasspathEntry entry) {
        entries.remove(entry);
        Collections.sort(entries,alphabeticComparator);
    }

    public List<ClasspathEntry> getEntries() {
        return entries;
    }

    public int getSize() {
        return entries.size();
    }

    @Override
    public void reload() {
        DefaultMutableTreeNode top = (DefaultMutableTreeNode)root;
        top.removeAllChildren();

        classesSeen = new ArrayList<String>();

        ClassPool pool = new ClassPool();

        for(ClasspathEntry entry : entries) {
            DefaultMutableTreeNode jarNode = new DefaultMutableTreeNode(entry);
            pool.insertClassPath(entry.getEntry());

            if ( entry.isJar() ) {
                try {
                    JarFile jar = new JarFile(entry.getStringEntry());
                    Enumeration<JarEntry> jarEntries = jar.entries();
                    while(jarEntries.hasMoreElements()) {
                        JarEntry jarEntry = (JarEntry)jarEntries.nextElement();
                        if ( jarEntry.isDirectory() )
                            continue;

                        String entryName = jarEntry.getName();
                        
                        if ( entryName.endsWith(".class") ) {

                            try {

                                String cls = jarEntry.getName().replaceAll("/", "\\.");
                                String shortendCls = cls.substring(0,cls.length()-6);
                                classesSeen.add(shortendCls);
                                byte[] bytecode = IOUtil.getBytesFromStream(jar.getInputStream(jarEntry));
                                
                                pool.insertClassPath(new ByteArrayClassPath(shortendCls,bytecode));
                                CtClass clazz = pool.get(shortendCls);
                                DefaultMutableTreeNode classNode = new DefaultMutableTreeNode(clazz);

                                for(CtMethod method : clazz.getDeclaredMethods()) {
                                    DefaultMutableTreeNode methodNode = new DefaultMutableTreeNode(method);
                                    classNode.add(methodNode);
                                }

                                jarNode.add(classNode);
                            } catch(NotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            DefaultMutableTreeNode resourceNode = new DefaultMutableTreeNode(entryName);
                            jarNode.add(resourceNode);
                        }
                    }

                    top.add(jarNode);
                    
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            
        }
    }

    List<String> getClassesSeen() {
        return classesSeen;
    }
}
