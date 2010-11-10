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
package org.eclipse.wst.sse.sieditor.model.factory;

import java.util.List;

import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.w3c.dom.Document;

import org.eclipse.wst.sse.sieditor.core.common.IDisposable;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.AbstractModelReconcileAdapter;

/**
 * This is the base class for all the {@link IModelRootFactory} implementations.
 * Subclasses are responsible for the creation of the custom models
 * 
 * 
 * 
 */
public abstract class AbstractModelRootFactory implements IModelRootFactory {

	/**
	 * @param document
	 *            - the document to get the model root for
	 * @return the created model root
	 */
	protected abstract IModelRoot createModelRootFromDocument(
			final Document document);

    protected abstract List<IModelStateListener> getCustomModelStateListeners(final IModelRoot modelRoot);

    @Override
    public IModelRoot createModelRoot(final Document document) {
        final IStructuredModel structuredModel = ((IDOMDocument) document).getModel();
        final IModelRoot modelRoot = createModelRootFromDocument(document);
        registerCustomModelAdapters(modelRoot, document, structuredModel);
        return modelRoot;
    }

    /**
     * Utility method. Registers all the custom listeners with the given
     * document
     */
    private void registerCustomModelAdapters(final IModelRoot modelRoot, final Document document,
            final IStructuredModel structuredModel) {
        
        final List<IModelStateListener> customAdapters = getCustomModelStateListeners(modelRoot);
        
        for (final IModelStateListener adapter : customAdapters) {
            structuredModel.addModelStateListener(adapter);
            
            if (adapter instanceof AbstractModelReconcileAdapter) {
                ((AbstractModelReconcileAdapter) adapter).setModelReconcileRegistry(modelRoot.getEnv().getModelReconcileRegistry());
                ((AbstractModelReconcileAdapter) adapter).adapt(document);
            }
            modelRoot.getEnv().addDisposable((IDisposable) adapter);
        }
    }

}
