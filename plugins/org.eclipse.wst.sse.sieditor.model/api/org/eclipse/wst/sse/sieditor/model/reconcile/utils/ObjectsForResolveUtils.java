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
package org.eclipse.wst.sse.sieditor.model.reconcile.utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.command.emf.common.utils.UpdateNSPrefixUtils;
import org.eclipse.wst.sse.sieditor.model.reconcile.utils.searchresultprocessor.IXsdSearchResultProcessor;
import org.eclipse.wst.sse.sieditor.model.reconcile.utils.searchresultprocessor.XsdAttributeDeclarationSearchResultProcessor;
import org.eclipse.wst.sse.sieditor.model.reconcile.utils.searchresultprocessor.XsdElementDeclarationSearchResultProcessor;
import org.eclipse.wst.sse.sieditor.model.utils.ElementAttributeUtils;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;

public class ObjectsForResolveUtils {

    private static final ObjectsForResolveUtils INSTANCE = new ObjectsForResolveUtils();

    private ObjectsForResolveUtils() {

    }

    public static ObjectsForResolveUtils instance() {
        return INSTANCE;
    }

    // =========================================================
    // unresolved elements search helper
    // =========================================================

    public ObjectsForResolveContainer findObjectsForResolve(final EObject eObject, final List<XSDSchema> allSchemas) {
        final List<EObject> visited = new LinkedList<EObject>();
        return findObjectsForResolveInternal(eObject, allSchemas, visited);
    }

    /**
     * Utility internal method. Returns container with all the elements, which
     * need reconciling.
     */
    private ObjectsForResolveContainer findObjectsForResolveInternal(final EObject eObject, final List<XSDSchema> allSchemas,
            final List<EObject> visited) {
        final ObjectsForResolveContainer container = new ObjectsForResolveContainer();

        final Iterator<EObject> eAllContents = eObject.eContents().iterator();
        while (eAllContents.hasNext()) {
            final EObject eContent = eAllContents.next();

            if (visited.contains(eContent)) {
                continue;
            }

            processXsdElementDeclaration(container, allSchemas, eContent);
            processXsdAttributeDeclaration(container, allSchemas, eContent);
            processComplexTypeDefinition(container, allSchemas, eContent);
            processWsdlOperation(container, eContent);
            processWsdlMessage(container, eContent);
            processWsdlMessagePart(container, allSchemas, eContent);
            // TODO: needs to resolve XsdAttributeGroupDefinition &&
            // XsdModelGroupDefinition

            visited.add(eContent);
            final ObjectsForResolveContainer childsContainer = findObjectsForResolveInternal(eContent, allSchemas, visited);
            container.addAll(childsContainer);
        }
        return container;
    }

    // =========================================================
    // findObjectsForResolve helpers
    // =========================================================

    private void processXsdElementDeclaration(final ObjectsForResolveContainer container, final List<XSDSchema> allSchemas,
            final EObject eContent) {
        if (!(eContent instanceof XSDElementDeclaration)) {
            return;
        }
        processXsdNamedDeclaration(allSchemas, (XSDElementDeclaration) eContent, XsdElementDeclarationSearchResultProcessor
                .instance(), container);
    }

    private void processXsdAttributeDeclaration(final ObjectsForResolveContainer container, final List<XSDSchema> allSchemas,
            final EObject eContent) {
        if (!(eContent instanceof XSDAttributeDeclaration)) {
            return;
        }
        processXsdNamedDeclaration(allSchemas, (XSDAttributeDeclaration) eContent, XsdAttributeDeclarationSearchResultProcessor
                .instance(), container);
    }

