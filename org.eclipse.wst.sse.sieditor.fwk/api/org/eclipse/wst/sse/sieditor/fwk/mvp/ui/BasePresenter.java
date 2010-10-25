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

/**
 * Base implementation of a MVP-presenter which provides a hook method for
 * attaching to the MVP-view as {@link IViewListener} during construction.
 * Clients should consider extending {@link DefaultPresenter} which provides
 * enhanced features.
 * 
 *  * @param <T>
 *            The type of the MVP-view.
 * @deprecated the old ui layer architecture is not being used any more rendering this class obsolete
 */
@Deprecated
public abstract class BasePresenter<T extends IView> implements IPresenter<T> {

    private final T view;

    /**
     * @param view
     *            The MVP-view. Must not be null!
     * @throws IllegalArgumentException
     *             if the given MVP-view is null.
     */
    public BasePresenter(final T view) {
        if (view == null) {
            throw new IllegalArgumentException("The view must not be null"); //$NON-NLS-1$
        }
        this.view = view;
        attachViewListeners();
    }

    /**
     * @return The MVP-view.
     * @see org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IPresenter#getView()
     */
    public final T getView() {
        return view;
    }

    /**
     * This hook for attaching to a MVP-view as {@link IViewListener} is called
     * during construction.
     */
    protected abstract void attachViewListeners();
}
