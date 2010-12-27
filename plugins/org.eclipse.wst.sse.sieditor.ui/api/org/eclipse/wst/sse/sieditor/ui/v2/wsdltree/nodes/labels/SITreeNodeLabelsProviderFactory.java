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
package org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.labels;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.labels.AbstractTreeNodeLabelsProviderFactory;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.labels.provider.ITreeNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ImportedServicesNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategoryNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.labels.provider.FaultNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.labels.provider.ImportedServiceNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.labels.provider.ImportedServicesNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.labels.provider.OperationCategoryNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.labels.provider.ParameterNodeLabelsProvider;

public class SITreeNodeLabelsProviderFactory extends AbstractTreeNodeLabelsProviderFactory {

    private static final SITreeNodeLabelsProviderFactory INSTANCE = new SITreeNodeLabelsProviderFactory();

    private SITreeNodeLabelsProviderFactory() {

    }

    public static SITreeNodeLabelsProviderFactory instance() {
        return INSTANCE;
    }

    @Override
    protected ITreeNodeLabelsProvider getLabelsProviderForNonModelNode(final ITreeNode treeNode) {
        if (treeNode instanceof OperationCategoryNode) {
            return new OperationCategoryNodeLabelsProvider((OperationCategoryNode) treeNode);
        }
        if (treeNode instanceof ImportedServicesNode) {
            return new ImportedServicesNodeLabelsProvider();
        }
        return null;
    }

    @Override
    protected ITreeNodeLabelsProvider getLabelsProviderForSpecificModelObject(final IModelObject modelObject) {
        if (modelObject instanceof IParameter) {
            return new ParameterNodeLabelsProvider((IParameter) modelObject);
        }
        if (modelObject instanceof IFault) {
            return new FaultNodeLabelsProvider((IFault) modelObject);
        }
        if (modelObject instanceof IDescription) {
            return new ImportedServiceNodeLabelsProvider((IDescription) modelObject);
        }
        return null;
    }

}