    private <T extends XSDNamedComponent> void processXsdNamedDeclaration(final List<XSDSchema> allSchemas,
            final T xsdNamedDeclaration, final IXsdSearchResultProcessor<T> xsdSearchResultProcessor,
            final ObjectsForResolveContainer container) {

        if (xsdSearchResultProcessor.isReference(xsdNamedDeclaration)) {
            final T resolvedDeclaration = xsdSearchResultProcessor.getReference(xsdNamedDeclaration);

            final boolean prefixAndNamespaceValid = areDestinationSchemaPrefixAndNamespaceValid(xsdNamedDeclaration.getSchema(),
                    xsdNamedDeclaration.getElement(), XSDConstants.REF_ATTRIBUTE, resolvedDeclaration.getTargetNamespace());

            if (!prefixAndNamespaceValid) {
                xsdSearchResultProcessor.getResolveCollection(xsdNamedDeclaration, container).add(xsdNamedDeclaration);
            }

            if (resolvedDeclaration.eContainer() == null
                    || !isContainerXsdSchemaValid(allSchemas, xsdNamedDeclaration, resolvedDeclaration.getSchema(),
                            resolvedDeclaration.getTargetNamespace())) {
                xsdSearchResultProcessor.getResolveCollection(xsdNamedDeclaration, container).add(xsdNamedDeclaration);
            }

        } else {
            final XSDTypeDefinition typeDefinition = xsdSearchResultProcessor.getTypeDefinition(xsdNamedDeclaration);

            final boolean prefixAndNamespaceValid = areDestinationSchemaPrefixAndNamespaceValid(xsdNamedDeclaration.getSchema(),
                    xsdNamedDeclaration.getElement(), XSDConstants.TYPE_ATTRIBUTE, typeDefinition != null ? typeDefinition
                            .getTargetNamespace() : null);

            if (!prefixAndNamespaceValid) {
                xsdSearchResultProcessor.getResolveCollection(xsdNamedDeclaration, container).add(xsdNamedDeclaration);
            }

            if (typeDefinition != null
                    && typeDefinition.eContainer() == null
                    || !isContainerXsdSchemaValid(allSchemas, xsdNamedDeclaration, typeDefinition.getSchema(), typeDefinition
                            .getTargetNamespace())) {
                if (!EmfXsdUtils.isSchemaForSchemaNS(typeDefinition.getTargetNamespace())) {
                    xsdSearchResultProcessor.getResolveCollection(xsdNamedDeclaration, container).add(xsdNamedDeclaration);
                }
            }
        }
    }

    private void processComplexTypeDefinition(final ObjectsForResolveContainer container, final List<XSDSchema> allSchemas,
            final EObject eContent) {
        if (!(eContent instanceof XSDComplexTypeDefinition)) {
            return;
        }
        final XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) eContent;
        final XSDTypeDefinition baseTypeDefinition = complexType.getBaseTypeDefinition();

        // destinationSchemaPrefixAndNamespaceAreValid(complexType.getSchema(),
        // complexType.getElement(),
        // XSDConstants.BASE_ATTRIBUTE);

