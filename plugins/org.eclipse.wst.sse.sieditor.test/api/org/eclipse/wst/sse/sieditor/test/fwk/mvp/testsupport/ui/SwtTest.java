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
package org.eclipse.wst.sse.sieditor.test.fwk.mvp.testsupport.ui;

import org.eclipse.swt.widgets.Display;

/**
 * Base test for SWT tests giving subclasses access to protected a
 * {@link Display} field.
 * 
 * 
 */
public class SwtTest {

    /**
     * {@link Display} instance for usage in the tests. Will be created in
     * {@link #setUp()} and closed in {@link #tearDown()}.
     */
    protected Display display;

    /**
     * Creates a {@link Display}.
     * 
     * @throws Exception
     *             Any exception.
     */
    public void setUp() throws Exception {
        display = Display.getDefault();
    }

    /**
     * Closes the {@link Display}.
     * 
     * @throws Exception
     *             Any exception.
     */
    public void tearDown() throws Exception {
        if (display != null) {
            display.close();
        }
    }
}
