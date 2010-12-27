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

import java.util.List;
import java.util.Map;

import javax.wsdl.Types;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.XSDSchemaExtensibilityElement;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

public class AddNewSchemaCommand extends AbstractCompositeEnsuringDefinitionNotificationOperation {

    private AddNewSchemaCommandInternal addNewSchemaInternal;

    public AddNewSchemaCommand(IWsdlModelRoot root, String namespace) {
        super(root, root.getDescription(), Messages.AddNewSchemaCommand_add_new_schema_command_label);
        addNewSchemaInternal = new AddNewSchemaCommandInternal(root, namespace);
    }

    public ISchema getNewSchema() {
        return addNewSchemaInternal.getNewSchema();
    }

    @Override
    public AbstractNotificationOperation getNextOperation(List<AbstractNotificationOperation> subOperations) {
        AbstractNotificationOperation nextOperation = super.getNextOperation(subOperations);
        if (nextOperation != null) {
            return nextOperation;
        }
        if (subOperations.isEmpty() || (subOperations.size() == 1 && isDefinitionEnsured())) {
            return addNewSchemaInternal;
        }
        return null;
    }

    /**
     * 
     * 
     */
    public static class AddNewSchemaCommandInternal extends AbstractWSDLNotificationOperation {
        private static final String XMLNS_PREFIX_BASE = "ns";; //$NON-NLS-1$

        private static String generateXmlnsPrefix(final Map<String, String> wsdlNamespacesMap, Map<String, String> xsdNamespaceMap) {
            for (int i = 0; i < 10000; i++) {
                final String key = XMLNS_PREFIX_BASE + String.valueOf(i);
                if (wsdlNamespacesMap.containsKey(key) || xsdNamespaceMap.containsKey(key)) {
                    continue;
                }
                return key;
            }

            throw new IllegalStateException("Cannot generate xmlns prefix"); //$NON-NLS-1$
        }

        private final String namespace;
        private Schema newSchema;

        public AddNewSchemaCommandInternal(IWsdlModelRoot root, String namespace) {
            super(root, root.getDescription(), Messages.AddNewSchemaCommand_add_new_schema_command_label);

            this.namespace = namespace;
        }

        @SuppressWarnings("unchecked")
        @Override
        public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            final XSDSchema xsdSchema;

            xsdSchema = EmfXsdUtils.getXSDFactory().createXSDSchema();
            xsdSchema.setSchemaForSchemaQNamePrefix(EmfXsdUtils.XSD_PREFIX);
            xsdSchema.setTargetNamespace(namespace);

            final Map<String, String> qNamePrefixToNamespaceMap = xsdSchema.getQNamePrefixToNamespaceMap();
            qNamePrefixToNamespaceMap.put(xsdSchema.getSchemaForSchemaQNamePrefix(), EmfXsdUtils.getSchemaForSchemaNS());

            final XSDSchemaExtensibilityElement schemaExtensibilityEntity = EmfWsdlUtils.getWSDLFactory()
                    .createXSDSchemaExtensibilityElement();
            schemaExtensibilityEntity.setSchema(xsdSchema);

            final Description description = (Description) getModelRoot().getDescription();
            final Definition definition = description.getComponent();
            final String xmlnsPrefix = generateXmlnsPrefix(definition.getNamespaces(), qNamePrefixToNamespaceMap);
            qNamePrefixToNamespaceMap.put(xmlnsPrefix, namespace);

            Types types = definition.getETypes();
            if (types == null) {
                types = EmfWsdlUtils.getWSDLFactory().createTypes();
                definition.setTypes(types);
            }

            types.addExtensibilityElement(schemaExtensibilityEntity);
            schemaExtensibilityEntity.setEnclosingDefinition(definition);

            xsdSchema.updateElement();

            definition.addNamespace(xmlnsPrefix, namespace);

            newSchema = new Schema(xsdSchema, description, null);

            return Status.OK_STATUS;
        }

        public Schema getNewSchema() {
            return newSchema;
        }
    }
}