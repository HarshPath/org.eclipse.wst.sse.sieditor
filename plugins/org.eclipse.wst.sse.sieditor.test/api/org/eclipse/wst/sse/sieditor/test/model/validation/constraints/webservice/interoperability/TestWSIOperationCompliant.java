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

import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.HAS_AT_MOST_ONE_PART_LISTED_IN_PARTS_ATTRIBUTE;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.MISSING_NAMESPACE_ATTRIBUTE_IN_SOAP_BODY;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.NAMESPACE_ATTRIBUTE_ISNOT_ALLOWED_IN_SOAPBIND_ELEMENTS_WHEN_DOCUMENT_STYLE_IS_SPECIFIED;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.OPERATION_DOESNOT_HAVE_SIMILAR_BINDING_OPERATION;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.THE_PART_DOESNOT_HAVE_ELEMENT_ATTRIBUTE_IN_SOME_INPUT_MESSAGE;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.THE_PART_DOESNOT_HAVE_ELEMENT_ATTRIBUTE_IN_SOME_OUTPUT_MESSAGE;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.THE_PART_DOESNOT_HAVE_TYPE_ATTRIBUTE_IN_SOME_INPUT_MESSAGE;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.THE_VALUE_OF_USE_ATTRIBUTE_IN_SOAP_BODY_IS_NOT_LITERAL;

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
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;

public class TestWSIOperationCompliant extends SIEditorBaseTest {
    private IDescription modelDescription;
    private IValidationService validationService;
    private IValidationStatusProvider provider;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        final IWsdlModelRoot modelRoot = (IWsdlModelRoot) getModelRoot("validation/WSDLWithRPCandDOcumentStyleBindings.wsdl", //$NON-NLS-1$
                "WSDLWithRPCandDOcumentStyleBindings.wsdl", ServiceInterfaceEditor.EDITOR_ID); //$NON-NLS-1$

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
    public void testToManyParatsInTheMessageWhenDocumentStyleIsSpecified() throws Exception {

        final IOperation documentStyleBindingOperation = modelDescription.getInterface("PortTypeWithManyParameters").get(0) //$NON-NLS-1$
                .getOperation("documentStyleBindingOperation").get(0); //$NON-NLS-1$
        final List<IValidationStatus> documentStyleBindingOperationStatus = provider.getStatus(documentStyleBindingOperation);
        assertEquals(2, documentStyleBindingOperationStatus.size());
        final IValidationStatus moreThanOneInputParameterStatus = documentStyleBindingOperationStatus.get(0);
        assertEquals(IStatus.WARNING, moreThanOneInputParameterStatus.getSeverity());
        // assertEquals(WSIUtils.THE_OPERATION_HAS_MORE_THAN_ONE_INPUT_PARAMETER,
        // moreThanOneInputParameterStatus.getMessage());
        final IValidationStatus moreThanOneOutputParameterStatus = documentStyleBindingOperationStatus.get(1);
        assertEquals(IStatus.WARNING, moreThanOneOutputParameterStatus.getSeverity());
        // assertEquals(WSIUtils.THE_OPERATION_HAS_MORE_THAN_ONE_OUTPUT_PARAMETER,
        // moreThanOneOutputParameterStatus.getMessage());

        final IParameter inputParameterWithWarning = ((List<IParameter>) documentStyleBindingOperation.getAllInputParameters())
                .get(1);
        final List<IValidationStatus> inputParameterWithWarningStatus = provider.getStatus(inputParameterWithWarning);
        assertEquals(1, inputParameterWithWarningStatus.size());
        final IValidationStatus inputPartDoesNotHaveElementAttribute = inputParameterWithWarningStatus.get(0);
        assertEquals(IStatus.WARNING, inputPartDoesNotHaveElementAttribute.getSeverity());
        assertEquals(THE_PART_DOESNOT_HAVE_ELEMENT_ATTRIBUTE_IN_SOME_INPUT_MESSAGE, inputPartDoesNotHaveElementAttribute
                .getMessage());

        final IParameter outputParameterWithWarning = ((List<IParameter>) documentStyleBindingOperation.getAllOutputParameters())
                .get(1);
        final List<IValidationStatus> outputParameterWithWarningStatus = provider.getStatus(outputParameterWithWarning);
        assertEquals(1, outputParameterWithWarningStatus.size());
        final IValidationStatus outputPartDoesNotHaveElementAttribute = outputParameterWithWarningStatus.get(0);
        assertEquals(IStatus.WARNING, outputPartDoesNotHaveElementAttribute.getSeverity());
        assertEquals(THE_PART_DOESNOT_HAVE_ELEMENT_ATTRIBUTE_IN_SOME_OUTPUT_MESSAGE, outputPartDoesNotHaveElementAttribute
                .getMessage());

    }

