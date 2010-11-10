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
package org.eclipse.wst.sse.sieditor.command.emf.wsdl;

import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class AddSchemaForSchemaCommand extends AbstractNotificationOperation {

    private final ISchema schema;
    private final String prefix;

    public AddSchemaForSchemaCommand(final ISchema schema) {
        super(schema.getModelRoot(), schema, Messages.AddSchemaForSchemaCommand_0);
        this.schema = schema;
        final Element element = schema.getComponent().getElement();
        if (element != null) {
            prefix = element.getPrefix();
        } else {
            prefix = EmfXsdUtils.XSD_PREFIX;
        }
    }

    @Override
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        final XSDSchema xsdSchema = schema.getComponent();
        xsdSchema.setSchemaForSchemaQNamePrefix(prefix);
        final Map<String, String> qNamePrefixToNamespaceMap = xsdSchema.getQNamePrefixToNamespaceMap();

        qNamePrefixToNamespaceMap.put(prefix, EmfXsdUtils.getSchemaForSchemaNS());
        return Status.OK_STATUS;
    }
}
