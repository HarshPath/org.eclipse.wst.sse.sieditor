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
package org.eclipse.wst.sse.sieditor.command.emf.wsdl;

import java.util.List;

import org.eclipse.wst.sse.sieditor.command.common.AbstractCompositeNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.commands.UpdateSchemasImportCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class InlineNamespaceCompositeCommand extends AbstractCompositeNotificationOperation {

    private CloneNamespaceCommand cloneNamespaceCommand;

    private UpdateSchemasImportCommand updateSchemasImportsCommand;

    private ISchema newInlinedSchema;

    public InlineNamespaceCompositeCommand(final IWsdlModelRoot root, final ISchema schemaToInline) {
        super(root, schemaToInline, Messages.InlineNamespaceCompositeCommand_inline_namespace_command);
    }

    @Override
    public AbstractNotificationOperation getNextOperation(final List<AbstractNotificationOperation> subOperations) {
        final IWsdlModelRoot wsdlModelRoot = (IWsdlModelRoot) root;
        final ISchema schema = (ISchema) modelObject;

        if (cloneNamespaceCommand == null) {
            cloneNamespaceCommand = new CloneNamespaceCommand(wsdlModelRoot, schema);
            return cloneNamespaceCommand;
        }

        if (updateSchemasImportsCommand == null) {
            newInlinedSchema = cloneNamespaceCommand.getTargetSchema();
            final List<ISchema> containedSchemas = wsdlModelRoot.getDescription().getContainedSchemas();
            updateSchemasImportsCommand = new UpdateSchemasImportCommand(wsdlModelRoot, containedSchemas
                    .toArray(new ISchema[containedSchemas.size()]), schema.getNamespace(), null, false);
            return updateSchemasImportsCommand;
        }

        return null;
    }

    public ISchema getNewInlinedSchema() {
        return newInlinedSchema;
    }

    /**
     * @return the schema to inline
     */
    public ISchema getSourceSchema() {
        return (ISchema) modelObject;
    }

}
