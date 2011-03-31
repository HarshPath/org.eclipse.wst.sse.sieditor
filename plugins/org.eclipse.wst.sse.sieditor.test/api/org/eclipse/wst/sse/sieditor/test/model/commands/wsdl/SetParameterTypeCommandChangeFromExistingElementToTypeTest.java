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
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandChainTest;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.util.WSDLConstants;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddInParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.SetParameterTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

public class SetParameterTypeCommandChangeFromExistingElementToTypeTest extends AbstractCommandChainTest {

    private static final String SERVICE_INTERFACE_NAME = "NewWSDLFile";
    private static final String OPERATION_NAME = "NewOperation";
    private static final String NEW_PARAMETER_NAME = "newParameter";

    private IWsdlModelRoot wsdlModelRoot;
    private IServiceInterface serviceInterface;
    private IOperation operation;

    private OperationParameter parameter;

    private AddInParameterCommand addInParameterCommand;
    private SetParameterTypeCommand setParameterTypeCommand;

    @Override
    protected String getWsdlFilename() {
        return "NewWSDLFile.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/simple/";
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        parameter = (OperationParameter) operation.getInputParameter(NEW_PARAMETER_NAME).get(0);
        assertNull(parameter.getComponent().getElementDeclaration());
        assertFalse(parameter.getComponent().getElement().hasAttribute(WSDLConstants.ELEMENT_ATTRIBUTE));
        assertNotNull(parameter.getComponent().getTypeDefinition());
        assertTrue(parameter.getComponent().getElement().hasAttribute(WSDLConstants.TYPE_ATTRIBUTE));
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertTrue(operation.getInputParameter(NEW_PARAMETER_NAME).isEmpty());
    }

    @Override
    protected AbstractNotificationOperation getNextOperation(final IWsdlModelRoot modelRoot) throws Exception {
        this.wsdlModelRoot = modelRoot;
        this.serviceInterface = wsdlModelRoot.getDescription().getInterface(SERVICE_INTERFACE_NAME).get(0);
        this.operation = serviceInterface.getOperation(OPERATION_NAME).get(0);

        if (addInParameterCommand == null) {
            assertEquals(1, operation.getAllInputParameters().size());
            addInParameterCommand = new AddInParameterCommand(wsdlModelRoot, operation, NEW_PARAMETER_NAME);
            return addInParameterCommand;
        }
        if (setParameterTypeCommand == null) {
            assertEquals(2, operation.getAllInputParameters().size());
            parameter = addInParameterCommand.getParameter();

            final Part component = parameter.getComponent();
            assertNotNull(component.getElementDeclaration());
            assertTrue(component.getElement().hasAttribute(WSDLConstants.ELEMENT_ATTRIBUTE));
            assertNull(component.getTypeDefinition());
            assertFalse(component.getElement().hasAttribute(WSDLConstants.TYPE_ATTRIBUTE));

            setParameterTypeCommand = new SetParameterTypeCommand(parameter, Schema.getDefaultSimpleType());
            return setParameterTypeCommand;
        }
        return null;
    }

}
