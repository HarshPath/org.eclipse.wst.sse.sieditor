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

import org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementOfAnonymousTypeStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementOfGlobalTypeStrategy;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class ElementOfAnonymousTypeStrategyTest extends ElementOfGlobalTypeStrategyTest {

    @Override
    protected ElementOfGlobalTypeStrategy createStrategy() {
        return new ElementOfAnonymousTypeStrategy(controllerMock);
    }

    @Test
    public void testGetBaseType() {
        IElement input = createMock(IElement.class);
        IType type = createMock(IType.class);
        IType baseType = createMock(IType.class);
        expect(input.getType()).andReturn(type).once();
        expect(type.getBaseType()).andReturn(baseType).once();

        replay(input, type, baseType);
        
        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        IType baseType2 = strategy.getBaseType();
        verify(input, type, baseType);

        Assert.assertEquals(baseType, baseType2);

    }

    @Test
    public void testGetConstraintsSectionController() {
        IElement input = createMock(IElement.class);
        ISimpleType type = createMock(ISimpleType.class);
        expect(input.getType()).andReturn(type).once();

        replay(input, type);
        
        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
         
        strategy.setInput(treeNode);
        IConstraintsController constraintsSectionController = strategy.getConstraintsSectionController();
        verify(input, type);

        Assert.assertNotNull(constraintsSectionController);
    }

    @Test
    public void testIsBaseTypeApplicable() {
        boolean baseTypeApplicable = strategy.isBaseTypeApplicable();
        Assert.assertEquals(false, baseTypeApplicable);
    }

    @Test
    public void testIsConstraintsSectionApplicable() {
        IElement input = createMock(IElement.class);
        ISimpleType type = createMock(ISimpleType.class);
        expect(input.getType()).andReturn(type).once();

        replay(input, type);
        
        ITreeNode treeNode = createMock(ITreeNode.class);
        expect(treeNode.getModelObject()).andReturn(input).anyTimes();
        replay(treeNode);
        
        strategy.setInput(treeNode);
        boolean constraintsSectionApplicable = strategy.isConstraintsSectionApplicable();
        verify(input, type);

        Assert.assertEquals(true, constraintsSectionApplicable);
    }

    @Override
    public void testIsConstraintsSectionApplicableForAnySimpleType() {
        // test not applicable
        return;
    }

}
