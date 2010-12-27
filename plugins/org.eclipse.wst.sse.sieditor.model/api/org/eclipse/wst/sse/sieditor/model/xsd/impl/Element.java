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
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupContent;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDAttributeUseCategory;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.emf.xsd.RenameElementCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetAnonymousCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetElementNillableCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetElementOccurences;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetElementTypeCommand;
import org.eclipse.wst.sse.sieditor.core.common.Nil;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.generic.IllegalInputException;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class Element extends AbstractXSDComponent implements IElement,
        org.eclipse.wst.sse.sieditor.model.write.xsd.api.IElement {

    private final XSDConcreteComponent _component;
    private final XSDModelGroup _container;
    private final IModelObject _parent;
    private final Schema _schema;

    public Element(final IXSDModelRoot modelRoot, final XSDConcreteComponent element, final XSDModelGroup container,
            final IModelObject parent, final Schema schema) {
        super(modelRoot);
        Nil.checkNil(element, "element"); //$NON-NLS-1$
        if (!(element instanceof XSDAttributeDeclaration))
            Nil.checkNil(container, "container"); //$NON-NLS-1$
        Nil.checkNil(parent, "parent"); //$NON-NLS-1$
        Nil.checkNil(schema, "schema"); //$NON-NLS-1$

        // Element can be one of XSDElementDeclaration or
        // XSDAttributeDeclaration
        if (!((element instanceof XSDParticle && ((XSDParticle) element).getContent() instanceof XSDElementDeclaration) || element instanceof XSDAttributeDeclaration)) {
            throw new RuntimeException("element has to be one of XSDParticle or XSDAttributeDeclaration"); //$NON-NLS-1$
        }

        this._component = element;
        this._container = container;
        this._parent = parent;
        this._schema = schema;
    }

    public String getNamespace() {
        return _schema.getNamespace();
    }

    public int getMaxOccurs() {
        if (_component instanceof XSDAttributeDeclaration) {
            final XSDAttributeUseCategory use = ((XSDAttributeUse) _component.getContainer()).getUse();
            // 1 if required
            if (XSDAttributeUseCategory.PROHIBITED_LITERAL.equals(use))
                return 0;
            // 0 if attribute is prohibited or optional
            return 1;
        }
        return ((XSDParticle) _component).getMaxOccurs();
    }

    public int getMinOccurs() {
        if (_component instanceof XSDAttributeDeclaration) {
            final XSDAttributeUseCategory use = ((XSDAttributeUse) _component.getContainer()).getUse();
            // 1 if required
            if (XSDAttributeUseCategory.REQUIRED_LITERAL.equals(use))
                return 1;
            // 0 if attribute is prohibited or optional
            return 0;
        }
        return ((XSDParticle) _component).getMinOccurs();
    }

    public boolean getNillable() {
        if (_component instanceof XSDAttributeDeclaration)
            return false;
        final XSDParticle particle = (XSDParticle) _component;
        return ((XSDElementDeclaration) particle.getContent()).isNillable();
    }

    public IType getType() {
        if (_component instanceof XSDAttributeDeclaration) {
            final XSDAttributeDeclaration attribute = (XSDAttributeDeclaration) _component;
            if (attribute.isAttributeDeclarationReference()) {
                return null;
            }
            XSDSimpleTypeDefinition type = attribute.getAnonymousTypeDefinition();
            if (null != type) {
                // the type is anonymous
                return new SimpleType(getModelRoot(), _schema, this, type);
            }
            type = attribute.getTypeDefinition();
            return null == type ? null : (IType) _schema.resolveComponent(type, true);
        } else if (_component instanceof XSDParticle) {
            final XSDParticle particle = (XSDParticle) _component;
            if (!(particle.getContent() instanceof XSDElementDeclaration))
                throw new RuntimeException("Element content should be XSDElementDeclaration");//$NON-NLS-1$
            final XSDElementDeclaration element = (XSDElementDeclaration) particle.getContent();
            if (element.isElementDeclarationReference()) {
                final XSDElementDeclaration resolvedElement = element.getResolvedElementDeclaration();
                return (AbstractType) (null == resolvedElement ? null : _schema.resolveComponent(resolvedElement, true));
            }
            XSDTypeDefinition type = element.getAnonymousTypeDefinition();
            if (null != type) {
                // the type is anonymous
                if (type instanceof XSDComplexTypeDefinition) {

                    return new StructureType(getModelRoot(), _schema, this, type);
                }
                return new SimpleType(getModelRoot(), _schema, this, (XSDSimpleTypeDefinition) type);
            }
            type = element.getTypeDefinition();
            return (IType) _schema.resolveComponent(type, true);
        } else {
            throw new RuntimeException("Component must be XSDElementDeclartion or XSDAttributeDeclaration"); //$NON-NLS-1$
        } 
    }

    public String getName() {
        if (_component instanceof XSDAttributeDeclaration) {
            final XSDAttributeDeclaration attribute = (XSDAttributeDeclaration) _component;
            if (attribute.isAttributeDeclarationReference())
                return null == attribute.getResolvedAttributeDeclaration() ? null : attribute.getResolvedAttributeDeclaration()
                        .getName();
            return attribute.getName();
        } else if (_component instanceof XSDParticle) {
            final XSDElementDeclaration component = (XSDElementDeclaration) ((XSDParticle) _component).getContent();
            if (component.isElementDeclarationReference())
                return null == component.getResolvedElementDeclaration() ? null : component.getResolvedElementDeclaration()
                        .getName();
            return component.getName();
        } else
            throw new RuntimeException("Component must be XSDElementDeclaration or XSDAttributeDeclaration"); //$NON-NLS-1$
    }

    public boolean isAttribute() {
        return _component instanceof XSDAttributeDeclaration;
    }

    public String getDocumentation() {
        final List<XSDAnnotation> annotations = new ArrayList<XSDAnnotation>(1);
        final XSDAnnotation annotation;
        if (_component instanceof XSDParticle) {
            final XSDElementDeclaration content = (XSDElementDeclaration) ((XSDParticle) _component).getContent();
            annotation = content.getAnnotation();
        } else {
            annotation = ((XSDAttributeDeclaration) _component).getAnnotation();
        }
        if (null != annotation)
            annotations.add(annotation);
        if (annotations.size() == 0)
            return ""; //$NON-NLS-1$
        final org.w3c.dom.Element documentation = getFirstElement(annotations);
        return super.getDocumentation(documentation);
    }

    public IModelObject getParent() {
        return _parent;
    }

    public IType setAnonymousType(final boolean isSimple) throws ExecutionException {
        if (!isSimple && _component instanceof XSDAttributeDeclaration)
            return null;
        final SetAnonymousCommand command = new SetAnonymousCommand(getModelRoot(), this, (AbstractType) getType(), isSimple);
        getModelRoot().getEnv().execute(command);

        return command.getType();
    }

    public void setMaxOccurs(final int value) throws ExecutionException {
        if (_component instanceof XSDParticle) {
            final SetElementOccurences command = new SetElementOccurences(getModelRoot(), this, getMinOccurs(), value);
            getModelRoot().getEnv().execute(command);
        }
    }

    public void setMinOccurs(final int value) throws ExecutionException {
        if (_component instanceof XSDParticle) {
            final SetElementOccurences command = new SetElementOccurences(getModelRoot(), this, value, getMaxOccurs());
            getModelRoot().getEnv().execute(command);
        }
    }

    public void setNillable(final boolean nillable) throws ExecutionException {
        if (_component instanceof XSDParticle) {
            final XSDParticleContent content = ((XSDParticle) _component).getContent();
            if (content instanceof XSDElementDeclaration) {
                final SetElementNillableCommand command = new SetElementNillableCommand(getModelRoot(), this, nillable);
                getModelRoot().getEnv().execute(command);
            }
        }
    }

    public void setType(final IType type) throws ExecutionException {
        IType resolvedType = null;
        if (type instanceof AbstractType)
            resolvedType = _schema.resolveType((AbstractType) type);
        // DOIT Import if needed
        if (null != resolvedType) {
            final SetElementTypeCommand command = new SetElementTypeCommand(getModelRoot(), this, resolvedType);
            getModelRoot().getEnv().execute(command);

        }
    }

    public void setName(final String name) throws IllegalInputException, ExecutionException {
        Nil.checkNil(name, "name"); //$NON-NLS-1$
        if (!EmfXsdUtils.isValidNCName(name))
            throw new IllegalInputException("Entered Element name is not valid"); //$NON-NLS-1$
        final RenameElementCommand cmd = new RenameElementCommand(getModelRoot(), this, name);
        getModelRoot().getEnv().execute(cmd);
    }

    public void delete() {
        if (_component instanceof XSDParticle) {
            _container.getContents().remove(_component);
        } else if (_component instanceof XSDAttributeDeclaration) {
            if (_parent instanceof StructureType) {
                final XSDNamedComponent component = (XSDNamedComponent) _parent.getComponent();
                if (component instanceof XSDComplexTypeDefinition) {
                    for (final XSDAttributeGroupContent attrContent : new ArrayList<XSDAttributeGroupContent>(
                            ((XSDComplexTypeDefinition) component).getAttributeContents())) {
                        if ((attrContent instanceof XSDAttributeUse)
                                && _component.equals(((XSDAttributeUse) attrContent).getContent())) {
                            ((XSDComplexTypeDefinition) component).getAttributeContents().remove(attrContent);
                        }
                    }
                }
            }
        }
    }

    @Override
    public XSDConcreteComponent getComponent() {
        return _component;
    }

    public Schema getSchema() {
        return _schema;
    }

    public XSDModelGroup getContainer() {
        return _container;
    }

}
