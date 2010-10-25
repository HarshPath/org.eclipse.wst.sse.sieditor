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

import javax.wsdl.Fault;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.Operation;

import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.WSDLAnalyzer;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;

/**
 * Command for removing the fault elements in operations
 * 
 * 
 * 
 */
public class DeleteFaultCommand extends AbstractWSDLNotificationOperation {
	private final IFault fault;

    public DeleteFaultCommand(final IWsdlModelRoot root, final IOperation component, final IFault fault) {
        super(root, component, Messages.DeleteFaultCommand_delete_fault_command_label);
		this.fault = fault;
    }

    public boolean canExecute() {
        return !(null == fault || null == modelObject || null == getModelRoot());
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        final ServiceOperation component = (ServiceOperation) modelObject;
        final Operation operation = component.getComponent();
        Fault wsdlFault =  fault.getComponent();
        if (null == wsdlFault || !operation.getEFaults().contains(wsdlFault)) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
            		MessageFormat.format(Messages.DeleteFaultCommand_msg_can_not_find_fault_component_for_X, fault.getName()));
        }

        Description description = (Description) getModelRoot().getDescription();
        WSDLAnalyzer wsdlAnalyzer = new WSDLAnalyzer(description.getComponent());
        ServiceInterface serviceInterface = (ServiceInterface) component.getParent();

        wsdlAnalyzer.removeFaultBindings(serviceInterface.getComponent(), operation, wsdlFault);

        // Remove faults from the EMF model as well
        operation.getEFaults().remove(wsdlFault);

        return Status.OK_STATUS;
    }
}