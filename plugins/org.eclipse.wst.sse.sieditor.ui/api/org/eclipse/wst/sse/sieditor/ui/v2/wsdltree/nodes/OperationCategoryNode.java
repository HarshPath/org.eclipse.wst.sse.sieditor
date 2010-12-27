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
package org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.UIUtils;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.WSDLNodeFactory;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;

public class OperationCategoryNode extends AbstractWsdlTreeNode {

    private final IOperation operation;

    public IOperation getOperation() {
        return operation;
    }

    private final OperationCategory operationCategory;
    private final SIFormPageController nodeMapperContainer;

    public OperationCategoryNode(final ITreeNode parent, final OperationCategory operationCategory, final IOperation operation,
            final SIFormPageController nodeMapperContainer) {
        super(null, parent, nodeMapperContainer == null ? null : nodeMapperContainer.getTreeNodeMapper(), UIUtils
                .getCorrespondingITreenodeCategory(operationCategory));
        this.operationCategory = operationCategory;
        this.nodeMapperContainer = nodeMapperContainer;
        this.operation = operation;
    }

    /**
     * Based on which type of Category node, parameter nodes are returned<br/>
     * -For Input category node - Input parameter nodes<br/>
     * -For Output category node - Output parameter nodes<br/>
     * -For Fault category node - Fault nodes are returned
     */
    @Override
    public Object[] getChildren() {

        final ArrayList<IParameter> parameters = new ArrayList<IParameter>();
        final ArrayList<ParameterNode> parameterNodes = new ArrayList<ParameterNode>();

        if (operationCategory.equals(OperationCategory.INPUT)) {

            parameters.addAll(getOperation().getAllInputParameters());

        } else if (operationCategory.equals(OperationCategory.OUTPUT)) {

            parameters.addAll(getOperation().getAllOutputParameters());

        } else {

            final ArrayList<IFault> faults = new ArrayList<IFault>(getOperation().getAllFaults());
            final ArrayList<FaultNode> faultNodes = new ArrayList<FaultNode>();

            for (final IFault fault : faults) {
                faultNodes.add(getFaultNode(fault));
            }
            return faultNodes.toArray();
        }

        if (parameters.size() > 0) {
            for (final IParameter parameter : parameters) {
                parameterNodes.add(getParameterNode(parameter));
            }
        }
        return parameterNodes.toArray();
    }

    @Override
    public Image getImage() {
        return getImage(getParent().isReadOnly());
    }

    public Image getImage(final boolean isReadOnly) {
        if (operationCategory.equals(OperationCategory.INPUT))
            return isReadOnly ? getImageRegistry().get(Activator.NODE_OPER_INPUT_GRAY) : getImageRegistry().get(
                    Activator.NODE_OPER_INPUT);
        else if (operationCategory.equals(OperationCategory.OUTPUT))
            return isReadOnly ? getImageRegistry().get(Activator.NODE_OPER_OUTPUT_GRAY) : getImageRegistry().get(
                    Activator.NODE_OPER_OUTPUT);
        else if (operationCategory.equals(OperationCategory.FAULT))
            return isReadOnly ? getImageRegistry().get(Activator.NODE_OPER_FAULTS_GRAY) : getImageRegistry().get(
                    Activator.NODE_OPER_FAULTS);
        return null;
    }

    @Override
    public boolean hasChildren() {
        switch (operationCategory) {
        case INPUT:
            return !getOperation().getAllInputParameters().isEmpty();
        case OUTPUT:
            return !getOperation().getAllOutputParameters().isEmpty();
        case FAULT:
            return !getOperation().getAllFaults().isEmpty();
        default:
            return true;
        }
    }

    @Override
    public IModelObject getModelObject() {
        return null;
    }

    public OperationCategory getOperationCategory() {
        return operationCategory;
    }

    private FaultNode getFaultNode(final IFault fault) {
        FaultNode faultNode = (FaultNode) nodeMapperContainer.getTreeNodeMapper().getTreeNode(fault);
        if (faultNode == null)
            faultNode = WSDLNodeFactory.getInstance().createFaultNode(this, fault, nodeMapperContainer);
        return faultNode;
    }

    private ParameterNode getParameterNode(final IParameter parameter) {
        final List<ITreeNode> treeNodes = nodeMapperContainer.getTreeNodeMapper().getTreeNode(parameter, this);
        ParameterNode parameterNode = null;
        if (treeNodes.isEmpty()) {
            parameterNode = WSDLNodeFactory.getInstance().createParameterNode(this, parameter, nodeMapperContainer);
        } else {
            parameterNode = (ParameterNode) treeNodes.get(0);
        }
        return parameterNode;
    }

    @Override
    protected ImageRegistry getImageRegistry() {
        return Activator.getDefault().getImageRegistry();
    }

    /**
     * Returns a tooltip that should be shown for the element in the tree. If
     * the categories (INPUT, OUTPUT, FAULTS) are shown in the tree then tooltip
     * will be <code>null</code>. Otherwise a tooltip should be provided like
     * "Input Parameter Bla bla"
     * 
     * @param element
     *            Parameter or Fault node
     * @return the tooltip to display in tree
     */
    public String getTooltipTextFor(final ITreeNode element) {
        String tooltip = null;
        if (!nodeMapperContainer.isShowCategoryNodes()) {
            switch (operationCategory) {
            case INPUT:
                tooltip = Messages.OperationCategoryNode_input_param_tooltip + element.getDisplayName();
                break;
            case OUTPUT:
                tooltip = Messages.OperationCategoryNode_output_param_tooltip + element.getDisplayName();
                break;
            case FAULT:
                tooltip = Messages.OperationCategoryNode_fault_tooltip + element.getDisplayName();
                break;
            }
        }
        return tooltip;
    }
}
