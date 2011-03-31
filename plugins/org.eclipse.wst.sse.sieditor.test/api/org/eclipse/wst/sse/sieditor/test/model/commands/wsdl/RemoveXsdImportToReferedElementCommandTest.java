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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchemaContent;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class RemoveXsdImportToReferedElementCommandTest extends AbstractCommandTest {

    private static final String SCHEMA_TARGET_NAMESPACE = "http://namespace1";
    private static final String SCHEMA_TARGET_NAMESPACE_TO_REMOVE = "http://www.example.org/NewWSDLFile/";

    private IWsdlModelRoot modelRoot;

    @Override
    protected String getWsdlFilename() {
        return "NamespaceInlineWSDL.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/extract/";
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        if (modelRoot == null) {
            ResourceUtils.copyFileIntoTestProject("pub/extract/NamespaceImportsXSD.xsd", Document_FOLDER_NAME, this.getProject(),
                    "NamespaceImportsXSD.xsd");
            ResourceUtils.copyFileIntoTestProject("pub/extract/NamespaceIncludesXSD.xsd", Document_FOLDER_NAME,
                    this.getProject(), "NamespaceIncludesXSD.xsd");
            ResourceUtils.copyFileIntoTestProject("pub/extract/NewXMLSchema1.xsd", Document_FOLDER_NAME, this.getProject(),
                    "NewXMLSchema1.xsd");
            getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
        }
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final ISchema schemaToUpdate = modelRoot.getDescription().getSchema(SCHEMA_TARGET_NAMESPACE)[0];
        final XSDImport xsdImport = getXsdImport(schemaToUpdate);
        assertNull(xsdImport);
        // two validation errors expected - one for the element reference and
        // one for the type reference
        assertThereAreValidationErrorsPresent(2);
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final ISchema schemaToUpdate = modelRoot.getDescription().getSchema(SCHEMA_TARGET_NAMESPACE)[0];
        final XSDImport xsdImport = getXsdImport(schemaToUpdate);
        assertNotNull(xsdImport);
        assertThereAreNoValidationErrors();
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        this.modelRoot = modelRoot;

        final ISchema schemaToUpdate = modelRoot.getDescription().getSchema(SCHEMA_TARGET_NAMESPACE)[0];

        assertThereAreNoValidationErrors();

        final XSDImport xsdImport = getXsdImport(schemaToUpdate);
        assertNotNull(xsdImport);

        return new AbstractNotificationOperation(modelRoot, schemaToUpdate, "Remove Import") {
            @Override
            public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
                ((ISchema) modelObject).getComponent().getContents().remove(xsdImport);
                return Status.OK_STATUS;
            }
        };
    }

    private XSDImport getXsdImport(final ISchema schemaToUpdate) {
        for (final XSDSchemaContent content : schemaToUpdate.getComponent().getContents()) {
            if (content instanceof XSDImport) {
                if (((XSDImport) content).getNamespace().equals(SCHEMA_TARGET_NAMESPACE_TO_REMOVE)) {
                    return (XSDImport) content;
                }
            }
        }
        return null;
    }
}
