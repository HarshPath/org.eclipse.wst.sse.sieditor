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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IElementNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.ElementNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.StructureTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.test.util.EasymockModelUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.Activator;

public class StructureTypeNodeTest {

    protected IStructureType type;

    protected StructureTypeNode node;

    @Before
    public void setUp() throws Exception {
        type = EasymockModelUtils.createIStructureTypeMockFromSameModel();
        node = new StructureTypeNode(type, createMock(ITreeNode.class), new TreeNodeMapper());
        reset(type);
    }

    @After
    public void tearDown() throws Exception {
        type = null;
        node = null;
    }

    @Test
    public void testGetDisplayName() {
        final String typeName = "StructureTypeName"; //$NON-NLS-1$
        expect(type.getName()).andReturn(typeName).atLeastOnce();
        replay(type);
        
        assertEquals(typeName, node.getDisplayName());
        verify(type);
    }

    @Test
    public void testGetImage() {
        type = EasymockModelUtils.createIStructureTypeMockFromSameModel();
        expect(type.isElement()).andReturn(true).once();
        replay(type);
        
        final Display display = Display.getDefault();
        final Image elementImage = new Image(display, 10, 10);
        final Image structureImage = new Image(display, 10, 10);
        
        StructureTypeNode node = createElementNode(type, display, elementImage, structureImage);

        assertEquals(elementImage, node.getImage());
        verify(type);
        
        type = EasymockModelUtils.createIStructureTypeMockFromSameModel();
        expect(type.isElement()).andReturn(false).once();
        replay(type);
        node = createElementNode(type, display, elementImage, structureImage);
        
        assertEquals(structureImage, node.getImage());
        verify(type);
    }

    protected StructureTypeNode createElementNode(final IType type, final Display display, final Image elementImage,
            final Image structureImage) {
        
    	ITreeNode parentNode = createNiceMock(ITreeNode.class);
    	replay(parentNode);
        final StructureTypeNode node = new StructureTypeNode(type, parentNode, new TreeNodeMapper()) {
            @Override
            protected ImageRegistry getImageRegistry() {
                final ImageRegistry registry = new ImageRegistry(display);
                registry.put(Activator.NODE_STRUCTURE_TYPE, structureImage);
                registry.put(Activator.NODE_ELEMENT, elementImage);
                return registry;
            }
        };
        return node;
    }

    @Test
    public void testGetChildren() {
        final Collection<IElement> elements = new ArrayList<IElement>();
        expect(type.getAllElements()).andReturn(elements).once();
        replay(type);
        
        assertEquals(0, node.getChildren().length);
        verify(type);
        
        reset(type);
        IElement element1 = EasymockModelUtils.createIElementMockFromSameModel();
        IElement element2 = EasymockModelUtils.createIElementMockFromSameModel();
        elements.add(element1);
        elements.add(element2);
        expect(type.getAllElements()).andReturn(elements).once();
        replay(type, element1, element2);
        
        class TestStructureTypeNode extends StructureTypeNode {
            
            private int createElementInvoked = 0;
            
            TestStructureTypeNode(final IModelObject type, final ITreeNode parent, final TreeNodeMapper nodeMapper) {
                super(type, parent, nodeMapper);
            }
            
            public int getCreateElementInvoked() {
                return createElementInvoked;
            }
            
            protected IElementNode createElementNode(final IElement element) {
                createElementInvoked ++;
                return new ElementNode(element, this, getNodeMapper());
            }
        };
        
        ITreeNode parentNode = createNiceMock(ITreeNode.class);
        replay(parentNode);
        
        final TestStructureTypeNode node = new TestStructureTypeNode(type, parentNode, new TreeNodeMapper());
        Object[] children = node.getChildren();
        assertEquals(2, children.length);
        assertTrue(children[0] instanceof ElementNode);
        assertTrue(children[1] instanceof ElementNode);
        assertEquals(2, node.getCreateElementInvoked());
        verify(type);
        
        // get the nodes from the hash TreeNodeMapper
        reset(type);
        expect(type.getAllElements()).andReturn(elements).once();
        replay(type);
        
        children = node.getChildren();
        assertEquals(2, children.length);
        assertTrue(children[0] instanceof ElementNode);
        assertTrue(children[1] instanceof ElementNode);
        // number of createElement() method invocations stays the same
        // nodes are taken from the TreeNodeMapper
        assertEquals(2, node.getCreateElementInvoked());
        verify(type);
    }
    
    @Test
    public void testGetChildrenForRefAttribute() {
    	IElement attributeRef = EasymockModelUtils.createReferingIElementMockFromSameModel();
    	expect(attributeRef.isAttribute()).andReturn(true).anyTimes();
    	Collection<IElement> elements = new ArrayList<IElement>();
    	elements.add(attributeRef);
    	
    	type = EasymockModelUtils.createIStructureTypeMockFromSameModel(true);
    	expect(type.isAnonymous()).andReturn(true).anyTimes();
		expect(type.getAllElements()).andReturn(elements);

		ITreeNode parentNode = createNiceMock(ITreeNode.class);
		replay(type, attributeRef, parentNode);
        
        node = new StructureTypeNode(type, parentNode, new TreeNodeMapper());
        assertEquals(0, node.getChildren().length);
        verify(type);
    }

    @Test
    public void testIsReadOnly() {
    	ITreeNode parentNode = createNiceMock(ITreeNode.class);
        type = EasymockModelUtils.createIStructureTypeMockFromSameModel();
        replay(type, parentNode);
        node = new StructureTypeNode(type, parentNode, new TreeNodeMapper());
        assertFalse(node.isReadOnly());
    }

}
