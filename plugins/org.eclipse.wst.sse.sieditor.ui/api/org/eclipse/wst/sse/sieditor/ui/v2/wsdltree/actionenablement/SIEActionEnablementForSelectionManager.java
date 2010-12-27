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
package org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.actionenablement;

import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.formpage.SIMasterDetailsBlock;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.SITreeContextMenuListener;

public class SIEActionEnablementForSelectionManager {

    // =========================================================
    // fields
    // =========================================================
    
    private SIFormPageController controller;

    private final SIMasterDetailsBlock masterBlock;
    private final SITreeContextMenuListener menuListener;

    public SIEActionEnablementForSelectionManager(final SIMasterDetailsBlock masterBlock) {
        this.masterBlock = masterBlock;
        this.menuListener = null;
    }

    public SIEActionEnablementForSelectionManager(final SITreeContextMenuListener menuListener) {
        this.menuListener = menuListener;
        this.masterBlock = null;
    }

    /**
     * method for notifying the action enablement manager that the selection has
     * changed
     * 
     * @param selection
     *            - the new selection
     */
    public void selectionChanged(final IStructuredSelection selection) {
        updateMasterBlockActionEnablement(selection);
        updateMenuListenerActionEnablement(selection);
    }

    /**
     * utility method. updates the action enablement of the master block based
     * on the given selection
     * 
     * @param selection
     */
    private void updateMasterBlockActionEnablement(final IStructuredSelection selection) {
        if (masterBlock == null) {
            return;
        }
        masterBlock.getAddFaultBtn().setEnabled(isAddFaultActionEnabled(selection));
        masterBlock.getAddInParameterBtn().setEnabled(isAddInParameterActionEnabled(selection));
        masterBlock.getAddOperationBtn().setEnabled(isAddOperationActionEnabled(selection));
        masterBlock.getAddOutParameterBtn().setEnabled(isAddOutParameterActionEnabled(selection));
        masterBlock.getAddServiceBtn().setEnabled(isAddServiceActionEnabled(selection));
        masterBlock.getRemoveButton().setEnabled(isDeleteActionEnabled(selection));
    }

    /**
     * utility method. update the action enablement of the menu listener based
     * on the given selection
     * 
     * @param selection
     */
    private void updateMenuListenerActionEnablement(final IStructuredSelection selection) {
        if (menuListener == null) {
            return;
        }
        menuListener.getAddServiceAction().setEnabled(isAddServiceActionEnabled(selection));
        menuListener.getAddOperationAction().setEnabled(isAddOperationActionEnabled(selection));
        menuListener.getAddFaultAction().setEnabled(isAddFaultActionEnabled(selection));
        menuListener.getAddInParameterAction().setEnabled(isAddInParameterActionEnabled(selection));
        menuListener.getAddOutParameterAction().setEnabled(isAddOutParameterActionEnabled(selection));
        menuListener.getOpenInNewEditorAction().setEnabled(isOpenInNewEditorActionEnabled(selection));
        menuListener.getDeleteAction().setEnabled(isDeleteActionEnabled(selection));
    }

    // =========================================================
    // setters
    // =========================================================

    public void setController(final SIFormPageController controller) {
        this.controller = controller;
    }

    // =========================================================
    // helpers
    // =========================================================

    protected boolean isAddServiceActionEnabled(final IStructuredSelection selection) {
    	boolean allElementsPartOfEditedDocument = controller.areAllItemsPartOfEditedDocument(selection.toList());
        return selection.isEmpty() ? true : allElementsPartOfEditedDocument && controller.isAddNewServiceInterfaceEnabled((ITreeNode) selection.getFirstElement());
    }

    protected boolean isAddOperationActionEnabled(final IStructuredSelection selection) {
        return (selection.size() != 1) ? false : controller.isAddNewOperationEnabled((ITreeNode) selection.getFirstElement());
    }

    protected boolean isAddFaultActionEnabled(final IStructuredSelection selection) {
        return (selection.size() != 1) ? false : controller.isAddNewFaultEnabled((ITreeNode) selection.getFirstElement());
    }

    protected boolean isAddInParameterActionEnabled(final IStructuredSelection selection) {
        return (selection.size() != 1) ? false : controller.isAddNewInParameterEnabled((ITreeNode) selection.getFirstElement());
    }

    protected boolean isAddOutParameterActionEnabled(final IStructuredSelection selection) {
        return (selection.size() != 1) ? false : controller.isAddNewOutParameterEnabled((ITreeNode) selection.getFirstElement());
    }

    protected boolean isDeleteActionEnabled(final IStructuredSelection selection) {
        return controller.isDeleteItemsEnabled(selection.toArray());
    }

    protected boolean isOpenInNewEditorActionEnabled(final IStructuredSelection selection) {
        return (selection.size() != 1) ? false : controller.isOpenInNewEditorEnabled((ITreeNode) selection
                .getFirstElement());
    }
}
