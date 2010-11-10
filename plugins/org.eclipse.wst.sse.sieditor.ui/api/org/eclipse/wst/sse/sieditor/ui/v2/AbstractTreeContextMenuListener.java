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
package org.eclipse.wst.sse.sieditor.ui.v2;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.ContextMenuConstants;

public abstract class AbstractTreeContextMenuListener implements IMenuListener {

    protected final TreeViewer treeViewer;

    private final IFormPageController controller;

    private Action openInNewEditorAction;

    protected Separator groupOpenInNewEditor;

    public AbstractTreeContextMenuListener(final IFormPageController controller, final TreeViewer treeViewer) {
        this.controller = controller;
        this.treeViewer = treeViewer;

        groupOpenInNewEditor = new Separator(ContextMenuConstants.GROUP_OPEN_IN_NEW_EDITOR);
    }

    protected IFormPageController getController() {
        return controller;
    }

    public void addOpenInNewEditorMenuAction(final IMenuManager manager) {
        if (openInNewEditorAction == null) {
            openInNewEditorAction = new Action(Messages.AbstractTreeContextMenuListener_0, Action.AS_PUSH_BUTTON) {
                @Override
                public void run() {
                    final ITreeNode firstElement = (ITreeNode) ((IStructuredSelection) treeViewer.getSelection())
                            .getFirstElement();
                    controller.openInNewEditor(firstElement);
                }
            };
            openInNewEditorAction.setId(ContextMenuConstants.OPEN_IN_NEW_EDITOR_ACTION_ID);
        }
        manager.appendToGroup(ContextMenuConstants.GROUP_OPEN_IN_NEW_EDITOR, openInNewEditorAction);
    }

    public Action getOpenInNewEditorAction() {
        return openInNewEditorAction;
    }
}
