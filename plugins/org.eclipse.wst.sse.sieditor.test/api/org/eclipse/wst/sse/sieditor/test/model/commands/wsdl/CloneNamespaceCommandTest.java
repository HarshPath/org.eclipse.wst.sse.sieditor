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

import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.CloneNamespaceCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class CloneNamespaceCommandTest extends AbstractCommandTest {

    private Map<String, String> importsMap;
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
        final ISchema[] iSchemas = modelRoot.getDescription().getSchema("http://namespace1");
        assertEquals("two schemas with the test namespace expected in the document", 2, iSchemas.length);

        assertEquals(initialNamespacesCount + 1, modelRoot.getDescription().getContainedSchemas().size());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(initialNamespacesCount, modelRoot.getDescription().getContainedSchemas().size());

        final ISchema[] iSchemas = modelRoot.getDescription().getSchema("http://namespace1");
        assertEquals("only one schema with the test namespace expected in the document", 1, iSchemas.length);

        assertThereAreNoValidationErrors();
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        ResourceUtils.copyFileIntoTestProject("pub/extract/NamespaceImportsXSD.xsd", Document_FOLDER_NAME, this.getProject(),
                "NamespaceImportsXSD.xsd");
        getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

        initialNamespacesCount = modelRoot.getDescription().getContainedSchemas().size();

        final ISchema[] iSchemas = modelRoot.getDescription().getSchema("http://namespace1");
        assertEquals("only one schema with the test namespace expected in the document", 1, iSchemas.length);
        final ISchema schema = iSchemas[0];

        return new CloneNamespaceCommand(modelRoot, schema);
    }
}
