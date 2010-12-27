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
package org.eclipse.wst.sse.sieditor.ui.v2.sections.elements;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.NamespaceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController.CardinalityType;

public class StructureTypeStrategy implements IElementStrategy {

    private IStructureType structureType;
    private final IDataTypesFormPageController formPageController;

    public StructureTypeStrategy(final IDataTypesFormPageController formPageController) {
        this.formPageController = formPageController;
    }

    public void setInput(final ITreeNode treeNode) {
        final IModelObject input = treeNode == null ? null : treeNode.getModelObject();
        structureType = (IStructureType) input;
    }

    public CardinalityType getCardinality() {
        return null;
    }

    public String getName() {
        return structureType.getName();
    }

    public String getNamespace() {
        return NamespaceNode.getNamespaceDisplayText(structureType.getNamespace());
    }

    public boolean getNillable() {
        return structureType.isNillable();
    }

    public IType getType() {
        return structureType.getBaseType();
    }

    public boolean isCardinalityApplicable() {
        return false;
    }

    public boolean isCardinalityEditable() {
        return false;
    }

    public boolean isConstraintsSectionApplicable() {
        final IType elementType = structureType.getType();
        return elementType instanceof ISimpleType;
    }

    public boolean isNameApplicable() {
        return true;
    }

    public boolean isNameEditable() {
        return true;
    }

    public boolean isNamespaceApplicable() {
        return true;
    }

    public boolean isNamespaceEditable() {
        return false;
    }

    public boolean isNillableApplicable() {
        return false;
    }

    public boolean isNillableEditable() {
        return false;
    }

    public void setCardinality(final CardinalityType cardinality) {
        throw new IllegalStateException();

    }

    public void setName(final String name) {
        if (!name.equals(getName())) {
            formPageController.renameType(structureType, name);
        }
    }

    public void setNamespace(final String namespace) {
        throw new IllegalStateException();
    }

    public void setNillable(final boolean nillable) {
    }

    public void setType(final IType type) {
    }

    public boolean isTypeApplicable() {
        return true;
    }

    public boolean isTypeEditable() {
        return true;
    }

    public IConstraintsController getConstraintsSectionController() {
        return null;
    }

    public IType getBaseType() {
        return getType();
    }

    public boolean isBaseTypeApplicable() {
        return false;
    }

    public boolean isBaseTypeEditable() {
        return isBaseTypeApplicable();
    }

    public void setBaseType(final IType baseType) {
        formPageController.setStructureTypeContent(structureType, baseType);
    }

}
