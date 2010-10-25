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

import org.eclipse.wst.wsdl.MessageReference;
import org.eclipse.wst.wsdl.Operation;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationFault;

/**
 * Command for deleting the fault parameter
 * 
 * 
 * 
 */
public class DeleteFaultParameterCommand extends AbstractDeleteParameterCommand {

    private final IFault _component;

    public DeleteFaultParameterCommand(IWsdlModelRoot root, final IFault component, final IParameter parameter,
            final IOperation operation) {
        super(root, operation, parameter);
        this._component = component;
    }

    @Override
    protected MessageReference getMessageReference(Operation operation) {
        return _component.getComponent();
    }

}
