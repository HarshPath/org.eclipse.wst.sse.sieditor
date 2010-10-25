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

import static org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils.getWSDLFactory;
import static org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils.makeMessageName;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.MessageReference;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Part;

import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils;
import org.eclipse.wst.sse.sieditor.model.utils.NameGenerator;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

public abstract class AbstractAddParameterCommand extends AbstractWSDLNotificationOperation {

    private final String _name;
    protected Part _part;
    private OperationParameter _parameter;
    private SetParameterTypeCommand setParameterTypeCommand = null;

    public AbstractAddParameterCommand(IWsdlModelRoot root, final IOperation component, final String name) {
        super(root, component, Messages.AbstractAddParameterCommand_abstract_add_parameter_command_label);
        this._name = name;
    }

    @Override
    public boolean canExecute() {
        return !(null == _name || null == modelObject || null == getModelRoot());
    }

    protected abstract byte getParameterType();

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        final ServiceOperation component = (ServiceOperation) modelObject;
        final Operation operation = component.getComponent();
        final Description description = (Description) getModelRoot().getDescription();
        final Definition definition = description.getComponent();

        MessageReference messageReference[] = new MessageReference[1];
        IStatus status = createMessageReference(operation, messageReference);
        if (!StatusUtils.canContinue(status)) {
            return status;
        }

        Message msg = messageReference[0].getEMessage();
        // definition.equals(msg.getEnclosingDefinition()) -> message can be referenced from imported WSDL
        if (null == msg || !definition.equals(msg.getEnclosingDefinition())) {
            if (Logger.isDebugEnabled()) {
                Logger.getDebugTrace().trace("", "Message Not Present for output in operation " + operation.getName()); //$NON-NLS-1$ //$NON-NLS-2$
            }
            // Create anew message
            final String msgName = makeMessageName(operation.getName(), getNewMessageSuffix(), definition);
            final AddMessageCommand command = new AddMessageCommand(description, messageReference[0], msgName, _name);
            status = getModelRoot().getEnv().execute(command);
            if (!StatusUtils.canContinue(status)) {
                return status;
            }

            _part = command.getPart();
            msg = messageReference[0].getEMessage();
        }

        // Create a new Part
        if (null == _part) {
            _part = getWSDLFactory().createPart();
            _part.setName(_name);
            _part.setEnclosingDefinition(definition);
        }
        msg.addPart(_part);
        if (null == _parameter)
            _parameter = new OperationParameter(getModelRoot(), _part, component, msg, getParameterType());

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

        // Set Parameter Type
        setParameterTypeCommand = new SetParameterTypeCommand(_parameter, addGlobalElementCommand
                .getStructureType());
        status = getModelRoot().getEnv().execute(setParameterTypeCommand);
        if (!StatusUtils.canContinue(status)) {
            return status;
        }

        return Status.OK_STATUS;
    }

    protected abstract IStatus createMessageReference(Operation operation, MessageReference[] createdMessageReference)
            throws ExecutionException;
    
    protected abstract String getNewMessageSuffix();

    public OperationParameter getParameter() {
        return _parameter;
    }

}
