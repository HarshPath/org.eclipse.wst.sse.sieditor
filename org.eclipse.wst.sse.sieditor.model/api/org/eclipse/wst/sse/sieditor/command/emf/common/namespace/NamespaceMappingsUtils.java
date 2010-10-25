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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.wst.wsdl.Definition;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

/**
 * This is the set namespace utility class. This class has methods for setting
 * schema and definition namespaces and namespace mappings
 * 
 * 
 * 
 */
public class NamespaceMappingsUtils {

    private static final NamespaceMappingsUtils INSTANCE = new NamespaceMappingsUtils();

    private NamespaceMappingsUtils() {

    }

    public static NamespaceMappingsUtils instance() {
        return INSTANCE;
    }

    /**
     * This method updates all the schema namespaces which have the same value
     * as the old namespace value.
     * 
     * @return the first updated namespace prefix or <code>null</code> if no
     *         namespace was updated
     */
    public String updateNamespacePrefixMappings(final XSDSchema schema, final String oldNamespaceValue,
            final String newNamespaceValue) {

        final List<String> prefixMappingsToUpdate = findElementNamespaces(schema.getElement(), oldNamespaceValue, schema
                .getQNamePrefixToNamespaceMap());

        if (prefixMappingsToUpdate.isEmpty()) {
            return null;
        }
        for (final String prefix : prefixMappingsToUpdate) {
            schema.getQNamePrefixToNamespaceMap().put(prefix, newNamespaceValue);
        }
        schema.elementChanged(schema.getElement());

        for (final String prefix : prefixMappingsToUpdate) {
            if (!EmfXsdUtils.XMLNS_PREFIX.equals(prefix)) {
                return prefix;
            } else {
                continue;
            }
        }
        return null; // the default prefix
    }

    public void updateDefinitionNamespace(final Definition definition, final String namespacePrefix,
            final String newNamespaceValue) {
        if (definition.getNamespace(namespacePrefix) != null) {
            definition.getNamespaces().remove(namespacePrefix);
        }
        definition.addNamespace(namespacePrefix, newNamespaceValue);
        definition.elementChanged(definition.getElement());
    }

    /**
     * This method updates all the definition namespaces which have the same
     * value as the old namespace value.
     * 
     * @return the first updated namespace prefix or <code>null</code> if no
     *         namespace was updated
     */
    public String updateNamespacePrefixMappings(final IDescription description, final String oldNamespaceValue,
            final String newNamespaceValue) {
        final String prefix = updateDefinitionMappings(description.getComponent(), oldNamespaceValue, newNamespaceValue);
        updateContainedSchemasPrefixMappings(description, oldNamespaceValue, newNamespaceValue);
        return prefix;
    }

    @SuppressWarnings("unchecked")
    private String updateDefinitionMappings(final Definition definition, final String oldNamespaceValue,
            final String newNamespaceValue) {
        final List<String> mappingsToUpdate = findElementNamespaces(definition.getElement(), oldNamespaceValue, definition
                .getNamespaces());

        if (mappingsToUpdate.isEmpty()) {
            return null;
        }
        for (String prefix : mappingsToUpdate) {
            if (prefix.equals(EmfXsdUtils.XMLNS_PREFIX)) {
                prefix = ""; //$NON-NLS-1$
            }
            definition.getNamespaces().remove(prefix);
            if (prefix.equals("")) { //$NON-NLS-1$
                definition.getElement().setAttribute(EmfXsdUtils.XMLNS_PREFIX, newNamespaceValue);
            } else {
                definition.getElement().setAttribute(EmfXsdUtils.XMLNS_PREFIX + ':' + prefix, newNamespaceValue);
            }
            definition.elementChanged(definition.getElement());
        }
        return mappingsToUpdate.get(0);
    }

    /**
     * This method updates all the contained schema prefix mappings which are
     * pointing to the old prefix value. The prefix values are updated with the
     * new given one.
     */
    public void updateContainedSchemasPrefixMappings(final IDescription description, final String oldNamespaceValue,
            final String newNamespaceValue) {
        final List<ISchema> containedSchemas = description.getContainedSchemas();

        for (final ISchema schema : containedSchemas) {
            updateNamespacePrefixMappings(schema.getComponent(), oldNamespaceValue, newNamespaceValue);
        }
    }

    /**
     * Utility methods. Returns list of all the element namespaces with the
     * given oldNamespaceValue
     * 
     * @param element
     * @param oldNamespaceValue
     * @param namespaceMap
     * @return list of the namespaces with the given oldNamespaceValue
     */
    public List<String> findElementNamespaces(final Element element, final String oldNamespaceValue,
            final Map<String, String> namespaceMap) {
        final List<String> mappingsToUpdate = new LinkedList<String>();
        final Iterator<Entry<String, String>> iter = namespaceMap.entrySet().iterator();
        while (iter.hasNext()) {
            final Entry<String, String> entry = iter.next();
            final String namespaceValue = entry.getValue();

            if ((entry.getKey() == null || entry.getKey().equals("")) && namespaceValue != null && namespaceValue.equals(oldNamespaceValue) //$NON-NLS-1$
                    && element.getAttribute(EmfXsdUtils.XMLNS_PREFIX) != null) {
                mappingsToUpdate.add(EmfXsdUtils.XMLNS_PREFIX);
            } else if (namespaceValue != null && namespaceValue.equals(oldNamespaceValue)
                    && element.getAttribute(EmfXsdUtils.XMLNS_PREFIX + ':' + entry.getKey()) != null) {
                mappingsToUpdate.add(entry.getKey());
                iter.remove();
            }
        }
        return mappingsToUpdate;
    }

    /**
     * Method for adding a new namespace with the given value to the schema. It
     * returns the prefix by which the namespace is added to the namespace map
     * 
     * @param schema
     * @param newNamespaceValue
     * 
     * @return the namespace prefix
     */
    public String addXsdSchemaNamespaceMapping(final XSDSchema schema, final String newNamespaceValue) {
        final String prefix = EmfWsdlUtils.generateNewNSAttribute(schema.getQNamePrefixToNamespaceMap());
        schema.getQNamePrefixToNamespaceMap().put(prefix, newNamespaceValue);
        schema.elementChanged(schema.getElement());
        return prefix;
    }

    /**
     * Method for adding a new namespace with the given value to the definition.
     * It returns the prefix by which the namespace is added to the namespace
     * map
     * 
     * @param definition
     * @param newNamespaceValue
     * 
     * @return the namespace prefix
     */
    public String addDefaultDefinitionSchemaNamespaceMapping(final Definition definition, final String newNamespaceValue) {
        definition.addNamespace("", newNamespaceValue); //$NON-NLS-1$
        definition.getElement().setAttribute(EmfXsdUtils.XMLNS_PREFIX, newNamespaceValue);
        return ""; //$NON-NLS-1$
    }

    @SuppressWarnings("unchecked")
    public String addDefinitionNamespaceMapping(final Definition definition, final String newNamespaceValue) {
        final String prefix = EmfWsdlUtils.generateNewNSAttribute(definition.getNamespaces());
        definition.addNamespace(prefix, newNamespaceValue);
        definition.elementChanged(definition.getElement());
        return prefix;
    }

}
