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
package org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.INamedObject;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IElementNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IStructureTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.impl.AbstractTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.utils.UIUtils;

public class StructureTypeNode extends AbstractTreeNode implements IStructureTypeNode {

    public StructureTypeNode(final IModelObject type, final ITreeNode parent, final TreeNodeMapper nodeMapper) {
        super(type, parent, nodeMapper);
    }

    @Override
    public String getDisplayName() {
		return UIUtils.instance().getDisplayName((INamedObject)getModelObject());
    }

    @Override
    public Image getImage() {
        final ImageRegistry imageRegistry = getImageRegistry();
        Image image;
        if (isReadOnly()) {
            image = ((IStructureType) getModelObject()).isElement() ? imageRegistry.get(Activator.NODE_ELEMENT_GRAY)
                    : imageRegistry.get(Activator.NODE_STRUCTURE_TYPE_GRAY);
        } else {
            image = ((IStructureType) getModelObject()).isElement() ? imageRegistry.get(Activator.NODE_ELEMENT) : imageRegistry
                    .get(Activator.NODE_STRUCTURE_TYPE);
        }
        return image;
    }

    @Override
    public Object[] getChildren() {
        final Collection<IElement> allElements = ((IStructureType) getModelObject()).getAllElements();
        if (!allElements.isEmpty()) {
            ArrayList<IElement> elements = new ArrayList<IElement>(allElements);
            ArrayList<ITreeNode> elementNodes = new ArrayList<ITreeNode>(elements.size());
            for (IElement element : elements) {
            	// do skip attribute references
            	if(element.isAttribute() && EmfXsdUtils.isReference(element)) {
            		continue;
            	}
                List<ITreeNode> treeNodes = getNodeMapper().getTreeNode(element, getCategories(), this);
                ITreeNode elementNode = treeNodes.isEmpty() ? null : treeNodes.get(0);
                if (elementNode == null) {
                    elementNode = createElementNode(element);
                    getNodeMapper().addToNodeMap(element, elementNode);
                }
                elementNodes.add(elementNode);
            }
            return elementNodes.toArray();
        }
        return UIConstants.EMPTY_ARRAY;
    }

    protected IElementNode createElementNode(final IElement element) {
        return new ElementNode(element, this, getNodeMapper());
    }
}
