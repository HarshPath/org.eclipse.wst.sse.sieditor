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
package org.eclipse.wst.sse.sieditor.command.emf.wsdl;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.WSDLFactory;
import org.eclipse.wst.wsdl.XSDSchemaExtensibilityElement;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

/**
 * This command clones the given schema and adds it to the given definition.
 * 
 */
public class CloneNamespaceCommand extends AbstractNotificationOperation {

    private ISchema targetSchema;

    public CloneNamespaceCommand(final IWsdlModelRoot root, final ISchema sourceSchema) {
        super(root, sourceSchema, Messages.CloneNamespaceCommand_clone_namespace_command_label);
    }

    @Override
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        final IWsdlModelRoot wsdlModelRoot = (IWsdlModelRoot) root;
        final ISchema sourceSchema = (ISchema) modelObject;

        final XSDSchema newXsdSchema = (XSDSchema) sourceSchema.getComponent().cloneConcreteComponent(false, false);

        for (final XSDConcreteComponent component : sourceSchema.getComponent().getContents()) {
            if (component instanceof XSDImport) {
                final XSDImport newXsdImport = XSDFactory.eINSTANCE.createXSDImport();
                newXsdImport.setNamespace(((XSDImport) component).getNamespace());
                newXsdImport.setSchemaLocation(((XSDImport) component).getSchemaLocation());
                newXsdSchema.getContents().add(newXsdImport);

            } else if (component instanceof XSDInclude) {
                final XSDInclude newXsdInclude = XSDFactory.eINSTANCE.createXSDInclude();
                newXsdInclude.setSchemaLocation(((XSDInclude) component).getSchemaLocation());
                newXsdSchema.getContents().add(newXsdInclude);

            } else if (component instanceof XSDNamedComponent) {
                EmfXsdUtils.cloneWithAnnotation((XSDNamedComponent) component, newXsdSchema);
            }
        }

        final XSDSchemaExtensibilityElement extensibilityElement = WSDLFactory.eINSTANCE.createXSDSchemaExtensibilityElement();
        extensibilityElement.setEnclosingDefinition(wsdlModelRoot.getDescription().getComponent());

        extensibilityElement.setSchema(newXsdSchema);

        wsdlModelRoot.getDescription().getComponent().getETypes().addExtensibilityElement(extensibilityElement);

        targetSchema = new Schema(newXsdSchema, wsdlModelRoot.getDescription(), null);
        return Status.OK_STATUS;
    }

    public ISchema getTargetSchema() {
        return targetSchema;
    }

}
