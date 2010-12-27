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
package org.eclipse.wst.sse.sieditor.command.emf.xsd;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.impl.XSDImportImpl;

import org.eclipse.wst.sse.sieditor.command.common.AbstractXSDNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;

public class ResolveImportedSchemaCommand extends AbstractXSDNotificationOperation {

    private final XSDImportImpl xsdImport;
    private XSDSchema resolvedSchema;

    public ResolveImportedSchemaCommand(XSDImportImpl xsdImport, IXSDModelRoot root, IModelObject object) {
        super(root, object, "resolve schema from import element command"); //$NON-NLS-1$
        this.xsdImport = xsdImport;
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        xsdImport.reset();
        resolvedSchema = xsdImport.importSchema();
        return Status.OK_STATUS;
    }

    public XSDSchema getResolvedSchema() {
        return resolvedSchema;
    }

    @Override
    protected IStatus doExecute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        // Do not notify listeners, because of infinite loop.
        // Infinite loop is caused by use of this command in
        // org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema.getReferredSchemas()
        return run(monitor, info);
    }
    
    @Override
    public boolean shouldNotifyOnDidCommit() {
        // Because the command is used in model methods for resolving schemas on model load,
        // it should not notify the listeners. Otherwise the editor will become dirty on open.
        return false;
    }

}
