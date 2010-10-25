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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.wst.sse.sieditor.command.common.AbstractCompositeNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.DeleteSetCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.dependencies.SchemaNode;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.search.DocumentType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

/**
 * This command replaces the given inlined schemas with schema imports.
 * 
 * 
 * 
 */
public class ExtractXmlSchemaCompositeCommand extends AbstractCompositeNotificationOperation {

    private ChangeSchemasImportsLocationCommand changeSchemaImportsCommand;
    private DeleteSetCommand deleteSchemaCommand;
    private ImportSchemaCommand importSchemaCommand;

    private final List<SchemaNode> updatedSchemas;
    private final List<SchemaNode> schemasToExtract;
    private final Collection<IModelObject> schemaModelObjects;

    private final IProgressMonitor progressMonitor;

    public ExtractXmlSchemaCompositeCommand(final IWsdlModelRoot root, final IModelObject modelObject,
            final Set<SchemaNode> schemasToExtract, final IProgressMonitor progressMonitor) {
        super(root, modelObject, Messages.ExtractXmlSchemaCompositeCommand_command_label);

        this.updatedSchemas = new LinkedList<SchemaNode>(schemasToExtract);
        this.schemasToExtract = new LinkedList<SchemaNode>(schemasToExtract);
        this.schemaModelObjects = new HashSet<IModelObject>();
        for (final SchemaNode node : schemasToExtract) {
            schemaModelObjects.add(node.getSchema());
        }
        this.progressMonitor = progressMonitor;
    }

    @Override
    public AbstractNotificationOperation getNextOperation(final List<AbstractNotificationOperation> subOperations) {
        final IWsdlModelRoot wsdlModelRoot = (IWsdlModelRoot) root;

        if (deleteSchemaCommand == null) {
            progressMonitor.beginTask(Messages.ExtractXmlSchemaCompositeCommand_removing_inlined_xml_schemas_subtask, 1);
            final List<IModelObject> parents = new LinkedList<IModelObject>();
            parents.add(wsdlModelRoot.getModelObject());
            deleteSchemaCommand = new DeleteSetCommand(root, parents, schemaModelObjects);
            return deleteSchemaCommand;
        }

        if (importSchemaCommand == null) {
            // mark "remove schemas" worked
            progressMonitor.worked(1);
            progressMonitor.beginTask(Messages.ExtractXmlSchemaCompositeCommand_imprting_extracted_xml_schemas_subtask,
                    schemasToExtract.size());
        } else {
            // mark "import extracted schema" worked
            progressMonitor.worked(1);
        }

        if (!schemasToExtract.isEmpty()) {
            final SchemaNode schema = schemasToExtract.get(0);

            final String schemaLocationString = "platform:/resource" + schema.getIFile().getFullPath().toString(); //$NON-NLS-1$
            URI schemaLocationUri = null;
            try {
                schemaLocationUri = new URI(schemaLocationString);
            } catch (final URISyntaxException e) {
                throw new RuntimeException(e);
            }

            importSchemaCommand = new ImportSchemaCommand(wsdlModelRoot, wsdlModelRoot.getDescription(), schemaLocationUri,
                    schema.getNamespace(), null, DocumentType.XSD_SHEMA);

            schemasToExtract.remove(0);
            return importSchemaCommand;
        }

        if (changeSchemaImportsCommand == null) {
            progressMonitor.beginTask(Messages.ExtractXmlSchemaCompositeCommand_updating_xml_schema_imports_subtask,
                    updatedSchemas.size());
        } else {
            progressMonitor.worked(1);
        }

        if (!updatedSchemas.isEmpty()) {
            final SchemaNode schemaNode = updatedSchemas.get(0);
            final List<ISchema> containedSchemas = wsdlModelRoot.getDescription().getContainedSchemas();
            changeSchemaImportsCommand = new ChangeSchemasImportsLocationCommand(wsdlModelRoot, containedSchemas
                    .toArray(new ISchema[containedSchemas.size()]), schemaNode.getNamespace(), schemaNode.getFullPath());
            updatedSchemas.remove(0);
            return changeSchemaImportsCommand;
        }
        return null;
    }

}
