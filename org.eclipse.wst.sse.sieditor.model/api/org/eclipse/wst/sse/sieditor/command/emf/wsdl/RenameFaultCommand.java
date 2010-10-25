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
import org.eclipse.emf.query.statements.FROM;
import org.eclipse.emf.query.statements.IQueryResult;
import org.eclipse.emf.query.statements.SELECT;
import org.eclipse.emf.query.statements.WHERE;
import org.eclipse.wst.wsdl.BindingFault;
import org.eclipse.wst.wsdl.Fault;
import org.eclipse.wst.wsdl.binding.soap.SOAPFault;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.BindingFaultCondition;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;

/**
 * 
 * 
 * 
 */
public class RenameFaultCommand extends AbstractNotificationOperation {

    private final String _newValue;

    public RenameFaultCommand(IWsdlModelRoot root, final IFault component, final String name) {
        super(root, component, Messages.RenameFaultCommand_rename_fault_command_label);
        this._newValue = name;
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

        Fault fault = (Fault) modelObject.getComponent();
        EObject baseComponent = root.getModelObject().getComponent();
        updateBindingFaults(baseComponent, fault);
        fault.setName(_newValue);
        return Status.OK_STATUS;
    }

    private void updateBindingFaults(EObject baseComponent, Fault searchForComponent) {

        BindingFaultCondition condition = new BindingFaultCondition(searchForComponent);
        IQueryResult result = new SELECT(new FROM(baseComponent), new WHERE(condition)).execute();

        for (Object next : result) {

            ((BindingFault) next).setName(_newValue);
            EList elements = ((BindingFault) next).getEExtensibilityElements();
            for (Object elmt : elements) {
                if (elmt instanceof SOAPFault) {
                    SOAPFault soapFault = (SOAPFault) elmt;
                    soapFault.setName(_newValue);
                }
            }
        }
    }
}