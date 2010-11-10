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
package org.eclipse.wst.sse.sieditor.ui.v2.sections.elements;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.SimpleTypeConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController.CardinalityType;

public class SimpleTypeStrategy implements IElementStrategy {

    private ISimpleType simpleType;
    private final IDataTypesFormPageController formPageController;

    private IConstraintsController constraintsController;

    public SimpleTypeStrategy(IDataTypesFormPageController formPageController) {
        this.formPageController = formPageController;
    }

    public CardinalityType getCardinality() {
        return null;
    }

    public IConstraintsController getConstraintsSectionController() {
        if (constraintsController == null) {
            constraintsController = new SimpleTypeConstraintsController(formPageController);
            constraintsController.setType(simpleType);
        }
        if (simpleType.getComponent().eResource() == null) {
            // we must have deleted the simpleType the constraint controller has
            // reference to. set it explicitly to null
            constraintsController.setType(null);
        }
        return constraintsController;
    }

    public String getName() {
        return simpleType.getName();
    }

    public String getNamespace() {
        return simpleType.getNamespace();
    }

    public boolean getNillable() {
        return false;
    }

    public IType getType() {
        return simpleType;
    }

    public boolean isCardinalityApplicable() {
        return false;
    }

    public boolean isCardinalityEditable() {
        return false;
    }

    public boolean isConstraintsSectionApplicable() {
        return true;
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

    public void setCardinality(CardinalityType cardinality) {
    }

    public void setInput(ITreeNode treeNode) {
        IModelObject input = treeNode == null ? null : treeNode.getModelObject();
        simpleType = (ISimpleType) input;
        constraintsController = null;
    }

    public void setName(String name) {
        if (name != null && !name.equals(getName())) {
            formPageController.renameType(simpleType, name);
        }

    }

    public void setNamespace(String namespace) {
        if (namespace != null && !namespace.equals(getNamespace())) {
            // TODOformPageController.
        }

    }

    public void setNillable(boolean nillable) {
    }

    public void setType(IType type) {
        // TODO Auto-generated method stub

    }

    public IType getBaseType() {
        return simpleType.getBaseType();
    }

    public boolean isBaseTypeApplicable() {
        return true;
    }

    public boolean isBaseTypeEditable() {
        return true;
    }

    public void setBaseType(IType baseType) {
        formPageController.setSimpleTypeBaseType(simpleType, (ISimpleType) baseType);
    }

    public boolean isTypeApplicable() {
        return false;
    }

    public boolean isTypeEditable() {
        return false;
    }

}
