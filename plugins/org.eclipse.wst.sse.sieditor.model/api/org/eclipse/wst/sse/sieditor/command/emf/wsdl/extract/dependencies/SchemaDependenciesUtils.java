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
package org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.util.XSDConstants;

import org.eclipse.wst.sse.sieditor.model.utils.ElementAttributeUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

public class SchemaDependenciesUtils {

    private final static SchemaDependenciesUtils INSTANCE = new SchemaDependenciesUtils();

    private SchemaDependenciesUtils() {

    }

    public static SchemaDependenciesUtils instance() {
        return INSTANCE;
    }

    public Set<SchemaNode> getSchemaDependencies(final SchemaNode rootNode) {
        final Set<SchemaNode> set = new HashSet<SchemaNode>();
        this.populateSchemaDependenciesSet(rootNode, set);
        set.remove(rootNode);
        return set;
    }

    private void populateSchemaDependenciesSet(final SchemaNode node, final Set<SchemaNode> set) {
        set.add(node);
        for (final SchemaNode imported : node.getImports()) {
            if (!set.contains(imported)) {
                populateSchemaDependenciesSet(imported, set);
            }
        }
    }

    public SchemaNode buildSchemaDependenciesTree(final ISchema rootSchema) {
        final Map<String, SchemaNode> dependenciesMap = new TreeMap<String, SchemaNode>();
        final SchemaNode rootNode = buildSchemaDependenciesInternal(rootSchema.getComponent(), rootSchema.getNamespace(),
                rootSchema, dependenciesMap);
        rootNode.setSchema(rootSchema);
        return rootNode;
    }

    private SchemaNode buildSchemaDependenciesInternal(final XSDSchema xsdSchema, final String namespace,
            final ISchema rootSchema, final Map<String, SchemaNode> dependenciesMap) {

        if (dependenciesMap.get(namespace) != null) {
            return dependenciesMap.get(namespace);
        }

        final SchemaNode dependencyNode = new SchemaNode(namespace);
        final ISchema schema = new Schema(xsdSchema, rootSchema.getParent(), null);

        dependencyNode.setSchema(schema);

        dependenciesMap.put(dependencyNode.getNamespace(), dependencyNode);

        if (xsdSchema != null) {
            final EList<XSDSchemaContent> contents = xsdSchema.getContents();
            for (final XSDSchemaContent content : contents) {
                if (!(content instanceof XSDImport)) {
                    continue;
                }

                final XSDImport importDirective = (XSDImport) content;
                if (ElementAttributeUtils.hasAttributeValue(importDirective.getElement(), XSDConstants.SCHEMALOCATION_ATTRIBUTE)) {
                    continue;
                }
                XSDSchema resolvedSchema = importDirective.getResolvedSchema();
                if (resolvedSchema == null) {
                    // try to resolve the schema from the definition
                    final ISchema[] schemas = ((IDescription) rootSchema.getRoot()).getSchema(importDirective.getNamespace());
                    if (schemas.length > 0) {
                        resolvedSchema = schemas[0].getComponent();
                    }
                }
                dependencyNode.addImport(buildSchemaDependenciesInternal(resolvedSchema, importDirective.getNamespace(),
                        rootSchema, dependenciesMap));
            }
        }
        return dependencyNode;
    }

    public Map<String, String> createFilenamesMap(final SchemaNode schemaNode, final Set<SchemaNode> dependenciesSet) {
        final Map<String, String> locationsMap = new HashMap<String, String>();
        locationsMap.put(schemaNode.getNamespace(), schemaNode.getFilename());

        for (final SchemaNode node : dependenciesSet) {
            locationsMap.put(node.getNamespace(), node.getFilename());
        }
        return locationsMap;
    }

}
