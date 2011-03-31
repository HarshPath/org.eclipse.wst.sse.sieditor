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
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RenameServiceInterfaceCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceInterface;

public class RenameServiceInterfaceCommandTest extends AbstractCommandTest {
    private String originalName;
    private String newName;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertTrue(modelRoot.getDescription().getInterface(originalName).isEmpty());
        assertFalse(modelRoot.getDescription().getInterface(newName).isEmpty());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertTrue(modelRoot.getDescription().getInterface(newName).isEmpty());
        assertFalse(modelRoot.getDescription().getInterface(originalName).isEmpty());
        assertNotNull(modelRoot.getDescription().getInterface(originalName).get(0));
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ServiceInterface intf = (ServiceInterface) modelRoot.getDescription().getAllInterfaces().iterator().next();
        originalName = intf.getName();
        newName = "renamedInterface";
        return new RenameServiceInterfaceCommand(modelRoot, intf, newName);
    }
}
