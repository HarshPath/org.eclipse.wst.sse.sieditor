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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.wst.sse.sieditor.command.common.AbstractCompositeNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.ImportSchemaCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaNode;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.search.DocumentType;
import org.eclipse.wst.sse.sieditor.model.utils.ResourceUtils;

public class WsdlImportSchemasCompositeCommand extends AbstractCompositeNotificationOperation {

    private ImportSchemaCommand wsdlImportSchemaCommand;

    private final List<SchemaNode> schemas;
    private final IProgressMonitor progressMonitor;

    public WsdlImportSchemasCompositeCommand(final IWsdlModelRoot root, final List<SchemaNode> schemas,
            final IProgressMonitor progressMonitor) {
        super(root, root.getModelObject(), Messages.WsdlImportSchemasCompositeCommand_import_in_wsdl_command_label);
        this.schemas = new LinkedList<SchemaNode>(schemas);
        this.progressMonitor = progressMonitor;
    }

    @Override
    public AbstractNotificationOperation getNextOperation(final List<AbstractNotificationOperation> subOperations) {
        final IWsdlModelRoot wsdlModelRoot = (IWsdlModelRoot) root;

        if (wsdlImportSchemaCommand == null) {
            progressMonitor.beginTask(Messages.ExtractXmlSchemaCompositeCommand_imprting_extracted_xml_schemas_subtask, schemas
                    .size());
        } else {
            // mark "import extracted schema" worked
            progressMonitor.worked(1);
        }

        if (!schemas.isEmpty()) {
            final SchemaNode schema = schemas.get(0);

            final String schemaLocationString = ResourceUtils.PLATFORM_RESOURCE_PREFIX + schema.getIFile().getFullPath().toString();
            URI schemaLocationUri = null;
            try {
                schemaLocationUri = new URI(schemaLocationString);
            } catch (final URISyntaxException e) {
                throw new RuntimeException(e);
            }
            wsdlImportSchemaCommand = new ImportSchemaCommand(wsdlModelRoot, wsdlModelRoot.getDescription(), schemaLocationUri,
                    schema.getNamespace(), null, DocumentType.XSD_SHEMA);

            schemas.remove(0);
            return wsdlImportSchemaCommand;
        }

        return null;
    }

}
