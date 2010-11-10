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
/**
 * 
 */
package org.eclipse.wst.sse.sieditor.ui.v2.common;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TreeItem;

import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;

/**
 * A common parent for the cell modifiers of the SI and DT tree
 * 
 * 
 * 
 */
public class AbstractTreeViewerCellModifier implements ICellModifier {

    protected final AbstractFormPageController controller;
    protected Object selectedElement;

    public AbstractTreeViewerCellModifier(AbstractFormPageController controller) {
        this.controller = controller;
    }

    public void setSelectedElement(Object firstElement) {
        this.selectedElement = firstElement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object,
     * java.lang.String)
     */
    public boolean canModify(Object element, String property) {
        return element == selectedElement && element instanceof ITreeNode && !((ITreeNode)element).isReadOnly() && ! controller.isResourceReadOnly();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object,
     * java.lang.String)
     */
    public Object getValue(Object element, String property) {
        if (element instanceof ITreeNode) {
            return ((ITreeNode) element).getDisplayName();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object,
     * java.lang.String, java.lang.Object)
     */
    public void modify(Object element, String property, Object value) {
        if (element instanceof TreeItem) {
            TreeItem treeItem = (TreeItem) element;
            if (treeItem.getData() instanceof ITreeNode) {
                controller.editItemNameTriggered((ITreeNode) treeItem.getData(), (String) value);
            }
        }
    }

}
