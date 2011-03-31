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

import static org.easymock.EasyMock.createNiceMock;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.BasePresenter;
import org.eclipse.wst.sse.sieditor.fwk.mvp.ui.IView;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class BasePresenterTest {

    private final class MockPresenter extends BasePresenter<IView> {

        private boolean isAttachViewListenersCalled;

        MockPresenter(final IView view) {
            super(view);
        }

        @Override
        protected void attachViewListeners() {
            isAttachViewListenersCalled = true;
        }
    }

    private MockPresenter mockPresenter;

    private IView view;

    @Before
    public void setUp() throws Exception {
        view = createNiceMock(IView.class);
        mockPresenter = new MockPresenter(view);
    }

    @Test
    public void testBasePresenter() {
        assertTrue(mockPresenter.isAttachViewListenersCalled);
        assertNotNull(mockPresenter.getView());
        assertSame(view, mockPresenter.getView());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBasePresenterEx() {
        new BasePresenter<IView>(null) {

            @Override
            protected void attachViewListeners() {
            }
        };
    }
}
