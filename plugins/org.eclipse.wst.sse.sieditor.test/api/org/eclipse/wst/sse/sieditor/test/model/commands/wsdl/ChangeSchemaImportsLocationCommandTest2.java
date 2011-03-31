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
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDImport;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.commands.UpdateSchemasImportCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class ChangeSchemaImportsLocationCommandTest2 extends AbstractCommandTest {

    private ISchema schema;

    @Override
    protected String getWsdlFilename() {
        return "NamespaceImportsWSDL.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/extract/";
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        ResourceUtils.copyFileIntoTestProject("pub/extract/NamespaceImportsXSD.xsd", Document_FOLDER_NAME, this.getProject(),
                "NamespaceImportsXSD.xsd");
        ResourceUtils.copyFileIntoTestProject("pub/extract/NamespaceIncludesXSD.xsd", Document_FOLDER_NAME, this.getProject(),
                "NamespaceIncludesXSD.xsd");
        ResourceUtils.copyFileIntoTestProject("pub/extract/ExtractedSchema1.xsd", Document_FOLDER_NAME, this.getProject(),
                "ExtractedSchema1.xsd");
        getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
    }

    
    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        boolean found = false;
        for (final XSDConcreteComponent component : schema.getComponent().getContents()) {
            if (component instanceof XSDImport && ((XSDImport) component).getNamespace().equals("http://namespace3")) {
                found = true;
                assertNotNull(((XSDImport) component).getSchemaLocation());
                assertEquals("../../test/Test.xsd", ((XSDImport) component).getSchemaLocation());
                break;
            }
        }
        assertTrue(found);
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        boolean found = false;
        for (final XSDConcreteComponent component : schema.getComponent().getContents()) {
            if (component instanceof XSDImport && ((XSDImport) component).getNamespace().equals("http://namespace3")) {
                found = true;
                assertNull(((XSDImport) component).getSchemaLocation());
                break;
            }
        }
        assertTrue(found);
        assertThereAreNoValidationErrors();
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        assertThereAreNoValidationErrors();
        
        schema = modelRoot.getDescription().getSchema("http://namespace1")[0];

        boolean found = false;
        for (final XSDConcreteComponent component : schema.getComponent().getContents()) {
            if (component instanceof XSDImport && ((XSDImport) component).getNamespace().equals("http://namespace3")) {
                found = true;
                assertNull(((XSDImport) component).getSchemaLocation());
                break;
            }
        }
        assertTrue(found);

        return new UpdateSchemasImportCommand(modelRoot, new ISchema[] { schema }, "http://namespace3", new Path(
                "platform:", "/resource/test/Test.xsd"), true);
    }

}
