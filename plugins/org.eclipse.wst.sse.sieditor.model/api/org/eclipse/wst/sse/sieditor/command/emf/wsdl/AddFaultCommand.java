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
import org.eclipse.wst.wsdl.Fault;
import org.eclipse.wst.wsdl.Operation;

import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils;
import org.eclipse.wst.sse.sieditor.model.utils.NameGenerator;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

/**
 * Command for adding fault elements for operations
 * 
 */
public class AddFaultCommand extends AbstractWSDLNotificationOperation {
    private Fault _fault;
    private final String _name;
    private OperationFault _modelObject;
    private SetParameterTypeCommand setParameterTypeCommand = null;
    private AddStructureTypeCommand addGlobalElementCommand = null;
    private AddMessageCommand addMessageCommand = null;

    public AddFaultCommand(IWsdlModelRoot root, final IOperation component, final String name) {
        super(root, component, Messages.AddFaultCommand_add_fault_command_label);
        this._name = name;
    }

    @Override
    public boolean canExecute() {
        return !(null == _name || null == modelObject || null == getModelRoot());
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        final ServiceOperation component = (ServiceOperation) modelObject;
        final Operation operation = component.getComponent();

        final Description description = (Description) getModelRoot().getDescription();
        final Definition definition = description.getComponent();

        _fault = getWSDLFactory().createFault();
        _fault.setEnclosingDefinition(definition);
        _fault.setName(_name);

        final String msgName = makeMessageName(operation.getName(), ServiceOperation.OPERATION_FAULT_SUFFIX, definition);
        addMessageCommand = new AddMessageCommand(description, _fault, msgName, null);
        IStatus status = getModelRoot().getEnv().execute(addMessageCommand);
        if (!StatusUtils.canContinue(status)) {
            return status;
        }

        _fault.getEMessage();

        operation.addFault(_fault);
        _modelObject = new OperationFault(getModelRoot(), component, _fault);

        ISchema schema = EmfWsdlUtils.getDefaultSchema(description);
        if (schema == null) { // Create new schema with target namespace
            AddNewSchemaCommand addNewSchemaCommand = new AddNewSchemaCommand(getModelRoot(), definition.getTargetNamespace());
            status = getModelRoot().getEnv().execute(addNewSchemaCommand);
            if (!StatusUtils.canContinue(status)) {
                return status;
            }
            schema = addNewSchemaCommand.getNewSchema();
        }

        addGlobalElementCommand = new AddStructureTypeCommand((IXSDModelRoot) schema.getModelRoot(), schema, NameGenerator
                .getNewFaultElementDefaultName(schema), true, (AbstractType) Schema.getDefaultSimpleType());
        status = getModelRoot().getEnv().execute(addGlobalElementCommand);
        if (!StatusUtils.canContinue(status)) {
            return status;
        }

        final OperationParameter parameter = (OperationParameter) _modelObject.getParameters().iterator().next();
        setParameterTypeCommand = new SetParameterTypeCommand(parameter, addGlobalElementCommand
                .getStructureType());
        status = getModelRoot().getEnv().execute(setParameterTypeCommand);
        if (!StatusUtils.canContinue(status)) {
            return status;
        }

        return Status.OK_STATUS;
    }

    public OperationFault getFault() {
        return _modelObject;
    }
}
