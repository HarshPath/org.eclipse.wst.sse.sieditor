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

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.TypePropertyEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.selectionlisteners.AbstractTypePropertyEditorEventListener;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.typecommitters.ITypeCommitter;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class AbstractTypePropertyEditorEventListenerTest {

    private TypePropertyEditor propertyEditor;
    private ITypeCommitter committer;

    private IType type;

    private static final String TYPE_NAME = "type name" + System.currentTimeMillis(); //$NON-NLS-1$

    @Before
    public void setUp() {
        type = createMock(IType.class);
        propertyEditor = createMock(TypePropertyEditor.class);
        committer = createMock(ITypeCommitter.class);
    }

    @Test
    public void setType_Type() {
        expect(propertyEditor.getTypeCommitter()).andReturn(committer);
        propertyEditor.setSelectedType(type);
        committer.commitType(type);

        replay(propertyEditor);
        replay(committer);

        new AbstractTypePropertyEditorEventListener(propertyEditor) {
            @Override
            public void widgetSelected(final SelectionEvent e) {
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
            }

            @Override
            public void setType(final IType type) {
                super.setType(type);
            }
        }.setType(type);

        verify(propertyEditor);
        verify(committer);
    }

    @Test
    public void setType_Name_StaleEditor() {
        expect(propertyEditor.getTypeCommitter()).andReturn(committer);

        propertyEditor.setSelectedTypeName(TYPE_NAME);
        expect(propertyEditor.getSelectedTypeName()).andReturn(TYPE_NAME);
        propertyEditor.setSelectedType(type);
        expect(propertyEditor.isStale()).andReturn(true);
        committer.commitName(type, TYPE_NAME);

        replay(propertyEditor);
        replay(committer);

        new AbstractTypePropertyEditorEventListener(propertyEditor) {
            @Override
            public void widgetSelected(final SelectionEvent e) {
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
            }

            @Override
            public void setType(final String typeName) {
                super.setType(typeName);
            }

            @Override
            protected IType getSelectedType(final String coreSimpleTypeName) {
                return type;
            };
        }.setType(TYPE_NAME);

        verify(propertyEditor);
        verify(committer);
    }
}
