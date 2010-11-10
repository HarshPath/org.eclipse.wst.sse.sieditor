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
package org.eclipse.wst.sse.sieditor.ui.v2.nodes.impl;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.INamedObject;
import org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.ImportedTypesNode;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ImportedServicesNode;

public abstract class AbstractTreeNode implements ITreeNode {
    
    private ITreeNode parent;
    
    private IModelObject modelObject;
    
    private TreeNodeMapper nodeMapper;
    
    /**
     * Do not use this field directly, but do use it via {@link #getCategories()}
     */
    private int categories = 0;

    /**
     * This constructor will assume node category as CATEGORY_MAIN, or CATEGORY_IMPORTED (in case that parent.isImportedNode()).
     * It is required to create a tree node with explicitly set category
     * @deprecated do use {@link #AbstractTreeNode(IModelObject, ITreeNode, TreeNodeMapper, int)}
     */
    public AbstractTreeNode(IModelObject modelObject, ITreeNode parent, TreeNodeMapper nodeMapper) {
        this.modelObject = modelObject;
        this.parent = parent;
        this.nodeMapper = nodeMapper;
        
        if(parent == null) {
            categories = isImportedNode() ? CATEGORY_IMPORTED : CATEGORY_MAIN;
        }
        else {
            categories = parent.getCategories();
        }
        
        if(EmfXsdUtils.isReference(modelObject)) {
            categories |= CATEGORY_REFERENCE;
        }
    }
    
    /**
     * Create a tree node with specified categories. 
     * This node will have these categories and the categories from the parent node.
     * @see ITreeNode
     */
    public AbstractTreeNode(IModelObject modelObject, ITreeNode parent, TreeNodeMapper nodeMapper, int categories) {
        this(modelObject, parent, nodeMapper);
        this.categories |= categories;
    }

	/**
	 * Update categories with the actual state of the model object, and do
	 * return the actual categories state.
	 */
    public int getCategories() {
    	updateReferenceCategory();
        return categories; 
    }
    
    public String getDisplayName() {
        String name = null;
        if(modelObject instanceof INamedObject) {
            name = ((INamedObject) modelObject).getName();
        }
        return name == null ? UIConstants.EMPTY_STRING : name;
    }

    public abstract Image getImage();
    
    public abstract Object[] getChildren();

    public IModelObject getModelObject() {
        return modelObject;
    }

    public ITreeNode getParent() {
        return parent;
    }

    public boolean hasChildren() {
        Object[] children = getChildren();
        return children != null && children.length > 0;
    }
    
    public boolean isReadOnly() {
    	if(modelObject == null) {
    		return true;
    	}
    	int updatedCategories = getCategories();

        if((updatedCategories & CATEGORY_IMPORTED) != 0 ||
           (updatedCategories & CATEGORY_REFERENCE) != 0) {
            return true;
        }
        boolean isPartOfTheSameDoc = EmfWsdlUtils.isModelObjectPartOfModelRoot(modelObject.getModelRoot(), modelObject);
        return !isPartOfTheSameDoc;
    }

    protected ImageRegistry getImageRegistry() {
        return Activator.getDefault().getImageRegistry();
    }

    protected TreeNodeMapper getNodeMapper() {
        return nodeMapper;
    }
    
    public boolean isImportedNode() {
        return isImportedNode(this);
    }
    
    private void updateReferenceCategory() {
    	if((parent != null && (parent.getCategories() & CATEGORY_REFERENCE) != 0) ||
    			EmfXsdUtils.isReference(modelObject)) {
            categories |= CATEGORY_REFERENCE;
        }
    	else {
    		categories &= ~CATEGORY_REFERENCE;
    	}
    }
    
    private boolean isImportedNode(ITreeNode node) {
        ITreeNode parent = node.getParent();
        
        if(node instanceof ImportedServicesNode ||
                node instanceof ImportedTypesNode) {
            return true;
        }
        else if(parent != null) {
            return isImportedNode(parent);
        }
        return false;
    }

    @Override
    public String toString() {
        String info = "Node categories: "; //$NON-NLS-1$
        if((categories & CATEGORY_MAIN) > 0) {
            info += "CATEGORY_MAIN; "; //$NON-NLS-1$
        }
        if((categories & CATEGORY_IMPORTED) > 0) {
            info += "CATEGORY_IMPORTED; "; //$NON-NLS-1$
        }
        if((categories & CATEGORY_INPUT) > 0) {
            info += "CATEGORY_INPUT; "; //$NON-NLS-1$
        }
        if((categories & CATEGORY_OUTPUT) > 0) {
            info += "CATEGORY_OUTPUT; "; //$NON-NLS-1$
        }
        if((categories & CATEGORY_FAULT) > 0) {
            info += "CATEGORY_FAULT; "; //$NON-NLS-1$
        }
        if((categories & CATEGORY_REFERENCE) > 0) {
            info += "CATEGORY_REFERENCE; "; //$NON-NLS-1$
        }
        if((categories & CATEGORY_STATIC_ROOT) > 0) {
            info += "CATEGORY_STATIC_ROOT; "; //$NON-NLS-1$
        }
        
        info += "isReadOnly=" + isReadOnly() + "; "; //$NON-NLS-1$ //$NON-NLS-2$
        return info + super.toString();
    }

}
