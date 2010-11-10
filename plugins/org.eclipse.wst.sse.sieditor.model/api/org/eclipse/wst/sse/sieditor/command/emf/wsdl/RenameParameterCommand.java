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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Part;

import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationParameter;

/**
 * Command for renaming operation parameter
 * 
 * 
 * 
 */
public class RenameParameterCommand extends AbstractWSDLNotificationOperation {
    private final String newValue;
    private final IParameter parameter;
    private String oldName;

    public RenameParameterCommand(final IWsdlModelRoot root, final IParameter parameter, final String name) {
        super(root, parameter, Messages.RenameParameterCommand_rename_operation_parameter_label);
        this.parameter = parameter;
        this.oldName = parameter.getName();
        newValue = name;
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        Part part = ((OperationParameter) modelObject).getComponent();
        EObject parentComponent = parameter.getParent().getComponent();
        if (parentComponent instanceof Operation) {
            Operation operation = (Operation) parentComponent;
            // this collection contains copies of the parts in the message and
            // is not updated when part is renamed
            EList<Part> parameterOrdering = operation.getEParameterOrdering();
            for (Part currentPart : parameterOrdering) {
                if (currentPart.getName().equals(oldName)) {
                    currentPart.setName(newValue);
                    // updates the DOM model after the part is renamed
                    operation.updateElement(false);
                    break;
                }
            }
        }
        part.setName(newValue);
        return Status.OK_STATUS;
    }

}
