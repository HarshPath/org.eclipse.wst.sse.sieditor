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
 *    Richard Birenheide - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.WSDLNodeFactory;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.impl.AbstractTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;

public class OperationNode extends AbstractTreeNode {

    private final SIFormPageController nodeMapperContainer;

    public OperationNode(final ITreeNode parent, final IOperation operation, final SIFormPageController nodeMapperContainer) {
        super(operation, parent, nodeMapperContainer == null ? null : nodeMapperContainer.getTreeNodeMapper());
        this.nodeMapperContainer = nodeMapperContainer;
    }

    @Override
    public Object[] getChildren() {

        final List<ITreeNode> result = new ArrayList<ITreeNode>();
        final IOperation operation = getModelObject();

        // For a Request-Response operation all 3 categories are present
        if (operation.getOperationStyle().equals(OperationType.REQUEST_RESPONSE)) {
            result.add(getOperationCategoryNode(OperationCategory.INPUT, operation));
            result.add(getOperationCategoryNode(OperationCategory.OUTPUT, operation));
            result.add(getOperationCategoryNode(OperationCategory.FAULT, operation));
        } else {
            // For the other type (we support only notification type) Only the
            // Input node is present
            result.add(getOperationCategoryNode(OperationCategory.INPUT, operation));
            if (nodeMapperContainer.isAsynchronousOperationFaultsEnabled()) {
                result.add(getOperationCategoryNode(OperationCategory.FAULT, operation));
            }
        }

        if (!nodeMapperContainer.isShowCategoryNodes()) {
            final List subnodesList = new ArrayList();
            for (final Iterator iterator = result.iterator(); iterator.hasNext();) {
                final ITreeNode treeNode = (ITreeNode) iterator.next();
                final Object[] subnodes = treeNode.getChildren();
                subnodesList.addAll(Arrays.asList(subnodes));
            }
            return subnodesList.toArray();
        }
        return result.toArray();
    }

    @Override
    public Image getImage() {
        return isReadOnly() ? Activator.getDefault().getImage(Activator.NODE_OPERATION_GRAY) : Activator.getDefault().getImage(
                Activator.NODE_OPERATION);
    }

    @Override
    public IOperation getModelObject() {
        return (IOperation) super.getModelObject();
    }

    private OperationCategoryNode getOperationCategoryNode(final OperationCategory operationCategory, final IOperation operation) {
        OperationCategoryNode operationCategoryNode = (OperationCategoryNode) nodeMapperContainer.getTreeNodeMapper()
                .getCategoryNode(operationCategory.toString(), operation);
        if (operationCategoryNode == null)
            operationCategoryNode = WSDLNodeFactory.getInstance().createOperationCategoryNode(this, operationCategory, operation,
                    nodeMapperContainer);
        return operationCategoryNode;
    }

}
