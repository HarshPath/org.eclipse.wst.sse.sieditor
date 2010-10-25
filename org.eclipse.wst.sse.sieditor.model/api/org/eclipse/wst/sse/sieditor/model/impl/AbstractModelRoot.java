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
package org.eclipse.wst.sse.sieditor.model.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.resource.Resource;

import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IChangeListener;
import org.eclipse.wst.sse.sieditor.model.api.IModelChangeEvent;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.EnvironmentFactory;

public abstract class AbstractModelRoot implements IModelRoot {

    private final IEnvironment env;

    private final Collection<IChangeListener> listeners;

    private boolean isReadOnly;
    

    public AbstractModelRoot(final Resource eResource) {
        this.listeners = new HashSet<IChangeListener>(2);
        this.env = EnvironmentFactory.createEnvironment(eResource.getURI().toString(), eResource.getResourceSet());
    }

    public boolean addChangeListener(final IChangeListener listener) {
        return listeners.add(listener);
    }

    public boolean removeChangeListener(final IChangeListener listener) {
        return listeners.remove(listener);
    }

    public Collection<IChangeListener> get_listeners() {
        return Collections.unmodifiableCollection(listeners);
    }

    public void notifyListeners(final IModelChangeEvent event) {
        for (final IChangeListener listener : listeners) {
            try {
                listener.componentChanged(event);
            } catch (final Exception e) {
                // Ignore any errors in notification
                Logger.log(Activator.PLUGIN_ID, IStatus.ERROR,
                        "Exception during notification of model change listener " + listener.toString(), e); //$NON-NLS-1$
            }
        }
    }

    // @Override
    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setReadOnly(final boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    public IEnvironment getEnv() {
        return env;
    }

    public IModelRoot getRoot() {
        final IModelObject rootObject = this.getModelObject().getRoot();
        return rootObject == null ? this : rootObject.getModelRoot();
    }
    
}
