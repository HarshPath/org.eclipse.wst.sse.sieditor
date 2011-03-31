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
package org.eclipse.wst.sse.sieditor.test.model.commands.xsd.base;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Before;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.RemoveTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;

public abstract class RemoveTypeCommandAbstractTest extends AbstractCommandTest {

    private Schema schema;
    private String typeName;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        ResourceUtils.copyFileIntoTestProject("pub/extract/NamespaceImportsXSD.xsd", Document_FOLDER_NAME, getProject(),
                "NamespaceImportsXSD.xsd");
        ResourceUtils.copyFileIntoTestProject("pub/extract/NamespaceIncludesXSD.xsd", Document_FOLDER_NAME, getProject(),
                "NamespaceIncludesXSD.xsd");
        getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
    };

    @Override
    protected String getWsdlFilename() {
        return "NamespaceImportsWSDL.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/extract/";
    }

    protected abstract IType getTypeToRemove(final ISchema schema);

    protected abstract String getSchemaNamespace();

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertNull(schema.getType(false, typeName));
        assertThereAreValidationErrorsPresent(-1);
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertNotNull(schema.getAllTypes(typeName));
        assertThereAreNoValidationErrors();
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ISchema[] schemas = modelRoot.getDescription().getSchema(getSchemaNamespace());
        schema = (Schema) schemas[0];

        assertThereAreNoValidationErrors();

        final IType type = getTypeToRemove(schema);
        assertNotNull("type to remove was not found", type);
        
        typeName = type.getName();
        return new RemoveTypeCommand(modelRoot, schema, type);
    }
}
