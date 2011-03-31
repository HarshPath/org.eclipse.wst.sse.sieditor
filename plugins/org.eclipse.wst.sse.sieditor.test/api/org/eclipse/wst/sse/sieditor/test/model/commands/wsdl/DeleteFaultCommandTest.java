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
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddFaultCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteFaultCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;

public class DeleteFaultCommandTest extends AbstractCommandTest {
    private static final String FAULT_NAME = "faultToBeDeleted";

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final IServiceInterface intf = modelRoot.getDescription().getAllInterfaces().iterator().next();
        final ServiceOperation operation = (ServiceOperation) intf.getAllOperations().iterator().next();

        assertTrue(operation.getFault(FAULT_NAME).isEmpty());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final IServiceInterface intf = modelRoot.getDescription().getAllInterfaces().iterator().next();
        final ServiceOperation operation = (ServiceOperation) intf.getAllOperations().iterator().next();

        assertFalse(operation.getFault(FAULT_NAME).isEmpty());
        assertNotNull(operation.getFault(FAULT_NAME).get(0));
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final IServiceInterface intf = modelRoot.getDescription().getAllInterfaces().iterator().next();
        final ServiceOperation operation = (ServiceOperation) intf.getAllOperations().iterator().next();
        modelRoot.getEnv().execute(new AddFaultCommand(modelRoot, operation, FAULT_NAME) {
            @Override
            public boolean canUndo() {
                return false;
            }

            @Override
            public boolean canRedo() {
                return false;
            }
        });

        return new DeleteFaultCommand(modelRoot, operation, operation.getFault(FAULT_NAME).get(0));
    }

}
