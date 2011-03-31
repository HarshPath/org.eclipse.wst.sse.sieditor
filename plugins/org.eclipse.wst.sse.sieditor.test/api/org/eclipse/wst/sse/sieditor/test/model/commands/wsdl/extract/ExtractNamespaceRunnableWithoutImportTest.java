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
package org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.extract;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.ExtractNamespaceRunnable;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaDependenciesUtils;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaNode;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;

public class ExtractNamespaceRunnableWithoutImportTest extends SIEditorBaseTest {

    private IWsdlModelRoot modelRoot;

    private static final String EXTRACTED_SCHEMAS_DIRECTORY = "extracted" + System.currentTimeMillis();
    private static final String EXTRACTED_ROOT_SCHEMA_FILENAME = "Extracted.xsd";
    private static final String WSDL_FILE_NAME = "NamespaceImportsWSDL.wsdl";

    private static final List<String> NAMESPACES;

    private File[] schemaFiles;

    private SchemaNode schemaNode;
    private Set<SchemaNode> dependenciesSet;

    private IProject project;

    static {
        NAMESPACES = new LinkedList<String>();
        NAMESPACES.add("http://namespace1");
        NAMESPACES.add("http://www.example.org/NewWSDLFile/");
        NAMESPACES.add("http://namespace2");
        NAMESPACES.add("http://namespace3");
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        project = this.getProject();
        if (modelRoot == null) {
            modelRoot = (IWsdlModelRoot) getModelRoot("pub/extract/" + WSDL_FILE_NAME, WSDL_FILE_NAME,
                    ServiceInterfaceEditor.EDITOR_ID);

            ResourceUtils.copyFileIntoTestProject("pub/extract/NamespaceImportsXSD.xsd", Document_FOLDER_NAME, getTestProject(),
                    "NamespaceImportsXSD.xsd");
            getTestProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
        }
    }

    @Test
    public void testExtractDependentXmlSchema() throws Exception {
        assertInitialState();

        final ISchema schema = modelRoot.getDescription().getSchema("http://www.example.org/NewWSDLFile/")[0];

        schemaNode = SchemaDependenciesUtils.instance().buildSchemaDependenciesTree(schema);
        dependenciesSet = SchemaDependenciesUtils.instance().getSchemaDependencies(schemaNode);

        schemaNode.setFilename(EXTRACTED_ROOT_SCHEMA_FILENAME);

        // prepare paths
        final IPath projectLocation = new Path("resource").setDevice("platform:").append(getTestProject().getFullPath());
        final IPath wsdlLocationPath = projectLocation.append(Document_FOLDER_NAME).append(WSDL_FILE_NAME);
        final IPath schemaExractPath = wsdlLocationPath.removeLastSegments(1).removeFirstSegments(1).setDevice(null)
                .removeLastSegments(1).append(EXTRACTED_SCHEMAS_DIRECTORY);

        // set extract path
        schemaNode.setPath(schemaExractPath);
        schemaNode.updateImportsPaths();

        final ExtractNamespaceRunnable extractRunnable = createExtractRunnable(schema, schemaNode, dependenciesSet,
                wsdlLocationPath);

        // prepare monitor mock
        final IProgressMonitor monitorMock = EasyMock.createMock(IProgressMonitor.class);
        EasyMock.expect(monitorMock.isCanceled()).andReturn(false).anyTimes();
        monitorMock.beginTask(Messages.ExtractNamespaceRunnable_extracting_xml_schema_subtask, dependenciesSet.size() + 1);
        for (int i = 0; i < dependenciesSet.size() + 1; i++) {
            monitorMock.worked(1);
        }
        monitorMock.done();

        EasyMock.replay(monitorMock);

        // run runnable
        extractRunnable.run(monitorMock);

        assertNotNull(extractRunnable.getStatus());
        assertTrue("Extract failed with status message: " + extractRunnable.getStatus().getMessage(), extractRunnable.getStatus()
                .isOK());

        final java.io.File extractDir = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(schemaExractPath).toFile();
        assertTrue(extractDir.exists());
        assertTrue(extractDir.isDirectory());
        schemaFiles = extractDir.listFiles();

        assertEquals("only one schema should be extracted", 1, schemaFiles.length);

        EasyMock.verify(monitorMock);

        final LinkedList<String> extractedNamespaces = new LinkedList<String>();
        assertPostExtractFileState(schemaFiles[0], extractedNamespaces);
        assertEquals("http://www.example.org/NewWSDLFile/", extractedNamespaces.get(0));
    }

