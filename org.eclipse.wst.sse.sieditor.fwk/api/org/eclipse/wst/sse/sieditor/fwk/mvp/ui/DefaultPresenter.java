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

//import org.eclipse.wst.sse.sieditor.util.log.SmartLog;

/**
 * Default implementation of a MVP-presenter which attaches to the MVP-view as
 * {@link IViewListener}. Handling of MVP-view creation is delegated to the
 * {@link #handleViewCreated(IView)} hook method. Handling of MVP-view disposal
 * is delegated to the {@link #handleViewDispose(IView)} hook method, thereafter
 * this MVP-presenter is detached from the MVP-view.
 * 
 *  * @param <T>
 *            The type of the MVP-view.
 * @deprecated the old ui layer architecture is not being used any more rendering this class obsolete
 */
@Deprecated
public abstract class DefaultPresenter<T extends IView> extends
    BasePresenter<T> {

    //static final SmartLog LOG = SmartLog.getSmartLog(DefaultPresenter.class);

    /**
     * @param view
     *            The MVP-view.
     * @see BasePresenter#BasePresenter(IView)
     */
    public DefaultPresenter(final T view) {
        super(view);
    }

    /**
     * Attaches to the MVP-view as {@link IViewListener} and delegates events to
     * {@link #handleViewCreated(IView)} or {@link #handleViewDispose(IView)}.
     * 
     * @see org.eclipse.wst.sse.sieditor.fwk.mvp.ui.BasePresenter#attachViewListeners()
     */
    @Override
    protected final void attachViewListeners() {

        getView().addViewListener(new IViewListener() {

            public void viewCreated(final IView view) {
                handleViewCreated(getView());
            }

            public void viewDispose(final IView view) {
                handleViewDispose(getView());
                getView().removeViewListener(this);
            }
        });
    }

    /**
     * Hook for handling the creation of a MVP-view. This implementation does
     * nothing.
     * 
     * @param view
     *            The MVP-view that was created.
     */
    protected void handleViewCreated(final T view) {
    }

    /**
     * Hook for handling the disposal of a MVP-view. This implementation does
     * nothing.
     * 
     * @param view
     *            The MVP-view that is to be disposed.
     */
    protected void handleViewDispose(final T view) {
    }
}
