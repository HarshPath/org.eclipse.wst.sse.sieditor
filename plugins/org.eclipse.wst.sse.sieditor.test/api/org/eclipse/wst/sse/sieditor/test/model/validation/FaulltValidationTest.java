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
package org.eclipse.wst.sse.sieditor.test.model.validation;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusProvider;
import org.eclipse.wst.sse.sieditor.model.validation.ValidationService;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FaulltValidationTest extends SIEditorBaseTest {

    private IDescription modelDescription;
    private IWsdlModelRoot modelRoot;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        modelRoot = (IWsdlModelRoot) getModelRoot("validation/FaultErrorMarker.wsdl", "FaultErrorMarker.wsdl",
                ServiceInterfaceEditor.EDITOR_ID);
        modelDescription = modelRoot.getDescription();
    }

    @Test
    public void testFaultError() {
        final ValidationService validationService = editor.getValidationService();

        modelRoot.getEnv().getEditingDomain().getCommandStack().execute(
                new RecordingCommand(modelRoot.getEnv().getEditingDomain()) {
                    @Override
                    protected void doExecute() {
                        /*
                         * this dummy command needs to be executed in order to
                         * start the validation.
                         * 
                         * our validation starts after a command transaction has
                         * ended.
                         */
                    }
                });
        final IValidationStatusProvider validationStatusProvider = validationService.getValidationStatusProvider();
        assertNotNull(validationStatusProvider.getStatus(modelDescription));

        final List<IServiceInterface> servInt = modelDescription.getInterface("ServiceInterface1");
        final IFault fault = servInt.get(0).getOperation("NewOperation1").get(0).getAllFaults().iterator().next();
        final List<IValidationStatus> status = validationStatusProvider.getStatus(fault);
        assertEquals(1, status.size());
        assertEquals(IStatus.ERROR, status.get(0).getSeverity());
    }

    @Override
    @After
    public void tearDown() throws Exception {
        disposeModel();
        super.tearDown();
    }
}
