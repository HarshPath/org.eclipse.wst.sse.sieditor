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
package org.eclipse.wst.sse.sieditor.model.wsdl.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.wsdl.OperationType;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.PortType;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddOperationCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteOperationCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RenameServiceInterfaceCommand;
import org.eclipse.wst.sse.sieditor.core.common.CollectionTypeUtils;
import org.eclipse.wst.sse.sieditor.core.common.Condition;
import org.eclipse.wst.sse.sieditor.core.common.Nil;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.generic.DuplicateException;
import org.eclipse.wst.sse.sieditor.model.generic.IllegalInputException;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;

/**
 * 
 * 
 * 
 */
public class ServiceInterface extends AbstractWSDLComponent implements IServiceInterface,
        org.eclipse.wst.sse.sieditor.model.write.wsdl.api.IServiceInterface {

    public ServiceInterface(final IWsdlModelRoot modelRoot, final Description parent, final PortType portType) {
        super(portType, modelRoot, parent);
        Nil.checkNil(portType, "PortType"); //$NON-NLS-1$
        Nil.checkNil(parent, "parent"); //$NON-NLS-1$
    }

    @SuppressWarnings("unchecked")
    public Collection<IOperation> getAllOperations() {
        final List<IOperation> modelOperations = new ArrayList<IOperation>(1);
        final List<Operation> operations = ((PortType) component).getEOperations();
        OperationType style = null;
        for (Operation current : operations) {
            style = current.getStyle();
            // Only In-Out and Asynchronous operations are supported
            if (null != style && (OperationType.NOTIFICATION.equals(style) || OperationType.SOLICIT_RESPONSE.equals(style))) {
                continue;
            }
            modelOperations.add(new ServiceOperation(getModelRoot(), current, this));
        }
        return modelOperations;
    }

    @SuppressWarnings("unchecked")
    public List<IOperation> getOperation(final String name) {
        List<Operation> operations = CollectionTypeUtils.find(getComponent().getEOperations(), new Condition<Operation>() {
            public boolean isSatisfied(Operation in) {
                return null == name ? null == in.getName() : name.equals(in.getName());
            }
        });
        List<IOperation> serviceOperations = new ArrayList<IOperation>();
        for (Operation operation : operations) {
            serviceOperations.add(new ServiceOperation(getModelRoot(), operation, this));
        }
        return serviceOperations;
    }

    public String getName() {
        return ((PortType) component).getQName().getLocalPart();
    }

    public String getNamespace() {
        return ((PortType) component).getQName().getNamespaceURI();
    }

    public void setName(final String name) throws IllegalInputException, DuplicateException, ExecutionException {
        Nil.checkNil(name, "name"); //$NON-NLS-1$
        if (!EmfXsdUtils.isValidNCName(name))
            throw new IllegalInputException("Entered ServiceInterface name is not valid"); //$NON-NLS-1$

        getModelRoot().getEnv().execute(new RenameServiceInterfaceCommand(getModelRoot(), this, name));
    }

    public void setNamespace(final String namespace) {
        // Do Nothing
    }

    public IOperation addOperation(final String name, org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType operationType)
            throws DuplicateException, IllegalInputException, ExecutionException {
        Nil.checkNil(name, "name"); //$NON-NLS-1$
        if (!EmfXsdUtils.isValidNCName(name))
            throw new IllegalInputException("Operation name is not a valid Name"); //$NON-NLS-1$

        final AddOperationCommand command = new AddOperationCommand(getModelRoot(), this, name, operationType);
        getModelRoot().getEnv().execute(command);
        return command.getOperation();
    }

    public void removeOperation(final IOperation operation) throws ExecutionException {
        Nil.checkNil(operation, "operation"); //$NON-NLS-1$
        final DeleteOperationCommand command = new DeleteOperationCommand(getModelRoot(), this, operation);
        getModelRoot().getEnv().execute(command);
    }

    /*
     * public void removeOperation(final String name) throws ExecutionException
     * { Nil.checkNil(name, "name"); //$NON-NLS-1$ final DeleteOperationCommand
     * command = new DeleteOperationCommand(getModelRoot(), this, name);
     * getModelRoot().getEnv().execute(command); }
     */

    @Override
    public PortType getComponent() {
        return (PortType) super.getComponent();
    }
}
