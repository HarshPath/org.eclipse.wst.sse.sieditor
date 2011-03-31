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
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.test.model.commands.wsdl;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AbstractCompositeEnsuringDefinitionNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddServiceInterfaceAndOperationCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;

public class AddServiceInterfaceAndOperationCommandTest extends AbstractCommandTest {

    private static final String PARAMETER_NAME = "Parameter";
    private static final String OPERATION_NAME = "NewTestOperation";

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final List<IServiceInterface> interfaces = modelRoot.getDescription().getInterface(OPERATION_NAME);
        assertEquals(0, interfaces.size());
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final IDescription description = modelRoot.getDescription();
        final List<IServiceInterface> interfaces = description.getInterface(OPERATION_NAME);
        assertEquals(1, interfaces.size());
        final IServiceInterface iFace = interfaces.get(0);
        assertNotNull(iFace);
        final Collection<IOperation> allOperations = iFace.getAllOperations();
        assertEquals(1, allOperations.size());
        final IOperation operation = allOperations.iterator().next();
        assertNotNull(operation);
        assertEquals(0, operation.getAllFaults().size());
        final Collection<IParameter> inputs = operation.getAllInputParameters();
        assertEquals(1, inputs.size());
        final IParameter input = inputs.iterator().next();
        assertNotNull(input);
        assertEquals(PARAMETER_NAME, input.getName());
        final Collection<IParameter> outputs = operation.getAllOutputParameters();
        assertEquals(1, outputs.size());
        final IParameter output = outputs.iterator().next();
        assertNotNull(input);
        assertEquals(PARAMETER_NAME, output.getName());
    }

    @Override
    protected AbstractCompositeEnsuringDefinitionNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        return new AddServiceInterfaceAndOperationCommand(modelRoot, modelRoot.getDescription(), OPERATION_NAME);
    }

}
