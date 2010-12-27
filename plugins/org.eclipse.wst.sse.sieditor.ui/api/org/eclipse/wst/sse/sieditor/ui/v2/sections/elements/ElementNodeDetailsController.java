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
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.AttributeStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;

public class ElementNodeDetailsController {

    private final IDataTypesFormPageController formPageController;

    private IElementStrategy strategy;

    private ITreeNode input;

    public ElementNodeDetailsController(final IDataTypesFormPageController formPageController) {
        this.formPageController = formPageController;
    }

    public IDataTypesFormPageController getFormPageController() {
        return formPageController;
    }

    public IConstraintsController getConstraintsController() {
        return strategy.getConstraintsSectionController();
    }

    public String getName() {
        final String name = strategy.getName();
        return name == null ? UIConstants.EMPTY_STRING : name;
    }

    public void setName(final String name) {
        if (!name.equals(getName())) {
            strategy.setName(name);
        }
    }

    public boolean isNameEditable() {
        return strategy.isNameEditable();
    }

    public String getNamespace() {
        return strategy.getNamespace();
    }

    public boolean isNamespaceEditable() {
        return strategy.isNamespaceEditable();
    }

    public boolean isNillable() {
        return strategy.getNillable();
    }

    public boolean isNillableEditable() {
        return strategy.isNillableEditable();
    }

    public boolean isNillableVisible() {
        return strategy.isNillableApplicable();
    }

    public void setNillable(final boolean nillable) {
        strategy.setNillable(nillable);
    }

    public boolean isCardinalityEditable() {
        return strategy.isCardinalityEditable();
    }

    public boolean isCardinalityVisible() {
        return strategy.isCardinalityApplicable();
    }

    public CardinalityType getCardinality() {
        return strategy.getCardinality();
    }

    public void setCardinality(final CardinalityType cardinality) {
        strategy.setCardinality(cardinality);
    }

    public IType getType() {
        final IType type = strategy.getType();
        if (type != null && !type.isAnonymous() && type.getName() == null) {
            return strategy.getBaseType();
        }
        return type;
    }

    public void setType(final IType type) {
        if (getType() != type) {
            strategy.setType(type);
            // initStrategy();
        }
    }

    public IType getBaseType() {
        return strategy.getBaseType();
    }

    public void setBaseType(final IType baseType) {
        if (getBaseType() != baseType) {
            strategy.setBaseType(baseType);
            // initStrategy();
        }
    }

    public boolean isBaseTypeApplicable() {
        return strategy.isBaseTypeApplicable();
    }

    public boolean isBaseTypeEditable() {
        return strategy.isBaseTypeEditable();
    }

    public boolean isConstraintsSectionApplicable() {
        return strategy.isConstraintsSectionApplicable();
    }

    public boolean isTypeApplicable() {
        return strategy.isTypeApplicable();
    }

    private ElementType getElementType(final IElement element) {
        if (!element.isAttribute()) {
            final IType type = element.getType();
            if (type instanceof IStructureType) {
                final IStructureType structureType = (IStructureType) type;
                if (structureType.isElement()) {
                    return ElementType.GLOBAL_ELEMENT_REFERENCE;
                    // 2: Element refers to a Global Element
                } else {
                    if (structureType.isAnonymous()) {
                        return ElementType.ANONYMOUS_TYPE;
                        // 1.2: Element of Complex Anonymous Type
                    } else {
                        return ElementType.GLOBAL_TYPE;
                        // 1.1: Element of Complex Global Type
                    }
                }
            } else {
                if (type.isAnonymous()) {
                    return ElementType.ANONYMOUS_TYPE;
                    // 1.2: Element of Simple Anonymous Type
                } else {
                    return ElementType.GLOBAL_TYPE;
                    // 1.1: Element of Simple Global Type
                }
            }
        } else {
            return ElementType.ATTRIBUTE;
            // 3: Attribute (only 'local' supported for now)
        }

    }

    public enum ElementType {
        GLOBAL_TYPE, ANONYMOUS_TYPE, GLOBAL_ELEMENT_REFERENCE, ATTRIBUTE, ATTRIBUTE_REFERENCE
    }

    public static class CardinalityType {
        public static final int UNBOUNDED = -1;
        public static final CardinalityType ZERO_TO_ONE = new CardinalityType(0, 1);
        public static final CardinalityType ONE_TO_ONE = new CardinalityType(1, 1);
        public static final CardinalityType ZERO_TO_MANY = new CardinalityType(0, UNBOUNDED);
        public static final CardinalityType ONE_TO_MANY = new CardinalityType(1, UNBOUNDED);

        public static final CardinalityType[] VALUES = { ZERO_TO_ONE, ONE_TO_ONE, ZERO_TO_MANY, ONE_TO_MANY };

        int min;
        int max;
        String stringValue;

        CardinalityType(final int min, final int max) {
            this.min = min;
            this.max = max;
            final StringBuilder buf = new StringBuilder(4);
            buf.append(min).append(" .. "); //$NON-NLS-1$
            if (max == UNBOUNDED) {
                buf.append('*');
            } else {
                buf.append(max);
            }
            stringValue = buf.toString();
        }

        @Override
        public String toString() {
            return stringValue;
        }

        public static CardinalityType get(final int min, final int max) {
            if (min == 0 && max == 1)
                return CardinalityType.ZERO_TO_ONE;
            if (min == 1 && max == 1)
                return CardinalityType.ONE_TO_ONE;
            if (min == 0 && (max == UNBOUNDED))
                return CardinalityType.ZERO_TO_MANY;
            if (min == 1 && (max == UNBOUNDED))
                return CardinalityType.ONE_TO_MANY;

            return new CardinalityType(min, max);
        }

        public static CardinalityType[] values() {
            return VALUES;
        }
    }

    public void setInput(final ITreeNode input) {
        this.input = input;
        initStrategy();
    }

    public ITreeNode getInput() {
        return input;
    }

    private void initStrategy() {
        strategy = calculateStrategy(input);
        strategy.setInput(input);
    }

    protected IElementStrategy calculateStrategy(final ITreeNode treeNode) {
        final IModelObject input = treeNode == null ? null : treeNode.getModelObject();
        if (input instanceof IElement) {
            final ElementType type = getElementType((IElement) input);
            switch (type) {
            case GLOBAL_TYPE:
                return new ElementOfGlobalTypeStrategy(formPageController);
            case ANONYMOUS_TYPE:
                return new ElementOfAnonymousTypeStrategy(formPageController);
            case GLOBAL_ELEMENT_REFERENCE:
                return new ElementRefToGlobalElementStrategy(formPageController);
            case ATTRIBUTE:
            case ATTRIBUTE_REFERENCE:
                return new AttributeStrategy(formPageController);
            }
        } else if (input instanceof IStructureType) {
            if (((IStructureType) input).isElement()) {
                return new GlobalElementStrategy(formPageController);
            }
            return new StructureTypeStrategy(formPageController);
        } else if (input instanceof ISimpleType) {
            return new SimpleTypeStrategy(formPageController);
        }

        throw new IllegalArgumentException();
    }
}
