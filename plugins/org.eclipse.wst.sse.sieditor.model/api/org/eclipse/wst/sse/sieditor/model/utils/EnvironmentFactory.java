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
package org.eclipse.wst.sse.sieditor.model.utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.TransactionalEditingDomainImpl;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.emf.workspace.AbstractEMFOperation;
import org.eclipse.emf.workspace.WorkspaceEditingDomainFactory;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.CompositeTextOperationWrapper;
import org.eclipse.wst.sse.sieditor.command.common.TextCommandWrapper;
import org.eclipse.wst.sse.sieditor.core.common.ICommandStackListener;
import org.eclipse.wst.sse.sieditor.core.common.IDisposable;
import org.eclipse.wst.sse.sieditor.core.common.IEditValidator;
import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.core.common.IModelReconcileRegistry;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

/**
 *         A class, which is responsible for the creation of
 *         <code>IEnviroment</code> instances.
 */
public final class EnvironmentFactory {

    public final static class EnvironmentImpl implements IEnvironment, Adapter {

        private TransactionalEditingDomain editingDomain;

        private CompositeTextOperationWrapper operationWrapper = null;

        private final String id;

        private IEditValidator editValidator;

        private IUndoContext undoContext;

        private IOperationHistory operationHistory;

        private final IModelReconcileRegistry modelReconcileRegistry;
    	
    	private List<IDisposable> registryOfDisposables = new LinkedList<IDisposable>();

		private ResourceSet resourceSet;
		
		private CommandStackWrapper commandStackWrapper;
    	
        private static IEnvironment createEnvironment(final String id, final ResourceSet resourceSet) {
            for (final Adapter adapter : resourceSet.eAdapters()) {
                if (adapter instanceof IEnvironment) {
                    return (IEnvironment) adapter;
                }
            }
            final EnvironmentImpl env;

            env = new EnvironmentImpl(id);
            env.init(resourceSet);

            return env;
        }

        private void init(final ResourceSet resourceSet) {
            this.resourceSet = resourceSet;
			editingDomain = TransactionUtil.getEditingDomain(resourceSet);
            if (editingDomain == null) {
                final IWorkbenchOperationSupport operationSupport = PlatformUI.getWorkbench().getOperationSupport();
                operationHistory = operationSupport.getOperationHistory();
                commandStackWrapper = new CommandStackWrapper(operationHistory);
                addDisposable(commandStackWrapper);
                
                final TransactionalEditingDomain result = new TransactionalEditingDomainImpl(new ComposedAdapterFactory(
                        ComposedAdapterFactory.Descriptor.Registry.INSTANCE), commandStackWrapper, resourceSet);

                WorkspaceEditingDomainFactory.INSTANCE.mapResourceSet(result);
                editingDomain = result;
                editingDomain.setID(id);
            } 
            else {
            	throw new IllegalStateException("TransactionalEditingDomain for the current file already exists. " + //$NON-NLS-1$
            			"Do close all editors for the current file before openening this one. "); //$NON-NLS-1$
            }
            resourceSet.eAdapters().add(this);
        }

        private EnvironmentImpl(final String id) {
            if (id == null) {
                throw new IllegalArgumentException("The id of the environment cannot be null"); //$NON-NLS-1$
            }
            this.modelReconcileRegistry = new ModelReconcileRegistry();
            this.id = id;
        }

        public void addCommandStackListener(final ICommandStackListener listener) {
        	commandStackWrapper.addCommandStackListener(listener);
        }

        public void removeCommandStackListener(final ICommandStackListener listener) {
        	commandStackWrapper.removeCommandStackListener(listener);
        }

        public IStatus execute(final AbstractEMFOperation operation) throws ExecutionException {
            return execute(operation, null);
        }

        public IStatus execute(final AbstractEMFOperation operation, final IProgressMonitor monitor) throws ExecutionException {
            return execute(operation, monitor, null);
        }

        public IStatus execute(final AbstractEMFOperation operation, final IProgressMonitor monitor, final IAdaptable info)
                throws ExecutionException {

            IStatus status = Status.OK_STATUS;
            if (editValidator != null) {
                status = editValidator.canEdit();
            }

            if (status.isOK()) {
                final RunWithBusyCursor runWithBusyCursorRunnable = new RunWithBusyCursor(operation, monitor, info);
                BusyIndicator.showWhile(Display.getDefault(), runWithBusyCursorRunnable);
                return runWithBusyCursorRunnable.getStatus();
            }

            return status;
        }

        private class RunWithBusyCursor implements Runnable {

            private AbstractEMFOperation operation;
            private final IProgressMonitor monitor;
            private final IAdaptable info;
            private IStatus status;

            public RunWithBusyCursor(final AbstractEMFOperation operation, final IProgressMonitor monitor, final IAdaptable info) {
                this.operation = operation;
                this.monitor = monitor;
                this.info = info;
            }

