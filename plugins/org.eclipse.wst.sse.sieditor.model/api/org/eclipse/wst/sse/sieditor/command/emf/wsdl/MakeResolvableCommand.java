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
package org.eclipse.wst.sse.sieditor.command.emf.wsdl;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;

import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.CopyTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;

public class MakeResolvableCommand extends AbstractWSDLNotificationOperation {

    private final IType type;
    private final IDescription description;
    private IType resolvedType;

    public MakeResolvableCommand(IWsdlModelRoot modelRoot, IDescription description, IType type) {
        super(modelRoot, type, Messages.MakeResolvableCommand_make_resolvable_command_label);
        this.description = description;
        this.type = type;
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        resolvedType = ((Description) description).resolveType(type.getComponent());
        final IModelObject parent = type.getParent();

        if ((null == resolvedType || resolvedType instanceof UnresolvedType) && parent instanceof Schema) {
            final ISchema[] schemas = description.getSchema(type.getNamespace());
            ISchema schema = null;
            if (null == schemas || 0 == schemas.length) {
                AddNewSchemaCommand cmd = new AddNewSchemaCommand(getModelRoot(), type.getNamespace());
                IStatus status = getModelRoot().getEnv().execute(cmd);
                if (!StatusUtils.canContinue(status)) {
                    return status;
                }

                schema = cmd.getNewSchema();
                ((Schema) schema).setResolver(((Description) description).getSchemaResolver());
            } else
                schema = (Schema) schemas[0];

            final XSDNamedComponent eType = type.getComponent();
            IType oldType = schema.getType(eType instanceof XSDElementDeclaration, type.getName());

            if (null != oldType) {
                resolvedType = oldType;
            } else {
                final CopyTypeCommand command = new CopyTypeCommand(getModelRoot(), description, eType, schema, type
                        .getName());
                IStatus status = getModelRoot().getEnv().execute(command);
                if (!StatusUtils.canContinue(status)) {
                    return status;
                }
                resolvedType = command.getCopiedType();
            }
        }
        return Status.OK_STATUS;
    }

    public IType getResolvedType() {
        return resolvedType;
    }

}