    @Test
    public void testExtractXmlSchemaPlusDependencies() throws Exception {
        assertInitialState();

        final ISchema schema = modelRoot.getDescription().getSchema("http://namespace1")[0];

        schemaNode = SchemaDependenciesUtils.instance().buildSchemaDependenciesTree(schema);
        dependenciesSet = SchemaDependenciesUtils.instance().getSchemaDependencies(schemaNode);

        schemaNode.setFilename(EXTRACTED_ROOT_SCHEMA_FILENAME);
        int k = 0;
        for (final SchemaNode node : dependenciesSet) {
            node.setFilename("Dependent" + (k++) + ".xsd");
        }

        // prepare paths
        final IPath projectLocation = new Path("resource").setDevice("platform:").append(getTestProject().getFullPath());
        final IPath wsdlLocationPath = projectLocation.append(Document_FOLDER_NAME).append(WSDL_FILE_NAME);
        final IPath schemaExractPath = wsdlLocationPath.removeLastSegments(1).removeFirstSegments(1).setDevice(null)
                .removeLastSegments(1).append(EXTRACTED_SCHEMAS_DIRECTORY);

        // set extract path
        schemaNode.setPath(schemaExractPath);
        schemaNode.updateImportsPaths();

        final ExtractNamespaceRunnable extractRunnable = createExtractRunnable(schema, schemaNode, dependenciesSet,
                wsdlLocationPath);

        // prepare monitor mock
        final IProgressMonitor monitorMock = EasyMock.createMock(IProgressMonitor.class);
        EasyMock.expect(monitorMock.isCanceled()).andReturn(false).anyTimes();
        monitorMock.beginTask(Messages.ExtractNamespaceRunnable_extracting_xml_schema_subtask, dependenciesSet.size() + 1);
        for (int i = 0; i < dependenciesSet.size() + 1; i++) {
            monitorMock.worked(1);
        }
        monitorMock.done();

        EasyMock.replay(monitorMock);

        // run runnable
        extractRunnable.run(monitorMock);

        assertNotNull(extractRunnable.getStatus());
        assertTrue("Extract failed with status message: " + extractRunnable.getStatus().getMessage(), extractRunnable.getStatus()
                .isOK());

        final java.io.File extractDir = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(schemaExractPath).toFile();
        assertTrue(extractDir.exists());
        assertTrue(extractDir.isDirectory());
        schemaFiles = extractDir.listFiles();

        assertEquals(4, schemaFiles.length);

        EasyMock.verify(monitorMock);

        assertPostExtractState(schemaFiles);
    }

    protected ExtractNamespaceRunnable createExtractRunnable(final ISchema schema, final SchemaNode schemaNode,
            final Set<SchemaNode> dependenciesSet, final IPath wsdlLocationPath) {
        final ExtractNamespaceRunnable extractRunnable = new ExtractNamespaceRunnable(schemaNode, dependenciesSet, true,
                wsdlLocationPath) {
            @Override
            protected IStatus extractSchemas(final IProgressMonitor monitor, final Set<SchemaNode> schemasToExtract,
                    final IPath wsdlLocationPath, final String wsdlEncoding) throws IOException, CoreException {
                final IStatus status = super.extractSchemas(monitor, schemasToExtract, wsdlLocationPath, wsdlEncoding);
                assertEquals("UTF-8", wsdlEncoding);
                return status;
            }
        };
        return extractRunnable;
    }

