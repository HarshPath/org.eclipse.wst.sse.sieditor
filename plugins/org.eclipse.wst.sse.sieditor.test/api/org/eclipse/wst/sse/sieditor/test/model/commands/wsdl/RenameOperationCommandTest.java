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
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RenameOperationCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;

public class RenameOperationCommandTest extends AbstractCommandTest {
    private String originalName;
    private String newName;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final IServiceInterface intf = modelRoot.getDescription().getAllInterfaces().iterator().next();
        assertTrue(intf.getOperation(originalName).isEmpty());
        assertFalse(intf.getOperation(newName).isEmpty());
        assertNotNull(intf.getOperation(newName).get(0));
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final IServiceInterface intf = modelRoot.getDescription().getAllInterfaces().iterator().next();
        assertTrue(intf.getOperation(newName).isEmpty());
        assertFalse(intf.getOperation(originalName).isEmpty());
        assertNotNull(intf.getOperation(originalName).get(0));
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final IServiceInterface intf = modelRoot.getDescription().getAllInterfaces().iterator().next();
        final ServiceOperation operation = (ServiceOperation) intf.getAllOperations().iterator().next();
        originalName = operation.getName();
        newName = "renamedOperation";
        return new RenameOperationCommand(modelRoot, operation, newName);
    }
}
