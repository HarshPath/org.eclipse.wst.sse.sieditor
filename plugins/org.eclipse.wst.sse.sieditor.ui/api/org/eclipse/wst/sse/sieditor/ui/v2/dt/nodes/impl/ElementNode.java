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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IElementNode;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;

public class ElementNode extends AbstractXsdTreeNode implements IElementNode {

    public ElementNode(final IModelObject element, final ITreeNode parent, final TreeNodeMapper nodeMapper) {
        super(element, parent, nodeMapper);
    }

    @Override
    public IElement getModelObject() {
        return (IElement) super.getModelObject();
    }

    @Override
    public Image getImage() {
        final ImageRegistry imageRegistry = getImageRegistry();
        Image image;
        if (isReadOnly()) {
            if (EmfXsdUtils.isReference(getModelObject())) {
                image = getModelObject().isAttribute() ? imageRegistry.get(Activator.NODE_ATTRIBUTE_GRAY_REF) : imageRegistry
                        .get(Activator.NODE_ELEMENT_GRAY_REF);
            } else {
                image = getModelObject().isAttribute() ? imageRegistry.get(Activator.NODE_ATTRIBUTE_GRAY) : imageRegistry
                        .get(Activator.NODE_ELEMENT_GRAY);
            }
        } else {
            image = getModelObject().isAttribute() ? imageRegistry.get(Activator.NODE_ATTRIBUTE) : imageRegistry
                    .get(Activator.NODE_ELEMENT);
        }
        return image;
    }

    @Override
    public Object[] getChildren() {
        final IType type = getModelObject().getType();
        if (type instanceof IStructureType && type.isAnonymous() && !isRecursive()) {
            final ArrayList<ITreeNode> elementNodes = new ArrayList<ITreeNode>();
            for (final IElement element : ((IStructureType) type).getAllElements()) {
                // do skip attribute references
                if (element.isAttribute() && EmfXsdUtils.isReference(element)) {
                    continue;
                }
                final List<ITreeNode> treeNodes = getNodeMapper().getTreeNode(element, getCategories(), this);
                ITreeNode elementNode = treeNodes.isEmpty() ? null : treeNodes.get(0);
                if (elementNode == null) {
                    elementNode = createElement(element);
                    getNodeMapper().addToNodeMap(element, elementNode);
                }
                elementNodes.add(elementNode);
            }
            return elementNodes.toArray();
        }
        return new Object[0];
    }

    private boolean isRecursive() {
        final int categories = getCategories();
        final boolean isCategoryReference = (categories & ITreeNode.CATEGORY_REFERENCE) != 0;
        if (isCategoryReference) {
            // search parent nodes for the referred type
            return existAsParent(getModelObject().getType());
        }
        return false;
    }

    private boolean existAsParent(final IModelObject refModelObject) {
        ITreeNode currentNode = this;
        ITreeNode parentNode = this.getParent();
        IModelObject parentModelObject;
        int parentCategories;
        boolean isParentCategoryReference;

        while (parentNode != null) {
            parentCategories = parentNode.getCategories();
            isParentCategoryReference = (parentCategories & ITreeNode.CATEGORY_REFERENCE) != 0
                    && parentNode.getModelObject() instanceof IElement;
            parentModelObject = isParentCategoryReference ? ((IElement) parentNode.getModelObject()).getType() : parentNode
                    .getModelObject();

            if (refModelObject.equals(parentModelObject)) {
                return true;
            }
            currentNode = parentNode;
            parentNode = currentNode.getParent();
        }
        return false;
    }

    protected IElementNode createElement(final IElement element) {
        return new ElementNode(element, this, getNodeMapper());
    }

}
