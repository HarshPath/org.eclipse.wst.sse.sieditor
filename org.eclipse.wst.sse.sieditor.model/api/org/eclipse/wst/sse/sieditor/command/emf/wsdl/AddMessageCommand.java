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

import javax.xml.namespace.QName;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.MessageReference;
import org.eclipse.wst.wsdl.Part;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;

/**
 * 
 * 
 * 
 */
public class AddMessageCommand extends AbstractNotificationOperation {
    public static final String DEFAULT_PART_NAME = "Parameter"; //$NON-NLS-1$

    private MessageReference _ref;
    private Message _message;
    private final String _name;
    private final String _partName;

    private Part _part;


    public AddMessageCommand(final IDescription description, final MessageReference ref, final String name, String partName) {
        super(description.getModelRoot(), description, Messages.AddMessageCommand_add_message_command_label);
        this._ref = ref;
        this._name = name;
        this._partName = partName;
    }

    @Override
    public boolean canExecute() {
        return !(null == modelObject || null == _name);
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        final Definition definition = getDefinition();
        _message = getWSDLFactory().createMessage();
        _message.setQName(new QName(definition.getTargetNamespace(), _name));
        _message.setEnclosingDefinition(definition);

        EmfWsdlUtils.ensurePrefix(definition, definition.getTargetNamespace());

        definition.addMessage(_message);
        if (_ref != null) {
            _ref.setEMessage(_message);
        }

        _part = getWSDLFactory().createPart();
        _message.addPart(_part);
        _part.setName(null == _partName ? DEFAULT_PART_NAME : _partName);
        _part.setEnclosingDefinition(definition);

        return Status.OK_STATUS;
    }

    private Definition getDefinition() {
        return ((Description) modelObject).getComponent();
    }

    public Message getMessage() {
        return _message;
    }

    public Part getPart() {
        return _part;
    }
}