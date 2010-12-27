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
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;

import org.eclipse.wst.sse.sieditor.model.reconcile.utils.IXsdReconcileUtils.NamespaceResolverType;

public class XsdAttributeDeclarationTypeDefinitionResolver extends
        BaseComponentResolver<XSDAttributeDeclaration, XSDSimpleTypeDefinition> {

    private static final XsdAttributeDeclarationTypeDefinitionResolver INSTANCE = new XsdAttributeDeclarationTypeDefinitionResolver();

    private XsdAttributeDeclarationTypeDefinitionResolver() {

    }

    public static XsdAttributeDeclarationTypeDefinitionResolver instance() {
        return INSTANCE;
    }

    @Override
    public void componentResolved(final XSDAttributeDeclaration sourceComponent, final XSDSimpleTypeDefinition resolvedComponent) {
        sourceComponent.setTypeDefinition(resolvedComponent);
    }

    @Override
    public XSDSimpleTypeDefinition resolveConcreteComponent(final XSDAttributeDeclaration sourceComponent,
            final XSDSchema xsdSchema, final NamespaceResolverType namespaceResolverType) {
        final String targetNamespaceForResolve = getTargetNamespaceForResolve(sourceComponent, xsdSchema, namespaceResolverType);
        final String resolveComponentLocalName = getResolveComponentLocalName(sourceComponent.getElement(), sourceComponent
                .getTypeDefinition());
        return resolveUtils().resolveSimpleTypeDefinition(xsdSchema, targetNamespaceForResolve, resolveComponentLocalName);
    }

    @Override
    public String getResolveComponentAttributeValue(final XSDAttributeDeclaration sourceComponent) {
        return sourceComponent.getElement().getAttribute(XSDConstants.TYPE_ATTRIBUTE);
    }

    private String getTargetNamespaceForResolve(final XSDAttributeDeclaration sourceComponent, final XSDSchema xsdSchema,
            final NamespaceResolverType namespaceResolverType) {
        return (namespaceResolverType == NamespaceResolverType.ELEMENT) ? sourceComponent.getTypeDefinition()
                .getTargetNamespace() : xsdSchema.getTargetNamespace();
    }

    @Override
    public boolean shouldProceedWithResolveOf(final XSDAttributeDeclaration sourceComponent) {
        return sourceComponent.getTypeDefinition() != null;
    }

}
