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
 *    Stanislav Nichev - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.command.emf.common;

import java.util.List;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.common.setcommandbuilder.ISetTypeCommandBuilder;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

/**
 * this is the new type command. the command is responsible for the creation of
 * the type and then making a reference to the source element.
 * 
 * 
 * 
 */
public abstract class AbstractNewTypeCompositeCommand extends BaseNewTypeCompositeCommand {

    protected AbstractNotificationOperation addTypeCommand;
    protected AbstractNotificationOperation setTypeCommand;
    protected AbstractNotificationOperation addElementCommand;

    public AbstractNewTypeCompositeCommand(final IModelRoot modelRoot, final IModelObject modelObject,
            final String operationLabel, final ISchema schema, final String typeName,
            final ISetTypeCommandBuilder setTypeCommandBuilder) {

        super(modelRoot, modelObject, operationLabel, schema, setTypeCommandBuilder, typeName);

        setTransactionPolicy(TransactionPolicy.MULTI);
    }

    @Override
    public AbstractNotificationOperation getNextOperation(final List<AbstractNotificationOperation> subOperations) {
        if (addTypeCommand == null) {
            addTypeCommand = createCreateTypeCommand();
            return addTypeCommand;
        }
        if (type == null) {
            type = extractCreatedType(addTypeCommand);
        }

        final AbstractNotificationOperation intermediateOperation = getNextIntermediateCommand();
        if (intermediateOperation != null) {
            return intermediateOperation;
        }

        if (setTypeCommand == null) {
            setTypeCommand = setTypeCommandBuilder().createSetTypeCommand(getType());
            return setTypeCommand;
        }
        // no more operations to execute. stop execution
        return null;
    }

    /**
     * @return the create type command, responsible for the creation of the new
     *         type
     */
    protected abstract AbstractNotificationOperation createCreateTypeCommand();

    /**
     * utility method. implementors should return any intermediate commands
     * between add element and set element. if no intermediate commands are
     * needed - <code>null</code> should be returned
     * 
     * @return any intermediate command to execute or <code>null</code> if such
     *         should not be executed
     */
    protected abstract AbstractNotificationOperation getNextIntermediateCommand();

    /**
     * utility method. extracts the created type by the create type command.
     * note that the create type command must already be executed prior to this
     * method call. <br>
     * implementors may not validate the successful execution of the create type
     * command
     * 
     * @param createTypeCommand
     * 
     * @return the created type by the create type command
     */
    protected abstract IType extractCreatedType(final AbstractNotificationOperation createTypeCommand);

    // ===========================================================
    // helpers
    // ==========================================================

    protected ISetTypeCommandBuilder setTypeCommandBuilder() {
        return setTypeCommandBuilder;
    }
}
