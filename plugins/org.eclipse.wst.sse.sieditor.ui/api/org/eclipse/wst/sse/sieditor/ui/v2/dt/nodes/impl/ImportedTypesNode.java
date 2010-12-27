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
package org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.Image;
import org.eclipse.xsd.util.XSDConstants;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IImportedTypesNode;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;

public class ImportedTypesNode extends AbstractXsdTreeNode implements IImportedTypesNode {

    public ImportedTypesNode(final IModelObject modelObject, final ITreeNode parent, final TreeNodeMapper nodeMapper) {
        this(modelObject, nodeMapper);
    }

    public ImportedTypesNode(final IModelObject modelObject, final TreeNodeMapper nodeMapper) {
        super(modelObject, null, nodeMapper, ITreeNode.CATEGORY_STATIC_ROOT);
    }

    @Override
    public Object[] getChildren() {

        final Set<ISchema> referedSchemasSet = new HashSet<ISchema>();
        final List<ISchema> containedSchemas = getContainedSchemas();
        for (final ISchema schema : containedSchemas) {
            final Collection<ISchema> allReferredSchemas = schema.getAllReferredSchemas();
            for (final ISchema referredSchema : allReferredSchemas) {
                // exclude built-in types schema
                if (!XSDConstants.isSchemaForSchemaNamespace(referredSchema.getNamespace())) {
                    referedSchemasSet.add(referredSchema);
                }
            }
        }
        referedSchemasSet.removeAll(containedSchemas);
        referedSchemasSet.addAll(getWSDLImportedSchemas());

        final List<ITreeNode> nodes = new ArrayList<ITreeNode>();
        for (final ISchema referredSchema : referedSchemasSet) {
            final List<ITreeNode> treeNodes = getNodeMapper().getTreeNode(referredSchema, getCategories(), this);
            final ITreeNode treeNode = treeNodes.isEmpty() ? null : treeNodes.get(0);
            if (treeNode instanceof ImportedSchemaNode) {
                nodes.add(treeNode);
            } else {
                final ImportedSchemaNode schemaNode = new ImportedSchemaNode(referredSchema, this, getNodeMapper());
                getNodeMapper().addToNodeMap(referredSchema, schemaNode);
                nodes.add(schemaNode);
            }
        }
        return nodes.toArray();
    }

    /**
     * {@link ImportedXsdTypesNode} overrides this method to provide it's single
     * root schema
     * 
     * @return
     */
    protected List<ISchema> getContainedSchemas() {
        return ((IDescription) getModelObject()).getContainedSchemas();
    }

    private List<ISchema> getWSDLImportedSchemas() {
        List<ISchema> importedSchemas = new ArrayList<ISchema>();

        if (getModelObject() instanceof IDescription) {
            final IDescription description = (IDescription) getModelObject();
            importedSchemas = description.getAllVisibleSchemas();
            importedSchemas.removeAll(description.getContainedSchemas());
            final Iterator<ISchema> iterator = importedSchemas.iterator();
            while (iterator.hasNext()) {
                final ISchema schema = iterator.next();
                if (XSDConstants.isSchemaForSchemaNamespace(schema.getNamespace())) {
                    iterator.remove();
                }
            }
        }

        return importedSchemas;
    }

    @Override
    public Image getImage() {
        return getImageRegistry().get(Activator.NODE_IMPORTED_TYPES);
    }
}
