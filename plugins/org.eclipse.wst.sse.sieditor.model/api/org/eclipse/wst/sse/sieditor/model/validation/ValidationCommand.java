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
package org.eclipse.wst.sse.sieditor.model.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.AbstractEMFOperation;

public class ValidationCommand extends AbstractEMFOperation {
    
    private final ArrayList<Notification> notifications;
    private final ValidationService validationService;

    public ValidationCommand(final TransactionalEditingDomain domain, final String label, final List<Notification> notifications, final ValidationService validationService) {
        super(domain, label);
        this.validationService = validationService;
        this.notifications = new ArrayList<Notification>(notifications);
        // this is the validation operation. No validation needs to be
        // started after its transaction is commited
        final Map<String, Boolean> options = new HashMap<String, Boolean>();
        options.put(Transaction.OPTION_NO_VALIDATION, Boolean.TRUE);
        setOptions(options);
        setReuseParentTransaction(false);

    }

    @Override
    public IStatus doExecute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        if (notifications.isEmpty()) {
            return Status.OK_STATUS;
        }
        validationService.liveValidate(notifications);
        return Status.OK_STATUS;
    }

    @Override
    public boolean canRedo() {
        return false;
    }

    @Override
    public boolean canUndo() {
        return false;
    }

}