        if (baseTypeDefinition != null
                && baseTypeDefinition.eContainer() == null
                || !isContainerXsdSchemaValid(allSchemas, complexType, baseTypeDefinition.getSchema(), baseTypeDefinition
                        .getTargetNamespace())) {
            container.getComplexTypesForExtensionResolve().add(complexType);
            container.getComplexTypesForRestrictionResolve().add(complexType);
        }
    }

    private void processWsdlOperation(final ObjectsForResolveContainer container, final EObject eContent) {
        if (!(eContent instanceof Operation)) {
            return;
        }
        final Operation operation = (Operation) eContent;
        container.getOperationsForResolve().add(operation);
    }

    private void processWsdlMessage(final ObjectsForResolveContainer container, final EObject eContent) {
        if (!(eContent instanceof Message)) {
            return;
        }
        final Message message = (Message) eContent;
        container.getMessagesForResolve().add(message);
    }

    private void processWsdlMessagePart(final ObjectsForResolveContainer container, final List<XSDSchema> allSchemas,
            final EObject eContent) {
        if (!(eContent instanceof Part)) {
            return;
        }
        final Part part = (Part) eContent;
        if ((part.getElementDeclaration() != null && (part.getElementDeclaration().eContainer() == null || !allSchemas
                .contains(part.getElementDeclaration().getSchema())))
                || (part.getTypeDefinition() != null && (part.getTypeDefinition().eContainer() == null || !allSchemas
                        .contains(part.getTypeDefinition().getSchema())))) {

            container.getMessagePartsForResolve().add(part);
        }
    }

    // =========================================================
    // helpers
    // =========================================================

    public boolean isContainerXsdSchemaValid(final List<XSDSchema> allSchemas, final XSDNamedComponent sourceComponent,
            final XSDSchema destinationSchema, final String destinationSchemaTargetNamespace) {
        if (destinationSchema == null) {
            return false;
        }
        final String destinationSchemaLocation = destinationSchema.getSchemaLocation();
        final String sourceSchemaLocation = sourceComponent.getSchema().getSchemaLocation();
        final boolean resolveComponentSchemaIsInSameDocument = destinationSchemaLocation != null
                && destinationSchemaLocation.equals(sourceSchemaLocation);

        final String sourceSchemaTNS = sourceComponent.getSchema().getTargetNamespace();
        final boolean needsImportOrIncludeDirective = !(resolveComponentSchemaIsInSameDocument && sourceSchemaTNS != null && sourceSchemaTNS
                .equals(destinationSchemaTargetNamespace));

        boolean hasImportOrIncludeDirective = true;
        if (needsImportOrIncludeDirective) {
            hasImportOrIncludeDirective = hasImportOrIncludeDirective(sourceComponent.getSchema(), destinationSchema,
                    destinationSchemaTargetNamespace);
        }

        final boolean isReachable = !needsImportOrIncludeDirective || needsImportOrIncludeDirective
                && hasImportOrIncludeDirective;

        return isReachable
                && (!resolveComponentSchemaIsInSameDocument || resolveComponentSchemaIsInSameDocument
                        && isDestinationSchemaInAllSchemas(allSchemas, destinationSchema));
    }

    private boolean isDestinationSchemaInAllSchemas(final List<XSDSchema> allSchemas, final XSDSchema destinationSchema) {
        for (final XSDSchema xsdSchema : allSchemas) {
            if (xsdSchema.getTargetNamespace() != null
                    && xsdSchema.getTargetNamespace().equals(destinationSchema.getTargetNamespace())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given source schema has access to the destinationSchema. If
     * destinationSchemaTargetNamespace is for schema for schema always returns
     * true.
     */
    private boolean hasImportOrIncludeDirective(final XSDSchema sourceSchema, final XSDSchema destinationSchema,
            final String destinationSchemaTargetNamespace) {
        if (EmfXsdUtils.isSchemaForSchemaNS(destinationSchemaTargetNamespace)) {
            return true;
        }
        if (destinationSchemaTargetNamespace == null) {
            return false;
        }

        final EList<XSDSchemaContent> sourceComponentSchemaContents = sourceSchema.getContents();
        for (final XSDSchemaContent content : sourceComponentSchemaContents) {
            if (content instanceof XSDImport) {
                final XSDImport xsdImport = (XSDImport) content;
                if (destinationSchemaTargetNamespace.equals(xsdImport.getNamespace())) {
                    return true;
                }
            } else if (content instanceof XSDInclude) {
                final XSDInclude include = (XSDInclude) content;
                final String includeSchemaLocation = include.getSchema().getSchemaLocation();
                final String destinationSchemaLocation = destinationSchema.getSchema().getSchemaLocation();
                if (includeSchemaLocation != null && includeSchemaLocation.equals(destinationSchemaLocation)) {
                    return true;
                }
            }
        }

        return false;
    }

    // =========================================================
    // helpers
    // =========================================================

    /**
     * This method checks if the prefix value of the given element is as
     * expected.
     */
    private boolean areDestinationSchemaPrefixAndNamespaceValid(final XSDSchema xsdSchema, final Element element,
            final String attributeName, final String expectedDestinationSchemaNamespace) {

        // FIXME: Intentionally disabling the prefix checks. The WSDL API is not
        // running the validation on the XSDs when validation performed on the
        // Definition. The error markers cannot be "removed" when undo of
        // operation which breaks the prefix value.
        if (true) {
            return true;
        }
        
        if(expectedDestinationSchemaNamespace == null) {
            return true;
        }

        final String destinationNamespacePrefix = getDestinationNamespacePrefix(element, attributeName);
        if (destinationNamespacePrefix == null) {
            // no prefix, same schema
            return true;
        }

        final String actualDestinationNamespace = xsdSchema.getQNamePrefixToNamespaceMap().get(destinationNamespacePrefix);
        return expectedDestinationSchemaNamespace.equals(actualDestinationNamespace);
    }

    private String getDestinationNamespacePrefix(final Element element, final String attributeName) {
        if (element == null) {
            return null;
        }
        if (!ElementAttributeUtils.hasAttributeValue(element, attributeName)) {
            return null;
        }
        final String qName = element.getAttribute(attributeName);
        return UpdateNSPrefixUtils.instance().extractPrefixFromQName(qName);
    }

}
