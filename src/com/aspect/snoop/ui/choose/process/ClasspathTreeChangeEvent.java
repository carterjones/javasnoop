/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aspect.snoop.ui.choose.process;

import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author Administrator
 */
class ClasspathTreeChangeEvent {

	private DefaultTreeModel jarsTreeModel;
	private DefaultTreeModel classesTreeModel;
	
	public ClasspathTreeChangeEvent(DefaultTreeModel jarsTreeModel, DefaultTreeModel classesTreeModel) {
		this.jarsTreeModel = jarsTreeModel;
		this.classesTreeModel = classesTreeModel;
	}

	public DefaultTreeModel getClassesTreeModel() {
		return classesTreeModel;
	}

	public DefaultTreeModel getJarsTreeModel() {
		return jarsTreeModel;
	}

}
