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
package org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.labels;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.ImportedSchemaNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.ImportedTypesNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.labels.provider.ElementNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.labels.provider.ImportedSchemaNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.labels.provider.ImportedTypesNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.labels.provider.SimpleTypeNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.labels.provider.StructureTypeNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.labels.AbstractTreeNodeLabelsProviderFactory;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.labels.provider.ITreeNodeLabelsProvider;

public class DTTreeNodeLabelsProviderFactory extends AbstractTreeNodeLabelsProviderFactory {

    private static final DTTreeNodeLabelsProviderFactory INSTANCE = new DTTreeNodeLabelsProviderFactory();

    protected DTTreeNodeLabelsProviderFactory() {

    }

    public static DTTreeNodeLabelsProviderFactory instance() {
        return INSTANCE;
    }

    @Override
    protected ITreeNodeLabelsProvider getLabelsProviderForNonModelNode(final ITreeNode treeNode) {
        if (treeNode instanceof ImportedTypesNode) {
            return new ImportedTypesNodeLabelsProvider();
        }
        if (treeNode instanceof ImportedSchemaNode) {
            return new ImportedSchemaNodeLabelsProvider((ISchema) ((ImportedSchemaNode) treeNode).getModelObject());
        }
        return null;
    }

    @Override
    protected ITreeNodeLabelsProvider getLabelsProviderForSpecificModelObject(final IModelObject modelObject) {
        if (modelObject instanceof IStructureType) {
            return new StructureTypeNodeLabelsProvider((IStructureType) modelObject);
        }
        if (modelObject instanceof ISimpleType) {
            return new SimpleTypeNodeLabelsProvider((ISimpleType) modelObject);
        }
        if (modelObject instanceof IElement) {
            return new ElementNodeLabelsProvider((IElement) modelObject);
        }
        return null;
    }

}
