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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.impl.XSDImportImpl;

import org.eclipse.wst.sse.sieditor.command.common.AbstractXSDNotificationOperation;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.utils.ResourceUtils;
import org.eclipse.wst.sse.sieditor.model.utils.URIHelper;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;

/**
 * Command for importing an external schema document Imports another schema
 * within a schema
 * 
 * 
 * 
 */
public class ImportSchemaCommand extends AbstractXSDNotificationOperation {
    private final AbstractType _type;
    private ISchema _importedSchema;

    public ImportSchemaCommand(final IXSDModelRoot root, final ISchema schema, final AbstractType type) {
        super(root, schema, Messages.ImportSchemaCommand_import_schema_command_label);
        this._type = type;
    }

    @Override
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        final ISchema typeSchema = _type.getParent();
        if (_type == UnresolvedType.instance() || this.modelObject.equals(typeSchema)
                || EmfXsdUtils.isSchemaForSchemaNS(typeSchema.getNamespace())) {
            _importedSchema = typeSchema;
            return Status.OK_STATUS;
        }

        final XSDSchema xsdSchema = (XSDSchema) modelObject.getComponent();
        final List<XSDSchemaContent> contents = xsdSchema.getContents();
        final String typeTargetNamespace = _type.getParent().getNamespace();
        if (typeTargetNamespace == null) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                    Messages.ImportSchemaCommand_cannot_refer_schema_without_namespace);
        }

        URI uri;
        try {
            uri = URIUtil.fromString(URLDecoder.decode(((Schema) _type.getParent()).getComponent().eResource().getURI()
                    .toString(), "UTF-8")); //$NON-NLS-1$
        } catch (final URISyntaxException e) {
            throw new ExecutionException(e.getLocalizedMessage());
        } catch (final UnsupportedEncodingException e) {
            throw new ExecutionException(e.getLocalizedMessage());
        }

        String schemaRelativePath;
        try {
            schemaRelativePath = ResourceUtils.makeRelativeLocation(URIHelper.createEncodedURI(xsdSchema.getSchemaLocation())
                    .toString(), uri);
        } catch (final UnsupportedEncodingException e) {
            throw new ExecutionException(e.getLocalizedMessage());
        } catch (final URISyntaxException e) {
            throw new ExecutionException(e.getLocalizedMessage());
        }

        try {
            schemaRelativePath = URLDecoder.decode(schemaRelativePath, "UTF-8"); //$NON-NLS-1$
        } catch (final UnsupportedEncodingException e) {
            throw new ExecutionException(e.getLocalizedMessage());
        }

        for (final XSDSchemaContent schemaContent : contents) {
            if (schemaContent instanceof XSDImport) {
                final XSDImport importDirective = (XSDImport) schemaContent;

                final String importedNamespace = importDirective.getNamespace();
                String importedLocation = importDirective.getSchemaLocation();
                if (importedLocation == null) {
                    importedLocation = ""; //$NON-NLS-1$
                }

                final boolean resourceAlreadyImported = typeTargetNamespace.equals(importedNamespace)
                        && schemaRelativePath.equals(importedLocation);

                if (resourceAlreadyImported) {
                    _importedSchema = _type.getParent();

                    Logger.log(new Status(IStatus.OK, Activator.PLUGIN_ID, String.format(
                            "Schema with targetNamespace [%s] already imported in schema [%s]. Import will be skipped", //$NON-NLS-1$
                            typeTargetNamespace, xsdSchema.getTargetNamespace())));

                    return Status.OK_STATUS;
                }
            }
        }

        // we need to add an import
        final XSDImport xsdImport = XSDFactory.eINSTANCE.createXSDImport();
        xsdImport.setNamespace(_type.getNamespace());
        if (!"".equals(schemaRelativePath)) { //$NON-NLS-1$
            xsdImport.setSchemaLocation(schemaRelativePath);
        }
        // the following line forces the xsd import to resolve the schema before
        // it is requested.
        // Added after the command implementation. Should'nt cause bugs

        XSDSchema resolvedSchema = ((XSDImportImpl) xsdImport).importSchema();
        xsdSchema.getContents().add(0, xsdImport);

        // if the resolved schema is not present - return the parent schema of
        // this type
        if (null == resolvedSchema) {
            resolvedSchema = _type.getParent().getComponent();
        }

        if (null == resolvedSchema) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.ImportSchemaCommand_can_not_resolve_schema_X, xsdSchema.getSchemaLocation()));
        }

        _importedSchema = new Schema(resolvedSchema, getModelRoot(), null, uri);
        return Status.OK_STATUS;
    }

    @Override
    public boolean canExecute() {
        return !(null == modelObject || null == _type);
    }

    public ISchema getSchema() {
        return _importedSchema;
    }
}
