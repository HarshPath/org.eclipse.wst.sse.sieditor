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
 *    Stanislav Nichev - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.model.reconcile.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.wst.wsdl.XSDSchemaExtensibilityElement;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.impl.XSDImportImpl;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.command.emf.common.utils.UpdateNSPrefixUtils;
import org.eclipse.wst.sse.sieditor.model.reconcile.IResolveUtils;
import org.eclipse.wst.sse.sieditor.model.reconcile.ResolveUtils;
import org.eclipse.wst.sse.sieditor.model.reconcile.utils.componentresolver.IConcreteComponentResolver;
import org.eclipse.wst.sse.sieditor.model.reconcile.utils.componentresolver.XsdAttributeDeclarationAttributeReferenceResolver;
import org.eclipse.wst.sse.sieditor.model.reconcile.utils.componentresolver.XsdAttributeDeclarationTypeDefinitionResolver;
import org.eclipse.wst.sse.sieditor.model.reconcile.utils.componentresolver.XsdComplexTypeDefinitionExtensionResolver;
import org.eclipse.wst.sse.sieditor.model.reconcile.utils.componentresolver.XsdComplexTypeDefinitionRestrictionResolver;
import org.eclipse.wst.sse.sieditor.model.reconcile.utils.componentresolver.XsdElementDeclarationElementReferenceResolver;
import org.eclipse.wst.sse.sieditor.model.reconcile.utils.componentresolver.XsdElementDeclarationTypeDefinitionResolver;

/**
 * This is the default implementation of the {@link IXsdReconcileUtils}
 * interface.
 * 
 */
public class XsdReconcileUtils implements IXsdReconcileUtils {

    /**
     * During the resolving of elements, we are checking xsd:imports and
     * xsd:includes. This constant tells how "deep" we want to go searching for
     * elements for resolve.
     */
    private static final int SEARCH_LEVEL_DEPTH = 3;

    private static final IXsdReconcileUtils INSTANCE = new XsdReconcileUtils();

    private XsdReconcileUtils() {

    }

    public static IXsdReconcileUtils instance() {
        return INSTANCE;
    }

    // =========================================================
    // reconcile schema contents
    // =========================================================

    @Override
    public void reconcileSchemaContents(final XSDSchema schema, final ObjectsForResolveContainer container) {
        reconcileSchemaContentsInternal(schema, schema.getQNamePrefixToNamespaceMap(), container, new HashSet<XSDSchema>(), 0,
                NamespaceResolverType.ELEMENT);
        reconcileSchemaContentsInternal(schema, schema.getQNamePrefixToNamespaceMap(), container, new HashSet<XSDSchema>(), 0,
                NamespaceResolverType.SCHEMA);
    }

