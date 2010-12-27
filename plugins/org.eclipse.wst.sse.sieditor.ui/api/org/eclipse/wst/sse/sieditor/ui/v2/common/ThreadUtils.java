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
package org.eclipse.wst.sse.sieditor.ui.v2.common;

import org.eclipse.swt.widgets.Display;

public class ThreadUtils {

    /**
     * Executes 'runnable' in Display.getDefault().syncExec(runnable).
     * In case that the current thread is the main/UI one, it just calls
     * runnable.run().
     * All exceptions threw by 'runnable' are wrapped in RuntimeException and
     * are re-threw. In this way Display.syncExec(...) can not hide exception.
     * @param runnable that will be executed in the main/UI thread
     */
    public static void displaySyncExec(final Runnable runnable) {
        final Exception exception[] = new Exception[1];
        
        Runnable wrapRunnable = new Runnable() {
            public void run() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    exception[0] = e;
                }
                
            }
        };
        
        if(Display.getCurrent() == null) {
            Display.getDefault().syncExec(wrapRunnable);
        }
        else {
            runnable.run();
        }
        
        
        if(exception[0] != null) {
            throw new RuntimeException(exception[0]);
        }
    }
}
