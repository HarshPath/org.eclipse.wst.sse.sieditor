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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.wst.sse.sieditor.command.common.AbstractCompositeNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaNode;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class UpdateSchemasImportsCompositeCommand extends AbstractCompositeNotificationOperation {

    private UpdateSchemasImportCommand updateSchemasImportCommand;
    private final List<SchemaNode> schemas;

    private final IProgressMonitor progressMonitor;

    public UpdateSchemasImportsCompositeCommand(final IWsdlModelRoot root, final List<SchemaNode> schemasToExtract,
            final IProgressMonitor progressMonitor) {
        super(root, root.getModelObject(), Messages.UpdateSchemasImportsCompositeCommand_update_imports_command_label);
        this.schemas = new LinkedList<SchemaNode>(schemasToExtract);
        this.progressMonitor = progressMonitor;
    }

    @Override
    public AbstractNotificationOperation getNextOperation(final List<AbstractNotificationOperation> subOperations) {
        final IWsdlModelRoot wsdlModelRoot = (IWsdlModelRoot) root;

        if (updateSchemasImportCommand == null) {
            progressMonitor.beginTask(Messages.ExtractXmlSchemaCompositeCommand_updating_xml_schema_imports_subtask, schemas
                    .size());
        } else {
            progressMonitor.worked(1);
        }

        if (!schemas.isEmpty()) {
            final SchemaNode schemaNode = schemas.get(0);
            final List<ISchema> containedSchemas = wsdlModelRoot.getDescription().getContainedSchemas();
            updateSchemasImportCommand = new UpdateSchemasImportCommand(wsdlModelRoot, containedSchemas
                    .toArray(new ISchema[containedSchemas.size()]), schemaNode.getNamespace(), schemaNode.getFullPath(), true);
            schemas.remove(0);
            return updateSchemasImportCommand;
        }

        return null;
    }

}
