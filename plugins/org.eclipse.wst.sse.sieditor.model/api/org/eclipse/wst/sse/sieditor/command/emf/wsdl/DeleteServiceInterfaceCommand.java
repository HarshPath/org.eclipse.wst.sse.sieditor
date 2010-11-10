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

import javax.wsdl.PortType;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.Definition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.WSDLAnalyzer;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;

/**
 * Command for deleting a service interface
 * 
 * 
 * 
 */
public class DeleteServiceInterfaceCommand extends AbstractWSDLNotificationOperation {
    private final IDescription _description;
	private final IServiceInterface serviceInterface;

    public DeleteServiceInterfaceCommand(final IWsdlModelRoot root, final IDescription description, final IServiceInterface serviceInterface) {
        super(root, description, Messages.DeleteServiceInterfaceCommand_delete_service_interface_command_label);
        this._description = description;
		this.serviceInterface = serviceInterface;
    }

    public boolean canExecute() {
        return !(null == getModelRoot() || null == _description || null == serviceInterface);
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
    	final Definition definition = ((Description) _description).getComponent();
    	WSDLAnalyzer wsdlAnalyzer = new WSDLAnalyzer(definition);
    	PortType portType = serviceInterface.getComponent();
        // Remove Bindings and ports
        wsdlAnalyzer.removeBindings(portType);
    	definition.getEPortTypes().remove(portType);

        return Status.OK_STATUS;
    }
}