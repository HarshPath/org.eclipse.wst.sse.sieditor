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

import java.util.List;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.NameGenerator;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceInterface;

public class AddServiceInterfaceAndOperationCommand extends AbstractCompositeEnsuringDefinitionNotificationOperation {

    private final AddServiceInterfaceCommand addServiceInterfaceCommand;
    private ServiceInterface serviceInterface;
    public AddServiceInterfaceAndOperationCommand(IWsdlModelRoot root, IDescription modelObject, String newSIName) {
        super(root, modelObject, Messages.AddServiceInterfaceCommand_add_service_interface_command_label);
        addServiceInterfaceCommand = new AddServiceInterfaceCommand(root, modelObject, newSIName);
    }

    // TODO REFACTOR!
    @Override
    public AbstractNotificationOperation getNextOperation(List<AbstractNotificationOperation> subOperations) {
        AbstractNotificationOperation nextOperation = super.getNextOperation(subOperations);
        if (nextOperation != null) {
            return nextOperation;
        }
        if (subOperations.isEmpty() || (isDefinitionEnsured() && subOperations.size() == 1)) {
            return addServiceInterfaceCommand;
        } else if (subOperations.size() == 1 || (isDefinitionEnsured() && subOperations.size() == 2)) {
            serviceInterface = addServiceInterfaceCommand.getInterface();
            // add operation so that the interface is not empty
            String newOperationName = NameGenerator.getNewOperationName(serviceInterface);
            AddOperationCommand addOperationCommand = new AddOperationCommand((IWsdlModelRoot) getModelRoot(), serviceInterface, newOperationName,
                    OperationType.REQUEST_RESPONSE);
            return addOperationCommand;
        }
        return null;
    }

    public ServiceInterface getServiceInterface() {
        return serviceInterface;
    }
}
