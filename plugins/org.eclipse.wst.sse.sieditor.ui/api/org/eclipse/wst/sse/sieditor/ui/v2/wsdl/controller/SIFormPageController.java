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
package org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller;

import java.security.InvalidParameterException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.DeleteSetCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddFaultCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddInParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddOperationCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddOutParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddServiceInterfaceAndOperationCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.ChangeOperationTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteOperationCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RenameFaultCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RenameOperationCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RenameParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RenameServiceInterfaceCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.SetFaultTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.SetParameterTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.tns.ChangeDefinitionTNSCompositeCommand;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.mm.ModelManager;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.INamedObject;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.generic.IllegalInputException;
import org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils;
import org.eclipse.wst.sse.sieditor.model.utils.NameGenerator;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceInterface;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.eventing.ISIEvent;
import org.eclipse.wst.sse.sieditor.ui.v2.eventing.ISIEventListener;
import org.eclipse.wst.sse.sieditor.ui.v2.eventing.SIEvent;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.WSDLNodeFactory;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.typeselect.ISIComponentSearchListProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.typeselect.SIEditorSearchListProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.utils.UIUtils;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.FaultNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ImportedServicesNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategory;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategoryNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ParameterNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ServiceInterfaceNode;

/**
 * Implementation of the Presenter in the MVP(Model-View-Presenter) set for the
 * WSDL Tree. Presenter hold all the functional logic involved in the usage of
 * the Editor.
 * 
 * Implements a list of listeners IWSDLViewListener - Listens to the changes on
 * the View (or UI) ITypeDropListener - Listens to any drop action on this tree.
 * IChangeListener - Listens to any change in the model IEventListener - Listens
 * to any event from the view or the model through the EventBroker
 * 
 */
public class SIFormPageController extends AbstractFormPageController {

    /*
     * By default the category nodes for parameters (input, output) and faults
     * will be hidden.
     */
    private boolean showCategoryNodes = false;
    private IDataTypesFormPageController dtController;
    private final boolean asynchronousOperationFaultsEnabled;

    /**
     * The controller is intended to manage edition of the model. <br>
     * Via a {@link TreeNodeMapper} it helps create and sustain a metaModel of
     * tree nodes (the objects displayed in the tree). The mapper provides the
     * connection from a {@link IModelObject}<br>
     * Provide services to the listeners as - Provide mapping between model
     * objects and treeNodes - Notify on model refresh, - Notify on model
     * element refresh - Notify for treeNode selection request,
     * 
     * @param model
     *            the model on which the controller will be operating
     * @param readOnly
     */
    public SIFormPageController(final IWsdlModelRoot model, final boolean readOnly,
            final boolean asynchronousOperationsFaultsEnabled) {
        super(model, readOnly);
        this.asynchronousOperationFaultsEnabled = asynchronousOperationsFaultsEnabled;

    }

    /**
     * Method used to determine weather asynchronous style operations<br>
     * are allowed to contain faults.
     * 
     * @return true if faults are allowed to be in async operations<br>
     *         false otherwise.
     */
    public boolean isAsynchronousOperationFaultsEnabled() {
        return asynchronousOperationFaultsEnabled;
    }

    /**
     * Returns <code>true</code> if the category nodes for parameters (input,
     * output) and faults should be shown.
     * 
     * @return <code>true</code> if the category nodes for parameters (input,
     *         output) and faults should be shown.
     */
    public boolean isShowCategoryNodes() {
        return showCategoryNodes;
    }

    /**
     * Set to <code>true</code> if the category nodes for parameters (input,
     * output) and faults should be shown.
     * 
     * @param showCategoryNodes
     *            <code>true</code> if the category nodes for parameters (input,
     *            output) and faults should be shown.
     */
    public void setShowCategoryNodes(final boolean showCategoryNodes) {
        this.showCategoryNodes = showCategoryNodes;
        fireRefreshTreeEvent();
    }

    protected void fireRefreshTreeEvent() {
        final ISIEvent refreshEvent = new SIEvent(ISIEvent.ID_REFRESH_TREE, null);
        for (final ISIEventListener eventListener : eventListeners) {
            eventListener.notifyEvent(refreshEvent);
        }
    }

