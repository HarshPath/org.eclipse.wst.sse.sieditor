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
 *    Richard Birenheide - initial API and implementation.
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
        final SchemaNode rootNode = buildSchemaDependenciesInternal(rootSchema.getComponent(), rootSchema, dependenciesMap);
        rootNode.setSchema(rootSchema);
        return rootNode;
    }

    private SchemaNode buildSchemaDependenciesInternal(final XSDSchema xsdSchema, final ISchema rootSchema,
            final Map<String, SchemaNode> dependenciesMap) {

        if (dependenciesMap.get(xsdSchema.getTargetNamespace()) != null) {
            return dependenciesMap.get(xsdSchema.getTargetNamespace());
        }

        final SchemaNode dependencyNode = new SchemaNode(xsdSchema.getTargetNamespace());
        final ISchema schema = new Schema(xsdSchema, rootSchema.getParent(), null);

        dependencyNode.setSchema(schema);

        dependenciesMap.put(dependencyNode.getNamespace(), dependencyNode);

        final EList<XSDSchemaContent> contents = xsdSchema.getContents();
        for (final XSDSchemaContent content : contents) {
            if (!(content instanceof XSDImport)) {
                continue;
            }

            final XSDImport importDirective = (XSDImport) content;
            if (importDirective.getElement().getAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE) != null) {
                continue;
            }
            dependencyNode.addImport(buildSchemaDependenciesInternal(importDirective.getResolvedSchema(), rootSchema,
                    dependenciesMap));
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