    @Test
    public void testDoesNotHaveTypeAttributeInMessageWhenRPCStyleIsSpecified() throws Exception {

        final IOperation newOperationOne = modelDescription.getInterface("Service").get(0) //$NON-NLS-1$
                .getOperation("NewOperation1").get(0); //$NON-NLS-1$
        final List<IValidationStatus> newOperationOneStatus = provider.getStatus(newOperationOne);
        assertEquals(0, newOperationOneStatus.size());

        final IParameter inputParameterWithWarning = ((List<IParameter>) newOperationOne.getAllInputParameters()).get(2);
        final List<IValidationStatus> inputParameterWithWarningStatus = provider.getStatus(inputParameterWithWarning);
        int numberOfWarnings = 0;
        int indexOfTheWarning = -1;
        for (final IValidationStatus status : inputParameterWithWarningStatus) {
            if (status.getSeverity() == IStatus.WARNING) {
                numberOfWarnings++;
            }
            indexOfTheWarning++;
        }
        assertEquals("There are more or less warnings", numberOfWarnings, 1);
        assertEquals(THE_PART_DOESNOT_HAVE_TYPE_ATTRIBUTE_IN_SOME_INPUT_MESSAGE, inputParameterWithWarningStatus.get(
                indexOfTheWarning).getMessage());

        final IParameter outputParameterWithWarning = ((List<IParameter>) newOperationOne.getAllOutputParameters()).get(1);
        final List<IValidationStatus> outputParameterWithWarningStatus = provider.getStatus(outputParameterWithWarning);
        assertEquals(1, outputParameterWithWarningStatus.size());
        assertEquals(IStatus.WARNING, outputParameterWithWarningStatus.get(0).getSeverity());

    }

    @Test
    public void testMissingNamespaceAttributeInSoapBodyWhenRPCStyleIsSpecified() throws Exception {

        final IOperation newOperationTwo = modelDescription.getInterface("Service").get(0) //$NON-NLS-1$
                .getOperation("NewOperation2").get(0); //$NON-NLS-1$
        final List<IValidationStatus> newOperationTwoStatus = provider.getStatus(newOperationTwo);
        assertEquals(1, newOperationTwoStatus.size());
        assertEquals(IStatus.WARNING, newOperationTwoStatus.get(0).getSeverity());
        assertEquals(MISSING_NAMESPACE_ATTRIBUTE_IN_SOAP_BODY, newOperationTwoStatus.get(0).getMessage());

    }

    @Test
    public void testThatNamespaceAttributeIsNotAllowedWhenDocumentStyleIsSpecified() throws Exception {

        final IOperation newOperationThree = modelDescription.getInterface("Service").get(0) //$NON-NLS-1$
                .getOperation("NewOperation3").get(0); //$NON-NLS-1$
        final List<IValidationStatus> newOperationThreeStatus = provider.getStatus(newOperationThree);
        assertEquals(1, newOperationThreeStatus.size());
        assertEquals(IStatus.WARNING, newOperationThreeStatus.get(0).getSeverity());
        assertEquals(NAMESPACE_ATTRIBUTE_ISNOT_ALLOWED_IN_SOAPBIND_ELEMENTS_WHEN_DOCUMENT_STYLE_IS_SPECIFIED,
                newOperationThreeStatus.get(0).getMessage());

    }

