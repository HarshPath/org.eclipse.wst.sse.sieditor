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
package org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.commands;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDImport;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.utils.SchemaLocationUtils;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class UpdateSchemasImportCommand extends AbstractNotificationOperation {

    private final IPath newImportSchemaLocationPath;
    private final String importSchemaNamespace;

    private final boolean skipImporterSchema;

    /**
     * This command updates all the given schema imports matching the given
     * importSchemaNamespace. The schema import is updated with the new
     * location.
     * 
     * @param schemas
     *            in which will search for import tags
     * @param importSchemaNamespace
     *            the namespace of the import tag which should be considered
     * @param newImportSchemaLocationPath
     *            new schemaLocation which will set to import tag
     */
    public UpdateSchemasImportCommand(final IWsdlModelRoot root, final ISchema[] schemas, final String importSchemaNamespace,
            final IPath newImportSchemaLocationPath, final boolean skipImporterSchema) {
        super(root, schemas, Messages.UpdateSchemaImportsCommand_command_label);
        this.newImportSchemaLocationPath = newImportSchemaLocationPath;
        this.importSchemaNamespace = importSchemaNamespace;
        this.skipImporterSchema = skipImporterSchema;
    }

    @Override
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        final IWsdlModelRoot wsdlModelRoot = (IWsdlModelRoot) root;
        final ISchema[] schemas = (ISchema[]) getModelObjects();
        for (final ISchema schema : schemas) {
            if (schema.getNamespace() == null && skipImporterSchema) {
                continue; // skip the "importer" special schema
            }
            updateSchemaImport(wsdlModelRoot, schema, importSchemaNamespace, newImportSchemaLocationPath);
        }
        return Status.OK_STATUS;
    }

    private void updateSchemaImport(final IWsdlModelRoot wsdlModelRoot, final ISchema schema, final String namespace,
            final IPath newImportSchemaLocationPath) {

        final XSDImport oldXsdImport = getXsdImport(schema, namespace);
        if (oldXsdImport == null) {
            return;
        }

        schema.getComponent().getContents().remove(oldXsdImport);

        final XSDImport newXsdImport = XSDFactory.eINSTANCE.createXSDImport();

        String newLocation = null;
        if (newImportSchemaLocationPath != null) {
            final IPath wsdlLocation = new Path(wsdlModelRoot.getDescription().getRawLocation());
            final IPath relativeWsdlLocation = locationUtils().getLocationRelativeToWorkspace(wsdlLocation);
            final IPath relativeXmlSchemaLocation = locationUtils().getLocationRelativeToWorkspace(newImportSchemaLocationPath);

            final String relativeSchemaToWsdlLocation = locationUtils().getSchemaToWsdlRelativeLocation(
                    relativeXmlSchemaLocation, relativeWsdlLocation);
            newLocation = new Path(relativeSchemaToWsdlLocation).append(newImportSchemaLocationPath.lastSegment()).toString();
        }

        newXsdImport.setSchemaLocation(newLocation);
        newXsdImport.setNamespace(namespace);

        schema.getComponent().getContents().add(0, newXsdImport);
    }

    private XSDImport getXsdImport(final ISchema schema, final String importSchemaNamespace) {
        for (final XSDConcreteComponent component : schema.getComponent().getContents()) {
            if (component instanceof XSDImport) {
                final XSDImport xsdImport = (XSDImport) component;
                if (xsdImport.getNamespace() != null && xsdImport.getNamespace().equals(importSchemaNamespace)) {
                    return xsdImport;
                }
            }
        }
        return null;
    }

    // =========================================================
    // helpers
    // =========================================================

    protected SchemaLocationUtils locationUtils() {
        return SchemaLocationUtils.instance();
    }

}
