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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.wsdl.Fault;
import org.eclipse.wst.wsdl.Input;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Output;
import org.eclipse.wst.wsdl.Part;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddFaultCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddInParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddOutParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.ChangeOperationTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteFaultCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteInParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteOutParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RenameOperationCommand;
import org.eclipse.wst.sse.sieditor.core.common.CollectionTypeUtils;
import org.eclipse.wst.sse.sieditor.core.common.Condition;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.core.common.Nil;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.generic.DuplicateException;
import org.eclipse.wst.sse.sieditor.model.generic.IllegalInputException;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.utils.WSDLAnalyzer;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType;

/**
 * 
 * 
 * 
 */
public class ServiceOperation extends AbstractWSDLComponent implements IOperation,
        org.eclipse.wst.sse.sieditor.model.write.wsdl.api.IOperation {
    public static final String OPERATION_INMSG_SUFFIX = "Request"; //$NON-NLS-1$
    public static final String OPERATION_OUTMSG_SUFFIX = "Response"; //$NON-NLS-1$
    public static final String OPERATION_FAULT_SUFFIX = "Fault"; //$NON-NLS-1$

    public ServiceOperation(final IWsdlModelRoot modelRoot, final Operation operation, final IServiceInterface sInterface) {
        super(operation, modelRoot, sInterface);
        Nil.checkNil(operation, "operation"); //$NON-NLS-1$
        Nil.checkNil(sInterface, "sInterface"); //$NON-NLS-1$
    }

    @SuppressWarnings("unchecked")
    public Collection<IFault> getAllFaults() {
        final List<IFault> faultParameters = new ArrayList<IFault>(1);
        final List<Fault> faults = ((Operation) component).getEFaults();
        for (Fault fault : faults) {
            faultParameters.add(new OperationFault(getModelRoot(), this, fault));
        }

        return faultParameters;
    }

    public Collection<IParameter> getAllInputParameters() {
        final List<IParameter> inParameters = new ArrayList<IParameter>(1);
        final Operation operation = (Operation) component;
        final Input input = operation.getEInput();
        if (null == input && null != operation.getEOutput()) {
            if (Logger.isDebugEnabled())
                Logger.getDebugTrace().trace("", "Notification Operation"); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            if (null != input && null != input.getEMessage()) {
                final Message message = input.getEMessage();
                processParts(inParameters, message, IParameter.INPUT);
            }
        }

        return inParameters;
    }

    public Collection<IParameter> getAllOutputParameters() {
        final List<IParameter> outParameters = new ArrayList<IParameter>(1);
        final Output output = ((Operation) component).getEOutput();
        if (null == output) {
            if (Logger.isDebugEnabled())
                Logger.getDebugTrace().trace("", "Asynchronous Operation"); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            if (null != output.getEMessage()) {
                final Message message = output.getEMessage();
                processParts(outParameters, message, IParameter.OUTPUT);
            }
        }

        return outParameters;
    }

    @SuppressWarnings("unchecked")
    private void processParts(final List<IParameter> parameters, final Message message, byte parameterType) {
        final List<Part> parts = message.getEParts();
        for (Part current : parts) {
            parameters.add(new OperationParameter(getModelRoot(), current, this, message, parameterType));
        }
    }

    public String getName() {
        return ((Operation) component).getName();
    }

    public boolean isDocumentStyle() {
        final WSDLAnalyzer wsdlAnalyzer = new WSDLAnalyzer(((Description) getModelRoot().getDescription()).getComponent());
        return wsdlAnalyzer.isDocStyleCompliant((Operation) component);
    }

    public List<IFault> getFault(final String name) {
        return CollectionTypeUtils.find(getAllFaults(), new Condition<IFault>() {

            public boolean isSatisfied(IFault in) {
                return name == null ? null == in.getName() : name.equals(in.getName());
            }

        });
    }

    public List<IParameter> getInputParameter(final String name) {
        return CollectionTypeUtils.find(getAllInputParameters(), new Condition<IParameter>() {

            public boolean isSatisfied(IParameter in) {
                return null == name ? null == in.getName() : name.equals(in.getName());
            }

        });
    }

    public List<IParameter> getOutputParameter(final String name) {
        return CollectionTypeUtils.find(getAllOutputParameters(), new Condition<IParameter>() {

            public boolean isSatisfied(IParameter out) {
                return null == name ? null == out.getName() : name.equals(out.getName());
            }

        });
    }

    public IFault addFault(final String name) throws IllegalInputException, DuplicateException, ExecutionException {
        Nil.checkNil(name, "name"); //$NON-NLS-1$

        final AddFaultCommand command = new AddFaultCommand(getModelRoot(), this, name);
        getModelRoot().getEnv().execute(command);
        return command.getFault();
    }

    public IParameter addInputParameter(final String name) throws IllegalInputException, DuplicateException, ExecutionException {
        Nil.checkNil(name, "name"); //$NON-NLS-1$

        final AddInParameterCommand command = new AddInParameterCommand(getModelRoot(), this, name);
        getModelRoot().getEnv().execute(command);
        return command.getParameter();
    }

    public IParameter addOutputParameter(final String name) throws IllegalInputException, DuplicateException, ExecutionException {
        Nil.checkNil(name, "name"); //$NON-NLS-1$

        if (getOperationStyle() == OperationType.REQUEST_RESPONSE) {
            final AddOutParameterCommand command = new AddOutParameterCommand(getModelRoot(), this, name);
            getModelRoot().getEnv().execute(command);
            return command.getParameter();
        }
        return null;
    }

    /*
     * public void removeFault(final String name) throws ExecutionException {
     * Nil.checkNil(name, "name"); //$NON-NLS-1$
     * getModelRoot().getEnv().execute(new DeleteFaultCommand(getModelRoot(),
     * this, name)); }
     */

    public void removeFault(final IFault fault) throws ExecutionException {
        Nil.checkNil(fault, "fault"); //$NON-NLS-1$
        getModelRoot().getEnv().execute(new DeleteFaultCommand(getModelRoot(), this, fault));
    }

    /*
     * public void removeInputParameter(final String name) throws
     * ExecutionException { Nil.checkNil(name, "name"); //$NON-NLS-1$ final
     * DeleteInParameterCommand command = new
     * DeleteInParameterCommand(getModelRoot(), this, name);
     * getModelRoot().getEnv().execute(command); }
     */

    public void removeInputParameter(final IParameter parameter) throws ExecutionException {
        Nil.checkNil(parameter, "parameter"); //$NON-NLS-1$
        final DeleteInParameterCommand command = new DeleteInParameterCommand(getModelRoot(), this, parameter);
        getModelRoot().getEnv().execute(command);
    }

    /*
     * public void removeOutputParameter(final String name) throws
     * ExecutionException { Nil.checkNil(name, "name"); //$NON-NLS-1$ final
     * DeleteOutParameterCommand command = new
     * DeleteOutParameterCommand(getModelRoot(), this, name);
     * getModelRoot().getEnv().execute(command); }
     */

    public void removeOutputParameter(final IParameter parameter) throws ExecutionException {
        Nil.checkNil(parameter, "parameter"); //$NON-NLS-1$
        final DeleteOutParameterCommand command = new DeleteOutParameterCommand(getModelRoot(), this, parameter);
        getModelRoot().getEnv().execute(command);
    }

    public void setName(final String name) throws IllegalInputException, DuplicateException {
        Nil.checkNil(name, "name"); //$NON-NLS-1$
        if (!EmfXsdUtils.isValidNCName(name))
            throw new IllegalInputException("Entered Operation name is not valid"); //$NON-NLS-1$

        try {
            getModelRoot().getEnv().execute(new RenameOperationCommand(getModelRoot(), this, name));
        } catch (ExecutionException e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.ERROR, "Can not rename service operation from " + getName() + //$NON-NLS-1$
                    " to " + name, e); //$NON-NLS-1$
        }
    }

    public OperationType getOperationStyle() {
        final Operation operation = (Operation) component;

        final javax.wsdl.OperationType style = operation.getStyle();
        if (style == javax.wsdl.OperationType.REQUEST_RESPONSE || operation.getEOutput() != null)
            return OperationType.REQUEST_RESPONSE;
        else if (style == javax.wsdl.OperationType.ONE_WAY)
            return OperationType.ASYNCHRONOUS;
        else if (style == null && operation.getEInput() == null && operation.getEOutput() == null)
            return OperationType.REQUEST_RESPONSE;
        else {
            throw new RuntimeException("Only Request-Response and Asynchronous Operations are supported"); //$NON-NLS-1$
        }
    }

    public void setOperationType(OperationType operationType) throws ExecutionException {
        Nil.checkNil(operationType, "operationType"); //$NON-NLS-1$
        if (getOperationStyle() == operationType)
            return;
        ChangeOperationTypeCommand command = new ChangeOperationTypeCommand(getModelRoot(), this, operationType, false);
        getModelRoot().getEnv().execute(command);
    }

    @Override
    public Operation getComponent() {
        return (Operation) super.getComponent();
    }
}
