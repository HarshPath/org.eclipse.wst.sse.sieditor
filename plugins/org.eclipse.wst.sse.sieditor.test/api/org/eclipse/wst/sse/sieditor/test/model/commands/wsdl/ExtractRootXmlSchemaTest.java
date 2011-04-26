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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.ExtractXmlSchemaCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaDependenciesUtils;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaNode;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDImport;
import org.junit.Before;

public class ExtractRootXmlSchemaTest extends AbstractCommandTest {

	private static final String DEPENDENT_SCHEMA_PREFIX = "DependentSchema";
	private static final String NAMESPACE_IMPORTS_XSD = "NamespaceImportsXSD.xsd";
	private static final String DCS_DIRECTORY_IN_TEST_PLUGIN = "pub/extract/ExtractRootXMLSchemmaTest/";
	
	private Map<String, String> importsMap;
	
	private static final String EXTRACTED_ROOT_SCHEMA_FILENAME = "ExtractedSchema1.xsd";
	private ISchema schema;
	private Set<SchemaNode> schemasToExtract;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		ResourceUtils.copyFileIntoTestProject(DCS_DIRECTORY_IN_TEST_PLUGIN + NAMESPACE_IMPORTS_XSD, Document_FOLDER_NAME,
				this.getProject(), NAMESPACE_IMPORTS_XSD);
		ResourceUtils.copyFileIntoTestProject(DCS_DIRECTORY_IN_TEST_PLUGIN + "DependentSchema1.xsd", Document_FOLDER_NAME,
				this.getProject(), "DependentSchema1.xsd");
		ResourceUtils.copyFileIntoTestProject(DCS_DIRECTORY_IN_TEST_PLUGIN + "DependentSchema2.xsd", Document_FOLDER_NAME,
				this.getProject(), "DependentSchema2.xsd");
		ResourceUtils.copyFileIntoTestProject(DCS_DIRECTORY_IN_TEST_PLUGIN + "DependentSchema3.xsd", Document_FOLDER_NAME,
				this.getProject(), "DependentSchema3.xsd");
		ResourceUtils.copyFileIntoTestProject(DCS_DIRECTORY_IN_TEST_PLUGIN + "ExtractedSchema1.xsd", Document_FOLDER_NAME,
				this.getProject(), "ExtractedSchema1.xsd");
		getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		setupCommandParameters();
		
	}

	private void setupCommandParameters() throws Exception {
		schema = getModelRoot().getDescription().getSchema("http://namespace1")[0];
        SchemaNode schemaNode = SchemaDependenciesUtils.instance().buildSchemaDependenciesTree(schema);
        Set<SchemaNode> dependenciesSet = SchemaDependenciesUtils.instance().getSchemaDependencies(schemaNode);

        schemaNode.setFilename(EXTRACTED_ROOT_SCHEMA_FILENAME);
        int k = 1;
        for (final SchemaNode node : dependenciesSet) {
            node.setFilename(DEPENDENT_SCHEMA_PREFIX + (k++) + ".xsd");
        }

        // prepare paths
        final IPath projectLocation = new Path("resource").setDevice("platform:").append(getProject().getFullPath());
        final IPath wsdlLocationPath = projectLocation.append(Document_FOLDER_NAME).append(getWsdlFilename());
        final IPath schemaExractPath = wsdlLocationPath.removeLastSegments(1).removeFirstSegments(1).setDevice(null);
        
        // set extract path
        schemaNode.setPath(schemaExractPath);
        schemaNode.updateImportsPaths();
		schemasToExtract = new HashSet<SchemaNode>();
		schemasToExtract.add(schemaNode);
		schemasToExtract.addAll(dependenciesSet);

        importsMap = new HashMap<String, String>();
		for (final SchemaNode node : schemasToExtract) {
			importsMap.put(node.getNamespace(), node.getFilename());
		}
		
	}

	@Override
	protected String getWsdlFilename() {
		return "NamespaceImportsWSDL.wsdl";
	}

	@Override
	protected String getWsdlFoldername() {
		return DCS_DIRECTORY_IN_TEST_PLUGIN;
	}

	@Override
	protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
		assertEquals(1, modelRoot.getDescription().getContainedSchemas().size());
		final ISchema schema = modelRoot.getDescription().getContainedSchemas().get(0);
		assertNull(schema.getNamespace());
		assertNotNull(schema.getComponent());

		int importedSchemas = 0;
		for (final XSDConcreteComponent component : schema.getComponent().getContents()) {
			if (component instanceof XSDImport) {
				importedSchemas++;
				String importNamespace = ((XSDImport) component).getNamespace();
				String schemaLocation = importsMap.get(importNamespace);
				assertNotNull("Schema location is null for namespace: " + importNamespace, schemaLocation);
				assertEquals(schemaLocation, ((XSDImport) component).getSchemaLocation());
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
		return new ExtractXmlSchemaCompositeCommand(modelRoot, schema, schemasToExtract, new NullProgressMonitor());
	}

}
