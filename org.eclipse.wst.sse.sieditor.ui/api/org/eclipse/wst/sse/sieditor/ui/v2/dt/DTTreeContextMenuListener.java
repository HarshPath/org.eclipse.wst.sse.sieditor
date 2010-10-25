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
package org.eclipse.wst.sse.sieditor.ui.v2.dt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractTreeContextMenuListener;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.actionenablement.DataTypesEditorActionEnablementForSelectionManager;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;

public class DTTreeContextMenuListener extends AbstractTreeContextMenuListener {

    private Action addNamespaceAction;

    private Action deleteAction;

    private Action addElementAction;

    private Action addSimpleTypeAction;

    private Action addStructureTypeAction;

    private Action addAttributeAction;

    private GroupMarker groupGlobalActions;

    private Separator groupContextActions;

    private Separator groupEdit;

    private Separator groupCopyPaste;

    private Separator groupRefactor;

    private boolean init = true;

    private Action copyTypeAction;

    private Action pasteTypeAction;

    private Action addGlobalElementAction;

    private Separator groupRefactorNamespace;

    private Action extractNamespaceAction;

    private Separator groupRefactorGlobalElement;

    private Action convertToAnonymousTypeWithTypeContentsAction;

    private Action convertToAnonymousTypeAction;

    private Action convertToGlobalTypeAction;

    private final DataTypesEditorActionEnablementForSelectionManager selectionEnablementManager;

    public DTTreeContextMenuListener(final IDataTypesFormPageController controller, final TreeViewer treeViewer) {
        super(controller, treeViewer);
        this.selectionEnablementManager = new DataTypesEditorActionEnablementForSelectionManager(this);
        this.selectionEnablementManager.setController(controller);
    }

    @Override
    protected IDataTypesFormPageController getController() {
        return (IDataTypesFormPageController) super.getController();
    }

    public void menuAboutToShow(final IMenuManager menu) {
        createGroups(menu);

        if (getController() instanceof ISiEditorDataTypesFormPageController) {
            addAddNamespaceMenuAction(menu);
        }
        addAddGlobalElementMenuAction(menu);
        addAddSimpleTypeMenuAction(menu);
        addAddStructureTypeMenuAction(menu);

        addAddElementMenuAction(menu);
        addAddAttributeMenuAction(menu);

        addRefactorSubMenu(menu);

        addOpenInNewEditorMenuAction(menu);

        if (getController() instanceof ISiEditorDataTypesFormPageController) {
            addCopyTypeMenuAction(menu);
            addPasteTypeMenuAction(menu);
        }

        addRemoveMenuAction(menu);

        updateActionsState((IStructuredSelection) treeViewer.getSelection());
    }

    protected void updateActionsState(final IStructuredSelection selection) {
        selectionEnablementManager.selectionChanged(selection);
    }

    private void createGroups(final IMenuManager menu) {
        if (init) {
            groupGlobalActions = new GroupMarker(ContextMenuConstants.GROUP_ADD_ITEMS);
            groupContextActions = new Separator(ContextMenuConstants.GROUP_CONTEXT_ITEMS);
            groupEdit = new Separator(ContextMenuConstants.GROUP_EDIT);
            groupCopyPaste = new Separator(ContextMenuConstants.GROUP_COPY_PASTE);
            groupRefactor = new Separator(ContextMenuConstants.GROUP_REFACTOR);
            groupRefactorGlobalElement = new Separator(ContextMenuConstants.GROUP_REFACTOR_GLOBAL_TYPE);
            groupRefactorNamespace = new Separator(ContextMenuConstants.GROUP_REFACTOR_NAMESPACE);
            init = false;
        }
        menu.add(groupGlobalActions);
        menu.add(groupContextActions);
        menu.add(groupRefactor);
        menu.add(groupOpenInNewEditor);
        menu.add(groupCopyPaste);
        menu.add(groupEdit);
        // menu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    public void addAddNamespaceMenuAction(final IMenuManager manager) {
        if (addNamespaceAction == null) {
            addNamespaceAction = new Action(Messages.DTTreeContextMenuListener_add_namespace_action, Action.AS_PUSH_BUTTON) {
                @Override
                public void run() {
                    ((ISiEditorDataTypesFormPageController) getController()).handleAddNewNamespaceAction();
                }
            };
            addNamespaceAction.setId(ContextMenuConstants.ADD_NAMESPACE_ACTION_ID);
        }
        manager.appendToGroup(ContextMenuConstants.GROUP_ADD_ITEMS, addNamespaceAction);
    }

    public void addAddSimpleTypeMenuAction(final IMenuManager manager) {
        if (addSimpleTypeAction == null) {
            addSimpleTypeAction = new Action(Messages.DTTreeContextMenuListener_add_simple_type_action, Action.AS_PUSH_BUTTON) {
                @Override
                public void run() {
                    getController().handleAddSimpleTypeAction(
                            (ITreeNode) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement());
                }
            };
            addSimpleTypeAction.setId(ContextMenuConstants.ADD_SIMPLE_TYPE_ACTION_ID);
        }
        manager.appendToGroup(ContextMenuConstants.GROUP_ADD_ITEMS, addSimpleTypeAction);
    }

