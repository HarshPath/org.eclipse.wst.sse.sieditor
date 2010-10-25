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
package org.eclipse.wst.sse.sieditor.ui.v2;

import org.eclipse.wst.sse.sieditor.model.api.INamedObject;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategory;

public class UIUtils {

    public static String getDisplayName(INamedObject namedObject) {
        return namedObject.getName() == null ? UIConstants.EMPTY_STRING : namedObject.getName();
    }

    public static int getCorrespondingITreenodeCategory(OperationCategory operationCategory) {
        int nodeCategory = ITreeNode.CATEGORY_FAULT;
        
        if(operationCategory == OperationCategory.INPUT) {
            nodeCategory = ITreeNode.CATEGORY_INPUT;
        }
        else if(operationCategory == OperationCategory.OUTPUT) {
            nodeCategory = ITreeNode.CATEGORY_OUTPUT;
        }
        return nodeCategory;
    }
}
