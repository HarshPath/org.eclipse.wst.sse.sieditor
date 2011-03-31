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
package org.eclipse.wst.sse.sieditor.test.fwk.mvp.testsupport;

/**
 * 
 * @param <T>
 */
public class Argument<T> {

    private T t;

    /**
     * Returns the value.
     * 
     * @return The arguments value or <code>null</code>
     */
    public T getValue() {
        return t;
    }

    /**
     * @param t
     *            The value to set
     */
    public void setValue(final T t) {
        this.t = t;
    }
}
