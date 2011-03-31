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
package org.eclipse.wst.sse.sieditor.test.ui.v2.sections.elements;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import junit.framework.Assert;

import org.eclipse.wst.sse.sieditor.ui.v2.dt.AttributeStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementOfAnonymousTypeStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementOfGlobalTypeStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementRefToGlobalElementStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.GlobalElementStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.IElementStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.SimpleTypeStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.StructureTypeStrategy;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;

public class ElementNodeDetailsControllerTest {
    
    private IElementStrategy strategy;
    private final ElementNodeDetailsController controller;
    private final IDataTypesFormPageController dtFormPageControllerMock;

    public ElementNodeDetailsControllerTest() {
        dtFormPageControllerMock = createMock(IDataTypesFormPageController.class);
        
        controller = new ElementNodeDetailsController(dtFormPageControllerMock) {
            @Override
            protected IElementStrategy calculateStrategy(final ITreeNode input) {
                final IElementStrategy calculatedStrategy = super.calculateStrategy(input);
                strategy = calculatedStrategy;
                return calculatedStrategy;
            }
        };
        
    }
    
    @Test
    public void testSetAttributeInput() throws Exception {

        final IElement elementMock = createMock(IElement.class);
        expect(elementMock.isAttribute()).andReturn(true).once();
        replay(elementMock);
        
        final ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(elementMock).anyTimes();
        replay(treeNode);
        
        controller.setInput(treeNode);
        verify(elementMock);        
        Assert.assertTrue(strategy instanceof AttributeStrategy);
        
    }
    
    @Test
    public void testSetGlobalElementRefInput() throws Exception {

        final IElement elementMock = createMock(IElement.class);
        expect(elementMock.isAttribute()).andReturn(false).once();        
        final IStructureType complexTypeMock = createMock(IStructureType.class);
        expect(complexTypeMock.isElement()).andReturn(true).once();        
        expect(elementMock.getType()).andReturn(complexTypeMock).once();
        
        replay(elementMock, complexTypeMock);
        
        final ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(elementMock).anyTimes();
        replay(treeNode);
        
        controller.setInput(treeNode);        
        verify(elementMock);        
        Assert.assertTrue(strategy instanceof ElementRefToGlobalElementStrategy);
    }    

    @Test
    public void testSetAnonymousComplexTypeInput() throws Exception {

        final IElement elementMock = createMock(IElement.class);
        final IStructureType complexTypeMock = createMock(IStructureType.class);

        expect(elementMock.isAttribute()).andReturn(false).once();        
        expect(complexTypeMock.isElement()).andReturn(false).once();
        expect(complexTypeMock.isAnonymous()).andReturn(true).once();
        expect(elementMock.getType()).andReturn(complexTypeMock).once();
        
        replay(elementMock, complexTypeMock);
        
        final ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(elementMock).anyTimes();
        replay(treeNode);
        
        controller.setInput(treeNode);        
        verify(elementMock);        
        Assert.assertTrue(strategy instanceof ElementOfAnonymousTypeStrategy);        
    }
    
    @Test
    public void testSetGlobalComplexTypeInput() throws Exception {

        final IElement elementMock = createMock(IElement.class);
        final IStructureType complexTypeMock = createMock(IStructureType.class);
        
        expect(elementMock.isAttribute()).andReturn(false).once();        
        expect(complexTypeMock.isElement()).andReturn(false).once();
        expect(complexTypeMock.isAnonymous()).andReturn(false).once();
        expect(elementMock.getType()).andReturn(complexTypeMock).once();
        
        replay(elementMock, complexTypeMock);
        
        final ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(elementMock).anyTimes();
        replay(treeNode);
        
        controller.setInput(treeNode);        
        verify(elementMock);        
        Assert.assertTrue(strategy instanceof ElementOfGlobalTypeStrategy);        

    }
    
    @Test
    public void testSetAnonymousSimpleTypeInput() throws Exception {
        
        final IElement elementMock = createMock(IElement.class);
        expect(elementMock.isAttribute()).andReturn(false).once();
        final ISimpleType simpleTypeMock = createMock(ISimpleType.class);
        expect(simpleTypeMock.isAnonymous()).andReturn(true).once();
        expect(elementMock.getType()).andReturn(simpleTypeMock).once();
        
        replay(elementMock, simpleTypeMock);
        
        final ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(elementMock).anyTimes();
        replay(treeNode);
        
        controller.setInput(treeNode);        
        verify(elementMock);        
        Assert.assertTrue(strategy instanceof ElementOfAnonymousTypeStrategy);        
    }
    
    @Test
    public void testSetGlobalSimpleTypeInput() throws Exception {

        final IElement elementMock = createMock(IElement.class);
        final ISimpleType simpleTypeMock = createMock(ISimpleType.class);
        
        expect(elementMock.isAttribute()).andReturn(false).once();        
        expect(simpleTypeMock.isAnonymous()).andReturn(false).once();
        expect(elementMock.getType()).andReturn(simpleTypeMock).once();
        
        replay(elementMock, simpleTypeMock);
        
        final ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(elementMock).anyTimes();
        replay(treeNode);
        
        controller.setInput(treeNode);        
        verify(elementMock);        
        Assert.assertTrue(strategy instanceof ElementOfGlobalTypeStrategy);        
    }
    
    @Test
    public void testGlobalElementStrategy() throws Exception {
    	final IStructureType elementMock = createMock(IStructureType.class);
        
        final ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(elementMock).anyTimes();
        expect(elementMock.isElement()).andReturn(true);
        replay(treeNode, elementMock);
        
        controller.setInput(treeNode);        
        verify(elementMock);        
        Assert.assertTrue(strategy instanceof GlobalElementStrategy);        
    }
    
    @Test
    public void testStructureTypeStrategy() throws Exception {
        final IStructureType elementMock = createMock(IStructureType.class);
        
        final ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(elementMock).anyTimes();
        expect(elementMock.isElement()).andReturn(false);
        replay(treeNode, elementMock);
        
        controller.setInput(treeNode);        
        verify(elementMock);        
        Assert.assertTrue(strategy instanceof StructureTypeStrategy);        
    }
    
    @Test
    public void testSimpleTypeStrategy() throws Exception {

    	final ISimpleType elementMock = createMock(ISimpleType.class);
        replay(elementMock);
        
        final ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(elementMock).anyTimes();
        replay(treeNode);
        
        controller.setInput(treeNode);        
        verify(elementMock);        
        Assert.assertTrue(strategy instanceof SimpleTypeStrategy);        
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testIllegalTypeForStrategy() throws Exception {
        final IModelObject modelObject = createMock(IModelObject.class);
        
        final ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(modelObject).anyTimes();
        replay(treeNode);
        
        controller.setInput(treeNode);        
    }
        
}
