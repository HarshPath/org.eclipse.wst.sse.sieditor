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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.transaction.internal.AllowChangePropagationBlockingOption;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Input;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Output;
import org.eclipse.wst.wsdl.WSDLFactory;

import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.NameGenerator;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.utils.WSDLAnalyzer;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;

/**
 * 
 * 
 */
public class ChangeOperationTypeCommand extends AbstractWSDLNotificationOperation {
    private final OperationType _operationType;

    private Output _output;

    private final boolean allowAsyncOperationFaults;

    /**
     * Command changing the operation type of the given operation. If the new
     * style is {@link OperationType#ASYNCHRONOUS} and the
     * {@link AllowChangePropagationBlockingOption} parameter is true only the
     * outputs will be removed.
     * 
     * @param root
     *            the model root
     * @param component
     *            the operation which the command modifies
     * @param operationType
     *            the new type to be set
     * @param allowAsyncOperationFaults
     *            flag determining if the {@link OperationType#ASYNCHRONOUS}
     *            operation is allowed to have faults
     */

    public ChangeOperationTypeCommand(IWsdlModelRoot root, final IOperation component, final OperationType operationType,
            boolean allowAsyncOperationFaults) {
        super(root, component, Messages.ChangeOperationTypeCommand_change_operation_type_label);
        this._operationType = operationType;
        this.allowAsyncOperationFaults = allowAsyncOperationFaults;
    }

    public boolean canExecute() {
        return !(null == modelObject || null == getModelRoot() || null == _operationType);
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        final ServiceOperation component = (ServiceOperation) modelObject;
        final Operation operation = component.getComponent();
        final Description description = (Description) getModelRoot().getDescription();
        final Definition definition = description.getComponent();
        if (_operationType == OperationType.ASYNCHRONOUS) {
            _output = operation.getEOutput();
            if (null != _output || operation.getEFaults() != null) {
                // update the binding
                WSDLAnalyzer wsdlAnalyzer = new WSDLAnalyzer(definition);
                ServiceInterface serviceInterface = (ServiceInterface) component.getParent();
                if (allowAsyncOperationFaults) {
                    wsdlAnalyzer.removeAllOutputBindings(serviceInterface.getComponent(), operation);
                } else {
                    wsdlAnalyzer.removeOutputAndFaultBindings(serviceInterface.getComponent(), operation);
                    operation.eUnset(WSDLFactory.eINSTANCE.getWSDLPackage().getOperation_EFaults());
                }
                operation.eUnset(WSDLFactory.eINSTANCE.getWSDLPackage().getOperation_EOutput());
            }
            //asynchronous operations MUST have an input.
            ensureAsyncOperationHasInput(operation);
        } else {
            AddOutParameterCommand addOutParameterCommand = new AddOutParameterCommand((IWsdlModelRoot) root, component,
                    AddMessageCommand.DEFAULT_PART_NAME);

            IStatus status = getModelRoot().getEnv().execute(addOutParameterCommand);
            if (!StatusUtils.canContinue(status)) {
                return status;
            }
        }
        operation.setStyle(_operationType == OperationType.REQUEST_RESPONSE ? javax.wsdl.OperationType.REQUEST_RESPONSE
                : javax.wsdl.OperationType.ONE_WAY);

        return Status.OK_STATUS;
    }
    
    private void ensureAsyncOperationHasInput(Operation operation) throws ExecutionException {
        
        Input input = operation.getEInput();
        
        if (input == null || input.getEMessage() == null) {
        	operation.eUnset(WSDLFactory.eINSTANCE.getWSDLPackage().getOperation_EInput());
        	IOperation iOperation = (IOperation) modelObject;
        	String newInputParamName = NameGenerator.getInputParameterName(iOperation);
        	AddInParameterCommand cmd = new AddInParameterCommand((IWsdlModelRoot) root, iOperation, newInputParamName);
        	getModelRoot().getEnv().execute(cmd);
        }
        
    }
}