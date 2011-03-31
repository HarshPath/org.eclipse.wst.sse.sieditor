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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RenameParameterCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;

public class RenameParameterCommandTest extends AbstractCommandTest {
    private String originalName;
    private String newName;

    private boolean isInputParameter;

    @Override
    public void testCommandExecution() throws Throwable {
        isInputParameter = true;
        super.testCommandExecution();
        isInputParameter = false;
        super.testCommandExecution();
    }

    private AbstractNotificationOperation getRenameInputOperation(final IWsdlModelRoot modelRoot) {
        final IServiceInterface intf = modelRoot.getDescription().getAllInterfaces().iterator().next();
        final ServiceOperation operation = (ServiceOperation) intf.getAllOperations().iterator().next();
        final OperationParameter inputParameter = (OperationParameter) operation.getAllInputParameters().iterator().next();

        originalName = inputParameter.getName();
        newName = "renamedInputParameter";

        return new RenameParameterCommand(modelRoot, inputParameter, newName);
    }

    private AbstractNotificationOperation getRenameOutputOperation(final IWsdlModelRoot modelRoot) {
        final IServiceInterface intf = modelRoot.getDescription().getAllInterfaces().iterator().next();
        final ServiceOperation operation = (ServiceOperation) intf.getAllOperations().iterator().next();
        final OperationParameter outputParameter = (OperationParameter) operation.getAllOutputParameters().iterator().next();

        originalName = outputParameter.getName();
        newName = "renamedOutputParameter";

        return new RenameParameterCommand(modelRoot, outputParameter, newName);
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final IServiceInterface intf = modelRoot.getDescription().getAllInterfaces().iterator().next();
        final ServiceOperation operation = (ServiceOperation) intf.getAllOperations().iterator().next();
        if (isInputParameter) {
            assertTrue(operation.getInputParameter(originalName).isEmpty());
            assertFalse(operation.getInputParameter(newName).isEmpty());
            assertNotNull(operation.getInputParameter(newName).get(0));
        } else {
            assertTrue(operation.getOutputParameter(originalName).isEmpty());
            assertFalse(operation.getOutputParameter(newName).isEmpty());
            assertNotNull(operation.getOutputParameter(newName).get(0));
        }
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final IServiceInterface intf = modelRoot.getDescription().getAllInterfaces().iterator().next();
        final ServiceOperation operation = (ServiceOperation) intf.getAllOperations().iterator().next();
        if (isInputParameter) {
            assertTrue(operation.getInputParameter(newName).isEmpty());
            assertFalse(operation.getInputParameter(originalName).isEmpty());
            assertNotNull(operation.getInputParameter(originalName).get(0));
        } else {
            assertTrue(operation.getOutputParameter(newName).isEmpty());
            assertFalse(operation.getOutputParameter(originalName).isEmpty());
            assertNotNull(operation.getOutputParameter(originalName).get(0));
        }
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        return isInputParameter ? getRenameInputOperation(modelRoot) : getRenameOutputOperation(modelRoot);
    }
}
