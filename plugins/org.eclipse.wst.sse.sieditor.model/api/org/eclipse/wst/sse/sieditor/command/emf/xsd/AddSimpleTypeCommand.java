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
package org.eclipse.wst.sse.sieditor.command.emf.xsd;

import static org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils.getXSDFactory;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractXSDNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.SimpleType;

/**
 * Command for adding a new SimpleType Definition
 * 
 * 
 */
public class AddSimpleTypeCommand extends AbstractXSDNotificationOperation {

    private final String _name;
    private SimpleType _simpleType;

    public AddSimpleTypeCommand(final IXSDModelRoot root, final ISchema schema, final String name) {
        super(root, schema, Messages.AddSimpleTypeCommand_add_simple_type_command_label);
        this._name = name;
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        final Schema schema = (Schema) modelObject;
        final XSDSimpleTypeDefinition eSimpleType = getXSDFactory().createXSDSimpleTypeDefinition();
        eSimpleType.setName(_name);
        final XSDSchema emfSchema = schema.getComponent();
        eSimpleType.setTargetNamespace(emfSchema.getTargetNamespace());
        final IType defaultBaseType = Schema.getDefaultSimpleType();
        eSimpleType.setBaseTypeDefinition((XSDSimpleTypeDefinition) defaultBaseType.getComponent());
        _simpleType = new SimpleType(getModelRoot(), schema, eSimpleType);
        emfSchema.getContents().add(eSimpleType);

        return Status.OK_STATUS;
    }

    public SimpleType getSimpleType() {
        return _simpleType;
    }
}