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
package org.eclipse.wst.sse.sieditor.test.fwk.mvp.ui;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.DefaultPresenter;
import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IView;
import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IViewListener;

import org.eclipse.wst.sse.sieditor.test.fwk.mvp.testsupport.Argument;
import org.eclipse.wst.sse.sieditor.test.fwk.mvp.testsupport.Arguments;
import org.eclipse.wst.sse.sieditor.test.fwk.mvp.testsupport.ui.AbstractPresenterTest;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class DefaultPresenterTest extends AbstractPresenterTest {

    private final class MockDefaultPresenter<T extends IView> extends
        DefaultPresenter<IView> {

        private boolean isHandleViewCreatedCalled;

        private boolean isHandleViewDisposeCalled;

        public MockDefaultPresenter(final IView view) {
            super(view);
        }

        @Override
        protected void handleViewCreated(final IView view) {
            super.handleViewCreated(view);
            isHandleViewCreatedCalled = true;
        }

        @Override
        protected void handleViewDispose(final IView view) {
            isHandleViewDisposeCalled = true;
            super.handleViewDispose(view);
        }
    }

    private MockDefaultPresenter<IView> mockDefaultPresenter;

    private Argument<IViewListener> removeViewListenerArgument;

    private IView view;

    @Before
    public void setUp() {
        view = createMock(IView.class);
        removeViewListenerArgument = new Argument<IViewListener>();
        setUpViewExpectations(view);
    }

    @Test
    public void testAttachViewListeners() {
        replay(view);
        mockDefaultPresenter = new MockDefaultPresenter<IView>(view);
        verify(view);
    }

    @Test
    public void testHandleViewCreated() {
        replay(view);
        mockDefaultPresenter = new MockDefaultPresenter<IView>(view);
        fireViewCreated(view);
        assertTrue(mockDefaultPresenter.isHandleViewCreatedCalled);
        verify(view);
    }

    @Test
    public void testHandleViewDispose() {
        expect(
            view.removeViewListener(Arguments.any(removeViewListenerArgument)))
            .andReturn(true);
        replay(view);
        mockDefaultPresenter = new MockDefaultPresenter<IView>(view);

        //        addViewListenerArgument.getValue().viewDispose(view);
        fireViewDisposed(view);
        assertTrue(mockDefaultPresenter.isHandleViewDisposeCalled);
        assertSame(viewListener.getValue(), removeViewListenerArgument
            .getValue());
        verify(view);
    }
}
