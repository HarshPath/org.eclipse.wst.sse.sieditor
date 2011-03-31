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
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddOperationCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteOutParameterCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;

public class DeleteOutParameterCommandTest extends AbstractCommandTest {
    private String parameterName;
    private ServiceOperation operation;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertTrue(operation.getOutputParameter(parameterName).isEmpty());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertFalse(operation.getOutputParameter(parameterName).isEmpty());
        assertNotNull(operation.getOutputParameter(parameterName).get(0));
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ServiceInterface intf = (ServiceInterface) modelRoot.getDescription().getAllInterfaces().iterator().next();
        final AddOperationCommand cmd = new AddOperationCommand(modelRoot, intf, "myNewOperation2",
                OperationType.REQUEST_RESPONSE) {
            @Override
            public boolean canUndo() {
                return false;
            }

            @Override
            public boolean canRedo() {
                return false;
            }
        };

        modelRoot.getEnv().execute(cmd);

        operation = cmd.getOperation();
        final IParameter parameter = operation.getAllOutputParameters().iterator().next();
        parameterName = parameter.getName();

        return new DeleteOutParameterCommand(modelRoot, operation, parameter);
    }
}
