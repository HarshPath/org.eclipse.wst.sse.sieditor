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
package org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes;

import org.eclipse.swt.graphics.Image;

import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;

public class FaultNode extends AbstractWsdlTreeNode {

    public FaultNode(final ITreeNode parent, final IFault fault, final SIFormPageController nodeMapperContainer) {
        super(fault, parent, nodeMapperContainer == null ? null : nodeMapperContainer.getTreeNodeMapper());

    }

    @Override
    public Object[] getChildren() {
        return new Object[0];
    }

    @Override
    public Image getImage() {
        return isReadOnly() ? getImageRegistry().get(Activator.NODE_OPER_FAULT_OBJECT_GRAY) : getImageRegistry().get(
                Activator.NODE_OPER_FAULT_OBJECT);
    }

}
