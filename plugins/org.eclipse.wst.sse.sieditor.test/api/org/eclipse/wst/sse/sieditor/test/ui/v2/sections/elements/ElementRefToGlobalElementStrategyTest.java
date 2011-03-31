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
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import junit.framework.Assert;

import org.easymock.EasyMock;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ElementConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.SimpleTypeConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementOfGlobalTypeStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementRefToGlobalElementStrategy;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class ElementRefToGlobalElementStrategyTest extends ElementOfGlobalTypeStrategyTest {

    @Override
    protected ElementOfGlobalTypeStrategy createStrategy() {
        return new ElementRefToGlobalElementStrategy(controllerMock);
    }

    @Test
    public void testGetName() {
        IElement input = createMock(IElement.class);
        IStructureType type = createMock(IStructureType.class);
        expect(input.getType()).andReturn(type).once();
        String typeName = "name";
        expect(type.getName()).andReturn(typeName).once();
        replay(input, type);

        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        String name = strategy.getName();
        verify(input, type);

        Assert.assertEquals(typeName, name);
    }

    @Test
    public void testGetNamespace() {
        IElement input = createMock(IElement.class);
        IStructureType type = createMock(IStructureType.class);
        expect(input.getType()).andReturn(type).once();
        String typeNs = "ns";
        expect(type.getNamespace()).andReturn(typeNs).once();
        replay(input, type);

        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        String ns = strategy.getNamespace();
        verify(input, type);

        Assert.assertEquals(typeNs, ns);
    }

    @Test
    public void testGetNillable() {
        IElement input = createMock(IElement.class);
        IStructureType type = createMock(IStructureType.class);
        expect(input.getType()).andReturn(type).once();
        expect(type.isNillable()).andReturn(true).once();
        replay(input, type);

        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        boolean nillable = strategy.getNillable();
        verify(input, type);

        Assert.assertEquals(true, nillable);
    }

    @Test
    public void testIsBaseTypeApplicable() {
        IElement input = createMock(IElement.class);
        IStructureType type = createMock(IStructureType.class);
        ISimpleType type2 = createMock(ISimpleType.class);
        expect(input.getType()).andReturn(type).times(2);
        expect(type.getType()).andReturn(type2).times(2);
        expect(type2.isAnonymous()).andReturn(true).once();
        replay(input, type, type2);

        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        boolean baseTypeApplicable = strategy.isBaseTypeApplicable();
        verify(input, type, type2);

        Assert.assertEquals(true, baseTypeApplicable);

        // negative testing
        EasyMock.reset(input, type, type2);
        expect(input.getType()).andReturn(type).times(2);
        expect(type.getType()).andReturn(type2).times(2);
        expect(type2.isAnonymous()).andReturn(false).once();
        replay(input, type, type2);

        baseTypeApplicable = strategy.isBaseTypeApplicable();
        verify(input, type, type2);

        Assert.assertEquals(false, baseTypeApplicable);
    }

    @Test
    public void testIsBaseTypeEditable() {
        Assert.assertEquals(true, strategy.isBaseTypeEditable());
    }

    @Test
    public void testSetName() {
        IElement input = createMock(IElement.class);
        IType type = createMock(IType.class);
        expect(input.getType()).andReturn(type).once();
        controllerMock.renameType(type, "newName");

        replay(input, type, controllerMock);

        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        strategy.setName("newName");

        verify(input, controllerMock);
    }

    @Test
    public void testSetNillable() {
        IElement input = createMock(IElement.class);
        IStructureType type = createMock(IStructureType.class);
        expect(input.getType()).andReturn(type).times(2);
        expect(type.isNillable()).andReturn(false).once();
        controllerMock.setGlobalElementNillable(type, true);

        replay(input, type, controllerMock);

        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        strategy.setNillable(true);

        verify(input, type, controllerMock);

        // try with same nillable, set nillable should not happen
        reset(input, type, controllerMock);

        expect(input.getType()).andReturn(type).once();
        expect(type.isNillable()).andReturn(true).once();

        replay(input, type, controllerMock);

        strategy.setNillable(true);
        verify(input, type, controllerMock);
    }

    @Test
    public void testSetType() {
        IElement input = createMock(IElement.class);
        IStructureType type = createMock(IStructureType.class);
        expect(input.getType()).andReturn(type).times(2);
        expect(type.getType()).andReturn(null).once();
        IType type2 = createMock(IType.class);
        controllerMock.setStructureType(type, type2);

        replay(input, controllerMock, type, type2);

        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        strategy.setType(type2);

        verify(input, controllerMock, type, type2);
        // TODO: test with same type
    }

    @Test
    public void testGetBaseType() {
        IElement input = createMock(IElement.class);
        IStructureType type = createMock(IStructureType.class);
        IType type2 = createMock(IType.class);
        IType baseType = createMock(IType.class);
        expect(input.getType()).andReturn(type).once();
        expect(type.getType()).andReturn(type2).once();
        expect(type2.getBaseType()).andReturn(baseType).once();

        replay(input, type, type2, baseType);

        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        IType baseType2 = strategy.getBaseType();
        verify(input, type, type2, baseType);

        Assert.assertEquals(baseType, baseType2);
    }

    @Test
    public void testSetBaseType() {
        IElement input = createMock(IElement.class);
        IStructureType type = createMock(IStructureType.class);
        ISimpleType type2 = createMock(ISimpleType.class);
        ISimpleType baseType = createMock(ISimpleType.class);
        expect(input.getType()).andReturn(type).times(2);
        expect(type.getType()).andReturn(type2).times(2);
        expect(type2.getBaseType()).andReturn(null).once();
        controllerMock.setSimpleTypeBaseType(type2, baseType);

        replay(input, type, type2, baseType, controllerMock);

        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        strategy.setBaseType(baseType);

        verify(input, type, type2, baseType, controllerMock);
        // TODO: test with same type
    }

    @Test
    public void testGetType() {
        IElement input = createMock(IElement.class);
        IStructureType type = createMock(IStructureType.class);
        IType type2 = createMock(IType.class);
        expect(input.getType()).andReturn(type).once();
        expect(type.getType()).andReturn(type2);

        replay(input, type, type2);

        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        IType type3 = strategy.getType();
        verify(input, type, type2);

        Assert.assertEquals(type2, type3);

    }

    @Override
    public void testGetConstraintsSectionController() {
        IElement element = EasyMock.createNiceMock(IElement.class);
        IStructureType type = EasyMock.createNiceMock(IStructureType.class);
        ISimpleType simpleType = EasyMock.createNiceMock(ISimpleType.class);
        expect(type.isElement()).andStubReturn(true);
        expect(type.getType()).andStubReturn(simpleType);
        EasyMock.expect(type.isAnonymous()).andStubReturn(false);
        EasyMock.expect(element.getType()).andStubReturn(type);
        EasyMock.replay(element, type);
        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(element).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        IConstraintsController controller = strategy.getConstraintsSectionController();

        Assert.assertNotNull(controller);
        Assert.assertTrue(controller instanceof ElementConstraintsController);
        Assert.assertEquals(simpleType, controller.getType());
    }

    @Override
    public void testGetConstraintsSectionControllerAnonymous() {
        IElement element = EasyMock.createNiceMock(IElement.class);
        IStructureType type = EasyMock.createNiceMock(IStructureType.class);
        ISimpleType simpleType = EasyMock.createNiceMock(ISimpleType.class);
        expect(type.isElement()).andStubReturn(true);
        expect(type.getType()).andStubReturn(simpleType);
        EasyMock.expect(simpleType.isAnonymous()).andStubReturn(true);
        EasyMock.expect(element.getType()).andStubReturn(type);
        EasyMock.replay(element, type, simpleType);
        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(element).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        IConstraintsController controller = strategy.getConstraintsSectionController();

        Assert.assertNotNull(controller);
        Assert.assertTrue(controller instanceof SimpleTypeConstraintsController);
        Assert.assertEquals(simpleType, controller.getType());
    }

    @Override
    public void testIsConstraintsSectionApplicable() {
        IElement input = createMock(IElement.class);
        IStructureType type = EasyMock.createNiceMock(IStructureType.class);
        ISimpleType simpleType = EasyMock.createNiceMock(ISimpleType.class);
        expect(type.isElement()).andStubReturn(true);
        expect(type.getType()).andStubReturn(simpleType);
        expect(input.getType()).andStubReturn(type);
        replay(input, type);
        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);

        Assert.assertEquals(true, strategy.isConstraintsSectionApplicable());
    }

    @Test
    public void testIsConstraintsSectionApplicableForAnySimpleType() {
        IElement input = createMock(IElement.class);
        ISimpleType type = EasyMock.createMock(ISimpleType.class);
        IStructureType structure = EasyMock.createMock(IStructureType.class);
        expect(input.getType()).andStubReturn(structure);
        expect(structure.getType()).andReturn(type);
        XSDTypeDefinition anySimpleType = createMock(XSDTypeDefinition.class);
        expect(anySimpleType.getTargetNamespace()).andReturn(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
        expect(anySimpleType.getName()).andReturn("anySimpleType");
        expect(type.getComponent()).andReturn(anySimpleType);
        replay(input, type, anySimpleType);
        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);

        Assert.assertFalse(strategy.isConstraintsSectionApplicable());
    }

}