    public void addAddGlobalElementMenuAction(final IMenuManager manager) {
        if (addGlobalElementAction == null) {
            addGlobalElementAction = new Action(Messages.DTTreeContextMenuListener_Add_global_element, Action.AS_PUSH_BUTTON) {
                @Override
                public void run() {
                    getController().handleAddGlobalElementAction(
                            (IDataTypesTreeNode) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement());
                }
            };
            addGlobalElementAction.setId(ContextMenuConstants.ADD_GLOBAL_ELEMENT_ACTION_ID);
        }
        manager.appendToGroup(ContextMenuConstants.GROUP_ADD_ITEMS, addGlobalElementAction);
    }

    public void addAddStructureTypeMenuAction(final IMenuManager manager) {
        if (addStructureTypeAction == null) {
            addStructureTypeAction = new Action(Messages.DTTreeContextMenuListener_add_structure_type_action,
                    Action.AS_PUSH_BUTTON) {
                @Override
                public void run() {
                    getController().handleAddStructureTypeAction(
                            (ITreeNode) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement());
                }
            };
            addStructureTypeAction.setId(ContextMenuConstants.ADD_STRUCTURE_TYPE_ACTION_ID);
        }
        manager.appendToGroup(ContextMenuConstants.GROUP_ADD_ITEMS, addStructureTypeAction);
    }

    public void addAddElementMenuAction(final IMenuManager manager) {
        if (addElementAction == null) {
            addElementAction = new Action(Messages.DTTreeContextMenuListener_add_element_action, Action.AS_PUSH_BUTTON) {
                @Override
                public void run() {
                    getController().handleAddElementAction(
                            (ITreeNode) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement());
                }
            };
            addElementAction.setId(ContextMenuConstants.ADD_ELEMENT_ACTION_ID);
        }
        manager.appendToGroup(ContextMenuConstants.GROUP_CONTEXT_ITEMS, addElementAction);
    }

    private void addAddAttributeMenuAction(final IMenuManager manager) {
        if (addAttributeAction == null) {
            addAttributeAction = new Action(Messages.DataTypesMasterDetailsBlock_add_attribute_button, Action.AS_PUSH_BUTTON) {
                @Override
                public void run() {
                    getController().handleAddAttributeAction(
                            (ITreeNode) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement());
                }
            };
            addAttributeAction.setId(ContextMenuConstants.ADD_ATTRIBUTE_ACTION_ID);
        }
        manager.appendToGroup(ContextMenuConstants.GROUP_CONTEXT_ITEMS, addAttributeAction);
    }

    private void addRefactorSubMenu(final IMenuManager manager) {
        final IMenuManager submenuManager = new MenuManager(Messages.DTTreeContextMenuListener_refactor_submenu,
                ContextMenuConstants.REFACTOR_SUBMENU_ID);

        submenuManager.add(groupRefactorGlobalElement);
        submenuManager.add(groupRefactorNamespace);

        addExtractNamespaceAction(submenuManager);

        addConvertToGlobalTypeAction(submenuManager);
        addConvertToAnonymoysTypeAction(submenuManager);
        addConvertToAnonymoysTypeWithTypeContentsAction(submenuManager);

        manager.appendToGroup(ContextMenuConstants.GROUP_REFACTOR, submenuManager);
    }

    private void addExtractNamespaceAction(final IMenuManager submenuManager) {
        if (extractNamespaceAction == null) {
            extractNamespaceAction = new Action(Messages.DTTreeContextMenuListener_extract_namespace_action,
                    Action.AS_PUSH_BUTTON) {
                @Override
                public void run() {
                    getController().handleExtractNamespace(
                            (ITreeNode) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement());
                }
            };
            extractNamespaceAction.setId(ContextMenuConstants.EXTRACT_NAMESPACE_ACTION_ID);
        }
        submenuManager.appendToGroup(ContextMenuConstants.GROUP_REFACTOR_NAMESPACE, extractNamespaceAction);
    }

    private void addConvertToGlobalTypeAction(final IMenuManager submenuManager) {
        if (convertToGlobalTypeAction == null) {
            convertToGlobalTypeAction = new Action(Messages.DTTreeContextMenuListener_make_type_global_context_menu_label,
                    Action.AS_PUSH_BUTTON) {
                @Override
                public void run() {
                    getController().handleConvertToGlobalAction(
                            (ITreeNode) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement());
                }
            };
            convertToGlobalTypeAction.setId(ContextMenuConstants.CONVERT_TO_GLOBAL_TYPE_ACTION_ID);
        }
        submenuManager.appendToGroup(ContextMenuConstants.GROUP_REFACTOR_GLOBAL_TYPE, convertToGlobalTypeAction);
    }

