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
package org.eclipse.wst.sse.sieditor.ui.v2.nodes.labels;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.labels.provider.DefaultNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.labels.provider.ITreeNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.labels.provider.NullNodeLabelsProvider;

public abstract class AbstractTreeNodeLabelsProviderFactory implements ITreeNodeLabelsProviderFactory {

    @Override
    public final ITreeNodeLabelsProvider getLabelsProvider(final ITreeNode treeNode) {
        final ITreeNodeLabelsProvider labelsProvider = getLabelsProviderForNonModelNode(treeNode);
        if (labelsProvider != null) {
            return labelsProvider;
        }
        return getLabelsProvider(treeNode.getModelObject());
    }

    protected abstract ITreeNodeLabelsProvider getLabelsProviderForNonModelNode(final ITreeNode treeNode);

    @Override
    public final ITreeNodeLabelsProvider getLabelsProvider(final IModelObject modelObject) {
        final ITreeNodeLabelsProvider labelsProvider = getLabelsProviderForSpecificModelObject(modelObject);
        if (labelsProvider != null) {
            return labelsProvider;
        }
        if (modelObject != null) {
            return new DefaultNodeLabelsProvider(modelObject);
        }
        return new NullNodeLabelsProvider();
    }

    protected abstract ITreeNodeLabelsProvider getLabelsProviderForSpecificModelObject(final IModelObject modelObject);

}
