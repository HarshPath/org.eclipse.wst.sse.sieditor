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

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;
import org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.extract.ExtractNamespaceRunnableWithoutImportTest;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDImport;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.ExtractXmlSchemaCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaNode;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class ExtractDependentXmlSchemaTest extends AbstractCommandTest {

    private Map<String, String> importsMap;

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
        getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final List<ISchema> containedSchemas = modelRoot.getDescription().getContainedSchemas();

        assertEquals(4, containedSchemas.size());

        final ISchema schema = containedSchemas.get(0);
        assertEquals("http://namespace1", schema.getNamespace());
        assertNotNull(schema.getComponent());

        assertEquals("Only one import should be updated", 1, importsMap.size());
        int importedSchemas = 0;
        for (final XSDConcreteComponent component : schema.getComponent().getContents()) {
            if (component instanceof XSDImport) {
                importedSchemas++;
                if (importsMap.get(((XSDImport) component).getNamespace()) != null) {
                    assertEquals(importsMap.get(((XSDImport) component).getNamespace()), ((XSDImport) component)
                            .getSchemaLocation());
                }
            }
        }
        assertEquals(4, importedSchemas);

        assertThereAreNoValidationErrors();
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(4, modelRoot.getDescription().getContainedSchemas().size());
        assertThereAreNoValidationErrors();
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        assertThereAreNoValidationErrors();

        assertEquals(4, modelRoot.getDescription().getContainedSchemas().size());

        final ISchema schema = modelRoot.getDescription().getSchema("http://www.example.org/NewWSDLFile/")[0];

        final ExtractNamespaceRunnableWithoutImportTest extractTest = new ExtractNamespaceRunnableWithoutImportTest() {
            @Override
            protected void refreshProjectNFile(final IFile file) throws IOException, CoreException {
            }
        };
        extractTest.setTestProject(this.getProject());
        extractTest.setModelRoot(modelRoot);
        // run test
        extractTest.testExtractDependentXmlSchema();

        if (extractTest.getSchemaNode() == null || !extractTest.getDependenciesSet().isEmpty()) {
            fail("prerequisites were not built");
        }

        final Set<SchemaNode> schemasToExtract = new HashSet<SchemaNode>();
        schemasToExtract.add(extractTest.getSchemaNode());
        schemasToExtract.addAll(extractTest.getDependenciesSet());

        importsMap = new HashMap<String, String>();
        for (final SchemaNode node : schemasToExtract) {
            importsMap.put(node.getNamespace(), "../" + ExtractNamespaceRunnableWithoutImportTest.getExtractedSchemasDirectory() + "/"
                    + node.getFilename());
        }

        return new ExtractXmlSchemaCompositeCommand(modelRoot, schema, schemasToExtract, new NullProgressMonitor());
    }

}
