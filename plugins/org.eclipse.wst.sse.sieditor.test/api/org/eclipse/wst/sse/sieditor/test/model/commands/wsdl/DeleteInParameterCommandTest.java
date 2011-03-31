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
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.DeleteInParameterCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;

public class DeleteInParameterCommandTest extends AbstractCommandTest {
    private String parameterName;
    private IOperation operation;
    private Operation wsdlOperation;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertTrue(operation.getInputParameter(parameterName).isEmpty());
        assertEquals(2, wsdlOperation.getEParameterOrdering().size());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertFalse(operation.getInputParameter(parameterName).isEmpty());
        assertNotNull(operation.getInputParameter(parameterName).get(0));
        assertEquals(3, wsdlOperation.getEParameterOrdering().size());
    }

    @Override
    protected String getWsdlFilename() {
        return "rpcWSDL.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/csns/";
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final IServiceInterface serviceInterface = modelRoot.getDescription().getAllInterfaces().iterator().next();
        operation = serviceInterface.getOperation("NewOperation").get(0);
        wsdlOperation = (Operation) operation.getComponent();
        parameterName = "NewOperationRequest3";
        final IParameter parameter = operation.getInputParameter(parameterName).get(0);

        return new DeleteInParameterCommand(modelRoot, operation, parameter);
    }
}
