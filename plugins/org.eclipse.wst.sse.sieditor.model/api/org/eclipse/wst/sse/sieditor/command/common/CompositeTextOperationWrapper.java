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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.workspace.AbstractEMFOperation;
import org.eclipse.wst.xml.core.internal.document.XMLModelNotifier;
import org.w3c.dom.Node;

import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.core.common.IModelReconcileRegistry;
import org.eclipse.wst.sse.sieditor.model.XMLModelNotifierWrapper;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.ModelChangeEvent;
import org.eclipse.wst.sse.sieditor.model.reconcile.IModelReconciler;
import org.eclipse.wst.sse.sieditor.model.reconcile.ModelReconciler;
import org.eclipse.wst.sse.sieditor.model.utils.EmfModelPatcher;

public class CompositeTextOperationWrapper extends AbstractEMFOperation {

    private final AbstractNotificationOperation operation;

    private final List<TextCommandWrapper> textCommands = new ArrayList<TextCommandWrapper>();

    private XMLModelNotifier modelNotifier;

    /**
     * Constructs a new command
     * 
     * @param root
     *            - Model root for getting the listeners
     * @param modelObjToRefresh
     *            - Changed model object
     */
    public CompositeTextOperationWrapper(final AbstractNotificationOperation operation) {
        super(operation.getModelRoot().getEnv().getEditingDomain(), operation.getLabel());

        final Map<Object, Object> options = new HashMap<Object, Object>();
        options.put(Transaction.OPTION_NO_VALIDATION, Boolean.TRUE);
        setOptions(options);

        this.operation = operation;

        if (operation instanceof TextCommandWrapper) {
            /*
             * we are wrapping text command and we must explicitly add it to the
             * textCommands collection
             */
            addTextCommand((TextCommandWrapper) operation);
        }
    }

    public void addTextCommand(final TextCommandWrapper textCommand) {
        this.textCommands.add(textCommand);
        if (modelNotifier() != textCommand.getModelNotifier()) {
            modelNotifier = textCommand.getModelNotifier();
        }
    }

