/*******************************************************************************
 * Copyright (c) 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Emil Simeonov - initial API and implementation.
 *    Dimitar Donchev - initial API and implementation.
 *    Dimitar Tenev - initial API and implementation.
 *    Nevena Manova - initial API and implementation.
 *    Georgi Konstantinov - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.core.common;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.service.debug.DebugTrace;

import org.eclipse.wst.sse.sieditor.core.SIEditorCoreActivator;

/**
 * Provides logging and tracing capabilities.
 * 
 * Due to Eclipse bug 285292 (https://bugs.eclipse.org/bugs/show_bug.cgi?id=285292)
 * tracing should be used by obtaining the DebugTrace instance and then invoking any of the trace methods on it.
 * <br/>
 * Example: Logger.getDebugTrace().trace("", "Something to trace")
 * <br/>
 * 
 * How to enable and use tracing:<br/>
 * Create debug.options file in the eclipse installation folder with the following contents:<br/>
 * org.eclipse.wst.sse.sieditor.core=true<br/>
 * org.eclipse.wst.sse.sieditor.core/debug=true<br/>
 * Invoke the runtime workbench with the osgi.debug=debug.options SYSTEM property.<br/>
 * The trace output can be found in <WORKSPACE>/.metadata/trace.log
 * 
 * <br/>
 * 
 * Please use the tracing only as a debug mechanism. Don't use it for logging errors or warnings.
 * 
 *
 */
public class Logger {

	public static void logError(String message) {
		log(null, IStatus.ERROR, message, null);
	}
	
	public static void logError(String message, Throwable exception) {
		log(null, IStatus.ERROR, message, exception);
	}
	
	public static void logError(String pluginId, String message, Throwable exception) {
		log(pluginId, IStatus.ERROR, message, exception);
	}
	
	public static void logWarning(String message) {
		log(null, IStatus.WARNING, message, null);
	}
	
	public static void logWarning(String message, Throwable exception) {
		log(null, IStatus.WARNING, message, exception);
	}
	
	public static void logWarning(String pluginId, String message, Throwable exception) {
		log(pluginId, IStatus.WARNING, message, exception);
	}
	
	public static void log(String pluginId, int severity, String message, Throwable exception) {
		if (pluginId == null) {
			pluginId = SIEditorCoreActivator.PLUGIN_ID;
		}
		IStatus status = null;
		
		if (exception != null) {
			status = new Status(severity, pluginId, message, exception);
		} else {
			status = new Status(severity, pluginId, message);
		}
		
		log(status);
	}
	
	public static void log(IStatus status) {
		SIEditorCoreActivator.getDefault().getLog().log(status);
	}
	
	public static DebugTrace getDebugTrace() {
		return SIEditorCoreActivator.getDefault().getDebugTrace();
	}
	
	public static boolean isDebugEnabled() {
		return SIEditorCoreActivator.getDefault().isDebugging();
	}
}
