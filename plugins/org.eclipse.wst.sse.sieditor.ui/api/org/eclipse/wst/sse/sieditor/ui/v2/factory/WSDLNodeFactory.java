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
 *    Sweta Rao - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.factory;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.FaultNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ImportedServicesNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategory;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategoryNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ParameterNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ServiceInterfaceNode;

/**
 * Class used to create Nodes for the WSDL tree. The nodes are also added in the
 * Tree node mapper
 * 
 * 
 * 
 */
public class WSDLNodeFactory {

    private static WSDLNodeFactory singletonInstance;

    private WSDLNodeFactory() {

    }

    public static WSDLNodeFactory getInstance() {
        if (null == singletonInstance)
            singletonInstance = new WSDLNodeFactory();
        return singletonInstance;
    }

    public ImportedServicesNode createImportedServicesNode(final IDescription description,
            SIFormPageController nodeMapperContainer) {

        ImportedServicesNode importedServicesNode = new ImportedServicesNode(description, nodeMapperContainer);
        addToNodeMap(description, importedServicesNode, nodeMapperContainer);
        return importedServicesNode;
    }

    public ServiceInterfaceNode createServiceInterfacenode(final ITreeNode parent, final IServiceInterface serviceInterface,
            SIFormPageController nodeMapperContainer) {
        ServiceInterfaceNode serviceInterfaceNode = new ServiceInterfaceNode(parent, serviceInterface, nodeMapperContainer);
        addToNodeMap(serviceInterface, serviceInterfaceNode, nodeMapperContainer);
        return serviceInterfaceNode;
    }

    public OperationNode createOperationNode(final ITreeNode parent, final IOperation operation,
            SIFormPageController nodeMapperContainer) {
        OperationNode operationNode = new OperationNode(parent, operation, nodeMapperContainer);
        addToNodeMap(operation, operationNode, nodeMapperContainer);
        return operationNode;
    }

    public OperationCategoryNode createOperationCategoryNode(final ITreeNode parent, final OperationCategory operationCategory,
            final IOperation operation, SIFormPageController nodeMapperContainer) {
        OperationCategoryNode operationCategoryNode = new OperationCategoryNode(parent, operationCategory, operation,
                nodeMapperContainer);
        addToCategoryNodeMap(operationCategory.toString(), operation, operationCategoryNode, nodeMapperContainer);
        return operationCategoryNode;
    }

    public FaultNode createFaultNode(ITreeNode parent, IFault fault, SIFormPageController nodeMapperContainer) {
        FaultNode faultNode = new FaultNode(parent, fault, nodeMapperContainer);
        addToNodeMap(fault, faultNode, nodeMapperContainer);
        return faultNode;
    }

    public ParameterNode createParameterNode(ITreeNode parentNode, IParameter parameter, SIFormPageController nodeMapperContainer) {
        ParameterNode parameterNode = new ParameterNode(parentNode, parameter, nodeMapperContainer);
        addToNodeMap(parameter, parameterNode, nodeMapperContainer);
        return parameterNode;
    }

    private void addToNodeMap(IModelObject modelObject, ITreeNode treeNode, SIFormPageController nodeMapperContainer) {
        nodeMapperContainer.getTreeNodeMapper().addToNodeMap(modelObject, treeNode);
    }

    private static void addToCategoryNodeMap(final String category, final Object modelObject, final ITreeNode treeNode,
            SIFormPageController nodeMapperContainer) {
        nodeMapperContainer.getTreeNodeMapper().addToCategoryNodeMap(category, modelObject, treeNode);
    }

}
