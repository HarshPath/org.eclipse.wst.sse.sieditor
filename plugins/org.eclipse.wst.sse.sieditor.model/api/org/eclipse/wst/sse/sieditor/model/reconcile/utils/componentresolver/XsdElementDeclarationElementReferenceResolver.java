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
package org.eclipse.wst.sse.sieditor.model.reconcile.utils.componentresolver;

import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;

import org.eclipse.wst.sse.sieditor.model.reconcile.utils.IXsdReconcileUtils.NamespaceResolverType;

public class XsdElementDeclarationElementReferenceResolver extends
        BaseComponentResolver<XSDElementDeclaration, XSDElementDeclaration> {

    private static final XsdElementDeclarationElementReferenceResolver INSTANCE = new XsdElementDeclarationElementReferenceResolver();

    private XsdElementDeclarationElementReferenceResolver() {

    }

    public static XsdElementDeclarationElementReferenceResolver instance() {
        return INSTANCE;
    }

    @Override
    public void componentResolved(final XSDElementDeclaration sourceComponent, final XSDElementDeclaration resolvedComponent) {
        sourceComponent.setResolvedElementDeclaration(resolvedComponent);
    }

    @Override
    public String getResolveComponentAttributeValue(final XSDElementDeclaration sourceComponent) {
        return sourceComponent.getElement().getAttribute(XSDConstants.REF_ATTRIBUTE);
    }

    @Override
    public XSDElementDeclaration resolveConcreteComponent(final XSDElementDeclaration sourceComponent, final XSDSchema xsdSchema,
            final NamespaceResolverType namespaceResolverType) {
        return resolveUtils().resolveElementDeclaration(xsdSchema,
                getTargetNamespaceForResolve(sourceComponent, xsdSchema, namespaceResolverType),
                getResolveComponentLocalName(sourceComponent));
    }

    private String getTargetNamespaceForResolve(final XSDElementDeclaration sourceComponent, final XSDSchema xsdSchema,
            final NamespaceResolverType namespaceResolverType) {
        return (namespaceResolverType == NamespaceResolverType.ELEMENT) ? sourceComponent.getResolvedElementDeclaration()
                .getTargetNamespace() : xsdSchema.getTargetNamespace();
    }

    private String getResolveComponentLocalName(final XSDElementDeclaration sourceComponent) {
        return sourceComponent.getResolvedElementDeclaration().getName();
    }

    @Override
    public boolean shouldProceedWithResolveOf(final XSDElementDeclaration sourceComponent) {
        return sourceComponent.isElementDeclarationReference();
    }

}
