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
package org.eclipse.wst.sse.sieditor.core.editorfwk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.workspace.AbstractEMFOperation;

import org.eclipse.wst.sse.sieditor.core.SIEditorCoreActivator;
import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.core.i18n.Messages;

public class ModelHandler {
    public static IModelObject retrieveModelObject(final IEnvironment env, final URI uri, final boolean isReadonly) {
        final IModelObject[] results = new IModelObject[1];
        final AbstractEMFOperation operation = new AbstractEMFOperation(env.getEditingDomain(), Messages.ModelHandler_0) {
            @Override
            protected IStatus doExecute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
                results[0] = retrieveModelObject(getEditingDomain().getResourceSet(), uri, isReadonly);

                return Status.OK_STATUS;
            }

            @Override
            public boolean canUndo() {
                return false;
            }

            @Override
            public boolean canRedo() {
                return false;
            }
        };

        try {
            operation.execute(new NullProgressMonitor(), null);
        } catch (ExecutionException e) {
            Logger.logError(String.format("Loading of resource [%s] failed. See nested exception for details.", uri.toString())); //$NON-NLS-1$
        }

        return results[0];
    }

    public static IModelObject retrieveModelObject(URI uri) {
        return retrieveModelObject(new ResourceSetImpl(), uri, false);
    }

    public static IModelObject retrieveModelObject(final ResourceSet resourceSet, final URI uri, final boolean isReadonly) {
        final Resource resource = resourceSet.getResource(uri, true);
        final List<EObject> emfObjects = resource.getContents();
        final ExtensibleObjectFactory factory = ExtensibleObjectFactoryRegistry.get(uri);

        return factory == null ? null : factory.createModelObject(emfObjects, isReadonly);
    }

    public static IModelObject retrieveModelObject(String s, String uri) {
        ResourceSet rs = new ResourceSetImpl();
        Resource resource = rs.createResource(URI.createURI(uri));

        try {
            ByteArrayInputStream is = new ByteArrayInputStream(s.getBytes("UTF-8")); //$NON-NLS-1$
            resource.load(is, null);
        } catch (IOException e) {
            Logger.log(SIEditorCoreActivator.PLUGIN_ID, IStatus.ERROR, "Can not load resource " + resource.getURI(), e); //$NON-NLS-1$
            return null;
        }
        Object emfObjectList = resource.getContents();
        String extension = uri.substring(uri.lastIndexOf('.') + 1);
        ExtensibleObjectFactory factory = ExtensibleObjectFactoryRegistry.get(extension);
        return factory.createModelObject(emfObjectList, false);// TODO model
        // object is
        // always created
        // with false -
        // how to get the
        // correct state?
    }
}
