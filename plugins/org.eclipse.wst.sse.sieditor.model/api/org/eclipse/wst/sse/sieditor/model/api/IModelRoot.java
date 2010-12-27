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
 *    Keshav Veerapaneni - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.model.api;

import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;

/**
 * Root object for getting the respective model. Supports all the common
 * fucntionaliy e.g. adding and removing the listeners
 * 
 * 
 */
public interface IModelRoot extends org.eclipse.wst.sse.sieditor.core.editorfwk.IModelObject {

    public boolean addChangeListener(IChangeListener listener);

    public boolean removeChangeListener(IChangeListener listener);

    public void notifyListeners(IModelChangeEvent event);

    public IEnvironment getEnv();

    public IModelObject getModelObject();

    /**
     * 
     * @return the root of all models or this if this is the root of all models
     */
    public IModelRoot getRoot();
    
}