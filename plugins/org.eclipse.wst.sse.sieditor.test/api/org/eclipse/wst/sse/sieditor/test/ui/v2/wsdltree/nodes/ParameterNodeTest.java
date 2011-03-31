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
package org.eclipse.wst.sse.sieditor.test.ui.v2.wsdltree.nodes;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import junit.framework.Assert;

import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.providers.WSDLContentProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategoryNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ParameterNode;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;

public class ParameterNodeTest {

    @Test
    public void testDisplayName() {
        final IParameter parameter = createNiceMock(IParameter.class);
        expect(parameter.getName()).andReturn("MyName");
        replay(parameter);

        final ParameterNode paramNode = new ParameterNode(null, parameter, null);
        paramNode.getDisplayName();

        verify(parameter);
    }

    @Test
    public void testImageDelegateToParent() {
        final OperationCategoryNode treeNode = createNiceMock(OperationCategoryNode.class);
        expect(treeNode.getImage(true)).andReturn(null);
        replay(treeNode);

        final ParameterNode paramNode = new ParameterNode(treeNode, null, null);
        paramNode.getImage();

        verify(treeNode);
    }

    @Test
    public void testHasNoChildren() {
        final ParameterNode paramNode = new ParameterNode(null, null, null);
        assertFalse(paramNode.hasChildren());
        assertNull(paramNode.getChildren());
    }

    @Test
    public void testGetParentNode() {
        final ITreeNode categoryNodeMock = createMock(ITreeNode.class);
        final ITreeNode operationNodeMock = createMock(ITreeNode.class);
        expect(categoryNodeMock.getParent()).andReturn(operationNodeMock);
        expect(categoryNodeMock.getCategories()).andReturn(ITreeNode.CATEGORY_MAIN).anyTimes();
        replay(categoryNodeMock, operationNodeMock);
        
        final SIFormPageController controller = new SIFormPageController(null,false,true);
        controller.setShowCategoryNodes(true);
        final ParameterNode paramNode = new ParameterNode(categoryNodeMock,null,controller);
        final WSDLContentProvider wsdlContentProvider = new WSDLContentProvider(controller);
        
        Assert.assertEquals(categoryNodeMock,wsdlContentProvider.getParent(paramNode));
        controller.setShowCategoryNodes(false);
        Assert.assertEquals(operationNodeMock,wsdlContentProvider.getParent(paramNode));
    }
    
}
