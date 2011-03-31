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
package org.eclipse.wst.sse.sieditor.test.ui.v2.dt.propertyEditor.typecommitters;

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.expect;

import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.ParameterTypeEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.typecommitters.ParameterTypeEditorTypeCommitter;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class ParameterTypeEditorTypeCommitterTest {

    private ParameterTypeEditor editor;
    private ParameterTypeEditorTypeCommitter committer;
    private SIFormPageController controller;
    
    private ITreeNode input;
    private IType type;
    
    private static final String TYPE_NAME = "ala-bala" + System.currentTimeMillis(); //$NON-NLS-1$
    
    @Before
    public void setUp() {
        editor = createMock(ParameterTypeEditor.class);
        controller = createMock(SIFormPageController.class);
        type = createMock(IType.class);
        input = createMock(ITreeNode.class);
    }
    
    @Test
    public void commitName() {
        expect(editor.getSiFormPageController()).andReturn(controller);
        editor.setInput(input);
        expect(editor.getInput()).andReturn(input);
        controller.editParameterTypeTriggered(input, TYPE_NAME);
        replay(editor, controller);

        committer = new ParameterTypeEditorTypeCommitter(editor.getSiFormPageController());
        committer.setInput(input);
        committer.commitName(type, TYPE_NAME);
        verify(controller);
    }

    @Test
    public void commitType() {
        expect(editor.getSiFormPageController()).andReturn(controller);
        expect(editor.getInput()).andReturn(input);
        controller.editParameterTypeTriggered(input, type);
        replay(editor, controller);
        
        committer = new ParameterTypeEditorTypeCommitter(editor.getSiFormPageController());
        committer.setInput(input);
        committer.commitType(type);
        verify(controller);
    }
    
}
