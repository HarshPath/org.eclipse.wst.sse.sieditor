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
package org.eclipse.wst.sse.sieditor.test.model.validation;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomainEvent;
import org.eclipse.wst.sse.sieditor.model.validation.EditingDomainListener;
import org.junit.Test;

public class EditingDomainListenerTest {

    private EditingDomainListener editingDomainListener;

    @Test
    public void validate_OptionNoValidation_True() {
        final boolean called[] = { false };

        editingDomainListener = new EditingDomainListener(null) {
            @Override
            protected IStatus executeValidationCommand(final TransactionalEditingDomainEvent event) throws ExecutionException {
                called[0] = true;
                return null;
            }
        };

        final TransactionalEditingDomain editingDomain = createMock(TransactionalEditingDomain.class);
        final Transaction transaction = createMock(Transaction.class);

        final Map optionsMap = new HashMap<String, Object>();
        optionsMap.put(Transaction.OPTION_NO_VALIDATION, Boolean.TRUE);

        expect(transaction.getOptions()).andReturn(optionsMap);

        replay(transaction);

        final TransactionalEditingDomainEvent event = new TransactionalEditingDomainEvent(editingDomain, -1, transaction);

        editingDomainListener.transactionClosed(event);
        assertFalse(called[0]);
    }

    @Test
    public void validate_OptionNoValidation_False() {
        final boolean called[] = { false };

        editingDomainListener = new EditingDomainListener(null) {
            @Override
            protected IStatus executeValidationCommand(final TransactionalEditingDomainEvent event) throws ExecutionException {
                called[0] = true;
                return Status.OK_STATUS;
            }
        };

        final TransactionalEditingDomain editingDomain = createMock(TransactionalEditingDomain.class);
        final Transaction transaction = createMock(Transaction.class);

        final Map optionsMap = new HashMap<String, Object>();
        optionsMap.put(Transaction.OPTION_NO_VALIDATION, Boolean.FALSE);

        expect(transaction.getOptions()).andReturn(optionsMap);
        expect(transaction.getStatus()).andReturn(Status.OK_STATUS);

        replay(transaction);

        final TransactionalEditingDomainEvent event = new TransactionalEditingDomainEvent(editingDomain, -1, transaction);

        editingDomainListener.transactionClosed(event);
        assertTrue(called[0]);
    }

}
