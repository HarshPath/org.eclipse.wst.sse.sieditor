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
package org.eclipse.wst.sse.sieditor.model.utils;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;

import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;

public class SimpleTypeFacetsUtils implements ISimpleTypeFacetsUtils {

    private static final ISimpleTypeFacetsUtils INSTANCE = new SimpleTypeFacetsUtils();

    private SimpleTypeFacetsUtils() {

    }

    public static ISimpleTypeFacetsUtils instance() {
        return INSTANCE;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.model.utils.ISimpleTypeFacetsUtils#
     * areLengthFacetsSupported(org.eclipse.xsd.XSDSimpleTypeDefinition)
     */
    public boolean areLengthFacetsSupported(final XSDSimpleTypeDefinition type) {
        final XSDTypeDefinition rootType = EmfXsdUtils.getRootBaseType(type);
        if (rootType instanceof XSDSimpleTypeDefinition) {
            return ((XSDSimpleTypeDefinition) rootType).getValidFacets().contains(XSDConstants.LENGTH_ELEMENT_TAG);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.model.utils.ISimpleTypeFacetsUtils#
     * areInclusiveExclusiveFacetsSupported
     * (org.eclipse.xsd.XSDSimpleTypeDefinition)
     */
    public boolean areInclusiveExclusiveFacetsSupported(final XSDSimpleTypeDefinition type) {
        final XSDTypeDefinition rootType = EmfXsdUtils.getRootBaseType(type);
        if (rootType instanceof XSDSimpleTypeDefinition) {
            final EList<String> validFacets = ((XSDSimpleTypeDefinition) rootType).getValidFacets();
            return validFacets.contains(XSDConstants.MAXINCLUSIVE_ELEMENT_TAG)
                    || validFacets.contains(XSDConstants.MAXEXCLUSIVE_ELEMENT_TAG)
                    || validFacets.contains(XSDConstants.MININCLUSIVE_ELEMENT_TAG)
                    || validFacets.contains(XSDConstants.MINEXCLUSIVE_ELEMENT_TAG);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.model.utils.ISimpleTypeFacetsUtils#
     * isEnumerationFacetSupported(org.eclipse.xsd.XSDSimpleTypeDefinition)
     */
    public boolean isEnumerationFacetSupported(final XSDSimpleTypeDefinition type) {
        final XSDTypeDefinition rootType = EmfXsdUtils.getRootBaseType(type);
        if (rootType instanceof XSDSimpleTypeDefinition) {
            return ((XSDSimpleTypeDefinition) rootType).getValidFacets().contains(XSDConstants.ENUMERATION_ELEMENT_TAG);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.model.utils.ISimpleTypeFacetsUtils#
     * isWhitespaceFacetSupported(org.eclipse.xsd.XSDSimpleTypeDefinition)
     */
    public boolean isWhitespaceFacetSupported(final XSDSimpleTypeDefinition type) {
        final XSDTypeDefinition rootType = EmfXsdUtils.getRootBaseType(type);
        if (rootType instanceof XSDSimpleTypeDefinition) {
            return ((XSDSimpleTypeDefinition) rootType).getValidFacets().contains(XSDConstants.WHITESPACE_ELEMENT_TAG);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.model.utils.ISimpleTypeFacetsUtils#
     * isPatternFacetSupported
     * (org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType)
     */
    public boolean isPatternFacetSupported(final ISimpleType type) {
        if (type == null) {
            return false;
        }
        final ISimpleType baseType = EmfXsdUtils.getRootBaseType(type);
        return baseType != null && !(baseType instanceof UnresolvedType);
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.model.utils.ISimpleTypeFacetsUtils#
     * isTotalDigitsFacetSupported(org.eclipse.xsd.XSDSimpleTypeDefinition)
     */
    public boolean isTotalDigitsFacetSupported(final XSDSimpleTypeDefinition type) {
        final XSDTypeDefinition rootType = EmfXsdUtils.getRootBaseType(type);
        if (rootType instanceof XSDSimpleTypeDefinition) {
            return ((XSDSimpleTypeDefinition) rootType).getValidFacets().contains(XSDConstants.TOTALDIGITS_ELEMENT_TAG);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.model.utils.ISimpleTypeFacetsUtils#
     * isFractionDigitsFacetSupported(org.eclipse.xsd.XSDSimpleTypeDefinition)
     */
    public boolean isFractionDigitsFacetSupported(final XSDSimpleTypeDefinition type) {
        final XSDTypeDefinition rootType = EmfXsdUtils.getRootBaseType(type);
        if (rootType instanceof XSDSimpleTypeDefinition) {
            return ((XSDSimpleTypeDefinition) rootType).getValidFacets().contains(XSDConstants.FRACTIONDIGITS_ELEMENT_TAG);
        }
        return false;
    }
}
