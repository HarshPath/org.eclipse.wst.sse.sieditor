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
package org.eclipse.wst.sse.sieditor.model.reconcile.adapters.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.wsdl.Types;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;

import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;

/**
 * XsdSchema concrete components cache utility class. The XsdSchema has cached
 * collections. These collections are not cleared on remove of referred
 * namespace or import directive. This class has methods for clearing those
 * cached collections.
 * 
 */
public class ConcreteComponentsCacheUtils {

    private static final ConcreteComponentsCacheUtils INSTANCE = new ConcreteComponentsCacheUtils();

    private ConcreteComponentsCacheUtils() {

    }

    public static ConcreteComponentsCacheUtils instance() {
        return INSTANCE;
    }

    public void clearConcreteComponentsCacheForSchema(final XSDSchema xsdSchema, final Types eTypes) {
        final Collection<String> importedNamespaces = getImportedNamespaces(xsdSchema);
        clearConcreteComponentsCache(xsdSchema.getAttributeDeclarations(), xsdSchema, importedNamespaces, eTypes);
        clearConcreteComponentsCache(xsdSchema.getAttributeGroupDefinitions(), xsdSchema, importedNamespaces, eTypes);
        clearConcreteComponentsCache(xsdSchema.getModelGroupDefinitions(), xsdSchema, importedNamespaces, eTypes);
        clearConcreteComponentsCache(xsdSchema.getTypeDefinitions(), xsdSchema, importedNamespaces, eTypes);
        clearConcreteComponentsCache(xsdSchema.getIdentityConstraintDefinitions(), xsdSchema, importedNamespaces, eTypes);
        clearConcreteComponentsCache(xsdSchema.getNotationDeclarations(), xsdSchema, importedNamespaces, eTypes);
        clearConcreteComponentsCache(xsdSchema.getElementDeclarations(), xsdSchema, importedNamespaces, eTypes);
    }

    private <T extends XSDConcreteComponent> void clearConcreteComponentsCache(final EList<T> concreteComponents,
            final XSDSchema xsdSchema, final Collection<String> importedNamespaces, final Types eTypes) {
        clearCachedComponentsFromMissingSchema(concreteComponents, eTypes);
        clearCachedComponentsFromMissingImport(concreteComponents, importedNamespaces);
    }

    private <T extends XSDConcreteComponent> void clearCachedComponentsFromMissingSchema(final EList<T> concreteComponents,
            final Types eTypes) {
        if (eTypes == null) {
            return;
        }

        final List<XSDConcreteComponent> toRemove = new LinkedList<XSDConcreteComponent>();

        for (final XSDConcreteComponent concreteComponent : concreteComponents) {
            final EObject eContainer = concreteComponent.eContainer();

            final String enclosingDefintionLocation = eTypes.getEnclosingDefinition().getLocation();
            if (eContainer instanceof XSDSchema && ((XSDSchema) eContainer).getSchemaLocation() != null
                    && ((XSDSchema) eContainer).getSchemaLocation().equals(enclosingDefintionLocation)
                    && !eTypes.getSchemas().contains(eContainer)) {

                toRemove.add(concreteComponent);
            }
        }

        concreteComponents.removeAll(toRemove);
    }

    private <T extends XSDConcreteComponent> void clearCachedComponentsFromMissingImport(final EList<T> concreteComponents,
            final Collection<String> importedNamespaces) {
        final List<XSDConcreteComponent> toRemove = new LinkedList<XSDConcreteComponent>();

        for (final XSDConcreteComponent concreteComponent : concreteComponents) {
            final XSDSchema concreteComponentSchema = ((XSDNamedComponent) concreteComponent).getSchema();

            final String componentTargetNamespace = concreteComponentSchema == null ? null : concreteComponentSchema
                    .getTargetNamespace();
            if (!importedNamespaces.contains(componentTargetNamespace)
                    && !EmfXsdUtils.isSchemaForSchemaNS(componentTargetNamespace)) {
                toRemove.add(concreteComponent);
            }
        }

        concreteComponents.removeAll(toRemove);
    }

    private Collection<String> getImportedNamespaces(final XSDSchema xsdSchema) {
        final Set<String> importedNamespaces = new HashSet<String>();
        for (final XSDSchemaContent schemaContent : xsdSchema.getContents()) {
            if (!(schemaContent instanceof XSDImport)) {
                continue;
            }
            importedNamespaces.add(((XSDImport) schemaContent).getNamespace());
        }
        return importedNamespaces;
    }

}
