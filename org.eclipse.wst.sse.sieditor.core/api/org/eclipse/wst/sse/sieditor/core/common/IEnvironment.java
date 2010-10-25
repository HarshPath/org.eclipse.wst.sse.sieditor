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
package org.eclipse.wst.sse.sieditor.core.common;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.AbstractEMFOperation;

/**
 *  Provides access to common environmental properties of an
 *         editor instance.
 */
public interface IEnvironment {

    TransactionalEditingDomain getEditingDomain();

    String getId();

    void setUndoContext(IUndoContext undoContext);

    IUndoContext getUndoContext();

    void setOperationHistory(IOperationHistory operationHistory);

    IOperationHistory getOperationHistory();

    IStatus execute(AbstractEMFOperation operation) throws ExecutionException;

    IStatus execute(AbstractEMFOperation operation, IProgressMonitor monitor) throws ExecutionException;

    IStatus execute(AbstractEMFOperation operation, IProgressMonitor monitor, IAdaptable info) throws ExecutionException;

    void dispose();

    void addCommandStackListener(ICommandStackListener l);

    void removeCommandStackListener(ICommandStackListener l);

    /**
     * NOTE: this method must be explicitly called with after the operation
     * chain execution - after the wrapper command execution
     */
    void finalizeWrapperCommandExecution();

    AbstractEMFOperation getCompositeTextOperationWrapper();
    
    /**
     * All disposable objects will be disposed on {@link IEnvironment#dispose()}
     * @param disposable object which will be added to registry of disposable objects for later disposal
     * @see IDisposable
     */
    void addDisposable(IDisposable disposable);

    IModelReconcileRegistry getModelReconcileRegistry();

    void setEditValidator(IEditValidator editValidator);
    
}
