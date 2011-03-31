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

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationService;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusProvider;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;

public class TestWSIPortTypesCompliant extends SIEditorBaseTest {

    private IDescription modelDescription;
    private IValidationService validationService;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testMissingBindingsForThePortType() throws Exception {
        final IWsdlModelRoot modelRoot = (IWsdlModelRoot) getModelRoot("validation/WSDLwithoutBindingsForThePortType.wsdl", //$NON-NLS-1$
                "WSDLwithoutBindingsForThePortType.wsdl", ServiceInterfaceEditor.EDITOR_ID); //$NON-NLS-1$

        modelDescription = modelRoot.getDescription();

        validationService = editor.getValidationService();
        // final ValidationService validationService = new
        // ValidationService(resourceSet);
        // validationService.addModelAdapter(new EsmXsdModelAdapter());
        // validationService.addModelAdapter(new
        // ESMModelAdapter(modelRoot));

        modelRoot.getEnv().getEditingDomain().getCommandStack().execute(
                new RecordingCommand(modelRoot.getEnv().getEditingDomain()) {
                    @Override
                    protected void doExecute() {
                        // validationService.validate(definition);
                    }
                });

        final IServiceInterface portType = modelDescription.getInterface("WSDLwithoutDocumentStyleBinding").get(0); //$NON-NLS-1$

        final IValidationStatusProvider provider = validationService.getValidationStatusProvider();
        final List<IValidationStatus> status = provider.getStatus(portType);
        assertEquals(1, status.size());
        IValidationStatus iValidationStatus = status.get(0);
        assertEquals(IStatus.WARNING, iValidationStatus.getSeverity());
        assertEquals(WSIConstants.BINDINGS_ARE_NOT_SPECIFIED_MESSAGE, iValidationStatus.getMessage());
    }

    @Test
    public void testWsdlWithoutSOAPBindings() throws Exception {
        final IWsdlModelRoot modelRoot = (IWsdlModelRoot) getModelRoot("validation/WSDLwithoutSOAPBinding.wsdl", //$NON-NLS-1$
                "WSDLwithoutSOAPBinding.wsdl", ServiceInterfaceEditor.EDITOR_ID); //$NON-NLS-1$

        modelDescription = modelRoot.getDescription();

        validationService = editor.getValidationService();
        // final ValidationService validationService = new
        // ValidationService(resourceSet);
        // validationService.addModelAdapter(new EsmXsdModelAdapter());
        // validationService.addModelAdapter(new
        // ESMModelAdapter(modelRoot));

        modelRoot.getEnv().getEditingDomain().getCommandStack().execute(
                new RecordingCommand(modelRoot.getEnv().getEditingDomain()) {
                    @Override
                    protected void doExecute() {
                        // validationService.validate(definition);
                    }
                });

        final IServiceInterface portType = modelDescription.getInterface("WSDLwithoutSOAPBinding").get(0); //$NON-NLS-1$

        final IValidationStatusProvider provider = validationService.getValidationStatusProvider();
        final List<IValidationStatus> status = provider.getStatus(portType);
        assertEquals(1, status.size());
        IValidationStatus iValidationStatus = status.get(0);
        assertEquals(IStatus.WARNING, iValidationStatus.getSeverity());
        assertEquals(WSIConstants.NO_SOAP_BINDING_MESSAGE, iValidationStatus.getMessage());
    }

    @Test
    public void testBigWSICompliantWsdl() throws Exception {
        final IWsdlModelRoot modelRoot = (IWsdlModelRoot) getModelRoot("validation/BigWSDLWhichIsWSICompliant.wsdl", //$NON-NLS-1$
                "BigWSDLWhichIsWSICompliant.wsdl", ServiceInterfaceEditor.EDITOR_ID); //$NON-NLS-1$

        modelDescription = modelRoot.getDescription();

        validationService = editor.getValidationService();

        modelRoot.getEnv().getEditingDomain().getCommandStack().execute(
                new RecordingCommand(modelRoot.getEnv().getEditingDomain()) {
                    @Override
                    protected void doExecute() {
                        // validationService.validate(definition);
                    }
                });
        final IValidationStatusProvider provider = validationService.getValidationStatusProvider();

        final IOperation newOperation = modelDescription.getInterface("NewWSDLFile3").get(0).getOperation("NewOperation").get(0); //$NON-NLS-1$ //$NON-NLS-2$
        final List<IValidationStatus> status = provider.getStatus(newOperation);
        assertEquals(0, status.size());

        final IOperation newOperationOne = modelDescription.getInterface("Service").get(0).getOperation("NewOperation1").get(0); //$NON-NLS-1$//$NON-NLS-2$
        final List<IValidationStatus> statusOne = provider.getStatus(newOperationOne);
        assertEquals(0, statusOne.size());

        final IOperation newOperationTwo = modelDescription.getInterface("Service").get(0).getOperation("NewOperation2").get(0); //$NON-NLS-1$ //$NON-NLS-2$
        final List<IValidationStatus> statusTwo = provider.getStatus(newOperationTwo);
        assertEquals(0, statusTwo.size());

        final IOperation newOperationThree = modelDescription.getInterface("Service").get(0).getOperation("NewOperation3").get(0); //$NON-NLS-1$//$NON-NLS-2$
        final List<IValidationStatus> statusThree = provider.getStatus(newOperationThree);
        assertEquals(0, statusThree.size());

    }

    @Override
    @After
    public void tearDown() throws Exception {
        disposeModel();
        super.tearDown();
    }
}
