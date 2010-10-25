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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.impl.XSDImportImpl;

import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.search.DocumentType;
import org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils;
import org.eclipse.wst.sse.sieditor.model.utils.ResourceUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

/**
 * Command for adding a new service interface object
 * 
 * 
 * 
 */

public class ImportSchemaCommand extends AbstractWSDLNotificationOperation {

    private final AbstractType typeHandle;
    private final URI schemaUri;
    private final IDescription description;
    private Schema schema;
    private final DocumentType docType;
    private final String targetNamespace;

    public ImportSchemaCommand(final IWsdlModelRoot root, final IDescription description, final URI wsdlUri, final URI schemaUri,
            final AbstractType typeHandle, final DocumentType docType) {
        this(root, description, schemaUri, typeHandle.getComponent().getTargetNamespace(), typeHandle, docType);
    }

    public ImportSchemaCommand(final IWsdlModelRoot root, final IDescription description, final URI schemaUri,
            final String targetNamespace, final AbstractType typeHandle, final DocumentType docType) {
        super(root, description, Messages.ImportSchemaCommand_import_schema_command_label);
        this.schemaUri = schemaUri;
        this.typeHandle = typeHandle;
        this.description = description;
        this.docType = docType;
        this.targetNamespace = targetNamespace;
    }

    @Override
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        if ("".equals(targetNamespace)) {//$NON-NLS-1$
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.ImportSchemaCommand_msg_namespace_is_empty);
        }

        if (DocumentType.XSD_SHEMA.equals(docType)) {
            // try to find the schema in the already visible schemas

            if (targetNamespace != null && !targetNamespace.equals("")) {//$NON-NLS-1$ 
                final Definition definition = description.getComponent();
                return importXsdSchema(definition, targetNamespace, schemaUri);
            }

            schema = findSchemaInVisibleSchemas(targetNamespace);

            if (schema != null) {
                return Status.OK_STATUS;
            }

            return new Status(Status.ERROR, Activator.PLUGIN_ID, MessageFormat.format(Messages.ImportSchemaCommand_0, schemaUri));
        }
        return Status.CANCEL_STATUS;
    }

    private IStatus importXsdSchema(final Definition definition, final String namespace, final URI schemaUri)
            throws ExecutionException {

        final Schema foundSchema = findSchemaInVisibleSchemas(namespace);
        if (foundSchema != null) {
            schema = foundSchema;
            return Status.OK_STATUS;
        }

        final XSDSchema xsdSchema = getContainedEmptyNamespaceSchema();

        // we need to add an import
        final XSDImport xsdImport = XSDFactory.eINSTANCE.createXSDImport();
        if (namespace != null) {
            xsdImport.setNamespace(namespace);
        }
        final String wsdlLocation = definition.getLocation();
        final String schemaRelativePath = ResourceUtils.makeRelativeLocation(wsdlLocation, schemaUri);
        try {
            xsdImport.setSchemaLocation(URLDecoder.decode(schemaRelativePath, "UTF-8")); //$NON-NLS-1$
        } catch (final UnsupportedEncodingException e) {
            throw new ExecutionException("Unsuported ecoding", e); //$NON-NLS-1$
        }

        xsdSchema.getContents().add(0, xsdImport);

        final XSDSchema resolvedSchema = getResolvedSchema(xsdImport);

        if (null == resolvedSchema) {
            return new Status(IStatus.WARNING, Activator.PLUGIN_ID, Messages.ImportSchemaCommand_msg_can_not_resolve_XSD_schema);
        }

        schema = new Schema(resolvedSchema, null, schemaUri);

        return Status.OK_STATUS;
    }

    private XSDSchema getResolvedSchema(final XSDImport xsdImport) {
        XSDSchema resolvedSchema = xsdImport.getResolvedSchema();
        if (null == resolvedSchema) {
            if (typeHandle == null && xsdImport instanceof XSDImportImpl) {
                resolvedSchema = ((XSDImportImpl) xsdImport).importSchema();
            } else if (typeHandle != null) {
                resolvedSchema = typeHandle.getParent().getComponent();
                xsdImport.setResolvedSchema(resolvedSchema);
            }
        }
        return resolvedSchema;
    }

    private Schema findSchemaInVisibleSchemas(final String namespace) {
        final List<ISchema> allVisibleSchemas = description.getAllVisibleSchemas();
        for (final ISchema current : allVisibleSchemas) {
            final String visibleNamespace = current.getNamespace();
            // if the Namespace of current schema matches the type's schema
            if (visibleNamespace != null ? visibleNamespace.equals(namespace) : namespace == null) {
                // check if they are one and the same, and if so return current.
                if (current.equals(typeHandle.getParent())) {
                    return (Schema) current;
                }
            }
        }
        return null;
    }

    private XSDSchema getContainedEmptyNamespaceSchema() {
        final ISchema[] schemas = ((Description) description).getSchema(null, false);

        return schemas.length > 0 ? schemas[0].getComponent() : EmfWsdlUtils.addXSDSchema(description.getComponent(), null);
    }

    public Schema getSchema() {
        return schema;
    }
}
