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
package org.eclipse.wst.sse.sieditor.ui.v2.nodes;

import org.eclipse.swt.graphics.Image;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;

public interface ITreeNode {

    public final static int CATEGORY_MAIN = 1;

    public final static int CATEGORY_IMPORTED = 2;

    public final static int CATEGORY_INPUT = 4;

    public final static int CATEGORY_OUTPUT = 8;

    public final static int CATEGORY_FAULT = 16;

    public final static int CATEGORY_REFERENCE = 32;
    
    public final static int CATEGORY_ALL = ~0;

    /**
     * Category representing root tree nodes, for example - the Imported Types
     * Node
     */
    public final static int CATEGORY_STATIC_ROOT = 64;

    /**
     * Contains categories of the tree node. Can be more than one category
     * (using bitwise or |).
     */
    public int getCategories();

    /**
     * 
     * @return - Returns the name to be displayed in the Tree
     */
    public String getDisplayName();

    /**
     * 
     * @return - Returns the Image which is appended to the node in the Tree
     */
    public Image getImage();

    /**
     * 
     * @return - Returns the parent node
     */
    public ITreeNode getParent();

    /**
     * 
     * @return - Returns an array of all its Child nodes
     */
    public Object[] getChildren();

    /**
     * 
     * @return - Returns true if it has children.
     */
    public boolean hasChildren();

    /**
     * 
     * @return - Returns the model object which each node represents
     */
    public IModelObject getModelObject();

    public boolean isReadOnly();

    public boolean isImportedNode();
}
