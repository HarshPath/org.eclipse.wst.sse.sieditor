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
 *    Keshav Veerapaneni - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.command.emf.wsdl;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.MessageReference;
import org.eclipse.wst.wsdl.Operation;

import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;

/**
 * 
 * 
 * 
 */
public class AddInParameterCommand extends AbstractAddParameterCommand {
    public AddInParameterCommand(IWsdlModelRoot root, final IOperation component, String name) {
        super(root, component, name);
    }

    @Override
    protected IStatus createMessageReference(Operation operation, MessageReference[] createdMessageReference) throws ExecutionException {
    	createdMessageReference[0] = operation.getEInput();
        IStatus status = Status.OK_STATUS;
        if (null == createdMessageReference[0]) {
            // Create Input
        	if (Logger.isDebugEnabled()) {
        		Logger.getDebugTrace().trace("", "Input message not present for operation " + operation.getName()); //$NON-NLS-1$ //$NON-NLS-2$
        	}

            AddInputCommand command = new AddInputCommand(getModelRoot(), operation, null, ((ServiceOperation) modelObject).getOperationStyle());
            status = getModelRoot().getEnv().execute(command);
           	if(StatusUtils.canContinue(status)) {
           		createdMessageReference[0] = operation.getEInput();
           	}
        }
        return status;
    }

    @Override
    protected byte getParameterType() {
        return IParameter.INPUT;
    }

	@Override
	protected String getNewMessageSuffix() {
		return ServiceOperation.OPERATION_INMSG_SUFFIX;
	}
}