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
package org.eclipse.wst.sse.sieditor.ui.v2.providers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.common.AbstractEditorLabelProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;

public class WSDLLabelProvider extends AbstractEditorLabelProvider {

    public WSDLLabelProvider() {
    }

    @Override
    public String getText(final Object element) {
        if (!(element instanceof ITreeNode)) {
            return UIConstants.EMPTY_STRING;
        }
        final ITreeNode treeNode = (ITreeNode) element;
        return treeNode.getTreeDisplayText();
    }

    @Override
    public Color getForeground(final Object element) {
        return Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
    }

}