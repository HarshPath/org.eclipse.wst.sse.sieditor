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

import org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType.Whitespace;

public interface IConstraintsController {

	public abstract void setType(ISimpleType type);
	
	IType getType();

	public abstract String getLength();

	public abstract void setLength(String value);

	public abstract boolean isLengthVisible();

        public abstract boolean isMinMaxVisible();
	
	public abstract boolean isMinMaxInclusiveExclusiveVisible();

	public abstract String getMinLength();

	public abstract void setMinLength(String value);

	public abstract String getMaxLength();

	public abstract void setMaxLength(String value);

	public abstract String getMinInclusive();

	public abstract void setMinInclusive(String value);

	public abstract String getMaxInclusive();

	public abstract void setMaxInclusive(String value);

	public abstract String getMinExclusive();

	public abstract void setMinExclusive(String value);

	public abstract String getMaxExclusive();

	public abstract void setMaxExclusive(String value);

	public abstract String getTotalDigits();

	public abstract void setTotalDigits(String value);

	public abstract boolean isTotalDigitsVisible();

	public abstract String getFractionDigits();

	public abstract void setFractionDigits(String value);

	public abstract Whitespace getWhitespace();

	public abstract void setWhitespace(Whitespace whitespace);

	public abstract boolean isFractionDigitsVisible();

	public abstract boolean isWhitespaceVisible();

	public abstract boolean isPatternsVisible();

	public abstract IFacet[] getPatterns();

	public abstract void addPattern(String value);

	public abstract void deletePattern(IFacet facet);

	public abstract void setPattern(IFacet facet, String value);

	public abstract IFacet[] getEnums();

	public abstract void setEnum(IFacet facet, String value);

	public abstract void addEnum(String value);

	public abstract void deleteEnum(IFacet facet);

	public abstract boolean isEnumsVisible();

        public abstract boolean isBaseTypeResolvable();

        public abstract boolean isEditable();
        
}