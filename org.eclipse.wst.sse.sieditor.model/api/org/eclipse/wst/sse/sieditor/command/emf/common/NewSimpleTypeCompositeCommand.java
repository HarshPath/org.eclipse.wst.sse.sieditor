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
 *    Stanislav Nichev - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.command.emf.common;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.common.setcommandbuilder.ISetTypeCommandBuilder;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddSimpleTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

/**
 * subclass of the {@link AbstractNewTypeCompositeCommand}. This implementation
 * is responsible for the creation of new simple type
 * 
 * 
 * 
 */
public class NewSimpleTypeCompositeCommand extends AbstractNewTypeCompositeCommand {

    public NewSimpleTypeCompositeCommand(final IModelRoot modelRoot, final IModelObject modelObject, final String operationLabel,
            final ISchema schema, final String typeName, final ISetTypeCommandBuilder setTypeCommandBuilder) {
        super(modelRoot, modelObject, operationLabel, schema, typeName, setTypeCommandBuilder);
    }

    @Override
    protected AbstractNotificationOperation createCreateTypeCommand() {
        return new AddSimpleTypeCommand((IXSDModelRoot) schema.getModelRoot(), schema, typeName);
    }

    @Override
    protected IType extractCreatedType(final AbstractNotificationOperation createTypeCommand) {
        return ((AddSimpleTypeCommand) createTypeCommand).getSimpleType();
    }

    @Override
    protected AbstractNotificationOperation getNextIntermediateCommand() {
        return null;
    }

}
