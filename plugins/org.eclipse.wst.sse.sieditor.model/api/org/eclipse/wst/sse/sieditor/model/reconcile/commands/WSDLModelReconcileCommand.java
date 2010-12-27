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
package org.eclipse.wst.sse.sieditor.model.reconcile.commands;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Import;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.XSDSchemaExtensibilityElement;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.reconcile.utils.IWsdlReconcileUtils;
import org.eclipse.wst.sse.sieditor.model.reconcile.utils.IXsdReconcileUtils;
import org.eclipse.wst.sse.sieditor.model.reconcile.utils.ObjectsForResolveContainer;
import org.eclipse.wst.sse.sieditor.model.reconcile.utils.ObjectsForResolveUtils;
import org.eclipse.wst.sse.sieditor.model.reconcile.utils.WsdlReconcileUtils;
import org.eclipse.wst.sse.sieditor.model.reconcile.utils.XsdReconcileUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;

/**
 * The WSDL model reconcile command subclass of
 * {@link AbstractModelReconcileCommand}
 * 
 */
public class WSDLModelReconcileCommand extends AbstractNotificationOperation {

    private final List<XSDSchema> allSchemas;
    private final IDescription description;

    @SuppressWarnings("unchecked")
    public WSDLModelReconcileCommand(final IModelRoot modelRoot, final IModelObject modelObject, final IDescription description) {
        super(modelRoot, modelObject, Messages.WSDLModelReconcileCommand_wsdl_model_reconcile_operation_label);

        allSchemas = new LinkedList<XSDSchema>();
        if (description.getComponent().getETypes() != null) {
            allSchemas.addAll(description.getComponent().getETypes().getSchemas());
        }
        this.description = description;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        final Definition definition = description.getComponent();
        final ObjectsForResolveContainer container = objectsForResolveUtils().findObjectsForResolve(definition, allSchemas);

        reconcileSchemasContents(container);
        reconcileMessageParts(container.getMessagePartsForResolve());
        wsdlReconcileUtils().reconcileMessages(container.getMessagesForResolve(), definition);
        wsdlReconcileUtils().reconcileBindingsQNames(definition);

        final List<Definition> definitionsForResolve = getDefinitionsForResolve();
        for (final Definition definitionForResolve : definitionsForResolve) {
            // this.reconcileUtils().reconcileBindingsQNames(definition.getEBindings(),
            // definition.getTargetNamespace(),
            // definitionForResolve.getEPortTypes());
            wsdlReconcileUtils().reconcileOperationsMessagesReferences(container.getOperationsForResolve(), definition,
                    definitionForResolve.getEMessages(), null);
            // this.reconcileUtils().reconcileBindingsPortTypes(definition,
            // definitionForResolve.getEPortTypes());
            wsdlReconcileUtils().reconcileServicePortBindings(definition, definitionForResolve.getEBindings());
        }
        return Status.OK_STATUS;
    }

    private void reconcileSchemasContents(final ObjectsForResolveContainer container) {
        // we need to resolve the references for all the elements referring to
        // the changed schemas
        for (final XSDSchema schema : allSchemas) {
            if (schema.eContainer() == null || schema.eContainer() instanceof XSDSchemaExtensibilityElement
                    && schema.eContainer().eContainer() == null) {
                continue;
            }
            xsdReconcileUtils().reconcileSchemaContents(schema, container);
        }
    }

    private void reconcileMessageParts(final List<Part> messageParts) {
        // we need to fix the WSDL message parts for all the schemas
        for (final XSDSchema schema : allSchemas) {
            wsdlReconcileUtils().reconcileMessagePartsReferences(schema, messageParts, description.getComponent());
        }
    }

    @SuppressWarnings("unchecked")
    private List<Definition> getDefinitionsForResolve() {
        final List<Definition> definitionsForResolve = new LinkedList<Definition>();
        definitionsForResolve.add(description.getComponent());

        final EList<Import> eImports = description.getComponent().getEImports();
        for (final Import definitionImport : eImports) {
            if (definitionImport.getEDefinition() != null) {
                definitionsForResolve.add(definitionImport.getEDefinition());
            }
        }
        return definitionsForResolve;
    }

    // =========================================================
    // helpers
    // =========================================================

    protected IXsdReconcileUtils xsdReconcileUtils() {
        return XsdReconcileUtils.instance();
    }

    protected IWsdlReconcileUtils wsdlReconcileUtils() {
        return WsdlReconcileUtils.instance();
    }

    protected ObjectsForResolveUtils objectsForResolveUtils() {
        return ObjectsForResolveUtils.instance();
    }
}
