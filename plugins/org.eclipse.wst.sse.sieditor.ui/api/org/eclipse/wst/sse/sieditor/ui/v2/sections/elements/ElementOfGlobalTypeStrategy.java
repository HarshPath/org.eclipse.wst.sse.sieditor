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
 *    Tsvetan Stoyanov - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.sections.elements;

import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ElementConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.SimpleTypeConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.NamespaceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.sections.elements.ElementNodeDetailsController.CardinalityType;

/**
 * &lt;element name="something" type="global_type"/&gt;
 * 
 * 
 * 
 */
public class ElementOfGlobalTypeStrategy implements IElementStrategy {

    protected IElement input;

    protected IDataTypesFormPageController formPageController;

    protected IConstraintsController constraintsController;

    public ElementOfGlobalTypeStrategy(IDataTypesFormPageController formPageController) {
        this.formPageController = formPageController;
    }

    public CardinalityType getCardinality() {
        return CardinalityType.get(input.getMinOccurs(), input.getMaxOccurs());
    }

    public String getName() {
        return input.getName();
    }

    public String getNamespace() {
        return NamespaceNode.getNamespaceDisplayText(input.getNamespace());
    }

    public boolean getNillable() {
        return input.getNillable();
    }

    public boolean isCardinalityApplicable() {
        return true;
    }

    public boolean isCardinalityEditable() {
        return true;
    }

    public boolean isConstraintsSectionApplicable() {
        IType type = getType();
        if (type == null) {
            return false;
        }
        // Cannot add facets to anySimpleType
        XSDNamedComponent component = type.getComponent();
        if (component instanceof XSDTypeDefinition && XSDConstants.isAnySimpleType((XSDTypeDefinition) component)) {
            return false;
        }
        return type instanceof ISimpleType
                || (((IStructureType) type).isElement() && ((IStructureType) type).getType() instanceof ISimpleType);
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
        return true;
    }

    public boolean isNillableEditable() {
        return true;
    }

    public void setCardinality(CardinalityType cardinality) {
        if (cardinality != null) {
            formPageController.setCardinality(null, input, cardinality.min, cardinality.max);
        }
    }

    public void setName(String name) {
        if (!name.equals(getName())) {
            formPageController.renameElement(input, name);
        }
    }

    public void setNamespace(String namespace) {
        throw new IllegalStateException("Namespace cannot be changed!"); //$NON-NLS-1$
    }

    public void setNillable(boolean nillable) {
        if (nillable != getNillable()) {
            formPageController.setNillable(null, input, nillable);
        }
    }

    public void setInput(ITreeNode treeNode) {
        IModelObject input = treeNode == null ? null : treeNode.getModelObject();
        this.input = (IElement) input;
        constraintsController = null;
    }

    public IType getType() {
        return input.getType();
    }

    public void setType(IType type) {
        if (type != null && !type.equals(getType())) {
            formPageController.setTypeForElement(type, null, input);
        }
    }

    public IConstraintsController getConstraintsSectionController() {
        if (constraintsController == null) {
            if (getType().isAnonymous()) {
                constraintsController = new SimpleTypeConstraintsController(formPageController);
            } else {
                constraintsController = new ElementConstraintsController(formPageController, (IElement) input);
            }
            constraintsController.setType((ISimpleType) getType());
        }
        return constraintsController;
    }

    public IType getBaseType() {
        return null;
    }

    public boolean isBaseTypeApplicable() {
        return false;
    }

    public boolean isBaseTypeEditable() {
        return false;
    }

    public void setBaseType(IType baseType) {
    }

    public boolean isTypeApplicable() {
        return true;
    }

    public boolean isTypeEditable() {
        return true;
    }

}
