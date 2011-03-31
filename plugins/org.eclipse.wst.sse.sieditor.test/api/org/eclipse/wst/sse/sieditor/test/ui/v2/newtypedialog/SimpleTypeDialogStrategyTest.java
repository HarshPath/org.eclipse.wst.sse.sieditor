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
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.NewTypeDialog;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.SimpleTypeDialogStrategy;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

/**
 *
 * 
 */
public class SimpleTypeDialogStrategyTest {

    private static final String SAMPLE_NAME = "sampleName"; //$NON-NLS-1$
    private static final String NEW_GENERATED_NAME = DataTypesFormPageController.SIMPLE_TYPE_DEFAULT_NAME + "1";;
    private ISimpleType simpleTypeInput;
    private ISchema schemaMock;

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

    private static class SimpleTypeDialogStrategyExposer extends SimpleTypeDialogStrategy {
        public String type;
        public String name;
        public ISchema schema;

        public String abstractsGetDefaultNameExpose(final ISchema schema, final String type) {
            return getDefaultName(schema, type);
        }

        public boolean abstractIsGlobalElementNameNotDuplicateExpose(final ISchema schema, final String name, final String type) {
            return isGlobalElementNameNotDuplicate(schema, name, type);
        }
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.SimpleTypeDialogStrategy#getDialogTitle()}
     * .
     */
    @Test
    public final void testGetDialogTitle() {
        final SimpleTypeDialogStrategy strategy = new SimpleTypeDialogStrategy();
        assertEquals(Messages.SimpleTypeDialogStrategy_window_title_new_base_type_dialog, strategy.getDialogTitle());
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.SimpleTypeDialogStrategy#isElementEnabled()}
     * .
     */
    @Test
    public final void testIsRadioButtonsEnablement() {
        final SimpleTypeDialogStrategy strategy = new SimpleTypeDialogStrategy();
        assertFalse(strategy.isElementEnabled());
        assertFalse(strategy.isStructureTypeEnabled());
        assertTrue(strategy.isSimpleTypeEnabled());
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.SimpleTypeDialogStrategy#getDefaultName(java.lang.String)}
     * .
     */
    @Test
    public final void testGetDefaultNameString() {
        final SimpleTypeDialogStrategyExposer strategy = createAndInitStrategy();
        final IType type1 = createMock(IStructureType.class);
        final IType type2 = createMock(ISimpleType.class);
        final IType[] types = new IType[] { type1, type2 };

        expect(schemaMock.getAllTypes(NEW_GENERATED_NAME)).andReturn(types).atLeastOnce();
        replay(schemaMock);

        assertEquals(strategy.abstractsGetDefaultNameExpose(schemaMock, NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE), strategy
                .getDefaultName(NewTypeDialog.RADIO_SELECTION_ELEMENT));
        verify(simpleTypeInput, schemaMock);

    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.SimpleTypeDialogStrategy#isDuplicateName(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public final void testIsDuplicateName() {
        SimpleTypeDialogStrategyExposer strategy;
        strategy = createAndInitStrategy();

        final IType type1 = createNiceMock(IStructureType.class);
        final IType type2 = createNiceMock(ISimpleType.class);
        final IType[] types = new IType[] { type1, type2 };
        expect(schemaMock.getAllTypes(NEW_GENERATED_NAME)).andReturn(types).atLeastOnce();
        replay(schemaMock, type1, type2);

        assertEquals(!strategy.abstractIsGlobalElementNameNotDuplicateExpose(schemaMock, NEW_GENERATED_NAME,
                NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE), strategy.isDuplicateName(NEW_GENERATED_NAME,
                NewTypeDialog.RADIO_SELECTION_ELEMENT));

        verify(simpleTypeInput, schemaMock, type1, type2);
    }

    private SimpleTypeDialogStrategyExposer createAndInitStrategy() {
        final SimpleTypeDialogStrategyExposer strategy = new SimpleTypeDialogStrategyExposer();
        simpleTypeInput = createNiceMock(ISimpleType.class);
        schemaMock = createNiceMock(ISchema.class);
        expect(simpleTypeInput.getParent()).andReturn(schemaMock).atLeastOnce();
        replay(simpleTypeInput);
        strategy.setInput(simpleTypeInput);
        return strategy;
    }

}
