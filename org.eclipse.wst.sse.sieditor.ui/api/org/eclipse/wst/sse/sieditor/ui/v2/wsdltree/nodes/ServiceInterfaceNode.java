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

import org.eclipse.swt.graphics.Image;

import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.WSDLNodeFactory;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.impl.AbstractTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;

public class ServiceInterfaceNode extends AbstractTreeNode {

    private final SIFormPageController nodeMapperContainer;

    public ServiceInterfaceNode(ITreeNode parent, IServiceInterface serviceInterface, SIFormPageController nodeMapperContainer) {
        super(serviceInterface, parent, nodeMapperContainer == null ? null : nodeMapperContainer.getTreeNodeMapper());
        this.nodeMapperContainer = nodeMapperContainer;
    }

    public Object[] getChildren() {
        // Returns all opertion nodes
        IServiceInterface serviceInterface = (IServiceInterface)getModelObject();
        ArrayList<IOperation> operations = new ArrayList<IOperation>(serviceInterface.getAllOperations());
        ArrayList<ITreeNode> operationNodes = new ArrayList<ITreeNode>();
        for (IOperation operation : operations) {
            operationNodes.add(getOperationNode(operation));
        }
        return operationNodes.toArray();
    }

    public Image getImage() {
        return isReadOnly() ? Activator.getDefault().getImage(Activator.NODE_SI_GRAY) :
            Activator.getDefault().getImage(Activator.NODE_SI); 
    }

    public boolean hasChildren() {
        IServiceInterface serviceInterface = (IServiceInterface)getModelObject();
        return !serviceInterface.getAllOperations().isEmpty();
    }

    private OperationNode getOperationNode(final IOperation operation) {
        OperationNode operationNode = (OperationNode) nodeMapperContainer.getTreeNodeMapper().getTreeNode(operation);
        if (operationNode == null)
            operationNode = WSDLNodeFactory.getInstance().createOperationNode(this, operation, nodeMapperContainer);
        return operationNode;
    }

}
