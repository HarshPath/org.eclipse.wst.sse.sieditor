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

import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDNamedComponent;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.common.utils.RefactorRenameUtils;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.INamedObject;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;

/**
 * Command for renaming an XSD Component
 * 
 * 
 */
public class RenameNamedComponent extends AbstractNotificationOperation {
    protected final String _name;
    protected final INamedObject _namedObject;

    public RenameNamedComponent(final IModelRoot root, final INamedObject namedObject, final String name) {
        super(root, namedObject, Messages.RenameNamedComponent_rename_operation_label);
        this._name = name;
        this._namedObject = namedObject;
    }

    @Override
    public org.eclipse.core.runtime.IStatus run(final org.eclipse.core.runtime.IProgressMonitor monitor,
            final org.eclipse.core.runtime.IAdaptable info) throws org.eclipse.core.commands.ExecutionException {
        final XSDNamedComponent namedComponent = (XSDNamedComponent) _namedObject.getComponent();
        setNamedComponentNewName(namedComponent, _name);

        final EObject baseComponent = root.getModelObject().getComponent();
        EmfXsdUtils.updateModelReferencers(baseComponent, namedComponent);

        return Status.OK_STATUS;
    }

    protected void setNamedComponentNewName(final XSDNamedComponent namedComponent, final String name) {
        RefactorRenameUtils.instance().refactorRenameComponent(root.getRoot().getModelObject().getComponent(), namedComponent,
                _name);
    }

    @Override
    public boolean canExecute() {
        if (modelObject == null || _namedObject == null || _name == null) {
            return false;
        }

        if (!(_namedObject.getComponent() instanceof XSDNamedComponent)) {
            return false;
        }

        return true;
    }

}
