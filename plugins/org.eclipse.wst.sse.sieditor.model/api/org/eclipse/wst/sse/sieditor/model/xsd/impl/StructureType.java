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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.workspace.AbstractEMFOperation;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDAttributeGroupContent;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDDerivationMethod;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddElementCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.CopyElementCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.DeleteElementCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.RenameStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetBaseTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.core.common.Nil;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.generic.DuplicateException;
import org.eclipse.wst.sse.sieditor.model.generic.IllegalInputException;
import org.eclipse.wst.sse.sieditor.model.impl.ModelChangeEvent;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class StructureType extends AbstractType implements IStructureType,
        org.eclipse.wst.sse.sieditor.model.write.xsd.api.IStructureType {

    public StructureType(final IXSDModelRoot modelRoot, final Schema schema, final XSDNamedComponent component) {
        this(modelRoot, schema, schema, component);
    }

    public StructureType(final IXSDModelRoot modelRoot, final Schema schema, final IModelObject parent,
            final XSDNamedComponent component) {
        super(modelRoot, component, schema, parent);
        if (!(component instanceof XSDElementDeclaration || component instanceof XSDComplexTypeDefinition)) {
            throw new RuntimeException("Component must be either XSDElementDeclaration or XSDComplexTypeDefinition"); //$NON-NLS-1$
        }
        final XSDElementDeclaration _element = component instanceof XSDElementDeclaration ? (XSDElementDeclaration) component
                : null;

        if (null == _element)
            Assert.isTrue(component instanceof XSDComplexTypeDefinition);
    }

    public String getDocumentation() {
        final List<XSDAnnotation> annotations = new ArrayList<XSDAnnotation>(1);
        XSDAnnotation annotation = null;
        if (getComponent() instanceof XSDElementDeclaration) {
            annotation = ((XSDElementDeclaration) getComponent()).getAnnotation();
        } else if (getComponent() instanceof XSDComplexTypeDefinition) {
            annotation = ((XSDTypeDefinition) getComponent()).getAnnotation();
        }
        if (null != annotation)
            annotations.add(annotation);
        if (annotations.size() == 0)
            return ""; //$NON-NLS-1$
        final org.w3c.dom.Element documentation = getFirstElement(annotations);
        return super.getDocumentation(documentation);
    }

    public IType getBaseType() {
        return getBaseType(getComponent(), getSchema());
    }

    static IType getBaseType(final XSDConcreteComponent component, final Schema schema) {
        // deliberately not using the following line in order to preserve the
        // checks from the prev. implementation
        // XSDTypeDefinition baseType = getXSDTypeDefinition().getBaseType();
        XSDTypeDefinition baseType = null;
        if (component instanceof XSDElementDeclaration) {
            final XSDTypeDefinition type = ((XSDElementDeclaration) component).getType();
            if (type == null || type.getName() != null) {
                return null;
            }
            baseType = type.getBaseType();
        } else if (component instanceof XSDComplexTypeDefinition) {
            baseType = ((XSDTypeDefinition) component).getBaseType();
        }
        if (baseType == null) {
            return null;
        }
        // If type is not set which will not be the case, as the type will be
        // anyType
        return (IType) schema.resolveComponent(baseType, true);
    }

    public void setType(final IType type) throws ExecutionException {
        // if (null == _element)
        // return;
        IType resolvedType = null;
        if (type instanceof AbstractType)
            resolvedType = getSchema().resolveType((AbstractType) type);
        // DOIT Import if needed
        if (null != resolvedType && resolvedType.getComponent() instanceof XSDTypeDefinition) {
            final SetStructureTypeCommand command = new SetStructureTypeCommand(getModelRoot(), this, resolvedType);
            getModelRoot().getEnv().execute(command);
        }
    }

    public void setBaseType(final IType baseType) throws ExecutionException {
        /*
         * IType resolvedType = null; if (baseType instanceof AbstractType)
         * resolvedType = _schema.resolveType((AbstractType) baseType);
         * 
         * if (null == resolvedType || ((AbstractType)
         * resolvedType).getComponent() instanceof XSDElementDeclaration)
         * return;
         */

        final SetBaseTypeCommand command = new SetBaseTypeCommand(getModelRoot(), this, baseType);
        getModelRoot().getEnv().execute(command);
    }

    public Collection<IElement> getAllElements() {
        final List<IElement> elements = new ArrayList<IElement>();
        final XSDTypeDefinition xsdTypeDefinition = getXSDTypeDefinition();
        if (null != getElement()
                && (null == xsdTypeDefinition || xsdTypeDefinition instanceof XSDSimpleTypeDefinition || null != xsdTypeDefinition
                        .getName())) {
            return Collections.emptyList();
        }
        elements.addAll(processType((XSDComplexTypeDefinition) xsdTypeDefinition));
        elements.addAll(processAttributeGroupContent((XSDComplexTypeDefinition) xsdTypeDefinition));

        return elements;
    }

    private List<Element> processType(final XSDComplexTypeDefinition type) {
        final XSDComplexTypeContent content = type.getContent();
        if (content instanceof XSDParticle) {
            final XSDParticle particle = (XSDParticle) content;
            final XSDParticleContent particleContent = particle.getContent();
            if (particleContent instanceof XSDModelGroup) {
                return processModelGroup((XSDModelGroup) particleContent);
            }
        }
        return new ArrayList<Element>();
    }

    private List<Element> processParticle(final XSDParticle particle, final XSDModelGroup container) {
        final XSDParticleContent content = particle.getContent();
        if (content instanceof XSDModelGroup)
            return processModelGroup((XSDModelGroup) content);
        else if (content instanceof XSDElementDeclaration) {
            final List<Element> elements = new ArrayList<Element>(1);
            Element element = null;
            element = new Element(getModelRoot(), particle, container, this, getSchema());
            elements.add(element);
            return elements;
        }
        return new ArrayList<Element>();
    }

    private List<Element> processModelGroup(final XSDModelGroup modelGroup) {
        final List<XSDParticle> particles = modelGroup.getParticles();
        final List<Element> result = new ArrayList<Element>(1);
        for (final XSDParticle particle : particles) {
            result.addAll(processParticle(particle, modelGroup));
        }
        return result;
    }

    private List<Element> processAttributeGroupContent(final XSDComplexTypeDefinition type) {
        final List<XSDAttributeGroupContent> contents = type.getAttributeContents();
        final List<Element> result = new ArrayList<Element>(contents.size());
        for (final XSDAttributeGroupContent content : contents) {
            if (content instanceof XSDAttributeUse) {
                result.add(new Element(getModelRoot(), ((XSDAttributeUse) content).getContent(), null, this, getSchema()));
            }
        }
        return result;
    }

    public Collection<IElement> getElements(final String name) {
        final Collection<IElement> result = new ArrayList<IElement>(1);
        final Collection<IElement> elements = getAllElements();
        if (name != null) {
            for (final IElement element : elements) {
                if (name.equals(element.getName())) {
                    result.add(element);
                }
            }
        }
        return result;
    }

    public IElement addElement(final String name) throws IllegalInputException, ExecutionException {
        Nil.checkNil(name, "name"); //$NON-NLS-1$

        if (!EmfXsdUtils.isValidNCName(name))
            throw new IllegalInputException("Entered element name is not valid"); //$NON-NLS-1$

        final AddElementCommand command = new AddElementCommand(getModelRoot(), this, name);
        getModelRoot().getEnv().execute(command);
        return command.getElement();
    }

    public IElement copyElement(final IElement element) throws IllegalInputException, ExecutionException {
        Nil.checkNil(element, "element"); //$NON-NLS-1$
        if (element instanceof Element) {

            final CopyElementCommand command;

            command = new CopyElementCommand(getModelRoot(), this, element, getSchema());
            getModelRoot().getEnv().execute(command);

            return command.getCopiedElement();
        }
        return null;
    }

    public void removeElement(final IElement element) throws ExecutionException {
        Nil.checkNil(element, "element"); //$NON-NLS-1$
        final List<IElement> elements = (List<IElement>) getAllElements();
        final int position = elements.indexOf(element);
        if (-1 != position) {
            final Element match = (Element) elements.get(position);
            getModelRoot().getEnv().execute(new DeleteElementCommand(getModelRoot(), this, match));
        }
    }

    public void removeElement(final String name) throws ExecutionException {
        Nil.checkNil(name, "name"); //$NON-NLS-1$
        final Collection<IElement> elements = getAllElements();
        for (final IElement element : elements) {
            if (name.equals(element.getName())) {
                final DeleteElementCommand command = new DeleteElementCommand(getModelRoot(), this, element);
                getModelRoot().getEnv().execute(command);
            }
        }
        // notify listeners only once
        getModelRoot().notifyListeners(new ModelChangeEvent(this));
    }

    public boolean isElement() {
        return getComponent() instanceof XSDElementDeclaration;
    }

    public boolean isComplexTypeSimpleContent() {
        if (isElement()) {
            final XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration) getComponent();

            if (xsdElementDeclaration.getType() == null) {
                return false;
            }
            final XSDTypeDefinition baseType = xsdElementDeclaration.getType().getBaseType();
            if (baseType instanceof XSDSimpleTypeDefinition) {
                return true;
            }
            return isComplexTypeSimpleContentInternal((XSDComplexTypeDefinition) baseType, new HashSet<XSDTypeDefinition>());

        } else {
            final XSDComplexTypeDefinition xsdComplexType = (XSDComplexTypeDefinition) getComponent();
            return isComplexTypeSimpleContentInternal(xsdComplexType, new HashSet<XSDTypeDefinition>());
        }
    }

    private boolean isComplexTypeSimpleContentInternal(final XSDComplexTypeDefinition xsdComplexType,
            final Collection<XSDTypeDefinition> visited) {
        if (xsdComplexType == null) {
            return false;
        }
        final XSDTypeDefinition baseType = xsdComplexType.getBaseType();
        if (baseType instanceof XSDSimpleTypeDefinition) {
            return xsdComplexType.getDerivationMethod() == XSDDerivationMethod.EXTENSION_LITERAL;

        } else if (baseType instanceof XSDComplexTypeDefinition && !visited.contains(baseType)) {
            visited.add(baseType);
            return xsdComplexType.getDerivationMethod() == XSDDerivationMethod.EXTENSION_LITERAL
                    && isComplexTypeSimpleContentInternal((XSDComplexTypeDefinition) baseType, visited);
        }
        return false;
    }

    public Schema getSchema() {
        return (Schema) getParent();
    }

    public void setName(final String name) throws IllegalInputException, DuplicateException, ExecutionException {
        Nil.checkNil(name, "name"); //$NON-NLS-1$

        if (!EmfXsdUtils.isValidNCName(name)) {
            throw new IllegalInputException("Entered Type name '" + name + "' is not valid"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        final XSDSchema schema = getSchema().getComponent();
        if (isElement()) {
            if (schema.resolveElementDeclaration(name).eContainer() != null) {
                throw new DuplicateException("Element with name '" + name + "' already exists"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } else {
            if (schema.resolveTypeDefinition(name).eContainer() != null) {
                throw new DuplicateException("Type with name '" + name + "' already exists"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        final AbstractEMFOperation command = new RenameStructureTypeCommand(getModelRoot(), this, name);
        getModelRoot().getEnv().execute(command);
    }

    /*
     * public org.w3c.dom.Element setDocumentation(String description) throws
     * ExecutionException { Nil.checkNil(description, "description");
     * //$NON-NLS-1$ XSDAnnotation annotation = null; if (null != _element) {
     * annotation = _element.getAnnotation(); if (null == annotation) {
     * AddAnotationCommand command = new AddAnotationCommand(_element,
     * getModelRoot(), this); if
     * (getModelRoot().getEnv().execute(command).isOK()) { annotation =
     * command.getAnnotation(); } } } else { annotation = _type.getAnnotation();
     * if (null == annotation) { AddAnotationCommand command = new
     * AddAnotationCommand(_type, getModelRoot(), this); if
     * (getModelRoot().getEnv().execute(command).isOK()) { annotation =
     * command.getAnnotation(); } } } super.setDocumentation(annotation,
     * description); return null; }
     */

    public void setNamespace(final String namespace) {
        // Do Nothing
    }

    public IType getType() {
        // not sure about the following condition
        if (getComponent() instanceof XSDTypeDefinition) {
            return null;
        }
        if (!(getComponent() instanceof XSDElementDeclaration)) {
            return null;
        }
        final XSDElementDeclaration element = (XSDElementDeclaration) getComponent();

        XSDTypeDefinition type = element.getAnonymousTypeDefinition();
        if (type != null) {
            // type is anonymous
            if (type instanceof XSDComplexTypeDefinition) {
                return new StructureType(getModelRoot(), getSchema(), this, type);
            }
            return new SimpleType(getModelRoot(), getSchema(), this, (XSDSimpleTypeDefinition) type);
        }

        type = element.getTypeDefinition();
        if (null == type) {
            return null;
        }

        return (IType) getSchema().resolveComponent(type, true);
    }

    public static boolean isGlobalElement(final IType type) {
        return (type instanceof IStructureType) && ((IStructureType) type).isElement();
    }

    public boolean isNillable() {
        final XSDElementDeclaration element = getElement();
        if (element != null) {
            return element.isNillable();
        }
        return false;
    }

    public XSDElementDeclaration getElement() {
        return getComponent() instanceof XSDElementDeclaration ? (XSDElementDeclaration) getComponent() : null;
    }

    public XSDTypeDefinition getXSDTypeDefinition() {
        final XSDElementDeclaration element = getElement();
        return element == null ? (XSDComplexTypeDefinition) getComponent() : element.getType();
    }

}
