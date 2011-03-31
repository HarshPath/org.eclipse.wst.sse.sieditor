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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddOperationCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.ChangeOperationTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.WSDLModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;

public class ChangeAsynchronousOperationTypeCommandTest extends AbstractCommandTest {
    private static final String NEW_OPERATION_NAME = "newSynchronousOperation";

    private ServiceOperation operation;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(OperationType.REQUEST_RESPONSE, operation.getOperationStyle());
        assertTrue(operation.getAllInputParameters().size() > 0);
        final Collection<IParameter> allOutputParameters = operation.getAllOutputParameters();
        assertTrue(allOutputParameters.size() > 0);
        final IParameter parameter = allOutputParameters.iterator().next();
        assertNotSame(UnresolvedType.instance(), parameter.getType());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(0, operation.getAllOutputParameters().size());
        assertEquals(0, operation.getAllFaults().size());
        assertTrue(operation.getAllInputParameters().size() > 0);
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final WSDLModelRoot root = (WSDLModelRoot) modelRoot;

        final AddOperationCommand addOperationCommand;

        addOperationCommand = new AddOperationCommand(root, root.getDescription().getAllInterfaces()
                .iterator().next(), NEW_OPERATION_NAME, OperationType.ASYNCHRONOUS) {
            @Override
            public boolean canUndo() {
                return false;
            }

            @Override
            public boolean canRedo() {
                return false;
            }
        };

        root.getEnv().execute(addOperationCommand);
        operation = addOperationCommand.getOperation();
        return new ChangeOperationTypeCommand(root, operation, OperationType.REQUEST_RESPONSE, false);
    }
}