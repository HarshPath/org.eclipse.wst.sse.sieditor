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

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.BindingOperation;
import org.eclipse.wst.wsdl.Definition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractWSDLNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.common.utils.RefactorRenameUtils;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;

/**
 * Command for renaming an operation
 * 
 * 
 */
public class RenameOperationCommand extends AbstractWSDLNotificationOperation {
    private final String name;

    public RenameOperationCommand(final IWsdlModelRoot root, final IOperation component, final String name) {
        super(root, component, Messages.RenameOperationCommand_rename_command_label);
        this.name = name;
    }

    @Override
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {

        final Description description = (Description) getModelRoot().getDescription();
        final Definition definition = description.getComponent();
        final ServiceInterface serviceInterface = (ServiceInterface) modelObject.getParent();

        // DOIT move this to WSDLAnalyzer
        final List<Binding> bindings = definition.getEBindings();
        for (final Binding binding : bindings) {
            if (!(serviceInterface.getComponent().equals(binding.getEPortType()))) {
                continue;
            }
            binding.updateElement(true);
            final List<BindingOperation> ops = binding.getEBindingOperations();
            for (final BindingOperation bindingOperation : ops) {
                if (bindingOperation.getOperation() == null) {
                    continue;
                }
                RefactorRenameUtils.instance().refactorRenameComponent(root.getRoot().getModelObject().getComponent(),
                        bindingOperation, bindingOperation.getOperation().getName());
                bindingOperation.setName(bindingOperation.getOperation().getName());
            }
        }

        RefactorRenameUtils.instance().refactorRenameComponent(root.getRoot().getModelObject().getComponent(),
                ((ServiceOperation) modelObject).getComponent(), name);

        return Status.OK_STATUS;
    }
}