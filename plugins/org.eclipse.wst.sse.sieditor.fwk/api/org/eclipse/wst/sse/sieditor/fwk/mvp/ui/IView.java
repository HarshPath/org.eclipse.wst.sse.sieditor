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
package org.eclipse.wst.sse.sieditor.fwk.mvp.ui;

import org.eclipse.swt.widgets.Composite;

/**
 * An MVP-view notifies about its creation. This feature is needed by its
 * presenters for view-population.
 * 
 * @deprecated the old ui layer architecture is not being used any more rendering this class obsolete
 */
@Deprecated
public interface IView {

    /**
     * Adds a {@link IViewListener}.
     * 
     * @param viewListener
     *            Listener to add.
     * @return true, if the given listener was added.
     */
    boolean addViewListener(IViewListener viewListener);

    /**
     * Removes a {@link IViewListener}.
     * 
     * @param viewListener
     *            Listener to remove.
     * @return true, if the given listener was removed.
     */
    boolean removeViewListener(IViewListener viewListener);

    /**
     * Returns the SWT Composite used as a parent to create child ui widgets
     * 
     */ 
    Composite getUIHost();  //if there is a general way to do this without SWT dependency it will be good
}
