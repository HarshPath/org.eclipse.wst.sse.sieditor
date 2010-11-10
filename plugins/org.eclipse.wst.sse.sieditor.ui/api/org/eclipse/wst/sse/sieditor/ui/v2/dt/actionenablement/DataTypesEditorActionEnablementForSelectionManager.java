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
package org.eclipse.wst.sse.sieditor.ui.v2.dt.actionenablement;

import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.wst.sse.sieditor.ui.v2.dt.DTTreeContextMenuListener;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesMasterDetailsBlock;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ISiEditorDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;

public class DataTypesEditorActionEnablementForSelectionManager {

    // =========================================================
    // fields
    // =========================================================

    private IDataTypesFormPageController controller;

    private final DataTypesMasterDetailsBlock masterBlock;
    private final DTTreeContextMenuListener menuListener;

    public DataTypesEditorActionEnablementForSelectionManager(final DataTypesMasterDetailsBlock masterBlock) {
        this.masterBlock = masterBlock;
        this.menuListener = null;
    }

    public DataTypesEditorActionEnablementForSelectionManager(final DTTreeContextMenuListener menuListener) {
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
        masterBlock.getAddGlobalElementButton().setEnabled(isAddGlobalElementEnabled(selection));
        masterBlock.getAddSimpleTypeButon().setEnabled(isAddSimpleTypeEnabled(selection));
        masterBlock.getAddComplexTypeButton().setEnabled(isAddStructureTypeEnabled(selection));
        masterBlock.getAddElementButton().setEnabled(isAddElementEnabled(selection));
        masterBlock.getAddAttributeButton().setEnabled(isAddAttributeEnabled(selection));
        if (masterBlock.getAddNamespacesButton() != null) {
            masterBlock.getAddNamespacesButton().setEnabled(isAddNamespaceEnabled(selection));
        }
        masterBlock.getRemoveButton().setEnabled(isRemoveEnabled(selection));
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
        if (menuListener.getAddNamespaceAction() != null) {
            menuListener.getAddNamespaceAction().setEnabled(isAddNamespaceEnabled(selection));
        }
        menuListener.getAddGlobalElementAction().setEnabled(isAddGlobalElementEnabled(selection));
        menuListener.getAddSimpleTypeAction().setEnabled(isAddSimpleTypeEnabled(selection));
        menuListener.getAddStructureTypeAction().setEnabled(isAddStructureTypeEnabled(selection));
        menuListener.getAddElementAction().setEnabled(isAddElementEnabled(selection));
        menuListener.getAddAttributeAction().setEnabled(isAddAttributeEnabled(selection));
        menuListener.getConvertToGlobalTypeAction().setEnabled(isConvertToGlobalTypeEnabled(selection));
        menuListener.getConvertToAnonymousTypeAction().setEnabled(isConvertToAnonymousTypeEnabled(selection));
        menuListener.getConvertToAnonymousTypeWithTypeContentsAction().setEnabled(
                isConvertToAnonymoysTypeWithTypeContentsEnabled(selection));
        menuListener.getExtractNamespaceAction().setEnabled(isExtractNamespaceEnabled(selection));
        menuListener.getOpenInNewEditorAction().setEnabled(isOpenInNewEditorEnabled(selection));
        if (menuListener.getCopyTypeAction() != null) {
            menuListener.getCopyTypeAction().setEnabled(isCopyEnabled(selection));
        }
        if (menuListener.getPasteTypeAction() != null) {
            menuListener.getPasteTypeAction().setEnabled(isPasteEnabled(selection));
        }
        menuListener.getDeleteAction().setEnabled(isRemoveEnabled(selection));
    }

    // =========================================================
    // helpers
    // =========================================================

    protected boolean isAddNamespaceEnabled(final IStructuredSelection selection) {
        if (controller instanceof ISiEditorDataTypesFormPageController) {
            final boolean allElementsPartOfEditedDocument = controller.areAllItemsPartOfEditedDocument(selection.toList());
            return allElementsPartOfEditedDocument
                    && ((ISiEditorDataTypesFormPageController) controller).isAddNamespaceEnabled((ITreeNode) selection
                            .getFirstElement());
        }
        return false;
    }

