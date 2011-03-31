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
package org.eclipse.wst.sse.sieditor.test.ui.v2.common.propertyEditor;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertTrue;

import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.AttributeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.GlobalElementDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.ITypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.LocalElementDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.ElementTypeEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

/**
 *
 * 
 */
public class ElementTypeEditorTest {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    private static class ElementTypeEditorExposer extends ElementTypeEditor {
        public ElementTypeEditorExposer(final ElementNodeDetailsController detailsController, final ITypeDisplayer typeDisplayer) {
            super(detailsController, typeDisplayer);
        }

        @Override
        public ITypeDialogStrategy createNewTypeDialogStrategy() {
            return super.createNewTypeDialogStrategy();
        }
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.ElementTypeEditor#createNewTypeDialogStrategy()}
     * .
     */
    @Test
    public final void createNewTypeDialogStrategyGlobalElement() {
        final IDataTypesFormPageController dtController = createMock(DataTypesFormPageController.class);
        final ElementNodeDetailsController detailsController = new ElementNodeDetailsController(dtController);
        final IStructureType structureType = org.easymock.EasyMock.createMock(IStructureType.class);
        org.easymock.EasyMock.expect(structureType.isElement()).andReturn(Boolean.valueOf(true)).anyTimes();
        org.easymock.EasyMock.replay(structureType);
        
        final ITreeNode treeNode = org.easymock.EasyMock.createMock(ITreeNode.class);
        org.easymock.EasyMock.expect(treeNode.getModelObject()).andReturn(structureType).anyTimes();
        org.easymock.EasyMock.replay(treeNode);
        
        detailsController.setInput(treeNode);
        final ElementTypeEditorExposer typeEditor = new ElementTypeEditorExposer(detailsController, createMock(ITypeDisplayer.class));
        typeEditor.setInput(treeNode);
        final ITypeDialogStrategy strategy = typeEditor.createNewTypeDialogStrategy();

        assertTrue(strategy instanceof GlobalElementDialogStrategy);
        org.easymock.EasyMock.verify(structureType);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.ElementTypeEditor#createNewTypeDialogStrategy()}
     * .
     */
    @Test
    public final void createNewTypeDialogStrategyLocalElement() {
        final IDataTypesFormPageController dtController = createMock(DataTypesFormPageController.class);
        final ElementNodeDetailsController detailsController = new ElementNodeDetailsController(dtController);
        final IElement elementMock = org.easymock.EasyMock.createMock(IElement.class);
        org.easymock.EasyMock.expect(elementMock.isAttribute()).andStubReturn(Boolean.valueOf(false));
        org.easymock.EasyMock.expect(elementMock.getType()).andReturn(createMock(IType.class));
        org.easymock.EasyMock.replay(elementMock);
        
        final ITreeNode treeNode = org.easymock.EasyMock.createMock(ITreeNode.class);
        org.easymock.EasyMock.expect(treeNode.getModelObject()).andReturn(elementMock).anyTimes();
        org.easymock.EasyMock.replay(treeNode);
        
        detailsController.setInput(treeNode);
        
        final ElementTypeEditorExposer typeEditor = new ElementTypeEditorExposer(detailsController, createMock(ITypeDisplayer.class));
        typeEditor.setInput(treeNode);
        
        final ITypeDialogStrategy strategy = typeEditor.createNewTypeDialogStrategy();

        assertTrue(strategy instanceof LocalElementDialogStrategy);
        org.easymock.EasyMock.verify(elementMock);
    }
    
    @Test
    public final void createNewTypeDialogStrategyAttribute() {
        final IDataTypesFormPageController dtController = createMock(DataTypesFormPageController.class);
        final ElementNodeDetailsController detailsController = new ElementNodeDetailsController(dtController);
        final IElement elementMock = org.easymock.EasyMock.createMock(IElement.class);
        org.easymock.EasyMock.expect(elementMock.isAttribute()).andStubReturn(Boolean.valueOf(true));
        org.easymock.EasyMock.expect(elementMock.getType()).andStubReturn(createMock(IType.class));
        org.easymock.EasyMock.replay(elementMock);
        
        final ITreeNode treeNode = org.easymock.EasyMock.createMock(ITreeNode.class);
        org.easymock.EasyMock.expect(treeNode.getModelObject()).andReturn(elementMock).anyTimes();
        org.easymock.EasyMock.replay(treeNode);
        
        detailsController.setInput(treeNode);
        
        final ElementTypeEditorExposer typeEditor = new ElementTypeEditorExposer(detailsController, createMock(ITypeDisplayer.class));
        typeEditor.setInput(treeNode);
        
        final ITypeDialogStrategy strategy = typeEditor.createNewTypeDialogStrategy();

        assertTrue(strategy instanceof AttributeDialogStrategy);
        final AttributeDialogStrategy attributeStrategy = (AttributeDialogStrategy) strategy;
        Assert.assertEquals(false, attributeStrategy.isElementEnabled());
        Assert.assertEquals(false, attributeStrategy.isStructureTypeEnabled());
        Assert.assertEquals(true, attributeStrategy.isSimpleTypeEnabled());
    }

}

