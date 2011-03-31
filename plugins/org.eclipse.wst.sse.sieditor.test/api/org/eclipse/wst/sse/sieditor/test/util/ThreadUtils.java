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
package org.eclipse.wst.sse.sieditor.test.util;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.swt.widgets.Display;

public class ThreadUtils {
	
	public static void waitOutOfUI(final long milliseconds) {
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException,
					InterruptedException {
				Thread.sleep(milliseconds);
			}
		};
		try {
			ModalContext.run(runnable , true, new NullProgressMonitor(), Display.getDefault());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	public static void callOutOfUI(IRunnableWithProgress runnable) {
		try {
			ModalContext.run(runnable , true, new NullProgressMonitor(), Display.getDefault());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
}
