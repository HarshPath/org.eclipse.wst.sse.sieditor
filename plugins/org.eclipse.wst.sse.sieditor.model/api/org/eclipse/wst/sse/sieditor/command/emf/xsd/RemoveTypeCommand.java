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

import java.text.MessageFormat;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.xsd.XSDNamedComponent;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

/**
 * Command for Removing a type {@link IStructureType} or {@link ISimpleType}
 * 
 * 
 * 
 */
public class RemoveTypeCommand extends AbstractNotificationOperation {
    private final IType _type;

    public RemoveTypeCommand(final IModelRoot root, final ISchema schema, final IType type) {
        super(root, schema, Messages.RemoveTypeCommand_remove_type_command_label);
        this._type = type;
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        final Schema schema = (Schema) modelObject;
        boolean removed = false;

        XSDNamedComponent component = _type.getComponent();
        if (null != component) {
            schema.getComponent().getContents().remove(component);
            removed = true;
        }
        return removed ? Status.OK_STATUS : new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(
                Messages.RemoveTypeCommand_msg_can_not_remove_type_X, _type.getName()));
    }
}
