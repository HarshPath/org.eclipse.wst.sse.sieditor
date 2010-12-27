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
package org.eclipse.wst.sse.sieditor.ui.v2;

import java.util.List;

import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.reconcile.IModelReconciler;
import org.eclipse.wst.sse.sieditor.model.reconcile.ModelReconciler;
import org.eclipse.wst.sse.sieditor.ui.AbstractEditorPage;
import org.eclipse.wst.sse.sieditor.ui.view.impl.SISourceEditorPart;

public class PageChangedReconcileManager {

    public void performReconcile(final int newPageIndex, final int oldPageIndex, final List pages, final IModelRoot modelRoot) {
        if (oldPageIndex == -1) {
            return;
        }
        final Object oldPage = pages.get(oldPageIndex);
        final Object newPage = pages.get(newPageIndex);

        if (!(oldPage instanceof SISourceEditorPart) || !(newPage instanceof AbstractEditorPage)) {
            return;
        }

        modelReconciler().reconcileModel(modelRoot, modelRoot.getEnv().getModelReconcileRegistry());
    }

    protected IModelReconciler modelReconciler() {
        return ModelReconciler.instance();
    }

}
