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
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.util.WSDLConstants;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.SetParameterTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

public class SetParameterTypeCommandChangeFromMissingElementToTypeTest extends AbstractCommandTest {

    private static final String SERVICE_INTERFACE_NAME = "NewWSDLFile";
    private static final String OPERATION_NAME = "NewOperation";

    private IWsdlModelRoot wsdlModelRoot;
    private IServiceInterface serviceInterface;
    private IOperation operation;

    private OperationParameter parameter;

    private SetParameterTypeCommand setParameterTypeCommand;

    @Override
    protected String getWsdlFilename() {
        return "WsdlWithoutContent.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/csns/";
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        parameter = (OperationParameter) operation.getInputParameter("parameters").get(0);
        assertNull(parameter.getComponent().getElementDeclaration());
        assertFalse(parameter.getComponent().getElement().hasAttribute(WSDLConstants.ELEMENT_ATTRIBUTE));
        assertNotNull(parameter.getComponent().getTypeDefinition());
        assertTrue(parameter.getComponent().getElement().hasAttribute(WSDLConstants.TYPE_ATTRIBUTE));

        assertThereAreNoValidationErrors();
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertTrue(parameter.getComponent().getElement().hasAttribute(WSDLConstants.ELEMENT_ATTRIBUTE));
        assertNull(parameter.getComponent().getTypeDefinition());
        assertFalse(parameter.getComponent().getElement().hasAttribute(WSDLConstants.TYPE_ATTRIBUTE));
        assertThereAreValidationErrorsPresent(1);
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        this.wsdlModelRoot = modelRoot;
        this.serviceInterface = wsdlModelRoot.getDescription().getInterface(SERVICE_INTERFACE_NAME).get(0);
        this.operation = serviceInterface.getOperation(OPERATION_NAME).get(0);

        parameter = (OperationParameter) operation.getInputParameter("parameters").get(0);

        final Part component = parameter.getComponent();

        assertNull(component.getElementDeclaration());
        assertTrue(component.getElement().hasAttribute(WSDLConstants.ELEMENT_ATTRIBUTE));
        assertNull(component.getTypeDefinition());
        assertFalse(component.getElement().hasAttribute(WSDLConstants.TYPE_ATTRIBUTE));

        setParameterTypeCommand = new SetParameterTypeCommand(parameter, Schema.getDefaultSimpleType());
        return setParameterTypeCommand;
    }

}
