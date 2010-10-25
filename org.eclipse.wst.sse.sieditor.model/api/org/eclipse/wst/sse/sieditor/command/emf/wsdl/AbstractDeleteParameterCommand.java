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

import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.MessageReference;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Part;

import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;

public abstract class AbstractDeleteParameterCommand extends AbstractWSDLNotificationOperation {

    protected final Part part;

    public AbstractDeleteParameterCommand(IWsdlModelRoot root, final IOperation component, final IParameter parameter) {
        super(root, component, Messages.AbstractDeleteParameterCommand_delete_operation_parameter_command_label);
		this.part = ((OperationParameter) parameter).getComponent();
    }

    public boolean canExecute() {
        return !(null == part || null == modelObject || null == getModelRoot());
    }

    protected abstract MessageReference getMessageReference(Operation operation);

    public org.eclipse.core.runtime.IStatus run(org.eclipse.core.runtime.IProgressMonitor monitor,
            org.eclipse.core.runtime.IAdaptable info) throws org.eclipse.core.commands.ExecutionException {
        final Operation operation = ((ServiceOperation) modelObject).getComponent();
        final MessageReference messageReference = getMessageReference(operation);
        if (null == messageReference) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
            		MessageFormat.format(Messages.AbstractDeleteParameterCommand_msg_can_not_find_msg_reference_of_operation_X, operation.getName()));
        }
        final Message eMessage = messageReference.getEMessage();
        if (null == eMessage) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
            		MessageFormat.format(Messages.AbstractDeleteParameterCommand_msg_can_not_find_msg_of_operation_X, operation.getName()));
        }

        // Remove part from EMF Model
        eMessage.getEParts().remove(part);
        return Status.OK_STATUS;
    }
}
