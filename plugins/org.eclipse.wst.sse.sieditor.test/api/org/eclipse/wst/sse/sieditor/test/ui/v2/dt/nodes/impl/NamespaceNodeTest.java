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
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.NamespaceNode;
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

public class NamespaceNodeTest {
    
    private ISchema schema;
    
    private NamespaceNode node;
    
    @Before
    public void setUp() throws Exception {
        schema = EasymockModelUtils.createISchemaMockFromSameModel();
        node = new NamespaceNode(schema, createMock(ITreeNode.class), new TreeNodeMapper());
        reset(schema);
    }

    @After
    public void tearDown() throws Exception {
        schema = null;
        node = null;
    }

    @Test
    public void testGetDisplayName() {
        final IModelRoot modelRoot = createMock(IModelRoot.class);
        expect(modelRoot.getModelObject()).andReturn(schema).anyTimes();
        replay(modelRoot);
        
        final XSDSchema xsdSchema = createMock(XSDSchema.class);
        replay(xsdSchema);
        
        final String namespace = "http://namespace.com/schema"; //$NON-NLS-1$
        expect(schema.getNamespace()).andReturn(namespace).atLeastOnce();
        expect(schema.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(schema.getComponent()).andReturn(xsdSchema).anyTimes();
        replay(schema);
        
        assertEquals(namespace, node.getDisplayName());
        verify(schema);
    }

    @Test
    public void testGetImage() {
        final Display display = Display.getDefault();
        final Image namespaceImage = new Image(display, 10, 10);
        
        ITreeNode parentNode = createNiceMock(ITreeNode.class);
        schema = EasymockModelUtils.createISchemaMockFromSameModel();
        replay(schema, parentNode);
        
		final NamespaceNode node = new NamespaceNode(schema, parentNode, new TreeNodeMapper()) {
            protected ImageRegistry getImageRegistry() {
                final ImageRegistry registry = new ImageRegistry(display);
                registry.put(Activator.NODE_NAMESPACE, namespaceImage);
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
        
        class TestNamespaceNode extends NamespaceNode {
            
            public int createSimpleTypeInvoked = 0;
            
            public int createStructureTypeInvoked = 0;
            
            public TestNamespaceNode(final IModelObject element, final ITreeNode parent, final TreeNodeMapper nodeMapper) {
                super(element, parent, nodeMapper);
            }
            
            public int getCreateSimpleTypeInvoked() {
                return createSimpleTypeInvoked;
            }
            
            public int getCreateStructureTypeInvoked() {
                return createStructureTypeInvoked;
            }

            @Override
            protected IDataTypesTreeNode createStructureTypeNode(final IType type, final TreeNodeMapper nodeMapper, final ITreeNode parentNode) {
                createStructureTypeInvoked ++;
                return new StructureTypeNode(type, this, nodeMapper);
            }

            protected IDataTypesTreeNode createSimpleTypeNode(final IType type, final ITreeNode parentNode) {
                createSimpleTypeInvoked ++;
                return new SimpleTypeNode(type, this);
            }
        };
        
        ITreeNode parentNode = createNiceMock(ITreeNode.class);
        replay(parentNode);
        final TestNamespaceNode testNode = new TestNamespaceNode(schema, parentNode, new TreeNodeMapper());
        
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
        expect(schema.getAllContainedTypes()).andReturn(types).once();
        replay(schema);
        
        assertEquals(2, testNode.getChildren().length);
        verify(schema);
        assertEquals(1, testNode.getCreateSimpleTypeInvoked());
        assertEquals(1, testNode.getCreateStructureTypeInvoked());
    }
    
    @Test
    public void testGetParent() {
        assertNotNull(node.getParent());
    }
    
    @Test
    public void testGetModelObject() {
        assertTrue(node.getModelObject() instanceof ISchema);
    }

    @Test
    public void testIsReadOnly() {
    	ITreeNode parentNode = createNiceMock(ITreeNode.class);
        schema = EasymockModelUtils.createISchemaMockFromSameModel();
        replay(schema, parentNode);
        node = new NamespaceNode(schema, parentNode, new TreeNodeMapper());
        assertFalse(node.isReadOnly());
    }

}
