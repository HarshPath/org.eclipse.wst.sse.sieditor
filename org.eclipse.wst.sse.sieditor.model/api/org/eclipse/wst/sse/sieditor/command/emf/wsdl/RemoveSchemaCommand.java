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
package org.eclipse.wst.sse.sieditor.command.emf.wsdl;

import java.util.List;

import javax.wsdl.Types;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.XSDSchemaExtensibilityElement;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

/**
 * Command for removing a service interface object
 * 
 * 
 * 
 */
public class RemoveSchemaCommand extends AbstractWSDLNotificationOperation {
    private final ISchema _schema;

    public RemoveSchemaCommand(final IWsdlModelRoot root, final ISchema schema) {
        super(root, root.getDescription(), Messages.RemoveSchemaCommand_remve_schema_command_label);
        this._schema = schema;
    }

    @SuppressWarnings("unchecked")
	@Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        final Description description = (Description) modelObject;
        final Definition definition = description.getComponent();
        final Types types = definition.getETypes();

        final List extensibilityElements = types.getExtensibilityElements();
        final XSDSchema xsdSchema = _schema.getComponent();
        for (Object extensibilityElement : extensibilityElements) {
            if (extensibilityElement instanceof XSDSchemaExtensibilityElement) {
                final XSDSchema schema = ((XSDSchemaExtensibilityElement) extensibilityElement).getSchema();
                if (schema.equals(xsdSchema)) {
                    extensibilityElements.remove(extensibilityElement);
                    break;
                }
            }
        }

        return Status.OK_STATUS;
    }
}