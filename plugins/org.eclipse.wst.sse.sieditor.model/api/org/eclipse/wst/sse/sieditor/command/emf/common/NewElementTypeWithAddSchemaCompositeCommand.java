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

import org.eclipse.wst.sse.sieditor.command.emf.common.setcommandbuilder.ISetTypeCommandBuilder;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class NewElementTypeWithAddSchemaCompositeCommand extends AbstractNewTypeWithAddSchemaCompositeCommand {

    public NewElementTypeWithAddSchemaCompositeCommand(IModelRoot root, IModelObject modelObjToRefresh, String operationLabel,
            final ISetTypeCommandBuilder setTypeCommandBuilder, String typeName) {
        super(root, modelObjToRefresh, operationLabel, setTypeCommandBuilder, typeName);
    }

    @Override
    protected AbstractNewTypeCompositeCommand createAddNewTypeCommand(ISchema schema) {
        return new NewElementTypeCompositeCommand(schema.getModelRoot(), modelObject, operationLabel, schema, typeName,
                setTypeCommandBuilder);
    }

}
