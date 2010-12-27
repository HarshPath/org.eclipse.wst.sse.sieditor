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
package org.eclipse.wst.sse.sieditor.ui.v2.common;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.internal.impl.DefinitionImpl;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDTypeDefinition;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusProvider;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategoryNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ParameterNode;

public abstract class AbstractEditorLabelProvider extends ColumnLabelProvider {

    private static final int TOOLTIP_TIME_DISPLAYED = 5000;
    private static final int TOOLTIP_DISPLAY_TIME = 500;

    public static final String ANONYMOUS_LABEL = Messages.DataTypesLabelProvider_anonymous_type_label;
    public static final String ANY_TYPE = "anyType"; //$NON-NLS-1$

    public AbstractEditorLabelProvider() {
    }

    @Override
    public String getToolTipText(final Object element) {
        if (element instanceof ITreeNode) {
            final ITreeNode treeNode = (ITreeNode) element;
            final IModelObject modelObject = treeNode.getModelObject();

            // Messages for Definition objects must not be shown in the UI tree,
            // because there are global for the WSDL
            if (modelObject instanceof Description) {
                return null;
            }

            final IValidationStatusProvider validationStatusProvider = getValidationStatusProvider(modelObject);
            if (validationStatusProvider != null) {
                final List<IValidationStatus> statusList = validationStatusProvider.getStatus(modelObject);
                if (!statusList.isEmpty()) {
                    final StringBuilder buf = new StringBuilder();
                    for (final IValidationStatus status : statusList) {
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
            final ITreeNode parent = treeNode.getParent();
            if (parent instanceof OperationCategoryNode) {
                return ((OperationCategoryNode) parent).getTooltipTextFor(treeNode);
            }
        }
        return null;
    }
    
    @Override
    public int getToolTipDisplayDelayTime(final Object object) {
        return TOOLTIP_DISPLAY_TIME;
    }

    @Override
    public Point getToolTipShift(final Object object) {
        return new Point(5, 5);
    }

    @Override
    public int getToolTipTimeDisplayed(final Object object) {
        return TOOLTIP_TIME_DISPLAYED;
    }

    @Override
    public Image getImage(final Object element) {
        return element instanceof ITreeNode ? decorateImage(((ITreeNode) element).getImage(), element) : null;
    }

    protected Image decorateImage(final Image image, final Object element) {

        final ITreeNode treeNode = (ITreeNode) element;

        final IModelObject modelObject = treeNode.getModelObject();
        final IValidationStatusProvider provider = getValidationStatusProvider(modelObject);

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
        final Integer statusMarker = provider.getStatusMarker(modelObject);
        if (statusMarker != IStatus.OK) {
            return Activator.getDefault().getImage(image, statusMarker);
        }

        return image;

    }

    protected IValidationStatusProvider getValidationStatusProvider(final Object modelObject) {
        if (modelObject instanceof IModelObject) {
            return (IValidationStatusProvider) Platform.getAdapterManager().getAdapter(modelObject,
                    IValidationStatusProvider.class);
        }

        return null;
    }

    private static String getTypeDisplayText(final ITreeNode treeNode) {
        if (treeNode instanceof ParameterNode) {
            return getParameterNodeName(((ParameterNode) treeNode));
        }
        return null;
    }

    private static String getParameterNodeName(final ParameterNode parameterNode) {
        final IModelObject modelObject = parameterNode.getModelObject();
        if (modelObject == null) {
            return null;
        }
        final Part part = (Part) modelObject.getComponent();
        if (part == null) {
            return null;
        }
        final QName elementName = part.getElementName();
        final QName typeName = part.getTypeName();
        final Map extAtts = part.getExtensionAttributes();

        if (elementName == null && typeName == null && (extAtts == null || extAtts.isEmpty())) {
            return null;
        }

        if (elementName != null && typeName != null) {
            return elementName != null ? ((part.getElementDeclaration() != null ? elementName.getLocalPart() : null)) : (part
                    .getTypeDefinition() != null ? typeName.getLocalPart() : null);
        }

        return returnNameAccordingToPartTypeName(part, elementName, typeName);
    }

    private static String returnNameAccordingToPartTypeName(final Part part, final QName elementName, final QName typeName) {
        final XSDElementDeclaration elementDeclaration = part.getElementDeclaration();
        final XSDTypeDefinition typeDefinition = part.getTypeDefinition();

        if (elementDeclaration != null) {
            return returnPartName(part, elementName, elementDeclaration, elementDeclaration.getType(), elementDeclaration
                    .getElement());

        } else if (typeDefinition != null) {
            return returnPartName(part, typeName, typeDefinition, typeDefinition.getBaseType(), typeDefinition.getElement());

        }
        return elementName != null ? ((elementDeclaration != null ? elementName.getLocalPart() : null))
                : (typeDefinition != null ? typeName.getLocalPart() : null);
    }

    private static String returnPartName(final Part part, final QName elementName, final XSDNamedComponent concreteComponent,
            final XSDConcreteComponent resolveComponent, final Element domElement) {
        if (elementName != null) {
            if (EmfXsdUtils.isSchemaForSchemaNS(elementName.getNamespaceURI())) {
                return concreteComponent != null ? part.getElementName().getLocalPart() : null;
            }
            if (concreteComponent == null || domElement == null) {
                return null;
            }
            if (resolveComponent != null && !EmfWsdlUtils.couldBeVisibleType(concreteComponent)) {
                final Definition definition = part.getEnclosingDefinition();
                if (!((DefinitionImpl) definition).resolveSchema(concreteComponent.getTargetNamespace()).isEmpty()
                        || !definition.getNamespaces().containsValue(concreteComponent.getTargetNamespace())) {
                    return null;
                }
            }
        }

        return elementName != null ? ((concreteComponent != null ? elementName.getLocalPart() : null)) : null;
    }
}
