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
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.DataTypesEditor;

public class InlineNamespaceCompositeCommandTestWithAnnotations extends AbstractCommandTest {

    private int initialNamespacesCount;

    @Override
    protected String getWsdlFilename() {
        return "ECC_PRODUCTIONORDERDELRC.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/csns/inlineSchemaWithAnnotation/";
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(initialNamespacesCount + 1, modelRoot.getDescription().getContainedSchemas().size());
        assertNotNull("Schema was not inlined: http://sap.com/xi/APPL/SE/Global", ((Description)modelRoot.getDescription()).getSchema("http://sap.com/xi/APPL/SE/Global", false));
        assertThereAreNoValidationErrors();
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(initialNamespacesCount, modelRoot.getDescription().getContainedSchemas().size());
        assertThereAreNoValidationErrors();
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        ResourceUtils.copyFileIntoTestProject("pub/csns/inlineSchemaWithAnnotation/ExtractedSchema2.xsd", Document_FOLDER_NAME, this.getProject(),
                "ExtractedSchema2.xsd");
        getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

        initialNamespacesCount = modelRoot.getDescription().getContainedSchemas().size();

        final ISchema[] iSchemas = ((Description)modelRoot.getDescription()).getSchema("http://sap.com/xi/APPL/SE/Global", false);
        assertEquals("schema to inline already in document", 0, iSchemas.length);

        final IXSDModelRoot xsdModelRoot = (IXSDModelRoot) getModelRoot("pub/csns/inlineSchemaWithAnnotation/ExtractedSchema2.xsd",
                "ExtractedSchema2.xsd", DataTypesEditor.EDITOR_ID);

        return new InlineNamespaceCompositeCommand(modelRoot, xsdModelRoot.getSchema());
    }
}
