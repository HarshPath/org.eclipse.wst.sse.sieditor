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
package org.eclipse.wst.sse.sieditor.ui.v2.dt.quickFix;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IMarkerResolution;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddSchemaForSchemaCommand;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

public class MissingSchemaForSchemaQuickFix implements IMarkerResolution {

    private String label;
    private ISchema schema;

    public MissingSchemaForSchemaQuickFix(String label, ISchema schema) {
        this.label = label;
        this.schema = schema;
    }

    public String getLabel() {

        return label;
    }

    public void run(IMarker marker) {

        AddSchemaForSchemaCommand command = new AddSchemaForSchemaCommand(schema);
        try {
            schema.getModelRoot().getEnv().execute(command);
            schema.getComponent().update();

        } catch (ExecutionException e) {
            final IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.MissingSchemaForSchemaQuickFix_0, e);
            Logger.log(status);
            StatusUtils.showStatusDialog(Messages.MissingSchemaForSchemaQuickFix_0, status);
        }

    }

}
