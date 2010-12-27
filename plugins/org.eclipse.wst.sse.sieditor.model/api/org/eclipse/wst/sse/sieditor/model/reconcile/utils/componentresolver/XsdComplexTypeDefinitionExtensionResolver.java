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

import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.command.emf.common.utils.RemapReferencesUtils;
import org.eclipse.wst.sse.sieditor.model.reconcile.utils.IXsdReconcileUtils.NamespaceResolverType;

public class XsdComplexTypeDefinitionExtensionResolver extends BaseComponentResolver<XSDComplexTypeDefinition, XSDTypeDefinition> {

    private static final XsdComplexTypeDefinitionExtensionResolver INSTANCE = new XsdComplexTypeDefinitionExtensionResolver();

    XsdComplexTypeDefinitionExtensionResolver() {

    }

    public static XsdComplexTypeDefinitionExtensionResolver instance() {
        return INSTANCE;
    }

    protected String getDerivationMethod() {
        return XSDConstants.EXTENSION_ELEMENT_TAG;
    }

    @Override
    public void componentResolved(final XSDComplexTypeDefinition sourceComponent, final XSDTypeDefinition resolvedComponent) {
        final XSDComplexTypeContent content = sourceComponent.getContent();
        if (content instanceof XSDSimpleTypeDefinition && resolvedComponent instanceof XSDSimpleTypeDefinition) {
            ((XSDSimpleTypeDefinition) content).setBaseTypeDefinition((XSDSimpleTypeDefinition) resolvedComponent);
        }
        sourceComponent.setBaseTypeDefinition(resolvedComponent);
    }

    @Override
    public String getResolveComponentAttributeValue(final XSDComplexTypeDefinition sourceComponent) {
        return getResolveComponentAttributeValueInternal(sourceComponent, getDerivationMethod());
    }

    @Override
    public XSDTypeDefinition resolveConcreteComponent(final XSDComplexTypeDefinition sourceComponent, final XSDSchema xsdSchema,
            final NamespaceResolverType namespaceResolverType) {
        return resolveUtils().resolveSimpleTypeDefinition(xsdSchema,
                getTargetNamespaceForResolve(sourceComponent, xsdSchema, namespaceResolverType),
                getResolveComponentLocalName(sourceComponent, getDerivationMethod()));
    }

    @Override
    public boolean shouldProceedWithResolveOf(final XSDComplexTypeDefinition sourceComponent) {
        final Element content = remapUtils().getComplexTypeContentElement(sourceComponent.getElement());
        if (content == null) {
            return false;
        }
        final Element extension = remapUtils().getComplexTypeContainingElement(content, getDerivationMethod());
        return sourceComponent.getBaseTypeDefinition() != null && extension != null
                && extension.getAttribute(XSDConstants.BASE_ATTRIBUTE) != null;
    }

    private String getTargetNamespaceForResolve(final XSDComplexTypeDefinition sourceComponent, final XSDSchema xsdSchema,
            final NamespaceResolverType namespaceResolverType) {
        return (namespaceResolverType == NamespaceResolverType.ELEMENT) ? sourceComponent.getBaseTypeDefinition()
                .getTargetNamespace() : xsdSchema.getTargetNamespace();
    }

    private static String getResolveComponentAttributeValueInternal(final XSDComplexTypeDefinition sourceComponent,
            final String derivationMethod) {
        final Element content = RemapReferencesUtils.instance().getComplexTypeContentElement(sourceComponent.getElement());
        final Element extension = RemapReferencesUtils.instance().getComplexTypeContainingElement(content, derivationMethod);
        return extension == null ? null : extension.getAttribute(XSDConstants.BASE_ATTRIBUTE);
    }

    public static String getResolveComponentLocalName(final XSDComplexTypeDefinition sourceComponent,
            final String derivationMethod) {
        String localName = sourceComponent.getBaseTypeDefinition().getName();
        if (localName == null) {
            final String typeQName = getResolveComponentAttributeValueInternal(sourceComponent, derivationMethod);
            if (typeQName != null) {
                localName = typeQName.substring(typeQName.indexOf(':') + 1);
            }
        }
        return localName;
    }

}
