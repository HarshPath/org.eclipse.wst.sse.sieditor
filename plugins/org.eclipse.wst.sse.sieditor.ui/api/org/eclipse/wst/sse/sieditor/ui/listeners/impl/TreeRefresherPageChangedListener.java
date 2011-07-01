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
package org.eclipse.wst.sse.sieditor.ui.listeners.impl;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorPage;
import org.eclipse.wst.sse.sieditor.ui.listeners.IPageChangedListener;

public class TreeRefresherPageChangedListener implements IPageChangedListener  {

    @Override
    public void pageChanged(final int newPageIndex, final int oldPageIndex, final List pages, final IModelRoot modelRoot) {
        if (oldPageIndex == -1) {
            return;
        }
        final Object newPage = pages.get(newPageIndex);

        if (!(newPage instanceof AbstractEditorPage)) {
            return;
        }
        final TreeViewer treeViewer = ((AbstractEditorPage)newPage).getTreeViewer();
        final Object[] expandedElements = treeViewer.getExpandedElements();
        treeViewer.refresh(true);
        treeViewer.setExpandedElements(expandedElements);
    }

}
