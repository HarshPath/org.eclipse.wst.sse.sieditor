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
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;

public class AddServiceInterfaceCommandTest extends AbstractCommandTest {
    private static final String ADDED_SERVICE_INTERFACE_NAME = "newServiceInterface";

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertFalse(modelRoot.getDescription().getInterface(ADDED_SERVICE_INTERFACE_NAME).isEmpty());
        assertNotNull(modelRoot.getDescription().getInterface(ADDED_SERVICE_INTERFACE_NAME).get(0));
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertTrue(modelRoot.getDescription().getInterface(ADDED_SERVICE_INTERFACE_NAME).isEmpty());
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        return new AddServiceInterfaceCommand(modelRoot, modelRoot.getDescription(), ADDED_SERVICE_INTERFACE_NAME);
    }

}
