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

import org.eclipse.jface.viewers.ICellModifier;

import org.eclipse.wst.sse.sieditor.ui.v2.common.AbstractTreeViewerCellModifier;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategoryNode;

public class SITreeViewerCellModifier extends AbstractTreeViewerCellModifier implements ICellModifier {

    public SITreeViewerCellModifier(SIFormPageController controller) {
        super(controller);
    }
    
    public boolean canModify(Object element, String property) {
        return super.canModify(element, property) && !(element instanceof OperationCategoryNode)
        && !((ITreeNode) element).isReadOnly();
    }

}