    private void assertInitialState() {
        final Definition definition = modelRoot.getDescription().getComponent();
        assertNotNull(definition);
        assertNotNull(definition.getETypes());

        final List<XSDSchema> schemas = definition.getETypes().getSchemas();
        assertEquals(4, schemas.size());
        assertEquals("http://www.example.org/NewWSDLFile/", schemas.get(0).getTargetNamespace());
        assertEquals("http://namespace1", schemas.get(1).getTargetNamespace());

        int inlineImports = 0;
        int externalImports = 0;
        for (final XSDConcreteComponent component : schemas.get(1).getContents()) {
            if (component instanceof XSDImport) {
                final XSDImport imported = (XSDImport) component;
                if (imported.getSchemaLocation() == null) {
                    inlineImports++;
                } else {
                    externalImports++;
                }
            }
        }
        assertEquals(3, inlineImports);
        assertEquals(1, externalImports);

        assertEquals("http://namespace2", schemas.get(2).getTargetNamespace());
        assertEquals("http://namespace3", schemas.get(3).getTargetNamespace());
    }

    protected void assertPostExtractState(final java.io.File[] schemaFiles) throws Exception {
        final List<String> extractedNamespaces = new LinkedList<String>();
        for (final java.io.File file : schemaFiles) {
            assertPostExtractFileState(file, extractedNamespaces);
        }
        for (final String namespace : NAMESPACES) {
            assertTrue(extractedNamespaces.contains(namespace));
        }
    }

    private void assertPostExtractFileState(final java.io.File file, final List<String> extractedNamespaces) throws IOException,
            CoreException {
        final IPath schemaPath = new Path(file.toString());
        final IPath schemaRelativePath = schemaPath.makeRelativeTo(ResourcesPlugin.getWorkspace().getRoot().getLocation())
                .removeFirstSegments(1);

        final IXSDModelRoot extractedSchemaModelRoot = getXSDModelRoot(project.getFile(schemaRelativePath));
        assertNotNull("No ISchema for extracted schema file: " + schemaPath.toOSString(), extractedSchemaModelRoot.getSchema());
        final XSDSchema xsdSchema = extractedSchemaModelRoot.getSchema().getComponent();
        assertNotNull("No XSDSchema for extracted schema file: " + schemaPath.toOSString(), xsdSchema);
        final String targetNamespace = xsdSchema.getTargetNamespace();
        assertNotNull("Extracted schema does not have targetNamespace set", targetNamespace);
        extractedNamespaces.add(targetNamespace);

        for (final XSDSchemaContent contents : xsdSchema.getContents()) {
            if (contents instanceof XSDImport) {
                final XSDImport xsdImport = (XSDImport) contents;
                assertNotNull(xsdImport.getSchemaLocation());
            } else if (contents instanceof XSDInclude) {
                final XSDInclude xsdInclude = (XSDInclude) contents;
                assertNotNull(xsdInclude.getSchemaLocation());
                assertTrue("xsdInclude location does not start with ['../data/']: " + xsdInclude.getSchemaLocation(), xsdInclude
                        .getSchemaLocation().startsWith("../data/"));
            }
        }
    }

    @Override
    @After
    public void tearDown() throws Exception {
        for (final File file : schemaFiles) {
            if (file.exists()) {
                file.delete();
            }
        }
        super.tearDown();
    }

    // =========================================================
    // helpers
    // =========================================================

    public SchemaNode getSchemaNode() {
        return schemaNode;
    }

    public Set<SchemaNode> getDependenciesSet() {
        return dependenciesSet;
    }

    public void setModelRoot(final IWsdlModelRoot modelRoot) {
        this.modelRoot = modelRoot;
    }

    public static String getExtractedSchemasDirectory() {
        return EXTRACTED_SCHEMAS_DIRECTORY;
    }

    private IProject getTestProject() {
        return project;
    }

    public void setTestProject(final IProject project) {
        this.project = project;
    }

}
