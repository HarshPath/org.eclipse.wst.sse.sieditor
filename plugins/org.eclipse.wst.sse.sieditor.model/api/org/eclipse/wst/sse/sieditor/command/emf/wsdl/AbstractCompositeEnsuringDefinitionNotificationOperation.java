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
package org.eclipse.wst.sse.sieditor.command.emf.wsdl;

import java.util.List;

import org.eclipse.wst.sse.sieditor.command.common.AbstractCompositeNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;

/**
 * Composite operation with predefined first optional
 * {@link EnsureDefinitionCommand}. <br>
 * <br>
 * When overriding {@link #getNextOperation(List)} always include a call to
 * super.getNextOperation(subOperations);, and have in mind the two possible
 * outcomes.
 * 
 * 
 * 
 * @see {@link EnsureDefinitionCommand}, <br>
 *      {@link AbstractCompositeNotificationOperation}, <br>
 *      <code>this.</code>{@link #getNextOperation(List)}<br>
 *      {@link #isDefinitionEnsured()}
 */

public abstract class AbstractCompositeEnsuringDefinitionNotificationOperation extends AbstractCompositeNotificationOperation {

    protected final EnsureDefinitionCommand ensureDefinitionCommand;
    protected boolean definitionEnsured;

    /**
     * 
     * @return
     */
    public boolean isDefinitionEnsured() {
        return definitionEnsured;
    }

    public AbstractCompositeEnsuringDefinitionNotificationOperation(IModelRoot root, IDescription modelObject,
            String operationLabel) {
        super(root, modelObject, operationLabel);
        ensureDefinitionCommand = new EnsureDefinitionCommand(root, modelObject, Messages.AbstractCompositeEnsuringDefinitionNotificationOperation_0);
        setTransactionPolicy(TransactionPolicy.MULTI);
    }

    /**
     * Custom implementation of the
     * {@link AbstractCompositeNotificationOperation#getNextOperation(List)}.<br>
     * <br>
     * MUST BE OVERRIDEN, WITH A CALL TO super.getNextOperation(subOperations);
     * 
     * @return {@link EnsureDefinitionCommand} if applicable he OR if not or
     *         already returned, <code>null</code>.
     * 
     * @see {@link #isDefinitionEnsured()}
     * 
     */
    @Override
    public AbstractNotificationOperation getNextOperation(List<AbstractNotificationOperation> subOperations) {
        if (ensureDefinitionCommand.canExecute() && subOperations.isEmpty()) {
            definitionEnsured = true;
            return ensureDefinitionCommand;
        }
        return null;
    }

}