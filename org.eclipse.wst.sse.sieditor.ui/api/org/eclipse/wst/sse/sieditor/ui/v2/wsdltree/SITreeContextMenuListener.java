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
package org.eclipse.wst.sse.sieditor.ui.v2.wsdltree;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractTreeContextMenuListener;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.actionenablement.SIEActionEnablementForSelectionManager;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategory;

public class SITreeContextMenuListener extends AbstractTreeContextMenuListener {

    protected Action addOperationAction;

    protected Action deleteAction;

    protected Action addInParameterAction;

    protected Action addOutParameterAction;

    protected Action addFaultAction;

    protected Action addServiceAction;

    private GroupMarker groupItems;

    private Separator groupEdit;

    private boolean init = true;

    private final SIEActionEnablementForSelectionManager enablementManager;

    public SITreeContextMenuListener(final SIFormPageController controller, final TreeViewer treeViewer) {
        super(controller, treeViewer);
        this.enablementManager = new SIEActionEnablementForSelectionManager(this);
        this.enablementManager.setController(getController());
    }

    @Override
    protected SIFormPageController getController() {
        return (SIFormPageController) super.getController();
    }

    public void menuAboutToShow(final IMenuManager menu) {
        createGroups(menu);
        addAddServiceMenuAction(menu);
        addAddOperationMenuAction(menu);
        addAddInParamterMenuAction(menu);
        addAddOutParamterMenuAction(menu);
        addAddFaultMenuAction(menu);
        addOpenInNewEditorMenuAction(menu);
        addDeleteMenuAction(menu);
        updateActionsState(((IStructuredSelection) treeViewer.getSelection()));
    }

    protected void updateActionsState(final IStructuredSelection selection) {
        enablementManager.selectionChanged(selection);
    }

    private void createGroups(final IMenuManager menu) {
        if (init) {
            groupItems = new GroupMarker(ContextMenuConstants.GROUP_ADD_ITEMS);
            groupEdit = new Separator(ContextMenuConstants.GROUP_EDIT);
            init = false;
        }

        menu.add(groupItems);
        menu.add(groupOpenInNewEditor);
        menu.add(groupEdit);
    }

    public void addAddServiceMenuAction(final IMenuManager manager) {
        if (addServiceAction == null) {
            addServiceAction = new Action(Messages.SITreeContextMenuListener_add_service_action, Action.AS_PUSH_BUTTON) {
                @Override
                public void run() {
                    getController().addNewServiceInterface();
                }
            };
            addServiceAction.setId(ContextMenuConstants.ADD_SERVICE_ACTION_ID);
        }
        manager.appendToGroup(ContextMenuConstants.GROUP_ADD_ITEMS, addServiceAction);
    }

    public void addAddOperationMenuAction(final IMenuManager manager) {
        if (addOperationAction == null) {
            addOperationAction = new Action(Messages.SITreeContextMenuListener_add_operation_action, Action.AS_PUSH_BUTTON) {
                @Override
                public void run() {
                    getController().addNewOperation(
                            (ITreeNode) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement());
                }
            };
            addOperationAction.setId(ContextMenuConstants.ADD_OPERATION_ACTION_ID);
        }
        manager.appendToGroup(ContextMenuConstants.GROUP_ADD_ITEMS, addOperationAction);
    }

    public void addAddInParamterMenuAction(final IMenuManager manager) {
        if (addInParameterAction == null) {
            addInParameterAction = new Action(Messages.SITreeContextMenuListener_add_in_parameter_action, Action.AS_PUSH_BUTTON) {
                @Override
                public void run() {
                    getController().addNewParameter(
                            (ITreeNode) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement(),
                            OperationCategory.INPUT);
                }
            };
            addInParameterAction.setId(ContextMenuConstants.ADD_IN_PARAMETER_ACTION_ID);
        }
        manager.appendToGroup(ContextMenuConstants.GROUP_ADD_ITEMS, addInParameterAction);
    }

    public void addAddOutParamterMenuAction(final IMenuManager manager) {
        if (addOutParameterAction == null) {
            addOutParameterAction = new Action(Messages.SITreeContextMenuListener_add_out_parameter_action, Action.AS_PUSH_BUTTON) {
                @Override
                public void run() {
                    getController().addNewParameter(
                            (ITreeNode) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement(),
                            OperationCategory.OUTPUT);
                }
            };
            addOutParameterAction.setId(ContextMenuConstants.ADD_OUT_PARAMETER_ACTION_ID);
        }
        manager.appendToGroup(ContextMenuConstants.GROUP_ADD_ITEMS, addOutParameterAction);
    }

    public void addAddFaultMenuAction(final IMenuManager manager) {
        if (addFaultAction == null) {
            addFaultAction = new Action(Messages.SITreeContextMenuListener_add_fault_action, Action.AS_PUSH_BUTTON) {
                @Override
                public void run() {
                    getController().addNewFault((ITreeNode) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement());
                }
            };
            addFaultAction.setId(ContextMenuConstants.ADD_FAULT_ACTION_ID);
        }
        manager.appendToGroup(ContextMenuConstants.GROUP_ADD_ITEMS, addFaultAction);
    }

    /**
     * Displays this action in the context menu.
     * 
     * @param manager
     */
    public void addDeleteMenuAction(final IMenuManager manager) {
        if (deleteAction == null) {
            deleteAction = new Action(Messages.SITreeContextMenuListener_delete_action, Action.AS_PUSH_BUTTON) {
                @Override
                public void run() {
                    final List<ITreeNode> items = ((IStructuredSelection) treeViewer.getSelection()).toList();
                    getController().deleteItemsTriggered(items);
                }
            };
            deleteAction.setId(ContextMenuConstants.DELETE_ACTION_ID);
        }
        manager.appendToGroup(ContextMenuConstants.GROUP_EDIT, deleteAction);
    }

    // =========================================================
    // getters
    // =========================================================

    public Action getAddFaultAction() {
        return addFaultAction;
    }

    public Action getAddInParameterAction() {
        return addInParameterAction;
    }

    public Action getAddOperationAction() {
        return addOperationAction;
    }

    public Action getAddOutParameterAction() {
        return addOutParameterAction;
    }

    public Action getAddServiceAction() {
        return addServiceAction;
    }

    public Action getDeleteAction() {
        return deleteAction;
    }
}
