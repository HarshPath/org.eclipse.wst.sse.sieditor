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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.TypePropertyEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.selectionlisteners.TypePropertyEditorTypeComboEventListener;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

public class TypePropertyEditorTypeComboEventListenerTest {

    private AbstractFormPageController controller;
    private ITreeNode input;
    private IModelObject model;
    private TypePropertyEditor propertyEditor;
    private CCombo typeCombo;

    private TypePropertyEditorTypeComboEventListener listener;

    private IType typeResult;
    
    private IType selectedTypeMock;
    private String nameResult;

    private IType typeMock;

    private static final String DISPLAY_TEXT = "Test Display Text " + System.currentTimeMillis(); //$NON-NLS-1$
    private static final boolean SHOW_COMPLEX_TYPES = false;

    private static final String SELECTED_TYPE_NAME = "ala-bala-type"; //$NON-NLS-1$

    @Before
    public void setUp() {
        propertyEditor = createMock(TypePropertyEditor.class);
        controller = createMock(AbstractFormPageController.class);

        typeCombo = new CCombo(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SINGLE);
        typeCombo.add(Messages.TypePropertyEditor_browse_button);
        typeCombo.add(SELECTED_TYPE_NAME);

        input = createMock(ITreeNode.class);
        model = createMock(IModelObject.class);
        typeMock = createMock(IType.class);
        
        selectedTypeMock = createMock(IType.class);

        listener = new TypePropertyEditorTypeComboEventListener(propertyEditor) {
            @Override
            protected void setType(final IType type) {
                typeResult = type;
            }

            @Override
            protected void setType(final String coreSimpleTypeName) {
                nameResult = coreSimpleTypeName;
            }

            @Override
            protected String getSelectedTypeName(final IType selectedType, final ITreeNode input) {
                return SELECTED_TYPE_NAME;
            }
        };
    }

    @Test
    public void widgetSelected_BrowseSelected() {
        expect(propertyEditor.getTypeCombo()).andReturn(typeCombo);

        expect(propertyEditor.getFormPageController()).andReturn(controller);
        expect(propertyEditor.getInput()).andReturn(input).anyTimes();
        expect(input.getModelObject()).andReturn(model);
        expect(propertyEditor.getTypeDialogDisplayText()).andReturn(DISPLAY_TEXT);
        expect(propertyEditor.showComplexTypes()).andReturn(SHOW_COMPLEX_TYPES);
        expect(propertyEditor.getSelectedType()).andReturn(selectedTypeMock);
        expect(controller.openTypesDialog(DISPLAY_TEXT, model, SHOW_COMPLEX_TYPES)).andReturn(typeMock);

        //select "Browse..." (first index)
        typeCombo.select(0);

        replay(propertyEditor, controller);
        replay(input);

        listener.focusLost(null);
        assertSame(typeMock, typeResult);

        verify(controller);
    }

    @Test
    public void widgetSelected_TypeSelected() {
        expect(propertyEditor.getTypeCombo()).andReturn(typeCombo);

        // select SELECTED_TYPE_NAME
        typeCombo.select(1); 
        replay( propertyEditor);

        listener.focusLost(null);
        assertEquals(SELECTED_TYPE_NAME, nameResult);
    }

}
