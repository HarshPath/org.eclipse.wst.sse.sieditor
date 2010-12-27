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
package org.eclipse.wst.sse.sieditor.model.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.transaction.TransactionalEditingDomainEvent;
import org.eclipse.emf.transaction.TransactionalEditingDomainListenerImpl;
import org.eclipse.emf.transaction.impl.TransactionImpl;

import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

public class EditingDomainListener extends TransactionalEditingDomainListenerImpl {

    private final List<Notification> notifications = new ArrayList<Notification>(10);
    private boolean validating = false;

    private final ValidationService validationService;

    public EditingDomainListener(final ValidationService validationService) {
        this.validationService = validationService;
    }

    @Override
    public void transactionClosing(final TransactionalEditingDomainEvent event) {

        final Map<?, ?> transactionOptions = event.getTransaction().getOptions();
        // if this is our batch validation's transaction no need to validate
        // again
        // we do not need to validate the abstract notification operations
        // either
        if (Boolean.TRUE.equals(transactionOptions.get(ValidationService.OPTION_BATCH_VALIDTION))) {
            return;
        }
        if (!validating) {
            final List<Notification> list = ((TransactionImpl) event.getTransaction()).getNotifications();
            notifications.addAll(list);
        }
    }

    @Override
    public void transactionClosed(final TransactionalEditingDomainEvent event) {
        if (validating || Boolean.TRUE.equals(event.getTransaction().getOptions().get(Transaction.OPTION_NO_VALIDATION))) {
            return;
        }

        if (!event.getTransaction().getStatus().isOK()) {
            return;
        }

        IStatus status = null;
        validating = true;

        try {
            status = executeValidationCommand(event);
        } catch (final Exception e) {
            status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, "validation did not complete successfully", e); //$NON-NLS-1$
        } finally {
            validating = false;
            notifications.clear();
        }

        if (!status.isOK()) {
            Logger.log(status);
        }
    }

    protected IStatus executeValidationCommand(final TransactionalEditingDomainEvent event) throws ExecutionException {
        final ValidationCommand validateOperation = new ValidationCommand(event.getSource(), Messages.ValidationService_1,
                notifications, validationService);
        return validateOperation.execute(null, null);
    }

}
