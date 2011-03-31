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
package org.eclipse.wst.sse.sieditor.test.model.validation.constraints.webservice.interoperability;

import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.INAPPROPRIATE_SOAP_BINDING_STYLE;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.INCOMPATIBILITY_BETWEEN_THE_INPUT_OUTPUT_IN_OPERATION_AND_INPUT_OUTPUT_IN_BINDING_OPERATION;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.NAMESPACE_ATTRIBUTE_MUST_NOT_BE_SPECIFIED_IN_SOAP_FAULT_HEADER_FAULT_AND_HEADER;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.OPERATION_DOESNOT_HAVE_SIMILAR_BINDING_OPERATION;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationService;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusProvider;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;

public class TestWSIOperationCompliantWithBigWSDL extends SIEditorBaseTest {
    private IDescription modelDescription;
    private IValidationService validationService;
    private IValidationStatusProvider provider;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        final IWsdlModelRoot modelRoot = (IWsdlModelRoot) getModelRoot("validation/WSDlWithDocumentStyleAndFaults.wsdl", //$NON-NLS-1$
                "WSDlWithDocumentStyleAndFaults.wsdl", ServiceInterfaceEditor.EDITOR_ID); //$NON-NLS-1$

        modelDescription = modelRoot.getDescription();

        validationService = editor.getValidationService();

