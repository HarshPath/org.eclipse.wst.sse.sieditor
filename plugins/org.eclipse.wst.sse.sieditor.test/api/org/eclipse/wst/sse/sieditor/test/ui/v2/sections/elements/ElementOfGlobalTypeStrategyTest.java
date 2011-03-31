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

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import junit.framework.Assert;

import org.eclipse.wst.sse.sieditor.ui.v2.dt.ElementConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.SimpleTypeConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.NamespaceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementOfGlobalTypeStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController.CardinalityType;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class ElementOfGlobalTypeStrategyTest implements IElementStrategyTest {

    protected ElementOfGlobalTypeStrategy strategy;
    protected IDataTypesFormPageController controllerMock;

    public ElementOfGlobalTypeStrategyTest() {
        controllerMock = createMock(IDataTypesFormPageController.class);
        strategy = createStrategy();
    }

    protected ElementOfGlobalTypeStrategy createStrategy() {
        return new ElementOfGlobalTypeStrategy(controllerMock);
    }

    @Test
    public void testGetCardinality() {
    	testCardinalityText(0, 5);
    	testCardinalityText(10, 0);
    	testCardinalityText(0, CardinalityType.UNBOUNDED);
    	testCardinalityText(1, 1);
    	testCardinalityText(0, 1);
    	testCardinalityText(1, CardinalityType.UNBOUNDED);
    }

    private void testCardinalityText(int min, int max) {
    	final String expectedCardinalityString = min + " .. " + (max == CardinalityType.UNBOUNDED ? "*" : max);
    	
    	IElement input = createMock(IElement.class);
        expect(input.getMinOccurs()).andReturn(min).anyTimes();
        expect(input.getMaxOccurs()).andReturn(max).anyTimes();
        replay(input);

        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        CardinalityType cardinality = strategy.getCardinality();

        Assert.assertEquals(expectedCardinalityString, cardinality.toString());
    }

    @Test
    public void testGetName() {
        IElement input = createMock(IElement.class);
        String elementName = "name";
        expect(input.getName()).andReturn(elementName).once();
        replay(input);

        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        String name = strategy.getName();
        verify(input);

        Assert.assertEquals(elementName, name);
    }

    @Test
    public void testGetNamespace() {
        IElement input = createMock(IElement.class);
        String elementNs = "namespace";
        expect(input.getNamespace()).andReturn(elementNs).once();
        replay(input);

        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        String ns = strategy.getNamespace();
        verify(input);

        Assert.assertEquals(NamespaceNode.getNamespaceDisplayText(elementNs), ns);
    }

    @Test
    public void testGetNillable() {
        IElement input = createMock(IElement.class);
        expect(input.getNillable()).andReturn(true).once();
        replay(input);

        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        boolean nillable = strategy.getNillable();
        verify(input);

        Assert.assertEquals(true, nillable);
    }

    @Test
    public void testIsCardinalityApplicable() {
        Assert.assertEquals(true, strategy.isCardinalityApplicable());
    }

    @Test
    public void testIsCardinalityEditable() {
        Assert.assertEquals(true, strategy.isCardinalityEditable());
    }

    @Test
    public void testIsConstraintsSectionApplicable() {
        IElement input = createMock(IElement.class);
        ISimpleType type = createNiceMock(ISimpleType.class);
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
        ISimpleType type = createMock(ISimpleType.class);
        expect(input.getType()).andStubReturn(type);
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
    public void testIsNillableEditable() {
        Assert.assertEquals(true, strategy.isNillableEditable());
    }

    @Test
    public void testGetBaseType() {
        Assert.assertNull(strategy.getBaseType());
    }

    @Test
    public void testGetConstraintsSectionControllerAnonymous() {
        IElement element = createNiceMock(IElement.class);
        ISimpleType type = createNiceMock(ISimpleType.class);
        expect(type.isAnonymous()).andStubReturn(true);
        expect(element.getType()).andStubReturn(type);
        replay(element, type);
        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(element).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        IConstraintsController controller = strategy.getConstraintsSectionController();

        Assert.assertNotNull(controller);
        Assert.assertTrue(controller instanceof SimpleTypeConstraintsController);
        Assert.assertEquals(type, controller.getType());
    }

    @Test
    public void testGetConstraintsSectionController() {
        IElement element = createNiceMock(IElement.class);
        ISimpleType type = createNiceMock(ISimpleType.class);
        expect(type.isAnonymous()).andStubReturn(false);
        expect(element.getType()).andStubReturn(type);
        replay(element, type);
        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(element).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        IConstraintsController controller = strategy.getConstraintsSectionController();

        Assert.assertNotNull(controller);
        Assert.assertTrue(controller instanceof ElementConstraintsController);
        Assert.assertEquals(type, controller.getType());
    }

    @Test
    public void testIsBaseTypeApplicable() {

        Assert.assertEquals(false, strategy.isBaseTypeApplicable());
    }

    @Test
    public void testIsBaseTypeEditable() {
        Assert.assertEquals(false, strategy.isBaseTypeEditable());
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
    public void testGetType() {
        IElement input = createMock(IElement.class);
        IType elementTypeMock = createMock(IType.class);
        expect(input.getType()).andReturn(elementTypeMock).once();
        replay(input, elementTypeMock);

        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        IType type = strategy.getType();
        verify(input, elementTypeMock);

        Assert.assertEquals(elementTypeMock, type);
    }

    @Test
    public void testIsNillableApplicable() {
        Assert.assertEquals(true, strategy.isNillableApplicable());
    }

    @Test
    public void testSetBaseType() {
        // nothing to test, ElementOfGlobalTypeStrategy.setBaseType() does
        // nothing
    }

    @Test
    public void testSetCardinality() {

        IElement input = createMock(IElement.class);
        controllerMock.setCardinality(null, input, 0, -1);
        replay(input, controllerMock);

        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        strategy.setCardinality(CardinalityType.ZERO_TO_MANY);

        verify(input, controllerMock);
    }

    @Test
    public void testSetInput() {
        // nothing to test here
    }

    @Test
    public void testSetName() {
        IElement input = createMock(IElement.class);

        controllerMock.renameElement(input, "newName");
        expect(input.getName()).andReturn("oldName").once();

        replay(input, controllerMock);

        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        strategy.setName("newName");

        verify(input, controllerMock);

        // try with same name, rename should not happen
        reset(input, controllerMock);
        expect(input.getName()).andReturn("oldName").once();
        replay(input, controllerMock);
        strategy.setName("oldName");
        verify(input, controllerMock);
    }

    @Test
    public void testSetNamespace() {
        try {
            strategy.setNamespace("ns");
        } catch (IllegalStateException e) {
            return;
        }
        Assert.fail();
    }

    @Test
    public void testSetNillable() {
        IElement input = createMock(IElement.class);

        controllerMock.setNillable(null, input, true);
        expect(input.getNillable()).andReturn(false).once();

        replay(input, controllerMock);

        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        strategy.setNillable(true);

        verify(input, controllerMock);

        // try with same nillable, set nillable should not happen
        reset(input, controllerMock);
        expect(input.getNillable()).andReturn(false).once();
        replay(input, controllerMock);
        strategy.setNillable(false);
        verify(input, controllerMock);
    }

    @Test
    public void testSetType() {
        IElement input = createMock(IElement.class);
        IType typeMock = createMock(IType.class);
        controllerMock.setTypeForElement(typeMock, null, input);
        expect(input.getType()).andReturn(null).once();

        replay(input, controllerMock, typeMock);

        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        strategy.setType(typeMock);

        verify(input, controllerMock, typeMock);

        // try with same nillable, set nillable should not happen
        reset(input, controllerMock, typeMock);
        expect(input.getType()).andReturn(typeMock).once();
        replay(input, controllerMock, typeMock);
        strategy.setType(typeMock);
        verify(input, controllerMock, typeMock);
    }
}