            public void run() {
                if (operation == null) {
                    throw new IllegalArgumentException("Operation cannot be null"); //$NON-NLS-1$
                }

                if (undoContext == null) {
                    throw new IllegalStateException("Undo context not set - cannot proceed with operation execution"); //$NON-NLS-1$
                }

                try {
                    if (operationWrapper == null) {
                        // first operation in the chain to be executed
                        if (operation instanceof AbstractNotificationOperation) {
                            /*
                             * wrap the first operation from the chain
                             * execution. this operation is wrapped only once
                             * for the chain execution. subsequent operations
                             * have the operationWrapper already set.
                             * 
                             * NOTE: the operation wrapper must be set to null
                             * after the operation chain execution has been
                             * completed with the
                             * #setCompositeTextOperationWrapper(null)
                             */
                            operationWrapper = new CompositeTextOperationWrapper((AbstractNotificationOperation) operation);
                            operation = operationWrapper;
                        }
                        operation.addContext(undoContext);
                        status = operationHistory.execute(operation, monitor == null ? new NullProgressMonitor() : monitor, info);
                    } else {
                        // we are running subsequent operation
                        operation.addContext(undoContext);
                        operation.setReuseParentTransaction(true);
                        status = operation.execute(monitor, info);
                        if (operation instanceof TextCommandWrapper) {
                            /*
                             * explicitly add text commands to the wrapper,
                             * since we are interested in text commands only.
                             * 
                             * Note: We are going to do undo/redo of the
                             * gathered text commands
                             */
                            operationWrapper.addTextCommand((TextCommandWrapper) operation);
                        }
                    }
                } catch (final ExecutionException e) {
                    status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Execution of operation failed", e); //$NON-NLS-1$
                    Logger.log(status);
                }
            }

            public IStatus getStatus() {
                return status;
            }
        }

        public void setUndoContext(final IUndoContext undoContext) {
            if (this.undoContext == null) {
                this.undoContext = undoContext;
            }
        }

        public IUndoContext getUndoContext() {
            return undoContext;
        }

        public void setOperationHistory(final IOperationHistory operationHistory) {
            this.operationHistory = operationHistory;
        }

        public IOperationHistory getOperationHistory() {
            return operationHistory;
        }

        public String getId() {
            return id;
        }

        public TransactionalEditingDomain getEditingDomain() {
            return editingDomain;
        }

        public void dispose() {
        	for(IDisposable disposable : registryOfDisposables) {
        		disposable.doDispose();
        	}
        	registryOfDisposables.clear();
        	resourceSet.eAdapters().remove(this);
        	
            if (editingDomain != null) {
                try {
                    editingDomain.dispose();
                } finally {
                    editingDomain = null;
                }
            }
            
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }

            if (this == obj) {
                return true;
            }

            if (obj instanceof IEnvironment) {
                final IEnvironment env = (IEnvironment) obj;
                final String envId = env.getId();
                if (envId == null) {
                    return false;
                }

                return id.equals(envId);
            }

            return false;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        public Notifier getTarget() {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean isAdapterForType(final Object type) {
            // TODO Auto-generated method stub
            return false;
        }

        public void notifyChanged(final Notification notification) {
            // TODO Auto-generated method stub

        }

        public void setTarget(final Notifier newTarget) {
            // TODO Auto-generated method stub

        }

        private void setCompositeTextOperationWrapper(final AbstractEMFOperation compositeTextOperationWrapper) {
            operationWrapper = (CompositeTextOperationWrapper) compositeTextOperationWrapper;

        }

        @Override
        public void finalizeWrapperCommandExecution() {
            setCompositeTextOperationWrapper(null);
        }

        @Override
        public CompositeTextOperationWrapper getCompositeTextOperationWrapper() {
            return operationWrapper;
        }

        @Override
        public IModelReconcileRegistry getModelReconcileRegistry() {
            return modelReconcileRegistry;
        }

        public void setEditValidator(IEditValidator editValidator) {
            this.editValidator = editValidator;

        }

        public IEditValidator getEditValidator() {
            return this.editValidator;
        }

		@Override
		public void addDisposable(IDisposable disposable) {
			if(!registryOfDisposables.contains(disposable)) {
				registryOfDisposables.add(disposable);
			}
		}
		
		public List<IDisposable> getRegistryOfDisposables() {
			return Collections.unmodifiableList(registryOfDisposables);
		}
    }

    /**
     * Creates an instance of <code>IEnvironment</code>. It always return a new
     * instance.
     * 
     * @param id
     *            environment unique id
     * @param resourceSet
     *            emf resource set
     * @return a new instance of <code>IEnvironment</code>
     */
    public static IEnvironment createEnvironment(final String id, final ResourceSet resourceSet) {
        return EnvironmentImpl.createEnvironment(id, resourceSet);
    }

    private EnvironmentFactory() {
    }
}
