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
package org.eclipse.wst.sse.sieditor.test.ui.readonly;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.IElementStrategy;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;

public class ElementNodeDetailsControllerReadOnlyTest {

    private static class TestDetailsController extends ElementNodeDetailsController {

        public IElementStrategy strategy;
        
        public void setStrategy(final IElementStrategy strategy) {
            this.strategy = strategy;
            setInput(null);
        }

        public TestDetailsController(final IDataTypesFormPageController formPageController) {
            super(formPageController);
        }

        @Override
        protected IElementStrategy calculateStrategy(final ITreeNode input) {
            return strategy;
        }

    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testIsNameEditable() {
        final TestDetailsController detailsController = new TestDetailsController(createMock(IDataTypesFormPageController.class));
        final IElementStrategy strategyMock = createMock(IElementStrategy.class);
        strategyMock.setInput(null);
        // attempt 1
        expect(strategyMock.isNameEditable()).andReturn(Boolean.valueOf(false));
        // attempt 2
        expect(strategyMock.isNameEditable()).andReturn(Boolean.valueOf(true));
        replay(strategyMock);
        detailsController.setStrategy(strategyMock);

        // attempt1
        assertFalse(detailsController.isNameEditable());
        // attempt2
        assertTrue(detailsController.isNameEditable());

        verify(strategyMock);
    }

    @Test
    public final void testIsNamespaceEditable() {
        final TestDetailsController detailsController = new TestDetailsController(createMock(IDataTypesFormPageController.class));
        final IElementStrategy strategyMock = createMock(IElementStrategy.class);
        strategyMock.setInput(null);
        // attempt 1
        expect(strategyMock.isNamespaceEditable()).andReturn(Boolean.valueOf(false));
        // attempt 2
        expect(strategyMock.isNamespaceEditable()).andReturn(Boolean.valueOf(true));
        replay(strategyMock);
        detailsController.setStrategy(strategyMock);

        // attempt1
        assertFalse(detailsController.isNamespaceEditable());
        // attempt2
        assertTrue(detailsController.isNamespaceEditable());

        verify(strategyMock);
    }

    @Test
    public final void testIsNillableEditable() {
        final TestDetailsController detailsController = new TestDetailsController(createMock(IDataTypesFormPageController.class));
        final IElementStrategy strategyMock = createMock(IElementStrategy.class);
        strategyMock.setInput(null);
        // attempt 1
        expect(strategyMock.isNillableEditable()).andReturn(Boolean.valueOf(false));
        // attempt 2
        expect(strategyMock.isNillableEditable()).andReturn(Boolean.valueOf(true));
        replay(strategyMock);
        detailsController.setStrategy(strategyMock);

        // attempt1
        assertFalse(detailsController.isNillableEditable());
        // attempt2
        assertTrue(detailsController.isNillableEditable());

        verify(strategyMock);
    }

    @Test
    public final void testIsCardinalityEditable() {
        final TestDetailsController detailsController = new TestDetailsController(createMock(IDataTypesFormPageController.class));
        final IElementStrategy strategyMock = createMock(IElementStrategy.class);
        strategyMock.setInput(null);
        // attempt 1
        expect(strategyMock.isCardinalityEditable()).andReturn(Boolean.valueOf(false));
        // attempt 2
        expect(strategyMock.isCardinalityEditable()).andReturn(Boolean.valueOf(true));
        replay(strategyMock);
        detailsController.setStrategy(strategyMock);

        // attempt1
        assertFalse(detailsController.isCardinalityEditable());
        // attempt2
        assertTrue(detailsController.isCardinalityEditable());

        verify(strategyMock);
    }

    @Test
    public final void testIsBaseTypeEditable() {
        final TestDetailsController detailsController = new TestDetailsController(createMock(IDataTypesFormPageController.class));
        final IElementStrategy strategyMock = createMock(IElementStrategy.class);
        strategyMock.setInput(null);
        // attempt 1
        expect(strategyMock.isBaseTypeEditable()).andReturn(Boolean.valueOf(false));
        // attempt 2
        expect(strategyMock.isBaseTypeEditable()).andReturn(Boolean.valueOf(true));
        replay(strategyMock);
        detailsController.setStrategy(strategyMock);

        // attempt1
        assertFalse(detailsController.isBaseTypeEditable());
        // attempt2
        assertTrue(detailsController.isBaseTypeEditable());

        verify(strategyMock);
    }

}
