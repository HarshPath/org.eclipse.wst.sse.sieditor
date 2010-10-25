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
package org.eclipse.wst.sse.sieditor.command.emf.wsdl;

import static org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils.makeMessageName;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Fault;

import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class SetFaultTypeCommand extends AbstractWSDLNotificationOperation {

    private Fault _fault;
    private SetParameterTypeCommand setParameterTypeCommand = null;
    private AddMessageCommand addMessageCommand = null;
    private final IType type;

    public SetFaultTypeCommand(IWsdlModelRoot root, final IFault component, final IType type) {
        super(root, component, Messages.AddFaultCommand_add_fault_command_label);
        this.type = type;
        this._fault = component.getComponent();
    }

    @Override
    public boolean canExecute() {
        return !(null == modelObject || null == getModelRoot());
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        IFault fault = (IFault) modelObject;
        IDescription description = ((IWsdlModelRoot) fault.getModelRoot()).getDescription();
        Definition definition = description.getComponent();

        final String msgName = makeMessageName(_fault.getName(), ServiceOperation.OPERATION_FAULT_SUFFIX, definition);
        addMessageCommand = new AddMessageCommand(description, _fault, msgName, null);
        IStatus status = getModelRoot().getEnv().execute(addMessageCommand);
        if (!StatusUtils.canContinue(status)) {
            return status;
        }

        final OperationParameter parameter = (OperationParameter) fault.getParameters().iterator().next();
        setParameterTypeCommand = new SetParameterTypeCommand(parameter, type);
        status = getModelRoot().getEnv().execute(setParameterTypeCommand);
        if (!StatusUtils.canContinue(status)) {
            return status;
        }

        return Status.OK_STATUS;
    }

}
