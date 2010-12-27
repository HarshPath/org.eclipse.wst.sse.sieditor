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
package org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.labels.provider;

import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.common.AbstractEditorLabelProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.utils.UIUtils;

public class ElementNodeLabelsProvider extends AbstractDTTreeNodeLabelsProvider {

    private final IElement element;

    public ElementNodeLabelsProvider(final IElement element) {
        this.element = element;
    }

    @Override
    public String getDisplayName() {
        return UIUtils.instance().getDisplayName(element);
    }

    @Override
    public String getTreeDisplayText() {
        return getDisplayName() + UIConstants.SPACE + UIConstants.COLON + UIConstants.SPACE + getTypeDisplayText();
    }

    @Override
    public String getTypeDisplayText() {
        final XSDTypeDefinition typeDefinition = getTypeDefinitionForDisplay();
        if (typeDefinition == null/* || typeDefinition.eContainer() == null*/) {
            return Messages.AbstractEditorLabelProvider_0;
        }

        final XSDTypeDefinition baseType = typeDefinition.getBaseType();
        if (typeDefinition instanceof XSDSimpleTypeDefinition && baseType instanceof XSDSimpleTypeDefinition) {
            return getSimpleTypeDisplayText((XSDSimpleTypeDefinition) typeDefinition, (XSDSimpleTypeDefinition) baseType);
        }
        return getTypeName(typeDefinition, baseType);
    }

    private XSDTypeDefinition getTypeDefinitionForDisplay() {

        if (element.getComponent() instanceof XSDAttributeDeclaration) {
            final XSDAttributeDeclaration attributeDeclaration = (XSDAttributeDeclaration) element.getComponent();
            return attributeDeclaration.getTypeDefinition();
        }

        final XSDElementDeclaration xsdElementDeclaration = getXsdElementDeclaration();
        if (xsdElementDeclaration != null) {
            if (xsdElementDeclaration.isElementDeclarationReference()) {
                return xsdElementDeclaration.getResolvedElementDeclaration().getTypeDefinition();
            }
            return xsdElementDeclaration.getTypeDefinition();
        }
        return null;
    }

    @Override
    protected String getTypeName(final XSDTypeDefinition typeDefinition, final XSDTypeDefinition baseType) {
        if (getXsdElementDeclaration() != null && getXsdElementDeclaration().isElementDeclarationReference()
                && EmfXsdUtils.isAnonymous(getXsdElementDeclaration().getResolvedElementDeclaration())) {
            return AbstractEditorLabelProvider.ANONYMOUS_LABEL;
        }
        return typeDefinition.getName() != null ? typeDefinition.getName() : baseType.getName();
    }

    // =========================================================
    // helpers
    // =========================================================

    private XSDElementDeclaration getXsdElementDeclaration() {
        if (element.getComponent() instanceof XSDParticle) {
            final XSDParticle particle = (XSDParticle) element.getComponent();
            if (particle.getContent() instanceof XSDElementDeclaration) {
                return (XSDElementDeclaration) particle.getContent();
            }
        }
        return null;
    }
}
