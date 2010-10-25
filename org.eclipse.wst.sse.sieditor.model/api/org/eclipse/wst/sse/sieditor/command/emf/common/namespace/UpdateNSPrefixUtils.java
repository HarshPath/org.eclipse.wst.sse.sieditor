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
package org.eclipse.wst.sse.sieditor.command.emf.common.namespace;

import java.util.Map;

import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;

/**
 * Utility class. This class has methods for updating element prefixes.
 * 
 * 
 * 
 */
public class UpdateNSPrefixUtils {

    private static final UpdateNSPrefixUtils INSTANCE = new UpdateNSPrefixUtils();

    private UpdateNSPrefixUtils() {

    }

    public static UpdateNSPrefixUtils instance() {
        return INSTANCE;
    }

    public void updateXsdElementDeclaration(final XSDSchema changedSchema, final String namespacePrefix,
            final XSDElementDeclaration xsdElementDeclaration) {
        final XSDElementDeclaration resolvedElementDeclaration = xsdElementDeclaration.getResolvedElementDeclaration();
        final boolean resolvedElementSchemaMatches = resolvedElementDeclaration.getSchema() == changedSchema;

        final XSDTypeDefinition typeDefinition = xsdElementDeclaration.getTypeDefinition();
        final boolean isElementReference = typeDefinition == null;

        final XSDNamedComponent elementNamedComponentType = isElementReference ? resolvedElementDeclaration : typeDefinition;

        updateDeclaration(changedSchema, namespacePrefix, xsdElementDeclaration, resolvedElementSchemaMatches,
                elementNamedComponentType);
    }

    public void updateXsdAttributeDeclaration(final XSDSchema changedSchema, final String namespacePrefix,
            final XSDAttributeDeclaration xsdAttributeDeclaration) {
        final XSDAttributeDeclaration resolvedAttributeDeclaration = xsdAttributeDeclaration.getResolvedAttributeDeclaration();
        final boolean resolvedAttributeSchemaMatches = resolvedAttributeDeclaration.getSchema() == changedSchema;

        final XSDSimpleTypeDefinition typeDefinition = xsdAttributeDeclaration.getTypeDefinition();
        final boolean isAttributeReference = typeDefinition == null;

        final XSDNamedComponent attributeNamedComponentType = isAttributeReference ? resolvedAttributeDeclaration
                : typeDefinition;

        updateDeclaration(changedSchema, namespacePrefix, xsdAttributeDeclaration, resolvedAttributeSchemaMatches,
                attributeNamedComponentType);
    }

    public void updateXsdAttributeGroupDeclaration(final XSDSchema changedSchema, final String namespacePrefix,
            final XSDAttributeGroupDefinition attributeGroup) {
        final XSDAttributeGroupDefinition resolvedAttributeGroupDefinition = attributeGroup.getResolvedAttributeGroupDefinition();
        final boolean resolvedAttributeGroupDefinitionMatches = resolvedAttributeGroupDefinition.getSchema() == changedSchema;

        updateDeclaration(changedSchema, namespacePrefix, attributeGroup, resolvedAttributeGroupDefinitionMatches,
                resolvedAttributeGroupDefinition);
    }

    public void updateXsdModelGroupDeclaration(final XSDSchema changedSchema, final String namespacePrefix,
            final XSDModelGroupDefinition xsdModelGroupDefinition) {
        final XSDModelGroupDefinition resolvedModelGroupDefinition = xsdModelGroupDefinition.getResolvedModelGroupDefinition();
        final boolean resolvedModelGroupDefinitionMatches = resolvedModelGroupDefinition.getSchema() == changedSchema;

        updateDeclaration(changedSchema, namespacePrefix, xsdModelGroupDefinition, resolvedModelGroupDefinitionMatches,
                resolvedModelGroupDefinition);
    }

