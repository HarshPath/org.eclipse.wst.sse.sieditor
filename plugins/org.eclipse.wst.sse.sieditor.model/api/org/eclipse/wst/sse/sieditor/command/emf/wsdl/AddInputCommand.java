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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.Input;
import org.eclipse.wst.wsdl.Operation;

import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;

/**
 * 
 * 
 * 
 */
public class AddInputCommand extends AbstractWSDLNotificationOperation {
    private final Operation _operation;
    private Input _input;
    private final String _name;
    private final org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType _type;

    public AddInputCommand(IWsdlModelRoot root, Operation operation, final String name,
            org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType type) {
        super(root, root.getDescription(), Messages.AddInputCommand_add_input_command_label);
        this._operation = operation;
        this._name = name;
        this._type = type;
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        if (null == _input) {
            _input = getWSDLFactory().createInput();
            _input.setEnclosingDefinition(((Description) getModelRoot().getDescription()).getComponent());
            if (null != _name)
                _input.setName(_name);
        }
        _operation.setEInput(_input);
        
        return Status.OK_STATUS;
    }

    public void undo() {

    }
}