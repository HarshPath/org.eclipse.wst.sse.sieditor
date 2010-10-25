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

import java.text.MessageFormat;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;

import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;

/**
 * Command for creating a wrapper element declaration for a given type
 * 
 * 
 * 
 */
public class EnsureParameterWrapperCommand extends AbstractWSDLNotificationOperation {

    private static final String MESSAGE_SUFFIX = "Parameter"; //$NON-NLS-1$

    private final Operation _operation;

    private static final String defaultName = "OperationRequest"; //$NON-NLS-1$
    private static final String defaultNamespace = "http://example.org"; //$NON-NLS-1$

    private AbstractType _newElement;

    private final AbstractType _type;

    public EnsureParameterWrapperCommand(final IWsdlModelRoot root, final Description description, final Operation operation,
            final AbstractType type) {
        super(root, description, Messages.EnsureParameterWrapperCommand_ensure_parameter_wrapper_command_label);
        this._operation = operation;
        this._type = type;
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        final Description description = (Description) getModelRoot().getDescription();

        ISchema schema = null;
        AbstractType resolvedType = null;
        if (_type.getRoot() == description) {
            schema = (Schema) _type.getParent();
            resolvedType = _type;
        } else {
            String namespace = description.getNamespace();
            namespace = (null == namespace || "".equals(namespace.trim()) //$NON-NLS-1$
            || !EmfXsdUtils.isValidURI(namespace)) ? defaultNamespace : namespace;
            final ISchema[] schemas = description.getSchema(namespace);
            schema = schemas.length > 0 ? schemas[0] : null;
            if (null == schema) {
                AddNewSchemaCommand cmd = new AddNewSchemaCommand(getModelRoot(), namespace);
                IStatus status = getModelRoot().getEnv().execute(cmd);
                if (!StatusUtils.canContinue(status)) {
                    return status;
                }
                schema = cmd.getNewSchema();
            }
            if (EmfXsdUtils.isSchemaForSchemaNS(_type.getNamespace()))
                resolvedType = _type;
            else
                resolvedType = (AbstractType) schema.resolveType(_type);
        }

        String name = _operation.getName();
        name = (null == name || "".equals(name.trim())) ? defaultName : name + MESSAGE_SUFFIX; //$NON-NLS-1$

        name = makeElementName(schema, name);

        StructureType structureType;
        final XSDSchema xsdSchema = schema.getComponent();
        final XSDSchemaContent resolvedTypeDefinition = xsdSchema.resolveElementDeclaration(name);

        if (resolvedTypeDefinition.eContainer() != null) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.EnsureParameterWrapperCommand_msg_failure_add_type_X_because_already_existing, name));
        }
        final AddStructureTypeCommand command = new AddStructureTypeCommand((IXSDModelRoot) schema.getModelRoot(), schema, name,
                true, resolvedType);
        command.execute(monitor, info);
        structureType = command.getStructureType();
        _newElement = structureType;
        return Status.OK_STATUS;
    }

    public boolean canExecute() {
        return null != _operation;
    }

    private final String makeElementName(final ISchema schema, final String prefix) {
        String name = prefix;
        if (null == schema.getType(true, name)) {
            return name;
        }

        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            name = prefix + i;
            if (null == schema.getType(true, name)) {
                return name;
            }
        }
        throw new IllegalStateException("Could not generate name for wrapper Element"); //$NON-NLS-1$
    }

    public AbstractType getWrappedType() {
        return _newElement;
    }
}