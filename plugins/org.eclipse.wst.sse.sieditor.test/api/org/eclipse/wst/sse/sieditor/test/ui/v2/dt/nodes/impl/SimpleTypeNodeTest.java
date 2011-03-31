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
import static org.junit.Assert.assertNull;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.SimpleTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.test.util.EasymockModelUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.Activator;

public class SimpleTypeNodeTest {

    protected SimpleTypeNode node;
    
    protected ISimpleType type;
    
    @Before
    public void setUp() throws Exception {
        type = EasymockModelUtils.createISimpleTypeMockFromSameModel();
        replay(type);
        node = new SimpleTypeNode(type, createMock(ITreeNode.class));
        reset(type);
    }

    @After
    public void tearDown() throws Exception {
        type = null;
        node = null;
    }

    @Test
    public void testGetDisplayName() {
        final String typeName = "SimpleTypeName"; //$NON-NLS-1$
        expect(type.getName()).andReturn(typeName).atLeastOnce();
        replay(type);
        
        assertEquals(typeName, node.getDisplayName());
        verify(type);
    }

    @Test
    public void testGetImage() {
        final Display display = Display.getDefault();
        final Image typeImage = new Image(display, 10, 10);
        final SimpleTypeNode node = createElementNode(type, display, typeImage);
        assertEquals(typeImage, node.getImage());
    }

    protected SimpleTypeNode createElementNode(IType type, final Display display, final Image typeImage) {
    	ITreeNode parentNode = createNiceMock(ITreeNode.class);
        type = EasymockModelUtils.createISimpleTypeMockFromSameModel();
        replay(type, parentNode);
        final SimpleTypeNode node = new SimpleTypeNode(type, parentNode) {
            protected ImageRegistry getImageRegistry() {
                final ImageRegistry registry = new ImageRegistry(display);
                registry.put(Activator.NODE_SIMPLE_TYPE, typeImage);
                return registry;
            }
        };
        return node;
    }

    @Test
    public void testGetChildren() {
        assertNull(node.getChildren());
    }

    @Test
    public void testHasChildren() {
        assertFalse(node.hasChildren());
    }

    @Test
    public void testIsReadOnly() {
    	ITreeNode parentNode = createNiceMock(ITreeNode.class);
        type = EasymockModelUtils.createISimpleTypeMockFromSameModel();
        replay(type, parentNode);
        node = new SimpleTypeNode(type, parentNode);
        assertFalse(node.isReadOnly());
    }

}
