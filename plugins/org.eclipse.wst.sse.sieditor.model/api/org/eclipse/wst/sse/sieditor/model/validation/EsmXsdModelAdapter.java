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
package org.eclipse.wst.sse.sieditor.model.validation;

import java.util.Collection;
import java.util.Stack;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDDiagnostic;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFacet;
import org.eclipse.xsd.XSDFeature;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.INamedObject;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

public class EsmXsdModelAdapter implements IModelAdapter {

    public EObject adapatToEMF(Object source) {
        if (source instanceof INamedObject) {
            if (source instanceof IType) {
                final IType type = (IType) source;
                return type.isAnonymous() ? type.getParent().getComponent() : type.getComponent();
            }

            return ((INamedObject) source).getComponent();
        } else if (source instanceof ISchema) {
            return ((ISchema) source).getComponent();
        }

        return null;
    }

    public IModelObject adaptToModelObject(EObject source) {
        if (!(source instanceof XSDConcreteComponent)) {
            return null;
        }

        if (source instanceof XSDDiagnostic) {
            return adaptToModelObject(((XSDDiagnostic) source).getPrimaryComponent());
        }

        IXSDModelRoot modelRoot = getXSDModelRoot(source);
        if (source instanceof XSDImport) {

            XSDSchema resolvedSchema = ((XSDImport) source).getResolvedSchema();

            if (resolvedSchema != null) {

                ISchema referredSchema = modelRoot.getSchema().getReferredSchema(resolvedSchema);

                return referredSchema;

            }
            return null;

        }

        final XSDConcreteComponent xsdComponent = (XSDConcreteComponent) source;
        final ISchema schema = modelRoot.getSchema();
        final String targetNamespace = schema.getComponent().getTargetNamespace();
        final String componentTargetNamespace = xsdComponent.getSchema().getTargetNamespace();

        if ((targetNamespace != null && componentTargetNamespace == null)
                || (targetNamespace == null && componentTargetNamespace != null)) {
            return null;
        }

        if ((targetNamespace == null && componentTargetNamespace == null) || targetNamespace.equals(componentTargetNamespace)) {
            if (xsdComponent instanceof XSDSchema) {
                return modelRoot.getSchema();
            } else if (xsdComponent instanceof XSDTypeDefinition) {
                return adaptTypeDefiniton(false, (XSDTypeDefinition) xsdComponent);
            } else if (xsdComponent instanceof XSDElementDeclaration && ((XSDElementDeclaration) xsdComponent).isGlobal()) {
                return adaptTypeDefiniton(true, ((XSDElementDeclaration) xsdComponent));
            } else if (xsdComponent instanceof XSDFeature) {
                return adaptXSDFeature((XSDFeature) xsdComponent);
            } else if (xsdComponent instanceof XSDFacet) {
                return adaptXSDFacet((XSDFacet) xsdComponent);
            } else if (xsdComponent instanceof XSDModelGroup) {
                return adaptToParentNamedComponent(xsdComponent);
            } else if (xsdComponent instanceof XSDAttributeUse) {
                return adaptToParentNamedComponent(xsdComponent);
            }
        }

        return null;
    }

    private IModelObject adaptToParentNamedComponent(XSDConcreteComponent xsdComponent) {
        XSDConcreteComponent container = xsdComponent.getContainer();
        while (container != null) {
            if (container instanceof XSDNamedComponent && ((XSDNamedComponent) container).getName() != null) {
                return adaptToModelObject(container);
            }

            container = container.getContainer();
        }

        return null;
    }

    private IModelObject adaptXSDFacet(XSDFacet xsdFacet) {
        return adaptTypeDefiniton(false, xsdFacet.getSimpleTypeDefinition());
    }

    private IModelObject adaptXSDFeature(XSDFeature xsdFeature) {
        IXSDModelRoot modelRoot = getXSDModelRoot(xsdFeature);
        if (xsdFeature.isGlobal()) {
            final IType[] types = ((Schema) modelRoot.getSchema()).getAllTypes(xsdFeature.getName());
            if (types != null) {
                for (IType type : types) {
                    if (type.getComponent() == xsdFeature) {
                        return type;
                    }
                }
            }
        }

        final Stack<XSDNamedComponent> path = new Stack<XSDNamedComponent>();

        XSDConcreteComponent parent = xsdFeature;
        while (!(parent instanceof XSDSchema)) {
            if (parent instanceof XSDNamedComponent) {
                final XSDNamedComponent namedComponent = (XSDNamedComponent) parent;
                if (namedComponent.getName() != null) {
                    path.add(namedComponent);
                } else {
                    if (namedComponent instanceof XSDElementDeclaration) {
                        XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration) namedComponent;
                        if (xsdElementDeclaration.isElementDeclarationReference()) {
                            XSDElementDeclaration resolvedElementDeclaration = xsdElementDeclaration
                                    .getResolvedElementDeclaration();
                            path.add(resolvedElementDeclaration);
                        }
                    }
                }
            }

            parent = parent.getContainer();
        }

        IType type = null;
        if (!path.isEmpty()) {
            XSDNamedComponent namedComponent = path.pop();
            type = modelRoot.getSchema().getType(namedComponent instanceof XSDElementDeclaration, namedComponent.getName());
        }
        return resolveElement((IStructureType) type, xsdFeature, path);
    }

    private IElement resolveElement(IStructureType type, XSDFeature xsdFeature, Stack<XSDNamedComponent> path) {
        if (type == null || path.size() == 0) {
            return null;
        }

        final Collection<IElement> elements = type.getElements(path.pop().getName());
        if (elements.size() == 0) {
            return null;
        }

        // final IElement element = elements.iterator().next();
        for (IElement element : elements) {
            XSDConcreteComponent component = element.getComponent();
            if (component instanceof XSDParticle && ((XSDParticle) component).getContent() == xsdFeature) {
                return element;
            } else if ((component instanceof XSDAttributeDeclaration) && component == xsdFeature) {
                return element;
            }
        }

        IType nextType = elements.iterator().next().getType();
        IStructureType structureType = (IStructureType) nextType;
        return resolveElement(structureType, xsdFeature, path);
    }

    private IModelObject adaptTypeDefiniton(boolean isElementDeclaration, XSDNamedComponent typeDefinition) {
        if (typeDefinition.getName() != null) {
            IXSDModelRoot modelRoot = getXSDModelRoot(typeDefinition);
            IType[] allTypes = modelRoot.getSchema().getAllTypes(typeDefinition.getName());
            if(allTypes == null){
                return adaptToModelObject(typeDefinition.eContainer());
            }
            for (IType iType : allTypes) {
                if (iType.getComponent().equals(typeDefinition)) {
                    return iType;
                }
            }
        }
        return adaptToModelObject(typeDefinition.eContainer());

    }

    private IXSDModelRoot getXSDModelRoot(EObject xsdObject) {
        IXSDModelRoot xsdModelRoot = null;

        if (xsdObject instanceof XSDSchema) {
            return XSDFactory.getInstance().createXSDModelRoot((XSDSchema) xsdObject);
        } else if (xsdObject != null) {
            xsdModelRoot = getXSDModelRoot(xsdObject.eContainer());
        }

        return xsdModelRoot;
    }
}
