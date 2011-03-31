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

import org.easymock.EasyMock;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ElementConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.SimpleTypeConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.NamespaceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.GlobalElementStrategy;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class GlobalElementStrategyTest implements IElementStrategyTest {

    protected GlobalElementStrategy strategy;
    protected IDataTypesFormPageController controllerMock;

    public GlobalElementStrategyTest() {
        controllerMock = createMock(IDataTypesFormPageController.class);
        strategy = new GlobalElementStrategy(controllerMock);
    }

    @Test
    public void testGetBaseType() {
        IStructureType structureType = createMock(IStructureType.class);
        IType type = createMock(IType.class);
        IType baseType = createMock(IType.class);

        EasyMock.expect(structureType.getType()).andReturn(type).once();
        EasyMock.expect(type.getBaseType()).andReturn(baseType).once();
        EasyMock.replay(structureType, type, baseType);

        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(structureType).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        IType baseType2 = strategy.getBaseType();

        EasyMock.verify(structureType, type, baseType);
        Assert.assertEquals(baseType, baseType2);

    }

    @Test
    public void testGetCardinality() {
        Assert.assertNull(strategy.getCardinality());
    }

    @Test
    public void testGetConstraintsSectionController() {
        IStructureType structureType = createMock(IStructureType.class);
        ISimpleType type = createMock(ISimpleType.class);

        expect(structureType.getType()).andReturn(type).times(2);
        expect(type.isAnonymous()).andStubReturn(true);

        replay(structureType, type);
        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(structureType).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        IConstraintsController constraintsSectionController = strategy.getConstraintsSectionController();
        verify(structureType, type);

        Assert.assertNotNull(constraintsSectionController);
        Assert.assertTrue(constraintsSectionController instanceof SimpleTypeConstraintsController);
        
        EasyMock.reset(type, structureType);
        expect(structureType.getType()).andReturn(type).times(2);
        expect(type.isAnonymous()).andStubReturn(false);

        replay(structureType, type);        

        treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(structureType).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        constraintsSectionController = strategy.getConstraintsSectionController();
        verify(structureType, type);

        Assert.assertNotNull(constraintsSectionController);
        Assert.assertTrue(constraintsSectionController instanceof ElementConstraintsController);
    }

    @Test
    public void testGetName() {
        IStructureType structureType = createMock(IStructureType.class);
        expect(structureType.getName()).andReturn("name").once();

        replay(structureType);
        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(structureType).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        String name = strategy.getName();
        verify(structureType);

        Assert.assertEquals("name", name);
    }

    @Test
    public void testGetNamespace() {
        IStructureType structureType = createMock(IStructureType.class);
        expect(structureType.getNamespace()).andReturn("ns").once();

        replay(structureType);
        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(structureType).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        String ns = strategy.getNamespace();
        verify(structureType);

        Assert.assertEquals(NamespaceNode.getNamespaceDisplayText("ns"), ns);
    }

    @Test
    public void testGetNillable() {
        IStructureType structureType = createMock(IStructureType.class);
        expect(structureType.isNillable()).andReturn(true).once();

        replay(structureType);
        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(structureType).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        boolean nillable = strategy.getNillable();
        verify(structureType);

        Assert.assertEquals(true, nillable);
    }

    @Test
    public void testGetType() {
        IStructureType structureType = createMock(IStructureType.class);
        ISimpleType type = createMock(ISimpleType.class);

        expect(structureType.getType()).andReturn(type).once();

        replay(structureType, type);
        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(structureType).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        IType type2 = strategy.getType();
        verify(structureType, type);

        Assert.assertEquals(type, type2);
    }

    @Test
    public void testIsBaseTypeApplicable() {
        boolean baseTypeApplicable = strategy.isBaseTypeApplicable();
        Assert.assertEquals(false, baseTypeApplicable);
    }

    @Test
    public void testIsBaseTypeEditable() {
        Assert.assertEquals(strategy.isBaseTypeEditable(), strategy.isBaseTypeApplicable());
    }

    @Test
    public void testIsCardinalityApplicable() {
        Assert.assertEquals(false, strategy.isCardinalityApplicable());
    }

    @Test
    public void testIsCardinalityEditable() {
        Assert.assertEquals(false, strategy.isCardinalityEditable());
    }

    @Test
    public void testIsConstraintsSectionApplicable() {
        IStructureType type = createMock(IStructureType.class);
        ISimpleType type2 = createMock(ISimpleType.class);
        expect(type.getType()).andReturn(type2).once();
        replay(type, type2);

        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(type).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        boolean constraintsSectionApplicable = strategy.isConstraintsSectionApplicable();
        verify(type, type2);

        Assert.assertEquals(true, constraintsSectionApplicable);
    }

    @Test
    public void testIsNameApplicable() {
        Assert.assertEquals(true, strategy.isNameApplicable());
    }

    @Test
    public void testIsNameEditable() {
        Assert.assertEquals(true, strategy.isNameEditable());
    }

    @Test
    public void testIsNamespaceApplicable() {
        Assert.assertEquals(true, strategy.isNamespaceApplicable());
    }

    @Test
    public void testIsNamespaceEditable() {
        Assert.assertEquals(false, strategy.isNamespaceEditable());
    }

    @Test
    public void testIsNillableApplicable() {
        Assert.assertEquals(true, strategy.isNillableApplicable());
    }

    @Test
    public void testIsNillableEditable() {
        Assert.assertEquals(true, strategy.isNillableEditable());
    }

    @Test
    public void testIsTypeApplicable() {
        Assert.assertEquals(true, strategy.isTypeApplicable());
    }

    @Test
    public void testIsTypeEditable() {
        Assert.assertEquals(true, strategy.isTypeEditable());
    }

    @Test
    public void testSetBaseType() {
        // TODO Auto-generated method stub

    }

    @Test
    public void testSetCardinality() {
        // TODO Auto-generated method stub

    }

    @Test
    public void testSetInput() {
        // nothing to test
    }

    @Test
    public void testSetName() {
        // TODO Auto-generated method stub

    }

    @Test
    public void testSetNamespace() {
        // TODO Auto-generated method stub

    }

    @Test
    public void testSetNillable() {
        // TODO Auto-generated method stub

    }

    @Test
    public void testSetType() {
        // TODO Auto-generated method stub

    }

}
