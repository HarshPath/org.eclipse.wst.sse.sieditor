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
package org.eclipse.wst.sse.sieditor.test.model.commands.wsdl;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.InlineNamespaceCompositeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.DataTypesEditor;

public class InlineNamespaceCompositeCommandTest extends AbstractCommandTest {

    private int initialNamespacesCount;

    @Override
    protected String getWsdlFilename() {
        return "NamespaceImportsWSDL.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/extract/";
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(initialNamespacesCount + 1, modelRoot.getDescription().getContainedSchemas().size());
        assertEquals("http://www.example.org/NamespaceImportsXSD", modelRoot.getDescription().getContainedSchemas().get(4)
                .getNamespace());
        assertThereAreNoValidationErrors();
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(initialNamespacesCount, modelRoot.getDescription().getContainedSchemas().size());
        assertThereAreNoValidationErrors();
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        ResourceUtils.copyFileIntoTestProject("pub/extract/NamespaceImportsXSD.xsd", Document_FOLDER_NAME, this.getProject(),
                "NamespaceImportsXSD.xsd");
        getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

        initialNamespacesCount = modelRoot.getDescription().getContainedSchemas().size();

        final ISchema[] iSchemas = modelRoot.getDescription().getSchema("http://www.example.org/NamespaceImportsXSD");
        assertEquals("schema to inline already in document", 0, iSchemas.length);

        final IXSDModelRoot xsdModelRoot = (IXSDModelRoot) getModelRoot("pub/extract/NamespaceImportsXSD.xsd",
                "NamespaceImportsXSD.xsd", DataTypesEditor.EDITOR_ID);

        return new InlineNamespaceCompositeCommand(modelRoot, xsdModelRoot.getSchema());
    }
}
