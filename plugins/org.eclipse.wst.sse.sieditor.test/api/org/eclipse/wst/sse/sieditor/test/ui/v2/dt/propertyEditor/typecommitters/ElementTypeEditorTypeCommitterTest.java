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

import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.typecommitters.ElementTypeEditorTypeCommitter;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class ElementTypeEditorTypeCommitterTest {

    private ElementNodeDetailsController detailsController;
    private ElementTypeEditorTypeCommitter committer;
    
    private IType typeMock;
    
    @Before
    public void setUp() {
        detailsController = createMock(ElementNodeDetailsController.class);
        committer = new ElementTypeEditorTypeCommitter(detailsController);
        typeMock = createMock(IType.class);
    }
    
    @Test
    public void commitName() {
        detailsController.setType(typeMock);
        replay(detailsController);
        committer.commitName(typeMock, null);
        verify(detailsController);
    }

    @Test
    public void commitType() {
        detailsController.setType(typeMock);
        replay(detailsController);
        committer.commitType(typeMock);
        verify(detailsController);
    }
    
}
