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
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.AbstractEMFOperation;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

/**
 * This command should not notify SIE Model
 */
public class SaveCommand extends AbstractEMFOperation{
	
	private final SaveHandler saveHandler;

	//TODO If we're going to save also xsd files, the getEnv() method should be moved to a parent interface
	public SaveCommand(TransactionalEditingDomain domain, SaveHandler saveHandler) {
		super(domain, Messages.SaveCommand_save_command_label);
		this.saveHandler = saveHandler;
		setOptions(new HashMap<Object, Object>());
	}
	
	public IStatus execute(IProgressMonitor monitor) throws ExecutionException {
		return execute(monitor, null);
	}

	@Override
	protected IStatus doExecute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		
		return saveHandler.save(monitor);
	}
	
	@SuppressWarnings("unchecked")
    @Override
    /**
     * Add Transaction.OPTION_NO_VALIDATION so that validation errors don't rollback the transaction
     */
    public void setOptions(Map<?, ?> options) {
        ((Map<Object, Object>) options).put(Transaction.OPTION_NO_VALIDATION, Boolean.TRUE);
        super.setOptions(options);
    }

}
