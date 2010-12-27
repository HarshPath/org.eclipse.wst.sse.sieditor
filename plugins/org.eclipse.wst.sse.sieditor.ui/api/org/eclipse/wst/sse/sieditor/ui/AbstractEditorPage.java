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
package org.eclipse.wst.sse.sieditor.ui;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import org.eclipse.wst.sse.sieditor.model.api.IChangeListener;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractMasterDetailsBlock;
import org.eclipse.wst.sse.sieditor.ui.v2.common.ThreadUtils;
import org.eclipse.wst.sse.sieditor.ui.v2.eventing.ISIEvent;
import org.eclipse.wst.sse.sieditor.ui.v2.eventing.ISIEventListener;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.RepairContextMenuListener;

public abstract class AbstractEditorPage extends FormPage implements IChangeListener, ISIEventListener {

    private static final String CONTEXT_MENU_ID = "org.eclipse.wst.sse.sieditor.ui.wsdltree.menu";//$NON-NLS-1$

    protected boolean readOnly;
    protected boolean isDirty;

    protected IModelRoot modelRoot;
    protected AbstractFormPageController controller;
    protected AbstractMasterDetailsBlock masterDetailsBlock;

    public AbstractEditorPage(final FormEditor editor, final String id, final String title) {
        super(editor, id, title);
    }

    /**
     * Method used to set the common model to the editor page. Must be called
     * before the page addition (before the init() method). To reset (refresh)
     * the model on an already initialized page call with reset =
     * <code>true</code>
     * 
     * @param isReadOnly
     */
    public void setModel(final IModelRoot model, final boolean isReadOnly, final boolean reset) {

        if (reset) {
            modelRoot.removeChangeListener(this);
        }

        readOnly = isReadOnly;
        modelRoot = model;

        if (reset) {
            modelRoot.addChangeListener(this);
            controller.setNewModel(modelRoot, readOnly);
            isDirty = false;
            // must be called from UI
            ThreadUtils.displaySyncExec(new Runnable() {
                public void run() {
                    firePropertyChange(PROP_DIRTY);
                }
            });
        }
    }

    public boolean isSupportedModelObject(final Object anObject) {
        return anObject instanceof IModelObject;
    }

    public IModelRoot getModel() {
        return modelRoot;
    }

    protected void createContextMenu() {
        final MenuManager menuManager = new MenuManager(null, CONTEXT_MENU_ID);
        menuManager.setRemoveAllWhenShown(true);
        final TreeViewer treeViewer = masterDetailsBlock.getTreeViewer();
        menuManager.addMenuListener(createContextMenuListener(treeViewer));

        final Menu contextMenu = menuManager.createContextMenu(treeViewer.getTree());
        treeViewer.getTree().setMenu(contextMenu);

        // Register context menu with platform so that menu items can be
        // contributed through PopupMenu Eclipse extension point.
        getSite().registerContextMenu(menuManager, treeViewer);

        menuManager.addMenuListener(createRepairContextMenuListener());
    }

    protected RepairContextMenuListener createRepairContextMenuListener() {
        return new RepairContextMenuListener();
    }

    protected abstract IMenuListener createContextMenuListener(TreeViewer treeViewer);

    protected void expandTreeNode(final ISIEvent event) {
        for (final Object param : event.getEventParams()) {
            if (param instanceof ITreeNode) {
                getMasterDetailsBlock().getTreeViewer().expandToLevel(param, 1);
            }
        }
    }

    // =========================================================
    // helpers
    // =========================================================

    public AbstractMasterDetailsBlock getMasterDetailsBlock() {
        return masterDetailsBlock;
    }

    public AbstractFormPageController getController() {
        return controller;
    }

    public TreeViewer getTreeViewer() {
        return masterDetailsBlock.getTreeViewer();
    }
}
