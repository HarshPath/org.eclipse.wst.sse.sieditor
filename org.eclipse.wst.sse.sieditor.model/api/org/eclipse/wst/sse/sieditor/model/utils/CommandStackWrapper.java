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
package org.eclipse.wst.sse.sieditor.model.utils;

import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.emf.workspace.impl.WorkspaceCommandStackImpl;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.undo.IStructuredTextUndoManager;
import org.eclipse.wst.sse.core.internal.undo.StructuredTextCommandImpl;
import org.eclipse.wst.sse.core.internal.undo.StructuredTextCompoundCommandImpl;

import org.eclipse.wst.sse.sieditor.core.common.ICommandStackListener;
import org.eclipse.wst.sse.sieditor.core.common.IDisposable;

public class CommandStackWrapper extends WorkspaceCommandStackImpl implements
		IDisposable {

	private HashSet<ICommandStackListener> commandStackListeners;

	private CommandStack wrappedCommandStack;
	private IStructuredTextUndoManager undoManager;
	
	public CommandStackWrapper(IOperationHistory history) {
		super(history);
		commandStackListeners = new HashSet<ICommandStackListener>(1);
	}

	@Override
	public void execute(final Command command, final Map<?, ?> options)
			throws InterruptedException, RollbackException {
		
		if (command instanceof StructuredTextCommandImpl
				|| command instanceof StructuredTextCompoundCommandImpl) {
			notifyCommandStackListeners(command);
			return;
		}
		super.execute(command, options);
	}

	@Override
	public void execute(final Command command) {
		
		if (command instanceof StructuredTextCommandImpl
				|| command instanceof StructuredTextCompoundCommandImpl) {
			notifyCommandStackListeners(command);
			return;
		}
		super.execute(command);
	}

	@Override
	public void doDispose() {
		undoManager.setCommandStack(wrappedCommandStack);
	}

	public void addCommandStackListener(final ICommandStackListener l) {
		commandStackListeners.add(l);
	}

	public void removeCommandStackListener(final ICommandStackListener l) {
		commandStackListeners.remove(l);
	}

	public void notifyCommandStackListeners(final Command command) {
		for (final ICommandStackListener l : commandStackListeners) {
			l.commandToBeExecuted(command);
		}
	}

	public void registerTo(IStructuredModel structuredModel) {
		undoManager = structuredModel.getUndoManager();
		wrappedCommandStack = undoManager.getCommandStack();
		undoManager.setCommandStack(this);
		wrappedCommandStack.flush();
	}
	
	// --- START WRAPPED COMMANDSTACK METHODS ---
	/*
	public boolean canUndo() {
		return super.canUndo() || wrappedCommandStack.canUndo();
	}

	public void undo() {
		IUndoableOperation undoOperation = getOperationHistory().getUndoOperation(
				getDefaultUndoContext());
		if(undoOperation == null) {
			wrappedCommandStack.undo();
		}
		else {
			super.undo();
		}
	}


	public boolean canRedo() {
		return super.canRedo() || wrappedCommandStack.canRedo();
	}

	public Command getUndoCommand() {
		Command undoCommand = super.getUndoCommand();
		if(undoCommand == null) {
			IUndoableOperation undoOperation = getOperationHistory().getUndoOperation(
					getDefaultUndoContext());
			if(undoOperation instanceof CompositeTextOperationWrapper) {
				undoCommand = ((CompositeTextOperationWrapper)undoOperation).getCompositeTextOperationNoEMF();
			}
		}
		Command undoCommand = super.getUndoCommand();
		return undoCommand == null ? wrappedCommandStack.getUndoCommand() : undoCommand;
	}

	public Command getRedoCommand() {
		Command redoCommand = super.getRedoCommand();
		return redoCommand == null ? wrappedCommandStack.getRedoCommand() : redoCommand;
	}

	public Command getMostRecentCommand() {
		Command mostRecentCommand = super.getMostRecentCommand();
		return mostRecentCommand == null ? wrappedCommandStack.getMostRecentCommand() : mostRecentCommand;
	}

	public void redo() {
		IUndoableOperation redoOperation = getOperationHistory().getRedoOperation(
				getDefaultUndoContext());
		if(redoOperation == null) {
			wrappedCommandStack.redo();
		}
		else {
			super.redo();
		}
	}

	public void flush() {
		super.flush();
		wrappedCommandStack.flush();
	}
*/
//	public void addCommandStackListener(CommandStackListener listener) {
//		wrappedCommandStack.addCommandStackListener(listener);
//	}
//
//	public void removeCommandStackListener(CommandStackListener listener) {
//		wrappedCommandStack.removeCommandStackListener(listener);
//	}
	
	// --- END WRAPPED COMMANDSTACK METHODS ---
}