    private void addConvertToAnonymoysTypeAction(final IMenuManager submenuManager) {
        if (convertToAnonymousTypeAction == null) {
            convertToAnonymousTypeAction = new Action(Messages.DTTreeContextMenuListener_make_type_anonymous_context_menu_label,
                    Action.AS_PUSH_BUTTON) {
                @Override
                public void run() {
                    getController().handleConvertToAnonymousTypeAction(
                            (ITreeNode) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement());
                }
            };
            convertToAnonymousTypeAction.setId(ContextMenuConstants.CONVERT_TO_ANONYMOUS_TYPE_ID);
        }
        submenuManager.appendToGroup(ContextMenuConstants.GROUP_REFACTOR_GLOBAL_TYPE, convertToAnonymousTypeAction);
    }

    private void addConvertToAnonymoysTypeWithTypeContentsAction(final IMenuManager submenuManager) {
        if (convertToAnonymousTypeWithTypeContentsAction == null) {
            convertToAnonymousTypeWithTypeContentsAction = new Action(
                    Messages.DTTreeContextMenuListener_inline_structure_type_action, Action.AS_PUSH_BUTTON) {
                @Override
                public void run() {
                    getController().handleConvertToAnonymousTypeWithTypeContentsAction(
                            (ITreeNode) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement());
                }
            };
            convertToAnonymousTypeWithTypeContentsAction
                    .setId(ContextMenuConstants.CONVERT_TO_ANONYMOUS_TYPE_WITH_TYPE_CONTENTS_ID);
        }
        submenuManager.appendToGroup(ContextMenuConstants.GROUP_REFACTOR_GLOBAL_TYPE,
                convertToAnonymousTypeWithTypeContentsAction);
    }

    /**
     * Displays this action in the context menu.
     * 
     * @param manager
     */
    public void addRemoveMenuAction(final IMenuManager manager) {
        if (deleteAction == null) {
            deleteAction = new Action(Messages.DTTreeContextMenuListener_remove_action, Action.AS_PUSH_BUTTON) {
                @Override
                public void run() {
                    getController().handleRemoveAction(((IStructuredSelection) treeViewer.getSelection()).toList());
                }
            };
            deleteAction.setId(ContextMenuConstants.REMOVE_ACTION_ID);
        }
        manager.appendToGroup(ContextMenuConstants.GROUP_COPY_PASTE, deleteAction);
    }

    public void addCopyTypeMenuAction(final IMenuManager manager) {
        if (copyTypeAction == null) {
            copyTypeAction = new Action(Messages.DTTreeContextMenuListener_0, Action.AS_PUSH_BUTTON) {
                @Override
                public void run() {
                    getController().handleCopyTypeAction(
                            (ITreeNode) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement());
                }
            };
            copyTypeAction.setId(ContextMenuConstants.COPY_TYPE_ACTION_ID);
        }
        manager.appendToGroup(ContextMenuConstants.GROUP_COPY_PASTE, copyTypeAction);
    }

    public void addPasteTypeMenuAction(final IMenuManager manager) {
        if (pasteTypeAction == null) {
            pasteTypeAction = new Action(Messages.DTTreeContextMenuListener_1, Action.AS_PUSH_BUTTON) {
                @Override
                public void run() {
                    getController().handlePasteTypeAction(
                            (ITreeNode) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement());
                }
            };
            pasteTypeAction.setId(ContextMenuConstants.PASTE_TYPE_ACTION_ID);
        }
        manager.appendToGroup(ContextMenuConstants.GROUP_COPY_PASTE, pasteTypeAction);
    }

    // =========================================================
    // getters
    // =========================================================

    @Override
    public Action getOpenInNewEditorAction() {
        return super.getOpenInNewEditorAction();
    }

    public Action getAddAttributeAction() {
        return addAttributeAction;
    }

    public Action getAddElementAction() {
        return addElementAction;
    }

    public Action getAddGlobalElementAction() {
        return addGlobalElementAction;
    }

    public Action getAddNamespaceAction() {
        return addNamespaceAction;
    }

    public Action getAddSimpleTypeAction() {
        return addSimpleTypeAction;
    }

    public Action getAddStructureTypeAction() {
        return addStructureTypeAction;
    }

    public Action getCopyTypeAction() {
        return copyTypeAction;
    }

    public Action getPasteTypeAction() {
        return pasteTypeAction;
    }

    public Action getDeleteAction() {
        return deleteAction;
    }

    public Action getConvertToGlobalTypeAction() {
        return convertToGlobalTypeAction;
    }

    public Action getConvertToAnonymousTypeAction() {
        return convertToAnonymousTypeAction;
    }

    public Action getConvertToAnonymousTypeWithTypeContentsAction() {
        return convertToAnonymousTypeWithTypeContentsAction;
    }

    public Action getExtractNamespaceAction() {
        return extractNamespaceAction;
    }

}
