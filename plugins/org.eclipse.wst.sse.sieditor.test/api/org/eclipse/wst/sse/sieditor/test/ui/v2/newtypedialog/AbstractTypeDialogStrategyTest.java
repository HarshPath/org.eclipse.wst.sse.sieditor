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

import static org.junit.Assert.assertEquals;

import java.text.MessageFormat;

import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.AbstractTypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.NewTypeDialog;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

/**
 *
 * 
 */
public class AbstractTypeDialogStrategyTest {

    private static final String STRUCTURE_TYPE_NAME = "structureTypeName";
    private static final String SIMPLE_TYPE_NAME = "simpleTypeName";
    private static final String ELEMENT_NAME = "elementName";

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

    private static class AbstractTypeDialogStrategyInheritor extends AbstractTypeDialogStrategy {
        public IDataTypesFormPageController controllerMock;

        @Override
        protected IDataTypesFormPageController getDTController() {
            return controllerMock;
        }

        @Override
        public String getDefaultName(final String type) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean isDuplicateName(final String name, final String type) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public ISchema getSchema() {
            // TODO Auto-generated method stub
            return null;
        }

    }

//    /**
//     * Test method for
//     * {@link org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.AbstractTypeDialogStrategy#createGlobalType(java.lang.String, java.lang.String, org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema)}
//     * .
//     */
//    @Test
//    public final void testCreateGlobalType() {
//        final AbstractTypeDialogStrategyInheritor strategy = new AbstractTypeDialogStrategyInheritor();
//        strategy.controllerMock = createMock(IDataTypesFormPageController.class);
//        final ISchema schemaMock = createMock(ISchema.class);
//        final IStructureType newElementMock = createMock(IStructureType.class);
//        expect(strategy.controllerMock.addNewElement(schemaMock, ELEMENT_NAME)).andReturn(newElementMock);
//        final IStructureType newStructureTypeMock = createMock(IStructureType.class);
//        expect(strategy.controllerMock.addNewStructureType(schemaMock, STRUCTURE_TYPE_NAME)).andReturn(newStructureTypeMock);
//        final ISimpleType newSimpleTypeMock = createMock(ISimpleType.class);
//        expect(strategy.controllerMock.addNewSimpleType(schemaMock, SIMPLE_TYPE_NAME)).andReturn(newSimpleTypeMock);
//        // and for ne Status.CancelStatus result :
//        expect(strategy.controllerMock.addNewSimpleType(schemaMock, SIMPLE_TYPE_NAME)).andReturn(null);
//
//        replay(strategy.controllerMock);
//
//        assertEquals(Status.OK_STATUS, strategy.createGlobalType(NewTypeDialog.RADIO_SELECTION_ELEMENT, ELEMENT_NAME, schemaMock));
//        assertEquals(Status.OK_STATUS, strategy.createGlobalType(NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE,
//                STRUCTURE_TYPE_NAME, schemaMock));
//        assertEquals(Status.OK_STATUS, strategy.createGlobalType(NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE, SIMPLE_TYPE_NAME,
//                schemaMock));
//        // the expected cancel status result
//        assertEquals(Status.CANCEL_STATUS, strategy.createGlobalType(NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE, SIMPLE_TYPE_NAME,
//                schemaMock));
//
//        verify(strategy.controllerMock);
//    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.AbstractTypeDialogStrategy#getDuplicateNameErrorMessage(java.lang.String)}
     * .
     */
    @Test
    public final void testGetDuplicateNameErrorMessage() {
        // String elementType = UIConstants.EMPTY_STRING;
        // if (NewTypeDialog.RADIO_SELECTION_ELEMENT.equals(type)) {
        // elementType = Messages.AbstractTypeDialogStrategy_msg_error_element;
        // } else if (NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE.equals(type)) {
        // elementType =
        // Messages.AbstractTypeDialogStrategy_msg_error_simple_type;
        // } else if (NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE.equals(type))
        // {
        // elementType =
        // Messages.AbstractTypeDialogStrategy_msg_error_structure_type;
        // } else {
        // return
        // Messages.AbstractTypeDialogStrategy_msg_error_duplicate_element;
        // }
        // return
        // MessageFormat.format(Messages.AbstractTypeDialogStrategy_msg_error_duplicate_X,
        // elementType);
        final AbstractTypeDialogStrategyInheritor strategy = new AbstractTypeDialogStrategyInheritor();
        assertEquals(MessageFormat.format(Messages.AbstractTypeDialogStrategy_msg_error_duplicate_X,
                Messages.AbstractTypeDialogStrategy_msg_error_element), strategy
                .getDuplicateNameErrorMessage(NewTypeDialog.RADIO_SELECTION_ELEMENT));
        assertEquals(MessageFormat.format(Messages.AbstractTypeDialogStrategy_msg_error_duplicate_X,
                Messages.AbstractTypeDialogStrategy_msg_error_structure_type), strategy
                .getDuplicateNameErrorMessage(NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE));
        assertEquals(MessageFormat.format(Messages.AbstractTypeDialogStrategy_msg_error_duplicate_X,
                Messages.AbstractTypeDialogStrategy_msg_error_simple_type), strategy
                .getDuplicateNameErrorMessage(NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE));
        assertEquals(Messages.AbstractTypeDialogStrategy_msg_error_duplicate_element, strategy
                .getDuplicateNameErrorMessage(NewTypeDialog.RADIO_SELECTION_NONE));

    }

}
