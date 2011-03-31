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
package org.eclipse.wst.sse.sieditor.test.ui.v2.dt.nodes.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.ImportedSchemaNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.ImportedTypesNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.SimpleTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.StructureTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.test.util.EasymockModelUtils;
import org.eclipse.xsd.XSDSchema;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.Activator;

public class ImportedSchemaNodeTest {

    private static final String LOCATION = "location";

    private static final String NAMESPACE = "http://namespace.com/schema"; //$NON-NLS-1$

    private ISchema schema;

    private ImportedSchemaNode node;

    @Before
    public void setUp() throws Exception {
        schema = EasymockModelUtils.createISchemaMockFromSameModel();
        replay(schema);

        final ImportedTypesNode importedTypesNode = new ImportedTypesNode(null, new TreeNodeMapper());
        node = new ImportedSchemaNode(schema, importedTypesNode, new TreeNodeMapper());

        reset(schema);
    }

    @After
    public void tearDown() throws Exception {
        schema = null;
        node = null;
    }

    @Test
    public void testIsReadOnly() {
        replay(schema);
        assertTrue(node.isReadOnly());
    }

    @Test
    public void testGetDisplayNameWithNullMainSchemaLocation() {
        // see ImportedSchemaNode class for mainSchemaLocation and
        // importedSchemaLocation
        final IModelRoot modelRoot = createMock(IModelRoot.class);
        expect(modelRoot.getModelObject()).andReturn(schema).anyTimes();
        replay(modelRoot);

        final XSDSchema xsdSchema = createMock(XSDSchema.class);
        replay(xsdSchema);

        expect(schema.getNamespace()).andReturn(NAMESPACE).atLeastOnce();
        expect(schema.getLocation()).andReturn(LOCATION);
        expect(schema.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(schema.getComponent()).andReturn(xsdSchema).anyTimes();
        replay(schema);

        final ImportedSchemaNode node = new ImportedSchemaNode(schema, createMock(ITreeNode.class), new TreeNodeMapper());

        assertEquals(NAMESPACE + UIConstants.SPACE + UIConstants.OPEN_BRACKET + LOCATION + UIConstants.CLOSE_BRACKET, node
                .getDisplayName());
        verify(schema);

        reset(schema);
        expect(schema.getNamespace()).andReturn(NAMESPACE).atLeastOnce();
        expect(schema.getLocation()).andReturn(null);
        expect(schema.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(schema.getComponent()).andReturn(xsdSchema).anyTimes();
        replay(schema);

        assertEquals(NAMESPACE + UIConstants.SPACE + UIConstants.OPEN_BRACKET + UIConstants.CLOSE_BRACKET, node.getDisplayName());
        verify(schema);
    }

    @Test
    public void testGetDisplayNameWithNullImportedSchemaLocation() {
        // see ImportedSchemaNode class for mainSchemaLocation and
        // importedSchemaLocation

        final IModelRoot modelRoot = createMock(IModelRoot.class);
        expect(modelRoot.getModelObject()).andReturn(schema).anyTimes();
        replay(modelRoot);

        final XSDSchema xsdSchema = createMock(XSDSchema.class);
        replay(xsdSchema);

        expect(schema.getNamespace()).andReturn(NAMESPACE).atLeastOnce();
        expect(schema.getLocation()).andReturn(null);
        expect(schema.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(schema.getComponent()).andReturn(xsdSchema).anyTimes();
        replay(schema);

        final ImportedSchemaNode node = new ImportedSchemaNode(schema, createMock(ITreeNode.class), new TreeNodeMapper()); //$NON-NLS-1$

        assertEquals(NAMESPACE + UIConstants.SPACE + UIConstants.OPEN_BRACKET + UIConstants.CLOSE_BRACKET, node.getDisplayName());
        verify(schema);
    }

    @Test
    public void testGetDisplayName1() {
        testDisplayText("file:///c:/workspace/project1/src/wsdls/SalesOrder.wsdl", //$NON-NLS-1$
                "file:///c:/workspace/project1/src/wsdls/SalesOrder.wsdl", //$NON-NLS-1$ 
                "file:///c:/workspace/project1/src/wsdls/SalesOrder.wsdl"); //$NON-NLS-1$
    }

    private void testDisplayText(final String mainSchemaLocation, final String importedSchemaLocation,
            final String expectedDisplayName) {
        // see ImportedSchemaNode class for mainSchemaLocation and
        // importedSchemaLocation
        final IModelRoot modelRoot = createMock(IModelRoot.class);
        expect(modelRoot.getModelObject()).andReturn(schema).anyTimes();
        replay(modelRoot);

        final XSDSchema xsdSchema = createMock(XSDSchema.class);
        replay(xsdSchema);

        expect(schema.getNamespace()).andReturn(NAMESPACE).atLeastOnce();
        expect(schema.getLocation()).andReturn(importedSchemaLocation);
        expect(schema.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(schema.getComponent()).andReturn(xsdSchema).anyTimes();
        replay(schema);

        final ImportedSchemaNode node = new ImportedSchemaNode(schema, createMock(ITreeNode.class), new TreeNodeMapper());

        assertEquals(NAMESPACE + UIConstants.SPACE + UIConstants.OPEN_BRACKET + expectedDisplayName + UIConstants.CLOSE_BRACKET,
                node.getDisplayName());
        verify(schema);
    }

    @Test
    public void testGetImage() {
        replay(schema);
        final Display display = Display.getDefault();
        final Image namespaceImage = new Image(display, 10, 10);
        final ImportedTypesNode importedTypesNode = new ImportedTypesNode(null, new TreeNodeMapper());
        final ImportedSchemaNode node = new ImportedSchemaNode(schema, importedTypesNode, new TreeNodeMapper()) {
            @Override
            protected ImageRegistry getImageRegistry() {
                final ImageRegistry registry = new ImageRegistry(display);
                registry.put(Activator.NODE_NAMESPACE_GRAY, namespaceImage);
                return registry;
            }
        };
        assertEquals(namespaceImage, node.getImage());
    }

    @Test
    public void testGetChildren() {
        expect(schema.getAllContainedTypes()).andReturn(new ArrayList<IType>());
        replay(schema);
        assertEquals(0, node.getChildren().length);

        class TestImportedSchemaNode extends ImportedSchemaNode {

            public int createSimpleTypeInvoked = 0;

            public int createStructureTypeInvoked = 0;

            public TestImportedSchemaNode(final IModelObject element, final ITreeNode parent, final TreeNodeMapper nodeMapper) {
                super(element, parent, nodeMapper);
            }

            public int getCreateSimpleTypeInvoked() {
                return createSimpleTypeInvoked;
            }

            public int getCreateStructureTypeInvoked() {
                return createStructureTypeInvoked;
            }

            @Override
            protected IDataTypesTreeNode createStructureTypeNode(final IType type, final TreeNodeMapper nodeMapper,
                    final ITreeNode parentNode) {
                createStructureTypeInvoked++;
                return new StructureTypeNode(type, this, nodeMapper);
            }

            @Override
            protected IDataTypesTreeNode createSimpleTypeNode(final IType type, final ITreeNode parentNode) {
                createSimpleTypeInvoked++;
                return new SimpleTypeNode(type, this);
            }
        }
        ;
        final ImportedTypesNode importedTypesNode = new ImportedTypesNode(null, new TreeNodeMapper());
        final TestImportedSchemaNode testNode = new TestImportedSchemaNode(schema, importedTypesNode, new TreeNodeMapper());

        final Collection<IType> types = new ArrayList<IType>();
        final ISimpleType simpleType = EasymockModelUtils.createISimpleTypeMockFromSameModel();
        types.add(simpleType);
        final IStructureType structureType = EasymockModelUtils.createIStructureTypeMockFromSameModel();
        types.add(structureType);
        replay(simpleType, structureType);

        reset(schema);
        expect(schema.getAllContainedTypes()).andReturn(types).once();
        replay(schema);

        assertEquals(2, testNode.getChildren().length);
        verify(schema);
        assertEquals(1, testNode.getCreateSimpleTypeInvoked());
        assertEquals(1, testNode.getCreateStructureTypeInvoked());

        reset(schema);
        expect(schema.getAllContainedTypes()).andReturn(types).atLeastOnce();
        replay(schema);

        assertEquals(2, testNode.getChildren().length);
        verify(schema);
        assertEquals(1, testNode.getCreateSimpleTypeInvoked());
        assertEquals(1, testNode.getCreateStructureTypeInvoked());
    }

}