    /**
     * Adds new Service Interface with a internally generated name.
     */
    public void addNewServiceInterface() {
        final IDescription description = getWsdlModelRoot().getDescription();
        if (!isEditAllowed(model)) {
            final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.SIFormPageController_msg_editing_wsdl_X_not_allowed, description.getLocation()));
            StatusUtils.showStatusDialog(Messages.SIFormPageController_dlg_title_add_new_service_interface, status);
            return;
        }

        final String newSIName = NameGenerator.getNewServiceInterfaceName(description);

        final AddServiceInterfaceAndOperationCommand compositeNotificationOperation = new AddServiceInterfaceAndOperationCommand(
                getWsdlModelRoot(), description, newSIName);

        try {

            final IStatus status = model.getEnv().execute(compositeNotificationOperation);

            if (!StatusUtils.canContinue(status)) {
                StatusUtils.showStatusDialog(Messages.SIFormPageController_dlg_title_add_new_service_interface, MessageFormat
                        .format(Messages.SIFormPageController_msg_failure_add_new_service_interface_to_wsdl_X, description
                                .getLocation()), status);
            }

            final ServiceInterface serviceInterface = compositeNotificationOperation.getServiceInterface();
            fireTreeNodeSelectionEvent(serviceInterface);
            fireTreeNodeExpandEvent(serviceInterface);
            fireTreeNodeEditEvent(serviceInterface);
            
        } catch (final ExecutionException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Failed to add new Service Interface to " + description.getLocation(), //$NON-NLS-1$
                    e);
        }
    }

    protected IWsdlModelRoot getWsdlModelRoot() {
        return (IWsdlModelRoot) model;
    }


    /**
     * Adds a new Operation to the given service interface
     * 
     * @param parent
     *            - a Service interface node, where the operation will be added,
     *            or any of a SInode's children
     */
    public void addNewOperation(ITreeNode parent) {

        while (!(parent instanceof ServiceInterfaceNode)) {
            parent = parent.getParent();
        }
        if (!isEditAllowed(parent.getModelObject())) {
            final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.SIFormPageController_msg_editing_element_X_not_allowed, parent.getDisplayName()));
            StatusUtils.showStatusDialog(Messages.SIFormPageController_dlg_title_add_new_operation, status);
            return;
        }
        final IServiceInterface serviceInterface = (IServiceInterface) parent.getModelObject();

        final String newOperationName = NameGenerator.getNewOperationName(serviceInterface);

        try {
            final AddOperationCommand command = new AddOperationCommand(getWsdlModelRoot(), serviceInterface, newOperationName,
                    OperationType.REQUEST_RESPONSE);
            final IStatus status = model.getEnv().execute(command);
            if (!StatusUtils.canContinue(status)) {
                StatusUtils.showStatusDialog(Messages.SIFormPageController_dlg_title_add_new_operation, MessageFormat.format(
                        Messages.SIFormPageController_msg_failure_add_new_operation_to_service_interface_X, serviceInterface
                                .getName()), status);
                return;
            }
            final IOperation addedOperation = command.getOperation();
            // calls the getChildren method of the SI node in order to add the
            // newly created children to the
            // SITreeNodeMapper
            getTreeNodeMapper().getTreeNode(serviceInterface).getChildren();

            fireTreeNodeSelectionEvent(addedOperation);
            fireTreeNodeEditEvent(addedOperation);
        } catch (final ExecutionException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Failed to add new operation to " + serviceInterface.getName(), e); //$NON-NLS-1$
        }
    }

    public boolean isAddNewServiceInterfaceEnabled(final ITreeNode node) {
    	if (isResourceReadOnly()) {
            return false;
        }
        if (node == null || node.isImportedNode()) {
            return node instanceof ImportedServicesNode;
        }

        return node instanceof ServiceInterfaceNode || node instanceof OperationNode || node instanceof ParameterNode
                || node instanceof FaultNode || node instanceof OperationCategoryNode;
    }

    public boolean isAddNewOperationEnabled(final ITreeNode node) {
    	if(node instanceof ImportedServicesNode) {
    		return false;
    	}
        // Service and operation - always
        return isAddNewServiceInterfaceEnabled(node);
    }


    /*
     * Adds a new Input parameter for
     */
    private void addNewInputParameter(final IOperation operation) {

        final String newInputParamName = NameGenerator.getInputParameterName(operation);

        final Collection<IParameter> allInputParameters = operation.getAllInputParameters();
        if (!allInputParameters.isEmpty()) {
            final IParameter firstInParam = allInputParameters.iterator().next();
            final IModelRoot modelRoot = firstInParam.getModelRoot();
            if (!EmfWsdlUtils.isModelObjectPartOfModelRoot(modelRoot, firstInParam)) {
                final boolean removeInputParams = StatusUtils.showDialogWithResult(MessageDialog.QUESTION,
                        Messages.SIFormPageController_0, MessageFormat.format(Messages.SIFormPageController_1, operation
                                .getName()));
                if (!removeInputParams) {
                    return;
                }
            }
        }

        try {
            final AddInParameterCommand command = new AddInParameterCommand(getWsdlModelRoot(), operation, newInputParamName);
            final IStatus status = model.getEnv().execute(command);
            if (!StatusUtils.canContinue(status)) {
                StatusUtils.showStatusDialog(Messages.SIFormPageController_dlg_title_add_new_input_parameter, MessageFormat
                        .format(Messages.SIFormPageController_msg_failure_add_new_input_parameter_to_operation_X, operation
                                .getName()), status);
                return;
            }
            final IParameter addedInputParameter = command.getParameter();

            final Object[] children = getTreeNodeMapper().getTreeNode(operation).getChildren();

            // re-get the children to create node for newly created element
            if (showCategoryNodes) {
                for (final Object object : children) {
                    final OperationCategoryNode categoryNode = (OperationCategoryNode) object;
                    if (categoryNode.getOperationCategory().equals(OperationCategory.INPUT)) {
                        categoryNode.getChildren();
                    }
                }
            }
            fireTreeNodeSelectionEvent(addedInputParameter);
            fireTreeNodeEditEvent(addedInputParameter);
        } catch (final ExecutionException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Failed to add new input parameter to operation " //$NON-NLS-1$
                    + operation.getName(), e);
        }
    }


    /*
     * Adds a Output parameter to the given Operation
     */
    private void addNewOutputParameter(final IOperation operation) {

        final String newOutputParamName = NameGenerator.getOutputParameterName(operation);

        final Collection<IParameter> allOutputParameters = operation.getAllOutputParameters();
        if (!allOutputParameters.isEmpty()) {
            final IParameter firstOutParam = allOutputParameters.iterator().next();
            final IModelRoot modelRoot = firstOutParam.getModelRoot();
            if (!EmfWsdlUtils.isModelObjectPartOfModelRoot(modelRoot, firstOutParam)) {
                final boolean removeOutputParams = StatusUtils.showDialogWithResult(MessageDialog.QUESTION,
                        Messages.SIFormPageController_2, MessageFormat.format(Messages.SIFormPageController_3, operation
                                .getName()));
                if (!removeOutputParams) {
                    return;
                }
            }
        }

        try {
            final AddOutParameterCommand command = new AddOutParameterCommand(getWsdlModelRoot(), operation, newOutputParamName);
            final IStatus status = model.getEnv().execute(command);
            if (!StatusUtils.canContinue(status)) {
                StatusUtils.showStatusDialog(Messages.SIFormPageController_dlg_title_add_new_output_parameter, MessageFormat
                        .format(Messages.SIFormPageController_msg_failure_add_new_output_parameter_to_operation_X, operation
                                .getName()), status);
                return;
            }
            final IParameter addedOutputParameter = command.getParameter();

            final Object[] children = getTreeNodeMapper().getTreeNode(operation).getChildren();

            // re-get the children to create node for newly created element
            if (showCategoryNodes) {
                for (final Object object : children) {
                    final OperationCategoryNode categoryNode = (OperationCategoryNode) object;
                    if (categoryNode.getOperationCategory().equals(OperationCategory.OUTPUT)) {
                        categoryNode.getChildren();
                    }
                }
            }

            fireTreeNodeSelectionEvent(addedOutputParameter);
            fireTreeNodeEditEvent(addedOutputParameter);
        } catch (final ExecutionException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Failed to add new output parameter to operation " //$NON-NLS-1$
                    + operation.getName(), e);
        }
    }

    /**
     * Adds a new fault parameter to the operation relative to the selected tree
     * node
     * 
     * @param selectedElement
     *            - the should element be one of the following types
     *            :ParameterNode, CategoryNode , OperationNode. In order to be
     *            able to determine where to add the new fault.
     */
    public void addNewFault(ITreeNode selectedElement) {
        while (!(selectedElement instanceof OperationCategoryNode) && !(selectedElement instanceof OperationNode)) {
            selectedElement = selectedElement.getParent();
        }
        IOperation operation;
        if (selectedElement instanceof OperationNode) {
            operation = ((OperationNode) selectedElement).getModelObject();
        } else {
            operation = ((OperationCategoryNode) selectedElement).getOperation();
        }

        if (!isEditAllowed(operation)) {
            final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.SIFormPageController_msg_editing_element_X_not_allowed, selectedElement.getDisplayName()));
            StatusUtils.showStatusDialog(Messages.SIFormPageController_dlg_title_add_new_parameter, status);
            return;
        }

        final String newFaultName = NameGenerator.getNewFaultName(operation);

        try {
            final AddFaultCommand command = new AddFaultCommand(getWsdlModelRoot(), operation, newFaultName);
            final IStatus status = model.getEnv().execute(command);
            if (!StatusUtils.canContinue(status)) {
                StatusUtils.showStatusDialog(Messages.SIFormPageController_dlg_title_add_fault, MessageFormat.format(
                        Messages.SIFormPageController_smg_failure_add_fault_to_operation_X, operation.getName()), status);
                return;
            }
            final IFault addedFault = command.getFault();

            final Object[] children = getTreeNodeMapper().getTreeNode(operation).getChildren();
            // re-get the children to create node for newly created element
            if (showCategoryNodes) {
                for (final Object object : children) {
                    final OperationCategoryNode categoryNode = (OperationCategoryNode) object;
                    if (categoryNode.getOperationCategory().equals(OperationCategory.FAULT)) {
                        categoryNode.getChildren(); // calls the getChildren
                        // method
                        // of the category node in order
                        // to add the
                        // newly created children to the SITreeNodeMapper
                    }
                }
            }
            fireTreeNodeSelectionEvent(addedFault);
            fireTreeNodeEditEvent(addedFault);
        } catch (final ExecutionException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Failed to add new fault to operation " + operation.getName(), e); //$NON-NLS-1$
        }
    }

    public boolean isAddNewFaultEnabled(final ITreeNode node) {
        if (isResourceReadOnly() || node == null || node.isImportedNode()) {
            return false;
        }

        IOperation operation = null;

        if (node instanceof OperationNode) {
            operation = ((OperationNode) node).getModelObject();
        } else if (node instanceof OperationCategoryNode) {
            final IOperation iOperation = (IOperation) node.getParent().getModelObject();
            if (isPartOfEdittedDocument(iOperation)) {
                operation = iOperation;
            }
        } else if ((node instanceof FaultNode) || (node instanceof ParameterNode)) {
            return isAddNewFaultEnabled(node.getParent());
        }

        if (operation != null) {
            return operation.getOperationStyle().equals(OperationType.REQUEST_RESPONSE) || isAsynchronousOperationFaultsEnabled();
        }

        return false;
    }


    /**
     * Method called from View, whenever an Add action is called Either by the
     * tool bar buttons or the menu actions
     * 
     * @param the
     *            element from which the Operation is determined is one of it's
     *            children node representation
     */
    public void addNewParameter(ITreeNode selectedNode, final OperationCategory category) {
        if (!isEditAllowed(selectedNode.getModelObject())) {
            final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.SIFormPageController_msg_editing_element_X_not_allowed, selectedNode.getDisplayName()));
            StatusUtils.showStatusDialog(Messages.SIFormPageController_dlg_title_add_new_parameter, status);
            return;
        }

        // search for the parent of the newly aded param
        while (!(selectedNode instanceof OperationNode)) {
            selectedNode = selectedNode.getParent();
        }

        switch (category) {
        case INPUT:
            addNewInputParameter(((OperationNode) selectedNode).getModelObject());
            break;
        case OUTPUT:
            addNewOutputParameter(((OperationNode) selectedNode).getModelObject());
            break;
        case FAULT:
            throw new IllegalArgumentException("to add a fault use SIFormPageController.addNewFault()"); //$NON-NLS-1$
        default:
            throw new IllegalArgumentException("category should be INPUT or OUTPUT"); //$NON-NLS-1$
        }
    }

    public boolean isAddNewInParameterEnabled(final ITreeNode node) {
        if (isResourceReadOnly() || node == null || node.isImportedNode()) {
            return false;
        }

        return (node instanceof OperationNode) || (node instanceof ParameterNode) || (node instanceof FaultNode)
                || (node instanceof OperationCategoryNode);
    }

    public boolean isAddNewOutParameterEnabled(final ITreeNode node) {
        if (isResourceReadOnly() || node == null || node.isImportedNode()) {
            return false;
        }

        final IModelObject modelObject = node.getModelObject();

        IOperation operation = null;

        if (node instanceof OperationNode) {
            operation = (IOperation) modelObject;
        } else if (node instanceof ParameterNode || node instanceof FaultNode) {
            return isAddNewOutParameterEnabled(node.getParent());
        }

        if ((node instanceof OperationCategoryNode) && isPartOfEdittedDocument(node.getParent().getModelObject())) {
            final OperationCategoryNode categoryNode = (OperationCategoryNode) node;
            operation = categoryNode.getOperation();
        }

        if (operation != null) {
            return OperationType.REQUEST_RESPONSE.equals(operation.getOperationStyle());
        }

        return false;
    }

    /**
     * Method called to determine if the passed treeNode is deletable via this
     * controller. This method checks if the model is set as editable, if the
     * node is one of the managed by the controller types and if any of the
     * preEdit listeners deny the delete (via the super.isDeleteAllowed)
     * 
     * @param node
     *            the node for which the check is performed
     * @return true if the node could be deleted, false if not.
     */
    public boolean isDeleteItemEnabled(final ITreeNode node) {
        return !isResourceReadOnly()
                && node != null
                && !node.isImportedNode()
                && (node instanceof ServiceInterfaceNode || node instanceof OperationNode || node instanceof ParameterNode || node instanceof FaultNode)
                && isPartOfEdittedDocument(node.getModelObject());
    }

    /**
     * 
     * @see isDeleteItemEnabled() for multiple selection
     */
    public boolean isDeleteItemsEnabled(final Object[] nodes) {

        if (nodes == null)
            return false;
        if (nodes.length == 0) {
            return false;
        }
        for (final Object node : nodes) {
            if (!(node instanceof ITreeNode)) {
                return false;
            }
            if (!isDeleteItemEnabled((ITreeNode) node)) {
                return false;
            }

        }
        return true;
    }

    /**
     * Method called to determine if the passed treeNode could be renamed via
     * this controller. This method checks if the model is set as editable, if
     * the node is one of the managed by the controller types and if any of the
     * preEdit listeners deny the delete (via the super.isDeleteAllowed)
     * 
     * @param node
     *            the node for which the check is performed
     * @return true if the node could be deleted, false if not.
     */
    public boolean isRenameItemEnabled(final ITreeNode node) {
        return !isResourceReadOnly()
                && node != null
                && !node.isReadOnly()
                && isPartOfEdittedDocument(node.getModelObject())
                && (node instanceof ServiceInterfaceNode || node instanceof OperationNode || node instanceof ParameterNode || node instanceof FaultNode)
                && node.getModelObject() instanceof INamedObject;
    }

    /**
     * After a delete action, the node related to that element and its child
     * nodes need to be removed This methods removes such nodes from the tree
     * node mapper, which contains all nodes present in the tree.
     * 
     * @param treeNode
     */
    public void removeNodeNItsChildrenFromMap(final ITreeNode treeNode) {
        if (treeNode.hasChildren()) {
            for (final Object childNode : treeNode.getChildren()) {
                if (childNode instanceof ITreeNode)
                    removeNodeNItsChildrenFromMap((ITreeNode) childNode);
            }
        }
        if (treeNode instanceof OperationCategoryNode)
            getTreeNodeMapper().removeCategoryNodeFromMap(((OperationCategoryNode) treeNode).getOperationCategory().toString(),
                    ((OperationCategoryNode) treeNode).getOperation());
        else
            getTreeNodeMapper().removeNodeFromMap(treeNode.getModelObject());
    }

    /**
     * Deletes the selected Operation <br>
     * Caution - this method does not consider any pre-edit listeners. The
     * caller should taka cera of that (e.g. {@link
     * this#deleteItemTriggered(ITreeNode)}
     * 
     * @param operationNode
     */
    protected void deleteOperation(final ITreeNode operationNode) {
        final ITreeNode nextTreeNode = getNextTreeNode(operationNode);
        final ITreeNode serviceInterfaceNode = operationNode.getParent();
        final IServiceInterface serviceInterface = (IServiceInterface) serviceInterfaceNode.getModelObject();
        // Moving this before the remove statement to avoid the selection
        // highlighting problem.
        fireTreeNodeSelectionEvent(nextTreeNode);
        try {
            final IOperation operation = (IOperation) operationNode.getModelObject();
            final DeleteOperationCommand cmd = new DeleteOperationCommand(getWsdlModelRoot(), serviceInterface, operation);
            final IStatus status = model.getEnv().execute(cmd);
            if (!StatusUtils.canContinue(status)) {
                StatusUtils.showStatusDialog(Messages.SIFormPageController_dlg_title_delete_operation, MessageFormat.format(
                        Messages.SIFormPageController_msg_failure_delete_operation_X, operation.getName()), status);
                return;
            }
        } catch (final ExecutionException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Failed to delete operation " + operationNode.getDisplayName(), e); //$NON-NLS-1$
            return;
        }
        removeNodeNItsChildrenFromMap(operationNode);
    }

    /**
     * Method called from View, whenever a Delete action is called either by the
     * tool bar buttons or the menu actions
     */
    public void deleteItemsTriggered(final List<ITreeNode> items) {

        final IStatus status = deleteItems(items);

        if (status.getSeverity() != IStatus.OK) {
            StatusUtils.showStatusDialog(Messages.DataTypesFormPageController_dlg_title_delete_element, status);
            return;
        }

    }

    private IStatus deleteItems(final Collection<ITreeNode> items) {

        if (items == null || items.size() == 0) {
            return new Status(IStatus.INFO, Activator.PLUGIN_ID,
                    Messages.DataTypesFormPageController_msg_target_element_can_not_be_deleted);
        }
        ITreeNode nextTreeNode = null;
        final Set<IModelObject> parents = new HashSet<IModelObject>();
        final ArrayList<IModelObject> objectsToRemove = new ArrayList<IModelObject>();

        for (final ITreeNode item : items) {

            final IModelObject object = item.getModelObject();

            if (!isEditAllowed(object)) {
                return new Status(IStatus.INFO, Activator.PLUGIN_ID,
                        Messages.DataTypesFormPageController_msg_target_element_can_not_be_deleted);
            }
            // select a next tree node
            if (nextTreeNode == null) {
                final ITreeNode next = getNextTreeNode(item);
                if (next != null && !items.contains(next)) {
                    nextTreeNode = next;

                }
            }

            objectsToRemove.add(object);
            parents.add(object.getParent());
        }
        final DeleteSetCommand delete = new DeleteSetCommand(model, parents, objectsToRemove);
        try {
            final IStatus status = model.getEnv().execute(delete);
            if (!StatusUtils.canContinue(status)) {

                return status;
            }
            fireTreeNodeSelectionEvent(nextTreeNode);
        } catch (final ExecutionException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Cannot delete elements ", e); //$NON-NLS-1$
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                    Messages.DataTypesFormPageController_msg_target_element_can_not_be_deleted, e);
        }
        for (final ITreeNode item : items) {
            removeNodeNItsChildrenFromMap(item);
        }
        return Status.OK_STATUS;
    }

    /**
     * Returns the next tree node after a delete operation on a given node. 3
     * different cases are handled a. First, it returns the tree node which is
     * after this tree node. b. When there are no nodes after this tree node
     * (when the last tree node is deleted), it returns the previous tree node
     * c. If the deleted node is the only child, then the parent node is
     * selected.
     * 
     * @param selectedTreeNode
     * @return
     */
    @Override
    protected ITreeNode getNextTreeNode(final ITreeNode selectedTreeNode) {
        if (null == selectedTreeNode) {
            return null;
        }

        Object[] children = null;

        final ITreeNode parent = selectedTreeNode.getParent();
        if (null == parent) {
            if (selectedTreeNode instanceof ServiceInterfaceNode) {
                children = getAllServiceInterfaceNodes((IDescription) ((ServiceInterfaceNode) selectedTreeNode).getModelObject()
                        .getParent());
            } else {
                throw new InvalidParameterException(
                        "the tree node given is a Parentless tree node, not an instace of ServiceInterfaceNode"); //$NON-NLS-1$
            }
        } else {
            if (parent instanceof OperationCategoryNode && !showCategoryNodes) {
                children = parent.getParent().getChildren();
            } else {
                children = parent.getChildren();
            }
        }
        ITreeNode nextSiblingTreeNode = getNextSiblingTreeNode(selectedTreeNode, children);
        if (nextSiblingTreeNode instanceof OperationCategoryNode && !showCategoryNodes) {
            nextSiblingTreeNode = nextSiblingTreeNode.getParent();
        }
        return nextSiblingTreeNode;
    }

    /*
     * Checks if a parameter with the given name already exists
     */
    protected boolean isParamExisting(final IOperation operation, final OperationCategory type, final String newName) {
        if (type.equals(OperationCategory.INPUT)) {
            if (!operation.getInputParameter(newName).isEmpty()) {
                return true;
            }
        } else if (type.equals(OperationCategory.OUTPUT)) {
            if (!operation.getOutputParameter(newName).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void editDescriptionNamespaceTriggered(final String newNamespace) {
        final IDescription description = getWsdlModelRoot().getDescription();
        if (!isEditAllowed(description)) {
            final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.SIFormPageController_msg_editing_wsdl_X_not_allowed, description.getLocation()));
            StatusUtils.showStatusDialog(Messages.SIFormPageController_dlg_title_editing_namespace, status);
            return;
        }
        if (newNamespace == null) {
            return;
        }
        final String currentNamespace = description.getNamespace();
        if (currentNamespace == null ? newNamespace.trim() == UIConstants.EMPTY_STRING : currentNamespace.equals(newNamespace
                .trim())) {
            return;
        }
        try {
            final IStatus status = model.getEnv().execute(
                    new ChangeDefinitionTNSCompositeCommand(getWsdlModelRoot(), getWsdlModelRoot().getDescription(), newNamespace));
            if (!StatusUtils.canContinue(status)) {
                StatusUtils.showStatusDialog(Messages.SIFormPageController_dlg_title_editing_namespace,
                        Messages.SIFormPageController_msg_failure_set_target_namespace, status);
                return;
            }
        } catch (final ExecutionException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR,
                    "Failed to edit namespace of description " + description.getLocation(), e); //$NON-NLS-1$
        }

    }

    /**
     * Method called to when changing the type of a parameter
     * 
     */
    public void editParameterTypeTriggered(final ITreeNode node, final IType newType) {
        if (!((node instanceof ParameterNode) || (node instanceof FaultNode)) || null == newType){
            throw new IllegalArgumentException();
        }

        final IParameter parameter = utils().getParameterFromUINode(node);

        if (!isEditAllowed(parameter)) {
            final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.SIFormPageController_msg_editing_element_X_not_allowed, node.getDisplayName()));
            StatusUtils.showStatusDialog(Messages.SIFormPageController_dlg_title_edit_parameter_type, status);

            return;
        }

        if (parameter == null && node instanceof FaultNode) {
            final FaultNode faultNode = (FaultNode) node;
            final IModelObject modelObject = faultNode.getModelObject();
            final IFault iFault = (IFault) modelObject;

            try {
                model.getEnv().execute(new SetFaultTypeCommand((IWsdlModelRoot) modelObject.getModelRoot(), iFault, newType));
            } catch (final ExecutionException e) {
                Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Failed to edit type of fault " + iFault.getName(), e); //$NON-NLS-1$
            }
            return;
        }

        if (null != parameter.getType() && parameter.getType().equals(newType))
            return;

        editParameterType(parameter, newType);
    }

    /**
     * Method called to when changing the type of a parameter
     * 
     */
    public void editParameterTypeTriggered(final ITreeNode node, final String newTypeName) {
        if (!((node instanceof ParameterNode) || (node instanceof FaultNode))) {
            return;
        }
        final IType newType = utils().getCommonTypeByName(newTypeName);

        if (newType == null) {
            return;
        }

        final IParameter parameter = utils().getParameterFromUINode(node);

        if (!isEditAllowed(parameter)) {
            final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.SIFormPageController_msg_editing_element_X_not_allowed, node.getDisplayName()));
            StatusUtils.showStatusDialog(Messages.SIFormPageController_dlg_title_edit_parameter_type, status);

            return;
        }

        if (null != parameter.getType() && parameter.getType().getName().equals(newTypeName)) {
            return;
        }

        editParameterType(parameter, newType);

    }

    /**
     * Method setting the type of a parameter. Does not perform checks on the
     * parameters - they should have already be performed.
     * 
     * @param parameter
     *            the parameter which's type is being changed
     * @param newType
     *            the type to be set
     */
    private void editParameterType(final IParameter parameter, final IType newType) {
        IOperation operation = null;
        if (parameter.getParent() != null && parameter.getParent() instanceof IFault) {
            operation = (IOperation) parameter.getParent().getParent();
        } else {
            operation = (IOperation) parameter.getParent();
        }

        if (operation == null) {
            return;
        }

        if (null != newType) {
            try {
                final SetParameterTypeCommand cmd = new SetParameterTypeCommand(parameter, newType);
                final IStatus status = model.getEnv().execute(cmd);
                if (!StatusUtils.canContinue(status)) {
                    StatusUtils.showStatusDialog(Messages.SIFormPageController_dlg_title_set_parameter_type, MessageFormat
                            .format(Messages.SIFormPageController_msg_failure_set_type_X_of_parameter_Y, newType.getName(),
                                    parameter.getName()), status);
                    return;
                }
            } catch (final ExecutionException e) {
                Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Failed to edit type of parameter " + parameter.getName(), e); //$NON-NLS-1$
            }
        }
    }

    /**
     * Method called from View, whenever an Edit action is performed by renaming
     * the element
     */
    public void editItemNameTriggered(final ITreeNode treeNode, final String newName) {

        if (newName == null) {
            return;
        }

        // if object is nof of type INamed Object - (it could not be named,
        // right?)
        if (!(treeNode.getModelObject() instanceof INamedObject)) {
            return;
        }

        if (!isEditAllowed(treeNode.getModelObject())) {
            final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.SIFormPageController_msg_editing_element_X_not_allowed, treeNode.getDisplayName()));
            StatusUtils.showStatusDialog(Messages.SIFormPageController_dlg_title_edit_name, status);
            return;
        }

        final INamedObject modelObject = (INamedObject) treeNode.getModelObject();

        if (modelObject.getName() != null && modelObject.getName().trim().equals(newName.trim())) {
            return;
        }

        try {

            AbstractNotificationOperation command = null;
            if (treeNode instanceof ParameterNode) {
                final IParameter parameter = (IParameter) modelObject;
                command = new RenameParameterCommand(getWsdlModelRoot(), parameter, newName);
            }
            if (treeNode instanceof FaultNode) {
                command = new RenameFaultCommand(getWsdlModelRoot(), (IFault) modelObject, newName);
            }

            if (modelObject instanceof IServiceInterface) {
                command = new RenameServiceInterfaceCommand(getWsdlModelRoot(), (IServiceInterface) modelObject, newName);
            }
            if (modelObject instanceof IOperation) {
                command = new RenameOperationCommand(getWsdlModelRoot(), (IOperation) modelObject, newName);
            }
            if (command == null) {
                throw new IllegalInputException("controller can not hangle the rename of this object"); //$NON-NLS-1$
            }
            final IStatus status = model.getEnv().execute(command);
            if (!StatusUtils.canContinue(status)) {
                StatusUtils.showStatusDialog(Messages.SIFormPageController_dlg_title_rename_item, MessageFormat.format(
                        Messages.SIFormPageController_msg_failure_set_new_name_X_item_Y, newName, treeNode.getDisplayName()),
                        status);
                return;
            }
        } catch (final IllegalInputException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Failed to edit name of item " + treeNode.getDisplayName(), e); //$NON-NLS-1$
            fireShowErrorMsgEvent(Messages.SIFormPageController_error_msg_entered_name_is_not_valid);
        } catch (final ExecutionException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Failed to edit name of item " + treeNode.getDisplayName(), e); //$NON-NLS-1$
        }
    }

    public void editOperationTypeTriggered(final ITreeNode operationTreeNode, final OperationType type) {
        if (operationTreeNode == null || type == null) {
            return;
        }

        final IOperation operation = (IOperation) operationTreeNode.getModelObject();

        if (!isEditAllowed(operation)) {
            final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.SIFormPageController_msg_failure_edit_type_of_operation_X, operation.getName()));
            StatusUtils.showStatusDialog(Messages.SIFormPageController_dlg_title_edit_operation_type, status);
            return;
        }
        try {
            final IStatus status = model.getEnv().execute(
                    new ChangeOperationTypeCommand(getWsdlModelRoot(), operation, type, isAsynchronousOperationFaultsEnabled()));
            if (!StatusUtils.canContinue(status)) {
                StatusUtils.showStatusDialog(Messages.SIFormPageController_dlg_title_edit_operation_type, MessageFormat.format(
                        Messages.SIFormPageController_msg_failure_edit_type_of_operation_X, operation.getName()), status);
                return;
            }
        } catch (final ExecutionException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Failed to edit type of operation " + operation.getName(), e); //$NON-NLS-1$
        }
    }

    @Override
    public boolean canEdit(final ITreeNode node) {
        if (null == node || null == node.getModelObject() || node.isReadOnly()) {
            return false;
        }
        return isEditAllowed(node.getModelObject());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.listeners.IWSDLViewListener#canEdit
     * (org.eclipse.wst.sse.sieditor.ui.wsdltree.nodes.ITreeNode)
     */
    public Object[] getAllServiceInterfaceNodes(final IDescription description) {
        final ArrayList<ITreeNode> serviceInterfaceNodes = new ArrayList<ITreeNode>();
        serviceInterfaceNodes.addAll(getServiceInterfaceNodes(null, description));

        // Put imported services node
        final ITreeNode treeNode = treeNodeMapper.getTreeNode(description);
        ImportedServicesNode importedServicesNode = null;
        if (treeNode instanceof ImportedServicesNode) {
            importedServicesNode = (ImportedServicesNode) treeNode;
        } else {
            importedServicesNode = WSDLNodeFactory.getInstance().createImportedServicesNode(description, this);
        }
        if (importedServicesNode.hasChildren()) {
            serviceInterfaceNodes.add(importedServicesNode);
            treeNodeMapper.addToNodeMap(description, importedServicesNode);
        }

        return serviceInterfaceNodes.toArray();
    }

    public List<ServiceInterfaceNode> getServiceInterfaceNodes(final ITreeNode parent, final IDescription description) {
        final List<IServiceInterface> serviceInterfaces = new ArrayList<IServiceInterface>(description.getAllInterfaces());
        final List<ServiceInterfaceNode> serviceInterfaceNodes = new ArrayList<ServiceInterfaceNode>();
        ServiceInterfaceNode serviceInterfaceNode = null;
        for (final IServiceInterface serviceInterface : serviceInterfaces) {
            serviceInterfaceNode = (ServiceInterfaceNode) treeNodeMapper.getTreeNode(serviceInterface);
            if (serviceInterfaceNode == null) {
                serviceInterfaceNode = WSDLNodeFactory.getInstance().createServiceInterfacenode(parent, serviceInterface, this);
            }
            serviceInterfaceNodes.add(serviceInterfaceNode);
        }
        return serviceInterfaceNodes;
    }

    protected static org.eclipse.wst.sse.sieditor.model.write.wsdl.api.IDescription getWritableDescription(
            final IDescription description) {
        return (org.eclipse.wst.sse.sieditor.model.write.wsdl.api.IDescription) ModelManager.getInstance().getWriteSupport(
                description);
    }

    protected static org.eclipse.wst.sse.sieditor.model.write.wsdl.api.IServiceInterface getWritableServiceInterface(
            final IServiceInterface serviceInterface) {
        return (org.eclipse.wst.sse.sieditor.model.write.wsdl.api.IServiceInterface) ModelManager.getInstance()
                .getWriteSupport(serviceInterface);
    }

    protected static org.eclipse.wst.sse.sieditor.model.write.wsdl.api.IOperation getWritableOperation(
            final IOperation operation) {
        return (org.eclipse.wst.sse.sieditor.model.write.wsdl.api.IOperation) ModelManager.getInstance().getWriteSupport(
                operation);
    }

    protected static org.eclipse.wst.sse.sieditor.model.write.wsdl.api.IParameter getWritableParameter(
            final IParameter parameter) {
        return (org.eclipse.wst.sse.sieditor.model.write.wsdl.api.IParameter) ModelManager.getInstance().getWriteSupport(
                parameter);
    }

    protected static org.eclipse.wst.sse.sieditor.model.write.api.INamedObject getWritableNamedObject(
            final INamedObject namedObject) {
        return (org.eclipse.wst.sse.sieditor.model.write.api.INamedObject) ModelManager.getInstance().getWriteSupport(
                namedObject);
    }

    @Override
    protected IModelObject getModelObject() {
        return getWsdlModelRoot().getDescription();
    }

    /**
     * Method setting a reference to the {@link DataTypesFormPageController} of
     * the model <br>
     * to this {@link SIFormPageController} in order for the SI controller to
     * operate with the DT part of the model
     * 
     * @param dtController
     */
    public void setDTController(final IDataTypesFormPageController dtController) {
        this.dtController = dtController;
    }

    public IDataTypesFormPageController getDtController() {
        return dtController;
    }

    @Override
    protected ISIComponentSearchListProvider createSearchListProvider(final IModelObject selectedModelObject,
            final IFile contextFile, final XSDSchema[] schemas, final boolean showComplexTypes) {
        if (selectedModelObject instanceof OperationParameter) {
            return new SIEditorSearchListProvider(contextFile, schemas, true, true, true);
        }
        return super.createSearchListProvider(selectedModelObject, contextFile, schemas, showComplexTypes);
    }

    @Override
    protected String getEditorID() {
        return ServiceInterfaceEditor.EDITOR_ID;
    }

    // ===========================================================
    // helpers
    // ===========================================================

    protected UIUtils utils() {
        return UIUtils.instance();
    }

}
