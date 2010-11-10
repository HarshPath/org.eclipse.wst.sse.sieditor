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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.impl.XSDImportImpl;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.utils.SchemaLocationUtils;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class ChangeSchemasImportsLocationCommand extends AbstractNotificationOperation {

    private final IPath newImportSchemaLocationPath;
    private final String importSchemaNamespace;

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
    public ChangeSchemasImportsLocationCommand(final IWsdlModelRoot root, final ISchema[] schemas,
            final String importSchemaNamespace, final IPath newImportSchemaLocationPath) {
        super(root, schemas, Messages.UpdateSchemaImportsCommand_command_label);
        this.newImportSchemaLocationPath = newImportSchemaLocationPath;
        this.importSchemaNamespace = importSchemaNamespace;
    }

    @Override
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        final IWsdlModelRoot wsdlModelRoot = (IWsdlModelRoot) root;
        final ISchema[] schemas = (ISchema[]) getModelObjects();
        for (final ISchema schema : schemas) {
            updateSchemaImports(wsdlModelRoot, schema);
        }
        return Status.OK_STATUS;
    }

    private void updateSchemaImports(final IWsdlModelRoot wsdlModelRoot, final ISchema schema) {
        final XSDImport xsdImport = getXsdSchemaImportForUpdate(wsdlModelRoot, schema);
        if (xsdImport == null) {
            return;
        }
        updateSchemaLocation(wsdlModelRoot, schema, xsdImport);
    }

    private XSDImport getXsdSchemaImportForUpdate(final IWsdlModelRoot wsdlModelRoot, final ISchema schema) {
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

    private void updateSchemaLocation(final IWsdlModelRoot wsdlModelRoot, final ISchema schema, final XSDImport oldXsdImport) {
        final IPath wsdlLocation = new Path(wsdlModelRoot.getDescription().getRawLocation());
        final IPath relativeWsdlLocation = locationUtils().getLocationRelativeToWorkspace(wsdlLocation);
        final IPath relativeXmlSchemaLocation = locationUtils().getLocationRelativeToWorkspace(newImportSchemaLocationPath);

        final String relativeSchemaToWsdlLocation = locationUtils().getSchemaToWsdlRelativeLocation(relativeXmlSchemaLocation,
                relativeWsdlLocation);
        final IPath newLocation = new Path(relativeSchemaToWsdlLocation).append(newImportSchemaLocationPath.lastSegment());

        oldXsdImport.setSchemaLocation(newLocation.toOSString());

        if (oldXsdImport.getResolvedSchema() == null) {
            ((XSDImportImpl) oldXsdImport).importSchema();
        }
        schema.getComponent().eNotify(
                new ENotificationImpl((InternalEObject) schema.getComponent(), Notification.SET,
                        XSDPackage.XSD_SCHEMA_DIRECTIVE__RESOLVED_SCHEMA, null, oldXsdImport));
    }

    // =========================================================
    // helpers
    // =========================================================

    protected SchemaLocationUtils locationUtils() {
        return SchemaLocationUtils.instance();
    }

}
