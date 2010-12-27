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
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;

import org.eclipse.wst.sse.sieditor.model.reconcile.utils.IXsdReconcileUtils.NamespaceResolverType;

public class XsdElementDeclarationTypeDefinitionResolver extends
        BaseComponentResolver<XSDElementDeclaration, XSDTypeDefinition> {

    private static final XsdElementDeclarationTypeDefinitionResolver INSTANCE = new XsdElementDeclarationTypeDefinitionResolver();

    private XsdElementDeclarationTypeDefinitionResolver() {

    }

    public static XsdElementDeclarationTypeDefinitionResolver instance() {
        return INSTANCE;
    }

    @Override
    public void componentResolved(final XSDElementDeclaration sourceComponent, final XSDTypeDefinition resolvedComponent) {
        sourceComponent.setTypeDefinition(resolvedComponent);
    }

    @Override
    public XSDTypeDefinition resolveConcreteComponent(final XSDElementDeclaration sourceComponent, final XSDSchema xsdSchema,
            final NamespaceResolverType namespaceResolverType) {
        final String targetNamespaceForResolve = getTargetNamespaceForResolve(sourceComponent, xsdSchema, namespaceResolverType);
        final String resolveComponentLocalName = getResolveComponentLocalName(sourceComponent.getElement(), sourceComponent
                .getTypeDefinition());
        return resolveUtils().resolveTypeDefinition(xsdSchema, targetNamespaceForResolve, resolveComponentLocalName);
    }

    @Override
    public boolean shouldProceedWithResolveOf(final XSDElementDeclaration sourceComponent) {
        return sourceComponent.getTypeDefinition() != null;
    }

    private String getTargetNamespaceForResolve(final XSDElementDeclaration sourceComponent, final XSDSchema xsdSchema,
            final NamespaceResolverType namespaceResolverType) {
        return (namespaceResolverType == NamespaceResolverType.ELEMENT) ? sourceComponent.getTypeDefinition()
                .getTargetNamespace() : xsdSchema.getTargetNamespace();
    }

    @Override
    public String getResolveComponentAttributeValue(final XSDElementDeclaration sourceComponent) {
        return sourceComponent.getElement().getAttribute(XSDConstants.TYPE_ATTRIBUTE);
    }

}
