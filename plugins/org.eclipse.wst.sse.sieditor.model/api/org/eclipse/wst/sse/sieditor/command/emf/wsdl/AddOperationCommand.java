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

import static org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils.getWSDLFactory;
import static org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils.makeMessageName;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Input;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Output;
import org.eclipse.wst.wsdl.PortType;

import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils;
import org.eclipse.wst.sse.sieditor.model.utils.NameGenerator;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

/**
 * Command for adding a new operaiton
 * 
 * 
 */
public class AddOperationCommand extends AbstractWSDLNotificationOperation {
    private String _name;
    private ServiceOperation _modelOperation;
    private OperationType _operationType;
    private AddMessageCommand addInputMessageCommand;
    private SetParameterTypeCommand setInParameterTypeCommand;
    private SetParameterTypeCommand setOutParameterTypeCommand;

    public AddOperationCommand(IWsdlModelRoot root, IServiceInterface object, String name, OperationType operationType) {
        super(root, object, Messages.AddOperationCommand_add_new_operation_command_label);
        this._name = name;
        this._operationType = operationType;
    }

    @Override
    public boolean canExecute() {
        return !(getModelRoot() == null || modelObject == null || _name == null)
                && ((ServiceInterface) modelObject).getOperation(_name).isEmpty();
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        final ServiceInterface intf = (ServiceInterface) modelObject;
        final Operation newOperation = getWSDLFactory().createOperation();
        newOperation.setName(_name);
        newOperation
                .setStyle(_operationType == null || _operationType == OperationType.REQUEST_RESPONSE ? javax.wsdl.OperationType.REQUEST_RESPONSE
                        : javax.wsdl.OperationType.ONE_WAY);

        final Description description = (Description) getModelRoot().getDescription();
        final Definition definition = description.getComponent();

        final PortType portType = intf.getComponent();
        portType.addOperation(newOperation);
        newOperation.setEnclosingDefinition(definition);

        // Add Input
        AddInputCommand addInputCommand = new AddInputCommand(getModelRoot(), newOperation, null, _operationType);
        IStatus status = getModelRoot().getEnv().execute(addInputCommand);
        if (!StatusUtils.canContinue(status)) {
            return status;
        }

        final Input input = newOperation.getEInput();

        // Create a new message
        String msgName = makeMessageName(newOperation.getName(), ServiceOperation.OPERATION_INMSG_SUFFIX, definition);
        addInputMessageCommand = new AddMessageCommand(description, input, msgName, null);
        status = getModelRoot().getEnv().execute(addInputMessageCommand);
        if (!StatusUtils.canContinue(status)) {
            return status;
        }

        input.getEMessage();

        if (null == _operationType || _operationType == OperationType.REQUEST_RESPONSE) {
            // Create Output
            final AddOutputCommand addOutputCommand = new AddOutputCommand(getModelRoot(), newOperation, null, _operationType);
            status = getModelRoot().getEnv().execute(addOutputCommand);
            if (!StatusUtils.canContinue(status)) {
                return status;
            }

            final Output output = newOperation.getEOutput();

            // Create a new message
            msgName = makeMessageName(newOperation.getName(), ServiceOperation.OPERATION_OUTMSG_SUFFIX, definition);
            AddMessageCommand addOutputMessageCommand = new AddMessageCommand(description, output, msgName, null);
            status = getModelRoot().getEnv().execute(addOutputMessageCommand);
            if (!StatusUtils.canContinue(status)) {
                return status;
            }

            output.getEMessage();
        }
        _modelOperation = new ServiceOperation(getModelRoot(), newOperation, intf);

        ISchema schema = EmfWsdlUtils.getDefaultSchema(description);
        if (schema == null) { // Create new schema with target namespace
            AddNewSchemaCommand addNewSchemaCommand = new AddNewSchemaCommand(getModelRoot(), definition.getTargetNamespace());
            status = getModelRoot().getEnv().execute(addNewSchemaCommand);
            if (!StatusUtils.canContinue(status)) {
                return status;
            }
            schema = addNewSchemaCommand.getNewSchema();
        }

        AddStructureTypeCommand addGlobalElementCommand = new AddStructureTypeCommand((IXSDModelRoot) schema.getModelRoot(),
                schema, NameGenerator.getNewElementDefaultName(schema), true, (AbstractType) Schema.getDefaultSimpleType());
        status = getModelRoot().getEnv().execute(addGlobalElementCommand);
        if (!StatusUtils.canContinue(status)) {
            return status;
        }

        OperationParameter parameter = (OperationParameter) _modelOperation.getAllInputParameters().iterator().next();
        setInParameterTypeCommand = new SetParameterTypeCommand(parameter, addGlobalElementCommand
                .getStructureType());
        getModelRoot().getEnv().execute(setInParameterTypeCommand);
        if (!StatusUtils.canContinue(status)) {
            return status;
        }

        if (_operationType == OperationType.REQUEST_RESPONSE) {
            addGlobalElementCommand = new AddStructureTypeCommand((IXSDModelRoot) schema.getModelRoot(), schema, NameGenerator
                    .getNewElementDefaultName(schema), true, (AbstractType) Schema.getDefaultSimpleType());
            status = getModelRoot().getEnv().execute(addGlobalElementCommand);
            if (!StatusUtils.canContinue(status)) {
                return status;
            }

            parameter = (OperationParameter) _modelOperation.getAllOutputParameters().iterator().next();
            setOutParameterTypeCommand = new SetParameterTypeCommand(parameter, addGlobalElementCommand
                    .getStructureType());
            getModelRoot().getEnv().execute(setOutParameterTypeCommand);
            if (!StatusUtils.canContinue(status)) {
                return status;
            }
        }

        return Status.OK_STATUS;
    }

    public ServiceOperation getOperation() {
        return _modelOperation;
    }

}