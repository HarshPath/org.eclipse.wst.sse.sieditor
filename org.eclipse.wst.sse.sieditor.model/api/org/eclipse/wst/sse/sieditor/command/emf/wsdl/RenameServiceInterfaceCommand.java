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

import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.Definition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceInterface;

/**
 * Command for renaming a service interface object
 * 
 * 
 * 
 */
public class RenameServiceInterfaceCommand extends AbstractWSDLNotificationOperation {
    private final String name;

    public RenameServiceInterfaceCommand(IWsdlModelRoot root, final IServiceInterface component, final String name) {
        super(root, component, Messages.RenameServiceInterfaceCommand_rename_service_interface_label);
        this.name = name;
    }

    @SuppressWarnings("unchecked")
	@Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        final ServiceInterface intf = (ServiceInterface) modelObject;
        final Description description = (Description) getModelRoot().getDescription();
        final Definition definition = description.getComponent();
        final QName newName = new QName(intf.getNamespace(), name);
        intf.getComponent().setQName(newName);

        final List<Binding> bindings = definition.getEBindings();
        for (Binding binding : bindings) {
            if (intf.getComponent().equals(binding.getEPortType()))
                binding.updateElement();
        }
        return Status.OK_STATUS;
    }
}