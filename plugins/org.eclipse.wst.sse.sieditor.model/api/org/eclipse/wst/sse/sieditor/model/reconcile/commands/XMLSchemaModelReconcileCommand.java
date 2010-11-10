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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

/**
 * The XSD model reconcile command subclass of
 * {@link AbstractModelReconcileCommand}
 * 
 * 
 * 
 */
public class XMLSchemaModelReconcileCommand extends AbstractModelReconcileCommand {

    private final XSDSchema schema;

    public XMLSchemaModelReconcileCommand(final IModelRoot modelRoot, final IModelObject modelObject, final XSDSchema schema) {
        super(modelRoot, modelObject, Messages.XMLSchemaModelReconcileCommand_xsd_model_reconcile_operation_label);
        this.schema = schema;
    }

    @Override
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        final ObjectsForResolveContainer container = findObjectsForResolve(schema);

        // we need to fix the element and attribute references
        reconcileUtils().reconcileSchemaContents(schema, container.getElementsForReferenceResolve(),
                container.getElementsForTypeResolve(), container.getAttributesForResolve());

        // XXX: we are not checking for XsdAttributeGroupDeclaration and
        // XsdModelGroupDeclaration elements. If we need to add such resolve
        // methods, this would be the right place to call them

        return Status.OK_STATUS;
    }

}
