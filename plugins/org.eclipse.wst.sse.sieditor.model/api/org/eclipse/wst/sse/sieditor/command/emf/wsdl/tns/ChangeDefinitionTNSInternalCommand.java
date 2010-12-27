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
package org.eclipse.wst.sse.sieditor.command.emf.wsdl.tns;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.Definition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.common.utils.ImportsUpdaterUtils;
import org.eclipse.wst.sse.sieditor.command.emf.common.utils.NamespaceMappingsUtils;
import org.eclipse.wst.sse.sieditor.command.emf.common.utils.RemapReferencesUtils;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;

/**
 * This command is responsible for the update of the definition targetNamespace.
 * If the definition has schemas without prefix and with the same
 * targetNamespace as the definition, it updates their targetNamespace as well. <br>
 * <br>
 * <i>NOTE: This command updates schema imports for the renamed schema
 * targetNamespaces.</i>
 * 
 */
class ChangeDefinitionTNSInternalCommand extends AbstractWSDLNotificationOperation {

    private final String newNamespaceValue;
    private final String oldNamespaceValue;

    public ChangeDefinitionTNSInternalCommand(final IWsdlModelRoot root, final IDescription desc, final String newNamespace) {
        super(root, desc, Messages.ChangeTargetNamespaceCommand_change_target_namespace_command_label);
        this.newNamespaceValue = newNamespace;
        this.oldNamespaceValue = desc.getNamespace();
    }

    @Override
    public boolean canExecute() {
        return super.canExecute() && newNamespaceValue != null;
    }

    @Override
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        final IDescription description = (IDescription) modelObject;
        final Definition definition = description.getComponent();

        definition.setTargetNamespace(newNamespaceValue);

        String prefix = namespaceUtils().updateNamespacePrefixMappings(description, oldNamespaceValue, newNamespaceValue);
        if (prefix == null) {
            prefix = namespaceUtils().addDefaultDefinitionSchemaNamespaceMapping(definition, newNamespaceValue);
        }
        this.importsUpdaterUtils().updateContainedSchemas(definition, oldNamespaceValue, newNamespaceValue);
        this.importsUpdaterUtils().updateDefinitionImports(definition, oldNamespaceValue, newNamespaceValue);
        this.remapUtils().remapMessageParts(definition, null, oldNamespaceValue, newNamespaceValue, prefix);

        return Status.OK_STATUS;
    }

    // =========================================================
    // helpers
    // =========================================================

    private NamespaceMappingsUtils namespaceUtils() {
        return NamespaceMappingsUtils.instance();
    }

    private ImportsUpdaterUtils importsUpdaterUtils() {
        return ImportsUpdaterUtils.instance();
    }

    private RemapReferencesUtils remapUtils() {
        return RemapReferencesUtils.instance();
    }

}
