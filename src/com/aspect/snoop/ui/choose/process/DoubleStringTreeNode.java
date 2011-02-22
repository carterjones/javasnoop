package com.aspect.snoop.ui.choose.process;

import javax.swing.tree.DefaultMutableTreeNode;


public class DoubleStringTreeNode extends DefaultMutableTreeNode {

	private boolean isDuplicate;

	public boolean isDuplicate() {
		return isDuplicate;
	}

	public DoubleStringTreeNode(String desc, String val, boolean isDuplicate) {
		super(new String[] {desc, val});
		this.isDuplicate = isDuplicate;
	}

	public String toString() {
		String[] data =  (String[]) this.getUserObject();
		if (data == null) {
			return null;
		}
		return data[0];
	}
}


