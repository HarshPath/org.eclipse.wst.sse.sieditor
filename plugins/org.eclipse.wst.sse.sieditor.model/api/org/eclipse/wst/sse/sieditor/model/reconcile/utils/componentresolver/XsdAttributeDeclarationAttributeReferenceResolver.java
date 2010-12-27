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

import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;

import org.eclipse.wst.sse.sieditor.model.reconcile.utils.IXsdReconcileUtils.NamespaceResolverType;

public class XsdAttributeDeclarationAttributeReferenceResolver extends
        BaseComponentResolver<XSDAttributeDeclaration, XSDAttributeDeclaration> {

    private static final XsdAttributeDeclarationAttributeReferenceResolver INSTANCE = new XsdAttributeDeclarationAttributeReferenceResolver();

    private XsdAttributeDeclarationAttributeReferenceResolver() {

    }

    public static XsdAttributeDeclarationAttributeReferenceResolver instance() {
        return INSTANCE;
    }

    @Override
    public void componentResolved(final XSDAttributeDeclaration sourceComponent, final XSDAttributeDeclaration resolvedComponent) {
        sourceComponent.setResolvedAttributeDeclaration(resolvedComponent);
    }

    @Override
    public XSDAttributeDeclaration resolveConcreteComponent(final XSDAttributeDeclaration sourceComponent,
            final XSDSchema xsdSchema, final NamespaceResolverType namespaceResolverType) {
        return resolveUtils().resolveAttributeDeclaration(xsdSchema,
                getTargetNamespaceForResolve(sourceComponent, xsdSchema, namespaceResolverType),
                getResolveComponentLocalName(sourceComponent));
    }

    @Override
    public String getResolveComponentAttributeValue(final XSDAttributeDeclaration sourceComponent) {
        return sourceComponent.getElement().getAttribute(XSDConstants.REF_ATTRIBUTE);
    }

    private String getTargetNamespaceForResolve(final XSDAttributeDeclaration sourceComponent, final XSDSchema xsdSchema,
            final NamespaceResolverType namespaceResolverType) {
        return (namespaceResolverType == NamespaceResolverType.ELEMENT) ? sourceComponent.getResolvedAttributeDeclaration()
                .getTargetNamespace() : xsdSchema.getTargetNamespace();
    }

    private String getResolveComponentLocalName(final XSDAttributeDeclaration sourceComponent) {
        return sourceComponent.getResolvedAttributeDeclaration().getName();
    }

    @Override
    public boolean shouldProceedWithResolveOf(final XSDAttributeDeclaration sourceComponent) {
        return sourceComponent.isAttributeDeclarationReference();
    }

}
