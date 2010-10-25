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
package org.eclipse.wst.sse.sieditor.model.reconcile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.core.common.IModelReconcileRegistry;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.reconcile.commands.AbstractModelReconcileCommand;
import org.eclipse.wst.sse.sieditor.model.reconcile.commands.WSDLModelReconcileCommand;
import org.eclipse.wst.sse.sieditor.model.reconcile.commands.XMLSchemaModelReconcileCommand;

/**
 * This is default implementation of the {@link IModelReconciler} interface.
 * 
 * 
 * 
 */
public class ModelReconciler implements IModelReconciler {

    private static final IModelReconciler INSTANCE = new ModelReconciler();

    private ModelReconciler() {

    }

    public static IModelReconciler instance() {
        return INSTANCE;
    }

    @Override
    public void aboutToReconcileModel(final IModelReconcileRegistry modelReconcileRegistry) {
        modelReconcileRegistry.clearRegistry();
    }

    @Override
    public boolean needsToReconcileModel(final IModelReconcileRegistry modelReconcileRegistry) {
        return modelReconcileRegistry.needsReconciling();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void reconcileModel(final IModelRoot modelRoot, final IModelReconcileRegistry modelReconcileRegistry) {
        final IModelObject modelObject = modelRoot.getModelObject();

        if (modelRoot instanceof IWsdlModelRoot) {
            final Definition definition = ((IWsdlModelRoot) modelRoot).getDescription().getComponent();

            List<XSDSchema> changedSchemas = modelReconcileRegistry.getChangedSchemas();
            if (changedSchemas.isEmpty() && definition.getETypes() != null) {
                changedSchemas = definition.getETypes().getSchemas();
            }

            reconcileWsdlModel(definition, changedSchemas, modelRoot, modelObject);

        } else if (modelRoot instanceof IXSDModelRoot) {
            reconcileXsdModel(((IXSDModelRoot) modelRoot).getSchema().getComponent(), modelRoot, modelObject);
        }
    }

    @Override
    public void modelReconciled(final IModelReconcileRegistry modelReconcileRegistry) {
        modelReconcileRegistry.clearRegistry();
    }

    private void reconcileWsdlModel(final Definition definition, final List<XSDSchema> changedSchemas,
            final IModelRoot modelRoot, final IModelObject modelObject) {
        final WSDLModelReconcileCommand command = new WSDLModelReconcileCommand(modelRoot, modelObject, changedSchemas,
                definition);
        executeReconcileModelCommand(command);
    }

    private void reconcileXsdModel(final XSDSchema schema, final IModelRoot modelRoot, final IModelObject modelObject) {
        final XMLSchemaModelReconcileCommand command = new XMLSchemaModelReconcileCommand(modelRoot, modelObject, schema);
        executeReconcileModelCommand(command);
    }

    private void executeReconcileModelCommand(final AbstractModelReconcileCommand command) {
        try {
            final Map<Object, Object> options = new HashMap<Object, Object>();
            options.put(Transaction.OPTION_NO_VALIDATION, Boolean.TRUE);
            options.put(Transaction.OPTION_NO_UNDO, Boolean.TRUE);

            command.setOptions(options);
            command.setReuseParentTransaction(true);

            command.execute(null, null);

        } catch (final ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
