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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import junit.framework.Assert;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.providers.WSDLContentProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.FaultNode;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.ui.Activator;

public class FaultNodeTest {

    @Test
    public void testNodeHasNoChildren() {
        final FaultNode faultNode = new FaultNode(null, null, null);

        assertEquals(0, faultNode.getChildren().length);
        assertFalse(faultNode.hasChildren());
    }

    @Test
    public void testGetImage() {
        final Display display = Display.getDefault();
        final Image faultImage = new Image(display, 10, 10);
        
        final SIFormPageController controller = new SIFormPageController(null, false, true);
        controller.setShowCategoryNodes(true);
        final FaultNode node = new FaultNode(null, null, controller) {
            @Override
            protected ImageRegistry getImageRegistry() {
                final ImageRegistry registry = new ImageRegistry(display);
                registry.put(Activator.NODE_OPER_FAULT_OBJECT_GRAY, faultImage);
                return registry;
            }
        };

        assertEquals(faultImage, node.getImage());
    }

    @Test
    public void testDisplayName() {
        final IFault fault = createNiceMock(IFault.class);
        expect(fault.getName()).andReturn("MyName");
        replay(fault);

        final FaultNode paramNode = new FaultNode(null, fault, null);
        paramNode.getDisplayName();

        verify(fault);
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
        final FaultNode faultNode = new FaultNode(categoryNodeMock,null,controller);
        final WSDLContentProvider wsdlContentProvider = new WSDLContentProvider(controller);

        Assert.assertEquals(categoryNodeMock,wsdlContentProvider.getParent(faultNode));
        controller.setShowCategoryNodes(false);
        Assert.assertEquals(operationNodeMock,wsdlContentProvider.getParent(faultNode));
    }
}
