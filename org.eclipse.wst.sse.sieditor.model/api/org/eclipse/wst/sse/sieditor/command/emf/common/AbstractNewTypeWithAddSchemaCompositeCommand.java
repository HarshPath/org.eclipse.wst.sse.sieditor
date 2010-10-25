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
package org.eclipse.wst.sse.sieditor.command.emf.common;

import java.util.List;

import org.eclipse.wst.wsdl.Definition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.common.setcommandbuilder.ISetTypeCommandBuilder;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddNewSchemaCommand;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public abstract class AbstractNewTypeWithAddSchemaCompositeCommand extends BaseNewTypeCompositeCommand {

    protected AbstractNotificationOperation addNewSchemaCommand;
    protected AbstractNewTypeCompositeCommand addNewTypeCommand;

    public AbstractNewTypeWithAddSchemaCompositeCommand(IModelRoot root, IModelObject modelObjToRefresh, String operationLabel,
            final ISetTypeCommandBuilder setTypeCommandBuilder, String typeName) {
        super(root, modelObjToRefresh, operationLabel, null, setTypeCommandBuilder, typeName);
    }

    protected abstract AbstractNewTypeCompositeCommand createAddNewTypeCommand(ISchema schema);

    @Override
    public AbstractNotificationOperation getNextOperation(List<AbstractNotificationOperation> subOperations) {
        if (addNewSchemaCommand == null && root instanceof IWsdlModelRoot) {
            IWsdlModelRoot wsdlModelRoot = (IWsdlModelRoot) modelObject.getModelRoot();
            IDescription description = wsdlModelRoot.getDescription();
            Definition definition = description.getComponent();
            addNewSchemaCommand = new AddNewSchemaCommand(wsdlModelRoot, definition.getTargetNamespace());
            return addNewSchemaCommand;
        }
        if (schema == null && addNewSchemaCommand != null) {
            schema = ((AddNewSchemaCommand) addNewSchemaCommand).getNewSchema();
        }

        if (addNewTypeCommand == null) {
            addNewTypeCommand = createAddNewTypeCommand(schema);
            return addNewTypeCommand;
        }

        type = addNewTypeCommand.getType();

        return null;
    }
}
