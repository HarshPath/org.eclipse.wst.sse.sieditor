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
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IView;
import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IViewListener;
import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.ViewDelegate;

/**
 *
 */

@SuppressWarnings("nls")
public class ViewDelegateTest {

    private final class MockViewListener implements IViewListener {

        private boolean isViewCreatedCalled;

        private boolean isViewDisposeCalled;

        private IView viewCreatedArgument;

        private IView viewDisposeArgument;

        public void viewCreated(final IView view) {
            viewCreatedArgument = view;
            isViewCreatedCalled = true;
        }

        public void viewDispose(final IView view) {
            viewDisposeArgument = view;
            isViewDisposeCalled = true;
            viewDelegate.removeViewListener(this);
        }
    }

    private IView view;

    private ViewDelegate viewDelegate;

    private MockViewListener viewListener1;

    private MockViewListener viewListener2;

    @Before
    public void setUp() throws Exception {
        view = createMock(IView.class);
        viewDelegate = new ViewDelegate(view);
        // viewListener1 = createMock(IViewListener.class);
        // viewListener2 = createMock(IViewListener.class);
        viewListener1 = new MockViewListener();
        viewListener2 = new MockViewListener();
    }

    @Test
    public void testAddGetRemove() {

        assertTrue(viewDelegate.getViewListeners().isEmpty());

        assertTrue(viewDelegate.addViewListener(viewListener1));
        assertTrue(viewDelegate.addViewListener(createMock(IViewListener.class)));
        assertTrue(viewDelegate.addViewListener(createMock(IViewListener.class)));
        assertEquals(3, viewDelegate.getViewListeners().size());

        assertTrue(viewDelegate.removeViewListener(viewListener1));
        assertFalse(viewDelegate.removeViewListener(createMock(IViewListener.class)));
        assertEquals(2, viewDelegate.getViewListeners().size());

        try {
            viewDelegate.getViewListeners().add(viewListener1);
            fail("UnsupportedOperationException expected!");
        } catch (final UnsupportedOperationException e) { // That is expected!
        }
    }

    @Test
    public void testNotifyViewCreated() {

        // viewListener1.viewCreated(Arguments.any(viewArgument));
        // replay(viewListener1);
        // viewListener2.viewCreated(Arguments.any(viewArgument));
        // replay(viewListener2);

        viewDelegate.addViewListener(viewListener1);
        viewDelegate.addViewListener(viewListener2);

        viewDelegate.notifyViewCreated();
        assertTrue(viewListener1.isViewCreatedCalled);
        assertSame(view, viewListener1.viewCreatedArgument);
        assertTrue(viewListener2.isViewCreatedCalled);
        assertSame(view, viewListener2.viewCreatedArgument);
        // verify(viewListener1);
        // verify(viewListener2);
    }

    @Test
    public void testNotifyViewDispose() {

        // viewListener1.viewDispose(Arguments.any(viewArgument));
        // replay(viewListener1);
        // viewListener2.viewDispose(Arguments.any(viewArgument));
        // replay(viewListener2);

        expect(view.removeViewListener(isA(IViewListener.class))).andReturn(true);
        expectLastCall().times(2);
        replay(view);

        viewDelegate.addViewListener(viewListener1);
        viewDelegate.addViewListener(viewListener2);

        viewDelegate.notifyViewDispose();
        assertTrue(viewListener1.isViewDisposeCalled);
        assertSame(view, viewListener1.viewDisposeArgument);
        assertTrue(viewListener2.isViewDisposeCalled);
        assertSame(view, viewListener2.viewDisposeArgument);
        // verify(viewListener1);
        // verify(viewListener2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testViewDelegate() {
        new ViewDelegate(null);
    }
}
