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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.workspace.AbstractEMFOperation;
import org.eclipse.emf.workspace.EMFCommandOperation;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;

/**
 * Inherit in order to create a custom command composed of several commands.
 * 
 * The next command to be executed is determined by overriding the
 * {@link #getNextOperation(List)} method.<br>
 * 
 * The behaviour of transaction managing and undo/redo execution is defined by
 * the {@link TransactionPolicy} and is set by the
 * {@link #setTransactionPolicy(TransactionPolicy)}. <br>
 * 
 * When facing problems with inconsistent change application on undo/redo, a
 * probable fix is setting the transaction policy to
 * {@link TransactionPolicy#MULTI}
 * 
 * 
 * 
 */
public abstract class AbstractCompositeNotificationOperation extends AbstractNotificationOperation {

    protected List<AbstractNotificationOperation> subOperations;

    /**
     * Enumeration describing the collection of predefined behaviours for
     * transaction handling of the composite notification operation. See each
     * one for details:{@link #MULTI}, {@link #SINGLE}, {@link #MIXED} *(default
     * behaviour)<br>
     * When facing problems with inconsistent change application on undo/redo, a
     * probable fix is setting the transactionPolicy to Multiple, thus fine
     * graining the stack order in which transaction changes and custom
     * implementations are applied
     * 
     * @see TransactionPolicy.{@link #MULTI}<br>
     *      TransactionPolicy.{@link #SINGLE}<br>
     *      TransactionPolicy.{@link #MIXED} * <B>DEFAULT BEHAVIOUR</B> <br>
     * 
     *      changes and custom implementations are applied.
     * 
     */
    protected enum TransactionPolicy {
        /**
         * <b>Each one</b> of the composed operations will be executed in a
         * <b>separate transaction</b>.<br>
         * 
         * Calls to super.doUndo()/super.doRedo() of the <b>parent</b>
         * transaction will be <b>suppressed</b>.<br>
         * 
         * On Undo/redo, the <br>
         * {@link AbstractNotificationOperation#undo(IProgressMonitor, IAdaptable)}
         * {@link AbstractNotificationOperation#redo(IProgressMonitor, IAdaptable)}
         * <br>
         * methods will be called in propper order.<br>
         * 
         * note: policy is recomended for commands with custom implementations
         * of undo/redo.
         */
        MULTI,
        /**
         * <b>ALL</b> composed operations will be <b>forced</b> to run in the
         * <b>parent transaction</b>.<br>
         * 
         * Therefore all the changes will be contained in it.<br>
         * 
         * Calls to super.doUndo()/super.doRedo() of the parent transaction will
         * be transfered. Afterwards, in proper order, the<br>
         * 
         * {@link AbstractNotificationOperation#undo(IProgressMonitor, IAdaptable)}
         * <br>
         * {@link AbstractNotificationOperation#redo(IProgressMonitor, IAdaptable)}
         * <br>
         * methods of each operation will be called insuring each custom
         * implementation of it is applied, though not in the right moment.<br>
         * 
         * note: This mode does not guarantee that custom implementations of
         * undo/redo are applied on time.
         */
        SINGLE,
        /**
         * Operations will be run in separate or as part of the parent's
         * transaction, in regards to their
         * {@link EMFCommandOperation#isReuseParentTransaction()} property.
         * Therefore some the changes will be contained in the parent's
         * transaction.<br>
         * 
         * Calls to super.doUndo()/super.doRedo() of the parent transaction will
         * be transfered.<br>
         * 
         * {@link AbstractNotificationOperation#undo(IProgressMonitor, IAdaptable)}
         * <br>
         * {@link AbstractNotificationOperation#redo(IProgressMonitor, IAdaptable)}
         * <br>
         * methods of each operation will be called, in proper order,
         * afterwards. <br>
         * 
         * This policy applies change to all insuring that each custom
         * implementation of it is called, though not in the right moment.<br>
         * 
         * note: This mode does not guarantee that custom implementations of
         * undo/redo are applied on time.
         */
        MIXED
    }

    private TransactionPolicy transactionPolicy = TransactionPolicy.MIXED;

    /**
     * Method used to set the Transaction policy determining composed operations
     * transactions behaviour of the class.
     * 
     * @param transactionPolicy
     * 
     * @see {@link TransactionPolicy},<br>
     *      {@link AbstractCompositeNotificationOperation},<br>
     *      TransactionPolicy.{@link #MULTI}<br>
     *      TransactionPolicy.{@link #SINGLE}<br>
     *      TransactionPolicy.{@link #MIXED} * <B>DEFAULT BEHAVIOUR</B> <br>
     */
    public void setTransactionPolicy(TransactionPolicy transactionPolicy) {
        this.transactionPolicy = transactionPolicy;
    }

    public AbstractCompositeNotificationOperation(final IModelRoot root, final IModelObject modelObjToRefresh,
            final String operationLabel) {
        super(root, modelObjToRefresh, operationLabel);
        subOperations = new ArrayList<AbstractNotificationOperation>(2);
    }

    public AbstractCompositeNotificationOperation(final IModelRoot root, final IModelObject[] modelObjsToRefresh,
            final String operationLabel) {
        super(root, modelObjsToRefresh, operationLabel);
        subOperations = new ArrayList<AbstractNotificationOperation>(2);
    }

    @Override
    public boolean canUndo() {
        for (final AbstractNotificationOperation entry : subOperations) {
            if (!entry.canUndo()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canRedo() {
        for (final AbstractNotificationOperation subOperation : subOperations) {
            if (!subOperation.canRedo()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        IStatus status = Status.OK_STATUS;
        AbstractNotificationOperation operation;
        while ((operation = getNextOperation(subOperations)) != null) {
            switch (transactionPolicy) {
            case SINGLE:
                operation.setReuseParentTransaction(true);
                break;
            case MULTI:
                operation.setReuseParentTransaction(false);
                break;
            }
            status = operation.execute(monitor, info);
            if (!status.isOK()) {
                break;
            }
            subOperations.add(operation);
        }
        return status;
    }

    /**
     * Override to provide the next operation to be executed.<br>
     * Called when execution starts and after each composed command execution.
     * 
     * When using {@link TransactionPolicy#MIXED}, use the
     * {@link AbstractEMFOperation#setReuseParentTransaction(boolean)} to state
     * weather the command should be executed in own separate transaction.
     * Implement such cases with caution for undo/redo changes application.
     * 
     * @param subOperations
     *            the list of already added and executed commands
     * @return {@link AbstractNotificationOperation} the next operation to be
     *         executed.
     * 
     * @see {@link TransactionPolicy},<br>
     *      {@link AbstractCompositeNotificationOperation},<br>
     *      {@link #setTransactionPolicy(TransactionPolicy)}
     */
    public abstract AbstractNotificationOperation getNextOperation(List<AbstractNotificationOperation> subOperations);

}
