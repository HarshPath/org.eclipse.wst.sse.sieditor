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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertSame;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.TypePropertyEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.selectionlisteners.TypePropertyEditorBrowseButtonSelectionListener;
import org.junit.Before;
import org.junit.Test;

public class TypePropertyEditorBrowseButtonSelectionListenerTest {

    private AbstractFormPageController controller;
    private ITreeNode input;
    private IModelObject model;
    private TypePropertyEditor propertyEditor;

    private TypePropertyEditorBrowseButtonSelectionListener listener;
    
    private IType typeResult;
    private IType typeMock;

    private static final String DISPLAY_TEXT = "Test Display Text " + System.currentTimeMillis(); //$NON-NLS-1$
    private static final boolean SHOW_COMPLEX_TYPES = false;
    
    @Before
    public void setUp() {
        propertyEditor = createMock(TypePropertyEditor.class);
        controller = createMock(AbstractFormPageController.class);
        input = createMock(ITreeNode.class);
        model = createMock(IModelObject.class);
        typeMock = createMock(IType.class);
        
        listener = new TypePropertyEditorBrowseButtonSelectionListener(propertyEditor) {
            @Override
            protected void setType(final IType type) {
                typeResult = type;
            }
        };
    }

    @Test
    public void widgetSelected() {
        expect(propertyEditor.getFormPageController()).andReturn(controller);
        expect(propertyEditor.getInput()).andReturn(input);
        expect(input.getModelObject()).andReturn(model);
        expect(propertyEditor.getTypeDialogDisplayText()).andReturn(DISPLAY_TEXT);
        expect(propertyEditor.showComplexTypes()).andReturn(SHOW_COMPLEX_TYPES);
        expect(controller.openTypesDialog(DISPLAY_TEXT, model, SHOW_COMPLEX_TYPES)).andReturn(typeMock);

        replay(propertyEditor, controller);
        replay(input);

        listener.widgetSelected(null);
        assertSame(typeMock, typeResult);
        
        verify(controller);
    }
    

}