        modelRoot.getEnv().getEditingDomain().getCommandStack().execute(
                new RecordingCommand(modelRoot.getEnv().getEditingDomain()) {
                    @Override
                    protected void doExecute() {
                        // validationService.validate(definition);
                    }
                });
        provider = validationService.getValidationStatusProvider();
    }

    @Test
    public void testForInappropriateBindingStyle() throws Exception {
        final IServiceInterface portType = modelDescription.getInterface("PortType1").get(0); //$NON-NLS-1$
        final IValidationStatusProvider provider = validationService.getValidationStatusProvider();
        final List<IValidationStatus> status = provider.getStatus(portType);
        assertEquals(1, status.size());
        IValidationStatus iValidationStatus = status.get(0);
        assertEquals(IStatus.WARNING, iValidationStatus.getSeverity());
        assertEquals(INAPPROPRIATE_SOAP_BINDING_STYLE, iValidationStatus.getMessage());
    }

    @Test
    public void testMissingBindingOperationForAGivenOperationCaseOne() throws Exception {
        final IServiceInterface portType = modelDescription.getInterface("PortType2").get(0); //$NON-NLS-1$
        final IOperation operation = portType.getOperation("operation3").get(0); //$NON-NLS-1$
        final List<IValidationStatus> statusesForOperation = provider.getStatus(operation);
        assertEquals(1, statusesForOperation.size());
        IValidationStatus iValidationStatus = statusesForOperation.get(0);
        assertEquals(IStatus.WARNING, iValidationStatus.getSeverity());
        assertEquals(OPERATION_DOESNOT_HAVE_SIMILAR_BINDING_OPERATION, iValidationStatus.getMessage());
    }

    @Test
    public void testMissingBindingOperationForAGivenOperationCaseTwo() throws Exception {
        final IServiceInterface portType = modelDescription.getInterface("PortType3").get(0); //$NON-NLS-1$
        final IOperation operation = portType.getOperation("operation5").get(0); //$NON-NLS-1$
        final List<IValidationStatus> statusesForOperation = provider.getStatus(operation);
        assertEquals(1, statusesForOperation.size());
        IValidationStatus iValidationStatus = statusesForOperation.get(0);
        assertEquals(IStatus.WARNING, iValidationStatus.getSeverity());
        assertEquals(OPERATION_DOESNOT_HAVE_SIMILAR_BINDING_OPERATION, iValidationStatus.getMessage());
    }

    @Test
    public void testForIncompatibilityBetweenTheInputOutputAndBindingInputOutputCaseOne() throws Exception {
        final IServiceInterface portType = modelDescription.getInterface("PortType4").get(0); //$NON-NLS-1$
        final IOperation operation = portType.getOperation("operation6").get(0); //$NON-NLS-1$
        final List<IValidationStatus> statusesForOperation = provider.getStatus(operation);
        assertEquals(1, statusesForOperation.size());
        IValidationStatus iValidationStatus = statusesForOperation.get(0);
        assertEquals(IStatus.WARNING, iValidationStatus.getSeverity());
        assertEquals(INCOMPATIBILITY_BETWEEN_THE_INPUT_OUTPUT_IN_OPERATION_AND_INPUT_OUTPUT_IN_BINDING_OPERATION,
                iValidationStatus.getMessage());
    }

    @Test
    public void testForIncompatibilityBetweenTheInputOutputAndBindingInputOutputCaseTwo() throws Exception {
        final IServiceInterface portType = modelDescription.getInterface("PortType5").get(0); //$NON-NLS-1$
        final IOperation operation = portType.getOperation("operation7").get(0); //$NON-NLS-1$
        final List<IValidationStatus> statusesForOperation = provider.getStatus(operation);
        assertEquals(1, statusesForOperation.size());
        IValidationStatus iValidationStatus = statusesForOperation.get(0);
        assertEquals(IStatus.WARNING, iValidationStatus.getSeverity());
        assertEquals(INCOMPATIBILITY_BETWEEN_THE_INPUT_OUTPUT_IN_OPERATION_AND_INPUT_OUTPUT_IN_BINDING_OPERATION,
                iValidationStatus.getMessage());
    }
    
    @Test
    public void testThatNamespaceMissingInNonSoapBodyElementsWhenRPCStyleIsSpecifiedCaseOne() throws Exception {
        final IServiceInterface portType = modelDescription.getInterface("PortType6").get(0); //$NON-NLS-1$
        final IOperation operation = portType.getOperation("operation8").get(0); //$NON-NLS-1$
        final List<IValidationStatus> statusesForOperation = provider.getStatus(operation);
        assertEquals(1, statusesForOperation.size());
        IValidationStatus iValidationStatus = statusesForOperation.get(0);
        assertEquals(IStatus.WARNING, iValidationStatus.getSeverity());
        assertEquals(NAMESPACE_ATTRIBUTE_MUST_NOT_BE_SPECIFIED_IN_SOAP_FAULT_HEADER_FAULT_AND_HEADER,
                iValidationStatus.getMessage());
    }
    
    @Test
    public void testForIncompatibilityBetweenTheInputOutputAndBindingInputOutputWhenDocumentStyleIsSpecified() throws Exception {
        final IServiceInterface portType = modelDescription.getInterface("PortType7").get(0); //$NON-NLS-1$
        final IOperation operation = portType.getOperation("operation9").get(0); //$NON-NLS-1$
        final List<IValidationStatus> statusesForOperation = provider.getStatus(operation);
        assertEquals(1, statusesForOperation.size());
        IValidationStatus iValidationStatus = statusesForOperation.get(0);
        assertEquals(IStatus.WARNING, iValidationStatus.getSeverity());
        assertEquals(INCOMPATIBILITY_BETWEEN_THE_INPUT_OUTPUT_IN_OPERATION_AND_INPUT_OUTPUT_IN_BINDING_OPERATION,
                iValidationStatus.getMessage());
    }
    
    @Test
    public void testThatNamespaceMissingInNonSoapBodyElementsWhenRPCStyleIsSpecifiedCaseTwo() throws Exception {
        final IServiceInterface portType = modelDescription.getInterface("PortType8").get(0); //$NON-NLS-1$
        final IOperation operation = portType.getOperation("operation10").get(0); //$NON-NLS-1$
        final List<IValidationStatus> statusesForOperation = provider.getStatus(operation);
        assertEquals(1, statusesForOperation.size());
        IValidationStatus iValidationStatus = statusesForOperation.get(0);
        assertEquals(IStatus.WARNING, iValidationStatus.getSeverity());
        assertEquals(NAMESPACE_ATTRIBUTE_MUST_NOT_BE_SPECIFIED_IN_SOAP_FAULT_HEADER_FAULT_AND_HEADER,
                iValidationStatus.getMessage());
    }
    @Override
    @After
    public void tearDown() throws Exception {
        disposeModel();
        super.tearDown();
    }
}
