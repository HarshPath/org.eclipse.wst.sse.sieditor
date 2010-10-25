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

import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.w3c.dom.Document;

import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;

/**
 * This is the interface for all the {@link IModelRoot} factories. Implementors
 * are responsible for the creation of the model root
 * 
 * 
 * 
 */
public interface IModelRootFactory {

    /**
     * @param document
     *            - the model to create the {@link IStructuredModel} for
     * @return the created {@link IModelRoot}
     * 
     */
    public IModelRoot createModelRoot(Document document);

}
