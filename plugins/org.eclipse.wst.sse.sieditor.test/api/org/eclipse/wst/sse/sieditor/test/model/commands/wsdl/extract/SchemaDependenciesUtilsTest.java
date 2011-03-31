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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaDependenciesUtils;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaNode;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;

public class SchemaDependenciesUtilsTest extends SIEditorBaseTest {

    private IWsdlModelRoot modelRoot;

    private static final String[] ROOT_IMPORTS = { "http://namespace3", "http://namespace2",
            "http://www.example.org/NewWSDLFile/" };

    private static final Map<String, String[]> map;

    private static final String[] NAMESPACE3_IMPORTS = { "http://www.example.org/NewWSDLFile/", "http://namespace1" };

    static {
        map = new HashMap<String, String[]>();
        map.put("http://namespace3", NAMESPACE3_IMPORTS);
        map.put("http://namespace2", new String[] {});
        map.put("http://www.example.org/NewWSDLFile/", new String[] {});
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        ResourceUtils.copyFileIntoTestProject("pub/extract/NamespaceImportsXSD.xsd", Document_FOLDER_NAME, this.getProject(),
                "NamespaceImportsXSD.xsd");
        ResourceUtils.copyFileIntoTestProject("pub/extract/NamespaceIncludesXSD.xsd", Document_FOLDER_NAME, this.getProject(),
        "NamespaceIncludesXSD.xsd");

        modelRoot = (IWsdlModelRoot) getModelRoot("pub/extract/NamespaceImportsWSDL.wsdl", "NamespaceImportsWSDL.wsdl",
                ServiceInterfaceEditor.EDITOR_ID);
    }

    @Test
    public void testBuildDependenciesTree() {
        final ISchema[] schemas = modelRoot.getDescription().getSchema("http://namespace1");

        final SchemaNode root = SchemaDependenciesUtils.instance().buildSchemaDependenciesTree(schemas[0]);

        assertEquals(ROOT_IMPORTS.length, root.getImports().size());
        assertEquals("http://namespace1", root.getNamespace());

        for (int i = 0; i < root.getImports().size(); i++) {
            final SchemaNode imported = root.getImports().get(i);
            assertEquals(ROOT_IMPORTS[i], imported.getNamespace());
            assertEquals(map.get(ROOT_IMPORTS[i]).length, imported.getImports().size());
            for (int k = 0; k < map.get(ROOT_IMPORTS[i]).length; k++) {
                assertEquals(map.get(ROOT_IMPORTS[i])[k], imported.getImports().get(k).getNamespace());
            }
        }

        final Set<SchemaNode> schemasSet = SchemaDependenciesUtils.instance().getSchemaDependencies(root);
        assertEquals(3, schemasSet.size());
        assertFalse(schemasSet.contains(root));
    }

    @Test
    public void testCreateFilenamesMap() {
        final SchemaNode node1 = new SchemaNode("http://ns1");
        node1.setFilename("test1.xsd");
        node1.setPath(new Path("/test/project"));

        final SchemaNode node2 = new SchemaNode("http://ns2");
        node2.setFilename("test2.xsd");
        node2.setPath(new Path("/test/project"));

        final SchemaNode node3 = new SchemaNode("http://ns3");
        node3.setFilename("test3.xsd");
        node3.setPath(new Path("/test/project"));

        final SchemaNode node4 = new SchemaNode("http://ns4");
        node4.setFilename("test4.xsd");
        node4.setPath(new Path("/test/project"));

        final Set<SchemaNode> set = new HashSet<SchemaNode>();
        set.add(node2);
        set.add(node3);
        set.add(node4);

        final Map<String, String> map = SchemaDependenciesUtils.instance().createFilenamesMap(node1, set);
        assertEquals(4, map.size());

        assertNotNull(map.get("http://ns1"));
        assertEquals("test1.xsd", map.get("http://ns1"));

        assertNotNull(map.get("http://ns2"));
        assertEquals("test2.xsd", map.get("http://ns2"));

        assertNotNull(map.get("http://ns3"));
        assertEquals("test3.xsd", map.get("http://ns3"));

        assertNotNull(map.get("http://ns4"));
        assertEquals("test4.xsd", map.get("http://ns4"));
    }

    @Override
    @After
    public void tearDown() throws Exception {
        disposeModel();
        super.tearDown();
    }

}
