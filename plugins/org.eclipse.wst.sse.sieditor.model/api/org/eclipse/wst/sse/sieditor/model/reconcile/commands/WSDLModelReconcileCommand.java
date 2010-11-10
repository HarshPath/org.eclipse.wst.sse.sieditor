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
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

/**
 * The WSDL model reconcile command subclass of
 * {@link AbstractModelReconcileCommand}
 * 
 * 
 * 
 */
public class WSDLModelReconcileCommand extends AbstractModelReconcileCommand {

    private final List<XSDSchema> changedSchemas;
    private final Definition definition;

    public WSDLModelReconcileCommand(final IModelRoot modelRoot, final IModelObject modelObject,
            final List<XSDSchema> changedSchemas, final Definition definition) {
        super(modelRoot, modelObject, Messages.WSDLModelReconcileCommand_wsdl_model_reconcile_operation_label);
        this.changedSchemas = changedSchemas;
        this.definition = definition;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        final ObjectsForResolveContainer container = findObjectsForResolve(definition);

        reconcileSchemasContents(container.getElementsForReferenceResolve(), container.getElementsForTypeResolve(), container
                .getAttributesForResolve());
        reconcileMessageParts(container.getMessagePartsForResolve());
        reconcileUtils().reconcileMessages(container.getMessagesForResolve(), definition);
        reconcileUtils().reconcileBindingsQNames(definition);

        final List<Definition> definitionsForResolve = getDefinitionsForResolve();
        for (final Definition definitionForResolve : definitionsForResolve) {
            // this.reconcileUtils().reconcileBindingsQNames(definition.getEBindings(),
            // definition.getTargetNamespace(),
            // definitionForResolve.getEPortTypes());
            reconcileUtils().reconcileOperationsMessagesReferences(container.getOperationsForResolve(), definition,
                    definitionForResolve.getEMessages(), null);
            // this.reconcileUtils().reconcileBindingsPortTypes(definition,
            // definitionForResolve.getEPortTypes());
            reconcileUtils().reconcileServicePortBindings(definition, definitionForResolve.getEBindings());
        }

        return Status.OK_STATUS;
    }

    private void reconcileSchemasContents(final List<XSDElementDeclaration> elementsForReferenceResolve,
            final List<XSDElementDeclaration> elementsForTypeResolve, final List<XSDAttributeDeclaration> attributesForResolve) {
        // we need to resolve the references for all the elements referring to
        // the changed schemas
        for (final XSDSchema schema : changedSchemas) {
            reconcileUtils().reconcileSchemaContents(schema, elementsForReferenceResolve, elementsForTypeResolve,
                    attributesForResolve);
        }
    }

    @SuppressWarnings("unchecked")
    private void reconcileMessageParts(final List<Part> messageParts) {
        if (definition.getETypes() != null) {
            // we need to fix the WSDL message parts for all the schemas
            final List<XSDSchema> allSchemas = definition.getETypes().getSchemas();
            for (final XSDSchema schema : allSchemas) {
                reconcileUtils().reconcileMessagePartsReferences(schema, messageParts, definition);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<Definition> getDefinitionsForResolve() {
        final List<Definition> definitionsForResolve = new LinkedList<Definition>();
        definitionsForResolve.add(definition);

        final EList<Import> eImports = definition.getEImports();
        for (final Import definitionImport : eImports) {
            if (definitionImport.getEDefinition() != null) {
                definitionsForResolve.add(definitionImport.getEDefinition());
            }
        }
        return definitionsForResolve;
    }

}
