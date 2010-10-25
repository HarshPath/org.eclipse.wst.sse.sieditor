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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.common.namespace.ImportsUpdaterUtils;
import org.eclipse.wst.sse.sieditor.command.emf.common.namespace.NamespaceMappingsUtils;
import org.eclipse.wst.sse.sieditor.command.emf.common.namespace.RemapReferencesUtils;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

/**
 * Command for setting namespace for {@link XSDSchema}
 * 
 * 
 * 
 */
public class SetNamespaceCommand extends AbstractNotificationOperation {

    private final String newNamespace;
    private final String oldNamespace;

    public SetNamespaceCommand(final IModelRoot root, final ISchema schema, final String namespace) {
        super(root, schema, Messages.SetNamespaceCommand_set_namespace_command_label);
        this.oldNamespace = schema.getNamespace();
        this.newNamespace = namespace;
    }

    @Override
    public org.eclipse.core.runtime.IStatus run(final org.eclipse.core.runtime.IProgressMonitor monitor,
            final org.eclipse.core.runtime.IAdaptable info) throws org.eclipse.core.commands.ExecutionException {
        return setNewNamespace(oldNamespace, newNamespace);
    }

    private IStatus setNewNamespace(final String oldNamespaceValue, final String newNamespaceValue) {
        final XSDSchema schema = (XSDSchema) modelObject.getComponent();

        if (newNamespaceValue == null || newNamespaceValue.isEmpty()) {
            schema.setTargetNamespace(null);
            return Status.OK_STATUS;
        }

        String namespacePrefix = setSchemaNamespaceUtils().updateNamespacePrefixMappings(schema, oldNamespaceValue,
                newNamespaceValue);
        if (namespacePrefix == null) {
            namespacePrefix = setSchemaNamespaceUtils().addXsdSchemaNamespaceMapping(schema, newNamespaceValue);
        }
        schema.setTargetNamespace(newNamespaceValue);

        if (root instanceof IWsdlModelRoot) {
            final IDescription description = ((IWsdlModelRoot) root).getDescription();
            final Definition definition = description.getComponent();

            setSchemaNamespaceUtils().updateDefinitionNamespace(definition, namespacePrefix, newNamespaceValue);
            setSchemaNamespaceUtils().updateContainedSchemasPrefixMappings(description, oldNamespaceValue, newNamespaceValue);
            
            importsUpdaterUtils().updateSchemaImports(definition, oldNamespaceValue, newNamespaceValue);
            elementReferencesRemapUtils().remapSchemaReferences(definition, schema, namespacePrefix);
            elementReferencesRemapUtils().remapMessageParts(definition, schema, oldNamespaceValue, newNamespaceValue,
                    namespacePrefix);

        } else if (root instanceof IXSDModelRoot) {
            elementReferencesRemapUtils().remapSchemaReferences(schema, schema, namespacePrefix);
        }

        return Status.OK_STATUS;
    }

    // =========================================================
    // helpers
    // =========================================================

    private NamespaceMappingsUtils setSchemaNamespaceUtils() {
        return NamespaceMappingsUtils.instance();
    }

    private RemapReferencesUtils elementReferencesRemapUtils() {
        return RemapReferencesUtils.instance();
    }

    private ImportsUpdaterUtils importsUpdaterUtils() {
        return ImportsUpdaterUtils.instance();
    }

}