    private void reconcileSchemaContentsInternal(final XSDSchema schema, final Map<String, String> prefixesMap,
            final ObjectsForResolveContainer container, final Set<XSDSchema> visitedSchemas, final int currentSearchLevel,
            final NamespaceResolverType namespaceResolverType) {

        if (currentSearchLevel >= SEARCH_LEVEL_DEPTH || visitedSchemas.contains(schema)) {
            return;
        }

        if (currentSearchLevel != 0 && schema.getSchemaLocation() == null && schema.eContainer() == null) {
            return;
        }

        if (!container.getElementsForReferenceResolve().isEmpty()) {
            reconcileConcreteComponents(schema, prefixesMap, container.getElementsForReferenceResolve(),
                    XsdElementDeclarationElementReferenceResolver.instance(), namespaceResolverType);
        }
        if (!container.getElementsForTypeResolve().isEmpty()) {
            reconcileConcreteComponents(schema, prefixesMap, container.getElementsForTypeResolve(),
                    XsdElementDeclarationTypeDefinitionResolver.instance(), namespaceResolverType);
        }
        if (!container.getAttributesForReferenceResolve().isEmpty()) {
            reconcileConcreteComponents(schema, prefixesMap, container.getAttributesForReferenceResolve(),
                    XsdAttributeDeclarationAttributeReferenceResolver.instance(), namespaceResolverType);
        }
        if (!container.getAttributesForTypeResolve().isEmpty()) {
            reconcileConcreteComponents(schema, prefixesMap, container.getAttributesForTypeResolve(),
                    XsdAttributeDeclarationTypeDefinitionResolver.instance(), namespaceResolverType);
        }
        if (!container.getComplexTypesForExtensionResolve().isEmpty()) {
            reconcileConcreteComponents(schema, prefixesMap, container.getComplexTypesForExtensionResolve(),
                    XsdComplexTypeDefinitionExtensionResolver.instance(), namespaceResolverType);
        }
        if (!container.getComplexTypesForRestrictionResolve().isEmpty()) {
            reconcileConcreteComponents(schema, prefixesMap, container.getComplexTypesForRestrictionResolve(),
                    XsdComplexTypeDefinitionRestrictionResolver.instance(), namespaceResolverType);
        }

        visitedSchemas.add(schema);

        for (final XSDSchemaContent content : schema.getContents()) {
            if (container.areSchemaContentsCollectionsEmpty()) {
                return;
            }
            if (content instanceof XSDImport) {
                final XSDImport xsdImport = (XSDImport) content;
                XSDSchema resolvedSchema = xsdImport.getResolvedSchema();
                if (resolvedSchema == null || resolvedSchema.eContainer() instanceof XSDSchemaExtensibilityElement
                        && resolvedSchema.eContainer().eContainer() == null) {
                    ((XSDImportImpl) xsdImport).reset();
                    resolvedSchema = ((XSDImportImpl) xsdImport).importSchema();
                }
                if (resolvedSchema != null) {
                    reconcileSchemaContentsInternal(resolvedSchema, prefixesMap, container, visitedSchemas,
                            currentSearchLevel + 1, namespaceResolverType);
                }
            }
            if (content instanceof XSDInclude) {
                final XSDInclude xsdInclude = (XSDInclude) content;
                final XSDSchema resolvedSchema = xsdInclude.getResolvedSchema();
                if (resolvedSchema != null) {
                    reconcileSchemaContentsInternal(resolvedSchema, prefixesMap, container, visitedSchemas,
                            currentSearchLevel + 1, namespaceResolverType);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void reconcileConcreteComponents(final XSDSchema xsdSchema, Map<String, String> prefixesMap,
            final java.util.List<XSDNamedComponent> componentsForResolve, final IConcreteComponentResolver resolver,
            final NamespaceResolverType namespaceResolverType) {

        final List<XSDNamedComponent> reconciledConcreteComponents = new LinkedList<XSDNamedComponent>();

        for (final XSDNamedComponent sourceComponent : componentsForResolve) {
            if (!resolver.shouldProceedWithResolveOf(sourceComponent)) {
                continue;
            }

            final XSDNamedComponent resolvedComponent = resolver.resolveConcreteComponent(sourceComponent, xsdSchema,
                    namespaceResolverType);

            if (resolvedComponent != null) {
                final String attributeValue = resolver.getResolveComponentAttributeValue(sourceComponent);
                if (attributeValue == null || attributeValue.isEmpty()) {
                    continue;
                }
                final String prefix = prefixUtils().extractPrefixFromQName(attributeValue);
                String tnsForPrefix = prefixesMap.get(prefix);

                if (resolvedComponent.getTargetNamespace() != null
                        && !resolvedComponent.getTargetNamespace().equals(tnsForPrefix)) {
                    // double check prefixes
                    prefixesMap = sourceComponent.getSchema().getQNamePrefixToNamespaceMap();
                    tnsForPrefix = prefixesMap.get(prefix);
                }

                if (isElementCorrectlyResolved(xsdSchema, prefixesMap, xsdSchema.getElement(), resolvedComponent
                        .getTargetNamespace(), prefix, tnsForPrefix)) {
                    resolver.componentResolved(sourceComponent, resolvedComponent);
                    reconciledConcreteComponents.add(sourceComponent);
                }
            }
        }

        componentsForResolve.removeAll(reconciledConcreteComponents);
    };

    // =========================================================
    // helpers
    // =========================================================

    protected IResolveUtils resolveUtils() {
        return ResolveUtils.instance();
    }

    /**
     * Utility method. Checks if the prefix is an actual and valid one, so that
     * we can safely set the new reference.
     */
    private boolean isElementCorrectlyResolved(final XSDSchema schema, final Map<String, String> prefixesMap,
            final Element element, final String resolvedTargetNamespace, final String prefix, final String tnsForPrefix) {

        final boolean targetNamespacesAreTheSame = (tnsForPrefix != null && tnsForPrefix.equals(resolvedTargetNamespace));

        // this is used to resolve referred elements from importing schemas
        final boolean prefixValueMatchesResolvedTargetNamespace = (prefixesMap.get(prefix) != null && prefixesMap.get(prefix)
                .equals(resolvedTargetNamespace));

        return targetNamespacesAreTheSame && (/*
                                               * element.getAttribute(tnsPrefixAttribute
                                               * (prefix)) != null ||
                                               */prefixValueMatchesResolvedTargetNamespace);
    }

    // /**
    // * Utility method. It builds the tns prefix attribute for the given
    // prefix.
    // * If the prefix is some custom prefix (for e.g. "ns1"), it returns
    // "xmlns:"
    // * + "ns1". <br>
    // * <br>
    // * If the prefix is the default one, it simply returns it.
    // */
    // private String tnsPrefixAttribute(final String prefix) {
    // String prefixAttribute = null;
    // if (EmfXsdUtils.XMLNS_PREFIX.equals(prefix)) {
    // prefixAttribute = EmfXsdUtils.XMLNS_PREFIX;
    // } else {
    // prefixAttribute = EmfXsdUtils.XMLNS_PREFIX + ':' + prefix;
    // }
    // return prefixAttribute;
    // }

    private UpdateNSPrefixUtils prefixUtils() {
        return UpdateNSPrefixUtils.instance();
    }

}