    @Override
    protected IStatus doExecute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        final IEnvironment env = operation.getModelRoot().getEnv();
        try {
            final AbstractEMFOperation compositeTextOperationWrapper = env.getCompositeTextOperationWrapper();
            if (compositeTextOperationWrapper != null && compositeTextOperationWrapper != this) {
                throw new IllegalArgumentException(
                        "It is not allowed to nest ComositeTextOperationWrapper in operation executions."); //$NON-NLS-1$
            }

            preUndoRedoOfCompositeCommand();

            final IStatus status = operation.doExecute(monitor, info);

            basicPatchEmfModel(operation.getModelRoot());

            doReconcile();

            return status;
        } finally {
            // @see - EnvironmentFactory.RunWithBusyCursor#run() comments for
            // more information
            env.finalizeWrapperCommandExecution();
        }
    }

    @Override
    protected IStatus doRedo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        preUndoRedoOfCompositeCommand();

        for (int i = 0; i < textCommands.size(); i++) {
            final TextCommandWrapper command = textCommands.get(i);
            final IStatus redoStatus = command.doRedo(monitor, info);
            if (!redoStatus.isOK()) {
                return redoStatus;
            }
        }

        postUndoRedoOfCompositeCommand();
        return Status.OK_STATUS;
    }

    @Override
    protected IStatus doUndo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        preUndoRedoOfCompositeCommand();

        for (int i = textCommands.size() - 1; i >= 0; i--) {
            final TextCommandWrapper command = textCommands.get(i);
            final IStatus undoStatus = command.doUndo(monitor, info);
            if (!undoStatus.isOK()) {
                return undoStatus;
            }
        }

        postUndoRedoOfCompositeCommand();
        return Status.OK_STATUS;
    }

    @Override
    protected void didCommit(final Transaction transaction) {
        super.didCommit(transaction);
        operation.didCommit(transaction);
        
        if (operation.shouldNotifyOnDidCommit()) {
            notifyListeners();
        }
    }

    @Override
    protected void didRedo(final Transaction tx) {
        super.didRedo(tx);
        operation.didRedo(tx);
        notifyListeners();
    }

    @Override
    protected void didUndo(final Transaction tx) {
        operation.didUndo(tx);
        super.didUndo(tx);
        notifyListeners();
    }

    @Override
    public boolean canUndo() {
        if (operation != null) {
            return operation.canUndo();
        }
        for (final TextCommandWrapper textcommand : this.textCommands) {
            if (!textcommand.canUndo()) {
                return false;
            }
        }
        return super.canUndo();
    }

    @Override
    public boolean canRedo() {
        if (operation != null) {
            return operation.canRedo();
        }
        for (final TextCommandWrapper textcommand : this.textCommands) {
            if (!textcommand.canRedo()) {
                return false;
            }
        }
        return super.canRedo();
    }

    @Override
    public boolean canExecute() {
        return operation.canExecute();
    }
    
    /**
     * Notify UI to refresh the tree items
     */
    protected void notifyListeners() {
        final IModelRoot modelRoot = operation.getModelRoot();
        modelRoot.notifyListeners(new ModelChangeEvent(modelRoot.getModelObject()));
    }

    // =========================================================
    // undo/redo execution life cycle methods
    // =========================================================

    protected void preUndoRedoOfCompositeCommand() {
        // we are not firing about to reconcile for text commands. such commands
        // are already executed and the reconcile registry is updated with the
        // changes from that execution.
        if (!(operation instanceof TextCommandWrapper)) {
            modelReconciler().aboutToReconcileModel(getModelReconcileRegistry());
        }
    }

    protected void postUndoRedoOfCompositeCommand() {
        fullPatchEmfModel(operation.getModelRoot());
        doReconcile();
    }

    protected void doReconcile() {
        if (modelNotifier() != null) {
            modelNotifier().endChanging();
        }

        if (modelReconciler().needsToReconcileModel(getModelReconcileRegistry())) {
            modelReconciler().reconcileModel(getDocumentModelRoot(), getModelReconcileRegistry());
            modelReconciler().modelReconciled(getModelReconcileRegistry(), modelNotifier());
        }
    }

    /**
     * Performs basic patch of the EMF model - skips the patch of the child
     * elements. <br>
     * <br>
     * NOTE: We need to pass the direct model root - if the modelObject is in
     * namespace of a WSDL, we pass the IXsdModelRoot.
     */
    protected void basicPatchEmfModel(final IModelRoot changedObjectDirectModelRoot) {
        EmfModelPatcher.instance().patchEMFModelAfterDomChange(changedObjectDirectModelRoot, new HashSet<Node>());
    }

    /**
     * Performs full patch of the EMF model - including child elements.<br>
     * <br>
     * NOTE: We need to pass the direct model root - if the modelObject is in
     * namespace of a WSDL, we pass the IXsdModelRoot.
     */
    protected void fullPatchEmfModel(final IModelRoot changedObjectDirectModelRoot) {
        if (modelNotifier() instanceof XMLModelNotifierWrapper) {
            EmfModelPatcher.instance().patchEMFModelAfterDomChange(changedObjectDirectModelRoot,
                    ((XMLModelNotifierWrapper) modelNotifier()).getChangedNodes());
        }
    }

    // =========================================================
    // helpers
    // =========================================================

    protected IModelRoot getDocumentModelRoot() {
        return operation.getModelRoot().getRoot();
    }

    protected IModelReconcileRegistry getModelReconcileRegistry() {
        return operation.getModelRoot().getEnv().getModelReconcileRegistry();
    }

    protected IModelReconciler modelReconciler() {
        return ModelReconciler.instance();
    }

    protected XMLModelNotifier modelNotifier() {
        return modelNotifier;
    }

}
