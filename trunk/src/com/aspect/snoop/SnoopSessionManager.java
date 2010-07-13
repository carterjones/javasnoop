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

package com.aspect.snoop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.aspect.snoop.util.SessionPersistenceUtil;

/**
 * This class knows about sessions and instrumentation.  Also, it is thread-safe.
 * 
 * @author David Anderson
 *
 */
public class SnoopSessionManager {
	
	public SnoopSession loadSession(File xmlFile) throws FileNotFoundException, IOException {
		return SessionPersistenceUtil.loadSession(xmlFile);
	}
	
	/**
	 * This function is the entry point into the instrumentation of the
	 * running program. It turns on the rules we instrument.
	 */
	public void applySessionRules(SnoopSession session) {

		for (FunctionHookInterceptor hook : session.getFunctionHooks()) {

			// FIXME: detect if hook is already in place instrumented

			// instrument hook
			//InstrumentationUtil.instrument(hook.getClass(), hook);
		}

	}
}
