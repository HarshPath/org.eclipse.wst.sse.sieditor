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
package org.eclipse.wst.sse.sieditor.ui.v2.common;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.xsd.XSDElementDeclaration;

import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusProvider;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategoryNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ParameterNode;

public abstract class AbstractEditorLabelProvider extends ColumnLabelProvider {
    private static final int TOOLTIP_TIME_DISPLAYED = 5000;
    private static final int TOOLTIP_DISPLAY_TIME = 500;

    private static final String ANONYMOUS_LABEL = Messages.DataTypesLabelProvider_anonymous_type_label;

    public AbstractEditorLabelProvider() {
    }

    @Override
    public String getToolTipText(Object element) {
        if (element instanceof ITreeNode) {
            ITreeNode treeNode = (ITreeNode) element;
            IModelObject modelObject = treeNode.getModelObject();

            // Messages for Definition objects must not be shown in the UI tree,
            // because there are global for the WSDL
            if (modelObject instanceof Description) {
                return null;
            }

            IValidationStatusProvider validationStatusProvider = getValidationStatusProvider(modelObject);
            if (validationStatusProvider != null) {
                List<IValidationStatus> statusList = validationStatusProvider.getStatus(modelObject);
                if (!statusList.isEmpty()) {
                    StringBuilder buf = new StringBuilder();
                    for (IValidationStatus status : statusList) {
                        buf.append(status.getMessage()).append('\n');
                    }
                    if (buf.length() > 0) {
                        buf.deleteCharAt(buf.length() - 1);
                    }
                    return buf.toString();
                }
            }

            // in case the element is in category - delegate the tooltip to the
            // category node. It should decide whether to show a tooltip or not.
            // Tooltip should be shown in case the categories are hidden and it
            // should say for example "input parameter Bla Bla"...
            ITreeNode parent = treeNode.getParent();
            if (parent instanceof OperationCategoryNode) {
                return ((OperationCategoryNode) parent).getTooltipTextFor(treeNode);
            }
        }
        return null;
    }

    public static String getTypeDisplayText(IType type, ITreeNode treeNode) {
        if (type == null) {
            String typeDisplayText = getTypeDisplayText(treeNode);
            if (typeDisplayText != null) {
                return typeDisplayText;
            }
            return Messages.AbstractEditorLabelProvider_0;
        } else if (type.isAnonymous()) {
            if (type instanceof IStructureType && type.getComponent() instanceof XSDElementDeclaration) {
                return type.getName();
            } else if (type instanceof IStructureType) {
                return ANONYMOUS_LABEL;
            }
            IType baseType = type.getBaseType();
            if ((baseType != null) && (baseType.getName() != null)) {
                return new StringBuilder(baseType.getName()).append(UIConstants.SPACE).append(UIConstants.OPEN_BRACKET).append(
                        ANONYMOUS_LABEL).append(UIConstants.CLOSE_BRACKET).toString();
            }
            return ANONYMOUS_LABEL;
        } else {
            return type.getName();
        }
    }

    @Override
    public int getToolTipDisplayDelayTime(Object object) {
        return TOOLTIP_DISPLAY_TIME;
    }

    @Override
    public Point getToolTipShift(Object object) {
        return new Point(5, 5);
    }

    @Override
    public int getToolTipTimeDisplayed(Object object) {
        return TOOLTIP_TIME_DISPLAYED;
    }

    @Override
    public Image getImage(Object element) {
        return element instanceof ITreeNode ? decorateImage(((ITreeNode) element).getImage(), element) : null;
    }

    protected Image decorateImage(Image image, Object element) {

        final ITreeNode treeNode = (ITreeNode) element;

        IModelObject modelObject = treeNode.getModelObject();
        IValidationStatusProvider provider = getValidationStatusProvider(modelObject);

        // Warnings/Errors icons for Definition objects must not be shown in the
        // UI tree, because there are global for the WSDL
        if (modelObject instanceof Description) {
            return image;
        }

        if (provider == null) {
            if (Logger.isDebugEnabled()) {
                Logger.logError("SIE label provider could not acquire validation service"); //$NON-NLS-1$
            }
            return image;
        }
        Integer statusMarker = provider.getStatusMarker(modelObject);
        if (statusMarker != IStatus.OK) {
            return Activator.getDefault().getImage(image, statusMarker);
        }

        return image;

    }

    protected IValidationStatusProvider getValidationStatusProvider(Object modelObject) {
        if (modelObject instanceof IModelObject) {
            return (IValidationStatusProvider) Platform.getAdapterManager().getAdapter(modelObject,
                    IValidationStatusProvider.class);
        }

        return null;
    }

    private static String getTypeDisplayText(ITreeNode treeNode) {
        return getTreeNodeTypeDisplayName(treeNode);
    }

    private static String getTreeNodeTypeDisplayName(ITreeNode treeNode) {
        if (treeNode instanceof ParameterNode) {
            IModelObject modelObject = ((ParameterNode) treeNode).getModelObject();
            if (modelObject == null) {
                return null;
            }
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
