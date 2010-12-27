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

import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.common.AbstractEditorLabelProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.utils.UIUtils;

public class StructureTypeNodeLabelsProvider extends AbstractDTTreeNodeLabelsProvider {

    private final IStructureType iStructureType;

    public StructureTypeNodeLabelsProvider(final IStructureType structureType) {
        this.iStructureType = structureType;
    }

    @Override
    public String getDisplayName() {
        return UIUtils.instance().getDisplayName(iStructureType);
    }

    @Override
    public String getTreeDisplayText() {
        return !iStructureType.isElement() ? getDisplayName() : getDisplayName() + UIConstants.SPACE + UIConstants.COLON
                + UIConstants.SPACE + getTypeDisplayText();
    }

    @Override
    public String getTypeDisplayText() {
        String typeDisplayText = null;
        if (iStructureType.isElement()) {
            typeDisplayText = getDisplayTextForTypeOfGlobalElement(iStructureType);
        } else {
            typeDisplayText = getDisplayTextForTypeOfStructureType(iStructureType);
        }

        if (typeDisplayText == null) {
            return UnresolvedType.instance().getName();
        }
        return typeDisplayText;
    }

    // =========================================================
    // type display texts
    // =========================================================

    private String getDisplayTextForTypeOfStructureType(final IStructureType structureType) {
        final XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        return complexType.getBaseType() == null ? null : complexType.getBaseType().getName();
    }

    private String getDisplayTextForTypeOfGlobalElement(final IStructureType structureType) {
        final XSDElementDeclaration elementDeclaration = (XSDElementDeclaration) structureType.getComponent();

        final XSDTypeDefinition typeDefinition = elementDeclaration.getTypeDefinition();
        if (typeDefinition == null/* || typeDefinition.eContainer() == null*/) {
            return null;
        }

        if (EmfXsdUtils.isAnonymous(elementDeclaration)) {
            return AbstractEditorLabelProvider.ANONYMOUS_LABEL;

        }
        final XSDTypeDefinition baseType = typeDefinition.getBaseType();

        if (typeDefinition instanceof XSDSimpleTypeDefinition) {
            return getSimpleTypeDisplayText((XSDSimpleTypeDefinition) typeDefinition, (XSDSimpleTypeDefinition) baseType);
        }

        if (typeDefinition instanceof XSDComplexTypeDefinition
                && !AbstractEditorLabelProvider.ANY_TYPE.equals(baseType.getName())) {
            return baseType.getName();
        }
        return typeDefinition.getName();
    }

    @Override
    protected String getTypeName(final XSDTypeDefinition typeDefinition, final XSDTypeDefinition baseType) {
        return typeDefinition.getName() != null ? typeDefinition.getName() : baseType.getName();
    }
}
