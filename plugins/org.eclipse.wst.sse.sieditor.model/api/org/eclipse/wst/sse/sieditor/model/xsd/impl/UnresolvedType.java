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
package org.eclipse.wst.sse.sieditor.model.xsd.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.commands.ExecutionException;

import org.eclipse.wst.sse.sieditor.model.generic.DuplicateException;
import org.eclipse.wst.sse.sieditor.model.generic.IllegalInputException;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class UnresolvedType extends AbstractType implements IStructureType,
        org.eclipse.wst.sse.sieditor.model.write.xsd.api.IStructureType , ISimpleType, org.eclipse.wst.sse.sieditor.model.write.xsd.api.ISimpleType{
    private static final Schema SCHEMA_FOR_SCHEMA = Schema.getSchemaForSchema();
    private static final UnresolvedType instance = new UnresolvedType();

    public static UnresolvedType instance() {
		return instance;
	}

	@Override
    public String getNamespace() {
        return Messages.UnresolvedType_1;
    }

    @Override
    public String getName() {
        return Messages.UnresolvedType_2;
    }

    public Collection<IElement> getAllElements() {
        return new ArrayList<IElement>();
    }

    public Collection<IElement> getElements(final String name) {
        return new ArrayList<IElement>();
    }

    public IType getType() {
        return this;
    }

    public boolean isElement() {
        return false;
    }
    
    @Override
    public boolean isComplexTypeSimpleContent() {
        return false;
    }

    public boolean isNillable() {
        return false;
    }

    public IType getBaseType() {
        return this;
    }

    @Override
    public boolean isAnonymous() {
        return false;
    }

    public String getDocumentation() {
        return null;
    }

    public IElement addElement(final String name) throws IllegalInputException, ExecutionException {
        throw new UnsupportedOperationException();
    }

    public IElement copyElement(final IElement element) throws IllegalInputException, ExecutionException {
        throw new UnsupportedOperationException();
    }

    public void removeElement(final IElement element) throws ExecutionException {
        throw new UnsupportedOperationException();
    }

    public void removeElement(final String attribute) throws ExecutionException {
        throw new UnsupportedOperationException();
    }

    public void setType(final IType type) throws ExecutionException {
        throw new UnsupportedOperationException();
    }

    public void setBaseType(final IType baseType) throws ExecutionException {
        throw new UnsupportedOperationException();
    }

    public void setName(final String name) throws IllegalInputException, DuplicateException, ExecutionException {
        throw new UnsupportedOperationException();
    }

    public void setNamespace(final String namespace) throws IllegalInputException, ExecutionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAnonymous(final boolean anonymousType) {
        throw new UnsupportedOperationException();
    }

    private UnresolvedType() {
        super(SCHEMA_FOR_SCHEMA.getModelRoot(), EmfXsdUtils.getSchemaForSchema().resolveTypeDefinition("anyType"), //$NON-NLS-1$
                SCHEMA_FOR_SCHEMA);
	}

    public boolean getCollapseWhiteSpaces() {
        return false;
    }

    public IFacet[] getEnumerations() {
        return null;
    }

    public String getFractionDigits() {
        return null;
    }

    public String getLength() {
        return null;
    }

    public String getMaxExclusive() {
        return null;
    }

    public String getMaxInclusive() {
        return null;
    }

    public String getMaxLength() {
        return null;
    }

    public String getMinExclusive() {
        return null;
    }

    public String getMinInclusive() {
        return null;
    }

    public String getMinLength() {
        return null;
    }

    public IFacet[] getPatterns() {
        return null;
    }

    public String getTotalDigits() {
        return null;
    }

    public Whitespace getWhitespace() {
        return null;
    }

    public void addEnumeration(final String value) throws ExecutionException {
        throw new UnsupportedOperationException();
    }

    public void addPattern(final String pattern) throws ExecutionException {
        throw new UnsupportedOperationException();
    }

    public void removeEnumeration(final IFacet facet) throws ExecutionException {
        throw new UnsupportedOperationException();
    }

    public void removePattern(final IFacet facet) throws ExecutionException {
        throw new UnsupportedOperationException();
    }

    public void setCollapseWhiteSpaces(final boolean collapse) throws ExecutionException {
        throw new UnsupportedOperationException();
    }

    public void setLength(final int length) throws ExecutionException {
        throw new UnsupportedOperationException();
    }

    public void setMaxExclusive(final String value) throws ExecutionException {
        throw new UnsupportedOperationException();
    }

    public void setMaxInclusive(final String value) throws ExecutionException {
        throw new UnsupportedOperationException();
    }

    public void setMaxLength(final int length) throws ExecutionException {
        throw new UnsupportedOperationException();
    }

    public void setMinExclusive(final String value) throws ExecutionException {
        throw new UnsupportedOperationException();
    }

    public void setMinInclusive(final String value) throws ExecutionException {
        throw new UnsupportedOperationException();
    }

    public void setMinLength(final int length) throws ExecutionException {
        throw new UnsupportedOperationException();
    }
}


