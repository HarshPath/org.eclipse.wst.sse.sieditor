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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.Fault;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.Part;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddFaultParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteFaultParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RenameFaultCommand;
import org.eclipse.wst.sse.sieditor.core.common.CollectionTypeUtils;
import org.eclipse.wst.sse.sieditor.core.common.Condition;
import org.eclipse.wst.sse.sieditor.core.common.Nil;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.generic.DuplicateException;
import org.eclipse.wst.sse.sieditor.model.generic.IllegalInputException;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;

/**
 * 
 */
public class OperationFault extends AbstractWSDLComponent implements IFault,
        org.eclipse.wst.sse.sieditor.model.write.wsdl.api.IFault {

    //private Message _message;
    
    public OperationFault(final IWsdlModelRoot modelRoot, final IOperation operation, final Fault fault) {
        super(fault, modelRoot,operation);
        Nil.checkNil(operation, "operation"); //$NON-NLS-1$
        Nil.checkNil(fault, "fault"); //$NON-NLS-1$
        // message can be null in case of new fault
        //this._message = fault.getEMessage();
    }

    public List<IParameter> getParameter(final String name) {
        return CollectionTypeUtils.find(getParameters(), new Condition<IParameter>() {

            public boolean isSatisfied(IParameter in) {
                return null == name ? null == in.getName() : name.equals(in.getName());
            }
        });
    }

    @SuppressWarnings("unchecked")
    public Collection<IParameter> getParameters() {
        final List<IParameter> parameters = new ArrayList<IParameter>(1);
        Message message = getMessage();
        if (null != message) {
            final List<Part> parts = message.getEParts();
            for (Part current : parts) {
                parameters.add(new OperationParameter(getModelRoot(), current, this, message, IParameter.FAULT));
            }
        }
        return parameters;
    }

    public String getName() {
        return ((Fault) component).getName();
    }

    public IParameter addParameter(String name) throws IllegalInputException, DuplicateException, ExecutionException {
        // DOIT check if parameter already exists
        Nil.checkNil(name, "name"); //$NON-NLS-1$

        final AddFaultParameterCommand command = new AddFaultParameterCommand(getModelRoot(), this, name, (IOperation) getParent());
        getModelRoot().getEnv().execute(command);
        /*
        if (null == _message)
            _message = ((Fault) component).getEMessage();*/
        return command.getParameter();
    }

/*
    public boolean removeParameter(String name) throws ExecutionException {
        Nil.checkNil(name, "name"); //$NON-NLS-1$

        if (null == _message || null == _message.getPart(name))
            return false;

        final DeleteFaultParameterCommand command = new DeleteFaultParameterCommand(getModelRoot(), this, name,
                _parent);
        getModelRoot().getEnv().execute(command);
        return true;
    }
*/
    public boolean removeParameter(IParameter parameter) throws ExecutionException {
        Nil.checkNil(parameter, "parameter"); //$NON-NLS-1$
        final DeleteFaultParameterCommand command = new DeleteFaultParameterCommand(getModelRoot(), this, parameter, (IOperation) getParent());
        return getModelRoot().getEnv().execute(command) == Status.OK_STATUS;
    }


    public void setName(String name) throws IllegalInputException, DuplicateException, ExecutionException {
        Nil.checkNil(name, "name"); //$NON-NLS-1$

        final RenameFaultCommand command = new RenameFaultCommand(getModelRoot(), this, name);
        getModelRoot().getEnv().execute(command);
        ;
    }

    @Override
    public Fault getComponent() {
        return (Fault) super.getComponent();
    }
    
    private Message getMessage() {
		return getComponent().getEMessage();
	}
}