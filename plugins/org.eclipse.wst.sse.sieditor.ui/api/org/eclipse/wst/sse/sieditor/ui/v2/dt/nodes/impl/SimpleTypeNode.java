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
package org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl;

import org.eclipse.swt.graphics.Image;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.ISimpleTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;

public class SimpleTypeNode extends AbstractXsdTreeNode implements ISimpleTypeNode {

    public SimpleTypeNode(final IModelObject simpleType, final ITreeNode parent) {
        super(simpleType, parent, null);
    }

    @Override
    public Image getImage() {
        return isReadOnly() ? getImageRegistry().get(Activator.NODE_SIMPLE_TYPE_GRAY) : getImageRegistry().get(
                Activator.NODE_SIMPLE_TYPE);
    }

    @Override
    public Object[] getChildren() {
        return null;
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

}
