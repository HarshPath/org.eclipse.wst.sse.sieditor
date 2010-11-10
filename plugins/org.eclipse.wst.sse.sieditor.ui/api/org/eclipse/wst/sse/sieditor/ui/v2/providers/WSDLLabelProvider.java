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
package org.eclipse.wst.sse.sieditor.ui.v2.providers;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.wsdl.Part;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.common.AbstractEditorLabelProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesLabelProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.FaultNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ParameterNode;

public class WSDLLabelProvider extends AbstractEditorLabelProvider {

    public static final String NAME_TYPE_DELIMITER = " : "; //$NON-NLS-1$

    private static final DataTypesLabelProvider dtLP = new DataTypesLabelProvider();

    public WSDLLabelProvider() {
    }

    public String getText(Object element) {
        if (!(element instanceof ITreeNode)) {
            return UIConstants.EMPTY_STRING;
        }

        ITreeNode treeNode = (ITreeNode) element;
        StringBuffer result = new StringBuffer(treeNode.getDisplayName());

        if ((element instanceof ParameterNode) || (element instanceof FaultNode)) {
            result.append(NAME_TYPE_DELIMITER);
            IType type = getTypeFromUINode(treeNode);
            String typeDisplayName = getTypeDisplayText(type, treeNode);

            result.append(decorateText(typeDisplayName, treeNode));
        }
        return result.toString();
    }

    protected IType getTypeFromUINode(ITreeNode element) {
        if (element instanceof ParameterNode) {
            return ((IParameter) ((ParameterNode) element).getModelObject()).getType();
        }

        if (element instanceof FaultNode) {
            Iterator<IParameter> iterator = ((IFault) ((FaultNode) element).getModelObject()).getParameters().iterator();
            IParameter parameter = iterator.hasNext() ? iterator.next() : null;
            if (parameter != null) {
                return parameter.getType();
            }
        }

        return null;
    }

    /**
     * This method is used to make any changes to how a type is visible.
     * 
     * @param text
     * @param element
     * @return
     */
    private String decorateText(String text, Object element) {
        String ret = text;
        if (element instanceof ParameterNode) {
            if (text.indexOf(".element") != -1) //$NON-NLS-1$
                ret = text.substring(0, text.lastIndexOf(".element")) + " [Element]"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        return ret;
    }

    public Color getForeground(Object element) {
        return Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
    }

    public static String getTypeDisplayText(ITreeNode treeNode) {
        return getTreeNodeTypeDisplayName(treeNode);
    }

    private static String getTreeNodeTypeDisplayName(ITreeNode treeNode) {
        if (treeNode instanceof ParameterNode) {
            IModelObject modelObject = ((ParameterNode) treeNode).getModelObject();
            Part part = (Part) modelObject.getComponent();

            if (part != null && part.getElementName() != null) {
                return part.getElementName().getLocalPart();
            }

            if (part != null && part.getTypeName() != null) {
                return part.getTypeName().getLocalPart();
            }
        }

        return null;
    }
}