    @Test
    public void testThatMissingBindingForANewOperation4() throws Exception {

        final IOperation newOperationFour = modelDescription.getInterface("Service").get(0) //$NON-NLS-1$
                .getOperation("NewOperation4").get(0); //$NON-NLS-1$
        final List<IValidationStatus> newOperationFourStatus = provider.getStatus(newOperationFour);
        assertEquals(1, newOperationFourStatus.size());
        assertEquals(IStatus.WARNING, newOperationFourStatus.get(0).getSeverity());
        assertEquals(OPERATION_DOESNOT_HAVE_SIMILAR_BINDING_OPERATION, newOperationFourStatus.get(0).getMessage());

    }

    @Test
    public void testNotLiteralValueForSomeUseAttributeCaseOne() throws Exception {

        final IOperation newOperationFive = modelDescription.getInterface("Service").get(0) //$NON-NLS-1$
                .getOperation("NewOperation5").get(0); //$NON-NLS-1$
        final List<IValidationStatus> newOperationFiveStatus = provider.getStatus(newOperationFive);
        assertEquals(1, newOperationFiveStatus.size());
        assertEquals(IStatus.WARNING, newOperationFiveStatus.get(0).getSeverity());
        assertEquals(THE_VALUE_OF_USE_ATTRIBUTE_IN_SOAP_BODY_IS_NOT_LITERAL, newOperationFiveStatus.get(0).getMessage());
    }

    @Test
    public void testNotLiteralValueForSomeUseAttributeCaseTwo() throws Exception {

        final IOperation newOperationSix = modelDescription.getInterface("Service").get(0) //$NON-NLS-1$
                .getOperation("NewOperation5").get(0); //$NON-NLS-1$
        final List<IValidationStatus> newOperationSixStatus = provider.getStatus(newOperationSix);
        assertEquals(1, newOperationSixStatus.size());
        assertEquals(IStatus.WARNING, newOperationSixStatus.get(0).getSeverity());
        assertEquals(THE_VALUE_OF_USE_ATTRIBUTE_IN_SOAP_BODY_IS_NOT_LITERAL, newOperationSixStatus.get(0).getMessage());
    }

    @Test
    public void testNotAllWSDLPartsRefferedByPartAndPartsAttributeWhenDocumentStyleIsSpecified() throws Exception {

        final IOperation setUserSettingsOperation = modelDescription.getInterface("FINAL").get(0) //$NON-NLS-1$
                .getOperation("SetUserOofSettings").get(0); //$NON-NLS-1$
        final List<IValidationStatus> newOperationSixStatus = provider.getStatus(setUserSettingsOperation);
        assertEquals(1, newOperationSixStatus.size());
        assertEquals(IStatus.WARNING, newOperationSixStatus.get(0).getSeverity());
        assertEquals(HAS_AT_MOST_ONE_PART_LISTED_IN_PARTS_ATTRIBUTE, newOperationSixStatus.get(0).getMessage());
    }

    @Test
    public void testPortTypeForMissingTransportAttributeAndInappropriateValueForTransportAttribute() throws Exception {
        final IServiceInterface portType = modelDescription.getInterface("Service").get(0); //$NON-NLS-1$
        final IValidationStatusProvider provider = validationService.getValidationStatusProvider();
        final List<IValidationStatus> status = provider.getStatus(portType);
        assertEquals(7, status.size());
        final IValidationStatus missingTransportAttributeStatus = status.get(0);
        assertEquals(IStatus.WARNING, missingTransportAttributeStatus.getSeverity());
        // the assertion messages change their position in status list, because
        // when the messages were being created, they are added in Set which is
        // tree based collection and the positions can be different any time
        // assertEquals(TRANSPORT_ATTRIBUTE_MISSING,
        // missingTransportAttributeStatus.getMessage());
        final IValidationStatus badTransportAttributeValue = status.get(1);
        assertEquals(IStatus.WARNING, badTransportAttributeValue.getSeverity());
        // assertEquals(INAPPROPRIATE_TRANSPORT_ATTRIBUTE_IN_SOME_SOAP_BINDING,
        // badTransportAttributeValue.getMessage());

    }

    @Override
    @After
    public void tearDown() throws Exception {
        disposeModel();
        super.tearDown();
    }
}
