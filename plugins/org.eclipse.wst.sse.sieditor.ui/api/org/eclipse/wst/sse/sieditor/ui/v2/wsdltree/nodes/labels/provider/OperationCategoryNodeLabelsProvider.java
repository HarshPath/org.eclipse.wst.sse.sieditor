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
package org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.labels.provider;

import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.labels.provider.ITreeNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategory;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategoryNode;

public class OperationCategoryNodeLabelsProvider implements ITreeNodeLabelsProvider {

    private final OperationCategoryNode operationCategoryNode;

    public OperationCategoryNodeLabelsProvider(final OperationCategoryNode operationCategoryNode) {
        this.operationCategoryNode = operationCategoryNode;
    }

    @Override
    public String getDisplayName() {
        if (operationCategoryNode.getOperationCategory().equals(OperationCategory.INPUT))
            return Messages.SI_INPUT_XTND;
        else if (operationCategoryNode.getOperationCategory().equals(OperationCategory.OUTPUT))
            return Messages.SI_OUTPUT_XTND;
        else if (operationCategoryNode.getOperationCategory().equals(OperationCategory.FAULT))
            return Messages.SI_FAULTS_XTND;
        else
            return null;
    }

    @Override
    public String getTreeDisplayText() {
        return getDisplayName();
    }

    @Override
    public String getTypeDisplayText() {
        return UIConstants.EMPTY_STRING;
    }

}
