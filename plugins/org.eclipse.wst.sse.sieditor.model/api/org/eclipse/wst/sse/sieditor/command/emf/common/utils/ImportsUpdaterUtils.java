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
package org.eclipse.wst.sse.sieditor.command.emf.common.utils;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Import;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;

/**
 * This class has methods for updating definition schema imports.
 * 
 */
public class ImportsUpdaterUtils {

    private static final ImportsUpdaterUtils INSTANCE = new ImportsUpdaterUtils();

    private ImportsUpdaterUtils() {

    }

    public static ImportsUpdaterUtils instance() {
        return INSTANCE;
    }

    /**
     * Method for updating schema imports namespaces. This method checks all the
     * schemas contained in the given definition and updates there import
     * directives if such update is needed. Schema import directives with the
     * given oldNamespaceValue are updated
     * 
     * @param definition
     *            - the definition to update the schemas from
     * @param oldNamespaceValue
     *            - the old namespace value we are changing
     * @param newNamespaceValue
     *            - the new namespace value we are setting
     */
    @SuppressWarnings("unchecked")
    public void updateSchemaImports(final Definition difinition, final String oldNamespaceValue, final String newNamespaceValue) {

        List<XSDSchema> schemas = new LinkedList<XSDSchema>();
        if (difinition.getETypes() != null && difinition.getETypes().getSchemas() != null) {
            schemas = difinition.getETypes().getSchemas();
        }

        for (final XSDSchema schema : schemas) {
            if (newNamespaceValue != null && newNamespaceValue.equals(schema.getTargetNamespace())) {
                continue;
            }
            final EList<XSDSchemaContent> contents = schema.getContents() != null ? schema.getContents()
                    : new BasicEList<XSDSchemaContent>();

            for (final XSDSchemaContent xsdSchemaContent : contents) {
                if (xsdSchemaContent instanceof XSDImport) {
                    final XSDImport importDirective = (XSDImport) xsdSchemaContent;
                    if (shouldUpdateSchemaImportDirective(oldNamespaceValue, schema, importDirective)) {
                        importDirective.setNamespace(newNamespaceValue);
                    }
                }
            }
        }
    }

    /**
     * Utility method. Checks if the given import directive should be updated.
     * 
     * @param oldNamespaceValue
     * @param schema
     * @param importDirective
     * @return
     */
    private boolean shouldUpdateSchemaImportDirective(final String oldNamespaceValue, final XSDSchema schema,
            final XSDImport importDirective) {
        final boolean isSameSchemaLocation = importDirective.getSchemaLocation() == null
                || (importDirective.getSchemaLocation() != null && importDirective.getSchemaLocation().equals(
                        schema.getSchemaLocation()));
        return isSameSchemaLocation && (oldNamespaceValue != null && oldNamespaceValue.equals(importDirective.getNamespace()));
    }

    /**
     * Utility method. Updates all the inner schemas with the same target
     * namespace as the target namespace of the definition. It also updates the
     * schema imports
     * 
     * @param definition
     * @param oldNamespaceValue
     * @param newNamespaceValue
     */
    @SuppressWarnings("unchecked")
    public void updateContainedSchemas(final Definition definition, final String oldNamespaceValue, final String newNamespaceValue) {

        List<XSDSchema> schemas = new LinkedList<XSDSchema>();
        if (definition.getETypes() != null && definition.getETypes().getSchemas() != null) {
            schemas = definition.getETypes().getSchemas();
        }

        for (final XSDSchema schema : schemas) {
            if ((schema.getTargetNamespace() == null && oldNamespaceValue == null)
                    || (schema.getTargetNamespace() != null && schema.getTargetNamespace().equals(oldNamespaceValue))) {
                schema.setTargetNamespace(newNamespaceValue);
            } else {
                updateSchemaImports(definition, oldNamespaceValue, newNamespaceValue);
            }
        }
    }

    /**
     * Utility method. Updates all definition imports - adds prefix for each
     * imported definition, remaps message parts, operation messages, bindings,
     * etc.
     */
    @SuppressWarnings("unchecked")
    public void updateDefinitionImports(final Definition definition, final String oldNamespaceValue,
            final String newNamespaceValue) {
        final EList<Import> eImports = (definition.getEImports() == null ? new BasicEList<Import>() : definition.getEImports());

        for (final Import wsdlImport : eImports) {
            if (wsdlImport.getNamespaceURI() != null && wsdlImport.getNamespaceURI().equals(oldNamespaceValue)
                    && wsdlImport.getEDefinition() != null) {
                final String prefix = namespaceUtils().addDefinitionNamespaceMapping(definition, oldNamespaceValue);
                remapUtils()
                        .remapOperationsMessagesReferences(definition, wsdlImport.getEDefinition(), oldNamespaceValue, prefix);
                remapUtils().remapBindingsPortTypes(definition, wsdlImport.getEDefinition(), oldNamespaceValue);
                remapUtils().remapServicePortBindings(definition, wsdlImport.getEDefinition(), oldNamespaceValue);
            }
        }
    }

    // =========================================================
    // helpers
    // =========================================================

    private NamespaceMappingsUtils namespaceUtils() {
        return NamespaceMappingsUtils.instance();
    }

    private RemapReferencesUtils remapUtils() {
        return RemapReferencesUtils.instance();
    }

}
