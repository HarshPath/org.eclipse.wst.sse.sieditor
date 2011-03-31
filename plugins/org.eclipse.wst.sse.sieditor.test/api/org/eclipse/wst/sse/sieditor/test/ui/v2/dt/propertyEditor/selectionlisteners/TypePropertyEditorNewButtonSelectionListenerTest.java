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
package org.eclipse.wst.sse.sieditor.test.ui.v2.dt.propertyEditor.selectionlisteners;

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.ITypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.NewTypeDialog;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.TypePropertyEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.selectionlisteners.TypePropertyEditorNewButtonSelectionListener;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class TypePropertyEditorNewButtonSelectionListenerTest {

    private ITypeDialogStrategy strategy;
    private NewTypeDialog newTypeDialog;
    private TypePropertyEditor propertyEditor;
    private AbstractFormPageController controller;

    private IStatus okStatus;
    private IStatus errorStatus;

    private ISchema schemaMock;

    private TypePropertyEditorNewButtonSelectionListener listener;

    private String newTypeToCreate;
    private String newTypeNameToCreate;

    private static final String TYPE_NAME = "type" + System.currentTimeMillis(); //$NON-NLS-1$

    @Before
    public void setUp() {
        strategy = createMock(ITypeDialogStrategy.class);
        newTypeDialog = createMock(NewTypeDialog.class);
        propertyEditor = createMock(TypePropertyEditor.class);
        controller = createMock(AbstractFormPageController.class);

        schemaMock = createMock(ISchema.class);
        okStatus = new Status(Status.OK, "test.plugin", null); //$NON-NLS-1$
        errorStatus = new Status(Status.ERROR, "test.plugin", null); //$NON-NLS-1$

        listener = new TypePropertyEditorNewButtonSelectionListener(propertyEditor) {
            @Override
            protected NewTypeDialog createNewTypeDialog(final ITypeDialogStrategy strategy) {
                return newTypeDialog;
            }

            @Override
            protected void newType(final String newTypeType, final String newTypeName, final ITypeDialogStrategy strategy) {
                assertSame(TypePropertyEditorNewButtonSelectionListenerTest.this.strategy, strategy);
                newTypeToCreate = newTypeType;
                newTypeNameToCreate = newTypeName;
                super.newType(newTypeType, newTypeName, strategy);
            }
        };
    }

    @Test
    public void widgetSelected_ErrorStatus() {
        expect(propertyEditor.createNewTypeDialogStrategy()).andReturn(strategy);
        expect(newTypeDialog.createAndOpen()).andReturn(errorStatus);
        replay(propertyEditor, newTypeDialog);
        listener.widgetSelected(null);
    }

    @Test
    public void widgetSelected_CreateNewType_ElementType() {
        setUpMocks(NewTypeDialog.RADIO_SELECTION_ELEMENT);
        controller.newElementType(TYPE_NAME, schemaMock, propertyEditor);
        replay(strategy, schemaMock);
        replay(propertyEditor, controller, newTypeDialog);
        listener.widgetSelected(null);
        assertEquals(TYPE_NAME, newTypeNameToCreate);
        assertEquals(NewTypeDialog.RADIO_SELECTION_ELEMENT, newTypeToCreate);
        verify(controller);
    }

    @Test
    public void widgetSelected_CreateNewType_StructureType() {
        setUpMocks(NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE);
        controller.newStructureType(TYPE_NAME, schemaMock, propertyEditor);
        replay(strategy, schemaMock);
        replay(propertyEditor, controller, newTypeDialog);
        listener.widgetSelected(null);
        assertEquals(TYPE_NAME, newTypeNameToCreate);
        assertEquals(NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE, newTypeToCreate);
        verify(controller);
    }

    @Test
    public void widgetSelected_CreateNewType_SimpleType() {
        setUpMocks(NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE);
        controller.newSimpleType(TYPE_NAME, schemaMock, propertyEditor);
        replay(strategy, schemaMock);
        replay(propertyEditor, controller, newTypeDialog);
        listener.widgetSelected(null);
        assertEquals(TYPE_NAME, newTypeNameToCreate);
        assertEquals(NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE, newTypeToCreate);
        verify(controller);
    }
    
    // ===========================================================
    // helpers
    // ===========================================================
    
    private void setUpMocks(final String elementType) {
        expect(propertyEditor.createNewTypeDialogStrategy()).andReturn(strategy);
        expect(propertyEditor.getFormPageController()).andReturn(controller);
        expect(strategy.getSchema()).andReturn(schemaMock);
        expect(newTypeDialog.createAndOpen()).andReturn(okStatus);
        expect(newTypeDialog.getNewTypeName()).andReturn(TYPE_NAME);
        expect(newTypeDialog.getNewTypeType()).andReturn(elementType);
    }

}
