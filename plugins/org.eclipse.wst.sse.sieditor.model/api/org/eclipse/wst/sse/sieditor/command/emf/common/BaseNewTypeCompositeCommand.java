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
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.command.emf.common;

import org.eclipse.wst.sse.sieditor.command.common.AbstractCompositeNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.common.setcommandbuilder.ISetTypeCommandBuilder;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public abstract class BaseNewTypeCompositeCommand extends AbstractCompositeNotificationOperation {

    protected IType type;
    protected ISchema schema;
    protected final String typeName;
    protected final ISetTypeCommandBuilder setTypeCommandBuilder;

    public BaseNewTypeCompositeCommand(final IModelRoot root, final IModelObject modelObjToRefresh, final String operationLabel, final ISchema schema,
            final ISetTypeCommandBuilder setTypeCommandBuilder, final String typeName) {
        super(root, modelObjToRefresh, operationLabel);
        this.schema = schema;
        this.setTypeCommandBuilder = setTypeCommandBuilder;
        this.typeName = typeName;
    }

    public IType getType() {
        return type;
    }
    
    protected ISetTypeCommandBuilder getTypeCommandBuilder() {
        return setTypeCommandBuilder;
    }

}
