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
package org.eclipse.wst.sse.sieditor.test.model;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractXSDCommandTest;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.EnsureSchemaElementCommand;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;

public class EnsureSchemaCommandRepatchTest extends AbstractXSDCommandTest {

    @Override
    protected void assertPostRedoState(IStatus redoStatus, IXSDModelRoot modelRoot) {
        final IModelObject modelObject = modelRoot.getModelObject();

        assertNotNull(modelObject);
        assertTrue(modelObject.getComponent() instanceof XSDSchema);
        assertNotNull(((XSDSchema) modelObject.getComponent()).getElement());

        final Document document = modelRoot.getSchema().getComponent().getDocument();

        assertEquals(Node.PROCESSING_INSTRUCTION_NODE, document.getFirstChild().getNodeType());
        assertEquals(Node.TEXT_NODE, document.getFirstChild().getNextSibling().getNodeType());
        assertEquals(Node.ELEMENT_NODE, document.getFirstChild().getNextSibling().getNextSibling().getNodeType());
    }

    @Override
    protected void assertPostUndoState(IStatus undoStatus, IXSDModelRoot modelRoot) {
        final IModelObject modelObject = modelRoot.getModelObject();

        assertNotNull(modelObject);
        assertTrue(modelObject.getComponent() instanceof XSDSchema);
        assertNull(((XSDSchema) modelObject.getComponent()).getElement());

        final Document document = modelRoot.getSchema().getComponent().getDocument();

        assertNull(document.getFirstChild());
    }

    @Override
    protected AbstractNotificationOperation getOperation(IXSDModelRoot modelRoot) throws Exception {
        return new EnsureSchemaElementCommand(modelRoot.getSchema(), "");
    }

    @Override
    protected String getFilename() {
        return "empty.xsd";
    }

    @Override
    protected String getFolderName() {
        return "pub/csns/badContent/";
    }

}
