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

import javax.xml.namespace.QName;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.PortType;

import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceInterface;

/**
 * Command for adding a new service interface object
 * 
 * 
 * 
 */
public class AddServiceInterfaceCommand extends AbstractWSDLNotificationOperation {
    private final String _name;
    private ServiceInterface intf;

    public AddServiceInterfaceCommand(final IWsdlModelRoot root, final IDescription description, final String name) {
        super(root, description, Messages.AddServiceInterfaceCommand_add_service_interface_command_label);
        this._name = name;
    }

    public boolean canExecute() {
        return !(null == getModelRoot() || null == modelObject || null == _name);
    }

    public ServiceInterface getInterface() {
        return intf;
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        final Description description = (Description) modelObject;
        final Definition definition = description.getComponent();
        final PortType portType = (PortType) definition.createPortType();
        portType.setEnclosingDefinition(definition);
        portType.setQName(new QName(definition.getTargetNamespace(), _name));
        portType.setUndefined(false);
        definition.addPortType(portType);

        intf = new ServiceInterface(getModelRoot(), description, portType);

        return Status.OK_STATUS;
    }
}