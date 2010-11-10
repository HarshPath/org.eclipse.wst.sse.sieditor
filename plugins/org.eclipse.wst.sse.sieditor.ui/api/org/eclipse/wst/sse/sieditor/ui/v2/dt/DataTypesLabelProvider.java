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
package org.eclipse.wst.sse.sieditor.ui.v2.dt;

import org.eclipse.jface.viewers.ILabelProviderListener;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.common.AbstractEditorLabelProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IElementNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IStructureTypeNode;

public class DataTypesLabelProvider extends AbstractEditorLabelProvider {

    public String getText(Object element) {
        if (element instanceof IDataTypesTreeNode) {
            IDataTypesTreeNode node = (IDataTypesTreeNode) element;
            String displayText = node.getDisplayName();
            String typeText = null;
            if (node instanceof IElementNode) {
                IElement elementModelObject = (IElement) node.getModelObject();
                IType type = elementModelObject.getType();
                typeText = getTypeDisplayText(type, node);
            } else if (element instanceof IStructureTypeNode) {
                IStructureType structureType = (IStructureType) node.getModelObject();
                if (structureType.isElement()) {
                    IType type = structureType.getType();
                    typeText = getTypeDisplayText(type, node);
                }
            }
            if (displayText == null) {
                displayText = UIConstants.EMPTY_STRING;
            }
            return typeText == null ? displayText : new StringBuilder(displayText).append(UIConstants.SPACE).append(
                    UIConstants.COLON).append(UIConstants.SPACE).append(typeText).toString();
        }
        return null;
    }

    public void addListener(ILabelProviderListener listener) {
    }

    public void dispose() {
    }

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {
    }

}
