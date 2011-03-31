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

import static org.easymock.EasyMock.expect;

import org.eclipse.wst.sse.sieditor.test.fwk.mvp.testsupport.Argument;
import org.eclipse.wst.sse.sieditor.test.fwk.mvp.testsupport.Arguments;

import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IView;
import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IViewListener;

/**
 * This abstract base class for presenter tests allows the test to easy fire
 * view- open and dispose lifecycle events to the presenter.
 * 
 * 
 */
public abstract class AbstractPresenterTest {

    /**
     * 
     */
    protected Argument<IViewListener> viewListener;

    /**
     * Fires a view created event to the presenter. Ensure that
     * {@link AbstractPresenterTest#setUpViewExpectations(IView)} is called
     * before.
     * 
     * @param view
     */
    protected void fireViewCreated(final IView view) {
        viewListener.getValue().viewCreated(view);
    }

    /**
     * Fires a view disposed event to the presenter. Ensure that
     * {@link AbstractPresenterTest#setUpViewExpectations(IView)} is called
     * before.
     * 
     * @param view
     */
    protected void fireViewDisposed(final IView view) {
        viewListener.getValue().viewDispose(view);
    }

    /**
     * Sets up the expectation that view.addViewListener is called and saves the
     * listener in an {@link Argument}.
     * 
     * @param view
     */
    protected void setUpViewExpectations(final IView view) {
        viewListener = new Argument<IViewListener>();
        expect(view.addViewListener(Arguments.any(viewListener))).andReturn(
            true);
    }
}
