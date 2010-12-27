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
package org.eclipse.wst.sse.sieditor.ui.v2.dt;

import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDWhiteSpace;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType.Whitespace;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;

public class ElementConstraintsController extends SimpleTypeConstraintsController {

    private static final IFacet[] EMTPTY_FACETS = new IFacet[0];

    private final IDataTypesFormPageController formPageController;
    private IElement input;
    private IStructureType structureInput;

    public ElementConstraintsController(IDataTypesFormPageController formPageController, IElement input) {
        super(formPageController);
        this.formPageController = formPageController;
        this.input = input;
    }

    public ElementConstraintsController(IDataTypesFormPageController formPageController, IStructureType structureInput) {
        super(formPageController);
        this.formPageController = formPageController;
        this.structureInput = structureInput;
    }

    public IFacet[] getEnums() {
        return EMTPTY_FACETS;
    }

    public String getFractionDigits() {
        return UIConstants.EMPTY_STRING;
    }

    public String getLength() {
        return UIConstants.EMPTY_STRING;
    }

    @Override
    public String getMaxExclusive() {
        return UIConstants.EMPTY_STRING;
    }

    public String getMaxInclusive() {
        return UIConstants.EMPTY_STRING;
    }

    @Override
    public String getMaxLength() {
        return UIConstants.EMPTY_STRING;
    }

    @Override
    public String getMinExclusive() {
        return UIConstants.EMPTY_STRING;
    }

    @Override
    public String getMinInclusive() {
        return UIConstants.EMPTY_STRING;
    }

    @Override
    public String getMinLength() {
        return UIConstants.EMPTY_STRING;
    }

    @Override
    public IFacet[] getPatterns() {
        return EMTPTY_FACETS;
    }

    @Override
    public String getTotalDigits() {
        return UIConstants.EMPTY_STRING;
    }

    @Override
    public Whitespace getWhitespace() {
        return null;
    }

    public void addEnum(String value) {
        setFacet(value, XSDPackage.XSD_ENUMERATION_FACET);
    }

    public void addPattern(String value) {
        setFacet(value, XSDPackage.XSD_PATTERN_FACET);
    }

    public void setFractionDigits(String value) {
        if (!getFractionDigits().equals(value)) {
            setFacet(value, XSDPackage.XSD_FRACTION_DIGITS_FACET);
        }
    }

    public void setLength(String value) {
        if (!getLength().equals(value)) {
            setFacet(value, XSDPackage.XSD_LENGTH_FACET);
        }
    }

    public void setMaxExclusive(String value) {
        if (!getMaxExclusive().equals(value)) {
            setFacet(value, XSDPackage.XSD_MAX_EXCLUSIVE_FACET);
        }
    }

    public void setMaxInclusive(String value) {
        if (!getMaxInclusive().equals(value)) {
            setFacet(value, XSDPackage.XSD_MAX_INCLUSIVE_FACET);
        }
    }

    public void setMaxLength(String value) {
        if (!getMaxLength().equals(value)) {
            setFacet(value, XSDPackage.XSD_MAX_LENGTH_FACET);
        }
    }

    public void setMinExclusive(String value) {
        if (!getMinExclusive().equals(value)) {
            setFacet(value, XSDPackage.XSD_MIN_EXCLUSIVE_FACET);
        }
    }

    public void setMinInclusive(String value) {
        if (!getMinInclusive().equals(value)) {
            setFacet(value, XSDPackage.XSD_MIN_INCLUSIVE_FACET);
        }
    }

    public void setMinLength(String value) {
        if (!getMinLength().equals(value)) {
            setFacet(value, XSDPackage.XSD_MIN_LENGTH_FACET);
        }
    }

    public void setTotalDigits(String value) {
        if (!getTotalDigits().equals(value)) {
            setFacet(value, XSDPackage.XSD_TOTAL_DIGITS_FACET);
        }
    }

    public void setWhitespace(Whitespace whitespace) {
        if (whitespace != null) {
            XSDWhiteSpace value = null;
            switch (whitespace) {
            case COLLAPSE:
                value = XSDWhiteSpace.COLLAPSE_LITERAL;
                break;
            case PRESERVE:
                value = XSDWhiteSpace.PRESERVE_LITERAL;
                break;
            case REPLACE:
                value = XSDWhiteSpace.REPLACE_LITERAL;
                break;
            default:
                throw new IllegalArgumentException();
            }
            setFacet(value.getLiteral(), XSDPackage.XSD_WHITE_SPACE_FACET);
        }
    }

    private void setFacet(String value, int facetId) {
        if (input != null) {
            formPageController.setElementFacet(input, value, facetId);
        } else {
            formPageController.setGlobalElementFacet(structureInput, value, facetId);
        }
    }
}
