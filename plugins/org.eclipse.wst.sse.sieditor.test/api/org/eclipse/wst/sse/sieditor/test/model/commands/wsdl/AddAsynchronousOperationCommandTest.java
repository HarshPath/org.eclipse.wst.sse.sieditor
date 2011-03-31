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

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddOperationCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceInterface;

public class AddAsynchronousOperationCommandTest extends AbstractCommandTest {
    private static final String ADDED_OPERATION_NAME = "addedOperation";

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final ServiceInterface intf = (ServiceInterface) modelRoot.getDescription().getAllInterfaces().iterator().next();
        final List<IOperation> ops = intf.getOperation(ADDED_OPERATION_NAME);
        assertFalse(ops.isEmpty());
        final IOperation operation = ops.get(0);
        assertNotNull(operation);
        assertEquals(OperationType.ASYNCHRONOUS, operation.getOperationStyle());
        assertEquals(0, operation.getAllOutputParameters().size());
        assertEquals(0, operation.getAllFaults().size());
        assertTrue(operation.getAllInputParameters().size() > 0);
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final ServiceInterface intf = (ServiceInterface) modelRoot.getDescription().getAllInterfaces().iterator().next();
        assertTrue(intf.getOperation(ADDED_OPERATION_NAME).isEmpty());
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ServiceInterface intf = (ServiceInterface) modelRoot.getDescription().getAllInterfaces().iterator().next();

        return new AddOperationCommand(modelRoot, intf, ADDED_OPERATION_NAME, OperationType.ASYNCHRONOUS);
    }
}
