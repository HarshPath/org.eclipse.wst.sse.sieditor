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
package org.eclipse.wst.sse.sieditor.test.model;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesContentProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.NamespaceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.SimpleTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.providers.WSDLContentProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategory;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategoryNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ServiceInterfaceNode;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;

public class ActionsSelectionTest extends SIEditorBaseTest {

    private static final String FILE_NAME = "pub/simple/CommonEntities.wsdl";
    private static final String PROJECT_NAME = "actions_sel_test";

    @Test
    public void testSIActions() throws Exception {
        final IWsdlModelRoot modelRoot = getWSDLModelRoot(FILE_NAME, "CommonEntities.wsdl");
        Assert.assertNotNull(modelRoot);

        final SIFormPageController controller = new SIFormPageController(modelRoot, false, false);
        final WSDLContentProvider contentProvider = new WSDLContentProvider(controller);

        final Object[] elements = contentProvider.getElements(modelRoot);
        testWSDLSelection(elements, contentProvider, controller);
    }

    private void testWSDLSelection(final Object[] elements, final WSDLContentProvider contentProvider,
            final SIFormPageController controller) {
        if (elements != null) {
            for (final Object object : elements) {
                if (object instanceof ITreeNode) {
                    final ITreeNode node = (ITreeNode) object;

                    // actual testing start
                    // interface - always
                    Assert.assertTrue(controller.isAddNewServiceInterfaceEnabled(node));
                    controller.addNewServiceInterface();
                    // operation - always
                    Assert.assertTrue(controller.isAddNewOperationEnabled(node));
                    controller.addNewOperation(node);
                    // in, out, fault - always, except for service interface
                    if (!(node instanceof ServiceInterfaceNode)) {
                        Assert.assertTrue(controller.isAddNewInParameterEnabled(node));
                        controller.addNewParameter(node, OperationCategory.INPUT);
                        Assert.assertTrue(controller.isAddNewOutParameterEnabled(node));
                        controller.addNewParameter(node, OperationCategory.OUTPUT);
                        Assert.assertTrue(controller.isAddNewFaultEnabled(node));
                        controller.addNewFault(node);
                    }
                    // delete - if not OperationCategory
                    if (!(node instanceof OperationCategoryNode)) {
                        Assert.assertTrue(controller.isDeleteItemEnabled(node));
                    }
                    // actual testing end

                    final Object[] children = contentProvider.getChildren(node);
                    testWSDLSelection(children, contentProvider, controller);
                }
            }
        }
    }

    @Test
    public void testDTActions() throws Exception {
        final IWsdlModelRoot modelRoot = getWSDLModelRoot(FILE_NAME, "CommonEntities.wsdl");
        Assert.assertNotNull(modelRoot);

        final DataTypesFormPageController controller = new DataTypesFormPageController(modelRoot, false);
        final DataTypesContentProvider contentProvider = new DataTypesContentProvider(controller);

        final Object[] elements = contentProvider.getElements(modelRoot);
        testXSDSelection(elements, contentProvider, controller);
    }

    private void testXSDSelection(final Object[] elements, final DataTypesContentProvider contentProvider,
            final DataTypesFormPageController controller) {
        if (elements != null) {
            for (final Object object : elements) {
                if (object instanceof IDataTypesTreeNode) {
                    final IDataTypesTreeNode node = (IDataTypesTreeNode) object;
                    final IModelObject modelObject = node.getModelObject();

                    if (!(modelObject instanceof ISchema || modelObject instanceof ISimpleType)) {
                        if (modelObject instanceof IStructureType) {
                            final IStructureType structure = (IStructureType) modelObject;
                            if (structure.getType() instanceof ISimpleType) {
                                Assert.assertFalse(controller.isAddElementEnabled(node));
                                continue;
                            }
                        }
                        // add element
                        if (node.isReadOnly()
                                && ((node.getCategories() & ITreeNode.CATEGORY_REFERENCE) == ITreeNode.CATEGORY_REFERENCE)
                                && node.getParent().isReadOnly()) {
                            Assert.assertFalse(controller.isAddElementEnabled(node));
                        } else {
                            Assert.assertTrue(controller.isAddElementEnabled(node));
                        }
                        controller.handleAddElementAction(node);
                    }

                    // Simple Type
                    if (node.isReadOnly()
                            && ((node.getCategories() & ITreeNode.CATEGORY_REFERENCE) != ITreeNode.CATEGORY_REFERENCE)) {
                        Assert.assertFalse(controller.isAddSimpleTypeEnabled(node));
                    } else {
                        Assert.assertTrue(controller.isAddSimpleTypeEnabled(node));
                    }
                    controller.handleAddSimpleTypeAction(node);
                    // Structure Type
                    if (node.isReadOnly()
                            && ((node.getCategories() & ITreeNode.CATEGORY_REFERENCE) != ITreeNode.CATEGORY_REFERENCE)) {
                        Assert.assertFalse(controller.isAddStructureEnabled(node));
                    } else {
                        Assert.assertTrue(controller.isAddStructureEnabled(node));
                    }
                    controller.handleAddStructureTypeAction(node);
                    // if not namespace, simple type or simple type element -
                    // also Attribute should be possible
                    boolean executeAttributeCheck = true;
                    if ((node instanceof SimpleTypeNode) || (node instanceof NamespaceNode)) {
                        executeAttributeCheck = false;
                    }
                    if (modelObject instanceof IStructureType) {
                        final IStructureType structure = (IStructureType) modelObject;
                        // global element
                        if (structure.isElement()
                                && !(structure.getType() instanceof IStructureType && structure.getType().isAnonymous())) {
                            executeAttributeCheck = false;
                        }
                    }
                    if (executeAttributeCheck) {
                        if (node.isReadOnly()
                                && ((node.getCategories() & ITreeNode.CATEGORY_REFERENCE) == ITreeNode.CATEGORY_REFERENCE)
                                && node.getParent().isReadOnly()) {
                            Assert.assertFalse(controller.isAddAttributeEnabled(node));
                        } else {
                            Assert.assertTrue(controller.isAddAttributeEnabled(node));
                        }
                        controller.handleAddAttributeAction(node);
                    }
                    // delete - always
                    final List<IDataTypesTreeNode> nodes = new ArrayList<IDataTypesTreeNode>();
                    nodes.add(node);
                    controller.isRemoveItemsEnabled(nodes);
                    // actual testing end

                    final Object[] children = contentProvider.getChildren(node);
                    testXSDSelection(children, contentProvider, controller);
                }
            }
        }
    }

    @Override
    protected String getProjectName() {
        return PROJECT_NAME;
    }
}
