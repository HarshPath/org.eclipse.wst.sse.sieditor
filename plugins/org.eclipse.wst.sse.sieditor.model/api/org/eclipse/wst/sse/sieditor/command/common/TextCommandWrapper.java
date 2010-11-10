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
package org.eclipse.wst.sse.sieditor.command.common;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.transaction.Transaction;

import org.eclipse.wst.sse.sieditor.model.XMLModelNotifierWrapper;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

/**
 * This command must be used only as a wrapper for Text commands which come from
 * the source editor.
 */
public class TextCommandWrapper extends AbstractNotificationOperation {

    private boolean isFromDidCommit = false;
    private final Command command;
    private final XMLModelNotifierWrapper modelNotifier;

    public TextCommandWrapper(final IModelRoot root, final IModelObject modelObject, final Command command,
            final XMLModelNotifierWrapper modelNotifier) {
        super(root, modelObject, Messages.TextCommand_0);
        this.command = command;
        this.modelNotifier = modelNotifier;
    }

    @Override
    protected void initializeOperationOptions() {
        final Map<Object, Object> options = new HashMap<Object, Object>();
        options.put(Transaction.OPTION_NO_VALIDATION, Boolean.TRUE);
        setOptions(options);
    }

    @Override
    /**
     * The method must not call notifyListeners().
     * The reason is that not dirty SIE must be updated automatically, when the underling WSDL file is updated.
     * In case that the listeners are updated the editor will become dirty. 
     */
    protected void didCommit(final Transaction transaction) {
        isFromDidCommit = true;
        try {
            super.didCommit(transaction);
        } finally {
            isFromDidCommit = false;
        }
    }

    @Override
    protected void notifyListeners() {
        // do not notify listeners, because the editor will become dirty in case
        // that file content is changed
        // from the system.
        if (!isFromDidCommit) {
            super.notifyListeners();
        }
    }

    @Override
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        // this method has an empty implementation in StructuredTextCommandImpl
        // but call it for completeness
        command.execute();
        return Status.OK_STATUS;
    }

    @Override
    protected IStatus doRedo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        command.redo();
        // EmfModelPatcher.instance().patchEMFModelAfterDomChange(getModelRoot(),
        // modelNotifier.getChangedNodes());
        return Status.OK_STATUS;
    }

    @Override
    protected IStatus doUndo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        command.undo();
        // EmfModelPatcher.instance().patchEMFModelAfterDomChange(getModelRoot(),
        // modelNotifier.getChangedNodes());
        return Status.OK_STATUS;
    }

    @Override
    public boolean canRedo() {
        return command.canExecute();
    }

    @Override
    public boolean canUndo() {
        return command.canUndo();
    }

    // =========================================================
    // helpers
    // =========================================================

    public XMLModelNotifierWrapper getModelNotifier() {
        return modelNotifier;
    }

    public Command getCommand() {
		return command;
	}
}
