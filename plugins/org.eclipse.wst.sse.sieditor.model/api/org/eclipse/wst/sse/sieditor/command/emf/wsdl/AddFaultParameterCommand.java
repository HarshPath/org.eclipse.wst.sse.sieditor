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

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;

/**
 * Command for adding a fault parameter for fault
 * 
 * 
 */
public class AddFaultParameterCommand extends AbstractAddParameterCommand {

    private final IFault _fault;

    public AddFaultParameterCommand(final IWsdlModelRoot root, final IFault component, final String name,
            final IOperation operation) {
        super(root, operation, name);
        this._fault = component;
    }
    
    
	@Override
    protected IStatus createMessageReference(Operation operation, MessageReference[] createdMessageReference) throws ExecutionException {
		createdMessageReference[0] = ((OperationFault)_fault).getComponent();
        return Status.OK_STATUS;
    }

    @Override
    protected byte getParameterType() {
        return IParameter.FAULT;
    }


	@Override
	protected String getNewMessageSuffix() {
		return ServiceOperation.OPERATION_FAULT_SUFFIX;
	}
}
