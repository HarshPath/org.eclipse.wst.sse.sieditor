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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.wst.wsdl.MessageReference;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Part;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;

/**
 * Command for deleting the input parameter
 * 
 * 
 * 
 */
public class DeleteInParameterCommand extends AbstractDeleteParameterCommand {
    public DeleteInParameterCommand(IWsdlModelRoot root, final IOperation component, final IParameter parameter) {
        super(root, component, parameter);
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

        IStatus status = super.run(monitor, info);
        if (status.getSeverity() == IStatus.ERROR) {
            return status;
        }

        final String name = part.getName();

        final Operation operation = ((ServiceOperation) modelObject).getComponent();
        EList<Part> parameterOrdering = operation.getEParameterOrdering();
        for (Part currentPart : parameterOrdering) {
            if (currentPart.getName().equals(name)) {
                parameterOrdering.remove(currentPart);
                // updates the DOM model after the part is renamed
                operation.updateElement(false);
                break;
            }
        }

        return Status.OK_STATUS;
    }

    @Override
    protected MessageReference getMessageReference(Operation operation) {
        return operation.getEInput();
    }

}
