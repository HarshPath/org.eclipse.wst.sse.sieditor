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
 *    Stanislav Nichev - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.model.reconcile;

import org.eclipse.wst.xml.core.internal.document.XMLModelNotifier;

import org.eclipse.wst.sse.sieditor.core.common.IModelReconcileRegistry;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;

/**
 * This is the interface for the custom SIE model reconcilers. Implementors are
 * responsible for the reconciling of the model
 * 
 */
public interface IModelReconciler {

    /**
     * Control method. It notifies the model that the reconciling is about to
     * begin.<br>
     * <br>
     * <i>NOTE: This method needs to be called prior to the actual DOM
     * notification changes, so that the {@link IModelReconciler} can determine
     * what was changed and whether reconciling is indeed needed.</i>
     */
    public void aboutToReconcileModel(final IModelReconcileRegistry modelReconcileRegistry);

    /**
     * Control method. Returns whether the model needs to be reconciled. This
     * method needs to be called after the
     * {@link #aboutToReconcileModel(IModelRoot)} and before the
     * {@link #modelReconciled(IModelRoot)} methods.
     */
    public boolean needsToReconcileModel(final IModelReconcileRegistry modelReconcileRegistry);

    /**
     * The main {@link IModelReconciler} method. This method handles the actual
     * reconcilation of the underlying model.
     */
    public void reconcileModel(final IModelRoot modelRoot, final IModelReconcileRegistry modelReconcileRegistry);

    /**
     * Control method. It notifies the model that reconciling has finished.
     * @param xmlModelNotifier 
     */
    public void modelReconciled(final IModelReconcileRegistry modelReconcileRegistry, XMLModelNotifier xmlModelNotifier);

}