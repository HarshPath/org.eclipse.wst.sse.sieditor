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
 *    Keshav Veerapaneni - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.model.write.xsd.api;

import org.eclipse.core.commands.ExecutionException;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet;

/**
 * @deprecated The write API is deprecated. Use commands instead.
 * 
 *
 */
public interface ISimpleType extends IType {

	void setMinLength(int length) throws ExecutionException;

	void setMaxLength(int length) throws ExecutionException;

	void setMinInclusive(String value) throws ExecutionException;

	void setMinExclusive(String value) throws ExecutionException;

	void setMaxInclusive(String value) throws ExecutionException;

	void setMaxExclusive(String value) throws ExecutionException;
	
	void setLength(int length) throws ExecutionException;
	
	void addPattern(String pattern) throws ExecutionException;
	
	void addEnumeration(String value) throws ExecutionException;
	
	void removeEnumeration(IFacet facet) throws ExecutionException;
	
	void removePattern(IFacet facet) throws ExecutionException;
	
	void setCollapseWhiteSpaces(boolean collapse) throws ExecutionException;

}
