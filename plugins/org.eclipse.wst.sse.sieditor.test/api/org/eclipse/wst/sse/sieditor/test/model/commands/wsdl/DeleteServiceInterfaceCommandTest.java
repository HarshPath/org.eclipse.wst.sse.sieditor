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
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddServiceInterfaceCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteServiceInterfaceCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;

public class DeleteServiceInterfaceCommandTest extends AbstractCommandTest {
    private static final String SERVICE_INTERFACE_NAME = "serviceInterfaceToBeDeleted";

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertTrue(modelRoot.getDescription().getInterface(SERVICE_INTERFACE_NAME).isEmpty());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertFalse(modelRoot.getDescription().getInterface(SERVICE_INTERFACE_NAME).isEmpty());
        assertNotNull(modelRoot.getDescription().getInterface(SERVICE_INTERFACE_NAME).get(0));
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final Description descr = (Description) modelRoot.getDescription();

        final AddServiceInterfaceCommand cmd = new AddServiceInterfaceCommand(modelRoot, descr, SERVICE_INTERFACE_NAME) {
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
        return new DeleteServiceInterfaceCommand(modelRoot, descr, cmd.getInterface());
    }
}
