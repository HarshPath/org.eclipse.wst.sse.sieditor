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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.swt.widgets.Composite;

/**
 * Supports implementing an MVP-view if it is not possible to use one of the
 * base classes provided by MVP.
 * 
 * @see IView
 * @deprecated the old ui layer architecture is not being used any more rendering this class obsolete
 */
@Deprecated
public class ViewDelegate implements IView {

    /**
     * The registered {@link IViewListener}s.
     */
    protected Collection<IViewListener> viewListeners =
        Collections.synchronizedCollection(new HashSet<IViewListener>());

    private final IView view;

    /**
     * @param view
     *            The MVP-view that delegates to this. Must not be null!
     */
    public ViewDelegate(final IView view) {
        if (view == null) {
            throw new IllegalArgumentException("The view must not be null"); //$NON-NLS-1$
        }
        this.view = view;
    }

    /**
     * @see org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IView#addViewListener(org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IViewListener)
     */
    public boolean addViewListener(final IViewListener viewListener) {
        return viewListeners.add(viewListener);
    }

    /**
     * @return The {@link IViewListener}s as unmodifiable.
     */
    public Collection<IViewListener> getViewListeners() {
        return Collections.unmodifiableCollection(viewListeners);
    }

    /**
     * Notifies the {@link IViewListener}s that a view was created.
     */
    public void notifyViewCreated() {
        // Make a copy to enable modifications of during iteration.
        final Collection<IViewListener> localViewListeners =
            new ArrayList<IViewListener>(viewListeners);
        for (final IViewListener viewListener : localViewListeners) {
            viewListener.viewCreated(view);
        }
    }

    /**
     * Notifies the {@link IViewListener}s that a view is about to be disposed.
     */
    public void notifyViewDispose() {
        // Make a copy to enable modifications of during iteration.
        final Collection<IViewListener> localViewListeners =
            new ArrayList<IViewListener>(viewListeners);
        for (final IViewListener viewListener : localViewListeners) {
            viewListener.viewDispose(view);
        }
    }

    /**
     * @see org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IView#removeViewListener(org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IViewListener)
     */
    public boolean removeViewListener(final IViewListener viewListener) {
        return viewListeners.remove(viewListener);
    }

    /**
     * this returns an SWT Composite and is used to create its children
     */
    public Composite getUIHost() {
		return view.getUIHost();
	}


}
