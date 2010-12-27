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

import org.eclipse.wst.sse.sieditor.command.emf.xsd.EnsureSchemaElementCommand;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

public class MissingSchemaElementQuickFix implements IMarkerResolution {

    private ISchema schema;

    public MissingSchemaElementQuickFix(ISchema modelRoot) {
        this.schema = modelRoot;
    }

    @Override
    public String getLabel() {
        return Messages.MissingSchemaElementQuickFix_0;
    }

    @Override
    public void run(IMarker marker) {
        IStatus status = null;
        try {
            status = schema.getModelRoot().getEnv().execute(
                    new EnsureSchemaElementCommand(schema, Messages.MissingSchemaElementQuickFix_0));
        } catch (ExecutionException e) {
            if (status == null) {
                status = new Status(IStatus.INFO, Activator.PLUGIN_ID,
                        Messages.MissingSchemaElementQuickFix_2, e);
            }
            StatusUtils.showStatusDialog(Messages.MissingSchemaElementQuickFix_1, status);
        }
    }
}
