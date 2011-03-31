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
import org.eclipse.wst.wsdl.Operation;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.ChangeOperationTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.WSDLModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType;

public class ChangeToAsynchronousOperationTypeNoInputTest extends AbstractCommandTest {

    protected IOperation operation;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(OperationType.ASYNCHRONOUS, operation.getOperationStyle());
        assertEquals(0, operation.getAllOutputParameters().size());
        assertEquals(1, operation.getAllInputParameters().size());

        final Operation wsdlOperation = (Operation) operation.getComponent();
        final org.eclipse.wst.wsdl.Message message = wsdlOperation.getEInput().getEMessage();

        assertNotNull(message);
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final Operation wsdlOperation = (Operation) operation.getComponent();

        assertNull(wsdlOperation.getEInput());
        assertNotNull(wsdlOperation.getEOutput());
        assertNotNull(wsdlOperation.getEOutput().getMessage());
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final WSDLModelRoot root = (WSDLModelRoot) modelRoot;
        operation = root.getDescription().getAllInterfaces().iterator().next().getOperation("NewOperation").get(0);

        return new ChangeOperationTypeCommand(root, operation, OperationType.ASYNCHRONOUS, false);
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/csns/change_to_async_operation/";
    }

    @Override
    protected String getWsdlFilename() {
        return "noinput.wsdl";
    }

}