    protected boolean isAddGlobalElementEnabled(final IStructuredSelection selection) {
        if (controller instanceof ISiEditorDataTypesFormPageController && selection.size() > 1) {
            return false;
        }
        final boolean allElementsPartOfEditedDocument = controller.areAllItemsPartOfEditedDocument(selection.toList());
        return allElementsPartOfEditedDocument
                && controller.isAddGlobalElementEnabled((IDataTypesTreeNode) selection.getFirstElement());
    }

    protected boolean isAddSimpleTypeEnabled(final IStructuredSelection selection) {
        if (controller instanceof ISiEditorDataTypesFormPageController && selection.size() > 1) {
            return false;
        }
        final boolean allElementsPartOfEditedDocument = controller.areAllItemsPartOfEditedDocument(selection.toList());
        return allElementsPartOfEditedDocument
                && controller.isAddSimpleTypeEnabled((IDataTypesTreeNode) selection.getFirstElement());
    }

    protected boolean isAddStructureTypeEnabled(final IStructuredSelection selection) {
        if (controller instanceof ISiEditorDataTypesFormPageController && selection.size() > 1) {
            return false;
        }
        final boolean allElementsPartOfEditedDocument = controller.areAllItemsPartOfEditedDocument(selection.toList());
        return allElementsPartOfEditedDocument
                && controller.isAddStructureEnabled((IDataTypesTreeNode) selection.getFirstElement());
    }

    protected boolean isAddElementEnabled(final IStructuredSelection selection) {
        return (selection.size() == 1) ? controller.isAddElementEnabled((IDataTypesTreeNode) selection.getFirstElement()) : false;
    }

    protected boolean isAddAttributeEnabled(final IStructuredSelection selection) {
        return (selection.size() == 1) ? controller.isAddAttributeEnabled((IDataTypesTreeNode) selection.getFirstElement())
                : false;
    }

    protected boolean isCopyEnabled(final IStructuredSelection selection) {
        return controller.isCopyEnabled((IDataTypesTreeNode) selection.getFirstElement());
    }

    protected boolean isPasteEnabled(final IStructuredSelection selection) {
        return controller.isPasteEnabled((IDataTypesTreeNode) selection.getFirstElement());
    }

    protected boolean isConvertToAnonymoysTypeWithTypeContentsEnabled(final IStructuredSelection selection) {
        return (selection.size() != 1) ? false : controller
                .isConvertToAnonymousTypeWithTypeContentsEnabled((IDataTypesTreeNode) selection.getFirstElement());
    }

    protected boolean isConvertToGlobalTypeEnabled(final IStructuredSelection selection) {
        return (selection.size() != 1) ? false : controller.isConvertToGlobalTypeEnabled((IDataTypesTreeNode) selection
                .getFirstElement());
    }

    protected boolean isConvertToAnonymousTypeEnabled(final IStructuredSelection selection) {
        return (selection.size() != 1) ? false : controller.isConvertToAnonymousTypeEnabled((IDataTypesTreeNode) selection
                .getFirstElement());
    }

    protected boolean isExtractNamespaceEnabled(final IStructuredSelection selection) {
        return (selection.size() != 1) ? false : controller.isExtractNamespaceEnabled((IDataTypesTreeNode) selection
                .getFirstElement());
    }

    protected boolean isOpenInNewEditorEnabled(final IStructuredSelection selection) {
        return (selection.size() != 1) ? false : controller.isOpenInNewEditorEnabled((IDataTypesTreeNode) selection
                .getFirstElement());
    }

    protected boolean isRemoveEnabled(final IStructuredSelection selection) {
        return controller.isRemoveItemsEnabled(selection.toList());
    }

    // =========================================================
    // setters
    // =========================================================

    public void setController(final IDataTypesFormPageController controller) {
        this.controller = controller;
    }

}
