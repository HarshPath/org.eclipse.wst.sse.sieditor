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
package org.eclipse.wst.sse.sieditor.command.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.transaction.impl.TransactionImpl;
import org.eclipse.emf.workspace.AbstractEMFOperation;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;

/**
 * Abstract command to be implemented for ensuring that the notificaiton's are
 * delivered if the command is successful.
 */

public abstract class AbstractNotificationOperation extends AbstractEMFOperation {

    private final IModelObject[] modelObjects;
    protected IModelObject modelObject;
    protected final IModelRoot root;
    protected final String operationLabel;

    /**
     * Constructs a new command
     * 
     * @param root
     *            - Model root for getting the listeners
     * @param modelObjToRefresh
     *            - Changed model object
     */
    public AbstractNotificationOperation(final IModelRoot root, final IModelObject modelObjToRefresh, final String operationLabel) {
        this(root, new IModelObject[] { modelObjToRefresh }, operationLabel);
    }

    /**
     * Constructs a new command
     * 
     * @param root
     *            - Model root for getting the listeners
     * @param modelObject
     *            - Changed model objects
     */
    public AbstractNotificationOperation(final IModelRoot root, final IModelObject[] modelObjsToRefresh,
            final String operationLabel) {
        super(root.getEnv().getEditingDomain(), operationLabel);

        this.modelObjects = modelObjsToRefresh == null ? new IModelObject[0] : modelObjsToRefresh;
        this.modelObject = modelObjects.length > 0 ? modelObjects[0] : null;
        this.root = root;
        this.operationLabel = operationLabel;

        initializeOperationOptions();
    }

    /**
     * Utility method, responsible for the operation options initialization
     */
    protected void initializeOperationOptions() {
        final Map<Object, Object> options = new HashMap<Object, Object>();
        options.put(Transaction.OPTION_NO_VALIDATION, Boolean.TRUE);
        options.put(Transaction.OPTION_NO_UNDO, Boolean.TRUE);
        setOptions(options);
    }

    @Override
    protected IStatus doExecute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        return run(monitor, info);
    }

    public abstract IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException;
    
    public boolean shouldNotifyOnDidCommit() {
        return true;
    }

    @Override
    protected void didCommit(final Transaction transaction) {
        super.didCommit(transaction);
        if (transaction.getParent() != null) {
            transferNotificationsToParent(transaction);
        }
    }

    protected void transferNotificationsToParent(final Transaction transaction) {
        final List<Notification> currentNotifications = ((TransactionImpl) transaction).getNotifications();
        final TransactionImpl parent = (TransactionImpl) ((TransactionImpl) transaction).getParent();
        if (parent != null) {
            final List<Notification> parentNotifications = parent.getNotifications();
            for (final Notification currents : currentNotifications) {
                if (!parentNotifications.contains(currents)) {
                    parent.add(currents);
                }
            }
        }
    }

    @Override
    protected void didRedo(final Transaction tx) {
        super.didRedo(tx);
        processPostUndoRedo(tx);
    }

    @Override
    protected void didUndo(final Transaction tx) {
        super.didUndo(tx);
        processPostUndoRedo(tx);
    }

    private void processPostUndoRedo(final Transaction tx) {
        if (tx.getParent() != null) {
            transferNotificationsToParent(tx);
        }
    }

    public IModelRoot getModelRoot() {
        return root;
    }

    public IModelObject[] getModelObjects() {
        return this.modelObjects;
    }

    public IModelObject getModelObject() {
        return modelObject;
    }
}
