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
package org.eclipse.wst.sse.sieditor.model.utils;

import org.eclipse.xsd.XSDSimpleTypeDefinition;

import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;

public interface ISimpleTypeFacetsUtils {

    public boolean areLengthFacetsSupported(final XSDSimpleTypeDefinition type);

    public boolean areInclusiveExclusiveFacetsSupported(final XSDSimpleTypeDefinition type);

    public boolean isEnumerationFacetSupported(final XSDSimpleTypeDefinition type);

    public boolean isWhitespaceFacetSupported(final XSDSimpleTypeDefinition type);

    /**
     * Returns <code>true</code> if the Pattern Facet is supported for the
     * provided simple type argument. By XSD Spec, the pattern facet is
     * applicable for any base type, but not for unresolved base type
     * 
     * @param type
     * @return
     */
    public boolean isPatternFacetSupported(final ISimpleType type);

    public boolean isTotalDigitsFacetSupported(final XSDSimpleTypeDefinition type);

    public boolean isFractionDigitsFacetSupported(final XSDSimpleTypeDefinition type);

}