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
package org.eclipse.wst.sse.sieditor.model.xsd.impl;

import static org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils.getXSDPackage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDFacet;
import org.eclipse.xsd.XSDFractionDigitsFacet;
import org.eclipse.xsd.XSDLengthFacet;
import org.eclipse.xsd.XSDMaxExclusiveFacet;
import org.eclipse.xsd.XSDMaxInclusiveFacet;
import org.eclipse.xsd.XSDMaxLengthFacet;
import org.eclipse.xsd.XSDMinExclusiveFacet;
import org.eclipse.xsd.XSDMinInclusiveFacet;
import org.eclipse.xsd.XSDMinLengthFacet;
import org.eclipse.xsd.XSDPatternFacet;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTotalDigitsFacet;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDWhiteSpace;
import org.eclipse.xsd.XSDWhiteSpaceFacet;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddFacetCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.DeleteFacetCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.RenameNamedComponent;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetBaseTypeCommand;
import org.eclipse.wst.sse.sieditor.core.common.Nil;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.generic.DuplicateException;
import org.eclipse.wst.sse.sieditor.model.generic.IllegalInputException;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

/**
 * 
 * 
 */
public class SimpleType extends AbstractType implements ISimpleType,
        org.eclipse.wst.sse.sieditor.model.write.xsd.api.ISimpleType {

    private final XSDSimpleTypeDefinition _simpleType;

    public SimpleType(final IXSDModelRoot modelRoot, final ISchema schema, final XSDSimpleTypeDefinition type) {
        this(modelRoot, schema, schema, type);
    }
    public SimpleType(final IXSDModelRoot modelRoot, final ISchema schema, final IModelObject parent, final XSDSimpleTypeDefinition type) {
        super(modelRoot, type, schema, parent);
        this._simpleType = type;
    }

    public String getDocumentation() {
        final Element documentation = getFirstElement(_simpleType.getAnnotations());
        return super.getDocumentation(documentation);
    }

    public IType getBaseType() {
        final XSDTypeDefinition baseType = _simpleType.getBaseType();
        if (null == baseType)
            return null;
        Schema parentSchema = (Schema) getParent();
        if (null == baseType.getName()) {
            return UnresolvedType.instance();
        }

        return (IType) parentSchema.resolveComponent(baseType, true);
    }

    public void setBaseType(final IType baseType) throws ExecutionException {
        if (!(baseType instanceof ISimpleType))
            return;
        final SetBaseTypeCommand command = new SetBaseTypeCommand(getModelRoot(), this, (AbstractType) baseType);
        getModelRoot().getEnv().execute(command);
    }

    public String getLength() {
        return _simpleType.getLengthFacet() == null ? null : _simpleType.getLengthFacet().getLexicalValue();
    }

    public String getMaxExclusive() {
        return _simpleType.getMaxExclusiveFacet() == null ? null : _simpleType.getMaxExclusiveFacet().getLexicalValue();
    }

    public String getMaxInclusive() {
        return _simpleType.getMaxInclusiveFacet() == null ? null : _simpleType.getMaxInclusiveFacet().getLexicalValue();
    }

    public String getMaxLength() {
        return _simpleType.getMaxLengthFacet() == null ? null : _simpleType.getMaxLengthFacet().getLexicalValue();
    }

    public String getMinExclusive() {
        return _simpleType.getMinExclusiveFacet() == null ? null : _simpleType.getMinExclusiveFacet().getLexicalValue();
    }

    public String getMinInclusive() {
        return _simpleType.getMinInclusiveFacet() == null ? null : _simpleType.getMinInclusiveFacet().getLexicalValue();
    }

    public String getMinLength() {
        return _simpleType.getMinLengthFacet() == null ? null : _simpleType.getMinLengthFacet().getLexicalValue();
    }

    public IFacet[] getPatterns() {
        List<XSDPatternFacet> patterns = _simpleType.getPatternFacets();
        List<IFacet> enums = new ArrayList<IFacet>(1);
        for (XSDPatternFacet pattern : patterns) {
            enums.add(new Facet(getModelRoot(), pattern, this, getXSDPackage().getXSDPatternFacet()));
        }
        return enums.toArray(new IFacet[enums.size()]);
    }

    public IFacet[] getEnumerations() {
        List<XSDEnumerationFacet> enumerations = _simpleType.getEnumerationFacets();
        List<IFacet> enums = new ArrayList<IFacet>(1);
        for (XSDEnumerationFacet enumFacet : enumerations) {
            enums.add(new Facet(getModelRoot(), enumFacet, this, getXSDPackage().getXSDEnumerationFacet()));
        }
        return enums.toArray(new IFacet[enums.size()]);
    }

    public boolean getCollapseWhiteSpaces() {
        return _simpleType.getWhiteSpaceFacet() == null ? false
                : _simpleType.getWhiteSpaceFacet().getValue() == XSDWhiteSpace.COLLAPSE_LITERAL;
    }

    public void addEnumeration(final String value) throws ExecutionException {
        Nil.checkNil(value, "value"); //$NON-NLS-1$
        final AddFacetCommand command = new AddFacetCommand(getModelRoot(), this, getXSDPackage().getXSDEnumerationFacet(), null,
                value);
        getModelRoot().getEnv().execute(command);
    }

    public void addPattern(String pattern) throws ExecutionException {
        Nil.checkNil(pattern, "value"); //$NON-NLS-1$
        final AddFacetCommand command = new AddFacetCommand(getModelRoot(), this, getXSDPackage().getXSDPatternFacet(), null,
                pattern);
        getModelRoot().getEnv().execute(command);
    }

    public void setLength(int length) throws ExecutionException {
        Nil.checkNil(length, "length"); //$NON-NLS-1$
        final XSDLengthFacet facet = _simpleType.getLengthFacet();
        final AddFacetCommand command = new AddFacetCommand(getModelRoot(), this, getXSDPackage().getXSDLengthFacet(), facet,
                length + ""); //$NON-NLS-1$
        getModelRoot().getEnv().execute(command);
    }

    public void setMaxExclusive(String value) throws ExecutionException {
        Nil.checkNil(value, "length"); //$NON-NLS-1$
        final XSDMaxExclusiveFacet facet = _simpleType.getMaxExclusiveFacet();
        final AddFacetCommand command = new AddFacetCommand(getModelRoot(), this, getXSDPackage().getXSDMaxExclusiveFacet(),
                facet, value);
        getModelRoot().getEnv().execute(command);
    }

    public void setMaxInclusive(String value) throws ExecutionException {
        Nil.checkNil(value, "length"); //$NON-NLS-1$
        final XSDMaxInclusiveFacet facet = _simpleType.getMaxInclusiveFacet();
        final AddFacetCommand command = new AddFacetCommand(getModelRoot(), this, getXSDPackage().getXSDMaxInclusiveFacet(),
                facet, value);
        getModelRoot().getEnv().execute(command);
    }

    public void setMaxLength(int length) throws ExecutionException {
        Nil.checkNil(length, "length"); //$NON-NLS-1$
        final XSDMaxLengthFacet facet = _simpleType.getMaxLengthFacet();
        final AddFacetCommand command = new AddFacetCommand(getModelRoot(), this, getXSDPackage().getXSDMaxLengthFacet(), facet,
                length + ""); //$NON-NLS-1$
        getModelRoot().getEnv().execute(command);
    }

    public void setMinExclusive(String value) throws ExecutionException {
        Nil.checkNil(value, "length"); //$NON-NLS-1$
        final XSDMinExclusiveFacet facet = _simpleType.getMinExclusiveFacet();
        final AddFacetCommand command = new AddFacetCommand(getModelRoot(), this, getXSDPackage().getXSDMinExclusiveFacet(),
                facet, value);
        getModelRoot().getEnv().execute(command);
    }

    public void setMinInclusive(String value) throws ExecutionException {
        Nil.checkNil(value, "length"); //$NON-NLS-1$
        final XSDMinInclusiveFacet facet = _simpleType.getMinInclusiveFacet();
        final AddFacetCommand command = new AddFacetCommand(getModelRoot(), this, getXSDPackage().getXSDMinInclusiveFacet(),
                facet, value + ""); //$NON-NLS-1$
        getModelRoot().getEnv().execute(command);
    }

    public void setMinLength(final int length) throws ExecutionException {
        Nil.checkNil(length, "length"); //$NON-NLS-1$
        final XSDMinLengthFacet facet = _simpleType.getMinLengthFacet();
        final AddFacetCommand command = new AddFacetCommand(getModelRoot(), this, getXSDPackage().getXSDMinLengthFacet(), facet,
                length + ""); //$NON-NLS-1$
        getModelRoot().getEnv().execute(command);
    }

    public void setCollapseWhiteSpaces(boolean collapse) throws ExecutionException {
        final XSDWhiteSpaceFacet facet = _simpleType.getWhiteSpaceFacet();
        if (collapse) {
            final AddFacetCommand command = new AddFacetCommand(getModelRoot(), this, getXSDPackage().getXSDWhiteSpaceFacet(),
                    facet, XSDWhiteSpace.COLLAPSE_LITERAL + ""); //$NON-NLS-1$
            getModelRoot().getEnv().execute(command);
        } else if (null != facet) {
            final DeleteFacetCommand command = new DeleteFacetCommand(getModelRoot(), this, facet);
            getModelRoot().getEnv().execute(command);
        }
    }

    public void setName(final String name) throws IllegalInputException, DuplicateException, ExecutionException {
        Nil.checkNil(name, "name"); //$NON-NLS-1$

        if (!EmfXsdUtils.isValidNCName(name))
            throw new IllegalInputException("Entered Type name '" + name + "' is not valid"); //$NON-NLS-1$ //$NON-NLS-2$

        final XSDTypeDefinition resolvedTypeDefinition = getParent().getComponent().resolveTypeDefinition(name);
        if (resolvedTypeDefinition.eContainer() != null) {
            throw new DuplicateException("Type with name '" + name + "' already exists"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        final RenameNamedComponent command = new RenameNamedComponent(getModelRoot(), this, name);
        getModelRoot().getEnv().execute(command);
    }

    /*
     * public Element setDocumentation(String description) throws
     * ExecutionException { Nil.checkNil(description, "description");
     * //$NON-NLS-1$ XSDAnnotation annotation = _simpleType.getAnnotation(); if
     * (null == annotation) { AddAnotationCommand command = new
     * AddAnotationCommand(_simpleType, getModelRoot(), this); if
     * (getModelRoot().getEnv().execute(command).isOK()) annotation =
     * command.getAnnotation(); } if (annotation != null) {
     * super.setDocumentation(annotation, description); } // TODO inform user of
     * failure and log??? return null; }
     */

    public void setNamespace(String namespace) {
        // Do Nothing
    }

    public void removeEnumeration(IFacet facet) throws ExecutionException {
        Nil.checkNil(facet, "facet"); //$NON-NLS-1$
        if (facet instanceof Facet && equals(((Facet) facet).getParent())) {
            final XSDFacet enumeration = facet.getComponent();
            if (enumeration instanceof XSDEnumerationFacet) {
                final DeleteFacetCommand command = new DeleteFacetCommand(getModelRoot(), this, enumeration);
                getModelRoot().getEnv().execute(command);
            }
        }
    }

    public void removePattern(IFacet facet) throws ExecutionException {
        Nil.checkNil(facet, "facet"); //$NON-NLS-1$
        if (facet instanceof Facet && equals(((Facet) facet).getParent())) {
            final XSDFacet pattern = facet.getComponent();
            if (pattern instanceof XSDPatternFacet) {
                final DeleteFacetCommand command = new DeleteFacetCommand(getModelRoot(), this, pattern);
                getModelRoot().getEnv().execute(command);
            }
        }
    }

    public Whitespace getWhitespace() {
        XSDWhiteSpaceFacet whiteSpaceFacet = _simpleType.getWhiteSpaceFacet();
        if (whiteSpaceFacet == null) {
            return null;
        }

        XSDWhiteSpace value = whiteSpaceFacet.getValue();
        if (value == XSDWhiteSpace.COLLAPSE_LITERAL) {
            return Whitespace.COLLAPSE;
        } else if (value == XSDWhiteSpace.PRESERVE_LITERAL) {
            return Whitespace.PRESERVE;
        } else if (value == XSDWhiteSpace.REPLACE_LITERAL) {
            return Whitespace.REPLACE;
        }

        return null;
    }

    public String getTotalDigits() {
        XSDTotalDigitsFacet totalDigitsFacet = _simpleType.getTotalDigitsFacet();
        if (totalDigitsFacet == null) {
            return null;
        }

        return totalDigitsFacet.getLexicalValue();
    }

    public String getFractionDigits() {
        XSDFractionDigitsFacet fractionDigitsFacet = _simpleType.getFractionDigitsFacet();
        if (fractionDigitsFacet == null) {
            return null;
        }

        return fractionDigitsFacet.getLexicalValue();
    }

}
