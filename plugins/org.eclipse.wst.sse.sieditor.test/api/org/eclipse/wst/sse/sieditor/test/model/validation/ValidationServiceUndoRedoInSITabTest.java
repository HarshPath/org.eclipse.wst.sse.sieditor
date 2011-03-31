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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.DeleteSetCommand;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationService;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

public class ValidationServiceUndoRedoInSITabTest extends AbstractCommandTest {

    private IDescription modelDescription;
    private IValidationService validationService;

    private IParameter inputParameter;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        inputParameter = null;
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        modelDescription = modelRoot.getDescription();
        validationService = editor.getValidationService();

        final ISchema modelsSchema = modelDescription.getSchema("http://www.example.org/")[0];
        final IType type = modelsSchema.getType(true, "NewOperation");
        final ArrayList<IModelObject> parents = new ArrayList<IModelObject>(2);
        parents.add(modelsSchema);
        final ArrayList<IModelObject> toDelete = new ArrayList<IModelObject>(2);
        toDelete.add(type);

        inputParameter = modelDescription.getInterface("tst").get(0).getOperation("NewOperation").get(0).getInputParameter(
                "Parameter").get(0);
        final List<IValidationStatus> status = validationService.getValidationStatusProvider().getStatus(inputParameter);
        assertZeroExpectedErrors(status);

        return new DeleteSetCommand(modelRoot, parents, toDelete);
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertTrue(redoStatus.isOK());
        inputParameter = modelDescription.getInterface("tst").get(0).getOperation("NewOperation").get(0).getInputParameter(
                "Parameter").get(0);
        final List<IValidationStatus> status = validationService.getValidationStatusProvider().getStatus(inputParameter);
        int numberOfErrors = 0;
        int expectedNumberOfErrors = 1;
        for (IValidationStatus currentStatus : status) {
            if (currentStatus.getSeverity() == IStatus.ERROR)
                ++numberOfErrors;
        }
        assertEquals(expectedNumberOfErrors, numberOfErrors);
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertTrue(undoStatus.isOK());
        inputParameter = modelDescription.getInterface("tst").get(0).getOperation("NewOperation").get(0).getInputParameter(
                "Parameter").get(0);
        final List<IValidationStatus> status = validationService.getValidationStatusProvider().getStatus(inputParameter);
        assertZeroExpectedErrors(status);
    }

    private void assertZeroExpectedErrors(final List<IValidationStatus> status) {
        int numberOfErrors = 0;
        int expectedNumberOfErrors = 0;
        for (IValidationStatus currentStatus : status) {
            if (currentStatus.getSeverity() == IStatus.ERROR)
                ++numberOfErrors;
        }
        assertEquals(expectedNumberOfErrors, numberOfErrors);
    }

    @Override
    protected String getWsdlFilename() {
        return "validaitionServiceTest.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "validation/";
    }

}
