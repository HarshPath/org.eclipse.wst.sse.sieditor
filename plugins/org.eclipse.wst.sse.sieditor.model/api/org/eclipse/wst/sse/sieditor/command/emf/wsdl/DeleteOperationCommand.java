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

import java.text.MessageFormat;

import javax.wsdl.Operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.PortType;

import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.WSDLAnalyzer;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;

/**
 * Command for deleting the operation
 * 
 * 
 */
public class DeleteOperationCommand extends AbstractWSDLNotificationOperation {
	private final IOperation operation;

    public DeleteOperationCommand(IWsdlModelRoot root, IServiceInterface object, IOperation operation) {
        super(root, object, Messages.DeleteOperationCommand_delete_operation_label);
		this.operation = operation;
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        final ServiceInterface intf = (ServiceInterface) modelObject;
        final PortType portType = intf.getComponent();
        final Operation wsdlOperation = ((ServiceOperation) operation).getComponent();

        // 1 - name, 2 - in message name, 3 - out message name
        if (null == wsdlOperation || !portType.getEOperations().contains(wsdlOperation)) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
            		MessageFormat.format(Messages.DeleteOperationCommand_msg_can_not_find_wsdl_operation_component_for_X, operation.getName()));
        }

        Description description = (Description) intf.getParent();
        WSDLAnalyzer wsdlAnalyzer = new WSDLAnalyzer(description.getComponent());

        // Remove Operation Bindings
        wsdlAnalyzer.removeOperationBindings(portType, wsdlOperation);
        portType.getEOperations().remove(wsdlOperation);


        return Status.OK_STATUS;
    }
}