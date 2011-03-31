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
package org.eclipse.wst.sse.sieditor.test.ui.v2.newtypedialog;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.LocalElementDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.NewTypeDialog;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

/**
 *
 * 
 */
public class LocalElementDialogStrategyTest {

    private static final String NEW_SIMPLE_TYPE_GENERATED_NAME = "SimpleType1";
    private static final String NEW_STRUCTURE_TYPE_GENERATED_NAME = "StryctyreType1";
    private static final String NEW_ELEMENT_GENERATED_NAME = "Element1";

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    private IElement elementInput;
    private ISchema schemaMock;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    private static class LocalElementDialogStrategyExposer extends LocalElementDialogStrategy {

        public String abstractsGetDefaultNameExpose(final ISchema schema, final String type) {
            return getDefaultName(schema, type);
        }

        public boolean abstractIsGlobalElementNameNotDuplicateExpose(final ISchema schema, final String name, final String type) {
            return isGlobalElementNameNotDuplicate(schema, name, type);
        }

        @Override
        protected ISchema findParentSchema() {
            return super.findParentSchema();
        }
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.LocalElementDialogStrategy#getDefaultName(java.lang.String)}
     * .
     */
    @Test
    public final void testGetDefaultNameString() {
        final LocalElementDialogStrategyExposer strategy = createAndInitStrategy();

        final IStructureType type1 = createMock(IStructureType.class);
        final IStructureType type2 = createMock(IStructureType.class);
        final ISimpleType type3 = createMock(ISimpleType.class);

        final IType[] types = new IType[] { type1, type2 };

        expect(type1.isElement()).andReturn(true).anyTimes();
        expect(type2.isElement()).andReturn(false).anyTimes();

        expect(schemaMock.getAllTypes(NEW_SIMPLE_TYPE_GENERATED_NAME)).andReturn(types).atLeastOnce();
        replay(schemaMock, type1, type2);

        assertEquals(strategy.abstractsGetDefaultNameExpose(schemaMock, NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE), strategy
                .getDefaultName(NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE));
        assertEquals(strategy.abstractsGetDefaultNameExpose(schemaMock, NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE), strategy
                .getDefaultName(NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE));
        assertEquals(strategy.abstractsGetDefaultNameExpose(schemaMock, NewTypeDialog.RADIO_SELECTION_ELEMENT), strategy
                .getDefaultName(NewTypeDialog.RADIO_SELECTION_ELEMENT));

        verify(elementInput, schemaMock, type1, type2);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.LocalElementDialogStrategy#isDuplicateName(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public final void testIsDuplicateName() {
        final LocalElementDialogStrategyExposer strategy = createAndInitStrategy();

        final IStructureType type1 = createMock(IStructureType.class);
        final IStructureType type2 = createMock(IStructureType.class);
        final ISimpleType type3 = createMock(ISimpleType.class);

        final IType[] types = new IType[] { type1, type2, type3 };

        expect(type1.isElement()).andReturn(true).anyTimes();
        expect(type2.isElement()).andReturn(false).anyTimes();

        expect(schemaMock.getAllTypes(NEW_ELEMENT_GENERATED_NAME)).andReturn(types).atLeastOnce();
        expect(schemaMock.getAllTypes(NEW_STRUCTURE_TYPE_GENERATED_NAME)).andReturn(types).atLeastOnce();
        expect(schemaMock.getAllTypes(NEW_SIMPLE_TYPE_GENERATED_NAME)).andReturn(types).atLeastOnce();
        replay(schemaMock, type1, type2);

        assertEquals(!strategy.abstractIsGlobalElementNameNotDuplicateExpose(schemaMock, NEW_ELEMENT_GENERATED_NAME,
                NewTypeDialog.RADIO_SELECTION_ELEMENT), strategy.isDuplicateName(NEW_ELEMENT_GENERATED_NAME,
                NewTypeDialog.RADIO_SELECTION_ELEMENT));

        assertEquals(!strategy.abstractIsGlobalElementNameNotDuplicateExpose(schemaMock, NEW_STRUCTURE_TYPE_GENERATED_NAME,
                NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE), strategy.isDuplicateName(NEW_STRUCTURE_TYPE_GENERATED_NAME,
                NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE));

        assertEquals(!strategy.abstractIsGlobalElementNameNotDuplicateExpose(schemaMock, NEW_SIMPLE_TYPE_GENERATED_NAME,
                NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE), strategy.isDuplicateName(NEW_SIMPLE_TYPE_GENERATED_NAME,
                NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE));

        verify(elementInput, schemaMock, type1, type2);
    }

    @Test
    public final void testFindParentSchema() {
        final LocalElementDialogStrategyExposer strategy = createAndInitStrategy();
        reset(elementInput);
        final IModelObject parent = createMock(IModelObject.class);
        expect(elementInput.getParent()).andReturn(parent);
        expect(parent.getParent()).andReturn(schemaMock);
        replay(elementInput, parent);

        assertEquals(schemaMock, strategy.findParentSchema());

        verify(elementInput, parent);

        reset(elementInput, parent);
        expect(elementInput.getParent()).andReturn(parent);
        expect(parent.getParent()).andReturn(null);
        replay(elementInput, parent);
        assertEquals(null, strategy.findParentSchema());
        verify(elementInput, parent);
    }

    private LocalElementDialogStrategyExposer createAndInitStrategy() {
        final LocalElementDialogStrategyExposer strategy = new LocalElementDialogStrategyExposer();
        elementInput = createNiceMock(IElement.class);
        schemaMock = createNiceMock(ISchema.class);
        expect(elementInput.getParent()).andReturn(schemaMock).atLeastOnce();
        replay(elementInput);
        strategy.setInput(elementInput);
        return strategy;
    }
}
