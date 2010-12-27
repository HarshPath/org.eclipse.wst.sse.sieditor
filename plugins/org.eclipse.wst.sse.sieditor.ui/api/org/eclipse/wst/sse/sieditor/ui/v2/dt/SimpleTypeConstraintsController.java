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

import org.eclipse.xsd.XSDSimpleTypeDefinition;

import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.utils.ISimpleTypeFacetsUtils;
import org.eclipse.wst.sse.sieditor.model.utils.SimpleTypeFacetsUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType.Whitespace;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;

public class SimpleTypeConstraintsController implements IConstraintsController {

    private final IDataTypesFormPageController formPageController;
    private ISimpleType type;

    public SimpleTypeConstraintsController(final IDataTypesFormPageController formPageController) {
        super();
        this.formPageController = formPageController;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#setType
     * (org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType)
     */
    public void setType(final ISimpleType type) {
        this.type = type;
    }

    public ISimpleType getType() {
        return type;
    }

    public String getLength() {
        return ensureNotNull(type.getLength());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#setLength
     * (java.lang.String)
     */
    public void setLength(final String value) {
        if (!getLength().equals(value)) {
            formPageController.setSimpleTypeLengthFacet(type, ensureNullIfEmpty(value));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#
     * isLengthVisible()
     */
    public boolean isLengthVisible() {
        if (type != null && type.getComponent() instanceof XSDSimpleTypeDefinition && type.getComponent().getSchema() != null) {
            return getSimpleTypeFacets().areLengthFacetsSupported((XSDSimpleTypeDefinition) type.getComponent());
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#
     * isMinMaxInclusiveExclusiveVisible()
     */
    public boolean isMinMaxInclusiveExclusiveVisible() {
        if (type != null && type.getComponent() instanceof XSDSimpleTypeDefinition && type.getComponent().getSchema() != null) {
            return getSimpleTypeFacets().areInclusiveExclusiveFacetsSupported((XSDSimpleTypeDefinition) type.getComponent());
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#getMinLength
     * ()
     */
    public String getMinLength() {
        return ensureNotNull(type.getMinLength());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#setMinLength
     * (java.lang.String)
     */
    public void setMinLength(final String value) {
        if (!getMinLength().equals(value)) {
            formPageController.setSimpleTypeMinLengthFacet(type, ensureNullIfEmpty(value));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#getMaxLength
     * ()
     */
    public String getMaxLength() {
        return ensureNotNull(type.getMaxLength());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#setMaxLength
     * (java.lang.String)
     */
    public void setMaxLength(final String value) {
        if (!getMaxLength().equals(value)) {
            formPageController.setSimpleTypeMaxLengthFacet(type, ensureNullIfEmpty(value));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#
     * getMinInclusive()
     */
    public String getMinInclusive() {
        return ensureNotNull(type.getMinInclusive());
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#
     * setMinInclusive(java.lang.String)
     */
    public void setMinInclusive(final String value) {
        if (!getMinInclusive().equals(value)) {
            formPageController.setSimpleTypeMinInclusiveFacet(type, ensureNullIfEmpty(value));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#
     * getMaxInclusive()
     */
    public String getMaxInclusive() {
        return ensureNotNull(type.getMaxInclusive());
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#
     * setMaxInclusive(java.lang.String)
     */
    public void setMaxInclusive(final String value) {
        if (!getMaxInclusive().equals(value)) {
            formPageController.setSimpleTypeMaxInclusiveFacet(type, ensureNullIfEmpty(value));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#
     * getMinExclusive()
     */
    public String getMinExclusive() {
        return ensureNotNull(type.getMinExclusive());
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#
     * setMinExclusive(java.lang.String)
     */
    public void setMinExclusive(final String value) {
        if (!getMinExclusive().equals(value)) {
            formPageController.setSimpleTypeMinExclusiveFacet(type, ensureNullIfEmpty(value));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#
     * getMaxExclusive()
     */
    public String getMaxExclusive() {
        return ensureNotNull(type.getMaxExclusive());
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#
     * setMaxExclusive(java.lang.String)
     */
    public void setMaxExclusive(final String value) {
        if (!getMaxExclusive().equals(value)) {
            formPageController.setSimpleTypeMaxExclusiveFacet(type, ensureNullIfEmpty(value));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#getTotalDigits
     * ()
     */
    public String getTotalDigits() {
        return ensureNotNull(type.getTotalDigits());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#setTotalDigits
     * (java.lang.String)
     */
    public void setTotalDigits(final String value) {
        if (!getTotalDigits().equals(value)) {
            formPageController.setSimpleTypeTotalDigitsFacet(type, ensureNullIfEmpty(value));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#
     * isTotalDigitsVisible()
     */
    public boolean isTotalDigitsVisible() {
        if (type != null && type.getComponent() instanceof XSDSimpleTypeDefinition && type.getComponent().getSchema() != null) {
            return getSimpleTypeFacets().isTotalDigitsFacetSupported((XSDSimpleTypeDefinition) type.getComponent());
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#
     * getFractionDigits()
     */
    public String getFractionDigits() {
        return ensureNotNull(type.getFractionDigits());
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#
     * setFractionDigits(java.lang.String)
     */
    public void setFractionDigits(final String value) {
        if (!getFractionDigits().equals(value)) {
            formPageController.setSimpleTypeFractionDigitsFacet(type, ensureNullIfEmpty(value));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#getWhitespace
     * ()
     */
    public Whitespace getWhitespace() {
        return type.getWhitespace();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#setWhitespace
     * (org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType.Whitespace)
     */
    public void setWhitespace(final Whitespace whitespace) {
        if (getWhitespace() != whitespace) {
            formPageController.setSimpleTypeWhitespaceFacet(type, whitespace);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#
     * isFractionDigitsVisible()
     */
    public boolean isFractionDigitsVisible() {
        if (type != null && type.getComponent() instanceof XSDSimpleTypeDefinition && type.getComponent().getSchema() != null) {
            return getSimpleTypeFacets().isFractionDigitsFacetSupported((XSDSimpleTypeDefinition) type.getComponent());
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#
     * isWhitespaceVisible()
     */
    public boolean isWhitespaceVisible() {
        if (type != null && type.getComponent() instanceof XSDSimpleTypeDefinition && type.getComponent().getSchema() != null) {
            return getSimpleTypeFacets().isWhitespaceFacetSupported((XSDSimpleTypeDefinition) type.getComponent());
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#
     * isPatternsVisible()
     */
    public boolean isPatternsVisible() {
        return getSimpleTypeFacets().isPatternFacetSupported(type);
    }

    public boolean isBaseTypeResolvable() {
        final ISimpleType resolvedBaseType = EmfXsdUtils.getRootBaseType(type);
        return !(resolvedBaseType == null || resolvedBaseType instanceof UnresolvedType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#getPatterns
     * ()
     */
    public IFacet[] getPatterns() {
        return type.getPatterns();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#addPattern
     * (java.lang.String)
     */
    public void addPattern(final String value) {
        formPageController.addSimpleTypePatternFacet(type, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#deletePattern
     * (org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet)
     */
    public void deletePattern(final IFacet facet) {
        formPageController.deleteSimpleTypePatternFacet(type, facet);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#setPattern
     * (org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet, java.lang.String)
     */
    public void setPattern(final IFacet facet, final String value) {
        formPageController.setSimpleTypePatternFacet(type, facet, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#getEnums()
     */
    public IFacet[] getEnums() {
        return type.getEnumerations();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#setEnum
     * (org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet, java.lang.String)
     */
    public void setEnum(final IFacet facet, final String value) {
        formPageController.setSimpleTypeEnumFacet(type, facet, value);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#addEnum
     * (java.lang.String)
     */
    public void addEnum(final String value) {
        formPageController.addSimpleTypeEnumFacet(type, value);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#deleteEnum
     * (org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet)
     */
    public void deleteEnum(final IFacet facet) {
        formPageController.deleteSimpleTypeEnumFacet(type, facet);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.dt.IConstraintsController#isEnumsVisible
     * ()
     */
    public boolean isEnumsVisible() {
        if (type != null && type.getComponent() instanceof XSDSimpleTypeDefinition && type.getComponent().getSchema() != null) {
            return getSimpleTypeFacets().isEnumerationFacetSupported((XSDSimpleTypeDefinition) type.getComponent());
        }
        return false;
    }

    private String ensureNotNull(final String value) {
        if (value == null) {
            return ""; //$NON-NLS-1$
        }

        return value;
    }

    private String ensureNullIfEmpty(final String value) {
        if (value.length() == 0) {
            return null;
        }

        return value;
    }

    public boolean isEditable() {
        return isBaseTypeResolvable();
    }

    public boolean isMinMaxVisible() {
        return isLengthVisible();
    }

    // =========================================================
    // helpers
    // ========================================================

    protected ISimpleTypeFacetsUtils getSimpleTypeFacets() {
        return SimpleTypeFacetsUtils.instance();
    }
}