    private void updateDeclaration(final XSDSchema changedSchema, final String namespacePrefix,
            final XSDSchemaContent schemaContentElement, final boolean resolvedSchemaOfSchemaContentMatches,
            final XSDNamedComponent attributeNamedComponentType) {
        final boolean shouldCheckAttributeType = attributeNamedComponentType != null
                && !EmfXsdUtils.isSchemaForSchemaNS(attributeNamedComponentType.getTargetNamespace());

        final boolean shouldEditElement = shouldCheckAttributeType
                && changedSchema.getTargetNamespace().equals(attributeNamedComponentType.getTargetNamespace());

        if (shouldEditElement
                && (resolvedSchemaOfSchemaContentMatches || attributeNamedComponentType.getSchema() == changedSchema)) {
            updateElementAttributes(namespacePrefix, schemaContentElement, changedSchema);
        }
    }

    /**
     * Utility method. Updates the {@link XSDConstants#REF_ATTRIBUTE} or
     * {@link XSDConstants#TYPE_ATTRIBUTE} attributes of the given schema
     * element to include the given namespacePrefix
     * 
     * @param namespacePrefix
     * @param contentElement
     * @param schema
     */
    private void updateElementAttributes(final String namespacePrefix, final XSDSchemaContent contentElement,
            final XSDSchema schema) {
        updateElementAttribute(XSDConstants.TYPE_ATTRIBUTE, namespacePrefix, contentElement, schema
                .getQNamePrefixToNamespaceMap());
        updateElementAttribute(XSDConstants.REF_ATTRIBUTE, namespacePrefix, contentElement, schema.getQNamePrefixToNamespaceMap());
    }

    /**
     * Utility method. Updates the element's given attribute (if needed)
     * 
     * @param attributeName
     * @param namespacePrefix
     * @param contentElement
     * @param prefixMap
     */
    private void updateElementAttribute(final String attributeName, final String namespacePrefix,
            final XSDSchemaContent contentElement, final Map<String, String> prefixMap) {
        final Element element = contentElement.getElement();
        if (element.getAttribute(attributeName) != null && !"".equals(element.getAttribute(attributeName)) //$NON-NLS-1$
                && prefixNeedsUpdate(namespacePrefix, element.getAttribute(attributeName), prefixMap)) {

            final String newAttributeValue = createNewAttributeValue(namespacePrefix, element, attributeName);
            element.setAttribute(attributeName, newAttributeValue);

            contentElement.elementChanged(element);
        }
    }

    /**
     * Utility method. It determines whether the given attribute value should be
     * updated with the given namespacePrefix
     * 
     * @param namespacePrefix
     *            - the namespace prefix we need to update the old ones with
     * @param attributeValueToCheck
     * @param prefixMap
     * @return <code>true</code> if the element prefix needs to be updated with
     *         the given one
     */
    private boolean prefixNeedsUpdate(final String namespacePrefix, final String attributeValueToCheck,
            final Map<String, String> prefixMap) {
        if (attributeValueToCheck == null)
            return false;

        boolean prefixNeedsUpdate = EmfXsdUtils.XMLNS_PREFIX.equals(namespacePrefix)
                || !attributeValueToCheck.startsWith(namespacePrefix + ':');

        if (prefixNeedsUpdate) {
            final int endIndex = attributeValueToCheck.indexOf(':');
            if (endIndex != -1) {
                // for some reason, we are receiving types which points to the
                // schema-for-schema namespace. we need to exclude those
                final String originalPrefix = attributeValueToCheck.substring(0, endIndex);
                prefixNeedsUpdate &= !EmfXsdUtils.isSchemaForSchemaNS(prefixMap.get(originalPrefix));
            }
        }

        return prefixNeedsUpdate;
    }

    private String createNewAttributeValue(String namespacePrefix, final Element element, final String attributeName) {
        if (EmfXsdUtils.XMLNS_PREFIX.equals(namespacePrefix)) {
            namespacePrefix = ""; //$NON-NLS-1$
        } else {
            namespacePrefix = namespacePrefix + ':';
        }

        // extract the element name from the attribute value
        final int beginIndex = element.getAttribute(attributeName).indexOf(':') + 1;
        final String elementName = element.getAttribute(attributeName).substring(beginIndex);

        return namespacePrefix + elementName;
    }

    // =========================================================
    // helpers
    // =========================================================

    /**
     * Utility method. Extracts the TNS prefix from the given QName.
     */
    public String extractPrefixFromQName(final String qname) {
        final int endIndex = qname.indexOf(':');
        if (endIndex != -1) {
            return qname.substring(0, endIndex);
        }
        return null;
    }

}
