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

class JVMDescriptor {
	
	public JVMDescriptor(String id, String title, boolean isAttachable) {
		super();
		this.id = id;
		this.title = title;
		this.isAttachable = isAttachable;
                this.jar = "";
	}
	private String id;
	private String title;
	private boolean isAttachable = false;
	
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	public void setAttachable(boolean isAttachable) {
		this.isAttachable = isAttachable;
	}
	public boolean isAttachable() {
		return isAttachable;
	}

    String jvmArgs;
    String mainArgs;
    String commandLine;
    String vmVersion;
    String mainClass;
    String jar;

    public void setJar(String jar) {
        this.jar = jar;
    }

    public String getJar() {
        return this.jar;
    }

    public void setJVMArguments(String jvmArgs) {
        this.jvmArgs = jvmArgs;
    }

    public String getJVMArguments() {
        return this.jvmArgs;
    }

    public void setMainArguments(String mainArgs) {
        this.mainArgs = mainArgs;
    }

    public String getMainArguments() {
        return this.mainArgs;
    }

    

    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }

    public String getCommandLine() {
        return this.commandLine;
    }

    public void setVMVersion(String vmVersion) {
        this.vmVersion = vmVersion;
    }

    public String getVMVersion() {
        return this.vmVersion;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }
}
