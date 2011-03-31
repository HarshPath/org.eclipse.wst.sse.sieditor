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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IElementNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.ElementNode;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.test.util.EasymockModelUtils;
import org.eclipse.xsd.XSDConcreteComponent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.Activator;

public class ElementNodeTest {
    
    protected IElement element;
    
    protected ElementNode node;

    @Before
    public void setUp() throws Exception {
        element = createIElementMockFromSameModel();
    }

    @After
    public void tearDown() throws Exception {
        element = null;
        node = null;
    }
    
    @Test
    public void testGetDisplayName() {
        final String elementName = "ElementName"; //$NON-NLS-1$
        expect(element.getName()).andReturn(elementName).atLeastOnce();
        replay(element);

        node = new ElementNode(element, createMock(ITreeNode.class), new TreeNodeMapper());
        
        assertEquals(elementName, node.getDisplayName());
        verify(element);
    }

    @Test
    public void testGetImage() {
        expect(element.isAttribute()).andReturn(true).once();
        replay(element);
        
        node = new ElementNode(element, createNiceMock(ITreeNode.class), new TreeNodeMapper());
        final Display display = Display.getDefault();
        final Image attributeImage = new Image(display, 10, 10);
        final Image structureImage = new Image(display, 10, 10);
        
        node = createElementNode(element, display, attributeImage, structureImage);

        assertEquals(attributeImage, node.getImage());
        verify(element);
        
        element = createIElementMockFromSameModel();
        expect(element.isAttribute()).andReturn(false).once();
        replay(element);
        node = createElementNode(element, display, attributeImage, structureImage);
        
        assertEquals(structureImage, node.getImage());
        verify(element);
    }

    protected ElementNode createElementNode(final IElement element, final Display display, final Image attributeImage,
            final Image structureImage) {
        ITreeNode parentNode = createNiceMock(ITreeNode.class);
        replay(parentNode);
		final ElementNode node = new ElementNode(element, parentNode, new TreeNodeMapper()) {
            protected ImageRegistry getImageRegistry() {
                final ImageRegistry registry = new ImageRegistry(display);
                registry.put(Activator.NODE_ATTRIBUTE, attributeImage);
                registry.put(Activator.NODE_ELEMENT, structureImage);
                return registry;
            }
        };
        return node;
    }

    @Test
    public void testGetChildrenTypeNull() {
        // type is null
        expect(element.getType()).andReturn(null);
        replay(element);
        node = new ElementNode(element, createMock(ITreeNode.class), new TreeNodeMapper());
        assertEquals(0, node.getChildren().length);
        verify(element);
    }
    
    @Test
    public void testGetChildrenForRefAttribute() {
    	IElement attributeRef = EasymockModelUtils.createReferingIElementMockFromSameModel();
    	expect(attributeRef.isAttribute()).andReturn(true).anyTimes();
    	Collection<IElement> elements = new ArrayList<IElement>();
    	elements.add(attributeRef);
    	
    	IStructureType structureType = createMock(IStructureType.class);
    	expect(structureType.isAnonymous()).andReturn(true).anyTimes();
		expect(structureType.getAllElements()).andReturn(elements);

		expect(element.getType()).andReturn(structureType);
        
		ITreeNode parentNode = createNiceMock(ITreeNode.class);
		replay(element, structureType, attributeRef, parentNode);
        
        node = new ElementNode(element, parentNode, new TreeNodeMapper());
        assertEquals(0, node.getChildren().length);
        verify(element);
    }
    
    @Test
    public void testGetChildrenAnonymousTypeNotStructure() {
    	ITreeNode parentNode = createNiceMock(ITreeNode.class);
        final IType type = createMock(IType.class);
        expect(element.getType()).andReturn(type);
        replay(element, type, parentNode);
        node = new ElementNode(element, parentNode, new TreeNodeMapper());
        
        assertEquals(0, node.getChildren().length);
        verify(element, type);
    }
    
    @Test
    public void testGetChildrenAnonymousTypeStructure() {
        final IStructureType structure = createMock(IStructureType.class);
        expect(structure.isAnonymous()).andReturn(true);
        
        final Collection<IElement> elements = new ArrayList<IElement>();
        final IElement element1 = EasymockModelUtils.createIElementMockFromSameModel();
        final IElement element2 = EasymockModelUtils.createIElementMockFromSameModel();
        elements.add(element1);
        elements.add(element2);
        expect(structure.getAllElements()).andReturn(elements);
        
        expect(element.getType()).andReturn(structure);
        
        ITreeNode parentNode = createNiceMock(ITreeNode.class);
        replay(element, structure, parentNode, element1, element2);
        
        TestElementNode testNode = new TestElementNode(element, parentNode, new TreeNodeMapper());
        final Object[] children = testNode.getChildren();
        assertEquals(2, children.length);
        assertTrue(children[0] instanceof ElementNode);
        assertTrue(children[1] instanceof ElementNode);
        assertEquals(2, testNode.getCreateElementInvoked());
        verify(element, structure);
        
        // get the nodes from the hash TreeNodeMapper
        reset(element, structure);
        expect(element.getType()).andReturn(structure);
        expect(structure.isAnonymous()).andReturn(true);
        expect(structure.getAllElements()).andReturn(elements);
        replay(element, structure);
        
        assertEquals(2, testNode.getChildren().length);
        // number of createElement() method invocations stays the same
        // nodes are taken from the TreeNodeMapper
        assertEquals(2, testNode.getCreateElementInvoked());
        verify(element, structure);
    }
    
    @Test
    public void testIsReadOnlyImportedNode() {
        element = createNiceMock(IElement.class);
        replay(element);
        
        final ITreeNode treeNode = createNiceMock(ITreeNode.class);
        expect(treeNode.getCategories()).andReturn(ITreeNode.CATEGORY_IMPORTED).anyTimes();
        replay(treeNode);
        
        node = new ElementNode(element, treeNode, new TreeNodeMapper());
        
        assertTrue(node.isReadOnly());
    }
    
    @Test
    public void testIsReadOnlyReferencedNode() {
        element = createMock(IElement.class);
        
        final ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getCategories()).andReturn(ITreeNode.CATEGORY_REFERENCE).anyTimes();
        replay(treeNode);
        
        node = new ElementNode(element, treeNode, new TreeNodeMapper());
        
        assertTrue(node.isReadOnly());
    }
    
    private IElement createIElementMockFromSameModel() {
        final IXSDModelRoot modelRoot = createNiceMock(IXSDModelRoot.class);
        final IElement modelObject = createNiceMock(IElement.class);
        final XSDConcreteComponent eObject = createMock(XSDConcreteComponent.class);
        
        expect(modelRoot.getModelObject()).andReturn(modelObject).anyTimes();
        
        expect(modelObject.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(modelObject.getComponent()).andReturn(eObject).anyTimes();
        
        replay(modelRoot, eObject);
        return modelObject;
    }
    
    private class TestElementNode extends ElementNode {
        
        public int createElementInvoked = 0;
        
        public TestElementNode(final IModelObject element, final ITreeNode parent, final TreeNodeMapper nodeMapper) {
            super(element, parent, nodeMapper);
        }
        
        public int getCreateElementInvoked() {
            return createElementInvoked;
        }

        protected IElementNode createElement(final IElement element) {
            createElementInvoked ++;
            return new ElementNode(element, this, getNodeMapper());
        }
    };
}