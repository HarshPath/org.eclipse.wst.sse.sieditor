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
package org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes;

import org.eclipse.swt.graphics.Image;

import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.impl.AbstractTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;

public class ParameterNode extends AbstractTreeNode {

    public ParameterNode(final ITreeNode parent, final IParameter parameter,final SIFormPageController nodeMapperContainer) {
        super(parameter, parent, 
                nodeMapperContainer == null ? null : nodeMapperContainer.getTreeNodeMapper());
    }

    public Object[] getChildren() {
        // Has no children, as this is the last level in the tree
        return null;
    }

    public Image getImage() {
        return ((OperationCategoryNode)super.getParent()).getImage(isReadOnly());
    }

    /**
     * 
     * @return INPUT if this is an input parameter, OUTPUT - for an output
     *         parameter. May return null
     */
    public OperationCategory getOperationCategory() {
        final ITreeNode parent = super.getParent();
        if (parent instanceof OperationCategoryNode) {
            return ((OperationCategoryNode) parent).getOperationCategory();
        }
        return null;
    }
    
}
